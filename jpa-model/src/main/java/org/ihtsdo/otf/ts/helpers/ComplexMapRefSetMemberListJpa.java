package org.ihtsdo.otf.ts.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.ihtsdo.otf.ts.rf2.ComplexMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.jpa.ComplexMapRefSetMemberJpa;

/**
 * JAXB enabled implementation of {@link ComplexMapRefSetMemberList}.
 */
@XmlRootElement(name = "complexMapRefSetMemberList")
public class ComplexMapRefSetMemberListJpa extends AbstractResultList<ComplexMapRefSetMember> implements
ComplexMapRefSetMemberList {

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.helpers.AbstractResultList#getObjects()
   */
  @Override
  @XmlElement(type = ComplexMapRefSetMemberJpa.class, name = "member")
  public List<ComplexMapRefSetMember> getObjects() {
    return super.getObjects();
  }

}
