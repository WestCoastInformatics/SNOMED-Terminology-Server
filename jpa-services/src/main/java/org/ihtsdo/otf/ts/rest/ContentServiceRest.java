/*
 * 
 */
package org.ihtsdo.otf.ts.rest;

import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.PfsParameter;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.rf2.Concept;

// TODO: Auto-generated Javadoc
/**
 * Represents a content available via a REST service.
 *
 * @author ${author}
 */
public interface ContentServiceRest {

  /**
   * Returns the concept for the specified parameters. As there may be multiple
   * simltaneous versions of the same concept this returns a list. The returned
   * concept(s) include descriptions language refsets and relationships.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the concepts
   * @throws Exception the exception
   */
  public ConceptList getConcepts(String terminologyId, String terminology,
    String version, String authToken) throws Exception;

  /**
   * Returns the single concept for the specified parameters. If there are more
   * than one, it throws an exception. The returned concept includes
   * descriptions language refsets and relationships.
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
   * Gets the concept for the specified identifier.
   *
   * @param id the internal concept id.  Used when other REST APIs
   * return information that includes internal identifiers.
   * @param authToken the auth token
   * @return the concept
   * @throws Exception the exception
   */
  public Concept getConcept(Long id, String authToken) throws Exception;
  
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
  
  /**
   * Returns the concept children.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param terminologyVersion the terminology version
   * @param pfs the pfs
   * @return the concept children
   * @throws Exception 
   */
  public ConceptList getConceptChildren(String terminologyId, String terminology, String terminologyVersion, PfsParameter pfs, String authToken) throws Exception;
  
  /**
   * Returns the concept descendants.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param terminologyVersion the terminology version
   * @param pfs the pfs
   * @return the concept descendants
   */
  public ConceptList getConceptDescendants(String terminologyId, String terminology, String terminologyVersion, PfsParameter pfs, String authToken);
  
}
