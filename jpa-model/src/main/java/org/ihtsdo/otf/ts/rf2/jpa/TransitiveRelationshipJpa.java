package org.ihtsdo.otf.ts.rf2.jpa;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.ContainedIn;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.TransitiveRelationship;

/**
 * Concrete implementation of {@link TransitiveRelationship} for use with JPA.
 */
@Entity
// @UniqueConstraint here is being used to create an index, not to enforce
// uniqueness
@Table(name = "transitive_relationships", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "subTypeConcept_id", "superTypeConcept_id"
    }), @UniqueConstraint(columnNames = {
        "superTypeConcept_id", "subTypeConcept_id"
    })
})
@Audited
@XmlRootElement(name = "transitiveRelationship")
public class TransitiveRelationshipJpa extends AbstractComponent implements
    TransitiveRelationship {

  /** The subtype concept. */
  @ManyToOne(targetEntity = ConceptJpa.class, optional = false)
  @ContainedIn
  private Concept subTypeConcept;

  /** The supertype concept. */
  @ManyToOne(targetEntity = ConceptJpa.class, optional = false)
  private Concept superTypeConcept;

  /**
   * Instantiates an empty {@link TransitiveRelationshipJpa}.
   */
  public TransitiveRelationshipJpa() {
    // do nothing
  }
  
  /**
   * Instantiates a {@link TransitiveRelationshipJpa} from the specified parameters.
   *
   * @param transitiveRelationship the transitive relationship
   */
  public TransitiveRelationshipJpa(TransitiveRelationship transitiveRelationship) {
    subTypeConcept = transitiveRelationship.getSubTypeConcept();
    superTypeConcept = transitiveRelationship.getSuperTypeConcept();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.rf2.TransitiveRelationship#getSubTypeConcept()
   */
  @Override
  public Concept getSubTypeConcept() {
    return subTypeConcept;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.rf2.TransitiveRelationship#setSubTypeConcept(org
   * .ihtsdo.otf.mapping.rf2.Concept)
   */
  @Override
  public void setSubTypeConcept(Concept subTypeConcept) {
    this.subTypeConcept = subTypeConcept;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.rf2.TransitiveRelationship#getSuperTypeConcept()
   */
  @Override
  public Concept getSuperTypeConcept() {
    return superTypeConcept;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.rf2.TransitiveRelationship#setSuperTypeConcept(org
   * .ihtsdo.otf.mapping.rf2.Concept)
   */
  @Override
  public void setSuperTypeConcept(Concept superTypeConcept) {
    this.superTypeConcept = superTypeConcept;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result =
        prime * result
            + ((subTypeConcept == null) ? 0 : subTypeConcept.hashCode());
    result =
        prime * result
            + ((superTypeConcept == null) ? 0 : superTypeConcept.hashCode());
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
    TransitiveRelationshipJpa other = (TransitiveRelationshipJpa) obj;
    if (subTypeConcept == null) {
      if (other.subTypeConcept != null)
        return false;
    } else if (!subTypeConcept.equals(other.subTypeConcept))
      return false;
    if (superTypeConcept == null) {
      if (other.superTypeConcept != null)
        return false;
    } else if (!superTypeConcept.equals(other.superTypeConcept))
      return false;
    return true;
  }

}
