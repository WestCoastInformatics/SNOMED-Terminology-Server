package org.ihtsdo.otf.ts.jpa.services.handlers;

import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler;

/**
 * Default implementation of {@link IdentifierAssignmentHandler}. This supports
 * "application-managed" identifier assignment.
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

  /**
   * Instantiates a {@link SnomedReleaseIdentifierAssignmentHandler} based on
   * counters. Subsequent assignments
   *
   * @param conceptSequence the concept sequence
   * @param descriptionSequence the description sequence
   * @param relationshipSequence the relationship sequence
   * @param isExtension indicator of extension - for partition id
   */
  public SnomedReleaseIdentifierAssignmentHandler(long conceptSequence,
      long descriptionSequence, long relationshipSequence, boolean isExtension) {
    this.conceptSequence = conceptSequence;
    this.descriptionSequence = descriptionSequence;
    this.relationshipSequence = relationshipSequence;
    this.isExtension = isExtension;
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
      String num = ct + (isExtension ? "1" : "0") + "1";
      String verhoeff = Verhoeff.generateVerhoeff(num);
      concept.setTerminology(num + verhoeff);
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
      String num = ct + (isExtension ? "1" : "0") + "1";
      String verhoeff = Verhoeff.generateVerhoeff(num);
      description.setTerminology(num + verhoeff);
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
      String num = ct + (isExtension ? "1" : "0") + "1";
      String verhoeff = Verhoeff.generateVerhoeff(num);
      relationship.setTerminology(num + verhoeff);
    }
    return relationship.getTerminologyId();
  }
  
}
