/*
 * 
 */
package org.ihtsdo.otf.ts.test.rest;

import java.util.HashSet;
import java.util.Set;

import org.ihtsdo.otf.ts.User;
import org.ihtsdo.otf.ts.helpers.ProjectList;
import org.ihtsdo.otf.ts.jpa.ProjectJpa;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Implementation of the "Project Service REST Edge Cases" Test Cases.
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
   * Test edge cases for adding and removing projects.
   *
   * @throws Exception the exception
   */
  @Test
  public void testEdgeCasesRestProject001() throws Exception {
	    
	  
		// Get all projects and choose the first one.
		ProjectList projectList = projectService.getProjects(adminAuthToken);
		Assert.assertTrue(projectList.getCount() > 0);
		ProjectJpa project = (ProjectJpa) projectList.getObjects().get(0);
		

	    /*Call "update" without any changes
	        TEST: get that project back from the server and it should be equals*/
		projectService.updateProject(project, adminAuthToken);
		ProjectJpa returnedProject = (ProjectJpa)projectService.getProject(project.getId(), adminAuthToken);
		Assert.assertEquals(returnedProject, project);
		
		// Here, add new project from scratch
	    ProjectJpa project2 = new ProjectJpa();
	    Set<String> values = new HashSet<>();
	    values.add("PUBLISHED");
	    project2.setActionWorkflowStatusValues(values);
	    User user = securityService.getUser(adminUser, adminAuthToken);
	    project2.addAdministrator(user);
	    project2.addAuthor(user);
	    project2.addLead(user);
	    project2.addScopeConcept("12345");
	    project2.addScopeExcludesConcept("12345");
	    project2.setDescription("Sample");
	    project2.setModuleId("12345");
	    project2.setName("Sample");
	    project2.setTerminology("SNOMEDCT");
	    project2.setTerminologyVersion("latest");

	    project2 =
	        (ProjectJpa) projectService.addProject(project2, adminAuthToken);
		
		
		// Call remove project with the id of the new project.
		projectService.removeProject(project2.getId(), adminAuthToken);
		
		// Call remove project again with the same id
		//   TEST: no exception, simply doesn't do anything.
		projectService.removeProject(project2.getId(), adminAuthToken);
		
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
