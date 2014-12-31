package org.ihtsdo.otf.ts.jpa.client;

import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status.Family;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.Project;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ConceptListJpa;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.ProjectList;
import org.ihtsdo.otf.ts.helpers.ProjectListJpa;
import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.helpers.SearchResultListJpa;
import org.ihtsdo.otf.ts.jpa.ProjectJpa;
import org.ihtsdo.otf.ts.rest.ContentServiceRest;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.jpa.AssociationReferenceConceptRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa;
import org.ihtsdo.otf.ts.rf2.jpa.LanguageRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RelationshipJpa;

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
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(resultString);
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
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getConceptForUser(java.lang.String
   * , java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public Concept getConceptForUser(String terminologyId, String terminology,
    String version, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/content/concept/"
            + terminology + "/" + version + "/" + terminologyId + "/foruser");
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
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
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(resultString);
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
   * @see org.ihtsdo.otf.ts.rest.ContentServiceRest#getConcept(java.lang.Long,
   * java.lang.String)
   */
  @Override
  public Concept getConcept(Long id, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/content/concept/id/"
            + id);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(resultString);
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
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, pfsString);

    String resultString = response.getEntity(String.class);
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(resultString);
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
  public ConceptList getDescendantConcepts(String terminologyId,
    String terminology, String version, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/content/concepts/"
            + terminology + "/" + version + "/" + terminologyId
            + "/descendants");
    String pfsString =
        (pfs != null ? ConfigUtility.getStringForGraph(pfs) : null);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, pfsString);

    String resultString = response.getEntity(String.class);
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(resultString);
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
  public ConceptList getChildConcepts(String terminologyId, String terminology,
    String version, PfsParameterJpa pfs, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/content/concepts/"
            + terminology + "/" + version + "/" + terminologyId + "/children");
    String pfsString =
        (pfs != null ? ConfigUtility.getStringForGraph(pfs) : null);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, pfsString);

    String resultString = response.getEntity(String.class);
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(resultString);
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
  public ConceptList getAncestorConcepts(String terminologyId,
    String terminology, String version, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/content/concepts/"
            + terminology + "/" + version + "/" + terminologyId + "/ancestors");
    String pfsString =
        (pfs != null ? ConfigUtility.getStringForGraph(pfs) : null);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, pfsString);

    String resultString = response.getEntity(String.class);
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(resultString);
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
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getDescription(java.lang.Long,
   * java.lang.String)
   */
  @Override
  public Description getDescription(Long id, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/content/description/id/" + id);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(resultString);
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
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(resultString);
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
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getRelationship(java.lang.Long,
   * java.lang.String)
   */
  @Override
  public Relationship getRelationship(Long id, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/content/relationship/id/" + id);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
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
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getRelationship(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public Relationship getRelationship(String terminologyId, String terminology,
    String version, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/content/relationship/" + terminology + "/" + version + "/"
            + terminologyId);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
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
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getLanguageRefSetMember(java.
   * lang.Long, java.lang.String)
   */
  @Override
  public LanguageRefSetMember getLanguageRefSetMember(Long id, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/content/language/id/" + id);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
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
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getLanguageRefSetMember(java.
   * lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public LanguageRefSetMember getLanguageRefSetMember(String terminologyId,
    String terminology, String version, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/content/language/"
            + terminology + "/" + version + "/" + terminologyId);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
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
   * @see org.ihtsdo.otf.ts.rest.ContentServiceRest#
   * getAssociationReferenceConceptRefSetMember(java.lang.Long,
   * java.lang.String)
   */
  @Override
  public AssociationReferenceConceptRefSetMember getAssociationReferenceConceptRefSetMember(
    Long id, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/content/associationReference/id/" + id);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    AssociationReferenceConceptRefSetMemberJpa member =
        (AssociationReferenceConceptRefSetMemberJpa) ConfigUtility
            .getGraphForString(resultString,
                AssociationReferenceConceptRefSetMemberJpa.class);
    return member;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.ContentServiceRest#
   * getAssociationReferenceConceptRefSetMember(java. lang.String,
   * java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public AssociationReferenceConceptRefSetMember getAssociationReferenceConceptRefSetMember(
    String terminologyId, String terminology, String version, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/content/associationReference/" + terminology + "/" + version
            + "/" + terminologyId);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    AssociationReferenceConceptRefSetMemberJpa member =
        (AssociationReferenceConceptRefSetMemberJpa) ConfigUtility
            .getGraphForString(resultString,
                AssociationReferenceConceptRefSetMemberJpa.class);
    return member;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getConceptsInScope(java.lang.
   * Long, java.lang.String)
   */
  @Override
  public ConceptList getConceptsInScope(Long projectId, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/content/project/id/"
            + projectId + "/scope");
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(resultString);
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
   * @see org.ihtsdo.otf.ts.rest.ContentServiceRest#getProject(java.lang.Long,
   * java.lang.String)
   */
  @Override
  public Project getProject(Long id, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/content/project/id/"
            + id);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    ProjectJpa project =
        (ProjectJpa) ConfigUtility.getGraphForString(resultString,
            ProjectJpa.class);
    return project;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentServiceRest#getProjects(java.lang.String)
   */
  @Override
  public ProjectList getProjects(String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/content/project/projects");
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    ProjectListJpa list =
        (ProjectListJpa) ConfigUtility.getGraphForString(resultString,
            ProjectListJpa.class);
    return list;
  }

}
