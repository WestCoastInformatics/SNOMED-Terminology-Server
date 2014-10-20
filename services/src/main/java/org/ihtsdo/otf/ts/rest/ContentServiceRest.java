package org.ihtsdo.otf.ts.rest;

import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.rf2.Concept;

/**
 * Represents a content available via a REST service.
 */
public interface ContentServiceRest {

  /**
   * Returns the concept for the specified parameters.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param terminologyVersion the terminology version
   * @param authToken the auth token
   * @return the concept
   * @throws Exception if anything goes wrong
   */
  public Concept getConcept(String terminologyId, String terminology,
    String terminologyVersion, String authToken) throws Exception;

  /**
   * Returns the concept for search string.
   *
   * @param terminology the terminology
   * @param terminologyVersion the terminology version
   * @param searchString the lucene search string
   * @param authToken the auth token
   * @return the concept for id
   * @throws Exception if anything goes wrong
   */
  public SearchResultList findConceptsForQuery(String terminology,
    String terminologyVersion, String searchString, String authToken)
    throws Exception;

  /**
   * Returns the descendants of a concept as mapped by relationships and inverse
   * relationships
   * 
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param terminologyVersion the terminology version
   * @param authToken
   * @return the search result list
   * @throws Exception if anything goes wrong
   */

  public SearchResultList findDescendantConcepts(String terminologyId,
    String terminology, String terminologyVersion, String authToken)
    throws Exception;

  /**
   * Find child concepts.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param terminologyVersion the terminology version
   * @param authToken the auth token
   * @return the search result list
   * @throws Exception if anything goes wrong
   */
  public SearchResultList findChildConcepts(String terminologyId,
    String terminology, String terminologyVersion, String authToken)
    throws Exception;

}
