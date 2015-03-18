package org.ihtsdo.otf.ts.jpa.algo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.FlushModeType;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.algo.Algorithm;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.jpa.services.HistoryServiceJpa;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.SimpleRefSetMember;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa;
import org.ihtsdo.otf.ts.rf2.jpa.LanguageRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RelationshipJpa;
import org.ihtsdo.otf.ts.rf2.jpa.SimpleRefSetMemberJpa;
import org.ihtsdo.otf.ts.services.helpers.ProgressEvent;
import org.ihtsdo.otf.ts.services.helpers.ProgressListener;
import org.ihtsdo.otf.ts.services.helpers.PushBackReader;

/**
 * Implementation of an algorithm to import RF2 delta data.
 */
public class Rf2DeltaLoaderAlgorithm extends HistoryServiceJpa implements
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
  @SuppressWarnings("unused")
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

  /** The existing concept cache. */
  private Map<String, Concept> existingConceptCache = new HashMap<>();

  /** The existing description ids. */
  private Set<String> existingDescriptionIds = new HashSet<>();

  /** The existing relationship ids. */
  private Set<String> existingRelationshipIds = new HashSet<>();

  /** The existing language ref set member ids. */
  private Set<String> existingLanguageRefSetMemberIds = new HashSet<>();

  /** The existing simple ref set member ids. */
  private Set<String> existingSimpleRefSetMemberIds = new HashSet<>();

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
      Logger.getLogger(getClass()).info("Start loading delta");
      Logger.getLogger(getClass()).info("  terminology = " + terminology);
      Logger.getLogger(getClass()).info("  version = " + terminologyVersion);
      Logger.getLogger(getClass()).info("  releaseVersion = " + releaseVersion);

      // Log memory usage
      Runtime runtime = Runtime.getRuntime();
      Logger.getLogger(getClass()).debug("MEMORY USAGE:");
      Logger.getLogger(getClass()).debug(" Total: " + runtime.totalMemory());
      Logger.getLogger(getClass()).debug(" Free:  " + runtime.freeMemory());
      Logger.getLogger(getClass()).debug(" Max:   " + runtime.maxMemory());

      // Track system level information
      long startTimeOrig = System.nanoTime();

      // This is OK because every time we query the database
      // it is for an object graph we have not yet seen so flushing
      // of changes is not important until the end.
      manager.setFlushMode(FlushModeType.COMMIT);

      // Setup history service
      // Turn of ID computation when loading a terminology
      setAssignIdentifiersFlag(false);
      setTransactionPerOperation(false);
      beginTransaction();

      // Precache all existing concept entires (not connected data like
      // rels/descs)
      Logger.getLogger(getClass()).info("  Cache concepts");
      ConceptList conceptList = getAllConcepts(terminology, terminologyVersion);
      for (Concept c : conceptList.getObjects()) {
        existingConceptCache.put(c.getTerminologyId(), c);
      }
      Logger.getLogger(getClass())
          .info("    count = " + conceptList.getCount());

      // Precache the description, langauge refset, and relationship id lists
      // THIS IS FOR DEBUG/QUALITY ASSURANCE
      Logger.getLogger(getClass()).info(
          "  Construct terminology id sets for quality assurance");
      Logger.getLogger(getClass()).info("  Cache description ids");
      existingDescriptionIds =
          new HashSet<>(getAllDescriptionTerminologyIds(terminology,
              terminologyVersion).getObjects());
      Logger.getLogger(getClass()).info(
          "    count = " + existingDescriptionIds.size());

      Logger.getLogger(getClass()).info("  Cache language refset member ids");
      existingLanguageRefSetMemberIds =
          new HashSet<>(getAllLanguageRefSetMemberTerminologyIds(terminology,
              terminologyVersion).getObjects());
      Logger.getLogger(getClass()).info(
          "    count = " + existingLanguageRefSetMemberIds.size());

      Logger.getLogger(getClass()).info("  Cache relationship ids");
      existingRelationshipIds =
          new HashSet<>(getAllRelationshipTerminologyIds(terminology,
              terminologyVersion).getObjects());
      Logger.getLogger(getClass()).info(
          "    count = " + existingRelationshipIds.size());

      Logger.getLogger(getClass()).info("  Cache simple refset member ids");
      existingSimpleRefSetMemberIds =
          new HashSet<>(getAllSimpleRefSetMemberTerminologyIds(terminology,
              terminologyVersion).getObjects());
      Logger.getLogger(getClass()).info(
          "    count = " + existingSimpleRefSetMemberIds.size());

      //
      // Load concepts
      //
      Logger.getLogger(getClass()).info("    Loading Concepts ...");
      loadConcepts();

      //
      // Load relationships - stated and inferred
      //
      Logger.getLogger(getClass()).info("    Loading Relationships ...");
      loadRelationships();

      //
      // Load descriptions and definitions
      //
      Logger.getLogger(getClass()).info("    Loading Descriptions ...");
      loadDescriptions();

      //
      // Load language refset members
      //
      Logger.getLogger(getClass()).info("    Loading Language Ref Sets...");
      loadLanguageRefSetMembers();

      //
      // Load simple refset members
      //
      Logger.getLogger(getClass()).info("    Loading Simple Ref Sets...");
      loadSimpleRefSetMembers();

      // Skip other delta data structures
      // TODO: implement this

      // Compute preferred namesf
      Logger.getLogger(getClass()).info(
          "  Compute preferred names for modified concepts");
      int ct = 0;
      for (String terminologyId : conceptCache.keySet()) {
        Concept concept = conceptCache.get(terminologyId);
        String pn = getComputedPreferredName(concept);
        if (!pn.equals(concept.getDefaultPreferredName())) {
          ct++;
          concept.setDefaultPreferredName(pn);
        }
        // Mark all cached concepts for update
        if (existingConceptCache.containsKey(terminologyId)) {
          updateConcept(concept);
        }
      }

      Logger.getLogger(getClass()).info("    changed = " + ct);

      // Commit the content changes
      Logger.getLogger(getClass()).info("  Committing");
      commit();
      clear();

      Logger.getLogger(getClass()).info(
          "      elapsed time = " + getTotalElapsedTimeStr(startTimeOrig));

      Logger.getLogger(getClass()).info("Done ...");

    } catch (Exception e) {
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
    Logger.getLogger(getClass()).info("    " + pct + "% " + note);
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

        // Skip if the effective time is before the release version
        if (fields[1].compareTo(releaseVersion) < 0) {
          continue;
        }

        // Stop if the effective time is past the release version
        if (fields[1].compareTo(releaseVersion) > 0) {
          reader.push(line);
          break;
        }

        // Check if concept exists from before
        Concept concept = existingConceptCache.get(fields[0]);

        // Setup delta concept (either new or based on existing one)
        Concept newConcept = null;
        if (concept == null) {
          newConcept = new ConceptJpa();
        } else {
          newConcept = new ConceptJpa(concept, true, true);
        }

        // Set fields
        newConcept.setTerminologyId(fields[0]);
        newConcept.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        newConcept.setActive(fields[2].equals("1"));
        newConcept.setModuleId(fields[3]);
        newConcept.setDefinitionStatusId(fields[4]);
        newConcept.setTerminology(terminology);
        newConcept.setTerminologyVersion(terminologyVersion);
        newConcept.setDefaultPreferredName("TBD");
        newConcept.setLastModifiedBy("loader");
        newConcept.setPublished(true);

        // If concept is new, add it
        if (concept == null) {
          newConcept = addConcept(newConcept);
          cacheConcept(newConcept);
          objectsAdded++;
        }

        // If concept has changed, update it
        else if (!newConcept.equals(concept)) {
          // Do not actually update the concept here, wait for any other
          // changes,
          // then do it at the end (to support cascade)
          cacheConcept(newConcept);
          objectsUpdated++;
        }

        // Otherwise, reset effective time (for modified check later)
        else {
          newConcept.setEffectiveTime(concept.getEffectiveTime());
        }
      }

    }

    Logger.getLogger(getClass()).info("      new = " + objectsAdded);
    Logger.getLogger(getClass()).info("      updated = " + objectsUpdated);

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

        // Skip if the effective time is before the release version
        if (fields[1].compareTo(releaseVersion) < 0) {
          continue;
        }

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
              getSingleConcept(fields[4], terminology, terminologyVersion);
        }

        // if the concept is not null
        if (concept != null) {

          cacheConcept(concept);

          // Load description from cache or db
          Description description = null;
          if (descriptionCache.containsKey(fields[0])) {
            description = descriptionCache.get(fields[0]);
          } else if (existingDescriptionIds.contains(fields[0])) {
            // If the description exists, it should be in the cache
            // from the cache concept call
            throw new Exception("Description unexpectedly not in cache: "
                + fields[0] + ", " + fields[4]);
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
          newDescription.setEffectiveTime(ConfigUtility.DATE_FORMAT
              .parse(fields[1]));
          newDescription.setActive(fields[2].equals("1"));
          newDescription.setModuleId(fields[3]);

          newDescription.setLanguageCode(fields[5]);
          newDescription.setTypeId(fields[6]);
          newDescription.setTerm(fields[7]);
          newDescription.setCaseSignificanceId(fields[8]);
          newDescription.setTerminology(terminology);
          newDescription.setTerminologyVersion(terminologyVersion);
          newDescription.setLastModifiedBy("loader");
          newDescription.setPublished(true);

          // If description is new, add it
          if (description == null) {
            newDescription = addDescription(newDescription);
            newDescription.getConcept().addDescription(newDescription);
            objectsAdded++;
          }

          // If description has changed, update it
          else if (!newDescription.equals(description)) {
            Logger.getLogger(getClass()).debug(
                "  update description - " + newDescription);

            // do not actually update the description, the concept is cached
            // and will be updated later, simply update the data structure
            newDescription.getConcept().removeDescription(description);
            newDescription.getConcept().addDescription(newDescription);
            objectsUpdated++;
          }

          // Otherwise, reset effective time (for modified check later)
          else {
            newDescription.setEffectiveTime(description.getEffectiveTime());
          }
          cacheDescription(newDescription);

        }

        // Major error if there is a delta description with a
        // non-existent concept
        else {
          throw new Exception("Could not find concept " + fields[4]
              + " for Description " + fields[0]);
        }
      }
    }
    Logger.getLogger(getClass()).info("      new = " + objectsAdded);
    Logger.getLogger(getClass()).info("      updated = " + objectsUpdated);
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

        // Skip if the effective time is before the release version
        if (fields[1].compareTo(releaseVersion) < 0) {
          continue;
        }

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
          // the description may not yet be in the cache because
          // the language refset entry could be the first element for
          // the concept that is changed. After the cache concept call
          // below, the description will be in the cache next time
          description =
              getDescription(fields[5], terminology, terminologyVersion);
        }

        // get the concept
        Concept concept = description.getConcept();
        // description should have concept (unless cached descriptions don't
        // have them)
        if (concept == null) {
          throw new Exception("Description" + fields[0]
              + " does not have concept");

        }

        cacheConcept(concept);

        // Ensure effective time is set on all appropriate objects
        LanguageRefSetMember member = null;
        if (languageRefSetMemberCache.containsKey(fields[0])) {
          member = languageRefSetMemberCache.get(fields[0]);
          // to investigate if there will be an update
        } else if (existingLanguageRefSetMemberIds.contains(fields[0])) {
          // If the language exists, it should be in the cache
          // from the cache concept call
          throw new Exception("Language member unexpectedly not in cache: "
              + fields[0] + ", " + fields[5] + ", "
              + concept.getTerminologyId());
        }

        if (member == null
            && existingLanguageRefSetMemberIds.contains(fields[0])) {
          throw new Exception(
              "LanguageRefSetMember "
                  + fields[0]
                  + " is in existing id cache, but was not precached via description "
                  + description.getTerminologyId());

        }

        // Setup delta language entry (either new or based on existing
        // one)
        LanguageRefSetMember newMember = null;
        if (member == null) {
          newMember = new LanguageRefSetMemberJpa();
        } else {
          newMember = new LanguageRefSetMemberJpa(member);
        }
        newMember.setDescription(description);

        newMember.setTerminologyId(fields[0]);
        newMember.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        newMember.setActive(fields[2].equals("1"));
        newMember.setModuleId(fields[3]);
        newMember.setRefSetId(fields[4]);
        newMember.setAcceptabilityId(fields[6]);
        newMember.setTerminology(terminology);
        newMember.setTerminologyVersion(terminologyVersion);
        newMember.setLastModifiedBy("loader");
        newMember.setPublished(true);

        // If language refset entry is new, add it
        if (member == null) {
          newMember = addLanguageRefSetMember(newMember);
          newMember.getDescription().addLanguageRefSetMember(newMember);
          objectsAdded++;
        }

        // If language refset entry is changed, update it
        else if (!newMember.equals(member)) {
          Logger.getLogger(getClass())
              .debug("  update language - " + newMember);
          // do not actually update the language, the description's concept is
          // cached
          // and will be updated later, simply update the data structure
          newMember.getDescription().removeLanguageRefSetMember(member);
          newMember.getDescription().addLanguageRefSetMember(newMember);

          objectsUpdated++;
        }

        // Otherwise, reset effective time (for modified check later)
        else {
          newMember.setEffectiveTime(member.getEffectiveTime());
        }
        cacheLanguageRefSetMember(newMember);
      }
    }

    Logger.getLogger(getClass()).info("      new = " + objectsAdded);
    Logger.getLogger(getClass()).info("      updated = " + objectsUpdated);

  }

  /**
   * Load simple ref set members.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("resource")
  private void loadSimpleRefSetMembers() throws Exception {

    // Setup variables
    String line = "";
    objectCt = 0;
    int objectsAdded = 0;
    int objectsUpdated = 0;

    // Iterate through language refset reader
    PushBackReader reader = readers.getReader(Rf2Readers.Keys.SIMPLE);
    while ((line = reader.readLine()) != null) {

      // split line
      String fields[] = line.split("\t");

      // if not header
      if (!fields[0].equals("id")) {

        // Skip if the effective time is before the release version
        if (fields[1].compareTo(releaseVersion) < 0) {
          continue;
        }

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
              getSingleConcept(fields[4], terminology, terminologyVersion);
        }

        cacheConcept(concept);

        // Get the member from the DB, if null, create a new one
        // No cache necessary because we will not encounter the
        // same object more than once per delta and nothing else
        // is connected to it.
        SimpleRefSetMember member = null;
        if (existingSimpleRefSetMemberIds.contains(fields[0])) {
          member = getSimpleRefSetMember(fields[0], terminology, terminologyVersion);
        }

        SimpleRefSetMember newMember = null;
        if (member == null) {
          newMember = new SimpleRefSetMemberJpa();
        } else {
          newMember = new SimpleRefSetMemberJpa(member);
        }

        newMember.setTerminologyId(fields[0]);
        newMember.setTerminology(terminology);
        newMember.setTerminologyVersion(terminologyVersion);
        newMember.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        newMember.setActive(fields[2].equals("1"));
        newMember.setModuleId(fields[3]);
        newMember.setRefSetId(fields[4]);
        newMember.setConcept(concept);
        newMember.setLastModifiedBy("loader");
        newMember.setPublished(true);

        // If language refset entry is new, add it
        if (member == null) {
          newMember = addSimpleRefSetMember(newMember);
          objectsAdded++;
        }

        // If language refset entry is changed, update it
        else if (!newMember.equals(member)) {
          // do not worry about assembling concept structure here
          // since cascade does not control the collection, simply update the member.
          updateSimpleRefSetMember(newMember);
          objectsUpdated++;
        }

        // Otherwise, reset effective time (for modified check later)
        else {
          newMember.setEffectiveTime(member.getEffectiveTime());
        }
      }
    }

    Logger.getLogger(getClass()).info("      new = " + objectsAdded);
    Logger.getLogger(getClass()).info("      updated = " + objectsUpdated);

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

        // Skip if the effective time is before the release version
        if (fields[1].compareTo(releaseVersion) < 0) {
          continue;
        }

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
              getSingleConcept(fields[4], terminology, terminologyVersion);
        }
        if (sourceConcept == null) {
          throw new Exception("Relationship " + fields[0] + " source concept "
              + fields[4] + " cannot be found");
        }

        cacheConcept(sourceConcept);

        // Retrieve destination concept
        if (conceptCache.containsKey(fields[5])) {
          destinationConcept = conceptCache.get(fields[5]);
        } else if (existingConceptCache.containsKey(fields[5])) {
          destinationConcept = existingConceptCache.get(fields[5]);
        } else {
          destinationConcept =
              getSingleConcept(fields[5], terminology, terminologyVersion);
        }
        if (destinationConcept == null) {
          throw new Exception("Relationship " + fields[0]
              + " destination concept " + fields[5] + " cannot be found");
        }

        // Retrieve relationship
        Relationship relationship = null;
        if (relationshipCache.containsKey(fields[0])) {
          relationship = relationshipCache.get(fields[0]);
        } else if (existingRelationshipIds.contains(fields[0])) {
          throw new Exception("Relationship unexpectedly not in cache: "
              + fields[0] + ", " + sourceConcept.getTerminologyId());
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
        newRelationship.setEffectiveTime(ConfigUtility.DATE_FORMAT
            .parse(fields[1]));
        newRelationship.setActive(fields[2].equals("1")); // active
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
        newRelationship.setPublished(true);

        // If relationship is new, add it
        if (!existingRelationshipIds.contains(fields[0])) {
          newRelationship = addRelationship(newRelationship);
          sourceConcept.addRelationship(newRelationship);
          objectsAdded++;
        }

        // If relationship is changed, update it
        else if (relationship != null && !newRelationship.equals(relationship)) {
          Logger.getLogger(getClass()).debug(
              "  update relationship - " + newRelationship);
          // do not actually update the relationship, the concept is cached
          // and will be updated later, simply update the data structure
          sourceConcept.removeRelationship(relationship);
          sourceConcept.addRelationship(newRelationship);
          objectsUpdated++;
        }

        // Otherwise, reset effective time (for modified check later)
        else {
          if (relationship != null) {
            newRelationship.setEffectiveTime(relationship.getEffectiveTime());
          }
        }
        cacheRelationship(newRelationship);

      }
    }

    Logger.getLogger(getClass()).info("      new = " + objectsAdded);
    Logger.getLogger(getClass()).info("      updated = " + objectsUpdated);

  }

  // helper function to update and store concept
  // as well as putting all descendant objects in the cache
  // for easy retrieval
  /**
   * Cache concept.
   *
   * @param c the c
   * @throws Exception the exception
   */
  private void cacheConcept(Concept c) throws Exception {
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
      // Unnecessary as it is handled in cacheConcept:
      // for (LanguageRefSetMember l : d.getLanguageRefSetMembers()) {
      // languageRefSetMemberCache.put(l.getTerminologyId(), l);
      // }
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
