package org.ihtsdo.otf.ts.jpa.client;

import java.util.Properties;

import org.ihtsdo.otf.ts.helpers.KeyValuePairList;
import org.ihtsdo.otf.ts.helpers.KeyValuePairLists;
import org.ihtsdo.otf.ts.rest.MetadataServiceRest;

/**
 * A client for connecting to a security  REST service.
 */
public class MetadataClientRest implements MetadataServiceRest {

  /** The config. */
  @SuppressWarnings("unused")
  private Properties config = null;

  /**
   * Instantiates a {@link ContentClientRest} from the specified parameters.
   *
   * @param config the config
   */
  public MetadataClientRest(Properties config) {
    this.config = config;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.mapping.rest.MetadataServiceRest#getMetadata(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public KeyValuePairLists getMetadata(String terminology, String version,
    String authToken) throws Exception {
    return null;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.mapping.rest.MetadataServiceRest#getAllMetadata(java.lang.String, java.lang.String)
   */
  @Override
  public KeyValuePairLists getAllMetadata(String terminology, String authToken)
    throws Exception {

    return null;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.mapping.rest.MetadataServiceRest#getAllTerminologiesLatestVersions(java.lang.String)
   */
  @Override
  public KeyValuePairList getAllTerminologiesLatestVersions(String authToken)
    throws Exception {

    return null;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.mapping.rest.MetadataServiceRest#getAllTerminologiesVersions(java.lang.String)
   */
  @Override
  public KeyValuePairLists getAllTerminologiesVersions(String authToken)
    throws Exception {

    return null;
  }


}
