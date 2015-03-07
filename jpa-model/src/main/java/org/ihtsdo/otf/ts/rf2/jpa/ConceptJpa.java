package org.ihtsdo.otf.ts.rf2.jpa;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
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
import org.ihtsdo.otf.ts.rf2.AssociationReferenceConceptRefSetMember;
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

  /** The anonymous flag. */
  @Column(nullable = false)
  private boolean anonymous = false;

  /** The descriptions. */
  @OneToMany(mappedBy = "concept", cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = DescriptionJpa.class)
  @IndexedEmbedded(targetElement = DescriptionJpa.class)
  // PG
  private Set<Description> descriptions = null;

  /** The description count. */
  @Transient
  private int descriptionCount = -1;

  /** The relationships. */
  @OneToMany(mappedBy = "sourceConcept", cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = RelationshipJpa.class)
  @IndexedEmbedded(targetElement = RelationshipJpa.class)
  private Set<Relationship> relationships = null;

  /** The relationship count. */
  @Transient
  private int relationshipCount = -1;

  /** The child count. */
  @Transient
  private int childCount = -1;

  /** The inverse relationships. */
  @OneToMany(mappedBy = "destinationConcept", targetEntity = RelationshipJpa.class)
  private Set<Relationship> inverseRelationships = null;

  /** The simple RefSet members. */
  @OneToMany(mappedBy = "concept", targetEntity = SimpleRefSetMemberJpa.class)
  private Set<SimpleRefSetMember> simpleRefSetMembers = null;

  /** The simpleMap RefSet members. */
  @OneToMany(mappedBy = "concept", targetEntity = SimpleMapRefSetMemberJpa.class)
  private Set<SimpleMapRefSetMember> simpleMapRefSetMembers = null;

  /** The complexMap RefSet members. */
  @OneToMany(mappedBy = "concept", targetEntity = ComplexMapRefSetMemberJpa.class)
  private Set<ComplexMapRefSetMember> complexMapRefSetMembers = null;

  /** The attributeValue RefSet members. */
  @OneToMany(mappedBy = "concept", targetEntity = AttributeValueConceptRefSetMemberJpa.class)
  private Set<AttributeValueConceptRefSetMember> attributeValueRefSetMembers =
      null;

  /** The associationReference RefSet members. */
  @OneToMany(mappedBy = "concept", targetEntity = AssociationReferenceConceptRefSetMemberJpa.class)
  private Set<AssociationReferenceConceptRefSetMember> associationReferenceRefSetMembers =
      null;

  /** The default preferred name. */
  @Column(nullable = false, length = 256)
  private String defaultPreferredName;

  /**
   * Instantiates an empty {@link ConceptJpa}.
   */
  public ConceptJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link ConceptJpa} from the specified parameters.
   *
   * @param concept the concept
   * @param cascadeCopy the cascade copy flag
   * @param deepCopy the deep copy flag
   */
  public ConceptJpa(Concept concept, boolean cascadeCopy, boolean deepCopy) {
    super(concept);
    defaultPreferredName = concept.getDefaultPreferredName();
    definitionStatusId = concept.getDefinitionStatusId();
    workflowStatus = concept.getWorkflowStatus();

    if (cascadeCopy || deepCopy) {
      descriptions = new HashSet<>();
      for (Description description : concept.getDescriptions()) {
        Description newDescription =
            new DescriptionJpa(description, cascadeCopy, deepCopy);
        newDescription.setConcept(this);
        descriptions.add(newDescription);
      }
      relationships = new HashSet<>();
      for (Relationship rel : concept.getRelationships()) {
        Relationship newRel = new RelationshipJpa(rel);
        newRel.setSourceConcept(this);
        relationships.add(newRel);
      }
    }

    if (deepCopy) {

      attributeValueRefSetMembers = new HashSet<>();
      for (AttributeValueConceptRefSetMember member : concept
          .getAttributeValueRefSetMembers()) {
        AttributeValueConceptRefSetMember newMember =
            new AttributeValueConceptRefSetMemberJpa(member);
        newMember.setConcept(this);
        attributeValueRefSetMembers.add(newMember);
      }

      associationReferenceRefSetMembers = new HashSet<>();
      for (AssociationReferenceConceptRefSetMember member : concept
          .getAssociationReferenceRefSetMembers()) {
        AssociationReferenceConceptRefSetMember newMember =
            new AssociationReferenceConceptRefSetMemberJpa(member);
        newMember.setConcept(this);
        associationReferenceRefSetMembers.add(newMember);
      }

      complexMapRefSetMembers = new HashSet<>();
      for (ComplexMapRefSetMember member : concept.getComplexMapRefSetMembers()) {
        ComplexMapRefSetMember newMember =
            new ComplexMapRefSetMemberJpa(member);
        newMember.setConcept(this);
        complexMapRefSetMembers.add(newMember);
      }

      simpleMapRefSetMembers = new HashSet<>();
      for (SimpleMapRefSetMember member : concept.getSimpleMapRefSetMembers()) {
        SimpleMapRefSetMember newMember = new SimpleMapRefSetMemberJpa(member);
        newMember.setConcept(this);
        simpleMapRefSetMembers.add(newMember);
      }

      simpleRefSetMembers = new HashSet<>();
      for (SimpleRefSetMember member : concept.getSimpleRefSetMembers()) {
        SimpleRefSetMember newMember = new SimpleRefSetMemberJpa(member);
        newMember.setConcept(this);
        simpleRefSetMembers.add(newMember);
      }

    }
  }

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
   * @see org.ihtsdo.otf.ts.rf2.Concept#isAnonymous()
   */
  @Override
  public boolean isAnonymous() {
    return anonymous;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#setAnonymous(boolean)
   */
  @Override
  public void setAnonymous(boolean anonymous) {
    this.anonymous = anonymous;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#getDescriptions()
   */
  @Override
  @XmlElement(type = DescriptionJpa.class, name = "description")
  public Set<Description> getDescriptions() {
    if (descriptions == null) {
      descriptions = new HashSet<>();
    }
    return descriptions;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#getDescriptionCount()
   */
  @Override
  public int getDescriptionCount() {
    return descriptionCount;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#setDescriptionCount(int)
   */
  @Override
  public void setDescriptionCount(int ct) {
    this.descriptionCount = ct;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#setDescriptions(java.util.Set)
   */
  @Override
  public void setDescriptions(Set<Description> descriptions) {
    if (descriptions != null) {
      this.descriptions = new HashSet<>();
      for (Description description : descriptions) {
        description.setConcept(this);
      }
      this.descriptions.addAll(descriptions);
    }
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
    if (descriptions == null) {
      descriptions = new HashSet<>();
    }
    description.setConcept(this);
    descriptions.add(description);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#removeDescription(org.ihtsdo.otf.ts.rf2.
   * Description)
   */
  @Override
  public void removeDescription(Description description) {
    if (descriptions == null) {
      return;
    }
    descriptions.remove(description);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#getRelationships()
   */
  @Override
  @XmlElement(type = RelationshipJpa.class, name = "relationship")
  public Set<Relationship> getRelationships() {
    if (relationships == null) {
      relationships = new HashSet<>();
    }
    return relationships;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#getRelationshipCount()
   */
  @Override
  public int getRelationshipCount() {
    return relationshipCount;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#setRelationshipCount(int)
   */
  @Override
  public void setRelationshipCount(int ct) {
    this.relationshipCount = ct;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#getChildCount()
   */
  @Override
  public int getChildCount() {
    return childCount;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#setChildCount(int)
   */
  @Override
  public void setChildCount(int ct) {
    this.childCount = ct;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#addRelationship(org.ihtsdo.otf.ts.rf2.
   * Relationship)
   */
  @Override
  public void addRelationship(Relationship relationship) {
    if (relationships == null) {
      relationships = new HashSet<>();
    }
    relationship.setSourceConcept(this);
    relationships.add(relationship);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.Concept#removeRelationship(org.ihtsdo.otf.ts.rf2.
   * Relationship)
   */
  @Override
  public void removeRelationship(Relationship relationship) {
    if (relationships == null) {
      return;
    }
    relationships.remove(relationship);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#setRelationships(java.util.Set)
   */
  @Override
  public void setRelationships(Set<Relationship> relationships) {
    if (relationships != null) {
      this.relationships = new HashSet<>();
      for (Relationship relationship : relationships) {
        relationship.setSourceConcept(this);
      }
      this.relationships.addAll(relationships);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#getSimpleRefSetMembers()
   */
  @XmlTransient
  @Override
  public Set<SimpleRefSetMember> getSimpleRefSetMembers() {
    if (simpleRefSetMembers == null) {
      simpleRefSetMembers = new HashSet<>();
    }
    return simpleRefSetMembers;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#setSimpleRefSetMembers(java.util.Set)
   */
  @Override
  public void setSimpleRefSetMembers(Set<SimpleRefSetMember> simpleRefSetMembers) {
    if (simpleRefSetMembers != null) {
      this.simpleRefSetMembers = new HashSet<>();
      for (SimpleRefSetMember member : simpleRefSetMembers) {
        member.setConcept(this);
      }
      this.simpleRefSetMembers.addAll(simpleRefSetMembers);
    }
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
    if (simpleRefSetMembers == null) {
      simpleRefSetMembers = new HashSet<>();
    }
    simpleRefSetMember.setConcept(this);
    simpleRefSetMembers.add(simpleRefSetMember);
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
    if (simpleRefSetMembers == null) {
      return;
    }
    simpleRefSetMembers.remove(simpleRefSetMember);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#getSimpleMapRefSetMembers()
   */
  @XmlTransient
  @Override
  public Set<SimpleMapRefSetMember> getSimpleMapRefSetMembers() {
    if (simpleMapRefSetMembers == null) {
      simpleMapRefSetMembers = new HashSet<>();
    }
    return simpleMapRefSetMembers;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#setSimpleMapRefSetMembers(java.util.Set)
   */
  @Override
  public void setSimpleMapRefSetMembers(
    Set<SimpleMapRefSetMember> simpleMapRefSetMembers) {
    if (simpleMapRefSetMembers != null) {
      this.simpleMapRefSetMembers = new HashSet<>();
      for (SimpleMapRefSetMember member : simpleMapRefSetMembers) {
        member.setConcept(this);
      }
      this.simpleMapRefSetMembers.addAll(simpleMapRefSetMembers);
    }
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
    if (simpleMapRefSetMembers == null) {
      simpleMapRefSetMembers = new HashSet<>();
    }
    simpleMapRefSetMember.setConcept(this);
    simpleMapRefSetMembers.add(simpleMapRefSetMember);
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
    if (simpleMapRefSetMembers == null) {
      return;
    }
    simpleMapRefSetMembers.remove(simpleMapRefSetMember);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.Concept#getComplexMapRefSetMembers()
   */
  @XmlTransient
  @Override
  public Set<ComplexMapRefSetMember> getComplexMapRefSetMembers() {
    if (complexMapRefSetMembers == null) {
      complexMapRefSetMembers = new HashSet<>();
    }
    return complexMapRefSetMembers;
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
    if (complexMapRefSetMembers != null) {
      this.complexMapRefSetMembers = new HashSet<>();
      for (ComplexMapRefSetMember member : complexMapRefSetMembers) {
        member.setConcept(this);
      }
      this.complexMapRefSetMembers.addAll(complexMapRefSetMembers);
    }
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
    if (complexMapRefSetMembers == null) {
      complexMapRefSetMembers = new HashSet<>();
    }
    complexMapRefSetMember.setConcept(this);
    complexMapRefSetMembers.add(complexMapRefSetMember);
  }

  /**
   * Removes a ComplexMapRefSetMember from the set of ComplexMapRefSetMembers.
   *
   * @param complexMapRefSetMember the ComplexMapRefSetMember to be removed
   */
  @Override
  public void removeComplexMapRefSetMember(
    ComplexMapRefSetMember complexMapRefSetMember) {
    if (complexMapRefSetMembers == null) {
      return;
    }
    complexMapRefSetMembers.remove(complexMapRefSetMember);
  }

  /**
   * Returns the set of AttributeValueRefSetMembers.
   *
   * @return the set of AttributeValueRefSetMembers
   */
  @XmlTransient
  @Override
  public Set<AttributeValueConceptRefSetMember> getAttributeValueRefSetMembers() {
    if (attributeValueRefSetMembers == null) {
      attributeValueRefSetMembers = new HashSet<>();
    }
    return attributeValueRefSetMembers;
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
    if (attributeValueRefSetMembers != null) {
      this.attributeValueRefSetMembers = new HashSet<>();
      for (AttributeValueConceptRefSetMember member : attributeValueRefSetMembers) {
        member.setConcept(this);
      }
      this.attributeValueRefSetMembers.addAll(attributeValueRefSetMembers);
    }
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
    if (attributeValueRefSetMembers == null) {
      attributeValueRefSetMembers = new HashSet<>();
    }
    attributeValueRefSetMember.setConcept(this);
    attributeValueRefSetMembers.add(attributeValueRefSetMember);
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
    if (attributeValueRefSetMembers == null) {
      return;
    }
    attributeValueRefSetMembers.remove(attributeValueRefSetMember);
  }

  /**
   * Returns the set of AssociationReferenceRefSetMembers.
   *
   * @return the set of AssociationReferenceRefSetMembers
   */
  @XmlTransient
  @Override
  public Set<AssociationReferenceConceptRefSetMember> getAssociationReferenceRefSetMembers() {
    if (associationReferenceRefSetMembers == null) {
      associationReferenceRefSetMembers = new HashSet<>();
    }
    return associationReferenceRefSetMembers;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.Concept#setAssociationReferenceRefSetMembers(java
   * .util.Set)
   */
  @Override
  public void setAssociationReferenceRefSetMembers(
    Set<AssociationReferenceConceptRefSetMember> associationReferenceRefSetMembers) {
    if (associationReferenceRefSetMembers != null) {
      this.associationReferenceRefSetMembers = new HashSet<>();
      for (AssociationReferenceConceptRefSetMember member : associationReferenceRefSetMembers) {
        member.setConcept(this);
      }
      this.associationReferenceRefSetMembers
          .addAll(associationReferenceRefSetMembers);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.Concept#addAssociationReferenceRefSetMember(org.ihtsdo
   * . otf.ts.rf2.AssociationReferenceRefSetMember)
   */
  @Override
  public void addAssociationReferenceRefSetMember(
    AssociationReferenceConceptRefSetMember associationReferenceRefSetMember) {
    if (associationReferenceRefSetMembers == null) {
      associationReferenceRefSetMembers = new HashSet<>();
    }
    associationReferenceRefSetMember.setConcept(this);
    associationReferenceRefSetMembers.add(associationReferenceRefSetMember);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rf2.Concept#removeAssociationReferenceRefSetMember(org
   * .ihtsdo .otf.ts.rf2.AssociationReferenceRefSetMember)
   */
  @Override
  public void removeAssociationReferenceRefSetMember(
    AssociationReferenceConceptRefSetMember associationReferenceRefSetMember) {
    if (associationReferenceRefSetMembers == null) {
      return;
    }
    associationReferenceRefSetMembers.remove(associationReferenceRefSetMember);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.jpa.AbstractComponent#getEffectiveTime()
   */
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  @Override
  public Date getEffectiveTime() {
    return super.getEffectiveTime();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.jpa.AbstractComponent#getTerminology()
   */
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  @Override
  public String getTerminology() {
    return super.getTerminology();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rf2.jpa.AbstractComponent#getTerminologyVersion()
   */
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
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
  @Fields({
      @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO),
      @Field(name = "all", index = Index.YES, analyze = Analyze.YES, store = Store.NO),
      @Field(name = "defaultPreferredNameSort", index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  })
  @Analyzer(definition = "noStopWord")
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
    return super.toString() + ", " + getDefinitionStatusId() + ","
        + getDefaultPreferredName();
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
    ConceptJpa other = (ConceptJpa) obj;
    if (definitionStatusId == null) {
      if (other.definitionStatusId != null)
        return false;
    } else if (!definitionStatusId.equals(other.definitionStatusId))
      return false;
    return true;
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
            + ((definitionStatusId == null) ? 0 : definitionStatusId.hashCode());
    return result;
  }
}