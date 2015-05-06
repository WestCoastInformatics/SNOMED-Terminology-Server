/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.rf2.jpa;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.hibernate.envers.Audited;
import org.ihtsdo.otf.ts.rf2.AttributeValueRefSetMember;
import org.ihtsdo.otf.ts.rf2.Component;

/**
 * Abstract implementation of {@link AttributeValueRefSetMember}.
 * @param <T> the {@link Component}
 */
@Entity
@Table(name = "attribute_value_refset_members")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING, length = 50)
@Audited
@XmlSeeAlso({
    AttributeValueDescriptionRefSetMemberJpa.class,
    AttributeValueConceptRefSetMemberJpa.class,
})
public abstract class AbstractAttributeValueRefSetMemberJpa<T extends Component>
    extends AbstractRefSetMemberJpa<T> implements AttributeValueRefSetMember<T> {

  /** The value id. */
  @Column(nullable = false)
  private String valueId;

  /**
   * Instantiates an empty {@link AbstractAttributeValueRefSetMemberJpa}.
   */
  protected AbstractAttributeValueRefSetMemberJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link AbstractAttributeValueRefSetMemberJpa} from the
   * specified parameters.
   *
   * @param member the member
   */
  protected AbstractAttributeValueRefSetMemberJpa(
      AttributeValueRefSetMember<T> member) {
    super(member);
    valueId = member.getValueId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.AttributeValueRefSetMember#getValueId()
   */
  @Override
  public String getValueId() {
    return this.valueId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.AttributeValueRefSetMember#setValueId(java.lang.String
   * )
   */
  @Override
  public void setValueId(String valueId) {
    this.valueId = valueId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.jpa.AbstractRefSetMemberJpa#hashCode()
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
   * org.ihtsdo.otf.ts.rf2.jpa.AbstractRefSetMemberJpa#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    @SuppressWarnings("unchecked")
    AbstractAttributeValueRefSetMemberJpa<? extends Component> other =
        (AbstractAttributeValueRefSetMemberJpa<? extends Component>) obj;
    if (valueId == null) {
      if (other.valueId != null)
        return false;
    } else if (!valueId.equals(other.valueId))
      return false;
    return true;
  }

}
