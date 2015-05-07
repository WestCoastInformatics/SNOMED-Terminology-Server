/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.rest.impl;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.ihtsdo.otf.ts.UserRole;
import org.ihtsdo.otf.ts.helpers.LocalException;
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

    // throw the local exception as a web application exception
    if (e instanceof LocalException) {
      throw new WebApplicationException(Response.status(500)
          .entity(e.getMessage()).build());
    }

    // throw the web application exception as-is, e.g. for 401 errors
    if (e instanceof WebApplicationException) {
      throw (WebApplicationException) e;
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

    // throw the local exception as a web application exception
    if (e instanceof LocalException) {
      throw new WebApplicationException(Response.status(500)
          .entity(e.getMessage()).build());
    }

    // throw the web application exception as-is, e.g. for 401 errors
    if (e instanceof WebApplicationException) {
      throw (WebApplicationException) e;
    }

    throw new WebApplicationException(Response
        .status(500)
        .entity(
            "Unexpected error trying to " + whatIsHappening
                + ". Please contact the administrator.").build());

  }

  /**
   * Authenticate.
   *
   * @param securityService the security service
   * @param authToken the auth token
   * @param perform the perform
   * @param authRole the auth role
   * @throws Exception the exception
   */
  public static void authenticate(SecurityService securityService,
    String authToken, String perform, UserRole authRole) throws Exception {
    // authorize call
    UserRole role = securityService.getApplicationRoleForToken(authToken);
    UserRole cmpRole = authRole;
    if (cmpRole == null) {
      cmpRole = UserRole.VIEWER;
    }
    if (!role.hasPrivilegesOf(cmpRole))
      throw new WebApplicationException(Response.status(401)
          .entity("User does not have permissions to " + perform + ".").build());
  }

  /**
   * Returns the total elapsed time str.
   *
   * @param time the time
   * @return the total elapsed time str
   */
  @SuppressWarnings({
    "boxing"
  })
  protected static String getTotalElapsedTimeStr(long time) {
    Long resultnum = (System.nanoTime() - time) / 1000000000;
    String result = resultnum.toString() + "s";
    resultnum = resultnum / 60;
    result = result + " / " + resultnum.toString() + "m";
    resultnum = resultnum / 60;
    result = result + " / " + resultnum.toString() + "h";
    return result;
  }

}
