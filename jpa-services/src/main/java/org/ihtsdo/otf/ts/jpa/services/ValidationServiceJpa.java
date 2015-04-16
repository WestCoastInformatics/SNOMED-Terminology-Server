/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.jpa.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.ihtsdo.otf.ts.ValidationResult;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.jpa.ValidationResultJpa;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.services.ValidationService;
import org.ihtsdo.otf.ts.services.handlers.ValidationCheck;

/**
 * Validation services for the Jpa model.
 */
public class ValidationServiceJpa extends RootServiceJpa implements
    ValidationService {

  /** The checks. */
  public static List<ValidationCheck> checks = null;
  static {
    checks = new ArrayList<>();
    Properties config;
    try {
      config = ConfigUtility.getConfigProperties();
      String key = "validation.service.handler";
      for (String handlerName : config.getProperty(key).split(",")) {
        if (handlerName.isEmpty())
          continue;

        // Add handlers to map
        ValidationCheck handlerService =
            ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
                handlerName, ValidationCheck.class);
        checks.add(handlerService);
      }
    } catch (Exception e) {
      e.printStackTrace();
      checks = null;
    }
  }

  /**
   * Instantiates an empty {@link ValidationServiceJpa}.
   *
   * @throws Exception the exception
   */
  public ValidationServiceJpa() throws Exception {
    super();
    if (checks == null) {
      throw new Exception("Check list is null, serious problem.");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ValidationService#validateConcept(org.ihtsdo
   * .otf.ts.rf2.Concept)
   */
  @Override
  public ValidationResult validateConcept(Concept concept) {
    ValidationResult result = new ValidationResultJpa();
    for (ValidationCheck check : checks) {
      final ValidationResult individualResult = check.validate(concept);
      result.addErrors(individualResult.getErrors());
      result.addWarnings(individualResult.getWarnings());
    }
    return result;
  }

}
