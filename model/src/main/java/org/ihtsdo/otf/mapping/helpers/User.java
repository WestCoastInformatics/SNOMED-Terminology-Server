package org.ihtsdo.otf.mapping.helpers;

/**
 * Represents a user TODO: fix!.
 *
 * @author ${author}
 */
public interface User {

  /**
   * Returns the id.
   *
   * @return the id
   */
  public String getId();

  /**
   * Returns the name.
   *
   * @return the name
   */
  public String getName();

  /**
   * Returns the email.
   *
   * @return the email
   */
  public String getEmail();

  /**
   * Returns the application role.
   *
   * @return the application role
   */
  public UserRole getApplicationRole();

  /**
   * Returns the user name.
   *
   * @return the user name
   */
  public String getUserName();

  /**
   * Returns the object id.
   *
   * @return the object id
   */
  public String getObjectId();

  /**
   * Sets the user name.
   *
   * @param userName the user name
   */
  public void setUserName(String userName);

  /**
   * Sets the name.
   *
   * @param fullName the name
   */
  public void setName(String fullName);

  /**
   * Sets the email.
   *
   * @param email the email
   */
  public void setEmail(String email);

  /**
   * Sets the application role.
   *
   * @param role the application role
   */
  public void setApplicationRole(UserRole role);

}
