/*
 * Copyright 2015 West Coast Informatics, LLC
 */
/*
 * 
 */
package org.ihtsdo.otf.ts.test.rest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.ihtsdo.otf.ts.ReleaseInfo;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.ReleaseInfoList;
import org.ihtsdo.otf.ts.jpa.ReleaseInfoJpa;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.DescriptionTypeRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.jpa.AbstractAssociationReferenceRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.AbstractAttributeValueRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ComplexMapRefSetMemberJpa;
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

  /**
   * Variables are re-used, but set to top-level for convenience of repeated
   * calls without declarations in each test method.
   */

  /** Set true to run tests, set false to get result numbers */
  private static boolean testFlag = false;

  /**
   * The date for which to test revisions, in YYYYMMDD format For tests to
   * function properly, object must have revision at this date.
   */
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

  /** The test date/name for the release info routines */
  private static String currentDate;
  
  /** The minimum pfs parameter object */
  private static PfsParameterJpa pfs;

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

    // release info testing (and teardown removal) information
    currentDate = dtFormat.format(new Date());

    // authentication -- use admin for this test
    authToken = securityService.authenticate(adminUser, adminPassword);
    
    // default paging
    pfs = new PfsParameterJpa();
    pfs.setStartIndex(0);
    pfs.setMaxResults(10);
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

    // test SNOMEDCT -- override global terminology
    // String terminology = "SNOMEDCT";

    // getReleaseHistory(String, String)
    // should be 36 release infos for SNOMEDCT
    try {
      releaseInfoList =
          historyService.getReleaseHistory(terminology, authToken);
      assertNotNull(releaseInfoList);
      assertTrue(releaseInfoList.getCount() == 36);
    } catch (Exception e) {
      fail("getReleaseHistory for " + terminology + ", " + authToken
          + " failed");
    }

    // getCurrentReleaseInfo(String, String)
    // should return the 20140731 release
    try {
      releaseInfo =
          historyService.getCurrentReleaseInfo(terminology, authToken);
      assertNotNull(releaseInfo);
      assertTrue(releaseInfo.getName().equals("20140731"));
    } catch (Exception e) {
      fail("getCurrentReleaseInfo for " + terminology + ", " + authToken
          + " failed)");
    }

    // getPlannedReleaseInfo(String, String)
    // should return the 20150131 release
    try {
      releaseInfo =
          historyService.getPlannedReleaseInfo(terminology, authToken);
      assertNotNull(releaseInfo);
      assertTrue(releaseInfo.getName().equals("20150131"));
    } catch (Exception e) {
      fail("getPlannedReleaseInfo for " + terminology + ", " + authToken
          + " failed)");
    }

    // getPreviousReleaseInfo(String, String)
    // this invocation should return the 20140131 release
    try {
      releaseInfo =
          historyService.getPreviousReleaseInfo(terminology, authToken);
      assertNotNull(releaseInfo);
      assertTrue(releaseInfo.getName().equals("20140131"));
    } catch (Exception e) {
      fail("getPreviousReleaseInfo for " + terminology + ", " + authToken
          + " failed)");
    }

    // addReleaseInfo(ReleaseInfoJpa, String)
    releaseInfo = new ReleaseInfoJpa();
    releaseInfo.setDescription("test description");
    releaseInfo.setEffectiveTime(new Date());
    releaseInfo.setName(currentDate);
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
    releaseInfo2 = null;
    try {
      releaseInfo2 =
          historyService
              .getReleaseInfo(terminology, currentDate, authToken);
      assertNotNull(releaseInfo2);
      assertTrue(releaseInfo2.getName().equals(currentDate));
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

    /**
     * finishReleaseCycle not currently used, untested
     */
  }

  /**
   * Test concept methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory003() throws Exception {

    /** Expected values */
    objectsCt = 10293;
    objectsAfterTestDateCt = 0;
    testDate = "20130731"; // revision for object must exist
    testId = "321087001";
    testObjectRevisionsCt = 5;
    
    ConceptList results;


    // get the component
    Concept c =
        contentService
            .getSingleConcept(testId, terminology, version, authToken);
  /*  
    Relationship r = contentService.getRelationship(c.getRelationships().iterator().next().getTerminologyId().toString(), terminology, version, authToken);
    historyService.findRelationshipRevisions(r.Id().toString(), "19700101", currentDate, pfs, authToken);
    
    historyService.findRelationshipsModifiedSinceDate(terminology,  "19700101", pfs, authToken);
 */   // test modified since date method
    results = historyService.findConceptsModifiedSinceDate(terminology, "19700101", pfs, authToken);
    assertTrue(results.getTotalCount() == 10293);
    
    // test modified since date method
    //results = historyService.findConceptsModifiedSinceDate(terminology, "20080131", pfs, authToken);
    //assertTrue(results.getTotalCount() == 2549);
    
    Concept c2 = historyService.findConceptReleaseRevision(c.getId().toString(), "20020131", authToken);
    
    // test revisions method
    results = historyService.findConceptRevisions(c.getId().toString(), "19700101", currentDate, pfs, authToken);
    assertTrue(results.getTotalCount() == 10293);
   
    
  }

  /**
   * Test description methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory004() throws Exception {

    /** Expected values */
    testDate = "20130731";
    testId = "id here";
    objectsCt = 10293;
    objectsAfterTestDateCt = 0;
    testObjectRevisionsCt = 0;

    
   
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

    ReleaseInfo releaseInfo = null;

    // test for release info created in test 001
    releaseInfo =
        historyService.getReleaseInfo(terminology, currentDate, authToken);
    if (releaseInfo != null) {
      historyService.removeReleaseInfo(releaseInfo.getId(), authToken);
    }
    /*
     * // test for release info created in test 002 String releaseVersion =
     * dtFormat.format(new Date()); releaseInfo =
     * historyService.getReleaseInfo(terminology, releaseVersion, authToken); if
     * (releaseInfo != null) {
     * historyService.removeReleaseInfo(releaseInfo.getId(), authToken); }
     */

    // logout
    securityService.logout(authToken);
  }

}
