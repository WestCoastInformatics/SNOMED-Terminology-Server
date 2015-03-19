package org.ihtsdo.otf.ts.test.other;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.jpa.services.ActionServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.services.ActionService;
import org.ihtsdo.otf.ts.services.ContentService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * A classifier integration test.
 */
public class ClassificationTest {

  /** The service. */
  private static ActionService actionService;

  /**  The content service. */
  @SuppressWarnings("unused")
  private static ContentService contentService;

  /**
   * Setup.
   * @throws Exception
   */
  @SuppressWarnings("static-method")
  @Before
  public void setup() throws Exception {
    if (actionService == null) {
      actionService = new ActionServiceJpa();
      contentService = new ContentServiceJpa();
    }
  }

  /**
   * Test get single concept for SNOMEDCT.
   * @throws Exception
   */
  @Test
  public void testClassify() throws Exception {
    Logger.getLogger(getClass()).info("TEST Classify");


//    String token = actionService.configureActionService(null);
  }

  /**
   * Teardown.
   */
  @After
  public void teardown() {
    // do nothing
  }
}
