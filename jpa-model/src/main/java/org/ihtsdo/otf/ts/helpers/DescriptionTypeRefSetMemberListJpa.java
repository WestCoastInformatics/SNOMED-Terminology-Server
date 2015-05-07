/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.ihtsdo.otf.ts.rf2.DescriptionTypeRefSetMember;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionTypeRefSetMemberJpa;

/**
 * JAXB enabled implementation of {@link DescriptionTypeRefSetMemberList}.
 */
@XmlRootElement(name = "descriptionTypeRefSetMemberList")
public class DescriptionTypeRefSetMemberListJpa extends
    AbstractResultList<DescriptionTypeRefSetMember> implements
    DescriptionTypeRefSetMemberList {

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.AbstractResultList#getObjects()
   */
  @Override
  @XmlElement(type = DescriptionTypeRefSetMemberJpa.class, name = "member")
  public List<DescriptionTypeRefSetMember> getObjects() {
    return super.getObjects();
  }

}
