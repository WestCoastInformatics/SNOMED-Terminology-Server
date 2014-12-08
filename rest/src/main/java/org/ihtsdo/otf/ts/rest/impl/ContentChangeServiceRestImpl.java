package org.ihtsdo.otf.ts.rest.impl;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.UserRole;
import org.ihtsdo.otf.ts.jpa.algo.TransitiveClosureAlgorithm;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.SecurityServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.helper.TerminologyUtility;
import org.ihtsdo.otf.ts.rest.ContentChangeServiceRest;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa;
import org.ihtsdo.otf.ts.rf2.jpa.LanguageRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RelationshipJpa;
import org.ihtsdo.otf.ts.services.ContentService;
import org.ihtsdo.otf.ts.services.SecurityService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * REST implementation for {@link ContentChangeServiceRest}.
 */
@Path("/edit")
@Api(value = "/edit", description = "Operations to edit content for a terminology.")
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

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#addConcept(org.ihtsdo.otf
   * .ts.rf2.jpa.ConceptJpa, java.lang.String)
   */
  @Override
  @PUT
  @Path("/concept/add")
  @ApiOperation(value = "Add new concept", notes = "Creates a new concept.", response = Concept.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public Concept addConcept(
    @ApiParam(value = "Concept, e.g. newConcept", required = true) ConceptJpa concept,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentChangeServiceRestImpl.class).info(
        "RESTful call PUT (ContentChange): /concept/add " + concept);
    try {
      authenticate(securityService, authToken, "add concept", UserRole.AUTHOR);

      // Create service and configure transaction scope
      ContentService contentService = new ContentServiceJpa();
      contentService.setTransactionPerOperation(false);
      // Run graph resolver
      if (concept != null) {
        contentService.getGraphResolutionHandler().resolve(
            concept,
            TerminologyUtility.getHierarchcialIsaRels(concept.getTerminology(),
                concept.getTerminologyVersion()));
      } else {
        throw new Exception("Unexpected null concept");
      }
      // Set the lastModifiedBy
      concept.setLastModifiedBy(securityService.getUsernameForToken(authToken));

      // Add concept and compute preferred name
      Concept newConcept = contentService.addConcept(concept);
      contentService.computePreferredName(newConcept);

      // Commit, close, and return
      contentService.commit();
      contentService.close();
      return newConcept;
    } catch (Exception e) {
      handleException(e, "trying to add a concept");
      return null;
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#updateConcept(org.ihtsdo
   * .otf.ts.rf2.jpa.ConceptJpa, java.lang.String)
   */
  @Override
  @POST
  @Path("/concept/update")
  @ApiOperation(value = "Update Concept", notes = "Updates the specified oncept.")
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public void updateConcept(
    @ApiParam(value = "Concept, e.g. update", required = true) ConceptJpa concept,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentChangeServiceRestImpl.class).info(
        "RESTful call POST (ContentChange): /concept/update " + concept);
    try {
      authenticate(securityService, authToken, "update concept",
          UserRole.AUTHOR);

      // Create service and configure transaction scope
      ContentService contentService = new ContentServiceJpa();
      contentService.setTransactionPerOperation(false);
      // Run graph resolver
      if (concept != null) {
        contentService.getGraphResolutionHandler().resolve(
            concept,
            TerminologyUtility.getHierarchcialIsaRels(concept.getTerminology(),
                concept.getTerminologyVersion()));
      } else {
        throw new Exception("Unexpected null concept");
      }
      // compute preferred name and udpate concept
      contentService.computePreferredName(concept);
      contentService.updateConcept(concept);

      // Commit, close, and return
      contentService.commit();
      contentService.close();

    } catch (Exception e) {
      handleException(e, "trying to update a concept");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#removeConcept(java.lang
   * .Long, java.lang.String)
   */
  @Override
  @DELETE
  @Path("/concept/remove/{id}")
  @ApiOperation(value = "Remove concept by id", notes = "Removes the concept for the specified id.")
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public void removeConcept(
    @ApiParam(value = "Concept internal id, e.g. 2", required = true) @PathParam("id") Long id,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful DELETE call (ContentChange): /concept/remove/id/" + id);
    try {
      authenticate(securityService, authToken, "remove concept",
          UserRole.AUTHOR);
      // Remove concept
      ContentService contentService = new ContentServiceJpa();
      contentService.removeConcept(id);
      contentService.close();
    } catch (Exception e) {
      handleException(e, "trying to remove a concept");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#addDescription(org.ihtsdo
   * .otf.ts.rf2.jpa.DescriptionJpa, java.lang.String)
   */
  @Override
  @PUT
  @Path("/description/add")
  @ApiOperation(value = "Add new description", notes = "Creates a new description.", response = Description.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public Description addDescription(
    @ApiParam(value = "Description, e.g. newDescription", required = true) DescriptionJpa description,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentChangeServiceRestImpl.class).info(
        "RESTful call PUT (ContentChange): /description/add " + description);
    authenticate(securityService, authToken, "add description", UserRole.AUTHOR);
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#updateDescription(org.ihtsdo
   * .otf.ts.rf2.jpa.DescriptionJpa, java.lang.String)
   */
  @Override
  @POST
  @Path("/description/update")
  @ApiOperation(value = "Update description", notes = "Updates the specified description.")
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public void updateDescription(
    @ApiParam(value = "Description, e.g. newDescription", required = true) DescriptionJpa description,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentChangeServiceRestImpl.class).info(
        "RESTful call PUT (ContentChange): /description/update " + description);
    authenticate(securityService, authToken, "update description",
        UserRole.AUTHOR);

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#removeDescription(java.
   * lang.Long, java.lang.String)
   */
  @Override
  @DELETE
  @Path("/description/remove/{id}")
  @ApiOperation(value = "Remove description by id", notes = "Removes the description for the specified id.")
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public void removeDescription(
    @ApiParam(value = "Descrption internal id, e.g. 2", required = true) @PathParam("id") Long id,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentChangeServiceRestImpl.class).info(
        "RESTful call PUT (ContentChange): /description/remove/id/" + id);
    authenticate(securityService, authToken, "remove description",
        UserRole.AUTHOR);

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#addRelationship(org.ihtsdo
   * .otf.ts.rf2.jpa.RelationshipJpa, java.lang.String)
   */
  @Override
  @PUT
  @Path("/relationship/add")
  @ApiOperation(value = "Add new relationship", notes = "Creates a new relationship.", response = Relationship.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public Relationship addRelationship(
    @ApiParam(value = "Relationship, e.g. newRelationship", required = true) RelationshipJpa relationship,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentChangeServiceRestImpl.class).info(
        "RESTful call PUT (ContentChange): /relationship/add " + relationship);
    authenticate(securityService, authToken, "add relationship",
        UserRole.AUTHOR);
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#updateRelationship(org.
   * ihtsdo.otf.ts.rf2.jpa.RelationshipJpa, java.lang.String)
   */
  @Override
  @POST
  @Path("/relationship/update")
  @ApiOperation(value = "Update relationship", notes = "Updates the specified relationship.")
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public void updateRelationship(
    @ApiParam(value = "Relationship, e.g. update", required = true) RelationshipJpa relationship,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentChangeServiceRestImpl.class).info(
        "RESTful call POST (ContentChange): /relationship/update "
            + relationship);
    authenticate(securityService, authToken, "update relationship",
        UserRole.AUTHOR);

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#removeRelationship(java
   * .lang.Long, java.lang.String)
   */
  @Override
  @DELETE
  @Path("/relationship/remove/{id}")
  @ApiOperation(value = "Remove relationship by id", notes = "Removes the relationship for the specified id.")
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public void removeRelationship(
    @ApiParam(value = "Relationship internal id, e.g. 2", required = true) @PathParam("id") Long id,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful DELETE call (ContentChange): /relationship/remove/id/" + id);
    authenticate(securityService, authToken, "remove relationship",
        UserRole.AUTHOR);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#addLanguageRefSetMember
   * (org.ihtsdo.otf.ts.rf2.jpa.LanguageRefSetMemberJpa, java.lang.String)
   */
  @Override
  @PUT
  @Path("/language/add")
  @ApiOperation(value = "Add new language refset member", notes = "Creates a new language refset member.", response = LanguageRefSetMember.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public LanguageRefSetMember addLanguageRefSetMember(
    @ApiParam(value = "language refset member, e.g. language refset member", required = true) LanguageRefSetMemberJpa member,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentChangeServiceRestImpl.class).info(
        "RESTful call PUT (ContentChange): /language/add " + member);
    authenticate(securityService, authToken, "add language refset member",
        UserRole.AUTHOR);
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#updateLanguageRefSetMember
   * (org.ihtsdo.otf.ts.rf2.jpa.LanguageRefSetMemberJpa, java.lang.String)
   */
  @Override
  @POST
  @Path("/language/update")
  @ApiOperation(value = "Update language refset member", notes = "Updates the specified language refset member.")
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public void updateLanguageRefSetMember(
    @ApiParam(value = "language refset member, e.g. language refset member", required = true) LanguageRefSetMemberJpa member,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentChangeServiceRestImpl.class).info(
        "RESTful call POST (ContentChange): /language/update " + member);
    authenticate(securityService, authToken, "update language refset member",
        UserRole.AUTHOR);

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#removeLanguageRefSetMember
   * (java.lang.Long, java.lang.String)
   */
  @Override
  @DELETE
  @Path("/language/remove/id/{id}")
  @ApiOperation(value = "Remove language refset member by id", notes = "Removes the language refset member for the specified id.")
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public void removeLanguageRefSetMember(
    @ApiParam(value = "language refset member internal id, e.g. 2", required = true) @PathParam("id") Long id,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful DELETE call (ContentChange): /language/remove/id/" + id);
    authenticate(securityService, authToken, "remove language refset member",
        UserRole.AUTHOR);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#computeTransitiveClosure
   * (java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @GET
  @Path("/transitive/compute/{terminology}/{version}/{rootId}")
  @ApiOperation(value = "Compute transitive closure by id, terminology, and version", notes = "Computes transitive closure for the specified parameters.")
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public void computeTransitiveClosure(
    @ApiParam(value = "Root concept terminology id, e.g. 138875005", required = true) @PathParam("rootId") String rootId,
    @ApiParam(value = "Concept terminology, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. 20140731", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    try {
      authenticate(securityService, authToken, "compute transitive closure",
          UserRole.ADMINISTRATOR);
      ContentService service = new ContentServiceJpa();
      TransitiveClosureAlgorithm algo = new TransitiveClosureAlgorithm();
      algo.setTerminology(terminology);
      algo.setTerminologyVersion(version);
      algo.reset();
      algo.setRootId(service.getSingleConcept(rootId, terminology, version)
          .getId());
      algo.compute();
      algo.close();
    } catch (Exception e) {
      handleException(e, "trying to clear concepts");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#clearTransitiveClosure(
   * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @GET
  @Path("/transitive/clear/{terminology}/{version}")
  @ApiOperation(value = "Remove transitive closure by terminology, and version", notes = "Removes all transitive closure relationships matching the specified parameters.")
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public void clearTransitiveClosure(
    @ApiParam(value = "Concept terminology, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. 20140731", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    try {
      authenticate(securityService, authToken, "clear transitive closure",
          UserRole.ADMINISTRATOR);
      // Remove concept
      ContentService contentService = new ContentServiceJpa();
      contentService.clearTransitiveClosure(terminology, version);
      contentService.close();
    } catch (Exception e) {
      handleException(e, "trying to clear concepts");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#clearConcepts(java.lang
   * .String, java.lang.String, java.lang.String)
   */
  @Override
  @GET
  @Path("/concept/clear/{terminology}/{version}")
  @ApiOperation(value = "Clear concepts", notes = "Removes all concepts matching the specified parameters.")
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public void clearConcepts(
    @ApiParam(value = "Concept terminology e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. 20140731", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    try {
      authenticate(securityService, authToken, "clear concepts",
          UserRole.ADMINISTRATOR);
      // Remove concept
      ContentService contentService = new ContentServiceJpa();
      contentService.clearConcepts(terminology, version);
      contentService.close();
    } catch (Exception e) {
      handleException(e, "trying to clear concepts");
    }
  }

}
