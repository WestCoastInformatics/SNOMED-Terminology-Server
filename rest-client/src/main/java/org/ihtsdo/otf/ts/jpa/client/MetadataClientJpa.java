package org.ihtsdo.otf.ts.jpa.client;

import java.util.Properties;

import org.ihtsdo.otf.mapping.helpers.KeyValuePairList;
import org.ihtsdo.otf.mapping.helpers.KeyValuePairLists;
import org.ihtsdo.otf.mapping.rest.MetadataServiceRest;

// TODO: Auto-generated Javadoc
/**
 * A client for connecting to a security  REST service.
 */
public class MetadataClientJpa implements MetadataServiceRest {

  /** The config. */
  private Properties config = null;

  /**
   * Instantiates a {@link ContentClientJpa} from the specified parameters.
   *
   * @param config the config
   */
  public MetadataClientJpa(Properties config) {
    this.config = config;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.mapping.rest.MetadataServiceRest#getMetadata(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public KeyValuePairLists getMetadata(String terminology, String version,
    String authToken) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.mapping.rest.MetadataServiceRest#getAllMetadata(java.lang.String, java.lang.String)
   */
  @Override
  public KeyValuePairLists getAllMetadata(String terminology, String authToken)
    throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.mapping.rest.MetadataServiceRest#getAllTerminologiesLatestVersions(java.lang.String)
   */
  @Override
  public KeyValuePairList getAllTerminologiesLatestVersions(String authToken)
    throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.mapping.rest.MetadataServiceRest#getAllTerminologiesVersions(java.lang.String)
   */
  @Override
  public KeyValuePairLists getAllTerminologiesVersions(String authToken)
    throws Exception {
    // TODO Auto-generated method stub
    return null;
  }


}
