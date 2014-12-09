package org.ihtsdo.otf.ts.rest;

import org.ihtsdo.otf.ts.helpers.KeyValuesMap;
import org.ihtsdo.otf.ts.helpers.RelationshipList;
import org.ihtsdo.otf.ts.helpers.StringList;

/**
 * Represents an action service available via a REST service.
 */
public interface ActionServiceRest {

  /**
   * Configure action service.
   *
   * @param workflowStatusList the workflow status list
   * @param authToken the auth token
   * @return the sessionToken
   * @throws Exception the exception
   */
  public String configureActionService(StringList workflowStatusList, String authToken) throws Exception;

  /**
   * Gets the progress for any currently-running operations for the specified session token.
   *
   * @param sessionToken the session token
   * @param authToken the auth token
   * @return the progress
   * @throws Exception the exception
   */
  public float getProgress(String sessionToken, String authToken) throws Exception;
  
  /**
   * Cancels any currently-running operations for the specified session token.
   *
   * @param sessionToken the session token
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void cancel(String sessionToken, String authToken) throws Exception;

  /**
   * Prepares data structures for full classification. 
   * This mostly involves building classifier axioms from the data. In theory, this only needs to be done once per session (assuming only add operations).
   *
   * @param sessionToken the session token
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void prepareToClassify(String sessionToken, String authToken) throws Exception;

  /**
   * Verifies that “prepare” successfully completed, and performs a full classification, leaving the classified ontology in memory for later retrieval.
   *
   * @param sessionToken the session token
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void classify(String sessionToken, String authToken) throws Exception;

  /**
   * Verifies that “prepare” and a full classification were performed, obtains changes since last classification run, adds needed axioms, and performs an incremental classification. Note: incremental classification is not supported if changes include retirement or removal of content – only additions are supported.
   *
   * @param sessionToken the session token
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void incrementalClassify(String sessionToken, String authToken) throws Exception;

  /**
   * Gets the classification equivalents.
   *
   * @param sessionToken the session token
   * @param authToken the auth token
   * @return the classification equivalents
   * @throws Exception the exception
   */
  public KeyValuesMap getClassificationEquivalents(String sessionToken, String authToken) throws Exception;

  /**
   * Gets the old inferred relationships.
   *
   * @param sessionToken the session token
   * @param authToken the auth token
   * @return the old inferred relationships
   * @throws Exception the exception
   */
  public RelationshipList getOldInferredRelationships(String sessionToken, String authToken) throws Exception;
  
  /**
   * Gets the new inferred relationships.
   *
   * @param sessionToken the session token
   * @param authToken the auth token
   * @return the new inferred relationships
   * @throws Exception the exception
   */
  public RelationshipList getNewInferredRelationships(String sessionToken, String authToken) throws Exception;

  /**
   * Retire old inferred relationships. Removes inferred relationships never inserted.
   *
   * @param sessionToken the session token
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void retireOldInferredRelationships(String sessionToken, String authToken) throws Exception;
  
  /**
   * Adds the new inferred relationships.
   *
   * @param sessionToken the session token
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void addNewInferredRelationships(String sessionToken, String authToken) throws Exception;

}
