package org.ihtsdo.otf.ts.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * JAXB enabled implementation of {@link UserList}.
 */
@XmlRootElement(name = "userList")
public class UserListJpa extends AbstractResultList<User> implements UserList {

  /** The users. */
  private List<User> users = new ArrayList<>();

  /**
   * Instantiates a new user list.
   */
  public UserListJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link UserListJpa} from the specified parameters.
   *
   * @param users the users
   */
  public UserListJpa(List<User> users) {
    this.users = users;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ping.helpers.MapUserList#addMapUser(org.ihtsdo.otf.mapping
   * .model.MapUser)
   */
  @Override
  public void addUser(User user) {
    users.add(user);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ping.helpers.MapUserList#removeMapUser(org.ihtsdo.otf
   * .mapping.model.MapUser)
   */
  @Override
  public void removeUser(User user) {
    users.remove(user);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ping.helpers.MapUserList#setMapUsers(java.util.List)
   */
  @Override
  public void setUsers(List<User> users) {
    this.users = new ArrayList<>();
    this.users.addAll(users);

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.helpers.MapUserList#getMapUsers()
   */
  @Override
  @XmlElement(type = UserJpa.class, name = "user")
  public List<User> getUsers() {
    return users;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ping.helpers.ResultList#getCount()
   */
  @Override
  @XmlElement(name = "count")
  public int getCount() {
    return users.size();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.helpers.ResultList#sortBy(java.util.Comparator)
   */
  @Override
  public void sortBy(Comparator<User> comparator) {
    Collections.sort(users, comparator);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.helpers.ResultList#contains(java.lang.Object)
   */
  @Override
  public boolean contains(User element) {
    return users.contains(element);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.helpers.ResultList#getIterable()
   */
  @XmlTransient
  @Override
  public Iterable<User> getIterable() {
    return users;
  }

}
