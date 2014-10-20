package org.ihtsdo.otf.ts.services;

import java.util.Properties;

import org.ihtsdo.otf.ts.helpers.User;

/**
 * Genericall represents a handler that can authenticate a user.
 */
public interface SecurityServiceHandler {

  /**
   * Authenticate.
   *
   * @param user the user
   * @param password the password
   * @param properties the properties
   * @return the user
   * @throws Exception 
   */
  public User authenticate(String user, String password, Properties properties) throws Exception;

  
  /**
   * Indicates whether or not the user should be timed out.
   *
   * @param user the user
   * @return true, if successful
   */
  public boolean timeoutUser(String user);
  
  /**
   * Computes token for user.  For example, a UUID or an MD5 or a counter.
   * Each login requires yields a potentially different token, even for the same user.
   *
   * @param user the user
   * @return the string
   */
  public String computeTokenForUser(String user);
}
