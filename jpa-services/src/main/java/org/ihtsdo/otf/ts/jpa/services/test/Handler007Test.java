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
import org.ihtsdo.otf.ts.jpa.services.handlers.Rf2ComputePreferredNameHandler;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa;
import org.ihtsdo.otf.ts.services.ContentService;
import org.ihtsdo.otf.ts.services.handlers.ComputePreferredNameHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration testing for {@link Rf2ComputePreferredNameHandler}.
 */
public class Handler007Test {

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
      handlerService = new Rf2ComputePreferredNameHandler();

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
  public void testHandlerNormalUse007() throws Exception {
    Logger.getLogger(getClass()).info("TEST testHandlerNormalUse007");

    // Retrieve concept 102276005 (SNOMEDCT) from the content service.
    ContentService contentService = new ContentServiceJpa();
    Concept sctConcept =
        contentService.getSingleConcept("102276005", "SNOMEDCT", "latest");

    // call computePreferredName(concept)
    // TEST: result should match the known preferred name for the concept
    // "Bipartite ossification"
    String pn = handlerService.computePreferredName(sctConcept);
    Logger.getLogger(getClass()).info(pn);
    assertEquals(pn, "Bipartite ossification");

    // call computePreferredName(concept.getDescriptions())
    // TEST: result should match the known preferred name for the concept
    // "Bipartite ossification"
    pn = handlerService.computePreferredName(sctConcept.getDescriptions());
    Logger.getLogger(getClass()).info(pn);
    assertEquals(pn, "Bipartite ossification");

    // For each description in the concept, call isPreferredName(description)
    // TEST: the description with typeId=900000000000013009 and language refset
    // member with
    // refSetId=900000000000509007 and acceptabilityId=900000000000548007 should
    // return true - all others should return false.
    for (Description description : sctConcept.getDescriptions()) {
      Set<LanguageRefSetMember> lrms = description.getLanguageRefSetMembers();
      boolean lrmsFound = false;
      for (LanguageRefSetMember lrm : lrms) {
        if (lrm.getRefSetId().equals("900000000000509007")
            && lrm.getAcceptabilityId().equals("900000000000548007")) {
          lrmsFound = true;
        }
      }
      if (description.getTypeId().equals("900000000000013009") && lrmsFound)
        assertEquals(handlerService.isPreferredName(description), true);
      else
        assertEquals(handlerService.isPreferredName(description), false);
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
  public void testHandlerDegenerateUse007() throws Exception {

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
  }

  /**
   * Test edge cases of the handler object.
   *
   * @throws Exception the exception
   */
  @Test
  public void testHandlerEdgeCases007() throws Exception {
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
