/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.jpa.services.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Properties;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.handlers.SnomedReleaseIdentifierAssignmentHandler;
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
import org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration testing for {@link SnomedReleaseIdentifierAssignmentHandler}.
 */
public class Handler008Test {

  /** The handler service. */
  private IdentifierAssignmentHandler handlerService;

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
      handlerService = new SnomedReleaseIdentifierAssignmentHandler();
      Properties p = new Properties();
      p.setProperty("concept.max", "0");
      p.setProperty("description.max", "0");
      p.setProperty("relationship.max", "0");
      p.setProperty("namespace.id", "0");
      handlerService.setProperties(p);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Test normal use of the handler object.
   *
   * @throws Exception the exception
   */
  @Test
  public void testHandlerNormalUse008() throws Exception {
    Logger.getLogger(getClass()).info("TEST testHandlerNormalUse008");

    /*
     * Retrieve concept 102276005 (SNOMEDCT) from the content service. Call
     * getTerminologyId() on the concept TEST: The concept's terminology id
     * should match the terminology id returned by the service Clear the
     * terminology id Call getTerminologyId() on the relationship TEST: the
     * terminology id should be reassigned with the next incremented value
     */
    ContentService contentService = new ContentServiceJpa();
    Concept sctConcept =
        contentService.getSingleConcept("102276005", "SNOMEDCT", "latest");
    String id = handlerService.getTerminologyId(sctConcept);
    assertEquals(sctConcept.getTerminologyId(), id);
    sctConcept.setTerminologyId("");
    id = handlerService.getTerminologyId(sctConcept);
    assertEquals("10019", id);

    /*
     * Retrieve description 165426013 (SNOMEDCT) from the content service. Call
     * getTerminologyId() on the description TEST: the description's terminology
     * id should match the terminology id returned by the service Clear the
     * terminology id Call getTerminologyId() on the relationship TEST: the
     * terminology id should be reassigned with the next incremented value
     */
    Description sctDescription =
        contentService.getDescription("165426013", "SNOMEDCT", "latest");
    id = handlerService.getTerminologyId(sctDescription);
    assertEquals(sctDescription.getTerminologyId(), id);
    sctDescription.setTerminologyId("");
    id = handlerService.getTerminologyId(sctDescription);
    assertEquals("10019", id);

    /*
     * Retrieve relationship 4285932028 (SNOMEDCT) from the content service.
     * Call getTerminologyId() on the relationship TEST: the relationship's
     * terminology id should match the terminology id returned by the service
     * Clear the terminology id Call getTerminologyId() on the relationship
     * TEST: the terminology id should be reassigned with the next incremented
     * value
     */
    Relationship sctRelationship =
        contentService.getRelationship("428056020", "SNOMEDCT", "latest");
    id = handlerService.getTerminologyId(sctRelationship);
    assertEquals(sctRelationship.getTerminologyId(), id);
    sctRelationship.setTerminologyId("");
    id = handlerService.getTerminologyId(sctRelationship);
    assertEquals("10019", id);

    /*
     * Retrieve language refset member 0ec8164a-47db-5f44-b3e4-50b33c495859
     * (SNOMEDCT) from the content service. Call getTerminologyId() on the
     * language refset member
     * 
     * TEST: language refset member's terminology id is a valid UUID
     */
    LanguageRefSetMember sctLrm =
        contentService.getLanguageRefSetMember(
            "0ec8164a-47db-5f44-b3e4-50b33c495859", "SNOMEDCT", "latest");
    id = handlerService.getTerminologyId(sctLrm);
    try {
      UUID.fromString(sctLrm.getTerminologyId());
    } catch (Exception e) {
      fail("Language refset member terminology id not in UUID format.");
    }

    /*
     * Retrieve concept attributeValue refset member
     * "0123aa2d-f249-54a5-9bed-0a3a4d5b7493" (SNOMEDCT) from the content
     * service. Call getTerminologyId() on the attribute value refset member
     * 
     * TEST: the attribute value refset member's terminology id is a valid UUID
     */
    AttributeValueConceptRefSetMember sctAvrm =
        (AttributeValueConceptRefSetMember) contentService
            .getAttributeValueRefSetMember(
                "0123aa2d-f249-54a5-9bed-0a3a4d5b7493", "SNOMEDCT", "latest");
    id = handlerService.getTerminologyId(sctAvrm);
    try {
      UUID.fromString(sctAvrm.getTerminologyId());
    } catch (Exception e) {
      fail("Attribute value concept refset member terminology id not in UUID format.");
    }

    /*
     * Retrieve description attributeValue refset member
     * "00fa0433-ec87-5ddc-a9c1-6fc12055c0e6" (SNOMEDCT) from the content
     * service. Call getTerminologyId() on the attribute value refset member
     * 
     * TEST: the attribute value refset member's terminology id is a valid UUID
     */
    AttributeValueDescriptionRefSetMember sctAvdrm =
        (AttributeValueDescriptionRefSetMember) contentService
            .getAttributeValueRefSetMember(
                "00fa0433-ec87-5ddc-a9c1-6fc12055c0e6", "SNOMEDCT", "latest");
    id = handlerService.getTerminologyId(sctAvdrm);
    try {
      UUID.fromString(sctAvdrm.getTerminologyId());
    } catch (Exception e) {
      fail("Attribute value description refset member terminology id not in UUID format.");
    }

    /*
     * Retrieve concept AssociationReference refset member
     * "00548d88-3544-55ce-9084-7b323ee327e7" (SNOMEDCT) from the content
     * service. Call getTerminologyId() on the association reference refset
     * member
     * 
     * TEST: the association reference refset member's terminology id is a valid
     * UUID
     */
    AssociationReferenceConceptRefSetMember sctArcrm =
        (AssociationReferenceConceptRefSetMember) contentService
            .getAssociationReferenceRefSetMember(
                "00548d88-3544-55ce-9084-7b323ee327e7", "SNOMEDCT", "latest");
    id = handlerService.getTerminologyId(sctArcrm);
    try {
      UUID.fromString(sctArcrm.getTerminologyId());
    } catch (Exception e) {
      fail("Association reference concept refset member terminology id not in UUID format.");
    }

    /*
     * Retrieve description AssociationReference refset member
     * "0123aa2d-f249-54a5-9bed-0a3a4d5b7493" (SNOMEDCT) from the content
     * service. Call getTerminologyId() on the association reference refset
     * member
     * 
     * TEST: the association reference refset member's terminology id is a valid
     * UUID
     */
    /** Commented out because currently no data for this condition. */
    /*
     * AssociationReferenceDescriptionRefSetMember sctArdrm =
     * (AssociationReferenceDescriptionRefSetMember)
     * contentService.getAssociationReferenceRefSetMember
     * ("0123aa2d-f249-54a5-9bed-0a3a4d5b7493", "SNOMEDCT", "latest"); id =
     * handlerService.getTerminologyId(sctArdrm); try {
     * UUID.fromString(sctArdrm.getTerminologyId()); } catch (Exception e) {
     * fail(
     * "Association reference description refset member terminology id not in UUID format."
     * ); }
     */

    /*
     * Retrieve a complex map refset member
     * "abcac540-87be-5405-9048-f14333f3abc2" (SNOMEDCT) from the content
     * service. Call getTerminologyId() on the complex map refset member
     * 
     * TEST: the complex map refset member's terminology id is a valid UUID
     */
    ComplexMapRefSetMember complexMapRefSetMember =
        contentService.getComplexMapRefSetMember(
            "abcac540-87be-5405-9048-f14333f3abc2", "SNOMEDCT", "latest");
    id = handlerService.getTerminologyId(complexMapRefSetMember);
    try {
      UUID.fromString(complexMapRefSetMember.getTerminologyId());
    } catch (Exception e) {
      fail("Complex map refset member terminology id not in UUID format.");
    }

    /*
     * Retrieve a description type refset member
     * "807f775b-1d66-5069-b58e-a37ace985dcf" (SNOMEDCT) from the content
     * service. Call getTerminologyId() on the description type refset member
     * 
     * TEST: the description type refset member's terminology id is a valid UUID
     */
    DescriptionTypeRefSetMember descriptionTypeRefSetMember =
        contentService.getDescriptionTypeRefSetMember(
            "807f775b-1d66-5069-b58e-a37ace985dcf", "SNOMEDCT", "latest");
    id = handlerService.getTerminologyId(descriptionTypeRefSetMember);
    try {
      UUID.fromString(descriptionTypeRefSetMember.getTerminologyId());
    } catch (Exception e) {
      fail("Description type refset member terminology id not in UUID format.");
    }

    /*
     * Retrieve a module dependency refset member
     * "f6431457-161b-5b46-9217-573c20c00070" (SNOMEDCT) from the content
     * service. Call getTerminologyId() on the module dependency refset member
     * 
     * TEST: the module dependency refset member's terminology id is a valid
     * UUID
     */
    ModuleDependencyRefSetMember moduleDependencyRefSetMember =
        contentService.getModuleDependencyRefSetMember(
            "f6431457-161b-5b46-9217-573c20c00070", "SNOMEDCT", "latest");
    id = handlerService.getTerminologyId(moduleDependencyRefSetMember);
    try {
      UUID.fromString(moduleDependencyRefSetMember.getTerminologyId());
    } catch (Exception e) {
      fail("Module dependency refset member terminology id not in UUID format.");
    }

    /*
     * Retrieve a refset descriptor refset member
     * "a6417d6f-5a66-5039-8e94-698d28a4da46" (SNOMEDCT) from the content
     * service. Call getTerminologyId() on the refset descriptor refset member
     * 
     * TEST: the refset descriptor refset member's terminology id is a valid
     * UUID
     */
    RefsetDescriptorRefSetMember refsetDescriptorRefSetMember =
        contentService.getRefsetDescriptorRefSetMember(
            "a6417d6f-5a66-5039-8e94-698d28a4da46", "SNOMEDCT", "latest");
    id = handlerService.getTerminologyId(refsetDescriptorRefSetMember);
    try {
      UUID.fromString(refsetDescriptorRefSetMember.getTerminologyId());
    } catch (Exception e) {
      fail("Refset descriptor refset member terminology id not in UUID format.");
    }

    /*
     * Retrieve a simple map refset member
     * "0001406e-99ce-5ebe-b84e-99691c094948" (SNOMEDCT) from the content
     * service. Call getTerminologyId() on the simple map refset member
     * 
     * TEST: the simple map refset member's terminology id is a valid UUID
     */
    SimpleMapRefSetMember simpleMapRefSetMember =
        contentService.getSimpleMapRefSetMember(
            "0001406e-99ce-5ebe-b84e-99691c094948", "SNOMEDCT", "latest");
    id = handlerService.getTerminologyId(simpleMapRefSetMember);
    try {
      UUID.fromString(simpleMapRefSetMember.getTerminologyId());
    } catch (Exception e) {
      fail("Simple map refset member terminology id not in UUID format.");
    }

    /*
     * Retrieve a simple refset member "06b90ec2-0c9b-5b00-8a64-35a3494cefb4"
     * (SNOMEDCT) from the content service. Call getTerminologyId() on the
     * simple refset member
     * 
     * TEST: the simple refset member's terminology id is a valid UUID
     */
    SimpleRefSetMember simpleRefSetMember =
        contentService.getSimpleRefSetMember(
            "06b90ec2-0c9b-5b00-8a64-35a3494cefb4", "SNOMEDCT", "latest");
    id = handlerService.getTerminologyId(simpleRefSetMember);
    try {
      UUID.fromString(simpleRefSetMember.getTerminologyId());
    } catch (Exception e) {
      fail("Simple refset member terminology id not in UUID format.");
    }
  }

  /*
   * Test degenerate use of the handler object.
   * 
   * @throws Exception the exception
   */
  /**
   * Test handler degenerate use001.
   *
   * @throws Exception the exception
   */
  @Test
  public void testHandlerDegenerateUse008() throws Exception {
    // Call getTerminologyId((Concept)null)
    // TEST: exception
    try {
      handlerService.getTerminologyId((Concept) null);
      fail("Calling resolve((Concept)null) should have thrown an exception.");
    } catch (Exception e) {
      // do nothing
    }

    // Call getTerminologyId((Description)null)
    // TEST: exception
    try {
      handlerService.getTerminologyId((Description) null);
      fail("Calling getTerminologyId((Description)null) should have thrown an exception.");
    } catch (Exception e) {
      // do nothing
    }

    // Call getTerminologyId((Relationship)null)
    // TEST: exception
    try {
      handlerService.getTerminologyId((Relationship) null);
      fail("Calling getTerminologyId((Relationship)null) should have thrown an exception.");
    } catch (Exception e) {
      // do nothing
    }

    // Call getTerminologyId((LanguageRefSetMember)null)
    // TEST: exception
    try {
      handlerService.getTerminologyId((LanguageRefSetMember) null);
      fail("Calling getTerminologyId((LanguageRefSetMember)null) should have thrown an exception.");
    } catch (Exception e) {
      // do nothing
    }

    // Call getTerminologyId((AttributeValueConceptRefSetMember)null)
    // TEST: exception
    try {
      handlerService.getTerminologyId((AttributeValueConceptRefSetMember) null);
      fail("Calling getTerminologyId((AttributeValueConceptRefSetMember)null) should have thrown an exception.");
    } catch (Exception e) {
      // do nothing
    }

    // Call getTerminologyId((AttributeValueConceptRefSetMember)null)
    // TEST: exception
    try {
      handlerService
          .getTerminologyId((AttributeValueDescriptionRefSetMember) null);
      fail("Calling getTerminologyId((AttributeValueDescriptionRefSetMember)null) should have thrown an exception.");
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
  public void testHandlerEdgeCases008() throws Exception {
    // Call computePreferredName(new ConceptJpa())
    // TEST: no exceptions
    handlerService.getTerminologyId(new ConceptJpa());

    handlerService.getTerminologyId(new DescriptionJpa());

    handlerService.getTerminologyId(new RelationshipJpa());

    handlerService.getTerminologyId(new LanguageRefSetMemberJpa());

    handlerService.getTerminologyId(new AttributeValueConceptRefSetMemberJpa());

    handlerService
        .getTerminologyId(new AttributeValueDescriptionRefSetMemberJpa());

    handlerService
        .getTerminologyId(new AssociationReferenceConceptRefSetMemberJpa());

    handlerService
        .getTerminologyId(new AssociationReferenceDescriptionRefSetMemberJpa());

    handlerService.getTerminologyId(new ComplexMapRefSetMemberJpa());

    handlerService.getTerminologyId(new SimpleMapRefSetMemberJpa());

    handlerService.getTerminologyId(new SimpleRefSetMemberJpa());

    handlerService.getTerminologyId(new DescriptionTypeRefSetMemberJpa());

    handlerService.getTerminologyId(new RefsetDescriptorRefSetMemberJpa());

    handlerService.getTerminologyId(new ModuleDependencyRefSetMemberJpa());
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
