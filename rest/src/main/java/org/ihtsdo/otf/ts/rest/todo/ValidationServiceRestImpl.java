package org.ihtsdo.otf.ts.rest.todo;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.UserRole;
import org.ihtsdo.otf.ts.ValidationResult;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.SecurityServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.ValidationServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.helper.TerminologyUtility;
import org.ihtsdo.otf.ts.rest.ValidationServiceRest;
import org.ihtsdo.otf.ts.rest.impl.RootServiceRestImpl;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.services.SecurityService;
import org.ihtsdo.otf.ts.services.ValidationService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * REST implementation for {@link ValidationServiceRest}.
 */
@Path("/validation")
@Api(value = "/validation", description = "Operations providing content validation.")
@Consumes({
  MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
@Produces({
  MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
public class ValidationServiceRestImpl extends RootServiceRestImpl implements
    ValidationServiceRest {

  /** The security service. */
  private SecurityService securityService;

  /**
   * Instantiates an empty {@link ValidationServiceRestImpl}.
   *
   * @throws Exception the exception
   */
  public ValidationServiceRestImpl() throws Exception {
    securityService = new SecurityServiceJpa();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ValidationServiceRest#validateConcept(org.ihtsdo
   * .otf.ts.rf2.Concept, java.lang.String)
   */
  @Override
  @POST
  @Path("/concept")
  @ApiOperation(value = "Validate concept", notes = "Performs validation checks on the concept and returns the results", response = ValidationResult.class)
  public ValidationResult validateConcept(
    @ApiParam(value = "The concept to validate, e.g. 'TBD'", required = true) ConceptJpa concept,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Validation): /concept " + concept.getTerminologyId());

    String user = "";
    try {
      authenticate(securityService, authToken, "validate the concept",
          UserRole.VIEWER);

      // Run graph resolution handler
      new ContentServiceJpa().getGraphResolutionHandler().resolve(
          concept,
          TerminologyUtility.getHierarchcialIsaRels(concept.getTerminology(),
              concept.getTerminologyVersion()));
      // Validate concept
      ValidationService service = new ValidationServiceJpa();
      ValidationResult result = service.validateConcept(concept);
      service.close();
      return result;
    } catch (Exception e) {
      handleException(e,
          "trying to validate the concept " + concept.getTerminologyId(), user);
      return null;
    }

  }

}
