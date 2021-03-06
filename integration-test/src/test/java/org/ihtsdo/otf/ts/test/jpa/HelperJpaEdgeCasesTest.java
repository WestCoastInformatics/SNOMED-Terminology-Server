/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.test.jpa;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.jpa.algo.FileSorter;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.helper.TerminologyUtility;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.services.ContentService;
import org.ihtsdo.otf.ts.services.helpers.PushBackReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Node;

/**
 * Implementation of the "Helper Jpa Normal Use" Test Cases.
 */
public class HelperJpaEdgeCasesTest {

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
   * Test edge cases of the {@link PushBackReader}. .
   * @throws Exception the exception
   */
  @Test
  public void testEdgeCasesHandlerJpa001() throws Exception {
    String line = reader.readLine();
    Assert.assertNull(reader.peek());

    reader.push(null);
    line = reader.readLine();
    Assert.assertNotNull(line);

  }

  /**
   * Test edge cases of the {@link TerminologyUtility}.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @Test
  public void testEdgeCasesHandlerJpa002() throws Exception {

    // Get a SNOMED concept and an ICD9CM concept
    ContentService contentService = new ContentServiceJpa();
    Concept sctConcept =
        contentService.getSingleConcept("138875005", "SNOMEDCT", "latest");
    Concept icdConcept =
        contentService.getSingleConcept("001-999.99", "ICD9CM", "2013");

    // tree-tops don't have parent, stated, or inferred rels.
    Assert.assertEquals(0,
        TerminologyUtility.getActiveInferredRelationships(sctConcept).size());
    Assert.assertEquals(0,
        TerminologyUtility.getActiveInferredRelationships(icdConcept).size());
    Assert.assertEquals(0,
        TerminologyUtility.getActiveParentConcepts(sctConcept).size());
    Assert.assertEquals(0,
        TerminologyUtility.getActiveParentConcepts(icdConcept).size());
    Assert.assertEquals(0,
        TerminologyUtility.getActiveStatedRelationships(sctConcept).size());
    Assert.assertEquals(0,
        TerminologyUtility.getActiveStatedRelationships(icdConcept).size());

    // Empty concepts yield empty results
    Assert.assertEquals(0,
        TerminologyUtility.getActiveInferredRelationships(new ConceptJpa())
            .size());
    Assert.assertEquals(0,
        TerminologyUtility.getActiveParentConcepts(new ConceptJpa()).size());
    Assert.assertEquals(0,
        TerminologyUtility.getActiveStatedRelationships(new ConceptJpa())
            .size());

    // fake terminologies yield an empty result
    Assert.assertEquals(0,
        TerminologyUtility.getHierarchicalIsaRels("FAKE", "FAKE").size());

    // null value yields the zero UUID.
    Assert.assertEquals("00000000-0000-0000-0000-000000000000",
        TerminologyUtility.getUuid(null).toString());

  }

  /**
   * Test edge cases of the {@link FileSorter}.
   *
   * @throws Exception the exception
   */
  @Test
  public void testEdgeCasesandlerJpa003() throws Exception {
    // n/a no edge cases
  }

  /**
   * Test edge cases of the RF2FileSorter.
   *
   * @throws Exception the exception
   */
  @Test
  public void testEdgeCasesHandlerJpa004() throws Exception {
    // n/a - tested by loaders
  }

  /**
   * Test edge cases of the {@link ConfigUtility}.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @Test
  public void testEdgeCasesHandlerJpa005() throws Exception {

    // NO edge cases of getConfigProperties (unless file is bad).

    // Test making an XML file from an empty JAXB-enabled object
    // Then read it back from the file
    // Then delete the file and the directory
    new File("tmp").mkdirs();
    Concept concept = new ConceptJpa();

    // Node
    Node node = ConfigUtility.getNodeForGraph(concept);
    Concept concept2 =
        (Concept) ConfigUtility.getGraphForNode(node, ConceptJpa.class);
    Assert.assertEquals(concept, concept2);

    // String
    String conceptStr = ConfigUtility.getStringForGraph(concept);
    Concept concept3 =
        (Concept) ConfigUtility.getGraphForString(conceptStr, ConceptJpa.class);
    Assert.assertEquals(concept, concept3);

    // Input Stream
    Concept concept4 =
        (Concept) ConfigUtility.getGraphForStream(new ByteArrayInputStream(
            conceptStr.getBytes("UTF-8")), ConceptJpa.class);
    Assert.assertEquals(concept, concept4);

    // File
    PrintWriter out = new PrintWriter(new FileWriter(new File("tmp/x.xml")));
    out.println(conceptStr);
    out.flush();
    out.close();
    Concept concept5 =
        (Concept) ConfigUtility.getGraphForFile(new File("tmp/x.xml"),
            ConceptJpa.class);
    Assert.assertEquals(concept, concept5);

    // Remove file and dir
    boolean success = ConfigUtility.deleteDirectory(new File("tmp"));
    if (success) {
      Assert.assertFalse(new File("tmp").exists());
    } else {
      Assert.fail("Failed to delete tmp directory");
    }
    // Remove non-existent dir - EDGE case
    Assert.assertFalse(ConfigUtility.deleteDirectory(new File("tmp")));

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
