package org.ihtsdo.otf.ts.rest.impl;

import javax.ws.rs.DELETE;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.UserJpa;
import org.ihtsdo.otf.ts.jpa.services.SecurityServiceJpa;
import org.ihtsdo.otf.ts.rest.ContentChangeServiceRest;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.TransitiveRelationship;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RelationshipJpa;
import org.ihtsdo.otf.ts.rf2.jpa.TransitiveRelationshipJpa;
import org.ihtsdo.otf.ts.services.SecurityService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * REST implementation for {@link ContentChangeServiceRest}.
 */
@Path("/edit")
@Api(value = "/edit", description = "Operations to retrieve RF2 content for a terminology.")
@Produces({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
public class ContentChangeServiceRestImpl extends RootServiceRestImpl implements
    ContentChangeServiceRest {

  /** The security service. */
  private SecurityService securityService;

  /**
   * Instantiates an empty {@link ContentChangeServiceRestImpl}.
   *
   * @throws Exception the exception
   */
  public ContentChangeServiceRestImpl() throws Exception {
    securityService = new SecurityServiceJpa();

  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#addConcept(org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa, org.ihtsdo.otf.ts.helpers.UserJpa, java.lang.String)
   */
  @Override
  @PUT
  @Path("/concept")
  @ApiOperation(value = "Add new transitiveRelationship", notes = "Create new transitiveRelationship.", response = Concept.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public Concept addConcept(
    @ApiParam(value = "Concept, e.g. newConcept", required = true) @PathParam("concept") ConceptJpa concept,
    @ApiParam(value = "User, e.g. 'guest'", required = true) @PathParam("user") UserJpa user,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentChangeServiceRestImpl.class).info(
        "RESTful call PUT (ContentChange): /concept "
            + concept);
    authenticate(securityService, authToken, "add Concept");
    return null;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#updateConcept(org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa, org.ihtsdo.otf.ts.helpers.UserJpa, java.lang.String)
   */
  @Override
  @POST
  @Path("/concept")
  @ApiOperation(value = "Update Concept", notes = "Update Concept.")
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public void updateConcept(
    @ApiParam(value = "Concept, e.g. update", required = true) @PathParam("concept") ConceptJpa concept,
    @ApiParam(value = "User, e.g. 'guest'", required = true) @PathParam("user") UserJpa user,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentChangeServiceRestImpl.class).info(
        "RESTful call POST (ContentChange): /concept "
            + concept);
    authenticate(securityService, authToken, "update Concept");

  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#removeConcept(java.lang.Long, org.ihtsdo.otf.ts.helpers.UserJpa, java.lang.String)
   */
  @Override
  @DELETE
  @Path("/concept/id/{id}")
  @ApiOperation(value = "Remove Concept by id", notes = "Removes the Concept for the specified id.")
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public void removeConcept(
    @ApiParam(value = "Concept internal id, e.g. 2", required = true) @PathParam("id") Long id,
    @ApiParam(value = "User, e.g. 'guest'", required = true) @PathParam("user") UserJpa user,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful DELETE call (ContentChange): /concept/id/" + id);
    authenticate(securityService, authToken, "remove Concept");
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#addDescription(org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa, org.ihtsdo.otf.ts.helpers.UserJpa, java.lang.String)
   */
  @Override
  @PUT
  @Path("/description")
  @ApiOperation(value = "Add new description", notes = "Create new description.", response = Description.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public Description addDescription(
    @ApiParam(value = "Description, e.g. newDescription", required = true) @PathParam("description") DescriptionJpa description,
    @ApiParam(value = "User, e.g. 'guest'", required = true) @PathParam("user") UserJpa user,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    authenticate(securityService, authToken, "add Description");
    return null;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#updateDescription(org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa, org.ihtsdo.otf.ts.helpers.UserJpa, java.lang.String)
   */
  @Override
  @POST
  @Path("/description")
  @ApiOperation(value = "Update description", notes = "Update description.")
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public void updateDescription(
    @ApiParam(value = "Description, e.g. newDescription", required = true) @PathParam("description") DescriptionJpa description,
    @ApiParam(value = "User, e.g. 'guest'", required = true) @PathParam("user") UserJpa user,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    authenticate(securityService, authToken, "update Description");

  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#removeDescription(java.lang.Long, org.ihtsdo.otf.ts.helpers.UserJpa, java.lang.String)
   */
  @Override
  @DELETE
  @Path("/description/id/{id}")
  @ApiOperation(value = "Remove description by id", notes = "Removes the description for the specified id.")
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public void removeDescription(
    @ApiParam(value = "Descrption internal id, e.g. 2", required = true) @PathParam("id") Long id,
    @ApiParam(value = "User, e.g. 'guest'", required = true) @PathParam("user") UserJpa user,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    authenticate(securityService, authToken, "remove Description");

  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#addRelationship(org.ihtsdo.otf.ts.rf2.jpa.RelationshipJpa, org.ihtsdo.otf.ts.helpers.UserJpa, java.lang.String)
   */
  @Override
  @PUT
  @Path("/relationship")
  @ApiOperation(value = "Add new relationship", notes = "Create new relationship.", response = Relationship.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public Relationship addRelationship(
    @ApiParam(value = "Relationship, e.g. newRelationship", required = true) @PathParam("relationship") RelationshipJpa relationship,
    @ApiParam(value = "User, e.g. 'guest'", required = true) @PathParam("user") UserJpa user,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentChangeServiceRestImpl.class).info(
        "RESTful call PUT (ContentChange): /relationship "
            + relationship);
    authenticate(securityService, authToken, "add Relationship");
    return null;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#updateRelationship(org.ihtsdo.otf.ts.rf2.jpa.RelationshipJpa, org.ihtsdo.otf.ts.helpers.UserJpa, java.lang.String)
   */
  @Override
  @POST
  @Path("/relationship")
  @ApiOperation(value = "Update relationship", notes = "Update relationship.")
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public void updateRelationship(
    @ApiParam(value = "Relationship, e.g. update", required = true) @PathParam("relationship") RelationshipJpa relationship,
    @ApiParam(value = "User, e.g. 'guest'", required = true) @PathParam("user") UserJpa user,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentChangeServiceRestImpl.class).info(
        "RESTful call POST (ContentChange): /relationship "
            + relationship);
    authenticate(securityService, authToken, "update Relationship");

  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#removeRelationship(java.lang.Long, org.ihtsdo.otf.ts.helpers.UserJpa, java.lang.String)
   */
  @Override
  @DELETE
  @Path("/relationship/id/{id}")
  @ApiOperation(value = "Remove relationship by id", notes = "Removes the relationship for the specified id.")
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public void removeRelationship(
    @ApiParam(value = "Relationship internal id, e.g. 2", required = true) @PathParam("id") Long id,
    @ApiParam(value = "User, e.g. 'guest'", required = true) @PathParam("user") UserJpa user,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful DELETE call (ContentChange): /relationship/id/" + id);
    authenticate(securityService, authToken, "remove Relationship");
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#addTransitiveRelationship(org.ihtsdo.otf.ts.rf2.jpa.TransitiveRelationshipJpa, org.ihtsdo.otf.ts.helpers.UserJpa, java.lang.String)
   */
  @Override
  @PUT
  @Path("/transitiveRelationship")
  @ApiOperation(value = "Add new transitiveRelationship", notes = "Create new transitiveRelationship.", response = TransitiveRelationship.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public TransitiveRelationship addTransitiveRelationship(
    @ApiParam(value = "TransitiveRelationship, e.g. newTransitiveRelationship", required = true) @PathParam("transitiveRelationship") TransitiveRelationshipJpa transitiveRelationship,
    @ApiParam(value = "User, e.g. 'guest'", required = true) @PathParam("user") UserJpa user,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentChangeServiceRestImpl.class).info(
        "RESTful call PUT (ContentChange): /transitiveRelationship "
            + transitiveRelationship);
    authenticate(securityService, authToken, "add TransitiveRelationship");
    return null;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#updateTransitiveRelationship(org.ihtsdo.otf.ts.rf2.jpa.TransitiveRelationshipJpa, org.ihtsdo.otf.ts.helpers.UserJpa, java.lang.String)
   */
  @Override
  @POST
  @Path("/transitiveRelationship")
  @ApiOperation(value = "Update transitiveRelationship", notes = "Update transitiveRelationship.")
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public void updateTransitiveRelationship(
    @ApiParam(value = "TransitiveRelationship, e.g. update", required = true) @PathParam("transitiveRelationship") TransitiveRelationshipJpa transitiveRelationship,
    @ApiParam(value = "User, e.g. 'guest'", required = true) @PathParam("user") UserJpa user,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentChangeServiceRestImpl.class).info(
        "RESTful call POST (ContentChange): /transitiveRelationship "
            + transitiveRelationship);
    authenticate(securityService, authToken, "update TransitiveRelationship");

  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#removeTransitiveRelationship(java.lang.Long, org.ihtsdo.otf.ts.helpers.UserJpa, java.lang.String)
   */
  @Override
  @DELETE
  @Path("/transitiveRelationship/id/{id}")
  @ApiOperation(value = "Remove transitiveRelationship by id", notes = "Removes the transitiveRelationship for the specified id.")
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public void removeTransitiveRelationship(
    @ApiParam(value = "TransitiveRelationship internal id, e.g. 2", required = true) @PathParam("id") Long id,
    @ApiParam(value = "User, e.g. 'guest'", required = true) @PathParam("user") UserJpa user,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful DELETE call (ContentChange): /transitiveRelationship/id/" + id);
    authenticate(securityService, authToken, "remove TransitiveRelationship");
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#computeTransitiveClosure(java.lang.String, java.lang.String, java.lang.String, org.ihtsdo.otf.ts.helpers.UserJpa, java.lang.String)
   */
  @Override
  @POST
  @Path("/transitiveClosure/{terminology}/{version}/{terminologyId}")
  @ApiOperation(value = "Compute transitive closure by id, terminology, and version", notes = "Removes all transitive closure matching the specified parameters.")
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })  public void computeTransitiveClosure(@ApiParam(value = "Concept terminology id, e.g. 102751005", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Concept terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. 20140731", required = true) @PathParam("version") String version,
    @ApiParam(value = "User, e.g. 'guest'", required = true) @PathParam("user") UserJpa user,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {

    authenticate(securityService, authToken, "compute TransitiveClosure");

  }
  
  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#clearTransitiveClosure(java.lang.String, java.lang.String, java.lang.String, org.ihtsdo.otf.ts.helpers.UserJpa, java.lang.String)
   */
  @Override
  @DELETE
  @Path("/transitiveClosure/{terminology}/{version}/{terminologyId}")
  @ApiOperation(value = "Remove transitive closure by id, terminology, and version", notes = "Removes all transitive closure matching the specified parameters.")
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })  public void clearTransitiveClosure(@ApiParam(value = "Concept terminology id, e.g. 102751005", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Concept terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. 20140731", required = true) @PathParam("version") String version,
    @ApiParam(value = "User, e.g. 'guest'", required = true) @PathParam("user") UserJpa user,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {

    authenticate(securityService, authToken, "clear TransitiveClosure");

  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#clearConcepts(java.lang.String, java.lang.String, java.lang.String, org.ihtsdo.otf.ts.helpers.UserJpa, java.lang.String)
   */
  @Override
  @DELETE
  @Path("/concepts/{terminology}/{version}/{terminologyId}")
  @ApiOperation(value = "Remove concept(s) by id, terminology, and version", notes = "Removes all concepts matching the specified parameters.")
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public void clearConcepts(
    @ApiParam(value = "Concept terminology id, e.g. 102751005", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Concept terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. 20140731", required = true) @PathParam("version") String version,
    @ApiParam(value = "User, e.g. 'guest'", required = true) @PathParam("user") UserJpa user,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    authenticate(securityService, authToken, "clear Concepts");

  }

}
