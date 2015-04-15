/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.rest.impl;

import java.util.HashSet;
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
import org.ihtsdo.otf.ts.ValidationResult;
import org.ihtsdo.otf.ts.helpers.AssociationReferenceRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.AttributeValueRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ComplexMapRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.DescriptionList;
import org.ihtsdo.otf.ts.helpers.DescriptionTypeRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.LanguageRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.LocalException;
import org.ihtsdo.otf.ts.helpers.ModuleDependencyRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.RefsetDescriptorRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.RelationshipList;
import org.ihtsdo.otf.ts.helpers.ReleaseInfoList;
import org.ihtsdo.otf.ts.helpers.SimpleMapRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.SimpleRefSetMemberList;
import org.ihtsdo.otf.ts.jpa.ReleaseInfoJpa;
import org.ihtsdo.otf.ts.jpa.algo.ReleaseRf2BeginAlgorithm;
import org.ihtsdo.otf.ts.jpa.algo.ReleaseRf2FinishAlgorithm;
import org.ihtsdo.otf.ts.jpa.algo.ReleaseRf2PerformAlgorithm;
import org.ihtsdo.otf.ts.jpa.algo.StartEditingCycleAlgorithm;
import org.ihtsdo.otf.ts.jpa.services.HistoryServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.SecurityServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.helper.TerminologyUtility;
import org.ihtsdo.otf.ts.rest.HistoryServiceRest;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceRefSetMember;
import org.ihtsdo.otf.ts.rf2.AttributeValueRefSetMember;
import org.ihtsdo.otf.ts.rf2.ComplexMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.DescriptionTypeRefSetMember;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.ModuleDependencyRefSetMember;
import org.ihtsdo.otf.ts.rf2.RefsetDescriptorRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.SimpleMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.SimpleRefSetMember;
import org.ihtsdo.otf.ts.services.HistoryService;
import org.ihtsdo.otf.ts.services.SecurityService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * REST implementation for {@link HistoryServiceRest}.
 */
@Path("/history")
@Api(value = "/history", description = "Operations to retrieve historical RF2 content for a terminology.")
@Consumes({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
@Produces({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
public class HistoryServiceRestImpl extends RootServiceRestImpl implements
    HistoryServiceRest {

  /** The security service. */
  private SecurityService securityService;

  /**
   * Instantiates an empty {@link HistoryServiceRestImpl}.
   *
   * @throws Exception the exception
   */
  public HistoryServiceRestImpl() throws Exception {
    securityService = new SecurityServiceJpa();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#findConceptsModifiedSinceDate
   * (java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  @POST
  @Path("/concept/{terminology}/{date:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}")
  @ApiOperation(value = "Get concepts modified since a date", notes = "Gets concepts changed since a date.", response = ConceptList.class)
  public ConceptList findConceptsModifiedSinceDate(
    @ApiParam(value = "Concept terminology , e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Date in the format YYYYMMDD , e.g. 20140731 or \"null\"", required = true) @PathParam("date") String date,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /concept/" + terminology + "/" + date);

    try {
      authenticate(securityService, authToken,
          "find the concepts modified since date", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      ConceptList cl =
          historyService.findConceptsModifiedSinceDate(terminology, (date
              .equals("null") ? null : ConfigUtility.DATE_FORMAT.parse(date)),
              pfs);

      // Lazy initialization errors happen without this

      for (Concept concept : cl.getObjects()) {
        if (concept != null) {
          historyService.getGraphResolutionHandler().resolve(
              concept,
              TerminologyUtility.getHierarchicalIsaRels(
                  concept.getTerminology(), concept.getTerminologyVersion()));
        }
      }

      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e, "trying to find concepts modified since date");
      return null;
    } finally {
      securityService.close();
    }

  }

  /**
   * Find concept revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the concept list
   */
  @Override
  @POST
  @Path("/concept/revisions/{id}/{startDate:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}/{endDate:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}/all")
  @ApiOperation(value = "Get concepts revisions in a date range", notes = "Gets all concept revisions in a date range. Use a null date to leave it open ended", response = ConceptList.class)
  public ConceptList findConceptRevisions(
    @ApiParam(value = "Concept unique id, i.e. 1", required = true) @PathParam("id") String id,
    @ApiParam(value = "Date in the format YYYYMMDD , e.g. 20140731 or \"null\"", required = true) @PathParam("startDate") String startDate,
    @ApiParam(value = "Date in the format YYYYMMDD , e.g. 20140731 or \"null\"", required = true) @PathParam("endDate") String endDate,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /concept/revisions/" + id + "/" + startDate
            + "/" + endDate + "/all");

    try {
      authenticate(securityService, authToken, "find the concept revisions",
          UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      ConceptList cl =
          historyService.findConceptRevisions(Long.valueOf(id),
              ConfigUtility.DATE_FORMAT.parse(startDate),
              ConfigUtility.DATE_FORMAT.parse(endDate), pfs);

      historyService.close();

      // Resolve to empty
      for (Concept concept : cl.getObjects()) {
        if (concept != null) {
          historyService.getGraphResolutionHandler().resolveEmpty(concept);
        }
      }
      return cl;
    } catch (Exception e) {
      handleException(e, "trying to find the concept revisions");
      return null;
    } finally {
      securityService.close();
    }
  }

  @Override
  @GET
  @Path("/concept/revisions/{id}/{release: [0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]}/release")
  @ApiOperation(value = "Get concepts release revision", notes = "Gets concept release revision.", response = Concept.class)
  public Concept findConceptReleaseRevision(
    @ApiParam(value = "Concept id, e.g. 2", required = true) @PathParam("id") String id,
    @ApiParam(value = "Release date in the format YYYYMMDD , e.g. 20140731", required = true) @PathParam("release") String release,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /concept/revisions/" + id + "/" + release
            + "/release");

    try {
      authenticate(securityService, authToken,
          "find the concept release revision", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      Concept concept =
          historyService.findConceptReleaseRevision(Long.valueOf(id),
              ConfigUtility.DATE_FORMAT.parse(release));

      historyService.close();

      // explicitly resolve to just the concept element
      historyService.getGraphResolutionHandler().resolveEmpty(concept);
      return concept;
    } catch (Exception e) {
      handleException(e, "trying to find the concept release revision");
      return null;
    } finally {
      securityService.close();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#findDescriptionsModifiedSinceDate
   * (java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  @POST
  @Path("/description/{terminology}/{date:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}")
  @ApiOperation(value = "Get descriptions modified since a date", notes = "Gets descriptions changed since a date.", response = DescriptionList.class)
  public DescriptionList findDescriptionsModifiedSinceDate(
    @ApiParam(value = "Description terminology , e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Date in the format YYYYMMDD , e.g. 20140731 or \"null\"", required = true) @PathParam("date") String date,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /description/" + terminology + "/" + date);

    try {
      authenticate(securityService, authToken,
          "find the descriptions modified since date", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      DescriptionList cl =
          historyService.findDescriptionsModifiedSinceDate(terminology,
              ConfigUtility.DATE_FORMAT.parse(date), pfs);

      // Lazy initialize
      for (Description description : cl.getObjects()) {
        if (description != null) {
          historyService.getGraphResolutionHandler().resolve(description);
        }
      }

      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e, "trying to find descriptions modified since date");
      return null;
    } finally {
      securityService.close();
    }

  }

  /**
   * Find description revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the description list
   */
  @Override
  @POST
  @Path("/description/revisions/{id}/{startDate:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}/{endDate:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}/all")
  @ApiOperation(value = "Get descriptions revisions in a date range", notes = "Gets all description revisions in a date range. Use a null date to leave it open ended", response = DescriptionList.class)
  public DescriptionList findDescriptionRevisions(
    @ApiParam(value = "Concept terminology , e.g. SNOMEDCT", required = true) @PathParam("id") String id,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("startDate") String startDate,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("endDate") String endDate,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /description/revisions/" + id + "/"
            + startDate + "/" + endDate + "/all");

    try {
      authenticate(securityService, authToken,
          "find the description revisions", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      DescriptionList dl =
          historyService.findDescriptionRevisions(Long.valueOf(id),
              ConfigUtility.DATE_FORMAT.parse(startDate),
              ConfigUtility.DATE_FORMAT.parse(endDate), pfs);

      historyService.close();

      // Resolve to empty
      for (Description description : dl.getObjects()) {
        if (description != null) {
          historyService.getGraphResolutionHandler().resolveEmpty(description);
        }
      }
      return dl;
    } catch (Exception e) {
      handleException(e, "trying to find the release revisions");
      return null;
    } finally {
      securityService.close();
    }
  }

  @Override
  @GET
  @Path("/description/revisions/{id}/{release: [0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]}/release")
  @ApiOperation(value = "Get descriptions release revision", notes = "Gets description release revision", response = Description.class)
  public Description findDescriptionReleaseRevision(
    @ApiParam(value = "Concept id , e.g. 2", required = true) @PathParam("id") String id,
    @ApiParam(value = "Release date in the format YYYYMMDD , e.g. latest", required = true) @PathParam("release") String release,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /description/revisions/" + id + "/" + release);

    try {
      authenticate(securityService, authToken,
          "find the description release revision", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      Description description =
          historyService.findDescriptionReleaseRevision(Long.valueOf(id),
              ConfigUtility.DATE_FORMAT.parse(release));


      historyService.close();

      // explicitly resolve to just the description element
      historyService.getGraphResolutionHandler().resolveEmpty(description);
      return description;
    } catch (Exception e) {
      handleException(e, "find the description release revision");
      return null;
    } finally {
      securityService.close();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#findRelationshipsModifiedSinceDate
   * (java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  @POST
  @Path("/relationship/{terminology}/{date:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}")
  @ApiOperation(value = "Get relationships modified since a date", notes = "Gets relationships changed since a date.", response = RelationshipList.class)
  public RelationshipList findRelationshipsModifiedSinceDate(
    @ApiParam(value = "Relationship terminology , e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("date") String date,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /relationship/" + terminology + "/" + date);

    try {
      authenticate(securityService, authToken,
          "find the relationships modified since date", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      RelationshipList cl =
          historyService.findRelationshipsModifiedSinceDate(terminology,
              ConfigUtility.DATE_FORMAT.parse(date), pfs);

      // Lazy initialize
      for (Relationship relationship : cl.getObjects()) {
        if (relationship != null) {
          historyService.getGraphResolutionHandler().resolve(relationship);
        }
      }

      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e, "trying to find relationships modified since date");
      return null;
    } finally {
      securityService.close();
    }

  }

  /**
   * Find relationship revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the relationship list
   */
  @Override
  @POST
  @Path("/relationship/revisions/{id}/{startDate:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}/{endDate:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}/all")
  @ApiOperation(value = "Get relationships revisions in a date range", notes = "Gets all relationship revisions in a date range. Use a null date to leave it open ended", response = RelationshipList.class)
  public RelationshipList findRelationshipRevisions(
    @ApiParam(value = "Concept terminology , e.g. SNOMEDCT", required = true) @PathParam("id") String id,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("startDate") String startDate,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("endDate") String endDate,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /relationship/revisions/" + id + "/"
            + startDate + "/" + endDate + "/all");

    try {
      authenticate(securityService, authToken, "find relationship revisions",
          UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      RelationshipList cl =
          historyService.findRelationshipRevisions(Long.valueOf(id),
              ConfigUtility.DATE_FORMAT.parse(startDate),
              ConfigUtility.DATE_FORMAT.parse(endDate), pfs);

      // explicitly do not want to use graph resolution handler.
      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e, "trying to find relationship revisions");
      return null;
    } finally {
      securityService.close();
    }
  }

  @Override
  @GET
  @Path("/relationship/revisions/{id}/{release: [0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]}/release")
  @ApiOperation(value = "Get relationships release revision", notes = "Gets relationship release revision", response = Relationship.class)
  public Relationship findRelationshipReleaseRevision(
    @ApiParam(value = "Concept id, e.g. 2", required = true) @PathParam("id") String id,
    @ApiParam(value = "Release date in the format YYYYMMDD , e.g. latest", required = true) @PathParam("release") String release,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass())
        .info(
            "RESTful call (History): /relationship/revisions/" + id + "/"
                + release);

    try {
      authenticate(securityService, authToken,
          "find relationship release revision", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      Relationship rel =
          historyService.findRelationshipReleaseRevision(Long.valueOf(id),
              ConfigUtility.DATE_FORMAT.parse(release));

      // explicitly do not want to use graph resolution handler.
      historyService.close();
      return rel;
    } catch (Exception e) {
      handleException(e, "trying to find relationship release revision");
      return null;
    } finally {
      securityService.close();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.HistoryServiceRest#
   * findLanguageRefSetMembersModifiedSinceDate(java.lang.String,
   * java.lang.String, org.ihtsdo.otf.ts.helpers.PfsParameterJpa,
   * java.lang.String)
   */
  @Override
  @POST
  @Path("/language/{terminology}/{date:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}")
  @ApiOperation(value = "Get language refset members modified since a date", notes = "Gets language refset members changed since a date.", response = LanguageRefSetMemberList.class)
  public LanguageRefSetMemberList findLanguageRefSetMembersModifiedSinceDate(
    @ApiParam(value = "LanguageRefSetMember terminology , e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("date") String date,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /language/" + terminology + "/" + date);

    try {
      authenticate(securityService, authToken,
          "find language refset members modified since date", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      LanguageRefSetMemberList cl =
          historyService.findLanguageRefSetMembersModifiedSinceDate(
              terminology, ConfigUtility.DATE_FORMAT.parse(date), pfs);

      // Lazy initialize
      for (LanguageRefSetMember member : cl.getObjects()) {
        if (member != null) {
          historyService.getGraphResolutionHandler().resolve(member);
        }
      }

      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e,
          "trying to find language refset members modified since date");
      return null;
    } finally {
      securityService.close();
    }

  }

  /**
   * Find languageRefSetMember revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the languageRefSetMember list
   */
  @Override
  @POST
  @Path("/language/revisions/{id}/{startDate:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}/{endDate:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}/all")
  @ApiOperation(value = "Get language refset members revisions in a date range", notes = "Gets all language refset members revisions in a date range. Use a null date to leave it open ended", response = LanguageRefSetMemberList.class)
  public LanguageRefSetMemberList findLanguageRefSetMemberRevisions(
    @ApiParam(value = "Concept terminology , e.g. SNOMEDCT", required = true) @PathParam("id") String id,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("startDate") String startDate,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("endDate") String endDate,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /language/revisions/" + id + "/" + startDate
            + "/" + endDate + "/all");

    try {
      authenticate(securityService, authToken,
          "find language refset member revisions", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      LanguageRefSetMemberList cl =
          historyService.findLanguageRefSetMemberRevisions(Long.valueOf(id),
              ConfigUtility.DATE_FORMAT.parse(startDate),
              ConfigUtility.DATE_FORMAT.parse(endDate), pfs);

      // explicitly do not want to use graph resolution handler.
      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e, "find language refset member revisions");
      return null;
    } finally {
      securityService.close();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.HistoryServiceRest#
   * findLanguageRefSetMemberReleaseRevisions(java.lang.String,
   * java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  @GET
  @Path("/language/revisions/{id}/{release: [0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]}/release")
  @ApiOperation(value = "Get language refset members release revision", notes = "Gets language refset members release revision", response = LanguageRefSetMember.class)
  public LanguageRefSetMember findLanguageRefSetMemberReleaseRevision(
    @ApiParam(value = "Concept id, e.g. 2", required = true) @PathParam("id") String id,
    @ApiParam(value = "Release date in the format YYYYMMDD , e.g. latest", required = true) @PathParam("release") String release,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /language/revisions/" + id + "/" + release);

    try {
      authenticate(securityService, authToken,
          "find language refset member release revision", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      LanguageRefSetMember member =
          historyService.findLanguageRefSetMemberReleaseRevision(
              Long.valueOf(id), ConfigUtility.DATE_FORMAT.parse(release));

      // explicitly do not want to use graph resolution handler.
      historyService.close();
      return member;
    } catch (Exception e) {
      handleException(e, "find language refset member release revision");
      return null;
    } finally {
      securityService.close();
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.HistoryServiceRest#
   * findAssociationReferenceRefSetMembersModifiedSinceDate(java.lang.String,
   * java.lang.String, org.ihtsdo.otf.ts.helpers.PfsParameterJpa,
   * java.lang.String)
   */
  @Override
  @POST
  @Path("/associationReference/{terminology}/{date:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}")
  @ApiOperation(value = "Get association reference refset members modified since a date", notes = "Gets association reference refset members changed since a date.", response = AssociationReferenceRefSetMemberList.class)
  public AssociationReferenceRefSetMemberList findAssociationReferenceRefSetMembersModifiedSinceDate(
    @ApiParam(value = "AssociationReferenceRefSetMember terminology , e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("date") String date,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /association reference/" + terminology + "/" + date);

    try {
      authenticate(securityService, authToken,
          "find association reference refset members modified since date", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      AssociationReferenceRefSetMemberList cl =
          historyService.findAssociationReferenceRefSetMembersModifiedSinceDate(
              terminology, ConfigUtility.DATE_FORMAT.parse(date), pfs);

      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e,
          "trying to find association reference refset members modified since date");
      return null;
    } finally {
      securityService.close();
    }

  }

  /**
   * Find association referenceRefSetMember revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the association referenceRefSetMember list
   */
  @Override
  @POST
  @Path("/associationReference/revisions/{id}/{startDate:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}/{endDate:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}/all")
  @ApiOperation(value = "Get association reference refset members revisions in a date range", notes = "Gets all association reference refset members revisions in a date range. Use a null date to leave it open ended", response = AssociationReferenceRefSetMemberList.class)
  public AssociationReferenceRefSetMemberList findAssociationReferenceRefSetMemberRevisions(
    @ApiParam(value = "Concept terminology , e.g. SNOMEDCT", required = true) @PathParam("id") String id,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("startDate") String startDate,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("endDate") String endDate,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /association reference/revisions/" + id + "/" + startDate
            + "/" + endDate + "/all");

    try {
      authenticate(securityService, authToken,
          "find association reference refset member revisions", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      AssociationReferenceRefSetMemberList cl =
          historyService.findAssociationReferenceRefSetMemberRevisions(Long.valueOf(id),
              ConfigUtility.DATE_FORMAT.parse(startDate),
              ConfigUtility.DATE_FORMAT.parse(endDate), pfs);

      // explicitly do not want to use graph resolution handler.
      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e, "find association reference refset member revisions");
      return null;
    } finally {
      securityService.close();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.HistoryServiceRest#
   * findAssociationReferenceRefSetMemberReleaseRevisions(java.lang.String,
   * java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  @GET
  @Path("/associationReference/revisions/{id}/{release: [0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]}/release")
  @ApiOperation(value = "Get association reference refset members release revision", notes = "Gets association reference refset members release revision", response = AssociationReferenceRefSetMember.class)
  public AssociationReferenceRefSetMember<?> findAssociationReferenceRefSetMemberReleaseRevision(
    @ApiParam(value = "Concept id, e.g. 2", required = true) @PathParam("id") String id,
    @ApiParam(value = "Release date in the format YYYYMMDD , e.g. latest", required = true) @PathParam("release") String release,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /association reference/revisions/" + id + "/" + release);

    try {
      authenticate(securityService, authToken,
          "find association reference refset member release revision", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      AssociationReferenceRefSetMember<?> member =
          historyService.findAssociationReferenceRefSetMemberReleaseRevision(
              Long.valueOf(id), ConfigUtility.DATE_FORMAT.parse(release));

      // explicitly do not want to use graph resolution handler.
      historyService.close();
      return member;
    } catch (Exception e) {
      handleException(e, "find association reference refset member release revision");
      return null;
    } finally {
      securityService.close();
    }
  }
  

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.HistoryServiceRest#
   * findAttributeValueRefSetMembersModifiedSinceDate(java.lang.String,
   * java.lang.String, org.ihtsdo.otf.ts.helpers.PfsParameterJpa,
   * java.lang.String)
   */
  @Override
  @POST
  @Path("/attributeValue/{terminology}/{date:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}")
  @ApiOperation(value = "Get attribute value refset members modified since a date", notes = "Gets attribute value refset members changed since a date.", response = AttributeValueRefSetMemberList.class)
  public AttributeValueRefSetMemberList findAttributeValueRefSetMembersModifiedSinceDate(
    @ApiParam(value = "AttributeValueRefSetMember terminology , e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("date") String date,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /attribute value/" + terminology + "/" + date);

    try {
      authenticate(securityService, authToken,
          "find attribute value refset members modified since date", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      AttributeValueRefSetMemberList cl =
          historyService.findAttributeValueRefSetMembersModifiedSinceDate(
              terminology, ConfigUtility.DATE_FORMAT.parse(date), pfs);

      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e,
          "trying to find attribute value refset members modified since date");
      return null;
    } finally {
      securityService.close();
    }

  }

  /**
   * Find attribute valueRefSetMember revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the attribute valueRefSetMember list
   */
  @Override
  @POST
  @Path("/attributeValue/revisions/{id}/{startDate:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}/{endDate:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}/all")
  @ApiOperation(value = "Get attribute value refset members revisions in a date range", notes = "Gets all attribute value refset members revisions in a date range. Use a null date to leave it open ended", response = AttributeValueRefSetMemberList.class)
  public AttributeValueRefSetMemberList findAttributeValueRefSetMemberRevisions(
    @ApiParam(value = "Concept terminology , e.g. SNOMEDCT", required = true) @PathParam("id") String id,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("startDate") String startDate,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("endDate") String endDate,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /attribute value/revisions/" + id + "/" + startDate
            + "/" + endDate + "/all");

    try {
      authenticate(securityService, authToken,
          "find attribute value refset member revisions", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      AttributeValueRefSetMemberList cl =
          historyService.findAttributeValueRefSetMemberRevisions(Long.valueOf(id),
              ConfigUtility.DATE_FORMAT.parse(startDate),
              ConfigUtility.DATE_FORMAT.parse(endDate), pfs);

      // explicitly do not want to use graph resolution handler.
      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e, "find attribute value refset member revisions");
      return null;
    } finally {
      securityService.close();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.HistoryServiceRest#
   * findAttributeValueRefSetMemberReleaseRevisions(java.lang.String,
   * java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  @GET
  @Path("/attributeValue/revisions/{id}/{release: [0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]}/release")
  @ApiOperation(value = "Get attribute value refset members release revision", notes = "Gets attribute value refset members release revision", response = AttributeValueRefSetMember.class)
  public AttributeValueRefSetMember<?> findAttributeValueRefSetMemberReleaseRevision(
    @ApiParam(value = "Concept id, e.g. 2", required = true) @PathParam("id") String id,
    @ApiParam(value = "Release date in the format YYYYMMDD , e.g. latest", required = true) @PathParam("release") String release,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /attribute value/revisions/" + id + "/" + release);

    try {
      authenticate(securityService, authToken,
          "find attribute value refset member release revision", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      AttributeValueRefSetMember<?> member =
          historyService.findAttributeValueRefSetMemberReleaseRevision(
              Long.valueOf(id), ConfigUtility.DATE_FORMAT.parse(release));

      // explicitly do not want to use graph resolution handler.
      historyService.close();
      return member;
    } catch (Exception e) {
      handleException(e, "find attribute value refset member release revision");
      return null;
    } finally {
      securityService.close();
    }
  }
  

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.HistoryServiceRest#
   * findComplexMapRefSetMembersModifiedSinceDate(java.lang.String,
   * java.lang.String, org.ihtsdo.otf.ts.helpers.PfsParameterJpa,
   * java.lang.String)
   */
  @Override
  @POST
  @Path("/complexMap/{terminology}/{date:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}")
  @ApiOperation(value = "Get complex map refset members modified since a date", notes = "Gets complex map refset members changed since a date.", response = ComplexMapRefSetMemberList.class)
  public ComplexMapRefSetMemberList findComplexMapRefSetMembersModifiedSinceDate(
    @ApiParam(value = "ComplexMapRefSetMember terminology , e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("date") String date,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /complex map/" + terminology + "/" + date);

    try {
      authenticate(securityService, authToken,
          "find complex map refset members modified since date", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      ComplexMapRefSetMemberList cl =
          historyService.findComplexMapRefSetMembersModifiedSinceDate(
              terminology, ConfigUtility.DATE_FORMAT.parse(date), pfs);

      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e,
          "trying to find complex map refset members modified since date");
      return null;
    } finally {
      securityService.close();
    }

  }

  /**
   * Find complex mapRefSetMember revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the complex mapRefSetMember list
   */
  @Override
  @POST
  @Path("/complexMap/revisions/{id}/{startDate:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}/{endDate:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}/all")
  @ApiOperation(value = "Get complex map refset members revisions in a date range", notes = "Gets all complex map refset members revisions in a date range. Use a null date to leave it open ended", response = ComplexMapRefSetMemberList.class)
  public ComplexMapRefSetMemberList findComplexMapRefSetMemberRevisions(
    @ApiParam(value = "Concept terminology , e.g. SNOMEDCT", required = true) @PathParam("id") String id,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("startDate") String startDate,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("endDate") String endDate,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /complex map/revisions/" + id + "/" + startDate
            + "/" + endDate + "/all");

    try {
      authenticate(securityService, authToken,
          "find complex map refset member revisions", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      ComplexMapRefSetMemberList cl =
          historyService.findComplexMapRefSetMemberRevisions(Long.valueOf(id),
              ConfigUtility.DATE_FORMAT.parse(startDate),
              ConfigUtility.DATE_FORMAT.parse(endDate), pfs);

      // explicitly do not want to use graph resolution handler.
      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e, "find complex map refset member revisions");
      return null;
    } finally {
      securityService.close();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.HistoryServiceRest#
   * findComplexMapRefSetMemberReleaseRevisions(java.lang.String,
   * java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  @GET
  @Path("/complexMap/revisions/{id}/{release: [0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]}/release")
  @ApiOperation(value = "Get complex map refset members release revision", notes = "Gets complex map refset members release revision", response = ComplexMapRefSetMember.class)
  public ComplexMapRefSetMember findComplexMapRefSetMemberReleaseRevision(
    @ApiParam(value = "Concept id, e.g. 2", required = true) @PathParam("id") String id,
    @ApiParam(value = "Release date in the format YYYYMMDD , e.g. latest", required = true) @PathParam("release") String release,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /complex map/revisions/" + id + "/" + release);

    try {
      authenticate(securityService, authToken,
          "find complex map refset member release revision", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      ComplexMapRefSetMember member =
          historyService.findComplexMapRefSetMemberReleaseRevision(
              Long.valueOf(id), ConfigUtility.DATE_FORMAT.parse(release));

      // explicitly do not want to use graph resolution handler.
      historyService.close();
      return member;
    } catch (Exception e) {
      handleException(e, "find complex map refset member release revision");
      return null;
    } finally {
      securityService.close();
    }
  }
  


  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.HistoryServiceRest#
   * findDescriptionTypeRefSetMembersModifiedSinceDate(java.lang.String,
   * java.lang.String, org.ihtsdo.otf.ts.helpers.PfsParameterJpa,
   * java.lang.String)
   */
  @Override
  @POST
  @Path("/descriptionType/{terminology}/{date:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}")
  @ApiOperation(value = "Get description type refset members modified since a date", notes = "Gets description type refset members changed since a date.", response = DescriptionTypeRefSetMemberList.class)
  public DescriptionTypeRefSetMemberList findDescriptionTypeRefSetMembersModifiedSinceDate(
    @ApiParam(value = "DescriptionTypeRefSetMember terminology , e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("date") String date,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /description type/" + terminology + "/" + date);

    try {
      authenticate(securityService, authToken,
          "find description type refset members modified since date", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      DescriptionTypeRefSetMemberList cl =
          historyService.findDescriptionTypeRefSetMembersModifiedSinceDate(
              terminology, ConfigUtility.DATE_FORMAT.parse(date), pfs);

      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e,
          "trying to find description type refset members modified since date");
      return null;
    } finally {
      securityService.close();
    }

  }

  /**
   * Find description typeRefSetMember revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the description typeRefSetMember list
   */
  @Override
  @POST
  @Path("/descriptionType/revisions/{id}/{startDate:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}/{endDate:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}/all")
  @ApiOperation(value = "Get description type refset members revisions in a date range", notes = "Gets all description type refset members revisions in a date range. Use a null date to leave it open ended", response = DescriptionTypeRefSetMemberList.class)
  public DescriptionTypeRefSetMemberList findDescriptionTypeRefSetMemberRevisions(
    @ApiParam(value = "Concept terminology , e.g. SNOMEDCT", required = true) @PathParam("id") String id,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("startDate") String startDate,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("endDate") String endDate,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /description type/revisions/" + id + "/" + startDate
            + "/" + endDate + "/all");

    try {
      authenticate(securityService, authToken,
          "find description type refset member revisions", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      DescriptionTypeRefSetMemberList cl =
          historyService.findDescriptionTypeRefSetMemberRevisions(Long.valueOf(id),
              ConfigUtility.DATE_FORMAT.parse(startDate),
              ConfigUtility.DATE_FORMAT.parse(endDate), pfs);

      // explicitly do not want to use graph resolution handler.
      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e, "find description type refset member revisions");
      return null;
    } finally {
      securityService.close();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.HistoryServiceRest#
   * findDescriptionTypeRefSetMemberReleaseRevisions(java.lang.String,
   * java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  @GET
  @Path("/descriptionType/revisions/{id}/{release: [0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]}/release")
  @ApiOperation(value = "Get description type refset members release revision", notes = "Gets description type refset members release revision", response = DescriptionTypeRefSetMember.class)
  public DescriptionTypeRefSetMember findDescriptionTypeRefSetMemberReleaseRevision(
    @ApiParam(value = "Concept id, e.g. 2", required = true) @PathParam("id") String id,
    @ApiParam(value = "Release date in the format YYYYMMDD , e.g. latest", required = true) @PathParam("release") String release,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /description type/revisions/" + id + "/" + release);

    try {
      authenticate(securityService, authToken,
          "find description type refset member release revision", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      DescriptionTypeRefSetMember member =
          historyService.findDescriptionTypeRefSetMemberReleaseRevision(
              Long.valueOf(id), ConfigUtility.DATE_FORMAT.parse(release));

      // explicitly do not want to use graph resolution handler.
      historyService.close();
      return member;
    } catch (Exception e) {
      handleException(e, "find description type refset member release revision");
      return null;
    } finally {
      securityService.close();
    }
  }
  


  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.HistoryServiceRest#
   * findModuleDependencyRefSetMembersModifiedSinceDate(java.lang.String,
   * java.lang.String, org.ihtsdo.otf.ts.helpers.PfsParameterJpa,
   * java.lang.String)
   */
  @Override
  @POST
  @Path("/moduleDependency/{terminology}/{date:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}")
  @ApiOperation(value = "Get module dependency refset members modified since a date", notes = "Gets module dependency refset members changed since a date.", response = ModuleDependencyRefSetMemberList.class)
  public ModuleDependencyRefSetMemberList findModuleDependencyRefSetMembersModifiedSinceDate(
    @ApiParam(value = "ModuleDependencyRefSetMember terminology , e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("date") String date,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /module dependency/" + terminology + "/" + date);

    try {
      authenticate(securityService, authToken,
          "find module dependency refset members modified since date", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      ModuleDependencyRefSetMemberList cl =
          historyService.findModuleDependencyRefSetMembersModifiedSinceDate(
              terminology, ConfigUtility.DATE_FORMAT.parse(date), pfs);

      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e,
          "trying to find module dependency refset members modified since date");
      return null;
    } finally {
      securityService.close();
    }

  }

  /**
   * Find module dependencyRefSetMember revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the module dependencyRefSetMember list
   */
  @Override
  @POST
  @Path("/moduleDependency/revisions/{id}/{startDate:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}/{endDate:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}/all")
  @ApiOperation(value = "Get module dependency refset members revisions in a date range", notes = "Gets all module dependency refset members revisions in a date range. Use a null date to leave it open ended", response = ModuleDependencyRefSetMemberList.class)
  public ModuleDependencyRefSetMemberList findModuleDependencyRefSetMemberRevisions(
    @ApiParam(value = "Concept terminology , e.g. SNOMEDCT", required = true) @PathParam("id") String id,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("startDate") String startDate,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("endDate") String endDate,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /module dependency/revisions/" + id + "/" + startDate
            + "/" + endDate + "/all");

    try {
      authenticate(securityService, authToken,
          "find module dependency refset member revisions", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      ModuleDependencyRefSetMemberList cl =
          historyService.findModuleDependencyRefSetMemberRevisions(Long.valueOf(id),
              ConfigUtility.DATE_FORMAT.parse(startDate),
              ConfigUtility.DATE_FORMAT.parse(endDate), pfs);

      // explicitly do not want to use graph resolution handler.
      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e, "find module dependency refset member revisions");
      return null;
    } finally {
      securityService.close();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.HistoryServiceRest#
   * findModuleDependencyRefSetMemberReleaseRevisions(java.lang.String,
   * java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  @GET
  @Path("/moduleDependency/revisions/{id}/{release: [0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]}/release")
  @ApiOperation(value = "Get module dependency refset members release revision", notes = "Gets module dependency refset members release revision", response = ModuleDependencyRefSetMember.class)
  public ModuleDependencyRefSetMember findModuleDependencyRefSetMemberReleaseRevision(
    @ApiParam(value = "Concept id, e.g. 2", required = true) @PathParam("id") String id,
    @ApiParam(value = "Release date in the format YYYYMMDD , e.g. latest", required = true) @PathParam("release") String release,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /module dependency/revisions/" + id + "/" + release);

    try {
      authenticate(securityService, authToken,
          "find module dependency refset member release revision", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      ModuleDependencyRefSetMember member =
          historyService.findModuleDependencyRefSetMemberReleaseRevision(
              Long.valueOf(id), ConfigUtility.DATE_FORMAT.parse(release));

      // explicitly do not want to use graph resolution handler.
      historyService.close();
      return member;
    } catch (Exception e) {
      handleException(e, "find module dependency refset member release revision");
      return null;
    } finally {
      securityService.close();
    }
  }
  


  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.HistoryServiceRest#
   * findRefsetDescriptorRefSetMembersModifiedSinceDate(java.lang.String,
   * java.lang.String, org.ihtsdo.otf.ts.helpers.PfsParameterJpa,
   * java.lang.String)
   */
  @Override
  @POST
  @Path("/refsetDescriptor/{terminology}/{date:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}")
  @ApiOperation(value = "Get refset descriptor refset members modified since a date", notes = "Gets refset descriptor refset members changed since a date.", response = RefsetDescriptorRefSetMemberList.class)
  public RefsetDescriptorRefSetMemberList findRefsetDescriptorRefSetMembersModifiedSinceDate(
    @ApiParam(value = "RefsetDescriptorRefSetMember terminology , e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("date") String date,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /refset descriptor/" + terminology + "/" + date);

    try {
      authenticate(securityService, authToken,
          "find refset descriptor refset members modified since date", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      RefsetDescriptorRefSetMemberList cl =
          historyService.findRefsetDescriptorRefSetMembersModifiedSinceDate(
              terminology, ConfigUtility.DATE_FORMAT.parse(date), pfs);

      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e,
          "trying to find refset descriptor refset members modified since date");
      return null;
    } finally {
      securityService.close();
    }

  }

  /**
   * Find refset descriptorRefSetMember revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the refset descriptorRefSetMember list
   */
  @Override
  @POST
  @Path("/refsetDescriptor/revisions/{id}/{startDate:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}/{endDate:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}/all")
  @ApiOperation(value = "Get refset descriptor refset members revisions in a date range", notes = "Gets all refset descriptor refset members revisions in a date range. Use a null date to leave it open ended", response = RefsetDescriptorRefSetMemberList.class)
  public RefsetDescriptorRefSetMemberList findRefsetDescriptorRefSetMemberRevisions(
    @ApiParam(value = "Concept terminology , e.g. SNOMEDCT", required = true) @PathParam("id") String id,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("startDate") String startDate,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("endDate") String endDate,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /refset descriptor/revisions/" + id + "/" + startDate
            + "/" + endDate + "/all");

    try {
      authenticate(securityService, authToken,
          "find refset descriptor refset member revisions", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      RefsetDescriptorRefSetMemberList cl =
          historyService.findRefsetDescriptorRefSetMemberRevisions(Long.valueOf(id),
              ConfigUtility.DATE_FORMAT.parse(startDate),
              ConfigUtility.DATE_FORMAT.parse(endDate), pfs);

      // explicitly do not want to use graph resolution handler.
      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e, "find refset descriptor refset member revisions");
      return null;
    } finally {
      securityService.close();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.HistoryServiceRest#
   * findRefsetDescriptorRefSetMemberReleaseRevisions(java.lang.String,
   * java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  @GET
  @Path("/refsetDescriptor/revisions/{id}/{release: [0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]}/release")
  @ApiOperation(value = "Get refset descriptor refset members release revision", notes = "Gets refset descriptor refset members release revision", response = RefsetDescriptorRefSetMember.class)
  public RefsetDescriptorRefSetMember findRefsetDescriptorRefSetMemberReleaseRevision(
    @ApiParam(value = "Concept id, e.g. 2", required = true) @PathParam("id") String id,
    @ApiParam(value = "Release date in the format YYYYMMDD , e.g. latest", required = true) @PathParam("release") String release,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /refset descriptor/revisions/" + id + "/" + release);

    try {
      authenticate(securityService, authToken,
          "find refset descriptor refset member release revision", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      RefsetDescriptorRefSetMember member =
          historyService.findRefsetDescriptorRefSetMemberReleaseRevision(
              Long.valueOf(id), ConfigUtility.DATE_FORMAT.parse(release));

      // explicitly do not want to use graph resolution handler.
      historyService.close();
      return member;
    } catch (Exception e) {
      handleException(e, "find refset descriptor refset member release revision");
      return null;
    } finally {
      securityService.close();
    }
  }
    
  
  


  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.HistoryServiceRest#
   * findSimpleMapRefSetMembersModifiedSinceDate(java.lang.String,
   * java.lang.String, org.ihtsdo.otf.ts.helpers.PfsParameterJpa,
   * java.lang.String)
   */
  @Override
  @POST
  @Path("/simpleMap/{terminology}/{date:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}")
  @ApiOperation(value = "Get simple map refset members modified since a date", notes = "Gets simple map refset members changed since a date.", response = SimpleMapRefSetMemberList.class)
  public SimpleMapRefSetMemberList findSimpleMapRefSetMembersModifiedSinceDate(
    @ApiParam(value = "SimpleMapRefSetMember terminology , e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("date") String date,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /simple map/" + terminology + "/" + date);

    try {
      authenticate(securityService, authToken,
          "find simple map refset members modified since date", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      SimpleMapRefSetMemberList cl =
          historyService.findSimpleMapRefSetMembersModifiedSinceDate(
              terminology, ConfigUtility.DATE_FORMAT.parse(date), pfs);

      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e,
          "trying to find simple map refset members modified since date");
      return null;
    } finally {
      securityService.close();
    }

  }

  /**
   * Find simple mapRefSetMember revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the simple mapRefSetMember list
   */
  @Override
  @POST
  @Path("/simpleMap/revisions/{id}/{startDate:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}/{endDate:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}/all")
  @ApiOperation(value = "Get simple map refset members revisions in a date range", notes = "Gets all simple map refset members revisions in a date range. Use a null date to leave it open ended", response = SimpleMapRefSetMemberList.class)
  public SimpleMapRefSetMemberList findSimpleMapRefSetMemberRevisions(
    @ApiParam(value = "Concept terminology , e.g. SNOMEDCT", required = true) @PathParam("id") String id,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("startDate") String startDate,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("endDate") String endDate,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /simple map/revisions/" + id + "/" + startDate
            + "/" + endDate + "/all");

    try {
      authenticate(securityService, authToken,
          "find simple map refset member revisions", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      SimpleMapRefSetMemberList cl =
          historyService.findSimpleMapRefSetMemberRevisions(Long.valueOf(id),
              ConfigUtility.DATE_FORMAT.parse(startDate),
              ConfigUtility.DATE_FORMAT.parse(endDate), pfs);

      // explicitly do not want to use graph resolution handler.
      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e, "find simple map refset member revisions");
      return null;
    } finally {
      securityService.close();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.HistoryServiceRest#
   * findSimpleMapRefSetMemberReleaseRevisions(java.lang.String,
   * java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  @GET
  @Path("/simpleMap/revisions/{id}/{release: [0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]}/release")
  @ApiOperation(value = "Get simple map refset members release revision", notes = "Gets simple map refset members release revision", response = SimpleMapRefSetMember.class)
  public SimpleMapRefSetMember findSimpleMapRefSetMemberReleaseRevision(
    @ApiParam(value = "Concept id, e.g. 2", required = true) @PathParam("id") String id,
    @ApiParam(value = "Release date in the format YYYYMMDD , e.g. latest", required = true) @PathParam("release") String release,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /simple map/revisions/" + id + "/" + release);

    try {
      authenticate(securityService, authToken,
          "find simple map refset member release revision", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      SimpleMapRefSetMember member =
          historyService.findSimpleMapRefSetMemberReleaseRevision(
              Long.valueOf(id), ConfigUtility.DATE_FORMAT.parse(release));

      // explicitly do not want to use graph resolution handler.
      historyService.close();
      return member;
    } catch (Exception e) {
      handleException(e, "find simple map refset member release revision");
      return null;
    } finally {
      securityService.close();
    }
  }
  


  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.HistoryServiceRest#
   * findSimpleRefSetMembersModifiedSinceDate(java.lang.String,
   * java.lang.String, org.ihtsdo.otf.ts.helpers.PfsParameterJpa,
   * java.lang.String)
   */
  @Override
  @POST
  @Path("/simple/{terminology}/{date:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}")
  @ApiOperation(value = "Get simple refset members modified since a date", notes = "Gets simple refset members changed since a date.", response = SimpleRefSetMemberList.class)
  public SimpleRefSetMemberList findSimpleRefSetMembersModifiedSinceDate(
    @ApiParam(value = "SimpleRefSetMember terminology , e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("date") String date,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /simple/" + terminology + "/" + date);

    try {
      authenticate(securityService, authToken,
          "find simple refset members modified since date", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      SimpleRefSetMemberList cl =
          historyService.findSimpleRefSetMembersModifiedSinceDate(
              terminology, ConfigUtility.DATE_FORMAT.parse(date), pfs);

      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e,
          "trying to find simple refset members modified since date");
      return null;
    } finally {
      securityService.close();
    }

  }

  /**
   * Find simpleRefSetMember revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the simpleRefSetMember list
   */
  @Override
  @POST
  @Path("/simple/revisions/{id}/{startDate:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}/{endDate:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}/all")
  @ApiOperation(value = "Get simple refset members revisions in a date range", notes = "Gets all simple refset members revisions in a date range. Use a null date to leave it open ended", response = SimpleRefSetMemberList.class)
  public SimpleRefSetMemberList findSimpleRefSetMemberRevisions(
    @ApiParam(value = "Concept terminology , e.g. SNOMEDCT", required = true) @PathParam("id") String id,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("startDate") String startDate,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("endDate") String endDate,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /simple/revisions/" + id + "/" + startDate
            + "/" + endDate + "/all");

    try {
      authenticate(securityService, authToken,
          "find simple refset member revisions", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      SimpleRefSetMemberList cl =
          historyService.findSimpleRefSetMemberRevisions(Long.valueOf(id),
              ConfigUtility.DATE_FORMAT.parse(startDate),
              ConfigUtility.DATE_FORMAT.parse(endDate), pfs);

      // explicitly do not want to use graph resolution handler.
      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e, "find simple refset member revisions");
      return null;
    } finally {
      securityService.close();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.HistoryServiceRest#
   * findSimpleRefSetMemberReleaseRevisions(java.lang.String,
   * java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  @GET
  @Path("/simple/revisions/{id}/{release: [0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]}/release")
  @ApiOperation(value = "Get simple refset members release revision", notes = "Gets simple refset members release revision", response = SimpleRefSetMember.class)
  public SimpleRefSetMember findSimpleRefSetMemberReleaseRevision(
    @ApiParam(value = "Concept id, e.g. 2", required = true) @PathParam("id") String id,
    @ApiParam(value = "Release date in the format YYYYMMDD , e.g. latest", required = true) @PathParam("release") String release,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /simple/revisions/" + id + "/" + release);

    try {
      authenticate(securityService, authToken,
          "find simple refset member release revision", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      SimpleRefSetMember member =
          historyService.findSimpleRefSetMemberReleaseRevision(
              Long.valueOf(id), ConfigUtility.DATE_FORMAT.parse(release));

      // explicitly do not want to use graph resolution handler.
      historyService.close();
      return member;
    } catch (Exception e) {
      handleException(e, "find simple refset member release revision");
      return null;
    } finally {
      securityService.close();
    }
  }
 
  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#findConceptsDeepModifiedSinceDate
   * (java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  @POST
  @Path("/concept/{terminology}/{date:([0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]|null)}/deep")
  @ApiOperation(value = "Get concepts modified since a date", notes = "Gets concepts where the concept or any part of it changed since specified date.", response = ConceptList.class)
  public ConceptList findConceptsDeepModifiedSinceDate(
    @ApiParam(value = "Concept terminology , e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Date in the format YYYYMMDD, e.g. 20140731 or \"null\"", required = true) @PathParam("date") String date,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (History): /concept/" + terminology + "/" + date
            + "/deep");

    try {
      authenticate(securityService, authToken,
          "find deep modified concepts since date", UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      ConceptList cl =
          historyService.findConceptsDeepModifiedSinceDate(terminology, (date
              .equals("null") ? null : ConfigUtility.DATE_FORMAT.parse(date)),
              pfs);

      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e, "trying to find concepts deep modified since date");
      return null;
    } finally {
      securityService.close();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#getReleaseHistory(java.lang.String
   * )
   */
  @Override
  @GET
  @Path("/releases/{terminology}")
  @ApiOperation(value = "Get release history", notes = "Gets all release info objects.", response = ReleaseInfoList.class)
  public ReleaseInfoList getReleaseHistory(
    @ApiParam(value = "Release info terminology , e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info(
        "RESTful call (History): /release/history/");

    try {
      authenticate(securityService, authToken, "get release history",
          UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      ReleaseInfoList result = historyService.getReleaseHistory(terminology);
      historyService.close();
      return result;

    } catch (Exception e) {
      handleException(e, "trying to get release history");
      return null;
    } finally {
      securityService.close();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#getCurrentReleaseInfo(java.lang
   * .String)
   */
  @Override
  @GET
  @Path("/release/{terminology}/current")
  @ApiOperation(value = "Get current release info", notes = "Gets release info for current release", response = ReleaseInfo.class)
  public ReleaseInfo getCurrentReleaseInfo(
    @ApiParam(value = "Release info terminology , e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info(
        "RESTful call (History): /release/current/");

    try {
      authenticate(securityService, authToken, "get current release info",
          UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      ReleaseInfo result = historyService.getCurrentReleaseInfo(terminology);
      historyService.close();
      return result;

    } catch (Exception e) {
      handleException(e, "trying to get current release info");
      return null;
    } finally {
      securityService.close();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#getPreviousReleaseInfo(java.lang
   * .String)
   */
  @Override
  @GET
  @Path("/release/{terminology}/previous")
  @ApiOperation(value = "Get previous release info", notes = "Gets release info for previous release", response = ReleaseInfo.class)
  public ReleaseInfo getPreviousReleaseInfo(
    @ApiParam(value = "Release info terminology , e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info(
        "RESTful call (History): /release/previous/");

    try {
      authenticate(securityService, authToken, "get previous release info",
          UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      ReleaseInfo result = historyService.getPreviousReleaseInfo(terminology);
      historyService.close();
      return result;

    } catch (Exception e) {
      handleException(e, "trying to get previous release info");
      return null;
    } finally {
      securityService.close();
    }
  }

  @Override
  @GET
  @Path("/release/{terminology}/planned")
  @ApiOperation(value = "Get planned release info", notes = "Gets release info for planned release", response = ReleaseInfo.class)
  public ReleaseInfo getPlannedReleaseInfo(
    @ApiParam(value = "Release info terminology , e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info(
        "RESTful call (History): /release/planned/");

    try {
      authenticate(securityService, authToken, "get planned release info",
          UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      ReleaseInfo result = historyService.getPlannedReleaseInfo(terminology);
      historyService.close();
      return result;

    } catch (Exception e) {
      handleException(e, "trying to get planned release info");
      return null;
    } finally {
      securityService.close();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#getReleaseInfo(java.lang.String,
   * java.lang.String)
   */
  @Override
  @GET
  @Path("/release/{terminology}/{name}")
  @ApiOperation(value = "Get release info", notes = "Gets release info for specified release name and terminology", response = ReleaseInfo.class)
  public ReleaseInfo getReleaseInfo(
    @ApiParam(value = "Release info terminology , e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Release version info, e.g. 'latest'", required = true) @PathParam("name") String name,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info(
        "RESTful call (History): /release/" + name);

    try {
      authenticate(securityService, authToken, "get release info for " + name,
          UserRole.VIEWER);

      HistoryService historyService = new HistoryServiceJpa();
      ReleaseInfo result = historyService.getReleaseInfo(terminology, name);
      historyService.close();
      return result;

    } catch (Exception e) {
      handleException(e, "trying to get release info for " + name);
      return null;
    } finally {
      securityService.close();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#addReleaseInfo(org.ihtsdo.otf
   * .ts.helpers.ReleaseInfo, java.lang.String)
   */
  @Override
  @PUT
  @Path("/release/add")
  @ApiOperation(value = "Add release info", notes = "Adds the specified release info", response = ReleaseInfo.class)
  public ReleaseInfo addReleaseInfo(
    @ApiParam(value = "Release info object, e.g. see output of /release/current", required = true) ReleaseInfoJpa releaseInfo,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info(
        "RESTful call (History): /release/add " + releaseInfo.getName());

    try {
      authenticate(securityService, authToken, "add release info",
          UserRole.ADMINISTRATOR);

      HistoryService historyService = new HistoryServiceJpa();
      releaseInfo.setLastModifiedBy(securityService
          .getUsernameForToken(authToken));
      ReleaseInfo result = historyService.addReleaseInfo(releaseInfo);
      historyService.close();
      return result;

    } catch (Exception e) {
      handleException(e, "trying to add release info");
      return null;
    } finally {
      securityService.close();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#updateReleaseInfo(org.ihtsdo.
   * otf.ts.helpers.ReleaseInfo, java.lang.String)
   */
  @Override
  @POST
  @Path("/release/update")
  @ApiOperation(value = "Update release info", notes = "Updatess the specified release info")
  public void updateReleaseInfo(
    @ApiParam(value = "Release info object, e.g. see output of /release/current", required = true) ReleaseInfoJpa releaseInfo,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info(
        "RESTful call (History): /release/update " + releaseInfo.getName());

    try {
      authenticate(securityService, authToken, "update release info",
          UserRole.ADMINISTRATOR);

      HistoryService historyService = new HistoryServiceJpa();
      releaseInfo.setLastModifiedBy(securityService
          .getUsernameForToken(authToken));
      historyService.updateReleaseInfo(releaseInfo);
      historyService.close();
    } catch (Exception e) {
      handleException(e, "trying to update release info");
    } finally {
      securityService.close();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#removeReleaseInfo(java.lang.String
   * , java.lang.String)
   */
  @Override
  @DELETE
  @Path("/release/remove/{id}")
  @ApiOperation(value = "Remove release info", notes = "Removes the release info for the specified id")
  public void removeReleaseInfo(
    @ApiParam(value = "Release info object id, e.g. 2", required = true) @PathParam("id") Long id,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info(
        "RESTful call (History): /release/remove/" + id);

    try {
      authenticate(securityService, authToken, "remove release info",
          UserRole.ADMINISTRATOR);

      HistoryService historyService = new HistoryServiceJpa();
      historyService.removeReleaseInfo(id);
      historyService.close();
    } catch (Exception e) {
      handleException(e, "trying to remove release info");
    } finally {
      securityService.close();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#startEditingCycle(java.lang.String
   * , java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @POST
  @Path("/release/startEditingCycle/{releaseVersion}/{terminology}/{version}")
  @ApiOperation(value = "Start the editing cycle", notes = "Marks the start of the editing cycle for the specified release for the specified terminology/version")
  public void startEditingCycle(
    @ApiParam(value = "Release version, e.g. 20150131", required = true) @PathParam("releaseVersion") String releaseVersion,
    @ApiParam(value = "Terminology, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info(
        "RESTful call (History): /release/startEditingCycle/" + releaseVersion
            + "/" + terminology + "/" + version);
    try {
      authenticate(securityService, authToken, "start editing cycle",
          UserRole.ADMINISTRATOR);
      // Perform operations
      StartEditingCycleAlgorithm algorithm =
          new StartEditingCycleAlgorithm(releaseVersion, terminology, version);
      algorithm.setUser(securityService.getUsernameForToken(authToken));
      algorithm.compute();
    } catch (Exception e) {
      handleException(e, "start editing cycle");
    } finally {
      securityService.close();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#beginRf2Release(java.lang.String,
   * java.lang.String, boolean, java.lang.String, boolean, java.lang.String)
   */
  @Override
  @POST
  @Path("/release/begin/{terminology}/{releaseVersion}/{validate}/{workflowStatusValues}/{saveIdentifiers}")
  @ApiOperation(value = "Begin a release", notes = "Begins release processing for the specified terminology and release version")
  public void beginRf2Release(
    @ApiParam(value = "Release version, e.g. 20150131", required = true) @PathParam("releaseVersion") String releaseVersion,
    @ApiParam(value = "Terminology, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Validation flag, e.g. true", required = true) @PathParam("validate") boolean validate,
    @ApiParam(value = "Workflow status values, e.g. \"WF1,WF2\"", required = true) @PathParam("workflowStatusValues") String workflowStatusValues,
    @ApiParam(value = "Save ids flag, e.g. true", required = true) @PathParam("saveIdentifiers") boolean saveIdentifiers,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info(
        "RESTful call (History): /release/begin/ " + terminology + "/"
            + releaseVersion + "/" + validate + "/" + workflowStatusValues
            + "/" + saveIdentifiers);

    try {
      authenticate(securityService, authToken, "begin release",
          UserRole.ADMINISTRATOR);

      // Perform the operation
      Set<String> statusSet = new HashSet<>();
      if (workflowStatusValues != null) {
        for (String status : workflowStatusValues.split(",")) {
          statusSet.add(status);
        }
      }
      ReleaseRf2BeginAlgorithm algorithm =
          new ReleaseRf2BeginAlgorithm(releaseVersion, terminology, validate,
              statusSet, saveIdentifiers);
      try {
        algorithm.compute();
      } catch (LocalException e) {
        // validation failure
        ValidationResult result = algorithm.getValidationResult();
        Logger.getLogger(getClass()).info("  VALIDATION FAILED");
        for (String error : result.getErrors()) {
          Logger.getLogger(getClass()).info("    ERROR: " + error);
        }
        for (String warning : result.getWarnings()) {
          Logger.getLogger(getClass()).info("    WARNING: " + warning);
        }
        if (!result.isValid()) {
          throw new Exception("Validation Failed");
        }
      }

    } catch (Exception e) {
      handleException(e, "start editing cycle");
    } finally {
      securityService.close();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#processRf2Release(java.lang.String
   * , java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  @POST
  @Path("/release/perform/{terminology}/{releaseVersion}/{moduleId}")
  @ApiOperation(value = "Process a release", notes = "Perform release processing for the specified terminology and release version")
  public void processRf2Release(
    @ApiParam(value = "Release version, e.g. 20150131", required = true) @PathParam("release") String releaseVersion,
    @ApiParam(value = "Terminology, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Output directory, e.g. /tmp", required = true) String outputDir,
    @ApiParam(value = "Module id, e.g. 17374234001", required = true) @PathParam("moduleId") String moduleId,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info(
        "RESTful call (History): /release/perform/" + terminology + "/"
            + releaseVersion + "/" + moduleId);

    try {
      authenticate(securityService, authToken, "perform release",
          UserRole.ADMINISTRATOR);
      // Perform operations
      ReleaseRf2PerformAlgorithm algorithm =
          new ReleaseRf2PerformAlgorithm(releaseVersion, terminology,
              outputDir, moduleId);
      algorithm.compute();
      algorithm.close();

    } catch (Exception e) {
      handleException(e, "start editing cycle");
    } finally {
      securityService.close();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#finishRf2Release(java.lang.String
   * , java.lang.String, java.lang.String)
   */
  @Override
  @POST
  @Path("/release/finish/{terminology}/{releaseVersion}")
  @ApiOperation(value = "Finish a release", notes = "Finishes release processing for the specified terminology and release version")
  public void finishRf2Release(
    @ApiParam(value = "Release version, e.g. 20150131", required = true) @PathParam("release") String releaseVersion,
    @ApiParam(value = "Terminology, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info(
        "RESTful call (History): /release/finish/" + terminology);

    try {
      authenticate(securityService, authToken, "perform release",
          UserRole.ADMINISTRATOR);
      // Perform operations
      ReleaseRf2FinishAlgorithm algorithm =
          new ReleaseRf2FinishAlgorithm(releaseVersion, terminology);
      algorithm.compute();
      algorithm.close();

    } catch (Exception e) {
      handleException(e, "start editing cycle");
    } finally {
      securityService.close();
    }
  }
}
