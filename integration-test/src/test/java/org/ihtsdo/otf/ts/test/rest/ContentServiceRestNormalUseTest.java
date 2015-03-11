package org.ihtsdo.otf.ts.test.rest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;

import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ConceptListJpa;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
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
          contentService.getConcepts(icd9TestId, icd9Terminology, icd9Version,
              authToken);
    } catch (Exception e) {
      fail("Get concepts threw unexpected exception");
    }
    assertTrue(conceptList.getCount() == 1);
    concept = conceptList.getObjects().get(0);
    assertTrue(concept.getTerminologyId().equals(icd9TestId));
    assertTrue(concept.getTerminology().equals(icd9Terminology));
    assertTrue(concept.getTerminologyVersion().equals(icd9Version));
    assertTrue(concept.getDefaultPreferredName().startsWith("MYCOSES"));

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
    assertTrue(PfsParameterForConceptTest.testSort(searchResults, pfs));

    // test descending order
    pfs.setAscending(false);

    searchResults =
        contentService.findConceptsForQuery(snomedTerminology, snomedVersion,
            query, pfs, authToken);
    assertTrue(searchResults.getCount() == 10);
    assertTrue(PfsParameterForConceptTest.testSort(searchResults, pfs));

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
    assertTrue(PfsParameterForConceptTest.testSort(searchResults, pfs));
    assertTrue(PfsParameterForConceptTest.testPaging(searchResults,
        storedResults, pfs));

    // Paged, sorted results, second page – Pfs parameter with startIndex 6, max
    // results 5 and sortField defaultPreferredName
    // TEST: 5 results, matching second set of 5 results from previous test
    pfs.setSortField("defaultPreferredName");
    pfs.setStartIndex(5);
    pfs.setMaxResults(5);
    searchResults =
        contentService.findConceptsForQuery(snomedTerminology, snomedVersion,
            query, pfs, authToken);

    assertTrue(PfsParameterForConceptTest.testPaging(searchResults,
        storedResults, pfs));
    assertTrue(PfsParameterForConceptTest.testSort(searchResults, pfs));

    // test lucene query restriction
    pfs = new PfsParameterJpa();
    pfs.setQueryRestriction("terminologyId:93563005");
    searchResults =
        contentService.findConceptsForQuery(snomedTerminology, snomedVersion,
            query, pfs, authToken);

    System.out.println("QR results: " + searchResults.toString());

    assertTrue(searchResults.getCount() == 1);
    assertTrue(searchResults.getObjects().get(0).getTerminologyId()
        .equals("93563005"));

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

    // Get children for SNOMEDCT concept // TEST: Expect 80 children
    conceptList =
        contentService.getChildConcepts(snomedTestId, snomedTerminology,
            snomedVersion, pfs, authToken);
    assertTrue(conceptList.getCount() == 80);

    // Get descendants for SNOMEDCT concept // TEST: Expect 94 descendants
    conceptList =
        contentService.getDescendantConcepts(snomedTestId, snomedTerminology,
            snomedVersion, pfs, authToken);
    assertTrue(conceptList.getCount() == 94);

    // Get ancestors for SNOMEDCT concept // TEST: Expect 11 ancestors
    conceptList =
        contentService.getAncestorConcepts(snomedTestId, snomedTerminology,
            snomedVersion, pfs, authToken);
    assertTrue(conceptList.getCount() == 11);

    // Get children for ICD9CM concept
    // TEST: Expect 8 children
    conceptList =
        contentService.getChildConcepts(icd9TestId, icd9Terminology,
            icd9Version, pfs, authToken);
    assertTrue(conceptList.getCount() == 8);

    // Get descendants for ICD9CM concept
    // TEST: Expect 81 descendants
    conceptList =
        contentService.getDescendantConcepts(icd9TestId, icd9Terminology,
            icd9Version, pfs, authToken);
    assertTrue(conceptList.getCount() == 81);

    // Get ancestors for ICD9CM concept
    // TEST: Expect 2 ancestors
    conceptList =
        contentService.getAncestorConcepts(icd9TestId, icd9Terminology,
            icd9Version, pfs, authToken);
    assertTrue(conceptList.getCount() == 2);

  }

  /**
   * Test Get methods for descriptions
   * @throws Exception
   */
  @Test
  public void testNormalUseRestContent003() throws Exception {

    Set<Description> descriptions;
    Concept concept;
    Description description, description2;

    /**
     * Test retrieval of SNOMEDCT descriptions
     */

    // Get SNOMEDCT concept
    concept =
        contentService.getSingleConcept(snomedTestId, snomedTerminology,
            snomedVersion, authToken);

    // Get descriptions for concept
    descriptions = concept.getDescriptions();

    // Get description by terminologyId (from first description retrieved for
    // concept)
    description =
        contentService.getDescription(descriptions.iterator().next()
            .getTerminologyId(), concept.getTerminology(),
            concept.getTerminologyVersion(), authToken);

    // TEST: ﻿Description has terminologyId
    assertTrue(description.getTerminologyId().equals("891000119115"));

    // TEST:﻿ Description has concept equal to retrieved concept
    assertTrue(description.getConcept().getId().equals(concept.getId()));

    // Get description by id (from previous description)
    description2 =
        contentService.getDescription(description.getId(), authToken);

    // TEST: Descriptions are equal
    assertTrue(description.equals(description2));

    /**
     * Test retrieval of ICD9CM descriptions
     */

    // Get ICD9CM concept
    concept =
        contentService.getSingleConcept(icd9TestId, icd9Terminology,
            icd9Version, authToken);

    descriptions = concept.getDescriptions();

    // Get description by terminologyId (from first description retrieved for
    // concept)
    description =
        contentService.getDescription(descriptions.iterator().next()
            .getTerminologyId(), concept.getTerminology(),
            concept.getTerminologyVersion(), authToken);

    // TEST: ﻿Description has terminologyId
    assertTrue(description.getTerminologyId().equals("D0000017"));

    // TEST:﻿ Description has concept equal to retrieved concept
    assertTrue(description.getConcept().getId().equals(concept.getId()));

    // Get description by id (from previous description)
    description2 =
        contentService.getDescription(description.getId(), authToken);

    // TEST: Descriptions are equal
    assertTrue(description.equals(description2));

  }

  /**
   * Test Get methods for relationships
   * @throws Exception
   */
  @Test
  public void testNormalUseRestContent004() throws Exception {

    Set<Relationship> relationships;
    Concept concept;
    Relationship relationship, relationship2;

    /**
     * Test retrieval of SNOMEDCT relationships
     */

    // Get SNOMEDCT concept
    concept =
        contentService.getSingleConcept(snomedTestId, snomedTerminology,
            snomedVersion, authToken);

    // Get relationships for concept
    relationships = concept.getRelationships();

    // Get relationship by terminologyId (from first relationship retrieved for
    // concept)
    relationship =
        contentService.getRelationship(relationships.iterator().next()
            .getTerminologyId(), concept.getTerminology(),
            concept.getTerminologyVersion(), authToken);

    // TEST: ﻿Relationship has terminologyId
    assertTrue(relationship.getTerminologyId().equals("4731220023"));

    // TEST:﻿ Relationship has concept equal to retrieved concept
    assertTrue(relationship.getSourceConcept().getId().equals(concept.getId()));

    // Get relationship by id (from previous relationship)
    relationship2 =
        contentService.getRelationship(relationship.getId(), authToken);

    // TEST: Relationships are equal
    assertTrue(relationship.equals(relationship2));

    /**
     * Test retrieval of ICD9CM relationships
     */

    // Get ICD9CM concept
    concept =
        contentService.getSingleConcept(icd9TestId, icd9Terminology,
            icd9Version, authToken);

    relationships = concept.getRelationships();

    // Get relationship by terminologyId (from first relationship retrieved for
    // concept)
    relationship =
        contentService.getRelationship(relationships.iterator().next()
            .getTerminologyId(), concept.getTerminology(),
            concept.getTerminologyVersion(), authToken);

    // TEST: ﻿Relationship has terminologyId
    assertTrue(relationship.getTerminologyId().equals("4816"));

    // TEST:﻿ Relationship has concept equal to retrieved concept
    assertTrue(relationship.getSourceConcept().getId().equals(concept.getId()));

    // Get relationship by id (from previous relationship)
    relationship2 =
        contentService.getRelationship(relationship.getId(), authToken);

    // TEST: Relationships are equal
    assertTrue(relationship.equals(relationship2));

  }

  /**
   * Test Get methods for languages
   * @throws Exception
   */
  @Test
  public void testNormalUseRestContent005() throws Exception {

    Set<LanguageRefSetMember> languages;
    Concept concept;
    Description description;
    LanguageRefSetMember language, language2;

    /**
     * Test retrieval of SNOMEDCT languages
     */

    // Get SNOMEDCT concept
    concept =
        contentService.getSingleConcept(snomedTestId, snomedTerminology,
            snomedVersion, authToken);

    // Get languages for concept
    description = concept.getDescriptions().iterator().next();
    languages = description.getLanguageRefSetMembers();

    // Get language by terminologyId (from first language retrieved for
    // concept)
    language =
        contentService.getLanguageRefSetMember(languages.iterator().next()
            .getTerminologyId(), concept.getTerminology(),
            concept.getTerminologyVersion(), authToken);

    System.out.println(language.toString());

    // TEST: ﻿LanguageRefSetMember has terminologyId
    assertTrue(language.getTerminologyId().equals(
        "5d556777-934a-582e-b828-02e3b7cfeb67"));

    // TEST:﻿ LanguageRefSetMember has concept equal to retrieved concept
    assertTrue(language.getDescription().getId().equals(description.getId()));

    // Get language by id (from previous language)
    language2 =
        contentService.getLanguageRefSetMember(language.getId(), authToken);

    // TEST: LanguageRefSetMembers are equal
    assertTrue(language.equals(language2));

    /**
     * ICD9CM does not have language ref set members, no test
     */

  }

  /**
   * Test Get rest functions for Association Reference Concept Ref Set Members
   * @throws Exception
   */
  @Test
  public void testNormalUseRestContent006() throws Exception {
    
    Set<AssociationReferenceConceptRefSetMember> refsetMembers;
    Concept concept;
    Description description;
    AssociationReferenceConceptRefSetMember refsetMember, refsetMember2;
    
    
    /**
     * Test retrieval of SNOMEDCT refsetMembers
     * NOTE:  Ref Set Member id hardcoded, as concept's set is @XmlTransient
     */

    String refSetMemberTerminologyId = "d9835599-19ac-56bd-89ad-18b37713dfbd";
    
    // Get refsetMember by terminologyId (from first refsetMember retrieved for
    // concept)
    refsetMember =
        contentService.getAssociationReferenceConceptRefSetMember(refSetMemberTerminologyId,
            snomedTerminology, snomedVersion,
            authToken);
    
    System.out.println(refsetMember.toString());
   
    // TEST: ﻿AssociationRefSetMember has terminologyId
    assertTrue(refsetMember.getTerminologyId().equals("d9835599-19ac-56bd-89ad-18b37713dfbd"));
    // TEST:﻿ AssociationRefSetMember has concept equal to retrieved concept
    assertTrue(refsetMember.getConcept().getTerminologyId().equals("103389009"));
    
    // Get refsetMember by id (from previous refsetMember)
    refsetMember2 = contentService.getAssociationReferenceConceptRefSetMember(refsetMember.getId(), authToken);

    // TEST: AssociationRefSetMembers are equal
    assertTrue(refsetMember.equals(refsetMember2));
    
    /**
     * ICD9CM does not have association reference concept ref set members
     * No test
     */
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
