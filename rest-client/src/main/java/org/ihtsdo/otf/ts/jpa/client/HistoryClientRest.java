package org.ihtsdo.otf.ts.jpa.client;

import java.util.Properties;

import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.rest.HistoryServiceRest;

/**
 * A client for connecting to a security  REST service.
 */
public class HistoryClientRest implements HistoryServiceRest {

  /** The config. */
  @SuppressWarnings("unused")
  private Properties config = null;

  /**
   * Instantiates a {@link ContentClientRest} from the specified parameters.
   *
   * @param config the config
   */
  public HistoryClientRest(Properties config) {
    this.config = config;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.mapping.rest.HistoryServiceRest#findDeltaConceptsForTerminology(java.lang.String, java.lang.String, java.lang.String, org.ihtsdo.otf.mapping.helpers.PfsParameter)
   */
  @Override
  public SearchResultList findDeltaConceptsForTerminology(String terminology,
    String version, String authToken, PfsParameterJpa pfsParameter)
    throws Exception {
    throw new Exception("Not implemented yet.");
  }

}
