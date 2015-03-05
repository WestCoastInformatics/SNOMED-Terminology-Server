package org.ihtsdo.otf.ts.model;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.CopyConstructorTester;
import org.ihtsdo.otf.ts.helpers.EqualsHashcodeTester;
import org.ihtsdo.otf.ts.helpers.GetterSetterTester;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa;
import org.ihtsdo.otf.ts.rf2.jpa.LanguageRefSetMemberJpa;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit testing for {@link LanguageRefSetMemberJpa}.
 */
public class ModelUnit009Test {

  /** The model object to test. */
  private LanguageRefSetMemberJpa object;

  /** The test fixture d1. */
  private Description d1;

  /** The test fixture d2. */
  private Description d2;

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
    object = new LanguageRefSetMemberJpa();
    // Set up some objects
    d1 = new DescriptionJpa();
    d1.setId(1L);
    d1.setTypeId("1");
    d2 = new DescriptionJpa();
    d1.setId(2L);
    d1.setTypeId("2");

  }

  /**
   * Test getter and setter methods of model object.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelGetSet001() throws Exception {
    Logger.getLogger(getClass()).info("TEST testModelGetSet001");
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
  public void testModelEqualsHashcode001() throws Exception {
    Logger.getLogger(getClass()).info("TEST testModelEqualsHashcode001");
    EqualsHashcodeTester tester = new EqualsHashcodeTester(object);
    tester.include("active");
    tester.include("moduleId");
    tester.include("terminology");
    tester.include("terminologyId");
    tester.include("terminologyVersion");
    tester.include("refSetId");
    tester.include("description");
    // needed for generic refset class
    tester.include("component");
    tester.include("acceptabilityId");

    // Set up some objects
    tester.proxy(Description.class, 1, d1);
    tester.proxy(Description.class, 2, d2);

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
  public void testModelCopy001() throws Exception {
    Logger.getLogger(getClass()).info("TEST testModelCopy001");
    CopyConstructorTester tester = new CopyConstructorTester(object);

    // Set up some objects
    tester.proxy(Description.class, 1, d1);
    tester.proxy(Description.class, 2, d2);

    assertTrue(tester.testCopyConstructor(LanguageRefSetMember.class));
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
