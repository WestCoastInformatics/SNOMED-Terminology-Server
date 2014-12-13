package org.ihtsdo.otf.ts.services.helpers;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.GetterSetterTester;
import org.ihtsdo.otf.ts.helpers.UserRole;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test for {@link UserImpl}.
 */
public class UserImplTest {

  /**
   * Setup.
   */
  @Before
  public void setup() {
    // do nothing
  }

  /**
   * Test getter and setter methods.
   */
  @Test
  public void testGetterSetter() {
    try {
      UserImpl user = new UserImpl();

      Logger.getLogger(this.getClass()).info(
          "  Testing " + user.getClass().getName());
      GetterSetterTester tester = new GetterSetterTester(user);
      tester.test();
      
      user.setApplicationRole(UserRole.LEAD);
      user.setEmail("test@example.com");
      user.setName("Test name");
      user.setUserName("username");

      UserImpl user2 = new UserImpl();
      user2.setApplicationRole(UserRole.LEAD);
      user2.setEmail("test@example.com");
      user2.setName("Test name");
      user2.setUserName("username");
      
      Assert.assertEquals(user, user2);
      Assert.assertEquals(user.hashCode(), user2.hashCode());
    
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  /**
   * Teardown.
   */
  @After
  public void teardown() {
    // do nothing
  }
}
