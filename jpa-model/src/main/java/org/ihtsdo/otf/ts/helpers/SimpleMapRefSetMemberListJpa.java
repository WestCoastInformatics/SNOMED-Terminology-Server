/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.ihtsdo.otf.ts.rf2.SimpleMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.jpa.SimpleMapRefSetMemberJpa;

/**
 * JAXB enabled implementation of {@link SimpleMapRefSetMemberList}.
 */
@XmlRootElement(name = "simpleMapRefSetMemberList")
public class SimpleMapRefSetMemberListJpa extends
    AbstractResultList<SimpleMapRefSetMember> implements
    SimpleMapRefSetMemberList {

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.helpers.AbstractResultList#getObjects()
   */
  @Override
  @XmlElement(type = SimpleMapRefSetMemberJpa.class, name = "member")
  public List<SimpleMapRefSetMember> getObjects() {
    return super.getObjects();
  }

}
