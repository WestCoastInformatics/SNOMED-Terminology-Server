package org.ihtsdo.otf.ts.jpa.client;

import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status.Family;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.AssociationReferenceRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.AssociationReferenceRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.AttributeValueRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.AttributeValueRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.ComplexMapRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ComplexMapRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ConceptListJpa;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
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
import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.helpers.SearchResultListJpa;
import org.ihtsdo.otf.ts.helpers.SimpleMapRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.SimpleMapRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.SimpleRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.SimpleRefSetMemberListJpa;
import org.ihtsdo.otf.ts.rest.ContentServiceRest;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * A client for connecting to a content REST service.
 */
public class ContentClientRest implements ContentServiceRest {

  /** The config. */
  private Properties config = null;

  /**
   * Instantiates a {@link ContentClientRest} from the specified parameters.
   *
   * @param config the config
   */
  public ContentClientRest(Properties config) {
    this.config = config;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.rest.ContentServiceRest#getConcept(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public ConceptList getConcepts(String terminologyId, String terminology,
    String version, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/content/concepts/"
            + terminology + "/" + version + "/" + terminologyId);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    ConceptListJpa list =
        (ConceptListJpa) ConfigUtility.getGraphForString(resultString,
            ConceptListJpa.class);
    return list;
  }

  /*
   * Commented out as not part of change service (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getConceptForUser(java.lang.String
   * , java.lang.String, java.lang.String, java.lang.String)
   * 
   * @Override public Concept getConceptForUser(String terminologyId, String
   * terminology, String version, String authToken) throws Exception { Client
   * client = Client.create(); WebResource resource =
   * client.resource(config.getProperty("base.url") + "/content/concept/" +
   * terminology + "/" + version + "/" + terminologyId + "/foruser");
   * ClientResponse response = resource.accept(MediaType.APPLICATION_XML)
   * .header("Authorization", authToken).get(ClientResponse.class);
   * 
   * String resultString = response.getEntity(String.class); if
   * (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
   * Logger.getLogger(getClass()).debug(resultString); } else { throw new
   * Exception(resultString); }
   * 
   * // converting to object ConceptJpa concept = (ConceptJpa)
   * ConfigUtility.getGraphForString(resultString, ConceptJpa.class); return
   * concept; }
   */
  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getSingleConcept(java.lang.String
   * , java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public Concept getSingleConcept(String terminologyId, String terminology,
    String version, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/content/concept/"
            + terminology + "/" + version + "/" + terminologyId);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    ConceptJpa c =
        (ConceptJpa) ConfigUtility.getGraphForString(resultString,
            ConceptJpa.class);
    return c;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.rest.ContentServiceRest#findConceptsForQuery(java
   * .lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public SearchResultList findConceptsForQuery(String terminology,
    String version, String searchString, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/content/concepts/"
            + terminology + "/" + version + "/query/" + searchString);
    String pfsString =
        (pfs != null ? ConfigUtility.getStringForGraph(pfs) : null);
    Logger.getLogger(getClass()).debug(pfsString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, pfsString);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    SearchResultListJpa list =
        (SearchResultListJpa) ConfigUtility.getGraphForString(resultString,
            SearchResultListJpa.class);
    return list;
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
  public ConceptList findDescendantConcepts(String terminologyId,
    String terminology, String version, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/content/concepts/"
            + terminology + "/" + version + "/" + terminologyId
            + "/descendants");
    String pfsString =
        (pfs != null ? ConfigUtility.getStringForGraph(pfs) : null);
    Logger.getLogger(getClass()).debug(pfsString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, pfsString);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
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
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getChildConcepts(java.lang.String
   * , java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  public ConceptList findChildConcepts(String terminologyId,
    String terminology, String version, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/content/concepts/"
            + terminology + "/" + version + "/" + terminologyId + "/children");
    String pfsString =
        (pfs != null ? ConfigUtility.getStringForGraph(pfs) : null);
    Logger.getLogger(getClass()).debug(pfsString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, pfsString);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
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
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getAncestorConcepts(java.lang
   * .String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  public ConceptList findAncestorConcepts(String terminologyId,
    String terminology, String version, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/content/concepts/"
            + terminology + "/" + version + "/" + terminologyId + "/ancestors");
    String pfsString =
        (pfs != null ? ConfigUtility.getStringForGraph(pfs) : null);
    Logger.getLogger(getClass()).debug(pfsString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, pfsString);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
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
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getAncestorConcepts(java.lang
   * .String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  public ConceptList getParentConcepts(String terminologyId,
    String terminology, String version, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/content/concepts/"
            + terminology + "/" + version + "/" + terminologyId + "/parents");
    String pfsString =
        (pfs != null ? ConfigUtility.getStringForGraph(pfs) : null);
    Logger.getLogger(getClass()).debug(pfsString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, pfsString);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
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
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getDescription(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public Description getDescription(String terminologyId, String terminology,
    String version, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/content/description/" + terminology + "/" + version + "/"
            + terminologyId);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
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
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#luceneReindex(java.lang
   * .String, java.lang.String)
   */
  @Override
  public void luceneReindex(String indexedObjects, String authToken)
    throws Exception {
    Client client = Client.create();

    WebResource resource =
        client.resource(config.getProperty("base.url") + "/content/reindex");
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.TEXT_PLAIN)
            .post(ClientResponse.class, indexedObjects);

    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // do nothing
    } else {
      if (response.getStatus() != 204)
        throw new Exception("Unexpected status " + response.getStatus());
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
  public void loadTerminologyRf2Snapshot(String terminology, String version,
    String inputDir, String authToken) throws Exception {

    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/terminology/load/rf2/snapshot" + terminology + "/" + version);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .put(ClientResponse.class, inputDir);

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
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#loadTerminologyRf2Full(java.lang
   * .String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public void loadTerminologyRf2Full(String terminology, String version,
    String inputDir, String authToken) throws Exception {

    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/terminology/load/rf2/full" + terminology + "/" + version);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .put(ClientResponse.class, inputDir);

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
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#loadTerminologyRf2Delta(java.
   * lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public void loadTerminologyRf2Delta(String terminology, String inputDir,
    String authToken) throws Exception {

    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/terminology/load/rf2/snapshot" + terminology);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .put(ClientResponse.class, inputDir);

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
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#loadTerminologyClaml(java.lang
   * .String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public void loadTerminologyClaml(String terminology, String version,
    String inputFile, String authToken) throws Exception {

    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/terminology/load/claml/" + terminology + "/" + version);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .put(ClientResponse.class, inputFile);

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
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#computeTransitiveClosure(java
   * .lang.String, java.lang.String)
   */
  @Override
  public void computeTransitiveClosure(String terminology, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/content/terminology/closure/compute/" + terminology);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .get(ClientResponse.class);

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
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#removeTerminology(java.lang.String
   * , java.lang.String, java.lang.String)
   */
  @Override
  public void removeTerminology(String terminology, String version,
    String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/terminology/remove/"
            + terminology + "/" + version);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .delete(ClientResponse.class);

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
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#findAssociationReferenceRefSetMembers
   * (java.lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  public AssociationReferenceRefSetMemberList findAssociationReferenceRefSetMembers(
    String refSetId, String terminology, String version, PfsParameterJpa pfs,
    String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/content/associationReferenceMember/refSet/" + terminology + "/"
            + version + "/" + refSetId);
    String pfsString =
        (pfs != null ? ConfigUtility.getStringForGraph(pfs) : null);
    Logger.getLogger(getClass()).debug(pfsString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, pfsString);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    AssociationReferenceRefSetMemberListJpa list =
        (AssociationReferenceRefSetMemberListJpa) ConfigUtility
            .getGraphForString(resultString,
                AssociationReferenceRefSetMemberListJpa.class);
    return list;
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
  public AttributeValueRefSetMemberList findAttributeValueRefSetMembers(
    String refSetId, String terminology, String version, PfsParameterJpa pfs,
    String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/content/attributeValueMember/refSet/" + terminology + "/"
            + version + "/" + refSetId);
    String pfsString =
        (pfs != null ? ConfigUtility.getStringForGraph(pfs) : null);
    Logger.getLogger(getClass()).debug(pfsString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, pfsString);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
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
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#findComplexMapRefSetMembers(java
   * .lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  public ComplexMapRefSetMemberList findComplexMapRefSetMembers(
    String refSetId, String terminology, String version, PfsParameterJpa pfs,
    String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/content/complexMapMember/refSet/" + terminology + "/" + version
            + "/" + refSetId);
    String pfsString =
        (pfs != null ? ConfigUtility.getStringForGraph(pfs) : null);
    Logger.getLogger(getClass()).debug(pfsString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, pfsString);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
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
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#findDescriptionTypeRefSetMembers
   * (java.lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  public DescriptionTypeRefSetMemberList findDescriptionTypeRefSetMembers(
    String refSetId, String terminology, String version, PfsParameterJpa pfs,
    String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/content/descriptionTypeMember/refSet/" + terminology + "/"
            + version + "/" + refSetId);
    String pfsString =
        (pfs != null ? ConfigUtility.getStringForGraph(pfs) : null);
    Logger.getLogger(getClass()).debug(pfsString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, pfsString);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
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
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#findLanguageRefSetMembers(java
   * .lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  public LanguageRefSetMemberList findLanguageRefSetMembers(String refSetId,
    String terminology, String version, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/content/languageMember/refSet/" + terminology + "/" + version
            + "/" + refSetId);
    String pfsString =
        (pfs != null ? ConfigUtility.getStringForGraph(pfs) : null);
    Logger.getLogger(getClass()).debug(pfsString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, pfsString);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
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
   * @see org.ihtsdo.otf.ts.rest.ContentServiceRest#
   * getModuleDependencyRefSetMembersForModule(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public ModuleDependencyRefSetMemberList getModuleDependencyRefSetMembersForModule(
    String moduleId, String terminology, String version, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/content/moduleDependencyMember/module/" + terminology + "/"
            + version + "/" + moduleId);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
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
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getRefsetDescriptorRefSetMembers
   * (java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public RefsetDescriptorRefSetMemberList getRefsetDescriptorRefSetMembers(
    String refSetId, String terminology, String version, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/content/refsetDescriptorMember/refSet/" + terminology + "/"
            + version + "/" + refSetId);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
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
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#findSimpleMapRefSetMembers(java
   * .lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  public SimpleMapRefSetMemberList findSimpleMapRefSetMembers(String refSetId,
    String terminology, String version, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/content/simpleMapMember/refSet/" + terminology + "/" + version
            + "/" + refSetId);
    String pfsString =
        (pfs != null ? ConfigUtility.getStringForGraph(pfs) : null);
    Logger.getLogger(getClass()).debug(pfsString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, pfsString);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
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
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#findSimpleRefSetMembers(java.
   * lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameterJpa, java.lang.String)
   */
  @Override
  public SimpleRefSetMemberList findSimpleRefSetMembers(String refSetId,
    String terminology, String version, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/content/simpleMember/refSet/" + terminology + "/" + version
            + "/" + refSetId);
    String pfsString =
        (pfs != null ? ConfigUtility.getStringForGraph(pfs) : null);
    Logger.getLogger(getClass()).debug(pfsString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, pfsString);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
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
   * @see org.ihtsdo.otf.ts.rest.ContentServiceRest#
   * getAssociationReferenceRefSetMembersForConcept(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public AssociationReferenceRefSetMemberList getAssociationReferenceRefSetMembersForConcept(
    String terminologyId, String terminology, String version, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/content/associationReferenceMember/concept/" + terminology
            + "/" + version + "/" + terminologyId);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    AssociationReferenceRefSetMemberListJpa list =
        (AssociationReferenceRefSetMemberListJpa) ConfigUtility
            .getGraphForString(resultString,
                AssociationReferenceRefSetMemberListJpa.class);
    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.ContentServiceRest#
   * getAssociationReferenceRefSetMembersForDescription(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public AssociationReferenceRefSetMemberList getAssociationReferenceRefSetMembersForDescription(
    String terminologyId, String terminology, String version, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/content/associationReferenceMember/description/" + terminology
            + "/" + version + "/" + terminologyId);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    AssociationReferenceRefSetMemberListJpa list =
        (AssociationReferenceRefSetMemberListJpa) ConfigUtility
            .getGraphForString(resultString,
                AssociationReferenceRefSetMemberListJpa.class);
    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.ContentServiceRest#
   * getAttributeValueRefSetMembersForConcept(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public AttributeValueRefSetMemberList getAttributeValueRefSetMembersForConcept(
    String terminologyId, String terminology, String version, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/content/attributeValueMember/concept/" + terminology + "/"
            + version + "/" + terminologyId);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
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
   * @see org.ihtsdo.otf.ts.rest.ContentServiceRest#
   * getAttributeValueRefSetMembersForDescription(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public AttributeValueRefSetMemberList getAttributeValueRefSetMembersForDescription(
    String terminologyId, String terminology, String version, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/content/attributeValueMember/description/" + terminology + "/"
            + version + "/" + terminologyId);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
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
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getComplexMapRefSetMembersForConcept
   * (java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public ComplexMapRefSetMemberList getComplexMapRefSetMembersForConcept(
    String terminologyId, String terminology, String version, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/content/complexMapMember/concept/" + terminology + "/"
            + version + "/" + terminologyId);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
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
   * @see org.ihtsdo.otf.ts.rest.ContentServiceRest#
   * getLanguageRefSetMembersForDescription(java.lang.String, java.lang.String,
   * java.lang.String, java.lang.String)
   */
  @Override
  public LanguageRefSetMemberList getLanguageRefSetMembersForDescription(
    String terminologyId, String terminology, String version, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/content/languageMember/description/" + terminology + "/"
            + version + "/" + terminologyId);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
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
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getSimpleMapRefSetMembersForConcept
   * (java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public SimpleMapRefSetMemberList getSimpleMapRefSetMembersForConcept(
    String terminologyId, String terminology, String version, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/content/simpleMapMember/concept/" + terminology + "/" + version
            + "/" + terminologyId);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
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
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getSimpleRefSetMembersForConcept
   * (java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public SimpleRefSetMemberList getSimpleRefSetMembersForConcept(
    String terminologyId, String terminology, String version, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/content/simpleMember/concept/" + terminology + "/" + version
            + "/" + terminologyId);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
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
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getInverseRelationshipsForConcept
   * (java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public RelationshipList getInverseRelationshipsForConcept(
    String terminologyId, String terminology, String version, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/content/relationship/inverse/" + terminology + "/" + version
            + "/" + terminologyId);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    RelationshipListJpa list =
        (RelationshipListJpa) ConfigUtility.getGraphForString(resultString,
            RelationshipListJpa.class);
    return list;
  }

}
