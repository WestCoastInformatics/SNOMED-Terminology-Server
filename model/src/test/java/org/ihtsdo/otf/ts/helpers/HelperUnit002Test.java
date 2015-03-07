package org.ihtsdo.otf.ts.helpers;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit testing for {@link ConfigUtility}.
 */
public class HelperUnit002Test {

  /** The helper object to test. */
  private CancelException object;

  /** The helper object to test. */
  private CancelException object2;

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
    object = new CancelException("test");
    Exception e = new Exception("inner");
    object2 = new CancelException("test2", e);
  }

  /**
   * Test normal use of the helper object.
   *
   * @throws Exception the exception
   */
  @Test
  public void testHelperNormalUse002() throws Exception {
    Logger.getLogger(getClass()).info("TEST testHelperNormalUse002");
    assertTrue(object.getMessage().equals("test"));
    assertTrue(object2.getMessage().equals("test2"));
    assertTrue(object2.getCause() != null);
    assertTrue(object2.getCause().getMessage().equals("inner"));
  }

  /**
   * Test degenerate use of the helper object.
   *
   * @throws Exception the exception
   */
  @Test
  public void testHelperDegenerateUse002() throws Exception {
    Logger.getLogger(getClass()).info("TEST testHelperDegenerateUse002");

  }

  /**
   * Test edge cases of the helper object.
   *
   * @throws Exception the exception
   */
  @Test
  public void testHelperEdgeCases002() throws Exception {
    Logger.getLogger(getClass()).info("TEST testHelperEdgeCases002");

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
