package org.ihtsdo.otf.ts.helpers;

import javax.xml.bind.annotation.XmlRootElement;

import org.ihtsdo.otf.ts.rf2.SimpleRefSetMember;

/**
 * JAXB enabled implementation of {@link SimpleRefSetMemberList}.
 */
@XmlRootElement(name = "simpleRefSetMemberList")
public class SimpleRefSetMemberListJpa extends
    AbstractResultList<SimpleRefSetMember> implements SimpleRefSetMemberList {
  // nothing extra
}
