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
import org.ihtsdo.otf.ts.ReleaseInfo;
import org.ihtsdo.otf.ts.algo.Algorithm;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.jpa.ReleaseInfoJpa;
import org.ihtsdo.otf.ts.jpa.services.HistoryServiceJpa;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceDescriptionRefSetMember;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceRefSetMember;
import org.ihtsdo.otf.ts.rf2.AttributeValueConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.AttributeValueDescriptionRefSetMember;
import org.ihtsdo.otf.ts.rf2.AttributeValueRefSetMember;
import org.ihtsdo.otf.ts.rf2.ComplexMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.DescriptionTypeRefSetMember;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.ModuleDependencyRefSetMember;
import org.ihtsdo.otf.ts.rf2.RefsetDescriptorRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.SimpleMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.SimpleRefSetMember;
import org.ihtsdo.otf.ts.rf2.jpa.AbstractAssociationReferenceRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.AbstractAttributeValueRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.AssociationReferenceConceptRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.AssociationReferenceDescriptionRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.AttributeValueConceptRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.AttributeValueDescriptionRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ComplexMapRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionTypeRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.LanguageRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ModuleDependencyRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RefsetDescriptorRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RelationshipJpa;
import org.ihtsdo.otf.ts.rf2.jpa.SimpleMapRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.SimpleRefSetMemberJpa;
import org.ihtsdo.otf.ts.services.helpers.ConceptReportHelper;
import org.ihtsdo.otf.ts.services.helpers.ProgressEvent;
import org.ihtsdo.otf.ts.services.helpers.ProgressListener;
import org.ihtsdo.otf.ts.services.helpers.PushBackReader;

/**
 * Implementation of an algorithm to import RF2 delta data.
 */
public class Rf2DeltaLoaderAlgorithm extends HistoryServiceJpa implements
    Algorithm {

  /** The commit count. */
  private final static int commitCt = 2000;

  /** Listeners. */
  private List<ProgressListener> listeners = new ArrayList<>();

  /** The terminology. */
  private String terminology;

  /** The terminology version. */
  private String terminologyVersion;

  /** The release version. */
  private String releaseVersion;

  /** The release version date. */
  private Date releaseVersionDate;

  /** The readers. */
  private Rf2Readers readers;

  /** The delta loader start date. */
  @SuppressWarnings("unused")
  private Date deltaLaderStartDate = new Date();

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

  /** The existing description type ref set member ids. */
  private Set<String> existingDescriptionTypeRefSetMemberIds = new HashSet<>();

  /** The existing refset descriptor ref set member ids. */
  private Set<String> existingRefsetDescriptorRefSetMemberIds = new HashSet<>();

  /** The existing module dependency ref set member ids. */
  private Set<String> existingModuleDependencyRefSetMemberIds = new HashSet<>();

  /** The loader. */
  final String loader = "loader";

  /** The init pref name. */
  final String initPrefName = "null";

  /** The published. */
  final String published = "PUBLISHED";

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

      releaseVersionDate = ConfigUtility.DATE_FORMAT.parse(releaseVersion);

      // Clear the query cache

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

      Logger.getLogger(getClass()).info(
          "  Cache description type refset member ids");
      existingDescriptionTypeRefSetMemberIds =
          new HashSet<>(getAllDescriptionTypeRefSetMemberTerminologyIds(
              terminology, terminologyVersion).getObjects());
      Logger.getLogger(getClass()).info(
          "    count = " + existingDescriptionTypeRefSetMemberIds.size());

      Logger.getLogger(getClass()).info(
          "  Cache refset descriptor refset member ids");
      existingRefsetDescriptorRefSetMemberIds =
          new HashSet<>(getAllRefsetDescriptorRefSetMemberTerminologyIds(
              terminology, terminologyVersion).getObjects());
      Logger.getLogger(getClass()).info(
          "    count = " + existingRefsetDescriptorRefSetMemberIds.size());

      Logger.getLogger(getClass()).info(
          "  Cache module dependency refset member ids");
      existingModuleDependencyRefSetMemberIds =
          new HashSet<>(getAllModuleDependencyRefSetMemberTerminologyIds(
              terminology, terminologyVersion).getObjects());
      Logger.getLogger(getClass()).info(
          "    count = " + existingModuleDependencyRefSetMemberIds.size());

      //
      // Load concepts
      //
      Logger.getLogger(getClass()).info("    Loading Concepts ...");
      loadConcepts();

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
      existingLanguageRefSetMemberIds = null;
      
      // Compute preferred names
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
          Logger.getLogger(getClass()).debug(
              ConceptReportHelper.getConceptReport(concept));
          updateConcept(concept);
        }
      }

      commit();
      clear();
      beginTransaction();

      // cache existing concepts again (after relationships)
      // Cascade objects are finished, these just need a concept with an id.
      conceptCache.clear();
      // Save descriptions cache for attributeValue/AssocationRef processing
      // descriptionCache.clear();
      languageRefSetMemberCache.clear();
      Logger.getLogger(getClass()).info("  Cache concepts");
      conceptList = getAllConcepts(terminology, terminologyVersion);
      for (Concept c : conceptList.getObjects()) {
        existingConceptCache.put(c.getTerminologyId(), c);
      }
      Logger.getLogger(getClass())
          .info("    count = " + conceptList.getCount());

      //
      // Load relationships - stated and inferred
      //
      Logger.getLogger(getClass()).info("    Loading Relationships ...");
      loadRelationships();

      // Clear relationships cache
      relationshipCache = null;
      existingRelationshipIds = null;
      
      commit();
      clear();
      beginTransaction();

      //
      // Load simple refset members
      //
      Logger.getLogger(getClass()).info("    Loading Simple Ref Sets...");
      loadSimpleRefSetMembers();

      commit();
      clear();
      beginTransaction();

      //
      // Load simple map refset members
      //
      Logger.getLogger(getClass()).info("    Loading Simple Map Ref Sets...");
      loadSimpleMapRefSetMembers();

      commit();
      clear();
      beginTransaction();

      //
      // Load complex map refset members
      //
      Logger.getLogger(getClass()).info("    Loading Complex Map Ref Sets...");
      loadComplexMapRefSetMembers();

      //
      // Load extended map refset members
      //
      Logger.getLogger(getClass()).info("    Loading Extended Map Ref Sets...");
      loadExtendedMapRefSetMembers();

      //
      // Load description type refset members
      //
      Logger.getLogger(getClass()).info(
          "    Loading Description Type Ref Sets...");
      loadDescriptionTypeRefSetMembers();

      //
      // Load refset descriptor refset members
      //
      Logger.getLogger(getClass()).info(
          "    Loading Refset Descriptor Ref Sets...");
      loadRefsetDescriptorRefSetMembers();

      //
      // Load module dependency refset members
      //
      Logger.getLogger(getClass()).info(
          "    Loading Module Dependency Ref Sets...");
      loadModuleDependencyRefSetMembers();

      commit();
      clear();
      beginTransaction();

      //
      // Load module dependency refset members
      //
      Logger.getLogger(getClass()).info(
          "    Loading Attribute Value Ref Sets...");
      loadAttributeValueRefSetMembers();

      commit();
      clear();
      beginTransaction();

      //
      // Load association reference refset members
      //
      Logger.getLogger(getClass()).info(
          "    Loading Association Reference Ref Sets...");
      loadAssociationReferenceRefSetMembers();

      commit();
      clear();
      beginTransaction();

      Logger.getLogger(getClass()).info("    changed = " + ct);

      // Commit the content changes
      Logger.getLogger(getClass()).info("  Committing");

      //
      // Create ReleaseInfo for this release if it does not already exist
      //
      ReleaseInfo info = getReleaseInfo(terminology, releaseVersion);
      if (info == null) {
        info = new ReleaseInfoJpa();
        info.setName(releaseVersion);
        info.setEffectiveTime(releaseVersionDate);
        info.setDescription(terminology + " " + releaseVersion + " release");
        info.setPlanned(false);
        info.setPublished(true);
        info.setReleaseBeginDate(info.getEffectiveTime());
        info.setReleaseFinishDate(info.getEffectiveTime());
        info.setTerminology(terminology);
        info.setTerminologyVersion(terminologyVersion);
        info.setLastModified(releaseVersionDate);
        info.setLastModifiedBy(loader);
        addReleaseInfo(info);
      }

      // Commit and clear resources
      commit();
      clear();

      Logger.getLogger(getClass()).info(
          getComponentStats(terminology, terminologyVersion));

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
        newConcept.setModuleId(fields[3].intern());
        newConcept.setDefinitionStatusId(fields[4].intern());
        newConcept.setTerminology(terminology);
        newConcept.setTerminologyVersion(terminologyVersion);
        newConcept.setDefaultPreferredName(initPrefName);
        newConcept.setLastModifiedBy(loader);
        newConcept.setLastModified(releaseVersionDate);
        newConcept.setPublished(true);
        newConcept.setWorkflowStatus(published);

        // If concept is new, add it
        if (concept == null) {
          newConcept = addConcept(newConcept);
          objectsAdded++;
        }

        // If concept has changed, update it
        else if (!newConcept.equals(concept)) {
          // Do not actually update the concept here, wait for any other
          // changes,
          // then do it at the end (to support cascade)
          objectsUpdated++;
        }

        // Otherwise, reset effective time (for modified check later)
        else {
          newConcept.setEffectiveTime(concept.getEffectiveTime());
        }

        // Cache the concept
        cacheConcept(newConcept);

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
          // it's not yet in the cache, put it there
          cacheConcept(concept);
        } else {
          // if the concept is new, it will have been added
          // if the concept is existing it will either have been udpated
          // or will be in the existing concept cache
          throw new Exception(
              "Concept of description should either be in cache or existing cache: "
                  + fields[4]);
        }

        // if the concept is not null
        if (concept != null) {

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
          newDescription.setLastModifiedBy(loader);
          newDescription.setLastModified(releaseVersionDate);
          newDescription.setPublished(true);
          newDescription.setWorkflowStatus(published);

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
            concept.removeDescription(description);
            concept.addDescription(newDescription);
            objectsUpdated++;
          }

          // Otherwise, reset effective time (for modified check later)
          else {
            newDescription.setEffectiveTime(description.getEffectiveTime());
          }

          // forcably recache the concept in case the description is new.
          conceptCache.remove(concept.getTerminologyId());
          cacheConcept(concept);

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
          // if here, the concept has already been cached.
        } else {
          // the description may not yet be in the cache because
          // the language refset entry could be the first element for
          // the concept that is changed. After the cache concept call
          // below, the description will be in the cache next time
          description =
              getDescription(fields[5], terminology, terminologyVersion);
          cacheConcept(description.getConcept());
        }

        // Ensure effective time is set on all appropriate objects
        LanguageRefSetMember member = null;
        if (languageRefSetMemberCache.containsKey(fields[0])) {
          member = languageRefSetMemberCache.get(fields[0]);
          // to investigate if there will be an update
        } else if (existingLanguageRefSetMemberIds.contains(fields[0])) {
          // If the language exists, it should be in the cache
          // from the cache concept call (either above or earlier for the
          // attached object)
          throw new Exception("Language member unexpectedly not in cache: "
              + fields[0] + ", " + fields[5]);
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
        newMember.setTerminology(terminology);
        newMember.setTerminologyVersion(terminologyVersion);
        newMember.setLastModifiedBy(loader);
        newMember.setLastModified(releaseVersionDate);
        newMember.setPublished(true);

        newMember.setAcceptabilityId(fields[6]);

        // If language refset entry is new, add it
        if (member == null) {
          newMember = addLanguageRefSetMember(newMember);
          description.addLanguageRefSetMember(newMember);
          objectsAdded++;
        }

        // If language refset entry is changed, update it
        else if (!newMember.equals(member)) {
          Logger.getLogger(getClass())
              .debug("  update language - " + newMember);

          // do not actually update the language, the description's concept is
          // cached
          // and will be updated later, simply update the data structure
          description.removeLanguageRefSetMember(member);
          description.addLanguageRefSetMember(newMember);

          objectsUpdated++;
        }

        // Otherwise, reset effective time (for modified check later)
        else {
          newMember.setEffectiveTime(member.getEffectiveTime());
        }

        // forcably recache the concept
        conceptCache.remove(description.getConcept().getTerminologyId());
        cacheConcept(description.getConcept());

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
  @SuppressWarnings("unchecked")
  private void loadSimpleRefSetMembers() throws Exception {
    Map<String, SimpleRefSetMember> cache = new HashMap<>();
    javax.persistence.Query query =
        manager
            .createQuery("select c from SimpleRefSetMemberJpa c where terminologyVersion = :version and terminology = :terminology");
    query.setParameter("terminology", terminology);
    query.setParameter("version", terminologyVersion);
    List<SimpleRefSetMemberJpa> members = query.getResultList();
    for (SimpleRefSetMemberJpa member : members) {
      cache.put(member.getTerminologyId(), member);
    }


    // Setup variables
    String line = "";
    objectCt = 0;
    int objectsAdded = 0;
    int objectsUpdated = 0;

    // Iterate through simple refset reader
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
        if (existingConceptCache.containsKey(fields[5])) {
          concept = existingConceptCache.get(fields[5]);
        } else {
          // retrieve concept
          concept =
              getSingleConcept(fields[5], terminology, terminologyVersion);
        }

        // Get the member from the DB, if null, create a new one
        // No cache necessary because we will not encounter the
        // same object more than once per delta and nothing else
        // is connected to it.
        SimpleRefSetMember member = null;
        if (cache.containsKey(fields[0])) {
          member = cache.get(fields[0]);
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
        newMember.setLastModifiedBy(loader);
        newMember.setLastModified(releaseVersionDate);
        newMember.setPublished(true);

        // If simple refset entry is new, add it
        if (member == null) {
          newMember = addSimpleRefSetMember(newMember);
          objectsAdded++;
        }

        // If simple refset entry is changed, update it
        else if (!newMember.equals(member)) {
          // do not worry about assembling concept structure here
          // since cascade does not control the collection, simply update the
          // member.
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
   * Load simple map ref set members.
   *
   * @throws Exception the exception
   */
  private void loadSimpleMapRefSetMembers() throws Exception {

    Map<String, SimpleMapRefSetMember> cache = new HashMap<>();
    javax.persistence.Query query =
        manager
            .createQuery("select c from SimpleMapRefSetMemberJpa c where terminologyVersion = :version and terminology = :terminology");
    query.setParameter("terminology", terminology);
    query.setParameter("version", terminologyVersion);
    @SuppressWarnings("unchecked")
    List<SimpleMapRefSetMemberJpa> members = query.getResultList();
    for (SimpleMapRefSetMemberJpa member : members) {
      cache.put(member.getTerminologyId(), member);
    }

    // Setup variables
    String line = "";
    objectCt = 0;
    int objectsAdded = 0;
    int objectsUpdated = 0;

    // Iterate through simple map refset reader
    PushBackReader reader = readers.getReader(Rf2Readers.Keys.SIMPLE_MAP);
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
        if (existingConceptCache.containsKey(fields[5])) {
          concept = existingConceptCache.get(fields[5]);
        } else {
          // retrieve concept
          concept =
              getSingleConcept(fields[5], terminology, terminologyVersion);
        }

        // Get the member from the DB, if null, create a new one
        // No cache necessary because we will not encounter the
        // same object more than once per delta and nothing else
        // is connected to it.
        SimpleMapRefSetMember member = null;
        if (cache.containsKey(fields[0])) {
          member = cache.get(fields[0]);
        }

        SimpleMapRefSetMember newMember = null;
        if (member == null) {
          newMember = new SimpleMapRefSetMemberJpa();
        } else {
          newMember = new SimpleMapRefSetMemberJpa(member);
        }

        newMember.setTerminologyId(fields[0]);
        newMember.setTerminology(terminology);
        newMember.setTerminologyVersion(terminologyVersion);
        newMember.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        newMember.setActive(fields[2].equals("1"));
        newMember.setModuleId(fields[3]);
        newMember.setRefSetId(fields[4]);
        newMember.setConcept(concept);
        newMember.setMapTarget(fields[6]);
        newMember.setLastModifiedBy(loader);
        newMember.setLastModified(releaseVersionDate);
        newMember.setPublished(true);

        // If simple map refset entry is new, add it
        if (member == null) {
          newMember = addSimpleMapRefSetMember(newMember);
          objectsAdded++;
        }

        // If simple map refset entry is changed, update it
        else if (!newMember.equals(member)) {
          // do not worry about assembling concept structure here
          // since cascade does not control the collection, simply update the
          // member.
          updateSimpleMapRefSetMember(newMember);
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
   * Load complex map ref set members.
   *
   * @throws Exception the exception
   */

  private void loadComplexMapRefSetMembers() throws Exception {

    Map<String, ComplexMapRefSetMember> cache = new HashMap<>();
    javax.persistence.Query query =
        manager
            .createQuery("select c from ComplexMapRefSetMemberJpa c where terminologyVersion = :version and terminology = :terminology");
    query.setParameter("terminology", terminology);
    query.setParameter("version", terminologyVersion);
    @SuppressWarnings("unchecked")
    List<ComplexMapRefSetMemberJpa> members = query.getResultList();
    for (ComplexMapRefSetMemberJpa member : members) {
      cache.put(member.getTerminologyId(), member);
    }

    // Setup variables
    String line = "";
    objectCt = 0;
    int objectsAdded = 0;
    int objectsUpdated = 0;

    // Iterate through complex map refset reader
    PushBackReader reader = readers.getReader(Rf2Readers.Keys.COMPLEX_MAP);
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
        if (existingConceptCache.containsKey(fields[5])) {
          concept = existingConceptCache.get(fields[5]);
        } else {
          // retrieve concept
          concept =
              getSingleConcept(fields[5], terminology, terminologyVersion);
        }

        // Ideally this would be an exception, but the full SNOMED release
        // contains early examples of complex map refsets without valid
        // concepts.
        if (concept == null) {
          Logger.getLogger(getClass()).error(
              "Complex map connected to nonexistent concept: " + fields[5]);
          continue;
        }

        // Get the member from the DB, if null, create a new one
        // No cache necessary because we will not encounter the
        // same object more than once per delta and nothing else
        // is connected to it.
        ComplexMapRefSetMember member = null;
        if (cache.containsKey(fields[0])) {
          member = cache.get(fields[0]);
        }

        ComplexMapRefSetMember newMember = null;
        if (member == null) {
          newMember = new ComplexMapRefSetMemberJpa();
        } else {
          newMember = new ComplexMapRefSetMemberJpa(member);
        }

        newMember.setTerminologyId(fields[0]);
        newMember.setTerminology(terminology);
        newMember.setTerminologyVersion(terminologyVersion);
        newMember.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        newMember.setActive(fields[2].equals("1"));
        newMember.setModuleId(fields[3]);
        newMember.setRefSetId(fields[4]);
        newMember.setConcept(concept);
        newMember.setMapGroup(Integer.parseInt(fields[6]));
        newMember.setMapPriority(Integer.parseInt(fields[7]));
        newMember.setMapRule(fields[8]);
        newMember.setMapAdvice(fields[9]);
        newMember.setMapTarget(fields[10]);
        newMember.setMapRelationId(fields[11]);
        newMember.setLastModifiedBy(loader);
        newMember.setLastModified(releaseVersionDate);
        newMember.setPublished(true);

        // If complex map refset entry is new, add it
        if (member == null) {
          newMember = addComplexMapRefSetMember(newMember);
          objectsAdded++;
        }

        // If complex map refset entry is changed, update it
        else if (!newMember.equals(member)) {
          // do not worry about assembling concept structure here
          // since cascade does not control the collection, simply update the
          // member.
          updateComplexMapRefSetMember(newMember);
          objectsUpdated++;
        }

        // Otherwise, reset effective time (for modified check later)
        else {
          newMember.setEffectiveTime(member.getEffectiveTime());
        }

        // Commit every so often
        if ((objectsAdded + objectsUpdated) % commitCt == 0) {
          Logger.getLogger(getClass()).info(
              "    commit - " + (objectsAdded + objectsUpdated));
          commit();
          clear();
          beginTransaction();
        }
      }
    }

    Logger.getLogger(getClass()).info("      new = " + objectsAdded);
    Logger.getLogger(getClass()).info("      updated = " + objectsUpdated);

  }

  /**
   * Load extended map ref set members.
   *
   * @throws Exception the exception
   */

  private void loadExtendedMapRefSetMembers() throws Exception {
    Map<String, ComplexMapRefSetMember> cache = new HashMap<>();
    javax.persistence.Query query =
        manager
            .createQuery("select c from ComplexMapRefSetMemberJpa c where terminologyVersion = :version and terminology = :terminology");
    query.setParameter("terminology", terminology);
    query.setParameter("version", terminologyVersion);
    @SuppressWarnings("unchecked")
    List<ComplexMapRefSetMemberJpa> members = query.getResultList();
    for (ComplexMapRefSetMemberJpa member : members) {
      cache.put(member.getTerminologyId(), member);
    }
    // Setup variables
    String line = "";
    objectCt = 0;
    int objectsAdded = 0;
    int objectsUpdated = 0;

    // Iterate through extended map refset reader
    PushBackReader reader = readers.getReader(Rf2Readers.Keys.EXTENDED_MAP);
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
        if (existingConceptCache.containsKey(fields[5])) {
          concept = existingConceptCache.get(fields[5]);
        } else {
          // retrieve concept
          concept =
              getSingleConcept(fields[5], terminology, terminologyVersion);
        }

        // Get the member from the DB, if null, create a new one
        // No cache necessary because we will not encounter the
        // same object more than once per delta and nothing else
        // is connected to it.
        ComplexMapRefSetMember member = null;
        if (cache.containsKey(fields[0])) {
          member = cache.get(fields[0]);
        }

        ComplexMapRefSetMember newMember = null;
        if (member == null) {
          newMember = new ComplexMapRefSetMemberJpa();
        } else {
          newMember = new ComplexMapRefSetMemberJpa(member);
        }

        newMember.setTerminologyId(fields[0]);
        newMember.setTerminology(terminology);
        newMember.setTerminologyVersion(terminologyVersion);
        newMember.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        newMember.setActive(fields[2].equals("1"));
        newMember.setModuleId(fields[3]);
        newMember.setRefSetId(fields[4]);
        newMember.setConcept(concept);
        newMember.setMapGroup(Integer.parseInt(fields[6]));
        newMember.setMapPriority(Integer.parseInt(fields[7]));
        newMember.setMapRule(fields[8]);
        newMember.setMapAdvice(fields[9]);
        newMember.setMapTarget(fields[10]);
        // field 11 is correlationId and is a fixed value based on terminology
        newMember.setMapRelationId(fields[12]);
        newMember.setLastModifiedBy(loader);
        newMember.setLastModified(releaseVersionDate);
        newMember.setPublished(true);

        // If complex map refset entry is new, add it
        if (member == null) {
          newMember = addComplexMapRefSetMember(newMember);
          objectsAdded++;
        }

        // If complex map refset entry is changed, update it
        else if (!newMember.equals(member)) {
          // do not worry about assembling concept structure here
          // since cascade does not control the collection, simply update the
          // member.
          updateComplexMapRefSetMember(newMember);
          objectsUpdated++;
        }

        // Otherwise, reset effective time (for modified check later)
        else {
          newMember.setEffectiveTime(member.getEffectiveTime());
        }

        // Commit every so often
        if ((objectsAdded + objectsUpdated) % commitCt == 0) {
          Logger.getLogger(getClass()).info(
              "    commit - " + (objectsAdded + objectsUpdated));
          commit();
          clear();
          beginTransaction();
        }

      }
    }

    Logger.getLogger(getClass()).info("      new = " + objectsAdded);
    Logger.getLogger(getClass()).info("      updated = " + objectsUpdated);

  }

  /**
   * Load description type ref set members.
   *
   * @throws Exception the exception
   */

  private void loadDescriptionTypeRefSetMembers() throws Exception {

    // Setup variables
    String line = "";
    objectCt = 0;
    int objectsAdded = 0;
    int objectsUpdated = 0;

    // Iterate through description type refset reader
    PushBackReader reader = readers.getReader(Rf2Readers.Keys.DESCRIPTION_TYPE);
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
        if (existingConceptCache.containsKey(fields[5])) {
          concept = existingConceptCache.get(fields[5]);
        } else {
          // retrieve concept
          concept =
              getSingleConcept(fields[5], terminology, terminologyVersion);
        }

        // Get the member from the DB, if null, create a new one
        // No cache necessary because we will not encounter the
        // same object more than once per delta and nothing else
        // is connected to it.
        DescriptionTypeRefSetMember member = null;
        if (existingDescriptionTypeRefSetMemberIds.contains(fields[0])) {
          member =
              getDescriptionTypeRefSetMember(fields[0], terminology,
                  terminologyVersion);
        }

        DescriptionTypeRefSetMember newMember = null;
        if (member == null) {
          newMember = new DescriptionTypeRefSetMemberJpa();
        } else {
          newMember = new DescriptionTypeRefSetMemberJpa(member);
        }

        newMember.setTerminologyId(fields[0]);
        newMember.setTerminology(terminology);
        newMember.setTerminologyVersion(terminologyVersion);
        newMember.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        newMember.setActive(fields[2].equals("1"));
        newMember.setModuleId(fields[3]);
        newMember.setRefSetId(fields[4]);
        newMember.setConcept(concept);
        newMember.setDescriptionFormat(fields[6]);
        newMember.setDescriptionLength(Integer.parseInt(fields[7]));
        newMember.setLastModifiedBy(loader);
        newMember.setLastModified(releaseVersionDate);
        newMember.setPublished(true);

        // If description type refset entry is new, add it
        if (member == null) {
          newMember = addDescriptionTypeRefSetMember(newMember);
          objectsAdded++;
        }

        // If description type refset entry is changed, update it
        else if (!newMember.equals(member)) {
          // do not worry about assembling concept structure here
          // since cascade does not control the collection, simply update the
          // member.
          updateDescriptionTypeRefSetMember(newMember);
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
   * Load refset descriptor ref set members.
   *
   * @throws Exception the exception
   */

  private void loadRefsetDescriptorRefSetMembers() throws Exception {

    // Setup variables
    String line = "";
    objectCt = 0;
    int objectsAdded = 0;
    int objectsUpdated = 0;

    // Iterate through refset descriptor refset reader
    PushBackReader reader =
        readers.getReader(Rf2Readers.Keys.REFSET_DESCRIPTOR);
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
        if (existingConceptCache.containsKey(fields[5])) {
          concept = existingConceptCache.get(fields[5]);
        } else {
          // retrieve concept
          concept =
              getSingleConcept(fields[5], terminology, terminologyVersion);
        }

        // Get the member from the DB, if null, create a new one
        // No cache necessary because we will not encounter the
        // same object more than once per delta and nothing else
        // is connected to it.
        RefsetDescriptorRefSetMember member = null;
        if (existingRefsetDescriptorRefSetMemberIds.contains(fields[0])) {
          member =
              getRefsetDescriptorRefSetMember(fields[0], terminology,
                  terminologyVersion);
        }

        RefsetDescriptorRefSetMember newMember = null;
        if (member == null) {
          newMember = new RefsetDescriptorRefSetMemberJpa();
        } else {
          newMember = new RefsetDescriptorRefSetMemberJpa(member);
        }

        newMember.setTerminologyId(fields[0]);
        newMember.setTerminology(terminology);
        newMember.setTerminologyVersion(terminologyVersion);
        newMember.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        newMember.setActive(fields[2].equals("1"));
        newMember.setModuleId(fields[3]);
        newMember.setRefSetId(fields[4]);
        newMember.setConcept(concept);
        // Refset descriptor unique attributes
        newMember.setAttributeDescription(fields[6]);
        newMember.setAttributeType(fields[7]);
        newMember.setAttributeOrder(Integer.valueOf(fields[8]));

        newMember.setLastModifiedBy(loader);
        newMember.setLastModified(releaseVersionDate);
        newMember.setPublished(true);

        // If refset descriptor refset entry is new, add it
        if (member == null) {
          newMember = addRefsetDescriptorRefSetMember(newMember);
          objectsAdded++;
        }

        // If description type refset entry is changed, update it
        else if (!newMember.equals(member)) {
          // do not worry about assembling concept structure here
          // since cascade does not control the collection, simply update the
          // member.
          updateRefsetDescriptorRefSetMember(newMember);
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
   * Load module dependency ref set members.
   *
   * @throws Exception the exception
   */

  private void loadModuleDependencyRefSetMembers() throws Exception {

    // Setup variables
    String line = "";
    objectCt = 0;
    int objectsAdded = 0;
    int objectsUpdated = 0;

    // Iterate through description type refset reader
    PushBackReader reader =
        readers.getReader(Rf2Readers.Keys.MODULE_DEPENDENCY);
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
        if (existingConceptCache.containsKey(fields[5])) {
          concept = existingConceptCache.get(fields[5]);
        } else {
          // retrieve concept
          concept =
              getSingleConcept(fields[5], terminology, terminologyVersion);
        }

        // Get the member from the DB, if null, create a new one
        // No cache necessary because we will not encounter the
        // same object more than once per delta and nothing else
        // is connected to it.
        ModuleDependencyRefSetMember member = null;
        if (existingModuleDependencyRefSetMemberIds.contains(fields[0])) {
          member =
              getModuleDependencyRefSetMember(fields[0], terminology,
                  terminologyVersion);
        }

        ModuleDependencyRefSetMember newMember = null;
        if (member == null) {
          newMember = new ModuleDependencyRefSetMemberJpa();
        } else {
          newMember = new ModuleDependencyRefSetMemberJpa(member);
        }

        newMember.setTerminologyId(fields[0]);
        newMember.setTerminology(terminology);
        newMember.setTerminologyVersion(terminologyVersion);
        newMember.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        newMember.setActive(fields[2].equals("1"));
        newMember.setModuleId(fields[3]);
        newMember.setRefSetId(fields[4]);
        newMember.setConcept(concept);
        // Refset descriptor unique attributes
        newMember.setSourceEffectiveTime(ConfigUtility.DATE_FORMAT
            .parse(fields[6]));
        newMember.setTargetEffectiveTime(ConfigUtility.DATE_FORMAT
            .parse(fields[7]));

        newMember.setLastModifiedBy(loader);
        newMember.setLastModified(releaseVersionDate);
        newMember.setPublished(true);

        // If module dependency refset entry is new, add it
        if (member == null) {
          newMember = addModuleDependencyRefSetMember(newMember);
          objectsAdded++;
        }

        // If module dependency refset entry is changed, update it
        else if (!newMember.equals(member)) {
          // do not worry about assembling concept structure here
          // since cascade does not control the collection, simply update the
          // member.
          updateModuleDependencyRefSetMember(newMember);
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
   * Load attribute value ref set members.
   *
   * @throws Exception the exception
   */

  private void loadAttributeValueRefSetMembers() throws Exception {
    Map<String, AttributeValueRefSetMember<?>> cache = new HashMap<>();
    javax.persistence.Query query =
        manager
            .createQuery("select c from AbstractAttributeValueRefSetMemberJpa c where terminologyVersion = :version and terminology = :terminology");
    query.setParameter("terminology", terminology);
    query.setParameter("version", terminologyVersion);
    @SuppressWarnings("unchecked")
    List<AbstractAttributeValueRefSetMemberJpa<?>> members = query.getResultList();
    for (AbstractAttributeValueRefSetMemberJpa<?> member : members) {
      cache.put(member.getTerminologyId(), member);
    }

    // Setup variables
    String line = "";
    objectCt = 0;
    int objectsAdded = 0;
    int objectsUpdated = 0;

    // Iterate through description type refset reader
    PushBackReader reader = readers.getReader(Rf2Readers.Keys.ATTRIBUTE_VALUE);
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

        AttributeValueRefSetMember<?> member = null;

        // Get concept from cache or from db
        Concept concept = null;
        Description description = null;
        if (existingConceptCache.containsKey(fields[5])) {
          concept = existingConceptCache.get(fields[5]);
        }
        if (descriptionCache.containsKey(fields[5])) {
          description = descriptionCache.get(fields[5]);
        } else if (existingDescriptionIds.contains(fields[5])) {
          description =
              getDescription(fields[5], terminology, terminologyVersion);
        }
        if (concept == null && description == null) {
          throw new Exception(
              "Attribute value member connected to nonexistent object");
        }

        // Get the member from the DB, if null, create a new one
        // No cache necessary because we will not encounter the
        // same object more than once per delta and nothing else
        // is connected to it.
        if (cache.containsKey(fields[0])) {
          member = cache.get(fields[0]);
        }

        AttributeValueRefSetMember<?> newMember = null;
        if (concept != null) {
          if (member == null) {
            newMember = new AttributeValueConceptRefSetMemberJpa();
          } else {
            newMember =
                new AttributeValueConceptRefSetMemberJpa(
                    (AttributeValueConceptRefSetMember) member);
          }
          ((AttributeValueConceptRefSetMember) newMember).setComponent(concept);

        }

        else if (description != null) {
          if (member == null) {
            newMember = new AttributeValueDescriptionRefSetMemberJpa();
          } else {
            newMember =
                new AttributeValueDescriptionRefSetMemberJpa(
                    (AttributeValueDescriptionRefSetMember) member);
          }
          ((AttributeValueDescriptionRefSetMember) newMember)
              .setComponent(description);
        }

        else {
          throw new Exception(
              "Attribute value member connected to nonexistent object");
        }

        newMember.setTerminologyId(fields[0]);
        newMember.setTerminology(terminology);
        newMember.setTerminologyVersion(terminologyVersion);
        newMember.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        newMember.setActive(fields[2].equals("1"));
        newMember.setModuleId(fields[3]);
        newMember.setRefSetId(fields[4]);
        // Attribute value unique attributes
        newMember.setValueId(fields[6]);

        newMember.setLastModifiedBy(loader);
        newMember.setLastModified(releaseVersionDate);
        newMember.setPublished(true);

        // If attribute value refset entry is new, add it
        if (member == null) {
          newMember = addAttributeValueRefSetMember(newMember);
          objectsAdded++;
        }

        // If attribute value refset entry is changed, update it
        else if (!newMember.equals(member)) {
          // do not worry about assembling concept structure here
          // since cascade does not control the collection, simply update the
          // member.
          updateAttributeValueRefSetMember(newMember);
          objectsUpdated++;
        }

        // Otherwise, reset effective time (for modified check later)
        else {
          newMember.setEffectiveTime(member.getEffectiveTime());
        }

        // Commit every so often
        if ((objectsAdded + objectsUpdated) % commitCt == 0) {
          Logger.getLogger(getClass()).info(
              "    commit - " + (objectsAdded + objectsUpdated));
          commit();
          clear();
          beginTransaction();
        }

      }
    }

    Logger.getLogger(getClass()).info("      new = " + objectsAdded);
    Logger.getLogger(getClass()).info("      updated = " + objectsUpdated);

  }

  /**
   * Load association reference ref set members.
   *
   * @throws Exception the exception
   */

  private void loadAssociationReferenceRefSetMembers() throws Exception {
    Map<String, AssociationReferenceRefSetMember<?>> cache = new HashMap<>();
    javax.persistence.Query query =
        manager
            .createQuery("select c from AbstractAssociationReferenceRefSetMemberJpa c where terminologyVersion = :version and terminology = :terminology");
    query.setParameter("terminology", terminology);
    query.setParameter("version", terminologyVersion);
    @SuppressWarnings("unchecked")
    List<AbstractAssociationReferenceRefSetMemberJpa<?>> members = query.getResultList();
    for (AbstractAssociationReferenceRefSetMemberJpa<?> member : members) {
      cache.put(member.getTerminologyId(), member);
    }
    // Setup variables
    String line = "";
    objectCt = 0;
    int objectsAdded = 0;
    int objectsUpdated = 0;

    // Iterate through description type refset reader
    PushBackReader reader =
        readers.getReader(Rf2Readers.Keys.ASSOCIATION_REFERENCE);
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

        AssociationReferenceRefSetMember<?> member = null;

        // Get concept from cache or from db
        Concept concept = null;
        Description description = null;
        if (existingConceptCache.containsKey(fields[5])) {
          concept = existingConceptCache.get(fields[5]);
        }
        if (descriptionCache.containsKey(fields[5])) {
          description = descriptionCache.get(fields[5]);
        } else if (existingDescriptionIds.contains(fields[5])) {
          description =
              getDescription(fields[5], terminology, terminologyVersion);
        }
        if (concept == null && description == null) {
          throw new Exception(
              "Association reference member connected to nonexistent object");
        }

        // Get the member from the DB, if null, create a new one
        // No cache necessary because we will not encounter the
        // same object more than once per delta and nothing else
        // is connected to it.
        if (cache.containsKey(fields[0])) {
          member = cache.get(fields[0]);
        }

        AssociationReferenceRefSetMember<?> newMember = null;
        if (concept != null) {
          if (member == null) {
            newMember = new AssociationReferenceConceptRefSetMemberJpa();
          } else {
            newMember =
                new AssociationReferenceConceptRefSetMemberJpa(
                    (AssociationReferenceConceptRefSetMember) member);
          }
          ((AssociationReferenceConceptRefSetMember) newMember)
              .setComponent(concept);

        }

        else if (description != null) {
          if (member == null) {
            newMember = new AssociationReferenceDescriptionRefSetMemberJpa();
          } else {
            newMember =
                new AssociationReferenceDescriptionRefSetMemberJpa(
                    (AssociationReferenceDescriptionRefSetMember) member);
          }
          ((AssociationReferenceDescriptionRefSetMember) newMember)
              .setComponent(description);
        }

        else {
          throw new Exception(
              "Association reference member connected to nonexistent object");
        }

        newMember.setTerminologyId(fields[0]);
        newMember.setTerminology(terminology);
        newMember.setTerminologyVersion(terminologyVersion);
        newMember.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        newMember.setActive(fields[2].equals("1"));
        newMember.setModuleId(fields[3]);
        newMember.setRefSetId(fields[4]);
        // Attribute value unique attributes
        newMember.setTargetComponentId(fields[6]);

        newMember.setLastModifiedBy(loader);
        newMember.setLastModified(releaseVersionDate);
        newMember.setPublished(true);

        // If attribute value refset entry is new, add it
        if (member == null) {
          newMember = addAssociationReferenceRefSetMember(newMember);
          objectsAdded++;
        }

        // If attribute value refset entry is changed, update it
        else if (!newMember.equals(member)) {
          // do not worry about assembling concept structure here
          // since cascade does not control the collection, simply update the
          // member.
          updateAssociationReferenceRefSetMember(newMember);
          objectsUpdated++;
        }

        // Otherwise, reset effective time (for modified check later)
        else {
          newMember.setEffectiveTime(member.getEffectiveTime());
        }
        // Commit every so often
        if ((objectsAdded + objectsUpdated) % commitCt == 0) {
          Logger.getLogger(getClass()).info(
              "    commit - " + (objectsAdded + objectsUpdated));
          commit();
          clear();
          beginTransaction();
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
        if (existingConceptCache.containsKey(fields[4])) {
          sourceConcept = existingConceptCache.get(fields[4]);
        }
        if (sourceConcept == null) {
          throw new Exception("Relationship " + fields[0] + " source concept "
              + fields[4] + " cannot be found");
        }

        // Retrieve destination concept
        if (existingConceptCache.containsKey(fields[5])) {
          destinationConcept = existingConceptCache.get(fields[5]);
          // no need to reread because we are not caching this concept
        }
        if (destinationConcept == null) {
          throw new Exception("Relationship " + fields[0]
              + " destination concept " + fields[5] + " cannot be found");
        }

        // Retrieve relationship if it exists
        Relationship relationship = null;
        if (relationshipCache.containsKey(fields[0])) {
          relationship = relationshipCache.get(fields[0]);
        } else if (existingRelationshipIds.contains(fields[0])) {
          relationship =
              getRelationship(fields[0], terminology, terminologyVersion);
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
        newRelationship.setLastModifiedBy(loader);
        newRelationship.setLastModified(releaseVersionDate);
        newRelationship.setPublished(true);
        newRelationship.setWorkflowStatus(published);

        // If relationship is new, add it
        if (!existingRelationshipIds.contains(fields[0])) {
          newRelationship = addRelationship(newRelationship);
          objectsAdded++;
        }

        // If relationship is changed, update it
        else if (relationship != null && !newRelationship.equals(relationship)) {
          updateRelationship(newRelationship);
          objectsUpdated++;
        }

        // Otherwise, reset effective time (for modified check later)
        else {
          if (relationship != null) {
            newRelationship.setEffectiveTime(relationship.getEffectiveTime());
          }
        }

        // Commit every so often
        if ((objectsAdded + objectsUpdated) % commitCt == 0) {
          Logger.getLogger(getClass()).info(
              "    commit - " + (objectsAdded + objectsUpdated));
          commit();
          clear();
          beginTransaction();
        }

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

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.jpa.services.RootServiceJpa#close()
   */
  @Override
  public void close() throws Exception {
    super.close();
    readers = null;
    languageRefSetMemberCache = null;
    existingConceptCache = null;
    existingDescriptionIds = null;
    existingRelationshipIds = null;
    existingLanguageRefSetMemberIds = null;
    existingDescriptionTypeRefSetMemberIds = null;
    existingRefsetDescriptorRefSetMemberIds = null;
    existingModuleDependencyRefSetMemberIds = null;
  }

}
