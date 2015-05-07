/*
 * Copyright 2015 West Coast Informatics, LLC
 */
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
import org.ihtsdo.otf.ts.ReleaseInfo;
import org.ihtsdo.otf.ts.algo.Algorithm;
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
import org.ihtsdo.otf.ts.services.handlers.ComputePreferredNameHandler;
import org.ihtsdo.otf.ts.services.helpers.ProgressEvent;
import org.ihtsdo.otf.ts.services.helpers.ProgressListener;
import org.ihtsdo.otf.ts.services.helpers.PushBackReader;

/**
 * Implementation of an algorithm to import RF2 snapshot data.
 */
public class Rf2SnapshotLoaderAlgorithm extends HistoryServiceJpa implements
    Algorithm {

  /** Listeners. */
  private List<ProgressListener> listeners = new ArrayList<>();

  /** The logging object ct threshold. */
  private final static int logCt = 2000;

  /** The commit count. */
  private final static int commitCt = 2000;

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

  /** hash sets for retrieving concepts. */
  private Map<String, Concept> conceptCache = new HashMap<>(); // used to

  /** hash sets for retrieving descriptions. */
  private Map<String, Description> descriptionCache = new HashMap<>(); // used
                                                                       // to

  /** hash set for storing default preferred names. */
  Map<String, String> defaultPreferredNames = new HashMap<>();

  /** The language ref set members. */
  Map<String, Set<LanguageRefSetMember>> languageRefSetMembers =
      new HashMap<>();

  /** counter for objects created, reset in each load section. */
  int objectCt; //

  /** The init pref name. */
  final String initPrefName = "No default preferred name found";

  /** The loader. */
  final String loader = "loader";

  /** The id. */
  final String id = "id";

  /** The published. */
  final String published = "PUBLISHED";

  /**
   * Instantiates an empty {@link Rf2SnapshotLoaderAlgorithm}.
   * @throws Exception if anything goes wrong
   */
  public Rf2SnapshotLoaderAlgorithm() throws Exception {
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
      Logger.getLogger(getClass()).info("Start loading snapshot");
      Logger.getLogger(getClass()).info("  terminology = " + terminology);
      Logger.getLogger(getClass()).info("  version = " + terminologyVersion);
      Logger.getLogger(getClass()).info("  releaseVersion = " + releaseVersion);
      releaseVersionDate = ConfigUtility.DATE_FORMAT.parse(releaseVersion);

      SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss a"); // format for

      // Track system level information
      long startTimeOrig = System.nanoTime();

      // control transaction scope
      setTransactionPerOperation(false);
      // Turn of ID computation when loading a terminology
      setAssignIdentifiersFlag(false);
      // Let loader set last modified flags.
      setLastModifiedFlag(false);

      // faster performance.
      beginTransaction();

      //
      // Load concepts
      //
      Logger.getLogger(getClass()).info("  Loading Concepts...");
      long startTime = System.nanoTime();
      loadConcepts();
      Logger.getLogger(getClass()).info(
          "    elapsed time = " + getElapsedTime(startTime) + "s"
              + " (Ended at " + ft.format(new Date()) + ")");

      // Commit here, then try relationships
      commit();
      clear();
      beginTransaction();

      //
      // Load descriptions and language refsets
      //
      Logger.getLogger(getClass()).info("  Loading Language Ref Sets...");
      startTime = System.nanoTime();
      loadLanguageRefSetMembers();
      Logger.getLogger(getClass()).info(
          "    elapsed time = " + getElapsedTime(startTime) + "s"
              + " (Ended at " + ft.format(new Date()) + ")");

      Logger.getLogger(getClass()).info("  Loading Descriptions...");
      startTime = System.nanoTime();
      loadDescriptions();
      Logger.getLogger(getClass()).info(
          "    elapsed time = " + getElapsedTime(startTime) + "s"
              + " (Ended at " + ft.format(new Date()) + ")");

      // Commit here, then try relationships
      commit();
      clear();
      beginTransaction();

      //
      // Load relationships
      //
      Logger.getLogger(getClass()).info("  Loading Relationships...");
      startTime = System.nanoTime();
      loadRelationships();
      Logger.getLogger(getClass()).info(
          "    elapsed time = " + getElapsedTime(startTime) + "s"
              + " (Ended at " + ft.format(new Date()) + ")");

      //
      // load AssocationReference RefSets (Content)
      //
      Logger.getLogger(getClass()).info(
          "  Loading Association Reference Ref Sets...");
      startTime = System.nanoTime();
      loadAssociationReferenceRefSets();
      Logger.getLogger(getClass()).info(
          "    elaped time = " + getElapsedTime(startTime).toString() + "s"
              + " (Ended at " + ft.format(new Date()) + ")");

      commit();
      clear();
      beginTransaction();

      //
      // Load AttributeValue RefSets (Content)
      //
      Logger.getLogger(getClass())
          .info("  Loading Attribute Value Ref Sets...");
      startTime = System.nanoTime();
      loadAttributeValueRefSets();
      Logger.getLogger(getClass()).info(
          "    elaped time = " + getElapsedTime(startTime).toString() + "s"
              + " (Ended at " + ft.format(new Date()) + ")");

      commit();
      clear();
      beginTransaction();

      //
      // Load Simple RefSets (Content)
      //
      Logger.getLogger(getClass()).info("  Loading Simple Ref Sets...");
      startTime = System.nanoTime();
      loadSimpleRefSets();
      Logger.getLogger(getClass()).info(
          "    elapsed time = " + getElapsedTime(startTime) + "s"
              + " (Ended at " + ft.format(new Date()) + ")");

      //
      // Load SimpleMapRefSets
      //
      Logger.getLogger(getClass()).info("  Loading Simple Map Ref Sets...");
      startTime = System.nanoTime();
      loadSimpleMapRefSets();
      Logger.getLogger(getClass()).info(
          "    elapsed time = " + getElapsedTime(startTime) + "s"
              + " (Ended at " + ft.format(new Date()) + ")");

      commit();
      clear();
      beginTransaction();

      //
      // Load ComplexMapRefSets
      //
      Logger.getLogger(getClass()).info("  Loading Complex Map Ref Sets...");
      startTime = System.nanoTime();
      loadComplexMapRefSets();
      Logger.getLogger(getClass()).info(
          "    elapsed time = " + getElapsedTime(startTime) + "s"
              + " (Ended at " + ft.format(new Date()) + ")");

      //
      // Load ExtendedMapRefSets
      //
      Logger.getLogger(getClass()).info("  Loading Extended Map Ref Sets...");
      startTime = System.nanoTime();
      loadExtendedMapRefSets();
      Logger.getLogger(getClass()).info(
          "    elapsed time = " + getElapsedTime(startTime) + "s"
              + " (Ended at " + ft.format(new Date()) + ")");

      commit();
      clear();
      beginTransaction();

      //
      // load RefsetDescriptor RefSets (Content)
      //
      Logger.getLogger(getClass()).info(
          "  Loading Refset Descriptor Ref Sets...");
      startTime = System.nanoTime();
      loadRefsetDescriptorRefSets();
      Logger.getLogger(getClass()).info(
          "    elaped time = " + getElapsedTime(startTime).toString() + "s"
              + " (Ended at " + ft.format(new Date()) + ")");

      //
      // load ModuleDependency RefSets (Content)
      //
      Logger.getLogger(getClass()).info(
          "  Loading Module Dependency Ref Sets...");
      startTime = System.nanoTime();
      loadModuleDependencyRefSets();
      Logger.getLogger(getClass()).info(
          "    elaped time = " + getElapsedTime(startTime).toString() + "s"
              + " (Ended at " + ft.format(new Date()) + ")");

      //
      // load DescriptionType RefSets (Content)
      //
      Logger.getLogger(getClass()).info(
          "  Loading Description Type Ref Sets...");
      startTime = System.nanoTime();
      loadDescriptionTypeRefSets();
      Logger.getLogger(getClass()).info(
          "    elaped time = " + getElapsedTime(startTime).toString() + "s"
              + " (Ended at " + ft.format(new Date()) + ")");

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

      // Clear concept cache
      // clear and commit
      commit();
      clear();
      conceptCache.clear();

      Logger.getLogger(getClass()).info(
          getComponentStats(terminology, terminologyVersion));

      // Final logging messages
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
  /**
   * Cancel.
   */
  @Override
  public void cancel() {
    throw new UnsupportedOperationException("cannot cancel.");
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
   * Load concepts.
   * 
   * @throws Exception the exception
   */
  private void loadConcepts() throws Exception {

    String line = "";
    objectCt = 0;

    PushBackReader reader = readers.getReader(Rf2Readers.Keys.CONCEPT);
    while ((line = reader.readLine()) != null) {

      final String fields[] = line.split("\t");
      final Concept concept = new ConceptJpa();

      if (!fields[0].equals(id)) { // header

        // Stop if the effective time is past the release version
        if (fields[1].compareTo(releaseVersion) > 0) {
          reader.push(line);
          break;
        }

        concept.setTerminologyId(fields[0]);
        concept.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        concept.setLastModified(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        concept.setActive(fields[2].equals("1"));
        concept.setModuleId(fields[3].intern());
        concept.setDefinitionStatusId(fields[4]);
        concept.setTerminology(terminology);
        concept.setTerminologyVersion(terminologyVersion);
        concept.setDefaultPreferredName(initPrefName);
        concept.setLastModified(releaseVersionDate);
        concept.setLastModifiedBy(loader);
        concept.setPublished(true);
        concept.setWorkflowStatus(published);

        // copy concept to shed any hibernate stuff
        addConcept(concept);
        conceptCache.put(fields[0], concept);

        if (++objectCt % logCt == 0) {
          Logger.getLogger(getClass()).info("    count = " + objectCt);
        }

      }
    }

    defaultPreferredNames.clear();

  }

  /**
   * Load relationships.
   * 
   * @throws Exception the exception
   */
  private void loadRelationships() throws Exception {

    String line = "";
    objectCt = 0;
    int conceptCt = 1;
    Concept prevConcept = null;

    PushBackReader reader = readers.getReader(Rf2Readers.Keys.RELATIONSHIP);
    // Iterate over relationships
    while ((line = reader.readLine()) != null) {

      // Split line
      final String fields[] = line.split("\t");
      // Skip header
      if (!fields[0].equals(id)) {

        // Stop if the effective time is past the release version
        if (fields[1].compareTo(releaseVersion) > 0) {
          reader.push(line);
          break;
        }

        // Configure relationship
        final Relationship relationship = new RelationshipJpa();
        relationship.setTerminologyId(fields[0]);
        relationship.setEffectiveTime(ConfigUtility.DATE_FORMAT
            .parse(fields[1]));
        relationship
            .setLastModified(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        relationship.setActive(fields[2].equals("1")); // active
        relationship.setModuleId(fields[3].intern()); // moduleId

        relationship.setRelationshipGroup(Integer.valueOf(fields[6])); // relationshipGroup
        relationship.setTypeId(fields[7]); // typeId
        relationship.setCharacteristicTypeId(fields[8].intern()); // characteristicTypeId
        // This is SNOMED specific
        relationship.setStated(fields[8].equals("900000000000010007"));
        relationship.setInferred(fields[8].equals("900000000000011006"));

        relationship.setTerminology(terminology);
        relationship.setTerminologyVersion(terminologyVersion);
        relationship.setModifierId(fields[9].intern());
        relationship.setLastModified(ConfigUtility.DATE_FORMAT
            .parse(releaseVersion));
        relationship.setLastModifiedBy(loader);
        relationship.setPublished(true);
        relationship.setWorkflowStatus(published);

        // get concepts from cache, they just need to have ids
        final Concept sourceConcept = conceptCache.get(fields[4]);
        final Concept destinationConcept = conceptCache.get(fields[5]);
        if (sourceConcept != null && destinationConcept != null) {
          relationship.setSourceConcept(sourceConcept);
          relationship.setDestinationConcept(destinationConcept);
          // unnecessary
          // sourceConcept.addRelationship(relationship);
          addRelationship(relationship);
          if (prevConcept == null) {
            prevConcept = sourceConcept;
          }

        } else {
          if (sourceConcept == null) {
            throw new Exception(
                "Relationship " + relationship.getTerminologyId()
                    + " -existent source concept " + fields[4]);
          }
          if (destinationConcept == null) {
            throw new Exception(
                "Relationship" + relationship.getTerminologyId()
                    + " references non-existent destination concept "
                    + fields[5]);
          }
        }

        if (prevConcept == null) {
          prevConcept = sourceConcept;
        }

        // Identify when concept has changed, increment concept count, update
        if (!relationship.getSourceConcept().getTerminologyId()
            .equals(prevConcept.getTerminologyId())) {
          conceptCt++;
        }

        // Log every so often
        if (++objectCt % logCt == 0) {
          Logger.getLogger(getClass()).info("    count = " + objectCt);
        }

        // Commit every so often
        if (conceptCt % commitCt == 0) {
          commit();
          clear();
          beginTransaction();
        }

        // always set prev concept
        prevConcept = relationship.getSourceConcept();

      }
    }

    // Final commit
    commit();
    clear();
    beginTransaction();

  }

  /**
   * Load descriptions.
   * 
   * @throws Exception the exception
   */
  private void loadDescriptions() throws Exception {

    // PN handler
    ComputePreferredNameHandler pnHandler =
        getComputePreferredNameHandler(terminology);
    Concept prevConcept = null;

    Set<Long> conceptsTouched = new HashSet<>();

    String line = "";
    objectCt = 0;
    int langCt = 0;
    // counter for concepts
    int conceptCt = 1;

    PushBackReader reader = readers.getReader(Rf2Readers.Keys.DESCRIPTION);
    while ((line = reader.readLine()) != null) {

      final String fields[] = line.split("\t");
      if (!fields[0].equals(id)) {

        // Stop if the effective time is past the release version
        if (fields[1].compareTo(releaseVersion) > 0) {
          reader.push(line);
          break;
        }

        final Description description = new DescriptionJpa();
        description.setTerminologyId(fields[0]);
        description
            .setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        description.setLastModified(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        description.setActive(fields[2].equals("1"));
        description.setModuleId(fields[3].intern());

        description.setLanguageCode(fields[5].intern());
        description.setTypeId(fields[6].intern());
        description.setTerm(fields[7]);
        description.setCaseSignificanceId(fields[8].intern());
        description.setTerminology(terminology);
        description.setTerminologyVersion(terminologyVersion);
        description.setLastModified(ConfigUtility.DATE_FORMAT
            .parse(releaseVersion));
        description.setLastModifiedBy(loader);
        description.setPublished(true);
        description.setWorkflowStatus(published);

        // set concept from cache and set initial prev concept
        Concept concept = conceptCache.get(fields[4]);
        if (prevConcept == null) {
          prevConcept = concept;
        }

        // Attach language refset members (if there are any)
        if (languageRefSetMembers.containsKey(description.getTerminologyId())) {
          for (LanguageRefSetMember member : languageRefSetMembers
              .get(description.getTerminologyId())) {
            langCt++;
            member.setDescription(description);
            description.addLanguageRefSetMember(member);

            // Check if this language refset and description form the
            // defaultPreferredName
            if (pnHandler.isPreferredName(description, member)) {
              // check for already assigned
              if (defaultPreferredNames.get(concept.getTerminologyId()) != null) {
                Logger.getLogger(getClass()).info(
                    "Multiple default preferred names for concept "
                        + concept.getTerminologyId());
                Logger.getLogger(getClass())
                    .info(
                        "  "
                            + "Existing: "
                            + defaultPreferredNames.get(concept
                                .getTerminologyId()));
                Logger.getLogger(getClass()).info(
                    "  " + "Replaced: " + description.getTerm());
              }
              // Save preferred name for later
              defaultPreferredNames.put(concept.getTerminologyId(),
                  description.getTerm());

            }
          }
          // Remove used ones so we can keep track
          languageRefSetMembers.remove(description.getTerminologyId());
        } else {
          // Early SNOMED release have no languages
          Logger.getLogger(getClass()).debug(
              "  Description has no languages: "
                  + description.getTerminologyId());
        }

        if (concept != null) {
          description.setConcept(concept);
          // unnecessary
          // concept.addDescription(description);

          // this also adds language refset entries
          addDescription(description);
          // Cache description for connecting refsets later
          descriptionCache.put(description.getTerminologyId(), description);
        } else {
          throw new Exception("Description " + description.getTerminologyId()
              + " references non-existent concept " + fields[4]);
        }

        // Identify when concept has changed, increment concept count,
        // set preferred name, and update concept
        if (!description.getConcept().getTerminologyId()
            .equals(prevConcept.getTerminologyId())) {
          conceptCt++;
          conceptsTouched.add(prevConcept.getId());
        }

        // Log and commit
        if (++objectCt % logCt == 0) {
          Logger.getLogger(getClass()).info("    count = " + objectCt);
        }
        if (conceptCt % commitCt == 0) {
          commit();
          clear();
          beginTransaction();
        }

        // Set prev concept id
        prevConcept = description.getConcept();
      }
    }
    if (prevConcept != null) {
      conceptsTouched.add(prevConcept.getId());
    }
    commit();
    clear();
    beginTransaction();

    Logger.getLogger(getClass()).info(
        "      " + objectCt + " descriptions loaded for " + (conceptCt - 1)
            + " concepts");
    Logger.getLogger(getClass()).info(
        "      " + langCt + " language ref sets loaded");

    // Set concept preferred names
    objectCt = 0;
    conceptCt = 0;
    Logger.getLogger(getClass()).info("  Set concept preferred names");
    for (Long id : conceptsTouched) {
      Concept concept = getConcept(id);
      if (defaultPreferredNames.get(concept.getTerminologyId()) != null) {
        concept.setDefaultPreferredName(defaultPreferredNames.get(concept
            .getTerminologyId()));
      } else {
        concept.setDefaultPreferredName("No default preferred name found");
      }
      updateConcept(concept);
      if (++objectCt % logCt == 0) {
        Logger.getLogger(getClass()).info("    count = " + objectCt);
      }
      if (++conceptCt % commitCt == 0) {
        commit();
        clear();
        beginTransaction();
      }
    }

    commit();
    clear();
    beginTransaction();

    if (languageRefSetMembers.size() > 0) {
      throw new Exception("There are unattached language refset members: "
          + languageRefSetMembers);
    }
    languageRefSetMembers = null;
  }

  /**
   * Load and cache all language refset members.
   * @throws Exception the exception
   */
  private void loadLanguageRefSetMembers() throws Exception {

    PushBackReader reader = readers.getReader(Rf2Readers.Keys.LANGUAGE);
    String line;
    while ((line = reader.readLine()) != null) {

      final String fields[] = line.split("\t");

      if (!fields[0].equals(id)) { // header

        // Stop if the effective time is past the release version
        if (fields[1].compareTo(releaseVersion) > 0) {
          reader.push(line);
          return;
        }
        final LanguageRefSetMember member = new LanguageRefSetMemberJpa();

        // Universal RefSet attributes
        member.setTerminologyId(fields[0]);
        member.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setLastModified(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setActive(fields[2].equals("1"));
        member.setModuleId(fields[3].intern());
        member.setRefSetId(fields[4].intern());
        member.setTerminology(terminology);
        member.setTerminologyVersion(terminologyVersion);
        member.setLastModified(releaseVersionDate);
        member.setLastModifiedBy(loader);
        member.setPublished(true);

        // Language unique attributes
        member.setAcceptabilityId(fields[6].intern());

        // Cache language refset members
        if (!languageRefSetMembers.containsKey(fields[5])) {
          languageRefSetMembers.put(fields[5],
              new HashSet<LanguageRefSetMember>());
        }
        languageRefSetMembers.get(fields[5]).add(member);
      }
    }
  }

  /**
   * Load AttributeRefSets (Content).
   * 
   * @throws Exception the exception
   */
  private void loadAttributeValueRefSets() throws Exception {

    String line = "";
    objectCt = 0;

    // Iterate through attribute value entries
    PushBackReader reader = readers.getReader(Rf2Readers.Keys.ATTRIBUTE_VALUE);
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      final String fields[] = line.split("\t");

      if (!fields[0].equals(id)) { // header

        // Stop if the effective time is past the release version
        if (fields[1].compareTo(releaseVersion) > 0) {
          reader.push(line);
          break;
        }

        AttributeValueRefSetMember<?> member = null;

        if (conceptCache.containsKey(fields[5])) {
          member = new AttributeValueConceptRefSetMemberJpa();
          // Retrieve concept -- firstToken is referencedComponentId
          final Concept concept = conceptCache.get(fields[5]);
          ((AttributeValueConceptRefSetMember) member).setConcept(concept);
        } else if (descriptionCache.containsKey(fields[5])) {
          member = new AttributeValueDescriptionRefSetMemberJpa();
          final Description description = descriptionCache.get(fields[5]);
          ((AttributeValueDescriptionRefSetMember) member)
              .setDescription(description);
        } else {
          throw new Exception(
              "Attribute value member connected to nonexistent object");
        }

        // Universal RefSet attributes
        member.setTerminologyId(fields[0]);
        member.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setLastModified(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setActive(fields[2].equals("1"));
        member.setLastModified(releaseVersionDate);
        member.setLastModifiedBy(loader);
        member.setPublished(true);
        member.setModuleId(fields[3].intern());
        member.setRefSetId(fields[4]);

        // AttributeValueRefSetMember unique attributes
        member.setValueId(fields[6]);

        // Terminology attributes
        member.setTerminology(terminology);
        member.setTerminologyVersion(terminologyVersion);

        addAttributeValueRefSetMember(member);

        // regularly commit at intervals
        if (++objectCt % logCt == 0) {
          Logger.getLogger(getClass()).info("  count = " + objectCt);
        }

      }
    }

  }

  /**
   * Load Association Reference Refset (Content).
   * 
   * @throws Exception the exception
   */
  private void loadAssociationReferenceRefSets() throws Exception {

    String line = "";
    objectCt = 0;

    PushBackReader reader =
        readers.getReader(Rf2Readers.Keys.ASSOCIATION_REFERENCE);
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      final String fields[] = line.split("\t");

      if (!fields[0].equals(id)) { // header

        // Stop if the effective time is past the release version
        if (fields[1].compareTo(releaseVersion) > 0) {
          reader.push(line);
          break;
        }

        AssociationReferenceRefSetMember<?> member = null;

        if (conceptCache.containsKey(fields[5])) {
          member = new AssociationReferenceConceptRefSetMemberJpa();
          // Retrieve concept -- firstToken is referencedComponentId
          final Concept concept = conceptCache.get(fields[5]);
          ((AssociationReferenceConceptRefSetMember) member)
              .setConcept(concept);
        } else if (descriptionCache.containsKey(fields[5])) {
          member = new AssociationReferenceDescriptionRefSetMemberJpa();
          final Description description = descriptionCache.get(fields[5]);
          ((AssociationReferenceDescriptionRefSetMember) member)
              .setDescription(description);
          if (description.getId() == null) {
            Logger.getLogger(getClass()).error(
                "UNSAVED description = " + description.getTerminologyId());
          }
        } else {
          throw new Exception(
              "Association reference member connected to nonexistent object");
        }

        // Universal RefSet attributes
        member.setTerminologyId(fields[0]);
        member.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setLastModified(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setActive(fields[2].equals("1"));
        member.setModuleId(fields[3].intern());
        member.setRefSetId(fields[4]);

        // AssociationReferenceRefSetMember unique attributes
        member.setTargetComponentId(fields[6]);

        // Terminology attributes
        member.setTerminology(terminology);
        member.setTerminologyVersion(terminologyVersion);
        member.setLastModified(releaseVersionDate);
        member.setLastModifiedBy(loader);
        member.setPublished(true);

        addAssociationReferenceRefSetMember(member);

        // regularly commit at intervals
        if (++objectCt % logCt == 0) {
          Logger.getLogger(getClass()).info("  count = " + objectCt);
        }

      }
    }

  }

  /**
   * Load SimpleRefSets (Content).
   * 
   * @throws Exception the exception
   */
  private void loadSimpleRefSets() throws Exception {

    String line = "";
    objectCt = 0;

    PushBackReader reader = readers.getReader(Rf2Readers.Keys.SIMPLE);
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      final String fields[] = line.split("\t");

      if (!fields[0].equals(id)) { // header

        // Stop if the effective time is past the release version
        if (fields[1].compareTo(releaseVersion) > 0) {
          reader.push(line);
          break;
        }

        final SimpleRefSetMember member = new SimpleRefSetMemberJpa();
        // Universal RefSet attributes
        member.setTerminologyId(fields[0]);
        member.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setLastModified(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setActive(fields[2].equals("1"));
        member.setModuleId(fields[3].intern());
        member.setRefSetId(fields[4]);

        // SimpleRefSetMember unique attributes
        // NONE

        // Terminology attributes
        member.setTerminology(terminology);
        member.setTerminologyVersion(terminologyVersion);
        member.setLastModified(releaseVersionDate);
        member.setLastModifiedBy(loader);
        member.setPublished(true);

        // Retrieve Concept -- firstToken is referencedComonentId
        final Concept concept = conceptCache.get(fields[5]);

        if (concept != null) {
          member.setConcept(concept);
          addSimpleRefSetMember(member);

          // regularly commit at intervals
          if (++objectCt % logCt == 0) {
            Logger.getLogger(getClass()).info("    count = " + objectCt);
          }
        } else {
          Logger.getLogger(getClass()).info(
              "simpleRefSetMember " + member.getTerminologyId()
                  + " references non-existent concept " + fields[5]);
        }
      }
    }

  }

  /**
   * Load SimpleMapRefSets (Crossmap).
   * 
   * @throws Exception the exception
   */
  private void loadSimpleMapRefSets() throws Exception {

    String line = "";
    objectCt = 0;

    PushBackReader reader = readers.getReader(Rf2Readers.Keys.SIMPLE_MAP);
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      final String fields[] = line.split("\t");

      if (!fields[0].equals(id)) { // header
        // Stop if the effective time is past the release version
        if (fields[1].compareTo(releaseVersion) > 0) {
          reader.push(line);
          break;
        }

        final SimpleMapRefSetMember member = new SimpleMapRefSetMemberJpa();

        // Universal RefSet attributes
        member.setTerminologyId(fields[0]);
        member.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setLastModified(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setActive(fields[2].equals("1"));
        member.setModuleId(fields[3].intern());
        member.setRefSetId(fields[4]);

        // SimpleMap unique attributes
        member.setMapTarget(fields[6]);

        // Terminology attributes
        member.setTerminology(terminology);
        member.setTerminologyVersion(terminologyVersion);
        member.setLastModified(releaseVersionDate);
        member.setLastModifiedBy(loader);
        member.setPublished(true);

        // Retrieve concept -- firstToken is referencedComponentId
        final Concept concept = conceptCache.get(fields[5]);

        if (concept != null) {
          member.setConcept(concept);
          addSimpleMapRefSetMember(member);

          // regularly commit at intervals
          if (++objectCt % logCt == 0) {
            Logger.getLogger(getClass()).info("    count = " + objectCt);

          }
        } else {
          Logger.getLogger(getClass()).info(
              "simpleMapRefSetMember " + member.getTerminologyId()
                  + " references non-existent concept " + fields[5]);
        }
      }
    }

  }

  /**
   * Load ComplexMapRefSets (Crossmap).
   * 
   * @throws Exception the exception
   */
  private void loadComplexMapRefSets() throws Exception {

    String line = "";
    objectCt = 0;

    PushBackReader reader = readers.getReader(Rf2Readers.Keys.COMPLEX_MAP);
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      final String fields[] = line.split("\t");

      if (!fields[0].equals(id)) { // header
        // Stop if the effective time is past the release version
        if (fields[1].compareTo(releaseVersion) > 0) {
          reader.push(line);
          break;
        }
        final ComplexMapRefSetMember member = new ComplexMapRefSetMemberJpa();
        member.setTerminologyId(fields[0]);
        member.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setLastModified(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setActive(fields[2].equals("1"));
        member.setModuleId(fields[3].intern());
        member.setRefSetId(fields[4]);
        // conceptId

        // ComplexMap unique attributes
        member.setMapGroup(Integer.parseInt(fields[6]));
        member.setMapPriority(Integer.parseInt(fields[7]));
        member.setMapRule(fields[8]);
        member.setMapAdvice(fields[9]);
        member.setMapTarget(fields[10]);
        member.setMapRelationId(fields[11]);

        // Terminology attributes
        member.setTerminology(terminology);
        member.setTerminologyVersion(terminologyVersion);
        member.setLastModified(releaseVersionDate);
        member.setLastModifiedBy(loader);
        member.setPublished(true);

        // set Concept
        final Concept concept = conceptCache.get(fields[5]);

        if (concept != null) {
          member.setConcept(concept);
          addComplexMapRefSetMember(member);

          // regularly commit at intervals
          if (++objectCt % logCt == 0) {
            Logger.getLogger(getClass()).info("    count = " + objectCt);
          }
        } else {
          Logger.getLogger(getClass()).info(
              "Complex map connected to nonexistent concept: " + fields[5]);
        }

      }
    }

  }

  /**
   * Load extended map ref sets.
   *
   * @throws Exception the exception
   */
  private void loadExtendedMapRefSets() throws Exception {

    String line = "";
    objectCt = 0;

    PushBackReader reader = readers.getReader(Rf2Readers.Keys.EXTENDED_MAP);
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      final String fields[] = line.split("\t");

      if (!fields[0].equals(id)) {

        // Stop if the effective time is past the release version
        if (fields[1].compareTo(releaseVersion) > 0) {
          reader.push(line);
          break;
        }

        final ComplexMapRefSetMember member = new ComplexMapRefSetMemberJpa();
        member.setTerminologyId(fields[0]);
        member.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setLastModified(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setActive(fields[2].equals("1"));
        member.setModuleId(fields[3].intern());
        member.setRefSetId(fields[4]);
        // conceptId

        // ComplexMap unique attributes
        member.setMapGroup(Integer.parseInt(fields[6]));
        member.setMapPriority(Integer.parseInt(fields[7]));
        member.setMapRule(fields[8]);
        member.setMapAdvice(fields[9]);
        member.setMapTarget(fields[10]);
        member.setMapRelationId(fields[12]);

        // Terminology attributes
        member.setTerminology(terminology);
        member.setTerminologyVersion(terminologyVersion);
        member.setLastModified(releaseVersionDate);
        member.setLastModifiedBy(loader);
        member.setPublished(true);

        // set Concept
        final Concept concept = conceptCache.get(fields[5]);

        if (concept != null) {
          member.setConcept(concept);
          addComplexMapRefSetMember(member);

          // regularly commit at intervals
          if (++objectCt % logCt == 0) {
            Logger.getLogger(getClass()).info("    count = " + objectCt);

          }
        } else {
          Logger.getLogger(getClass()).info(
              "complexMapRefSetMember " + member.getTerminologyId()
                  + " references non-existent concept " + fields[5]);
        }

      }
    }

  }

  /**
   * Load refset descriptor ref sets.
   *
   * @throws Exception the exception
   */
  private void loadRefsetDescriptorRefSets() throws Exception {

    String line = "";
    objectCt = 0;

    PushBackReader reader =
        readers.getReader(Rf2Readers.Keys.REFSET_DESCRIPTOR);
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      final String fields[] = line.split("\t");

      if (!fields[0].equals(id)) {

        // Stop if the effective time is past the release version
        if (fields[1].compareTo(releaseVersion) > 0) {
          reader.push(line);
          break;
        }

        final RefsetDescriptorRefSetMember member =
            new RefsetDescriptorRefSetMemberJpa();
        member.setTerminologyId(fields[0]);
        member.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setLastModified(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setActive(fields[2].equals("1"));
        member.setModuleId(fields[3].intern());
        member.setRefSetId(fields[4]);
        // conceptId

        // Refset descriptor unique attributes
        member.setAttributeDescription(fields[6]);
        member.setAttributeType(fields[7]);
        member.setAttributeOrder(Integer.valueOf(fields[8]));

        // Terminology attributes
        member.setTerminology(terminology);
        member.setTerminologyVersion(terminologyVersion);
        member.setLastModified(releaseVersionDate);
        member.setLastModifiedBy(loader);
        member.setPublished(true);

        // set Concept
        final Concept concept = conceptCache.get(fields[5]);

        if (concept != null) {
          member.setConcept(concept);
          addRefsetDescriptorRefSetMember(member);

          // regularly commit at intervals
          if (++objectCt % logCt == 0) {
            Logger.getLogger(getClass()).info("    count = " + objectCt);
          }
        } else {
          throw new Exception("RefsetDescriptorRefSetMember "
              + member.getTerminologyId() + " references non-existent concept "
              + fields[5]);
        }

      }
    }

  }

  /**
   * Load module dependency refset members.
   *
   * @throws Exception the exception
   */
  private void loadModuleDependencyRefSets() throws Exception {

    String line = "";
    objectCt = 0;

    PushBackReader reader =
        readers.getReader(Rf2Readers.Keys.MODULE_DEPENDENCY);
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      final String fields[] = line.split("\t");

      if (!fields[0].equals(id)) {

        // Stop if the effective time is past the release version
        if (fields[1].compareTo(releaseVersion) > 0) {
          reader.push(line);
          break;
        }

        final ModuleDependencyRefSetMember member =
            new ModuleDependencyRefSetMemberJpa();
        member.setTerminologyId(fields[0]);
        member.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setLastModified(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setActive(fields[2].equals("1"));
        member.setModuleId(fields[3].intern());
        member.setRefSetId(fields[4]);
        // conceptId

        // Refset descriptor unique attributes
        member.setSourceEffectiveTime(ConfigUtility.DATE_FORMAT
            .parse(fields[6]));
        member.setTargetEffectiveTime(ConfigUtility.DATE_FORMAT
            .parse(fields[7]));

        // Terminology attributes
        member.setTerminology(terminology);
        member.setTerminologyVersion(terminologyVersion);
        member.setLastModified(releaseVersionDate);
        member.setLastModifiedBy(loader);
        member.setPublished(true);

        // set Concept
        final Concept concept = conceptCache.get(fields[5]);

        if (concept != null) {
          member.setConcept(concept);
          addModuleDependencyRefSetMember(member);

          // regularly commit at intervals
          if (++objectCt % logCt == 0) {
            Logger.getLogger(getClass()).info("    count = " + objectCt);
          }
        } else {
          throw new Exception("ModuleDependencyRefSetMember "
              + member.getTerminologyId() + " references non-existent concept "
              + fields[5]);
        }

      }
    }

  }

  /**
   * Load description type refset members.
   *
   * @throws Exception the exception
   */
  private void loadDescriptionTypeRefSets() throws Exception {

    String line = "";
    objectCt = 0;

    PushBackReader reader = readers.getReader(Rf2Readers.Keys.DESCRIPTION_TYPE);
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      final String fields[] = line.split("\t");

      if (!fields[0].equals(id)) {

        // Stop if the effective time is past the release version
        if (fields[1].compareTo(releaseVersion) > 0) {
          reader.push(line);
          break;
        }

        final DescriptionTypeRefSetMember member =
            new DescriptionTypeRefSetMemberJpa();
        member.setTerminologyId(fields[0]);
        member.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setLastModified(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setActive(fields[2].equals("1"));
        member.setModuleId(fields[3].intern());
        member.setRefSetId(fields[4]);
        // conceptId

        // Refset descriptor unique attributes
        member.setDescriptionFormat(fields[6]);
        member.setDescriptionLength(Integer.valueOf(fields[7]));

        // Terminology attributes
        member.setTerminology(terminology);
        member.setTerminologyVersion(terminologyVersion);
        member.setLastModified(releaseVersionDate);
        member.setLastModifiedBy(loader);
        member.setPublished(true);

        // set Concept
        final Concept concept = conceptCache.get(fields[5]);

        if (concept != null) {
          member.setConcept(concept);
          addDescriptionTypeRefSetMember(member);

          // regularly commit at intervals
          if (++objectCt % logCt == 0) {
            Logger.getLogger(getClass()).info("    count = " + objectCt);
          }
        } else {
          throw new Exception("DescriptionTypeRefSetMember "
              + member.getTerminologyId() + " references non-existent concept "
              + fields[5]);
        }

      }
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
    conceptCache = null;
    descriptionCache = null;
    defaultPreferredNames = null;
  }
}
