package org.ihtsdo.otf.ts.jpa.client;

import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status.Family;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.Project;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.jpa.ProjectJpa;
import org.ihtsdo.otf.ts.jpa.algo.StartEditingCycleAlgorithm;
import org.ihtsdo.otf.ts.rest.ContentChangeServiceRest;
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
 * A client for connecting to a content change REST service.
 */
public class ContentChangeClientRest implements ContentChangeServiceRest {

  /** The config. */
  private Properties config = null;

  /**
   * Instantiates a {@link ContentChangeClientRest} from the specified
   * parameters.
   *
   * @param config the config
   */
  public ContentChangeClientRest(Properties config) {
    this.config = config;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#getConceptForUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public Concept getConceptForUser(String terminologyId, String terminology,
    String version, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/history/concept/"
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
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#addConcept(org.ihtsdo.otf
   * .ts.rf2.jpa.ConceptJpa, java.lang.String)
   */
  @Override
  public Concept addConcept(ConceptJpa concept, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/edit/concept/add");

    String conceptString =
        (concept != null ? ConfigUtility.getStringForGraph(concept) : null);
    Logger.getLogger(getClass()).debug(conceptString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .put(ClientResponse.class, conceptString);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    ConceptJpa result =
        (ConceptJpa) ConfigUtility.getGraphForString(resultString,
            ConceptJpa.class);

    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#updateConcept(org.ihtsdo
   * .otf.ts.rf2.jpa.ConceptJpa, java.lang.String)
   */
  @Override
  public void updateConcept(ConceptJpa concept, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client
            .resource(config.getProperty("base.url") + "/edit/concept/update");

    String conceptString =
        (concept != null ? ConfigUtility.getStringForGraph(concept) : null);
    Logger.getLogger(getClass()).debug(conceptString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, conceptString);

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
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#removeConcept(java.lang
   * .Long, java.lang.String)
   */
  @Override
  public void removeConcept(Long id, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/edit/concept/remove/" + id);
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
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#addDescription(org.ihtsdo
   * .otf .ts.rf2.jpa.DescriptionJpa, java.lang.String)
   */
  @Override
  public Description addDescription(DescriptionJpa description, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/edit/description/add");

    String descriptionString =
        (description != null ? ConfigUtility.getStringForGraph(description)
            : null);
    Logger.getLogger(getClass()).debug(descriptionString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .put(ClientResponse.class, descriptionString);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    DescriptionJpa result =
        (DescriptionJpa) ConfigUtility.getGraphForString(resultString,
            DescriptionJpa.class);

    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#updateDescription(org.ihtsdo
   * .otf.ts.rf2.jpa.DescriptionJpa, java.lang.String)
   */
  @Override
  public void updateDescription(DescriptionJpa description, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/edit/description/update");

    String descriptionString =
        (description != null ? ConfigUtility.getStringForGraph(description)
            : null);
    Logger.getLogger(getClass()).debug(descriptionString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, descriptionString);

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
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#removeDescription(java.lang
   * .Long, java.lang.String)
   */
  @Override
  public void removeDescription(Long id, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/edit/description/remove/" + id);
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
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#addDescription(org.ihtsdo
   * .otf .ts.rf2.jpa.DescriptionJpa, java.lang.String)
   */
  @Override
  public Relationship addRelationship(RelationshipJpa relationship,
    String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/edit/relationship/add");

    String relationshipString =
        (relationship != null ? ConfigUtility.getStringForGraph(relationship)
            : null);
    Logger.getLogger(getClass()).debug(relationshipString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .put(ClientResponse.class, relationshipString);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    RelationshipJpa result =
        (RelationshipJpa) ConfigUtility.getGraphForString(resultString,
            RelationshipJpa.class);

    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#updateRelationship(org.
   * ihtsdo .otf.ts.rf2.jpa.RelationshipJpa, java.lang.String)
   */
  @Override
  public void updateRelationship(RelationshipJpa relationship, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/edit/relationship/update");

    String relationshipString =
        (relationship != null ? ConfigUtility.getStringForGraph(relationship)
            : null);
    Logger.getLogger(getClass()).debug(relationshipString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, relationshipString);

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
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#removeRelationship(java
   * .lang .Long, java.lang.String)
   */
  @Override
  public void removeRelationship(Long id, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/edit/relationship/remove/" + id);
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
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#addDescription(org.ihtsdo
   * .otf .ts.rf2.jpa.DescriptionJpa, java.lang.String)
   */
  @Override
  public LanguageRefSetMember addLanguageRefSetMember(
    LanguageRefSetMemberJpa member, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/edit/language/add");

    String memberString =
        (member != null ? ConfigUtility.getStringForGraph(member) : null);
    Logger.getLogger(getClass()).debug(memberString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .put(ClientResponse.class, memberString);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    LanguageRefSetMemberJpa result =
        (LanguageRefSetMemberJpa) ConfigUtility.getGraphForString(resultString,
            LanguageRefSetMemberJpa.class);

    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#updateLanguageRefSetMember
   * (org.ihtsdo .otf.ts.rf2.jpa.LanguageRefSetMemberJpa, java.lang.String)
   */
  @Override
  public void updateLanguageRefSetMember(LanguageRefSetMemberJpa member,
    String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/edit/language/update");

    String memberString =
        (member != null ? ConfigUtility.getStringForGraph(member) : null);
    Logger.getLogger(getClass()).debug(memberString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, memberString);

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
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#removeLanguageRefSetMember
   * (java.lang .Long, java.lang.String)
   */
  @Override
  public void removeLanguageRefSetMember(Long id, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/edit/language/remove/" + id);
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
   * @see org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#
   * addAssociationConceptReferenceRefSetMember
   * (org.ihtsdo.otf.ts.rf2.jpa.AssociationReferenceConceptRefSetMemberJpa,
   * java.lang.String)
   */
  @Override
  public AssociationReferenceConceptRefSetMember addAssociationConceptReferenceRefSetMember(
    AssociationReferenceConceptRefSetMemberJpa member, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/edit/associationReference/add");

    String memberString =
        (member != null ? ConfigUtility.getStringForGraph(member) : null);
    Logger.getLogger(getClass()).debug(memberString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .put(ClientResponse.class, memberString);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    AssociationReferenceConceptRefSetMemberJpa result =
        (AssociationReferenceConceptRefSetMemberJpa) ConfigUtility
            .getGraphForString(resultString,
                AssociationReferenceConceptRefSetMemberJpa.class);

    return result;
  }

  /**
   * Update association reference concept ref set member.
   *
   * @param member the member
   * @param authToken the auth token
   * @throws Exception the exception
   */
  @Override
  public void updateAssociationReferenceConceptRefSetMember(
    AssociationReferenceConceptRefSetMemberJpa member, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/edit/associationReference/update");

    String memberString =
        (member != null ? ConfigUtility.getStringForGraph(member) : null);
    Logger.getLogger(getClass()).debug(memberString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, memberString);

    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // do nothing
    } else {
      throw new Exception("Unexpected status " + response.getStatus());
    }

  }

  /**
   * Removes the association reference ref set member.
   *
   * @param id the id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  @Override
  public void removeAssociationReferenceRefSetMember(Long id, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/edit/associationReference/remove/" + id);
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
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#computeTransitiveClosure
   * (java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public void computeTransitiveClosure(String terminologyId,
    String terminology, String version, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/transitive/compute/"
            + terminology + "/" + version + "/" + terminologyId);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).post(ClientResponse.class);

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
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#clearTransitiveClosure(
   * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public void clearTransitiveClosure(String terminology, String version,
    String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/transitive/clear/"
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

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#clearConcepts(java.lang
   * .String, java.lang.String, java.lang.String)
   */
  @Override
  public void clearConcepts(String terminology, String version, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/concept/clear/"
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

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#addProject(org.ihtsdo.otf
   * .ts.jpa.ProjectJpa, java.lang.String)
   */
  @Override
  public Project addProject(ProjectJpa project, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/edit/project/add");

    String projectString =
        (project != null ? ConfigUtility.getStringForGraph(project) : null);
    Logger.getLogger(getClass()).debug(projectString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .put(ClientResponse.class, projectString);

    String resultString = response.getEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    ProjectJpa result =
        (ProjectJpa) ConfigUtility.getGraphForString(resultString,
            ProjectJpa.class);

    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#updateProject(org.ihtsdo
   * .otf.ts.jpa.ProjectJpa, java.lang.String)
   */
  @Override
  public void updateProject(ProjectJpa project, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client
            .resource(config.getProperty("base.url") + "/edit/project/update");

    String projectString =
        (project != null ? ConfigUtility.getStringForGraph(project) : null);
    Logger.getLogger(getClass()).debug(projectString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, projectString);

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
   * org.ihtsdo.otf.ts.rest.ContentChangeServiceRest#removeProject(java.lang
   * .Long, java.lang.String)
   */
  @Override
  public void removeProject(Long id, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/edit/project/remove/" + id);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).delete(ClientResponse.class);

    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // do nothing
    } else {
      throw new Exception("Unexpected status " + response.getStatus());
    }

  }

  @Override
  public void startEditingCycle(String terminology, String version,
    String releaseVersion, String authToken) throws Exception {

    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/edit/begin/"
            + terminology + "/" + version + "/" + releaseVersion);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).delete(ClientResponse.class);

    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {

      StartEditingCycleAlgorithm algorithm =
          new StartEditingCycleAlgorithm(releaseVersion, terminology, version);
      algorithm.compute();
    }

  }

  @Override
  public void removeReleaseInfos(String terminology, String releaseInfoNames) {
    // TODO Auto-generated method stub

  }

  @Override
  public void releaseBegin(String releaseVersion, String terminology,
    String workflowStatusValues, boolean validate, boolean saveIdentifiers)
    throws Exception {
    // TODO Auto-generated method stub

  }

  @Override
  public void releaseProcess(String refSetId, String outputDirName,
    String effectiveTime, String moduleId) throws Exception {
    // TODO Auto-generated method stub

  }

  @Override
  public void releaseFinish(String releaseVersion, String terminology,
    String workflowStatusValues, boolean validate, boolean saveIdentifiers)
    throws Exception {
    // TODO Auto-generated method stub

  }

}
