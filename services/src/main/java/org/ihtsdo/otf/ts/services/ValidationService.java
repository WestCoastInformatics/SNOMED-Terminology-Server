package org.ihtsdo.otf.ts.services;

import org.ihtsdo.otf.ts.helpers.ValidationResult;
import org.ihtsdo.otf.ts.rf2.Concept;

/**
 * Generically represents a service for validating content.
 */
public interface ValidationService extends RootService {

  /**
   * Validate concept.
   *
   * @param concept the concept
   * @return the validation result
   */
  public ValidationResult validateConcept(Concept concept);

}