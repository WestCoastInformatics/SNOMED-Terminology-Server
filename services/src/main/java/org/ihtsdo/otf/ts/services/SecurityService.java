package org.ihtsdo.otf.ts.services;

import org.ihtsdo.otf.ts.helpers.User;
import org.ihtsdo.otf.ts.helpers.UserList;
import org.ihtsdo.otf.ts.helpers.UserRole;

/**
 * We want the web application to avoid needing to know anything about the
 * details of the security implementation (e.g. service URL, technology, etc).
 * The solution is to build a layer around security WITHIN our own service layer
 * where we can inject any security solution we want into the background.
 * 
 */
public interface SecurityService extends RootService {

  /**
   * Authenticate the user.
   * 
   * @param username the username
   * @param password the password
   * @return the token
   * @throws Exception the exception
   */
  public String authenticate(String username, String password) throws Exception;

  /**
   * Logout.
   *
   * @param authToken the auth token
   * @throws Exception 
   */
  public void logout(String authToken) throws Exception;
  
  /**
   * Returns the username for token.
   * 
   * @param authToken the auth token
   * @return the username for token
   * @throws Exception the exception
   */
  public String getUsernameForToken(String authToken) throws Exception;

  /**
   * Returns the application role for token.
   * 
   * @param authToken the auth token
   * @return the application role
   * @throws Exception the exception
   */
  public UserRole getApplicationRoleForToken(String authToken) throws Exception;

  /**
   * Get user by id.
   * @param id the id
   *
   * @return the user
   * @throws Exception the exception
   */
  public User getUser(Long id) throws Exception;

  /**
   * Get user by user.
   *
   * @param username the username
   * @return the user
   * @throws Exception the exception
   */
  public User getUser(String username) throws Exception;

  /**
   * Returns the users.
   *
   * @return the users
   */
  public UserList getUsers();

  /**
   * Adds the user.
   *
   * @param user the user
   * @return the user
   */
  public User addUser(User user);

  /**
   * Removes the user.
   *
   * @param id the id
   */
  public void removeUser(String id);

  /**
   * Update user.
   *
   * @param user the user
   */
  public void updateUser(User user);

}
