/*
 * 
 */
package org.ihtsdo.otf.ts.test.rest;

import static org.junit.Assert.assertTrue;

import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.test.helpers.DegenerateUseMethodTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * Implementation of the "Content Service REST Degenerate Use" Test Cases.
 *
 * @author ${author}
 */
public class ContentServiceRestDegenerateUseTest extends ContentServiceRestTest {

  /** The auth token. */
  private static String authToken;

  /** The test test id. */
  private String testId;

  /** The test terminology. */
  private String testTerminology;

  /** The test version. */
  private String testVersion;

  /** The concept used in testing. */
  private Concept concept;

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
    testTerminology = "SNOMEDCT";
    testVersion = "latest";
    testId = "121000119106";

    // get test concept
    concept =
        contentService.getSingleConcept(testId, testTerminology, testVersion,
            authToken);

  }

  /**
   * Test Get and Find methods for concepts.
   *
   * @throws Exception the exception
   */
  @Test
  public void testDegenerateUseRestContent001() throws Exception {

    /** Concept methods */

    // get concepts
    validParameters = new Object[] {
        testId, testTerminology, testVersion, authToken
    };

    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod("getConcepts",
            getParameterTypes(validParameters)), validParameters,
        new boolean[] {
            false, false, false, true
        });

    // get single concept
    validParameters = new Object[] {
        testId, testTerminology, testVersion, authToken
    };
    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod("getSingleConcept",
            getParameterTypes(validParameters)), validParameters,
        new boolean[] {
            false, false, false, true
        });

    // get concept by id
    validParameters = new Object[] {
        concept.getId(), authToken
    };
    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod("getConcept",
            getParameterTypes(validParameters)), validParameters);

    // find concepts
    validParameters =
        new Object[] {
            testTerminology, testVersion, "ossification",
            new PfsParameterJpa(), authToken
        };

    // Find method helper should not test terminology, version, or search string
    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod("findConceptsForQuery",
            getParameterTypes(validParameters)), validParameters,
        new boolean[] {
            false, false, false, true, true
        });

    // test for invalid values manually
    try {
      // unparseable terminology
      contentService.findConceptsForQuery("&%$#*", testVersion, "ossification",
          null, authToken);
    } catch (Exception e) {
      assertTrue(e.getClass().equals(IllegalArgumentException.class));
    }

    try {
      // unparseable version
      contentService.findConceptsForQuery(testTerminology, "&%$#*",
          "ossification", null, authToken);
    } catch (Exception e) {
      assertTrue(e.getClass().equals(IllegalArgumentException.class));
    }

    try {
      // unparseable searchString
      contentService.findConceptsForQuery(testTerminology, testVersion,
          "&%$#*", null, authToken);
    } catch (Exception e) {
      assertTrue(e.getClass().equals(IllegalArgumentException.class));
    }

  }

  /**
   * Test children, descendant, and ancestor services.
   *
   * @throws Exception the exception
   */
  @Test
  public void testDegenerateUseRestContent002() throws Exception {
    // get child concepts
    validParameters = new Object[] {
        testId, testTerminology, testVersion, new PfsParameterJpa(), authToken
    };
    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod("getChildConcepts",
            getParameterTypes(validParameters)), validParameters);

    // get descendant concepts
    validParameters = new Object[] {
        testId, testTerminology, testVersion, new PfsParameterJpa(), authToken
    };
    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod("getDescendantConcepts",
            getParameterTypes(validParameters)), validParameters);

    // get ancestor concepts
    validParameters = new Object[] {
        testId, testTerminology, testVersion, new PfsParameterJpa(), authToken
    };
    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod("getAncestorConcepts",
            getParameterTypes(validParameters)), validParameters);

  }

  /**
   * Test description services
   *
   * @throws Exception the exception
   */
  @Test
  public void testDegenerateUseRestContent003() throws Exception {

    Description description = concept.getDescriptions().iterator().next();

    // get description
    validParameters = new Object[] {
        description.getTerminologyId(), testTerminology, testVersion, authToken
    };

    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod("getDescription",
            getParameterTypes(validParameters)), validParameters);

    // get description
    validParameters = new Object[] {
        description.getId(), authToken
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
  public void testDegenerateUseRestContent004() throws Exception {

    Relationship relationship = concept.getRelationships().iterator().next();

    // get relationship
    validParameters = new Object[] {
        relationship.getTerminologyId(), testTerminology, testVersion, authToken
    };

    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod("getRelationship",
            getParameterTypes(validParameters)), validParameters);

    // get relationship
    validParameters = new Object[] {
        relationship.getId(), authToken
    };

    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod("getRelationship",
            getParameterTypes(validParameters)), validParameters);
  }
  
  /**
   * Test description services
   *
   * @throws Exception the exception
   */
  @Test
  public void testDegenerateUseRestContent005() throws Exception {

    Description description = concept.getDescriptions().iterator().next();
    LanguageRefSetMember language = description.getLanguageRefSetMembers().iterator().next();

    // get language
    validParameters = new Object[] {
        language.getTerminologyId(), testTerminology, testVersion, authToken
    };

    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod("getLanguageRefSetMember",
            getParameterTypes(validParameters)), validParameters);

    // get language
    validParameters = new Object[] {
        language.getId(), authToken
    };

    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod("getLanguageRefSetMember",
            getParameterTypes(validParameters)), validParameters);
  }
  
  
  /**
   * Test retrieval of SNOMEDCT refsetMembers
   * NOTE:  Ref Set Member id hardcoded, as concept's set is @XmlTransient
   */
  /**
   * Test relationship services
   *
   * @throws Exception the exception
   */
  @Test
  public void testDegenerateUseRestContent006() throws Exception {

    String refSetMemberTerminologyId = "d9835599-19ac-56bd-89ad-18b37713dfbd";

    AssociationReferenceConceptRefSetMember refsetMember = 
        contentService.getAssociationReferenceConceptRefSetMember(
            refSetMemberTerminologyId, 
            testTerminology, testVersion, authToken);

    // get relationship
    validParameters = new Object[] {
        refsetMember.getTerminologyId(), testTerminology, testVersion, authToken
    };

    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod("getAssociationReferenceConceptRefSetMember",
            getParameterTypes(validParameters)), validParameters);

    // get relationship
    validParameters = new Object[] {
        refsetMember.getId(), authToken
    };

    DegenerateUseMethodTestHelper.testDegenerateArguments(
        contentService,
        contentService.getClass().getMethod("getAssociationReferenceConceptRefSetMember",
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
  public Class<?>[] getParameterTypes(Object[] parameters) {
    Class<?>[] types = new Class<?>[parameters.length];
    for (int i = 0; i < parameters.length; i++) {
      types[i] = parameters[i].getClass();
    }
    return types;
  }

}