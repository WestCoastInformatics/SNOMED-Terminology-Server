package org.ihtsdo.otf.ts.rf2.jpa;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.envers.Audited;
import org.ihtsdo.otf.ts.rf2.AttributeValueConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.Concept;

/**
 * Concrete implementation of {@link AttributeValueConceptRefSetMember}.
 */
@Entity
@Audited
@DiscriminatorValue("Concept")
public class AttributeValueConceptRefSetMemberJpa extends AbstractAttributeValueRefSetMemberJpa<Concept>
    implements AttributeValueConceptRefSetMember {


  /** The Concept associated with this element */
  @ManyToOne(targetEntity = ConceptJpa.class, optional = true)
  private Concept concept;

  /**
   * Instantiates an empty {@link AttributeValueConceptRefSetMemberJpa}.
   */
  public AttributeValueConceptRefSetMemberJpa() {
    // do nothing
  }
  
  /**
   * Instantiates a {@link AttributeValueConceptRefSetMemberJpa} from the specified parameters.
   *
   * @param member the member
   */
  public AttributeValueConceptRefSetMemberJpa(AttributeValueConceptRefSetMember member) {
    super(member);
    this.concept = member.getConcept();
  }
  
  /**
   * {@inheritDoc}
   */
  @XmlTransient
  @Override
  public Concept getConcept() {
    return this.concept;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.rf2.RefSetMember#getComponent()
   */
  @Override
  public Concept getComponent() {
    return getConcept();
  }
  
  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.rf2.RefSetMember#setComponent(org.ihtsdo.otf.ts.rf2.Component)
   */
  @Override
  public void setComponent(Concept concept) {
    setConcept(concept);
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((concept == null) ? 0 : concept.hashCode());
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
    AttributeValueConceptRefSetMemberJpa other =
        (AttributeValueConceptRefSetMemberJpa) obj;
    if (concept == null) {
      if (other.concept != null)
        return false;
    } else if (!concept.equals(other.concept))
      return false;
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setConcept(Concept concept) {
    this.concept = concept;

  }

  /**
   * Returns the concept id. Used for XML/JSON serialization.
   * 
   * @return the concept id
   */
  @XmlElement
  public String getConceptId() {
    return concept != null ? concept.getTerminologyId() : null;
  }
  }
