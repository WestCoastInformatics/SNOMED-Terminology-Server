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
import java.util.Set;

import org.ihtsdo.otf.ts.ReleaseInfo;
import org.ihtsdo.otf.ts.helpers.AssociationReferenceRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.AttributeValueRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ComplexMapRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.DescriptionList;
import org.ihtsdo.otf.ts.helpers.DescriptionTypeRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.LanguageRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ModuleDependencyRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.RefsetDescriptorRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.RelationshipList;
import org.ihtsdo.otf.ts.helpers.ReleaseInfoList;
import org.ihtsdo.otf.ts.helpers.SimpleMapRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.SimpleRefSetMemberList;
import org.ihtsdo.otf.ts.jpa.ReleaseInfoJpa;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceRefSetMember;
import org.ihtsdo.otf.ts.rf2.AttributeValueRefSetMember;
import org.ihtsdo.otf.ts.rf2.ComplexMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.Component;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.DescriptionTypeRefSetMember;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.ModuleDependencyRefSetMember;
import org.ihtsdo.otf.ts.rf2.RefsetDescriptorRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.SimpleMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.SimpleRefSetMember;
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
   * 
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory001() throws Exception {

    ReleaseInfo releaseInfo = null;
    ReleaseInfoList releaseInfoList = null;

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
  }

  /**
   * Test add/update/remove releaseInfo use rest history0011.
   * 
   *
   * @throws Exception the exception
   */

  @Test
  public void testNormalUseRestHistory002() throws Exception {

    ReleaseInfo releaseInfo, releaseInfo2;

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
      e.printStackTrace();
      fail("addRelease info failed");
      
    }

    // wait to ensure object successfully added
    Thread.sleep(1000);

    // getReleaseInfo(String, String, String)
    releaseInfo2 = null;
    try {
      releaseInfo2 =
          historyService.getReleaseInfo(terminology, currentDate, authToken);
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

    objectNames.remove("Concept");

    // The test id for this concept
    testId = "321087001";

    ConceptList results;

    // get the component
    Concept c =
        contentService
            .getSingleConcept(testId, terminology, version, authToken);

    // test all concepts modified over all time
    results =
        historyService.findConceptsModifiedSinceDate(terminology, "19700101",
            pfs, authToken);
    assertTrue(results.getTotalCount() == 10293);

    // test all concepts modified after lower bound date
    results =
        historyService.findConceptsModifiedSinceDate(terminology, "20080131",
            pfs, authToken);
    assertTrue(results.getTotalCount() == 2652);

    // test paging filtering sorting based on truncated set

    // set pfs to no max results -- find revisions methods currently do not
    // return total
    // count correctly
    pfs.setMaxResults(-1);

    // test revisions method -- range: all time
    results =
        historyService.findConceptRevisions(c.getId().toString(), "19700101",
            currentDate, pfs, authToken);
    assertTrue(results.getTotalCount() == 12);

    // test revisions method lower bound
    results =
        historyService.findConceptRevisions(c.getId().toString(), "20060131",
            currentDate, pfs, authToken);
    assertTrue(results.getTotalCount() == 3);

    // test revisions method upper bound
    results =
        historyService.findConceptRevisions(c.getId().toString(), "19700101",
            "20040131", pfs, authToken);
    assertTrue(results.getTotalCount() == 9);

    // test retrieve release revision at specific release time
    // - time should match
    // - definition status id should match
    c =
        historyService.findConceptReleaseRevision(c.getId().toString(),
            "20060131", authToken);
    assertTrue(dtFormat.format(c.getEffectiveTime()).equals("20060131"));
    assertTrue(c.getDefinitionStatusId().equals("900000000000073002"));

    // NOTE: Concept has findDeepModified method, tested independently here
    // Only testing paging, as date restriction is tested individually
    // for each of the calls used by this routine

    results =
        historyService.findConceptsDeepModifiedSinceDate(terminology,
            "19700101", null, authToken);

    assertTrue(results.getCount() == 10293);

    // test paging
    pfs.setMaxResults(10);
    pfs.setStartIndex(0);
    ConceptList pagedResults = historyService.findConceptsDeepModifiedSinceDate(terminology, "19700101", pfs, authToken);

    assertTrue(results.getObjects().subList(0, 10).equals(pagedResults.getObjects()));
    
    pfs.setMaxResults(10);
    pfs.setStartIndex(100);
    pagedResults = historyService.findConceptsDeepModifiedSinceDate(terminology, "19700101", pfs, authToken);

    assertTrue(results.getObjects().subList(100, 110).equals(pagedResults.getObjects()));
    
    // test with date -- arbitrary
    // number validated through independent sql queries
    String testDate = "20130731";
    pfs.setMaxResults(-1);
    pfs.setStartIndex(-1);
    results = historyService.findConceptsDeepModifiedSinceDate(terminology, testDate, pfs, authToken);
  
    assertTrue(results.getCount() == 3096);
    
  }

  /**
   * Test description methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory004() throws Exception {

    objectNames.remove("Description");

    // The test id for this description
    testId = "387512014";

    DescriptionList results;

    // get the component
    Description c =
        contentService.getDescription(testId, terminology, version, authToken);

    // test all descriptions modified over all time
    results =
        historyService.findDescriptionsModifiedSinceDate(terminology,
            "19700101", pfs, authToken);
    assertTrue(results.getTotalCount() == 34105);

    // test all descriptions modified after lower bound date
    results =
        historyService.findDescriptionsModifiedSinceDate(terminology,
            "20080131", pfs, authToken);
    assertTrue(results.getTotalCount() == 6884);

    // set pfs to no max results
    pfs.setMaxResults(-1);

    // test revisions method -- range: all time
    results =
        historyService.findDescriptionRevisions(c.getId().toString(),
            "19700101", currentDate, pfs, authToken);
    assertTrue(results.getTotalCount() == 5);

    // test revisions method lower bound
    results =
        historyService.findDescriptionRevisions(c.getId().toString(),
            "20030731", currentDate, pfs, authToken);
    assertTrue(results.getTotalCount() == 4);

    // test revisions method upper bound
    results =
        historyService.findDescriptionRevisions(c.getId().toString(),
            "19700731", "20030731", pfs, authToken);
    assertTrue(results.getTotalCount() == 4);

    // test retrieve release revision at specific release time
    // - time should match
    // - should be unpublished (previous version at same time published)
    c =
        historyService.findDescriptionReleaseRevision(c.getId().toString(),
            "20030731", authToken);
    assertTrue(dtFormat.format(c.getEffectiveTime()).equals("20030731"));
    assertTrue(c.isPublished() == false);

  }

  /**
   * Test relationship methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory005() throws Exception {

    objectNames.remove("Relationship");

    // The test id for this component
    testId = "501314023";

    RelationshipList results;

    // get the component
    Relationship c =
        contentService.getRelationship(testId, terminology, version, authToken);

    // test all relationships modified over all time
    results =
        historyService.findRelationshipsModifiedSinceDate(terminology,
            "19700101", pfs, authToken);
    assertTrue(results.getTotalCount() == 79098);

    // test all relationships modified after lower bound date
    results =
        historyService.findRelationshipsModifiedSinceDate(terminology,
            "20080131", pfs, authToken);
    assertTrue(results.getTotalCount() == 45453);

    // set pfs to no max results

    pfs.setMaxResults(-1);

    // test revisions method -- range: all time
    results =
        historyService.findRelationshipRevisions(c.getId().toString(),
            "19700101", currentDate, pfs, authToken);
    assertTrue(results.getTotalCount() == 13);

    // test revisions method lower bound
    results =
        historyService.findRelationshipRevisions(c.getId().toString(),
            "20030731", currentDate, pfs, authToken);
    assertTrue(results.getTotalCount() == 10);

    // test revisions method upper bound
    results =
        historyService.findRelationshipRevisions(c.getId().toString(),
            "19700731", "20030731", pfs, authToken);
    assertTrue(results.getTotalCount() == 4);

    // test retrieve release revision at specific release time
    // - time should match
    // - should be unpublished (previous version at same time published)
    c =
        historyService.findRelationshipReleaseRevision(c.getId().toString(),
            "20020131", authToken);
    assertTrue(dtFormat.format(c.getEffectiveTime()).equals("20020131"));
    assertTrue(c.isPublished() == false);
  }

  /**
   * Test language ref set methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory006() throws Exception {

    objectNames.remove("LanguageRefSetMember");

    // The test id for this component
    testId = "e76075bd-3748-589f-b987-2ada691921ef";

    LanguageRefSetMemberList results;

    // get the component
    LanguageRefSetMember c = null;
    LanguageRefSetMemberList list =
        contentService.getLanguageRefSetMembersForDescription("456353019",
            terminology, version, authToken);
    for (LanguageRefSetMember a : list.getObjects()) {
      if (a.getTerminologyId().equals(testId)) {
        c = a;
      }
    }
    if (c == null)
      fail("Could not retrieve language ref set member");

    // test all descriptions modified over all time
    results =
        historyService.findLanguageRefSetMembersModifiedSinceDate(terminology,
            "19700101", pfs, authToken);
    assertTrue(results.getTotalCount() == 66653);

    // test all descriptions modified after lower bound date
    results =
        historyService.findLanguageRefSetMembersModifiedSinceDate(terminology,
            "20080131", pfs, authToken);
    assertTrue(results.getTotalCount() == 8763);

    // set pfs to no max results -- find methods currently do not return total
    // count correctly
    pfs.setMaxResults(-1);

    // test revisions method -- range: all time
    results =
        historyService.findLanguageRefSetMemberRevisions(c.getId().toString(),
            "19700101", currentDate, pfs, authToken);
    assertTrue(results.getTotalCount() == 5);

    // test revisions method lower bound
    results =
        historyService.findLanguageRefSetMemberRevisions(c.getId().toString(),
            "20040731", currentDate, pfs, authToken);
    assertTrue(results.getTotalCount() == 4);

    // test revisions method upper bound
    results =
        historyService.findLanguageRefSetMemberRevisions(c.getId().toString(),
            "19700731", "20040731", pfs, authToken);
    assertTrue(results.getTotalCount() == 2);

    // test retrieve release revision at specific release time
    // - time should match
    // - should be unpublished (previous version at same time published)
    c =
        historyService.findLanguageRefSetMemberReleaseRevision(c.getId()
            .toString(), "20080731", authToken);
    assertTrue(dtFormat.format(c.getEffectiveTime()).equals("20080731"));
    assertTrue(c.isPublished() == false);
  }

  /**
   * Test association reference ref set methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory007() throws Exception {

    objectNames.remove("AssociationReferenceRefSetMember");

    // The test id for this association reference ref set member
    testId = "cf39de4d-bbb9-59c3-b049-82a6c31ce87a";

    AssociationReferenceRefSetMemberList results;

    // no direct REST call to get reference ref set member
    // instead call by refset id and cycle until test id matched
    AssociationReferenceRefSetMemberList list =
        contentService.getAssociationReferenceRefSetMembersForConcept(
            "398901001", terminology, version, authToken);
    AssociationReferenceRefSetMember<?> c = null;
    for (AssociationReferenceRefSetMember<?> a : list.getObjects()) {
      if (a.getTerminologyId().equals(testId)) {
        c = a;
      }
    }
    if (c == null)
      fail("Could not retrieve association reference ref set member");

    // test all relationships modified over all time
    results =
        historyService.findAssociationReferenceRefSetMembersModifiedSinceDate(
            terminology, "19700101", pfs, authToken);
    assertTrue(results.getTotalCount() == 435);

    // test all relationships modified after lower bound date
    results =
        historyService.findAssociationReferenceRefSetMembersModifiedSinceDate(
            terminology, "20080131", pfs, authToken);
    assertTrue(results.getTotalCount() == 134);

    // set pfs to no max results -- find methods currently do not return total
    // count correctly
    pfs.setMaxResults(-1);

    // test revisions method -- range: all time
    results =
        historyService.findAssociationReferenceRefSetMemberRevisions(c.getId()
            .toString(), "19700101", currentDate, pfs, authToken);
    assertTrue(results.getTotalCount() == 2);

    // test revisions method lower bound
    results =
        historyService.findAssociationReferenceRefSetMemberRevisions(c.getId()
            .toString(), "20080731", currentDate, pfs, authToken);
    assertTrue(results.getTotalCount() == 1);

    // test revisions method upper bound
    results =
        historyService.findAssociationReferenceRefSetMemberRevisions(c.getId()
            .toString(), "19700731", "20080730", pfs, authToken);
    assertTrue(results.getTotalCount() == 1);

    // test retrieve release revision at specific release time
    // - time should match
    // - no other test, no examples of multiple revisions at same efffective
    // time
    c =
        historyService.findAssociationReferenceRefSetMemberReleaseRevision(c
            .getId().toString(), "20080731", authToken);
    assertTrue(dtFormat.format(c.getEffectiveTime()).equals("20080731"));
  }

  /**
   * Test attribute value ref set methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory008() throws Exception {

    objectNames.remove("AttributeValueRefSetMember");

    // The test id for this component
    testId = "de184ffc-03f3-5dae-9f2b-6c46020ede8a";

    AttributeValueRefSetMemberList results;

    // get the component
    AttributeValueRefSetMember<?> c = null;
    AttributeValueRefSetMemberList list =
        contentService.getAttributeValueRefSetMembersForDescription("89332015",
            terminology, version, authToken);
    for (AttributeValueRefSetMember<?> a : list.getObjects()) {
      if (a.getTerminologyId().equals(testId)) {
        c = a;
      }
    }
    if (c == null)
      fail("Could not retrieve attribute value ref set member");

    // test all attribute value ref set members modified over all time
    results =
        historyService.findAttributeValueRefSetMembersModifiedSinceDate(
            terminology, "19700101", pfs, authToken);
    assertTrue(results.getTotalCount() == 1994);

    // test all attribute value ref set members modified after lower bound date
    results =
        historyService.findAttributeValueRefSetMembersModifiedSinceDate(
            terminology, "20080131", pfs, authToken);
    assertTrue(results.getTotalCount() == 557);

    // set pfs to no max results -- find methods currently do not return total
    // count correctly
    pfs.setMaxResults(-1);

    // test revisions method -- range: all time
    results =
        historyService.findAttributeValueRefSetMemberRevisions(c.getId()
            .toString(), "19700101", currentDate, pfs, authToken);
    assertTrue(results.getTotalCount() == 4);

    // test revisions method lower bound
    results =
        historyService.findAttributeValueRefSetMemberRevisions(c.getId()
            .toString(), "20050131", currentDate, pfs, authToken);
    assertTrue(results.getTotalCount() == 3);

    // test revisions method upper bound
    results =
        historyService.findAttributeValueRefSetMemberRevisions(c.getId()
            .toString(), "19700731", "20050131", pfs, authToken);
    assertTrue(results.getTotalCount() == 2);

    // test retrieve release revision at specific release time
    // - time should match
    c =
        historyService.findAttributeValueRefSetMemberReleaseRevision(c.getId()
            .toString(), "20050131", authToken);
    assertTrue(dtFormat.format(c.getEffectiveTime()).equals("20050131"));
  }

  /**
   * Test complex map ref set methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory009() throws Exception {

    objectNames.remove("ComplexMapRefSetMember");

    // The test id for this component
    testId = "055c30ae-b018-5b82-bd3d-74ff9451de15";

    ComplexMapRefSetMemberList results;

    // get the component
    ComplexMapRefSetMember c = null;
    ComplexMapRefSetMemberList list =
        contentService.getComplexMapRefSetMembersForConcept("297249002",
            terminology, version, authToken);
    for (ComplexMapRefSetMember a : list.getObjects()) {
      if (a.getTerminologyId().equals(testId))
        c = a;
    }
    if (c == null)
      fail("Could not retrieve complex map ref set member");

    // test all complex map ref set members modified over all time
    results =
        historyService.findComplexMapRefSetMembersModifiedSinceDate(
            terminology, "19700101", pfs, authToken);
    assertTrue(results.getTotalCount() == 8731);

    // test all complex map ref set members modified after lower bound date
    results =
        historyService.findComplexMapRefSetMembersModifiedSinceDate(
            terminology, "20080131", pfs, authToken);
    assertTrue(results.getTotalCount() == 5021);

    // set pfs to no max results -- find methods currently do not return total
    // count correctly
    pfs.setMaxResults(-1);

    // test revisions method -- range: all time
    results =
        historyService.findComplexMapRefSetMemberRevisions(
            c.getId().toString(), "19700101", currentDate, pfs, authToken);
    assertTrue(results.getTotalCount() == 5);

    // test revisions method lower bound
    results =
        historyService.findComplexMapRefSetMemberRevisions(
            c.getId().toString(), "20060731", currentDate, pfs, authToken);
    assertTrue(results.getTotalCount() == 3);

    // test revisions method upper bound
    results =
        historyService.findComplexMapRefSetMemberRevisions(
            c.getId().toString(), "19700731", "20060731", pfs, authToken);
    assertTrue(results.getTotalCount() == 3);

    // test retrieve release revision at specific release time
    // - time should match
    c =
        historyService.findComplexMapRefSetMemberReleaseRevision(c.getId()
            .toString(), "20060731", authToken);
    assertTrue(dtFormat.format(c.getEffectiveTime()).equals("20060731"));
  }

  /**
   * Test description type ref set methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory010() throws Exception {

    objectNames.remove("DescriptionTypeRefSetMember");

    // The test id for this component
    testId = "807f775b-1d66-5069-b58e-a37ace985dcf";

    DescriptionTypeRefSetMemberList results;

    // get the component
    DescriptionTypeRefSetMember c = null;
    DescriptionTypeRefSetMemberList list =
        contentService.getDescriptionTypeRefSetMembersForConcept(
            "900000000000550004", terminology, version, authToken);
    for (DescriptionTypeRefSetMember a : list.getObjects()) {
      if (a.getTerminologyId().equals(testId))
        c = a;
    }
    if (c == null)
      fail("Could not retrieve description type ref set member");

    // test all description type ref set members modified over all time
    results =
        historyService.findDescriptionTypeRefSetMembersModifiedSinceDate(
            terminology, "19700101", pfs, authToken);
    assertTrue(results.getTotalCount() == 3);

    // test all description type ref set members modified after lower bound date
    results =
        historyService.findDescriptionTypeRefSetMembersModifiedSinceDate(
            terminology, "20080131", pfs, authToken);
    assertTrue(results.getTotalCount() == 1);

    // set pfs to no max results -- find methods currently do not return total
    // count correctly
    pfs.setMaxResults(-1);

    // test revisions method -- range: all time
    results =
        historyService.findDescriptionTypeRefSetMemberRevisions(c.getId()
            .toString(), "19700101", currentDate, pfs, authToken);
    assertTrue(results.getTotalCount() == 2);

    // test revisions method lower bound
    results =
        historyService.findDescriptionTypeRefSetMemberRevisions(c.getId()
            .toString(), "20140131", currentDate, pfs, authToken);
    assertTrue(results.getTotalCount() == 1);

    // test revisions method upper bound
    results =
        historyService.findDescriptionTypeRefSetMemberRevisions(c.getId()
            .toString(), "19700731", "20140130", pfs, authToken);
    assertTrue(results.getTotalCount() == 1);

    // test retrieve release revision at specific release time
    // - time should match
    c =
        historyService.findDescriptionTypeRefSetMemberReleaseRevision(c.getId()
            .toString(), "20140131", authToken);
    assertTrue(dtFormat.format(c.getEffectiveTime()).equals("20140131"));
  }

  /**
   * Test module dependency ref set methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory011() throws Exception {

    objectNames.remove("ModuleDependencyRefSetMember");

    // The test id for this component
    testId = "1244116f-fdb5-5645-afcc-5281288409da";

    ModuleDependencyRefSetMemberList results;

    // get the component
    ModuleDependencyRefSetMember c = null;
    ModuleDependencyRefSetMemberList list =
        contentService.getModuleDependencyRefSetMembersForModule(
            "900000000000207008", terminology, version, authToken);
    for (ModuleDependencyRefSetMember a : list.getObjects()) {
      if (a.getTerminologyId().equals(testId))
        c = a;
    }
    if (c == null)
      fail("Could not retrieve module dependency ref set member");

    // test all module dependency ref set members modified over all time
    results =
        historyService.findModuleDependencyRefSetMembersModifiedSinceDate(
            terminology, "19700101", pfs, authToken);
    assertTrue(results.getTotalCount() == 3);

    // test all module dependency ref set members modified after lower bound
    // date
    results =
        historyService.findModuleDependencyRefSetMembersModifiedSinceDate(
            terminology, "20080131", pfs, authToken);
    assertTrue(results.getTotalCount() == 3);

    // set pfs to no max results -- find methods currently do not return total
    // count correctly
    pfs.setMaxResults(-1);

    // test revisions method -- range: all time
    results =
        historyService.findModuleDependencyRefSetMemberRevisions(c.getId()
            .toString(), "19700101", currentDate, pfs, authToken);
    assertTrue(results.getTotalCount() == 26);

    // test revisions method lower bound
    results =
        historyService.findModuleDependencyRefSetMemberRevisions(c.getId()
            .toString(), "20140131", currentDate, pfs, authToken);
    assertTrue(results.getTotalCount() == 2);

    // test revisions method upper bound
    results =
        historyService.findModuleDependencyRefSetMemberRevisions(c.getId()
            .toString(), "19700731", "20140131", pfs, authToken);
    assertTrue(results.getTotalCount() == 25);

    // test retrieve release revision at specific release time
    // - time should match
    c =
        historyService.findModuleDependencyRefSetMemberReleaseRevision(c
            .getId().toString(), "20140131", authToken);
    assertTrue(dtFormat.format(c.getEffectiveTime()).equals("20140131"));
  }

  /**
   * Test refset descriptor ref set methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory012() throws Exception {

    objectNames.remove("RefsetDescriptorRefSetMember");

    // The test id for this component
    testId = "576ed8c3-1227-5489-b782-760a3b729b94";

    RefsetDescriptorRefSetMemberList results;

    // get the component
    RefsetDescriptorRefSetMember c = null;
    RefsetDescriptorRefSetMemberList list =
        contentService.getRefsetDescriptorRefSetMembers("447563008",
            terminology, version, authToken);
    for (RefsetDescriptorRefSetMember a : list.getObjects()) {
      if (a.getTerminologyId().equals(testId))
        c = a;
    }
    if (c == null)
      fail("Could not retrieve refset descriptor ref set member");

    // test all refset descriptor ref set members modified over all time
    results =
        historyService.findRefsetDescriptorRefSetMembersModifiedSinceDate(
            terminology, "19700101", pfs, authToken);
    assertTrue(results.getTotalCount() == 68);

    // test all refset descriptor ref set members modified after lower bound
    // date
    results =
        historyService.findRefsetDescriptorRefSetMembersModifiedSinceDate(
            terminology, "20080131", pfs, authToken);
    assertTrue(results.getTotalCount() == 22);

    // set pfs to no max results -- find methods currently do not return total
    // count correctly
    pfs.setMaxResults(-1);

    // test revisions method -- range: all time
    results =
        historyService.findRefsetDescriptorRefSetMemberRevisions(c.getId()
            .toString(), "19700101", currentDate, pfs, authToken);
    assertTrue(results.getTotalCount() == 1);

    // test revisions method lower bound
    results =
        historyService.findRefsetDescriptorRefSetMemberRevisions(c.getId()
            .toString(), "20020131", currentDate, pfs, authToken);
    assertTrue(results.getTotalCount() == 1);

    // test revisions method upper bound
    results =
        historyService.findRefsetDescriptorRefSetMemberRevisions(c.getId()
            .toString(), "19700731", "20020131", pfs, authToken);
    assertTrue(results.getTotalCount() == 1);

    // test retrieve release revision at specific release time
    // - time should match
    c =
        historyService.findRefsetDescriptorRefSetMemberReleaseRevision(c
            .getId().toString(), "20020131", authToken);
    assertTrue(dtFormat.format(c.getEffectiveTime()).equals("20020131"));
  }

  /**
   * Test simple map ref set member methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory013() throws Exception {

    objectNames.remove("SimpleMapRefSetMember");

    // The test id for this component
    testId = "f8dbedb7-12a7-5e33-bbc8-3d1252c77820";

    SimpleMapRefSetMemberList results;

    // get the component
    SimpleMapRefSetMember c = null;
    SimpleMapRefSetMemberList list =
        contentService.getSimpleMapRefSetMembersForConcept("244476009",
            terminology, version, authToken);
    for (SimpleMapRefSetMember a : list.getObjects()) {
      if (a.getTerminologyId().equals(testId))
        c = a;
    }
    if (c == null)
      fail("Could not retrieve complex map ref set member");

    // test all simple map ref set members modified over all time
    results =
        historyService.findSimpleMapRefSetMembersModifiedSinceDate(terminology,
            "19700101", pfs, authToken);
    assertTrue(results.getTotalCount() == 22060);

    // test all simple map ref set members modified after lower bound date
    results =
        historyService.findSimpleMapRefSetMembersModifiedSinceDate(terminology,
            "20080131", pfs, authToken);
    assertTrue(results.getTotalCount() == 1956);

    // set pfs to no max results -- find methods currently do not return total
    // count correctly
    pfs.setMaxResults(-1);

    // test revisions method -- range: all time
    results =
        historyService.findSimpleMapRefSetMemberRevisions(c.getId().toString(),
            "19700101", currentDate, pfs, authToken);
    assertTrue(results.getTotalCount() == 5);

    // test revisions method lower bound
    results =
        historyService.findSimpleMapRefSetMemberRevisions(c.getId().toString(),
            "20050131", currentDate, pfs, authToken);
    assertTrue(results.getTotalCount() == 3);

    // test revisions method upper bound
    results =
        historyService.findSimpleMapRefSetMemberRevisions(c.getId().toString(),
            "19700731", "20050131", pfs, authToken);
    assertTrue(results.getTotalCount() == 3);

    // test retrieve release revision at specific release time
    // - time should match
    c =
        historyService.findSimpleMapRefSetMemberReleaseRevision(c.getId()
            .toString(), "20050131", authToken);
    assertTrue(dtFormat.format(c.getEffectiveTime()).equals("20050131"));

  }

  /**
   * Test simple ref set member methods
   * @throws Exception
   */
  @Test
  public void testNormalUseRestHistory014() throws Exception {

    objectNames.remove("SimpleRefSetMember");

    // The test id for this component
    testId = "c0a26c19-e051-5cfa-86c5-a625ca44e690";

    SimpleRefSetMemberList results;

    // get the component
    SimpleRefSetMember c = null;
    SimpleRefSetMemberList list =
        contentService.getSimpleRefSetMembersForConcept("333536009",
            terminology, version, authToken);
    for (SimpleRefSetMember a : list.getObjects()) {
      if (a.getTerminologyId().equals(testId))
        c = a;
    }
    if (c == null)
      fail("Could not retrieve simple ref set member");

    // test all simple ref set members modified over all time
    results =
        historyService.findSimpleRefSetMembersModifiedSinceDate(terminology,
            "19700101", pfs, authToken);
    assertTrue(results.getTotalCount() == 35);

    // test all simple ref set members modified after lower bound date
    results =
        historyService.findSimpleRefSetMembersModifiedSinceDate(terminology,
            "20080131", pfs, authToken);
    assertTrue(results.getTotalCount() == 14);

    // set pfs to no max results -- find methods currently do not return total
    // count correctly
    pfs.setMaxResults(-1);

    // test revisions method -- range: all time
    results =
        historyService.findSimpleRefSetMemberRevisions(c.getId().toString(),
            "19700101", currentDate, pfs, authToken);
    assertTrue(results.getTotalCount() == 4);

    // test revisions method lower bound
    results =
        historyService.findSimpleRefSetMemberRevisions(c.getId().toString(),
            "20060131", currentDate, pfs, authToken);
    assertTrue(results.getTotalCount() == 3);

    // test revisions method upper bound
    results =
        historyService.findSimpleRefSetMemberRevisions(c.getId().toString(),
            "19700731", "20060131", pfs, authToken);
    assertTrue(results.getTotalCount() == 2);

    // test retrieve release revision at specific release time
    // - time should match
    c =
        historyService.findSimpleRefSetMemberReleaseRevision(c.getId()
            .toString(), "20060131", authToken);
    assertTrue(dtFormat.format(c.getEffectiveTime()).equals("20060131"));

  }

  /**
   * Teardown.
   *
   * @throws Exception the exception
   */
  @Override
  @After
  public void teardown() throws Exception {

    // NOTE: When contents of test 002 are enabled
    // this teardown class must remove release info
    // objects created by testing addReleaseInfo
    // and startEditingCycle
    ReleaseInfo releaseInfo = historyService.getReleaseInfo(terminology, currentDate, authToken);
    if (releaseInfo != null) {
      historyService.removeReleaseInfo(releaseInfo.getId(), authToken);
    }

    // logout
    securityService.logout(authToken);

  }

}
