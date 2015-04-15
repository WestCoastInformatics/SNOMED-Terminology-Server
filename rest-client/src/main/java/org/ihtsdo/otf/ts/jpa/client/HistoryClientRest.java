/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.jpa.client;

import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status.Family;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.ReleaseInfo;
import org.ihtsdo.otf.ts.helpers.AssociationReferenceRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.AssociationReferenceRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.AttributeValueRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.AttributeValueRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.ComplexMapRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ComplexMapRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ConceptListJpa;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.DescriptionList;
import org.ihtsdo.otf.ts.helpers.DescriptionListJpa;
import org.ihtsdo.otf.ts.helpers.DescriptionTypeRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.DescriptionTypeRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.LanguageRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.LanguageRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.ModuleDependencyRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ModuleDependencyRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.RefsetDescriptorRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.RefsetDescriptorRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.RelationshipList;
import org.ihtsdo.otf.ts.helpers.RelationshipListJpa;
import org.ihtsdo.otf.ts.helpers.ReleaseInfoList;
import org.ihtsdo.otf.ts.helpers.ReleaseInfoListJpa;
import org.ihtsdo.otf.ts.helpers.SimpleMapRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.SimpleMapRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.SimpleRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.SimpleRefSetMemberListJpa;
import org.ihtsdo.otf.ts.jpa.ReleaseInfoJpa;
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
import org.ihtsdo.otf.ts.rf2.jpa.ComplexMapRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionTypeRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.LanguageRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ModuleDependencyRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RefsetDescriptorRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RelationshipJpa;
import org.ihtsdo.otf.ts.rf2.jpa.SimpleMapRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.SimpleRefSetMemberJpa;

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
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/history/concept/"
            + terminology + "/" + date);
    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null
        ? new PfsParameterJpa() : pfs);
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
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/concept/revisions/" + id + "/" + startDate + "/"
            + endDate + "/all");

    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null
        ? new PfsParameterJpa() : pfs);
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
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/description/" + terminology + "/" + date);
    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null
        ? new PfsParameterJpa() : pfs);
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
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/description/revisions/" + id + "/" + startDate + "/"
            + endDate + "/all");

    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null
        ? new PfsParameterJpa() : pfs);
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
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/relationship/" + terminology + "/" + date);
    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null
        ? new PfsParameterJpa() : pfs);
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
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/relationship/revisions/" + id + "/" + startDate + "/"
            + endDate + "/all");

    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null
        ? new PfsParameterJpa() : pfs);
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
            + "/history/relationship/revisions/" + id + "/" + release + "/release");

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
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/history/language/"
            + terminology + "/" + date);
    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null
        ? new PfsParameterJpa() : pfs);
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
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/language/revisions/" + id + "/" + startDate + "/"
            + endDate + "/all");

    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null
        ? new PfsParameterJpa() : pfs);
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
   * @see org.ihtsdo.otf.ts.rest.HistoryServiceRest#
   * findAssociationReferenceRefSetMembersModifiedSinceDate(java.lang.String,
   * java.lang.String, org.ihtsdo.otf.ts.helpers.PfsParameterJpa,
   * java.lang.String)
   */
  @Override
  public AssociationReferenceRefSetMemberList findAssociationReferenceRefSetMembersModifiedSinceDate(
    String terminology, String date, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/history/associationReference/"
            + terminology + "/" + date);
    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null
        ? new PfsParameterJpa() : pfs);
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
    AssociationReferenceRefSetMemberListJpa list =
        (AssociationReferenceRefSetMemberListJpa) ConfigUtility.getGraphForString(
            resultString, AssociationReferenceRefSetMemberListJpa.class);

    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#findAssociationReferenceRefSetMemberRevisions
   * (java.lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  public AssociationReferenceRefSetMemberList findAssociationReferenceRefSetMemberRevisions(String id,
    String startDate, String endDate, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/associationReference/revisions/" + id + "/" + startDate + "/"
            + endDate + "/all");

    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null
        ? new PfsParameterJpa() : pfs);
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
    AssociationReferenceRefSetMemberListJpa list =
        (AssociationReferenceRefSetMemberListJpa) ConfigUtility.getGraphForString(
            resultString, AssociationReferenceRefSetMemberListJpa.class);

    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.HistoryServiceRest#
   * findAssociationReferenceRefSetMemberReleaseRevision(java.lang.String, java.lang.String,
   * java.lang.String)
   */
  @Override
  public AssociationReferenceRefSetMember findAssociationReferenceRefSetMemberReleaseRevision(
    String id, String release, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/associationReference/revisions/" + id + "/" + release);

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
    AssociationReferenceRefSetMember member =
        (AssociationReferenceRefSetMember<?>) ConfigUtility.getGraphForString(resultString,
            AssociationReferenceRefSetMember.class);

    return member;
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
  public AttributeValueRefSetMemberList findAttributeValueRefSetMembersModifiedSinceDate(
    String terminology, String date, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/history/attributeValue/"
            + terminology + "/" + date);
    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null
        ? new PfsParameterJpa() : pfs);
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
    AttributeValueRefSetMemberListJpa list =
        (AttributeValueRefSetMemberListJpa) ConfigUtility.getGraphForString(
            resultString, AttributeValueRefSetMemberListJpa.class);

    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#findAttributeValueRefSetMemberRevisions
   * (java.lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  public AttributeValueRefSetMemberList findAttributeValueRefSetMemberRevisions(String id,
    String startDate, String endDate, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/attributeValue/revisions/" + id + "/" + startDate + "/"
            + endDate + "/all");

    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null
        ? new PfsParameterJpa() : pfs);
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
    AttributeValueRefSetMemberListJpa list =
        (AttributeValueRefSetMemberListJpa) ConfigUtility.getGraphForString(
            resultString, AttributeValueRefSetMemberListJpa.class);

    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.HistoryServiceRest#
   * findAttributeValueRefSetMemberReleaseRevision(java.lang.String, java.lang.String,
   * java.lang.String)
   */
  @Override
  public AttributeValueRefSetMember findAttributeValueRefSetMemberReleaseRevision(
    String id, String release, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/attributeValue/revisions/" + id + "/" + release);

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
    AttributeValueRefSetMember<?> member =
        (AttributeValueRefSetMember<?>) ConfigUtility.getGraphForString(resultString,
            AttributeValueRefSetMember.class);

    return member;
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
  public ComplexMapRefSetMemberList findComplexMapRefSetMembersModifiedSinceDate(
    String terminology, String date, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/history/complexMap/"
            + terminology + "/" + date);
    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null
        ? new PfsParameterJpa() : pfs);
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
    ComplexMapRefSetMemberListJpa list =
        (ComplexMapRefSetMemberListJpa) ConfigUtility.getGraphForString(
            resultString, ComplexMapRefSetMemberListJpa.class);

    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#findComplexMapRefSetMemberRevisions
   * (java.lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  public ComplexMapRefSetMemberList findComplexMapRefSetMemberRevisions(String id,
    String startDate, String endDate, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/complexMap/revisions/" + id + "/" + startDate + "/"
            + endDate + "/all");

    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null
        ? new PfsParameterJpa() : pfs);
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
    ComplexMapRefSetMemberListJpa list =
        (ComplexMapRefSetMemberListJpa) ConfigUtility.getGraphForString(
            resultString, ComplexMapRefSetMemberListJpa.class);

    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.HistoryServiceRest#
   * findComplexMapRefSetMemberReleaseRevision(java.lang.String, java.lang.String,
   * java.lang.String)
   */
  @Override
  public ComplexMapRefSetMember findComplexMapRefSetMemberReleaseRevision(
    String id, String release, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/complexMap/revisions/" + id + "/" + release);

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
    ComplexMapRefSetMemberJpa member =
        (ComplexMapRefSetMemberJpa) ConfigUtility.getGraphForString(resultString,
            ComplexMapRefSetMemberJpa.class);

    return member;
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
  public DescriptionTypeRefSetMemberList findDescriptionTypeRefSetMembersModifiedSinceDate(
    String terminology, String date, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/history/descriptionType/"
            + terminology + "/" + date);
    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null
        ? new PfsParameterJpa() : pfs);
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
    DescriptionTypeRefSetMemberListJpa list =
        (DescriptionTypeRefSetMemberListJpa) ConfigUtility.getGraphForString(
            resultString, DescriptionTypeRefSetMemberListJpa.class);

    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#findDescriptionTypeRefSetMemberRevisions
   * (java.lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  public DescriptionTypeRefSetMemberList findDescriptionTypeRefSetMemberRevisions(String id,
    String startDate, String endDate, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/descriptionType/revisions/" + id + "/" + startDate + "/"
            + endDate + "/all");

    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null
        ? new PfsParameterJpa() : pfs);
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
    DescriptionTypeRefSetMemberListJpa list =
        (DescriptionTypeRefSetMemberListJpa) ConfigUtility.getGraphForString(
            resultString, DescriptionTypeRefSetMemberListJpa.class);

    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.HistoryServiceRest#
   * findDescriptionTypeRefSetMemberReleaseRevision(java.lang.String, java.lang.String,
   * java.lang.String)
   */
  @Override
  public DescriptionTypeRefSetMember findDescriptionTypeRefSetMemberReleaseRevision(
    String id, String release, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/descriptionType/revisions/" + id + "/" + release);

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
    DescriptionTypeRefSetMemberJpa member =
        (DescriptionTypeRefSetMemberJpa) ConfigUtility.getGraphForString(resultString,
            DescriptionTypeRefSetMemberJpa.class);

    return member;
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
  public ModuleDependencyRefSetMemberList findModuleDependencyRefSetMembersModifiedSinceDate(
    String terminology, String date, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/history/moduleDependency/"
            + terminology + "/" + date);
    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null
        ? new PfsParameterJpa() : pfs);
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
    ModuleDependencyRefSetMemberListJpa list =
        (ModuleDependencyRefSetMemberListJpa) ConfigUtility.getGraphForString(
            resultString, ModuleDependencyRefSetMemberListJpa.class);

    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#findModuleDependencyRefSetMemberRevisions
   * (java.lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  public ModuleDependencyRefSetMemberList findModuleDependencyRefSetMemberRevisions(String id,
    String startDate, String endDate, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/moduleDependency/revisions/" + id + "/" + startDate + "/"
            + endDate + "/all");

    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null
        ? new PfsParameterJpa() : pfs);
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
    ModuleDependencyRefSetMemberListJpa list =
        (ModuleDependencyRefSetMemberListJpa) ConfigUtility.getGraphForString(
            resultString, ModuleDependencyRefSetMemberListJpa.class);

    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.HistoryServiceRest#
   * findModuleDependencyRefSetMemberReleaseRevision(java.lang.String, java.lang.String,
   * java.lang.String)
   */
  @Override
  public ModuleDependencyRefSetMember findModuleDependencyRefSetMemberReleaseRevision(
    String id, String release, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/moduleDependency/revisions/" + id + "/" + release);

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
    ModuleDependencyRefSetMemberJpa member =
        (ModuleDependencyRefSetMemberJpa) ConfigUtility.getGraphForString(resultString,
            ModuleDependencyRefSetMemberJpa.class);

    return member;
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
  public RefsetDescriptorRefSetMemberList findRefsetDescriptorRefSetMembersModifiedSinceDate(
    String terminology, String date, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/history/refsetDescriptor/"
            + terminology + "/" + date);
    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null
        ? new PfsParameterJpa() : pfs);
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
    RefsetDescriptorRefSetMemberListJpa list =
        (RefsetDescriptorRefSetMemberListJpa) ConfigUtility.getGraphForString(
            resultString, RefsetDescriptorRefSetMemberListJpa.class);

    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#findRefsetDescriptorRefSetMemberRevisions
   * (java.lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  public RefsetDescriptorRefSetMemberList findRefsetDescriptorRefSetMemberRevisions(String id,
    String startDate, String endDate, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/refsetDescriptor/revisions/" + id + "/" + startDate + "/"
            + endDate + "/all");

    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null
        ? new PfsParameterJpa() : pfs);
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
    RefsetDescriptorRefSetMemberListJpa list =
        (RefsetDescriptorRefSetMemberListJpa) ConfigUtility.getGraphForString(
            resultString, RefsetDescriptorRefSetMemberListJpa.class);

    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.HistoryServiceRest#
   * findRefsetDescriptorRefSetMemberReleaseRevision(java.lang.String, java.lang.String,
   * java.lang.String)
   */
  @Override
  public RefsetDescriptorRefSetMember findRefsetDescriptorRefSetMemberReleaseRevision(
    String id, String release, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/refsetDescriptor/revisions/" + id + "/" + release);

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
    RefsetDescriptorRefSetMemberJpa member =
        (RefsetDescriptorRefSetMemberJpa) ConfigUtility.getGraphForString(resultString,
            RefsetDescriptorRefSetMemberJpa.class);

    return member;
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
  public SimpleMapRefSetMemberList findSimpleMapRefSetMembersModifiedSinceDate(
    String terminology, String date, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/history/simpleMap/"
            + terminology + "/" + date);
    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null
        ? new PfsParameterJpa() : pfs);
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
    SimpleMapRefSetMemberListJpa list =
        (SimpleMapRefSetMemberListJpa) ConfigUtility.getGraphForString(
            resultString, SimpleMapRefSetMemberListJpa.class);

    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#findSimpleMapRefSetMemberRevisions
   * (java.lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  public SimpleMapRefSetMemberList findSimpleMapRefSetMemberRevisions(String id,
    String startDate, String endDate, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/simpleMap/revisions/" + id + "/" + startDate + "/"
            + endDate + "/all");

    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null
        ? new PfsParameterJpa() : pfs);
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
    SimpleMapRefSetMemberListJpa list =
        (SimpleMapRefSetMemberListJpa) ConfigUtility.getGraphForString(
            resultString, SimpleMapRefSetMemberListJpa.class);

    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.HistoryServiceRest#
   * findSimpleMapRefSetMemberReleaseRevision(java.lang.String, java.lang.String,
   * java.lang.String)
   */
  @Override
  public SimpleMapRefSetMember findSimpleMapRefSetMemberReleaseRevision(
    String id, String release, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/simpleMap/revisions/" + id + "/" + release);

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
    SimpleMapRefSetMemberJpa member =
        (SimpleMapRefSetMemberJpa) ConfigUtility.getGraphForString(resultString,
            SimpleMapRefSetMemberJpa.class);

    return member;
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
  public SimpleRefSetMemberList findSimpleRefSetMembersModifiedSinceDate(
    String terminology, String date, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/history/simple/"
            + terminology + "/" + date);
    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null
        ? new PfsParameterJpa() : pfs);
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
    SimpleRefSetMemberListJpa list =
        (SimpleRefSetMemberListJpa) ConfigUtility.getGraphForString(
            resultString, SimpleRefSetMemberListJpa.class);

    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.HistoryServiceRest#findSimpleRefSetMemberRevisions
   * (java.lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  public SimpleRefSetMemberList findSimpleRefSetMemberRevisions(String id,
    String startDate, String endDate, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/simple/revisions/" + id + "/" + startDate + "/"
            + endDate + "/all");

    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null
        ? new PfsParameterJpa() : pfs);
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
    SimpleRefSetMemberListJpa list =
        (SimpleRefSetMemberListJpa) ConfigUtility.getGraphForString(
            resultString, SimpleRefSetMemberListJpa.class);

    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.HistoryServiceRest#
   * findSimpleRefSetMemberReleaseRevision(java.lang.String, java.lang.String,
   * java.lang.String)
   */
  @Override
  public SimpleRefSetMember findSimpleRefSetMemberReleaseRevision(
    String id, String release, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/history/simple/revisions/" + id + "/" + release);

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
    SimpleRefSetMemberJpa member =
        (SimpleRefSetMemberJpa) ConfigUtility.getGraphForString(resultString,
            SimpleRefSetMemberJpa.class);

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
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/history/concept/"
            + terminology + "/" + date + "/deep");
    String pfsString =
        ConfigUtility.getStringForGraph(pfs == null
        ? new PfsParameterJpa() : pfs);
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
    // TODO Auto-generated method stub

  }

  @Override
  public void processRf2Release(String releaseVersion, String terminology,
    String outputDir, String moduleId, String authToken) throws Exception {
    // TODO Auto-generated method stub

  }

  @Override
  public void finishRf2Release(String releaseVersion, String terminology,
    String authToken) throws Exception {
    // TODO Auto-generated method stub

  }

}
