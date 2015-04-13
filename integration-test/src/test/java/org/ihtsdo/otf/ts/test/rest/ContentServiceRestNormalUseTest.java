/*
 * 
 */
package org.ihtsdo.otf.ts.test.rest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.ihtsdo.otf.ts.helpers.AssociationReferenceRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.AttributeValueRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ComplexMapRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.DescriptionTypeRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.LanguageRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ModuleDependencyRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.PfsParameter;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.RefsetDescriptorRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.helpers.SimpleMapRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.SimpleRefSetMemberList;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceRefSetMember;
import org.ihtsdo.otf.ts.rf2.AttributeValueRefSetMember;
import org.ihtsdo.otf.ts.rf2.ComplexMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.Component;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.DescriptionTypeRefSetMember;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.ModuleDependencyRefSetMember;
import org.ihtsdo.otf.ts.rf2.SimpleMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.SimpleRefSetMember;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.test.helpers.PfsParameterTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * Implementation of the "Content Service REST Normal Use" Test Cases.
 *
 * @author ${author}
 */
public class ContentServiceRestNormalUseTest extends ContentServiceRestTest {

  /** The auth token. */
  private static String authToken;

  /** The snomed terminology. */
  private String snomedTerminology;

  /** The snomed version. */
  private String snomedVersion;

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

    icd9Terminology = "ICD9CM";
    icd9Version = "2013";
  }

  /**
   * Test get and find methods for Concept.
   *
   * @throws Exception the exception
   */
  // TODO DONE
  @Test
  public void testNormalUseRestContent001() throws Exception {

    String snomedTestId = "121000119106";
    String icd9TestId = "110-118.99";

    /** Test SNOMEDCT */

    // TEST: Retrieve concepts by terminology id
    ConceptList concepts =
        contentService.getConcepts(snomedTestId, snomedTerminology,
            snomedVersion, authToken);

    assertNotNull(concepts);
    assertTrue(concepts.getCount() == 1); // test data should return only 1
                                          // concept

    // get the concept from the list
    Concept concept = (Concept) concepts.getObjects().get(0);

    // check concept elements
    assertTrue(concept.getTerminologyId().equals(snomedTestId));
    assertTrue(concept.getTerminology().equals(snomedTerminology));
    assertTrue(concept.getTerminologyVersion().equals(snomedVersion));
    assertTrue(concept.getDefaultPreferredName().startsWith(
        "Lesion of skin of face"));

    // TEST: Retrieve single concept
    // Note that only concept has this method
    Concept concept_retrieved =
        contentService.getSingleConcept(concept.getTerminologyId(),
            concept.getTerminology(), concept.getTerminologyVersion(),
            authToken);

    assertNotNull(concept_retrieved);
    assertTrue(concept_retrieved.equals(concept));

    /** Test ICD9CM */

    // TEST: Retrieve concepts by terminology id
    concepts =
        contentService.getConcepts(icd9TestId, icd9Terminology, icd9Version,
            authToken);

    assertNotNull(concepts);
    assertTrue(concepts.getCount() == 1); // test data should return only 1
                                          // concept

    // get the concept from the list
    concept = (Concept) concepts.getObjects().get(0);

    // check concept elements
    assertTrue(concept.getTerminologyId().equals(icd9TestId));
    assertTrue(concept.getTerminology().equals(icd9Terminology));
    assertTrue(concept.getTerminologyVersion().equals(icd9Version));
    assertTrue(concept.getDefaultPreferredName().startsWith("MYCOSES"));

    // TEST: Retrieve single concept
    // Note that only concept has this method
    concept_retrieved =
        contentService.getSingleConcept(concept.getTerminologyId(),
            concept.getTerminology(), concept.getTerminologyVersion(),
            authToken);

    assertNotNull(concept_retrieved);
    assertTrue(concept_retrieved.equals(concept));
    
    SearchResultList results = contentService.findConceptsForQuery(snomedTerminology, snomedVersion, "ossification", new PfsParameterJpa(), authToken);
    
    // test paging and sorting
    PfsParameterTestHelper.testPagingAndSorting(contentService,
        contentService.getClass().getMethod("findConceptsForQuery", String.class,
            String.class, String.class, PfsParameterJpa.class, String.class), new Object[] {
            snomedTerminology, snomedVersion, "ossification", new PfsParameterJpa(), authToken
        }, ConceptJpa.class, results);
    
    // TEST - filtering: single field
    PfsParameterJpa pfs = new PfsParameterJpa();
    pfs.setQueryRestriction("defaultPreferredName:incomplete");
    results = contentService.findConceptsForQuery(snomedTerminology, snomedVersion, "ossification", pfs, authToken);
    assertNotNull(results);
    assertTrue(results.getCount() == 3); // returns 327877008, 93572002, 93563005 
    
    // TEST - filtering: multiple fields
    pfs = new PfsParameterJpa();
    pfs.setQueryRestriction("(terminologyId:93436006 or defaultPreferredName:\"Incomplete ossification of arch of caudal vertebra\")");
    results = contentService.findConceptsForQuery(snomedTerminology, snomedVersion, "ossification", pfs, authToken);
    assertNotNull(results);
    assertTrue(results.getCount() == 2); 
    
    // TEST - filtering: subfield
    pfs.setQueryRestriction("descriptions.term:bifid");
    results = contentService.findConceptsForQuery(snomedTerminology, snomedVersion, "ossification", pfs, authToken);
    assertNotNull(results);
    assertTrue(results.getCount() == 2); // returns 92493009, 102276005
    
    // TEST - filtering:  invalid restriction
    pfs.setQueryRestriction("invalid:invalid");
    results = contentService.findConceptsForQuery(snomedTerminology, snomedVersion, "ossification", pfs, authToken);
    assertNotNull(results);
    assertTrue(results.getCount() == 0); // no results
  
  }

  /**
   * Test normal use rest for Association Reference Ref Set Members.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseRestContent002() throws Exception {

    // set test ids
    String snomedTestId = "122456005";

    /** Test SNOMED */
    AssociationReferenceRefSetMemberList results =
        contentService.getAssociationReferenceRefSetMembersForConcept(
            snomedTestId, snomedTerminology, snomedVersion, authToken);

    // assert count
    assertNotNull(results);
    assertTrue(results.getCount() == 1);

    // check terminology id of contents
    Set<String> tids = new HashSet<String>();
    tids.add("b49c190a-89ac-5a2d-ad4e-33f09c026d61");

    for (AssociationReferenceRefSetMember<? extends Component> result : results
        .getObjects()) {
      if (!tids.contains(result.getTerminologyId())) {
        fail("Erroneous result retrieved");
      }
    }

  }

  /**
   * Test Get and Find methods for attribute value ref set members.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseRestContent003() throws Exception {

    String snomedTestId;

    /** Test SNOMED */

    // set test id for Attribute Value for Concept
    snomedTestId = "105592009";

    AttributeValueRefSetMemberList results =
        contentService.getAttributeValueRefSetMembersForConcept(snomedTestId,
            snomedTerminology, snomedVersion, authToken);

    // assert count
    assertNotNull(results);
    assertTrue(results.getCount() == 1);

    // check terminology id of contents
    Set<String> tids = new HashSet<String>();
    tids.add("97f68cf9-f53a-56db-89bb-3e08015d48fe");

    for (AttributeValueRefSetMember<?> result : results.getObjects()) {
      if (!tids.contains(result.getTerminologyId())) {
        fail("Erroneous result retrieved");
      }
    }

    // set test id for Attribute Value for Description
    snomedTestId = "166990010";

    results =
        contentService.getAttributeValueRefSetMembersForDescription(
            snomedTestId, snomedTerminology, snomedVersion, authToken);

    // assert count
    assertNotNull(results);
    assertTrue(results.getCount() == 1);

    // check terminology id of contents
    tids = new HashSet<String>();
    tids.add("f4ce2572-1592-51d3-839a-5b3fa05f6e63");

    for (AttributeValueRefSetMember<?> result : results.getObjects()) {
      if (!tids.contains(result.getTerminologyId())) {
        fail("Erroneous result retrieved");
      }
    }

    /** NO TEST FOR ICD9 -- no complex map ref set members */
  }

  /**
   * Test Get and Find methods for complex map ref set members.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseRestContent004() throws Exception {

    // set test ids
    String snomedTestId = "121000119106";

    /** Test SNOMED */
    ComplexMapRefSetMemberList results =
        contentService.getComplexMapRefSetMembersForConcept(snomedTestId,
            snomedTerminology, snomedVersion, authToken);

    // assert count
    assertNotNull(results);
    assertTrue(results.getCount() == 2);

    // check terminology id of contents
    Set<String> tids = new HashSet<String>();
    tids.add("28db8572-58d6-54bf-aa60-ea59596fa9ab");
    tids.add("2bcd5f4a-5212-5c46-8540-6c0e1e10285d");
    for (ComplexMapRefSetMember result : results.getObjects()) {
      if (!tids.contains(result.getTerminologyId())) {
        fail("Erroneous result retrieved");
      }
    }

    /** NO TEST FOR ICD9 -- no complex map ref set members */
  }

  /**
   * Test Get and Find methods for description type ref set members.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseRestContent005() throws Exception {

    // set test ids
    String snomedTestId = "900000000000550004";

    DescriptionTypeRefSetMemberList results =
        contentService.getDescriptionTypeRefSetMembersForConcept(snomedTestId,
            snomedTerminology, snomedVersion, authToken);

    // assert count
    assertNotNull(results);
    assertTrue(results.getCount() == 1);

    // check terminology id of contents
    Set<String> tids = new HashSet<String>();
    tids.add("900000000000550004");
    for (DescriptionTypeRefSetMember result : results.getObjects()) {
      if (!tids.contains(result.getTerminologyId())) {
        fail("Erroneous result retrieved");
      }
    }

    /** NO TEST FOR ICD9 -- no description types */
  }

  /**
   * Test normal use rest for language ref set members.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseRestContent006() throws Exception {

    // set test ids
    String snomedTestId = "513602011";

    /** Test SNOMED */
    LanguageRefSetMemberList results =
        contentService.getLanguageRefSetMembersForDescription(snomedTestId,
            snomedTerminology, snomedVersion, authToken);

    // assert count
    assertNotNull(results);
    assertTrue(results.getCount() == 2);

    // check terminology id of contents
    Set<String> tids = new HashSet<String>();
    tids.add("e5708b31-364e-5273-b89f-4d67aab6f9b3");
    tids.add("5b770e30-537a-5761-b280-776c31c43de2");
    for (LanguageRefSetMember result : results.getObjects()) {
      if (!tids.contains(result.getTerminologyId())) {
        fail("Erroneous result retrieved");
      }
    }
  }

  /**
   * Test normal use rest for module dependency refsets
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseRestContent007() throws Exception {

    // set module id
    String snomedTestId = "900000000000207008";

    /** Test SNOMED */
    ModuleDependencyRefSetMemberList results =
        contentService.getModuleDependencyRefSetMembersForModule(snomedTestId,
            snomedTerminology, snomedVersion, authToken);

    // assert count
    assertNotNull(results);
    assertTrue(results.getCount() == 1);

    // check terminology id of contents
    Set<String> tids = new HashSet<String>();
    tids.add("1244116f-fdb5-5645-afcc-5281288409da");

    for (ModuleDependencyRefSetMember result : results.getObjects()) {
      if (!tids.contains(result.getTerminologyId())) {
        fail("Erroneous result retrieved");
      }
    }

  }

  /**
   * Test normal use rest for ref set descriptors
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseRestContent008() throws Exception {

    String snomedTestId = "900000000000456007";

    /** Test SNOMED */
    RefsetDescriptorRefSetMemberList results =
        contentService.getRefsetDescriptorRefSetMembers(snomedTestId,
            snomedTerminology, snomedVersion, authToken);

    // assert count
    assertNotNull(results);
    assertTrue(results.getCount() == 68);

    /** NO TEST FOR ICD9 */

  }

  /**
   * Test normal use rest for simple map ref set members
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseRestContent009() throws Exception {

    String snomedTestId = "116314006";

    /** Test SNOMED */
    SimpleMapRefSetMemberList results =
        contentService.getSimpleMapRefSetMembersForConcept(snomedTestId,
            snomedTerminology, snomedVersion, authToken);

    // assert count
    assertNotNull(results);
    assertTrue(results.getCount() == 2);

    // check terminology id of contents
    Set<String> tids = new HashSet<String>();
    tids.add("79d7fe9d-1d01-54ff-a997-05308c30d623");
    tids.add("d466211f-4cb6-5e2c-86e5-3ce1a442e056");

    for (SimpleMapRefSetMember result : results.getObjects()) {
      if (!tids.contains(result.getTerminologyId())) {
        fail("Erroneous result retrieved");
      }
    }

  }

  /**
   * Test normal use rest for simple ref set members
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseRestContent010() throws Exception {

    String snomedTestId = "116011005";

    /** Test SNOMED */
    SimpleRefSetMemberList results =
        contentService.getSimpleRefSetMembersForConcept(snomedTestId,
            snomedTerminology, snomedVersion, authToken);

    // assert count
    assertNotNull(results);
    assertTrue(results.getCount() == 1);

    // check terminology id of contents
    Set<String> tids = new HashSet<String>();
    tids.add("3eff56a7-243d-5450-b5bf-8093a07b4f89");

    for (SimpleRefSetMember result : results.getObjects()) {
      if (!tids.contains(result.getTerminologyId())) {
        fail("Erroneous result retrieved");
      }
    }

  }

  /*
   * public void testNormalUseRestContent111001() throws Exception {
   * 
   * // local variables ConceptList conceptList = new ConceptListJpa(); Concept
   * concept, newConcept; String snomedTestId = ""; String icd9TestId = "";
   *//**
   * Procedure 1: Get concepts for SNOMEDCT.
   *
   * @param <clazz> the
   * @param clazz the clazz
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param terminologyVersion the terminology version
   * @return the concepts
   */
  /*
   * 
   * try { conceptList = contentService.getConcepts(snomedTestId,
   * snomedTerminology, snomedVersion, authToken); } catch (Exception e) {
   * fail("Get concepts threw unexpected exception"); }
   * assertTrue(conceptList.getCount() == 1); concept =
   * conceptList.getObjects().get(0);
   * assertTrue(concept.getTerminologyId().equals(snomedTestId));
   * assertTrue(concept.getTerminology().equals(snomedTerminology));
   * assertTrue(concept.getTerminologyVersion().equals(snomedVersion));
   * assertTrue(concept.getDefaultPreferredName().startsWith(
   * "Lesion of skin of face"));
   * 
   * // check relationships both through count and objects
   * 
   * assertTrue(concept.getRelationships().size() == 7);
   * 
   * // check descriptions both through count and objects
   * assertTrue(concept.getDescriptions().size() == 2);
   * 
   * // check language ref set members for (Description d :
   * concept.getDescriptions()) { assertTrue(d.getLanguageRefSetMembers().size()
   * == 2); }
   * 
   * // Get single concept for terminology/version // TEST: Returns one concept,
   * terminology fields matche try { newConcept =
   * contentService.getSingleConcept(snomedTestId, snomedTerminology,
   * snomedVersion, authToken); assertTrue(concept.equals(newConcept)); } catch
   * (Exception e) { fail("Get concepts threw unexpected exception"); }
   *//** Procedure 2: Get concepts for ICD9CM */
  /*
   * 
   * try { conceptList = contentService.getConcepts(icd9TestId, icd9Terminology,
   * icd9Version, authToken); } catch (Exception e) {
   * fail("Get concepts threw unexpected exception"); }
   * assertTrue(conceptList.getCount() == 1); concept =
   * conceptList.getObjects().get(0);
   * assertTrue(concept.getTerminologyId().equals(icd9TestId));
   * assertTrue(concept.getTerminology().equals(icd9Terminology));
   * assertTrue(concept.getTerminologyVersion().equals(icd9Version));
   * assertTrue(concept.getDefaultPreferredName().startsWith("MYCOSES"));
   * 
   * // check relationships both through count and objects
   * 
   * assertTrue(concept.getRelationships().size() == 1);
   * 
   * // check descriptions both through count and objects
   * 
   * assertTrue(concept.getDescriptions().size() == 1);
   * 
   * // check language ref set members for (Description d :
   * concept.getDescriptions()) { assertTrue(d.getLanguageRefSetMembers().size()
   * == 0); }
   * 
   * // Get single concept for terminology/version // TEST: Returns one concept,
   * terminology fields matche try { newConcept =
   * contentService.getSingleConcept(icd9TestId, icd9Terminology, icd9Version,
   * authToken); assertTrue(concept.equals(newConcept)); } catch (Exception e) {
   * fail("Get concepts threw unexpected exception"); }
   *//** Procedure 3: Find concepts */
  /*
   * 
   * // For test, execute findConceptsForQuery("ossification", ...) for //
   * SNOMEDCT String query = "ossification"; PfsParameterJpa pfs = new
   * PfsParameterJpa(); SearchResultList searchResults;
   * 
   * // Raw results – No pfs parameter // TEST: 10 results searchResults =
   * contentService.findConceptsForQuery(snomedTerminology, snomedVersion,
   * query, pfs, authToken);
   * 
   * assertTrue(searchResults.getCount() == 10);
   * 
   * // Sorted results – Pfs parameter with sortField defaultPreferredName //
   * TEST: 10 results, sorted alphabetically
   * pfs.setSortField("defaultPreferredName"); searchResults =
   * contentService.findConceptsForQuery(snomedTerminology, snomedVersion,
   * query, pfs, authToken); assertTrue(searchResults.getCount() == 10);
   * assertTrue(PfsParameterForConceptTest.testSort(searchResults, pfs));
   * 
   * // test descending order pfs.setAscending(false);
   * 
   * searchResults = contentService.findConceptsForQuery(snomedTerminology,
   * snomedVersion, query, pfs, authToken); assertTrue(searchResults.getCount()
   * == 10); assertTrue(PfsParameterForConceptTest.testSort(searchResults,
   * pfs));
   * 
   * // store the sorted results SearchResultList storedResults = searchResults;
   * 
   * // Paged, sorted results, first page – Pfs parameter with max results 5 and
   * // sortField defaultPreferredName // TEST: 5 results, matching first 5
   * results from previous test pfs.setSortField("defaultPreferredName");
   * pfs.setStartIndex(0); pfs.setMaxResults(5); searchResults =
   * contentService.findConceptsForQuery(snomedTerminology, snomedVersion,
   * query, pfs, authToken);
   * assertTrue(PfsParameterForConceptTest.testSort(searchResults, pfs));
   * assertTrue(PfsParameterForConceptTest.testPaging(searchResults,
   * storedResults, pfs));
   * 
   * // Paged, sorted results, second page – Pfs parameter with startIndex 6,
   * max // results 5 and sortField defaultPreferredName // TEST: 5 results,
   * matching second set of 5 results from previous test
   * pfs.setSortField("defaultPreferredName"); pfs.setStartIndex(5);
   * pfs.setMaxResults(5); searchResults =
   * contentService.findConceptsForQuery(snomedTerminology, snomedVersion,
   * query, pfs, authToken);
   * 
   * assertTrue(PfsParameterForConceptTest.testPaging(searchResults,
   * storedResults, pfs));
   * assertTrue(PfsParameterForConceptTest.testSort(searchResults, pfs));
   * 
   * // test lucene query restriction pfs = new PfsParameterJpa();
   * pfs.setQueryRestriction("terminologyId:93563005"); searchResults =
   * contentService.findConceptsForQuery(snomedTerminology, snomedVersion,
   * query, pfs, authToken);
   * 
   * System.out.println("QR results: " + searchResults.toString());
   * 
   * assertTrue(searchResults.getCount() == 1);
   * assertTrue(searchResults.getObjects().get(0).getTerminologyId()
   * .equals("93563005"));
   * 
   * }
   */

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
