/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.jpa.services.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.handlers.ClamlComputePreferredNameHandler;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa;
import org.ihtsdo.otf.ts.rf2.jpa.LanguageRefSetMemberJpa;
import org.ihtsdo.otf.ts.services.ContentService;
import org.ihtsdo.otf.ts.services.handlers.ComputePreferredNameHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration testing for {@link ClamlComputePreferredNameHandler}.
 */
public class Handler001Test {

  /** The handler service. */
  private ComputePreferredNameHandler handlerService;

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

    try {
      handlerService = new ClamlComputePreferredNameHandler();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * /** Test normal use of the handler object.
   *
   * @throws Exception the exception
   */
  @Test
  public void testHandlerNormalUse001() throws Exception {
    Logger.getLogger(getClass()).info("TEST testHandlerNormalUse001");

    // Retrieve concept 728.10 (ICD9CM) from the content service.
    ContentService contentService = new ContentServiceJpa();
    Concept icdConcept =
        contentService.getSingleConcept("728.10", "ICD9CM", "2013");

    // call computePreferredName(concept)
    // TEST: result should match the known preferred name for the concept
    // "Calcification and ossification, unspecified"
    String pn = handlerService.computePreferredName(icdConcept);
    Logger.getLogger(getClass()).info(pn);
    assertEquals(pn, "Calcification and ossification, unspecified");

    // Call getPreferredName(concept.getDescriptions());
    // TEST: result should match the known preferred name for the concept
    // "Calcification and ossification, unspecified"
    pn = handlerService.computePreferredName(icdConcept.getDescriptions());
    assertEquals(pn, "Calcification and ossification, unspecified");

    // For each description in the concept, call isPreferredName(description)
    // TEST: The description with a typeId of "4" should return true, others
    // should return false.
    for (Description description : icdConcept.getDescriptions()) {
      if (description.getTypeId().equals("4"))
        assertEquals(handlerService.isPreferredName(description), true);
      else
        assertEquals(handlerService.isPreferredName(description), false);
    }

    // For each description in the concept, call isPreferredName(description,
    // null)
    // TEST: should produce same result as previous tests.
    for (Description description : icdConcept.getDescriptions()) {
      if (description.getTypeId().equals("4"))
        assertEquals(handlerService.isPreferredName(description, null), true);
      else
        assertEquals(handlerService.isPreferredName(description, null), false);
    }

    // For each description in the concept, call isPreferredName(description,
    // new LanguageRefSetMemberJpa());
    // TEST: should produce same result as previous tests.
    for (Description description : icdConcept.getDescriptions()) {
      if (description.getTypeId().equals("4"))
        assertEquals(handlerService.isPreferredName(description,
            new LanguageRefSetMemberJpa()), true);
      else
        assertEquals(handlerService.isPreferredName(description,
            new LanguageRefSetMemberJpa()), false);
    }
  }

  /*
   * Test degenerate use of the handler object.
   * 
   * @throws Exception the exception
   */
  /**
   * Test handler degenerate use007.
   *
   * @throws Exception the exception
   */
  @Test
  public void testHandlerDegenerateUse001() throws Exception {
    // Call computePreferredName(null)
    // TEST: exception
    try {
      handlerService.computePreferredName((Concept) null);
      fail("Calling computePreferredName((Concept)null) should have thrown an exception.");
    } catch (Exception e) {
      // do nothing
    }

    // Call computePreferredName(null)
    // TEST: exception
    try {
      handlerService.computePreferredName((Set<Description>) null);
      fail("Calling computePreferredName((Set<Description>)null) should have thrown an exception.");
    } catch (Exception e) {
      // do nothing
    }
    // Call isPreferredName(null)
    // TEST: exception
    try {
      handlerService.isPreferredName(null);
      fail("Calling isPreferredName(null) should have thrown an exception.");
    } catch (Exception e) {
      // do nothing
    }
    // Call isPreferredName(null, null)
    // TEST: exception
    try {
      handlerService.isPreferredName(null, null);
      fail("Calling isPreferredName(null, null) should have thrown an exception.");
    } catch (Exception e) {
      // do nothing
    }
  }

  /**
   * Test edge cases of the handler object.
   *
   * @throws Exception the exception
   */
  @Test
  public void testHandlerEdgeCases001() throws Exception {
    // Call computePreferredName(new ConceptJpa())
    // TEST: returns null
    assertEquals(handlerService.computePreferredName(new ConceptJpa()), null);

    // Call computePreferredName(new HashSet<Description>())
    // TEST: returns null
    assertEquals(
        handlerService.computePreferredName(new HashSet<Description>()), null);

    // Call isPreferredName(new DescriptionJpa())
    // TEST: returns false
    assertEquals(handlerService.isPreferredName(new DescriptionJpa()), false);

    // Call isPreferredName(new DescriptionJpa(), new LanguageRefSetMemberJpa())
    // TEST: returns false
    assertEquals(handlerService.isPreferredName(new DescriptionJpa(),
        new LanguageRefSetMemberJpa()), false);
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
