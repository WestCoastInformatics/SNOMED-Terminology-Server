package org.ihtsdo.otf.mapping.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.ihtsdo.otf.mapping.services.helpers.ExceptionHandler;

/**
 * Top level class for all REST services.
 */
public class RootServiceRest {

  /**
   * Instantiates an empty {@link RootServiceRest}.
   */
  public RootServiceRest() {
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
      ExceptionHandler.handleException(e, whatIsHappening, "", "", "");
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
   * @param project the project
   * @param recordId the record id
   */
  public static void handleException(Exception e, String whatIsHappening,
    String userName, String project, String recordId) {
    try {
      ExceptionHandler.handleException(e, whatIsHappening, userName, project,
          recordId);
    } catch (Exception e1) {
      // do nothing
    }

    throw new WebApplicationException(Response
        .status(500)
        .entity(
            "Unexpected error trying to " + whatIsHappening
                + ". Please contact the administrator.").build());

  }

}
