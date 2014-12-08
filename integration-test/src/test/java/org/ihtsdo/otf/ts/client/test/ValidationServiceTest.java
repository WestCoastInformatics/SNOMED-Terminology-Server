package org.ihtsdo.otf.ts.client.test;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.ValidationResult;
import org.ihtsdo.otf.ts.jpa.client.ContentClientRest;
import org.ihtsdo.otf.ts.jpa.client.SecurityClientRest;
import org.ihtsdo.otf.ts.jpa.client.ValidationClientRest;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.services.helpers.ConceptReportHelper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for REST content service.
 */
public class ValidationServiceTest {

  /** The client. */
  private static ValidationClientRest client;

  /** The client. */
  private static ContentClientRest contentClient;

  /** The auth token. */
  private static String authToken;

  /**
   * Setup.
   * @throws Exception
   */
  @SuppressWarnings("static-method")
  @Before
  public void setup() throws Exception {
    if (client == null) {
      client =
          new ValidationClientRest(ConfigUtility.getTestConfigProperties());
      contentClient =
          new ContentClientRest(ConfigUtility.getTestConfigProperties());
      SecurityClientRest securityClient =
          new SecurityClientRest(ConfigUtility.getTestConfigProperties());
      authToken = securityClient.authenticate("guest", "guest");
    }
  }

  /**
   * Test validating a concept.
   * @throws Exception
   */
  @Test
  public void testValidate() throws Exception {
    Logger.getLogger(this.getClass()).info(
        "TEST - 10013000, SNOMEDCT, 20140731, " + authToken);

    // Test that this concept is valid
    ConceptJpa concept =
        (ConceptJpa) contentClient.getSingleConcept("10013000", "SNOMEDCT",
            "20140731", authToken);
    Logger.getLogger(this.getClass()).info(
        ConceptReportHelper.getConceptReport(concept));
    ValidationResult result = client.validateConcept(concept, authToken);
    Assert.assertTrue(result.isValid());

    // Remove SY description
    Description sy = null;
    for (Description d : concept.getDescriptions()) {
      if (d.getTerm().equals("Lateral meniscus of knee joint,")) {
        sy = d;
        break;
      }
    }
    concept.removeDescription(sy);
    Logger.getLogger(this.getClass()).info(
        ConceptReportHelper.getConceptReport(concept));
    result = client.validateConcept(concept, authToken);
    Assert.assertTrue(result.isValid());

    // Remove PT description
    Description pt = null;
    for (Description d : concept.getDescriptions()) {
      if (d.getTerm().equals(concept.getDefaultPreferredName())) {
        pt = d;
        break;
      }
    }
    concept.removeDescription(pt);
    Logger.getLogger(this.getClass()).info(
        ConceptReportHelper.getConceptReport(concept));
    result = client.validateConcept(concept, authToken);
    Assert.assertFalse(result.isValid());

    // Add pt back and remove fn
    concept.addDescription(pt);
    Description fn = null;
    for (Description d : concept.getDescriptions()) {
      if (d.getTerm().equals("Lateral meniscus structure (body structure)")) {
        fn = d;
        break;
      }
    }
    concept.removeDescription(pt);
    Logger.getLogger(this.getClass()).info(
        ConceptReportHelper.getConceptReport(concept));
    result = client.validateConcept(concept, authToken);
    Assert.assertFalse(result.isValid());
  }

  /**
   * Teardown.
   */
  @After
  public void teardown() {
    // do nothing
  }
}
