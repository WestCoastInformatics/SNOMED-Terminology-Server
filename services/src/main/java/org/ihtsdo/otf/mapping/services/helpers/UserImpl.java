package org.ihtsdo.otf.mapping.services.helpers;

import org.ihtsdo.otf.mapping.helpers.User;
import org.ihtsdo.otf.mapping.helpers.UserRole;

/**
 * Local implementation of {@link User}.
 */
public class UserImpl implements User {
  /** The user name. */
  private String userName;

  /** The name. */
  private String name;

  /** The email. */
  private String email;

  /** The application role. */
  private UserRole applicationRole;

  /**
   * The default constructor.
   */
  public UserImpl() {
    // do nothing
  }

  @Override
  public String getUserName() {
    return userName;
  }

  @Override
  public void setUserName(String username) {
    this.userName = username;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getEmail() {
    return email;
  }

  @Override
  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public UserRole getApplicationRole() {
    return applicationRole;
  }

  @Override
  public void setApplicationRole(UserRole role) {
    this.applicationRole = role;
  }

  @Override
  public Long getId() {
    return null;
  }

  @Override
  public String getObjectId() {
    return null;
  }
}