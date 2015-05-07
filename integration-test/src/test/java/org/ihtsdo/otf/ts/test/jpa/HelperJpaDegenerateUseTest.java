/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.test.jpa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Properties;

import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.jpa.algo.FileSorter;
import org.ihtsdo.otf.ts.jpa.services.helper.TerminologyUtility;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
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
   * Test degenerate use of the {@link FileSorter}.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @Test
  public void testDegenerateUseHandlerJpa003() throws Exception {
    File outFile = File.createTempFile("abc", "txt");
    try {
      // null input file
      FileSorter.sortFile(null, outFile.toString(),
          String.CASE_INSENSITIVE_ORDER);
      Assert.fail("Expected exception did not occur");
    } catch (Exception e) {
      // n/a
    }

    try {
      // nonexisten input file
      FileSorter.sortFile("abc", outFile.toString(),
          String.CASE_INSENSITIVE_ORDER);
      Assert.fail("Expected exception did not occur");
    } catch (Exception e) {
      // n/a
    }

    try {
      // null output file
      FileSorter.sortFile(
          "../config/dev-windows/src/main/resources/config.properties", null,
          String.CASE_INSENSITIVE_ORDER);
      Assert.fail("Expected exception did not occur");
    } catch (Exception e) {
      // n/a
    }

    try {
      // null comparator
      FileSorter.sortFile(
          "../config/dev-windows/src/main/resources/config.properties",
          outFile.toString(), null);
      Assert.fail("Expected exception did not occur");
    } catch (Exception e) {
      // n/a
    }

    try {
      // null file
      FileSorter.checkSortedFile(null, String.CASE_INSENSITIVE_ORDER);
      Assert.fail("Expected exception did not occur");
    } catch (Exception e) {
      // n/a
    }

    try {
      // nonexistent file
      FileSorter
          .checkSortedFile(new File("abc"), String.CASE_INSENSITIVE_ORDER);
      Assert.fail("Expected exception did not occur");
    } catch (Exception e) {
      // n/a
    }

    try {
      // null comparator
      FileSorter.checkSortedFile(new File(
          "../config/dev-windows/src/main/resources/config.properties"), null);
      Assert.fail("Expected exception did not occur");
    } catch (Exception e) {
      // n/a
    }

    try {
      // null file
      FileSorter.deleteSortedFiles(null);
      Assert.fail("Expected exception did not occur");
    } catch (Exception e) {
      // n/a
    }

    try {
      // nonexistent file
      FileSorter.deleteSortedFiles(new File("abc"));
      Assert.fail("Expected exception did not occur");
    } catch (Exception e) {
      // n/a
    }

  }

  /**
   * Test degenerate use of the RF2FileSorter.
   *
   * @throws Exception the exception
   */
  @Test
  public void testDegenerateUseHandlerJpa004() throws Exception {
    // n/a - tested by loaders
  }

  /**
   * Test degenerate use of the {@link ConfigUtility}.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @Test
  public void testDegenerateUseHandlerJpa005() throws Exception {

    // NO degnerate use of getConfigProperties (unless file is bad).

    // Node
    try {
      ConfigUtility.getNodeForGraph("");
      Assert.fail("Expected exception did not occur");
    } catch (Exception e) {
      // n/a
    }

    try {
      ConfigUtility.getNodeForGraph(null);
      Assert.fail("Expected exception did not occur");
    } catch (Exception e) {
      // n/a
    }

    // String
    try {
      ConfigUtility.getStringForGraph("");
      Assert.fail("Expected exception did not occur");
    } catch (Exception e) {
      // n/a
    }

    try {
      ConfigUtility.getStringForGraph(null);
      Assert.fail("Expected exception did not occur");
    } catch (Exception e) {
      // n/a
    }

    // Input Stream
    try {
      ConfigUtility.getGraphForStream(new FileInputStream(new File(
          "../config/dev-windows/src/main/resources/config.properties")),
          ConceptJpa.class);
      Assert.fail("Expected exception did not occur");
    } catch (Exception e) {
      // n/a
    }

    try {
      ConfigUtility.getGraphForFile(new File(
          "../config/dev-windows/src/main/resources/config.properties"),
          ConceptJpa.class);
      Assert.fail("Expected exception did not occur");
    } catch (Exception e) {
      // n/a
    }

    // Remove null
    try {
      ConfigUtility.deleteDirectory(null);
      Assert.fail("Expected exception did not occur");
    } catch (Exception e) {
      // n/a
    }

    // merge with various null params
    try {
      ConfigUtility.mergeSortedFiles(null, new File("def"),
          String.CASE_INSENSITIVE_ORDER, new File("ghi"), "");
      Assert.fail("Expected exception did not occur");
    } catch (Exception e) {
      // n/a
    }

    try {
      ConfigUtility.mergeSortedFiles(new File("abc"), null,
          String.CASE_INSENSITIVE_ORDER, new File("ghi"), "");
      Assert.fail("Expected exception did not occur");
    } catch (Exception e) {
      // n/a
    }

    try {
      ConfigUtility.mergeSortedFiles(new File("abc"), new File("def"), null,
          new File("ghi"), "");
      Assert.fail("Expected exception did not occur");
    } catch (Exception e) {
      // n/a
    }

    try {
      ConfigUtility.mergeSortedFiles(new File("abc"), new File("def"),
          String.CASE_INSENSITIVE_ORDER, null, "");
      Assert.fail("Expected exception did not occur");
    } catch (Exception e) {
      // n/a
    }

    try {
      ConfigUtility.mergeSortedFiles(new File("abc"), new File("def"),
          String.CASE_INSENSITIVE_ORDER, new File("ghi"), null);
      Assert.fail("Expected exception did not occur");
    } catch (Exception e) {
      // n/a
    }

    // Send email with various null params
    try {
      ConfigUtility.sendEmail("", null, "def@ghi.com", "", new Properties(),
          true);
      Assert.fail("Expected exception did not occur");
    } catch (Exception e) {
      // n/a
    }

    try {
      ConfigUtility.sendEmail("", "abc@def.com", null, "", new Properties(),
          true);
      Assert.fail("Expected exception did not occur");
    } catch (Exception e) {
      // n/a
    }

    try {
      ConfigUtility.sendEmail("", "abc@def.com", "def@ghi.com", "", null, true);
      Assert.fail("Expected exception did not occur");
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
