package org.ihtsdo.otf.ts.test.rest;

import java.util.Properties;

import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.jpa.client.SecurityClientRest;
import org.ihtsdo.otf.ts.jpa.services.SecurityServiceJpa;
import org.ihtsdo.otf.ts.services.SecurityService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;


/**
 * The Class SecurityServiceRestTest.
 */
@Ignore
public class SecurityServiceRestTest {
  /** The service. */
  protected static SecurityClientRest service;

  /** The properties. */
  protected static Properties properties;

  /** The viewer user password. */
  protected static String viewerUserName, viewerUserPassword;

  /** The admin user password. */
  protected static String adminUserName, adminUserPassword;

  /** The bad user password. */
  protected static String badUserName, badUserPassword;

  /**
   * Create test fixtures for class.
   *
   * @throws Exception the exception
   */
  @BeforeClass
  public static void setupClass() throws Exception {
    
    // get the properties
    properties = ConfigUtility.getConfigProperties();
    
    // instantiate the service
    service = new SecurityClientRest(properties);

    /**
     * Prerequisites
     */

    // test.user and test.password must be set
    viewerUserName = properties.getProperty("viewer.user");
    viewerUserPassword = properties.getProperty("viewer.password");

    if (viewerUserName == null || viewerUserName.isEmpty()) {
      throw new Exception(
          "Test prerequisite:  viewer.user must be set in config properties file");
    }

    if (viewerUserPassword == null || viewerUserPassword.isEmpty()) {
      throw new Exception(
          "Test prerequisite:  viewer.password must be set in config properties file");
    }

    // admin.user and admin.password must be set
    adminUserName = properties.getProperty("admin.user");
    adminUserPassword = properties.getProperty("admin.password");

    if (adminUserName == null || adminUserName.isEmpty()) {
      throw new Exception(
          "Test prerequisite:  admin.user must be set in config properties file");
    }

    if (adminUserPassword == null || adminUserPassword.isEmpty()) {
      throw new Exception(
          "Test prerequisite:  admin.password must be set in config properties file");
    }

    // bad user must be specified
    badUserName = properties.getProperty("bad.user");

    if (badUserName == null || badUserName.isEmpty()) {
      throw new Exception(
          "Test prerequisite:  A non-existent (bad) user must be specified in config properties file");
    }

    SecurityService securityService = new SecurityServiceJpa();
    if (securityService.getUser(badUserName) != null) {
      throw new Exception(
          "Test prerequisite:  The bad user specified in config properties file should not exist in database");
    }
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
