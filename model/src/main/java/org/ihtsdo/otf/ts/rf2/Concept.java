package org.ihtsdo.otf.ts.rf2;

import java.util.Set;

/**
 * Represents a concept in a terminology.
 */
public interface Concept extends Component {

  /**
   * Indicates whether or not the concept is anonymous.
   *
   * @return the is anonymous
   */
  public boolean isAnonymous();
  
  /**
   * Sets the anonymous flag.
   *
   * @param anonymous the new is anonymous
   */
  public void setAnonymous(boolean anonymous);
  
  /**
   * Returns the workflow status.
   * 
   * @return the workflow status
   */
  public String getWorkflowStatus();

  /**
   * Sets the workflow status.
   * 
   * @param workflowStatus the workflow status
   */
  public void setWorkflowStatus(String workflowStatus);

  /**
   * Returns the definition status id.
   * 
   * @return definitionStatusId the definition status id
   */
  public String getDefinitionStatusId();

  /**
   * Sets the definition status id.
   * 
   * @param definitionStatusId the definition status id
   */
  public void setDefinitionStatusId(String definitionStatusId);

  /**
   * Returns the descriptions.
   * 
   * @return the descriptions
   */
  public Set<Description> getDescriptions();

  /**
   * Returns the description count.
   *
   * @return the description count
   */
  public int getDescriptionCount();
  
  /**
   * Sets the description count.
   *
   * @param ct the description count
   */
  public void setDescriptionCount(int ct);

  /**
   * Sets the descriptions.
   * 
   * @param descriptions the descriptions
   */
  public void setDescriptions(Set<Description> descriptions);

  /**
   * Adds the description.
   * 
   * @param description the description
   */
  public void addDescription(Description description);

  /**
   * Removes the description.
   * 
   * @param description the description
   */
  public void removeDescription(Description description);

  /**
   * Returns the relationships.
   * 
   * @return the relationships
   */
  public Set<Relationship> getRelationships();

  /**
   * Returns the relationship count.
   *
   * @return the relationship count
   */
  public int getRelationshipCount();
  
  /**
   * Sets the relationship count.
   *
   * @param ct the relationship count
   */
  public void setRelationshipCount(int ct);

  /**
   * Returns the child count.
   *
   * @return the child count
   */
  public int getChildCount();
  
  /**
   * Sets the child count.
   *
   * @param ct the child count
   */
  public void setChildCount(int ct);

  /**
   * Adds the relationship.
   * 
   * @param relationship the relationship
   */
  public void addRelationship(Relationship relationship);

  /**
   * Removes the relationship.
   * 
   * @param relationship the relationship
   */
  public void removeRelationship(Relationship relationship);

  /**
   * Sets the relationships.
   * 
   * @param relationships the relationships
   */
  public void setRelationships(Set<Relationship> relationships);

  /**
   * Gets the default preferred name.
   * 
   * @return the default preferred name
   */
  public String getDefaultPreferredName();

  /**
   * Sets the default preferred name.
   * 
   * @param defaultPreferredName the new default preferred name
   */
  public void setDefaultPreferredName(String defaultPreferredName);

  /**
   * Returns the set of SimpleRefSetMembers
   * 
   * @return the set of SimpleRefSetMembers
   */
  public Set<SimpleRefSetMember> getSimpleRefSetMembers();

  /**
   * Sets the set of SimpleRefSetMembers
   * 
   * @param simpleRefSetMembers the set of SimpleRefSetMembers
   */
  public void setSimpleRefSetMembers(Set<SimpleRefSetMember> simpleRefSetMembers);

  /**
   * Adds a SimpleRefSetMember to the set of SimpleRefSetMembers
   * 
   * @param simpleRefSetMember the SimpleRefSetMembers to be added
   */
  public void addSimpleRefSetMember(SimpleRefSetMember simpleRefSetMember);

  /**
   * Removes a SimpleRefSetMember from the set of SimpleRefSetMembers
   * 
   * @param simpleRefSetMember the SimpleRefSetMember to be removed
   */
  public void removeSimpleRefSetMember(SimpleRefSetMember simpleRefSetMember);

  /**
   * Returns the set of SimpleMapRefSetMembers
   * 
   * @return the set of SimpleMapRefSetMembers
   */
  public Set<SimpleMapRefSetMember> getSimpleMapRefSetMembers();

  /**
   * Sets the set of SimpleMapRefSetMembers
   * 
   * @param simpleMapRefSetMembers the set of SimpleMapRefSetMembers
   */
  public void setSimpleMapRefSetMembers(
    Set<SimpleMapRefSetMember> simpleMapRefSetMembers);

  /**
   * Adds a SimpleMapRefSetMember to the set of SimpleMapRefSetMembers
   * 
   * @param simpleMapRefSetMember the SimpleMapRefSetMembers to be added
   */
  public void addSimpleMapRefSetMember(
    SimpleMapRefSetMember simpleMapRefSetMember);

  /**
   * Removes a SimpleMapRefSetMember from the set of SimpleMapRefSetMembers
   * 
   * @param simpleMapRefSetMember the SimpleMapRefSetMember to be removed
   */
  public void removeSimpleMapRefSetMember(
    SimpleMapRefSetMember simpleMapRefSetMember);

  /**
   * Returns the set of ComplexMapRefSetMembers
   * 
   * @return the set of ComplexMapRefSetMembers
   */
  public Set<ComplexMapRefSetMember> getComplexMapRefSetMembers();

  /**
   * Sets the set of ComplexMapRefSetMembers
   * 
   * @param complexMapRefSetMembers the set of ComplexMapRefSetMembers
   */
  public void setComplexMapRefSetMembers(
    Set<ComplexMapRefSetMember> complexMapRefSetMembers);

  /**
   * Adds a ComplexMapRefSetMember to the set of ComplexMapRefSetMembers
   * 
   * @param complexMapRefSetMember the ComplexMapRefSetMembers to be added
   */
  public void addComplexMapRefSetMember(
    ComplexMapRefSetMember complexMapRefSetMember);

  /**
   * Removes a ComplexMapRefSetMember from the set of ComplexMapRefSetMembers
   * 
   * @param complexMapRefSetMember the ComplexMapRefSetMember to be removed
   */
  public void removeComplexMapRefSetMember(
    ComplexMapRefSetMember complexMapRefSetMember);

  /**
   * Returns the set of AttributeValueRefSetMembers
   * 
   * @return the set of AttributeValueRefSetMembers
   */
  public Set<AttributeValueConceptRefSetMember> getAttributeValueRefSetMembers();

  /**
   * Sets the set of AttributeValueRefSetMembers
   * 
   * @param attributeValueRefSetMembers the set of AttributeValueRefSetMembers
   */
  public void setAttributeValueRefSetMembers(
    Set<AttributeValueConceptRefSetMember> attributeValueRefSetMembers);

  /**
   * Adds a AttributeValueRefSetMember to the set of AttributeValueRefSetMembers
   * 
   * @param attributeValueRefSetMember the AttributeValueRefSetMembers to be
   *          added
   */
  public void addAttributeValueRefSetMember(
    AttributeValueConceptRefSetMember attributeValueRefSetMember);

  /**
   * Removes a AttributeValueRefSetMember from the set of
   * AttributeValueRefSetMembers
   * 
   * @param attributeValueRefSetMember the AttributeValueRefSetMember to be
   *          removed
   */
  public void removeAttributeValueRefSetMember(
    AttributeValueConceptRefSetMember attributeValueRefSetMember);

  
  /**
   * Returns the set of AssociationReferenceRefSetMembers
   * 
   * @return the set of AssociationReferenceRefSetMembers
   */
  public Set<AssociationReferenceConceptRefSetMember> getAssociationReferenceRefSetMembers();

  /**
   * Sets the set of AssociationReferenceRefSetMembers
   * 
   * @param associationReferenceRefSetMembers the set of AssociationReferenceRefSetMembers
   */
  public void setAssociationReferenceRefSetMembers(
    Set<AssociationReferenceConceptRefSetMember> associationReferenceRefSetMembers);

  /**
   * Adds a AssociationReferenceRefSetMember to the set of AssociationReferenceRefSetMembers
   * 
   * @param associationReferenceRefSetMember the AssociationReferenceRefSetMembers to be
   *          added
   */
  public void addAssociationReferenceRefSetMember(
    AssociationReferenceConceptRefSetMember associationReferenceRefSetMember);

  /**
   * Removes a AssociationReferenceRefSetMember from the set of
   * AssociationReferenceRefSetMembers
   * 
   * @param associationReferenceRefSetMember the AssociationReferenceRefSetMember to be
   *          removed
   */
  public void removeAssociationReferenceRefSetMember(
    AssociationReferenceConceptRefSetMember associationReferenceRefSetMember);  
}
