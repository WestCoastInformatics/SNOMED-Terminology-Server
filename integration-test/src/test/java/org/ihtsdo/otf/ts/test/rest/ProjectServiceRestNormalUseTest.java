/*
 * 
 */
package org.ihtsdo.otf.ts.test.rest;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.ihtsdo.otf.ts.Project;
import org.ihtsdo.otf.ts.User;
import org.ihtsdo.otf.ts.classifier.model.Relationship;
import org.ihtsdo.otf.ts.helpers.PfsParameter;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.ProjectList;
import org.ihtsdo.otf.ts.helpers.SearchResult;
import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.jpa.ProjectJpa;
import org.ihtsdo.otf.ts.rf2.Concept;
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

    for (Project p : projectService.getProjects(adminAuthToken).getObjects()) {
        if (!p.getDescription().equals("Sample project."))
  	    projectService.removeProject(p.getId(), adminAuthToken);
      }
  }

  /**
   * Test get, update, and remove project.
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

    // TEST: verify that it is removed (call should return null)
    try {
      project3 = projectService.getProject(project2.getId(), adminAuthToken);
      fail("Cannot retrieve a removed project.");
    } catch (Exception e) {
      // do nothing
    }
  }

  /**
   * Test getProjects()
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseRestProject002() throws Exception {
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
	    ProjectJpa project2 = new ProjectJpa(project);
	    project = (ProjectJpa)projectService.addProject(project, adminAuthToken);
	    
	 // Add a second project
	    project2.setName("Sample 2");
	    project2.setDescription("Sample 2");
	    project2 = (ProjectJpa)projectService.addProject(project2, adminAuthToken);
	    
	 // Get the projects
	    ProjectList projectList = projectService.getProjects(adminAuthToken);
	    int projectCount = projectList.getCount();
	    Assert.assertTrue(projectList.contains(project));
	    Assert.assertTrue(projectList.contains(project2));
	    
	 // remove first project
	    projectService.removeProject(project.getId(), adminAuthToken);
	    projectList = projectService.getProjects(adminAuthToken);
	    Assert.assertTrue(projectList.getCount() == projectCount - 1);
	    
	 // remove second project
	    projectService.removeProject(project2.getId(), adminAuthToken);
	    projectList = projectService.getProjects(adminAuthToken);
	    Assert.assertTrue(projectList.getCount() == projectCount - 2); 
	    
	    
  }

  /**
   * Test find concepts in scope.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseRestProject003() throws Exception {
	// Get the projects
	    ProjectList projectList = projectService.getProjects(viewerAuthToken);
	    Assert.assertTrue(projectList.getCount() == 1);
	    Assert.assertTrue(projectList.getObjects().get(0).getName().equals("Sample Project"));
	    
	    Set<String> scopeConcepts = projectList.getObjects().get(0).getScopeConcepts();
	    Assert.assertTrue(scopeConcepts.size() == 1);
	    Assert.assertTrue(scopeConcepts.toArray()[0].toString().equals("138875005"));
	    
	// Call findConceptsInScope() pfs is null
	    SearchResultList resultList = projectService.findConceptsInScope(
	    		projectList.getObjects().get(0).getId(), null, viewerAuthToken);
	    Assert.assertTrue(resultList.getCount() == 10293);
	    
	// Call findConceptsInScope() pfs gets first 10
	    PfsParameterJpa pfs = new PfsParameterJpa();
        pfs.setMaxResults(10);
        resultList = projectService.findConceptsInScope(projectList.getObjects().get(0).getId(), pfs, viewerAuthToken);
        Assert.assertTrue(resultList.getCount() == 10);
        
    // Make sure first 10 are sorted by dpn
        Collections.sort(resultList.getObjects(), new Comparator<SearchResult>() {
            @Override
            public int compare(SearchResult o1, SearchResult o2) {
              return o1.getValue().compareTo(o2.getValue());
            }
          });
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
