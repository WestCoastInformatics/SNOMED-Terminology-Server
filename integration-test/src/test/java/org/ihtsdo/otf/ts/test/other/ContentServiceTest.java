package org.ihtsdo.otf.ts.test.other;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.SearchResult;
import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.jpa.client.ContentClientRest;
import org.ihtsdo.otf.ts.jpa.client.SecurityClientRest;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.services.helpers.ConceptReportHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for REST content service.
 */
public class ContentServiceTest {

  /** The client. */
  private static ContentClientRest client;

  /** The auth token. */
  private static String authToken;

  /** The Constant sctIsaRel. */
  private final static String sctIsaRel = "116680003";

  /**
   * Setup.
   * @throws Exception
   */
  @SuppressWarnings("static-method")
  @Before
  public void setup() throws Exception {
    if (client == null) {
      client = new ContentClientRest(ConfigUtility.getConfigProperties());
      SecurityClientRest securityClient =
          new SecurityClientRest(ConfigUtility.getConfigProperties());
      authToken = securityClient.authenticate("guest", "guest");
    }
  }

  /**
   * Test get single concept for SNOMEDCT.
   * @throws Exception
   */
  @Test
  public void test001GetSingleConceptSNOMEDCT() throws Exception {
    Logger.getLogger(getClass()).info(
        "TEST - " + "10013000, SNOMEDCT, latest, " + authToken);
    Concept c =
        client.getSingleConcept("10013000", "SNOMEDCT", "latest", authToken);
    Logger.getLogger(getClass()).info(
        ConceptReportHelper.getConceptReport(c));
    getConceptAssertions(c);
  }

  /**
   * Test getting a concept by id.
   * @throws Exception
   */
  @Test
  public void test004GetConcept() throws Exception {
    Concept c =
        client.getSingleConcept("10013000", "SNOMEDCT", "latest", authToken);
    Logger.getLogger(getClass()).info("TEST - " + c.getId());
    c = client.getConcept(c.getId(), authToken);
    Logger.getLogger(getClass()).info(
        ConceptReportHelper.getConceptReport(c));
    getConceptAssertions(c);
  }

  /**
   * Returns the concept assertions.
   *
   * @param c the c
   */
  @SuppressWarnings("static-method")
  private void getConceptAssertions(Concept c) {
    assertNotNull(c);
    assertNotEquals(c.getDefaultPreferredName(),
        "No default preferred name found");

    // one parent, no "other rels"
    Set<Relationship> isaRels = new HashSet<>();
    for (Relationship r : c.getRelationships()) {
      if (r.getTypeId().equals(sctIsaRel) && r.isActive()) {
        isaRels.add(r);
      }
    }
    assertEquals(2, isaRels.size());

    // three descriptions
    int descCt = 0;
    for (Description d : c.getDescriptions()) {
      descCt++;
      // each is active
      assertTrue(d.isActive());
      // each has 2 language refset members
      assertEquals(2, d.getLanguageRefSetMembers().size());
    }
    assertEquals(3, descCt);

  }

  /**
   * Test get single concept for ICD9CM.
   * @throws Exception
   */
  @Test
  public void test001GetSingleConceptICD9CM() throws Exception {
    Logger.getLogger(getClass()).info(
        "TEST - " + "339.8, ICD9CM, 2013, " + authToken);
    Concept c = client.getSingleConcept("339.8", "ICD9CM", "2013", authToken);
    assertNotNull(c);
    assertNotEquals(c.getDefaultPreferredName(),
        "No default preferred name found");
    Logger.getLogger(getClass()).info(
        "  defaultPreferredName = " + c.getDefaultPreferredName());
  }

  /**
   * Test get single concept for ICD9CM by id.
   * @throws Exception
   */
  @Test
  public void test004GetConceptICD9CM() throws Exception {
    Concept c = client.getSingleConcept("339.8", "ICD9CM", "2013", authToken);
    Logger.getLogger(getClass()).info("TEST - " + c.getId());
    c = client.getConcept(c.getId(), authToken);
    assertNotNull(c);
    assertNotEquals(c.getDefaultPreferredName(),
        "No default preferred name found");
    Logger.getLogger(getClass()).info(
        "  defaultPreferredName = " + c.getDefaultPreferredName());
  }

  /**
   * Test get concepts for ICD9CM.
   * @throws Exception
   */
  @Test
  public void test002GetConceptsSNOMEDCT() throws Exception {
    Logger.getLogger(getClass()).info(
        "TEST - " + "10013000, SNOMEDCT, latest, " + authToken);
    ConceptList c =
        client.getConcepts("10013000", "SNOMEDCT", "latest", authToken);
    assertNotNull(c);
    assertEquals(c.getTotalCount(), 1);
    assertNotEquals(c.getObjects().get(0).getDefaultPreferredName(),
        "No default preferred name found");
    Logger.getLogger(getClass()).info(
        " defaultPreferredName = "
            + c.getObjects().get(0).getDefaultPreferredName());
  }

  /**
   * Test get concepts for ICD9CM
   * @throws Exception
   */
  @Test
  public void test002GetConceptsICD9CM() throws Exception {
    Logger.getLogger(getClass()).info(
        "TEST - " + "339.8, ICD9CM, 2013, " + authToken);
    ConceptList c = client.getConcepts("339.8", "ICD9CM", "2013", authToken);
    assertNotNull(c);
    assertEquals(c.getTotalCount(), 1);
    assertNotEquals(c.getObjects().get(0).getDefaultPreferredName(),
        "No default preferred name found");
    Logger.getLogger(getClass()).info(
        " defaultPreferredName = "
            + c.getObjects().get(0).getDefaultPreferredName());
  }

  /**
   * Test get querying for concepts SNOMEDCT.
   * @throws Exception
   */
  @Test
  public void test003FindConceptsSNOMEDCT() throws Exception {
    Logger.getLogger(getClass()).info(
        "TEST - " + "SNOMEDCT, latest, sulphur, " + authToken);
    SearchResultList results =
        client.findConceptsForQuery("SNOMEDCT", "latest", "sulphur",
            new PfsParameterJpa(), authToken);
    for (SearchResult result : results.getObjects())
      Logger.getLogger(getClass()).info(result);
  }

  /**
   * Test get querying for concepts ICD9CM.
   * @throws Exception
   */
  @Test
  public void test003FindConceptsICD9CM() throws Exception {
    Logger.getLogger(getClass()).info(
        "TEST - " + "ICD9CM, 2013, sulphur, " + authToken);
    SearchResultList results =
        client.findConceptsForQuery("ICD9CM", "2013", "sulphur",
            new PfsParameterJpa(), authToken);
    for (SearchResult result : results.getObjects())
      Logger.getLogger(getClass()).info(result);
  }

  /**
   * Test get description for SNOMED.
   * @throws Exception
   */
  @Test
  public void test005GetDescriptionSNOMED() throws Exception {
    Logger.getLogger(getClass()).info(
        "TEST - " + "100114019, SNOMEDCT, latest, " + authToken);
    Description description =
        client.getDescription("100114019", "SNOMEDCT", "latest", authToken);
    assertNotNull(description);
    assertEquals(description.getTerm(), "Histidine");
    // TODO: need more tests
  }

  /**
   * Test get description for ICD9CM.
   * @throws Exception
   */
  @Test
  public void test005GetDescriptionICD9CM() throws Exception {
    Logger.getLogger(getClass()).info(
        "TEST - " + "D0000674, ICD9CM, 2013, " + authToken);
    Description description =
        client.getDescription("D0000674", "ICD9CM", "2013", authToken);
    Logger.getLogger(getClass()).info("  " + description);
    assertNotNull(description);
    assertEquals(
        description.getTerm(),
        "Tuberculous pleurisy, tubercle bacilli not found by bacteriological examination, but tuberculosis confirmed histologically");
    // TODO: need more tests
  }

  /**
   * Test get description for SNOMED.
   * @throws Exception
   */
  @Test
  public void test006GetDescriptionSNOMED() throws Exception {
    Logger.getLogger(getClass()).info(
        "TEST - " + "100114019, SNOMEDCT, latest, " + authToken);
    Description description =
        client.getDescription("100114019", "SNOMEDCT", "latest", authToken);
    description = client.getDescription(description.getId(), authToken);
    assertNotNull(description);
    assertEquals(description.getTerm(), "Histidine");
    // TODO: need more tests
  }

  /**
   * Test get description for ICD9CM.
   * @throws Exception
   */
  @SuppressWarnings("static-method")
  @Test
  public void test006GetDescriptionICD9CM() throws Exception {
    Description description =
        client.getDescription("D0000674", "ICD9CM", "2013", authToken);
    description = client.getDescription(description.getId(), authToken);
    assertNotNull(description);
    assertEquals(
        description.getTerm(),
        "Tuberculous pleurisy, tubercle bacilli not found by bacteriological examination, but tuberculosis confirmed histologically");
    // TODO: need more tests
  }



  /**
   * Teardown.
   */
  @After
  public void teardown() {
    // do nothing
  }
}
