package org.ihtsdo.otf.ts.rf2.jpa;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.ihtsdo.otf.ts.rf2.RefsetDescriptorRefSetMember;

/**
 * Concrete implementation of {@link RefsetDescriptorRefSetMember}.
 */
@Entity
@Table(name = "refset_descriptor_refset_members")
@Audited
public class RefsetDescriptorRefSetMemberJpa extends
    AbstractConceptRefSetMember implements RefsetDescriptorRefSetMember {

  /** The attribute description. */
  private String attributeDescription;

  /** The attribute type. */
  private String attributeType;

  /** The attribute order. */
  private int attributeOrder;

  /**
   * Instantiates an empty {@link RefsetDescriptorRefSetMemberJpa}.
   */
  public RefsetDescriptorRefSetMemberJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link RefsetDescriptorRefSetMemberJpa} from the specified
   * parameters.
   *
   * @param member the member
   */
  public RefsetDescriptorRefSetMemberJpa(RefsetDescriptorRefSetMember member) {
    super(member);
    attributeDescription = member.getAttributeDescription();
    attributeType = member.getAttributeType();
    attributeOrder = member.getAttributeOrder();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.RefsetDescriptorRefSetMember#getAttributeDescription
   * ()
   */
  @Override
  public String getAttributeDescription() {
    return attributeDescription;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.RefsetDescriptorRefSetMember#setAttributeDescription
   * (java.lang.String)
   */
  @Override
  public void setAttributeDescription(String attributeDescription) {
    this.attributeDescription = attributeDescription;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.RefsetDescriptorRefSetMember#getAttributeType()
   */
  @Override
  public String getAttributeType() {
    return attributeType;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.RefsetDescriptorRefSetMember#setAttributeType(java
   * .lang.String)
   */
  @Override
  public void setAttributeType(String attributeType) {
    this.attributeType = attributeType;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.RefsetDescriptorRefSetMember#getAttributeOrder()
   */
  @Override
  public int getAttributeOrder() {
    return attributeOrder;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.RefsetDescriptorRefSetMember#setAttributeOrder(int)
   */
  @Override
  public void setAttributeOrder(int attributeOrder) {
    this.attributeOrder = attributeOrder;
  }

  @Override
  public String toString() {
    return super.toString() + " " + attributeDescription + ", " + attributeType
        + ", " + attributeOrder;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.jpa.AbstractConceptRefSetMember#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result =
        prime
            * result
            + ((attributeDescription == null) ? 0 : attributeDescription
                .hashCode());
    result = prime * result + attributeOrder;
    result =
        prime * result
            + ((attributeType == null) ? 0 : attributeType.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.jpa.AbstractConceptRefSetMember#equals(java.lang.
   * Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    RefsetDescriptorRefSetMemberJpa other =
        (RefsetDescriptorRefSetMemberJpa) obj;
    if (attributeDescription == null) {
      if (other.attributeDescription != null)
        return false;
    } else if (!attributeDescription.equals(other.attributeDescription))
      return false;
    if (attributeOrder != other.attributeOrder)
      return false;
    if (attributeType == null) {
      if (other.attributeType != null)
        return false;
    } else if (!attributeType.equals(other.attributeType))
      return false;
    return true;
  }

}
