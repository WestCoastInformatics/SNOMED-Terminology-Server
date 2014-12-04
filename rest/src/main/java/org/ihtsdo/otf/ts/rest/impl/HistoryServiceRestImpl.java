package org.ihtsdo.otf.ts.rest.impl;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.DescriptionList;
import org.ihtsdo.otf.ts.helpers.LanguageRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.RelationshipList;
import org.ihtsdo.otf.ts.helpers.ReleaseInfo;
import org.ihtsdo.otf.ts.helpers.ReleaseInfoList;
import org.ihtsdo.otf.ts.helpers.UserRole;
import org.ihtsdo.otf.ts.jpa.services.HistoryServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.SecurityServiceJpa;
import org.ihtsdo.otf.ts.rest.HistoryServiceRest;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
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
@Produces({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
public class HistoryServiceRestImpl extends RootServiceRestImpl implements
    HistoryServiceRest {

  /** The security service. */
  private SecurityService securityService;

  /** The format. */
  private static FastDateFormat format = FastDateFormat.getInstance("yyyyMMdd");

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
  @Path("/concepts/{terminology}/{date: [0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]}")
  @ApiOperation(value = "Get concepts modified since a date", notes = "Gets concepts changed since a date.", response = ConceptList.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public ConceptList findConceptsModifiedSinceDate(
    @ApiParam(value = "Concept terminology , e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Date in the format YYYYMMDD , e.g. 20140731", required = true) @PathParam("date") String date,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (History): /concepts/" + terminology + "/" + date);

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.VIEWER))
        throw new WebApplicationException(
            Response
                .status(401)
                .entity(
                    "User does not have permissions to find concepts modoified since date.")
                .build());

      HistoryService historyService = new HistoryServiceJpa();
      ConceptList cl =
          historyService.findConceptsModifiedSinceDate(terminology,
              format.parse(date), pfs);

      // Consider whether this should be done
      /**
       * for (Concept concept : cl.getIterable()) { if (concept != null) {
       * contentService.getGraphResolutionHandler().resolve( concept,
       * TerminologyUtility.getHierarchcialIsaRels( concept.getTerminology(),
       * concept.getTerminologyVersion())); } }
       **/

      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e, "trying to find concepts modified since date");
      return null;
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
  @Path("/concepts/revisions/{id}/{startDate: [0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]}/{endDate: [0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]}/all")
  @ApiOperation(value = "Get concepts revisions in a date range", notes = "Gets all concept revisions in a date range. Use a null date to leave it open ended", response = ConceptList.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public ConceptList findConceptRevisions(
    @ApiParam(value = "Concept terminology , e.g. SNOMEDCT", required = true) @PathParam("id") String id,
    @ApiParam(value = "Date in the format YYYYMMDD , e.g. 20140731", required = true) @PathParam("startDate") String startDate,
    @ApiParam(value = "Date in the format YYYYMMDD , e.g. 20140731", required = true) @PathParam("endDate") String endDate,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (History): /concepts/revisions/" + id + "/" + startDate
            + "/" + endDate + "/all");

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.AUTHOR))
        throw new WebApplicationException(Response.status(401)
            .entity("User does not have permissions to retrieve the concept.")
            .build());

      HistoryService historyService = new HistoryServiceJpa();
      ConceptList cl =
          historyService.findConceptRevisions(Long.valueOf(id),
              format.parse(startDate), format.parse(endDate), pfs);

      // explicitly do not want to use graph resolution handler.
      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e, "trying to retrieve a concept");
      return null;
    }
  }

  @Override
  @POST
  @Path("/concepts/revisions/{id}/{release: [0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]}/release")
  @ApiOperation(value = "Get concepts release revision", notes = "Gets concept release revision.", response = Concept.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public Concept findConceptReleaseRevision(
    @ApiParam(value = "Concept id, e.g. 2", required = true) @PathParam("id") String id,
    @ApiParam(value = "Release date in the format YYYYMMDD , e.g. 20140731", required = true) @PathParam("release") String release,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (History): /concepts/revisions/" + id + "/" + release);

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.AUTHOR))
        throw new WebApplicationException(Response.status(401)
            .entity("User does not have permissions to retrieve the concept.")
            .build());

      HistoryService historyService = new HistoryServiceJpa();
      Concept concept =
          historyService.findConceptReleaseRevision(Long.valueOf(id),
              format.parse(release));

      // explicitly do not want to use graph resolution handler.
      historyService.close();
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
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#findDescriptionsModifiedSinceDate
   * (java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  @POST
  @Path("/descriptions/{terminology}/{date: [0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]}")
  @ApiOperation(value = "Get descriptions modified since a date", notes = "Gets descriptions changed since a date.", response = DescriptionList.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public DescriptionList findDescriptionsModifiedSinceDate(
    @ApiParam(value = "Description terminology , e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Date in the format YYYYMMDD , e.g. 20140731", required = true) @PathParam("terminology") String date,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (History): /descriptions/" + terminology + "/" + date);

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.VIEWER))
        throw new WebApplicationException(
            Response
                .status(401)
                .entity(
                    "User does not have permissions to find descriptions modoified since date.")
                .build());

      HistoryService historyService = new HistoryServiceJpa();
      DescriptionList cl =
          historyService.findDescriptionsModifiedSinceDate(terminology,
              format.parse(date), pfs);

      // Consider whether this should be done
      /**
       * for (Description description : cl.getIterable()) { if (description !=
       * null) { contentService.getGraphResolutionHandler().resolve(
       * description, TerminologyUtility.getHierarchcialIsaRels(
       * description.getTerminology(), description.getTerminologyVersion())); }
       * }
       **/

      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e, "trying to find descriptions modified since date");
      return null;
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
  @Path("/descriptions/revisions/{id}/{startDate: [0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]}/{endDate: [0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]}/all")
  @ApiOperation(value = "Get descriptions revisions in a date range", notes = "Gets all description revisions in a date range. Use a null date to leave it open ended", response = DescriptionList.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public DescriptionList findDescriptionRevisions(
    @ApiParam(value = "Concept terminology , e.g. SNOMEDCT", required = true) @PathParam("id") String id,
    @ApiParam(value = "Date in the format YYYYMMDD , e.g. 20140731", required = true) @PathParam("startDate") String startDate,
    @ApiParam(value = "Date in the format YYYYMMDD , e.g. 20140731", required = true) @PathParam("endDate") String endDate,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (History): /descriptions/revisions/" + id + "/"
            + startDate + "/" + endDate + "/all");

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.AUTHOR))
        throw new WebApplicationException(Response
            .status(401)
            .entity(
                "User does not have permissions to retrieve the description.")
            .build());

      HistoryService historyService = new HistoryServiceJpa();
      DescriptionList cl =
          historyService.findDescriptionRevisions(Long.valueOf(id),
              format.parse(startDate), format.parse(endDate), pfs);

      // explicitly do not want to use graph resolution handler.
      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e, "trying to retrieve a description");
      return null;
    }
  }

  @Override
  @POST
  @Path("/descriptions/revisions/{id}/{release: [0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]}/release")
  @ApiOperation(value = "Get descriptions release revision", notes = "Gets description release revision", response = Description.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public Description findDescriptionReleaseRevision(
    @ApiParam(value = "Concept id , e.g. 2", required = true) @PathParam("id") String id,
    @ApiParam(value = "Release date in the format YYYYMMDD , e.g. 20140731", required = true) @PathParam("release") String release,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(ContentServiceRestImpl.class)
        .info(
            "RESTful call (History): /descriptions/revisions/" + id + "/"
                + release);

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.AUTHOR))
        throw new WebApplicationException(Response
            .status(401)
            .entity(
                "User does not have permissions to retrieve the description.")
            .build());

      HistoryService historyService = new HistoryServiceJpa();
      Description description =
          historyService.findDescriptionReleaseRevision(Long.valueOf(id),
              format.parse(release));

      // explicitly do not want to use graph resolution handler.
      historyService.close();
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
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#findRelationshipsModifiedSinceDate
   * (java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  @POST
  @Path("/relationships/{terminology}/{date: [0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]}")
  @ApiOperation(value = "Get relationships modified since a date", notes = "Gets relationships changed since a date.", response = RelationshipList.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public RelationshipList findRelationshipsModifiedSinceDate(
    @ApiParam(value = "Relationship terminology , e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Date in the format YYYYMMDD , e.g. 20140731", required = true) @PathParam("terminology") String date,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (History): /relationships/" + terminology + "/" + date);

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.VIEWER))
        throw new WebApplicationException(
            Response
                .status(401)
                .entity(
                    "User does not have permissions to find relationships modoified since date.")
                .build());

      HistoryService historyService = new HistoryServiceJpa();
      RelationshipList cl =
          historyService.findRelationshipsModifiedSinceDate(terminology,
              format.parse(date), pfs);

      // Consider whether this should be done
      /**
       * for (Relationship relationship : cl.getIterable()) { if (relationship
       * != null) { contentService.getGraphResolutionHandler().resolve(
       * relationship, TerminologyUtility.getHierarchcialIsaRels(
       * relationship.getTerminology(), relationship.getTerminologyVersion()));
       * } }
       **/

      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e, "trying to find relationships modified since date");
      return null;
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
  @Path("/relationships/revisions/{id}/{startDate: [0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]}/{endDate: [0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]}/all")
  @ApiOperation(value = "Get relationships revisions in a date range", notes = "Gets all relationship revisions in a date range. Use a null date to leave it open ended", response = RelationshipList.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public RelationshipList findRelationshipRevisions(
    @ApiParam(value = "Concept terminology , e.g. SNOMEDCT", required = true) @PathParam("id") String id,
    @ApiParam(value = "Date in the format YYYYMMDD , e.g. 20140731", required = true) @PathParam("startDate") String startDate,
    @ApiParam(value = "Date in the format YYYYMMDD , e.g. 20140731", required = true) @PathParam("endDate") String endDate,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (History): /relationships/revisions/" + id + "/"
            + startDate + "/" + endDate + "/all");

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.AUTHOR))
        throw new WebApplicationException(Response
            .status(401)
            .entity(
                "User does not have permissions to retrieve the relationship.")
            .build());

      HistoryService historyService = new HistoryServiceJpa();
      RelationshipList cl =
          historyService.findRelationshipRevisions(Long.valueOf(id),
              format.parse(startDate), format.parse(endDate), pfs);

      // explicitly do not want to use graph resolution handler.
      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e, "trying to retrieve a relationship");
      return null;
    }
  }

  @Override
  @POST
  @Path("/relationships/revisions/{id}/{release: [0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]}/release")
  @ApiOperation(value = "Get relationships release revision", notes = "Gets relationship release revision", response = Relationship.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public Relationship findRelationshipReleaseRevision(
    @ApiParam(value = "Concept id, e.g. 2", required = true) @PathParam("id") String id,
    @ApiParam(value = "Release date in the format YYYYMMDD , e.g. 20140731", required = true) @PathParam("release") String release,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (History): /relationships/revisions/" + id + "/"
            + release);

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.AUTHOR))
        throw new WebApplicationException(Response
            .status(401)
            .entity(
                "User does not have permissions to retrieve the relationship.")
            .build());

      HistoryService historyService = new HistoryServiceJpa();
      Relationship rel =
          historyService.findRelationshipReleaseRevision(Long.valueOf(id),
              format.parse(release));

      // explicitly do not want to use graph resolution handler.
      historyService.close();
      return rel;
    } catch (Exception e) {
      handleException(e, "trying to retrieve a relationship");
      return null;
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
  @Path("/languages/{terminology}/{date: [0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]}")
  @ApiOperation(value = "Get language refset members modified since a date", notes = "Gets language refset members changed since a date.", response = LanguageRefSetMemberList.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public LanguageRefSetMemberList findLanguageRefSetMembersModifiedSinceDate(
    @ApiParam(value = "LanguageRefSetMember terminology , e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Date in the format YYYYMMDD , e.g. 20140731", required = true) @PathParam("terminology") String date,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (History): /languages/" + terminology + "/" + date);

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.VIEWER))
        throw new WebApplicationException(
            Response
                .status(401)
                .entity(
                    "User does not have permissions to find language refset members modoified since date.")
                .build());

      HistoryService historyService = new HistoryServiceJpa();
      LanguageRefSetMemberList cl =
          historyService.findLanguageRefSetMembersModifiedSinceDate(
              terminology, format.parse(date), pfs);

      // Consider whether this should be done
      /**
       * for (LanguageRefSetMember languageRefSetMember : cl.getIterable()) { if
       * (languageRefSetMember != null) {
       * contentService.getGraphResolutionHandler().resolve(
       * languageRefSetMember, TerminologyUtility.getHierarchcialIsaRels(
       * languageRefSetMember.getTerminology(),
       * languageRefSetMember.getTerminologyVersion())); } }
       **/

      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e,
          "trying to find language refset members modified since date");
      return null;
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
  @Path("/languages/revisions/{id}/{startDate: [0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]}/{endDate: [0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]}/all")
  @ApiOperation(value = "Get language refset members revisions in a date range", notes = "Gets all language refset members revisions in a date range. Use a null date to leave it open ended", response = LanguageRefSetMemberList.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public LanguageRefSetMemberList findLanguageRefSetMemberRevisions(
    @ApiParam(value = "Concept terminology , e.g. SNOMEDCT", required = true) @PathParam("id") String id,
    @ApiParam(value = "Date in the format YYYYMMDD , e.g. 20140731", required = true) @PathParam("startDate") String startDate,
    @ApiParam(value = "Date in the format YYYYMMDD , e.g. 20140731", required = true) @PathParam("endDate") String endDate,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (History): /languages/revisions/" + id + "/" + startDate
            + "/" + endDate + "/all");

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.AUTHOR))
        throw new WebApplicationException(
            Response
                .status(401)
                .entity(
                    "User does not have permissions to retrieve the languageRefSetMember.")
                .build());

      HistoryService historyService = new HistoryServiceJpa();
      LanguageRefSetMemberList cl =
          historyService.findLanguageRefSetMemberRevisions(Long.valueOf(id),
              format.parse(startDate), format.parse(endDate), pfs);

      // explicitly do not want to use graph resolution handler.
      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e, "trying to retrieve a languageRefSetMember");
      return null;
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
  @POST
  @Path("/languages/revisions/{id}/{release: [0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]}/release")
  @ApiOperation(value = "Get language refset members release revision", notes = "Gets language refset members release revision", response = LanguageRefSetMember.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public LanguageRefSetMember findLanguageRefSetMemberReleaseRevision(
    @ApiParam(value = "Concept id, e.g. 2", required = true) @PathParam("id") String id,
    @ApiParam(value = "Release date in the format YYYYMMDD , e.g. 20140731", required = true) @PathParam("release") String release,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken) {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (History): /languages/revisions/" + id + "/" + release);

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.AUTHOR))
        throw new WebApplicationException(
            Response
                .status(401)
                .entity(
                    "User does not have permissions to retrieve the languageRefSetMember.")
                .build());

      HistoryService historyService = new HistoryServiceJpa();
      LanguageRefSetMember member =
          historyService.findLanguageRefSetMemberReleaseRevision(
              Long.valueOf(id), format.parse(release));

      // explicitly do not want to use graph resolution handler.
      historyService.close();
      return member;
    } catch (Exception e) {
      handleException(e, "trying to retrieve a languageRefSetMember");
      return null;
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
  @Path("/concepts/{terminology}/{date: [0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]}/deep")
  @ApiOperation(value = "Get concepts modified since a date", notes = "Gets concepts where the concept or any part of it changed since specified date.", response = ConceptList.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public ConceptList findConceptsDeepModifiedSinceDate(
    @ApiParam(value = "Concept terminology , e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Date in the format YYYYMMDD , e.g. 20140731", required = true) @PathParam("date") String date,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (History): /concepts/" + terminology + "/" + date
            + "/deep");

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.VIEWER))
        throw new WebApplicationException(
            Response
                .status(401)
                .entity(
                    "User does not have permissions to find concepts deep modoified since date.")
                .build());

      HistoryService historyService = new HistoryServiceJpa();
      ConceptList cl =
          historyService.findConceptsDeepModifiedSinceDate(terminology,
              format.parse(date), pfs);

      historyService.close();
      return cl;
    } catch (Exception e) {
      handleException(e, "trying to find concepts deep modified since date");
      return null;
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
  @Path("/release/history")
  @ApiOperation(value = "Get release history", notes = "Gets all release info objects.", response = ReleaseInfoList.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public ReleaseInfoList getReleaseHistory(
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (History): /release/history/");

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.VIEWER))
        throw new WebApplicationException(Response.status(401)
            .entity("User does not have permissions to get release history.")
            .build());

      HistoryService historyService = new HistoryServiceJpa();
      ReleaseInfoList result = historyService.getReleaseHistory();
      historyService.close();
      return result;

    } catch (Exception e) {
      handleException(e, "trying to get release history");
      return null;
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
  @Path("/release/current")
  @ApiOperation(value = "Get current release info", notes = "Gets release info for current release", response = ReleaseInfo.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public ReleaseInfo getCurrentReleaseInfo(
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (History): /release/current/");

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.VIEWER))
        throw new WebApplicationException(Response
            .status(401)
            .entity(
                "User does not have permissions to get current release info.")
            .build());

      HistoryService historyService = new HistoryServiceJpa();
      ReleaseInfo result = historyService.getCurrentReleaseInfo();
      historyService.close();
      return result;

    } catch (Exception e) {
      handleException(e, "trying to get current release info");
      return null;
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
  @Path("/release/previous")
  @ApiOperation(value = "Get previous release info", notes = "Gets release info for previous release", response = ReleaseInfo.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public ReleaseInfo getPreviousReleaseInfo(
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (History): /release/previous/");

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.VIEWER))
        throw new WebApplicationException(Response
            .status(401)
            .entity(
                "User does not have permissions to get previous release info.")
            .build());

      HistoryService historyService = new HistoryServiceJpa();
      ReleaseInfo result = historyService.getPreviousReleaseInfo();
      historyService.close();
      return result;

    } catch (Exception e) {
      handleException(e, "trying to get previous release info");
      return null;
    }
  }

  @Override
  @GET
  @Path("/release/planned")
  @ApiOperation(value = "Get planned release info", notes = "Gets release info for planned release", response = ReleaseInfo.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public ReleaseInfo getPlannedReleaseInfo(
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (History): /release/planned/");

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.VIEWER))
        throw new WebApplicationException(Response
            .status(401)
            .entity(
                "User does not have permissions to get planned release info.")
            .build());

      HistoryService historyService = new HistoryServiceJpa();
      ReleaseInfo result = historyService.getPlannedReleaseInfo();
      historyService.close();
      return result;

    } catch (Exception e) {
      handleException(e, "trying to get planned release info");
      return null;
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
  @Path("/release/{release}")
  @ApiOperation(value = "Get release info", notes = "Gets release info for specified release", response = ReleaseInfo.class)
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public ReleaseInfo getReleaseInfo(
    @ApiParam(value = "Release version info, e.g. '20140731'", required = true) @HeaderParam("release") String release,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (History): /release/" + release);

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.VIEWER))
        throw new WebApplicationException(Response
            .status(401)
            .entity(
                "User does not have permissions to get release info for "
                    + release).build());

      HistoryService historyService = new HistoryServiceJpa();
      ReleaseInfo result = historyService.getReleaseInfo(release);
      historyService.close();
      return result;

    } catch (Exception e) {
      handleException(e, "trying to get release info for " + release);
      return null;
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
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public ReleaseInfo addReleaseInfo(
    @ApiParam(value = "Release info object, e.g. see output of /release/current", required = true) ReleaseInfo releaseInfo,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (History): /release/add " + releaseInfo.getName());

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.ADMINISTRATOR))
        throw new WebApplicationException(Response.status(401)
            .entity("User does not have permissions to add release info")
            .build());

      HistoryService historyService = new HistoryServiceJpa();
      ReleaseInfo result = historyService.addReleaseInfo(releaseInfo);
      historyService.close();
      return result;

    } catch (Exception e) {
      handleException(e, "trying to add release info");
      return null;
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
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public void updateReleaseInfo(
    @ApiParam(value = "Release info object, e.g. see output of /release/current", required = true) ReleaseInfo releaseInfo,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (History): /release/update " + releaseInfo.getName());

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.ADMINISTRATOR))
        throw new WebApplicationException(Response.status(401)
            .entity("User does not have permissions to update release info")
            .build());

      HistoryService historyService = new HistoryServiceJpa();
      historyService.updateReleaseInfo(releaseInfo);
      historyService.close();
    } catch (Exception e) {
      handleException(e, "trying to update release info");
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
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  public void removeReleaseInfo(
    @ApiParam(value = "Release info object id, e.g. 2", required = true) @PathParam("id") String id,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(ContentServiceRestImpl.class).info(
        "RESTful call (History): /release/remove/" + id);

    try {
      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.ADMINISTRATOR))
        throw new WebApplicationException(Response.status(401)
            .entity("User does not have permissions to remove release info")
            .build());

      HistoryService historyService = new HistoryServiceJpa();
      historyService.removeReleaseInfo(Long.valueOf(id));
      historyService.close();
    } catch (Exception e) {
      handleException(e, "trying to remove release info");
    }
  }

}
