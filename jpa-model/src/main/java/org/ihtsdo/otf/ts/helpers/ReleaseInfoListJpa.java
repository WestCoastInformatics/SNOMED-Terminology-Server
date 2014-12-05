package org.ihtsdo.otf.ts.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * JAXB-enabled implementation of {@link ReleaseInfoList}.
 */
@XmlRootElement(name = "releaseInfoList")
public class ReleaseInfoListJpa extends AbstractResultList<ReleaseInfo>
    implements ReleaseInfoList {


  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.helpers.AbstractResultList#getObjects()
   */
  @Override
  @XmlElement(type = ReleaseInfoJpa.class, name = "releaseInfo")
  public List<ReleaseInfo> getObjects() {
    return super.getObjects();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "ReleaseInfoListJpa [releaseInfos=" + getObjects()
        + ", getCount()=" + getCount() + "]";
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.helpers.AbstractResultList#getIterable()
   */
  @XmlTransient
  @Override
  public Iterable<ReleaseInfo> getIterable() {
    return super.getIterable();
  }

}
