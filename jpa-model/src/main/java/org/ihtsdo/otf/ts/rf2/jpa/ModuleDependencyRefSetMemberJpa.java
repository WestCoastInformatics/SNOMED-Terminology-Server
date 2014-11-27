package org.ihtsdo.otf.ts.rf2.jpa;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.envers.Audited;
import org.ihtsdo.otf.ts.rf2.ModuleDependencyRefSetMember;

/**
 * Concrete implementation of {@link ModuleDependencyRefSetMember}.
 */
@Entity
@Table(name = "module_dependency_refset_members")
@Audited
@XmlRootElement(name = "moduleDependency")
public class ModuleDependencyRefSetMemberJpa extends
    AbstractConceptRefSetMember implements ModuleDependencyRefSetMember {

  /** The source effective time. */
  private Date sourceEffectiveTime;

  /** The target effective time. */
  private Date targetEffectiveTime;

  /**
   * Instantiates an empty {@link ModuleDependencyRefSetMemberJpa}.
   */
  public ModuleDependencyRefSetMemberJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link ModuleDependencyRefSetMemberJpa} from the specified
   * parameters.
   *
   * @param member the member
   */
  public ModuleDependencyRefSetMemberJpa(ModuleDependencyRefSetMember member) {
    super(member);
    sourceEffectiveTime = member.getSourceEffectiveTime();
    targetEffectiveTime = member.getTargetEffectiveTime();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.ModuleDependencyRefSetMember#getSourceEffectiveTime()
   */
  @Override
  public Date getSourceEffectiveTime() {
    return sourceEffectiveTime;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.ModuleDependencyRefSetMember#setSourceEffectiveTime
   * (java.util.Date)
   */
  @Override
  public void setSourceEffectiveTime(Date sourceEffectiveTime) {
    this.sourceEffectiveTime = sourceEffectiveTime;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.ModuleDependencyRefSetMember#getTargetEffectiveTime()
   */
  @Override
  public Date getTargetEffectiveTime() {
    return targetEffectiveTime;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.ModuleDependencyRefSetMember#setTargetEffectiveTime
   * (java.util.Date)
   */
  @Override
  public void setTargetEffectiveTime(Date targetEffectiveTime) {
    this.targetEffectiveTime = targetEffectiveTime;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.jpa.AbstractComponent#toString()
   */
  @Override
  public String toString() {
    return super.toString() + ", " + sourceEffectiveTime + ", "
        + targetEffectiveTime;
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
            + ((sourceEffectiveTime == null) ? 0 : sourceEffectiveTime
                .hashCode());
    result =
        prime
            * result
            + ((targetEffectiveTime == null) ? 0 : targetEffectiveTime
                .hashCode());
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
    ModuleDependencyRefSetMemberJpa other =
        (ModuleDependencyRefSetMemberJpa) obj;
    if (sourceEffectiveTime == null) {
      if (other.sourceEffectiveTime != null)
        return false;
    } else if (!sourceEffectiveTime.equals(other.sourceEffectiveTime))
      return false;
    if (targetEffectiveTime == null) {
      if (other.targetEffectiveTime != null)
        return false;
    } else if (!targetEffectiveTime.equals(other.targetEffectiveTime))
      return false;
    return true;
  }

}
