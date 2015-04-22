/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.jpa.client;

import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status.Family;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.ReleaseInfo;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ConceptListJpa;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.DescriptionList;
import org.ihtsdo.otf.ts.helpers.DescriptionListJpa;
import org.ihtsdo.otf.ts.helpers.LanguageRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.LanguageRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.RelationshipList;
import org.ihtsdo.otf.ts.helpers.RelationshipListJpa;
import org.ihtsdo.otf.ts.helpers.ReleaseInfoList;
import org.ihtsdo.otf.ts.helpers.ReleaseInfoListJpa;
import org.ihtsdo.otf.ts.jpa.ReleaseInfoJpa;
import org.ihtsdo.otf.ts.rest.HistoryServiceRest;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa;
import org.ihtsdo.otf.ts.rf2.jpa.LanguageRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RelationshipJpa;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * A client for connecting to a history REST service.
 */
public class HistoryClientRest implements HistoryServiceRest {

  /** The config. */
  private Properties config = null;

  /**
   * Instantiates a {@link ContentClientRest} from the specified parameters.
   *
   * @param config the config
   */
  public HistoryClientRest(Properties config) {
    this.config = config;
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
  public ConceptList findConceptsModifiedSinceDate(String terminology,
    String date, PfsParameterJpa pfs, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Client - find concepts modified since date " + terminology
            + ", " + date + ", " + pfs);
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/history/concept/"
            + terminology + "/" + date);
    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null ? new PfsParameterJpa()
            : pfs);
    Logger.getLogger(this.getClass()).debug(pfsString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, pfsString);

    String resultString = response.getEntity(String.class);

    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(
          resultString.substring(0, Math.min(resultString.length(), 3999)));
    } else {
      throw new Exception(response.toString());
    }

    // converting to object
    ConceptListJpa list =
        (ConceptListJpa) ConfigUtility.getGraphForString(resultString,
            ConceptListJpa.class);

    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#findConceptRevisions(java.lang
   * .String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  public ConceptList findConceptRevisions(String id, String startDate,
    String endDate, PfsParameterJpa pfs, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Client - find concept revisions " + id + ", " + startDate
            + ", " + endDate + ", " + pfs);
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/concept/revisions/" + id + "/" + startDate + "/"
            + endDate + "/all");

    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null ? new PfsParameterJpa()
            : pfs);
    Logger.getLogger(this.getClass()).debug(pfsString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, pfsString);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(
          resultString.substring(0, Math.min(resultString.length(), 3999)));
    } else {
      throw new Exception(response.toString());
    }

    // converting to object
    ConceptListJpa list =
        (ConceptListJpa) ConfigUtility.getGraphForString(resultString,
            ConceptListJpa.class);

    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#findConceptReleaseRevision(java
   * .lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public Concept findConceptReleaseRevision(String id, String release,
    String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/concept/revisions/" + id + "/" + release + "/release");

    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(
          resultString.substring(0, Math.min(resultString.length(), 3999)));
    } else {
      throw new Exception(response.toString());
    }

    // converting to object
    ConceptJpa concept =
        (ConceptJpa) ConfigUtility.getGraphForString(resultString,
            ConceptJpa.class);

    return concept;
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
  public DescriptionList findDescriptionsModifiedSinceDate(String terminology,
    String date, PfsParameterJpa pfs, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Client - find descriptions modified since date " + terminology
            + ", " + date + ", " + pfs);
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/description/" + terminology + "/" + date);
    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null ? new PfsParameterJpa()
            : pfs);
    Logger.getLogger(this.getClass()).debug(pfsString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, pfsString);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(
          resultString.substring(0, Math.min(resultString.length(), 3999)));
    } else {
      throw new Exception(response.toString());
    }

    // converting to object
    DescriptionListJpa list =
        (DescriptionListJpa) ConfigUtility.getGraphForString(resultString,
            DescriptionListJpa.class);

    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#findDescriptionRevisions(java
   * .lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  public DescriptionList findDescriptionRevisions(String id, String startDate,
    String endDate, PfsParameterJpa pfs, String authToken) throws Exception {
    Client client = Client.create();
    Logger.getLogger(getClass()).debug(
        "History Client - find description revisions " + id + ", " + startDate
            + ", " + endDate + ", " + pfs);
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/description/revisions/" + id + "/" + startDate + "/"
            + endDate + "/all");

    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null ? new PfsParameterJpa()
            : pfs);
    Logger.getLogger(this.getClass()).debug(pfsString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, pfsString);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(
          resultString.substring(0, Math.min(resultString.length(), 3999)));
    } else {
      throw new Exception(response.toString());
    }

    // converting to object
    DescriptionListJpa list =
        (DescriptionListJpa) ConfigUtility.getGraphForString(resultString,
            DescriptionListJpa.class);

    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#findDescriptionReleaseRevision
   * (java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public Description findDescriptionReleaseRevision(String id, String release,
    String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/description/revisions/" + id + "/" + release);

    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(
          resultString.substring(0, Math.min(resultString.length(), 3999)));
    } else {
      throw new Exception(response.toString());
    }

    // converting to object
    DescriptionJpa description =
        (DescriptionJpa) ConfigUtility.getGraphForString(resultString,
            DescriptionJpa.class);

    return description;
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
  public RelationshipList findRelationshipsModifiedSinceDate(
    String terminology, String date, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Client - find relationships modified since date "
            + terminology + ", " + date + ", " + pfs);
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/relationship/" + terminology + "/" + date);
    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null ? new PfsParameterJpa()
            : pfs);
    Logger.getLogger(this.getClass()).debug(pfsString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, pfsString);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(
          resultString.substring(0, Math.min(resultString.length(), 3999)));
    } else {
      throw new Exception(response.toString());
    }

    // converting to object
    RelationshipListJpa list =
        (RelationshipListJpa) ConfigUtility.getGraphForString(resultString,
            RelationshipListJpa.class);

    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#findRelationshipRevisions(java
   * .lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  public RelationshipList findRelationshipRevisions(String id,
    String startDate, String endDate, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Client - find relationship revisions " + id + ", " + startDate
            + ", " + endDate + ", " + pfs);
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/relationship/revisions/" + id + "/" + startDate + "/"
            + endDate + "/all");

    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null ? new PfsParameterJpa()
            : pfs);
    Logger.getLogger(this.getClass()).debug(pfsString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, pfsString);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(
          resultString.substring(0, Math.min(resultString.length(), 3999)));
    } else {
      throw new Exception(response.toString());
    }

    // converting to object
    RelationshipListJpa list =
        (RelationshipListJpa) ConfigUtility.getGraphForString(resultString,
            RelationshipListJpa.class);

    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#findRelationshipReleaseRevision
   * (java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public Relationship findRelationshipReleaseRevision(String id,
    String release, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/relationship/revisions/" + id + "/" + release);

    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(
          resultString.substring(0, Math.min(resultString.length(), 3999)));
    } else {
      throw new Exception(response.toString());
    }

    // converting to object
    RelationshipJpa relationship =
        (RelationshipJpa) ConfigUtility.getGraphForString(resultString,
            RelationshipJpa.class);

    return relationship;
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
  public LanguageRefSetMemberList findLanguageRefSetMembersModifiedSinceDate(
    String terminology, String date, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Client - find language members modified since date "
            + terminology + ", " + date + ", " + pfs);

    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/history/language/"
            + terminology + "/" + date);
    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null ? new PfsParameterJpa()
            : pfs);
    Logger.getLogger(this.getClass()).debug(pfsString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, pfsString);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(
          resultString.substring(0, Math.min(resultString.length(), 3999)));
    } else {
      throw new Exception(response.toString());
    }

    // converting to object
    LanguageRefSetMemberListJpa list =
        (LanguageRefSetMemberListJpa) ConfigUtility.getGraphForString(
            resultString, LanguageRefSetMemberListJpa.class);

    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#findLanguageRefSetMemberRevisions
   * (java.lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  public LanguageRefSetMemberList findLanguageRefSetMemberRevisions(String id,
    String startDate, String endDate, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Client - find language member revisions " + id + ", "
            + startDate + ", " + endDate + ", " + pfs);
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/language/revisions/" + id + "/" + startDate + "/"
            + endDate + "/all");

    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null ? new PfsParameterJpa()
            : pfs);
    Logger.getLogger(this.getClass()).debug(pfsString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, pfsString);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(
          resultString.substring(0, Math.min(resultString.length(), 3999)));
    } else {
      throw new Exception(response.toString());
    }

    // converting to object
    LanguageRefSetMemberListJpa list =
        (LanguageRefSetMemberListJpa) ConfigUtility.getGraphForString(
            resultString, LanguageRefSetMemberListJpa.class);

    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.HistoryServiceRest#
   * findLanguageRefSetMemberReleaseRevision(java.lang.String, java.lang.String,
   * java.lang.String)
   */
  @Override
  public LanguageRefSetMember findLanguageRefSetMemberReleaseRevision(
    String id, String release, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/language/revisions/" + id + "/" + release);

    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(
          resultString.substring(0, Math.min(resultString.length(), 3999)));
    } else {
      throw new Exception(response.toString());
    }

    // converting to object
    LanguageRefSetMemberJpa member =
        (LanguageRefSetMemberJpa) ConfigUtility.getGraphForString(resultString,
            LanguageRefSetMemberJpa.class);

    return member;
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
  public ConceptList findConceptsDeepModifiedSinceDate(String terminology,
    String date, PfsParameterJpa pfs, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Client - find concepts deep modified since date "
            + terminology + ", " + date + ", " + pfs);
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/history/concept/"
            + terminology + "/" + date + "/deep");
    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null ? new PfsParameterJpa()
            : pfs);
    Logger.getLogger(this.getClass()).debug(pfsString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, pfsString);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(
          resultString.substring(0, Math.min(resultString.length(), 3999)));
    } else {
      throw new Exception(response.toString());
    }

    // converting to object
    ConceptListJpa list =
        (ConceptListJpa) ConfigUtility.getGraphForString(resultString,
            ConceptListJpa.class);

    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#getReleaseHistory(java.lang.String
   * , java.lang.String)
   */
  @Override
  public ReleaseInfoList getReleaseHistory(String terminology, String authToken)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Client - get release history " + terminology);
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/history/releases/"
            + terminology);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);
    if (response.getStatus() == 204) {
      return null;
    }
    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(
          resultString.substring(0, Math.min(resultString.length(), 3999)));
    }
    // handle null response
    else if (response.getStatus() == 204) {
      return null;
    } else {
      throw new Exception(response.toString());
    }

    // converting to object
    ReleaseInfoListJpa c =
        (ReleaseInfoListJpa) ConfigUtility.getGraphForString(resultString,
            ReleaseInfoListJpa.class);
    return c;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#getCurrentReleaseInfo(java.lang
   * .String)
   */
  @Override
  public ReleaseInfo getCurrentReleaseInfo(String terminology, String authToken)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Client - get current release info " + terminology);
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/history/release/"
            + terminology + "/current");
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);
    if (response.getStatus() == 204) {
      return null;
    }
    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(
          resultString.substring(0, Math.min(resultString.length(), 3999)));
    }
    // handle null response
    else if (response.getStatus() == 204) {
      return null;
    } else {
      throw new Exception(response.toString());
    }

    // converting to object
    ReleaseInfoJpa info =
        (ReleaseInfoJpa) ConfigUtility.getGraphForString(resultString,
            ReleaseInfoJpa.class);
    return info;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#getPreviousReleaseInfo(java.lang
   * .String, java.lang.String)
   */
  @Override
  public ReleaseInfo getPreviousReleaseInfo(String terminology, String authToken)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Client - get previous release info " + terminology);
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/history/release/"
            + terminology + "/previous");
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);
    if (response.getStatus() == 204) {
      return null;
    }
    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(
          resultString.substring(0, Math.min(resultString.length(), 3999)));
    }
    // handle null response
    else if (response.getStatus() == 204) {
      return null;
    } else {
      throw new Exception(response.toString());
    }

    // converting to object
    ReleaseInfoJpa info =
        (ReleaseInfoJpa) ConfigUtility.getGraphForString(resultString,
            ReleaseInfoJpa.class);
    return info;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#getPlannedReleaseInfo(java.lang
   * .String, java.lang.String)
   */
  @Override
  public ReleaseInfo getPlannedReleaseInfo(String terminology, String authToken)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Client - get planned release info " + terminology);
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/history/release/"
            + terminology + "/planned");
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);
    if (response.getStatus() == 204) {
      return null;
    }
    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(
          resultString.substring(0, Math.min(resultString.length(), 3999)));
    }
    // handle null response
    else if (response.getStatus() == 204) {
      return null;
    } else {
      throw new Exception(response.toString());
    }

    // converting to object
    ReleaseInfoJpa info =
        (ReleaseInfoJpa) ConfigUtility.getGraphForString(resultString,
            ReleaseInfoJpa.class);
    return info;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#getReleaseInfo(java.lang.String,
   * java.lang.String, java.lang.String)
   */
  @Override
  public ReleaseInfo getReleaseInfo(String terminology, String name,
    String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Client - get release info " + terminology + ", " + name);
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/history/release/"
            + terminology + "/" + name);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);
    if (response.getStatus() == 204) {
      return null;
    }
    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(
          resultString.substring(0, Math.min(resultString.length(), 3999)));
    } else {
      throw new Exception(response.toString());
    }

    // converting to object
    ReleaseInfoJpa info =
        (ReleaseInfoJpa) ConfigUtility.getGraphForString(resultString,
            ReleaseInfoJpa.class);
    return info;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#addReleaseInfo(org.ihtsdo.otf
   * .ts.helpers.ReleaseInfo, java.lang.String)
   */
  @Override
  public ReleaseInfo addReleaseInfo(ReleaseInfoJpa releaseInfo, String authToken)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Client - add release info " + releaseInfo);
    Client client = Client.create();
    WebResource resource =
        client
            .resource(config.getProperty("base.url") + "/history/release/add");
    String riString =
        ConfigUtility.getStringForGraph(releaseInfo == null
            ? new ReleaseInfoJpa() : releaseInfo);
    Logger.getLogger(this.getClass()).debug(riString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .put(ClientResponse.class, riString);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(
          resultString.substring(0, Math.min(resultString.length(), 3999)));
    } else {
      throw new Exception(response.toString());
    }

    // converting to object
    ReleaseInfoJpa info =
        (ReleaseInfoJpa) ConfigUtility.getGraphForString(resultString,
            ReleaseInfoJpa.class);

    return info;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#updateReleaseInfo(org.ihtsdo.
   * otf.ts.helpers.ReleaseInfo, java.lang.String)
   */
  @Override
  public void updateReleaseInfo(ReleaseInfoJpa releaseInfo, String authToken)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Client - update release info " + releaseInfo);
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/release/update");
    String riString =
        ConfigUtility.getStringForGraph(releaseInfo == null
            ? new ReleaseInfoJpa() : releaseInfo);
    Logger.getLogger(this.getClass()).debug(riString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, riString);

    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // do nothing
    } else {
      throw new Exception("Unexpected status " + response.getStatus());
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#removeReleaseInfo(java.lang.Long,
   * java.lang.String)
   */
  @Override
  public void removeReleaseInfo(Long id, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Client - remove release info " + id);
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/release/remove/" + id);

    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).delete(ClientResponse.class);

    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // do nothing
    } else {
      throw new Exception("Unexpected status " + response.getStatus());
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
  public void startEditingCycle(String releaseVersion, String terminology,
    String version, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Client - start editing cycle " + releaseVersion + ", "
            + terminology + ", " + version);
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/release/startEditingCycle/" + releaseVersion + "/"
            + terminology + "/" + version);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).post(ClientResponse.class);

    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // do nothing
    } else {
      throw new Exception("Unexpected status " + response.getStatus());
    }
  }

  @Override
  public void beginRf2Release(String releaseVersion, String terminology,
    boolean validate, String workflowStatusValues, boolean saveIdentifiers,
    String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Client - begin rf2 release " + releaseVersion + ", "
            + terminology);
    // TODO Auto-generated method stub

  }

  @Override
  public void processRf2Release(String releaseVersion, String terminology,
    String outputDir, String moduleId, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Client - process rf2 release " + releaseVersion + ", "
            + terminology);
    // TODO Auto-generated method stub

  }

  @Override
  public void finishRf2Release(String releaseVersion, String terminology,
    String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Client - finish rf2 release " + releaseVersion + ", "
            + terminology);
    // TODO Auto-generated method stub

  }

}
