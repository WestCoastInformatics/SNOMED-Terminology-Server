package org.ihtsdo.otf.ts.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.ihtsdo.otf.ts.rf2.AssociationReferenceRefSetMember;
import org.ihtsdo.otf.ts.rf2.Component;
import org.ihtsdo.otf.ts.rf2.jpa.AbstractAssociationReferenceRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.AssociationReferenceConceptRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.AssociationReferenceDescriptionRefSetMemberJpa;

/**
 * JAXB enabled implementation of {@link AssociationReferenceRefSetMemberList}.
 */
@XmlRootElement(name = "associationReferenceRefSetMemberList")
@XmlSeeAlso({
    AssociationReferenceDescriptionRefSetMemberJpa.class,
    AssociationReferenceConceptRefSetMemberJpa.class
})
public class AssociationReferenceRefSetMemberListJpa extends
    AbstractResultList<AssociationReferenceRefSetMember<? extends Component>>
    implements AssociationReferenceRefSetMemberList {

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.AbstractResultList#getObjects()
   */
  @Override
  @XmlElement(type = AbstractAssociationReferenceRefSetMemberJpa.class, name = "member")
  public List<AssociationReferenceRefSetMember<? extends Component>> getObjects() {
    return super.getObjects();
  }

}
