/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.jpa.services.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceDescriptionRefSetMember;
import org.ihtsdo.otf.ts.rf2.AttributeValueConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.AttributeValueDescriptionRefSetMember;
import org.ihtsdo.otf.ts.rf2.ComplexMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
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
import org.ihtsdo.otf.ts.rf2.jpa.LanguageRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RelationshipJpa;
import org.ihtsdo.otf.ts.rf2.jpa.SimpleMapRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.SimpleRefSetMemberJpa;
import org.ihtsdo.otf.ts.services.helpers.ConceptReportHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit testing for {@link ConceptReportHelper}.
 */
public class HelperUnit007Test {

  /** The concept. */
  private Concept concept;

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
    concept = new ConceptJpa();
  }

  /**
   * /** Test normal use of the helper object.
   *
   * @throws Exception the exception
   */
  @Test
  public void testHelperNormalUse007() throws Exception {
    Logger.getLogger(getClass()).info("TEST testHelperNormalUse007");

    // create concept and test if concept report has correct elements
    concept.setId(1L);
    concept.setTerminologyId("1");
    concept.setDefaultPreferredName("1");

    Logger.getLogger(getClass()).info(
        ConceptReportHelper.getConceptReport(concept));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "Concept = 1"));
    assertFalse(ConceptReportHelper.getConceptReport(concept).contains(
        "Description"));
    assertFalse(ConceptReportHelper.getConceptReport(concept).contains(
        "Relationship"));
    assertFalse(ConceptReportHelper.getConceptReport(concept).contains(
        "AttributeValue"));
    assertFalse(ConceptReportHelper.getConceptReport(concept).contains(
        "AssociationReference"));
    assertFalse(ConceptReportHelper.getConceptReport(concept).contains(
        "ComplexMap"));
    assertFalse(ConceptReportHelper.getConceptReport(concept).contains(
        "SimpleMap"));
    assertFalse(ConceptReportHelper.getConceptReport(concept)
        .contains("Simple"));

    Description d = new DescriptionJpa();
    d.setId(1L);
    d.setTerminologyId("1");
    d.setTerm("1");
    d.setTypeId("1");
    d.setConcept(concept);
    concept.addDescription(d);

    Logger.getLogger(getClass()).info(
        ConceptReportHelper.getConceptReport(concept));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "Concept = 1"));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "Description = 1"));
    assertFalse(ConceptReportHelper.getConceptReport(concept).contains(
        "Relationship"));
    assertFalse(ConceptReportHelper.getConceptReport(concept).contains(
        "AttributeValue"));
    assertFalse(ConceptReportHelper.getConceptReport(concept).contains(
        "AssociationReference"));
    assertFalse(ConceptReportHelper.getConceptReport(concept).contains(
        "ComplexMap"));
    assertFalse(ConceptReportHelper.getConceptReport(concept).contains(
        "SimpleMap"));
    assertFalse(ConceptReportHelper.getConceptReport(concept)
        .contains("Simple"));

    Relationship r = new RelationshipJpa();
    r.setId(1L);
    r.setTerminologyId("1");
    r.setTypeId("1");
    r.setSourceConcept(concept);
    r.setDestinationConcept(concept);
    concept.addRelationship(r);
    Logger.getLogger(getClass()).info(
        ConceptReportHelper.getConceptReport(concept));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "Concept = 1"));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "Description = 1"));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "Relationship = 1"));
    assertFalse(ConceptReportHelper.getConceptReport(concept).contains(
        "AttributeValue"));
    assertFalse(ConceptReportHelper.getConceptReport(concept).contains(
        "AssociationReference"));
    assertFalse(ConceptReportHelper.getConceptReport(concept).contains(
        "ComplexMap"));
    assertFalse(ConceptReportHelper.getConceptReport(concept).contains(
        "SimpleMap"));
    assertFalse(ConceptReportHelper.getConceptReport(concept)
        .contains("Simple"));

    AttributeValueConceptRefSetMember avmember =
        new AttributeValueConceptRefSetMemberJpa();
    avmember.setId(1L);
    avmember.setTerminologyId("1");
    avmember.setConcept(concept);
    concept.addAttributeValueRefSetMember(avmember);
    Logger.getLogger(getClass()).info(
        ConceptReportHelper.getConceptReport(concept));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "Concept = 1"));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "Description = 1"));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "Relationship = 1"));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "AttributeValue = 1"));
    assertFalse(ConceptReportHelper.getConceptReport(concept).contains(
        "AssociationReference"));
    assertFalse(ConceptReportHelper.getConceptReport(concept).contains(
        "ComplexMap"));
    assertFalse(ConceptReportHelper.getConceptReport(concept).contains(
        "SimpleMap"));
    assertFalse(ConceptReportHelper.getConceptReport(concept)
        .contains("Simple"));

    AssociationReferenceConceptRefSetMember asmember =
        new AssociationReferenceConceptRefSetMemberJpa();
    asmember.setId(1L);
    asmember.setTerminologyId("1");
    asmember.setConcept(concept);
    concept.addAssociationReferenceRefSetMember(asmember);
    Logger.getLogger(getClass()).info(
        ConceptReportHelper.getConceptReport(concept));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "Concept = 1"));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "Description = 1"));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "Relationship = 1"));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "AttributeValue = 1"));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "AssociationReference = 1"));
    assertFalse(ConceptReportHelper.getConceptReport(concept).contains(
        "ComplexMap"));
    assertFalse(ConceptReportHelper.getConceptReport(concept).contains(
        "SimpleMap"));
    assertFalse(ConceptReportHelper.getConceptReport(concept)
        .contains("Simple"));

    ComplexMapRefSetMember cmmember = new ComplexMapRefSetMemberJpa();
    cmmember.setId(1L);
    cmmember.setTerminologyId("1");
    cmmember.setConcept(concept);
    concept.addComplexMapRefSetMember(cmmember);
    Logger.getLogger(getClass()).info(
        ConceptReportHelper.getConceptReport(concept));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "Concept = 1"));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "Description = 1"));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "Relationship = 1"));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "AttributeValue = 1"));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "AssociationReference = 1"));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "ComplexMap = 1"));
    assertFalse(ConceptReportHelper.getConceptReport(concept).contains(
        "SimpleMap"));
    assertFalse(ConceptReportHelper.getConceptReport(concept)
        .contains("Simple"));

    SimpleRefSetMember smember = new SimpleRefSetMemberJpa();
    smember.setId(1L);
    smember.setTerminologyId("1");
    smember.setConcept(concept);
    concept.addSimpleRefSetMember(smember);
    Logger.getLogger(getClass()).info(
        ConceptReportHelper.getConceptReport(concept));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "Concept = 1"));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "Description = 1"));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "Relationship = 1"));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "AttributeValue = 1"));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "AssociationReference = 1"));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "ComplexMap = 1"));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "Simple = 1"));
    assertFalse(ConceptReportHelper.getConceptReport(concept).contains(
        "SimpleMap"));

    SimpleMapRefSetMember smmember = new SimpleMapRefSetMemberJpa();
    smmember.setId(1L);
    smmember.setTerminologyId("1");
    smmember.setConcept(concept);
    concept.addSimpleMapRefSetMember(smmember);
    Logger.getLogger(getClass()).info(
        ConceptReportHelper.getConceptReport(concept));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "Concept = 1"));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "Description = 1"));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "Relationship = 1"));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "AttributeValue = 1"));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "AssociationReference = 1"));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "ComplexMap = 1"));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "SimpleMap = 1"));
    assertTrue(ConceptReportHelper.getConceptReport(concept).contains(
        "Simple = 1"));

    // modify description and see if description report has correct elements
    Logger.getLogger(getClass()).info(
        ConceptReportHelper.getDescriptionReport(d));
    assertTrue(ConceptReportHelper.getDescriptionReport(d).contains(
        "Description = 1"));
    assertFalse(ConceptReportHelper.getDescriptionReport(d)
        .contains("Language"));
    assertFalse(ConceptReportHelper.getDescriptionReport(d).contains(
        "AttributeValue"));
    assertFalse(ConceptReportHelper.getDescriptionReport(d).contains(
        "AssociationReference"));

    LanguageRefSetMember lrmember = new LanguageRefSetMemberJpa();
    lrmember.setId(1L);
    lrmember.setTerminologyId("1");
    d.addLanguageRefSetMember(lrmember);
    Logger.getLogger(getClass()).info(
        ConceptReportHelper.getDescriptionReport(d));
    assertTrue(ConceptReportHelper.getDescriptionReport(d).contains(
        "Description = 1"));
    assertTrue(ConceptReportHelper.getDescriptionReport(d).contains(
        "Language = 1"));
    assertFalse(ConceptReportHelper.getDescriptionReport(d).contains(
        "AttributeValue"));
    assertFalse(ConceptReportHelper.getDescriptionReport(d).contains(
        "AssociationReference"));

    AttributeValueDescriptionRefSetMember avdmember =
        new AttributeValueDescriptionRefSetMemberJpa();
    avdmember.setId(1L);
    avdmember.setTerminologyId("1");
    avdmember.setDescription(d);
    d.addAttributeValueRefSetMember(avdmember);
    Logger.getLogger(getClass()).info(
        ConceptReportHelper.getDescriptionReport(d));
    assertTrue(ConceptReportHelper.getDescriptionReport(d).contains(
        "Description = 1"));
    assertTrue(ConceptReportHelper.getDescriptionReport(d).contains(
        "Language = 1"));
    assertTrue(ConceptReportHelper.getDescriptionReport(d).contains(
        "AttributeValue = 1"));
    assertFalse(ConceptReportHelper.getDescriptionReport(d).contains(
        "AssociationReference"));

    AssociationReferenceDescriptionRefSetMember asdmember =
        new AssociationReferenceDescriptionRefSetMemberJpa();
    asdmember.setId(1L);
    asdmember.setTerminologyId("1");
    asdmember.setDescription(d);
    d.addAssociationReferenceRefSetMember(asdmember);
    Logger.getLogger(getClass()).info(
        ConceptReportHelper.getDescriptionReport(d));
    assertTrue(ConceptReportHelper.getDescriptionReport(d).contains(
        "Description = 1"));
    assertTrue(ConceptReportHelper.getDescriptionReport(d).contains(
        "Language = 1"));
    assertTrue(ConceptReportHelper.getDescriptionReport(d).contains(
        "AttributeValue = 1"));
    assertTrue(ConceptReportHelper.getDescriptionReport(d).contains(
        "AssociationReference = 1"));
  }

  /*
   * Test degenerate use of the helper object.
   * 
   * @throws Exception the exception
   */
  /**
   * Test helper degenerate use007.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @Test
  public void testHelperDegenerateUse007() throws Exception {
    // provide a null concept
    try {
      ConceptReportHelper.getConceptReport(null);
      fail("Attempt to getConceptReport() on null concept should throw an error.");
    } catch (Exception e) {
      // do nothing
    }

    try {
      ConceptReportHelper.getDescriptionReport(null);
      fail("Attempt to getDescriptionReport() on null description should throw an error.");
    } catch (Exception e) {
      // do nothing
    }
  }

  /**
   * Test edge cases of the helper object.
   *
   * @throws Exception the exception
   */
  @Test
  public void testHelperEdgeCases007() throws Exception {
    // n/a
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
