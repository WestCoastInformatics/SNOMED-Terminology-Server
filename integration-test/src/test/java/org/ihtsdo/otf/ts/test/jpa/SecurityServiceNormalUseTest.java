package org.ihtsdo.otf.ts.test.jpa;

import org.ihtsdo.otf.ts.jpa.services.SecurityServiceJpa;
import org.ihtsdo.otf.ts.services.SecurityService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Implementation of the "Security Service Jpa Normal Use" Test Cases.
 */
public class SecurityServiceNormalUseTest {

  /** The service. */
  private SecurityService service;

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
    service = new SecurityServiceJpa();
  }

  /**
   * Test normal use of the authenticate methods of {@link SecurityServiceJpa}.
   * 
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseJpaSecurity001() throws Exception {
    // TODO: PG
  }

  /**
   * Test normal use of user management methods of {@link SecurityServiceJpa}.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseJpaSecurity002() throws Exception {
    // TODO: PG
  }

  /**
   * Test normal use of logout method of {@link SecurityServiceJpa}.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseJpaSecurity003() throws Exception {
    // TODO: PG
  }

  /**
   * Teardown.
   *
   * @throws Exception the exception
   */
  @After
  public void teardown() throws Exception {
    // close test fixtures per test
    service.close();
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
