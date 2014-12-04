package org.ihtsdo.otf.ts.rest;

import org.ihtsdo.otf.ts.helpers.UserJpa;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.TransitiveRelationship;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RelationshipJpa;
import org.ihtsdo.otf.ts.rf2.jpa.TransitiveRelationshipJpa;

/**
 * Represents a content change service available via a REST service.
 */
public interface ContentChangeServiceRest {

  /**
   * Adds the concept.
   *
   * @param concept the concept
   * @param user the user
   * @param authToken the auth token
   * @return the concept
   * @throws Exception the exception
   */
  public Concept addConcept(ConceptJpa concept, UserJpa user, String authToken) throws Exception;

  /**
   * Update concept.
   *
   * @param concept the concept
   * @param user the user
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void updateConcept(ConceptJpa concept, UserJpa user, String authToken) throws Exception;

  /**
   * Removes the concept.
   *
   * @param id the id
   * @param user the user
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeConcept(Long id, UserJpa user, String authToken) throws Exception;

  /**
   * Adds the description.
   *
   * @param description the description
   * @param user the user
   * @param authToken the auth token
   * @return the description
   * @throws Exception the exception
   */
  public Description addDescription(DescriptionJpa description, UserJpa user, String authToken) throws Exception;

  /**
   * Update description.
   *
   * @param description the description
   * @param user the user
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void updateDescription(DescriptionJpa description, UserJpa user, String authToken) throws Exception;

  /**
   * Removes the description.
   *
   * @param id the id
   * @param user the user
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeDescription(Long id, UserJpa user, String authToken) throws Exception;

  /**
   * Adds the relationship.
   *
   * @param relationship the relationship
   * @param user the user
   * @param authToken the auth token
   * @return the relationship
   * @throws Exception the exception
   */
  public Relationship addRelationship(RelationshipJpa relationship, UserJpa user, String authToken)
    throws Exception;

  /**
   * Update relationship.
   *
   * @param relationship the relationship
   * @param user the user
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void updateRelationship(RelationshipJpa relationship, UserJpa user, String authToken) throws Exception;

  /**
   * Removes the relationship.
   *
   * @param id the id
   * @param user the user
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeRelationship(Long id, UserJpa user, String authToken) throws Exception;

  /**
   * Adds the transitive relationship.
   *
   * @param transitiveRelationship the transitive relationship
   * @param user the user
   * @param authToken the auth token
   * @return the transitive relationship
   * @throws Exception the exception
   */
  public TransitiveRelationship addTransitiveRelationship(
    TransitiveRelationshipJpa transitiveRelationship, UserJpa user, String authToken) throws Exception;

  /**
   * Update transitive relationship.
   *
   * @param transitiveRelationship the transitive relationship
   * @param user the user
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void updateTransitiveRelationship(
    TransitiveRelationshipJpa transitiveRelationship, UserJpa user, String authToken) throws Exception;

  /**
   * Removes the transitive relationship.
   *
   * @param id the id
   * @param user the user
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeTransitiveRelationship(Long id, UserJpa user, String authToken) throws Exception;

  /**
   * Compute transitive closure.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the terminology version
   * @param user the user
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void computeTransitiveClosure(String terminologyId, String terminology, String version, UserJpa user, String authToken)
    throws Exception;

  /**
   * Clear transitive closure.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the terminology version
   * @param user the user
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void clearTransitiveClosure(String terminologyId, String terminology, String version, UserJpa user, String authToken)
    throws Exception;

  /**
   * Removes all concepts and connected data structures.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the terminology version
   * @param user the user
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void clearConcepts(String terminologyId, String terminology, String version, UserJpa user, String authToken) throws Exception;


}
