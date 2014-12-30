package org.ihtsdo.otf.ts.rest;

import org.ihtsdo.otf.ts.Project;
import org.ihtsdo.otf.ts.jpa.ProjectJpa;
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

/**
 * Represents a content change service available via a REST service.
 */
public interface ContentChangeServiceRest {

  /**
   * Adds the concept.
   *
   * @param concept the concept
   * @param authToken the auth token
   * @return the concept
   * @throws Exception the exception
   */
  public Concept addConcept(ConceptJpa concept, String authToken)
    throws Exception;

  /**
   * Update concept.
   *
   * @param concept the concept
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void updateConcept(ConceptJpa concept, String authToken)
    throws Exception;

  /**
   * Removes the concept.
   *
   * @param id the id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeConcept(Long id, String authToken) throws Exception;

  /**
   * Adds the description.
   *
   * @param description the description
   * @param authToken the auth token
   * @return the description
   * @throws Exception the exception
   */
  public Description addDescription(DescriptionJpa description, String authToken)
    throws Exception;

  /**
   * Update description.
   *
   * @param description the description
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void updateDescription(DescriptionJpa description, String authToken)
    throws Exception;

  /**
   * Removes the description.
   *
   * @param id the id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeDescription(Long id, String authToken) throws Exception;

  /**
   * Adds the relationship.
   *
   * @param relationship the relationship
   * @param authToken the auth token
   * @return the relationship
   * @throws Exception the exception
   */
  public Relationship addRelationship(RelationshipJpa relationship,
    String authToken) throws Exception;

  /**
   * Update relationship.
   *
   * @param relationship the relationship
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void updateRelationship(RelationshipJpa relationship, String authToken)
    throws Exception;

  /**
   * Removes the relationship.
   *
   * @param id the id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeRelationship(Long id, String authToken) throws Exception;

  /**
   * Adds the language refset member.
   *
   * @param member the member
   * @param authToken the auth token
   * @return the language refset member
   * @throws Exception the exception
   */
  public LanguageRefSetMember addLanguageRefSetMember(
    LanguageRefSetMemberJpa member, String authToken) throws Exception;

  /**
   * Update language refset member.
   *
   * @param member the member
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void updateLanguageRefSetMember(LanguageRefSetMemberJpa member,
    String authToken) throws Exception;

  /**
   * Removes the language refset member.
   *
   * @param id the id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeLanguageRefSetMember(Long id, String authToken)
    throws Exception;


  /**
   * Adds the association reference refset member.
   *
   * @param member the member
   * @param authToken the auth token
   * @return the AssociationReference refset member
   * @throws Exception the exception
   */
  public AssociationReferenceConceptRefSetMember addAssociationConceptReferenceRefSetMember(
    AssociationReferenceConceptRefSetMemberJpa member, String authToken) throws Exception;

  /**
   * Update association reference refset member.
   *
   * @param member the member
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void updateAssociationReferenceConceptRefSetMember(AssociationReferenceConceptRefSetMemberJpa member,
    String authToken) throws Exception;

  /**
   * Removes the association reference refset member.
   *
   * @param id the id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeAssociationReferenceRefSetMember(Long id, String authToken)
    throws Exception;
  
  /**
   * Compute transitive closure.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the terminology version
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void computeTransitiveClosure(String terminologyId,
    String terminology, String version, String authToken) throws Exception;

  /**
   * Clear transitive closure.
   *
   * @param terminology the terminology
   * @param version the terminology version
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void clearTransitiveClosure(String terminology, String version,
    String authToken) throws Exception;

  /**
   * Removes all concepts and connected data structures.
   *
   * @param terminology the terminology
   * @param version the terminology version
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void clearConcepts(String terminology, String version, String authToken)
    throws Exception;


  /**
   * Adds the project.
   *
   * @param project the project
   * @param authToken the auth token
   * @return the project
   * @throws Exception 
   */
  public Project addProject(ProjectJpa project, String authToken) throws Exception;
  
  /**
   * Update project.
   *
   * @param project the project
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void updateProject(ProjectJpa project, String authToken) throws 
  Exception;
  
  /**
   * Removes the project.
   *
   * @param id the id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeProject(Long id, String authToken) throws Exception;
  
}
