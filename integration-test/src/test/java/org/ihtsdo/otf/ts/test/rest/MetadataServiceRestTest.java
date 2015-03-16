package org.ihtsdo.otf.ts.test.rest;

import java.util.Properties;

import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.PfsParameter;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.jpa.client.MetadataClientRest;
import org.ihtsdo.otf.ts.jpa.client.SecurityClientRest;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.services.ContentService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 * Implementation of the "Metadata Service REST Normal Use" Test Cases.
 */
@Ignore
public class MetadataServiceRestTest {

  /** The service. */
  protected static MetadataClientRest metadataService;
  
  /**  The security service. */
  protected static SecurityClientRest securityService;

  /**  The properties. */
  protected static Properties properties;
  
  /**  The test password. */
  protected static String testUser;
  
  /**  The test password. */
  protected static String testPassword;

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
    metadataService = new MetadataClientRest(properties);
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
    
    if (testUser == null || testUser.isEmpty()) {
      throw new Exception("Test prerequisite: viewer.user must be specified");
    }
    if (testPassword == null || testPassword.isEmpty()) {
      throw new Exception("Test prerequisite: viewer.password must be specified");
    }
    
    // test that some terminology objects exist for both SNOMEDCT and ICD9CM
    ContentService contentService = new ContentServiceJpa();
    PfsParameter pfs = new PfsParameterJpa();
    pfs.setMaxResults(1);
    ConceptList conceptList;
    
    // check SNOMEDCT
    conceptList = contentService.getConcepts("SNOMEDCT", "latest", pfs);
    if (conceptList.getCount() == 0)
      throw new Exception("Could not retrieve any concepts for SNOMEDCT");
    if (conceptList.getTotalCount() != 10293) {
      throw new Exception("Metadata service requires SNOMEDCT loaded from the config project data.");
    }
    
    // check ICD9CM
    conceptList = contentService.getConcepts("ICD9CM", "2013", pfs);
    if (conceptList.getCount() == 0) {
      throw new Exception("Could not retrieve any concepts for ICD9CM");
    }
    if (conceptList.getTotalCount() != 17770) {
      throw new Exception("Metadata service requires ICD9CM loaded from config project data.");
    }
    contentService.close();
    
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
