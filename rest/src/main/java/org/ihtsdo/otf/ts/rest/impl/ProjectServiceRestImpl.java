package org.ihtsdo.otf.ts.rest.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.Project;
import org.ihtsdo.otf.ts.UserRole;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ProjectList;
import org.ihtsdo.otf.ts.jpa.ProjectJpa;
import org.ihtsdo.otf.ts.jpa.services.ProjectServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.SecurityServiceJpa;
import org.ihtsdo.otf.ts.rest.ProjectServiceRest;
import org.ihtsdo.otf.ts.services.ProjectService;
import org.ihtsdo.otf.ts.services.SecurityService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * REST implementation for {@link ProjectServiceRest}..
 */
@Path("/project")
@Consumes({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
@Produces({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
@Api(value = "/project", description = "Operations to retrieve project info.")
public class ProjectServiceRestImpl extends RootServiceRestImpl implements
    ProjectServiceRest {

  /** The security service. */
  private SecurityService securityService;

  /**
   * Instantiates an empty {@link ProjectServiceRestImpl}.
   *
   * @throws Exception the exception
   */
  public ProjectServiceRestImpl() throws Exception {
    securityService = new SecurityServiceJpa();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#addProject(org.ihtsdo.otf.ts.
   * jpa.ProjectJpa, java.lang.String)
   */
  @Override
  @PUT
  @Path("/add")
  @ApiOperation(value = "Add new project", notes = "Creates a new project.", response = Project.class)
  public Project addProject(
    @ApiParam(value = "Project, e.g. newProject", required = true) ProjectJpa project,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info(
        "RESTful call PUT (ContentChange): /add " + project);
    Logger.getLogger(getClass()).debug(project.toString());

    try {
      authenticate(securityService, authToken, "add project", UserRole.AUTHOR);

      // Create service and configure transaction scope
      ProjectService projectService = new ProjectServiceJpa();

      // check to see if project already exists
      for (Project p : projectService.getProjects().getObjects()) {
        if (p.getName().equals(project.getName())
            && p.getDescription().equals(project.getDescription())) {
          throw new Exception(
              "A project with this name and description already exists.");
        }
      }

      projectService.setTransactionPerOperation(false);
      projectService.beginTransaction();

      // Add project
      Project newProject = projectService.addProject(project);

      // Commit, close, and return
      projectService.commit();
      projectService.close();
      return newProject;
    } catch (Exception e) {
      handleException(e, "trying to add a project");
      return null;
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getConceptsInScope(java.lang.
   * Long, java.lang.String)
   */
  @Override
  @GET
  @Path("/id/{id}/scope")
  @ApiOperation(value = "Get project scope for the project id", notes = "Gets all concpets in scope for this project.", response = ConceptList.class)
  public ConceptList getConceptsInScope(

    @ApiParam(value = "Project internal id, e.g. 2", required = true) @PathParam("id") Long id,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /" + id + "/scope");

    try {
      authenticate(securityService, authToken, "get project scope",
          UserRole.VIEWER);

      ProjectService projectService = new ProjectServiceJpa();
      ConceptList list =
          projectService.getConceptsInScope(projectService.getProject(id));
      projectService.close();
      return list;
    } catch (Exception e) {
      handleException(e, "trying to retrieve scope concepts for project " + id);
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.ContentServiceRest#getProject(java.lang.Long,
   * java.lang.String)
   */
  @Override
  @GET
  @Path("/id/{id}")
  @ApiOperation(value = "Get project for id", notes = "Gets the project for the specified id.", response = ConceptList.class)
  public Project getProject(
    @ApiParam(value = "Project internal id, e.g. 2", required = true) @PathParam("id") Long id,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {
    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /id/" + id);

    try {
      authenticate(securityService, authToken, "retrieve the project",
          UserRole.VIEWER);

      ProjectService projectService = new ProjectServiceJpa();
      Project project = projectService.getProject(id);
      projectService.close();
      return project;
    } catch (Exception e) {
      handleException(e, "trying to retrieve a project");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getProjects(java.lang.String)
   */
  @Override
  @GET
  @Path("/projects}")
  @ApiOperation(value = "Get all projects", notes = "Gets all projects.", response = ConceptList.class)
  public ProjectList getProjects(
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {
    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /projects");

    try {
      authenticate(securityService, authToken, "retrieve projects",
          UserRole.VIEWER);

      ProjectService projectService = new ProjectServiceJpa();
      ProjectList list = projectService.getProjects();
      projectService.close();
      return list;
    } catch (Exception e) {
      handleException(e, "trying to retrieve the projects");
      return null;
    }
  }

}
