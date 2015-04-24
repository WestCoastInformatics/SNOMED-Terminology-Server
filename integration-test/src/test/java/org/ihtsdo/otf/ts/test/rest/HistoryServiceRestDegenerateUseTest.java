/*
 * Copyright 2015 West Coast Informatics, LLC
 */
/*
 * 
 */
package org.ihtsdo.otf.ts.test.rest;

import java.lang.reflect.Method;

import org.ihtsdo.otf.ts.helpers.LocalException;
import org.ihtsdo.otf.ts.helpers.PfsParameter;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.rf2.Component;
import org.ihtsdo.otf.ts.rf2.jpa.AbstractAssociationReferenceRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.AbstractAttributeValueRefSetMemberJpa;
import org.ihtsdo.otf.ts.services.ContentService;
import org.ihtsdo.otf.ts.test.helpers.DegenerateUseMethodTestHelper;
import org.ihtsdo.otf.ts.test.helpers.DegenerateUseMethodTestHelper.ExpectedFailure;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Implementation of the "History Service REST Degenerate Use" Test Cases.
 */
public class HistoryServiceRestDegenerateUseTest extends HistoryServiceRestTest {

  // override content service, will instantiate as jpa-level accessor
  ContentService contentService;

  Object[] validParameters;
  
  PfsParameter pfs;

  /**
   * Create test fixtures per test.
   *
   * @throws Exception the exception
   */
  @Override
  @Before
  public void setup() throws Exception {

    // ensure terminology correctly set
    terminology = "SNOMEDCT";
    version = "latest";
    
    // default paging (prevent large data results)
    pfs = new PfsParameterJpa();
    pfs.setMaxResults(1);
    pfs.setStartIndex(0);

    // authentication -- use admin for this test
    authToken = securityService.authenticate(adminUser, adminPassword);

    // use jpa-level content service to retrieve items for validity testing
    // i.e. quick retrieval of hibernate ids
    contentService = new ContentServiceJpa();
  }

  /**
   * Test release info methods
   * 
   * @throws Exception
   */
  @Test
  public void testDegenerateUseRestHistory001() throws Exception {

    // parameters identical for first four tests
    validParameters = new Object[] {
        terminology, authToken
    };

    // getCurrentReleaseInfo(String, String)
    DegenerateUseMethodTestHelper.testDegenerateArguments(
        historyService,
        historyService.getClass().getMethod("getCurrentReleaseInfo",
            getParameterTypes(validParameters)), validParameters,

        // Terminology field will return null (succeed) with null terminology
        // but will fail with empty string
        new ExpectedFailure[] {
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_SUCCESS,
            ExpectedFailure.EXCEPTION
        });

    // getPlannedReleaseInfo(String, String)
    DegenerateUseMethodTestHelper.testDegenerateArguments(
        historyService,
        historyService.getClass().getMethod("getPlannedReleaseInfo",
            getParameterTypes(validParameters)), validParameters,

        // Empty string will throw exception, null terminology 
        // will return null (succeed)
        new ExpectedFailure[] {
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_SUCCESS,
            ExpectedFailure.EXCEPTION
        });

    // getPreviousReleaseInfo(String, String)
    DegenerateUseMethodTestHelper.testDegenerateArguments(
        historyService,
        historyService.getClass().getMethod("getPreviousReleaseInfo",
            getParameterTypes(validParameters)), validParameters,

        // String fields will fail on empty strings, return no results on null
        // (correct behavior)
        new ExpectedFailure[] {
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_SUCCESS,
            ExpectedFailure.EXCEPTION
        });

    // getReleaseHistory(String, String)
    DegenerateUseMethodTestHelper.testDegenerateArguments(
        historyService,
        historyService.getClass().getMethod("getReleaseHistory",
            getParameterTypes(validParameters)), validParameters,

        // String fields will fail on empty strings, return no results on null
        // (correct behavior)
        new ExpectedFailure[] {
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_SUCCESS,
            ExpectedFailure.EXCEPTION
        });

    // getReleaseInfo(String, String, String)
    validParameters = new Object[] {
        terminology, "20080731", authToken
    };

    DegenerateUseMethodTestHelper.testDegenerateArguments(
        historyService,
        historyService.getClass().getMethod("getReleaseInfo",
            getParameterTypes(validParameters)), validParameters,

        // String fields will fail on empty strings, return no results on null
        // (correct behavior)
        new ExpectedFailure[] {
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_SUCCESS,
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_SUCCESS,
            ExpectedFailure.EXCEPTION
        });

  }

  /**
   * Test add/update/remove releaseInfo use rest history0011.
   * 
   * NOTE: ReleaseInfo methods should be in test 001. Due to minor issue with
   * JDBC connection pools, normal use tests currently placed in test 002
   * (editing cycle methods), which are skipped as authoring-related elements
   * are not functional. THerefore, they are not tested there, nor here.
   *
   * @throws Exception the exception
   */

  @Test
  @Ignore
  public void testDegenerateUseRestHistory002() throws Exception {

    // intentionally left blank, see note above
  }

  /**
   * Test concept methods
   * @throws Exception
   */
  @Test
  public void testDegenerateUseRestHistory003() throws Exception {

    objectNames.remove("Concept");

    // The test id for this concept
    String testId = "321087001";
    String revision = "20080131";
    
    
    
    Component component =
        contentService.getSingleConcept(testId, terminology, version);
  
    testDegenerateUse(component, revision);
    
    // concept also has deep modified routine
    Method method =
        historyService.getClass().getMethod(
            "findConceptsDeepModifiedSinceDate",
            new Class<?>[] {
                String.class, String.class, PfsParameterJpa.class, String.class
            });

    Object[] parameters = new Object[] {
        terminology, "20080131", pfs, authToken
    };
    
    // note that null terminology returns a null object (success)
    DegenerateUseMethodTestHelper.testDegenerateArguments(historyService, method, parameters, new ExpectedFailure[] {
        ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_SUCCESS,
        ExpectedFailure.EXCEPTION,
        ExpectedFailure.EXCEPTION, ExpectedFailure.EXCEPTION
    });
    

  }

  /**
   * Test description methods
   * @throws Exception
   */
  @Test
  public void testDegenerateUseRestHistory004() throws Exception {

    objectNames.remove("Description");

    // The test id for this description
    String testId = "387512014";
    String revision = "20030731";

    Component component =
        contentService.getDescription(testId, terminology, version);
    testDegenerateUse(component, revision);

  }

  /**
   * Test relationship methods
   * @throws Exception
   */
  @Test
  public void testDegenerateUseRestHistory005() throws Exception {

    objectNames.remove("Relationship");

    // The test id for this component
    String testId = "501314023";
    String revision = "20020131";

    Component component =
        contentService.getRelationship(testId, terminology, version);
    testDegenerateUse(component, revision);

  }

  /**
   * Test language ref set methods
   * @throws Exception
   */
  @Test
  public void testDegenerateUseRestHistory006() throws Exception {

    objectNames.remove("LanguageRefSetMember");

    // The test id for this component
    String testId = "e76075bd-3748-589f-b987-2ada691921ef";
    String revision = "20080731";

    Component component =
        contentService.getLanguageRefSetMember(testId, terminology, version);
    testDegenerateUse(component, revision);
  }

  /**
   * Test association reference ref set methods
   * @throws Exception
   */
  @Test
  public void testDegenerateUseRestHistory007() throws Exception {

    objectNames.remove("AssociationReferenceRefSetMember");

    // The test id for this association reference ref set member
    String testId = "cf39de4d-bbb9-59c3-b049-82a6c31ce87a";
    String revision = "20080731";

    // need cast to abstract form (two types of association reference ref set member)
    Component component = (AbstractAssociationReferenceRefSetMemberJpa<?>)
        contentService.getAssociationReferenceRefSetMember(testId, terminology,
            version);
    testDegenerateUse(component, revision);
  }

  /**
   * Test attribute value ref set methods
   * @throws Exception
   */
  @Test
  public void testDegenerateUseRestHistory008() throws Exception {

    objectNames.remove("AttributeValueRefSetMember");

    // The test id for this component
    String testId = "de184ffc-03f3-5dae-9f2b-6c46020ede8a";
    String revision = "20050131";

    // need cast to abstract form (two types of attribute value ref set member)
    Component component = (AbstractAttributeValueRefSetMemberJpa<?>)
        contentService.getAttributeValueRefSetMember(testId, terminology,
            version);
    testDegenerateUse(component, revision);
  }

  /**
   * Test complex map ref set methods
   * @throws Exception
   */
  @Test
  public void testDegenerateUseRestHistory009() throws Exception {

    objectNames.remove("ComplexMapRefSetMember");

    // The test id for this component
    String testId = "055c30ae-b018-5b82-bd3d-74ff9451de15";
    String revision = "20060731";

    Component component =
        contentService.getComplexMapRefSetMember(testId, terminology, version);
    testDegenerateUse(component, revision);
  }

  /**
   * Test description type ref set methods
   * @throws Exception
   */
  @Test
  public void testDegenerateUseRestHistory010() throws Exception {

    objectNames.remove("DescriptionTypeRefSetMember");

    // The test id for this component
    String testId = "807f775b-1d66-5069-b58e-a37ace985dcf";
    String revision = "20140131";

    Component component =
        contentService.getDescriptionTypeRefSetMember(testId, terminology,
            version);
    testDegenerateUse(component, revision);
  }

  /**
   * Test module dependency ref set methods
   * @throws Exception
   */
  @Test
  public void testDegenerateUseRestHistory011() throws Exception {

    objectNames.remove("ModuleDependencyRefSetMember");

    // The test id for this component
    String testId = "1244116f-fdb5-5645-afcc-5281288409da";
    String revision = "20140131";

    Component component =
        contentService.getModuleDependencyRefSetMember(testId, terminology,
            version);
    testDegenerateUse(component, revision);
  }

  /**
   * Test refset descriptor ref set methods
   * @throws Exception
   */
  @Test
  public void testDegenerateUseRestHistory012() throws Exception {

    objectNames.remove("RefsetDescriptorRefSetMember");

    // The test id for this component
    String testId = "576ed8c3-1227-5489-b782-760a3b729b94";
    String revision = "20020131";

    Component component =
        contentService.getRefsetDescriptorRefSetMember(testId, terminology,
            version);
    testDegenerateUse(component, revision);
  }

  /**
   * Test simple map ref set member methods
   * @throws Exception
   */
  @Test
  public void testDegenerateUseRestHistory013() throws Exception {

    objectNames.remove("SimpleMapRefSetMember");

    // The test id for this component
    String testId = "f8dbedb7-12a7-5e33-bbc8-3d1252c77820";
    String revision = "20050131";

    Component component =
        contentService.getSimpleMapRefSetMember(testId, terminology, version);
    testDegenerateUse(component, revision);
  }

  /**
   * Test simple ref set member methods
   * @throws Exception
   */
  @Test
  public void testDegenerateUseRestHistory014() throws Exception {

    objectNames.remove("SimpleRefSetMember");

    // The test id for this component
    String testId = "c0a26c19-e051-5cfa-86c5-a625ca44e690";
    String revision = "20060131";

    Component component =
        contentService.getSimpleRefSetMember(testId, terminology, version);
    testDegenerateUse(component, revision);
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
   * Test degenerate use.
   *
   * @param component the component used for id retrieval
   * @param revision an existing revision date of the component
   * @throws Exception the exception
   */
  private void testDegenerateUse(Component component, String revision)
    throws Exception {

    // get the class name
    Class<? extends Component> clazz = component.getClass();

    testDegenerateUseModifiedSinceDateMethod(clazz);
    testDegenerateUseRevisionsMethod(clazz, component);
    testDegenerateUseReleaseRevisionMethod(clazz, component, revision);
  }

  /**
   * Test modified since date method for degenerate use.
   *
   * @param clazz the clazz
   * @throws Exception the exception
   */
  private void testDegenerateUseModifiedSinceDateMethod(Class<?> clazz)
    throws Exception {

    Method method =
        historyService.getClass().getMethod(
            "find" + getClassShortName(clazz) + "sModifiedSinceDate",
            new Class<?>[] {
                String.class, String.class, PfsParameterJpa.class, String.class
            });

    Object[] parameters = new Object[] {
        terminology, "19700101", pfs, authToken
    };

    // blank and null terminologies will both throw exceptions
    DegenerateUseMethodTestHelper.testDegenerateArguments(historyService,
        method, parameters, new ExpectedFailure[] {
            ExpectedFailure.STRING_INVALID_EXCEPTION_NULL_NO_RESULTS,
            ExpectedFailure.EXCEPTION,
            ExpectedFailure.EXCEPTION, ExpectedFailure.EXCEPTION
        });
  }

  /**
   * Test revisions method for degenerate use.
   *
   * @param clazz the clazz
   * @throws Exception
   * @throws LocalException
   */
  private void testDegenerateUseRevisionsMethod(Class<?> clazz,
    Component component) throws LocalException, Exception {

    Method method =
        historyService.getClass().getMethod(
            "find" + getClassShortName(clazz) + "Revisions",
            new Class<?>[] {
                String.class, String.class, String.class,
                PfsParameterJpa.class, String.class
            });

    Object[] parameters =
        new Object[] {
            component.getId().toString(), "19700101", "20150131", pfs,
            authToken
        };

    // terminology does not have invalid value
    DegenerateUseMethodTestHelper.testDegenerateArguments(historyService,
        method, parameters);

  }

  /**
   * Test release revision method for degenerate use.
   *
   * @param clazz the clazz
   * @throws Exception
   * @throws LocalException
   */
  private void testDegenerateUseReleaseRevisionMethod(Class<?> clazz,
    Component component, String revision) throws LocalException, Exception {
    Method method =
        historyService.getClass().getMethod(
            "find" + getClassShortName(clazz) + "ReleaseRevision",
            new Class<?>[] {
                String.class, String.class, String.class
            });

    Object[] parameters = new Object[] {
        component.getId().toString(), revision, authToken
    };

    // terminology does not have invalid value
    DegenerateUseMethodTestHelper.testDegenerateArguments(historyService,
        method, parameters);
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
