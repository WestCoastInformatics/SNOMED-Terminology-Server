package org.ihtsdo.otf.ts.test.rest;

import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.jpa.client.SecurityClientRest;
import org.ihtsdo.otf.ts.rest.SecurityServiceRest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Implementation of the "Security Service REST Normal Use" Test Cases.
 */
public class SecurityServiceNormalUseTest {

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
   * Test normal use of the authenticate methods of {@link SecurityServiceRest}.
   * 
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseRestSecurity001() throws Exception {
    String authToken = service.authenticate("guest", "guest");
    Assert.assertEquals(authToken, "guest");
    authToken = service.authenticate("admin", "admin");
    Assert.assertEquals(authToken, "admin");
  }

  /**
   * Test normal use of user management methods for {@link SecurityServiceRest}.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseRestSecurity002() throws Exception {

    // TODO: PG
  }

  /**
   * Test normal use of logout for {@link SecurityServiceRest}.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseRestSecurity003() throws Exception {
    service.authenticate("guest", "guest");
    service.authenticate("admin", "admin");
    service.logout("guest");
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
