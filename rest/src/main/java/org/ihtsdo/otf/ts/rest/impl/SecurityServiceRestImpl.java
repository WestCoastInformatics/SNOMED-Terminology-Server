package org.ihtsdo.otf.ts.rest.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.LocalException;
import org.ihtsdo.otf.ts.helpers.User;
import org.ihtsdo.otf.ts.helpers.UserJpa;
import org.ihtsdo.otf.ts.helpers.UserList;
import org.ihtsdo.otf.ts.helpers.UserRole;
import org.ihtsdo.otf.ts.jpa.services.SecurityServiceJpa;
import org.ihtsdo.otf.ts.rest.SecurityServiceRest;
import org.ihtsdo.otf.ts.services.SecurityService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * REST implementation for {@link SecurityServiceRest}.
 */
@Path("/security")
@Api(value = "/security", description = "Operations supporting security.")
@Produces({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
public class SecurityServiceRestImpl extends RootServiceRestImpl implements
    SecurityServiceRest {

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.SecurityServiceRest#authenticate(java.lang.String,
   * java.lang.String)
   */
  @Override
  @POST
  @Path("/authenticate/{username}")
  @Consumes({
    MediaType.TEXT_PLAIN
  })
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  @ApiOperation(value = "Authenticate a user.", notes = "Performs authentication on specified username and password and returns a token upon successful authentication. Throws 401 error if not.", response = String.class)
  public String authenticate(
    @ApiParam(value = "Username, e.g. 'guest'", required = true) @PathParam("username") String username,
    @ApiParam(value = "Password, as string post data, e.g. 'guest'", required = true) String password) {

    Logger.getLogger(SecurityServiceRestImpl.class)
        .info(
            "RESTful call (Authentication): /authentication for user = "
                + username);
    try {
      SecurityService securityService = new SecurityServiceJpa();
      return securityService.authenticate(username, password);
    } catch (LocalException e) {
      throw new WebApplicationException(Response.status(401)
          .entity(e.getMessage()).build());
    } catch (Exception e) {
      handleException(e, "trying to authenticate a user");
      return null;
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.SecurityServiceRest#logout(java.lang.String)
   */
  @Override
  @GET
  @Path("/logout/{authToken}")
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  @ApiOperation(value = "Logs out an auth token.", notes = "Performs logout on specified auth token.", response = String.class)
  public boolean logout(
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @PathParam("authToken") String authToken) {

    Logger.getLogger(SecurityServiceRestImpl.class).info(
        "RESTful call (Authentication): /logout for authToken = " + authToken);
    try {
      SecurityService securityService = new SecurityServiceJpa();
      securityService.logout(authToken);
      return true;
    } catch (LocalException e) {
      throw new WebApplicationException(Response.status(401)
          .entity(e.getMessage()).build());
    } catch (Exception e) {
      handleException(e, "trying to authenticate a user");
      return false;
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.SecurityServiceRest#getUser(java.lang.Long,
   * java.lang.String)
   */
  @Override
  @GET
  @Path("/user/id/{id}")
  @ApiOperation(value = "Get user by id", notes = "Gets the user for the specified id.", response = User.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public User getUser(
    @ApiParam(value = "User internal id, e.g. 2", required = true) @PathParam("id") Long id,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {
    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (Content): /user/id/" + id);

    try {
      SecurityService securityService = new SecurityServiceJpa();
      authenticate(securityService, authToken, "retrieve the user",
          UserRole.VIEWER);
      User user = securityService.getUser(id);
      securityService.close();
      return user;
    } catch (Exception e) {
      handleException(e, "trying to retrieve a user");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.SecurityServiceRest#getUser(java.lang.String,
   * java.lang.String)
   */
  @Override
  @GET
  @Path("/user/name/{username}")
  @ApiOperation(value = "Get user by name", notes = "Gets the user for the specified name.", response = User.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public User getUser(
    @ApiParam(value = "Username, e.g. \"guest\"", required = true) @PathParam("username") String username,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {
    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (Content): /user/name/" + username);

    try {
      SecurityService securityService = new SecurityServiceJpa();
      authenticate(securityService, authToken, "retrieve the user by username",
          UserRole.VIEWER);
      User user = securityService.getUser(username);
      securityService.close();
      return user;
    } catch (Exception e) {
      handleException(e, "trying to retrieve a user by username");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.SecurityServiceRest#getUsers(java.lang.String)
   */
  @Override
  @GET
  @Path("/user/users")
  @ApiOperation(value = "Get all users", notes = "Gets all users.", response = UserList.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public UserList getUsers(
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {
    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (Content): /user/users");

    try {
      SecurityService securityService = new SecurityServiceJpa();
      authenticate(securityService, authToken, "retrieve all users",
          UserRole.VIEWER);
      UserList list = securityService.getUsers();
      securityService.close();
      return list;
    } catch (Exception e) {
      handleException(e, "trying to retrieve all users");
      return null;
    }
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.rest.SecurityServiceRest#addUser(org.ihtsdo.otf.ts.helpers.UserJpa, java.lang.String)
   */
  @Override
  @PUT
  @Path("/user/add")
  @ApiOperation(value = "Add new user", notes = "Creates a new user.", response = User.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public User addUser(
    @ApiParam(value = "User, e.g. newUser", required = true) UserJpa user,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentChangeServiceRestImpl.class).info(
        "RESTful call PUT (ContentChange): /user/add " + user);

    try {
      SecurityService securityService = new SecurityServiceJpa();
      authenticate(securityService, authToken, "add concept",
          UserRole.ADMINISTRATOR);

      // Create service and configure transaction scope
      User newUser = securityService.addUser(user);
      securityService.close();
      return newUser;
    } catch (Exception e) {
      handleException(e, "trying to add a user");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.SecurityServiceRest#removeUser(java.lang.Long,
   * java.lang.String)
   */
  @Override
  @DELETE
  @Path("/user/remove/{id}")
  @ApiOperation(value = "Remove user by id", notes = "Removes the user for the specified id.")
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public void removeUser(
    @ApiParam(value = "User internal id, e.g. 2", required = true) @PathParam("id") Long id,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful DELETE call (ContentChange): /user/remove/id/" + id);
    try {
      SecurityService securityService = new SecurityServiceJpa();
      authenticate(securityService, authToken, "remove user",
          UserRole.ADMINISTRATOR);

      // Remove user
      securityService.removeUser(id);
      securityService.close();
    } catch (Exception e) {
      handleException(e, "trying to remove a user");
    }
  }

  @Override
  @POST
  @Path("/user/update")
  @ApiOperation(value = "Update user", notes = "Updates the specified user.")
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public void updateUser(
    @ApiParam(value = "User, e.g. update", required = true) UserJpa user,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentChangeServiceRestImpl.class).info(
        "RESTful call POST (ContentChange): /user/update " + user);

    try {
      SecurityService securityService = new SecurityServiceJpa();
      authenticate(securityService, authToken, "update concept",
          UserRole.ADMINISTRATOR);
      securityService.updateUser(user);
      securityService.close();
    } catch (Exception e) {
      handleException(e, "trying to update a concept");
    }
  }

}