package org.ihtsdo.otf.ts.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.ihtsdo.otf.ts.rf2.AttributeValueRefSetMember;
import org.ihtsdo.otf.ts.rf2.Component;

/**
 * JAXB enabled implementation of {@link AttributeValueRefSetMemberList}.
 */
@XmlRootElement(name = "attributeValueRefSetMemberList")
public class AttributeValueRefSetMemberListJpa extends AbstractResultList<AttributeValueRefSetMember<? extends Component>> implements
AttributeValueRefSetMemberList {


  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.helpers.AbstractResultList#getObjects()
   */
  @Override
  @XmlElement(type = AttributeValueRefSetMember.class, name = "member")
  public List<AttributeValueRefSetMember<? extends Component>> getObjects() {
    return super.getObjects();
  }

}
