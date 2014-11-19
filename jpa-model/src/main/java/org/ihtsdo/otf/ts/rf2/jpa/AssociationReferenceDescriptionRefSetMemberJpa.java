package org.ihtsdo.otf.ts.rf2.jpa;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.envers.Audited;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceDescriptionRefSetMember;
import org.ihtsdo.otf.ts.rf2.Description;

/**
 * Concrete implementation of {@link AssociationReferenceConceptRefSetMember}.
 */
@Entity
@Audited
@DiscriminatorValue("Description")
public class AssociationReferenceDescriptionRefSetMemberJpa extends
    AbstractAssociationReferenceRefSetMemberJpa<Description> implements
    AssociationReferenceDescriptionRefSetMember {

  /** The Description associated with this element */
  @ManyToOne(targetEntity = DescriptionJpa.class, optional = true)
  private Description description;

  @XmlTransient
  @Override
  public Description getDescription() {
    return this.description;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.RefSetMember#getComponent()
   */
  @Override
  public Description getComponent() {
    return getDescription();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.RefSetMember#setComponent(org.ihtsdo.otf.ts.rf2.Component
   * )
   */
  @Override
  public void setComponent(Description description) {
    setDescription(description);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result =
        prime * result + ((description == null) ? 0 : description.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    AssociationReferenceDescriptionRefSetMemberJpa other =
        (AssociationReferenceDescriptionRefSetMemberJpa) obj;
    if (description == null) {
      if (other.description != null)
        return false;
    } else if (!description.equals(other.description))
      return false;
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(Description description) {
    this.description = description;

  }

  /**
   * Returns the description id. Used for XML/JSON serialization.
   * 
   * @return the description id
   */
  @XmlElement
  public String getDescriptionId() {
    return description != null ? description.getTerminologyId() : null;
  }
}
