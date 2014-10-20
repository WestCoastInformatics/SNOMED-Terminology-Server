package org.ihtsdo.otf.ts.rest.impl;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.SearchResult;
import org.ihtsdo.otf.ts.helpers.SearchResultJpa;
import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.helpers.SearchResultListJpa;
import org.ihtsdo.otf.ts.helpers.UserRole;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.MetadataServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.SecurityServiceJpa;
import org.ihtsdo.otf.ts.rest.ContentServiceRest;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.services.ContentService;
import org.ihtsdo.otf.ts.services.MetadataService;
import org.ihtsdo.otf.ts.services.SecurityService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

// TODO: Auto-generated Javadoc
/**
 * REST implementation for content service.
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
  @Path("/concept/{terminology}/{terminologyVersion}/{terminologyId}")
  @ApiOperation(value = "Get concept by id, terminology, and version", notes = "Gets the concept for the specified parameters.", response = Concept.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public Concept getConcept(
    @ApiParam(value = "Concept terminology id, e.g. 102751005", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Concept terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. 20140731", required = true) @PathParam("terminologyVersion") String terminologyVersion,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (Content): /concept/" + terminology + "/"
            + terminologyVersion + "/" + terminologyId);

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.VIEWER))
        throw new WebApplicationException(Response.status(401)
            .entity("User does not have permissions to retrieve the concept.")
            .build());

      ContentService contentService = new ContentServiceJpa();
      Concept c =
          contentService.getConcept(terminologyId, terminology,
              terminologyVersion);

      if (c != null) {
        // Make sure to read descriptions and relationships (prevents
        // serialization error)
        for (Description d : c.getDescriptions()) {
          d.getLanguageRefSetMembers();
        }
        for (Relationship r : c.getRelationships()) {
          r.getDestinationConcept();
        }
      }

      contentService.close();
      return c;
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
  @GET
  @Path("/concept/{terminology}/{terminologyVersion}/query/{string}")
  @ApiOperation(value = "Find concepts matching a search query.", notes = "Gets a list of search results that match the lucene query.", response = String.class)
  public SearchResultList findConceptsForQuery(
    @ApiParam(value = "Terminology, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. 20140731", required = true) @PathParam("terminologyVersion") String terminologyVersion,
    @ApiParam(value = "Query, e.g. 'sulfur'", required = true) @PathParam("string") String searchString,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (Content): /concept/query/" + searchString);

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.VIEWER))
        throw new WebApplicationException(
            Response
                .status(401)
                .entity(
                    "User does not have permissions to find the concepts by query.")
                .build());

      ContentService contentService = new ContentServiceJpa();
      String filteredSearchString =
          searchString + " terminology:" + terminology + " termionlogyVersion:"
              + terminologyVersion;
      SearchResultList sr =
          contentService.findConceptsForQuery(filteredSearchString,
              new PfsParameterJpa());
      contentService.close();
      return sr;

    } catch (Exception e) {
      handleException(e, "trying to find the concepts by query");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.rest.ContentServiceRest#findDescendantConcepts(java
   * .lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @GET
  @Path("/concept/{terminology}/{terminologyVersion}/{terminologyId}/descendants")
  @ApiOperation(value = "Find concept descendants.", notes = "Gets a list of search results for each descendant concept.", response = Concept.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public SearchResultList findDescendantConcepts(
    @ApiParam(value = "Concept terminology id, e.g. 102751005", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Concept terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. 20140731", required = true) @PathParam("terminologyVersion") String terminologyVersion,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (Content): /concept/" + terminology + "/"
            + terminologyVersion + "/" + terminologyId + "/descendants");

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.VIEWER))
        throw new WebApplicationException(
            Response
                .status(401)
                .entity(
                    "User does not have permissions to find the descendant concepts.")
                .build());

      ContentService contentService = new ContentServiceJpa();

      // want all descendants, do not use PFS parameter
      SearchResultList results =
          contentService.findDescendantConcepts(terminologyId, terminology,
              terminologyVersion, null);

      contentService.close();
      return results;

    } catch (Exception e) {
      handleException(e, "trying to find descendant concepts");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.rest.ContentServiceRest#findChildConcepts(java.lang
   * .String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @GET
  @Path("/concept/{terminology}/{terminologyVersion}/{terminologyId}/children")
  @ApiOperation(value = "Find concept children.", notes = "Gets a list of search results for each child concept.", response = Concept.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public SearchResultList findChildConcepts(
    @ApiParam(value = "Concept terminology id, e.g. 102751005", required = true) @PathParam("terminologyId") String id,
    @ApiParam(value = "Concept terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. 20140731", required = true) @PathParam("terminologyVersion") String terminologyVersion,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (Content): /concept/" + terminology + "/"
            + terminologyVersion + "/" + id.toString() + "/descendants");

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.VIEWER))
        throw new WebApplicationException(Response
            .status(401)
            .entity(
                "User does not have permissions to find the child concepts.")
            .build());

      ContentService contentService = new ContentServiceJpa();
      MetadataService metadataService = new MetadataServiceJpa();

      String isaId = "";
      Map<String, String> relTypesMap =
          metadataService.getHierarchicalRelationshipTypes(terminology,
              terminologyVersion);
      for (Map.Entry<String, String> entry : relTypesMap.entrySet()) {
        if (entry.getValue().toLowerCase().startsWith("is"))
          isaId = entry.getKey();
      }

      SearchResultList results = new SearchResultListJpa();

      // get the concept and add it as first element of concept list
      Concept concept =
          contentService.getConcept(id.toString(), terminology,
              terminologyVersion);

      // if no concept, return empty list
      if (concept == null) {
        return results;
      }

      // cycle over relationships
      for (Relationship rel : concept.getInverseRelationships()) {

        if (rel.isActive() && rel.getTypeId().equals(new Long(isaId))
            && rel.getSourceConcept().isActive()) {

          Concept c = rel.getSourceConcept();

          SearchResult sr = new SearchResultJpa();
          sr.setId(c.getId());
          sr.setTerminologyId(c.getTerminologyId());
          sr.setTerminology(c.getTerminology());
          sr.setTerminologyVersion(c.getTerminologyVersion());
          sr.setValue(c.getDefaultPreferredName());

          // add search result to list
          results.addSearchResult(sr);
        }
      }

      metadataService.close();
      contentService.close();
      return results;

    } catch (Exception e) {
      handleException(e, "trying to find the child concepts");
      return null;
    }
  }

}
