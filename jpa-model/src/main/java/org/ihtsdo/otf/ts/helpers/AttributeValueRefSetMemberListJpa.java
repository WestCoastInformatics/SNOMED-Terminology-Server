package org.ihtsdo.otf.ts.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.ihtsdo.otf.ts.rf2.AttributeValueRefSetMember;
import org.ihtsdo.otf.ts.rf2.Component;
import org.ihtsdo.otf.ts.rf2.jpa.AbstractAttributeValueRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.AttributeValueConceptRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.AttributeValueDescriptionRefSetMemberJpa;

/**
 * JAXB enabled implementation of {@link AttributeValueRefSetMemberList}.
 */
@XmlRootElement(name = "attributeValueRefSetMemberList")
@XmlSeeAlso({
  AttributeValueDescriptionRefSetMemberJpa.class,
  AttributeValueConceptRefSetMemberJpa.class
})
public class AttributeValueRefSetMemberListJpa extends AbstractResultList<AttributeValueRefSetMember<? extends Component>> implements
AttributeValueRefSetMemberList {


  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.helpers.AbstractResultList#getObjects()
   */
  @Override
  @XmlElement(type = AbstractAttributeValueRefSetMemberJpa.class, name = "member")
  public List<AttributeValueRefSetMember<? extends Component>> getObjects() {
    return super.getObjects();
  }

}
