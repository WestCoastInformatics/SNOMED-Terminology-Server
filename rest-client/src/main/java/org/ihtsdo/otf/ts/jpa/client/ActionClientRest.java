package org.ihtsdo.otf.ts.jpa.client;

import java.util.Properties;

import org.ihtsdo.otf.ts.rest.ActionServiceRest;

/**
 * A client for connecting to an action REST service.
 */
public class ActionClientRest implements ActionServiceRest {

  /** The config. */
  @SuppressWarnings("unused")
  private Properties config = null;

  /**
   * Instantiates a {@link ActionClientRest} from the specified parameters.
   *
   * @param config the config
   */
  public ActionClientRest(Properties config) {
    this.config = config;
  }

  // TODO: implement rest services as they are added

}
