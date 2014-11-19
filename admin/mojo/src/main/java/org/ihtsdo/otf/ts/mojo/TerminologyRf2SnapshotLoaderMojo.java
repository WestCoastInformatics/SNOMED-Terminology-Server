package org.ihtsdo.otf.ts.mojo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.persistence.NoResultException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.FileSorter;
import org.ihtsdo.otf.ts.jpa.algo.TransitiveClosureAlgorithm;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.MetadataServiceJpa;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceRefSetMember;
import org.ihtsdo.otf.ts.rf2.AttributeValueRefSetMember;
import org.ihtsdo.otf.ts.rf2.ComplexMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.SimpleMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.SimpleRefSetMember;
import org.ihtsdo.otf.ts.rf2.jpa.AssociationReferenceConceptRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.AttributeValueConceptRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ComplexMapRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa;
import org.ihtsdo.otf.ts.rf2.jpa.LanguageRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RelationshipJpa;
import org.ihtsdo.otf.ts.rf2.jpa.SimpleMapRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.SimpleRefSetMemberJpa;
import org.ihtsdo.otf.ts.services.ContentService;
import org.ihtsdo.otf.ts.services.MetadataService;

import com.google.common.io.Files;

/**
 * Goal which loads an RF2 Snapshot of SNOMED CT data into a database.
 * 
 * <pre>
 *     <plugin>
 *       <groupId>org.ihtsdo.otf.mapping</groupId>
 *       <artifactId>mapping-admin-mojo</artifactId>
 *       <version>${project.version}</version>
 *       <executions>
 *         <execution>
 *           <id>load-rf2-snapshot</id>
 *           <phase>package</phase>
 *           <goals>
 *             <goal>load-rf2-snapshot</goal>
 *           </goals>
 *           <configuration>
 *             <terminology>SNOMEDCT</terminology>
 *           </configuration>
 *         </execution>
 *       </executions>
 *     </plugin>
 * </pre>
 * 
 * @goal load-rf2-snapshot
 * 
 * @phase package
 */
public class TerminologyRf2SnapshotLoaderMojo extends AbstractMojo {

  /**
   * Name of terminology to be loaded.
   * @parameter
   * @required
   */
  private String terminology;

  /** The date format. */
  private static final SimpleDateFormat dt = new SimpleDateFormat("yyyymmdd");

  /** The concepts by concept. */
  private BufferedReader conceptsByConcept;

  /** The descriptions by description. */
  private BufferedReader descriptionsByDescription;

  /** The relationships by source concept. */
  private BufferedReader relationshipsBySourceConcept;

  /** The language refsets by description. */
  private BufferedReader languageRefsetsByDescription;

  /** The attribute value refsets by reference component id. */
  private BufferedReader attributeValueRefsetsByRefCompId;

  /** The association reference refsets by ref component id. */
  private BufferedReader associationReferenceRefsetsByRefCompId;

  /** The simple refsets by concept. */
  private BufferedReader simpleRefsetsByConcept;

  /** The simple map refsets by concept. */
  private BufferedReader simpleMapRefsetsByConcept;

  /** The complex map refsets by concept. */
  private BufferedReader complexMapRefsetsByConcept;

  /** The extended map refsets by concept. */
  private BufferedReader extendedMapRefsetsByConcept;

  /** The version. */
  private String version = null;

  // set from run.config properties file
  /** the defaultPreferredNames values. */
  private String dpnTypeId;

  // set from run.config properties file
  /** The dpn ref set id. */
  private String dpnRefSetId;

  // set from run.config properties file
  /** The dpn acceptability id. */
  private String dpnAcceptabilityId;

  /** hash sets for retrieving concepts. */
  private Map<String, Concept> conceptCache = new HashMap<>(); // used to

  /** hash set for storing default preferred names. */
  Map<Long, String> defaultPreferredNames = new HashMap<>();

  /** counter for objects created, reset in each load section */
  int objectCt; //

  /** the number of objects to create before committing. */
  int commitCt = 2000;

  /** The error flag. */
  private boolean errorFlag = false;

  /**
   * Instantiates a {@link TerminologyRf2SnapshotLoaderMojo} from the specified
   * parameters.
   * 
   */
  public TerminologyRf2SnapshotLoaderMojo() {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.maven.plugin.Mojo#execute()
   */
  @Override
  public void execute() throws MojoFailureException {
    getLog().info("Starting loading RF2 data ...");

    try {

      // Track system level information
      long startTimeOrig = System.nanoTime();

      // create Entity Manager
      Properties config = ConfigUtility.getConfigProperties();

      // set the input directory
      String coreInputDirString =
          config.getProperty("loader." + terminology + ".input.data");
      File coreInputDir = new File(coreInputDirString);
      if (!coreInputDir.exists()) {
        throw new MojoFailureException("Specified loader." + terminology
            + ".input.data directory does not exist: " + coreInputDirString);
      }
      // set the parameters for determining defaultPreferredNames
      dpnTypeId = config.getProperty("loader.defaultPreferredNames.typeId");
      dpnRefSetId = config.getProperty("loader.defaultPreferredNames.refSetId");
      dpnAcceptabilityId =
          config.getProperty("loader.defaultPreferredNames.acceptabilityId");

      // Determine version from filename
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
        getLog().info("Version " + version);
      } else {
        throw new MojoFailureException(
            "Could not find concept file to determine version");
      }

      // Log settings
      getLog().info("Default preferred name settings:");
      getLog().info(" typeId:          " + dpnTypeId);
      getLog().info(" refSetId:        " + dpnRefSetId);
      getLog().info(" acceptabilityId: " + dpnAcceptabilityId);
      getLog().info(
          "Commit settings: Objects committed in blocks of "
              + Integer.toString(commitCt));
      Runtime runtime = Runtime.getRuntime();
      getLog().info("MEMORY USAGE:");
      getLog().info(" Total: " + runtime.totalMemory());
      getLog().info(" Free:  " + runtime.freeMemory());
      getLog().info(" Max:   " + runtime.maxMemory());

      // format for logging
      SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss a");

      try {

        // Prepare sorted input files
        File sortedFileDir = new File(coreInputDir, "/RF2-sorted-temp/");
        getLog().info("Preparing input files...");
        long startTime = System.nanoTime();
        sortRf2Files(coreInputDir, sortedFileDir);
        getLog().info(
            "    File preparation complete in " + getElapsedTime(startTime)
                + "s");

        // Open readers
        openSortedFileReaders(sortedFileDir);

        // load Concepts
        if (conceptsByConcept != null) {
          getLog().info("    Loading Concepts...");
          startTime = System.nanoTime();
          loadConcepts();
          getLog().info(
              "      " + Integer.toString(objectCt) + " Concepts loaded in "
                  + getElapsedTime(startTime) + "s" + " (Ended at "
                  + ft.format(new Date()) + ")");
        }

        // load Descriptions and Language Ref Set Members
        if (descriptionsByDescription != null
            && languageRefsetsByDescription != null) {
          getLog().info("    Loading Descriptions and LanguageRefSets...");
          startTime = System.nanoTime();
          loadDescriptionsAndLanguageRefSets();
          getLog().info(
              "      "
                  + " Descriptions and language ref set members loaded in "
                  + getElapsedTime(startTime) + "s" + " (Ended at "
                  + ft.format(new Date()) + ")");

          // set default preferred names
          getLog().info(" Setting default preferred names for all concepts...");
          startTime = System.nanoTime();
          setDefaultPreferredNames();
          getLog().info(
              "      " + "Names set in " + getElapsedTime(startTime).toString()
                  + "s");

        }

        // load Relationships
        if (relationshipsBySourceConcept != null) {
          getLog().info("    Loading Relationships...");
          startTime = System.nanoTime();
          loadRelationships();
          getLog().info(
              "      " + Integer.toString(objectCt) + " Concepts loaded in "
                  + getElapsedTime(startTime) + "s" + " (Ended at "
                  + ft.format(new Date()) + ")");
        }

        // load Simple RefSets (Content)
        if (simpleRefsetsByConcept != null) {
          getLog().info("    Loading Simple RefSets...");
          startTime = System.nanoTime();
          loadSimpleRefSets();
          getLog().info(
              "      " + Integer.toString(objectCt)
                  + " Simple Refsets loaded in " + getElapsedTime(startTime)
                  + "s" + " (Ended at " + ft.format(new Date()) + ")");
        }

        // load SimpleMapRefSets
        if (simpleMapRefsetsByConcept != null) {
          getLog().info("    Loading SimpleMap RefSets...");
          startTime = System.nanoTime();
          loadSimpleMapRefSets();
          getLog().info(
              "      " + Integer.toString(objectCt)
                  + " SimpleMap RefSets loaded in " + getElapsedTime(startTime)
                  + "s" + " (Ended at " + ft.format(new Date()) + ")");
        }

        // load ComplexMapRefSets
        if (complexMapRefsetsByConcept != null) {
          getLog().info("    Loading ComplexMap RefSets...");
          startTime = System.nanoTime();
          loadComplexMapRefSets();
          getLog().info(
              "      " + Integer.toString(objectCt)
                  + " ComplexMap RefSets loaded in "
                  + getElapsedTime(startTime) + "s" + " (Ended at "
                  + ft.format(new Date()) + ")");
        }

        // load ExtendedMapRefSets
        if (extendedMapRefsetsByConcept != null) {
          getLog().info("    Loading ExtendedMap RefSets...");
          startTime = System.nanoTime();
          loadExtendedMapRefSets();
          getLog().info(
              "      " + Integer.toString(objectCt)
                  + " ExtendedMap RefSets loaded in "
                  + getElapsedTime(startTime) + "s" + " (Ended at "
                  + ft.format(new Date()) + ")");
        }

        // load AttributeValue RefSets (Content)
        if (attributeValueRefsetsByRefCompId != null) {
          getLog().info("    Loading AttributeValue RefSets...");
          startTime = System.nanoTime();
          loadAttributeValueRefSets();
          getLog().info(
              "      " + Integer.toString(objectCt)
                  + " AttributeValue RefSets loaded in "
                  + getElapsedTime(startTime).toString() + "s" + " (Ended at "
                  + ft.format(new Date()) + ")");
        }

        // load AssocationReference RefSets (Content)
        if (associationReferenceRefsetsByRefCompId != null) {
          getLog().info("    Loading AssociationReference RefSets...");
          startTime = System.nanoTime();
          loadAssociationReferenceRefSets();
          getLog().info(
              "      " + Integer.toString(objectCt)
                  + " AssociationReference RefSets loaded in "
                  + getElapsedTime(startTime).toString() + "s" + " (Ended at "
                  + ft.format(new Date()) + ")");
        }

        conceptCache.clear();
        closeAllSortedFiles();

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
        // Walk up tree to the root
        // ASSUMPTION: single root
        String conceptId = isaRelType;
        String rootId = null;
        OUTER: while (true) {
          getLog().info("  Walk up tree from " + conceptId);
          Concept c =
              contentService.getSingleConcept(conceptId, terminology,
                  terminologyVersion);
          getLog().info("    concept = " + c.getTerminologyId());
          getLog().info("    concept.rels.ct = " + c.getRelationships().size());
          getLog().info("    isaRelType = " + isaRelType);
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
        contentService.close();

        getLog().info(
            "  Compute transitive closure from rootId " + rootId + " for "
                + terminology + ", " + terminologyVersion);
        TransitiveClosureAlgorithm algo = new TransitiveClosureAlgorithm();
        algo.setTerminology(terminology);
        algo.setTerminologyVersion(terminologyVersion);
        algo.reset();
        algo.setRootId(rootId);
        algo.compute();
        algo.close();

        // Final logging messages
        getLog().info(
            "    Total elapsed time for run: "
                + getTotalElapsedTimeStr(startTimeOrig));
        if (errorFlag) {
          getLog().info("    THIS RUN INCLUDED ERRORS, please review the log (search for \"ERROR\")");
        } else {
          getLog().info("    COMPLETED WITHOUT ERRORS");
        }
        getLog().info("done ...");

      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }

    } catch (Exception e) {
      e.printStackTrace();
      throw new MojoFailureException("Unexpected exception:", e);
    }
  }

  /**
   * Opens sorted data files.
   *
   * @param outputDir the output dir
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private void openSortedFileReaders(File outputDir) throws IOException {
    File conceptsByConceptsFile =
        new File(outputDir, "concepts_by_concept.sort");
    File descriptionsByDescriptionFile =
        new File(outputDir, "descriptions_by_description.sort");
    // File descriptions_core_by_description_file =
    // new File(outputDir, "descriptions_core_by_description.sort");
    // File descriptions_text_by_description_file =
    // new File(outputDir, "descriptions_text_by_description.sort");
    File relationshipsBySourceConceptFile =
        new File(outputDir, "relationship_by_source_concept.sort");
    // File relationships_by_dest_concept_file =
    // new File(outputDir, "relationship_by_dest_concept.sort");
    File languageRefsetsByDescriptionsFile =
        new File(outputDir, "language_refsets_by_description.sort");
    File attributeValueRefsetsByRefCompIdFile =
        new File(outputDir, "attribute_value_refsets_by_refCompId.sort");
    File associationReferenceRefsetsByRefCompIdFile =
        new File(outputDir, "association_reference_refsets_by_refCompId.sort");
    File simpleRefsetsByConceptFile =
        new File(outputDir, "simple_refsets_by_concept.sort");
    File simpleMapRefsetsByConceptFile =
        new File(outputDir, "simple_map_refsets_by_concept.sort");
    File complexMapRefsetsByConceptFile =
        new File(outputDir, "complex_map_refsets_by_concept.sort");
    File extendedMapRefsetsByConceptsFile =
        new File(outputDir, "extended_map_refsets_by_concept.sort");
    // Concepts
    conceptsByConcept =
        new BufferedReader(new FileReader(conceptsByConceptsFile));

    // Relationships by source concept
    relationshipsBySourceConcept =
        new BufferedReader(new FileReader(relationshipsBySourceConceptFile));

    // Descriptions by description id
    descriptionsByDescription =
        new BufferedReader(new FileReader(descriptionsByDescriptionFile));

    // Language RefSets by description id
    languageRefsetsByDescription =
        new BufferedReader(new FileReader(languageRefsetsByDescriptionsFile));

    // ******************************************************* //
    // Component RefSet Members //
    // ******************************************************* //

    // Attribute Value
    attributeValueRefsetsByRefCompId =
        new BufferedReader(new FileReader(attributeValueRefsetsByRefCompIdFile));

    // Attribute Value
    associationReferenceRefsetsByRefCompId =
        new BufferedReader(new FileReader(
            associationReferenceRefsetsByRefCompIdFile));

    // Simple
    simpleRefsetsByConcept =
        new BufferedReader(new FileReader(simpleRefsetsByConceptFile));

    // Simple Map
    simpleMapRefsetsByConcept =
        new BufferedReader(new FileReader(simpleMapRefsetsByConceptFile));

    // Complex map
    complexMapRefsetsByConcept =
        new BufferedReader(new FileReader(complexMapRefsetsByConceptFile));

    // Extended map
    extendedMapRefsetsByConcept =
        new BufferedReader(new FileReader(extendedMapRefsetsByConceptsFile));

  }

  // Used for debugging/efficiency monitoring
  /**
   * Returns the elapsed time.
   *
   * @param time the time
   * @return the elapsed time
   */
  @SuppressWarnings("boxing")
  private static Long getElapsedTime(long time) {
    return (System.nanoTime() - time) / 1000000000;
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

  /**
   * Returns the last modified.
   * 
   * @param directory the directory
   * @return the last modified
   */
  private long getLastModified(File directory) {
    File[] files = directory.listFiles();
    long lastModified = 0;

    for (int j = 0; j < files.length; j++) {
      if (files[j].isDirectory()) {
        long tempLastModified = getLastModified(files[j]);
        if (lastModified < tempLastModified) {
          lastModified = tempLastModified;
        }
      } else if (lastModified < files[j].lastModified()) {
        lastModified = files[j].lastModified();
      }
    }

    return lastModified;
  }

  /**
   * Sorts all files by concept or referencedComponentId.
   *
   * @param coreInputDir the core input dir
   * @param outputDir the output dir
   * @throws Exception the exception
   */
  @SuppressWarnings("null")
  private void sortRf2Files(File coreInputDir, File outputDir) throws Exception {

    // Check expectations and pre-conditions
    if (!outputDir.exists()
        || getLastModified(outputDir) < getLastModified(coreInputDir)) {

      // log reason for sort
      if (!outputDir.exists()) {
        getLog().info("     No sorted files exist -- sorting RF2 files");
      } else if (getLastModified(outputDir) < getLastModified(coreInputDir)) {
        getLog().info(
            "     Sorted files older than input files -- sorting RF2 files");
      }

      // delete any existing temporary files
      FileSorter.deleteSortedFiles(outputDir);

      // Test whether file/folder still exists (i.e. delete error)
      if (outputDir.exists()) {
        throw new MojoFailureException(
            "Could not delete existing sorted files folder "
                + outputDir.toString());
      }

      // attempt to make sorted files directory
      if (outputDir.mkdir()) {
        getLog().info(
            " Creating new sorted files folder " + outputDir.toString());
      } else {
        throw new MojoFailureException(
            " Could not create temporary sorted file folder "
                + outputDir.toString());
      }

    } else {
      getLog().info(
          "    Sorted files exist and are up to date.  No sorting required");
      return;
    }

    //
    // Set files
    //
    File coreRelInputFile = null;
    File coreStatedRelInputFile = null;
    File coreConceptInputFile = null;
    File coreDescriptionInputFile = null;
    File coreSimpleRefsetInputFile = null;
    File coreAttributeValueInputFile = null;
    File coreAssociationReferenceInputFile = null;
    File coreComplexMapInputFile = null;
    File coreExtendedMapInputFile = null;
    File coreSimpleMapInputFile = null;
    File coreLanguageInputFile = null;
    File coreIdentifierInputFile = null;
    File coreTextDefinitionInputFile = null;

    // CORE
    File coreTerminologyInputDir = new File(coreInputDir, "/Terminology/");
    getLog().info(
        "  Core Input Dir = " + coreTerminologyInputDir.toString() + " "
            + coreTerminologyInputDir.exists());

    for (File f : coreTerminologyInputDir.listFiles()) {
      if (f.getName().contains("sct2_Relationship_")) {
        if (coreRelInputFile != null)
          throw new MojoFailureException("Multiple Relationships Files!");
        coreRelInputFile = f;
      }
    }
    getLog().info(
        "  Core Rel Input File = " + coreRelInputFile.toString() + " "
            + coreRelInputFile.exists());

    for (File f : coreTerminologyInputDir.listFiles()) {
      if (f.getName().contains("sct2_StatedRelationship_")) {
        if (coreStatedRelInputFile != null)
          throw new MojoFailureException("Multiple Stated Relationships Files!");
        coreStatedRelInputFile = f;
      }
    }
    getLog().info(
        "  Core Stated Rel Input File = " + coreStatedRelInputFile.toString()
            + " " + coreStatedRelInputFile.exists());

    for (File f : coreTerminologyInputDir.listFiles()) {
      if (f.getName().contains("sct2_Concept_")) {
        if (coreConceptInputFile != null)
          throw new MojoFailureException("Multiple Concept Files!");
        coreConceptInputFile = f;
      }
    }
    getLog().info(
        "  Core Concept Input File = " + coreConceptInputFile.toString() + " "
            + coreConceptInputFile.exists());

    for (File f : coreTerminologyInputDir.listFiles()) {
      if (f.getName().contains("sct2_Description_")) {
        if (coreDescriptionInputFile != null)
          throw new MojoFailureException("Multiple Description Files!");
        coreDescriptionInputFile = f;
      }
    }
    getLog().info(
        "  Core Description Input File = "
            + coreDescriptionInputFile.toString() + " "
            + coreDescriptionInputFile.exists());

    for (File f : coreTerminologyInputDir.listFiles()) {
      if (f.getName().contains("sct2_Identifier_")) {
        if (coreIdentifierInputFile != null)
          throw new MojoFailureException("Multiple Identifier Files!");
        coreIdentifierInputFile = f;
      }
    }
    if (coreIdentifierInputFile != null) {
      getLog().info(
          "  Core Identifier Input File = "
              + coreIdentifierInputFile.toString() + " "
              + coreIdentifierInputFile.exists());
    }

    for (File f : coreTerminologyInputDir.listFiles()) {
      if (f.getName().contains("sct2_TextDefinition_")) {
        if (coreTextDefinitionInputFile != null)
          throw new MojoFailureException("Multiple TextDefinition Files!");
        coreTextDefinitionInputFile = f;
      }
    }
    if (coreTextDefinitionInputFile != null) {
      getLog().info(
          "  Core Text Definition Input File = "
              + coreTextDefinitionInputFile.toString() + " "
              + coreTextDefinitionInputFile.exists());
    }

    File coreRefsetInputDir = new File(coreInputDir, "/Refset/");
    File coreContentInputDir = new File(coreRefsetInputDir, "/Content/");
    getLog().info(
        "  Core Input Dir = " + coreContentInputDir.toString() + " "
            + coreContentInputDir.exists());

    for (File f : coreContentInputDir.listFiles()) {
      if (f.getName().contains("Refset_Simple")) {
        if (coreSimpleRefsetInputFile != null)
          throw new MojoFailureException("Multiple Simple Refset Files!");
        coreSimpleRefsetInputFile = f;
      }
    }
    getLog().info(
        "  Core Simple Refset Input File = "
            + coreSimpleRefsetInputFile.toString() + " "
            + coreSimpleRefsetInputFile.exists());

    for (File f : coreContentInputDir.listFiles()) {
      if (f.getName().contains("AttributeValue")) {
        if (coreAttributeValueInputFile != null)
          throw new MojoFailureException("Multiple Attribute Value Files!");
        coreAttributeValueInputFile = f;
      }
    }
    getLog().info(
        "  Core Attribute Value Input File = "
            + coreAttributeValueInputFile.toString() + " "
            + coreAttributeValueInputFile.exists());

    for (File f : coreContentInputDir.listFiles()) {
      if (f.getName().contains("AssociationReference")) {
        if (coreAssociationReferenceInputFile != null)
          throw new MojoFailureException(
              "Multiple Association Reference Files!");
        coreAssociationReferenceInputFile = f;
      }
    }
    getLog().info(
        "  Core Association Reference Input File = "
            + coreAssociationReferenceInputFile.toString() + " "
            + coreAssociationReferenceInputFile.exists());

    File coreCrossmapInputDir = new File(coreRefsetInputDir, "/Map/");
    getLog().info(
        "  Core Crossmap Input Dir = " + coreCrossmapInputDir.toString() + " "
            + coreCrossmapInputDir.exists());

    for (File f : coreCrossmapInputDir.listFiles()) {
      if (f.getName().contains("ComplexMap")) {
        if (coreComplexMapInputFile != null)
          throw new MojoFailureException("Multiple Complex Map Files!");
        coreComplexMapInputFile = f;
      }
    }
    if (coreComplexMapInputFile != null) {
      getLog().info(
          "  Core Complex Map Input File = "
              + coreComplexMapInputFile.toString() + " "
              + coreComplexMapInputFile.exists());
    }

    for (File f : coreCrossmapInputDir.listFiles()) {
      if (f.getName().contains("ExtendedMap")) {
        if (coreExtendedMapInputFile != null)
          throw new MojoFailureException("Multiple Extended Map Files!");
        coreExtendedMapInputFile = f;
      }
    }
    if (coreComplexMapInputFile != null) {
      getLog().info(
          "  Core Complex Map Input File = "
              + coreComplexMapInputFile.toString() + " "
              + coreComplexMapInputFile.exists());
    }

    for (File f : coreCrossmapInputDir.listFiles()) {
      if (f.getName().contains("SimpleMap")) {
        if (coreSimpleMapInputFile != null)
          throw new MojoFailureException("Multiple Simple Map Files!");
        coreSimpleMapInputFile = f;
      }
    }
    getLog().info(
        "  Core Simple Map Input File = " + coreSimpleMapInputFile.toString()
            + " " + coreSimpleMapInputFile.exists());

    File coreLanguageInputDir = new File(coreRefsetInputDir, "/Language/");
    getLog().info(
        "  Core Language Input Dir = " + coreLanguageInputDir.toString() + " "
            + coreLanguageInputDir.exists());

    for (File f : coreLanguageInputDir.listFiles()) {
      if (f.getName().contains("Language")) {
        if (coreLanguageInputFile != null)
          throw new MojoFailureException("Multiple Language Files!");
        coreLanguageInputFile = f;
      }
    }
    getLog().info(
        "  Core Language Input File = " + coreLanguageInputFile.toString()
            + " " + coreLanguageInputFile.exists());

    File coreMetadataInputDir = new File(coreRefsetInputDir, "/Metadata/");
    getLog().info(
        "  Core Metadata Input Dir = " + coreMetadataInputDir.toString() + " "
            + coreMetadataInputDir.exists());

    //
    // Initialize files
    //

    File conceptsByConceptFile =
        new File(outputDir, "concepts_by_concept.sort");
    File descriptionsByDescriptionFile =
        new File(outputDir, "descriptions_by_description.sort");
    File descriptionsCoreByDescriptionFile =
        new File(outputDir, "descriptions_core_by_description.sort");
    File descriptionsTextByDescriptionFile =
        new File(outputDir, "descriptions_text_by_description.sort");
    File relationshipsBySourceConceptFile =
        new File(outputDir, "relationship_by_source_concept.sort");
    File relationshipsByDestinationConceptFile =
        new File(outputDir, "relationship_by_dest_concept.sort");
    File languageRefsetsByDescriptionFile =
        new File(outputDir, "language_refsets_by_description.sort");
    File attributeValueRefsetsByRefCompIdFile =
        new File(outputDir, "attribute_value_refsets_by_refCompId.sort");
    File associationReferenceRefsetsByRefCompIdFile =
        new File(outputDir, "association_refsets_by_refCompId.sort");
    File simpleRefsetsByConceptFile =
        new File(outputDir, "simple_refsets_by_concept.sort");
    File simpleMapRefsetsByConceptFile =
        new File(outputDir, "simple_map_refsets_by_concept.sort");
    File complexMapRefsetsByConceptFile =
        new File(outputDir, "complex_map_refsets_by_concept.sort");
    File extendedMapRefsetsByConceptsFile =
        new File(outputDir, "extended_map_refsets_by_concept.sort");

    // Sort concepts file by concept id
    sortRf2File(coreConceptInputFile, conceptsByConceptFile, 0);

    // Sort relationships by source and destination concept id
    sortRf2File(coreRelInputFile, relationshipsBySourceConceptFile, 4);
    sortRf2File(coreRelInputFile, relationshipsByDestinationConceptFile, 5);

    // Sort descriptions by description id
    sortRf2File(coreDescriptionInputFile, descriptionsCoreByDescriptionFile, 0);

    // If text descriptions file exists, sort and merge
    if (coreTextDefinitionInputFile != null) {

      // Sort the text definition file by description id
      sortRf2File(coreTextDefinitionInputFile,
          descriptionsTextByDescriptionFile, 0);

      // Merge the two description files
      getLog().info("        Merging description files...");
      File mergedDesc =
          mergeSortedFiles(descriptionsTextByDescriptionFile,
              descriptionsCoreByDescriptionFile, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                  String v1[] = s1.split("\t");
                  String v2[] = s2.split("\t");
                  return v1[0].compareTo(v2[0]);
                }
              }, outputDir, ""); // header line

      // rename the temporary file
      Files.move(mergedDesc, descriptionsByDescriptionFile);

    } else {
      // copy the core descriptions file
      Files.copy(descriptionsCoreByDescriptionFile,
          descriptionsByDescriptionFile);
    }

    // Sort language refsets by concept
    sortRf2File(coreLanguageInputFile, languageRefsetsByDescriptionFile, 5);

    // Sort attribute value refset file by referenced component id
    sortRf2File(coreAttributeValueInputFile,
        attributeValueRefsetsByRefCompIdFile, 5);

    // Sort association reference input file by referenced component id
    sortRf2File(coreAssociationReferenceInputFile,
        associationReferenceRefsetsByRefCompIdFile, 5);

    // Sort simple refsets by concept
    sortRf2File(coreSimpleRefsetInputFile, simpleRefsetsByConceptFile, 5);

    // Sort simple map refsets by concept
    sortRf2File(coreSimpleMapInputFile, simpleMapRefsetsByConceptFile, 5);

    // Sort complex map refsets by concept
    sortRf2File(coreComplexMapInputFile, complexMapRefsetsByConceptFile, 5);

    // Sort extended map refsets input file by concept
    sortRf2File(coreExtendedMapInputFile, extendedMapRefsetsByConceptsFile, 5);

    // Open concepts reader
    conceptsByConcept =
        new BufferedReader(new FileReader(conceptsByConceptFile));

    // Open reltionships reader
    relationshipsBySourceConcept =
        new BufferedReader(new FileReader(relationshipsBySourceConceptFile));

    // Open descriptions reader
    descriptionsByDescription =
        new BufferedReader(new FileReader(descriptionsByDescriptionFile));

    // Open language refset reader
    languageRefsetsByDescription =
        new BufferedReader(new FileReader(languageRefsetsByDescriptionFile));

    // Open attribute value refset reader
    attributeValueRefsetsByRefCompId =
        new BufferedReader(new FileReader(attributeValueRefsetsByRefCompIdFile));

    // Open association reference refset reader
    associationReferenceRefsetsByRefCompId =
        new BufferedReader(new FileReader(
            associationReferenceRefsetsByRefCompIdFile));

    // Open simple refset reader
    simpleRefsetsByConcept =
        new BufferedReader(new FileReader(simpleRefsetsByConceptFile));

    // Open simple map refset reader
    simpleMapRefsetsByConcept =
        new BufferedReader(new FileReader(simpleMapRefsetsByConceptFile));

    // Open complex map refset reader
    complexMapRefsetsByConcept =
        new BufferedReader(new FileReader(complexMapRefsetsByConceptFile));

    // Open extended map refset reader
    extendedMapRefsetsByConcept =
        new BufferedReader(new FileReader(extendedMapRefsetsByConceptsFile));

  }

  /**
   * Helper function for sorting an individual file with colum comparator.
   * 
   * @param fileIn the input file to be sorted
   * @param fileOut the resulting sorted file
   * @param sortColumn the column ([0, 1, ...] to compare by
   * @throws Exception the exception
   */
  private void sortRf2File(File fileIn, File fileOut, final int sortColumn)
    throws Exception {

    Comparator<String> comp;
    // Split on \t and sort by sortColumn
    comp = new Comparator<String>() {
      @Override
      public int compare(String s1, String s2) {
        String v1[] = s1.split("\t");
        String v2[] = s2.split("\t");
        return v1[sortColumn].compareTo(v2[sortColumn]);
      }
    };

    getLog().info(
        " Sorting " + fileIn.toString() + "  into " + fileOut.toString()
            + " by column " + Integer.toString(sortColumn));
    FileSorter.sortFile(fileIn.toString(), fileOut.toString(), comp);

  }

  /**
   * Merge-sort two files.
   * 
   * @param files1 the first set of files
   * @param files2 the second set of files
   * @param comp the comparator
   * @param dir the sort dir
   * @param headerLine the header_line
   * @return the sorted {@link File}
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @SuppressWarnings("null")
  private File mergeSortedFiles(File files1, File files2,
    Comparator<String> comp, File dir, String headerLine) throws IOException {

    final BufferedReader in1 = new BufferedReader(new FileReader(files1));
    final BufferedReader in2 = new BufferedReader(new FileReader(files2));
    final File outFile = File.createTempFile("t+~", ".tmp", dir);
    final BufferedWriter out = new BufferedWriter(new FileWriter(outFile));

    getLog().info(
        "Merging files: " + files1.getName() + " - " + files2.getName()
            + " into " + outFile.getName());
    String line1 = in1.readLine();
    String line2 = in2.readLine();
    String line = null;
    if (!headerLine.isEmpty()) {
      line = headerLine;
      out.write(line);
      out.newLine();
    }
    while (line1 != null || line2 != null) {
      if (line1 == null) {
        line = line2;
        line2 = in2.readLine();
      } else if (line2 == null) {
        line = line1;
        line1 = in1.readLine();
      } else if (comp.compare(line1, line2) < 0) {
        line = line1;
        line1 = in1.readLine();
      } else {
        line = line2;
        line2 = in2.readLine();
      }
      // if a header line, do not write
      if (!line.startsWith("id")) {
        out.write(line);
        out.newLine();
      }
    }
    out.flush();
    out.close();
    in1.close();
    in2.close();
    return outFile;
  }

  /**
   * Returns the concept either from the cache or by reading it from the
   * service.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param terminologyVersion the terminology version
   * @param contentService the content service
   * @return the concept
   * @throws Exception the exception
   */
  private Concept getConcept(String terminologyId, String terminology,
    String terminologyVersion, ContentService contentService) throws Exception {

    if (conceptCache.containsKey(terminologyId + terminology
        + terminologyVersion)) {

      // uses hibernate first-level cache
      return conceptCache.get(terminologyId + terminology + terminologyVersion);
    }
    try {
      Concept c =
          contentService.getSingleConcept(terminologyId, terminology,
              terminologyVersion);
      conceptCache.put(terminologyId + terminology + terminologyVersion, c);
      return c;
    } catch (NoResultException e) {
      // Log and return null if there are no releases
      getLog().debug(
          "Concept query for terminologyId = " + terminologyId
              + ", terminology = " + terminology + ", terminologyVersion = "
              + terminologyVersion + " returned no results!");
      return null;
    }

  }

  /**
   * Closes all sorted temporary files.
   * 
   * @throws Exception if something goes wrong
   */
  private void closeAllSortedFiles() throws Exception {
    if (conceptsByConcept != null) {
      conceptsByConcept.close();
    }
    if (descriptionsByDescription != null) {
      descriptionsByDescription.close();
    }
    if (relationshipsBySourceConcept != null) {
      relationshipsBySourceConcept.close();
    }
    if (languageRefsetsByDescription != null) {
      languageRefsetsByDescription.close();
    }
    if (attributeValueRefsetsByRefCompId != null) {
      attributeValueRefsetsByRefCompId.close();
    }
    if (associationReferenceRefsetsByRefCompId != null) {
      associationReferenceRefsetsByRefCompId.close();
    }
    if (simpleRefsetsByConcept != null) {
      simpleRefsetsByConcept.close();
    }
    if (simpleMapRefsetsByConcept != null) {
      simpleMapRefsetsByConcept.close();
    }
    if (complexMapRefsetsByConcept != null) {
      complexMapRefsetsByConcept.close();
    }
    if (extendedMapRefsetsByConcept != null) {
      extendedMapRefsetsByConcept.close();
    }
  }

  /**
   * Load concepts.
   * 
   * @throws Exception the exception
   */
  private void loadConcepts() throws Exception {

    String line = "";
    objectCt = 0;

    // Begin transaction
    ContentService contentService = new ContentServiceJpa();
    contentService.setTransactionPerOperation(false);
    contentService.beginTransaction();

    while ((line = conceptsByConcept.readLine()) != null) {

      String fields[] = line.split("\t");
      Concept concept = new ConceptJpa();

      // Skip header
      if (!fields[0].equals("id")) {

        concept.setTerminologyId(fields[0]);
        concept.setEffectiveTime(dt.parse(fields[1]));
        concept.setActive(fields[2].equals("1") ? true : false);
        concept.setLastModified(new Date());
        concept.setLastModifiedBy("loader");
        concept.setModuleId(fields[3]);
        concept.setDefinitionStatusId(fields[4]);
        concept.setTerminology(terminology);
        concept.setTerminologyVersion(version);
        concept.setDefaultPreferredName("null");

        getLog().debug(
            "  Add concept " + concept.getTerminologyId() + " "
                + concept.getDefaultPreferredName());
        contentService.addConcept(concept);

        conceptCache.put(new String(fields[0] + concept.getTerminology()
            + concept.getTerminologyVersion()), concept);

        // regularly commit at intervals
        if (++objectCt % commitCt == 0) {
          getLog().info("  commit = " + objectCt);
          contentService.commit();
          contentService.beginTransaction();
        }
      }
    }

    // Commit any remaining objects
    contentService.commit();
    contentService.close();

    defaultPreferredNames.clear();

    // print memory information
    Runtime runtime = Runtime.getRuntime();
    getLog().info("MEMORY USAGE:");
    getLog().info(" Total: " + runtime.totalMemory());
    getLog().info(" Free:  " + runtime.freeMemory());
    getLog().info(" Max:   " + runtime.maxMemory());

  }

  /**
   * Load relationships.
   * 
   * @throws Exception the exception
   */
  private void loadRelationships() throws Exception {

    String line = "";
    objectCt = 0;

    // Begin transaction
    ContentService contentService = new ContentServiceJpa();
    contentService.setTransactionPerOperation(false);
    contentService.beginTransaction();

    while ((line = relationshipsBySourceConcept.readLine()) != null) {

      String fields[] = line.split("\t");
      Relationship relationship = new RelationshipJpa();

      // header
      if (!fields[0].equals("id")) {
        relationship.setTerminologyId(fields[0]);
        relationship.setEffectiveTime(dt.parse(fields[1]));
        relationship.setActive(fields[2].equals("1") ? true : false);
        relationship.setLastModified(new Date());
        relationship.setLastModifiedBy("loader");
        relationship.setModuleId(fields[3]);

        relationship.setRelationshipGroup(Integer.valueOf(fields[6]));
        relationship.setTypeId(fields[7]);
        relationship.setCharacteristicTypeId(fields[8]);
        relationship.setTerminology(terminology);
        relationship.setTerminologyVersion(version);
        relationship.setModifierId(fields[9]);

        Concept sourceConcept =
            getConcept(fields[4], relationship.getTerminology(),
                relationship.getTerminologyVersion(), contentService);
        Concept destinationConcept =
            getConcept(fields[5], relationship.getTerminology(),
                relationship.getTerminologyVersion(), contentService);

        if (sourceConcept != null && destinationConcept != null) {
          relationship.setSourceConcept(sourceConcept);
          relationship.setDestinationConcept(destinationConcept);

          contentService.addRelationship(relationship);

          // Regularly commit at intervals
          if (++objectCt % commitCt == 0) {
            getLog().info("  commit = " + objectCt);
            contentService.commit();
            contentService.beginTransaction();
          }
        } else {
          if (sourceConcept == null) {
            getLog().error(
                "ERROR: Relationship " + relationship.getTerminologyId()
                    + " references non-existent source concept " + fields[4]);
            errorFlag = true;
          }
          if (destinationConcept == null) {
            getLog().error(
                "ERROR: Relationship " + relationship.getTerminologyId()
                    + " references non-existent destination concept "
                    + fields[5]);
            errorFlag = true;
          }

        }
      }
    }

    // Commit any remaining objects
    contentService.commit();
    contentService.close();

    // Print memory information
    Runtime runtime = Runtime.getRuntime();
    getLog().info("MEMORY USAGE:");
    getLog().info(" Total: " + runtime.totalMemory());
    getLog().info(" Free:  " + runtime.freeMemory());
    getLog().info(" Max:   " + runtime.maxMemory());
  }

  /**
   * Load descriptions.
   * 
   * @throws Exception the exception
   */
  private void loadDescriptionsAndLanguageRefSets() throws Exception {

    Concept concept;
    Description description;
    LanguageRefSetMember language;
    // Setup counters
    int descCt = 0;
    int langCt = 0;
    int skipCt = 0;

    // Begin transaction
    ContentService contentService = new ContentServiceJpa();
    contentService.setTransactionPerOperation(false);
    contentService.beginTransaction();

    // Load and persist first description
    description = getNextDescription(contentService);

    // Load first language ref set member
    language = getNextLanguage();

    // Cycle over descriptions (-1 means descriptions are done)
    while (!description.getTerminologyId().equals("-1")) {

      // If current language ref set references a lexicographically "lower"
      // String terminologyId, SKIP: description is not in data set
      while (language.getDescription().getTerminologyId()
          .compareTo(description.getTerminologyId()) < 0
          && !language.getTerminologyId().equals("-1")) {
        getLog().error(
            "ERROR:     " + "Language Ref Set " + language.getTerminologyId()
                + " references non-existent description "
                + language.getDescription().getTerminologyId());
        errorFlag = true;
        language = getNextLanguage();
        skipCt++;
      }

      // Cycle over language ref sets until new description id found or end of
      // language ref sets found
      while (language.getDescription().getTerminologyId()
          .equals(description.getTerminologyId())
          && !language.getTerminologyId().equals("-1")) {

        // Set the description
        language.setDescription(description);
        description.addLanguageRefSetMember(language);
        langCt++;

        // Check if this language refset and description form the
        // defaultPreferredName
        if (description.isActive() && description.getTypeId().equals(dpnTypeId)
            && language.getRefSetId().equals(dpnRefSetId)
            && language.isActive()
            && language.getAcceptabilityId().equals(dpnAcceptabilityId)) {

          // Retrieve the concept for this description
          concept = description.getConcept();
          if (defaultPreferredNames.get(concept.getId()) != null) {
            getLog().info(
                "Multiple default preferred names for concept "
                    + concept.getTerminologyId());
            getLog().info(
                "  " + "Existing: "
                    + defaultPreferredNames.get(concept.getId()));
            getLog().info("  " + "Replaced: " + description.getTerm());
          }
          defaultPreferredNames.put(concept.getId(), description.getTerm());

        }

        // Get the next language ref set member
        language = getNextLanguage();
      }

      // Persist the description
      contentService.addDescription(description);

      // Get the next description
      description = getNextDescription(contentService);

      // Increment description count
      descCt++;

      // Regularly commit at intervals
      if (descCt % commitCt == 0) {
        getLog().info("  commit = " + descCt);
        contentService.commit();
        contentService.beginTransaction();
      }

    }

    // Commit any remaining objects
    contentService.commit();
    contentService.close();

    getLog().info("      descCt = " + descCt);
    getLog().info("      langCt = " + langCt);
    getLog().info("      skipCt = " + skipCt);

    // print memory information
    Runtime runtime = Runtime.getRuntime();
    getLog().info("MEMORY USAGE:");
    getLog().info(" Total: " + runtime.totalMemory());
    getLog().info(" Free:  " + runtime.freeMemory());
    getLog().info(" Max:   " + runtime.maxMemory());
  }

  /**
   * Sets the default preferred names.
   * 
   * @throws Exception the exception
   */
  private void setDefaultPreferredNames() throws Exception {

    // Begin transaction
    ContentService contentService = new ContentServiceJpa();
    contentService.setTransactionPerOperation(false);
    contentService.beginTransaction();

    Iterator<Concept> conceptIterator = conceptCache.values().iterator();
    objectCt = 0;
    while (conceptIterator.hasNext()) {
      Concept cachedConcept = conceptIterator.next();
      Concept dbConcept = contentService.getConcept(cachedConcept.getId());
      dbConcept.getDescriptions();
      dbConcept.getRelationships();
      if (defaultPreferredNames.get(dbConcept.getId()) != null) {
        dbConcept.setDefaultPreferredName(defaultPreferredNames.get(dbConcept
            .getId()));
      } else {
        dbConcept.setDefaultPreferredName("No default preferred name found");
      }
      contentService.updateConcept(dbConcept);
      if (++objectCt % commitCt == 0) {
        getLog().info("  commit = " + objectCt);
        contentService.commit();
        contentService.beginTransaction();
      }
    }
    contentService.commit();
    contentService.close();

    // print memory information
    Runtime runtime = Runtime.getRuntime();
    getLog().info("MEMORY USAGE:");
    getLog().info(" Total: " + runtime.totalMemory());
    getLog().info(" Free:  " + runtime.freeMemory());
    getLog().info(" Max:   " + runtime.maxMemory());

  }

  /**
   * Returns the next description.
   * @param contentService
   * 
   * @return the next description
   * @throws Exception the exception
   */
  private Description getNextDescription(ContentService contentService)
    throws Exception {

    String line, fields[];
    Description description = new DescriptionJpa();
    description.setTerminologyId("-1");

    if ((line = descriptionsByDescription.readLine()) != null) {

      line = line.replace("\r", "");
      fields = line.split("\t");

      if (!fields[0].equals("id")) { // header

        description.setTerminologyId(fields[0]);
        description.setEffectiveTime(dt.parse(fields[1]));
        description.setActive(fields[2].equals("1") ? true : false);
        description.setLastModified(new Date());
        description.setLastModifiedBy("loader");
        description.setModuleId(fields[3]);

        description.setLanguageCode(fields[5]);
        description.setTypeId(fields[6]);
        description.setTerm(fields[7]);
        description.setCaseSignificanceId(fields[8]);
        description.setTerminology(terminology);
        description.setTerminologyVersion(version);

        // set concept from cache
        Concept concept =
            getConcept(fields[4], description.getTerminology(),
                description.getTerminologyVersion(), contentService);

        if (concept != null) {
          description.setConcept(concept);
        } else {
          getLog().error(
              "ERROR: Description " + description.getTerminologyId()
                  + " references non-existent concept " + fields[4]);
          errorFlag = true;
        }
        // otherwise get next line
      } else {
        description = getNextDescription(contentService);
      }
    }

    return description;
  }

  /**
   * Utility function to return the next line of language ref set files in
   * object form.
   * 
   * @return a partial language ref set member (lacks full description)
   * @throws Exception the exception
   */
  private LanguageRefSetMember getNextLanguage() throws Exception {

    String line, fields[];
    LanguageRefSetMember member = new LanguageRefSetMemberJpa();
    member.setTerminologyId("-1");

    // if non-null
    if ((line = languageRefsetsByDescription.readLine()) != null) {

      line = line.replace("\r", "");

      fields = line.split("\t");

      if (!fields[0].equals("id")) { // header line

        // Universal RefSet attributes
        member.setTerminologyId(fields[0]);
        member.setEffectiveTime(dt.parse(fields[1]));
        member.setActive(fields[2].equals("1") ? true : false);
        member.setLastModified(new Date());
        member.setLastModifiedBy("loader");
        member.setModuleId(fields[3]);
        member.setRefSetId(fields[4]);

        // Language unique attributes
        member.setAcceptabilityId(fields[6]);

        // Terminology attributes
        member.setTerminology(terminology);
        member.setTerminologyVersion(version);

        // Set a dummy description with terminology id only
        Description description = new DescriptionJpa();
        description.setTerminologyId(fields[5]);
        member.setDescription(description);

        // if header line, get next record
      } else {
        member = getNextLanguage();
      }

      // if null, set a dummy description value to avoid null-pointer exceptions
      // in main loop
    } else {
      Description description = new DescriptionJpa();
      description.setTerminologyId("-1");
      member.setDescription(description);
    }

    return member;
  }

  /**
   * Load AttributeRefSets (Content).
   * 
   * @throws Exception the exception
   */
  @SuppressWarnings("boxing")
  private void loadAttributeValueRefSets() throws Exception {

    String line = "";
    objectCt = 0;

    // Begin transaction
    ContentService contentService = new ContentServiceJpa();
    contentService.setTransactionPerOperation(false);
    contentService.beginTransaction();

    // Iterate through attribute value entries
    while ((line = attributeValueRefsetsByRefCompId.readLine()) != null) {

      line = line.replace("\r", "");
      String fields[] = line.split("\t");
      AttributeValueRefSetMember<Concept> member =
          new AttributeValueConceptRefSetMemberJpa();

      if (!fields[0].equals("id")) { // header

        // Universal RefSet attributes
        member.setTerminologyId(fields[0]);
        member.setEffectiveTime(dt.parse(fields[1]));
        member.setActive(fields[2].equals("1") ? true : false);
        member.setLastModified(new Date());
        member.setLastModifiedBy("loader");
        member.setModuleId(fields[3]);
        member.setRefSetId(fields[4]);

        // AttributeValueRefSetMember unique attributes
        member.setValueId(fields[6]);

        // Terminology attributes
        member.setTerminology(terminology);
        member.setTerminologyVersion(version);

        // Retrieve concept -- firstToken is referencedComponentId
        Concept concept =
            getConcept(fields[5], member.getTerminology(),
                member.getTerminologyVersion(), contentService);

        if (concept != null) {

          member.setComponent(concept);
          contentService.addAttributeValueRefSetMember(member);

          // regularly commit at intervals
          if (++objectCt % commitCt == 0) {
            getLog().info("  commit = " + objectCt);
            contentService.commit();
            contentService.beginTransaction();
          }
        } else {
          // This is a description attribute value refset and can be skipped
        }
      }
    }

    // commit any remaining objects
    contentService.commit();
    contentService.close();

    // print memory information
    Runtime runtime = Runtime.getRuntime();
    getLog().info("MEMORY USAGE:");
    getLog().info(" Total: " + runtime.totalMemory());
    getLog().info(" Free:  " + runtime.freeMemory());
    getLog().info(" Max:   " + runtime.maxMemory());
  }

  /**
   * Load Association Reference Refset (Content).
   * 
   * @throws Exception the exception
   */
  @SuppressWarnings("boxing")
  private void loadAssociationReferenceRefSets() throws Exception {

    String line = "";
    objectCt = 0;

    // begin transaction
    ContentService contentService = new ContentServiceJpa();
    contentService.setTransactionPerOperation(false);
    contentService.beginTransaction();

    while ((line = associationReferenceRefsetsByRefCompId.readLine()) != null) {

      line = line.replace("\r", "");
      String fields[] = line.split("\t");
      AssociationReferenceRefSetMember<Concept> member =
          new AssociationReferenceConceptRefSetMemberJpa();

      if (!fields[0].equals("id")) { // header

        // Universal RefSet attributes
        member.setTerminologyId(fields[0]);
        member.setEffectiveTime(dt.parse(fields[1]));
        member.setActive(fields[2].equals("1") ? true : false);
        member.setLastModified(new Date());
        member.setLastModifiedBy("loader");
        member.setModuleId(fields[3]);
        member.setRefSetId(fields[4]);

        // AttributeValueRefSetMember unique attributes
        member.setTargetComponentId(fields[6]);

        // Terminology attributes
        member.setTerminology(terminology);
        member.setTerminologyVersion(version);

        // Retrieve concept -- firstToken is referencedComponentId
        Concept concept =
            getConcept(fields[5], member.getTerminology(),
                member.getTerminologyVersion(), contentService);

        if (concept != null) {

          member.setComponent(concept);
          contentService.addAssociationReferenceRefSetMember(member);

          // regularly commit at intervals
          if (++objectCt % commitCt == 0) {
            getLog().info("  commit = " + objectCt);
            contentService.commit();
            contentService.beginTransaction();
          }
        } else {
          // This is a description association reference and can be skipped
        }
      }
    }

    // Commit any remaining objects
    contentService.commit();
    contentService.close();

    // print memory information
    Runtime runtime = Runtime.getRuntime();
    getLog().info("MEMORY USAGE:");
    getLog().info(" Total: " + runtime.totalMemory());
    getLog().info(" Free:  " + runtime.freeMemory());
    getLog().info(" Max:   " + runtime.maxMemory());
  }

  /**
   * Load SimpleRefSets (Content).
   * 
   * @throws Exception the exception
   */

  private void loadSimpleRefSets() throws Exception {

    String line = "";
    objectCt = 0;

    // Begin transaction
    ContentService contentService = new ContentServiceJpa();
    contentService.setTransactionPerOperation(false);
    contentService.beginTransaction();

    // Iterate through simple refset entries
    while ((line = simpleRefsetsByConcept.readLine()) != null) {

      line = line.replace("\r", "");
      String fields[] = line.split("\t");
      SimpleRefSetMember member = new SimpleRefSetMemberJpa();

      if (!fields[0].equals("id")) { // header

        // Universal RefSet attributes
        member.setTerminologyId(fields[0]);
        member.setEffectiveTime(dt.parse(fields[1]));
        member.setActive(fields[2].equals("1") ? true : false);
        member.setLastModified(new Date());
        member.setLastModifiedBy("loader");
        member.setModuleId(fields[3]);
        member.setRefSetId(fields[4]);

        // Terminology attributes
        member.setTerminology(terminology);
        member.setTerminologyVersion(version);

        // Retrieve Concept -- firstToken is referencedComonentId
        Concept concept =
            getConcept(fields[5], member.getTerminology(),
                member.getTerminologyVersion(), contentService);

        if (concept != null) {
          member.setConcept(concept);
          contentService.addSimpleRefSetMember(member);

          // regularly commit at intervals
          if (++objectCt % commitCt == 0) {
            getLog().info("  commit = " + objectCt);
            contentService.commit();
            contentService.beginTransaction();
          }
        } else {
          getLog().error(
              "ERROR: simpleRefSetMember " + member.getTerminologyId()
                  + " references non-existent concept " + fields[5]);
          errorFlag = true;
        }
      }
    }

    // commit any remaining objects
    contentService.commit();
    contentService.close();

    // print memory information
    Runtime runtime = Runtime.getRuntime();
    getLog().info("MEMORY USAGE:");
    getLog().info(" Total: " + runtime.totalMemory());
    getLog().info(" Free:  " + runtime.freeMemory());
    getLog().info(" Max:   " + runtime.maxMemory());
  }

  /**
   * Load SimpleMapRefSets (Crossmap).
   * 
   * @throws Exception the exception
   */
  private void loadSimpleMapRefSets() throws Exception {

    String line = "";
    objectCt = 0;

    // begin transaction
    ContentService contentService = new ContentServiceJpa();
    contentService.setTransactionPerOperation(false);
    contentService.beginTransaction();

    while ((line = simpleMapRefsetsByConcept.readLine()) != null) {

      line = line.replace("\r", "");
      String fields[] = line.split("\t");
      SimpleMapRefSetMember member = new SimpleMapRefSetMemberJpa();

      if (!fields[0].equals("id")) { // header

        // Universal RefSet attributes
        member.setTerminologyId(fields[0]);
        member.setEffectiveTime(dt.parse(fields[1]));
        member.setActive(fields[2].equals("1") ? true : false);
        member.setLastModified(new Date());
        member.setLastModifiedBy("loader");
        member.setModuleId(fields[3]);
        member.setRefSetId(fields[4]);

        // SimpleMap unique attributes
        member.setMapTarget(fields[6]);

        // Terminology attributes
        member.setTerminology(terminology);
        member.setTerminologyVersion(version);

        // Retrieve concept -- firstToken is referencedComponentId
        Concept concept =
            getConcept(fields[5], member.getTerminology(),
                member.getTerminologyVersion(), contentService);

        if (concept != null) {
          member.setConcept(concept);
          contentService.addSimpleMapRefSetMember(member);

          // regularly commit at intervals
          if (++objectCt % commitCt == 0) {
            getLog().info("  commit = " + objectCt);
            contentService.commit();
            contentService.beginTransaction();
          }
        } else {
          getLog().error(
              "ERROR: simpleMapRefSetMember " + member.getTerminologyId()
                  + " references non-existent concept " + fields[5]);
          errorFlag = true;
        }
      }
    }

    // commit any remaining objects
    contentService.commit();
    contentService.close();

    // print memory information
    Runtime runtime = Runtime.getRuntime();
    getLog().info("MEMORY USAGE:");
    getLog().info(" Total: " + runtime.totalMemory());
    getLog().info(" Free:  " + runtime.freeMemory());
    getLog().info(" Max:   " + runtime.maxMemory());
  }

  /**
   * Load ComplexMapRefSets (Crossmap).
   * 
   * @throws Exception the exception
   */
  private void loadComplexMapRefSets() throws Exception {

    String line = "";
    objectCt = 0;

    // begin transaction
    ContentService contentService = new ContentServiceJpa();
    contentService.setTransactionPerOperation(false);
    contentService.beginTransaction();

    while ((line = complexMapRefsetsByConcept.readLine()) != null) {

      line = line.replace("\r", "");
      String fields[] = line.split("\t");
      ComplexMapRefSetMember member = new ComplexMapRefSetMemberJpa();

      if (!fields[0].equals("id")) { // header

        member.setTerminologyId(fields[0]);
        member.setEffectiveTime(dt.parse(fields[1]));
        member.setActive(fields[2].equals("1") ? true : false);
        member.setLastModified(new Date());
        member.setLastModifiedBy("loader");
        member.setModuleId(fields[3]);
        member.setRefSetId(fields[4]);
        // conceptId

        // ComplexMap unique attributes
        member.setMapGroup(Integer.parseInt(fields[6]));
        member.setMapPriority(Integer.parseInt(fields[7]));
        member.setMapRule(fields[8]);
        member.setMapAdvice(fields[9]);
        member.setMapTarget(fields[10]);
        member.setMapRelationId(fields[11]);

        // ComplexMap unique attributes NOT set by file (mapBlock
        // elements)
        member.setMapBlock(0); // default value
        member.setMapBlockRule(null); // no default
        member.setMapBlockAdvice(null); // no default

        // Terminology attributes
        member.setTerminology(terminology);
        member.setTerminologyVersion(version);

        // set Concept
        Concept concept =
            getConcept(fields[5], member.getTerminology(),
                member.getTerminologyVersion(), contentService);

        if (concept != null) {
          member.setConcept(concept);
          contentService.addComplexMapRefSetMember(member);

          // regularly commit at intervals
          if (++objectCt % commitCt == 0) {
            getLog().info("  commit = " + objectCt);
            contentService.commit();
            contentService.beginTransaction();
          }
        } else {
          getLog().error(
              "ERROR: complexMapRefSetMember " + member.getTerminologyId()
                  + " references non-existent concept " + fields[5]);
          errorFlag = true;
        }

      }
    }

    // commit any remaining objects
    contentService.commit();
    contentService.close();

    // print memory information
    Runtime runtime = Runtime.getRuntime();
    getLog().info("MEMORY USAGE:");
    getLog().info(" Total: " + runtime.totalMemory());
    getLog().info(" Free:  " + runtime.freeMemory());
    getLog().info(" Max:   " + runtime.maxMemory());
  }

  /**
   * Load ExtendedMapRefSets (Crossmap).
   * 
   * @throws Exception the exception
   */

  // NOTE: ExtendedMap RefSets are loaded into ComplexMapRefSetMember
  // where mapRelationId = mapCategoryId
  private void loadExtendedMapRefSets() throws Exception {

    String line = "";
    objectCt = 0;

    // begin transaction
    ContentService contentService = new ContentServiceJpa();
    contentService.setTransactionPerOperation(false);
    contentService.beginTransaction();

    while ((line = extendedMapRefsetsByConcept.readLine()) != null) {

      line = line.replace("\r", "");
      String fields[] = line.split("\t");
      ComplexMapRefSetMember member = new ComplexMapRefSetMemberJpa();

      if (!fields[0].equals("id")) { // header

        member.setTerminologyId(fields[0]);
        member.setEffectiveTime(dt.parse(fields[1]));
        member.setActive(fields[2].equals("1") ? true : false);
        member.setLastModified(new Date());
        member.setLastModifiedBy("loader");
        member.setModuleId(fields[3]);
        member.setRefSetId(fields[4]);
        // conceptId

        // ComplexMap unique attributes
        member.setMapGroup(Integer.parseInt(fields[6]));
        member.setMapPriority(Integer.parseInt(fields[7]));
        member.setMapRule(fields[8]);
        member.setMapAdvice(fields[9]);
        member.setMapTarget(fields[10]);
        member.setMapRelationId(fields[12]);

        // ComplexMap unique attributes NOT set by file (mapBlock
        // elements)
        member.setMapBlock(1); // default value
        member.setMapBlockRule(null); // no default
        member.setMapBlockAdvice(null); // no default

        // Terminology attributes
        member.setTerminology(terminology);
        member.setTerminologyVersion(version);

        // set Concept
        Concept concept =
            getConcept(fields[5], member.getTerminology(),
                member.getTerminologyVersion(), contentService);

        if (concept != null) {
          member.setConcept(concept);
          contentService.addComplexMapRefSetMember(member);

          // regularly commit at intervals
          if (++objectCt % commitCt == 0) {
            getLog().info("  commit = " + objectCt);
            contentService.commit();
            contentService.beginTransaction();
          }
        } else {
          getLog().error(
              "ERROR: complexMapRefSetMember " + member.getTerminologyId()
                  + " references non-existent concept " + fields[5]);
          errorFlag = true;
        }

      }
    }

    // commit any remaining objects
    contentService.commit();
    contentService.close();

    // print memory information
    Runtime runtime = Runtime.getRuntime();
    getLog().info("MEMORY USAGE:");
    getLog().info(" Total: " + runtime.totalMemory());
    getLog().info(" Free:  " + runtime.freeMemory());
    getLog().info(" Max:   " + runtime.maxMemory());
  }
}
