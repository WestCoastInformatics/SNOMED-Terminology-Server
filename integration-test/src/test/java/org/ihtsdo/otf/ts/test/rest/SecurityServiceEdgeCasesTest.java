package org.ihtsdo.otf.ts.test.rest;

import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.jpa.client.SecurityClientRest;
import org.ihtsdo.otf.ts.rest.SecurityServiceRest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Implementation of the "Security Service REST Degenerate Use" Test Cases.
 */
public class SecurityServiceEdgeCasesTest {

  /** The service. */
  private SecurityClientRest service;

  /**
   * Create test fixtures for class.
   *
   * @throws Exception the exception
   */
  @BeforeClass
  public static void setupClass() throws Exception {
    // do nothing
  }

  /**
   * Create test fixtures per test.
   *
   * @throws Exception the exception
   */
  @Before
  public void setup() throws Exception {
    service = new SecurityClientRest(ConfigUtility.getConfigProperties());
  }

  /**
   * Test edge cases of the authenticate methods of {@link SecurityServiceRest}.
   * 
   * @throws Exception the exception
   */
  @Test
  public void testEdgeCasesRestSecurity001() throws Exception {

    service.authenticate("guest", "guest");
    service.authenticate("guest", "guest");
    service.authenticate("admin", ".");
    service.authenticate("admin", ".");
  }

  //
  // No known edge cases for user managment tools 
  // so the edge case test 002 is left out.
  //
  
  /**
   * Test edge cases of logout for {@link SecurityServiceRest}.
   *
   * @throws Exception the exception
   */
  @Test
  public void testEdgeCasesRestSecurity003() throws Exception {
    service.logout("guest");

    service.authenticate("guest", "guest");
    service.logout("guest");
    service.logout("guest");

    service.authenticate("admin", ".");
    service.logout("admin");
    service.logout("admin");

  }

  /**
   * Teardown.
   *
   * @throws Exception the exception
   */
  @After
  public void teardown() throws Exception {
    // do nothing
  }

  /**
   * Teardown class.
   *
   * @throws Exception the exception
   */
  @AfterClass
  public static void teardownClass() throws Exception {
    // do nothing
  }

}
