package org.ihtsdo.otf.ts.mojo;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.jpa.algo.TransitiveClosureAlgorithm;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.MetadataServiceJpa;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.services.ContentService;
import org.ihtsdo.otf.ts.services.MetadataService;

/**
 * Goal which loads an RF2 Snapshot of SNOMED CT data into a database.
 * 
 * See admin/loader/pom.xml for sample usage
 * 
 * @goal compute-transitive-closure
 * 
 * @phase package
 */
public class TransitiveClosureComputerMojo extends AbstractMojo {

  /**
   * Name of terminology to be loaded.
   * @parameter
   * @required
   */
  private String terminology;

  /** The version. */
  private String version = null;

  /**
   * Instantiates a {@link TransitiveClosureComputerMojo} from the specified
   * parameters.
   * 
   */
  public TransitiveClosureComputerMojo() {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.maven.plugin.Mojo#execute()
   */
  @Override
  public void execute() throws MojoFailureException {
    getLog().info("Starting computation of transitive closure ...");

    try {

      // Track system level information
      long startTimeOrig = System.nanoTime();

      // Load config properties
      Properties config = ConfigUtility.getConfigProperties();

      // Set the input directory
      String coreInputDirString =
          config.getProperty("loader." + terminology + ".input.data");
      File coreInputDir = new File(coreInputDirString);
      if (!coreInputDir.exists()) {
        throw new MojoFailureException("Specified loader." + terminology
            + ".input.data directory does not exist: " + coreInputDirString);
      }

      //
      // Determine version
      //
      File coreConceptInputFile = null;
      File coreTerminologyInputDir = new File(coreInputDir, "/Terminology/");
      for (File f : coreTerminologyInputDir.listFiles()) {
        if (f.getName().contains("sct2_Concept_")) {
          if (coreConceptInputFile != null)
            throw new MojoFailureException("Multiple Concept Files!");
          coreConceptInputFile = f;
        }
      }
      if (coreConceptInputFile != null) {
        int index = coreConceptInputFile.getName().indexOf(".txt");
        version = coreConceptInputFile.getName().substring(index - 8, index);
        getLog().info("  terminology = " + terminology);
        getLog().info("  version = " + version);
      } else {
        throw new MojoFailureException(
            "Could not find concept file to determine version");
      }

      // Log memory usage
      Runtime runtime = Runtime.getRuntime();
      getLog().info("MEMORY USAGE:");
      getLog().info(" Total: " + runtime.totalMemory());
      getLog().info(" Free:  " + runtime.freeMemory());
      getLog().info(" Max:   " + runtime.maxMemory());

      try {

        // Compute transitive closure
        MetadataService metadataService = new MetadataServiceJpa();
        String terminologyVersion =
            metadataService.getLatestVersion(terminology);
        Map<String, String> hierRelTypeMap =
            metadataService.getHierarchicalRelationshipTypes(terminology,
                terminologyVersion);
        String isaRelType =
            hierRelTypeMap.keySet().iterator().next().toString();
        metadataService.close();
        ContentService contentService = new ContentServiceJpa();
        getLog().info("  Clear transitive closure");
        contentService.clearTransitiveClosure(terminology, terminologyVersion);
        // Walk up tree to the root
        // ASSUMPTION: single root
        String conceptId = isaRelType;
        String rootId = null;
        OUTER: while (true) {
          getLog().info("  Walk up tree from " + conceptId);
          Concept c =
              contentService.getSingleConcept(conceptId, terminology,
                  terminologyVersion);
          for (Relationship r : c.getRelationships()) {
            getLog().info(
                "      rel = " + r.getTerminologyId() + ", " + r.isActive()
                    + ", " + r.getTypeId());
            if (r.isActive() && r.getTypeId().equals(isaRelType)) {
              conceptId = r.getDestinationConcept().getTerminologyId();
              continue OUTER;
            }
          }
          rootId = conceptId;
          break;
        }
        Long rootIdLong =
            contentService.getSingleConcept(rootId, terminology,
                terminologyVersion).getId();

        contentService.close();
        getLog().info(
            "  Compute transitive closure from  " + rootId + "/" + terminology
                + "/" + terminologyVersion);
        TransitiveClosureAlgorithm algo = new TransitiveClosureAlgorithm();
        algo.setTerminology(terminology);
        algo.setTerminologyVersion(terminologyVersion);
        algo.reset();
        algo.setRootId(rootIdLong);
        algo.compute();
        algo.close();

        // Final logging messages
        getLog().info(
            "      elapsed time = " + getTotalElapsedTimeStr(startTimeOrig));
        getLog().info("done ...");

      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }

      // Clean-up
    } catch (Exception e) {
      e.printStackTrace();
      throw new MojoFailureException("Unexpected exception:", e);
    }
  }

  /**
   * Returns the total elapsed time str.
   *
   * @param time the time
   * @return the total elapsed time str
   */
  @SuppressWarnings("boxing")
  private static String getTotalElapsedTimeStr(long time) {
    Long resultnum = (System.nanoTime() - time) / 1000000000;
    String result = resultnum.toString() + "s";
    resultnum = resultnum / 60;
    result = result + " / " + resultnum.toString() + "m";
    resultnum = resultnum / 60;
    result = result + " / " + resultnum.toString() + "h";
    return result;
  }
}
