package org.ihtsdo.otf.ts.jpa.services.test;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.services.helpers.ProgressEvent;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit testing for {@link Concept}.
 */
public class HelperUnit008Test {

  /** The helper object to test. */
  private ProgressEvent object;

  private String note = "";
  
  private int percent = 0;
  
  private long progress = 0;
  
  private Object source = new Object();

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
  }



  /**	/**
	 * Test normal use of the helper object.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testHelperNormalUse008() throws Exception {
		Logger.getLogger(getClass()).info("TEST testHelperNormalUse008");
		note = "500 of 1000 completed";
		percent = 50;
		progress = 500;
		source = new Object();

		object = new ProgressEvent(source, percent, progress, note);
		assertEquals(object.getNote(), note);
		assertEquals(object.getPercent(), percent);
		assertEquals(object.getProgress(), progress);
		assertEquals(object.getSource(), source);
		assertEquals(object.getScaledPercent(50, 100), 75);
		assertEquals(object.getScaledPercent(80, 100), 90);
	}
	
  /* Test degenerate use of the helper object.
   *
   * @throws Exception the exception
   */
  @Test
  public void testHelperDegenerateUse008() throws Exception {
		Logger.getLogger(getClass()).info("TEST testHelperDegeneratelUse008");
		
		// note is null - TEST: no exceptions expected
		note = null;
		percent = 50;
		progress = 500;
		object = new ProgressEvent(source, percent, progress, note);
		assertEquals(object.getNote(), note);
		assertEquals(object.getPercent(), percent);
		assertEquals(object.getProgress(), progress);
		assertEquals(object.getSource(), source);
		assertEquals(object.getScaledPercent(50, 100), 75);
		
		// source is null - TEST: no exceptions expected
		note = null;
		source = null;
		percent = 50;
		progress = 500;
		object = new ProgressEvent(source, percent, progress, note);
		assertEquals(object.getNote(), note);
		assertEquals(object.getPercent(), percent);
		assertEquals(object.getProgress(), progress);
		assertEquals(object.getSource(), source);
		assertEquals(object.getScaledPercent(50, 100), 75);
  }

  /**
   * Test edge cases of the helper object.
   *
   * @throws Exception the exception
   */
  @Test
  public void testHelperEdgeCases008() throws Exception {
		Logger.getLogger(getClass()).info("TEST testHelperEdgeCases008");
		
		// lower bounds test - TEST: no exceptions expected
		note = "";
		percent = 0;
		progress = 0;
		source = new Object();
		object = new ProgressEvent(source, percent, progress, note);
		assertEquals(object.getNote(), note);
		assertEquals(object.getPercent(), percent);
		assertEquals(object.getProgress(), progress);
		assertEquals(object.getSource(), source);
		assertEquals(object.getScaledPercent(50, 100), 50);
		assertEquals(object.getScaledPercent(0, 50), 0);
		assertEquals(object.getScaledPercent(0, 0), 0);
		
		// upper bounds test - TEST: no exceptions expected
		note = "";
		percent = 100;
		progress = 1000;
		source = new Object();
		object = new ProgressEvent(source, percent, progress, note);
		assertEquals(object.getNote(), note);
		assertEquals(object.getPercent(), percent);
		assertEquals(object.getProgress(), progress);
		assertEquals(object.getSource(), source);
		assertEquals(object.getScaledPercent(50, 100), 100);
		assertEquals(object.getScaledPercent(0, 50), 50);
		assertEquals(object.getScaledPercent(100, 100), 100);
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
