package org.ihtsdo.otf.ts.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.ihtsdo.otf.ts.rf2.ModuleDependencyRefSetMember;
import org.ihtsdo.otf.ts.rf2.jpa.ModuleDependencyRefSetMemberJpa;

/**
 * JAXB enabled implementation of {@link ModuleDependencyRefSetMemberList}.
 */
@XmlRootElement(name = "moduleDependencyRefSetMemberList")
public class ModuleDependencyRefSetMemberListJpa extends AbstractResultList<ModuleDependencyRefSetMember> implements
ModuleDependencyRefSetMemberList {

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.helpers.AbstractResultList#getObjects()
   */
  @Override
  @XmlElement(type = ModuleDependencyRefSetMemberJpa.class, name = "member")
  public List<ModuleDependencyRefSetMember> getObjects() {
    return super.getObjects();
  }

}
