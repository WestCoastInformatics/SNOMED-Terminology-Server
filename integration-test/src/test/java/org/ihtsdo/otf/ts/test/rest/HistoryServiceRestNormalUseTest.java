/*
 * 
 */
package org.ihtsdo.otf.ts.test.rest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.ReleaseInfo;
import org.ihtsdo.otf.ts.helpers.PfsParameter;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.ReleaseInfoList;
import org.ihtsdo.otf.ts.helpers.ResultList;
import org.ihtsdo.otf.ts.jpa.ReleaseInfoJpa;
import org.ihtsdo.otf.ts.rf2.Component;
import org.ihtsdo.otf.ts.rf2.DescriptionTypeRefSetMember;
import org.ihtsdo.otf.ts.rf2.jpa.AbstractAssociationReferenceRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.AbstractAttributeValueRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ComplexMapRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa;
import org.ihtsdo.otf.ts.rf2.jpa.LanguageRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ModuleDependencyRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RefsetDescriptorRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RelationshipJpa;
import org.ihtsdo.otf.ts.rf2.jpa.SimpleMapRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.SimpleRefSetMemberJpa;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Implementation of the "History Service REST Normal Use" Test Cases.
 */
public class HistoryServiceRestNormalUseTest extends HistoryServiceRestTest {
  
  /** Set true to run tests, set false to get result numbers */
  private static boolean testFlag = false;

  /** The date for which to test revisions, in YYYYMMDD format */
  private static String testDate;

  /** The expected total count of all revisions for an object class */
  private static int objectsCt;

  /**
   * The expected total count of all revisions on or after the test date for an
   * object class
   */
  private static int objectsAfterTestDateCt;

  /** The expected number of revisions for the test object */
  private static int testObjectRevisionsCt;

  /** The terminology id for which to retrieve a test object */
  private static String testId;


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

    // authentication -- use admin for this test
    authToken = securityService.authenticate(adminUser, adminPassword);
  }

  /**
   * Test release info methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory001() throws Exception {

    ReleaseInfo releaseInfo = null;
    ReleaseInfo releaseInfo2 = null;
    ReleaseInfoList releaseInfoList = null;

    // getReleaseHistory(String, String)

    try {
      releaseInfoList =
          historyService.getReleaseHistory(terminology, authToken);
      assertNotNull(releaseInfoList);
    } catch (Exception e) {
      fail("getReleaseHistory for " + terminology + ", " + authToken
          + " failed");
    }

    // getCurrentReleaseInfo(String, String)
    try {
      releaseInfo =
          historyService.getCurrentReleaseInfo(terminology, authToken);
      assertNotNull(releaseInfo);
    } catch (Exception e) {
      fail("getCurrentReleaseInfo for " + terminology + ", " + authToken
          + " failed)");
    }

    // getPlannedReleaseInfo(String, String)
    try {
      releaseInfo =
          historyService.getPlannedReleaseInfo(terminology, authToken);
      assertNotNull(releaseInfo);
    } catch (Exception e) {
      fail("getPlannedReleaseInfo for " + terminology + ", " + authToken
          + " failed)");
    }

    // getPreviousReleaseInfo(String, String)
    // this invocation should return null
    try {
      releaseInfo =
          historyService.getPreviousReleaseInfo(terminology, authToken);
      assertNull(releaseInfo);
    } catch (Exception e) {
      fail("getPreviousReleaseInfo for " + terminology + ", " + authToken
          + " failed)");
    }

    // addReleaseInfo(ReleaseInfoJpa, String)
    releaseInfo = new ReleaseInfoJpa();
    releaseInfo.setDescription("test description");
    releaseInfo.setEffectiveTime(new Date());
    releaseInfo.setName("testName");
    releaseInfo.setPlanned(false);
    releaseInfo.setPublished(true);
    releaseInfo.setTerminology(terminology);
    releaseInfo.setTerminologyVersion(dtFormat.format(new Date()));

    try {
      releaseInfo =
          historyService
              .addReleaseInfo((ReleaseInfoJpa) releaseInfo, authToken);
      assertNotNull(releaseInfo);
      assertNotNull(releaseInfo.getId());
    } catch (Exception e) {
      fail("addRelease info failed");
    }

    // getReleaseInfo(String, String, String)
    try {
      releaseInfo2 =
          historyService.getReleaseInfo(terminology, "testName", authToken);
      assertNotNull(releaseInfo2);
      assertTrue(releaseInfo2.getName().equals("testName"));
    } catch (Exception e) {
      e.printStackTrace();
      fail("getReleaseInfo failed");
    }

    // updateReleaseInfo(ReleaseInfoJpa, String)
    // only need to test one field, GetterSetterTest covers others
    try {
      releaseInfo2.setName("newTestName");
      historyService
          .updateReleaseInfo((ReleaseInfoJpa) releaseInfo2, authToken);
      releaseInfo2 =
          historyService.getReleaseInfo(terminology, "newTestName", authToken);
      assertFalse(releaseInfo.getName().equals(releaseInfo2.getName()));
    } catch (Exception e) {
      e.printStackTrace();
      fail("updateReleaseInfo failed");
    }

    // re-try getCurrent/PreviousReleaseInfo
    try {
      releaseInfo =
          historyService.getCurrentReleaseInfo(terminology, authToken);
      releaseInfo2 =
          historyService.getPreviousReleaseInfo(terminology, authToken);

      assertNotNull(releaseInfo);
      assertNotNull(releaseInfo2);
      assertFalse(releaseInfo.equals(releaseInfo2));
      assertTrue(releaseInfo2.getEffectiveTime().compareTo(
          releaseInfo.getEffectiveTime()) < 0);
    } catch (Exception e) {
      fail("Second get current/previous release info failed");
    }

    // removeReleaseInfo(Long, String)
    try {
      releaseInfo =
          historyService.getReleaseInfo(terminology, "newTestName", authToken);
      assertNotNull(releaseInfo);
      assertNotNull(releaseInfo.getId());
      historyService.removeReleaseInfo(releaseInfo.getId(), authToken);
      releaseInfo =
          historyService.getReleaseInfo(terminology, "newTestName", authToken);
      assertNull(releaseInfo);
    } catch (Exception e) {
      fail("Remove release info failed");
    }
  }

  /**
   * Editing cycle methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory002() throws Exception {

    String releaseVersion = dtFormat.format(new Date());
    try {
      historyService.startEditingCycle(releaseVersion, terminology, version,
          authToken);
    } catch (Exception e) {
      fail("Unexpected exception trying to start the editing cycle");
    }
  }

  /**
   * Test concept methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory003() throws Exception {

    /** The object to test */
    Class<?> testClass = ConceptJpa.class;

    /** Expected values */
    objectsCt = 10293;
    objectsAfterTestDateCt = 0;
    testDate = "20130731";  // revision for object must exist
    testId = "10200004";
    testObjectRevisionsCt = 9;

    testComponent(testClass);

  }

  /**
   * Test description methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory004() throws Exception {
    /** The object to test */
    Class<?> testClass = DescriptionJpa.class;

    /** Expected values */
    testDate = "20130731";
    testId = "id here";
    objectsCt = 10293;
    objectsAfterTestDateCt = 0;
    testObjectRevisionsCt = 0;

    // testComponent(testClass);

  }

  /**
   * Test language ref set methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory005() throws Exception {
    /** The object to test */
    Class<?> testClass = LanguageRefSetMemberJpa.class;

    /** Expected values */
    testDate = "20130731";
    testId = "id here";
    objectsCt = 10293;
    objectsAfterTestDateCt = 0;
    testObjectRevisionsCt = 0;

    // testComponent(testClass);
  }

  /**
   * Test relationship methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory006() throws Exception {
    /** The object to test */
    Class<?> testClass = RelationshipJpa.class;

    /** Expected values */
    testDate = "20130731";
    testId = "id here";
    objectsCt = 10293;
    objectsAfterTestDateCt = 0;
    testObjectRevisionsCt = 0;

    // testComponent(testClass);
  }
  
  /**
   * Test association reference ref set methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory007() throws Exception {
    /** The object to test */
    Class<?> testClass = AbstractAssociationReferenceRefSetMemberJpa.class;

    /** Expected values */
    testDate = "20130731";
    testId = "id here";
    objectsCt = 10293;
    objectsAfterTestDateCt = 0;
    testObjectRevisionsCt = 0;

    // testComponent(testClass);
  }
  
  /**
   * Test attribute value ref set methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory008() throws Exception {
    /** The object to test */
    Class<?> testClass = AbstractAttributeValueRefSetMemberJpa.class;

    /** Expected values */
    testDate = "20130731";
    testId = "id here";
    objectsCt = 10293;
    objectsAfterTestDateCt = 0;
    testObjectRevisionsCt = 0;

    // testComponent(testClass);
  }
  
  /**
   * Test complex map ref set methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory009() throws Exception {
    /** The object to test */
    Class<?> testClass = ComplexMapRefSetMemberJpa.class;

    /** Expected values */
    testDate = "20130731";
    testId = "id here";
    objectsCt = 10293;
    objectsAfterTestDateCt = 0;
    testObjectRevisionsCt = 0;

    // testComponent(testClass);
  }
  
  /**
   * Test description type ref set methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory010() throws Exception {
    /** The object to test */
    Class<?> testClass = DescriptionTypeRefSetMember.class;

    /** Expected values */
    testDate = "20130731";
    testId = "id here";
    objectsCt = 10293;
    objectsAfterTestDateCt = 0;
    testObjectRevisionsCt = 0;

    // testComponent(testClass);
  }
  
  /**
   * Test module dependency ref set methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory011() throws Exception {
    /** The object to test */
    Class<?> testClass = ModuleDependencyRefSetMemberJpa.class;

    /** Expected values */
    testDate = "20130731";
    testId = "id here";
    objectsCt = 10293;
    objectsAfterTestDateCt = 0;
    testObjectRevisionsCt = 0;

    // testComponent(testClass);
  }
  
  /**
   * Test refset descriptor ref set methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory012() throws Exception {
    /** The object to test */
    Class<?> testClass = RefsetDescriptorRefSetMemberJpa.class;

    /** Expected values */
    testDate = "20130731";
    testId = "id here";
    objectsCt = 10293;
    objectsAfterTestDateCt = 0;
    testObjectRevisionsCt = 0;

    // testComponent(testClass);
  }
  
  /**
   * Test simple map ref set member methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory013() throws Exception {
    /** The object to test */
    Class<?> testClass = SimpleMapRefSetMemberJpa.class;

    /** Expected values */
    testDate = "20130731";
    testId = "id here";
    objectsCt = 10293;
    objectsAfterTestDateCt = 0;
    testObjectRevisionsCt = 0;

    // testComponent(testClass);
  }
  
  /**
   * Test simple ref set member methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory014() throws Exception {
    /** The object to test */
    Class<?> testClass = SimpleRefSetMemberJpa.class;

    /** Expected values */
    testDate = "20130731";
    testId = "id here";
    objectsCt = 10293;
    objectsAfterTestDateCt = 0;
    testObjectRevisionsCt = 0;

    // testComponent(testClass);
  }

  /**
   * Teardown.
   *
   * @throws Exception the exception
   */
  @Override
  @After
  public void teardown() throws Exception {

    ReleaseInfo releaseInfo;

    // test for release info created in test 001
    releaseInfo =
        historyService.getReleaseInfo(terminology, "newTestName", authToken);
    if (releaseInfo != null) {
      historyService.removeReleaseInfo(releaseInfo.getId(), authToken);
    }

    // test for release info created in test 002
    String releaseVersion = dtFormat.format(new Date());
    releaseInfo =
        historyService.getReleaseInfo(terminology, releaseVersion, authToken);
    if (releaseInfo != null)
      historyService.removeReleaseInfo(releaseInfo.getId(), authToken);

    // logout
    securityService.logout(authToken);
  }

  /**
   * Test modified since date method for normal use
   *
   * @param clazz the clazz
   * @return
   * @throws Exception the exception
   */
  private ResultList<?> testNormalUseModifiedSinceDateMethod(Class<?> clazz,
    String textDate) throws Exception {

    Method method =
        historyService.getClass().getMethod(
            "find" + getClassShortName(clazz) + "sModifiedSinceDate",
            new Class<?>[] {
                String.class, String.class, PfsParameterJpa.class, String.class
            });

    PfsParameter pfs = new PfsParameterJpa();
    /*
     * pfs.setMaxResults(10); pfs.setStartIndex(0);
     */
    pfs.setSortField("lastModified");
    pfs.setAscending(false);

    // test since 1970 (i.e. all concepts)
    parameters = new Object[] {
        new String(terminology), textDate, pfs, authToken
    };
    try {
      results = (ResultList<?>) method.invoke(historyService, parameters);
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }

    for (int i = 0; i < Math.min(results.getCount(), 10); i++) {
      System.out.println(results.getObjects().get(i).toString());
    }

    return results;
  }

  /**
   * Test revisions method for normal use.
   *
   * @param clazz the clazz
   * @param textDate, Component c
   * @throws Exception
   */
  private ResultList<?> testNormalUseRevisionsMethod(Class<?> clazz,
    String textDate, Component c) throws Exception {

    Method method =
        historyService.getClass().getMethod(
            "find" + getClassShortName(clazz) + "Revisions",
            new Class<?>[] {
                String.class, String.class, String.class,
                PfsParameterJpa.class, String.class
            });

    PfsParameter pfs = new PfsParameterJpa();
    pfs.setMaxResults(10);
    pfs.setStartIndex(0);
    pfs.setSortField("effectiveTime");
    pfs.setAscending(true);

    // test between 1970 (i.e. all concepts) and current date/time
    parameters =
        new Object[] {
            c.getId().toString(), new String("19700101"),
            dtFormat.format(new Date()), pfs, authToken
        };

    try {
      results = (ResultList<?>) method.invoke(historyService, parameters);
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }

    return results;

  }

  /**
   * Test release revision method for normal use.
   *
   * @param clazz the clazz
   * @return
   * @throws Exception
   */
  private Component testNormalUseReleaseRevisionMethod(Class<?> clazz,
    Component c) throws Exception {

    Component revisionComponent = null;

    Method method =
        historyService.getClass().getMethod(
            "find" + getClassShortName(clazz) + "ReleaseRevision",
            new Class<?>[] {
                String.class, String.class, String.class
            });

    // test for retrieved release dates between first component's effective time
    // and today's date
    parameters = new Object[] {
        c.getId().toString(), dtFormat.format(c.getEffectiveTime()), authToken
    };

    try {
      revisionComponent = (Component) method.invoke(historyService, parameters);
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }

    return revisionComponent;

  }

  /**
   * Test deep copy modified since date method for normal use.
   *
   * @param clazz the clazz
   * @throws Exception
   */
  private void testNormalUseDeepCopyModifiedSinceDateMethod(Class<?> clazz,
    Component c) throws Exception {

    Method method =
        historyService.getClass().getMethod(
            "find" + getClassShortName(clazz) + "sDeepModifiedSinceDate",
            new Class<?>[] {
                String.class, String.class, String.class,
                PfsParameterJpa.class, String.class
            });

    PfsParameter pfs = new PfsParameterJpa();
    pfs.setMaxResults(10);
    pfs.setStartIndex(0);
    pfs.setSortField("effectiveTime");
    pfs.setAscending(false);

    // test for retrieved release dates between first component's effective time
    // and today's date
    parameters =
        new Object[] {
            c.getId().toString(), dtFormat.format(c.getEffectiveTime()),
            dtFormat.format(new Date()), pfs, authToken
        };

    try {
      results = (ResultList<?>) method.invoke(historyService, parameters);
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  /**
   * Test component class. Assumes global parameters correctly set.
   *
   * @param testClass the test class
   * @throws Exception the exception
   */
  private void testComponent(Class<?> testClass) throws Exception {

    /** Get the component for testing */
    Component component = getComponent(testClass, testId);

    /**
     * findObjectsModifiedSinceDate method
     */

    // retrieve all objects since 1970 (beginning of time)
    try {
      results =
          (ResultList<?>) testNormalUseModifiedSinceDateMethod(testClass,
              "19700101");

      if (testFlag == true) {
        assertTrue(results.getCount() == objectsCt);
      } else {
        Logger.getLogger(HistoryServiceRestNormalUseTest.class).info(
            "All Object Ct = " + results.getCount());
      }
    } catch (Exception e) {
      fail("Failed to get all objects for " + testClass.getSimpleName());
    }

    // retrieve all objects modified since test date
    try {
      results =
          (ResultList<?>) testNormalUseModifiedSinceDateMethod(testClass,
              testDate);

      if (testFlag == true) {
        assertTrue(results.getCount() == objectsAfterTestDateCt);
      } else {
        Logger.getLogger(HistoryServiceRestNormalUseTest.class).info(
            "All Objects Since Date Ct = " + results.getCount());
      }
    } catch (Exception e) {
      fail("Failed to get all objects since date for "
          + testClass.getSimpleName());
    }

    /**
     * findObjectRevisions method
     */

    // get all revisions for test object
    try {
      results =
          (ResultList<?>) testNormalUseRevisionsMethod(testClass, "19700101",
              component);

      if (testFlag == true) {
        assertTrue(results.getCount() == testObjectRevisionsCt);
      } else {
        Logger.getLogger(HistoryServiceRestNormalUseTest.class).info(
            "All Test Object Revisions Ct = " + results.getCount());
      }
    } catch (Exception e) {
      fail("Failed to get all revisions for test object for "
          + testClass.getSimpleName());
    }

    // retrieve all objects modified since test date
    try {
      results =
          (ResultList<?>) testNormalUseRevisionsMethod(testClass, testDate,
              component);

      if (testFlag == true) {
        assertTrue(results.getCount() == objectsCt);
      } else {
        Logger.getLogger(HistoryServiceRestNormalUseTest.class).info(
            "All Test Object Revisions Since Date Ct = " + results.getCount());
      }
    } catch (Exception e) {
      fail("Failed to get revisions for test object on or after test date for "
          + testClass.getSimpleName());
    }

    /** findObjectRevisions method */

    // get the revision for specific date
    try {
      Component c =
          this.testNormalUseReleaseRevisionMethod(testClass, component);

      // test total revisions
      if (testFlag == true) {
        assertNotNull(c);
        assertTrue(c.getId().equals(component.getId()));
        assertTrue(c.getLastModified().equals(testDate));
      } else {
        Logger.getLogger(HistoryServiceRestNormalUseTest.class).info(
            "Successfully retrieved revision for object");
      }
    } catch (Exception e) {
      fail("Failed to retrieve release revision for "
          + testClass.getSimpleName());
    }

    /** Remove the object from the set of objects to be tested */

    if (objectNames != null)
      objectNames.remove(getClassShortName(testClass));
  }

  /**
   * Helper function to get the test component by terminologyId
   * @param clazz
   * @param terminologyId
   * @return
   * @throws NoSuchMethodException
   * @throws SecurityException
   * @throws IllegalAccessException
   * @throws IllegalArgumentException
   * @throws InvocationTargetException
   */
  private Component getComponent(Class<?> clazz, String terminologyId)
    throws NoSuchMethodException, SecurityException, IllegalAccessException,
    IllegalArgumentException, InvocationTargetException {

    Method method =
        historyService.getClass().getMethod("get" + getClassShortName(clazz),
            new Class<?>[] {
                String.class, String.class, String.class, String.class
            });

    parameters = new Object[] {
        terminologyId, terminology, version, authToken
    };

    return (Component) method.invoke(historyService, parameters);
  }

}
