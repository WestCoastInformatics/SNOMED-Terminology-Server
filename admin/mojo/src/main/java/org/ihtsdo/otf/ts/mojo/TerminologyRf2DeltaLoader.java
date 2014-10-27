package org.ihtsdo.otf.ts.mojo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.DescriptionList;
import org.ihtsdo.otf.ts.helpers.LanguageRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.RelationshipList;
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

/**
 * Goal which loads an RF2 Delta of SNOMED CT data
 * 
 * <pre>
 * 		<plugin> 
 * 			<groupId>org.ihtsdo.otf.mapping</groupId>
 * 			<artifactId>mapping-admin-mojo</artifactId>
 * 			<version>${project.version}</version> 
 * 			<executions> 
 * 				<execution>
 * 					<id>load-rf2-delta</id> 
 * 					<phase>package</phase> 
 * 					<goals>
 * 						<goal>load-rf2-delta</goal> 
 * 			 		</goals> 
 * 					<configuration>
 * 						<terminology>SNOMEDCT</terminology> 
 * 					</configuration> 
 * 				</execution>
 * 			</executions>
 * 		 </plugin>
 * </pre>
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

  /** The terminology version. */
  private String terminologyVersion;

  /** The loaded properties. */
  @SuppressWarnings("unused")
  private Properties config;

  /** The input directory. */
  private File deltaDir;

  /** the defaultPreferredNames type id. */
  private String dpnTypeId;

  /** The dpn ref set id. */
  private String dpnRefSetId;

  /** The dpn acceptability id. */
  private String dpnAcceptabilityId;

  /** The concept reader. */
  /** File readers. */
  private BufferedReader conceptReader;

  /** The description reader. */
  private BufferedReader descriptionReader;

  /** The text definition reader. */
  private BufferedReader textDefinitionReader;

  /** The relationship reader. */
  private BufferedReader relationshipReader;

  /** The language reader. */
  private BufferedReader languageReader;

  /** progress tracking variables. */
  private int objectCt;

  /** The log ct. */
  private int logCt = 2000;

  /** The date format. */
  SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss a");

  /** The start time. */
  long startTime;

  /** The time at which drip feed was started. */
  private Date deltaLoaderStartDate = new Date();

  /** Content and Mapping Services. */
  private HistoryService historyService = null;

  /**
   * The cache of objects in this delta file. Unsure if speed or memory will be
   * constraint here - if speed, these will be kept - if memory, these will be
   * changed to Set<String> terminologyId and objects will be retrieved via
   * services whenever needed
   * */
  private Map<String, Concept> existingConceptCache = new HashMap<>();

  /** The concept cache. */
  private Map<String, Concept> conceptCache = new HashMap<>();

  /** The description cache. */
  private Map<String, Description> descriptionCache = new HashMap<>();

  /** The relationship cache. */
  private Map<String, Relationship> relationshipCache = new HashMap<>();

  /** The language ref set member cache. */
  private Map<String, LanguageRefSetMember> languageRefSetMemberCache =
      new HashMap<>();

  /** The language by description cache. */
  private Map<String, List<LanguageRefSetMember>> languageByDescriptionCache =
      new HashMap<>();

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
      // instantiate the global variables
      instantiateGlobalVars();

      // instantiate terminology file readers
      instantiateFileReaders();

      // precache all existing concepts
      getLog().info(
          "Retrieving all concepts for " + terminology + " "
              + terminologyVersion);
      ConceptList conceptList =
          historyService.getAllConcepts(terminology, terminologyVersion);
      getLog().info("  " + conceptList.getCount() + " concepts retrieved");

      for (Concept c : conceptList.getConcepts()) {
        existingConceptCache.put(c.getTerminologyId(), c);
      }

      getLog().info("Constructing terminology id sets for quality assurance");

      // precache the ids of all existing objects other than concepts
      // THIS IS FOR DEBUG/QUALITY ASSURANCE
      existingDescriptionIds =
          historyService.getAllDescriptionTerminologyIds(terminology,
              terminologyVersion);
      existingLanguageRefSetMemberIds =
          historyService.getAllLanguageRefSetMemberTerminologyIds(terminology,
              terminologyVersion);
      existingRelationshipIds =
          historyService.getAllRelationshipTerminologyIds(terminology,
              terminologyVersion);

      getLog().info(
          "  Terminology " + terminology + ", " + terminologyVersion
              + " has the following objects:");
      getLog().info(
          "    Descriptions:           " + existingDescriptionIds.size());
      getLog().info(
          "    Relationships:          " + existingRelationshipIds.size());
      getLog().info(
          "    LanguageRefSetMembers:  "
              + existingLanguageRefSetMemberIds.size());

      // load new data
      loadDelta();

      // compute the number of modified objects of each type
      getLog().info("Computing number of modified objects...");
      int nConceptsUpdated = 0;
      int nDescriptionsUpdated = 0;
      int nLanguagesUpdated = 0;
      int nRelationshipsUpdated = 0;

      for (Concept c : conceptCache.values()) {
        if (c.getEffectiveTime().equals(this.deltaLoaderStartDate))
          nConceptsUpdated++;
      }

      for (Relationship r : relationshipCache.values()) {
        if (r.getEffectiveTime().equals(this.deltaLoaderStartDate))
          nRelationshipsUpdated++;
      }

      for (Description d : descriptionCache.values()) {
        if (d.getEffectiveTime().equals(this.deltaLoaderStartDate))
          nDescriptionsUpdated++;
      }

      for (LanguageRefSetMember l : languageRefSetMemberCache.values()) {
        if (l.getEffectiveTime().equals(this.deltaLoaderStartDate))
          nLanguagesUpdated++;
      }

      getLog().info("  Cached objects modified by this delta");
      getLog().info("    " + nConceptsUpdated + " concepts");
      getLog().info("    " + nDescriptionsUpdated + " descriptions");
      getLog().info("    " + nRelationshipsUpdated + " relationships");
      getLog().info("    " + nLanguagesUpdated + " language ref set members");

      // commit the content changes
      getLog().info("Committing...");
      historyService.commit();
      getLog().info("  Done.");

      getLog()
          .info(
              "Checking database contents against number of previously modified objects");

      ConceptList modifiedConcepts =
          historyService.getConceptsModifiedSinceDate(terminology,
              deltaLoaderStartDate, null);
      RelationshipList modifiedRelationships =
          historyService.getRelationshipsModifiedSinceDate(terminology,
              deltaLoaderStartDate);
      DescriptionList modifiedDescriptions =
          historyService.getDescriptionsModifiedSinceDate(terminology,
              deltaLoaderStartDate);
      LanguageRefSetMemberList modifiedLanguageRefSetMembers =
          historyService.getLanguageRefSetMembersModifiedSinceDate(terminology,
              deltaLoaderStartDate);

      getLog().info(
          (modifiedConcepts.getCount() != nConceptsUpdated) ? "  "
              + nConceptsUpdated + " concepts expected, found"
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

      // open a new content service to clear the manager, want the state
      // of the database
      // not persisted objects hanging around
      historyService.close();
      historyService = new HistoryServiceJpa();
      historyService.setTransactionPerOperation(false);
      historyService.beginTransaction();
      computeDefaultPreferredNames();
      historyService.commit();
      getLog().info("  Done.");

      //
      // Compute transitive closure
      //
      MetadataService metadataService = new MetadataServiceJpa();
      String version = metadataService.getLatestVersion(terminology);
      Map<String, String> hierRelTypeMap =
          metadataService
              .getHierarchicalRelationshipTypes(terminology, version);
      String isaRelType = hierRelTypeMap.keySet().iterator().next().toString();
      metadataService.close();
      ContentService contentService = new ContentServiceJpa();
      // Walk up tree to the root
      // ASSUMPTION: single root
      String conceptId = isaRelType;
      String rootId = null;
      OUTER: while (true) {
        getLog().info("  Walk up tree from " + conceptId);
        Concept c =
            contentService.getSingleConcept(conceptId, terminology, version);
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
              + terminology + ", " + version);
      TransitiveClosureAlgorithm algo = new TransitiveClosureAlgorithm();
      algo.setTerminology(terminology);
      algo.setTerminologyVersion(terminologyVersion);
      algo.setRootId(rootId);
      algo.reset();
      algo.compute();
      algo.close();

      getLog().info("");
      getLog().info("==================================");
      getLog().info("Delta load completed successfully!");
      getLog().info("==================================");
    } catch (Throwable e) {
      e.printStackTrace();
      throw new MojoFailureException("Unexpected exception:", e);
    }

  }

  /**
   * Instantiate global vars.
   * 
   * @throws Exception the exception
   */
  private void instantiateGlobalVars() throws Exception {

    // create Entity Manager
    Properties config = ConfigUtility.getConfigProperties();

    // instantiate the services
    historyService = new HistoryServiceJpa();

    // set the transaction per operation on the service managers
    historyService.setTransactionPerOperation(false);

    // initialize the transactions
    historyService.beginTransaction();

    // set the delta file directory=
    deltaDir =
        new File(config.getProperty("loader." + terminology + ".delta.data"));
    if (!deltaDir.exists()) {
      throw new MojoFailureException("Specified loader." + terminology
          + ".input.delta.data directory does not exist: "
          + deltaDir.getAbsolutePath());
    }

    // get the first file for determining
    File files[] = deltaDir.listFiles();
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

    // Set terminology version based on the filename
    // int index = fileName.indexOf(".txt");
    // terminologyVersion = fileName.substring(index - 8, index);
    // Actually, set based on the metadata
    MetadataService metadataService = new MetadataServiceJpa();
    terminologyVersion = metadataService.getLatestVersion(terminology);
    metadataService.close();

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

  }

  /**
   * Instantiate file readers.
   * 
   * @throws Exception the exception
   */
  private void instantiateFileReaders() throws Exception {

    getLog().info("Opening readers for Terminology files...");

    // concepts file
    for (File f : deltaDir.listFiles()) {
      if (f.getName().contains("_Concept_Delta_")) {
        getLog().info("  Concept file:      " + f.getName());
        conceptReader = new BufferedReader(new FileReader(f));
      } else if (f.getName().contains("_Relationship_Delta_")) {
        getLog().info("  Relationship file: " + f.getName());
        relationshipReader = new BufferedReader(new FileReader(f));

        /*
         * Removed due to invalid relationship loading } else if
         * (f.getName().contains("_StatedRelationship_")) {
         * getLog().info("  Stated Relationship file: " + f.getName());
         * statedRelationshipReader = new BufferedReader(new FileReader(f));
         */
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
    // load concepts
    if (conceptReader != null) {
      getLog().info("Loading Concepts...");
      startTime = System.nanoTime();
      loadConcepts(conceptReader);
      getLog().info(
          "  " + Integer.toString(objectCt) + " Concepts loaded in "
              + getElapsedTime(startTime) + "s" + " (Ended at "
              + ft.format(new Date()) + ")");
    }

    // load relationships
    if (relationshipReader != null) {
      getLog().info("Loading Relationships...");
      startTime = System.nanoTime();
      loadRelationships(relationshipReader);
      getLog().info(
          "  " + Integer.toString(objectCt) + " Relationships loaded in "
              + getElapsedTime(startTime) + "s" + " (Ended at "
              + ft.format(new Date()) + ")");
      getLog().info(
          "  Running total of concepts modified: " + conceptCache.size());
    }

    //
    // DO NOT process STATED relationships
    //
    /*
     * // load relationships if (statedRelationshipReader != null) {
     * getLog().info("Loading Stated Relationships..."); startTime =
     * System.nanoTime(); loadRelationships(statedRelationshipReader);
     * getLog().info( "  " + Integer.toString(objectCt) +
     * " Stated Relationships loaded in " + getElapsedTime(startTime) + "s" +
     * " (Ended at " + ft.format(new Date()) + ")"); getLog().info(
     * "  Running total of concepts modified: " + conceptCache.size()); }
     */

    // load descriptions
    if (descriptionReader != null) {
      getLog().info("Loading Descriptions...");
      startTime = System.nanoTime();
      loadDescriptions(descriptionReader);
      getLog().info(
          "  " + Integer.toString(objectCt) + " Descriptions loaded in "
              + getElapsedTime(startTime) + "s" + " (Ended at "
              + ft.format(new Date()) + ")");
      getLog().info(
          "  Running total of concepts modified: " + conceptCache.size());
    }

    // load text definitions
    if (descriptionReader != null) {
      getLog().info("Loading Text Definitions...");
      startTime = System.nanoTime();
      loadDescriptions(textDefinitionReader);
      getLog().info(
          "  " + Integer.toString(objectCt) + " Text Definitions loaded in "
              + getElapsedTime(startTime) + "s" + " (Ended at "
              + ft.format(new Date()) + ")");
      getLog().info(
          "  Running total of concepts modified: " + conceptCache.size());
    }

    // load langauge refset entries
    if (languageReader != null) {
      getLog().info("Loading Language Ref Sets...");
      startTime = System.nanoTime();
      loadLanguageRefSetMembers(languageReader);
      getLog().info(
          "  " + Integer.toString(objectCt) + " LanguageRefSets loaded in "
              + getElapsedTime(startTime) + "s" + " (Ended at "
              + ft.format(new Date()) + ")");
      getLog().info(
          "  Running total of concepts modified: " + conceptCache.size());
    }
  }

  /**
   * Loads the concepts from the delta files and.
   *
   * @param reader the reader
   * @throws Exception the exception
   */
  private void loadConcepts(BufferedReader reader) throws Exception {

    String line = "";
    objectCt = 0;
    Long localTime = System.currentTimeMillis();

    int objectsAdded = 0;
    int objectsUpdated = 0;

    while ((line = reader.readLine()) != null) {

      String fields[] = line.split("\t");

      // if not header
      if (!fields[0].equals("id")) {

        // check if concept exists
        Concept concept = existingConceptCache.get(fields[0]);

        // contentService.getConcept(fields[0],
        // terminology, terminologyVersion);

        if (concept == null) {
          concept = new ConceptJpa();
          objectsAdded++;
        } else {

          // contentService.updateConcept(concept);
          objectsUpdated++;
        }

        concept.setTerminologyId(fields[0]);
        concept.setEffectiveTime(deltaLoaderStartDate);
        concept.setActive(fields[2].equals("1") ? true : false);
        concept.setLastModified(new Date());
        concept.setLastModifiedBy("loader");
        concept.setModuleId(fields[3]);
        concept.setDefinitionStatusId(fields[4]);
        concept.setTerminology(terminology);
        concept.setTerminologyVersion(terminologyVersion);
        concept.setDefaultPreferredName("TBD");
        concept.setLastModifiedBy("loader");
        if (concept.getId() == null) {
          historyService.addConcept(concept);
        } else {
          historyService.updateConcept(concept);
        }

        cacheConcept(concept);

        if (++objectCt % logCt == 0) {
          Long objPerMinute =
              (logCt * 1000 * 60) / (System.currentTimeMillis() - localTime);
          getLog().info(
              "  " + objectCt + " loaded (" + objPerMinute.toString()
                  + " per minute)");
          localTime = System.currentTimeMillis();
        }
      }

    }

    getLog().info(
        "  " + objectsAdded + " new concepts, " + objectsUpdated
            + " updated concepts");
  }

  /**
   * Load descriptions.
   *
   * @param reader the reader
   * @throws Exception the exception
   */
  private void loadDescriptions(BufferedReader reader) throws Exception {

    String line = "";
    objectCt = 0;
    Long localTime = System.currentTimeMillis();

    int objectsAdded = 0;
    int objectsUpdated = 0;

    while ((line = reader.readLine()) != null) {

      String fields[] = line.split("\t");

      if (!fields[0].equals("id")) { // header

        /*
         * getLog().info( "  Processing description " + fields[0] +
         * " attached to concept " + fields[4]);
         */
        // set concept from cache if possible, otherwise retrieve
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

        // if the concept is not null, process
        if (concept != null) {

          cacheConcept(concept);

          // create the description object
          Description description = null;
          if (descriptionCache.containsKey(fields[0])) {
            description = descriptionCache.get(fields[0]);
          } else if (existingDescriptionIds.contains(fields[0])) {

            getLog()
                .warn(
                    "** Description "
                        + fields[0]
                        + " is in existing id cache, but was not precached via concept "
                        + concept.getTerminologyId());
            // description =
            // contentService.getDescription(fields[0],
            // terminology, terminologyVersion);
          }

          // if the description is not in database, instantiate a new
          // jpa object
          if (description == null) {
            description = new DescriptionJpa();
            objectsAdded++;
          } else {
            objectsUpdated++;
          }

          description.setConcept(concept);

          description.setTerminologyId(fields[0]);
          description.setEffectiveTime(deltaLoaderStartDate);
          description.setActive(fields[2].equals("1") ? true : false);
          description.setLastModified(new Date());
          description.setLastModifiedBy("loader");
          description.setModuleId(fields[3]);

          description.setLanguageCode(fields[5]);
          description.setTypeId(fields[6]);
          description.setTerm(fields[7]);
          description.setCaseSignificanceId(fields[8]);
          description.setTerminology(terminology);
          description.setTerminologyVersion(terminologyVersion);

          // ensure proper effective time and cache description
          cacheDescription(description);

          // if new description, add it, otherwise update
          if (description.getId() == null) {
            historyService.addDescription(description);
          } else {
            historyService.updateDescription(description);
          }

        } else {

          getLog().info(
              "Could not find concept " + fields[4] + " for Description "
                  + fields[0]);
        }

        if (++objectCt % logCt == 0) {
          Long objPerMinute =
              (logCt * 1000 * 60) / (System.currentTimeMillis() - localTime);
          getLog().info(
              "  " + objectCt + " loaded (" + objPerMinute.toString()
                  + " per minute)");
          localTime = System.currentTimeMillis();
        }
      }
    }
    getLog().info(
        "  " + objectsAdded + " new descriptions, " + objectsUpdated
            + " updated descriptions");
  }

  /**
   * Load language ref set members.
   *
   * @param reader the reader
   * @throws Exception the exception
   */
  private void loadLanguageRefSetMembers(BufferedReader reader)
    throws Exception {

    String line = "";
    objectCt = 0;
    Long localTime = System.currentTimeMillis();

    int objectsAdded = 0;
    int objectsUpdated = 0;

    while ((line = reader.readLine()) != null) {

      String fields[] = line.split("\t");

      if (!fields[0].equals("id")) { // header line

        // get the description
        Description description = null;
        if (descriptionCache.containsKey(fields[5])) {
          description = descriptionCache.get(fields[5]);

        } else {
          getLog().info(
              "  Description " + fields[5]
                  + " is not in cache, retrieving from database");
          description =
              historyService.getDescription(fields[5], terminology,
                  terminologyVersion);
        }

        // get the concept
        Concept concept = null;
        String conceptId = description.getConcept().getTerminologyId();
        if (conceptCache.containsKey(conceptId)) {
          concept = conceptCache.get(conceptId);
        } else if (existingConceptCache.containsKey(conceptId)) {
          concept = existingConceptCache.get(conceptId);

        } else {
          getLog().info(
              "  Concept " + conceptId
                  + " is not in cache, retrieving from database");
          concept =
              historyService.getSingleConcept(conceptId, terminology,
                  terminologyVersion);
        }

        // process the language ref set member
        if (concept != null && description != null) {

          cacheConcept(concept);
          cacheDescription(description);

          // ensure effective time is set on all appropriate objects

          LanguageRefSetMember member = null;
          if (languageRefSetMemberCache.containsKey(fields[0])) {

            member = languageRefSetMemberCache.get(fields[0]);
          } else {
            if (this.existingLanguageRefSetMemberIds.contains(fields[0])) {
              getLog()
                  .warn(
                      "** LanguageRefSetMember "
                          + fields[0]
                          + " is in existing id cache, but was not precached via description "
                          + description.getTerminologyId());

              // getLog().info("New language ref set member (from delta)");
              /*
               * languageRefSetMember = contentService
               * .getLanguageRefSetMember(fields[0], terminology,
               * terminologyVersion);
               */
            }
          }

          if (member == null) {
            objectsAdded++;
            member = new LanguageRefSetMemberJpa();
          } else {
            objectsUpdated++;
          }

          member.setDescription(description);

          // Universal RefSet attributes
          member.setTerminologyId(fields[0]);
          member.setEffectiveTime(deltaLoaderStartDate);
          member.setActive(fields[2].equals("1") ? true : false);
          member.setLastModified(new Date());
          member.setLastModifiedBy("loader");
          member.setModuleId(fields[3]);
          member.setRefSetId(fields[4]);

          // Language unique attributes
          member.setAcceptabilityId(fields[6]);

          // Terminology attributes
          member.setTerminology(terminology);
          member.setTerminologyVersion(terminologyVersion);

          cacheLanguageRefSetMember(member);

          if (member.getId() == null) {
            historyService.addLanguageRefSetMember(member);
          } else {
            historyService.updateLanguageRefSetMember(member);
          }

        } else {
          if (concept == null) {
            getLog().warn(
                "Could not find concept for language ref set " + fields[0]);

            getLog().warn("  Delta line: " + line);
          }
          if (description == null) {
            getLog().warn(
                "Could not find description " + fields[5]
                    + " for language ref set " + fields[0]);
            getLog().warn("  Delta line: " + line);
          }
        }

        if (++objectCt % logCt == 0) {
          Long objPerMinute =
              (logCt * 1000 * 60) / (System.currentTimeMillis() - localTime);
          getLog().info(
              "  " + objectCt + " loaded (" + objPerMinute.toString()
                  + " per minute)");
          localTime = System.currentTimeMillis();
        }
      }
    }

    getLog().info(
        "  " + objectsAdded + " new language ref set members, "
            + objectsUpdated + " updated language ref set members");

    getLog()
        .info(
            languageByDescriptionCache.keySet().size()
                + " descriptions for language ref set members in languageByDescriptionCache");
  }

  /**
   * Load relationships.
   *
   * @param reader the reader
   * @throws Exception the exception
   */
  private void loadRelationships(BufferedReader reader) throws Exception {

    System.out
        .println("Cached relationships size: " + relationshipCache.size());

    String line = "";
    objectCt = 0;
    Long localTime = System.currentTimeMillis();

    int objectsAdded = 0;
    int objectsUpdated = 0;

    while ((line = reader.readLine()) != null) {

      String fields[] = line.split("\t");

      if (!fields[0].equals("id")) { // header

        // otherwise retrieve
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

        if (conceptCache.containsKey(fields[5])) {
          destinationConcept = conceptCache.get(fields[5]);
        } else if (existingConceptCache.containsKey(fields[5])) {
          destinationConcept = existingConceptCache.get(fields[5]);
        } else {
          destinationConcept =
              historyService.getSingleConcept(fields[5], terminology,
                  terminologyVersion);
        }

        if (sourceConcept != null && destinationConcept != null) {

          cacheConcept(sourceConcept);
          cacheConcept(destinationConcept);

          // see if relationship is in cache, otherwise get it from
          // content service
          Relationship relationship = null;

          if (relationshipCache.containsKey(fields[0])) {
            relationship = relationshipCache.get(fields[0]);

          } else {
            if (existingRelationshipIds.contains(fields[0])) {
              getLog()
                  .warn(
                      "** Relationship "
                          + fields[0]
                          + " is in existing id cache, but was not precached via concepts "
                          + sourceConcept.getTerminologyId() + " or "
                          + destinationConcept.getTerminologyId());
            }

          }
          /*
           * System.out .println("Fetching relationship " + fields[0]);
           * 
           * relationship = contentService.getRelationship(fields[0],
           * terminology, terminologyVersion); System.out.println("  Complete");
           */

          if (relationship == null) {
            objectsAdded++;
            relationship = new RelationshipJpa();
          } else {
            objectsUpdated++;
          }

          relationship.setTerminologyId(fields[0]);
          relationship.setEffectiveTime(deltaLoaderStartDate);
          relationship.setActive(fields[2].equals("1") ? true : false); // active
          relationship.setLastModified(new Date());
          relationship.setLastModifiedBy("loader");
          relationship.setModuleId(fields[3]); // moduleId

          relationship.setRelationshipGroup(Integer.valueOf(fields[6])); // relationshipGroup
          relationship.setTypeId(fields[7]); // typeId
          relationship.setCharacteristicTypeId(fields[8]); // characteristicTypeId
          relationship.setTerminology(terminology);
          relationship.setTerminologyVersion(terminologyVersion);
          relationship.setModifierId(fields[9]);

          relationship.setSourceConcept(sourceConcept);
          relationship.setDestinationConcept(destinationConcept);

          // cache the relationship and ensure correct time set
          // cacheRelationship(relationship);

          // if new relationship, add it, otherwise update existing
          if (relationship.getId() == null) {
            historyService.addRelationship(relationship);
          } else {
            historyService.updateRelationship(relationship);
          }

        } else {
          if (sourceConcept == null)
            getLog().warn(
                "Could not find source concept " + fields[4]
                    + " for relationship " + fields[0]);
          if (destinationConcept == null)
            getLog().warn(
                "Could not find destination concept " + fields[5]
                    + " for relationship " + fields[0]);
        }
      }

      if (++objectCt % logCt == 0) {
        Long objPerMinute =
            (logCt * 1000 * 60) / (System.currentTimeMillis() - localTime);
        getLog().info(
            "  " + objectCt + " loaded (" + objPerMinute.toString()
                + " per minute)");
        localTime = System.currentTimeMillis();
      }
    }

    getLog().info(
        "  " + objectsAdded + " new relationships, " + objectsUpdated
            + " updated relationships");

  }

  /**
   * Calculates default preferred names for any concept that has changed. Note:
   * at this time computes for concepts that have only changed due to
   * relationships, which is unnecessary
   *
   * @throws Exception the exception
   */
  private void computeDefaultPreferredNames() throws Exception {

    int dpnNotFoundCt = 0;
    int dpnFoundCt = 0;
    int dpnSkippedCt = 0;

    getLog().info("Checking database against calculated modifications");
    ConceptList modifiedConcepts =
        historyService.getConceptsModifiedSinceDate(terminology,
            deltaLoaderStartDate, null);

    getLog().info(
        "Computing default preferred names for " + modifiedConcepts.getCount()
            + " concepts");

    // cycle over concepts
    for (Concept concept : modifiedConcepts.getConcepts()) {

      if (concept.isActive() == false) {
        dpnSkippedCt++;
      } else {

        getLog().info("Checking concept " + concept.getTerminologyId());

        boolean dpnFound = false;

        // cycle over this concept's descriptions
        for (Description description : concept.getDescriptions()) {

          getLog().info(
              "  Checking description " + description.getTerminologyId()
                  + ", active = " + description.isActive() + ", typeId = "
                  + description.getTypeId());

          // if description is active and type id matches parameter,
          // check
          // this description
          if (description.isActive()
              && description.getTypeId().equals(dpnTypeId)) {

            // cycle over this description's language ref set member
            for (LanguageRefSetMember language : description
                .getLanguageRefSetMembers()) {

              getLog().info(
                  "    Checking language " + language.getTerminologyId()
                      + ", active = " + language.isActive() + ", refSetId = "
                      + language.getRefSetId() + ", acceptabilityId = "
                      + language.getAcceptabilityId());

              // if language ref set is active and matches
              // parameters,
              // this description has the default preferred name
              if (language.getRefSetId().equals(dpnRefSetId)
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
                  getLog().warn(
                      "  " + "Replaced with: " + description.getTerm());
                }

                // set the default preferred name to this
                // description's term
                concept.setDefaultPreferredName(description.getTerm());

                // set flag for default preferred name found to
                // true
                dpnFound = true;

              }
            }
          }
        }

        if (dpnFound == false) {

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

    getLog().info(
        "  Set default preferred name for " + dpnFoundCt + " concepts");
    getLog().info(
        "  Could not set preferred name for " + dpnNotFoundCt + " concepts");
    getLog().info(
        "  Skipped concepts (inactive) for " + dpnSkippedCt + " concepts");

  }

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

      c.setEffectiveTime(this.deltaLoaderStartDate);

      // relationships are NOT pre loaded, no need
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
   * Helper function to update timestamp on and cache a description as well as
   * its descendant objects, which are cached for later use.
   *
   * @param d the d
   */
  private void cacheDescription(Description d) {
    if (!descriptionCache.containsKey(d.getTerminologyId())) {
      d.setEffectiveTime(this.deltaLoaderStartDate);
      for (LanguageRefSetMember l : d.getLanguageRefSetMembers()) {
        languageRefSetMemberCache.put(l.getTerminologyId(), l);
      }
      descriptionCache.put(d.getTerminologyId(), d);
    }
  }

  /**
   * Helper function to cache and update a language ref set member.
   *
   * @param l the l
   */
  private void cacheLanguageRefSetMember(LanguageRefSetMember l) {
    l.setEffectiveTime(this.deltaLoaderStartDate);
    languageRefSetMemberCache.put(l.getTerminologyId(), l);

  }

}
