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

import org.ihtsdo.otf.ts.ReleaseInfo;
import org.ihtsdo.otf.ts.helpers.PfsParameter;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.ReleaseInfoList;
import org.ihtsdo.otf.ts.helpers.ResultList;
import org.ihtsdo.otf.ts.jpa.ReleaseInfoJpa;
import org.ihtsdo.otf.ts.rf2.Component;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa;
import org.ihtsdo.otf.ts.rf2.jpa.LanguageRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RelationshipJpa;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Implementation of the "History Service REST Normal Use" Test Cases.
 */
public class HistoryServiceRestNormalUseTest extends HistoryServiceRestTest {

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
    historyService.startEditingCycle(releaseVersion, terminology, version,
        authToken);
  }

  /**
   * Test concept methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory003() throws Exception {
    testNormalUse(ConceptJpa.class);
  }

  /**
   * Test description methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory004() throws Exception {
    testNormalUse(DescriptionJpa.class);
  }

  /**
   * Test language ref set methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory005() throws Exception {
    testNormalUse(LanguageRefSetMemberJpa.class);
  }

  /**
   * Test relationship methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory006() throws Exception {
    testNormalUse(RelationshipJpa.class);
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
   * Test normal use.
   *
   * @param clazz the clazz
   * @throws Exception the exception
   */
  private void testNormalUse(Class<?> clazz) throws Exception {

    // clear results and parameters
    results = null;
    parameters = null;

    testNormalUseModifiedSinceDateMethod(clazz);
    /*
     * testNormalUseRevisionsMethod(clazz);
     * testNormalUseReleaseRevisionsMethod(clazz);
     * testNormalUseDeepCopyModifiedSinceDateMethod(clazz);
     */

    // remove this object from names of objects to be tested

    if (objectNames != null)
      objectNames.remove(getClassShortName(clazz));
  }

  /**
   * Test modified since date method for normal use
   *
   * @param clazz the clazz
   * @throws Exception the exception
   */
  private void testNormalUseModifiedSinceDateMethod(Class<?> clazz)
    throws Exception {

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
    pfs.setSortField("effectiveTime");
    pfs.setAscending(false);
    

    // test since 1970 (i.e. all concepts)
    parameters = new Object[] {
        new String(terminology), new String("19700101"), pfs, authToken
    };
    try {
      results = (ResultList<?>) method.invoke(historyService, parameters);
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }

    for (int i = 0; i < Math.min(results.getCount(), 10); i++) {
      System.out.println(results.getObjects().get(i).toString());
    }
  }

  /**
   * Test revisions method for normal use.
   *
   * @param clazz the clazz
   * @throws Exception
   */
  private void testNormalUseRevisionsMethod(Class<?> clazz) throws Exception {

    // results must be populated
    if (results == null)
      throw new Exception(
          "Results must be populated using testNormalUseModifiedSinceDateMethod");

    Component c = (Component) results.getObjects().iterator().next();

    System.out.println("Component: " + c.toString());

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
    pfs.setAscending(false);

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

  }

  /**
   * Test release revisions method for normal use.
   *
   * @param clazz the clazz
   * @throws Exception
   */
  private void testNormalUseReleaseRevisionsMethod(Class<?> clazz)
    throws Exception {

    // results must be populated
    if (results == null)
      testNormalUseModifiedSinceDateMethod(clazz);

    Component c = (Component) results.getObjects().iterator().next();

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
   * Test deep copy modified since date method for normal use.
   *
   * @param clazz the clazz
   * @throws Exception 
   */
  private void testNormalUseDeepCopyModifiedSinceDateMethod(Class<?> clazz) throws Exception {
    // TODO Find data conditions for this

    // results must be populated
    if (results == null)
      testNormalUseModifiedSinceDateMethod(clazz);

    Component c = (Component) results.getObjects().iterator().next();

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

}
