package org.ihtsdo.otf.ts.rest.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.LocalException;
import org.ihtsdo.otf.ts.jpa.services.SecurityServiceJpa;
import org.ihtsdo.otf.ts.rest.SecurityServiceRest;
import org.ihtsdo.otf.ts.services.SecurityService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Security service for authentication.
 */
@Path("/security")
@Api(value = "/security", description = "Operations supporting security.")
@Produces({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
public class SecurityServiceRestImpl extends RootServiceRestImpl implements
    SecurityServiceRest {

  /**
   * Authenticate.
   * 
   * @param username the username
   * @param password the password
   * @return the string
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

  @Override
  @GET
  @Path("/logout/{authToken}")
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  @ApiOperation(value = "Logs out an auth token.", notes = "Performs logout on specified auth token.", response = String.class)
  public void logout(
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @PathParam("authToken") String authToken) {

    Logger.getLogger(SecurityServiceRestImpl.class).info(
        "RESTful call (Authentication): /logout for authToken = " + authToken);
    try {
      SecurityService securityService = new SecurityServiceJpa();
      securityService.logout(authToken);
    } catch (LocalException e) {
      throw new WebApplicationException(Response.status(401)
          .entity(e.getMessage()).build());
    } catch (Exception e) {
      handleException(e, "trying to authenticate a user");
    }

  }

}