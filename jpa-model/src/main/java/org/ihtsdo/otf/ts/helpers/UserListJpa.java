package org.ihtsdo.otf.ts.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * JAXB enabled implementation of {@link UserList}.
 */
@XmlRootElement(name = "userList")
public class UserListJpa extends AbstractResultList<User> implements UserList {

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.helpers.AbstractResultList#getObjects()
   */
  @Override
  @XmlElement(type = UserJpa.class, name = "user")
  public List<User> getObjects() {
    return super.getObjects();
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.helpers.AbstractResultList#getIterable()
   */
  @XmlTransient
  @Override
  public Iterable<User> getIterable() {
    return super.getIterable();
  }

}
