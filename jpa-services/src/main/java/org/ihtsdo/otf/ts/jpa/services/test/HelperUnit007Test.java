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
 * Unit testing for {@link Concept}.
 */
public class HelperUnit007Test {

  /** The helper object to test. */
  private ConceptReportHelper object;

  private Concept c;
  
  private Description d;

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
    object = new ConceptReportHelper();
    c = new ConceptJpa();
  }



  /**	/**
	 * Test normal use of the helper object.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testHelperNormalUse007() throws Exception {
		Logger.getLogger(getClass()).info("TEST testHelperNormalUse007");

		// create concept and test if concept report has correct elements
		c.setId(1L);
		c.setTerminologyId("1");
		c.setDefaultPreferredName("1");

		Logger.getLogger(getClass()).info(object.getConceptReport(c));
		assertTrue(object.getConceptReport(c).contains("Concept = 1"));
		assertFalse(object.getConceptReport(c).contains("Description"));
		assertFalse(object.getConceptReport(c).contains("Relationship"));
		assertFalse(object.getConceptReport(c).contains("AttributeValue"));
		assertFalse(object.getConceptReport(c).contains("AssociationReference"));
		assertFalse(object.getConceptReport(c).contains("ComplexMap"));
		assertFalse(object.getConceptReport(c).contains("SimpleMap"));
		assertFalse(object.getConceptReport(c).contains("Simple"));

		Description d = new DescriptionJpa();
		d.setId(1L);
		d.setTerminologyId("1");
		d.setTerm("1");
		d.setTypeId("1");
		d.setConcept(c);
		c.addDescription(d);

		Logger.getLogger(getClass()).info(object.getConceptReport(c));
		assertTrue(object.getConceptReport(c).contains("Concept = 1"));
		assertTrue(object.getConceptReport(c).contains("Description = 1"));
		assertFalse(object.getConceptReport(c).contains("Relationship"));
		assertFalse(object.getConceptReport(c).contains("AttributeValue"));
		assertFalse(object.getConceptReport(c).contains("AssociationReference"));
		assertFalse(object.getConceptReport(c).contains("ComplexMap"));
		assertFalse(object.getConceptReport(c).contains("SimpleMap"));
		assertFalse(object.getConceptReport(c).contains("Simple"));

		Relationship r = new RelationshipJpa();
		r.setId(1L);
		r.setTerminologyId("1");
		r.setTypeId("1");
		r.setSourceConcept(c);
		r.setDestinationConcept(c);
		c.addRelationship(r);
		Logger.getLogger(getClass()).info(object.getConceptReport(c));
		assertTrue(object.getConceptReport(c).contains("Concept = 1"));
		assertTrue(object.getConceptReport(c).contains("Description = 1"));
		assertTrue(object.getConceptReport(c).contains("Relationship = 1"));
		assertFalse(object.getConceptReport(c).contains("AttributeValue"));
		assertFalse(object.getConceptReport(c).contains("AssociationReference"));
		assertFalse(object.getConceptReport(c).contains("ComplexMap"));
		assertFalse(object.getConceptReport(c).contains("SimpleMap"));
		assertFalse(object.getConceptReport(c).contains("Simple"));

		AttributeValueConceptRefSetMember avmember = new AttributeValueConceptRefSetMemberJpa();
		avmember.setId(1L);
		avmember.setTerminologyId("1");
		avmember.setConcept(c);
		c.addAttributeValueRefSetMember(avmember);
		Logger.getLogger(getClass()).info(object.getConceptReport(c));
		assertTrue(object.getConceptReport(c).contains("Concept = 1"));
		assertTrue(object.getConceptReport(c).contains("Description = 1"));
		assertTrue(object.getConceptReport(c).contains("Relationship = 1"));
		assertTrue(object.getConceptReport(c).contains("AttributeValue = 1"));
		assertFalse(object.getConceptReport(c).contains("AssociationReference"));
		assertFalse(object.getConceptReport(c).contains("ComplexMap"));
		assertFalse(object.getConceptReport(c).contains("SimpleMap"));
		assertFalse(object.getConceptReport(c).contains("Simple"));

		AssociationReferenceConceptRefSetMember asmember = new AssociationReferenceConceptRefSetMemberJpa();
		asmember.setId(1L);
		asmember.setTerminologyId("1");
		asmember.setConcept(c);
		c.addAssociationReferenceRefSetMember(asmember);
		Logger.getLogger(getClass()).info(object.getConceptReport(c));
		assertTrue(object.getConceptReport(c).contains("Concept = 1"));
		assertTrue(object.getConceptReport(c).contains("Description = 1"));
		assertTrue(object.getConceptReport(c).contains("Relationship = 1"));
		assertTrue(object.getConceptReport(c).contains("AttributeValue = 1"));
		assertTrue(object.getConceptReport(c).contains(
				"AssociationReference = 1"));
		assertFalse(object.getConceptReport(c).contains("ComplexMap"));
		assertFalse(object.getConceptReport(c).contains("SimpleMap"));
		assertFalse(object.getConceptReport(c).contains("Simple"));

		ComplexMapRefSetMember cmmember = new ComplexMapRefSetMemberJpa();
		cmmember.setId(1L);
		cmmember.setTerminologyId("1");
		cmmember.setConcept(c);
		c.addComplexMapRefSetMember(cmmember);
		Logger.getLogger(getClass()).info(object.getConceptReport(c));
		assertTrue(object.getConceptReport(c).contains("Concept = 1"));
		assertTrue(object.getConceptReport(c).contains("Description = 1"));
		assertTrue(object.getConceptReport(c).contains("Relationship = 1"));
		assertTrue(object.getConceptReport(c).contains("AttributeValue = 1"));
		assertTrue(object.getConceptReport(c).contains(
				"AssociationReference = 1"));
		assertTrue(object.getConceptReport(c).contains("ComplexMap = 1"));
		assertFalse(object.getConceptReport(c).contains("SimpleMap"));
		assertFalse(object.getConceptReport(c).contains("Simple"));

		SimpleRefSetMember smember = new SimpleRefSetMemberJpa();
		smember.setId(1L);
		smember.setTerminologyId("1");
		smember.setConcept(c);
		c.addSimpleRefSetMember(smember);
		Logger.getLogger(getClass()).info(object.getConceptReport(c));
		assertTrue(object.getConceptReport(c).contains("Concept = 1"));
		assertTrue(object.getConceptReport(c).contains("Description = 1"));
		assertTrue(object.getConceptReport(c).contains("Relationship = 1"));
		assertTrue(object.getConceptReport(c).contains("AttributeValue = 1"));
		assertTrue(object.getConceptReport(c).contains(
				"AssociationReference = 1"));
		assertTrue(object.getConceptReport(c).contains("ComplexMap = 1"));
		assertTrue(object.getConceptReport(c).contains("Simple = 1"));
		assertFalse(object.getConceptReport(c).contains("SimpleMap"));

		SimpleMapRefSetMember smmember = new SimpleMapRefSetMemberJpa();
		smmember.setId(1L);
		smmember.setTerminologyId("1");
		smmember.setConcept(c);
		c.addSimpleMapRefSetMember(smmember);
		Logger.getLogger(getClass()).info(object.getConceptReport(c));
		assertTrue(object.getConceptReport(c).contains("Concept = 1"));
		assertTrue(object.getConceptReport(c).contains("Description = 1"));
		assertTrue(object.getConceptReport(c).contains("Relationship = 1"));
		assertTrue(object.getConceptReport(c).contains("AttributeValue = 1"));
		assertTrue(object.getConceptReport(c).contains(
				"AssociationReference = 1"));
		assertTrue(object.getConceptReport(c).contains("ComplexMap = 1"));
		assertTrue(object.getConceptReport(c).contains("SimpleMap = 1"));
		assertTrue(object.getConceptReport(c).contains("Simple = 1"));

		// modify description and see if description report has correct elements
		Logger.getLogger(getClass()).info(object.getDescriptionReport(d));
		assertTrue(object.getDescriptionReport(d).contains("Description = 1"));
		assertFalse(object.getDescriptionReport(d).contains("Language"));
		assertFalse(object.getDescriptionReport(d).contains("AttributeValue"));
		assertFalse(object.getDescriptionReport(d).contains(
				"AssociationReference"));

		LanguageRefSetMember lrmember = new LanguageRefSetMemberJpa();
		lrmember.setId(1L);
		lrmember.setTerminologyId("1");
		d.addLanguageRefSetMember(lrmember);
		Logger.getLogger(getClass()).info(object.getDescriptionReport(d));
		assertTrue(object.getDescriptionReport(d).contains("Description = 1"));
		assertTrue(object.getDescriptionReport(d).contains("Language = 1"));
		assertFalse(object.getDescriptionReport(d).contains("AttributeValue"));
		assertFalse(object.getDescriptionReport(d).contains(
				"AssociationReference"));

		AttributeValueDescriptionRefSetMember avdmember = new AttributeValueDescriptionRefSetMemberJpa();
		avdmember.setId(1L);
		avdmember.setTerminologyId("1");
		avdmember.setDescription(d);
		d.addAttributeValueRefSetMember(avdmember);
		Logger.getLogger(getClass()).info(object.getDescriptionReport(d));
		assertTrue(object.getDescriptionReport(d).contains("Description = 1"));
		assertTrue(object.getDescriptionReport(d).contains("Language = 1"));
		assertTrue(object.getDescriptionReport(d)
				.contains("AttributeValue = 1"));
		assertFalse(object.getDescriptionReport(d).contains(
				"AssociationReference"));
		
		AssociationReferenceDescriptionRefSetMember asdmember = new AssociationReferenceDescriptionRefSetMemberJpa();
		asdmember.setId(1L);
		asdmember.setTerminologyId("1");
		asdmember.setDescription(d);
		d.addAssociationReferenceRefSetMember(asdmember);
		Logger.getLogger(getClass()).info(object.getDescriptionReport(d));
		assertTrue(object.getDescriptionReport(d).contains("Description = 1"));
		assertTrue(object.getDescriptionReport(d).contains("Language = 1"));
		assertTrue(object.getDescriptionReport(d)
				.contains("AttributeValue = 1"));
		assertTrue(object.getDescriptionReport(d).contains(
				"AssociationReference = 1"));
	}
	
  /* Test degenerate use of the helper object.
   *
   * @throws Exception the exception
   */
  @Test
  public void testHelperDegenerateUse007() throws Exception {
    // provide a null concept
	  try {
		  object.getConceptReport(null);
		  fail("Attempt to getConceptReport() on null concept should throw an error.");
	  } catch (Exception e) {
		 // do nothing 
	  }
	  
	  try {
		  object.getDescriptionReport(null);
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
