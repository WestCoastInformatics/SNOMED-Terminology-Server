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
import org.ihtsdo.otf.ts.rf2.AssociationReferenceDescriptionRefSetMember;
import org.ihtsdo.otf.ts.rf2.Description;

/**
 * Concrete implementation of {@link AssociationReferenceConceptRefSetMember}.
 */
@Entity
@Audited
@DiscriminatorValue("Description")
@XmlRootElement(name = "descriptionAssociationRef")
public class AssociationReferenceDescriptionRefSetMemberJpa extends
    AbstractAssociationReferenceRefSetMemberJpa<Description> implements
    AssociationReferenceDescriptionRefSetMember {

  /** The Description associated with this element. */
  @ManyToOne(targetEntity = DescriptionJpa.class, optional = true)
  private Description description;

  /**
   * Instantiates an empty
   * {@link AssociationReferenceDescriptionRefSetMemberJpa}.
   */
  public AssociationReferenceDescriptionRefSetMemberJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link AssociationReferenceDescriptionRefSetMemberJpa} from
   * the specified parameters.
   *
   * @param member the member
   */
  public AssociationReferenceDescriptionRefSetMemberJpa(
      AssociationReferenceDescriptionRefSetMember member) {
    super(member);
    description = member.getDescription();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.DescriptionRefSetMember#getDescription()
   */
  @XmlTransient
  @Override
  public Description getDescription() {
    return this.description;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.RefSetMember#getComponent()
   */
  @XmlTransient
  @Override
  public Description getComponent() {
    return getDescription();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.RefSetMember#setComponent(org.ihtsdo.otf.ts.rf2.Component
   * )
   */
  @Override
  public void setComponent(Description description) {
    setDescription(description);
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
            + ((description == null || description.getTerminologyId() == null)
                ? 0 : description.getTerminologyId().hashCode());
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
    AssociationReferenceDescriptionRefSetMemberJpa other =
        (AssociationReferenceDescriptionRefSetMemberJpa) obj;
    if (description == null) {
      if (other.description != null)
        return false;
    } else if (description.getTerminologyId() == null) {
      if (other.description == null
          || other.description.getTerminologyId() != null)
        return false;
    } else if (!description.getTerminologyId().equals(
        other.description.getTerminologyId()))
      return false;
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.DescriptionRefSetMember#setDescription(org.ihtsdo
   * .otf.ts.rf2.Description)
   */
  @Override
  public void setDescription(Description description) {
    this.description = description;

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.jpa.AbstractComponent#toString()
   */
  @Override
  public String toString() {
    return super.toString() + ", " + getDescriptionId() + ", "
        + getDescriptionTerminologyId() + ", " + getTargetComponentId();
  }

  /**
   * Returns the description id. Used for XML/JSON serialization.
   * 
   * @return the description id
   */
  @XmlElement
  private Long getDescriptionId() {
    return description != null ? description.getId() : null;
  }

  /**
   * Sets the description id.
   *
   * @param descriptionId the description id
   */
  @SuppressWarnings("unused")
  private void setDescriptionId(Long descriptionId) {
    if (description == null) {
      description = new DescriptionJpa();
    }
    description.setId(descriptionId);
  }

  /**
   * Returns the description terminology id. Used for XML/JSON serialization.
   * 
   * @return the description terminology id
   */
  @XmlElement
  private String getDescriptionTerminologyId() {
    return description != null ? description.getTerminologyId() : "";
  }

  /**
   * Sets the description terminology id.
   *
   * @param descriptionId the description terminology id
   */
  @SuppressWarnings("unused")
  private void setDescriptionTerminologyId(String descriptionId) {
    if (description == null) {
      description = new DescriptionJpa();
    }
    description.setTerminologyId(descriptionId);
    description.setTerminology(getTerminology());
    description.setTerminologyVersion(getTerminologyVersion());
  }

  /**
   * Returns the description term. Used for XML/JSON serialization.
   * 
   * @return the description term
   */
  @XmlElement
  private String getDescriptionTerm() {
    return description != null ? description.getTerm() : "";
  }

  /**
   * Sets the description term.
   *
   * @param term the description term
   */
  @SuppressWarnings("unused")
  private void setDescriptionTerm(String term) {
    if (description == null) {
      description = new DescriptionJpa();
    }
    description.setTerm(term);
  }
}
