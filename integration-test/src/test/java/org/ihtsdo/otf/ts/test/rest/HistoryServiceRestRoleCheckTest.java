/*
 * Copyright 2015 West Coast Informatics, LLC
 */
/*
 * 
 */
package org.ihtsdo.otf.ts.test.rest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Implementation of the "History Service REST Normal Use" Test Cases.
 */
public class HistoryServiceRestRoleCheckTest extends HistoryServiceRestTest {

  /**
   * Create test fixtures per test.
   *
   * @throws Exception the exception
   */
  @Override
  @Before
  public void setup() throws Exception {

    // ensure terminology correctly set
    terminology = "SNOMEDCT";

    // authentication -- use admin for this test
    authToken = securityService.authenticate(adminUser, adminPassword);
  }

  /**
   * Test release info methods
   * @throws Exception
   */
  @Test
  public void testRoleCheckRestHistory001() throws Exception {

  }

  /**
   * Teardown.
   *
   * @throws Exception the exception
   */
  @Override
  @After
  public void teardown() throws Exception {

    // logout
    securityService.logout(authToken);
  }

}
