package org.ihtsdo.otf.ts.lists;

import org.ihtsdo.otf.ts.User;
import org.ihtsdo.otf.ts.helpers.ProxyTester;
import org.ihtsdo.otf.ts.helpers.UserList;
import org.ihtsdo.otf.ts.helpers.UserListJpa;
import org.ihtsdo.otf.ts.jpa.UserJpa;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit testing for {@link UserList}.
 */
public class ListUnit018Test extends AbstractListUnit<User> {

  /** The list1 test fixture . */
  private UserList list1;

  /** The list2 test fixture . */
  private UserList list2;

  /** The test fixture o1. */
  private User o1;

  /** The test fixture o2. */
  private User o2;

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
    list1 = new UserListJpa();
    list2 = new UserListJpa();

    ProxyTester tester = new ProxyTester(new UserJpa());
    o1 = (User) tester.createObject(1);
    o2 = (User) tester.createObject(2);

  }

  /**
   * Test normal use of a list.
   * @throws Exception the exception
   */
  @Test
  public void testNormalUse018() throws Exception {
    testNormalUse(list1, list2, o1, o2);
  }

  /**
   * Test degenerate use of a list. Show that the underlying data structure
   * should NOT be manipulated.
   * 
   * @throws Exception the exception
   */
  @Test
  public void testDegenerateUse018() throws Exception {
    testDegenerateUse(list1, list2, o1, o2);
  }

  /**
   * Test edge cases of a list.
   * 
   * @throws Exception the exception
   */
  @Test
  public void testEdgeCases018() throws Exception {
    testEdgeCases(list1, list2, o1, o2);
  }

  /**
   * Test XML serialization of a list.
   *
   * 
   * @throws Exception the exception
   */
  @Test
  public void testXmlSerialization018() throws Exception {
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
