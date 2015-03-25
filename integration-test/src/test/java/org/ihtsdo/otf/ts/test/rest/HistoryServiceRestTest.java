package org.ihtsdo.otf.ts.test.rest;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.ResultList;
import org.ihtsdo.otf.ts.jpa.client.HistoryClientRest;
import org.ihtsdo.otf.ts.jpa.client.SecurityClientRest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;

// TODO: Auto-generated Javadoc
/**
 * Implementation of the "History Service REST Normal Use" Test Cases.
 *
 * @author ${author}
 */
@Ignore
public class HistoryServiceRestTest {

  /** The service. */
  protected static HistoryClientRest historyService;

  /** The security service. */
  protected static SecurityClientRest securityService;

  /** The properties. */
  protected static Properties properties;

  /** The test password. */
  protected static String viewerUser;

  /** The test password. */
  protected static String viewerPassword;

  /** The admin user */
  protected static String adminUser;

  /** The admin password */
  protected static String adminPassword;

  /** The auth token. */
  protected static String authToken = null;

  /** The terminology. */
  protected static String terminology = null;

  /** The terminology version. */
  protected static String version = null;

  /** Results saved between different method hecks */
  protected static ResultList<?> results = null;

  /** Parameters, saved between different method checks */
  protected static Object[] parameters;

  protected static Set<String> objectNames = null;

  /** The dt format. */
  SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");

  /**
   * Create test fixtures for class.
   *
   * @throws Exception the exception
   */
  @BeforeClass
  public static void setupClass() throws Exception {
    properties = ConfigUtility.getConfigProperties();
    securityService = new SecurityClientRest(properties);
    historyService = new HistoryClientRest(properties);

    viewerUser = properties.getProperty("viewer.user");
    viewerPassword = properties.getProperty("viewer.password");

    adminUser = properties.getProperty("admin.user");
    adminPassword = properties.getProperty("admin.password");

    // calculate the number of objects that should be tested
    objectNames = new HashSet<>();
    for (Method m : historyService.getClass().getMethods()) {
      // extract the names from findXXXXRevisions methods
      if (m.getName().matches("find.*Revisions")) {
        objectNames.add(m.getName().replace("find", "")
            .replace("Revisions", ""));
      }
    }
    Logger.getLogger(HistoryServiceRestTest.class).info(
        "Find methods will be tested for:");
    for (String objectName : objectNames) {
      Logger.getLogger(HistoryServiceRestTest.class).info("  " + objectName);
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
    if (objectNames == null) {
      Logger.getLogger(HistoryServiceRestTest.class).info(
          "List of objects found was not initialized; cannot determine if all objects were tested");
    }
    else if (objectNames.isEmpty() == false) {
      Logger.getLogger(HistoryServiceRestTest.class).info(
          "Find methods were not tested for: ");
      for (String objectName : objectNames) {
        Logger.getLogger(HistoryServiceRestTest.class).info("  " + objectName);
      }
    } else {
      Logger.getLogger(HistoryServiceRestTest.class).info(
          "All objects found were tested");
    }
  }

  
  protected String getClassShortName(Class<?> clazz) {

    return clazz.getName().substring(clazz.getName().lastIndexOf(".") + 1)
        .replace("Jpa", "");

  }
  
  
}
