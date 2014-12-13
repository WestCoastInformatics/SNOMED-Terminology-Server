package org.ihtsdo.otf.ts.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.jpa.LanguageRefSetMemberJpa;

/**
 * JAXB enabled implementation of {@link LanguageRefSetMemberList}.
 */
@XmlRootElement(name = "languageRefSetMemberList")
public class LanguageRefSetMemberListJpa extends
    AbstractResultList<LanguageRefSetMember> implements
    LanguageRefSetMemberList {

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.helpers.AbstractResultList#getObjects()
   */
  @Override
  @XmlElement(type = LanguageRefSetMemberJpa.class, name = "languageRefSetMember")
  public List<LanguageRefSetMember> getObjects() {
    return super.getObjects();
  }

}
