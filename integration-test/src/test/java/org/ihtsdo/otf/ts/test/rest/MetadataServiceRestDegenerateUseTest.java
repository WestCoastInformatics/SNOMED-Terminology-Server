package org.ihtsdo.otf.ts.test.rest;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Implementation of the "Metadata Service REST Degenerate Use" Test Cases.
 */
public class MetadataServiceRestDegenerateUseTest extends
    MetadataServiceRestTest {

  /**  The auth token. */
  private static String authToken;

  /**
   * Create test fixtures per test.
   *
   * @throws Exception the exception
   */
  @Override
  @Before
  public void setup() throws Exception {

    // authentication
    authToken = securityService.authenticate(testUser, testPassword);
  }

  /**
   * Test retrieval of all versions for all terminologies
   * @throws Exception
   */
  @SuppressWarnings("static-method")
  @Test
  public void testDegenerateUseRestMetadata001() throws Exception {
    try {
      metadataService.getAllTerminologiesVersions("InvalidAuthToken");
      fail("Getting all terminology/version pairs without authorization token succeeded.");
    } catch (Exception e) {
      // do nothing
    }

  }

  /**
   * Tests retrieval of all terminology and latest version pairs
   * 
   * NOTE: Test is identical to testDegenerateUseRestMetadata001 but uses
   * different API call.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @Test
  public void testDegenerateUseRestMetadata002() throws Exception {

    // test bad authorization
    try {
      metadataService.getAllTerminologiesLatestVersions("InvalidAuthToken");
      fail("Getting latest version for all terminologies without authorization token succeeded.");
    } catch (Exception e) {
      // do nothing
    }
  }

  /**
   * Test retrieving all metadata for a terminology
   * @throws Exception
   */
  @SuppressWarnings("static-method")
  @Test
  public void testDegenerateUseRestMetadata003() throws Exception {
    
    // test bad authorization
    try {
      metadataService.getAllMetadata("SNOMEDCT", "latest", "InvalidAuthToken");
      fail("Getting metadata for terminology and version without authorization token did not throw expected exception.");
    } catch (Exception e) {
      // do nothing
    }
    
    // test bad terminology version
    try {
      metadataService.getAllMetadata("SNOMEDCT",  "InvalidVersion",  authToken);
      fail("Getting metadata for existing terminology with invalid version did not throw expected exception");
    } catch (Exception e) {
      // do nothing
    }
    
    // test bad terminology
    try {
      metadataService.getAllMetadata("InvalidTerminology",  "InvalidVersion",  authToken);
      fail("Getting metadata for non-existent terminology with invalid version did not throw expected exception");
    } catch (Exception e) {
      // do nothing
    }
    
    
  }

  /**
   * Test retrieving all metadata for latest version of a terminology
   * @throws Exception
   */
  @SuppressWarnings("static-method")
  @Test
  public void testDegenerateUseRestMetadata004() throws Exception {

     // n/a - this method was removed
  }

  /**
   * Teardown.
   *
   * @throws Exception the exception
   */
  @Override
  @After
  public void teardown() throws Exception {

    // logout
    securityService.logout(authToken);
  }

}
