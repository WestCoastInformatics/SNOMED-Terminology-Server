package org.ihtsdo.otf.mapping.helpers;

import java.util.List;

/**
 * Represents a sortable list of {@link User}.
 */
public interface UserList extends ResultList<User> {

  /**
   * Adds the user.
   * 
   * @param user the user
   */
  public void addUser(User user);

  /**
   * Removes the user.
   * 
   * @param user the user
   */
  public void removeUser(User user);

  /**
   * Sets the users.
   * 
   * @param users the new users
   */
  public void setUsers(List<User> users);

  /**
   * Gets the users.
   * 
   * @return the users
   */
  public List<User> getUsers();

}
