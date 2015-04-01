/*
 * 
 */
package org.ihtsdo.otf.ts.test.rest;

import static org.junit.Assert.fail;


import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.ProjectList;
import org.ihtsdo.otf.ts.jpa.ProjectJpa;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Implementation of the "Project Service REST Degenerate Use" Test Cases.
 */
public class ProjectServiceRestDegenerateUseTest extends ProjectServiceRestTest {

	/** The viewer auth token. */
	private static String viewerAuthToken;

	/** The admin auth token. */
	private static String adminAuthToken;

	/**
	 * Create test fixtures per test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Override
	@Before
	public void setup() throws Exception {

		// authentication
		viewerAuthToken = securityService.authenticate(testUser, testPassword);
		adminAuthToken = securityService.authenticate(adminUser, adminPassword);

	}

	/**
	 * Test get, update, and remove project.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testDegenerateUseRestProject001() throws Exception {

		// Get all projects and choose the first one.
		ProjectList projectList = projectService.getProjects(adminAuthToken);
		Assert.assertTrue(projectList.getCount() > 0);
		ProjectJpa project = (ProjectJpa) projectList.getObjects().get(0);

		// Call "add project" using this project (attempting to add a duplicate)
		try {
			projectService.addProject(project, adminAuthToken);
			fail("Expected exception while adding a duplicate project.");
		} catch (Exception e) {
			// do nothing
		}

		// Call "add project" using a null project
		try {
			projectService.addProject(null, adminAuthToken);
			fail("Expected exception while adding a null project.");
		} catch (Exception e) {
			// do nothing
		}
		
		// Change the name but keep id.  This will also fail.
		String name = project.getName();
		project.setName(name + "2");
		try {
			projectService.addProject(project, adminAuthToken);
			fail("Expected exception while adding a duplicate project with new name.");
		} catch (Exception e) {
			// do nothing
		}
		
		// Change the project id to null and add the project.
		project.setName(name);
		project.setId(null);
		try {
			projectService.addProject(project, adminAuthToken);
			fail("Expected exception while adding a duplicate project with null id.");
		} catch (Exception e) {
			// do nothing
		}

		
		/*
		 * Get all projects and choose the first one. Change the id to -1.
		 * Update the project
		 */
		project.setId(-1L);
		try {
			projectService.updateProject(project, adminAuthToken);
			fail("Cannot update a project with id = -1.");
		} catch (Exception e) {
			// do nothing
		}
		


		// Call "update project" using a null project.
		try {
			projectService.updateProject(null, adminAuthToken);
			fail("Cannot update a null project.");
		} catch (Exception e) {
			// do nothing
		}

		// Call "remove project" with -1 as the project id.
		try {
			projectService.removeProject(-1L, adminAuthToken);			
		} catch (Exception e) {
			fail("Exception should not be thrown when removing a project with id = -1.");
		}

		// Call "remove project" using a null project id.
		try {
			projectService.removeProject(null, adminAuthToken);
		} catch (Exception e) {
			fail("Exception should not be thrown when removing a project with a null id.");
		}
	}

	/**
	 * Test getProject() with null identifier
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testDegenerateUseRestProject002() throws Exception {
		try {
			projectService.getProject(null, adminAuthToken);
		} catch (Exception e) {
			// do nothing
		}

	}

	/**
	 * Test findConceptsInScope with degenerate parameters
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testDegenerateUseRestProject003() throws Exception {

		// Call findConceptsInScope() project id is null
		try {
	        projectService.findConceptsInScope(
	    		null, new PfsParameterJpa(), viewerAuthToken);
		} catch (Exception e) {
			// do nothing
		}
	    
		/*Call "find concepts in scope" with a pfs that has a query restriction
        TEST: exception (because it is not supported).	*/
		ProjectList projectList = projectService.getProjects(viewerAuthToken);
		ProjectJpa project = (ProjectJpa)projectList.getObjects().get(0);
		PfsParameterJpa pfs = new PfsParameterJpa();
		pfs.setQueryRestriction("testQueryRestriction");
		try {
			projectService.findConceptsInScope(
		    		project.getId(), pfs, viewerAuthToken);
			fail("");
		} catch (Exception e) {
			// do nothing
		}
		
		/*Get all projects and choose the first one.
	    Set the identifier to -1
	    Call "find concepts in scope" using this project (and null pfs).
	        TEST: exception because project with this id does not exist.*/
		project.setId(-1L);
		try {
			projectService.findConceptsInScope(
		    		project.getId(), null, viewerAuthToken);
		} catch (Exception e) {
			// do nothing
		}
	}

	/**
	 * Teardown.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Override
	@After
	public void teardown() throws Exception {
	    
		// logout
		securityService.logout(viewerAuthToken);
		securityService.logout(adminAuthToken);
	}

}
