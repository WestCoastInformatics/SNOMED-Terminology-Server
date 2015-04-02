package org.ihtsdo.otf.ts.test.jpa;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Properties;

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
public class HelperJpaNormalUseTest {

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
   * Test normal use of the {@link PushBackReader}. .
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseHandlerJpa001() throws Exception {
    String line = reader.readLine();
    reader.push(line);
    String line2 = reader.readLine();
    Assert.assertEquals(line, line2);

  }

  /**
   * Test normal use of the {@link TerminologyUtility}.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @Test
  public void testNormalUseHandlerJpa002() throws Exception {

    // Get a SNOMED concept and an ICD9CM concept
    ContentService contentService = new ContentServiceJpa();
    Concept sctConcept =
        contentService.getSingleConcept("102276005", "SNOMEDCT", "latest");
    Concept icdConcept =
        contentService.getSingleConcept("728.1", "ICD9CM", "2013");

    Assert.assertEquals("900000000000011006",
        TerminologyUtility.getInferredType("SNOMEDCT", "latest"));
    Assert.assertEquals("6",
        TerminologyUtility.getInferredType("ICD9CM", "2013"));
    Assert.assertEquals("900000000000010007",
        TerminologyUtility.getStatedType("SNOMEDCT", "latest"));
    Assert
        .assertEquals("6", TerminologyUtility.getStatedType("ICD9CM", "2013"));

    Assert.assertEquals(3,
        TerminologyUtility.getActiveInferredRelationships(sctConcept).size());
    Assert.assertEquals(1,
        TerminologyUtility.getActiveInferredRelationships(icdConcept).size());
    Assert.assertEquals(2,
        TerminologyUtility.getActiveParentConcepts(sctConcept).size());
    Assert.assertEquals(1,
        TerminologyUtility.getActiveParentConcepts(icdConcept).size());
    Assert.assertEquals(3,
        TerminologyUtility.getActiveStatedRelationships(sctConcept).size());
    Assert.assertEquals(1,
        TerminologyUtility.getActiveStatedRelationships(icdConcept).size());
    Assert.assertEquals("116680003",
        TerminologyUtility.getHierarchicalIsaRels("SNOMEDCT", "latest")
            .iterator().next());
    Assert.assertEquals("7",
        TerminologyUtility.getHierarchicalIsaRels("ICD9CM", "2013").iterator()
            .next());

    Assert.assertEquals("dbbbc66b-f849-5685-b714-8c059b6f8848",
        TerminologyUtility.getUuid("abcdef").toString());

  }

  /**
   * Test normal use of the {@link FileSorter}.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @Test
  public void testNormalUseHandlerJpa003() throws Exception {
    File outFile = File.createTempFile("abc", "txt");
    FileSorter.sortFile(
        "../config/dev-windows/src/main/resources/config.properties",
        outFile.toString(), String.CASE_INSENSITIVE_ORDER);

    Assert.assertTrue(outFile.exists());
    // check sorted file
    Assert.assertTrue(FileSorter.checkSortedFile(outFile,
        String.CASE_INSENSITIVE_ORDER));

    // check unsorted file
    Assert.assertFalse(FileSorter.checkSortedFile(
        new File("../config/dev-windows/src/main/resources/config.properties"),
        String.CASE_INSENSITIVE_ORDER));

    FileSorter.deleteSortedFiles(outFile);
  }

  /**
   * Test normal use of the RF2FileSorter.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseHandlerJpa004() throws Exception {
    // n/a - tested by loaders
  }

  /**
   * Test normal use of the {@link ConfigUtility}.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @Test
  public void testNormalUseHandlerJpa005() throws Exception {

    // Verify config environment has all expected properties
    Properties properties = new Properties();
    properties.load(new FileInputStream(new File(
        "../config/dev-windows/src/main/resources/config.properties")));
    Properties config = ConfigUtility.getConfigProperties();
    for (Object o : properties.keySet()) {
      String prop = o.toString();
      Assert.assertNotNull(config.getProperty(prop));
    }

    // Test making an XML file from a JAXB-enabled object
    // Then read it back from the file
    // Then delete the file and the directory
    new File("tmp").mkdirs();
    ContentService service = new ContentServiceJpa();
    Concept concept =
        service.getSingleConcept("102276005", "SNOMEDCT", "latest");

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

    // Other methods are either inherently tested by being so fundamental, or
    // require
    // real external resources (like a mail server) that are not available in
    // this environment.

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
