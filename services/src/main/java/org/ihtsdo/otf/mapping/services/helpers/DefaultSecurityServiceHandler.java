package org.ihtsdo.otf.mapping.services.helpers;

import java.util.Properties;

import org.ihtsdo.otf.mapping.helpers.User;
import org.ihtsdo.otf.mapping.helpers.UserRole;
import org.ihtsdo.otf.mapping.services.SecurityServiceHandler;

/**
 * Implements a security handler that authorizes via IHTSDO authentication.
 */
public class DefaultSecurityServiceHandler implements SecurityServiceHandler {

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.mapping.services.SecurityServiceHandler#authenticate(java.lang.String, java.lang.String, java.util.Properties)
   */
  @Override
  public User authenticate(String username, String password,
    Properties properties) throws Exception {

    User user = new UserImpl();
    user.setUserName("guest");
    user.setName("Guest");
    user.setApplicationRole(UserRole.VIEWER);
    user.setEmail("guest@example.com");

    return user;
  }

  /**
   * Times out all users except "guest".
   * @see org.ihtsdo.otf.mapping.services.SecurityServiceHandler#timeoutUser(java.lang.String)
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
   * @see org.ihtsdo.otf.mapping.services.SecurityServiceHandler#computeTokenForUser(java.lang.String)
   */
  @Override
  public String computeTokenForUser(String user) {
    return user;
  }

}
