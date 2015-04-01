package org.ihtsdo.otf.ts.test.jpa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.helper.TerminologyUtility;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.services.ContentService;
import org.ihtsdo.otf.ts.services.helpers.PushBackReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
