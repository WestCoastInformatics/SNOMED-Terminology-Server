package org.ihtsdo.otf.ts.client.test;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.jpa.client.ContentClientRest;
import org.ihtsdo.otf.ts.jpa.client.SecurityClientRest;
import org.ihtsdo.otf.ts.jpa.client.ValidationClientRest;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.services.helpers.ConceptReportHelper;
import org.junit.After;
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
      client = new ValidationClientRest(ConfigUtility.getTestConfigProperties());
      contentClient = new ContentClientRest(ConfigUtility.getTestConfigProperties());
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
    ConceptJpa c =
        (ConceptJpa) contentClient.getSingleConcept("10013000", "SNOMEDCT", "20140731", authToken);
    client.validateConcept(c, authToken);
    // Log concept for development
    Logger.getLogger(this.getClass()).info(
        ConceptReportHelper.getConceptReport(c));
  }

  /**
   * Teardown.
   */
  @After
  public void teardown() {
    // do nothing
  }
}
