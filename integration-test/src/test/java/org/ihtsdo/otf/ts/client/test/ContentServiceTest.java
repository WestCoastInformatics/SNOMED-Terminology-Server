package org.ihtsdo.otf.ts.client.test;

import org.ihtsdo.otf.mapping.rf2.Concept;
import org.ihtsdo.otf.mapping.services.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.jpa.client.ContentClientJpa;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for REST content service.
 */
public class ContentServiceTest {

  /** The client. */
  private ContentClientJpa client;

  /**
   * Setup.
   * @throws Exception
   */
  @Before
  public void setup() throws Exception {
    client = new ContentClientJpa(ConfigUtility.getTestConfigProperties());

  }

  /**
   * Test get concept.
   * @throws Exception 
   */
  @Test
  public void testGetConcept() throws Exception {
    Concept c = client.getConcept("10013000", "SNOMEDCT", "20140731");
    System.out.println("c.name = " + c.getDefaultPreferredName());
  }

  /**
   * Teardown.
   */
  @After
  public void teardown() {
    // do nothing
  }
}
