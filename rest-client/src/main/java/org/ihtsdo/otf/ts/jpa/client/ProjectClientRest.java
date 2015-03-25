package org.ihtsdo.otf.ts.jpa.client;

import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status.Family;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.Project;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ConceptListJpa;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.ProjectList;
import org.ihtsdo.otf.ts.helpers.ProjectListJpa;
import org.ihtsdo.otf.ts.jpa.ProjectJpa;
import org.ihtsdo.otf.ts.rest.ProjectServiceRest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * A client for connecting to a content REST service.
 */
public class ProjectClientRest implements ProjectServiceRest {

  /** The config. */
  private Properties config = null;

  /**
   * Instantiates a {@link ProjectClientRest} from the specified parameters.
   *
   * @param config the config
   */
  public ProjectClientRest(Properties config) {
    this.config = config;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.rest.ContentServiceRest#addProject(org.ihtsdo.otf.ts.jpa.ProjectJpa, java.lang.String)
   */
  @Override
  public Project addProject(ProjectJpa project, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client
            .resource(config.getProperty("base.url") + "/project/add");

    String projectString =
        (project != null ? ConfigUtility.getStringForGraph(project) : null);
    Logger.getLogger(getClass()).debug(projectString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .put(ClientResponse.class, projectString);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    ProjectJpa result =
        (ProjectJpa) ConfigUtility.getGraphForString(resultString,
            ProjectJpa.class);

    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getConceptsInScope(java.lang.
   * Long, java.lang.String)
   */
  @Override
  public ConceptList getConceptsInScope(Long projectId, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/project/id/"
            + projectId + "/scope");
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    ConceptListJpa list =
        (ConceptListJpa) ConfigUtility.getGraphForString(resultString,
            ConceptListJpa.class);
    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.ContentServiceRest#getProject(java.lang.Long,
   * java.lang.String)
   */
  @Override
  public Project getProject(Long id, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/project/id/"
            + id);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    ProjectJpa project =
        (ProjectJpa) ConfigUtility.getGraphForString(resultString,
            ProjectJpa.class);
    return project;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getProjects(java.lang.String)
   */
  @Override
  public ProjectList getProjects(String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/project/projects");
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    ProjectListJpa list =
        (ProjectListJpa) ConfigUtility.getGraphForString(resultString,
            ProjectListJpa.class);
    return list;
  }

}
