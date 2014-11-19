package org.ihtsdo.otf.ts.rf2.jpa;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceDescriptionRefSetMember;
import org.ihtsdo.otf.ts.rf2.AttributeValueDescriptionRefSetMember;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;

/**
 * Concrete implementation of {@link Description} for use with JPA.
 */
@Entity
// @UniqueConstraint here is being used to create an index, not to enforce
// uniqueness
@Table(name = "descriptions", uniqueConstraints = @UniqueConstraint(columnNames = {
    "terminologyId", "terminology", "terminologyVersion", "id"
}))
@Audited
@XmlRootElement(name = "description")
public class DescriptionJpa extends AbstractComponent implements Description {

  /** The workflow status. */
  @Column(nullable = true)
  private String workflowStatus;

  /** The language code. */
  @Column(nullable = false, length = 10)
  private String languageCode;

  /** The typeId. */
  @Column(nullable = false)
  private String typeId;

  /** The term. */
  @Column(nullable = false, length = 4000)
  @Fields({
      @Field, @Field(name = "all", analyze = Analyze.YES, store = Store.NO)
  })
  @Analyzer(definition = "noStopWord")
  private String term;

  /** The case significance id. */
  @Column(nullable = false)
  private String caseSignificanceId;

  /** The concept. */
  @ManyToOne(targetEntity = ConceptJpa.class, optional = false)
  @ContainedIn
  private Concept concept;

  /** The language RefSet members */
  @OneToMany(mappedBy = "description", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, targetEntity = LanguageRefSetMemberJpa.class)
  // @IndexedEmbedded(targetElement = LanguageRefSetMemberJpa.class) PG
  private Set<LanguageRefSetMember> languageRefSetMembers = new HashSet<>();

  /** The attributeValue RefSet members. */
  @OneToMany(mappedBy = "description", cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = AttributeValueDescriptionRefSetMemberJpa.class)
  private Set<AttributeValueDescriptionRefSetMember> attributeValueRefSetMembers =
      new HashSet<>();

  /** The associationReference RefSet members. */
  @OneToMany(mappedBy = "description", cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = AssociationReferenceDescriptionRefSetMemberJpa.class)
  private Set<AssociationReferenceDescriptionRefSetMember> associationReferenceRefSetMembers =
      new HashSet<>();
  
  
  /**
   * Instantiates an empty {@link Description}.
   */
  public DescriptionJpa() {
    // empty
  }

  /**
   * Instantiates a {@link DescriptionJpa} from the specified parameters.
   *
   * @param description the description
   * @param cascadeCopy the cascade copy flag
   */
  public DescriptionJpa(Description description, boolean cascadeCopy) {
    super(description);
    caseSignificanceId = description.getCaseSignificanceId();
    concept = description.getConcept();
    languageCode = description.getLanguageCode();
    term = description.getTerm();
    typeId = description.getTypeId();
    workflowStatus = description.getWorkflowStatus();
    if (cascadeCopy) {
      languageRefSetMembers = new HashSet<>();
      for (LanguageRefSetMember member : description.getLanguageRefSetMembers()) {
        LanguageRefSetMember newMember = new LanguageRefSetMemberJpa(member);
        newMember.setDescription(this);
        languageRefSetMembers.add(newMember);
      }
      
      attributeValueRefSetMembers = new HashSet<>();
      for (AttributeValueDescriptionRefSetMember member : description.getAttributeValueRefSetMembers()) {
        AttributeValueDescriptionRefSetMember newMember = new AttributeValueDescriptionRefSetMemberJpa(member);
        newMember.setDescription(this);
        attributeValueRefSetMembers.add(newMember);
      }

      associationReferenceRefSetMembers = new HashSet<>();
      for (AssociationReferenceDescriptionRefSetMember member : description.getAssociationReferenceRefSetMembers()) {
        AssociationReferenceDescriptionRefSetMember newMember = new AssociationReferenceDescriptionRefSetMemberJpa(member);
        newMember.setDescription(this);
        associationReferenceRefSetMembers.add(newMember);
      }
      
    }
  }

  /**
   * Instantiates a {@link Description} from the specified parameters.
   * 
   * @param type the type
   */
  public DescriptionJpa(String type) {
    this.typeId = type;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Description#getWorkflowStatus()
   */
  @Override
  public String getWorkflowStatus() {
    return workflowStatus;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Description#setWorkflowStatus(java.lang.String)
   */
  @Override
  public void setWorkflowStatus(String workflowStatus) {
    this.workflowStatus = workflowStatus;
  }

  /**
   * Returns the language code.
   * 
   * @return the language code
   */
  @Override
  public String getLanguageCode() {
    return languageCode;
  }

  /**
   * Sets the language code.
   * 
   * @param languageCode the language code
   */
  @Override
  public void setLanguageCode(String languageCode) {
    this.languageCode = languageCode;
  }

  /**
   * Returns the type.
   * 
   * @return the type
   */
  @Override
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  public String getTypeId() {
    return typeId;
  }

  /**
   * Sets the type.
   * 
   * @param type the type
   */
  @Override
  public void setTypeId(String type) {
    this.typeId = type;
  }

  /**
   * Returns the term.
   * 
   * @return the term
   */
  @Override
  @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
  public String getTerm() {
    return term;
  }

  /**
   * Sets the term.
   * 
   * @param term the term
   */
  @Override
  public void setTerm(String term) {
    this.term = term;
  }

  /**
   * Returns the case significance id.
   * 
   * @return the case significance id
   */
  @Override
  public String getCaseSignificanceId() {
    return caseSignificanceId;
  }

  /**
   * Sets the case significance id.
   * 
   * @param caseSignificanceId the case significance id
   */
  @Override
  public void setCaseSignificanceId(String caseSignificanceId) {
    this.caseSignificanceId = caseSignificanceId;
  }

  /**
   * Returns the concept.
   * 
   * @return the concept
   */
  @XmlTransient
  @Override
  public Concept getConcept() {
    return this.concept;
  }

  /**
   * Sets the concept.
   * 
   * @param concept the concept
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

  /**
   * Returns the set of SimpleRefSetMembers
   * 
   * @return the set of SimpleRefSetMembers
   */
  @XmlElement(type = LanguageRefSetMemberJpa.class, name = "languageRefSetMember")
  @Override
  public Set<LanguageRefSetMember> getLanguageRefSetMembers() {
    return this.languageRefSetMembers;
  }

  /**
   * Sets the set of LanguageRefSetMembers
   * 
   * @param languageRefSetMembers the set of LanguageRefSetMembers
   */
  @Override
  public void setLanguageRefSetMembers(
    Set<LanguageRefSetMember> languageRefSetMembers) {
    this.languageRefSetMembers = languageRefSetMembers;
  }

  /**
   * Adds a LanguageRefSetMember to the set of LanguageRefSetMembers
   * 
   * @param languageRefSetMember the LanguageRefSetMembers to be added
   */
  @Override
  public void addLanguageRefSetMember(LanguageRefSetMember languageRefSetMember) {
    languageRefSetMember.setDescription(this);
    this.languageRefSetMembers.add(languageRefSetMember);
  }

  /**
   * Removes a LanguageRefSetMember from the set of LanguageRefSetMembers
   * 
   * @param languageRefSetMember the LanguageRefSetMember to be removed
   */
  @Override
  public void removeLanguageRefSetMember(
    LanguageRefSetMember languageRefSetMember) {
    this.languageRefSetMembers.remove(languageRefSetMember);
  }


  /**
   * Returns the set of AttributeValueRefSetMembers.
   *
   * @return the set of AttributeValueRefSetMembers
   */
  @XmlTransient
  @Override
  public Set<AttributeValueDescriptionRefSetMember> getAttributeValueRefSetMembers() {
    return this.attributeValueRefSetMembers;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.Description#setAttributeValueRefSetMembers(java.util.Set)
   */
  @Override
  public void setAttributeValueRefSetMembers(
    Set<AttributeValueDescriptionRefSetMember> attributeValueRefSetMembers) {
    this.attributeValueRefSetMembers = attributeValueRefSetMembers;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.Description#addAttributeValueRefSetMember(org.ihtsdo.
   * otf.ts.rf2.AttributeValueRefSetMember)
   */
  @Override
  public void addAttributeValueRefSetMember(
    AttributeValueDescriptionRefSetMember attributeValueRefSetMember) {
    attributeValueRefSetMember.setDescription(this);
    this.attributeValueRefSetMembers.add(attributeValueRefSetMember);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.Description#removeAttributeValueRefSetMember(org.ihtsdo
   * .otf.ts.rf2.AttributeValueRefSetMember)
   */
  @Override
  public void removeAttributeValueRefSetMember(
    AttributeValueDescriptionRefSetMember attributeValueRefSetMember) {
    this.attributeValueRefSetMembers.remove(attributeValueRefSetMember);
  }

  /**
   * Returns the set of AssociationReferenceRefSetMembers.
   *
   * @return the set of AssociationReferenceRefSetMembers
   */
  @XmlTransient
  @Override
  public Set<AssociationReferenceDescriptionRefSetMember> getAssociationReferenceRefSetMembers() {
    return this.associationReferenceRefSetMembers;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.Description#setAssociationReferenceRefSetMembers(java.util.Set)
   */
  @Override
  public void setAssociationReferenceRefSetMembers(
    Set<AssociationReferenceDescriptionRefSetMember> associationReferenceRefSetMembers) {
    this.associationReferenceRefSetMembers = associationReferenceRefSetMembers;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.Description#addAssociationReferenceRefSetMember(org.ihtsdo.
   * otf.ts.rf2.AssociationReferenceRefSetMember)
   */
  @Override
  public void addAssociationReferenceRefSetMember(
    AssociationReferenceDescriptionRefSetMember associationReferenceRefSetMember) {
    associationReferenceRefSetMember.setDescription(this);
    this.associationReferenceRefSetMembers.add(associationReferenceRefSetMember);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.Description#removeAssociationReferenceRefSetMember(org.ihtsdo
   * .otf.ts.rf2.AssociationReferenceRefSetMember)
   */
  @Override
  public void removeAssociationReferenceRefSetMember(
    AssociationReferenceDescriptionRefSetMember associationReferenceRefSetMember) {
    this.associationReferenceRefSetMembers.remove(associationReferenceRefSetMember);
  }  
    
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return super.toString()
        + (this.getConcept() == null ? null : getConcept().getTerminologyId())
        + "," + this.getLanguageCode() + "," + this.getTypeId() + ","
        + this.getTerm() + "," + this.getCaseSignificanceId();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result =
        prime
            * result
            + ((caseSignificanceId == null) ? 0 : caseSignificanceId.hashCode());
    result = prime * result + ((concept == null) ? 0 : concept.hashCode());
    result =
        prime * result + ((languageCode == null) ? 0 : languageCode.hashCode());
    result = prime * result + ((term == null) ? 0 : term.hashCode());
    result = prime * result + ((typeId == null) ? 0 : typeId.hashCode());
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
    DescriptionJpa other = (DescriptionJpa) obj;
    if (caseSignificanceId == null) {
      if (other.caseSignificanceId != null)
        return false;
    } else if (!caseSignificanceId.equals(other.caseSignificanceId))
      return false;
    if (concept == null) {
      if (other.concept != null)
        return false;
    } else if (!concept.equals(other.concept))
      return false;
    if (languageCode == null) {
      if (other.languageCode != null)
        return false;
    } else if (!languageCode.equals(other.languageCode))
      return false;
    if (term == null) {
      if (other.term != null)
        return false;
    } else if (!term.equals(other.term))
      return false;
    if (typeId == null) {
      if (other.typeId != null)
        return false;
    } else if (!typeId.equals(other.typeId))
      return false;
    return true;
  }

}
