package org.ihtsdo.otf.ts.test.jpa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.ihtsdo.otf.ts.jpa.services.helper.TerminologyUtility;
import org.ihtsdo.otf.ts.services.helpers.PushBackReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Implementation of the "Helper Jpa Degenerate Use" Test Cases.
 */
public class HelperJpaDegenerateUseTest {

  /** The fixture. */
  private PushBackReader reader;

  /**
   * Create test fixtures for class.
   *
   * @throws Exception the exception
   */
  @BeforeClass
  public static void setupClass() throws Exception {
    // do nothing
  }

  /**
   * Create test fixtures per test.
   *
   * @throws Exception the exception
   */
  @Before
  public void setup() throws Exception {
    reader =
        new PushBackReader(
            new BufferedReader(
                new FileReader(
                    new File(
                        "../config/src/main/resources/data/snomedct-20150131-delta/Terminology/sct2_Concept_Delta_INT_20150131.txt"))));

  }

  /**
   * Test degenerate use of the {@link PushBackReader}. .
   * @throws Exception the exception
   */
  @Test
  public void testDegenerateUseHandlerJpa001() throws Exception {

    reader.push("abc");
    try {
      reader.push("abc");
      Assert.fail("Expected exception did not occur");
    } catch (Exception e) {
      // n/a
    }
  }

  /**
   * Test degenerate use of the {@link TerminologyUtility}.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @Test
  public void testDegenerateUseHandlerJpa002() throws Exception {
    try {
      TerminologyUtility.getInferredType("FAKE", "FAKE");
      Assert.fail("Expected exception did not occur.");
    } catch (Exception e) {
      // n/a
    }

    try {
      TerminologyUtility.getInferredType(null, null);
      Assert.fail("Expected exception did not occur.");
    } catch (NullPointerException npe) {
      Assert.fail("Null pointer exception should not occur.");
    } catch (Exception e) {
      // n/a
    }

    try {
      TerminologyUtility.getStatedType("FAKE", "FAKE");
      Assert.fail("Expected exception did not occur.");
    } catch (Exception e) {
      // n/a
    }

    try {
      TerminologyUtility.getStatedType(null, null);
      Assert.fail("Expected exception did not occur.");
    } catch (NullPointerException npe) {
      Assert.fail("Null pointer exception should not occur.");
    } catch (Exception e) {
      // n/a
    }

    try {
      TerminologyUtility.getActiveInferredRelationships(null);
      Assert.fail("Expected exception did not occur.");
    } catch (NullPointerException npe) {
      Assert.fail("Null pointer exception should not occur.");
    } catch (Exception e) {
      // n/a
    }

    try {
      TerminologyUtility.getActiveParentConcepts(null);
      Assert.fail("Expected exception did not occur.");
    } catch (NullPointerException npe) {
      Assert.fail("Null pointer exception should not occur.");
    } catch (Exception e) {
      // n/a
    }

    try {
      TerminologyUtility.getActiveStatedRelationships(null);
      Assert.fail("Expected exception did not occur.");
    } catch (NullPointerException npe) {
      Assert.fail("Null pointer exception should not occur.");
    } catch (Exception e) {
      // n/a
    }

    try {
      TerminologyUtility.getHierarchicalIsaRels(null, null);
      Assert.fail("Expected exception did not occur.");
    } catch (NullPointerException npe) {
      Assert.fail("Null pointer exception should not occur.");
    } catch (Exception e) {
      // n/a
    }

    try {
      TerminologyUtility.getHierarchicalIsaRels(null, "");
      Assert.fail("Expected exception did not occur.");
    } catch (NullPointerException npe) {
      Assert.fail("Null pointer exception should not occur.");
    } catch (Exception e) {
      // n/a
    }

    try {
      TerminologyUtility.getHierarchicalIsaRels("", null);
      Assert.fail("Expected exception did not occur.");
    } catch (NullPointerException npe) {
      Assert.fail("Null pointer exception should not occur.");
    } catch (Exception e) {
      // n/a
    }

  }

  /**
   * Teardown.
   *
   * @throws Exception the exception
   */
  @After
  public void teardown() throws Exception {
    // close test fixtures per class
    reader.close();
  }

  /**
   * Teardown class.
   *
   * @throws Exception the exception
   */
  @AfterClass
  public static void teardownClass() throws Exception {
    // do nothing
  }

}
