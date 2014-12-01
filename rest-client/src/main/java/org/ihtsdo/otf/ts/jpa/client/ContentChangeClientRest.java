package org.ihtsdo.otf.ts.jpa.client;

import java.util.Properties;

import org.ihtsdo.otf.ts.helpers.User;
import org.ihtsdo.otf.ts.helpers.UserJpa;
import org.ihtsdo.otf.ts.rest.ContentChangeServiceRest;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.TransitiveRelationship;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RelationshipJpa;
import org.ihtsdo.otf.ts.rf2.jpa.TransitiveRelationshipJpa;

/**
 * A client for connecting to a content change REST service.
 */
public class ContentChangeClientRest implements ContentChangeServiceRest {

  /** The config. */
  @SuppressWarnings("unused")
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

  @Override
  public Concept addConcept(ConceptJpa concept, UserJpa user, String authToken)
    throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void updateConcept(ConceptJpa concept, UserJpa user, String authToken)
    throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void removeConcept(Long id, UserJpa user, String authToken)
    throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Description addDescription(DescriptionJpa description, UserJpa user,
    String authToken) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void updateDescription(DescriptionJpa description, UserJpa user,
    String authToken) throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void removeDescription(Long id, UserJpa user, String authToken)
    throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Relationship addRelationship(RelationshipJpa relationship,
    UserJpa user, String authToken) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void updateRelationship(RelationshipJpa relationship, UserJpa user,
    String authToken) throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void removeRelationship(Long id, UserJpa user, String authToken)
    throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public TransitiveRelationship addTransitiveRelationship(
    TransitiveRelationshipJpa transitiveRelationship, UserJpa user,
    String authToken) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void updateTransitiveRelationship(
    TransitiveRelationshipJpa transitiveRelationship, UserJpa user,
    String authToken) throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void removeTransitiveRelationship(Long id, UserJpa user,
    String authToken) throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void computeTransitiveClosure(String terminologyId,
    String terminology, String version, UserJpa user, String authToken)
    throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void clearTransitiveClosure(String terminologyId, String terminology,
    String version, UserJpa user, String authToken) throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void clearConcepts(String terminologyId, String terminology,
    String version, UserJpa user, String authToken) throws Exception {
    // TODO Auto-generated method stub
    
  }

  // TODO: implement rest services as they are added

}
