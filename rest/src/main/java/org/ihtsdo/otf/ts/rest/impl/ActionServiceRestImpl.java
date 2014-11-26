package org.ihtsdo.otf.ts.rest.impl;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.ihtsdo.otf.ts.jpa.services.SecurityServiceJpa;
import org.ihtsdo.otf.ts.rest.ActionServiceRest;
import org.ihtsdo.otf.ts.services.SecurityService;

import com.wordnik.swagger.annotations.Api;

/**
 * REST implementation for {@link ActionServiceRest}.
 */
@Path("/action")
@Api(value = "/action", description = "Operations to perform actions on terminology.")
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

  // TODO: implement rest services as they are added
}
