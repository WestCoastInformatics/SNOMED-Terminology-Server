/*
 * 
 */
package org.ihtsdo.otf.ts.test.rest;

import java.util.HashSet;
import java.util.Set;

import org.ihtsdo.otf.ts.Project;
import org.ihtsdo.otf.ts.User;
import org.ihtsdo.otf.ts.jpa.ProjectJpa;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Implementation of the "Project Service REST Normal Use" Test Cases.
 */
public class ProjectServiceRestNormalUseTest extends ProjectServiceRestTest {

  /** The viewer auth token. */
  private static String viewerAuthToken;

  /** The admin auth token. */
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
   * Test add/update/remove project.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @Test
  public void testNormalUseRestProject001() throws Exception {

    // Add a project
    ProjectJpa project = new ProjectJpa();
    Set<String> values = new HashSet<>();
    values.add("PUBLISHED");
    project.setActionWorkflowStatusValues(values);
    User user = securityService.getUser(adminUser, adminAuthToken);
    project.addAdministrator(user);
    project.addAuthor(user);
    project.addLead(user);
    project.addScopeConcept("12345");
    project.addScopeExcludesConcept("12345");
    project.setDescription("Sample");
    project.setModuleId("12345");
    project.setName("Sample");
    project.setTerminology("SNOMEDCT");
    project.setTerminologyVersion("latest");

    ProjectJpa project2 =
        (ProjectJpa) projectService.addProject(project, adminAuthToken);

    // TEST: retrieve the project and verify it is equal
    Assert.assertEquals(project, project2);

    // Update that newly added project
    project2.setName("Sample 2");
    projectService.updateProject(project2, adminAuthToken);
    Project project3 =
        projectService.getProject(project2.getId(), adminAuthToken);

    // TEST: retrieve the project and verify it is equal
    Assert.assertEquals(project2, project3);

    // Remove the project
    projectService.removeProject(project2.getId(), adminAuthToken);

    // TEST: verify that it is removed (call should fail)
    project3 = projectService.getProject(project2.getId(), adminAuthToken);
    Assert.assertNull(project3);
  }

  /**
   * Test get project(s).
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseRestProject002() throws Exception {
    //
  }

  /**
   * Test find concepts in scope.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseRestProject003() throws Exception {
    //
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
