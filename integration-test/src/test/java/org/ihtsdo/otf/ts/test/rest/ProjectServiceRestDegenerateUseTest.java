/*
 * 
 */
package org.ihtsdo.otf.ts.test.rest;

import static org.junit.Assert.fail;

import org.ihtsdo.otf.ts.Project;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.ProjectList;
import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.jpa.ProjectJpa;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Implementation of the "Project Service REST Normal Use" Test Cases.
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

		for (Project p : projectService.getProjects(adminAuthToken)
				.getObjects()) {
			if (!p.getDescription().equals("Sample project."))
				projectService.removeProject(p.getId(), adminAuthToken);
		}
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

		/*
		 * Authenticate "admin.user" with "admin.password" Get all projects and
		 * choose the first one. Call "add project" using this project
		 * (attempting to add a duplicate) TEST: exception occurs because you
		 * cannot add something that already has an ID. Call "add project" using
		 * a null project TEST: exception Get all projects and choose the first
		 * one. Change the id to -1. Update the project TEST: exception because
		 * there is no project with that id (e.g. "update" cannot be used to add
		 * a project) Call "update project" using a null project. TEST:
		 * exception Call "remove project" with -1 as the project id. TEST:
		 * returns null Call "remove project" with null TEST: exception
		 */

		// Get all projects and choose the first one.
		ProjectList projectList = projectService.getProjects(adminAuthToken);
		Assert.assertTrue(projectList.getCount() > 0);
		ProjectJpa project = (ProjectJpa) projectList.getObjects().get(0);

		// Call "add project" using this project (attempting to add a duplicate)
		ProjectJpa testProject;
		try {
			testProject = (ProjectJpa) projectService.addProject(project,
					adminAuthToken);
			fail("Cannot add a duplicate project.");
		} catch (Exception e) {
			// do nothing
		}

		// Call "add project" using a null project
		try {
			testProject = (ProjectJpa) projectService.addProject(null,
					adminAuthToken);
			fail("Cannot add a null project.");
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
			fail("Cannot remove a project with id = -1.");
		} catch (Exception e) {
			// do nothing
		}

		// Call "remove project" using a null project id.
		try {
			projectService.removeProject(null, adminAuthToken);
			fail("Cannot remove a project with a null id.");
		} catch (Exception e) {
			// do nothing
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
			Project nullProject = projectService.getProject(null,
					adminAuthToken);
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
	        ConceptList resultList = projectService.findConceptsInScope(
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
			ConceptList resultList = projectService.findConceptsInScope(
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
			ConceptList resultList = projectService.findConceptsInScope(
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

	    for (Project p : projectService.getProjects(adminAuthToken).getObjects()) {
	        if (!p.getDescription().equals("Sample project."))
	  	    projectService.removeProject(p.getId(), adminAuthToken);
	    }
	    
		// logout
		securityService.logout(viewerAuthToken);
		securityService.logout(adminAuthToken);
	}

}
