/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.ihtsdo.otf.ts.rf2.AssociationReferenceRefSetMember;
import org.ihtsdo.otf.ts.rf2.Component;

/**
 * JAXB enabled implementation of {@link AssociationReferenceRefSetMemberList}.
 */
@XmlRootElement(name = "associationReferenceRefSetMemberList")
public class AssociationReferenceRefSetMemberListJpa extends AbstractResultList<AssociationReferenceRefSetMember<? extends Component>> implements
AssociationReferenceRefSetMemberList {


  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.helpers.AbstractResultList#getObjects()
   */
  @Override
  @XmlElement(type = AssociationReferenceRefSetMember.class, name = "member")
  public List<AssociationReferenceRefSetMember<? extends Component>> getObjects() {
    return super.getObjects();
  }

}
