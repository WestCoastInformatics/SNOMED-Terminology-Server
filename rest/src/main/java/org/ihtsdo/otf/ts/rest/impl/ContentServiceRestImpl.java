package org.ihtsdo.otf.ts.rest.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
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
import org.ihtsdo.otf.ts.ReleaseInfo;
import org.ihtsdo.otf.ts.UserRole;
import org.ihtsdo.otf.ts.helpers.AssociationReferenceRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.AttributeValueRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ComplexMapRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.DescriptionTypeRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.LanguageRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ModuleDependencyRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.RefsetDescriptorRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.RelationshipList;
import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.helpers.SimpleMapRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.SimpleRefSetMemberList;
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
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
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
@Consumes({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
@Produces({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
@Api(value = "/content", description = "Operations to retrieve RF2 content for a terminology.")
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
  public ConceptList getConcepts(
    @ApiParam(value = "Concept terminology id, e.g. 102751005", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Concept terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
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
  @Path("/concept/{terminology}/{version}/{terminologyId}")
  @ApiOperation(value = "Get concept by id, terminology, and version", notes = "Gets the concept for the specified parameters. It assumes there is only one which may not be the case during dual independent review.", response = Concept.class)
  public Concept getSingleConcept(
    @ApiParam(value = "Concept terminology id, e.g. 102751005", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Concept terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
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
   * @see
   * org.ihtsdo.otf.mapping.rest.ContentServiceRest#findConceptsForQuery(java
   * .lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @POST
  @Path("/concepts/{terminology}/{version}/query/{query}")
  @ApiOperation(value = "Find concepts matching a search query.", notes = "Gets a list of search results that match the lucene query.", response = String.class)
  public SearchResultList findConceptsForQuery(
    @ApiParam(value = "Terminology, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Query, e.g. 'sulfur'", required = true) @PathParam("query") String query,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
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

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getChildConcepts(java.lang.String
   * , java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  @POST
  @Path("/concepts/{terminology}/{version}/{terminologyId}/children")
  @ApiOperation(value = "Get children", notes = "Gets the child concepts for the specified id.", response = ConceptList.class)
  public ConceptList findChildConcepts(
    @ApiParam(value = "Concept terminology id, e.g. 102751005", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Concept terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /concept/" + terminology + "/" + version + "/"
            + terminologyId + "/children");

    try {
      authenticate(securityService, authToken, "retrieve the child concepts",
          UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      Concept concept =
          contentService.getSingleConcept(terminologyId, terminology, version);
      ConceptList list = contentService.findChildConcepts(concept, pfs);
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

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getDescendantConcepts(java.lang
   * .String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  @POST
  @Path("/concepts/{terminology}/{version}/{terminologyId}/descendants")
  @ApiOperation(value = "Get descendants", notes = "Gets the descendant concepts for the specified id.", response = ConceptList.class)
  public ConceptList findDescendantConcepts(
    @ApiParam(value = "Concept terminology id, e.g. 102751005", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Concept terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /concept/" + terminology + "/" + version + "/"
            + terminologyId + "/descendants");

    try {
      authenticate(securityService, authToken,
          "retrieve the descendant concepts", UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      Concept concept =
          contentService.getSingleConcept(terminologyId, terminology, version);
      ConceptList list = contentService.findDescendantConcepts(concept, pfs);
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

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getAncestorConcepts(java.lang
   * .String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  @POST
  @Path("/concepts/{terminology}/{version}/{terminologyId}/ancestors")
  @ApiOperation(value = "Get ancestors", notes = "Gets the ancestor concepts for the specified id.", response = ConceptList.class)
  public ConceptList findAncestorConcepts(
    @ApiParam(value = "Concept terminology id, e.g. 102751005", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Concept terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /concept/" + terminology + "/" + version + "/"
            + terminologyId + "/ancestors");

    try {
      authenticate(securityService, authToken,
          "retrieve the ancestor concepts", UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      Concept concept =
          contentService.getSingleConcept(terminologyId, terminology, version);
      ConceptList list = contentService.findAncestorConcepts(concept, pfs);
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
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getDescription(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @GET
  @Path("/description/{terminology}/{version}/{terminologyId}")
  @ApiOperation(value = "Get description by id, terminology, and version", notes = "Gets the description for the specified parameters. It assumes there is only one which may not be the case during dual independent review.", response = Description.class)
  public Description getDescription(
    @ApiParam(value = "Description terminology id, e.g. 100114019", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Description terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Description terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
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
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#findAssociationReferenceRefSetMembers
   * (java.lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  @POST
  @Path("/associationReferenceMember/refSet/{terminology}/{version}/{refSetId}")
  @ApiOperation(value = "Find association reference refset members for a refset id.", notes = "Finds all association reference refset members for the specified parameters.", response = AssociationReferenceRefSetMemberList.class)
  public AssociationReferenceRefSetMemberList findAssociationReferenceRefSetMembers(
    @ApiParam(value = "Refset terminology id, e.g. 100114019", required = true) @PathParam("refSetId") String refSetId,
    @ApiParam(value = "Terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /associationReferenceMember/refSet/"
            + terminology + "/" + version + "/" + refSetId);

    try {
      authenticate(securityService, authToken,
          "retrieve association reference refset members", UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      AssociationReferenceRefSetMemberList result =
          contentService.findAssociationReferenceRefSetMembers(refSetId,
              terminology, version, pfs);
      contentService.close();
      return result;
    } catch (Exception e) {
      handleException(e,
          "trying to retrieve association reference refset members");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#findAttributeValueRefSetMembers
   * (java.lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  @POST
  @Path("/attributeValueMember/refSet/{terminology}/{version}/{refSetId}")
  @ApiOperation(value = "Find attribute value refset members for a refset id.", notes = "Finds all attribute value refset members for the specified parameters.", response = AttributeValueRefSetMemberList.class)
  public AttributeValueRefSetMemberList findAttributeValueRefSetMembers(
    @ApiParam(value = "Refset terminology id, e.g. 100114019", required = true) @PathParam("refSetId") String refSetId,
    @ApiParam(value = "Terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /attributeValueMember/refSet/" + terminology
            + "/" + version + "/" + refSetId);

    try {
      authenticate(securityService, authToken,
          "retrieve attribute value refset members", UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      AttributeValueRefSetMemberList result =
          contentService.findAttributeValueRefSetMembers(refSetId, terminology,
              version, pfs);
      contentService.close();
      return result;
    } catch (Exception e) {
      handleException(e, "trying to retrieve attribute value refset members");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#findComplexMapRefSetMembers(java
   * .lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  @POST
  @Path("/complexMapMember/refSet/{terminology}/{version}/{refSetId}")
  @ApiOperation(value = "Find complex map refset members for a refset id.", notes = "Finds all complex map refset members for the specified parameters.", response = ComplexMapRefSetMemberList.class)
  public ComplexMapRefSetMemberList findComplexMapRefSetMembers(
    @ApiParam(value = "Refset terminology id, e.g. 100114019", required = true) @PathParam("refSetId") String refSetId,
    @ApiParam(value = "Terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /complexMapMember/refSet/" + terminology + "/"
            + version + "/" + refSetId);

    try {
      authenticate(securityService, authToken,
          "retrieve complex map refset members", UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      ComplexMapRefSetMemberList result =
          contentService.findComplexMapRefSetMembers(refSetId, terminology,
              version, pfs);
      contentService.close();
      return result;
    } catch (Exception e) {
      handleException(e, "trying to retrieve complex map refset members");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#findDescriptionTypeRefSetMembers
   * (java.lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  @POST
  @Path("/descriptionTypeMember/refSet/{terminology}/{version}/{refSetId}")
  @ApiOperation(value = "Find description type refset members for a refset id.", notes = "Finds all description type refset members for the specified parameters.", response = DescriptionTypeRefSetMemberList.class)
  public DescriptionTypeRefSetMemberList findDescriptionTypeRefSetMembers(
    @ApiParam(value = "Refset terminology id, e.g. 100114019", required = true) @PathParam("refSetId") String refSetId,
    @ApiParam(value = "Terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /descriptionTypeMember/refSet/" + terminology
            + "/" + version + "/" + refSetId);

    try {
      authenticate(securityService, authToken,
          "retrieve description type refset members", UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      DescriptionTypeRefSetMemberList result =
          contentService.findDescriptionTypeRefSetMembers(refSetId,
              terminology, version, pfs);
      contentService.close();
      return result;
    } catch (Exception e) {
      handleException(e, "trying to retrieve description type refset members");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#findLanguageRefSetMembers(java
   * .lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  @POST
  @Path("/languageMember/refSet/{terminology}/{version}/{refSetId}")
  @ApiOperation(value = "Find language refset members for a refset id.", notes = "Finds all language refset members for the specified parameters.", response = LanguageRefSetMemberList.class)
  public LanguageRefSetMemberList findLanguageRefSetMembers(
    @ApiParam(value = "Refset terminology id, e.g. 100114019", required = true) @PathParam("refSetId") String refSetId,
    @ApiParam(value = "Terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /languageMember/refSet/" + terminology + "/"
            + version + "/" + refSetId);

    try {
      authenticate(securityService, authToken,
          "retrieve language refset members", UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      LanguageRefSetMemberList result =
          contentService.findLanguageRefSetMembers(refSetId, terminology,
              version, pfs);
      contentService.close();
      return result;
    } catch (Exception e) {
      handleException(e, "trying to retrieve language refset members");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.ContentServiceRest#
   * getModuleDependencyRefSetMembersForModule(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @GET
  @Path("/moduleDependencyMember/module/{terminology}/{version}/{moduleId}")
  @ApiOperation(value = "Find module dependency refset members for a module id.", notes = "Finds all module dependency refset members for the specified parameters.", response = ModuleDependencyRefSetMemberList.class)
  public ModuleDependencyRefSetMemberList getModuleDependencyRefSetMembersForModule(
    @ApiParam(value = "Refset terminology id, e.g. 100114019", required = true) @PathParam("moduleId") String moduleId,
    @ApiParam(value = "Terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /moduleDependencyMember/module/" + terminology
            + "/" + version + "/" + moduleId);

    try {
      authenticate(securityService, authToken,
          "retrieve module dependency refset members", UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      ModuleDependencyRefSetMemberList result =
          contentService.getModuleDependencyRefSetMembersForModule(moduleId,
              terminology, version);
      contentService.close();
      return result;
    } catch (Exception e) {
      handleException(e, "trying to retrieve module dependency refset members");
      return null;
    }
  }

  @Override
  @GET
  @Path("/refsetDescriptorMember/refSet/{terminology}/{version}/{refSetId}")
  @ApiOperation(value = "Find refset descriptor refset members for a refset id.", notes = "Finds all refset descriptor refset members for the specified parameters.", response = RefsetDescriptorRefSetMemberList.class)
  public RefsetDescriptorRefSetMemberList getRefsetDescriptorRefSetMembers(
    @ApiParam(value = "Refset terminology id, e.g. 100114019", required = true) @PathParam("refSetId") String refSetId,
    @ApiParam(value = "Terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /refsetDescriptorMember/refSet/" + terminology
            + "/" + version + "/" + refSetId);

    try {
      authenticate(securityService, authToken,
          "retrieve refset descriptor refset members", UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      RefsetDescriptorRefSetMemberList result =
          contentService.getRefsetDescriptorRefSetMembers(refSetId,
              terminology, version);
      contentService.close();
      return result;
    } catch (Exception e) {
      handleException(e, "trying to retrieve refset descriptor refset members");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#findSimpleMapRefSetMembers(java
   * .lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  @POST
  @Path("/simpleMapMember/refSet/{terminology}/{version}/{refSetId}")
  @ApiOperation(value = "Find simple map refset members for a refset id.", notes = "Finds all simple map refset members for the specified parameters.", response = SimpleMapRefSetMemberList.class)
  public SimpleMapRefSetMemberList findSimpleMapRefSetMembers(
    @ApiParam(value = "Refset terminology id, e.g. 100114019", required = true) @PathParam("refSetId") String refSetId,
    @ApiParam(value = "Terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /simpleMapMember/refSet/" + terminology + "/"
            + version + "/" + refSetId);

    try {
      authenticate(securityService, authToken,
          "retrieve simple map refset members", UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      SimpleMapRefSetMemberList result =
          contentService.findSimpleMapRefSetMembers(refSetId, terminology,
              version, pfs);
      contentService.close();
      return result;
    } catch (Exception e) {
      handleException(e, "trying to retrieve simple map refset members");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#findSimpleRefSetMembers(java.
   * lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  @POST
  @Path("/simpleMember/refSet/{terminology}/{version}/{refSetId}")
  @ApiOperation(value = "Find simple refset members for a refset id.", notes = "Finds all simple refset members for the specified parameters.", response = SimpleRefSetMemberList.class)
  public SimpleRefSetMemberList findSimpleRefSetMembers(
    @ApiParam(value = "Refset terminology id, e.g. 100114019", required = true) @PathParam("refSetId") String refSetId,
    @ApiParam(value = "Terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /simpleMember/refSet/" + terminology + "/"
            + version + "/" + refSetId);

    try {
      authenticate(securityService, authToken,
          "retrieve simple refset members", UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      SimpleRefSetMemberList result =
          contentService.findSimpleRefSetMembers(refSetId, terminology,
              version, pfs);
      contentService.close();
      return result;
    } catch (Exception e) {
      handleException(e, "trying to retrieve simple refset members");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.ContentServiceRest#
   * getAssociationReferenceRefSetMembersForConcept(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @GET
  @Path("/associationReferenceMember/concept/{terminology}/{version}/{terminologyId}")
  @ApiOperation(value = "Find association reference refset members for a concept id.", notes = "Finds all association reference refset members for the specified parameters.", response = AssociationReferenceRefSetMemberList.class)
  public AssociationReferenceRefSetMemberList getAssociationReferenceRefSetMembersForConcept(
    @ApiParam(value = "Concept id, e.g. 100114019", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /associationReferenceMember/CONCEPT/"
            + terminology + "/" + version + "/" + terminologyId);

    try {
      authenticate(securityService, authToken,
          "retrieve association reference concept refset members",
          UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      AssociationReferenceRefSetMemberList result =
          contentService.getAssociationReferenceRefSetMembersForConcept(
              terminologyId, terminology, version);
      contentService.close();
      return result;
    } catch (Exception e) {
      handleException(e,
          "trying to retrieve association reference concept refset members");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.ContentServiceRest#
   * getAssociationReferenceRefSetMembersForDescription(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @GET
  @Path("/associationReferenceMember/description/{terminology}/{version}/{terminologyId}")
  @ApiOperation(value = "Find association reference refset members for a description id.", notes = "Finds all association reference refset members for the specified parameters.", response = AssociationReferenceRefSetMemberList.class)
  public AssociationReferenceRefSetMemberList getAssociationReferenceRefSetMembersForDescription(
    @ApiParam(value = "Description id, e.g. 100114029", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /associationReferenceMember/description"
            + terminology + "/" + version + "/" + terminologyId);

    try {
      authenticate(securityService, authToken,
          "retrieve association reference description refset members",
          UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      AssociationReferenceRefSetMemberList result =
          contentService.getAssociationReferenceRefSetMembersForDescription(
              terminologyId, terminology, version);
      contentService.close();
      return result;
    } catch (Exception e) {
      handleException(e,
          "trying to retrieve association reference description refset members");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.ContentServiceRest#
   * getAttributeValueRefSetMembersForConcept(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @GET
  @Path("/attributeValueMember/concept/{terminology}/{version}/{terminologyId}")
  @ApiOperation(value = "Find attribute value refset members for a concept id.", notes = "Finds all attribute value refset members for the specified parameters.", response = AttributeValueRefSetMemberList.class)
  public AttributeValueRefSetMemberList getAttributeValueRefSetMembersForConcept(
    @ApiParam(value = "Concept id, e.g. 100114019", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /attributeValueMember/concept/" + terminology
            + "/" + version + "/" + terminologyId);

    try {
      authenticate(securityService, authToken,
          "retrieve attribute value concept refset members", UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      AttributeValueRefSetMemberList result =
          contentService.getAttributeValueRefSetMembersForConcept(
              terminologyId, terminology, version);
      contentService.close();
      return result;
    } catch (Exception e) {
      handleException(e,
          "trying to retrieve attribute value concept refset members");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.ContentServiceRest#
   * getAttributeValueRefSetMembersForDescription(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @GET
  @Path("/attributeValueMember/description/{terminology}/{version}/{terminologyId}")
  @ApiOperation(value = "Find attribute value refset members for a description id.", notes = "Finds all attribute value refset members for the specified parameters.", response = AttributeValueRefSetMemberList.class)
  public AttributeValueRefSetMemberList getAttributeValueRefSetMembersForDescription(
    @ApiParam(value = "Description id, e.g. 100114029", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /attributeValueMember/description/"
            + terminology + "/" + version + "/" + terminologyId);

    try {
      authenticate(securityService, authToken,
          "retrieve attribute value description refset members",
          UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      AttributeValueRefSetMemberList result =
          contentService.getAttributeValueRefSetMembersForDescription(
              terminologyId, terminology, version);
      contentService.close();
      return result;
    } catch (Exception e) {
      handleException(e,
          "trying to retrieve attribute value description refset members");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getComplexMapRefSetMembersForConcept
   * (java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @GET
  @Path("/complexMapMember/concept/{terminology}/{version}/{terminologyId}")
  @ApiOperation(value = "Find attribute value refset members for a concept id.", notes = "Finds all attribute value refset members for the specified parameters.", response = ComplexMapRefSetMemberList.class)
  public ComplexMapRefSetMemberList getComplexMapRefSetMembersForConcept(
    @ApiParam(value = "Concept id, e.g. 100114019", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /complexMapMember/concept/" + terminology
            + "/" + version + "/" + terminologyId);

    try {
      authenticate(securityService, authToken,
          "retrieve complex map refset members", UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      ComplexMapRefSetMemberList result =
          contentService.getComplexMapRefSetMembersForConcept(terminologyId,
              terminology, version);
      contentService.close();
      return result;
    } catch (Exception e) {
      handleException(e, "trying to retrieve complex map refset members");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.ContentServiceRest#
   * getLanguageRefSetMembersForDescription(java.lang.String, java.lang.String,
   * java.lang.String, java.lang.String)
   */
  @Override
  @GET
  @Path("/languageMember/description/{terminology}/{version}/{terminologyId}")
  @ApiOperation(value = "Find attribute value refset members for a description id.", notes = "Finds all attribute value refset members for the specified parameters.", response = LanguageRefSetMemberList.class)
  public LanguageRefSetMemberList getLanguageRefSetMembersForDescription(
    @ApiParam(value = "Description id, e.g. 100114029", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /languageMember/description/" + terminology
            + "/" + version + "/" + terminologyId);

    try {
      authenticate(securityService, authToken,
          "retrieve language refset members", UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      LanguageRefSetMemberList result =
          contentService.getLanguageRefSetMembersForDescription(terminologyId,
              terminology, version);
      contentService.close();
      return result;
    } catch (Exception e) {
      handleException(e, "trying to retrieve language refset members");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getSimpleMapRefSetMembersForConcept
   * (java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @GET
  @Path("/simpleMapMember/concept/{terminology}/{version}/{terminologyId}")
  @ApiOperation(value = "Find attribute value refset members for a concept id.", notes = "Finds all attribute value refset members for the specified parameters.", response = SimpleMapRefSetMemberList.class)
  public SimpleMapRefSetMemberList getSimpleMapRefSetMembersForConcept(
    @ApiParam(value = "Concept id, e.g. 100114019", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /simpleMapMember/concept/" + terminology + "/"
            + version + "/" + terminologyId);

    try {
      authenticate(securityService, authToken,
          "retrieve simple map refset members", UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      SimpleMapRefSetMemberList result =
          contentService.getSimpleMapRefSetMembersForConcept(terminologyId,
              terminology, version);
      contentService.close();
      return result;
    } catch (Exception e) {
      handleException(e, "trying to retrieve simple map refset members");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getSimpleRefSetMembersForConcept
   * (java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @GET
  @Path("/simpleMember/concept/{terminology}/{version}/{terminologyId}")
  @ApiOperation(value = "Find attribute value refset members for a concept id.", notes = "Finds all attribute value refset members for the specified parameters.", response = SimpleRefSetMemberList.class)
  public SimpleRefSetMemberList getSimpleRefSetMembersForConcept(
    @ApiParam(value = "Concept id, e.g. 100114019", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /simpleMember/concept/" + terminology + "/"
            + version + "/" + terminologyId);

    try {
      authenticate(securityService, authToken,
          "retrieve simple refset members", UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      SimpleRefSetMemberList result =
          contentService.getSimpleRefSetMembersForConcept(terminologyId,
              terminology, version);
      contentService.close();
      return result;
    } catch (Exception e) {
      handleException(e, "trying to retrieve simple refset members");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getInverseRelationshipsForConcept
   * (java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @GET
  @Path("/relationship/inverse/{terminology}/{version}/{terminologyId}")
  @ApiOperation(value = "Get inverse relationships for a concept id.", notes = "Gets inverse relationships for the specified parameters.", response = RelationshipList.class)
  public RelationshipList getInverseRelationshipsForConcept(
    @ApiParam(value = "Concept id, e.g. 100114019", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /relationship/inverse/" + terminology + "/"
            + version + "/" + terminologyId);

    try {
      authenticate(securityService, authToken,
          "retrieve inverse relationships", UserRole.VIEWER);

      ContentService contentService = new ContentServiceJpa();
      RelationshipList result =
          contentService.getInverseRelationshipsForConcept(terminologyId,
              terminology, version);
      contentService.close();
      return result;
    } catch (Exception e) {
      handleException(e, "trying to retrieve inverse relationships");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#luceneReindex(java.lang.String,
   * java.lang.String)
   */
  @Override
  @POST
  @Path("/reindex")
  @ApiOperation(value = "Reindexes specified objects", notes = "Recomputes lucene indexes for the specified comma-separated objects")
  public void luceneReindex(
    @ApiParam(value = "Comma-separated list of objects to reindex, e.g. ConceptJpa (optional)", required = false) String indexedObjects,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)

  throws Exception {
    Logger.getLogger(getClass()).info("test");
    Logger.getLogger(getClass()).info(
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
      Logger.getLogger(getClass()).info(
          "      elapsed time = " + getTotalElapsedTimeStr(startTimeOrig));
      Logger.getLogger(getClass()).info("done ...");

    } catch (Exception e) {
      Logger.getLogger(getClass()).info("ERROR:");
      e.printStackTrace();
      // handleException(e, "trying to reindex");
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#loadTerminologyClaml(java.lang
   * .String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @PUT
  @Path("/terminology/load/claml/{terminology}/{version}")
  @Consumes({
    MediaType.TEXT_PLAIN
  })
  @ApiOperation(value = "Loads ClaML terminology from file", notes = "Loads terminology from ClaML file, assigning specified version")
  public void loadTerminologyClaml(
    @ApiParam(value = "Terminology, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. 20140731", required = true) @PathParam("version") String version,
    @ApiParam(value = "ClaML input file", required = true) String inputFile,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful POST call (ContentChange): /terminology/load/claml/"
            + terminology + "/" + version + " from input file " + inputFile);

    // Track system level information
    long startTimeOrig = System.nanoTime();

    try {
      authenticate(securityService, authToken, "start editing cycle",
          UserRole.ADMINISTRATOR);

      // Load snapshot
      Logger.getLogger(getClass()).info("Load ClaML data from " + inputFile);
      ClamlLoaderAlgorithm algorithm = new ClamlLoaderAlgorithm();
      algorithm.setTerminology(terminology);
      algorithm.setTerminologyVersion(version);
      algorithm.setInputFile(inputFile);
      algorithm.compute();

      // Let service begin its own transaction
      Logger.getLogger(getClass()).info("Start computing transtive closure");
      TransitiveClosureAlgorithm algo = new TransitiveClosureAlgorithm();
      algo.setTerminology(terminology);
      algo.setTerminologyVersion(version);
      algo.reset();
      algo.compute();
      algo.close();

      // Final logging messages
      Logger.getLogger(getClass()).info(
          "      elapsed time = " + getTotalElapsedTimeStr(startTimeOrig));
      Logger.getLogger(getClass()).info("done ...");

    } catch (Exception e) {
      handleException(e, "trying to load terminology from ClaML file");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#loadTerminologyRf2Delta(java.
   * lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @PUT
  @Path("/terminology/load/rf2/delta/{terminology}")
  @Consumes({
    MediaType.TEXT_PLAIN
  })
  @ApiOperation(value = "Loads terminology RF2 delta from directory", notes = "Loads terminology RF2 delta from directory for specified terminology and version")
  public void loadTerminologyRf2Delta(
    @ApiParam(value = "Terminology, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "RF2 input directory", required = true) String inputDir,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful POST call (ContentChange): /terminology/load/rf2/delta"
            + terminology + " from input directory " + inputDir);

    // Track system level information
    long startTimeOrig = System.nanoTime();

    try {
      authenticate(securityService, authToken, "start editing cycle",
          UserRole.ADMINISTRATOR);

      Logger.getLogger(getClass()).info("Starting RF2 delta loader");
      Logger.getLogger(getClass()).info("  terminology = " + terminology);
      Logger.getLogger(getClass()).info("  inputDir = " + inputDir);

      // Check the input directory
      File inputDirFile = new File(inputDir);
      if (!inputDirFile.exists()) {
        throw new Exception("Specified input directory does not exist");
      }

      // Previous computation of terminology version is based on file name
      // but for delta/daily build files, this is not the current version
      // look up the current version instead
      MetadataService metadataService = new MetadataServiceJpa();
      final String version = metadataService.getLatestVersion(terminology);
      metadataService.close();
      if (version == null) {
        throw new Exception("Unable to determine terminology version.");
      }

      // Sort files
      Logger.getLogger(getClass()).info("  Sort RF2 Files");
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
      algorithm.close();

      // Compute transitive closure
      Logger.getLogger(getClass()).info(
          "  Compute transitive closure from  " + terminology + "/" + version);
      TransitiveClosureAlgorithm algo = new TransitiveClosureAlgorithm();
      algo.setTerminology(terminology);
      algo.setTerminologyVersion(version);
      algo.reset();
      algo.compute();

      // Clean-up
      readers.closeReaders();
      Logger.getLogger(getClass()).info("...done");

      // Final logging messages
      Logger.getLogger(getClass()).info(
          "      elapsed time = " + getTotalElapsedTimeStr(startTimeOrig));
      Logger.getLogger(getClass()).info("done ...");

    } catch (Exception e) {
      handleException(e, "trying to load terminology delta from RF2 directory");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#loadTerminologyRf2Full(java.lang
   * .String, java.lang.String, java.lang.String, java.lang.String)
   */
  @SuppressWarnings("resource")
  @Override
  @PUT
  @Path("/terminology/load/rf2/full/{terminology}/{version}")
  @Consumes({
    MediaType.TEXT_PLAIN
  })
  @ApiOperation(value = "Loads terminology RF2 full from directory", notes = "Loads terminology RF2 full from directory for specified terminology and version")
  public void loadTerminologyRf2Full(
    @ApiParam(value = "Terminology, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. 20140731", required = true) @PathParam("version") String version,
    @ApiParam(value = "RF2 input directory", required = true) String inputDir,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful POST call (ContentChange): /terminology/load/rf2/full/"
            + terminology + "/" + version + " from input file " + inputDir);

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
      Logger.getLogger(getClass()).info("  Get release versions");
      Rf2FileSorter sorter = new Rf2FileSorter();
      File conceptsFile =
          sorter.findFile(new File(inputDir, "Terminology"), "sct2_Concept");
      Set<String> releaseSet = new HashSet<>();
      BufferedReader reader = new BufferedReader(new FileReader(conceptsFile));
      String line;
      while ((line = reader.readLine()) != null) {
        final String fields[] = line.split("\t");
        if (!fields[1].equals("effectiveTime")) {
          try {
            ConfigUtility.DATE_FORMAT.parse(fields[1]);
          } catch (Exception e) {
            throw new Exception("Improperly formatted date found: " + fields[1]);
          }
          releaseSet.add(fields[1]);
        }
      }
      reader.close();
      List<String> releases = new ArrayList<>(releaseSet);
      Collections.sort(releases);

      // check that release info does not already exist
      HistoryService historyService = new HistoryServiceJpa();
      Logger.getLogger(getClass()).info("  Releases to process");
      for (String release : releases) {
        Logger.getLogger(getClass()).info("    release = " + release);

        ReleaseInfo releaseInfo =
            historyService.getReleaseInfo(terminology, release);
        if (releaseInfo != null) {
          throw new Exception("A release info already exists for " + release);
        }
      }
      historyService.close();

      // Sort files
      Logger.getLogger(getClass()).info("  Sort RF2 Files");
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
      algorithm.close();

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
        algorithm2.close();

      }

      // Compute transitive closure
      Logger.getLogger(getClass()).info(
          "  Compute transitive closure from  " + terminology + "/" + version);
      TransitiveClosureAlgorithm algo = new TransitiveClosureAlgorithm();
      algo.setTerminology(terminology);
      algo.setTerminologyVersion(version);
      algo.reset();
      algo.compute();

      //
      // Individual release infos will already be created by
      // snapshot and delta processes, so it is not needed here
      //

      // Clean-up
      readers.closeReaders();
      ConfigUtility
          .deleteDirectory(new File(inputDirFile, "/RF2-sorted-temp/"));

      // Final logging messages
      Logger.getLogger(getClass()).info(
          "      elapsed time = " + getTotalElapsedTimeStr(startTimeOrig));
      Logger.getLogger(getClass()).info("done ...");

    } catch (Exception e) {
      handleException(e, "trying to load full terminology from RF2 directory");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#loadTerminologyRf2Snapshot(java
   * .lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @PUT
  @Path("/terminology/load/rf2/snapshot/{terminology}/{version}")
  @Consumes({
    MediaType.TEXT_PLAIN
  })
  @ApiOperation(value = "Loads terminology RF2 snapshot from directory", notes = "Loads terminology RF2 snapshot from directory for specified terminology and version")
  public void loadTerminologyRf2Snapshot(
    @ApiParam(value = "Terminology, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. 20140731", required = true) @PathParam("version") String version,
    @ApiParam(value = "RF2 input directory", required = true) String inputDir,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass())
        .info(
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
      Logger.getLogger(getClass()).info("  Sort RF2 Files");
      Rf2FileSorter sorter = new Rf2FileSorter();
      sorter.setSortByEffectiveTime(false);
      sorter.setRequireAllFiles(true);
      File outputDir = new File(inputDirFile, "/RF2-sorted-temp/");
      sorter.sortFiles(inputDirFile, outputDir);
      String releaseVersion = sorter.getFileVersion();
      Logger.getLogger(getClass()).info("  releaseVersion = " + releaseVersion);

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
      algorithm.close();

      // Compute transitive closure
      Logger.getLogger(getClass()).info(
          "  Compute transitive closure from  " + terminology + "/" + version);
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
      Logger.getLogger(getClass()).info(
          "      elapsed time = " + getTotalElapsedTimeStr(startTimeOrig));
      Logger.getLogger(getClass()).info("done ...");

    } catch (Exception e) {
      handleException(e,
          "trying to load terminology snapshot from RF2 directory");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#computeTransitiveClosure(java
   * .lang.String, java.lang.String)
   */
  @Override
  @GET
  @Path("/terminology/closure/compute/{terminology}")
  @ApiOperation(value = "Computes terminology transitive closure", notes = "Computes transitive closure for the latest version of the specified terminology")
  public void computeTransitiveClosure(
    @ApiParam(value = "Terminology, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)

  throws Exception {

    Logger.getLogger(getClass()).info(
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
      Logger.getLogger(getClass()).info(
          "  Compute transitive closure from  " + terminology + "/" + version);
      TransitiveClosureAlgorithm algo = new TransitiveClosureAlgorithm();
      algo.setTerminology(terminology);
      algo.setTerminologyVersion(version);
      algo.reset();
      algo.compute();
      algo.close();

      // Final logging messages
      Logger.getLogger(getClass()).info(
          "      elapsed time = " + getTotalElapsedTimeStr(startTimeOrig));
      Logger.getLogger(getClass()).info("done ...");

    } catch (Exception e) {
      handleException(e, "trying to compute transitive closure");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#removeTerminology(java.lang.String
   * , java.lang.String, java.lang.String)
   */
  @Override
  @DELETE
  @Path("/terminology/remove/{terminology}/{version}")
  @ApiOperation(value = "Removes a terminology", notes = "Removes all elements for a specified terminology and version")
  public void removeTerminology(
    @ApiParam(value = "Terminology, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. 20140731", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
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
      Logger.getLogger(getClass()).info(
          "      elapsed time = " + getTotalElapsedTimeStr(startTimeOrig));
      Logger.getLogger(getClass()).info("done ...");

    } catch (Exception e) {
      handleException(e, "trying to load terminology from ClaML file");
    }
  }

}
