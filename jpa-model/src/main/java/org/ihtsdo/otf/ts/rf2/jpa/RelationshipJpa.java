package org.ihtsdo.otf.ts.rf2.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.ContainedIn;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Relationship;

/**
 * Concrete implementation of {@link Relationship} for use with JPA.
 */
@Entity
// @UniqueConstraint here is being used to create an index, not to enforce
// uniqueness
@Table(name = "relationships", uniqueConstraints = @UniqueConstraint(columnNames = {
    "terminologyId", "terminology", "terminologyVersion", "id"
}))
@Audited
@XmlRootElement(name = "relationship")
public class RelationshipJpa extends AbstractComponent implements Relationship {

  /** The workflow status. */
  @Column(nullable = true)
  private String workflowStatus;

  /** The source concept. */
  @ManyToOne(targetEntity = ConceptJpa.class, optional = false)
  @ContainedIn
  private Concept sourceConcept;

  /** The destination concept. */
  @ManyToOne(targetEntity = ConceptJpa.class, optional = false)
  private Concept destinationConcept;

  /** The type id. */
  @Column(nullable = false)
  private String typeId;

  /** The characteristic type id. */
  @Column(nullable = false)
  private String characteristicTypeId;

  /** The modifier id. */
  @Column(nullable = false)
  private String modifierId;

  /** The relationship group. */
  @Column(nullable = true)
  private Integer relationshipGroup;

  /**
   * Relationship.
   */
  public RelationshipJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link RelationshipJpa} from the specified parameters.
   *
   * @param relationship the relationship
   */
  public RelationshipJpa(Relationship relationship) {
    super(relationship);
    characteristicTypeId = relationship.getCharacteristicTypeId();
    // could be a problem if a concept has 2 relationships with the same
    // destination
    // because they coming from JSON would be separate objects, though if id is
    // always set, then its fine
    destinationConcept = relationship.getDestinationConcept();
    modifierId = relationship.getModifierId();
    relationshipGroup = relationship.getRelationshipGroup();
    // in deep copy contexts, this will be overridden
    sourceConcept = relationship.getSourceConcept();
    typeId = relationship.getTypeId();
    workflowStatus = relationship.getWorkflowStatus();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Relationship#getWorkflowStatus()
   */
  @Override
  public String getWorkflowStatus() {
    return workflowStatus;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Relationship#setWorkflowStatus(java.lang.String)
   */
  @Override
  public void setWorkflowStatus(String workflowStatus) {
    this.workflowStatus = workflowStatus;
  }

  /**
   * Returns the type id.
   * 
   * @return the type id
   */
  @Override
  public String getTypeId() {
    return typeId;
  }

  /**
   * Sets the type id.
   * 
   * @param typeId the type id
   */
  @Override
  public void setTypeId(String typeId) {
    this.typeId = typeId;
  }

  /**
   * Returns the characteristic type id.
   * 
   * @return the characteristic type id
   */
  @Override
  public String getCharacteristicTypeId() {
    return characteristicTypeId;
  }

  /**
   * Sets the characteristic type id.
   * 
   * @param characteristicTypeId the characteristic type id
   */
  @Override
  public void setCharacteristicTypeId(String characteristicTypeId) {
    this.characteristicTypeId = characteristicTypeId;
  }

  /**
   * Returns the modifier id.
   * 
   * @return the modifier id
   */
  @Override
  public String getModifierId() {
    return modifierId;
  }

  /**
   * Sets the modifier id.
   * 
   * @param modifierId the modifier id
   */
  @Override
  public void setModifierId(String modifierId) {
    this.modifierId = modifierId;
  }

  /**
   * Returns the source concept.
   * 
   * @return the source concept
   */
  @XmlTransient
  @Override
  public Concept getSourceConcept() {
    return sourceConcept;
  }

  /**
   * Sets the source concept.
   * 
   * @param sourceConcept the source concept
   */
  @Override
  public void setSourceConcept(Concept sourceConcept) {
    this.sourceConcept = sourceConcept;
  }

  /**
   * For serialization .
   *
   * @return the source concept id
   */
  @XmlElement
  private Long getSourceId() {
    return (sourceConcept != null) ? sourceConcept.getId() : 0;
  }

  /**
   * Sets the source concept id.
   *
   * @param sourceId the source concept id
   */
  @SuppressWarnings("unused")
  private void setSourceId(Long sourceId) {
    if (sourceConcept == null) {
      sourceConcept = new ConceptJpa();
    }
    sourceConcept.setId(sourceId);
  }

  /**
   * For serialization .
   *
   * @return the source concept terminology id
   */
  @XmlElement
  private String getSourceTerminologyId() {
    return (sourceConcept != null) ? sourceConcept.getTerminologyId() : "";
  }

  /**
   * Sets the source concept terminology id.
   *
   * @param sourceTerminologyId the source concept id
   */
  @SuppressWarnings("unused")
  private void setSourceTerminologyId(String sourceTerminologyId) {
    if (sourceConcept == null) {
      sourceConcept = new ConceptJpa();
    }
    sourceConcept.setTerminologyId(sourceTerminologyId);
    sourceConcept.setTerminology(getTerminology());
    sourceConcept.setTerminologyVersion(getTerminologyVersion());
  }

  /**
   * Returns the source concept preferred name. Used for XML/JSON serialization.
   * @return the source concept preferred name
   */
  @XmlElement
  private String getSourcePreferredName() {
    return sourceConcept != null ? sourceConcept.getDefaultPreferredName() : "";
  }

  /**
   * Sets the source concept preferred name.
   *
   * @param name the source concept preferred name
   */
  @SuppressWarnings("unused")
  private void setSourcePreferredName(String name) {
    // do nothing - here for JAXB
  }

  /**
   * Returns the destination concept.
   * 
   * @return the destination concept
   */
  @XmlTransient
  @Override
  public Concept getDestinationConcept() {
    return this.destinationConcept;
  }

  /**
   * Sets the destination concept.
   * 
   * @param destinationConcept the destination concept
   */
  @Override
  public void setDestinationConcept(Concept destinationConcept) {
    this.destinationConcept = destinationConcept;
  }

  /**
   * For serialization.
   *
   * @return the destination concept id
   */
  @XmlElement
  private Long getDestinationId() {
    return (destinationConcept != null) ? destinationConcept.getId() : 0;
  }

  /**
   * Sets the destination concept id.
   *
   * @param destinationId the destination id
   */
  @SuppressWarnings("unused")
  private void setDestinationId(Long destinationId) {
    if (destinationConcept == null) {
      destinationConcept = new ConceptJpa();
    }
    destinationConcept.setId(destinationId);
  }

  /**
   * For serialization.
   *
   * @return the destination concept terminology id
   */
  @XmlElement
  private String getDestinationTerminologyId() {
    return (destinationConcept != null) ? destinationConcept.getTerminologyId()
        : "";
  }

  /**
   * Sets the destination concept id.
   *
   * @param destinationConceptId the destination concept id
   */
  @SuppressWarnings("unused")
  private void setDestinationTerminologyId(String destinationConceptId) {
    if (destinationConcept == null) {
      destinationConcept = new ConceptJpa();
    }
    destinationConcept.setTerminologyId(destinationConceptId);
    destinationConcept.setTerminology(getTerminology());
    destinationConcept.setTerminologyVersion(getTerminologyVersion());
  }

  /**
   * Returns the destination concept preferred name. Used for XML/JSON
   * serialization.
   * @return the destination concept preferred name
   */
  @XmlElement
  private String getDestinationPreferredName() {
    return destinationConcept != null ? destinationConcept
        .getDefaultPreferredName() : "";
  }

  /**
   * Sets the destination concept preferred name.
   *
   * @param name the destination concept preferred name
   */
  @SuppressWarnings("unused")
  private void setDestinationPreferredName(String name) {
    // do nothing - here for JAXB
  }

  /**
   * Returns the relationship group.
   * 
   * @return the relationship group
   */
  @Override
  public Integer getRelationshipGroup() {
    return relationshipGroup;
  }

  /**
   * Sets the relationship group.
   * 
   * @param relationshipGroup the relationship group
   */
  @Override
  public void setRelationshipGroup(Integer relationshipGroup) {
    this.relationshipGroup = relationshipGroup;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.jpa.AbstractComponent#toString()
   */
  @Override
  public String toString() {
    return super.toString() + "," + getSourceId() + ","
        + getSourceTerminologyId() + "," + getDestinationId() + ","
        + getDestinationTerminologyId() + "," + this.getRelationshipGroup()
        + "," + this.getTypeId() + "," + this.getCharacteristicTypeId() + ","
        + this.getModifierId(); // end of
                                // relationship
                                // fields

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.jpa.AbstractComponent#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result =
        prime
            * result
            + ((characteristicTypeId == null) ? 0 : characteristicTypeId
                .hashCode());
    result =
        prime
            * result
            + ((destinationConcept == null || destinationConcept
                .getTerminologyId() == null) ? 0 : destinationConcept
                .getTerminologyId().hashCode());
    result =
        prime * result + ((modifierId == null) ? 0 : modifierId.hashCode());
    result =
        prime * result
            + ((relationshipGroup == null) ? 0 : relationshipGroup.hashCode());
    result =
        prime
            * result
            + ((sourceConcept == null || sourceConcept.getTerminologyId() == null)
                ? 0 : sourceConcept.getTerminologyId().hashCode());
    result = prime * result + ((typeId == null) ? 0 : typeId.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.jpa.AbstractComponent#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    RelationshipJpa other = (RelationshipJpa) obj;
    if (characteristicTypeId == null) {
      if (other.characteristicTypeId != null)
        return false;
    } else if (!characteristicTypeId.equals(other.characteristicTypeId))
      return false;
    if (destinationConcept == null) {
      if (other.destinationConcept != null)
        return false;
    } else if (destinationConcept.getTerminologyId() == null) {
      if (other.destinationConcept != null
          && other.destinationConcept.getTerminologyId() != null)
        return false;
    } else if (!destinationConcept.getTerminologyId().equals(
        other.destinationConcept.getTerminologyId()))
      return false;
    if (modifierId == null) {
      if (other.modifierId != null)
        return false;
    } else if (!modifierId.equals(other.modifierId))
      return false;
    if (relationshipGroup == null) {
      if (other.relationshipGroup != null)
        return false;
    } else if (!relationshipGroup.equals(other.relationshipGroup))
      return false;
    if (sourceConcept == null) {
      if (other.sourceConcept != null)
        return false;
    } else if (sourceConcept.getTerminologyId() == null) {
      if (other.sourceConcept != null
          && other.sourceConcept.getTerminologyId() != null)
        return false;
    } else if (!sourceConcept.getTerminologyId().equals(
        other.sourceConcept.getTerminologyId()))
      return false;
    if (typeId == null) {
      if (other.typeId != null)
        return false;
    } else if (!typeId.equals(other.typeId))
      return false;
    return true;
  }

}
