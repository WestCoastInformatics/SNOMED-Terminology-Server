package org.ihtsdo.otf.ts.rest.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.Project;
import org.ihtsdo.otf.ts.ReleaseInfo;
import org.ihtsdo.otf.ts.UserRole;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.ProjectList;
import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.jpa.ProjectJpa;
import org.ihtsdo.otf.ts.jpa.ReleaseInfoJpa;
import org.ihtsdo.otf.ts.jpa.algo.ClamlLoaderAlgorithm;
import org.ihtsdo.otf.ts.jpa.algo.LuceneReindexAlgorithm;
import org.ihtsdo.otf.ts.jpa.algo.Rf2DeltaLoaderAlgorithm;
import org.ihtsdo.otf.ts.jpa.algo.Rf2FileSorter;
import org.ihtsdo.otf.ts.jpa.algo.Rf2Readers;
import org.ihtsdo.otf.ts.jpa.algo.Rf2SnapshotLoaderAlgorithm;
import org.ihtsdo.otf.ts.jpa.algo.TransitiveClosureAlgorithm;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.HistoryServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.MetadataServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.SecurityServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.helper.TerminologyUtility;
import org.ihtsdo.otf.ts.rest.ContentServiceRest;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.services.ContentService;
import org.ihtsdo.otf.ts.services.HistoryService;
import org.ihtsdo.otf.ts.services.MetadataService;
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

  @Override
  @PUT
  @Path("/project/add")
  @ApiOperation(value = "Add new project", notes = "Creates a new project.", response = Project.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public Project addProject(
    @ApiParam(value = "Project, e.g. newProject", required = true) ProjectJpa project,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentChangeServiceRestImpl.class).info(
        "RESTful call PUT (ContentChange): /project/add " + project);
    Logger.getLogger(ContentChangeServiceRestImpl.class).debug(
        project.toString());

    try {
      authenticate(securityService, authToken, "add project", UserRole.AUTHOR);
      
      // Create service and configure transaction scope
      ContentService contentService = new ContentServiceJpa();

      // check to see if project already exists
      for (Project p : contentService.getProjects().getObjects()) {
        if (p.getName().equals(project.getName())
            && p.getDescription().equals(project.getDescription())) {
          throw new Exception(
              "A project with this name and description already exists.");
        }
      }
      
      contentService.setTransactionPerOperation(false);
      contentService.beginTransaction();

      // Add project
      Project newProject = contentService.addProject(project);

      // Commit, close, and return
      contentService.commit();
      contentService.close();
      return newProject;
    } catch (Exception e) {
      handleException(e, "trying to add a project");
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
            + "/query/" + query + " with PFS parameter "
            + (pfs == null ? "empty" : pfs.toString()));
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
  @POST
  @Path("/concepts/{terminology}/{version}/{terminologyId}/children")
  @ApiOperation(value = "Get children", notes = "Gets the child concepts for the specified id.", response = ConceptList.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public ConceptList getChildConcepts(
    @ApiParam(value = "Concept terminology id, e.g. 102751005", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Concept terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (Content): /concept/" + terminology + "/" + version + "/"
            + terminologyId + "/children");

    try {
      authenticate(securityService, authToken, "retrieve the child concepts",
          UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      Concept concept =
          contentService.getSingleConcept(terminologyId, terminology, version);
      ConceptList list = contentService.getChildConcepts(concept, pfs);
       for (Concept c : list.getObjects()) {
        contentService.getGraphResolutionHandler().resolve(
            c,
            TerminologyUtility.getHierarchcialIsaRels(c.getTerminology(),
                c.getTerminologyVersion()));
      }
      contentService.close();
      return list;
    } catch (Exception e) {
      handleException(e, "trying to retrieve child concepts");
      return null;
    }
  }

  @Override
  @POST
  @Path("/concepts/{terminology}/{version}/{terminologyId}/descendants")
  @ApiOperation(value = "Get descendants", notes = "Gets the descendant concepts for the specified id.", response = ConceptList.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public ConceptList getDescendantConcepts(
    @ApiParam(value = "Concept terminology id, e.g. 102751005", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Concept terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (Content): /concept/" + terminology + "/" + version + "/"
            + terminologyId + "/descendants");

    try {
      authenticate(securityService, authToken,
          "retrieve the descendant concepts", UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      Concept concept =
          contentService.getSingleConcept(terminologyId, terminology, version);
      ConceptList list = contentService.getDescendantConcepts(concept, pfs);
      for (Concept c : list.getObjects()) {
        contentService.getGraphResolutionHandler().resolve(
            c,
            TerminologyUtility.getHierarchcialIsaRels(c.getTerminology(),
                c.getTerminologyVersion()));
      }
      contentService.close();
      return list;
    } catch (Exception e) {
      handleException(e, "trying to retrieve descendant concepts");
      return null;
    }
  }

  @Override
  @POST
  @Path("/concepts/{terminology}/{version}/{terminologyId}/ancestors")
  @ApiOperation(value = "Get ancestors", notes = "Gets the ancestor concepts for the specified id.", response = ConceptList.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public ConceptList getAncestorConcepts(
    @ApiParam(value = "Concept terminology id, e.g. 102751005", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Concept terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (Content): /concept/" + terminology + "/" + version + "/"
            + terminologyId + "/ancestors");

    try {
      authenticate(securityService, authToken,
          "retrieve the ancestor concepts", UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      Concept concept =
          contentService.getSingleConcept(terminologyId, terminology, version);
      ConceptList list = contentService.getAncestorConcepts(concept, pfs);
      for (Concept c : list.getObjects()) {
        contentService.getGraphResolutionHandler().resolve(
            c,
            TerminologyUtility.getHierarchcialIsaRels(c.getTerminology(),
                c.getTerminologyVersion()));
      }
      contentService.close();
      return list;
    } catch (Exception e) {
      handleException(e, "trying to retrieve ancestor concepts");
      return null;
    }
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
      handleException(e,
          "trying to retrieve a association reference refset member");
      return null;
    }
  }

  @Override
  @GET
  @Path("/project/id/{id}/scope")
  @ApiOperation(value = "Get project scope for the project id", notes = "Gets all concpets in scope for this project.", response = ConceptList.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public ConceptList getConceptsInScope(
    @ApiParam(value = "Project internal id, e.g. 2", required = true) @PathParam("id") Long id,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (Content): /project/" + id + "/scope");

    try {
      authenticate(securityService, authToken, "get project scope",
          UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      ConceptList list =
          contentService.getConceptsInScope(contentService.getProject(id));
      contentService.close();
      return list;
    } catch (Exception e) {
      handleException(e, "trying to retrieve scope concepts for project " + id);
      return null;
    }
  }

  @Override
  @GET
  @Path("/project/id/{id}")
  @ApiOperation(value = "Get project for id", notes = "Gets the project for the specified id.", response = ConceptList.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public Project getProject(
    @ApiParam(value = "Project internal id, e.g. 2", required = true) @PathParam("id") Long id,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {
    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (Content): /project/id/" + id);

    try {
      authenticate(securityService, authToken, "retrieve the project",
          UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      Project project = contentService.getProject(id);
      contentService.close();
      return project;
    } catch (Exception e) {
      handleException(e, "trying to retrieve a project");
      return null;
    }
  }

  @Override
  @GET
  @Path("/project/projects}")
  @ApiOperation(value = "Get all projects", notes = "Gets all projects.", response = ConceptList.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public ProjectList getProjects(
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {
    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (Content): /project/projects");

    try {
      authenticate(securityService, authToken, "retrieve projects",
          UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      ProjectList list = contentService.getProjects();
      contentService.close();
      return list;
    } catch (Exception e) {
      handleException(e, "trying to retrieve the projects");
      return null;
    }
  }

  @Override
  @POST
  @Path("/reindex")
  @ApiOperation(value = "Reindexes specified objects", notes = "Recomputes lucene indexes for the specified comma-separated objects")
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public void luceneReindex(
    @ApiParam(value = "Comma-separated list of objects to reindex, e.g. ConceptJpa (optional)", required = false) String indexedObjects,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)

  throws Exception {
    Logger.getLogger(ContentServiceRestImpl.class).info("test");
    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful POST call (ContentChange): /reindex "
            + (indexedObjects == null ? "with no objects specified"
                : "with specified objects " + indexedObjects));

    // Track system level information
    long startTimeOrig = System.nanoTime();

    try {

      authenticate(securityService, authToken, "reindex",
          UserRole.ADMINISTRATOR);

      LuceneReindexAlgorithm algo = new LuceneReindexAlgorithm();

      algo.setIndexedObjects(indexedObjects);

      algo.compute();

      // Final logging messages
      Logger.getLogger(ContentServiceRestImpl.class).info(
          "      elapsed time = " + getTotalElapsedTimeStr(startTimeOrig));
      Logger.getLogger(ContentServiceRestImpl.class).info("done ...");

    } catch (Exception e) {
      Logger.getLogger(ContentServiceRestImpl.class).info("ERROR:");
      e.printStackTrace();
      // handleException(e, "trying to reindex");
    }

  }

  @Override
  @POST
  @Path("/terminology/load/claml/{terminology}/{version}")
  @ApiOperation(value = "Loads ClaML terminology from file", notes = "Loads terminology from ClaML file, assigning specified version")
  public void loadTerminologyClaml(
    @ApiParam(value = "Terminology, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. 20140731", required = true) @PathParam("version") String version,
    @ApiParam(value = "ClaML input file", required = true) String inputFile,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful POST call (ContentChange): /terminology/load/claml/"
            + terminology + "/" + version + " from input file "
            + inputFile);

    // Track system level information
    long startTimeOrig = System.nanoTime();

    try {
      authenticate(securityService, authToken, "start editing cycle",
          UserRole.ADMINISTRATOR);

      // Load snapshot
      Logger.getLogger(ContentServiceJpa.class).info(
          "Load ClaML data from " + inputFile);
      ClamlLoaderAlgorithm algorithm = new ClamlLoaderAlgorithm();
      algorithm.setTerminology(terminology);
      algorithm.setTerminologyVersion(version);
      algorithm.setInputFile(inputFile);
      algorithm.compute();

      // Let service begin its own transaction
      Logger.getLogger(ContentServiceJpa.class).info(
          "Start computing transtive closure");
      TransitiveClosureAlgorithm algo = new TransitiveClosureAlgorithm();
      algo.setTerminology(terminology);
      algo.setTerminologyVersion(version);
      algo.reset();
      algo.compute();
      algo.close();

      // Final logging messages
      Logger.getLogger(ContentServiceRestImpl.class).info(
          "      elapsed time = " + getTotalElapsedTimeStr(startTimeOrig));
      Logger.getLogger(ContentServiceRestImpl.class).info("done ...");

    } catch (Exception e) {
      handleException(e, "trying to load terminology from ClaML file");
    }
  }

  @Override
  @POST
  @Path("/terminology/load/rf2/delta/{terminology}")
  @ApiOperation(value = "Loads terminology RF2 delta from directory", notes = "Loads terminology RF2 delta from directory for specified terminology and version")
  public void loadTerminologyRf2Delta(
    @ApiParam(value = "Terminology, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "RF2 input directory", required = true) String inputDir,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful POST call (ContentChange): /terminology/load/rf2/delta"
            + terminology + " from input directory " + inputDir);

    // Track system level information
    long startTimeOrig = System.nanoTime();

    try {
      authenticate(securityService, authToken, "start editing cycle",
          UserRole.ADMINISTRATOR);

      Logger.getLogger(ContentServiceJpa.class).info(
          "Starting RF2 delta loader");
      Logger.getLogger(ContentServiceJpa.class).info(
          "  terminology = " + terminology);
      Logger.getLogger(ContentServiceJpa.class)
          .info("  inputDir = " + inputDir);

      // Check the input directory
      File inputDirFile = new File(inputDir);
      if (!inputDirFile.exists()) {
        throw new Exception("Specified input directory does not exist");
      }

      // Previous computation of terminology version is based on file name
      // but for delta/daily build files, this is not the current version
      // look up the current version instead
      MetadataService metadataService = new MetadataServiceJpa();
      final String version =
          metadataService.getLatestVersion(terminology);
      metadataService.close();
      if (version == null) {
        throw new Exception("Unable to determine terminology version.");
      }

      //
      // Verify that there is a release info for this version that is
      // marked as "isPlanned"
      //
      HistoryService historyService = new HistoryServiceJpa();
      ReleaseInfo releaseInfo =
          historyService.getReleaseInfo(terminology, version);
      if (releaseInfo == null) {
        throw new Exception("A release info must exist for "
            + version);
      } else if (!releaseInfo.isPlanned()) {
        throw new Exception("Release info for " + version
            + " is not marked as planned'");
      } else if (releaseInfo.isPublished()) {
        throw new Exception("Release info for " + version
            + " is marked as published");
      }
      historyService.close();

      // Sort files
      Logger.getLogger(ContentServiceJpa.class).info("  Sort RF2 Files");
      Rf2FileSorter sorter = new Rf2FileSorter();
      sorter.setSortByEffectiveTime(false);
      sorter.setRequireAllFiles(false);
      File outputDir = new File(inputDirFile, "/RF2-sorted-temp/");
      sorter.sortFiles(inputDirFile, outputDir);

      // Open readers
      Rf2Readers readers = new Rf2Readers(outputDir);
      readers.openReaders();

      // Load delta
      Rf2DeltaLoaderAlgorithm algorithm = new Rf2DeltaLoaderAlgorithm();
      algorithm.setTerminology(terminology);
      algorithm.setTerminologyVersion(version);
      algorithm.setReleaseVersion(sorter.getFileVersion());
      algorithm.setReaders(readers);
      algorithm.compute();

      // Compute transitive closure
      Logger.getLogger(this.getClass()).info(
          "  Compute transitive closure from  " + terminology + "/"
              + version);
      TransitiveClosureAlgorithm algo = new TransitiveClosureAlgorithm();
      algo.setTerminology(terminology);
      algo.setTerminologyVersion(version);
      algo.reset();
      algo.compute();

      // No changes to release info

      // Clean-up
      readers.closeReaders();
      Logger.getLogger(ContentServiceJpa.class).info("...done");

      // Final logging messages
      Logger.getLogger(ContentServiceRestImpl.class).info(
          "      elapsed time = " + getTotalElapsedTimeStr(startTimeOrig));
      Logger.getLogger(ContentServiceRestImpl.class).info("done ...");

    } catch (Exception e) {
      handleException(e, "trying to load terminology delta from RF2 directory");
    }
  }

  @Override
  @POST
  @Path("/terminology/load/rf2/full/{terminology}/{version}")
  @ApiOperation(value = "Loads terminology RF2 full from directory", notes = "Loads terminology RF2 full from directory for specified terminology and version")
  public void loadTerminologyRf2Full(
    @ApiParam(value = "Terminology, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. 20140731", required = true) @PathParam("version") String version,
    @ApiParam(value = "RF2 input directory", required = true) String inputDir,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful POST call (ContentChange): /terminology/load/rf2/full/"
            + terminology + "/" + version + " from input file "
            + inputDir);

    // Track system level information
    long startTimeOrig = System.nanoTime();

    try {
      authenticate(securityService, authToken, "start editing cycle",
          UserRole.ADMINISTRATOR);

      // Check the input directory
      File inputDirFile = new File(inputDir);
      if (!inputDirFile.exists()) {
        throw new Exception("Specified input directory does not exist");
      }

      // Get the release versions
      Logger.getLogger(ContentServiceJpa.class).info("  Get release versions");
      Rf2FileSorter sorter = new Rf2FileSorter();
      File conceptsFile = sorter.findFile(new File(inputDir), "sct2_Concept");
      Set<String> releaseSet = new HashSet<>();
      BufferedReader reader = new BufferedReader(new FileReader(conceptsFile));
      String line;
      while ((line = reader.readLine()) != null) {
        final String fields[] = line.split("\t");
        releaseSet.add(fields[1]);
      }
      reader.close();
      List<String> releases = new ArrayList<>(releaseSet);
      Collections.sort(releases);

      // check that release info does not already exist
      HistoryService historyService = new HistoryServiceJpa();
      for (String release : releases) {
        ReleaseInfo releaseInfo =
            historyService.getReleaseInfo(terminology, release);
        if (releaseInfo != null) {
          throw new Exception("A release info already exists for " + release);
        }
      }

      // Sort files
      Logger.getLogger(ContentServiceJpa.class).info("  Sort RF2 Files");
      sorter = new Rf2FileSorter();
      sorter.setSortByEffectiveTime(true);
      sorter.setRequireAllFiles(true);
      File outputDir = new File(inputDirFile, "/RF2-sorted-temp/");
      sorter.sortFiles(inputDirFile, outputDir);

      // Open readers
      Rf2Readers readers = new Rf2Readers(outputDir);
      readers.openReaders();

      // Load initial snapshot - first release version
      Rf2SnapshotLoaderAlgorithm algorithm = new Rf2SnapshotLoaderAlgorithm();
      algorithm.setTerminology(terminology);
      algorithm.setTerminologyVersion(version);
      algorithm.setReleaseVersion(releases.get(0));
      algorithm.setReaders(readers);
      algorithm.compute();

      // Load deltas
      for (String release : releases) {
        if (release.equals(releases.get(0))) {
          continue;
        }

        Rf2DeltaLoaderAlgorithm algorithm2 = new Rf2DeltaLoaderAlgorithm();
        algorithm2.setTerminology(terminology);
        algorithm2.setTerminologyVersion(version);
        algorithm2.setReleaseVersion(release);
        algorithm2.setReaders(readers);
        algorithm2.compute();

      }

      // Compute transitive closure
      Logger.getLogger(this.getClass()).info(
          "  Compute transitive closure from  " + terminology + "/"
              + version);
      TransitiveClosureAlgorithm algo = new TransitiveClosureAlgorithm();
      algo.setTerminology(terminology);
      algo.setTerminologyVersion(version);
      algo.reset();
      algo.compute();

      //
      // Create ReleaseInfo for each release, unless already exists
      //
      for (String release : releases) {
        ReleaseInfo info = historyService.getReleaseInfo(terminology, release);
        if (info != null) {
          info.setName(release);
          info.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(release));
          info.setDescription(terminology + " " + release + " release");
          info.setPlanned(false);
          info.setPublished(true);
          info.setReleaseBeginDate(info.getEffectiveTime());
          info.setReleaseFinishDate(info.getEffectiveTime());
          info.setTerminology(terminology);
          info.setTerminologyVersion(version);
          historyService.addReleaseInfo(info);
        }
      }

      // Clean-up
      readers.closeReaders();
      ConfigUtility
          .deleteDirectory(new File(inputDirFile, "/RF2-sorted-temp/"));

      // Final logging messages
      Logger.getLogger(ContentServiceRestImpl.class).info(
          "      elapsed time = " + getTotalElapsedTimeStr(startTimeOrig));
      Logger.getLogger(ContentServiceRestImpl.class).info("done ...");

    } catch (Exception e) {
      handleException(e, "trying to load full terminology from RF2 directory");
    }
  }

  @Override
  @POST
  @Path("/terminology/load/rf2/snapshot/{terminology}/{version}")
  @ApiOperation(value = "Loads terminology RF2 snapshot from directory", notes = "Loads terminology RF2 snapshot from directory for specified terminology and version")
  public void loadTerminologyRf2Snapshot(
    @ApiParam(value = "Terminology, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. 20140731", required = true) @PathParam("version") String version,
    @ApiParam(value = "RF2 input directory", required = true) String inputDir,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful POST call (ContentChange): /terminology/load/rf2/snapshot"
            + terminology + "/" + version + " from input directory "
            + inputDir);

    // Track system level information
    long startTimeOrig = System.nanoTime();

    try {
      authenticate(securityService, authToken, "start editing cycle",
          UserRole.ADMINISTRATOR);

      // Check the input directory
      File inputDirFile = new File(inputDir);
      if (!inputDirFile.exists()) {
        throw new Exception("Specified input directory does not exist");
      }

      // Sort files
      Logger.getLogger(ContentServiceJpa.class).info("  Sort RF2 Files");
      Rf2FileSorter sorter = new Rf2FileSorter();
      sorter.setSortByEffectiveTime(false);
      sorter.setRequireAllFiles(true);
      File outputDir = new File(inputDirFile, "/RF2-sorted-temp/");
      sorter.sortFiles(inputDirFile, outputDir);
      String releaseVersion = sorter.getFileVersion();
      Logger.getLogger(ContentServiceJpa.class).info(
          "  releaseVersion = " + releaseVersion);

      // Open readers
      Rf2Readers readers = new Rf2Readers(outputDir);
      readers.openReaders();

      // Load snapshot
      Rf2SnapshotLoaderAlgorithm algorithm = new Rf2SnapshotLoaderAlgorithm();
      algorithm.setTerminology(terminology);
      algorithm.setTerminologyVersion(version);
      algorithm.setReleaseVersion(releaseVersion);
      algorithm.setReaders(readers);
      algorithm.compute();

      //
      // Create ReleaseInfo for this release if it does not already exist
      //
      HistoryService historyService = new HistoryServiceJpa();
      historyService.setLastModifiedFlag(false);
      ReleaseInfo info =
          historyService.getReleaseInfo(terminology, releaseVersion);
      if (info == null) {
        info = new ReleaseInfoJpa();
        info.setName(releaseVersion);
        info.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(releaseVersion));
        info.setDescription(terminology + " " + releaseVersion + " release");
        info.setPlanned(false);
        info.setPublished(true);
        info.setReleaseBeginDate(info.getEffectiveTime());
        info.setReleaseFinishDate(info.getEffectiveTime());
        info.setTerminology(terminology);
        info.setTerminologyVersion(version);
        historyService.addReleaseInfo(info);
      }
      historyService.close();

      // Compute transitive closure
      Logger.getLogger(this.getClass()).info(
          "  Compute transitive closure from  " + terminology + "/"
              + version);
      TransitiveClosureAlgorithm algo = new TransitiveClosureAlgorithm();
      algo.setTerminology(terminology);
      algo.setTerminologyVersion(version);
      algo.reset();
      algo.compute();

      // Clean-up
      readers.closeReaders();
      ConfigUtility
          .deleteDirectory(new File(inputDirFile, "/RF2-sorted-temp/"));

      // Final logging messages
      Logger.getLogger(ContentServiceRestImpl.class).info(
          "      elapsed time = " + getTotalElapsedTimeStr(startTimeOrig));
      Logger.getLogger(ContentServiceRestImpl.class).info("done ...");

    } catch (Exception e) {
      handleException(e,
          "trying to load terminology snapshot from RF2 directory");
    }
  }

  @Override
  @GET
  @Path("/terminology/closure/compute/{terminology}")
  @ApiOperation(value = "Computes terminology transitive closure", notes = "Computes transitive closure for the latest version of the specified terminology")
  public void computeTransitiveClosure(
    @ApiParam(value = "Terminology, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)

  throws Exception {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful POST call (ContentChange): /terminology/closure/compute/"
            + terminology);

    // Track system level information
    long startTimeOrig = System.nanoTime();

    try {
      authenticate(securityService, authToken, "compute transitive closure",
          UserRole.ADMINISTRATOR);

      // Compute transitive closure
      MetadataService metadataService = new MetadataServiceJpa();
      String version = metadataService.getLatestVersion(terminology);
      metadataService.close();

      // Compute transitive closure
      Logger.getLogger(ContentServiceRestImpl.class).info(
          "  Compute transitive closure from  " + terminology + "/"
              + version);
      TransitiveClosureAlgorithm algo = new TransitiveClosureAlgorithm();
      algo.setTerminology(terminology);
      algo.setTerminologyVersion(version);
      algo.reset();
      algo.compute();
      algo.close();

      // Final logging messages
      Logger.getLogger(ContentServiceRestImpl.class).info(
          "      elapsed time = " + getTotalElapsedTimeStr(startTimeOrig));
      Logger.getLogger(ContentServiceRestImpl.class).info("done ...");

    } catch (Exception e) {
      handleException(e, "trying to compute transitive closure");
    }
  }

  @Override
  @POST
  @Path("/terminology/remove/{terminology}/{version}")
  @ApiOperation(value = "Removes a terminology", notes = "Removes all elements for a specified terminology and version")
  public void removeTerminology(
    @ApiParam(value = "Terminology, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. 20140731", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful POST call (ContentChange): /terminology/remove/" + terminology
            + "/" + version);

    // Track system level information
    long startTimeOrig = System.nanoTime();

    try {
      authenticate(securityService, authToken, "start editing cycle",
          UserRole.ADMINISTRATOR);

      ContentService contentService = new ContentServiceJpa();
      contentService.clearConcepts(terminology, version);
      contentService.close();

      // Final logging messages
      Logger.getLogger(ContentServiceRestImpl.class).info(
          "      elapsed time = " + getTotalElapsedTimeStr(startTimeOrig));
      Logger.getLogger(ContentServiceRestImpl.class).info("done ...");

    } catch (Exception e) {
      handleException(e, "trying to load terminology from ClaML file");
    }
  }

}
