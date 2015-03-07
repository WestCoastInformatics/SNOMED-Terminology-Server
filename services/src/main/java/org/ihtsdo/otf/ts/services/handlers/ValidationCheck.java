package org.ihtsdo.otf.ts.services.handlers;

import org.ihtsdo.otf.ts.ValidationResult;
import org.ihtsdo.otf.ts.helpers.Configurable;
import org.ihtsdo.otf.ts.rf2.Concept;

/**
 * Generically represents a validation check on a concept
 */
public interface ValidationCheck extends Configurable {

  /**
   * Validates the concept.
   *
   * @param c the c
   * @return the validation result
   */
  public ValidationResult validate(Concept c);
  
}
