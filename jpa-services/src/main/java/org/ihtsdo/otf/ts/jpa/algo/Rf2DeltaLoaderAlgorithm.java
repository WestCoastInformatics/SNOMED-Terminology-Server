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

  /** The existing simple ref set member ids. */
  private Set<String> existingSimpleRefSetMemberIds = new HashSet<>();

  /** The existing simple map ref set member ids. */
  private Set<String> existingSimpleMapRefSetMemberIds = new HashSet<>();

  /**
   * The existing complex map ref set member ids. This also includes extended
   * map ref set member ids.
   */
  private Set<String> existingComplexMapRefSetMemberIds = new HashSet<>();

  /** The existing description type ref set member ids. */
  private Set<String> existingDescriptionTypeRefSetMemberIds = new HashSet<>();

  /** The existing refset descriptor ref set member ids. */
  private Set<String> existingRefsetDescriptorRefSetMemberIds = new HashSet<>();

  /** The existing module dependency ref set member ids. */
  private Set<String> existingModuleDependencyRefSetMemberIds = new HashSet<>();

  /** The existing attribute value ref set member ids. */
  private Set<String> existingAttributeValueRefSetMemberIds = new HashSet<>();

  /** The existing association reference ref set member ids. */
  private Set<String> existingAssociationReferenceRefSetMemberIds =
      new HashSet<>();

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

      Logger.getLogger(getClass()).info("  Cache simple map refset member ids");
      existingSimpleMapRefSetMemberIds =
          new HashSet<>(getAllSimpleMapRefSetMemberTerminologyIds(terminology,
              terminologyVersion).getObjects());
      Logger.getLogger(getClass()).info(
          "    count = " + existingSimpleMapRefSetMemberIds.size());

      Logger.getLogger(getClass())
          .info("  Cache complex map refset member ids");
      existingComplexMapRefSetMemberIds =
          new HashSet<>(getAllComplexMapRefSetMemberTerminologyIds(terminology,
              terminologyVersion).getObjects());
      Logger.getLogger(getClass()).info(
          "    count = " + existingComplexMapRefSetMemberIds.size());

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

      Logger.getLogger(getClass()).info(
          "  Cache attribute value refset member ids");
      existingAttributeValueRefSetMemberIds =
          new HashSet<>(getAllAttributeValueRefSetMemberTerminologyIds(
              terminology, terminologyVersion).getObjects());
      Logger.getLogger(getClass()).info(
          "    count = " + existingAttributeValueRefSetMemberIds.size());

      Logger.getLogger(getClass()).info(
          "  Cache association reference refset member ids");
      existingAssociationReferenceRefSetMemberIds =
          new HashSet<>(getAllAssociationReferenceRefSetMemberTerminologyIds(
              terminology, terminologyVersion).getObjects());
      Logger.getLogger(getClass()).info(
          "    count = " + existingAssociationReferenceRefSetMemberIds.size());

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

      //
      // Load simple map refset members
      //
      Logger.getLogger(getClass()).info("    Loading Simple Map Ref Sets...");
      loadSimpleMapRefSetMembers();

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

      //
      // Load module dependency refset members
      //
      Logger.getLogger(getClass()).info(
          "    Loading Attribute Value Ref Sets...");
      loadAttributeValueRefSetMembers();

      //
      // Load association reference refset members
      //
      Logger.getLogger(getClass()).info(
          "    Loading Association Reference Ref Sets...");
      loadAssociationReferenceRefSetMembers();

      // Update descriptions

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
          updateConcept(concept);
        }
      }

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
            newDescription.getConcept().removeDescription(description);
            newDescription.getConcept().addDescription(newDescription);
            objectsUpdated++;
          }

          // Otherwise, reset effective time (for modified check later)
          else {
            newDescription.setEffectiveTime(description.getEffectiveTime());
          }
          // only cache the description if it is not there
          // so we avoid a changed description being different
          // in the cache than in the concept
          if (!descriptionCache.containsKey(newDescription.getTerminologyId())) {
            cacheDescription(newDescription);
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
        System.out.println(line);
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
          System.out.println("  description in cache");
          description = descriptionCache.get(fields[5]);
        } else {
          System.out.println("  load description from db");
          // the description may not yet be in the cache because
          // the language refset entry could be the first element for
          // the concept that is changed. After the cache concept call
          // below, the description will be in the cache next time
          description =
              getDescription(fields[5], terminology, terminologyVersion);
        }

        // get the concept
        Concept concept = description.getConcept();
        // description should have concept
        if (concept == null) {
          throw new Exception("Description" + fields[0]
              + " does not have concept");

        }

        System.out.println("  cache concept " + concept.getTerminologyId());
        if (existingConceptCache.containsKey(concept.getTerminologyId())) {
          System.out.println("    concept in existing cache");
        }
        cacheConcept(concept);

        // Ensure effective time is set on all appropriate objects
        LanguageRefSetMember member = null;
        if (languageRefSetMemberCache.containsKey(fields[0])) {
          System.out.println("  language in cache");
          member = languageRefSetMemberCache.get(fields[0]);
          // to investigate if there will be an update
        } else if (existingLanguageRefSetMemberIds.contains(fields[0])) {
          // If the language exists, it should be in the cache
          // from the cache concept call
          throw new Exception("Language member unexpectedly not in cache: "
              + fields[0] + ", " + fields[5] + ", "
              + concept.getTerminologyId());
        }

        // Setup delta language entry (either new or based on existing
        // one)
        LanguageRefSetMember newMember = null;
        if (member == null) {
          System.out.println("  prepare to add language");
          newMember = new LanguageRefSetMemberJpa();
        } else {
          System.out.println("  prepare to update language");
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
          System.out.println("  add language - " + newMember);
          newMember.getDescription().addLanguageRefSetMember(newMember);
          objectsAdded++;
        }

        // If language refset entry is changed, update it
        else if (!newMember.equals(member)) {
          Logger.getLogger(getClass())
              .debug("  update language - " + newMember);
          System.out.println("  update language - " + newMember);
          System.out.println("    old = " + member);
          System.out.println("    new = " + newMember);

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
  private void loadSimpleRefSetMembers() throws Exception {

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
        if (conceptCache.containsKey(fields[5])) {
          concept = conceptCache.get(fields[5]);
        } else if (existingConceptCache.containsKey(fields[5])) {
          concept = existingConceptCache.get(fields[5]);
        } else {
          // retrieve concept
          concept =
              getSingleConcept(fields[5], terminology, terminologyVersion);
        }

        cacheConcept(concept);

        // Get the member from the DB, if null, create a new one
        // No cache necessary because we will not encounter the
        // same object more than once per delta and nothing else
        // is connected to it.
        SimpleRefSetMember member = null;
        if (existingSimpleRefSetMemberIds.contains(fields[0])) {
          member =
              getSimpleRefSetMember(fields[0], terminology, terminologyVersion);
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
        if (conceptCache.containsKey(fields[5])) {
          concept = conceptCache.get(fields[5]);
        } else if (existingConceptCache.containsKey(fields[5])) {
          concept = existingConceptCache.get(fields[5]);
        } else {
          // retrieve concept
          concept =
              getSingleConcept(fields[5], terminology, terminologyVersion);
        }

        cacheConcept(concept);

        // Get the member from the DB, if null, create a new one
        // No cache necessary because we will not encounter the
        // same object more than once per delta and nothing else
        // is connected to it.
        SimpleMapRefSetMember member = null;
        if (existingSimpleMapRefSetMemberIds.contains(fields[0])) {
          member =
              getSimpleMapRefSetMember(fields[0], terminology,
                  terminologyVersion);
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
        if (conceptCache.containsKey(fields[5])) {
          concept = conceptCache.get(fields[5]);
        } else if (existingConceptCache.containsKey(fields[5])) {
          concept = existingConceptCache.get(fields[5]);
        } else {
          // retrieve concept
          concept =
              getSingleConcept(fields[5], terminology, terminologyVersion);
        }

        cacheConcept(concept);

        // Get the member from the DB, if null, create a new one
        // No cache necessary because we will not encounter the
        // same object more than once per delta and nothing else
        // is connected to it.
        ComplexMapRefSetMember member = null;
        if (existingComplexMapRefSetMemberIds.contains(fields[0])) {
          member =
              getComplexMapRefSetMember(fields[0], terminology,
                  terminologyVersion);
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
        if (conceptCache.containsKey(fields[5])) {
          concept = conceptCache.get(fields[5]);
        } else if (existingConceptCache.containsKey(fields[5])) {
          concept = existingConceptCache.get(fields[5]);
        } else {
          // retrieve concept
          concept =
              getSingleConcept(fields[5], terminology, terminologyVersion);
        }

        cacheConcept(concept);

        // Get the member from the DB, if null, create a new one
        // No cache necessary because we will not encounter the
        // same object more than once per delta and nothing else
        // is connected to it.
        ComplexMapRefSetMember member = null;
        if (existingComplexMapRefSetMemberIds.contains(fields[0])) {
          member =
              getComplexMapRefSetMember(fields[0], terminology,
                  terminologyVersion);
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
        if (conceptCache.containsKey(fields[5])) {
          concept = conceptCache.get(fields[5]);
        } else if (existingConceptCache.containsKey(fields[5])) {
          concept = existingConceptCache.get(fields[5]);
        } else {
          // retrieve concept
          concept =
              getSingleConcept(fields[5], terminology, terminologyVersion);
        }

        cacheConcept(concept);

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
        if (conceptCache.containsKey(fields[5])) {
          concept = conceptCache.get(fields[5]);
        } else if (existingConceptCache.containsKey(fields[5])) {
          concept = existingConceptCache.get(fields[5]);
        } else {
          // retrieve concept
          concept =
              getSingleConcept(fields[5], terminology, terminologyVersion);
        }

        cacheConcept(concept);

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
        if (conceptCache.containsKey(fields[5])) {
          concept = conceptCache.get(fields[5]);
        } else if (existingConceptCache.containsKey(fields[5])) {
          concept = existingConceptCache.get(fields[5]);
        } else {
          // retrieve concept
          concept =
              getSingleConcept(fields[5], terminology, terminologyVersion);
        }

        cacheConcept(concept);

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
        if (conceptCache.containsKey(fields[5])) {
          concept = conceptCache.get(fields[5]);
        } else if (existingConceptCache.containsKey(fields[5])) {
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
        if (existingAttributeValueRefSetMemberIds.contains(fields[0])) {
          member =
              getAttributeValueRefSetMember(fields[0], terminology,
                  terminologyVersion);
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
        if (conceptCache.containsKey(fields[5])) {
          concept = conceptCache.get(fields[5]);
        } else if (existingConceptCache.containsKey(fields[5])) {
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
        if (existingAssociationReferenceRefSetMemberIds.contains(fields[0])) {
          member =
              getAssociationReferenceRefSetMember(fields[0], terminology,
                  terminologyVersion);
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
        newRelationship.setLastModifiedBy(loader);
        newRelationship.setLastModified(releaseVersionDate);
        newRelationship.setPublished(true);
        newRelationship.setWorkflowStatus(published);

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
    existingSimpleRefSetMemberIds = null;
    existingSimpleMapRefSetMemberIds = null;
    existingComplexMapRefSetMemberIds = null;
    existingDescriptionTypeRefSetMemberIds = null;
    existingRefsetDescriptorRefSetMemberIds = null;
    existingModuleDependencyRefSetMemberIds = null;
    existingAttributeValueRefSetMemberIds = null;
    existingAssociationReferenceRefSetMemberIds = null;

  }

}
