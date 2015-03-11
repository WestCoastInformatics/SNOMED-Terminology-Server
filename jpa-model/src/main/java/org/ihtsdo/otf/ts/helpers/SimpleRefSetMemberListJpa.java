package org.ihtsdo.otf.ts.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.ihtsdo.otf.ts.rf2.SimpleRefSetMember;
import org.ihtsdo.otf.ts.rf2.jpa.SimpleRefSetMemberJpa;

/**
 * JAXB enabled implementation of {@link SimpleRefSetMemberList}.
 */
@XmlRootElement(name = "simpleRefSetMemberList")
public class SimpleRefSetMemberListJpa extends
    AbstractResultList<SimpleRefSetMember> implements SimpleRefSetMemberList {

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.AbstractResultList#getObjects()
   */
  @Override
  @XmlElement(type = SimpleRefSetMemberJpa.class, name = "member")
  public List<SimpleRefSetMember> getObjects() {
    return super.getObjects();
  }
}
