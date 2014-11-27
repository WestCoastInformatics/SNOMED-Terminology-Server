package org.ihtsdo.otf.ts.rf2.jpa;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.envers.Audited;
import org.ihtsdo.otf.ts.rf2.DescriptionTypeRefSetMember;

/**
 * Concrete implementation of {@link DescriptionTypeRefSetMember}.
 */
@Entity
@Table(name = "description_type_refset_members")
@Audited
@XmlRootElement(name = "descriptionType")
public class DescriptionTypeRefSetMemberJpa extends AbstractConceptRefSetMember
    implements DescriptionTypeRefSetMember {

  /** The description format. */
  private String descriptionFormat;

  /** The description length. */
  private int descriptionLength;

  /**
   * Instantiates an empty {@link DescriptionTypeRefSetMemberJpa}.
   */
  public DescriptionTypeRefSetMemberJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link DescriptionTypeRefSetMemberJpa} from the specified
   * parameters.
   *
   * @param member the member
   */
  public DescriptionTypeRefSetMemberJpa(DescriptionTypeRefSetMember member) {
    super(member);
    descriptionFormat = member.getDescriptionFormat();
    descriptionLength = member.getDescriptionLength();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.DescriptionTypeRefSetMember#getDescriptionFormat()
   */
  @Override
  public String getDescriptionFormat() {
    return descriptionFormat;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.DescriptionTypeRefSetMember#setDescriptionFormat(
   * java.lang.String)
   */
  @Override
  public void setDescriptionFormat(String descriptionFormat) {
    this.descriptionFormat = descriptionFormat;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.DescriptionTypeRefSetMember#getDescriptionLength()
   */
  @Override
  public int getDescriptionLength() {
    return descriptionLength;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.DescriptionTypeRefSetMember#setDescriptionLength(int)
   */
  @Override
  public void setDescriptionLength(int descriptionLength) {
    this.descriptionLength = descriptionLength;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.jpa.AbstractComponent#toString()
   */
  @Override
  public String toString() {
    return super.toString() + ", " + descriptionFormat + ", "
        + descriptionLength;
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
        prime * result
            + ((descriptionFormat == null) ? 0 : descriptionFormat.hashCode());
    result = prime * result + descriptionLength;
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
    DescriptionTypeRefSetMemberJpa other = (DescriptionTypeRefSetMemberJpa) obj;
    if (descriptionFormat == null) {
      if (other.descriptionFormat != null)
        return false;
    } else if (!descriptionFormat.equals(other.descriptionFormat))
      return false;
    if (descriptionLength != other.descriptionLength)
      return false;
    return true;
  }

}
