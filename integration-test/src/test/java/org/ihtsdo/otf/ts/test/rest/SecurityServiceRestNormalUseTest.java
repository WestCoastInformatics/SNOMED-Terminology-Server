package org.ihtsdo.otf.ts.test.rest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.User;
import org.ihtsdo.otf.ts.UserRole;
import org.ihtsdo.otf.ts.jpa.UserJpa;
import org.ihtsdo.otf.ts.jpa.services.SecurityServiceJpa;
import org.ihtsdo.otf.ts.rest.SecurityServiceRest;
import org.ihtsdo.otf.ts.services.SecurityService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Implementation of the "Security Service REST Normal Use" Test Cases.
 */
public class SecurityServiceRestNormalUseTest extends SecurityServiceRestTest {

  /**
   * Create test fixtures per test.
   *
   * @throws Exception the exception
   */
  @Before
  public void setup() throws Exception {
    // do nothing
  }

  /**
   * Test normal use of the authenticate methods of {@link SecurityServiceRest}.
   * 
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @Test
  public void testNormalUseRestSecurity001() throws Exception {
    String authToken = service.authenticate(viewerUserName, viewerUserPassword);
    if (authToken == null || authToken.isEmpty()) {
      fail("Failed to authenticate user");
    }
  }

  /**
   * Test normal use of user management methods for {@link SecurityServiceRest}.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @Test
  public void testNormalUseRestSecurity002() throws Exception {

    // local variables
    User user;
    String viewerUserNameAuthToken, adminAuthToken;

    // authorize the user
    adminAuthToken = service.authenticate(adminUserName, adminUserPassword);

    /**
     * Procedure 1: add a user
     */
    Logger.getLogger(SecurityServiceRestNormalUseTest.class).info(
        "  Procedure 1");

    user = new UserJpa();
    user.setApplicationRole(UserRole.VIEWER);
    user.setEmail("none");
    user.setName(badUserName);
    user.setUserName(badUserName);

    // add the user and verify that hibernate id has been set
    user = service.addUser((UserJpa) user, adminAuthToken);
    assertTrue(user != null);

    /**
     * Procedure 2: get a user
     */
    Logger.getLogger(SecurityServiceRestNormalUseTest.class).info(
        "  Procedure 2");

    user = service.getUser(user.getId(), adminAuthToken);
    assertTrue(user != null);

    /**
     * Procedure 3: update a user
     */
    Logger.getLogger(SecurityServiceRestNormalUseTest.class).info(
        "  Procedure 3");

    user.setEmail("new email");
    service.updateUser((UserJpa) user, adminAuthToken);
    user = service.getUser(badUserName, adminAuthToken);
    assertTrue(user.getEmail().equals("new email"));

    /**
     * Procedure 4: remove a user
     */
    Logger.getLogger(SecurityServiceRestNormalUseTest.class).info(
        "  Procedure 4");

    service.removeUser(user.getId(), adminAuthToken);
    user = service.getUser(badUserName, adminAuthToken);
    assertTrue(user == null);

    /**
     * Procedure 5: authenticate a user that does not exist
     */
    Logger.getLogger(SecurityServiceRestNormalUseTest.class).info(
        "  Procedure 5");

    // get the existing test user if it exists
    user = service.getUser(viewerUserName, adminAuthToken);

    // if user exists, remove it
    if (user != null) {
      service.removeUser(user.getId(), adminAuthToken);
    }

    // verify user does not exist
    user = service.getUser(viewerUserName, adminAuthToken);
    assertTrue(user == null);

    // authenticate user based on config parameters
    viewerUserNameAuthToken =
        service.authenticate(viewerUserName, viewerUserPassword);
    assertTrue(viewerUserNameAuthToken != null
        && !viewerUserNameAuthToken.isEmpty());

    // retrieve user and verify it exists
    user = service.getUser(viewerUserName, adminAuthToken);
    assertTrue(user != null && user.getUserName().equals(viewerUserName));

    /**
     * Procedure 6: Authenticate a user that exists in database with changed
     * details
     */
    Logger.getLogger(SecurityServiceRestNormalUseTest.class).info(
        "  Procedure 6");

    // save the email, modify it, re-retrieve, and verify change persisted
    String userEmail = user.getEmail();
    user.setEmail(userEmail + "_modified");
    service.updateUser((UserJpa) user, adminAuthToken);
    assertTrue(!user.getEmail().equals(userEmail));

    // authenticate the user and verify email overwritten
    viewerUserNameAuthToken =
        service.authenticate(viewerUserName, viewerUserPassword);
    assertTrue(viewerUserNameAuthToken != null
        && !viewerUserNameAuthToken.isEmpty());
    user = service.getUser(viewerUserName, adminAuthToken);
    assertTrue(user.getEmail().equals(userEmail));

  }

  /**
   * Test normal use of logout for {@link SecurityServiceRest}.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
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
  @SuppressWarnings("static-method")
  @After
  public void teardown() throws Exception {
    // remove the bad user if tests have failed
    SecurityService securityService = new SecurityServiceJpa();
    if (securityService.getUser(badUserName) != null) {
      securityService.removeUser(securityService.getUser(badUserName).getId());
    }
  }

}
