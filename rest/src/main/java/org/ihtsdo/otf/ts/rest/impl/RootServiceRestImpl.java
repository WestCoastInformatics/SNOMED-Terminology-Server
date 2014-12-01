package org.ihtsdo.otf.ts.rest.impl;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.ihtsdo.otf.ts.helpers.UserRole;
import org.ihtsdo.otf.ts.services.SecurityService;
import org.ihtsdo.otf.ts.services.handlers.ExceptionHandler;

/**
 * Top level class for all REST services.
 */
public class RootServiceRestImpl {

  /**
   * Instantiates an empty {@link RootServiceRestImpl}.
   */
  public RootServiceRestImpl() {
    // do nothing
  }

  /**
   * Handle exception.
   *
   * @param e the e
   * @param whatIsHappening the what is happening
   */
  @SuppressWarnings("static-method")
  public void handleException(Exception e, String whatIsHappening) {
    try {
      ExceptionHandler.handleException(e, whatIsHappening, "");
    } catch (Exception e1) {
      // do nothing
    }
    throw new WebApplicationException(Response
        .status(500)
        .entity(
            "Unexpected error trying to " + whatIsHappening
                + ". Please contact the administrator.").build());

  }

  /**
   * Handle exception.
   *
   * @param e the e
   * @param whatIsHappening the what is happening
   * @param userName the user name
   */
  public static void handleException(Exception e, String whatIsHappening,
    String userName) {
    try {
      ExceptionHandler.handleException(e, whatIsHappening, userName);
    } catch (Exception e1) {
      // do nothing
    }

    throw new WebApplicationException(Response
        .status(500)
        .entity(
            "Unexpected error trying to " + whatIsHappening
                + ". Please contact the administrator.").build());

  }
  
  public static void authenticate(SecurityService securityService, String authToken, String perform) throws Exception {
    // authorize call
    UserRole role = securityService.getApplicationRoleForToken(authToken);
    if (!role.hasPrivilegesOf(UserRole.VIEWER))
      throw new WebApplicationException(Response.status(401)
          .entity("User does not have permissions to " + perform + ".")
          .build());
  }

}
