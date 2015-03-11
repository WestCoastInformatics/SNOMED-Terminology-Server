/*
 * 
 */
package org.ihtsdo.otf.ts.test.rest;

import org.ihtsdo.otf.ts.test.helpers.ContentServiceRestDegenerateUseForMethodTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Implementation of the "Content Service REST Degenerate Use" Test Cases.
 */
public class ContentServiceRestDegenerateUseTest extends ContentServiceRestTest {

  /** The auth token. */
  private static String authToken;

  /** The snomed test id. */
  private String snomedTestId;

  /** The snomed terminology. */
  private String snomedTerminology;

  /** The snomed version. */
  private String snomedVersion;

  /** The icd9 test id. */
  private String icd9TestId;

  /** The icd9 terminology. */
  private String icd9Terminology;

  /** The icd9 version. */
  private String icd9Version;

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

    // set terminology and version
    snomedTerminology = "SNOMEDCT";
    snomedVersion = "latest";
    snomedTestId = "-1";

  }

  /**
   * Test Get and Find methods for concepts
   * @throws Exception
   */
  @Test
  public void testDegenerateUseRestContent001() throws Exception {

    // Invalid values used for testing:
    // null
    // String "-1" (except authToken)
    // Valid values used for testing:
    // Terminology: SNOMEDCT
    // Terminology version: latest
    // Terminology id: 121000119106
    
    
    /** Get concepts */
    Object[] validParameters = {snomedTestId, snomedTerminology, snomedVersion, authToken};
    Object[] invalidParameters = {"-1", "-1", "-1", "-1"};
    ContentServiceRestDegenerateUseForMethodTestHelper.testDegenerateArgumentsForServiceMethod(
        contentService, 
        "getConcepts",
        validParameters, 
        invalidParameters);
    
   /* *//**
     * Procedure 1: Get concepts
     * *//*

    *//** invalid terminologyId *//*
    // TEST: null 
    try {
      contentService.getConcepts(null, snomedTerminology, snomedVersion, authToken);
      fail("getConcepts with null terminology id did not throw expected exception");
    } catch (Exception e) {
      // do nothing
    }
    // ﻿TEST:﻿ invalid 
    try {
      contentService.getConcepts(invalidStr, snomedTerminology, snomedVersion, authToken);
      
      fail("getConcepts with invalid terminology id did not throw expected exception");
    } catch (Exception e) {
      // do nothing
    }

    *//** invalid terminology *//*
    // TEST: null 
    try {
      contentService.getConcepts(snomedTestId, null, snomedVersion, authToken);
      
      fail("getConcepts with null terminology did not throw expected exception");
    } catch (Exception e) {
      // do nothing
    }
    // TEST: invalid 
    try {
      contentService.getConcepts(snomedTestId, invalidStr, snomedVersion, authToken);
      
      fail("getConcepts with invalid terminology did not throw expected exception");
    } catch (Exception e) {
      // do nothing
    }

    *//** invalid terminology version *//*
    // TEST: null 
    try {
      contentService.getConcepts(snomedTestId, snomedTerminology, null, authToken);
      
      fail("getConcepts with null version did not throw expected exception");
    } catch (Exception e) {
      // do nothing
    }
    // TEST: invalid 
    try {
      contentService.getConcepts(snomedTestId, snomedTerminology, invalidStr, authToken);
      
      fail("getConcepts with invalid version did not throw expected exception");
    } catch (Exception e) {
      // do nothing
    }

    *//** invalid auth token *//*
    // TEST: null 
    try {
      contentService.getConcepts(snomedTestId, snomedTerminology, snomedVersion, null);
      
      fail("getConcepts with null authToken did not throw expected exception");
    } catch (Exception e) {
      // do nothing
    }
    
    // TEST: invalid 
    try {
      contentService.getConcepts(snomedTestId, snomedTerminology, snomedVersion, invalidStr);
      
      fail("getConcepts with invalid authToken did not throw expected exception");
    } catch (Exception e) {
      // do nothing
    }

    *//**
     * Procedure 2: Get Single Concepts
     *//*

    // Get single concept for:
    // invalid terminologyId
    // TEST: null 
    try {

      fail("did not throw expected exception");
    } catch (Exception e) {
      // do nothing
    }
    // TEST: invalid 
    try {

      fail("did not throw expected exception");
    } catch (Exception e) {
      // do nothing
    }
    // invalid terminology
    // TEST: null 
    try {

      fail("did not throw expected exception");
    } catch (Exception e) {
      // do nothing
    }
    // TEST: invalid 
    try {

      fail("did not throw expected exception");
    } catch (Exception e) {
      // do nothing
    }
    // invalid terminology
    // TEST: null 
    try {

      fail("did not throw expected exception");
    } catch (Exception e) {
      // do nothing
    }
    // TEST: invalid 
    try {

      fail("did not throw expected exception");
    } catch (Exception e) {
      // do nothing
    }
    // invalid auth token
    // TEST: null 
    try {

      fail("did not throw expected exception");
    } catch (Exception e) {
      // do nothing
    }
    // Get concepts by id for:
    // invalid hibernate id
    // ﻿TEST﻿: ﻿null 
    try {

      fail("did not throw expected exception");
    } catch (Exception e) {
      // do nothing
    }
    // ﻿TEST: invalid 
    try {

      fail("did not throw expected exception");
    } catch (Exception e) {
      // do nothing
    }
    // invalid auth token
    // TEST: null 
    try {

      fail("did not throw expected exception");
    } catch (Exception e) {
      // do nothing
    }*/
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
