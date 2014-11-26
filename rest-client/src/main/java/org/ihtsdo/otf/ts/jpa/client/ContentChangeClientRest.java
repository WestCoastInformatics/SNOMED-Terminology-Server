package org.ihtsdo.otf.ts.jpa.client;

import java.util.Properties;

import org.ihtsdo.otf.ts.rest.ContentChangeServiceRest;

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

  // TODO: implement rest services as they are added

}
