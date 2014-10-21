package org.ihtsdo.otf.ts.rest;

import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.rf2.Concept;

/**
 * Represents a content available via a REST service.
 */
public interface ContentServiceRest {

  /**
   * Returns the concept for the specified parameters.
   * As there may be multiple simltaneous versions of the same concept
   * this returns a list.
   * The returned concept(s) include descriptions language refsets 
   * and relationships.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the terminology version
   * @param authToken the auth token
   * @return the concept
   * @throws Exception if anything goes wrong
   */
  public ConceptList getConcepts(String terminologyId, String terminology,
    String version, String authToken) throws Exception;

  /**
   * Returns the single concept for the specified parameters.
   * If there are more than one, it throws an exception.
   * The returned concept includes descriptions language refsets 
   * and relationships.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the single concept
   * @throws Exception if there are more than one matching concepts.
   */
  public Concept getSingleConcept(String terminologyId, String terminology,
    String version, String authToken) throws Exception;

  /**
   * Returns the concept for search string.
   *
   * @param terminology the terminology
   * @param version the terminology version
   * @param searchString the lucene search string
   * @param pfs the paging, filtering, sorting parameter
   * @param authToken the auth token
   * @return the concept for id
   * @throws Exception if anything goes wrong
   */
  public SearchResultList findConceptsForQuery(String terminology,
    String version, String searchString, PfsParameterJpa pfs, String authToken)
    throws Exception;

}
