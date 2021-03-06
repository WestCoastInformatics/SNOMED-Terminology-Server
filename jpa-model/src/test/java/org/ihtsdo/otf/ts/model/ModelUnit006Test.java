/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.model;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.CopyConstructorTester;
import org.ihtsdo.otf.ts.helpers.EqualsHashcodeTester;
import org.ihtsdo.otf.ts.helpers.GetterSetterTester;
import org.ihtsdo.otf.ts.helpers.SearchCriteria;
import org.ihtsdo.otf.ts.helpers.SearchCriteriaJpa;
import org.ihtsdo.otf.ts.helpers.XmlSerializationTester;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit testing for {@link SearchCriteriaJpa}.
 */
public class ModelUnit006Test {

  /** The model object to test. */
  private SearchCriteriaJpa object;

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
    object = new SearchCriteriaJpa();
  }

  /**
   * Test getter and setter methods of model object.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelGetSet006() throws Exception {
    Logger.getLogger(getClass()).debug("TEST testModelGetSet006");
    GetterSetterTester tester = new GetterSetterTester(object);
    tester.test();
  }

  /**
   * Test equals and hascode methods.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelEqualsHashcode006() throws Exception {
    Logger.getLogger(getClass()).debug("TEST testModelEqualsHashcode006");
    EqualsHashcodeTester tester = new EqualsHashcodeTester(object);
    tester.include("findActiveOnly");
    tester.include("findDefinedOnly");
    tester.include("findDescendants");
    tester.include("findByDestinationId");
    tester.include("findInactiveOnly");
    tester.include("findByModuleId");
    tester.include("findPrimitiveOnly");
    tester.include("findByRelationshipDescendants");
    tester.include("findByRelationshipTypeId");
    tester.include("findSelf");
    tester.include("findBySourceId");

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
  public void testModelCopy006() throws Exception {
    Logger.getLogger(getClass()).debug("TEST testModelCopy006");
    CopyConstructorTester tester = new CopyConstructorTester(object);
    assertTrue(tester.testCopyConstructor(SearchCriteria.class));
  }

  /**
   * Test XML serialization.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelXmlSerialization006() throws Exception {
    Logger.getLogger(getClass()).debug("TEST testModelXmlTransient006");
    XmlSerializationTester tester = new XmlSerializationTester(object);
    assertTrue(tester.testXmlSerialization());
  }

  /**
   * Test not null fields.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelNotNullField006() throws Exception {
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
