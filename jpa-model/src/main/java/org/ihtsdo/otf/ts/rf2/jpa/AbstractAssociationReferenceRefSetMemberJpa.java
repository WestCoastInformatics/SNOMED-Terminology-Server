package org.ihtsdo.otf.ts.rf2.jpa;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceRefSetMember;
import org.ihtsdo.otf.ts.rf2.Component;

/**
 * Abstract implementation of {@link AssociationReferenceRefSetMember}.
 * @param <T> the {@link Component}
 */
@Entity
@Table(name = "association_reference_refset_members")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING, length = 50)
@Audited
public abstract class AbstractAssociationReferenceRefSetMemberJpa<T extends Component>
    extends AbstractRefSetMemberJpa<T> implements
    AssociationReferenceRefSetMember<T> {

  /** The target component id */
  @Column(nullable = false)
  private String targetComponentId;

  @Override
  public String getTargetComponentId() {
    return this.targetComponentId;
  }

  /**
   * Instantiates an empty {@link AbstractAssociationReferenceRefSetMemberJpa}.
   */
  protected AbstractAssociationReferenceRefSetMemberJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link AbstractAssociationReferenceRefSetMemberJpa} from the
   * specified parameters.
   *
   * @param member the member
   */
  protected AbstractAssociationReferenceRefSetMemberJpa(
      AssociationReferenceRefSetMember<T> member) {
    super(member);
    targetComponentId = member.getTargetComponentId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.AssociationReferenceRefSetMember#setTargetComponentId
   * (java.lang.String)
   */
  @Override
  public void setTargetComponentId(String targetComponentId) {
    this.targetComponentId = targetComponentId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result =
        prime * result
            + ((targetComponentId == null) ? 0 : targetComponentId.hashCode());
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
    @SuppressWarnings("unchecked")
    AbstractAssociationReferenceRefSetMemberJpa<? extends Component> other =
        (AbstractAssociationReferenceRefSetMemberJpa<? extends Component>) obj;
    if (targetComponentId == null) {
      if (other.targetComponentId != null)
        return false;
    } else if (!targetComponentId.equals(other.targetComponentId))
      return false;
    return true;
  }

}
