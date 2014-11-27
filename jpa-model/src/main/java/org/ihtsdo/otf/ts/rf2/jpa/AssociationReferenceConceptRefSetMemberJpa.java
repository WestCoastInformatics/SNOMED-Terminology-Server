package org.ihtsdo.otf.ts.rf2.jpa;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.envers.Audited;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.Concept;

/**
 * Concrete implementation of {@link AssociationReferenceConceptRefSetMember}.
 */
@Entity
@Audited
@DiscriminatorValue("Concept")
@XmlRootElement(name = "assocRefConcept")
public class AssociationReferenceConceptRefSetMemberJpa extends
    AbstractAssociationReferenceRefSetMemberJpa<Concept> implements
    AssociationReferenceConceptRefSetMember {

  /** The Concept associated with this element */
  @ManyToOne(targetEntity = ConceptJpa.class, optional = true)
  private Concept concept;

  /**
   * Instantiates an empty {@link AssociationReferenceConceptRefSetMemberJpa}.
   */
  public AssociationReferenceConceptRefSetMemberJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link AssociationReferenceConceptRefSetMemberJpa} from the
   * specified parameters.
   *
   * @param member the member
   */
  public AssociationReferenceConceptRefSetMemberJpa(
      AssociationReferenceConceptRefSetMember member) {
    super(member);
    concept = member.getConcept();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.ConceptRefSetMember#getConcept()
   */
  @XmlTransient
  @Override
  public Concept getConcept() {
    return this.concept;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.RefSetMember#getComponent()
   */
  @XmlTransient
  @Override
  public Concept getComponent() {
    return getConcept();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.RefSetMember#setComponent(org.ihtsdo.otf.ts.rf2.Component
   * )
   */
  @Override
  public void setComponent(Concept concept) {
    setConcept(concept);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.jpa.AbstractAssociationReferenceRefSetMemberJpa#hashCode
   * ()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((concept == null) ? 0 : concept.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.jpa.AbstractAssociationReferenceRefSetMemberJpa#equals
   * (java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    AssociationReferenceConceptRefSetMemberJpa other =
        (AssociationReferenceConceptRefSetMemberJpa) obj;
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
