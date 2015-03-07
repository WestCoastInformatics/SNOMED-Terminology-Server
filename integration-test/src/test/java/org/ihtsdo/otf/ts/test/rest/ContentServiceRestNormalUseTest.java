package org.ihtsdo.otf.ts.test.rest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ConceptListJpa;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.test.helpers.PfsParameterForConceptTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Implementation of the "Content Service REST Normal Use" Test Cases.
 */
public class ContentServiceRestNormalUseTest extends ContentServiceRestTest {

  private static String authToken;

  String snomedTestId, snomedTerminology, snomedVersion;

  String icd9TestId, icd9Terminology, icd9Version;

  /**
   * Create test fixtures per test.
   *
   * @throws Exception the exception
   */
  @Before
  public void setup() throws Exception {

    // authentication
    authToken = securityService.authenticate(testUser, testPassword);

    // set terminology and version
    snomedTerminology = "SNOMEDCT";
    snomedVersion = "latest";
    snomedTestId = "121000119106";

    icd9Terminology = "ICD9CM";
    icd9Version = "2013";
    icd9TestId = "110-118.99";
  }

  /**
   * Test Get and Find methods for concepts
   * @throws Exception
   */
  @Test
  public void testNormalUseRestContent001() throws Exception {

    // local variables
    ConceptList conceptList = new ConceptListJpa();
    Concept concept, newConcept;

    /** Procedure 1: Get concepts for SNOMEDCT */

    try {
      conceptList =
          contentService.getConcepts(snomedTestId, snomedTerminology,
              snomedVersion, authToken);
    } catch (Exception e) {
      fail("Get concepts threw unexpected exception");
    }
    assertTrue(conceptList.getCount() == 1);
    concept = conceptList.getObjects().get(0);
    assertTrue(concept.getTerminologyId().equals(snomedTestId));
    assertTrue(concept.getTerminology().equals(snomedTerminology));
    assertTrue(concept.getTerminologyVersion().equals(snomedVersion));
    assertTrue(concept.getDefaultPreferredName().startsWith(
        "Lesion of skin of face"));

    // check relationships both through count and objects
    assertTrue(concept.getRelationshipCount() == 7);
    assertTrue(concept.getRelationships().size() == 7);

    // check descriptions both through count and objects
    assertTrue(concept.getDescriptionCount() == 2);
    assertTrue(concept.getDescriptions().size() == 2);

    // check language ref set members
    for (Description d : concept.getDescriptions()) {
      assertTrue(d.getLanguageRefSetMemberCount() == 2);
      assertTrue(d.getLanguageRefSetMembers().size() == 2);
    }

    // Get single concept for terminology/version
    // TEST: Returns one concept, terminology fields matche
    try {
      newConcept =
          contentService.getSingleConcept(snomedTestId, snomedTerminology,
              snomedVersion, authToken);
      assertTrue(concept.equals(newConcept));
    } catch (Exception e) {
      fail("Get concepts threw unexpected exception");
    }

    // Get concept by id
    // getConcept(concept.getId(), ...)
    // TEST: Returns one concept, terminology id 138875005
    try {
      newConcept = contentService.getConcept(concept.getId(), authToken);
      assertTrue(concept.equals(newConcept));
    } catch (Exception e) {
      fail("Get concept by id threw unexpected exception");
    }

    /** Procedure 2: Get concepts for ICD9CM */

    try {
      conceptList =
          contentService.getConcepts(icd9TestId, icd9Terminology,
              icd9Version, authToken);
    } catch (Exception e) {
      fail("Get concepts threw unexpected exception");
    }
    assertTrue(conceptList.getCount() == 1);
    concept = conceptList.getObjects().get(0);
    assertTrue(concept.getTerminologyId().equals(icd9TestId));
    assertTrue(concept.getTerminology().equals(icd9Terminology));
    assertTrue(concept.getTerminologyVersion().equals(icd9Version));
    assertTrue(concept.getDefaultPreferredName().startsWith(
        "MYCOSES"));

    // check relationships both through count and objects
    System.out.println(concept.getRelationshipCount());
    System.out.println(concept.getDescriptionCount());
    assertTrue(concept.getRelationshipCount() == 1);
    assertTrue(concept.getRelationships().size() == 1);

    // check descriptions both through count and objects
    assertTrue(concept.getDescriptionCount() == 1);
    assertTrue(concept.getDescriptions().size() == 1);

    // check language ref set members
    for (Description d : concept.getDescriptions()) {
      System.out.println(d.getLanguageRefSetMemberCount());
      assertTrue(d.getLanguageRefSetMemberCount() == 0);
      assertTrue(d.getLanguageRefSetMembers().size() == 0);
    }

    // Get single concept for terminology/version
    // TEST: Returns one concept, terminology fields matche
    try {
      newConcept =
          contentService.getSingleConcept(icd9TestId, icd9Terminology,
              icd9Version, authToken);
      assertTrue(concept.equals(newConcept));
    } catch (Exception e) {
      fail("Get concepts threw unexpected exception");
    }
    
    /** Procedure 3: Find concepts */

    // For test, execute findConceptsForQuery("ossification", ...) for
    // SNOMEDCT
    String query = "ossification";
    PfsParameterJpa pfs = new PfsParameterJpa();
    SearchResultList searchResults;

    // Raw results – No pfs parameter
    // TEST: 10 results
    searchResults =
        contentService.findConceptsForQuery(snomedTerminology, snomedVersion,
            query, pfs, authToken);

    assertTrue(searchResults.getCount() == 10);

    // Sorted results – Pfs parameter with sortField defaultPreferredName
    // TEST: 10 results, sorted alphabetically
    pfs.setSortField("defaultPreferredName");
    searchResults =
        contentService.findConceptsForQuery(snomedTerminology, snomedVersion,
            query, pfs, authToken);
    assertTrue(searchResults.getCount() == 10);
    assertTrue(PfsParameterForConceptTest.testSort(searchResults, pfs.getSortField()));

    // store the sorted results
    SearchResultList storedResults = searchResults;

    // Paged, sorted results, first page – Pfs parameter with max results 5 and
    // sortField defaultPreferredName
    // TEST: 5 results, matching first 5 results from previous test
    pfs.setSortField("defaultPreferredName");
    pfs.setStartIndex(0);
    pfs.setMaxResults(5);
    searchResults =
        contentService.findConceptsForQuery(snomedTerminology, snomedVersion,
            query, pfs, authToken);
    assertTrue(PfsParameterForConceptTest.testSort(searchResults, "defaultPreferredName"));
    assertTrue(PfsParameterForConceptTest.testPaging(searchResults, storedResults, 1, 5));
    
    // Paged, sorted results, second page – Pfs parameter with startIndex 6, max
    // results 5 and sortField defaultPreferredName
    // TEST: 5 results, matching second set of 5 results from previous test
    pfs.setSortField("defaultPreferredName");
    pfs.setStartIndex(5);
    pfs.setMaxResults(5);
    searchResults =
        contentService.findConceptsForQuery(snomedTerminology, snomedVersion,
            query, pfs, authToken);

    assertTrue(PfsParameterForConceptTest.testPaging(searchResults, storedResults, 2, 5));
    assertTrue(PfsParameterForConceptTest.testSort(searchResults, "defaultPreferredName"));
    
    // test lucene query restriction
    pfs = new PfsParameterJpa();
    pfs.setQueryRestriction("terminologyId:93563005");
    searchResults =
        contentService.findConceptsForQuery(snomedTerminology, snomedVersion,
            query, pfs, authToken);
    
    System.out.println("QR results: " + searchResults.toString());
    
    assertTrue(searchResults.getCount() == 1);
    
    // TODO Test ordering (ascending/descending)
  }

  /**
   * Test transitive closure methods (ancestors, descendants, children)
   * @throws Exception
   */
  @Test
  public void testNormalUseRestContent002() throws Exception {

    PfsParameterJpa pfs = new PfsParameterJpa();
    ConceptList conceptList;

    // Get children for SNOMEDCT concept
    conceptList =
        contentService.getChildConcepts(snomedTestId, snomedTerminology,
            snomedVersion, pfs, authToken);
    System.out.println("N = " + conceptList.getCount());
    assertTrue(conceptList.getCount() == 38);

    // Get descendants for SNOMEDCT concept
    conceptList =
        contentService.getDescendantConcepts(snomedTestId, snomedTerminology,
            snomedVersion, pfs, authToken);
    assertTrue(conceptList.getCount() == 9911);

    // Get ancestors for SNOMEDCT concept
    // TEST: Expect 0 ancestors
    conceptList =
        contentService.getAncestorConcepts(snomedTestId, snomedTerminology,
            snomedVersion, pfs, authToken);
    assertTrue(conceptList.getCount() == 0);

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
