package org.ihtsdo.otf.ts.jpa.services.handlers;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.User;
import org.ihtsdo.otf.ts.helpers.UserRole;
import org.ihtsdo.otf.ts.services.handlers.SecurityServiceHandler;
import org.ihtsdo.otf.ts.services.helpers.UserImpl;

/**
 * Implements a security handler that authorizes via IHTSDO authentication.
 */
public class DefaultSecurityServiceHandler implements SecurityServiceHandler {

  /** The properties. */
  private Properties properties;

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.SecurityServiceHandler#authenticate(java
   * .lang.String, java.lang.String, java.util.Properties)
   */
  @Override
  public User authenticate(String username, String password) throws Exception {

    // username must not be null
    if (username == null)
      return null;

    // password must not be null
    if (password == null)
      return null;
    
    // for default security service, the password must equal the user name
    if (!username.equals(password))
      return null;

    // check properties
    if (properties == null) {
      properties = ConfigUtility.getConfigProperties();
    }
  
    User user = new UserImpl();

    // check specified admin users list from config file
    if (getAdminUsersFromConfigFile().contains(username)) {
      user.setApplicationRole(UserRole.ADMINISTRATOR);
      user.setUserName(username);
      user.setName(username.substring(0, 1).toUpperCase()
          + username.substring(1));
      user.setEmail(username + "@example.com");
      return user;
    }

    if (getViewerUsersFromConfigFile().contains(username)) {
      user.setApplicationRole(UserRole.VIEWER);
      user.setUserName(username);
      user.setName(username.substring(0, 1).toUpperCase()
          + username.substring(1));
      user.setEmail(username + "@example.com");
      return user;
    }

    // if user not specified, return null
    return null;
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

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.helpers.Configurable#setProperties(java.util.Properties)
   */
  @Override
  public void setProperties(Properties properties) {
    this.properties = properties;
  }

  public Set<String> getViewerUsersFromConfigFile() {
    HashSet<String> userSet = new HashSet<>();
    String userList = properties.getProperty("users.viewer");

    if (userList == null) {
      Logger
          .getLogger(DefaultSecurityServiceHandler.class)
          .warn(
              "Could not retrieve config parameter users.viewer for security handler DEFAULT");
      return userSet;
    }

    for (String user : userList.split(","))
      userSet.add(user);
    return userSet;
  }

  public Set<String> getAdminUsersFromConfigFile() {

    HashSet<String> userSet = new HashSet<>();
    String userList = properties.getProperty("users.admin");
    
    Logger
    .getLogger(DefaultSecurityServiceHandler.class).info(properties.keySet());

    if (userList == null) {
      Logger
          .getLogger(DefaultSecurityServiceHandler.class)
          .warn(
              "Could not retrieve config parameter users.admin for security handler DEFAULT");
      return userSet;
    }

    for (String user : userList.split(","))
      userSet.add(user);
    return userSet;
  }
}