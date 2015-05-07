/*
 * Copyright 2015 West Coast Informatics, LLC
 */
/*
 * 
 */
package org.ihtsdo.otf.ts.test.rest;

import static org.junit.Assert.assertTrue;

import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.test.helpers.DegenerateUseMethodTestHelper;
import org.ihtsdo.otf.ts.test.helpers.DegenerateUseMethodTestHelper.ExpectedFailure;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Implementation of the "Content Service REST Degenerate Use" Test Cases.
 */
public class ContentServiceRestDegenerateUseTest extends ContentServiceRestTest {

  /** The auth token. */
  private static String authToken;

  /** The test test id. */
  private String testId;

  /** The test terminology. */
  private String terminology;

  /** The test version. */
  private String version;

  /** The valid parameters used for reflection testing. */
  private Object[] validParameters;

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
    terminology = "SNOMEDCT";
    version = "latest";

  }

  /**
   * Test Get and Find methods for concepts.
   *
   * @throws Exception the exception
   */
  @Test
  public void testDegenerateUseRestContent001() throws Exception {

    testId = "121000119106";

    // get concepts
    validParameters = new Object[] {
        testId, terminology, version, authToken
    };

    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod("getConcepts",
            getParameterTypes(validParameters)), validParameters,

        // String fields will fail on empty strings, return no results on null
        // (correct behavior)
        new ExpectedFailure[] {
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.EXCEPTION
        });

    // get single concept
    validParameters = new Object[] {
        testId, terminology, version, authToken
    };
    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod("getSingleConcept",
            getParameterTypes(validParameters)), validParameters,
        // null/empty terminology id always throws exception, terminology,
        // terminology version all return no
        // results
        new ExpectedFailure[] {
            ExpectedFailure.EXCEPTION, ExpectedFailure.EXCEPTION,
            ExpectedFailure.EXCEPTION, ExpectedFailure.EXCEPTION
        });

    // find concepts
    validParameters = new Object[] {
        terminology, version, "ossification", new PfsParameterJpa(), authToken
    };

    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod("findConceptsForQuery",
            getParameterTypes(validParameters)), validParameters,
        // terminology id, terminology, terminology version all return no
        // results for null value
        new ExpectedFailure[] {
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.EXCEPTION, ExpectedFailure.EXCEPTION
        });

    // test for invalid values manually
    try {
      // unparseable terminology
      contentService.findConceptsForQuery("&%$#*", version, "ossification",
          null, authToken);
    } catch (Exception e) {
      assertTrue(e.getClass().equals(IllegalArgumentException.class));
    }

    try {
      // unparseable version
      contentService.findConceptsForQuery(terminology, "&%$#*", "ossification",
          null, authToken);
    } catch (Exception e) {
      assertTrue(e.getClass().equals(IllegalArgumentException.class));
    }

    try {
      // unparseable searchString
      contentService.findConceptsForQuery(terminology, version, "&%$#*", null,
          authToken);
    } catch (Exception e) {
      assertTrue(e.getClass().equals(IllegalArgumentException.class));
    }

  }

  /**
   * Test description services
   *
   * @throws Exception the exception
   */
  @Test
  public void testDegenerateUseRestContent002() throws Exception {

    testId = "121000119106";

    // get a description for testing
    Concept concept =
        contentService
            .getSingleConcept(testId, terminology, version, authToken);
    Description description = concept.getDescriptions().iterator().next();

    // get description
    validParameters = new Object[] {
        description.getTerminologyId(), terminology, version, authToken
    };

    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod("getDescription",
            getParameterTypes(validParameters)), validParameters);
  }

  /**
   * Test relationship services
   *
   * @throws Exception the exception
   */
  @Test
  public void testDegenerateUseRestContent003() throws Exception {
    testId = "121000119106";

    // get a relationship for testing
    Concept concept =
        contentService
            .getSingleConcept(testId, terminology, version, authToken);
    Relationship relationship = concept.getRelationships().iterator().next();

    // get relationship
    validParameters = new Object[] {
        relationship.getTerminologyId(), terminology, version, authToken
    };

    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod("getRelationship",
            getParameterTypes(validParameters)), validParameters);
  }

  /**
   * Test normal use rest for Association Reference Ref Set Members.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseRestContent004() throws Exception {

    // test id of concept with component to be tested
    String testId = "122456005";

    // set parameters
    validParameters = new Object[] {
        testId, terminology, version, authToken
    };

    // test method
    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod(
            "getAssociationReferenceRefSetMembersForConcept",
            getParameterTypes(validParameters)), validParameters,
        new ExpectedFailure[] {
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.EXCEPTION
        });

    // cannot test description method, no test data for valid call
  }

  /**
   * Test Get and Find methods for attribute value ref set members.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseRestContent005() throws Exception {

    // set test id for Attribute Value for Concept
    String testId = "105592009";

    // set parameters
    validParameters = new Object[] {
        testId, terminology, version, authToken
    };

    // test method
    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod(
            "getAttributeValueRefSetMembersForConcept",
            getParameterTypes(validParameters)), validParameters,
        new ExpectedFailure[] {
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.EXCEPTION
        });

    // cannot test attribute value for description, no valid test data for
    // comparison

  }

  /**
   * Test Get and Find methods for complex map ref set members.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseRestContent006() throws Exception {

    // set test ids
    String testId = "121000119106";

    // set parameters
    validParameters = new Object[] {
        testId, terminology, version, authToken
    };

    // test method
    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod(
            "getComplexMapRefSetMembersForConcept",
            getParameterTypes(validParameters)), validParameters,
        new ExpectedFailure[] {
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.EXCEPTION
        });
  }

  /**
   * Test Get and Find methods for description type ref set members.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseRestContent007() throws Exception {

    // set test ids
    String testId = "900000000000550004";

    // set parameters
    validParameters = new Object[] {
        testId, terminology, version, authToken
    };

    // test method
    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod(
            "getDescriptionTypeRefSetMembersForConcept",
            getParameterTypes(validParameters)), validParameters,
        new ExpectedFailure[] {
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.EXCEPTION
        });
  }

  /**
   * Test normal use rest for language ref set members.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseRestContent008() throws Exception {

    // set test ids
    String testId = "513602011";

    // set parameters
    validParameters = new Object[] {
        testId, terminology, version, authToken
    };

    // test method
    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod(
            "getLanguageRefSetMembersForDescription",
            getParameterTypes(validParameters)), validParameters,
        new ExpectedFailure[] {
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.EXCEPTION
        });
  }

  /**
   * Test normal use rest for module dependency refsets.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseRestContent009() throws Exception {

    // set module id
    String testId = "900000000000207008";

    // set parameters
    validParameters = new Object[] {
        testId, terminology, version, authToken
    };

    // test method
    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod(
            "getModuleDependencyRefSetMembersForModule",
            getParameterTypes(validParameters)), validParameters,
        new ExpectedFailure[] {
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.EXCEPTION
        });
  }

  /**
   * Test normal use rest for ref set descriptors.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseRestContent010() throws Exception {

    String testId = "447562003";

    // / set parameters
    validParameters = new Object[] {
        testId, terminology, version, authToken
    };

    // test method
    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod("getRefsetDescriptorRefSetMembers",
            getParameterTypes(validParameters)), validParameters,
        new ExpectedFailure[] {
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.EXCEPTION
        });

  }

  /**
   * Test normal use rest for simple map ref set members.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseRestContent011() throws Exception {

    String testId = "116314006";

    // set parameters
    validParameters = new Object[] {
        testId, terminology, version, authToken
    };

    // test method
    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod(
            "getSimpleMapRefSetMembersForConcept",
            getParameterTypes(validParameters)), validParameters,
        new ExpectedFailure[] {
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.EXCEPTION
        });

  }

  /**
   * Test normal use rest for simple ref set members.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseRestContent012() throws Exception {

    String testId = "116011005";

    // set parameters
    validParameters = new Object[] {
        testId, terminology, version, authToken
    };

    // test method
    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod("getSimpleRefSetMembersForConcept",
            getParameterTypes(validParameters)), validParameters,
        new ExpectedFailure[] {
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.EXCEPTION
        });
  }

  /**
   * Test children, descendant, and ancestor services.
   *
   * @throws Exception the exception
   */
  @Test
  public void testDegenerateUseRestContent013() throws Exception {

    testId = "128117002";

    // parameters are same for all four calls
    validParameters = new Object[] {
        testId, terminology, version, new PfsParameterJpa(), authToken
    };

    // get child concepts
    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod("findChildConcepts",
            getParameterTypes(validParameters)), validParameters);

    // get parent concepts
    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod("findParentConcepts",
            getParameterTypes(validParameters)), validParameters);

    // get descendant concepts
    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod("findDescendantConcepts",
            getParameterTypes(validParameters)), validParameters);

    // get ancestor concepts
    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod("findAncestorConcepts",
            getParameterTypes(validParameters)), validParameters);

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

  /**
   * Returns the parameter types.
   *
   * @param parameters the parameters
   * @return the parameter types
   */
  @SuppressWarnings("static-method")
  public Class<?>[] getParameterTypes(Object[] parameters) {
    Class<?>[] types = new Class<?>[parameters.length];
    for (int i = 0; i < parameters.length; i++) {
      types[i] = parameters[i].getClass();
    }
    return types;
  }

}
