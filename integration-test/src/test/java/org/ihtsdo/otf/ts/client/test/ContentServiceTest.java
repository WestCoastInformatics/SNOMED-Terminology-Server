package org.ihtsdo.otf.ts.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.SearchResult;
import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.jpa.client.ContentClientJpa;
import org.ihtsdo.otf.ts.jpa.client.SecurityClientJpa;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.services.helpers.ConfigUtility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for REST content service.
 */
public class ContentServiceTest {

  /** The client. */
  private static ContentClientJpa client;

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
      client = new ContentClientJpa(ConfigUtility.getTestConfigProperties());
      SecurityClientJpa securityClient =
          new SecurityClientJpa(ConfigUtility.getTestConfigProperties());
      authToken = securityClient.authenticate("guest", "guest");
    }
  }

  /**
   * Test get single concept for SNOMEDCT.
   * @throws Exception
   */
  @Test
  public void test001GetSingleConceptSNOMEDCT() throws Exception {
    Logger.getLogger(this.getClass()).info(
        "TEST - " + "10013000, SNOMEDCT, 20140731, " + authToken);
    Concept c =
        client.getSingleConcept("10013000", "SNOMEDCT", "20140731", authToken);
    assertNotNull(c);
    assertNotEquals(c.getDefaultPreferredName(),
        "No default preferred name found");
    Logger.getLogger(this.getClass()).info(
        "  defaultPreferredName = " + c.getDefaultPreferredName());
  }

  /**
   * Test get single concept for SNOMEDCT.
   * @throws Exception
   */
  @Test
  public void test001GetSingleConceptICD9CM() throws Exception {
    Logger.getLogger(this.getClass()).info(
        "TEST - " + "339.8, ICD9CM, 2013, " + authToken);
    Concept c = client.getSingleConcept("339.8", "ICD9CM", "2013", authToken);
    assertNotNull(c);
    assertNotEquals(c.getDefaultPreferredName(),
        "No default preferred name found");
    Logger.getLogger(this.getClass()).info(
        "  defaultPreferredName = " + c.getDefaultPreferredName());
  }

  /**
   * Test get concepts for SNOMEDCT.
   * @throws Exception
   */
  @Test
  public void test002GetConceptsSNOMEDCT() throws Exception {
    Logger.getLogger(this.getClass()).info(
        "TEST - " + "10013000, SNOMEDCT, 20140731, " + authToken);
    ConceptList c =
        client.getConcepts("10013000", "SNOMEDCT", "20140731", authToken);
    assertNotNull(c);
    assertEquals(c.getTotalCount(), 1);
    assertNotEquals(c.getConcepts().get(0).getDefaultPreferredName(),
        "No default preferred name found");
    Logger.getLogger(this.getClass()).info(
        " defaultPreferredName = "
            + c.getConcepts().get(0).getDefaultPreferredName());
  }

  /**
   * Test get concepts for ICD9CM
   * @throws Exception
   */
  @Test
  public void test002GetConceptsICD9CM() throws Exception {
    Logger.getLogger(this.getClass()).info(
        "TEST - " + "339.8, ICD9CM, 2013, " + authToken);
    ConceptList c = client.getConcepts("339.8", "ICD9CM", "2013", authToken);
    assertNotNull(c);
    assertEquals(c.getTotalCount(), 1);
    assertNotEquals(c.getConcepts().get(0).getDefaultPreferredName(),
        "No default preferred name found");
    Logger.getLogger(this.getClass()).info(
        " defaultPreferredName = "
            + c.getConcepts().get(0).getDefaultPreferredName());
  }

  /**
   * Test get querying for concepts SNOMEDCT.
   * @throws Exception
   */
  @Test
  public void test003FindConceptsSNOMEDCT() throws Exception {
    Logger.getLogger(this.getClass()).info(
        "TEST - " + "SNOMEDCT, 20140731, sulphur, " + authToken);
    SearchResultList results =
        client.findConceptsForQuery("SNOMEDCT", "20140731", "sulphur",
            new PfsParameterJpa(), authToken);
    for (SearchResult result : results.getIterable())
      Logger.getLogger(this.getClass()).info(result);
  }

  /**
   * Test get querying for concepts ICD9CM.
   * @throws Exception
   */
  @Test
  public void test003FindConceptsICD9CM() throws Exception {
    Logger.getLogger(this.getClass()).info(
        "TEST - " + "ICD9CM, 2013, sulphur, " + authToken);
    SearchResultList results =
        client.findConceptsForQuery("ICD9CM", "2013", "sulphur",
            new PfsParameterJpa(), authToken);
    for (SearchResult result : results.getIterable())
      Logger.getLogger(this.getClass()).info(result);
  }

  /**
   * Teardown.
   */
  @After
  public void teardown() {
    // do nothing
  }
}
