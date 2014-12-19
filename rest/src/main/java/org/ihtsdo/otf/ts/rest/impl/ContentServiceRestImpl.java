package org.ihtsdo.otf.ts.rest.impl;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.PfsParameter;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.helpers.UserRole;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.SecurityServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.helper.TerminologyUtility;
import org.ihtsdo.otf.ts.rest.ContentServiceRest;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.services.ContentService;
import org.ihtsdo.otf.ts.services.SecurityService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * REST implementation for {@link ContentServiceRest}..
 */
@Path("/content")
@Api(value = "/content", description = "Operations to retrieve RF2 content for a terminology.")
@Produces({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
public class ContentServiceRestImpl extends RootServiceRestImpl implements
    ContentServiceRest {

  /** The security service. */
  private SecurityService securityService;

  /**
   * Instantiates an empty {@link ContentServiceRestImpl}.
   *
   * @throws Exception the exception
   */
  public ContentServiceRestImpl() throws Exception {
    securityService = new SecurityServiceJpa();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.rest.ContentServiceRest#getConcept(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @GET
  @Path("/concepts/{terminology}/{version}/{terminologyId}")
  @ApiOperation(value = "Get concept(s) by id, terminology, and version", notes = "Gets all concepts matching the specified parameters.", response = Concept.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public ConceptList getConcepts(
    @ApiParam(value = "Concept terminology id, e.g. 102751005", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Concept terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (Content): /concept/" + terminology + "/" + version + "/"
            + terminologyId);

    try {
      authenticate(securityService, authToken, "retrieve the concept",
          UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      ConceptList cl =
          contentService.getConcepts(terminologyId, terminology, version);

      for (Concept concept : cl.getObjects()) {
        if (concept != null) {
          contentService.getGraphResolutionHandler().resolve(
              concept,
              TerminologyUtility.getHierarchcialIsaRels(
                  concept.getTerminology(), concept.getTerminologyVersion()));

        }
      }
      contentService.close();
      return cl;
    } catch (Exception e) {
      handleException(e, "trying to retrieve a concept");
      return null;
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.rest.ContentServiceRest#getConcept(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @GET
  @Path("/concept/{terminology}/{version}/{terminologyId}/foruser")
  @ApiOperation(value = "Get concept by id, terminology, and version for the current user", notes = "Gets the concepts matching the specified parameters where the current user is the last modified by.", response = Concept.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public Concept getConceptForUser(
    @ApiParam(value = "Concept terminology id, e.g. 102751005", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Concept terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (Content): /concept/" + terminology + "/" + version + "/"
            + terminologyId);

    try {
      authenticate(securityService, authToken, "retrieve the concept",
          UserRole.VIEWER);

      String username = securityService.getUsernameForToken(authToken);
      ContentService contentService = new ContentServiceJpa();
      ConceptList cl =
          contentService.getConcepts(terminologyId, terminology, version);

      for (Concept concept : cl.getObjects()) {
        if (concept != null && concept.getLastModified().equals(username)) {
          contentService.getGraphResolutionHandler().resolve(
              concept,
              TerminologyUtility.getHierarchcialIsaRels(
                  concept.getTerminology(), concept.getTerminologyVersion()));

          contentService.close();
          return concept;
        }
      }
      contentService.close();
      return null;
    } catch (Exception e) {
      handleException(e, "trying to retrieve a concept");
      return null;
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.rest.ContentServiceRest#getConcept(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @GET
  @Path("/concept/{terminology}/{version}/{terminologyId}")
  @ApiOperation(value = "Get concept by id, terminology, and version", notes = "Gets the concept for the specified parameters. It assumes there is only one which may not be the case during dual independent review.", response = Concept.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public Concept getSingleConcept(
    @ApiParam(value = "Concept terminology id, e.g. 102751005", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Concept terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (Content): /concept/" + terminology + "/" + version + "/"
            + terminologyId);

    try {
      authenticate(securityService, authToken, "retrieve the concept",
          UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      Concept concept =
          contentService.getSingleConcept(terminologyId, terminology, version);

      if (concept != null) {
        contentService.getGraphResolutionHandler().resolve(
            concept,
            TerminologyUtility.getHierarchcialIsaRels(concept.getTerminology(),
                concept.getTerminologyVersion()));
      }

      contentService.close();
      return concept;
    } catch (Exception e) {
      handleException(e, "trying to retrieve a concept");
      return null;
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.ContentServiceRest#getConcept(java.lang.Long)
   */
  @Override
  @GET
  @Path("/concept/id/{id}")
  @ApiOperation(value = "Get concept by id", notes = "Gets the concept for the specified id.", response = Concept.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public Concept getConcept(
    @ApiParam(value = "Concept internal id, e.g. 2", required = true) @PathParam("id") Long id,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {
    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (Content): /concept/id/" + id);

    try {
      authenticate(securityService, authToken, "retrieve the concept",
          UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      Concept concept = contentService.getConcept(id);

      if (concept != null) {
        contentService.getGraphResolutionHandler().resolve(
            concept,
            TerminologyUtility.getHierarchcialIsaRels(concept.getTerminology(),
                concept.getTerminologyVersion()));
      }
      contentService.close();
      return concept;
    } catch (Exception e) {
      handleException(e, "trying to retrieve a concept");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.rest.ContentServiceRest#findConceptsForQuery(java
   * .lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @POST
  @Path("/concepts/{terminology}/{version}/query/{query}")
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  @ApiOperation(value = "Find concepts matching a search query.", notes = "Gets a list of search results that match the lucene query.", response = String.class)
  public SearchResultList findConceptsForQuery(
    @ApiParam(value = "Terminology, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Query, e.g. 'sulfur'", required = true) @PathParam("query") String query,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (Content): /concept/" + terminology + "/" + version
            + "/query/" + query);
    try {
      authenticate(securityService, authToken, "find concepts by query",
          UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      SearchResultList sr =
          contentService.findConceptsForQuery(terminology, version, query, pfs);
      contentService.close();
      return sr;

    } catch (Exception e) {
      handleException(e, "trying to find the concepts by query");
      return null;
    }
  }

  @Override
  public ConceptList getConceptChildren(String terminologyId,
    String terminology, String terminologyVersion, PfsParameter pfs,
    String authToken) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ConceptList getConceptDescendants(String terminologyId,
    String terminology, String terminologyVersion, PfsParameter pfs,
    String authToken) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getDescription(java.lang.Long,
   * java.lang.String)
   */
  @Override
  @GET
  @Path("/description/id/{id}")
  @ApiOperation(value = "Get description by id", notes = "Gets the description for the specified id.", response = Description.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public Description getDescription(
    @ApiParam(value = "Description internal id, e.g. 2", required = true) @PathParam("id") Long id,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {
    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (Content): /description/id/" + id);

    try {
      authenticate(securityService, authToken, "retrieve the description",
          UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      Description description = contentService.getDescription(id);

      if (description != null) {
        contentService.getGraphResolutionHandler().resolve(description);
      }
      contentService.close();
      return description;
    } catch (Exception e) {
      handleException(e, "trying to retrieve a description");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getDescription(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @GET
  @Path("/description/{terminology}/{version}/{terminologyId}")
  @ApiOperation(value = "Get description by id, terminology, and version", notes = "Gets the description for the specified parameters. It assumes there is only one which may not be the case during dual independent review.", response = Description.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public Description getDescription(
    @ApiParam(value = "Description terminology id, e.g. 100114019", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Description terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Description terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (Content): /description/" + terminology + "/" + version
            + "/" + terminologyId);

    try {
      authenticate(securityService, authToken, "retrieve the description",
          UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      Description description =
          contentService.getDescription(terminologyId, terminology, version);

      if (description != null) {
        contentService.getGraphResolutionHandler().resolve(description);
      }

      contentService.close();
      return description;
    } catch (Exception e) {
      handleException(e, "trying to retrieve a description");
      return null;
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getRelationship(java.lang.Long,
   * java.lang.String)
   */
  @Override
  @GET
  @Path("/relationship/id/{id}")
  @ApiOperation(value = "Get relationship by id", notes = "Gets the relationship for the specified id.", response = Relationship.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public Relationship getRelationship(
    @ApiParam(value = "Relationship internal id, e.g. 2", required = true) @PathParam("id") Long id,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {
    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (Content): /relationship/id/" + id);

    try {
      authenticate(securityService, authToken, "retrieve the relationship",
          UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      Relationship relationship = contentService.getRelationship(id);
      if (relationship != null) {
        contentService.getGraphResolutionHandler().resolve(relationship);
      }
      contentService.close();
      return relationship;
    } catch (Exception e) {
      handleException(e, "trying to retrieve a relationship");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getRelationship(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @GET
  @Path("/relationship/{terminology}/{version}/{terminologyId}")
  @ApiOperation(value = "Get relationship by id, terminology, and version", notes = "Gets the relationship for the specified parameters. It assumes there is only one which may not be the case during dual independent review.", response = Relationship.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public Relationship getRelationship(
    @ApiParam(value = "Relationship terminology id, e.g. 100114019", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Relationship terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Relationship terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (Content): /relationship/" + terminology + "/" + version
            + "/" + terminologyId);

    try {
      authenticate(securityService, authToken, "retrieve the relationship",
          UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      Relationship relationship =
          contentService.getRelationship(terminologyId, terminology, version);

      if (relationship != null) {
        contentService.getGraphResolutionHandler().resolve(relationship);
      }

      contentService.close();
      return relationship;
    } catch (Exception e) {
      handleException(e, "trying to retrieve a relationship");
      return null;
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getLanguageRefSetMember(java.
   * lang.Long, java.lang.String)
   */
  @Override
  @GET
  @Path("/language/id/{id}")
  @ApiOperation(value = "Get language refset member by id", notes = "Gets the language refset member for the specified id.", response = LanguageRefSetMember.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public LanguageRefSetMember getLanguageRefSetMember(
    @ApiParam(value = "Language refset member internal id, e.g. 2", required = true) @PathParam("id") Long id,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {
    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (Content): /language/id/" + id);

    try {
      authenticate(securityService, authToken,
          "retrieve the language refset member", UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      LanguageRefSetMember member = contentService.getLanguageRefSetMember(id);

      if (member != null) {
        contentService.getGraphResolutionHandler().resolve(member);
      }
      contentService.close();
      return member;
    } catch (Exception e) {
      handleException(e, "trying to retrieve a language refset member");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getLanguageRefSetMember(java.
   * lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @GET
  @Path("/language/{terminology}/{version}/{terminologyId}")
  @ApiOperation(value = "Get language refset member by id, terminology, and version", notes = "Gets the language refset member for the specified parameters. It assumes there is only one which may not be the case during dual independent review.", response = LanguageRefSetMember.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public LanguageRefSetMember getLanguageRefSetMember(
    @ApiParam(value = "Language refset member terminology id, e.g. 100114019", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Language refset member terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Language refset member terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (Content): /language/" + terminology + "/" + version
            + "/" + terminologyId);

    try {
      authenticate(securityService, authToken,
          "retrieve the language refset member", UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      LanguageRefSetMember member =
          contentService.getLanguageRefSetMember(terminologyId, terminology,
              version);

      if (member != null) {
        contentService.getGraphResolutionHandler().resolve(member);
      }

      contentService.close();
      return member;
    } catch (Exception e) {
      handleException(e, "trying to retrieve a language refset member");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.ContentServiceRest#
   * getAssociationReferenceConceptRefSetMember(java. lang.Long,
   * java.lang.String)
   */
  @Override
  @GET
  @Path("/associationReference/id/{id}")
  @ApiOperation(value = "Get association reference refset member by id", notes = "Gets the association reference  refset member for the specified id.", response = AssociationReferenceConceptRefSetMember.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public AssociationReferenceConceptRefSetMember getAssociationReferenceConceptRefSetMember(
    @ApiParam(value = "association reference refset member internal id, e.g. 2", required = true) @PathParam("id") Long id,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {
    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (Content): /associationReference/id/" + id);

    try {
      authenticate(securityService, authToken,
          "retrieve the association reference refset member", UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      AssociationReferenceConceptRefSetMember member =
          (AssociationReferenceConceptRefSetMember) contentService
              .getAssociationReferenceRefSetMember(id);

      if (member != null) {
        contentService.getGraphResolutionHandler().resolve(member);
      }
      contentService.close();
      return member;
    } catch (Exception e) {
      handleException(e,
          "trying to retrieve a association reference refset member");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.ContentServiceRest#
   * getAssociationReferenceConceptRefSetMember(java. lang.String,
   * java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @GET
  @Path("/associationReference/{terminology}/{version}/{terminologyId}")
  @ApiOperation(value = "Get association reference refset member by id, terminology, and version", notes = "Gets the association reference refset member for the specified parameters. It assumes there is only one which may not be the case during dual independent review.", response = AssociationReferenceConceptRefSetMember.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public AssociationReferenceConceptRefSetMember getAssociationReferenceConceptRefSetMember(
    @ApiParam(value = "AssociationReferenceConcept refset member terminology id, e.g. 100114019", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "AssociationReferenceConcept refset member terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "AssociationReferenceConcept refset member terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (Content): /associationReference/" + terminology + "/"
            + version + "/" + terminologyId);

    try {
      authenticate(securityService, authToken,
          "retrieve the association reference refset member", UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      AssociationReferenceConceptRefSetMember member =
          (AssociationReferenceConceptRefSetMember) contentService
              .getAssociationReferenceRefSetMember(terminologyId, terminology,
                  version);

      if (member != null) {
        contentService.getGraphResolutionHandler().resolve(member);
      }

      contentService.close();
      return member;
    } catch (Exception e) {
      handleException(e, "trying to retrieve a association reference refset member");
      return null;
    }
  }

}
