package org.ihtsdo.otf.ts.services;

import org.ihtsdo.otf.ts.helpers.KeyValuesMap;
import org.ihtsdo.otf.ts.helpers.RelationshipList;
import org.ihtsdo.otf.ts.helpers.StringList;

/**
 * Generically represents a service for performing maintenance actions.
 */
public interface ActionService extends RootService {

  /**
   * Enable listeners.
   */
  public void enableListeners();
  
  /**
   * Disable listeners.
   */
  public void disableListeners();
  
  /**
   * Configure action service.
   *
   * @param workflowStatusList the workflow status list to use when applying actions
   * @return the sessionToken
   * @throws Exception the exception
   */
  public String configureActionService(StringList workflowStatusList) throws Exception;

  /**
   * Gets the progress for any currently-running operations for the specified session token.
   *
   * @param sessionToken the session token
   * @return the progress
   * @throws Exception the exception
   */
  public float getProgress(String sessionToken) throws Exception;
  
  /**
   * Cancels any currently-running operations for the specified session token.
   *
   * @param sessionToken the session token
   * @throws Exception the exception
   */
  public void cancel(String sessionToken) throws Exception;

  /**
   * Prepares data structures for full classification. 
   * This mostly involves building classifier axioms from the data. In theory, this only needs to be done once per session (assuming only add operations).
   *
   * @param sessionToken the session token
   * @throws Exception the exception
   */
  public void prepareToClassify(String sessionToken) throws Exception;

  /**
   * Verifies that “prepare” successfully completed, and performs a full classification, leaving the classified ontology in memory for later retrieval.
   *
   * @param sessionToken the session token
   * @throws Exception the exception
   */
  public void classify(String sessionToken) throws Exception;

  /**
   * Verifies that “prepare” and a full classification were performed, obtains changes since last classification run, adds needed axioms, and performs an incremental classification. Note: incremental classification is not supported if changes include retirement or removal of content – only additions are supported.
   *
   * @param sessionToken the session token
   * @throws Exception the exception
   */
  public void incrementalClassify(String sessionToken) throws Exception;

  /**
   * Gets the classification equivalents.
   *
   * @param sessionToken the session token
   * @return the classification equivalents
   * @throws Exception the exception
   */
  public KeyValuesMap getClassificationEquivalents(String sessionToken) throws Exception;

  /**
   * Gets the old inferred relationships.
   *
   * @param sessionToken the session token
   * @return the old inferred relationships
   * @throws Exception the exception
   */
  public RelationshipList getOldInferredRelationships(String sessionToken) throws Exception;
  
  /**
   * Gets the new inferred relationships.
   *
   * @param sessionToken the session token
   * @return the new inferred relationships
   * @throws Exception the exception
   */
  public RelationshipList getNewInferredRelationships(String sessionToken) throws Exception;


  
}