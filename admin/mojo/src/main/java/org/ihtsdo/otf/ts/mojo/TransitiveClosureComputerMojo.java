package org.ihtsdo.otf.ts.mojo;

import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.ihtsdo.otf.ts.jpa.algo.TransitiveClosureAlgorithm;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.MetadataServiceJpa;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.services.ContentService;
import org.ihtsdo.otf.ts.services.MetadataService;

/**
 * Goal which recomputes transitive closure for the latest version
 * of a specified terminology.
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
    getLog().info("Starting computation of transitive closure");
    getLog().info("  terminology = " + terminology);

    try {

      // Track system level information
      long startTimeOrig = System.nanoTime();

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
