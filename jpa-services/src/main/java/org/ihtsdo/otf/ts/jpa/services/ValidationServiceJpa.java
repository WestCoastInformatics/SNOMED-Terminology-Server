package org.ihtsdo.otf.ts.jpa.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.ValidationResult;
import org.ihtsdo.otf.ts.helpers.ValidationResultJpa;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.services.ValidationService;
import org.ihtsdo.otf.ts.services.handlers.ValidationCheck;

/**
 * Validation services for the Jpa model.
 */
public class ValidationServiceJpa extends RootServiceJpa implements ValidationService {

  /**  The checks. */
  public List<ValidationCheck> checks = null;

  /**
   * Instantiates an empty {@link ValidationServiceJpa}.
   *
   * @throws Exception the exception
   */
  public ValidationServiceJpa() throws Exception {
    super();
    if (checks == null) {
      checks = new ArrayList<>();
      Properties config = ConfigUtility.getConfigProperties();
      String key = "validation.service.handler";
      for (String handlerName : config.getProperty(key).split(",")) {

        // Add handlers to map
        ValidationCheck handlerService =
            ConfigUtility.newStandardHandlerInstanceWithConfiguration(
                key, handlerName, ValidationCheck.class);
        checks.add(handlerService);
      }
    }
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.services.ValidationService#validateConcept(org.ihtsdo.otf.ts.rf2.Concept)
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
