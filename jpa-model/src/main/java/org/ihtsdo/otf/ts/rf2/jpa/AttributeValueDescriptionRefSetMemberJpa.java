package org.ihtsdo.otf.ts.rf2.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.ihtsdo.otf.ts.rf2.AttributeValueDescriptionRefSetMember;
import org.ihtsdo.otf.ts.rf2.AttributeValueConceptRefSetMember;

/**
 * Concrete implementation of {@link AttributeValueConceptRefSetMember}.
 */
@Entity
@Table(name = "attribute_value_refset_members")
@Audited
public class AttributeValueDescriptionRefSetMemberJpa extends AbstractDescriptionRefSetMember
    implements AttributeValueDescriptionRefSetMember {

  /** The value id */
  @Column(nullable = false)
  private String valueId;

  /**
   * {@inheritDoc}
   */
  @Override
  public String getValueId() {
    return this.valueId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setValueId(String valueId) {
    this.valueId = valueId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return super.toString()
        + (this.getDescription() == null ? null : this.getDescription()
            .getTerminologyId()) + "," + this.getValueId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.rf2.jpa.AbstractConceptRefSetMember#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((valueId == null) ? 0 : valueId.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.rf2.jpa.AbstractConceptRefSetMember#equals(java.
   * lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    AttributeValueDescriptionRefSetMemberJpa other = (AttributeValueDescriptionRefSetMemberJpa) obj;
    if (valueId == null) {
      if (other.valueId != null)
        return false;
    } else if (!valueId.equals(other.valueId))
      return false;
    return true;
  }
}
