/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.jpa.services.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.handlers.DefaultIdentifierAssignmentHandler;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RelationshipJpa;
import org.ihtsdo.otf.ts.services.ContentService;
import org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration testing for {@link DefaultIdentifierAssignmentHandler}.
 */
public class Handler003Test {

  /** The handler service. */
  private IdentifierAssignmentHandler handlerService;

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
      handlerService = new DefaultIdentifierAssignmentHandler();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * /** Test normal use of the handler object.
   *
   * @throws Exception the exception
   */
  @Test
  public void testHandlerNormalUse003() throws Exception {
    Logger.getLogger(getClass()).info("TEST testHandlerNormalUse003");

    // Retrieve concept 728.1 (ICD9CM) from the content service.
    ContentService contentService = new ContentServiceJpa();
    Concept icdConcept =
        contentService.getSingleConcept("728.1", "ICD9CM", "2013");

    // call getTerminologyId(concept)
    // TEST: result should match the known terminology id for the concept
    String id = handlerService.getTerminologyId(icdConcept);
    Logger.getLogger(getClass()).info(id);
    assertEquals(id, icdConcept.getTerminologyId());

    // call getTerminologyId(description)
    // TEST: result should match the known terminology id for the description
    Description d = icdConcept.getDescriptions().iterator().next();
    id = handlerService.getTerminologyId(d);
    Logger.getLogger(getClass()).info(id);
    assertEquals(id, d.getTerminologyId());

    // call getTerminologyId(relationship)
    // TEST: result should match the known terminology id for the relationship
    Relationship r = icdConcept.getRelationships().iterator().next();
    id = handlerService.getTerminologyId(r);
    Logger.getLogger(getClass()).info(id);
    assertEquals(id, r.getTerminologyId());

  }

  /*
   * Test degenerate use of the handler object.
   * 
   * @throws Exception the exception
   */
  /**
   * Test handler degenerate use003.
   *
   * @throws Exception the exception
   */
  @Test
  public void testHandlerDegenerateUse003() throws Exception {
    // Call getTerminologyId(null)
    // TEST: exception
    try {
      handlerService.getTerminologyId((Concept) null);
      fail("Calling getTerminologyId((Concept)null) should have thrown an exception.");
    } catch (Exception e) {
      // do nothing
    }

    // Call getTerminologyId(null)
    // TEST: exception
    try {
      handlerService.getTerminologyId((Description) null);
      fail("Calling getTerminologyId((Description)null) should have thrown an exception.");
    } catch (Exception e) {
      // do nothing
    }

    // Call getTerminologyId(null)
    // TEST: exception
    try {
      handlerService.getTerminologyId((Relationship) null);
      fail("Calling getTerminologyId((Relationship)null) should have thrown an exception.");
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
  public void testHandlerEdgeCases003() throws Exception {
    // Call getTerminologyId(new ConceptJpa())
    // TEST: returns null
    assertEquals(handlerService.getTerminologyId(new ConceptJpa()), null);

    // Call getTerminologyId(new DescriptionJpa())
    // TEST: returns null
    assertEquals(handlerService.getTerminologyId(new DescriptionJpa()), null);

    // Call getTerminologyId(new RelationshipJpa())
    // TEST: returns null
    assertEquals(handlerService.getTerminologyId(new RelationshipJpa()), null);
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
