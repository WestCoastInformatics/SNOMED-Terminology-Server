package org.ihtsdo.otf.ts.rest;

import org.ihtsdo.otf.ts.helpers.ValidationResult;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;

/**
 * Represents a service for validating content.
 */
public interface ValidationServiceRest {

  /**
   * Validates the specified concept. Checks are defined the "run.config"
   * setting for the deployed server.
   *
   * @param concept the concept
   * @param authToken the auth token
   * @return the validation result
   * @throws Exception the exception
   */
  public ValidationResult validateConcept(ConceptJpa concept, String authToken)
    throws Exception;

}
