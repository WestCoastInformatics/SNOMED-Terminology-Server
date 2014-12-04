package org.ihtsdo.otf.ts.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa;

/**
 * JAXB enabled implementation of {@link DescriptionList}.
 */
@XmlRootElement(name = "descriptionList")
public class DescriptionListJpa extends AbstractResultList<Description>
    implements DescriptionList {


  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.helpers.AbstractResultList#getObjects()
   */
  @Override
  @XmlElement(type = DescriptionJpa.class, name = "description")
  public List<Description> getObjects() {
    return super.getObjects();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.helpers.ResultList#getIterable()
   */
  @XmlTransient
  @Override
  public Iterable<Description> getIterable() {
    return super.getIterable();
  }

}
