package org.ihtsdo.otf.ts.services.handlers;

import java.util.Properties;

import org.ihtsdo.otf.ts.helpers.User;
import org.ihtsdo.otf.ts.helpers.UserRole;
import org.ihtsdo.otf.ts.services.helpers.UserImpl;

/**
 * Implements a security handler that authorizes via IHTSDO authentication.
 */
public class DefaultSecurityServiceHandler implements SecurityServiceHandler {

  /**  The properties. */
  //private Properties properties;
  
  /* (non-Javadoc)
   * @see org.ihtsdo.otf.mapping.services.SecurityServiceHandler#authenticate(java.lang.String, java.lang.String, java.util.Properties)
   */
  @Override
  public User authenticate(String username, String password) throws Exception {

    User user = new UserImpl();
    user.setUserName("guest");
    user.setName("Guest");
    user.setApplicationRole(UserRole.VIEWER);
    user.setEmail("guest@example.com");

    return user;
  }

  /**
   * Times out all users except "guest".
   * @see org.ihtsdo.otf.ts.services.handlers.SecurityServiceHandler#timeoutUser(java.lang.String)
   */
  @Override
  public boolean timeoutUser(String user) {
    if (user.equals("guest")) {
      return false;
    }
    return true;
  }

  /**
   * Use the username as a token.
   * @see org.ihtsdo.otf.ts.services.handlers.SecurityServiceHandler#computeTokenForUser(java.lang.String)
   */
  @Override
  public String computeTokenForUser(String user) {
    return user;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.helpers.Configurable#setProperties(java.util.Properties)
   */
  @Override
  public void setProperties(Properties properties) {
    //this.properties = properties;
  }
}
