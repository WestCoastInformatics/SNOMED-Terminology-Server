package org.ihtsdo.otf.ts.client.test;

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

  /**  The Constant sctIsaRel. */
  private final static String sctIsaRel = "116680003";

  /**
   * Setup.
   * @throws Exception
   */
  @SuppressWarnings("static-method")
  @Before
  public void setup() throws Exception {
    if (client == null) {
      client = new ContentClientRest(ConfigUtility.getTestConfigProperties());
      SecurityClientRest securityClient =
          new SecurityClientRest(ConfigUtility.getTestConfigProperties());
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

    // one parent, no "other rels"
    Set<Relationship> isaRels = new HashSet<>();
    for (Relationship r : c.getRelationships()) {
      if (r.getTypeId().equals(sctIsaRel) && r.isActive()) {
        
        isaRels.add(r);
      }
    }
    assertEquals(1, isaRels.size());
    
    // three descriptions
    int descCt = 0;
    for (Description d : c.getDescriptions()) {
      descCt++;
      // each is active
      assertTrue(d.isActive());
      // each has 2 language refset members
      assertEquals(2,d.getLanguageRefSetMembers());
    }
    assertEquals(3, descCt);

    Logger.getLogger(this.getClass()).info(ConceptReportHelper.getConceptReport(c));
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
