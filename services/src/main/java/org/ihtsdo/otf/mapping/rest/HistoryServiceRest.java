package org.ihtsdo.otf.mapping.rest;

import org.ihtsdo.otf.mapping.helpers.PfsParameter;
import org.ihtsdo.otf.mapping.helpers.SearchResultList;

/**
 * Represents a security available via a REST service.
 */
public interface HistoryServiceRest {
  /**
   * Finds the concepts that have changed since some point in time.
   *
   * @param terminology the terminology
   * @param terminologyVersion the terminology version
   * @param authToken the auth token
   * @param pfsParameter the pfs parameter
   * @return the search result list
   * @throws Exception if anything goes wrong
   */

  public SearchResultList findDeltaConceptsForTerminology(String terminology,
    String terminologyVersion, String authToken, PfsParameter pfsParameter)
    throws Exception;

}
