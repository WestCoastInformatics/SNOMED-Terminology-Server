package org.ihtsdo.otf.ts.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.ihtsdo.otf.ts.rf2.RefsetDescriptorRefSetMember;
import org.ihtsdo.otf.ts.rf2.jpa.RefsetDescriptorRefSetMemberJpa;

/**
 * JAXB enabled implementation of {@link RefsetDescriptorRefSetMemberList}.
 */
@XmlRootElement(name = "refsetDescriptorRefSetMemberList")
public class RefsetDescriptorRefSetMemberListJpa extends AbstractResultList<RefsetDescriptorRefSetMember> implements
RefsetDescriptorRefSetMemberList {

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.helpers.AbstractResultList#getObjects()
   */
  @Override
  @XmlElement(type = RefsetDescriptorRefSetMemberJpa.class, name = "member")
  public List<RefsetDescriptorRefSetMember> getObjects() {
    return super.getObjects();
  }

}
