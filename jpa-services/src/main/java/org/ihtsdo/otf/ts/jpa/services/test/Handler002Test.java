/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.jpa.services.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.handlers.DefaultGraphResolutionHandler;
import org.ihtsdo.otf.ts.jpa.services.helper.TerminologyUtility;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.AttributeValueConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.AttributeValueDescriptionRefSetMember;
import org.ihtsdo.otf.ts.rf2.ComplexMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.DescriptionTypeRefSetMember;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.ModuleDependencyRefSetMember;
import org.ihtsdo.otf.ts.rf2.RefsetDescriptorRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.SimpleMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.SimpleRefSetMember;
import org.ihtsdo.otf.ts.rf2.jpa.AssociationReferenceConceptRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.AssociationReferenceDescriptionRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.AttributeValueConceptRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.AttributeValueDescriptionRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ComplexMapRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionTypeRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.LanguageRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ModuleDependencyRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RefsetDescriptorRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RelationshipJpa;
import org.ihtsdo.otf.ts.rf2.jpa.SimpleMapRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.SimpleRefSetMemberJpa;
import org.ihtsdo.otf.ts.services.ContentService;
import org.ihtsdo.otf.ts.services.handlers.GraphResolutionHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration testing for {@link DefaultGraphResolutionHandler}.
 */
public class Handler002Test {

  /** The handler service. */
  private GraphResolutionHandler handlerService;

  /**
   * Setup class.
   */
  @BeforeClass
  public static void setupClass() {
    // do nothing
  }

  /**
   * Setup.
   */
  @Before
  public void setup() {
    try {
      handlerService = new DefaultGraphResolutionHandler();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * /** Test normal use of the handler object.
   *
   * @throws Exception the exception
   */
  @Test
  public void testHandlerNormalUse002() throws Exception {
    Logger.getLogger(getClass()).info("TEST testHandlerNormalUse002");

    /*
     * Open a content service Retrieve concept 102276005 (SNOMEDCT) from the
     * content service. Call resolve on the concept Close the content service
     * TEST: The concept should have 3 descriptions, each with 2 language refset
     * entries, and it should have 8 relationships.
     */
    ContentService contentService = new ContentServiceJpa();
    Concept sctConcept =
        contentService.getSingleConcept("102276005", "SNOMEDCT", "latest");
    handlerService.resolve(sctConcept, TerminologyUtility
        .getHierarchicalIsaRels(sctConcept.getTerminology(),
            sctConcept.getTerminologyVersion()));
    contentService.close();
    for (Description description : sctConcept.getDescriptions()) {
      assertEquals(description.getLanguageRefSetMembers().size(), 2);
    }
    assertEquals(sctConcept.getRelationships().size(), 8);

    /*
     * Open a content service Retrieve description 165426013 (SNOMEDCT) from the
     * content service. Call resolve on the description Close the content
     * service TEST: the description should have 2 langauge refset entries, and
     * concept.getDefaultPreferredName should equal "Bipartite ossification"
     */
    contentService = new ContentServiceJpa();
    Description sctDescription =
        contentService.getDescription("165426013", "SNOMEDCT", "latest");
    handlerService.resolve(sctDescription);
    contentService.close();
    assertEquals(sctDescription.getLanguageRefSetMembers().size(), 2);
    assertEquals(sctDescription.getConcept().getDefaultPreferredName(),
        "Bipartite ossification");

    /*
     * Open a content service Retrieve relationship 4285932028 (SNOMEDCT) from
     * the content service. Call resolve on the relationship Close the content
     * service TEST: the relationship's source and destination concepts should
     * have id, terminologyId, terminology, defaultPreferredName set.
     */
    contentService = new ContentServiceJpa();
    Relationship sctRelationship =
        contentService.getRelationship("4285932028", "SNOMEDCT", "latest");
    handlerService.resolve(sctRelationship);
    contentService.close();
    assertTrue(sctRelationship.getSourceConcept().getId() > 0);
    assertTrue(sctRelationship.getSourceConcept().getTerminology().length() > 0);
    assertTrue(sctRelationship.getSourceConcept().getTerminologyId().length() > 0);
    assertTrue(sctRelationship.getSourceConcept().getDefaultPreferredName()
        .length() > 0);
    assertTrue(sctRelationship.getDestinationConcept().getId() > 0);
    assertTrue(sctRelationship.getDestinationConcept().getTerminology()
        .length() > 0);
    assertTrue(sctRelationship.getDestinationConcept().getTerminologyId()
        .length() > 0);
    assertTrue(sctRelationship.getDestinationConcept()
        .getDefaultPreferredName().length() > 0);

    /*
     * Open a content service Retrieve language refset member
     * 0ec8164a-47db-5f44-b3e4-50b33c495859 (SNOMEDCT) from the content service.
     * Call resolve on the language refset member Close the content service
     * TEST: language refset member should have a description with id,
     * terminologyId, terminology, and term set.
     */
    contentService = new ContentServiceJpa();
    LanguageRefSetMember sctLrm =
        contentService.getLanguageRefSetMember(
            "0ec8164a-47db-5f44-b3e4-50b33c495859", "SNOMEDCT", "latest");
    handlerService.resolve(sctLrm);
    contentService.close();
    assertTrue(sctLrm.getId() > 0);
    assertTrue(sctLrm.getTerminology().length() > 0);
    assertTrue(sctLrm.getTerminologyId().length() > 0);
    assertTrue(sctLrm.getDescription().getTerm().length() > 0);

    /*
     * Open a content service Retrieve concept attributeValue refset member
     * "0123aa2d-f249-54a5-9bed-0a3a4d5b7493" (SNOMEDCT) from the content
     * service. Close the content service TEST: the attribute value refset
     * member's concept should have id, terminologyId, terminology,
     * defaultPreferredName set.
     */
    contentService = new ContentServiceJpa();
    AttributeValueConceptRefSetMember sctAvrm =
        (AttributeValueConceptRefSetMember) contentService
            .getAttributeValueRefSetMember(
                "0123aa2d-f249-54a5-9bed-0a3a4d5b7493", "SNOMEDCT", "latest");
    handlerService.resolve(sctAvrm);
    contentService.close();
    assertTrue(sctAvrm.getConcept().getId() > 0);
    assertTrue(sctAvrm.getConcept().getTerminology().length() > 0);
    assertTrue(sctAvrm.getConcept().getTerminologyId().length() > 0);
    assertTrue(sctAvrm.getConcept().getDefaultPreferredName().length() > 0);

    /*
     * Open a content service Retrieve description attributeValue refset member
     * "00fa0433-ec87-5ddc-a9c1-6fc12055c0e6" (SNOMEDCT) from the content
     * service. Close the content service TEST: the attribute value refset
     * member's description should have id, terminologyId, terminology, term
     * set.
     */
    contentService = new ContentServiceJpa();
    AttributeValueDescriptionRefSetMember sctAvdrm =
        (AttributeValueDescriptionRefSetMember) contentService
            .getAttributeValueRefSetMember(
                "00fa0433-ec87-5ddc-a9c1-6fc12055c0e6", "SNOMEDCT", "latest");
    handlerService.resolve(sctAvdrm);
    contentService.close();
    assertTrue(sctAvdrm.getDescription().getId() > 0);
    assertTrue(sctAvdrm.getDescription().getTerminology().length() > 0);
    assertTrue(sctAvdrm.getDescription().getTerminologyId().length() > 0);
    assertTrue(sctAvdrm.getDescription().getTerm().length() > 0);

    /*
     * Open a content service Retrieve concept AssociationReference refset
     * member "00548d88-3544-55ce-9084-7b323ee327e7" (SNOMEDCT) from the content
     * service. Close the content service TEST: the association reference refset
     * member's concept should have id, terminologyId, terminology,
     * defaultPreferredName set.
     */
    contentService = new ContentServiceJpa();
    AssociationReferenceConceptRefSetMember sctArcrm =
        (AssociationReferenceConceptRefSetMember) contentService
            .getAssociationReferenceRefSetMember(
                "00548d88-3544-55ce-9084-7b323ee327e7", "SNOMEDCT", "latest");
    handlerService.resolve(sctArcrm);
    contentService.close();
    assertTrue(sctArcrm.getConcept().getId() > 0);
    assertTrue(sctArcrm.getConcept().getTerminology().length() > 0);
    assertTrue(sctArcrm.getConcept().getTerminologyId().length() > 0);
    assertTrue(sctArcrm.getConcept().getDefaultPreferredName().length() > 0);

    /*
     * Open a content service Retrieve description AssociationReference refset
     * member "0123aa2d-f249-54a5-9bed-0a3a4d5b7493" (SNOMEDCT) from the content
     * service. Close the content service TEST: the association reference refset
     * member's description should have id, terminologyId, terminology, term
     * set.
     */
    /** Commented out because currently no data for this condition. */
    /*
     * contentService = new ContentServiceJpa();
     * AssociationReferenceDescriptionRefSetMember sctArdrm =
     * (AssociationReferenceDescriptionRefSetMember)
     * contentService.getAssociationReferenceRefSetMember
     * ("0123aa2d-f249-54a5-9bed-0a3a4d5b7493", "SNOMEDCT", "latest");
     * handlerService.resolve(sctArdrm); contentService.close();
     * assertTrue(sctArdrm.getDescription().getId() > 0);
     * assertTrue(sctArdrm.getDescription().getTerminology().length() > 0);
     * assertTrue(sctArdrm.getDescription().getTerminologyId().length() > 0);
     * assertTrue(sctArdrm.getDescription().getTerm().length() > 0);
     */

    /*
     * Open a content service Retrieve a complex map refset member
     * "abcac540-87be-5405-9048-f14333f3abc2" (SNOMEDCT) from the content
     * service. Close the content service TEST: the complex map refset member's
     * concept should have id, terminologyId, terminology, defaultPreferredName
     * set.
     */
    contentService = new ContentServiceJpa();
    ComplexMapRefSetMember complexMapRefSetMember =
        contentService.getComplexMapRefSetMember(
            "abcac540-87be-5405-9048-f14333f3abc2", "SNOMEDCT", "latest");
    handlerService.resolve(complexMapRefSetMember);
    contentService.close();
    assertTrue(complexMapRefSetMember.getConcept().getId() > 0);
    assertTrue(complexMapRefSetMember.getConcept().getTerminology().length() > 0);
    assertTrue(complexMapRefSetMember.getConcept().getTerminologyId().length() > 0);
    assertTrue(complexMapRefSetMember.getConcept().getDefaultPreferredName()
        .length() > 0);

    /*
     * Open a content service Retrieve a description type refset member
     * "807f775b-1d66-5069-b58e-a37ace985dcf" (SNOMEDCT) from the content
     * service. Close the content service TEST: the description type refset
     * member's concept should have id, terminologyId, terminology,
     * defaultPreferredName set.
     */
    contentService = new ContentServiceJpa();
    DescriptionTypeRefSetMember descriptionTypeRefSetMember =
        contentService.getDescriptionTypeRefSetMember(
            "807f775b-1d66-5069-b58e-a37ace985dcf", "SNOMEDCT", "latest");
    handlerService.resolve(descriptionTypeRefSetMember);
    contentService.close();
    assertTrue(descriptionTypeRefSetMember.getConcept().getId() > 0);
    assertTrue(descriptionTypeRefSetMember.getConcept().getTerminology()
        .length() > 0);
    assertTrue(descriptionTypeRefSetMember.getConcept().getTerminologyId()
        .length() > 0);
    assertTrue(descriptionTypeRefSetMember.getConcept()
        .getDefaultPreferredName().length() > 0);

    /*
     * Open a content service Retrieve a module dependency refset member
     * "f6431457-161b-5b46-9217-573c20c00070" (SNOMEDCT) from the content
     * service. Close the content service TEST: the module dependency refset
     * member's concept should have id, terminologyId, terminology,
     * defaultPreferredName set.
     */
    contentService = new ContentServiceJpa();
    ModuleDependencyRefSetMember moduleDependencyRefSetMember =
        contentService.getModuleDependencyRefSetMember(
            "f6431457-161b-5b46-9217-573c20c00070", "SNOMEDCT", "latest");
    handlerService.resolve(moduleDependencyRefSetMember);
    contentService.close();
    assertTrue(moduleDependencyRefSetMember.getConcept().getId() > 0);
    assertTrue(moduleDependencyRefSetMember.getConcept().getTerminology()
        .length() > 0);
    assertTrue(moduleDependencyRefSetMember.getConcept().getTerminologyId()
        .length() > 0);
    assertTrue(moduleDependencyRefSetMember.getConcept()
        .getDefaultPreferredName().length() > 0);

    /*
     * Open a content service Retrieve a refset descriptor refset member
     * "a6417d6f-5a66-5039-8e94-698d28a4da46" (SNOMEDCT) from the content
     * service. Close the content service TEST: the refset descriptor refset
     * member's concept should have id, terminologyId, terminology,
     * defaultPreferredName set.
     */
    contentService = new ContentServiceJpa();
    RefsetDescriptorRefSetMember refsetDescriptorRefSetMember =
        contentService.getRefsetDescriptorRefSetMember(
            "a6417d6f-5a66-5039-8e94-698d28a4da46", "SNOMEDCT", "latest");
    handlerService.resolve(refsetDescriptorRefSetMember);
    contentService.close();
    assertTrue(refsetDescriptorRefSetMember.getConcept().getId() > 0);
    assertTrue(refsetDescriptorRefSetMember.getConcept().getTerminology()
        .length() > 0);
    assertTrue(refsetDescriptorRefSetMember.getConcept().getTerminologyId()
        .length() > 0);
    assertTrue(refsetDescriptorRefSetMember.getConcept()
        .getDefaultPreferredName().length() > 0);

    /*
     * Open a content service Retrieve a simple map refset member
     * "0001406e-99ce-5ebe-b84e-99691c094948" (SNOMEDCT) from the content
     * service. Close the content service TEST: the simple map refset member's
     * concept should have id, terminologyId, terminology, defaultPreferredName
     * set.
     */
    contentService = new ContentServiceJpa();
    SimpleMapRefSetMember simpleMapRefSetMember =
        contentService.getSimpleMapRefSetMember(
            "0001406e-99ce-5ebe-b84e-99691c094948", "SNOMEDCT", "latest");
    handlerService.resolve(simpleMapRefSetMember);
    contentService.close();
    assertTrue(simpleMapRefSetMember.getConcept().getId() > 0);
    assertTrue(simpleMapRefSetMember.getConcept().getTerminology().length() > 0);
    assertTrue(simpleMapRefSetMember.getConcept().getTerminologyId().length() > 0);
    assertTrue(simpleMapRefSetMember.getConcept().getDefaultPreferredName()
        .length() > 0);

    /*
     * Open a content service Retrieve a simple refset member
     * "06b90ec2-0c9b-5b00-8a64-35a3494cefb4" (SNOMEDCT) from the content
     * service. Close the content service TEST: the simple refset member's
     * concept should have id, terminologyId, terminology, defaultPreferredName
     * set.
     */
    contentService = new ContentServiceJpa();
    SimpleRefSetMember simpleRefSetMember =
        contentService.getSimpleRefSetMember(
            "06b90ec2-0c9b-5b00-8a64-35a3494cefb4", "SNOMEDCT", "latest");
    handlerService.resolve(simpleRefSetMember);
    contentService.close();
    assertTrue(simpleRefSetMember.getConcept().getId() > 0);
    assertTrue(simpleRefSetMember.getConcept().getTerminology().length() > 0);
    assertTrue(simpleRefSetMember.getConcept().getTerminologyId().length() > 0);
    assertTrue(simpleRefSetMember.getConcept().getDefaultPreferredName()
        .length() > 0);
  }

  /*
   * Test degenerate use of the handler object.
   * 
   * @throws Exception the exception
   */
  /**
   * Test handler degenerate use002.
   *
   * @throws Exception the exception
   */
  @Test
  public void testHandlerDegenerateUse002() throws Exception {
    // Call resolve((Concept)null)
    // TEST: exception
    try {
      handlerService.resolve((Concept) null,
          TerminologyUtility.getHierarchicalIsaRels("SNOMEDCT", "latest"));
      fail("Calling resolve((Concept)null) should have thrown an exception.");
    } catch (Exception e) {
      // do nothing
    }

    // Call resolve((Description)null)
    // TEST: exception
    try {
      handlerService.resolve((Description) null);
      fail("Calling resolve((Description)null) should have thrown an exception.");
    } catch (Exception e) {
      // do nothing
    }

    // Call resolve((Relationship)null)
    // TEST: exception
    try {
      handlerService.resolve((Relationship) null);
      fail("Calling resolve((Relationship)null) should have thrown an exception.");
    } catch (Exception e) {
      // do nothing
    }

    // Call resolve((LanguageRefSetMember)null)
    // TEST: exception
    try {
      handlerService.resolve((LanguageRefSetMember) null);
      fail("Calling resolve((LanguageRefSetMember)null) should have thrown an exception.");
    } catch (Exception e) {
      // do nothing
    }

    // Call resolve((AttributeValueConceptRefSetMember)null)
    // TEST: exception
    try {
      handlerService.resolve((AttributeValueConceptRefSetMember) null);
      fail("Calling resolve((AttributeValueConceptRefSetMember)null) should have thrown an exception.");
    } catch (Exception e) {
      // do nothing
    }

    // Call resolve((AttributeValueConceptRefSetMember)null)
    // TEST: exception
    try {
      handlerService.resolve((AttributeValueDescriptionRefSetMember) null);
      fail("Calling resolve((AttributeValueDescriptionRefSetMember)null) should have thrown an exception.");
    } catch (Exception e) {
      // do nothing
    }
  }

  /**
   * Test edge cases of the handler object.
   *
   * @throws Exception the exception
   */
  @Test
  public void testHandlerEdgeCases002() throws Exception {
    // Call computePreferredName(new ConceptJpa())
    // TEST: no exceptions
    handlerService.resolve(new ConceptJpa(),
        TerminologyUtility.getHierarchicalIsaRels("SNOMEDCT", "latest"));

    handlerService.resolve(new DescriptionJpa());

    handlerService.resolve(new RelationshipJpa());

    handlerService.resolve(new LanguageRefSetMemberJpa());

    handlerService.resolve(new AttributeValueConceptRefSetMemberJpa());

    handlerService.resolve(new AttributeValueDescriptionRefSetMemberJpa());

    handlerService.resolve(new AssociationReferenceConceptRefSetMemberJpa());

    handlerService
        .resolve(new AssociationReferenceDescriptionRefSetMemberJpa());

    handlerService.resolve(new ComplexMapRefSetMemberJpa());

    handlerService.resolve(new SimpleMapRefSetMemberJpa());

    handlerService.resolve(new SimpleRefSetMemberJpa());

    handlerService.resolve(new DescriptionTypeRefSetMemberJpa());

    handlerService.resolve(new RefsetDescriptorRefSetMemberJpa());

    handlerService.resolve(new ModuleDependencyRefSetMemberJpa());
  }

  /**
   * Teardown.
   */
  @After
  public void teardown() {
    // do nothing
  }

  /**
   * Teardown class.
   */
  @AfterClass
  public static void teardownClass() {
    // do nothing
  }

}
