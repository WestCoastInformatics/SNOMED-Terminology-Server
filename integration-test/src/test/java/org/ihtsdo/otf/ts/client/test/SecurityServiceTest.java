package org.ihtsdo.otf.ts.client.test;

import static org.junit.Assert.assertEquals;

import org.ihtsdo.otf.ts.jpa.client.SecurityClientRest;
import org.ihtsdo.otf.ts.services.handlers.DefaultSecurityServiceHandler;
import org.ihtsdo.otf.ts.services.helpers.ConfigUtility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for REST security service.
 */
public class SecurityServiceTest {

  /** The client. */
  private SecurityClientRest client;

  /**
   * Setup.
   * @throws Exception
   */
  @Before
  public void setup() throws Exception {
    client = new SecurityClientRest(ConfigUtility.getTestConfigProperties());

  }

  /**
   * Test authenticate. ASSUMPTION: security handler is
   * {@link DefaultSecurityServiceHandler}.
   * @throws Exception
   */
  @Test
  public void testAuthenticate() throws Exception {
    String authToken = client.authenticate("guest", "guest");
    assertEquals(authToken, "guest");
  }

  /**
   * Teardown.
   */
  @After
  public void teardown() {
    // do nothing
  }
}
