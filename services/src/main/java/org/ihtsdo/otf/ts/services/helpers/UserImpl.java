package org.ihtsdo.otf.ts.services.helpers;

import org.ihtsdo.otf.ts.User;
import org.ihtsdo.otf.ts.UserRole;

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
    return "";
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result =
        prime * result
            + ((applicationRole == null) ? 0 : applicationRole.hashCode());
    result = prime * result + ((email == null) ? 0 : email.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((userName == null) ? 0 : userName.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    UserImpl other = (UserImpl) obj;
    if (applicationRole != other.applicationRole)
      return false;
    if (email == null) {
      if (other.email != null)
        return false;
    } else if (!email.equals(other.email))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (userName == null) {
      if (other.userName != null)
        return false;
    } else if (!userName.equals(other.userName))
      return false;
    return true;
  }
}