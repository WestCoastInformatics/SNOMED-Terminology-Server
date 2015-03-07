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

  private static String authToken;

  /**
   * Create test fixtures per test.
   *
   * @throws Exception the exception
   */
  @Before
  public void setup() throws Exception {

    // authentication
    authToken = securityService.authenticate(testUser, testPassword);
  }

  /**
   * Test retrieval of all versions for all terminologies
   * @throws Exception
   */
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
   * different API call
   */
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
  @Test
  public void testDegenerateUseRestMetadata003() throws Exception {
    
    // test bad authorization
    try {
      metadataService.getMetadata("SNOMEDCT", "latest", "InvalidAuthToken");
      fail("Getting metadata for terminology and version without authorization token did not throw expected exception.");
    } catch (Exception e) {
      // do nothing
    }
    
    // test bad terminology version
    try {
      metadataService.getMetadata("SNOMEDCT",  "InvalidVersion",  authToken);
      fail("Getting metadata for existing terminology with invalid version did not throw expected exception");
    } catch (Exception e) {
      // do nothing
    }
    
    // test bad terminology
    try {
      metadataService.getMetadata("InvalidTerminology",  "InvalidVersion",  authToken);
      fail("Getting metadata for non-existent terminology with invalid version did not throw expected exception");
    } catch (Exception e) {
      // do nothing
    }
    
    
  }

  /**
   * Test retrieving all metadata for latest version of a terminology
   * @throws Exception
   */
  @Test
  public void testDegenerateUseRestMetadata004() throws Exception {

 // test bad authorization
    try {
      metadataService.getAllMetadata("SNOMEDCT", "Invalid token");
      fail("Getting metadata for terminology and version without authorization token did not throw expected exception.");
    } catch (Exception e) {
      // do nothing
    }
    
    // test bad terminology
    try {
      metadataService.getAllMetadata("InvalidTerminology", authToken);
      fail("Getting metadata for non-existent terminology with invalid version did not throw expected exception");
    } catch (Exception e) {
      // do nothing
    }
  }

  /**
   * Teardown.
   *
   * @throws Exception the exception
   */
  @After
  public void teardown() throws Exception {

    // logout
    securityService.logout(authToken);
  }

}
