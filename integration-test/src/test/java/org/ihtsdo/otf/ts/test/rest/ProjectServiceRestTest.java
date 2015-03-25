package org.ihtsdo.otf.ts.test.rest;

import java.util.Properties;

import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.PfsParameter;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.jpa.client.ContentClientRest;
import org.ihtsdo.otf.ts.jpa.client.ProjectClientRest;
import org.ihtsdo.otf.ts.jpa.client.SecurityClientRest;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.services.ContentService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 * Superclass for "Project Service REST" Test Cases.
 */
@Ignore
public class ProjectServiceRestTest {

  /** The service. */
  protected static ProjectClientRest projectService;
  
  /**  The security service. */
  protected static SecurityClientRest securityService;

  /**  The properties. */
  protected static Properties properties;
  
  /**  The test password. */
  protected static String testUser;
  
  /**  The test password. */
  protected static String testPassword;

  /**  The test password. */
  protected static String adminUser;
  
  /**  The test password. */
  protected static String adminPassword;

  /**
   * Create test fixtures for class.
   *
   * @throws Exception the exception
   */
  @BeforeClass
  public static void setupClass() throws Exception {
    
    // instantiate properties
    properties = ConfigUtility.getConfigProperties();
    
    // instantiate required services
    projectService = new ProjectClientRest(properties);
    securityService = new SecurityClientRest(properties);
    
    /**
     * Test prerequisites
     * Terminology SNOMEDCT exists in database
     * Terminology ICD9CM exists in database
     * The run.config.ts has "viewer.user" and "viewer.password" specified
     */
    
    // test run.config.ts has viewer user
    testUser = properties.getProperty("viewer.user");
    testPassword = properties.getProperty("viewer.password");

    // test run.config.ts has admin user
    testUser = properties.getProperty("admin.user");
    testPassword = properties.getProperty("admin.password");
    
    if (testUser == null || testUser.isEmpty()) {
      throw new Exception("Test prerequisite: viewer.user must be specified");
    }
    if (testPassword == null || testPassword.isEmpty()) {
      throw new Exception("Test prerequisite: viewer.password must be specified");
    }
    if (adminUser == null || adminUser.isEmpty()) {
      throw new Exception("Test prerequisite: admin.user must be specified");
    }
    if (adminPassword == null || adminPassword.isEmpty()) {
      throw new Exception("Test prerequisite: admin.password must be specified");
    }
    
  }

  /**
   * Create test fixtures per test.
   *
   * @throws Exception the exception
   */
  @Before
  public void setup() throws Exception {

    /**
     * Prerequisites
     */


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
