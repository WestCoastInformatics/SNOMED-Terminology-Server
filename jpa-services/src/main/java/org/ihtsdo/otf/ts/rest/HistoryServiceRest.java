package org.ihtsdo.otf.ts.rest;

import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.SearchResultList;

/**
 * Represents a security available via a REST service.
 */
public interface HistoryServiceRest {
  /**
   * Finds the concepts that have changed since some point in time.
   *
   * @param terminology the terminology
   * @param version the terminology version
   * @param authToken the auth token
   * @param pfsParameter the pfs parameter
   * @return the search result list
   * @throws Exception if anything goes wrong
   */

  public SearchResultList findDeltaConceptsForTerminology(String terminology,
    String version, String authToken, PfsParameterJpa pfsParameter)
    throws Exception;

}
