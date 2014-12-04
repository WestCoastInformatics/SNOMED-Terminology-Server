package org.ihtsdo.otf.ts.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.jpa.RelationshipJpa;

/**
 * JAXB enabled implementation of {@link RelationshipList}.
 */
@XmlRootElement(name = "relationshipList")
public class RelationshipListJpa extends AbstractResultList<Relationship>
    implements RelationshipList {

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.helpers.AbstractResultList#getObjects()
   */
  @Override
  @XmlElement(type = RelationshipJpa.class, name = "relationship")
  public List<Relationship> getObjects() {
    return super.getObjects();
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.helpers.AbstractResultList#getIterable()
   */
  @XmlTransient
  @Override
  public Iterable<Relationship> getIterable() {
    return super.getIterable();
  }

}
