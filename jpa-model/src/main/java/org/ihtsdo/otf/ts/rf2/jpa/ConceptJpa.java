package org.ihtsdo.otf.ts.rf2.jpa;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.StandardFilterFactory;
import org.apache.solr.analysis.StandardTokenizerFactory;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;
import org.ihtsdo.otf.ts.rf2.AttributeValueConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.ComplexMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.SimpleMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.SimpleRefSetMember;

/**
 * Jpa enabled implementation of {@link Concept}.
 */
@Entity
// @UniqueConstraint here is being used to create an index, not to enforce
// uniqueness
@Table(name = "concepts", uniqueConstraints = @UniqueConstraint(columnNames = {
    "terminologyId", "terminology", "terminologyVersion", "id"
}))
@Audited
@Indexed
@AnalyzerDef(name = "noStopWord", tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class), filters = {
    @TokenFilterDef(factory = StandardFilterFactory.class),
    @TokenFilterDef(factory = LowerCaseFilterFactory.class)
})
@XmlRootElement(name = "concept")
public class ConceptJpa extends AbstractComponent implements Concept {

  /** The workflow status. */
  @Column(nullable = true)
  private String workflowStatus;

  /** The definition status id. */
  @Column(nullable = false)
  private String definitionStatusId;

  /** The descriptions. */
  @OneToMany(mappedBy = "concept", cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = DescriptionJpa.class)
  @IndexedEmbedded(targetElement = DescriptionJpa.class)
  // PG
  private Set<Description> descriptions = new HashSet<>();

  /** The relationships. */
  @OneToMany(mappedBy = "sourceConcept", cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = RelationshipJpa.class)
  @IndexedEmbedded(targetElement = RelationshipJpa.class)
  private Set<Relationship> relationships = new HashSet<>();

  /** The inverse relationships. */
  @OneToMany(mappedBy = "destinationConcept", orphanRemoval = true, targetEntity = RelationshipJpa.class)
  private Set<Relationship> inverseRelationships = new HashSet<>();

  /** The simple RefSet members. */
  @OneToMany(mappedBy = "concept", cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = SimpleRefSetMemberJpa.class)
  private Set<SimpleRefSetMember> simpleRefSetMembers = new HashSet<>();

  /** The simpleMap RefSet members. */
  @OneToMany(mappedBy = "concept", cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = SimpleMapRefSetMemberJpa.class)
  private Set<SimpleMapRefSetMember> simpleMapRefSetMembers = new HashSet<>();

  /** The complexMap RefSet members. */
  @OneToMany(mappedBy = "concept", cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = ComplexMapRefSetMemberJpa.class)
  private Set<ComplexMapRefSetMember> complexMapRefSetMembers = new HashSet<>();

  /** The attributeValue RefSet members. */
  @OneToMany(mappedBy = "concept", cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = AttributeValueConceptRefSetMemberJpa.class)
  private Set<AttributeValueConceptRefSetMember> attributeValueRefSetMembers =
      new HashSet<>();

  /** The default preferred name. */
  @Column(nullable = false, length = 256)
  @Fields({
      @Field, @Field(name = "all", analyze = Analyze.YES, store = Store.NO)
  }) @Analyzer(definition = "noStopWord")
  private String defaultPreferredName;

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#getWorkflowStatus()
   */
  @Override
  public String getWorkflowStatus() {
    return workflowStatus;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#setWorkflowStatus(java.lang.String)
   */
  @Override
  public void setWorkflowStatus(String workflowStatus) {
    this.workflowStatus = workflowStatus;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#getDefinitionStatusId()
   */
  @Override
  public String getDefinitionStatusId() {
    return definitionStatusId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#setDefinitionStatusId(java.lang.String)
   */
  @Override
  public void setDefinitionStatusId(String definitionStatusId) {
    this.definitionStatusId = definitionStatusId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#getDescriptions()
   */
  @Override
  @XmlElement(type = DescriptionJpa.class, name = "description")
  public Set<Description> getDescriptions() {
    return descriptions;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#setDescriptions(java.util.Set)
   */
  @Override
  public void setDescriptions(Set<Description> descriptions) {
    this.descriptions = descriptions;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.Concept#addDescription(org.ihtsdo.otf.ts.rf2.Description
   * )
   */
  @Override
  public void addDescription(Description description) {
    description.setConcept(this);
    this.descriptions.add(description);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#removeDescription(org.ihtsdo.otf.ts.rf2.
   * Description)
   */
  @Override
  public void removeDescription(Description description) {
    this.descriptions.remove(description);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#getRelationships()
   */
  @Override
  @XmlElement(type = RelationshipJpa.class, name = "relationship")
  public Set<Relationship> getRelationships() {
    return relationships;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#setRelationships(java.util.Set)
   */
  @Override
  public void setRelationships(Set<Relationship> relationships) {
    this.relationships = relationships;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#getInverseRelationships()
   */
  @XmlTransient
  @Override
  public Set<Relationship> getInverseRelationships() {
    return inverseRelationships;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#setInverseRelationships(java.util.Set)
   */
  @Override
  public void setInverseRelationships(Set<Relationship> inverseRelationships) {
    this.inverseRelationships = inverseRelationships;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#getSimpleRefSetMembers()
   */
  @XmlTransient
  @Override
  public Set<SimpleRefSetMember> getSimpleRefSetMembers() {
    return this.simpleRefSetMembers;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#setSimpleRefSetMembers(java.util.Set)
   */
  @Override
  public void setSimpleRefSetMembers(Set<SimpleRefSetMember> simpleRefSetMembers) {
    this.simpleRefSetMembers = simpleRefSetMembers;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.Concept#addSimpleRefSetMember(org.ihtsdo.otf.ts.rf2
   * .SimpleRefSetMember)
   */
  @Override
  public void addSimpleRefSetMember(SimpleRefSetMember simpleRefSetMember) {
    simpleRefSetMember.setConcept(this);
    this.simpleRefSetMembers.add(simpleRefSetMember);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.Concept#removeSimpleRefSetMember(org.ihtsdo.otf.ts
   * .rf2.SimpleRefSetMember)
   */
  @Override
  public void removeSimpleRefSetMember(SimpleRefSetMember simpleRefSetMember) {
    this.simpleRefSetMembers.remove(simpleRefSetMember);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#getSimpleMapRefSetMembers()
   */
  @XmlTransient
  @Override
  public Set<SimpleMapRefSetMember> getSimpleMapRefSetMembers() {
    return this.simpleMapRefSetMembers;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#setSimpleMapRefSetMembers(java.util.Set)
   */
  @Override
  public void setSimpleMapRefSetMembers(
    Set<SimpleMapRefSetMember> simpleMapRefSetMembers) {
    this.simpleMapRefSetMembers = simpleMapRefSetMembers;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.Concept#addSimpleMapRefSetMember(org.ihtsdo.otf.ts
   * .rf2.SimpleMapRefSetMember)
   */
  @Override
  public void addSimpleMapRefSetMember(
    SimpleMapRefSetMember simpleMapRefSetMember) {
    simpleMapRefSetMember.setConcept(this);
    this.simpleMapRefSetMembers.add(simpleMapRefSetMember);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.Concept#removeSimpleMapRefSetMember(org.ihtsdo.otf
   * .ts.rf2.SimpleMapRefSetMember)
   */
  @Override
  public void removeSimpleMapRefSetMember(
    SimpleMapRefSetMember simpleMapRefSetMember) {
    this.simpleMapRefSetMembers.remove(simpleMapRefSetMember);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#getComplexMapRefSetMembers()
   */
  @XmlTransient
  @Override
  public Set<ComplexMapRefSetMember> getComplexMapRefSetMembers() {
    return this.complexMapRefSetMembers;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.Concept#setComplexMapRefSetMembers(java.util.Set)
   */
  @Override
  public void setComplexMapRefSetMembers(
    Set<ComplexMapRefSetMember> complexMapRefSetMembers) {
    this.complexMapRefSetMembers = complexMapRefSetMembers;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.Concept#addComplexMapRefSetMember(org.ihtsdo.otf.
   * ts.rf2.ComplexMapRefSetMember)
   */
  @Override
  public void addComplexMapRefSetMember(
    ComplexMapRefSetMember complexMapRefSetMember) {
    complexMapRefSetMember.setConcept(this);
    this.complexMapRefSetMembers.add(complexMapRefSetMember);
  }

  /**
   * Removes a ComplexMapRefSetMember from the set of ComplexMapRefSetMembers.
   *
   * @param complexMapRefSetMember the ComplexMapRefSetMember to be removed
   */
  @Override
  public void removeComplexMapRefSetMember(
    ComplexMapRefSetMember complexMapRefSetMember) {
    this.complexMapRefSetMembers.remove(complexMapRefSetMember);
  }

  /**
   * Returns the set of AttributeValueRefSetMembers.
   *
   * @return the set of AttributeValueRefSetMembers
   */
  @XmlTransient
  @Override
  public Set<AttributeValueConceptRefSetMember> getAttributeValueRefSetMembers() {
    return this.attributeValueRefSetMembers;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.Concept#setAttributeValueRefSetMembers(java.util.Set)
   */
  @Override
  public void setAttributeValueRefSetMembers(
    Set<AttributeValueConceptRefSetMember> attributeValueRefSetMembers) {
    this.attributeValueRefSetMembers = attributeValueRefSetMembers;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.Concept#addAttributeValueRefSetMember(org.ihtsdo.
   * otf.ts.rf2.AttributeValueRefSetMember)
   */
  @Override
  public void addAttributeValueRefSetMember(
    AttributeValueConceptRefSetMember attributeValueRefSetMember) {
    attributeValueRefSetMember.setConcept(this);
    this.attributeValueRefSetMembers.add(attributeValueRefSetMember);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.Concept#removeAttributeValueRefSetMember(org.ihtsdo
   * .otf.ts.rf2.AttributeValueRefSetMember)
   */
  @Override
  public void removeAttributeValueRefSetMember(
    AttributeValueConceptRefSetMember attributeValueRefSetMember) {
    this.attributeValueRefSetMembers.remove(attributeValueRefSetMember);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.jpa.AbstractComponent#getEffectiveTime()
   */
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.YES)
  @Override
  public Date getEffectiveTime() {
    return super.getEffectiveTime();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.jpa.AbstractComponent#getTerminology()
   */
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.YES)
  @Override
  public String getTerminology() {
    return super.getTerminology();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.jpa.AbstractComponent#getTerminologyVersion()
   */
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.YES)
  @Override
  public String getTerminologyVersion() {
    return super.getTerminologyVersion();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#getDefaultPreferredName()
   */
  @Override
  @Field(index = Index.YES, analyze = Analyze.YES, store = Store.YES)
  public String getDefaultPreferredName() {
    return defaultPreferredName;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.Concept#setDefaultPreferredName(java.lang.String)
   */
  @Override
  public void setDefaultPreferredName(String defaultPreferredName) {
    this.defaultPreferredName = defaultPreferredName;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.jpa.AbstractComponent#toString()
   */
  @Override
  public String toString() {

    return super.toString() + getDefinitionStatusId() + ","
        + getDefaultPreferredName();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    return true;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.rf2.jpa.AbstractComponent#hashCode()
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }
}