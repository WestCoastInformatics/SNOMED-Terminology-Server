package org.ihtsdo.otf.ts.lists;

import org.ihtsdo.otf.ts.helpers.ProxyTester;
import org.ihtsdo.otf.ts.helpers.SearchCriteria;
import org.ihtsdo.otf.ts.helpers.SearchCriteriaJpa;
import org.ihtsdo.otf.ts.helpers.SearchCriteriaList;
import org.ihtsdo.otf.ts.helpers.SearchCriteriaListJpa;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit testing for {@link SearchCriteriaList}.
 */
public class ListUnit014Test extends AbstractListUnit<SearchCriteria> {

  /** The list1 test fixture . */
  private SearchCriteriaList list1;

  /** The list2 test fixture . */
  private SearchCriteriaList list2;

  /** The test fixture o1. */
  private SearchCriteria o1;

  /** The test fixture o2. */
  private SearchCriteria o2;

  /**
   * Setup class.
   */
  @BeforeClass
  public static void setupClass() {
    // do nothing
  }

  /**
   * Setup.
   *
   * @throws Exception the exception
   */
  @Before
  public void setup() throws Exception {
    list1 = new SearchCriteriaListJpa();
    list2 = new SearchCriteriaListJpa();

    ProxyTester tester = new ProxyTester(new SearchCriteriaJpa());
    o1 = (SearchCriteria) tester.createObject(1);
    o2 = (SearchCriteria) tester.createObject(2);

  }

  /**
   * Test normal use of a list.
   * @throws Exception the exception
   */
  @Test
  public void testNormalUse014() throws Exception {
    testNormalUse(list1, list2, o1, o2);
  }

  /**
   * Test degenerate use of a list. Show that the underlying data structure
   * should NOT be manipulated.
   * 
   * @throws Exception the exception
   */
  @Test
  public void testDegenerateUse014() throws Exception {
    testDegenerateUse(list1, list2, o1, o2);
  }

  /**
   * Test edge cases of a list.
   * 
   * @throws Exception the exception
   */
  @Test
  public void testEdgeCases014() throws Exception {
    testEdgeCases(list1, list2, o1, o2);
  }

  /**
   * Test XML serialization of a list.
   *
   * 
   * @throws Exception the exception
   */
  @Test
  public void testXmlSerialization014() throws Exception {
    testXmllSerialization(list1, list2, o1, o2);
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
