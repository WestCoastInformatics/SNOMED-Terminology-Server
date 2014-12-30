package org.ihtsdo.otf.ts.jpa.algo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.algo.Algorithm;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.DescriptionList;
import org.ihtsdo.otf.ts.helpers.LanguageRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.RelationshipList;
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
import org.ihtsdo.otf.ts.services.HistoryService;
import org.ihtsdo.otf.ts.services.MetadataService;
import org.ihtsdo.otf.ts.services.helpers.ConceptReportHelper;
import org.ihtsdo.otf.ts.services.helpers.ProgressEvent;
import org.ihtsdo.otf.ts.services.helpers.ProgressListener;
import org.ihtsdo.otf.ts.services.helpers.PushBackReader;

/**
 * Implementation of an algorithm to import RF2 delta data.
 */
public class Rf2DeltaLoaderAlgorithm extends ContentServiceJpa implements
    Algorithm {

  /** Listeners. */
  private List<ProgressListener> listeners = new ArrayList<>();

  /** The terminology. */
  private String terminology;

  /** The terminology version. */
  private String terminologyVersion;

  /** The release version. */
  private String releaseVersion;

  /** The readers. */
  private Rf2Readers readers;

  /** The delta loader start date. */
  private Date deltaLoaderStartDate = new Date();

  /** counter for objects created, reset in each load section. */
  int objectCt; //

  /** The concept cache. */
  private Map<String, Concept> conceptCache = new HashMap<>();

  /** The description cache. */
  private Map<String, Description> descriptionCache = new HashMap<>();

  /** The relationship cache. */
  private Map<String, Relationship> relationshipCache = new HashMap<>();

  /** The language ref set member cache. */
  private Map<String, LanguageRefSetMember> languageRefSetMemberCache =
      new HashMap<>();

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

  /**  The history service. */
  private HistoryService historyService;

  /**
   * Instantiates an empty {@link Rf2DeltaLoaderAlgorithm}.
   * @throws Exception if anything goes wrong
   */
  public Rf2DeltaLoaderAlgorithm() throws Exception {
    super();
  }

  /**
   * Sets the terminology.
   *
   * @param terminology the terminology
   */
  public void setTerminology(String terminology) {
    this.terminology = terminology;
  }

  /**
   * Sets the terminology version.
   *
   * @param terminologyVersion the terminology version
   */
  public void setTerminologyVersion(String terminologyVersion) {
    this.terminologyVersion = terminologyVersion;
  }

  /**
   * Sets the release version.
   *
   * @param releaseVersion the rlease version
   */
  public void setReleaseVersion(String releaseVersion) {
    this.releaseVersion = releaseVersion;
  }

  /**
   * Sets the readers.
   *
   * @param readers the readers
   */
  public void setReaders(Rf2Readers readers) {
    this.readers = readers;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.jpa.algo.Algorithm#compute()
   */
  /**
   * Compute.
   *
   * @throws Exception the exception
   */
  @Override
  public void compute() throws Exception {
    try {
      Logger.getLogger(this.getClass()).info("Start loading delta");

      // Log memory usage
      Runtime runtime = Runtime.getRuntime();
      Logger.getLogger(this.getClass()).info("MEMORY USAGE:");
      Logger.getLogger(this.getClass())
          .info(" Total: " + runtime.totalMemory());
      Logger.getLogger(this.getClass()).info(" Free:  " + runtime.freeMemory());
      Logger.getLogger(this.getClass()).info(" Max:   " + runtime.maxMemory());
      SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss a"); // format for

      // Track system level information
      long startTimeOrig = System.nanoTime();

      // Setup history service
      historyService = new HistoryServiceJpa();
      historyService.setTransactionPerOperation(false);
      historyService.beginTransaction();

      // Previous computation of terminology version is based on file name
      // but for delta/daily build files, this is not the current version
      // look up the current version instead
      MetadataService metadataService = new MetadataServiceJpa();
      terminologyVersion = metadataService.getLatestVersion(terminology);
      metadataService.close();
      if (terminologyVersion == null) {
        throw new Exception("Unable to determine terminology version.");
      }

      // Precache all existing concept entires (not connected data like
      // rels/descs)
      Logger.getLogger(this.getClass()).info("  Cache concepts");
      ConceptList conceptList =
          historyService.getAllConcepts(terminology, terminologyVersion);
      for (Concept c : conceptList.getObjects()) {
        existingConceptCache.put(c.getTerminologyId(), c);
      }
      Logger.getLogger(this.getClass()).info(
          "    count = " + conceptList.getCount());

      // Precache the description, langauge refset, and relationship id lists
      // THIS IS FOR DEBUG/QUALITY ASSURANCE
      Logger.getLogger(this.getClass()).info(
          "  Construct terminology id sets for quality assurance");
      Logger.getLogger(this.getClass()).info("  Cache description ids");
      existingDescriptionIds =
          new HashSet<>(historyService.getAllDescriptionTerminologyIds(
              terminology, terminologyVersion).getObjects());
      Logger.getLogger(this.getClass()).info(
          "    count = " + existingDescriptionIds.size());
      Logger.getLogger(this.getClass()).info(
          "  Cache language refset member ids");
      existingLanguageRefSetMemberIds =
          new HashSet<>(historyService
              .getAllLanguageRefSetMemberTerminologyIds(terminology,
                  terminologyVersion).getObjects());
      Logger.getLogger(this.getClass()).info(
          "    count = " + existingLanguageRefSetMemberIds.size());
      Logger.getLogger(this.getClass()).info("  Cache relationship ids");
      existingRelationshipIds =
          new HashSet<>(historyService.getAllRelationshipTerminologyIds(
              terminology, terminologyVersion).getObjects());
      Logger.getLogger(this.getClass()).info(
          "    count = " + existingRelationshipIds.size());

      //
      // Load concepts
      //
      Logger.getLogger(this.getClass()).info("    Loading Concepts ...");
      loadConcepts();
      Logger.getLogger(this.getClass()).info(
          "      evaluated = " + Integer.toString(objectCt) + " (Ended at "
              + ft.format(new Date()) + ")");

      //
      // Load relationships - stated and inferred
      //
      Logger.getLogger(this.getClass()).info("    Loading Relationships ...");
      loadRelationships();
      Logger.getLogger(this.getClass()).info(
          "      evaluated = " + Integer.toString(objectCt) + " (Ended at "
              + ft.format(new Date()) + ")");

      //
      // Load descriptions and definitions
      //
      Logger.getLogger(this.getClass()).info("    Loading Descriptions ...");
      loadDescriptions();
      Logger.getLogger(this.getClass()).info(
          "      evaluated = " + Integer.toString(objectCt) + " (Ended at "
              + ft.format(new Date()) + ")");

      //
      // Load language refset members
      //
      Logger.getLogger(this.getClass())
          .info("    Loading Language Ref Sets...");
      loadLanguageRefSetMembers();
      Logger.getLogger(this.getClass()).info(
          "      evaluated = " + Integer.toString(objectCt) + " (Ended at "
              + ft.format(new Date()) + ")");

      // Skip other delta data structures
      // TODO: implement this

      // Remove concepts in the DB that were created by prior
      // deltas that no longer exist in the delta
      // Logger.getLogger(this.getClass()).info("    Retire non-existent concepts..");
      // TODO - bring this back once algo is worked out
      // retireRemovedConcepts(); - this is a special case only for processing
      // "daily builds"

      // Compute the number of modified objects of each type
      Logger.getLogger(this.getClass()).info(
          "  Computing number of modified objects...");
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
      Logger.getLogger(this.getClass()).info(
          "  Cached objects modified by this delta");
      Logger.getLogger(this.getClass()).info(
          "    " + nConceptsUpdated + " concepts");
      Logger.getLogger(this.getClass()).info(
          "    " + nDescriptionsUpdated + " descriptions");
      Logger.getLogger(this.getClass()).info(
          "    " + nRelationshipsUpdated + " relationships");
      Logger.getLogger(this.getClass()).info(
          "    " + nLanguagesUpdated + " language ref set members");

      // QA
      Logger
          .getLogger(this.getClass())
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
      Logger.getLogger(this.getClass()).info(
          (modifiedConcepts.getCount() != nConceptsUpdated) ? "  "
              + nConceptsUpdated + " concepts expected, found "
              + modifiedConcepts.getCount() : "  Concept count matches");
      Logger.getLogger(this.getClass()).info(
          (modifiedRelationships.getCount() != nRelationshipsUpdated) ? "  "
              + nRelationshipsUpdated + " relationships expected, found"
              + modifiedRelationships.getCount()
              : "  Relationship count matches");
      Logger.getLogger(this.getClass())
          .info(
              (modifiedDescriptions.getCount() != nDescriptionsUpdated) ? "  "
                  + nDescriptionsUpdated + " descriptions expected, found"
                  + modifiedDescriptions.getCount()
                  : "  Description count matches");
      Logger.getLogger(this.getClass()).info(
          (modifiedLanguageRefSetMembers.getCount() != nLanguagesUpdated)
              ? "  " + nLanguagesUpdated
                  + " languageRefSetMembers expected, found"
                  + modifiedLanguageRefSetMembers.getCount()
              : "  LanguageRefSetMember count matches");

      // Iterate over concepts
      Logger.getLogger(this.getClass()).info(
          "  Compute preferred names for modified concepts");
      int ct = 0;
      for (Concept concept : modifiedConcepts.getObjects()) {
        concept = historyService.getConcept(concept.getId());
        String pn = historyService.getComputedPreferredName(concept);
        if (!pn.equals(concept.getDefaultPreferredName())) {
          ct++;
          concept.setDefaultPreferredName(pn);
          historyService.updateConcept(concept);
        }
      }
      Logger.getLogger(this.getClass()).info("    changed = " + ct);

      // Commit the content changes
      Logger.getLogger(this.getClass()).info("  Committing");
      historyService.commit();

      // Final logging messages
      Logger.getLogger(this.getClass()).info(
          "      elapsed time = " + getTotalElapsedTimeStr(startTimeOrig));
      Logger.getLogger(this.getClass()).info("Done ...");

    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.jpa.algo.Algorithm#reset()
   */
  @Override
  public void reset() throws Exception {
    // do nothing
  }

  /**
   * Fires a {@link ProgressEvent}.
   * @param pct percent done
   * @param note progress note
   */
  public void fireProgressEvent(int pct, String note) {
    ProgressEvent pe = new ProgressEvent(this, pct, pct, note);
    for (int i = 0; i < listeners.size(); i++) {
      listeners.get(i).updateProgress(pe);
    }
    Logger.getLogger(this.getClass()).info("    " + pct + "% " + note);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.jpa.services.helper.ProgressReporter#addProgressListener
   * (org.ihtsdo.otf.ts.jpa.services.helper.ProgressListener)
   */
  /**
   * Adds the progress listener.
   *
   * @param l the l
   */
  @Override
  public void addProgressListener(ProgressListener l) {
    listeners.add(l);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.jpa.services.helper.ProgressReporter#removeProgressListener
   * (org.ihtsdo.otf.ts.jpa.services.helper.ProgressListener)
   */
  /**
   * Removes the progress listener.
   *
   * @param l the l
   */
  @Override
  public void removeProgressListener(ProgressListener l) {
    listeners.remove(l);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.jpa.algo.Algorithm#cancel()
   */
  @Override
  public void cancel() {
    throw new UnsupportedOperationException("cannot cancel.");
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
   * Loads the concepts from the delta files.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("resource")
  private void loadConcepts() throws Exception {

    // Setup vars
    String line;
    objectCt = 0;
    int objectsAdded = 0;
    int objectsUpdated = 0;

    // Iterate through concept reader
    PushBackReader reader = readers.getReader(Rf2Readers.Keys.CONCEPT);
    while ((line = reader.readLine()) != null) {

      // Split line
      String fields[] = line.split("\t");

      // if not header
      if (!fields[0].equals("id")) {

        // Stop if the effective time is past the release version
        if (fields[1].compareTo(releaseVersion) > 0) {
          reader.push(line);
          break;
        }

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
          Logger.getLogger(this.getClass()).info(
              "        add concept " + newConcept.getTerminologyId() + ".");
          newConcept = historyService.addConcept(newConcept);
          Logger.getLogger(this.getClass()).info(
              ConceptReportHelper.getConceptReport(newConcept));
          objectsAdded++;
        }

        // If concept has changed, update it
        else if (!newConcept.equals(concept)) {
          Logger.getLogger(this.getClass()).info(
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

    Logger.getLogger(this.getClass()).info("      new = " + objectsAdded);
    Logger.getLogger(this.getClass()).info("      updated = " + objectsUpdated);

  }

  /**
   * Load descriptions.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("resource")
  private void loadDescriptions() throws Exception {

    // Setup vars
    String line = "";
    objectCt = 0;
    int objectsAdded = 0;
    int objectsUpdated = 0;
    // Iterate through description reader
    PushBackReader reader = readers.getReader(Rf2Readers.Keys.DESCRIPTION);
    while ((line = reader.readLine()) != null) {
      // split line
      String fields[] = line.split("\t");

      // if not header
      if (!fields[0].equals("id")) {

        // Stop if the effective time is past the release version
        if (fields[1].compareTo(releaseVersion) > 0) {
          reader.push(line);
          break;
        }

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

          // Throw exception if it cant be found
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
            Logger.getLogger(this.getClass()).info(
                "        add description " + newDescription.getTerminologyId());
            newDescription = historyService.addDescription(newDescription);
            cacheDescription(newDescription);
            objectsAdded++;
          }

          // If description has changed, update it
          else if (!newDescription.equals(description)) {
            Logger.getLogger(this.getClass()).info(
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
    Logger.getLogger(this.getClass()).info("      new = " + objectsAdded);
    Logger.getLogger(this.getClass()).info("      updated = " + objectsUpdated);
  }

  /**
   * Load language ref set members.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("resource")
  private void loadLanguageRefSetMembers() throws Exception {

    // Setup variables
    String line = "";
    objectCt = 0;
    int objectsAdded = 0;
    int objectsUpdated = 0;

    // Iterate through language refset reader
    PushBackReader reader = readers.getReader(Rf2Readers.Keys.LANGUAGE);
    while ((line = reader.readLine()) != null) {

      // split line
      String fields[] = line.split("\t");

      // if not header
      if (!fields[0].equals("id")) {

        // Stop if the effective time is past the release version
        if (fields[1].compareTo(releaseVersion) > 0) {
          reader.push(line);
          break;
        }

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
          Logger.getLogger(this.getClass()).info(
              "        add language "
                  + newLanguageRefSetMember.getTerminologyId());
          newLanguageRefSetMember =
              historyService.addLanguageRefSetMember(newLanguageRefSetMember);
          cacheLanguageRefSetMember(newLanguageRefSetMember);
          objectsAdded++;
        }

        // If language refset entry is changed, update it
        else if (!newLanguageRefSetMember.equals(languageRefSetMember)) {
          Logger.getLogger(this.getClass()).info(
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

    Logger.getLogger(this.getClass()).info("      new = " + objectsAdded);
    Logger.getLogger(this.getClass()).info("      updated = " + objectsUpdated);

  }

  /**
   * Load relationships.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("resource")
  private void loadRelationships() throws Exception {

    // Setup variables
    String line = "";
    objectCt = 0;
    int objectsAdded = 0;
    int objectsUpdated = 0;

    // Iterate through relationships reader
    PushBackReader reader = readers.getReader(Rf2Readers.Keys.RELATIONSHIP);
    while ((line = reader.readLine()) != null) {

      // Split line
      String fields[] = line.split("\t");

      // If not header
      if (!fields[0].equals("id")) {

        // Stop if the effective time is past the release version
        if (fields[1].compareTo(releaseVersion) > 0) {
          reader.push(line);
          break;
        }

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
          Logger.getLogger(this.getClass()).info(
              "        add relationship " + newRelationship.getTerminologyId());
          newRelationship = historyService.addRelationship(newRelationship);
          cacheRelationship(newRelationship);
          objectsAdded++;
        }

        // If relationship is changed, update it
        else if (relationship != null && !newRelationship.equals(relationship)) {
          Logger.getLogger(this.getClass()).info(
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

    Logger.getLogger(this.getClass()).info("      new = " + objectsAdded);
    Logger.getLogger(this.getClass()).info("      updated = " + objectsUpdated);

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
    Logger.getLogger(this.getClass()).info("      retired =  " + ct);
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
