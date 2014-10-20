package org.ihtsdo.otf.mapping.services;

import java.util.Properties;

import org.ihtsdo.otf.mapping.helpers.User;

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
}
