package org.ihtsdo.otf.ts.rf2.jpa;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.ContainedIn;
import org.ihtsdo.otf.ts.rf2.AttributeValueConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.AttributeValueDescriptionRefSetMember;
import org.ihtsdo.otf.ts.rf2.Description;

/**
 * Concrete implementation of {@link AttributeValueConceptRefSetMember}.
 */
@Entity
@Audited
@DiscriminatorValue("Description")
public class AttributeValueDescriptionRefSetMemberJpa extends AbstractAttributeValueRefSetMemberJpa<Description>
    implements AttributeValueDescriptionRefSetMember {

  /** The description. */
  @ManyToOne(targetEntity = DescriptionJpa.class, optional = true)
  // NOTE: this may apply only to LanguageRefSetMember given how
  // description uses @IndexedEmbedded
  @ContainedIn
  private Description description;

  /**
   * Instantiates an empty {@link AttributeValueDescriptionRefSetMemberJpa}.
   */
  public AttributeValueDescriptionRefSetMemberJpa() {
    // do nothing
  }
  
  /**
   * Instantiates a {@link AttributeValueDescriptionRefSetMemberJpa} from the specified parameters.
   *
   * @param member the member
   */
  public AttributeValueDescriptionRefSetMemberJpa(AttributeValueDescriptionRefSetMember member) {
    super(member);
    description = member.getDescription();
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.rf2.DescriptionRefSetMember#getDescription()
   */
  @XmlTransient
  @Override
  public Description getDescription() {
    return this.description;
  }


  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.rf2.jpa.AbstractAttributeValueRefSetMemberJpa#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result =
        prime * result + ((description == null) ? 0 : description.hashCode());
    return result;
  }


  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.rf2.jpa.AbstractAttributeValueRefSetMemberJpa#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    AttributeValueDescriptionRefSetMemberJpa other =
        (AttributeValueDescriptionRefSetMemberJpa) obj;
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


  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.rf2.RefSetMember#getComponent()
   */
  @Override
  public Description getComponent() {
    return getDescription();
  }


  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.rf2.RefSetMember#setComponent(org.ihtsdo.otf.ts.rf2.Component)
   */
  @Override
  public void setComponent(Description component) {
    setDescription(component);    
  }

}
