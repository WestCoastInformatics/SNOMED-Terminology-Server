/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.test.rest;

import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.Project;
import org.ihtsdo.otf.ts.User;
import org.ihtsdo.otf.ts.UserRole;
import org.ihtsdo.otf.ts.helpers.UserList;
import org.ihtsdo.otf.ts.jpa.ProjectJpa;
import org.ihtsdo.otf.ts.jpa.UserJpa;
import org.ihtsdo.otf.ts.jpa.services.ProjectServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.SecurityServiceJpa;
import org.ihtsdo.otf.ts.rest.SecurityServiceRest;
import org.ihtsdo.otf.ts.services.ProjectService;
import org.ihtsdo.otf.ts.services.SecurityService;
import org.ihtsdo.otf.ts.test.helpers.DegenerateUseMethodTestHelper;
import org.ihtsdo.otf.ts.test.helpers.DegenerateUseMethodTestHelper.ExpectedFailure;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Implementation of the "Security Service REST Degenerate Use" Test Cases.
 */
public class SecurityServiceRestDegenerateUseTest extends
    SecurityServiceRestTest {

  String authToken = null;

  /**
   * Create test fixtures per test.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @Before
  public void setup() throws Exception {

    // before each test, ensure the bad user is removed
    // possibly added after a test failure
    SecurityService securityService = new SecurityServiceJpa();
    User badUser = securityService.getUser(badUserName);
    if (badUser != null)
      securityService.removeUser(badUser.getId());

    // authenticate user
    authToken = securityService.authenticate(adminUserName, adminUserPassword);
  }

  /**
   * Test degenerate use of the authenticate methods of
   * {@link SecurityServiceRest}.
   * 
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @Test
  public void testDegenerateUseRestSecurity001() throws Exception {

    Method method =
        service.getClass().getMethod("authenticate", new Class<?>[] {
            String.class, String.class
        });

    Object[] parameters = new Object[] {
        adminUserName, adminUserPassword
    };

    DegenerateUseMethodTestHelper.testDegenerateArguments(service, method,
        parameters);
  }

  /**
   * Test degenerate use of user management methods for
   * {@link SecurityServiceRest}.
   *
   * @throws Exception the exception
   */
  @Test
  public void testDegenerateUseRestSecurity002() throws Exception {

    // degenerate helper parameters
    Method method;
    Object[] parameters;

    // local variables
    User user = new UserJpa();
    user.setApplicationRole(UserRole.ADMINISTRATOR);
    user.setName("Bad User");
    user.setUserName(badUserName);
    user.setEmail("baduser@example.com");

    /**
     */
    Logger.getLogger(getClass()).info("Procedure 1: ADD services");

    method = service.getClass().getMethod("addUser", new Class<?>[] {
        UserJpa.class, String.class
    });

    parameters = new Object[] {
        user, authToken
    };

    // NOTE: This leaves bad user around for further use
    DegenerateUseMethodTestHelper.testDegenerateArguments(service, method,
        parameters);

    // Add user with incomplete user information (e.g. blank name or email)
    // TEST: Should throw deserialization error
    Logger.getLogger(getClass()).info("  Adding user with incomplete fields");
    for (Field field : UserJpa.class.getFields()) {

      // construct the user
      user.setName(properties.getProperty("bad.user"));
      user.setEmail("no email");
      user.setUserName(properties.getProperty("bad.user"));
      user.setApplicationRole(UserRole.VIEWER);

      // set the current iterated field to null and add it
      field.set(user, null);
      try {
        user = service.addUser((UserJpa) user, authToken);

        // if no exception thrown remove user and fail test
        service.removeUser(user.getId(), authToken);
        fail("ADD user with null field " + field.getName()
            + " did not throw expected exception");
      } catch (Exception e) {
        // do nothing
      }
    }

    /**
     * Procedure 2: Testing get services
     */
    Logger.getLogger(getClass()).info("Procedure 2: GET services");

    // first get the user
    user = service.getUser(adminUserName, authToken);

    // test
    method = service.getClass().getMethod("getUser", new Class<?>[] {
        Long.class, String.class
    });

    parameters = new Object[] {
        user.getId(), authToken
    };

    // invalid Long value should return null
    DegenerateUseMethodTestHelper.testDegenerateArguments(service, method,
        parameters, new ExpectedFailure[] {
            ExpectedFailure.LONG_INVALID_NO_RESULTS_NULL_EXCEPTION, ExpectedFailure.EXCEPTION
        });

    // Get user with invalid name (does not exist in database)
    // TEST: Should return null
    
    // first remove bad user (created by tests above)
    service.removeUser(service.getUser(badUserName, authToken).getId(), authToken);
    try {
      if (service.getUser(badUserName, authToken) != null) {
        fail("GET non-existent user did not return null");
      }
    } catch (Exception e) {
      fail("GET non-existent user returned exception instead of null");
    }

    // Procedure 3: Testing update services
    Logger.getLogger(getClass()).info("Procedure 3: UPDATE services");

    // Update user with null argument
    // TEST: Should throw exception
    try {
      service.updateUser(null, authToken);
      fail("Updating user with null value did not throw expected exception");
    } catch (Exception e) {
      // do nothing
    }

    // Update user with null hibernate id
    // TEST: Should throw exception

    user = new UserJpa();
    user.setName(properties.getProperty("bad.user"));
    user.setEmail("no email");
    user.setUserName(properties.getProperty("bad.user"));
    user.setApplicationRole(UserRole.VIEWER);

    // add the user
    user = service.addUser((UserJpa) user, authToken);
    try {
      // set the id to null and update
      service.updateUser((UserJpa) user, authToken);

      fail("Updating user with null hibernate id did not throw expected exception");

    } catch (Exception e) {
      // do nothing
    }

    // Update user with incomplete user information
    // TEST: Should throw deserialization error
    for (Field field : UserJpa.class.getFields()) {

      // construct the user user = new UserJpa();
      user.setName(properties.getProperty("bad.user"));
      user.setEmail("no email");
      user.setUserName(properties.getProperty("bad.user"));
      user.setApplicationRole(UserRole.VIEWER);

      // add the user
      service.addUser((UserJpa) user, authToken);
      try {
        // set the current iterated field to null and add it
        field.set(user, null);
        service.updateUser((UserJpa) user, authToken);

        // if no exception thrown remove user and fail test
        service.removeUser(user.getId(), authToken);
        fail("UPDATE user with null field " + field.getName()
            + " did not throw expected exception");
      } catch (Exception e) {
        // do nothing
      }
    }

    // Procedure 4: Testing delete services
    Logger.getLogger(getClass()).info("Procedure 4: DELETE services");

    // Delete user with null id
    // TEST: Should throw exception
    try {
      service.removeUser(new Long(null), authToken);
      fail("DELETE user with null id did not throw expected exception");
    } catch (Exception e) {
      // do nothing
    }

    // Delete user with invalid hibernate id (does not exist)
    // TEST: Should throw exception

    UserList userList = service.getUsers(authToken);
    Long badId = Collections.max(userList.getObjects(), new Comparator<User>() {

      @Override
      public int compare(User u1, User u2) {
        return u1.getId().compareTo(u2.getId());
      }
    }).getId() + 1;
    try {
      service.removeUser(badId, authToken);
      fail("DELETE user with non-existent id did not throw expected exception");
    } catch (Exception e) {
      // do nothing
    }

    // Delete user with valid id but used by a project
    // TEST: Should throw ForeignConstraint exception
    Project project = new ProjectJpa();
    project.setName("name");
    project.setDescription("description");
    project.setPublic(true);
    project.setScopeConcepts(null);
    project.setScopeDescendantsFlag(true);
    project.setScopeExcludesConcepts(null);
    project.setScopeExcludesDescendantsFlag(true);
    project.setTerminology("terminology");
    project.setTerminologyVersion("version");
    project.setLastModifiedBy("some_user");

    user = service.getUser(properties.getProperty("bad.user"), authToken);
    project.addAuthor(user);

    // add the project
    ProjectService projectService = new ProjectServiceJpa();
    projectService.addProject(project);

    // attempt to delete the user
    try {
      service.removeUser(user.getId(), authToken);
      fail("DELETE user attached to a project did not throw expected exception");
    } catch (Exception e) {
      // do nothing
    }

    // delete the user and project
    projectService.removeProject(project.getId());
    projectService.close();
    service.removeUser(user.getId(), authToken);

  }

  //
  // No degenerate test case for testDegenerateUseRestSecurity003: Logout
  //

  /**
   * Teardown.
   *
   * @throws Exception the exception
   */
  @After
  @SuppressWarnings("static-method")
  public void teardown() throws Exception {

    // before each test, ensure the bad user is removed
    // possibly added after a test failure
    SecurityService securityService = new SecurityServiceJpa();
    User badUser = securityService.getUser(badUserName);
    if (badUser != null)
      securityService.removeUser(badUser.getId());
    securityService.close();
  }

}
