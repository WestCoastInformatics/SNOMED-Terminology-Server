/*
 * 
 */
package org.ihtsdo.otf.ts.test.rest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;

import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ConceptListJpa;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.test.helpers.PfsParameterForConceptTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Implementation of the "Project Service REST Normal Use" Test Cases.
 */
public class ProjectServiceRestEdgeCasesTest extends ProjectServiceRestTest {


  /**  The viewer auth token. */
  private static String viewerAuthToken;
  
  /**  The admin auth token. */
  private static String adminAuthToken;


  /**
   * Create test fixtures per test.
   *
   * @throws Exception the exception
   */
  @Override
  @Before
  public void setup() throws Exception {

    // authentication
    viewerAuthToken = securityService.authenticate(testUser, testPassword);
    adminAuthToken = securityService.authenticate(adminUser, adminPassword);

  }

  /**
   * Test Get and Find methods for concepts.
   *
   * @throws Exception the exception
   */
  @Test
  public void testEdgeCasesRestProject001() throws Exception {

  }

  /**
   * Test transitive closure methods (ancestors, descendants, children).
   *
   * @throws Exception the exception
   */
  @Test
  public void testEdgeCasesRestProject002() throws Exception {

  }

  /**
   * Test Get methods for descriptions.
   *
   * @throws Exception the exception
   */
  @Test
  public void testEdgeCasesRestProject003() throws Exception {

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
    securityService.logout(viewerAuthToken);
    securityService.logout(adminAuthToken);
  }

}
