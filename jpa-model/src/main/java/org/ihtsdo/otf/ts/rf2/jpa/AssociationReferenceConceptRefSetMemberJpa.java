/*
 * Copyright 2015 West Coast Informatics, LLC
 */
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

  /** The Concept associated with this element. */
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
    result =
        prime
            * result
            + ((concept == null || concept.getTerminologyId() == null) ? 0
                : concept.getTerminologyId().hashCode());
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
    } else if (concept.getTerminologyId() == null) {
      if (other.concept != null && other.concept.getTerminologyId() != null)
        return false;
    } else if (!concept.getTerminologyId().equals(
        other.concept.getTerminologyId()))
      return false;
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.ConceptRefSetMember#setConcept(org.ihtsdo.otf.ts.
   * rf2.Concept)
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
  private Long getConceptId() {
    return concept != null ? concept.getId() : null;
  }

  /**
   * Sets the concept id.
   *
   * @param conceptId the concept id
   */
  @SuppressWarnings("unused")
  private void setConceptId(Long conceptId) {
    if (concept == null) {
      concept = new ConceptJpa();
    }
    concept.setId(conceptId);
  }

  /**
   * Returns the concept terminology id. Used for XML/JSON serialization.
   * 
   * @return the concept terminology id
   */
  @XmlElement
  private String getConceptTerminologyId() {
    return concept != null ? concept.getTerminologyId() : "";
  }

  /**
   * Sets the concept terminology id.
   *
   * @param conceptId the concept terminology id
   */
  @SuppressWarnings("unused")
  private void setConceptTerminologyId(String conceptId) {
    if (concept == null) {
      concept = new ConceptJpa();
    }
    concept.setTerminologyId(conceptId);
    concept.setTerminology(getTerminology());
    concept.setTerminologyVersion(getTerminologyVersion());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.jpa.AbstractComponent#toString()
   */
  @Override
  public String toString() {
    return super.toString() + ", " + getConceptId() + ", "
        + getConceptTerminologyId() + ", " + getTargetComponentId();
  }

  /**
   * Returns the concept preferred name. Used for XML/JSON serialization.
   * 
   * @return the concept preferred name
   */
  @XmlElement
  private String getConceptPreferredName() {
    return concept != null ? concept.getDefaultPreferredName() : "";
  }

  /**
   * Sets the concept preferred name.
   *
   * @param defaultPreferredName the concept preferred name
   */
  @SuppressWarnings("unused")
  private void setConceptPreferredName(String defaultPreferredName) {
    if (concept == null) {
      concept = new ConceptJpa();
    }
    concept.setDefaultPreferredName(defaultPreferredName);
  }

}
