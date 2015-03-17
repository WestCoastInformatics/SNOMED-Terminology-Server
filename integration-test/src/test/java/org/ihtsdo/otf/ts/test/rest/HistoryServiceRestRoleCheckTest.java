/*
 * 
 */
package org.ihtsdo.otf.ts.test.rest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.ihtsdo.otf.ts.ReleaseInfo;
import org.ihtsdo.otf.ts.helpers.ReleaseInfoList;
import org.ihtsdo.otf.ts.jpa.ReleaseInfoJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa;
import org.ihtsdo.otf.ts.rf2.jpa.LanguageRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RelationshipJpa;
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
