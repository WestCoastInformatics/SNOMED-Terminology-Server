/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.jpa.algo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
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

  /** The output dir. */
  private File outputDir;

  /** The output dir. */
  private File inputConceptsFile;

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

      Logger.getLogger(getClass()).info("Done ...");

      // load relationships
      List<Relationship> list = loadRelationships();
      Map<String, List<String>> parChdMap = new HashMap<>();
      Map<String, List<String>> otherMap = new HashMap<>();
      for (Relationship rel : list) {
        if (rel.getTypeId().equals("116680003")
            && rel.getCharacteristicTypeId().equals("900000000000011006")
            && rel.isActive()) {
          if (!parChdMap.containsKey(rel.getDestinationConcept()
              .getTerminologyId())) {
            parChdMap.put(rel.getDestinationConcept().getTerminologyId(),
                new ArrayList<String>());
          }
          parChdMap.get(rel.getDestinationConcept().getTerminologyId()).add(
              rel.getSourceConcept().getTerminologyId());
        } else if (rel.isActive()) {
          if (!otherMap.containsKey(rel.getDestinationConcept()
              .getTerminologyId())) {
            otherMap.put(rel.getDestinationConcept().getTerminologyId(),
                new ArrayList<String>());
          }
          otherMap.get(rel.getDestinationConcept().getTerminologyId()).add(
              rel.getSourceConcept().getTerminologyId());

        }
      }

      // 1. Find initial concepts
      Set<String> initialConcepts = new HashSet<>();
      BufferedReader in = new BufferedReader(new FileReader(inputConceptsFile));
      String line;
      ;
      while ((line = in.readLine()) != null) {
        initialConcepts.add(line);
      }
      Set<String> concepts = new HashSet<>();
      concepts.addAll(initialConcepts);
      
      // 2. Find parent related concepts      
      for (String concept : concepts) {
        concepts.addAll(otherMap.get(concept));
      }
      
      // 3. Find metadata concepts (definitionStatusId, typeId,
      // characteristicTypeId, modifierId, valueId, targetComponentId,
      // acceptabilityId)
      //   . concepts
      //   . descriptions
      //   . relationships
      //   . content refsets
      //   . map refsets
      //   . metadata refsets
      //   . language refsets
      
      // 4. Find all concepts on path to root
      // 5. repeat 3,4

    } catch (Exception e) {
      throw e;
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
      // Skip header
      if (!fields[0].equals("id")) {

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

        // relationship.setTerminology(terminology);
        // relationship.setTerminologyVersion(terminologyVersion);
        relationship.setModifierId(fields[9].intern());
        relationship.setPublished(true);
        // get concepts from cache, they just need to have ids
        final Concept sourceConcept = new ConceptJpa();
        sourceConcept.setTerminologyId(fields[4]);
        final Concept destinationConcept = new ConceptJpa();
        destinationConcept.setTerminologyId(fields[5]);

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

  public File getOutputDir() {
    return outputDir;
  }

  public void setOutputDir(File outputDir) {
    this.outputDir = outputDir;
  }

  public File getInputConceptsFile() {
    return inputConceptsFile;
  }

  public void setInputConceptsFile(File inputConceptsFile) {
    this.inputConceptsFile = inputConceptsFile;
  }
}
