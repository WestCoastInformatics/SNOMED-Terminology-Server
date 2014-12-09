package org.ihtsdo.otf.ts.jpa.services.handlers;

import java.util.Properties;

import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler;

/**
 * Snomed RF2 release implementation of {@link IdentifierAssignmentHandler}.
 * NOTE: if identifiers need to be stable across daily builds, you could simply
 * use this for normal editing and keep track of the sequences in the database
 * by, say, extending RootServiceJpa. (although some complexities arise with
 * dual independent review).
 */
public class SnomedReleaseIdentifierAssignmentHandler extends
    SnomedUuidHashIdentifierAssignmentHandler {

  /** The concept sequence. */
  private long conceptSequence;

  /** The description sequence. */
  private long descriptionSequence;

  /** The relationship sequence. */
  private long relationshipSequence;

  /** The is extension. */
  private boolean isExtension = false;

  /** The namespace id. */
  private String namespaceId = "";

  /**
   * Instantiates a {@link SnomedReleaseIdentifierAssignmentHandler} based on
   * counters. Subsequent assignments
   */
  public SnomedReleaseIdentifierAssignmentHandler() {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.helpers.Configurable#setProperties(java.util.Properties)
   */
  @Override
  public void setProperties(Properties p) {
    this.conceptSequence = Long.valueOf(p.getProperty("concept.max"));
    this.descriptionSequence = Long.valueOf(p.getProperty("description.max"));
    this.relationshipSequence =
        Long.valueOf(p.getProperty("relationships.max"));
    this.namespaceId = p.getProperty("namespace.id");
    this.isExtension = (namespaceId == null || namespaceId.isEmpty());
  }

  /**
   * Increment concept sequence.
   *
   * @return the long
   */
  private synchronized long incrementConceptSequence() {
    conceptSequence++;
    return conceptSequence;
  }

  /**
   * Increment description sequence.
   *
   * @return the long
   */
  private synchronized long incrementDescriptionSequence() {
    descriptionSequence++;
    return descriptionSequence;
  }

  /**
   * Increment relationship sequence.
   *
   * @return the long
   */
  private synchronized long incrementRelationshipSequence() {
    relationshipSequence++;
    return relationshipSequence;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler#assign(
   * org.ihtsdo.otf.ts.rf2.Concept)
   */
  @Override
  public String getTerminologyId(Concept concept) throws Exception {
    // If already assigned, leave alone
    if (!isSctid(concept.getTerminologyId())) {
      long ct = incrementConceptSequence();
      String num = ct + namespaceId + (isExtension ? "1" : "0") + "1";
      String verhoeff = Verhoeff.generateVerhoeff(num);
      return num + verhoeff;
    }
    return concept.getTerminologyId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler#assign(
   * org.ihtsdo.otf.ts.rf2.Description)
   */
  @Override
  public String getTerminologyId(Description description) throws Exception {
    // If already assigned, leave alone
    if (!isSctid(description.getTerminologyId())) {
      long ct = incrementDescriptionSequence();
      String num = ct + namespaceId + (isExtension ? "1" : "0") + "1";
      String verhoeff = Verhoeff.generateVerhoeff(num);
      return num + verhoeff;
    }
    return description.getTerminologyId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler#assign(
   * org.ihtsdo.otf.ts.rf2.Relationship)
   */
  @Override
  public String getTerminologyId(Relationship relationship) throws Exception {
    // If already assigned, leave alone
    if (!isSctid(relationship.getTerminologyId())) {
      long ct = incrementRelationshipSequence();
      String num = ct + namespaceId + (isExtension ? "1" : "0") + "1";
      String verhoeff = Verhoeff.generateVerhoeff(num);
      return num + verhoeff;
    }
    return relationship.getTerminologyId();
  }

}
