package org.ihtsdo.otf.ts.rest.todo;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.UserRole;
import org.ihtsdo.otf.ts.helpers.KeyValuesMap;
import org.ihtsdo.otf.ts.helpers.RelationshipList;
import org.ihtsdo.otf.ts.jpa.ProjectJpa;
import org.ihtsdo.otf.ts.jpa.services.ActionServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.SecurityServiceJpa;
import org.ihtsdo.otf.ts.rest.ActionServiceRest;
import org.ihtsdo.otf.ts.rest.impl.RootServiceRestImpl;
import org.ihtsdo.otf.ts.services.ActionService;
import org.ihtsdo.otf.ts.services.SecurityService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * REST implementation for {@link ActionServiceRest}.
 */
@Path("/action")
@Api(value = "/action", description = "Operations to perform actions on terminology.")
@Consumes({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
@Produces({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
public class ActionServiceRestImpl extends RootServiceRestImpl implements
    ActionServiceRest {

  /** The security service. */
  private SecurityService securityService;

  /**
   * Instantiates an empty {@link ActionServiceRestImpl}.
   *
   * @throws Exception the exception
   */
  public ActionServiceRestImpl() throws Exception {
    securityService = new SecurityServiceJpa();

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ActionServiceRest#configureActionService(org.ihtsdo
   * .otf.ts.helpers.StringList, java.lang.String)
   */
  @Override
  @POST
  @Path("/configure")
  @ApiOperation(value = "Configure service for a session", notes = "Takes configuration information and returns a reusable session token.", response = String.class)
  public String configureActionService(
    @ApiParam(value = "Project, e.g. the result of a content service get project call", required = true) ProjectJpa project,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Action): /configure");

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.AUTHOR))
        throw new WebApplicationException(
            Response
                .status(401)
                .entity(
                    "User does not have permissions to configure the action service.")
                .build());

      ActionService actionService = new ActionServiceJpa();
      String token = actionService.configureActionService(project);
      actionService.close();
      return token;
    } catch (Exception e) {
      handleException(e, "trying to configure action service");
      return null;
    }
  }

  @Override
  @GET
  @Path("/clear/{sessionToken}")
  @ApiOperation(value = "Clear resources for a session", notes = "Clears all resources for the specified session token.")
  public void clear(
    @ApiParam(value = "Session token, e.g. value from /action/configure", required = true) @HeaderParam("sessionToken") String sessionToken,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Action): /clear/" + sessionToken);

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.AUTHOR))
        throw new WebApplicationException(
            Response
                .status(401)
                .entity(
                    "User does not have permissions to clear the resources for a session token.")
                .build());

      ActionService actionService = new ActionServiceJpa();
      actionService.clear(sessionToken);
      actionService.close();
    } catch (Exception e) {
      handleException(e, "trying to clear resources");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.ActionServiceRest#getProgress(java.lang.String,
   * java.lang.String)
   */
  @Override
  @GET
  @Path("/progress/{sessionToken}")
  @ApiOperation(value = "Get session progress", notes = "Gets the progress for the current activity for the specified session token.", response = Float.class)
  public float getProgress(
    @ApiParam(value = "Session token, e.g. value from /action/configure", required = true) @HeaderParam("sessionToken") String sessionToken,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Action): /progress/" + sessionToken);

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.AUTHOR))
        throw new WebApplicationException(Response.status(401)
            .entity("User does not have permissions to get progress.").build());

      ActionService actionService = new ActionServiceJpa();
      float progress = actionService.getProgress(sessionToken);
      actionService.close();
      return progress;
    } catch (Exception e) {
      handleException(e, "trying to get progress for current operation  for "
          + sessionToken);
      return -1;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.ActionServiceRest#cancel(java.lang.String,
   * java.lang.String)
   */
  @Override
  @POST
  @Path("/cancel/{sessionToken}")
  @ApiOperation(value = "Cancel session operation", notes = "Cancels the current activity for the specified session token.")
  public void cancel(
    @ApiParam(value = "Session token, e.g. value from /action/configure", required = true) @HeaderParam("sessionToken") String sessionToken,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Action): /cancel/" + sessionToken);

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.AUTHOR))
        throw new WebApplicationException(Response.status(401)
            .entity("User does not have permissions to cancel.").build());

      ActionService actionService = new ActionServiceJpa();
      actionService.cancel(sessionToken);
      actionService.close();
    } catch (Exception e) {
      handleException(e, "trying to cancel current operation  for "
          + sessionToken);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ActionServiceRest#prepareToClassify(java.lang.String
   * , java.lang.String)
   */
  @Override
  @POST
  @Path("/classify/{sessionToken}/prepare")
  @ApiOperation(value = "Prepare classification data", notes = "Prepares classification data for the specified session token.")
  public void prepareToClassify(
    @ApiParam(value = "Session token, e.g. value from /action/configure", required = true) @HeaderParam("sessionToken") String sessionToken,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Action): /classify/" + sessionToken + "/prepare");

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.AUTHOR))
        throw new WebApplicationException(
            Response
                .status(401)
                .entity(
                    "User does not have permissions to prepare classification data.")
                .build());

      ActionService actionService = new ActionServiceJpa();
      actionService.prepareToClassify(sessionToken);
      actionService.close();
    } catch (Exception e) {
      handleException(e, "trying to prepare to classify for " + sessionToken);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.ActionServiceRest#classify(java.lang.String,
   * java.lang.String)
   */
  @Override
  @POST
  @Path("/classify/{sessionToken}")
  @ApiOperation(value = "Classification", notes = "Classifies data for the specified session token.")
  public void classify(
    @ApiParam(value = "Session token, e.g. value from /action/configure", required = true) @HeaderParam("sessionToken") String sessionToken,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Action): /classify/" + sessionToken);

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.AUTHOR))
        throw new WebApplicationException(Response.status(401)
            .entity("User does not have permissions to classify.").build());

      ActionService actionService = new ActionServiceJpa();
      actionService.classify(sessionToken);
      actionService.close();
    } catch (Exception e) {
      handleException(e, "trying to classify for " + sessionToken);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ActionServiceRest#incrementalClassify(java.lang.
   * String, java.lang.String)
   */
  @Override
  @POST
  @Path("/classify/{sessionToken}/incremental")
  @ApiOperation(value = "Incremental classification", notes = "Incrementally classifies data for the specified session token.")
  public void incrementalClassify(
    @ApiParam(value = "Session token, e.g. value from /action/configure", required = true) @HeaderParam("sessionToken") String sessionToken,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Action): /classify/" + sessionToken + "/incremental");

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.AUTHOR))
        throw new WebApplicationException(Response.status(401)
            .entity("User does not have permissions to incrementall classify.")
            .build());

      ActionService actionService = new ActionServiceJpa();
      actionService.incrementalClassify(sessionToken);
      actionService.close();
    } catch (Exception e) {
      handleException(e, "trying to incrementally classify for " + sessionToken);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ActionServiceRest#getClassificationEquivalents(java
   * .lang.String, java.lang.String)
   */
  @Override
  @GET
  @Path("/classify/{sessionToken}/equivalents")
  @ApiOperation(value = "Get classification equivalents", notes = "Gets classicifaction equivalencies for the specified session token.", response = KeyValuesMap.class)
  public KeyValuesMap getClassificationEquivalents(
    @ApiParam(value = "Session token, e.g. value from /action/configure", required = true) @HeaderParam("sessionToken") String sessionToken,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Action): /classify/" + sessionToken + "/equivalents");

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.AUTHOR))
        throw new WebApplicationException(
            Response
                .status(401)
                .entity(
                    "User does not have permissions to get classification equivalents.")
                .build());

      ActionService actionService = new ActionServiceJpa();
      KeyValuesMap map =
          actionService.getClassificationEquivalents(sessionToken);
      actionService.close();
      return map;
    } catch (Exception e) {
      handleException(e, "trying to get old inferred relationships for "
          + sessionToken);
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ActionServiceRest#getOldInferredRelationships(java
   * .lang.String, java.lang.String)
   */
  @Override
  @GET
  @Path("/classify/{sessionToken}/old")
  @ApiOperation(value = "Get inferred relationships no longer active after classification", notes = "Gets inferred relationships no longer active after classification for the specified session token.", response = RelationshipList.class)
  public RelationshipList getOldInferredRelationships(
    @ApiParam(value = "Session token, e.g. value from /action/configure", required = true) @HeaderParam("sessionToken") String sessionToken,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Action): /classify/" + sessionToken + "/old");

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.AUTHOR))
        throw new WebApplicationException(
            Response
                .status(401)
                .entity(
                    "User does not have permissions to get old inferred relationship.")
                .build());

      ActionService actionService = new ActionServiceJpa();
      RelationshipList results =
          actionService.getOldInferredRelationships(sessionToken);
      actionService.close();
      return results;
    } catch (Exception e) {
      handleException(e, "trying to get old inferred relationship for "
          + sessionToken);
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ActionServiceRest#getNewInferredRelationships(java
   * .lang.String, java.lang.String)
   */
  @Override
  @GET
  @Path("/classify/{sessionToken}/new")
  @ApiOperation(value = "Get new inferred relationships after classification", notes = "Gets new inferred relationships after classification for the specified session token.", response = RelationshipList.class)
  public RelationshipList getNewInferredRelationships(
    @ApiParam(value = "Session token, e.g. value from /action/configure", required = true) @HeaderParam("sessionToken") String sessionToken,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Action): /classify/" + sessionToken + "/new");

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.AUTHOR))
        throw new WebApplicationException(
            Response
                .status(401)
                .entity(
                    "User does not have permissions to get new inferred relationship.")
                .build());

      ActionService actionService = new ActionServiceJpa();
      RelationshipList results =
          actionService.getNewInferredRelationships(sessionToken);
      actionService.close();
      return results;
    } catch (Exception e) {
      handleException(e, "trying to get new inferred relationship for "
          + sessionToken);
      return null;
    }
  }

  @Override
  @POST
  @Path("/classify/{sessionToken}/old/retire")
  @ApiOperation(value = "Retire inferred relationships no longer active after classification", notes = "Retires inferred relationships no longer active after classification for the specified session token.", response = RelationshipList.class)
  public void retireOldInferredRelationships(
    @ApiParam(value = "Session token, e.g. value from /action/configure", required = true) @HeaderParam("sessionToken") String sessionToken,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Action): /classify/" + sessionToken + "/old/retire");

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.AUTHOR))
        throw new WebApplicationException(
            Response
                .status(401)
                .entity(
                    "User does not have permissions to retire old inferred relationship.")
                .build());

      ActionService actionService = new ActionServiceJpa();
      actionService.retireOldInferredRelationships(sessionToken);
      actionService.close();

    } catch (Exception e) {
      handleException(e, "trying to get old inferred relationship for "
          + sessionToken);

    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ActionServiceRest#addNewInferredRelationships(java
   * .lang.String, java.lang.String)
   */
  @Override
  @PUT
  @Path("/classify/{sessionToken}/new/add")
  @ApiOperation(value = "Add new inferred relationships after classification", notes = "Adds new inferred relationships after classification for the specified session token.", response = RelationshipList.class)
  public void addNewInferredRelationships(
    @ApiParam(value = "Session token, e.g. value from /action/configure", required = true) @HeaderParam("sessionToken") String sessionToken,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Action): /classify/" + sessionToken + "/new");

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.AUTHOR))
        throw new WebApplicationException(
            Response
                .status(401)
                .entity(
                    "User does not have permissions to get new inferred relationship.")
                .build());

      ActionService actionService = new ActionServiceJpa();
      actionService.addNewInferredRelationships(sessionToken);
      actionService.close();
    } catch (Exception e) {
      handleException(e, "trying to get new inferred relationship for "
          + sessionToken);
    }
  }
}
