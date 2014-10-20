package org.ihtsdo.otf.ts.rest;

import org.ihtsdo.otf.ts.helpers.KeyValuePairList;
import org.ihtsdo.otf.ts.helpers.KeyValuePairLists;

/**
 * Represents a security available via a REST service.
 */
public interface MetadataServiceRest {

  /**
   * Returns all metadata for a terminology and version
   * 
   * @param terminology the terminology
   * @param version the version
   * @param authToken
   * @return the all metadata
   * @throws Exception if anything goes wrong
   */
  public KeyValuePairLists getMetadata(String terminology, String version,
    String authToken) throws Exception;

  /**
   * Returns all metadata for the latest version.
   * 
   * @param terminology the terminology
   * @param authToken
   * @return the metadata
   * @throws Exception if anything goes wrong
   */

  public KeyValuePairLists getAllMetadata(String terminology, String authToken)
    throws Exception;

  /**
   * Returns all terminologies with only their latest version
   * @param authToken
   * @return the all terminologies latest versions
   * @throws Exception if anything goes wrong
   */
  public KeyValuePairList getAllTerminologiesLatestVersions(String authToken)
    throws Exception;

  /**
   * Returns all terminologies and all versions
   * @param authToken
   * @return all terminologies and versions
   * @throws Exception if anything goes wrong
   */

  public KeyValuePairLists getAllTerminologiesVersions(String authToken)
    throws Exception;

}
