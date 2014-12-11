/*
 * 
 */
package org.ihtsdo.otf.ts.mojo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.DescriptionList;
import org.ihtsdo.otf.ts.helpers.LanguageRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.RelationshipList;
import org.ihtsdo.otf.ts.helpers.ReleaseInfo;
import org.ihtsdo.otf.ts.jpa.algo.TransitiveClosureAlgorithm;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.HistoryServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.MetadataServiceJpa;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa;
import org.ihtsdo.otf.ts.rf2.jpa.LanguageRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RelationshipJpa;
import org.ihtsdo.otf.ts.services.ContentService;
import org.ihtsdo.otf.ts.services.HistoryService;
import org.ihtsdo.otf.ts.services.MetadataService;
import org.ihtsdo.otf.ts.services.helpers.ConceptReportHelper;

/**
 * Goal which loads an RF2 Delta of SNOMED CT data
 * 
 * See admin/loader/pom.xml for sample usage
 * 
 * @goal load-rf2-delta
 * 
 * @phase package
 */
public class TerminologyRf2DeltaLoader extends AbstractMojo {

  /**
   * Name of terminology to be loaded.
   * 
   * @parameter
   * @required
   */
  private String terminology;

  /**
   * The input directory
   * 
   * @parameter
   * @required
   */
  private String inputDir;

  /** The terminology version. */
  private String terminologyVersion;

  /** the defaultPreferredNames type id. */
  private String dpnTypeId;

  /** The dpn ref set id. */
  private String dpnRefSetId;

  /** The dpn acceptability id. */
  private String dpnAcceptabilityId;

  /** The concept reader. */
  private BufferedReader conceptReader;

  /** The description reader. */
  private BufferedReader descriptionReader;

  /** The text definition reader. */
  private BufferedReader textDefinitionReader;

  /** The relationship reader. */
  private BufferedReader relationshipReader;

  /** The stated relationship reader. */
  private BufferedReader statedRelationshipReader;

  /** The language reader. */
  private BufferedReader languageReader;

  /** progress tracking variables. */
  private int objectCt; //

  /** The ft. */
  private final static FastDateFormat ft = FastDateFormat
      .getInstance("hh:mm:ss a");

  /** The start time. */
  long startTime;

  /** The time at which drip feed was started. */
  private Date deltaLoaderStartDate = new Date();

  /** Content and Mapping Services. */
  private HistoryService historyService = null;

  /** The concept cache. */
  private Map<String, Concept> conceptCache = new HashMap<>();

  /** The description cache. */
  private Map<String, Description> descriptionCache = new HashMap<>();

  /** The relationship cache. */
  private Map<String, Relationship> relationshipCache = new HashMap<>();

  /** The language ref set member cache. */
  private Map<String, LanguageRefSetMember> languageRefSetMemberCache =
      new HashMap<>();

  // These track data that existed prior to the delta loader run

  /** The delta concept ids. */
  private Set<String> deltaConceptIds = new HashSet<>();

  /** The existing concept cache. */
  private Map<String, Concept> existingConceptCache = new HashMap<>();

  /** The existing description ids. */
  private Set<String> existingDescriptionIds = new HashSet<>();

  /** The existing relationship ids. */
  private Set<String> existingRelationshipIds = new HashSet<>();

  /** The existing language ref set member ids. */
  private Set<String> existingLanguageRefSetMemberIds = new HashSet<>();

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.maven.plugin.Mojo#execute()
   */
  @Override
  public void execute() throws MojoFailureException {

    try {
      getLog().info("Starting RF2 delta loader");
      getLog().info("  terminology = " + terminology);
      getLog().info("  inputDir = " + inputDir);

      // Create and configure services and variables and open files
      setup();

      // Precache all existing concept entires
      // (not connected data like rels/descs)
      getLog().info("  Cache concepts");
      ConceptList conceptList =
          historyService.getAllConcepts(terminology, terminologyVersion);
      for (Concept c : conceptList.getObjects()) {
        existingConceptCache.put(c.getTerminologyId(), c);
      }
      getLog().info("    count = " + conceptList.getCount());

      // Precache the description, langauge refset, and relationship id lists
      // THIS IS FOR DEBUG/QUALITY ASSURANCE
      getLog().info("  Construct terminology id sets for quality assurance");
      getLog().info("  Cache description ids");
      existingDescriptionIds =
          new HashSet<>(historyService.getAllDescriptionTerminologyIds(
              terminology, terminologyVersion).getObjects());
      getLog().info("    count = " + existingDescriptionIds.size());
      getLog().info("  Cache language refset member ids");
      existingLanguageRefSetMemberIds =
          new HashSet<>(historyService
              .getAllLanguageRefSetMemberTerminologyIds(terminology,
                  terminologyVersion).getObjects());
      getLog().info("    count = " + existingLanguageRefSetMemberIds.size());
      getLog().info("  Cache relationship ids");
      existingRelationshipIds =
          new HashSet<>(historyService.getAllRelationshipTerminologyIds(
              terminology, terminologyVersion).getObjects());
      getLog().info("    count = " + existingRelationshipIds.size());

      // Load delta data
      loadDelta();

      // Compute the number of modified objects of each type
      getLog().info("  Computing number of modified objects...");
      int nConceptsUpdated = 0;
      int nDescriptionsUpdated = 0;
      int nLanguagesUpdated = 0;
      int nRelationshipsUpdated = 0;

      for (Concept c : conceptCache.values()) {
        if (c.getEffectiveTime() == null) {
          nConceptsUpdated++;
        }
      }
      for (Relationship r : relationshipCache.values()) {
        if (r.getEffectiveTime() == null) {
          nRelationshipsUpdated++;
        }
      }
      for (Description d : descriptionCache.values()) {
        if (d.getEffectiveTime() == null) {
          nDescriptionsUpdated++;
        }
      }

      for (LanguageRefSetMember l : languageRefSetMemberCache.values()) {
        if (l.getEffectiveTime() == null) {
          nLanguagesUpdated++;
        }
      }

      // Report counts
      getLog().info("  Cached objects modified by this delta");
      getLog().info("    " + nConceptsUpdated + " concepts");
      getLog().info("    " + nDescriptionsUpdated + " descriptions");
      getLog().info("    " + nRelationshipsUpdated + " relationships");
      getLog().info("    " + nLanguagesUpdated + " language ref set members");

      // QA
      getLog()
          .info(
              "Checking database contents against number of previously modified objects");
      ConceptList modifiedConcepts =
          historyService.findConceptsModifiedSinceDate(terminology,
              deltaLoaderStartDate, null);
      RelationshipList modifiedRelationships =
          historyService.findRelationshipsModifiedSinceDate(terminology,
              deltaLoaderStartDate, null);
      DescriptionList modifiedDescriptions =
          historyService.findDescriptionsModifiedSinceDate(terminology,
              deltaLoaderStartDate, null);
      LanguageRefSetMemberList modifiedLanguageRefSetMembers =
          historyService.findLanguageRefSetMembersModifiedSinceDate(
              terminology, deltaLoaderStartDate, null);

      // Report
      getLog().info(
          (modifiedConcepts.getCount() != nConceptsUpdated) ? "  "
              + nConceptsUpdated + " concepts expected, found "
              + modifiedConcepts.getCount() : "  Concept count matches");
      getLog().info(
          (modifiedRelationships.getCount() != nRelationshipsUpdated) ? "  "
              + nRelationshipsUpdated + " relationships expected, found"
              + modifiedRelationships.getCount()
              : "  Relationship count matches");
      getLog()
          .info(
              (modifiedDescriptions.getCount() != nDescriptionsUpdated) ? "  "
                  + nDescriptionsUpdated + " descriptions expected, found"
                  + modifiedDescriptions.getCount()
                  : "  Description count matches");
      getLog().info(
          (modifiedLanguageRefSetMembers.getCount() != nLanguagesUpdated)
              ? "  " + nLanguagesUpdated
                  + " languageRefSetMembers expected, found"
                  + modifiedLanguageRefSetMembers.getCount()
              : "  LanguageRefSetMember count matches");
      getLog().info("Computing preferred names for modified concepts");

      computeDefaultPreferredNames();

      // Commit the content changes
      getLog().info("Committing...");
      historyService.commit();
      getLog().info("...done");

    } catch (Exception e) {
      e.printStackTrace();
      throw new MojoFailureException("Unexpected exception:", e);
    }

  }

  /**
   * Instantiate global vars.
   * 
   * @throws Exception the exception
   */
  private void setup() throws Exception {

    Properties config = ConfigUtility.getConfigProperties();

    // instantiate the services
    historyService = new HistoryServiceJpa();

    // set the transaction per operation on the service managers
    historyService.setTransactionPerOperation(false);

    // initialize the transactions
    historyService.beginTransaction();

    // Check the delta file directory=
    if (!new File(inputDir).exists()) {
      throw new MojoFailureException("Specified input dir does not exist");
    }

    // get the first file for determining
    File files[] = new File(inputDir).listFiles();
    if (files.length == 0)
      throw new MojoFailureException(
          "Could not determine terminology version, no files exist");

    // get version from file name, with expected format
    // '...INT_YYYYMMDD.txt'
    String fileName = files[0].getName();
    if (fileName.matches("sct2_*_INT_*.txt")) {
      throw new MojoFailureException(
          "Terminology filenames do not match pattern 'sct2_(ComponentName)_INT_(Date).txt");
    }
    terminologyVersion =
        fileName.substring(fileName.length() - 12, fileName.length() - 4);

    //
    // Verify that there is a release info for this version that is
    // marked as "isPlanned"
    //
    ReleaseInfo releaseInfo =
        historyService.getReleaseInfo(terminology, terminologyVersion);
    if (releaseInfo == null) {
      throw new Exception("A release info must exist for " + terminologyVersion);
    } else if (!releaseInfo.isPlanned()) {
      throw new Exception("Release info for " + terminologyVersion
          + " is not marked as planned'");
    } else if (releaseInfo.isPublished()) {
      throw new Exception("Release info for " + terminologyVersion
          + " is marked as published");
    }

    // Previous computation of terminology version is based on file name
    // but for delta/daily build files, this is not the current version
    // look up the current version instead
    MetadataService metadataService = new MetadataServiceJpa();
    terminologyVersion = metadataService.getLatestVersion(terminology);
    metadataService.close();
    if (terminologyVersion == null) {
      throw new Exception("Unable to determine terminology version.");
    }

    // set the parameters for determining defaultPreferredNames
    dpnTypeId = config.getProperty("loader.defaultPreferredNames.typeId");
    dpnRefSetId = config.getProperty("loader.defaultPreferredNames.refSetId");
    dpnAcceptabilityId =
        config.getProperty("loader.defaultPreferredNames.acceptabilityId");

    // output relevant properties/settings to console
    getLog().info("Terminology Version: " + terminologyVersion);
    getLog().info("Default preferred name settings:");
    getLog().info("  typeId:          " + dpnTypeId);
    getLog().info("  refSetId:        " + dpnRefSetId);
    getLog().info("  acceptabilityId: " + dpnAcceptabilityId);

    // Open files
    instantiateFileReaders();
  }

  /**
   * Instantiate file readers.
   * 
   * @throws Exception the exception
   */
  private void instantiateFileReaders() throws Exception {

    getLog().info("Opening readers for Terminology files...");

    // concepts file
    for (File f : new File(inputDir).listFiles()) {
      if (f.getName().contains("_Concept_Delta_")) {
        getLog().info("  Concept file:      " + f.getName());
        conceptReader = new BufferedReader(new FileReader(f));
      } else if (f.getName().contains("_StatedRelationship_")) {
        getLog().info("  Relationship file: " + f.getName());
        statedRelationshipReader = new BufferedReader(new FileReader(f));
      } else if (f.getName().contains("_Relationship_")) {
        getLog().info("  Relationship file: " + f.getName());
        relationshipReader = new BufferedReader(new FileReader(f));
      } else if (f.getName().contains("_Description_")) {
        getLog().info("  Description file: " + f.getName());
        descriptionReader = new BufferedReader(new FileReader(f));
      } else if (f.getName().contains("_TextDefinition_")) {
        getLog().info("  Text Definition file: " + f.getName());
        textDefinitionReader = new BufferedReader(new FileReader(f));
      } else if (f.getName().contains("_LanguageDelta-en")) {
        getLog().info("  Language file:    " + f.getName());
        languageReader = new BufferedReader(new FileReader(f));
      }
    }

    // check file readers were opened successfully
    if (conceptReader == null)
      throw new MojoFailureException("Could not open concept file reader");
    if (relationshipReader == null)
      throw new MojoFailureException("Could not open relationship file reader");
    if (statedRelationshipReader == null)
      throw new MojoFailureException(
          "Could not open stated relationship file reader");
    if (descriptionReader == null)
      throw new MojoFailureException("Could not open description file reader");
    if (languageReader == null)
      throw new MojoFailureException(
          "Could not open language ref set member file reader");
  }

  /**
   * Load delta.
   *
   * @throws Exception the exception
   */
  private void loadDelta() throws Exception {
    getLog().info("  Load delta data");

    // Load concepts
    if (conceptReader != null) {
      getLog().info("    Loading Concepts ...");
      startTime = System.nanoTime();
      loadConcepts(conceptReader);
      getLog().info(
          "      evaluated = " + Integer.toString(objectCt) + " (Ended at "
              + ft.format(new Date()) + ")");
    }

    // Load relationships
    if (relationshipReader != null) {
      getLog().info("    Loading Relationships ...");
      startTime = System.nanoTime();
      loadRelationships(relationshipReader);
      getLog().info(
          "      evaluated = " + Integer.toString(objectCt) + " (Ended at "
              + ft.format(new Date()) + ")");
    }

    // Load relationships
    if (statedRelationshipReader != null) {
      getLog().info("    Loading Stated Relationships ...");
      startTime = System.nanoTime();
      loadRelationships(statedRelationshipReader);
      getLog().info(
          "      evaluated = " + Integer.toString(objectCt) + " (Ended at "
              + ft.format(new Date()) + ")");
    }

    // Load descriptions
    if (descriptionReader != null) {
      getLog().info("    Loading Descriptions ...");
      startTime = System.nanoTime();
      loadDescriptions(descriptionReader);
      getLog().info(
          "      evaluated = " + Integer.toString(objectCt) + " (Ended at "
              + ft.format(new Date()) + ")");
    }

    // Load text definitions
    if (descriptionReader != null) {
      getLog().info("    Loading Text Definitions...");
      startTime = System.nanoTime();
      loadDescriptions(textDefinitionReader);
      getLog().info(
          "      evaluated = " + Integer.toString(objectCt) + " (Ended at "
              + ft.format(new Date()) + ")");
    }

    // Load language refset members
    if (languageReader != null) {
      getLog().info("    Loading Language Ref Sets...");
      startTime = System.nanoTime();
      loadLanguageRefSetMembers(languageReader);
      getLog().info(
          "      evaluated = " + Integer.toString(objectCt) + " (Ended at "
              + ft.format(new Date()) + ")");
    }

    // Skip other delta data structures
    // TODO: implement this

    // Remove concepts in the DB that were created by prior
    // deltas that no longer exist in the delta
    getLog().info("    Retire non-existent concepts..");
    // TODO - bring this back once algo is worked out
    // retireRemovedConcepts();

    // Compute transitive closure
    MetadataService metadataService = new MetadataServiceJpa();
    String terminologyVersion = metadataService.getLatestVersion(terminology);
    Map<String, String> hierRelTypeMap =
        metadataService.getHierarchicalRelationshipTypes(terminology,
            terminologyVersion);
    String isaRelType = hierRelTypeMap.keySet().iterator().next().toString();
    metadataService.close();
    ContentService contentService = new ContentServiceJpa();
    contentService.setLastModifiedFlag(false);

    // Clear prior transitive closure
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
      getLog().info("    concept = " + c.getTerminologyId());
      getLog().info("    concept.rels.ct = " + c.getRelationships().size());
      getLog().info("    isaRelType = " + isaRelType);
      for (Relationship r : c.getRelationships()) {
        getLog().info(
            "      rel = " + r.getTerminologyId() + ", " + r.isActive() + ", "
                + r.getTypeId());
        if (r.isActive() && r.getTypeId().equals(isaRelType)) {
          conceptId = r.getDestinationConcept().getTerminologyId();
          continue OUTER;
        }
      }
      rootId = conceptId;
      break;
    }

    getLog().info(
        "  Compute transitive closure from rootId " + rootId + " for "
            + terminology + ", " + terminologyVersion);
    TransitiveClosureAlgorithm algo = new TransitiveClosureAlgorithm();
    algo.setTerminology(terminology);
    algo.setTerminologyVersion(terminologyVersion);
    algo.reset();
    Long rootIdLong =
        contentService
            .getSingleConcept(rootId, terminology, terminologyVersion).getId();
    algo.setRootId(rootIdLong);
    // TODO: turn this back on
    // algo.compute();
    algo.close();

    contentService.close();

  }

  /**
   * Loads the concepts from the delta files.
   *
   * @param reader the reader
   * @throws Exception the exception
   */
  private void loadConcepts(BufferedReader reader) throws Exception {

    // Setup vars
    String line;
    objectCt = 0;
    int objectsAdded = 0;
    int objectsUpdated = 0;

    // Iterate through concept reader
    while ((line = reader.readLine()) != null) {

      // Split line
      String fields[] = line.split("\t");

      // if not header
      if (!fields[0].equals("id")) {

        // Check if concept exists from before
        Concept concept = existingConceptCache.get(fields[0]);

        // Track all delta concept ids so we can properly remove concepts later.
        deltaConceptIds.add(fields[0]);

        // Setup delta concept (either new or based on existing one)
        Concept newConcept = null;
        if (concept == null) {
          newConcept = new ConceptJpa();
        } else {
          newConcept = new ConceptJpa(concept, true, true);
        }

        // Set fields
        newConcept.setTerminologyId(fields[0]);
        // effective time is left null - to indicate a change
        newConcept.setActive(fields[2].equals("1") ? true : false);
        newConcept.setModuleId(fields[3]);
        newConcept.setDefinitionStatusId(fields[4]);
        newConcept.setTerminology(terminology);
        newConcept.setTerminologyVersion(terminologyVersion);
        newConcept.setDefaultPreferredName("TBD");
        newConcept.setLastModifiedBy("loader");

        // If concept is new, add it
        if (concept == null) {
          getLog().info(
              "        add concept " + newConcept.getTerminologyId() + ".");
          newConcept = historyService.addConcept(newConcept);
          getLog().info(ConceptReportHelper.getConceptReport(newConcept));
          objectsAdded++;
        }

        // If concept has changed, update it
        else if (!newConcept.equals(concept)) {
          getLog().info(
              "        update concept " + newConcept.getTerminologyId());
          historyService.updateConcept(newConcept);
          objectsUpdated++;
        }

        // Otherwise, reset effective time (for modified check later)
        else {
          newConcept.setEffectiveTime(concept.getEffectiveTime());
        }

        // Cache the concept element
        cacheConcept(newConcept);

      }

    }

    getLog().info("      new = " + objectsAdded);
    getLog().info("      updated = " + objectsUpdated);

  }

  /**
   * Load descriptions.
   *
   * @param reader the reader
   * @throws Exception the exception
   */
  private void loadDescriptions(BufferedReader reader) throws Exception {

    // Setup vars
    String line = "";
    objectCt = 0;
    int objectsAdded = 0;
    int objectsUpdated = 0;
    // Iterate through description reader
    while ((line = reader.readLine()) != null) {
      // split line
      String fields[] = line.split("\t");

      // if not header
      if (!fields[0].equals("id")) {

        // Get concept from cache or from db
        Concept concept = null;
        if (conceptCache.containsKey(fields[4])) {
          concept = conceptCache.get(fields[4]);
        } else if (existingConceptCache.containsKey(fields[4])) {
          concept = existingConceptCache.get(fields[4]);
        } else {
          // retrieve concept
          concept =
              historyService.getSingleConcept(fields[4], terminology,
                  terminologyVersion);
        }

        // if the concept is not null
        if (concept != null) {

          // Add concept to the cache
          cacheConcept(concept);

          // Load description from cache or db
          Description description = null;
          if (descriptionCache.containsKey(fields[0])) {
            description = descriptionCache.get(fields[0]);
          } else if (existingDescriptionIds.contains(fields[0])) {
            description =
                historyService.getDescription(fields[0], terminology,
                    terminologyVersion);
          }

          // TODO: either remove this, make it an exception, or treat it as a
          // normaml case
          if (description == null && existingDescriptionIds.contains(fields[0])) {
            throw new Exception(
                "** Description "
                    + fields[0]
                    + " is in existing id cache, but was not precached via concept "
                    + concept.getTerminologyId());

          }

          // Setup delta description (either new or based on existing one)
          Description newDescription = null;
          if (description == null) {
            newDescription = new DescriptionJpa();
          } else {
            newDescription = new DescriptionJpa(description, true, false);
          }
          newDescription.setConcept(concept);

          // Set fields
          newDescription.setTerminologyId(fields[0]);
          // effective time is left null - to indicate a change
          newDescription.setActive(fields[2].equals("1") ? true : false);
          newDescription.setModuleId(fields[3]);

          newDescription.setLanguageCode(fields[5]);
          newDescription.setTypeId(fields[6]);
          newDescription.setTerm(fields[7]);
          newDescription.setCaseSignificanceId(fields[8]);
          newDescription.setTerminology(terminology);
          newDescription.setTerminologyVersion(terminologyVersion);
          newDescription.setLastModifiedBy("loader");

          // If description is new, add it
          if (description == null) {
            getLog().info(
                "        add description " + newDescription.getTerminologyId());
            newDescription = historyService.addDescription(newDescription);
            cacheDescription(newDescription);
            objectsAdded++;
          }

          // If description has changed, update it
          else if (!newDescription.equals(description)) {
            getLog().info(
                "        update description "
                    + newDescription.getTerminologyId());
            historyService.updateDescription(newDescription);
            cacheDescription(newDescription);
            objectsUpdated++;
          }

          // Otherwise, reset effective time (for modified check later)
          else {
            newDescription.setEffectiveTime(description.getEffectiveTime());
          }

        }

        // Major error if there is a delta description with a
        // non-existent concept
        else {
          throw new Exception("Could not find concept " + fields[4]
              + " for Description " + fields[0]);
        }
      }
    }
    getLog().info("      new = " + objectsAdded);
    getLog().info("      updated = " + objectsUpdated);
  }

  /**
   * Load language ref set members.
   *
   * @param reader the reader
   * @throws Exception the exception
   */
  private void loadLanguageRefSetMembers(BufferedReader reader)
    throws Exception {

    // Setup variables
    String line = "";
    objectCt = 0;
    int objectsAdded = 0;
    int objectsUpdated = 0;

    // Iterate through language refset reader
    while ((line = reader.readLine()) != null) {

      // split line
      String fields[] = line.split("\t");

      // if not header
      if (!fields[0].equals("id")) {

        // Get the description
        Description description = null;
        if (descriptionCache.containsKey(fields[5])) {
          description = descriptionCache.get(fields[5]);
        } else {
          description =
              historyService.getDescription(fields[5], terminology,
                  terminologyVersion);
        }

        // get the concept
        Concept concept = description.getConcept();
        // description should have concept (unless cached descriptions don't
        // have them)
        if (concept == null) {
          throw new Exception("Description" + fields[0]
              + " does not have concept");

        }
        // Cache concept and description
        cacheConcept(concept);
        cacheDescription(description);

        // Ensure effective time is set on all appropriate objects
        LanguageRefSetMember languageRefSetMember = null;
        if (languageRefSetMemberCache.containsKey(fields[0])) {
          languageRefSetMember = languageRefSetMemberCache.get(fields[0]);
          // to investigate if there will be an update
        } else if (existingLanguageRefSetMemberIds.contains(fields[0])) {
          // retrieve languageRefSetMember
          languageRefSetMember =
              historyService.getLanguageRefSetMember(fields[0], terminology,
                  terminologyVersion);
        }

        if (languageRefSetMember == null
            && existingLanguageRefSetMemberIds.contains(fields[0])) {
          throw new Exception(
              "LanguageRefSetMember "
                  + fields[0]
                  + " is in existing id cache, but was not precached via description "
                  + description.getTerminologyId());

        }

        // Setup delta language entry (either new or based on existing
        // one)
        LanguageRefSetMember newLanguageRefSetMember = null;
        if (languageRefSetMember == null) {
          newLanguageRefSetMember = new LanguageRefSetMemberJpa();
        } else {
          newLanguageRefSetMember =
              new LanguageRefSetMemberJpa(languageRefSetMember);
        }
        newLanguageRefSetMember.setDescription(description);

        newLanguageRefSetMember.setTerminologyId(fields[0]);
        // effective time is left null - to indicate a change
        newLanguageRefSetMember.setActive(fields[2].equals("1") ? true : false);
        newLanguageRefSetMember.setModuleId(fields[3]);
        newLanguageRefSetMember.setRefSetId(fields[4]);
        newLanguageRefSetMember.setAcceptabilityId(fields[6]);
        newLanguageRefSetMember.setTerminology(terminology);
        newLanguageRefSetMember.setTerminologyVersion(terminologyVersion);
        newLanguageRefSetMember.setLastModifiedBy("loader");

        // If language refset entry is new, add it
        if (languageRefSetMember == null) {
          getLog().info(
              "        add language "
                  + newLanguageRefSetMember.getTerminologyId());
          newLanguageRefSetMember =
              historyService.addLanguageRefSetMember(newLanguageRefSetMember);
          cacheLanguageRefSetMember(newLanguageRefSetMember);
          objectsAdded++;
        }

        // If language refset entry is changed, update it
        else if (!newLanguageRefSetMember.equals(languageRefSetMember)) {
          getLog().info(
              "        update language "
                  + newLanguageRefSetMember.getTerminologyId());
          historyService.updateLanguageRefSetMember(newLanguageRefSetMember);
          cacheLanguageRefSetMember(newLanguageRefSetMember);
          objectsUpdated++;
        }

        // Otherwise, reset effective time (for modified check later)
        else {
          newLanguageRefSetMember.setEffectiveTime(languageRefSetMember
              .getEffectiveTime());
        }
      }
    }

    getLog().info("      new = " + objectsAdded);
    getLog().info("      updated = " + objectsUpdated);

  }

  /**
   * Load relationships.
   *
   * @param reader the reader
   * @throws Exception the exception
   */
  private void loadRelationships(BufferedReader reader) throws Exception {

    // Setup variables
    String line = "";
    objectCt = 0;
    int objectsAdded = 0;
    int objectsUpdated = 0;

    // Iterate through relationships reader
    while ((line = reader.readLine()) != null) {

      // Split line
      String fields[] = line.split("\t");

      // If not header
      if (!fields[0].equals("id")) {

        // Retrieve source concept
        Concept sourceConcept = null;
        Concept destinationConcept = null;
        if (conceptCache.containsKey(fields[4])) {
          sourceConcept = conceptCache.get(fields[4]);
        } else if (existingConceptCache.containsKey(fields[4])) {
          sourceConcept = existingConceptCache.get(fields[4]);
        } else {
          sourceConcept =
              historyService.getSingleConcept(fields[4], terminology,
                  terminologyVersion);
        }
        if (sourceConcept == null) {
          throw new Exception("Relationship " + fields[0] + " source concept "
              + fields[4] + " cannot be found");
        }

        // Retrieve destination concept
        if (conceptCache.containsKey(fields[5])) {
          destinationConcept = conceptCache.get(fields[5]);
        } else if (existingConceptCache.containsKey(fields[5])) {
          destinationConcept = existingConceptCache.get(fields[5]);
        } else {
          destinationConcept =
              historyService.getSingleConcept(fields[5], terminology,
                  terminologyVersion);
        }
        if (destinationConcept == null) {
          throw new Exception("Relationship " + fields[0]
              + " destination concept " + fields[5] + " cannot be found");
        }

        // Cache concepts
        cacheConcept(sourceConcept);
        cacheConcept(destinationConcept);

        // Retrieve relationship
        Relationship relationship = null;
        if (relationshipCache.containsKey(fields[0])) {
          relationship = relationshipCache.get(fields[0]);
        } else if (existingRelationshipIds.contains(fields[0])) {
          relationship =
              historyService.getRelationship(fields[0], terminology,
                  terminologyVersion);

        }

        // Verify cache
        if (relationship == null && existingRelationshipIds.contains(fields[0])) {
          throw new Exception("** Relationship " + fields[0]
              + " is in existing id cache, but was not precached via concepts "
              + sourceConcept.getTerminologyId() + " or "
              + destinationConcept.getTerminologyId());
        }

        // Setup delta relationship (either new or based on existing one)
        Relationship newRelationship = null;
        if (relationship == null) {
          newRelationship = new RelationshipJpa();
        } else {
          newRelationship = new RelationshipJpa(relationship);
        }

        // Set fields
        newRelationship.setTerminologyId(fields[0]);
        // effective time is left null - to indicate a change
        newRelationship.setActive(fields[2].equals("1") ? true : false); // active
        newRelationship.setModuleId(fields[3]); // moduleId
        newRelationship.setRelationshipGroup(Integer.valueOf(fields[6])); // relationshipGroup
        newRelationship.setTypeId(fields[7]); // typeId
        newRelationship.setCharacteristicTypeId(fields[8]); // characteristicTypeId
        newRelationship.setTerminology(terminology);
        newRelationship.setTerminologyVersion(terminologyVersion);
        newRelationship.setModifierId(fields[9]);
        newRelationship.setSourceConcept(sourceConcept);
        newRelationship.setDestinationConcept(destinationConcept);
        newRelationship.setLastModifiedBy("loader");
        // If relationship is new, add it
        if (!existingRelationshipIds.contains(fields[0])) {
          getLog().info(
              "        add relationship " + newRelationship.getTerminologyId());
          newRelationship = historyService.addRelationship(newRelationship);
          cacheRelationship(newRelationship);
          objectsAdded++;
        }

        // If relationship is changed, update it
        else if (relationship != null && !newRelationship.equals(relationship)) {
          getLog().info(
              "        update relationship "
                  + newRelationship.getTerminologyId());
          historyService.updateRelationship(newRelationship);
          cacheRelationship(newRelationship);
          objectsUpdated++;
        }

        // Otherwise, reset effective time (for modified check later)
        else {
          if (relationship != null) {
            newRelationship.setEffectiveTime(relationship.getEffectiveTime());
          }
        }
      }
    }

    getLog().info("      new = " + objectsAdded);
    getLog().info("      updated = " + objectsUpdated);

  }

  /**
   * Calculates default preferred names for any concept that has changed. Note:
   * at this time computes for concepts that have only changed due to
   * relationships, which is unnecessary
   *
   * @throws Exception the exception
   */
  private void computeDefaultPreferredNames() throws Exception {

    // Setup vars
    int dpnNotFoundCt = 0;
    int dpnFoundCt = 0;
    int dpnSkippedCt = 0;

    getLog().info("Checking database against calculated modifications");
    ConceptList modifiedConcepts =
        historyService.findConceptsModifiedSinceDate(terminology,
            deltaLoaderStartDate, null);
    getLog().info(
        "Computing default preferred names for " + modifiedConcepts.getCount()
            + " concepts");

    // Iterate over concepts
    for (Concept concept : modifiedConcepts.getObjects()) {

      // Skip if inactive
      if (!concept.isActive()) {
        dpnSkippedCt++;
        continue;
      }

      getLog().info("Checking concept " + concept.getTerminologyId());

      boolean dpnFound = false;

      // Iterate over descriptions
      for (Description description : concept.getDescriptions()) {
        getLog().info(
            "  Checking description " + description.getTerminologyId()
                + ", active = " + description.isActive() + ", typeId = "
                + description.getTypeId());
        // If active andn preferred type
        if (description.isActive() && description.getTypeId().equals(dpnTypeId)) {

          // Iterate over language refset members
          for (LanguageRefSetMember language : description
              .getLanguageRefSetMembers()) {
            getLog().info(
                "    Checking language " + language.getTerminologyId()
                    + ", active = " + language.isActive() + ", refSetId = "
                    + language.getRefSetId() + ", acceptabilityId = "
                    + language.getAcceptabilityId());

            // If prefrred and has correct refset
            if (new Long(language.getRefSetId()).equals(dpnRefSetId)
                && language.isActive()
                && language.getAcceptabilityId().equals(dpnAcceptabilityId)) {
              getLog().info("      MATCH FOUND: " + description.getTerm());
              // print warning for multiple names found
              if (dpnFound == true) {
                getLog().warn(
                    "Multiple default preferred names found for concept "
                        + concept.getTerminologyId());
                getLog().warn(
                    "  " + "Existing: " + concept.getDefaultPreferredName());
                getLog().warn("  " + "Replaced with: " + description.getTerm());
              }

              // Set preferred name
              concept.setDefaultPreferredName(description.getTerm());

              // set found to true
              dpnFound = true;

            }
          }
        }

        // Pref name not found
        if (!dpnFound) {
          dpnNotFoundCt++;
          getLog().warn(
              "Could not find defaultPreferredName for concept "
                  + concept.getTerminologyId());
          concept.setDefaultPreferredName("[Could not be determined]");
        } else {
          dpnFoundCt++;
        }
      }
    }

    getLog().info("  found =  " + dpnFoundCt);
    getLog().info("  not found = " + dpnNotFoundCt);
    getLog().info("  skipped = " + dpnSkippedCt);

  }

  /**
   * Retires concepts that were removed from prior deltas. Find concepts in the
   * DB that are not in the current delta and which have effective times greater
   * than the latest release date. The latest release date is the
   * "terminologyVersion" in this case.
   * @throws Exception
   */
  public void retireRemovedConcepts() throws Exception {
    int ct = 0;
    for (Concept concept : existingConceptCache.values()) {
      if (concept.getEffectiveTime() == null
          && !deltaConceptIds.contains(concept.getTerminologyId())
          && concept.isActive()) {
        // Because it's possible that a concept element changed and that
        // change was retracted, we need to double-check whether all of
        // the concept elements are also new. If so, proceed. It is possible
        // that ALL descriptions and relationships changed and all of those
        // changes were retracted. in that case the worst thing that happens
        // the record has to be remapped
        boolean proceed = true;
        for (Description description : concept.getDescriptions()) {
          if (description.getEffectiveTime() == null) {
            proceed = false;
            break;
          }
        }
        if (proceed) {
          for (Relationship relationship : concept.getRelationships()) {
            if (relationship.getEffectiveTime() == null) {
              proceed = false;
              break;
            }
          }
        }
        // One gap in the logic is if a concept was retired and that
        // retirement was retracted, we don't know. again, the consequence
        // is that the concept will have to be remapped.

        // Retire this concept.
        if (proceed) {
          ct++;
          concept.setActive(false);
          // effective time is left null - to indicate a change
          historyService.updateConcept(concept);
        }
      }
    }
    getLog().info("      retired =  " + ct);
  }

  // helper function to update and store concept
  // as well as putting all descendant objects in the cache
  // for easy retrieval
  /**
   * Cache concept.
   *
   * @param c the c
   */
  private void cacheConcept(Concept c) {
    if (!conceptCache.containsKey(c.getTerminologyId())) {
      for (Relationship r : c.getRelationships()) {
        relationshipCache.put(r.getTerminologyId(), r);
      }
      for (Description d : c.getDescriptions()) {
        for (LanguageRefSetMember l : d.getLanguageRefSetMembers()) {
          languageRefSetMemberCache.put(l.getTerminologyId(), l);
        }
        descriptionCache.put(d.getTerminologyId(), d);
      }
      conceptCache.put(c.getTerminologyId(), c);
    }
  }

  /**
   * Cache description.
   *
   * @param d the d
   */
  private void cacheDescription(Description d) {

    if (!descriptionCache.containsKey(d.getTerminologyId())) {
      for (LanguageRefSetMember l : d.getLanguageRefSetMembers()) {
        languageRefSetMemberCache.put(l.getTerminologyId(), l);
      }
      descriptionCache.put(d.getTerminologyId(), d);
    }
  }

  /**
   * Cache relationship.
   *
   * @param r the r
   */
  private void cacheRelationship(Relationship r) {
    relationshipCache.put(r.getTerminologyId(), r);
  }

  // helper function to cache and update a language ref set member
  /**
   * Cache language ref set member.
   *
   * @param l the l
   */
  private void cacheLanguageRefSetMember(LanguageRefSetMember l) {
    languageRefSetMemberCache.put(l.getTerminologyId(), l);
  }

}
