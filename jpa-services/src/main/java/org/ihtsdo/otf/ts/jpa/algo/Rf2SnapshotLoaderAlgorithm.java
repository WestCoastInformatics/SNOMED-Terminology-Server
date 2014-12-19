package org.ihtsdo.otf.ts.jpa.algo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.helper.ProgressEvent;
import org.ihtsdo.otf.ts.jpa.services.helper.ProgressListener;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceRefSetMember;
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
import org.ihtsdo.otf.ts.rf2.jpa.AttributeValueConceptRefSetMemberJpa;
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
import org.ihtsdo.otf.ts.services.ContentService;
import org.ihtsdo.otf.ts.services.handlers.ComputePreferredNameHandler;

/**
 * Implementation of an algorithm to import RF2 snapshot data.
 */
public class Rf2SnapshotLoaderAlgorithm extends ContentServiceJpa implements
    Algorithm {

  /** Listeners. */
  private List<ProgressListener> listeners = new ArrayList<>();

  /** The Constant commitCt. */
  private final static int commitCt = 2000;

  /** The terminology. */
  private String terminology;

  /** The terminology version. */
  private String terminologyVersion;

  /** The release version. */
  private String releaseVersion;

  /** The readers. */
  private Rf2Readers readers;

  /** hash sets for retrieving concepts. */
  private Map<String, Concept> conceptCache = new HashMap<>(); // used to

  /** hash set for storing default preferred names. */
  Map<Long, String> defaultPreferredNames = new HashMap<>();

  /** counter for objects created, reset in each load section. */
  int objectCt; //

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
      Logger.getLogger(this.getClass()).info("Start loading snapshot");

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

      //
      // Load concepts
      //
      Logger.getLogger(this.getClass()).info("  Loading Concepts...");
      long startTime = System.nanoTime();
      loadConcepts();
      Logger.getLogger(this.getClass()).info(
          "    elapsed time = " + getElapsedTime(startTime) + "s"
              + " (Ended at " + ft.format(new Date()) + ")");

      //
      // Load descriptions and language refsets
      //
      Logger.getLogger(this.getClass()).info(
          "  Loading Descriptions and LanguageRefSets...");
      startTime = System.nanoTime();
      loadDescriptionsAndLanguageRefSets();
      Logger.getLogger(this.getClass()).info(
          "    elapsed time = " + getElapsedTime(startTime) + "s"
              + " (Ended at " + ft.format(new Date()) + ")");

      //
      // Set default preferred names
      //
      Logger.getLogger(this.getClass()).info(
          "  Setting default preferred names for all concepts...");
      startTime = System.nanoTime();
      setDefaultPreferredNames();
      Logger.getLogger(this.getClass()).info(
          "    elapsed time = " + getElapsedTime(startTime).toString() + "s");

      //
      // Load relationships
      //
      Logger.getLogger(this.getClass()).info("  Loading Relationships...");
      startTime = System.nanoTime();
      loadRelationships();
      Logger.getLogger(this.getClass()).info(
          "    elapsed time = " + getElapsedTime(startTime) + "s"
              + " (Ended at " + ft.format(new Date()) + ")");

      //
      // Load Simple RefSets (Content)
      //
      Logger.getLogger(this.getClass()).info("  Loading Simple RefSets...");
      startTime = System.nanoTime();
      loadSimpleRefSets();
      Logger.getLogger(this.getClass()).info(
          "    elapsed time = " + getElapsedTime(startTime) + "s"
              + " (Ended at " + ft.format(new Date()) + ")");

      //
      // Load SimpleMapRefSets
      //
      Logger.getLogger(this.getClass()).info("  Loading SimpleMap RefSets...");
      startTime = System.nanoTime();
      loadSimpleMapRefSets();
      Logger.getLogger(this.getClass()).info(
          "    elapsed time = " + getElapsedTime(startTime) + "s"
              + " (Ended at " + ft.format(new Date()) + ")");

      //
      // Load ComplexMapRefSets
      //
      Logger.getLogger(this.getClass()).info("  Loading ComplexMap RefSets...");
      startTime = System.nanoTime();
      loadComplexMapRefSets();
      Logger.getLogger(this.getClass()).info(
          "    elapsed time = " + getElapsedTime(startTime) + "s"
              + " (Ended at " + ft.format(new Date()) + ")");

      //
      // Load ExtendedMapRefSets
      //
      Logger.getLogger(this.getClass())
          .info("  Loading ExtendedMap RefSets...");
      startTime = System.nanoTime();
      loadExtendedMapRefSets();
      Logger.getLogger(this.getClass()).info(
          "    elapsed time = " + getElapsedTime(startTime) + "s"
              + " (Ended at " + ft.format(new Date()) + ")");

      //
      // load AssocationReference RefSets (Content)
      //
      Logger.getLogger(this.getClass()).info(
          "  Loading AssociationReference RefSets...");
      startTime = System.nanoTime();
      loadAssociationReferenceRefSets();
      Logger.getLogger(this.getClass()).info(
          "    elaped time = " + getElapsedTime(startTime).toString() + "s"
              + " (Ended at " + ft.format(new Date()) + ")");

      //
      // Load AttributeValue RefSets (Content)
      //
      Logger.getLogger(this.getClass()).info(
          "  Loading AttributeValue RefSets...");
      startTime = System.nanoTime();
      loadAttributeValueRefSets();
      Logger.getLogger(this.getClass()).info(
          "    elaped time = " + getElapsedTime(startTime).toString() + "s"
              + " (Ended at " + ft.format(new Date()) + ")");

      //
      // load RefsetDescriptor RefSets (Content)
      //
      Logger.getLogger(this.getClass()).info(
          "  Loading RefsetDescriptor RefSets...");
      startTime = System.nanoTime();
      loadRefsetDescriptorRefSets();
      Logger.getLogger(this.getClass()).info(
          "    elaped time = " + getElapsedTime(startTime).toString() + "s"
              + " (Ended at " + ft.format(new Date()) + ")");

      //
      // load ModuleDependency RefSets (Content)
      //
      Logger.getLogger(this.getClass()).info(
          "  Loading ModuleDependency RefSets...");
      startTime = System.nanoTime();
      loadModuleDependencyRefSets();
      Logger.getLogger(this.getClass()).info(
          "    elaped time = " + getElapsedTime(startTime).toString() + "s"
              + " (Ended at " + ft.format(new Date()) + ")");

      //
      // load DescriptionType RefSets (Content)
      //
      Logger.getLogger(this.getClass()).info(
          "  Loading DescriptionType RefSets...");
      startTime = System.nanoTime();
      loadDescriptionTypeRefSets();
      Logger.getLogger(this.getClass()).info(
          "    elaped time = " + getElapsedTime(startTime).toString() + "s"
              + " (Ended at " + ft.format(new Date()) + ")");

      // Clear concept cache
      conceptCache.clear();

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
  @SuppressWarnings("resource")
  private void loadConcepts() throws Exception {

    String line = "";
    objectCt = 0;

    // begin transaction
    final ContentService contentService = new ContentServiceJpa();
    contentService.setLastModifiedFlag(false);
    contentService.setTransactionPerOperation(false);
    contentService.beginTransaction();

    PushBackReader reader = readers.getReader(Rf2Readers.Keys.CONCEPT);
    while ((line = reader.readLine()) != null) {

      final String fields[] = line.split("\t");
      final Concept concept = new ConceptJpa();

      if (!fields[0].equals("id")) { // header

        // Stop if the effective time is past the release version
        if (fields[1].compareTo(releaseVersion) > 0) {
          reader.push(line);
          break;
        }

        concept.setTerminologyId(fields[0]);
        concept.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        concept.setLastModified(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        concept.setActive(fields[2].equals("1") ? true : false);
        concept.setModuleId(fields[3]);
        concept.setDefinitionStatusId(fields[4]);
        concept.setTerminology(terminology);
        concept.setTerminologyVersion(terminologyVersion);
        concept.setDefaultPreferredName("null");
        concept.setLastModifiedBy("loader");
        concept.setWorkflowStatus("PUBLISHED");
        contentService.addConcept(concept);

        // copy concept to shed any hibernate stuff
        conceptCache.put(fields[0], concept);

        // regularly commit at intervals
        if (++objectCt % commitCt == 0) {
          Logger.getLogger(this.getClass()).info("    commit = " + objectCt);
          contentService.commit();
          contentService.beginTransaction();
        }
      }
    }

    // commit any remaining objects
    contentService.commit();
    contentService.close();

    defaultPreferredNames.clear();

    // print memory information
    Runtime runtime = Runtime.getRuntime();
    Logger.getLogger(this.getClass()).info("MEMORY USAGE:");
    Logger.getLogger(this.getClass()).info(" Total: " + runtime.totalMemory());
    Logger.getLogger(this.getClass()).info(" Free:  " + runtime.freeMemory());
    Logger.getLogger(this.getClass()).info(" Max:   " + runtime.maxMemory());

  }

  /**
   * Load relationships.
   * 
   * @throws Exception the exception
   */
  @SuppressWarnings("resource")
  private void loadRelationships() throws Exception {

    String line = "";
    objectCt = 0;

    // Begin transaction
    final ContentService contentService = new ContentServiceJpa();
    contentService.setLastModifiedFlag(false);
    contentService.setTransactionPerOperation(false);
    contentService.beginTransaction();

    PushBackReader reader = readers.getReader(Rf2Readers.Keys.RELATIONSHIP);
    // Iterate over relationships
    while ((line = reader.readLine()) != null) {

      // Split line
      final String fields[] = line.split("\t");
      // Skip header
      if (!fields[0].equals("id")) {

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
        relationship.setActive(fields[2].equals("1") ? true : false); // active
        relationship.setModuleId(fields[3]); // moduleId

        relationship.setRelationshipGroup(Integer.valueOf(fields[6])); // relationshipGroup
        relationship.setTypeId(fields[7]); // typeId
        relationship.setCharacteristicTypeId(fields[8]); // characteristicTypeId
        relationship.setTerminology(terminology);
        relationship.setTerminologyVersion(terminologyVersion);
        relationship.setModifierId(fields[9]);
        relationship.setLastModifiedBy("loader");

        final Concept sourceConcept = conceptCache.get(fields[4]);
        final Concept destinationConcept = conceptCache.get(fields[5]);
        if (sourceConcept != null && destinationConcept != null) {
          relationship.setSourceConcept(sourceConcept);
          relationship.setDestinationConcept(destinationConcept);

          contentService.addRelationship(relationship);

          // regularly commit at intervals
          if (++objectCt % commitCt == 0) {
            Logger.getLogger(this.getClass()).info("    commit = " + objectCt);
            contentService.commit();
            contentService.beginTransaction();
          }
        } else {
          if (sourceConcept == null) {
            Logger.getLogger(this.getClass()).info(
                "Relationship " + relationship.getTerminologyId()
                    + " references non-existent source concept " + fields[4]);
          }
          if (destinationConcept == null) {
            Logger.getLogger(this.getClass()).info(
                "Relationship " + relationship.getTerminologyId()
                    + " references non-existent destination concept "
                    + fields[5]);
          }

        }
      }
    }

    // commit any remaining objects
    contentService.commit();
    contentService.close();

    // print memory information
    Runtime runtime = Runtime.getRuntime();
    Logger.getLogger(this.getClass()).info("MEMORY USAGE:");
    Logger.getLogger(this.getClass()).info(" Total: " + runtime.totalMemory());
    Logger.getLogger(this.getClass()).info(" Free:  " + runtime.freeMemory());
    Logger.getLogger(this.getClass()).info(" Max:   " + runtime.maxMemory());
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
    int descCt = 0; // counter for descriptions
    int langCt = 0; // counter for language ref set members
    int skipCt = 0; // counter for number of language ref set members skipped

    // Begin transaction
    final ContentService contentService = new ContentServiceJpa();
    contentService.setLastModifiedFlag(false);
    contentService.setTransactionPerOperation(false);
    contentService.beginTransaction();

    ComputePreferredNameHandler pnHandler =
        contentService.getComputePreferredNameHandler(terminology);
    // Load and persist first description
    description = getNextDescription(contentService);

    // Load first language ref set member
    language = getNextLanguage();

    // Loop while there are descriptions
    while (description != null) {

      if (language == null) {
        throw new Exception("Description without langauge: "
            + description.getTerminologyId());
      }

      // if current language ref set references a lexicographically "lower"
      // String terminologyId, SKIP: description is not in data set
      while (language != null
          && language.getDescription().getTerminologyId()
              .compareTo(description.getTerminologyId()) < 0) {

        Logger.getLogger(this.getClass()).info(
            "     " + "Language Ref Set " + language.getTerminologyId()
                + " references non-existent description "
                + language.getDescription().getTerminologyId());
        language = getNextLanguage();
        skipCt++;
      }

      // Iterate over language ref sets until new description id found or end of
      // language ref sets found
      while (language != null && language.getDescription().getTerminologyId()
          .equals(description.getTerminologyId())) {

        // Set the description
        language.setDescription(description);
        description.addLanguageRefSetMember(language);
        langCt++;

        // Check if this language refset and description form the
        // defaultPreferredName
        if (pnHandler.isPreferredName(description, language)) {

          // retrieve the concept for this description
          concept = description.getConcept();
          if (defaultPreferredNames.get(concept.getId()) != null) {
            Logger.getLogger(this.getClass()).info(
                "Multiple default preferred names for concept "
                    + concept.getTerminologyId());
            Logger.getLogger(this.getClass()).info(
                "  " + "Existing: "
                    + defaultPreferredNames.get(concept.getId()));
            Logger.getLogger(this.getClass()).info(
                "  " + "Replaced: " + description.getTerm());
          }
          defaultPreferredNames.put(concept.getId(), description.getTerm());

        }

        // Get the next language ref set member
        language = getNextLanguage();
      }

      // Persist the description
      contentService.addDescription(description);

      // Pet the next description
      description = getNextDescription(contentService);

      // increment description count
      descCt++;

      // regularly commit at intervals
      if (descCt % commitCt == 0) {
        Logger.getLogger(this.getClass()).info("    commit = " + descCt);
        contentService.commit();
        contentService.beginTransaction();
      }

    }

    // commit any remaining objects
    contentService.commit();
    contentService.close();

    Logger.getLogger(this.getClass()).info(
        "      " + descCt + " descriptions loaded");
    Logger.getLogger(this.getClass()).info(
        "      " + langCt + " language ref sets loaded");
    Logger.getLogger(this.getClass()).info(
        "      " + skipCt + " language ref sets skipped (no description)");

    // print memory information
    Runtime runtime = Runtime.getRuntime();
    Logger.getLogger(this.getClass()).info("MEMORY USAGE:");
    Logger.getLogger(this.getClass()).info(" Total: " + runtime.totalMemory());
    Logger.getLogger(this.getClass()).info(" Free:  " + runtime.freeMemory());
    Logger.getLogger(this.getClass()).info(" Max:   " + runtime.maxMemory());
  }

  /**
   * Sets the default preferred names.
   * 
   * @throws Exception the exception
   */
  private void setDefaultPreferredNames() throws Exception {

    // Begin transaction
    ContentService contentService = new ContentServiceJpa();
    contentService.setLastModifiedFlag(false);
    contentService.setTransactionPerOperation(false);
    contentService.beginTransaction();

    Iterator<Concept> conceptIterator = conceptCache.values().iterator();
    objectCt = 0;
    while (conceptIterator.hasNext()) {
      final Concept cachedConcept = conceptIterator.next();
      final Concept dbConcept =
          contentService.getConcept(cachedConcept.getId());
      dbConcept.getDescriptions();
      dbConcept.getRelationships();
      if (defaultPreferredNames.get(dbConcept.getId()) != null) {
        dbConcept.setDefaultPreferredName(defaultPreferredNames.get(dbConcept
            .getId()));
      } else {
        dbConcept.setDefaultPreferredName("No default preferred name found");
      }
      // TODO: need to deal with this
      // contentService.updateConcept(dbConcept);
      if (++objectCt % commitCt == 0) {
        Logger.getLogger(this.getClass()).info("    commit = " + objectCt);
        contentService.commit();
        contentService.beginTransaction();
      }
    }
    contentService.commit();
    contentService.close();

    // Log memory information
    Runtime runtime = Runtime.getRuntime();
    Logger.getLogger(this.getClass()).info("MEMORY USAGE:");
    Logger.getLogger(this.getClass()).info(" Total: " + runtime.totalMemory());
    Logger.getLogger(this.getClass()).info(" Free:  " + runtime.freeMemory());
    Logger.getLogger(this.getClass()).info(" Max:   " + runtime.maxMemory());

  }

  /**
   * Returns the next description.
   *
   * @param contentService the content service
   * @return the next description
   * @throws Exception the exception
   */
  @SuppressWarnings("resource")
  private Description getNextDescription(ContentService contentService)
    throws Exception {

    String line, fields[];

    PushBackReader reader = readers.getReader(Rf2Readers.Keys.DESCRIPTION);
    if ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      fields = line.split("\t");

      if (!fields[0].equals("id")) { // header

        // Stop if the effective time is past the release version
        if (fields[1].compareTo(releaseVersion) > 0) {
          reader.push(line);
          return null;
        }
        final Description description = new DescriptionJpa();
        description.setTerminologyId(fields[0]);
        description
            .setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        description.setLastModified(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        description.setActive(fields[2].equals("1") ? true : false);
        description.setModuleId(fields[3]);

        description.setLanguageCode(fields[5]);
        description.setTypeId(fields[6]);
        description.setTerm(fields[7]);
        description.setCaseSignificanceId(fields[8]);
        description.setTerminology(terminology);
        description.setTerminologyVersion(terminologyVersion);
        description.setLastModifiedBy("loader");

        // set concept from cache
        Concept concept = conceptCache.get(fields[4]);

        if (concept != null) {
          description.setConcept(concept);
        } else {
          Logger.getLogger(this.getClass()).info(
              "Description " + description.getTerminologyId()
                  + " references non-existent concept " + fields[4]);
        }
        return description;
      }

      // otherwise get next line
      else {
        return getNextDescription(contentService);
      }
    }
    return null;
  }

  /**
   * Utility function to return the next line of language ref set files in
   * object form.
   * 
   * @return a partial language ref set member (lacks full description)
   * @throws Exception the exception
   */
  @SuppressWarnings("resource")
  private LanguageRefSetMember getNextLanguage() throws Exception {

    String line, fields[];
    // if non-null
    PushBackReader reader = readers.getReader(Rf2Readers.Keys.LANGUAGE);
    if ((line = reader.readLine()) != null) {
      line = line.replace("\r", "");
      fields = line.split("\t");

      if (!fields[0].equals("id")) { // header line

        // Stop if the effective time is past the release version
        if (fields[1].compareTo(releaseVersion) > 0) {
          reader.push(line);
          return null;
        }
        final LanguageRefSetMember member = new LanguageRefSetMemberJpa();

        // Universal RefSet attributes
        member.setTerminologyId(fields[0]);
        member.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setLastModified(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setActive(fields[2].equals("1") ? true : false);
        member.setModuleId(fields[3]);
        member.setRefSetId(fields[4]);

        // Language unique attributes
        member.setAcceptabilityId(fields[6]);

        // Terminology attributes
        member.setTerminology(terminology);
        member.setTerminologyVersion(terminologyVersion);
        member.setLastModifiedBy("loader");

        // Set a dummy description with terminology id only
        Description description = new DescriptionJpa();
        description.setTerminologyId(fields[5]);
        member.setDescription(description);
        return member;

      }
      // if header line, get next record
      else {
        return getNextLanguage();
      }

      // if null, set a dummy description value to avoid null-pointer exceptions
      // in main loop
    } else {
      return null;
    }

  }

  /**
   * Load AttributeRefSets (Content).
   * 
   * @throws Exception the exception
   */
  @SuppressWarnings({
      "boxing", "resource"
  })
  private void loadAttributeValueRefSets() throws Exception {

    String line = "";
    objectCt = 0;

    // Begin transaction
    ContentService contentService = new ContentServiceJpa();
    contentService.setLastModifiedFlag(false);
    contentService.setTransactionPerOperation(false);
    contentService.beginTransaction();

    // Iterate through attribute value entries
    PushBackReader reader = readers.getReader(Rf2Readers.Keys.ATTRIBUTE_VALUE);
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      String fields[] = line.split("\t");
      AttributeValueRefSetMember<Concept> member =
          new AttributeValueConceptRefSetMemberJpa();

      if (!fields[0].equals("id")) { // header

        // Stop if the effective time is past the release version
        if (fields[1].compareTo(releaseVersion) > 0) {
          reader.push(line);
          break;
        }

        // Universal RefSet attributes
        member.setTerminologyId(fields[0]);
        member.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setLastModified(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setActive(fields[2].equals("1") ? true : false);
        member.setLastModifiedBy("loader");
        member.setModuleId(fields[3]);
        member.setRefSetId(fields[4]);

        // AttributeValueRefSetMember unique attributes
        member.setValueId(fields[6]);

        // Terminology attributes
        member.setTerminology(terminology);
        member.setTerminologyVersion(terminologyVersion);
        member.setLastModifiedBy("loader");

        // Retrieve concept -- firstToken is referencedComponentId
        Concept concept = conceptCache.get(fields[5]);
        if (concept != null) {

          member.setComponent(concept);
          contentService.addAttributeValueRefSetMember(member);

          // regularly commit at intervals
          if (++objectCt % commitCt == 0) {
            Logger.getLogger(this.getClass()).info("  commit = " + objectCt);
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
    Logger.getLogger(this.getClass()).info("MEMORY USAGE:");
    Logger.getLogger(this.getClass()).info(" Total: " + runtime.totalMemory());
    Logger.getLogger(this.getClass()).info(" Free:  " + runtime.freeMemory());
    Logger.getLogger(this.getClass()).info(" Max:   " + runtime.maxMemory());
  }

  /**
   * Load Association Reference Refset (Content).
   * 
   * @throws Exception the exception
   */
  @SuppressWarnings({
      "boxing", "resource"
  })
  private void loadAssociationReferenceRefSets() throws Exception {

    String line = "";
    objectCt = 0;

    // begin transaction
    ContentService contentService = new ContentServiceJpa();
    contentService.setLastModifiedFlag(false);
    contentService.setTransactionPerOperation(false);
    contentService.beginTransaction();

    PushBackReader reader =
        readers.getReader(Rf2Readers.Keys.ASSOCIATION_REFERENCE);
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      String fields[] = line.split("\t");
      AssociationReferenceRefSetMember<Concept> member =
          new AssociationReferenceConceptRefSetMemberJpa();

      if (!fields[0].equals("id")) { // header

        // Universal RefSet attributes
        member.setTerminologyId(fields[0]);
        member.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setLastModified(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setActive(fields[2].equals("1") ? true : false);
        member.setModuleId(fields[3]);
        member.setRefSetId(fields[4]);

        // AttributeValueRefSetMember unique attributes
        member.setTargetComponentId(fields[6]);

        // Terminology attributes
        member.setTerminology(terminology);
        member.setTerminologyVersion(terminologyVersion);
        member.setLastModifiedBy("loader");

        // Retrieve concept -- firstToken is referencedComponentId
        Concept concept = conceptCache.get(fields[5]);

        if (concept != null) {

          member.setComponent(concept);
          contentService.addAssociationReferenceRefSetMember(member);

          // regularly commit at intervals
          if (++objectCt % commitCt == 0) {
            Logger.getLogger(this.getClass()).info("  commit = " + objectCt);
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
    Logger.getLogger(this.getClass()).info("MEMORY USAGE:");
    Logger.getLogger(this.getClass()).info(" Total: " + runtime.totalMemory());
    Logger.getLogger(this.getClass()).info(" Free:  " + runtime.freeMemory());
    Logger.getLogger(this.getClass()).info(" Max:   " + runtime.maxMemory());
  }

  /**
   * Load SimpleRefSets (Content).
   * 
   * @throws Exception the exception
   */

  @SuppressWarnings("resource")
  private void loadSimpleRefSets() throws Exception {

    String line = "";
    objectCt = 0;

    // begin transaction
    final ContentService contentService = new ContentServiceJpa();
    contentService.setLastModifiedFlag(false);
    contentService.setTransactionPerOperation(false);
    contentService.beginTransaction();

    PushBackReader reader = readers.getReader(Rf2Readers.Keys.SIMPLE);
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      final String fields[] = line.split("\t");

      if (!fields[0].equals("id")) { // header

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
        member.setActive(fields[2].equals("1") ? true : false);
        member.setModuleId(fields[3]);
        member.setRefSetId(fields[4]);

        // SimpleRefSetMember unique attributes
        // NONE

        // Terminology attributes
        member.setTerminology(terminology);
        member.setTerminologyVersion(terminologyVersion);
        member.setLastModifiedBy("loader");

        // Retrieve Concept -- firstToken is referencedComonentId
        final Concept concept = conceptCache.get(fields[5]);

        if (concept != null) {
          member.setConcept(concept);
          contentService.addSimpleRefSetMember(member);

          // regularly commit at intervals
          if (++objectCt % commitCt == 0) {
            Logger.getLogger(this.getClass()).info("    commit = " + objectCt);
            contentService.commit();
            contentService.beginTransaction();
          }
        } else {
          Logger.getLogger(this.getClass()).info(
              "simpleRefSetMember " + member.getTerminologyId()
                  + " references non-existent concept " + fields[5]);
        }
      }
    }

    // commit any remaining objects
    contentService.commit();
    contentService.close();

    // print memory information
    Runtime runtime = Runtime.getRuntime();
    Logger.getLogger(this.getClass()).info("MEMORY USAGE:");
    Logger.getLogger(this.getClass()).info(" Total: " + runtime.totalMemory());
    Logger.getLogger(this.getClass()).info(" Free:  " + runtime.freeMemory());
    Logger.getLogger(this.getClass()).info(" Max:   " + runtime.maxMemory());
  }

  /**
   * Load SimpleMapRefSets (Crossmap).
   * 
   * @throws Exception the exception
   */
  @SuppressWarnings("resource")
  private void loadSimpleMapRefSets() throws Exception {

    String line = "";
    objectCt = 0;

    // begin transaction
    final ContentService contentService = new ContentServiceJpa();
    contentService.setLastModifiedFlag(false);
    contentService.setTransactionPerOperation(false);
    contentService.beginTransaction();

    PushBackReader reader = readers.getReader(Rf2Readers.Keys.SIMPLE_MAP);
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      final String fields[] = line.split("\t");

      if (!fields[0].equals("id")) { // header
        final SimpleMapRefSetMember member = new SimpleMapRefSetMemberJpa();

        // Universal RefSet attributes
        member.setTerminologyId(fields[0]);
        member.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setLastModified(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setActive(fields[2].equals("1") ? true : false);
        member.setModuleId(fields[3]);
        member.setRefSetId(fields[4]);

        // SimpleMap unique attributes
        member.setMapTarget(fields[6]);

        // Terminology attributes
        member.setTerminology(terminology);
        member.setTerminologyVersion(terminologyVersion);
        member.setLastModifiedBy("loader");

        // Retrieve concept -- firstToken is referencedComponentId
        final Concept concept = conceptCache.get(fields[5]);

        if (concept != null) {
          member.setConcept(concept);
          contentService.addSimpleMapRefSetMember(member);

          // regularly commit at intervals
          if (++objectCt % commitCt == 0) {
            Logger.getLogger(this.getClass()).info("    commit = " + objectCt);
            contentService.commit();
            contentService.beginTransaction();
          }
        } else {
          Logger.getLogger(this.getClass()).info(
              "simpleMapRefSetMember " + member.getTerminologyId()
                  + " references non-existent concept " + fields[5]);
        }
      }
    }

    // commit any remaining objects
    contentService.commit();
    contentService.close();

    // print memory information
    Runtime runtime = Runtime.getRuntime();
    Logger.getLogger(this.getClass()).info("MEMORY USAGE:");
    Logger.getLogger(this.getClass()).info(" Total: " + runtime.totalMemory());
    Logger.getLogger(this.getClass()).info(" Free:  " + runtime.freeMemory());
    Logger.getLogger(this.getClass()).info(" Max:   " + runtime.maxMemory());
  }

  /**
   * Load ComplexMapRefSets (Crossmap).
   * 
   * @throws Exception the exception
   */
  @SuppressWarnings("resource")
  private void loadComplexMapRefSets() throws Exception {

    String line = "";
    objectCt = 0;

    // begin transaction
    final ContentService contentService = new ContentServiceJpa();
    contentService.setLastModifiedFlag(false);
    contentService.setTransactionPerOperation(false);
    contentService.beginTransaction();

    PushBackReader reader = readers.getReader(Rf2Readers.Keys.COMPLEX_MAP);
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      final String fields[] = line.split("\t");

      if (!fields[0].equals("id")) { // header
        // Stop if the effective time is past the release version
        if (fields[1].compareTo(releaseVersion) > 0) {
          reader.push(line);
          break;
        }
        final ComplexMapRefSetMember member = new ComplexMapRefSetMemberJpa();
        member.setTerminologyId(fields[0]);
        member.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setLastModified(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setActive(fields[2].equals("1") ? true : false);
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

        // Terminology attributes
        member.setTerminology(terminology);
        member.setTerminologyVersion(terminologyVersion);
        member.setLastModifiedBy("loader");

        // set Concept
        final Concept concept = conceptCache.get(fields[5]);

        if (concept != null) {
          member.setConcept(concept);
          contentService.addComplexMapRefSetMember(member);

          // regularly commit at intervals
          if (++objectCt % commitCt == 0) {
            Logger.getLogger(this.getClass()).info("    commit = " + objectCt);
            contentService.commit();
            contentService.beginTransaction();
          }
        } else {
          Logger.getLogger(this.getClass()).info(
              "complexMapRefSetMember " + member.getTerminologyId()
                  + " references non-existent concept " + fields[5]);
        }

      }
    }

    // commit any remaining objects
    contentService.commit();
    contentService.close();

    // print memory information
    Runtime runtime = Runtime.getRuntime();
    Logger.getLogger(this.getClass()).info("MEMORY USAGE:");
    Logger.getLogger(this.getClass()).info(" Total: " + runtime.totalMemory());
    Logger.getLogger(this.getClass()).info(" Free:  " + runtime.freeMemory());
    Logger.getLogger(this.getClass()).info(" Max:   " + runtime.maxMemory());
  }

  /**
   * Load extended map ref sets.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("resource")
  private void loadExtendedMapRefSets() throws Exception {

    String line = "";
    objectCt = 0;

    // begin transaction
    final ContentService contentService = new ContentServiceJpa();
    contentService.setLastModifiedFlag(false);
    contentService.setTransactionPerOperation(false);
    contentService.beginTransaction();

    PushBackReader reader = readers.getReader(Rf2Readers.Keys.EXTENDED_MAP);
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      final String fields[] = line.split("\t");

      if (!fields[0].equals("id")) {

        // Stop if the effective time is past the release version
        if (fields[1].compareTo(releaseVersion) > 0) {
          reader.push(line);
          break;
        }

        final ComplexMapRefSetMember member = new ComplexMapRefSetMemberJpa();
        member.setTerminologyId(fields[0]);
        member.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setLastModified(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setActive(fields[2].equals("1") ? true : false);
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

        // Terminology attributes
        member.setTerminology(terminology);
        member.setTerminologyVersion(terminologyVersion);
        member.setLastModifiedBy("loader");

        // set Concept
        final Concept concept = conceptCache.get(fields[5]);

        if (concept != null) {
          member.setConcept(concept);
          contentService.addComplexMapRefSetMember(member);

          // regularly commit at intervals
          if (++objectCt % commitCt == 0) {
            Logger.getLogger(this.getClass()).info("    commit = " + objectCt);
            contentService.commit();
            contentService.beginTransaction();
          }
        } else {
          Logger.getLogger(this.getClass()).info(
              "complexMapRefSetMember " + member.getTerminologyId()
                  + " references non-existent concept " + fields[5]);
        }

      }
    }

    // commit any remaining objects
    contentService.commit();
    contentService.close();

    // print memory information
    Runtime runtime = Runtime.getRuntime();
    Logger.getLogger(this.getClass()).info("MEMORY USAGE:");
    Logger.getLogger(this.getClass()).info(" Total: " + runtime.totalMemory());
    Logger.getLogger(this.getClass()).info(" Free:  " + runtime.freeMemory());
    Logger.getLogger(this.getClass()).info(" Max:   " + runtime.maxMemory());
  }

  /**
   * Load refset descriptor ref sets.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("resource")
  private void loadRefsetDescriptorRefSets() throws Exception {

    String line = "";
    objectCt = 0;

    // begin transaction
    final ContentService contentService = new ContentServiceJpa();
    contentService.setLastModifiedFlag(false);
    contentService.setTransactionPerOperation(false);
    contentService.beginTransaction();

    PushBackReader reader =
        readers.getReader(Rf2Readers.Keys.REFSET_DESCRIPTOR);
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      final String fields[] = line.split("\t");

      if (!fields[0].equals("id")) {

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
        member.setActive(fields[2].equals("1") ? true : false);
        member.setModuleId(fields[3]);
        member.setRefSetId(fields[4]);
        // conceptId

        // Refset descriptor unique attributes
        member.setAttributeDescription(fields[6]);
        member.setAttributeType(fields[7]);
        member.setAttributeOrder(Integer.valueOf(fields[8]));

        // Terminology attributes
        member.setTerminology(terminology);
        member.setTerminologyVersion(terminologyVersion);
        member.setLastModifiedBy("loader");

        // set Concept
        final Concept concept = conceptCache.get(fields[5]);

        if (concept != null) {
          member.setConcept(concept);
          contentService.addRefsetDescriptorRefSetMember(member);

          // regularly commit at intervals
          if (++objectCt % commitCt == 0) {
            Logger.getLogger(this.getClass()).info("    commit = " + objectCt);
            contentService.commit();
            contentService.beginTransaction();
          }
        } else {
          throw new Exception("RefsetDescriptorRefSetMember "
              + member.getTerminologyId() + " references non-existent concept "
              + fields[5]);
        }

      }
    }

    // commit any remaining objects
    contentService.commit();
    contentService.close();

    // print memory information
    Runtime runtime = Runtime.getRuntime();
    Logger.getLogger(this.getClass()).info("MEMORY USAGE:");
    Logger.getLogger(this.getClass()).info(" Total: " + runtime.totalMemory());
    Logger.getLogger(this.getClass()).info(" Free:  " + runtime.freeMemory());
    Logger.getLogger(this.getClass()).info(" Max:   " + runtime.maxMemory());
  }

  /**
   * Load module dependency refset members
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("resource")
  private void loadModuleDependencyRefSets() throws Exception {

    String line = "";
    objectCt = 0;

    // begin transaction
    final ContentService contentService = new ContentServiceJpa();
    contentService.setLastModifiedFlag(false);
    contentService.setTransactionPerOperation(false);
    contentService.beginTransaction();

    PushBackReader reader =
        readers.getReader(Rf2Readers.Keys.REFSET_DESCRIPTOR);
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      final String fields[] = line.split("\t");

      if (!fields[0].equals("id")) {

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
        member.setActive(fields[2].equals("1") ? true : false);
        member.setModuleId(fields[3]);
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
        member.setLastModifiedBy("loader");

        // set Concept
        final Concept concept = conceptCache.get(fields[5]);

        if (concept != null) {
          member.setConcept(concept);
          contentService.addModuleDependencyRefSetMember(member);

          // regularly commit at intervals
          if (++objectCt % commitCt == 0) {
            Logger.getLogger(this.getClass()).info("    commit = " + objectCt);
            contentService.commit();
            contentService.beginTransaction();
          }
        } else {
          throw new Exception("ModuleDependencyRefSetMember "
              + member.getTerminologyId() + " references non-existent concept "
              + fields[5]);
        }

      }
    }

    // commit any remaining objects
    contentService.commit();
    contentService.close();

    // print memory information
    Runtime runtime = Runtime.getRuntime();
    Logger.getLogger(this.getClass()).info("MEMORY USAGE:");
    Logger.getLogger(this.getClass()).info(" Total: " + runtime.totalMemory());
    Logger.getLogger(this.getClass()).info(" Free:  " + runtime.freeMemory());
    Logger.getLogger(this.getClass()).info(" Max:   " + runtime.maxMemory());
  }

  /**
   * Load description type refset members
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("resource")
  private void loadDescriptionTypeRefSets() throws Exception {

    String line = "";
    objectCt = 0;

    // begin transaction
    final ContentService contentService = new ContentServiceJpa();
    contentService.setLastModifiedFlag(false);
    contentService.setTransactionPerOperation(false);
    contentService.beginTransaction();

    PushBackReader reader =
        readers.getReader(Rf2Readers.Keys.REFSET_DESCRIPTOR);
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      final String fields[] = line.split("\t");

      if (!fields[0].equals("id")) {

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
        member.setActive(fields[2].equals("1") ? true : false);
        member.setModuleId(fields[3]);
        member.setRefSetId(fields[4]);
        // conceptId

        // Refset descriptor unique attributes
        member.setDescriptionFormat(fields[6]);
        member.setDescriptionLength(Integer.valueOf(fields[7]));

        // Terminology attributes
        member.setTerminology(terminology);
        member.setTerminologyVersion(terminologyVersion);
        member.setLastModifiedBy("loader");

        // set Concept
        final Concept concept = conceptCache.get(fields[5]);

        if (concept != null) {
          member.setConcept(concept);
          contentService.addDescriptionTypeRefSetMember(member);

          // regularly commit at intervals
          if (++objectCt % commitCt == 0) {
            Logger.getLogger(this.getClass()).info("    commit = " + objectCt);
            contentService.commit();
            contentService.beginTransaction();
          }
        } else {
          throw new Exception("DescriptionTypeRefSetMember "
              + member.getTerminologyId() + " references non-existent concept "
              + fields[5]);
        }

      }
    }

    // commit any remaining objects
    contentService.commit();
    contentService.close();

    // print memory information
    Runtime runtime = Runtime.getRuntime();
    Logger.getLogger(this.getClass()).info("MEMORY USAGE:");
    Logger.getLogger(this.getClass()).info(" Total: " + runtime.totalMemory());
    Logger.getLogger(this.getClass()).info(" Free:  " + runtime.freeMemory());
    Logger.getLogger(this.getClass()).info(" Max:   " + runtime.maxMemory());
  }
}
