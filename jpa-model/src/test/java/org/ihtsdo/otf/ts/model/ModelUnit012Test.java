package org.ihtsdo.otf.ts.model;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.CopyConstructorTester;
import org.ihtsdo.otf.ts.helpers.EqualsHashcodeTester;
import org.ihtsdo.otf.ts.helpers.GetterSetterTester;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.jpa.AssociationReferenceConceptRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit testing for {@link AssociationReferenceConceptRefSetMemberJpa}.
 */
public class ModelUnit012Test {

  /** The model object to test. */
  private AssociationReferenceConceptRefSetMemberJpa object;

  /** The test fixture c1. */
  private Concept c1;

  /** The test fixture c2. */
  private Concept c2;

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
    object = new AssociationReferenceConceptRefSetMemberJpa();
    // Set up some objects
    c1 = new ConceptJpa();
    c1.setId(1L);
    c1.setDefinitionStatusId("1");
    c2 = new ConceptJpa();
    c2.setId(2L);
    c2.setDefinitionStatusId("2");

  }

  /**
   * Test getter and setter methods of model object.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelGetSet012() throws Exception {
    Logger.getLogger(getClass()).debug("TEST testModelGetSet009");
    GetterSetterTester tester = new GetterSetterTester(object);
    tester.exclude("objectId");
    tester.test();
  }

  /**
   * Test equals and hascode methods.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelEqualsHashcode012() throws Exception {
    Logger.getLogger(getClass()).debug("TEST testModelEqualsHashcode012");
    EqualsHashcodeTester tester = new EqualsHashcodeTester(object);
    tester.include("active");
    tester.include("moduleId");
    tester.include("terminology");
    tester.include("terminologyId");
    tester.include("terminologyVersion");
    tester.include("refSetId");
    tester.include("concept");
    // needed for generic refset class
    tester.include("component");
    tester.include("targetComponentId");

    // Set up some objects
    tester.proxy(Concept.class, 1, c1);
    tester.proxy(Concept.class, 2, c2);

    assertTrue(tester.testIdentitiyFieldEquals());
    assertTrue(tester.testNonIdentitiyFieldEquals());
    assertTrue(tester.testIdentityFieldNotEquals());
    assertTrue(tester.testIdentitiyFieldHashcode());
    assertTrue(tester.testNonIdentitiyFieldHashcode());
    assertTrue(tester.testIdentityFieldDifferentHashcode());
  }

  /**
   * Test copy constructor.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelCopy012() throws Exception {
    Logger.getLogger(getClass()).debug("TEST testModelCopy009");
    CopyConstructorTester tester = new CopyConstructorTester(object);

    // Set up some objects
    tester.proxy(Concept.class, 1, c1);
    tester.proxy(Concept.class, 2, c2);

    assertTrue(tester
        .testCopyConstructor(AssociationReferenceConceptRefSetMember.class));
  }

  /**
   * Test concept reference in XML serialization.
   *
   * @throws Exception the exception
   */
  @Test
  public void testXmlTransient012() throws Exception {
    Logger.getLogger(getClass()).debug("TEST testXmlTransient012");
    Concept c = new ConceptJpa();
    c.setId(1L);
    c.setTerminologyId("1");
    // Definition status id is not persisted by the refset member
    // so it can't be reconstructed, but is part of the equals computation
    // c.setDefinitionStatusId("1");
    c.setDefaultPreferredName("1");
    AssociationReferenceConceptRefSetMember member =
        new AssociationReferenceConceptRefSetMemberJpa();
    member.setId(1L);
    member.setTerminologyId("1");
    member.setConcept(c);
    c.addAssociationReferenceRefSetMember(member);
    String xml = ConfigUtility.getStringForGraph(member);
    assertTrue(xml.contains("<conceptId>"));
    assertTrue(xml.contains("<conceptTerminologyId>"));
    assertTrue(xml.contains("<conceptPreferredName>"));
    AssociationReferenceConceptRefSetMember member2 =
        (AssociationReferenceConceptRefSetMember) ConfigUtility
            .getGraphForString(xml,
                AssociationReferenceConceptRefSetMemberJpa.class);
    assertTrue(member.equals(member2));
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
