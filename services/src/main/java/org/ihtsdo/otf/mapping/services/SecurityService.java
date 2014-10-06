package org.ihtsdo.otf.mapping.services;

import org.ihtsdo.otf.mapping.helpers.User;
import org.ihtsdo.otf.mapping.helpers.UserList;
import org.ihtsdo.otf.mapping.helpers.UserRole;

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
  public UserRole getApplicationRoleForToken(String authToken)
    throws Exception;
  

  /**
   * Get user.
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
   * @return the user
   */
  public User removeUser(String id);
  
  /**
   * Update user.
   *
   * @param user the user
   * @return the user
   */
  public User updateUser(User user);

  /**
   * Returns the user role for project.
   *
   * @param username the username
   * @param projectId the project id
   * @return the user role for project
   */
  public UserRole getUserRoleForProject(String username, Long projectId);
  
}
