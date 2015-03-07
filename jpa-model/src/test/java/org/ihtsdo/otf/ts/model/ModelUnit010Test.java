package org.ihtsdo.otf.ts.model;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.CopyConstructorTester;
import org.ihtsdo.otf.ts.helpers.EqualsHashcodeTester;
import org.ihtsdo.otf.ts.helpers.GetterSetterTester;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RelationshipJpa;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit testing for {@link RelationshipJpa}.
 */
public class ModelUnit010Test {

  /** The model object to test. */
  private RelationshipJpa object;

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
    object = new RelationshipJpa();
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
  public void testModelGetSet010() throws Exception {
    Logger.getLogger(getClass()).debug("TEST testModelGetSet010");
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
  public void testModelEqualsHashcode010() throws Exception {
    Logger.getLogger(getClass()).debug("TEST testModelEqualsHashcode010");
    EqualsHashcodeTester tester = new EqualsHashcodeTester(object);
    tester.include("active");
    tester.include("moduleId");
    tester.include("terminology");
    tester.include("terminologyId");
    tester.include("terminologyVersion");
    tester.include("characteristicTypeId");
    tester.include("destinationConcept");
    tester.include("modifierId");
    tester.include("relationshipGroup");
    tester.include("relationshipGroup");
    tester.include("sourceConcept");
    tester.include("typeId");

    // Set up objects
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
  public void testModelCopy010() throws Exception {
    Logger.getLogger(getClass()).debug("TEST testModelCopy010");
    CopyConstructorTester tester = new CopyConstructorTester(object);

    // Set up objects
    tester.proxy(Concept.class, 1, c1);
    tester.proxy(Concept.class, 2, c2);

    assertTrue(tester.testCopyConstructor(Relationship.class));
  }

  /**
   * Test concept reference in XML serialization.
   *
   * @throws Exception the exception
   */
  @Test
  public void testXmlTransient010() throws Exception {
    Logger.getLogger(getClass()).debug("TEST testXmlTransient010");
    Relationship r = new RelationshipJpa();
    r.setId(1L);
    r.setTerminologyId("1");

    Concept c = new ConceptJpa();
    c.setId(1L);
    c.setTerminologyId("1");
    c.setDefaultPreferredName("1");
    c.addRelationship(r);
    r.setSourceConcept(c);
    r.setDestinationConcept(c);

    String xml = ConfigUtility.getStringForGraph(r);
    assertTrue(xml.contains("<sourceId>"));
    assertTrue(xml.contains("<sourceTerminologyId>"));
    assertTrue(xml.contains("<sourcePreferredName>"));
    assertTrue(xml.contains("<destinationId>"));
    assertTrue(xml.contains("<destinationTerminologyId>"));
    assertTrue(xml.contains("<destinationPreferredName>"));
    Relationship r2 =
        (Relationship) ConfigUtility.getGraphForString(xml,
            RelationshipJpa.class);
    assertTrue(r.equals(r2));
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
