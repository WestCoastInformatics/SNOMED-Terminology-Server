/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.jpa.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.algo.Algorithm;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.jpa.services.HistoryServiceJpa;
import org.ihtsdo.otf.ts.rf2.ComplexMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.DescriptionTypeRefSetMember;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.ModuleDependencyRefSetMember;
import org.ihtsdo.otf.ts.rf2.RefsetDescriptorRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.jpa.ComplexMapRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionTypeRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.LanguageRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ModuleDependencyRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RefsetDescriptorRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RelationshipJpa;
import org.ihtsdo.otf.ts.services.helpers.ProgressEvent;
import org.ihtsdo.otf.ts.services.helpers.ProgressListener;
import org.ihtsdo.otf.ts.services.helpers.PushBackReader;

/**
 * Implementation of an algorithm to import RF2 snapshot data.
 */
public class Rf2SnapshotSamplerAlgorithm extends HistoryServiceJpa implements
    Algorithm {

  /** Listeners. */
  private List<ProgressListener> listeners = new ArrayList<>();

  /** The output concepts. */
  private Set<String> outputConcepts;

  /** The output descriptions. */
  private Set<String> outputDescriptions;

  /** The input concepts. */
  private Set<String> inputConcepts;

  /** The readers. */
  private Rf2Readers readers;

  /**
   * Instantiates an empty {@link Rf2SnapshotSamplerAlgorithm}.
   * @throws Exception if anything goes wrong
   */
  public Rf2SnapshotSamplerAlgorithm() throws Exception {
    super();
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
      Logger.getLogger(getClass()).info("Start sampling snapshot");

      // load relationships
      Logger.getLogger(getClass()).info("  Load relationships");
      List<Relationship> list = loadRelationships();
      Map<String, Set<String>> chdParMap = new HashMap<>();
      Map<String, Set<String>> otherMap = new HashMap<>();
      for (Relationship rel : list) {
        // active inferred isa
        if (rel.getTypeId().equals("116680003")
            && rel.getCharacteristicTypeId().equals("900000000000011006")
            && rel.isActive()) {
          if (!chdParMap.containsKey(rel.getSourceConcept().getTerminologyId())) {
            chdParMap.put(rel.getSourceConcept().getTerminologyId(),
                new HashSet<String>());
          }
          chdParMap.get(rel.getSourceConcept().getTerminologyId()).add(
              rel.getDestinationConcept().getTerminologyId());
        }
        // active, not isa
        else if (rel.isActive() && !rel.getTypeId().equals("116680003")) {
          if (!otherMap.containsKey(rel.getSourceConcept().getTerminologyId())) {
            otherMap.put(rel.getSourceConcept().getTerminologyId(),
                new HashSet<String>());
          }
          otherMap.get(rel.getSourceConcept().getTerminologyId()).add(
              rel.getDestinationConcept().getTerminologyId());
        }
      }

      Logger.getLogger(getClass()).info(
          "    chdPar count = " + chdParMap.size());
      Logger.getLogger(getClass()).info("    chdPar = " + chdParMap);
      Logger.getLogger(getClass()).info("    other count = " + otherMap.size());
      Logger.getLogger(getClass()).info("    other = " + otherMap);

      Logger.getLogger(getClass()).info("  Find initial concepts");
      // 1. Find initial concepts
      Set<String> concepts = new HashSet<>();
      Set<String> descriptions = new HashSet<>();
      concepts.addAll(inputConcepts);
      Logger.getLogger(getClass()).info("    count = " + concepts.size());

      // 2. Find other related concepts
      Logger.getLogger(getClass()).info("  Add distance 1 related concepts");
      for (String concept : new HashSet<>(concepts)) {
        if (otherMap.get(concept) != null) {
          Logger.getLogger(getClass()).info(
              "    add concepts = " + otherMap.get(concept));
          concepts.addAll(otherMap.get(concept));
        }
      }
      Logger.getLogger(getClass()).info("    count = " + concepts.size());

      int prevCt = -1;
      do {
        prevCt = concepts.size();
        // 3. Find metadata concepts (definitionStatusId, typeId,
        Logger.getLogger(getClass()).info("  Get metadata concepts");
        addConceptMetadata(concepts);
        Logger.getLogger(getClass()).info(
            "    count (after concepts) = " + concepts.size());

        addDescriptionMetadata(concepts, descriptions);
        Logger.getLogger(getClass()).info(
            "    count (after descriptions) = " + concepts.size());

        addRelationshipMetadata(concepts);
        Logger.getLogger(getClass()).info(
            "    count (after relationships) = " + concepts.size());

        addAttributeValueMetadata(concepts, descriptions);
        Logger.getLogger(getClass()).info(
            "    count (after attribute value) = " + concepts.size());

        addAssociationReferenceMetadata(concepts, descriptions);
        Logger.getLogger(getClass()).info(
            "    count (after association reference) = " + concepts.size());

        addSimpleMetadata(concepts);
        Logger.getLogger(getClass()).info(
            "    count (after simple) = " + concepts.size());

        addSimpleMapMetadata(concepts);
        Logger.getLogger(getClass()).info(
            "    count (after simple map) = " + concepts.size());

        addComplexMapMetadata(concepts);
        Logger.getLogger(getClass()).info(
            "    count (after complex map) = " + concepts.size());

        addLanguageMetadata(concepts, descriptions);
        Logger.getLogger(getClass()).info(
            "    count (after language) = " + concepts.size());

        addMetadataMetadata(concepts);
        Logger.getLogger(getClass()).info(
            "    count (after metadata) = " + concepts.size());

        // 4. Find all concepts on path to root (e.g. walk up ancestors)
        Logger.getLogger(getClass()).info("  Find paths to root");
        for (String chd : chdParMap.keySet()) {
          if (concepts.contains(chd)) {
            concepts.addAll(chdParMap.get(chd));
          }
        }
        Logger.getLogger(getClass()).info(
            "    count (after ancestors) = " + concepts.size());
        Logger.getLogger(getClass()).info("    prev count = " + prevCt);

      } while (concepts.size() != prevCt);

      // Set output concepts
      outputConcepts = concepts;
      outputDescriptions = descriptions;

      Logger.getLogger(getClass()).info("Done ...");

    } catch (Exception e) {
      throw e;
    }
  }

  /**
   * Adds the concept metadata.
   *
   * @param concepts the concepts
   * @throws Exception the exception
   */
  private void addConceptMetadata(Set<String> concepts) throws Exception {

    String line = "";
    PushBackReader reader = readers.getReader(Rf2Readers.Keys.CONCEPT);
    while ((line = reader.readLine()) != null) {

      final String fields[] = line.split("\t");
      final Concept concept = new ConceptJpa();

      if (!fields[0].equals("id")) { // header

        concept.setTerminologyId(fields[0]);
        concept.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        concept.setLastModified(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        concept.setActive(fields[2].equals("1"));
        concept.setModuleId(fields[3]);
        concept.setDefinitionStatusId(fields[4]);

        // Add definition status id
        if (concepts.contains(concept.getTerminologyId())) {
          concepts.add(concept.getModuleId());
          concepts.add(concept.getDefinitionStatusId());
        }
      }
    }

  }

  /**
   * Adds the description metadata.
   *
   * @param concepts the concepts
   * @param descriptions the descriptions
   * @throws Exception the exception
   */
  private void addDescriptionMetadata(Set<String> concepts,
    Set<String> descriptions) throws Exception {

    String line = "";
    PushBackReader reader = readers.getReader(Rf2Readers.Keys.DESCRIPTION);
    while ((line = reader.readLine()) != null) {

      final String fields[] = line.split("\t");
      final Description description = new DescriptionJpa();

      if (!fields[0].equals("id")) {

        description.setTerminologyId(fields[0]);
        description
            .setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        description.setLastModified(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        description.setActive(fields[2].equals("1"));
        description.setModuleId(fields[3]);

        description.setLanguageCode(fields[5]);
        description.setTypeId(fields[6]);
        description.setTerm(fields[7]);
        description.setCaseSignificanceId(fields[8]);

        // If concept id matches, add description metadata
        if (concepts.contains(fields[4])) {
          descriptions.add(description.getTerminologyId());
          concepts.add(description.getModuleId());
          concepts.add(description.getTypeId());
          concepts.add(description.getCaseSignificanceId());
        }
      }
    }
  }

  /**
   * Adds the relationship metadata.
   *
   * @param concepts the concepts
   * @throws Exception the exception
   */
  private void addRelationshipMetadata(Set<String> concepts) throws Exception {
    String line = "";
    PushBackReader reader = readers.getReader(Rf2Readers.Keys.RELATIONSHIP);
    // Iterate over relationships
    while ((line = reader.readLine()) != null) {

      // Split line
      final String fields[] = line.split("\t");
      final Relationship relationship = new RelationshipJpa();

      // Skip header
      if (!fields[0].equals("id")) {

        // Configure relationship
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
        relationship.setModifierId(fields[9].intern());
        relationship.setPublished(true);

        // Add metadata for matching entries
        if (concepts.contains(fields[4]) || concepts.contains(fields[5])) {
          concepts.add(relationship.getModuleId());
          concepts.add(relationship.getTypeId());
          concepts.add(relationship.getCharacteristicTypeId());
          concepts.add(relationship.getModifierId());
        }
      }
    }
  }

  /**
   * Adds the attribute value metadata.
   *
   * @param concepts the concepts
   * @param descriptions the descriptions
   * @throws Exception the exception
   */
  private void addAttributeValueMetadata(Set<String> concepts,
    Set<String> descriptions) throws Exception {

    String line = "";

    // Iterate through attribute value entries
    PushBackReader reader = readers.getReader(Rf2Readers.Keys.ATTRIBUTE_VALUE);
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      final String fields[] = line.split("\t");

      if (!fields[0].equals("id")) { // header

        // Add metadata if attached to description or concept
        if (concepts.contains(fields[5])) {
          // module id
          concepts.add(fields[3]);
          // refset id
          concepts.add(fields[4]);
          // value id
          concepts.add(fields[6]);
        }
        if (descriptions.contains(fields[5])) {
          // module id
          concepts.add(fields[3]);
          // refset id
          concepts.add(fields[4]);
          // value id
          descriptions.add(fields[6]);
        }
      }
    }
  }

  /**
   * Adds the association reference metadata.
   *
   * @param concepts the concepts
   * @param descriptions the descriptions
   * @throws Exception the exception
   */
  private void addAssociationReferenceMetadata(Set<String> concepts,
    Set<String> descriptions) throws Exception {

    String line = "";

    // Iterate through attribute value entries
    PushBackReader reader =
        readers.getReader(Rf2Readers.Keys.ASSOCIATION_REFERENCE);
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      final String fields[] = line.split("\t");

      if (!fields[0].equals("id")) { // header

        // Add metadata if attached to description or concept
        if (concepts.contains(fields[5])) {
          // module id
          concepts.add(fields[3]);
          // refset id
          concepts.add(fields[4]);
          // targetComponent id
          concepts.add(fields[6]);
        }
        if (descriptions.contains(fields[5])) {
          // module id
          concepts.add(fields[3]);
          // refset id
          concepts.add(fields[4]);
          // target component id
          descriptions.add(fields[6]);
        }
      }
    }
  }

  /**
   * Adds the simple metadata.
   *
   * @param concepts the concepts
   * @throws Exception the exception
   */
  private void addSimpleMetadata(Set<String> concepts) throws Exception {

    String line = "";

    // Iterate through attribute value entries
    PushBackReader reader = readers.getReader(Rf2Readers.Keys.SIMPLE);
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      final String fields[] = line.split("\t");

      if (!fields[0].equals("id")) { // header

        // Add metadata if attached to description or concept
        if (concepts.contains(fields[5])) {
          // module id
          concepts.add(fields[3]);
          // refset id
          concepts.add(fields[4]);
        }
      }
    }
  }

  /**
   * Adds the simple map metadata.
   *
   * @param concepts the concepts
   * @throws Exception the exception
   */
  private void addSimpleMapMetadata(Set<String> concepts) throws Exception {

    String line = "";

    // Iterate through attribute value entries
    PushBackReader reader = readers.getReader(Rf2Readers.Keys.SIMPLE_MAP);
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      final String fields[] = line.split("\t");

      if (!fields[0].equals("id")) { // header

        // Add metadata if attached to description or concept
        if (concepts.contains(fields[5])) {
          // module id
          concepts.add(fields[3]);
          // refset id
          concepts.add(fields[4]);
        }
      }
    }
  }

  /**
   * Adds the complex map metadata.
   *
   * @param concepts the concepts
   * @throws Exception the exception
   */
  private void addComplexMapMetadata(Set<String> concepts) throws Exception {

    String line = "";

    PushBackReader reader = readers.getReader(Rf2Readers.Keys.COMPLEX_MAP);
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      final String fields[] = line.split("\t");

      if (!fields[0].equals("id")) {

        final ComplexMapRefSetMember member = new ComplexMapRefSetMemberJpa();
        member.setTerminologyId(fields[0]);
        member.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setLastModified(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setActive(fields[2].equals("1"));
        member.setModuleId(fields[3].intern());
        member.setRefSetId(fields[4]);

        // ComplexMap unique attributes
        member.setMapGroup(Integer.parseInt(fields[6]));
        member.setMapPriority(Integer.parseInt(fields[7]));
        member.setMapRule(fields[8]);
        member.setMapAdvice(fields[9]);
        member.setMapTarget(fields[10]);
        member.setMapRelationId(fields[11]);

        if (concepts.contains(fields[5])) {
          concepts.add(member.getModuleId());
          concepts.add(member.getRefSetId());
          concepts.add(member.getMapRelationId());
          concepts.add(member.getModuleId());
        }
      }
    }

    // handle extended too

    reader = readers.getReader(Rf2Readers.Keys.EXTENDED_MAP);
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      final String fields[] = line.split("\t");

      if (!fields[0].equals("id")) {

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

        if (concepts.contains(fields[5])) {
          concepts.add(member.getModuleId());
          concepts.add(member.getRefSetId());
          // correlation id
          concepts.add(fields[11]);
          concepts.add(member.getMapRelationId());
          concepts.add(member.getModuleId());
        }
      }
    }
  }

  /**
   * Adds the language metadata.
   *
   * @param concepts the concepts
   * @param descriptions the descriptions
   * @throws Exception the exception
   */
  private void addLanguageMetadata(Set<String> concepts,
    Set<String> descriptions) throws Exception {

    PushBackReader reader = readers.getReader(Rf2Readers.Keys.LANGUAGE);
    String line;
    while ((line = reader.readLine()) != null) {

      final String fields[] = line.split("\t");

      if (!fields[0].equals("id")) { // header

        final LanguageRefSetMember member = new LanguageRefSetMemberJpa();
        member.setTerminologyId(fields[0]);
        member.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setLastModified(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setActive(fields[2].equals("1"));
        member.setModuleId(fields[3].intern());
        member.setRefSetId(fields[4].intern());
        // Language unique attributes
        member.setAcceptabilityId(fields[6].intern());

        if (descriptions.contains(fields[5])) {
          concepts.add(member.getModuleId());
          concepts.add(member.getRefSetId());
          concepts.add(member.getAcceptabilityId());
        }
      }
    }
  }

  /**
   * Adds the language metadata.
   *
   * @param concepts the concepts
   * @throws Exception the exception
   */
  private void addMetadataMetadata(Set<String> concepts) throws Exception {

    String line = "";

    PushBackReader reader =
        readers.getReader(Rf2Readers.Keys.MODULE_DEPENDENCY);
    while ((line = reader.readLine()) != null) {
      line = line.replace("\r", "");
      final String fields[] = line.split("\t");
      if (!fields[0].equals("id")) {
        final ModuleDependencyRefSetMember member =
            new ModuleDependencyRefSetMemberJpa();
        member.setTerminologyId(fields[0]);
        member.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setLastModified(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setActive(fields[2].equals("1"));
        member.setModuleId(fields[3].intern());
        member.setRefSetId(fields[4]);

        // Refset descriptor unique attributes
        member.setSourceEffectiveTime(ConfigUtility.DATE_FORMAT
            .parse(fields[6]));
        member.setTargetEffectiveTime(ConfigUtility.DATE_FORMAT
            .parse(fields[7]));

        if (concepts.contains(member.getModuleId())
            || concepts.contains(fields[5])) {
          concepts.add(member.getModuleId());
          concepts.add(member.getRefSetId());
        }
      }
    }

    reader = readers.getReader(Rf2Readers.Keys.DESCRIPTION_TYPE);
    while ((line = reader.readLine()) != null) {
      line = line.replace("\r", "");
      final String fields[] = line.split("\t");
      if (!fields[0].equals("id")) {
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

        if (concepts.contains(fields[5])) {
          concepts.add(member.getModuleId());
          concepts.add(member.getRefSetId());
          concepts.add(member.getDescriptionFormat());
        }
      }
    }

    reader = readers.getReader(Rf2Readers.Keys.REFSET_DESCRIPTOR);
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      final String fields[] = line.split("\t");
      if (!fields[0].equals("id")) {
        final RefsetDescriptorRefSetMember member =
            new RefsetDescriptorRefSetMemberJpa();
        member.setTerminologyId(fields[0]);
        member.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setLastModified(ConfigUtility.DATE_FORMAT.parse(fields[1]));
        member.setActive(fields[2].equals("1"));
        member.setModuleId(fields[3].intern());
        member.setRefSetId(fields[4]);

        // Refset descriptor unique attributes
        member.setAttributeDescription(fields[6]);
        member.setAttributeType(fields[7]);
        member.setAttributeOrder(Integer.valueOf(fields[8]));

        if (concepts.contains(fields[5])) {
          concepts.add(member.getModuleId());
          concepts.add(member.getRefSetId());
          concepts.add(member.getAttributeDescription());
          concepts.add(member.getAttributeType());
        }
      }
    }

  }

  /**
   * Load relationships.
   *
   * @return the list
   * @throws Exception the exception
   */
  private List<Relationship> loadRelationships() throws Exception {
    String line = "";
    List<Relationship> results = new ArrayList<>();
    PushBackReader reader = readers.getReader(Rf2Readers.Keys.RELATIONSHIP);
    // Iterate over relationships
    while ((line = reader.readLine()) != null) {

      // Split line
      final String fields[] = line.split("\t");
      // Skip header and keep only active entries
      if (!fields[0].equals("id") && fields[2].equals("1")) {

        // Configure relationship
        final Relationship relationship = new RelationshipJpa();
        relationship.setTerminologyId(fields[0]);
        relationship.setActive(fields[2].equals("1"));
        relationship.setModuleId(fields[3].intern()); // moduleId
        relationship.setTypeId(fields[7]); // typeId
        relationship.setCharacteristicTypeId(fields[8].intern()); // characteristicTypeId
        relationship.setModifierId(fields[9].intern());
        // get concepts from cache, they just need to have ids
        final Concept sourceConcept = new ConceptJpa();
        sourceConcept.setTerminologyId(fields[4]);
        relationship.setSourceConcept(sourceConcept);
        final Concept destinationConcept = new ConceptJpa();
        destinationConcept.setTerminologyId(fields[5]);
        relationship.setDestinationConcept(destinationConcept);

        results.add(relationship);
      }
    }
    return results;
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

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.jpa.services.RootServiceJpa#close()
   */
  @Override
  public void close() throws Exception {
    super.close();
    readers = null;
  }

  /**
   * Returns the output concepts
   *
   * @return the output concepts
   */
  public Set<String> getOutputConcepts() {
    return outputConcepts;
  }

  /**
   * Returns the output descriptions.
   *
   * @return the output descriptions
   */
  public Set<String> getOutputDescriptions() {
    return outputDescriptions;
  }

  /**
   * Returns the input concepts.
   *
   * @return the input concepts
   */
  public Set<String> getInputConcepts() {
    return inputConcepts;
  }

  /**
   * Sets the input concepts.
   *
   * @param inputConcepts the input concepts
   */
  public void setInputConcepts(Set<String> inputConcepts) {
    this.inputConcepts = inputConcepts;
  }
}
