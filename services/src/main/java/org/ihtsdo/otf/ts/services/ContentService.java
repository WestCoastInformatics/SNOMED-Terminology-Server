/*
 * 
 */
package org.ihtsdo.otf.ts.services;

import org.ihtsdo.otf.ts.Project;
import org.ihtsdo.otf.ts.UserRole;
import org.ihtsdo.otf.ts.helpers.AssociationReferenceRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.AttributeValueRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ComplexMapRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.LanguageRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ModuleDependencyRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.PfsParameter;
import org.ihtsdo.otf.ts.helpers.ProjectList;
import org.ihtsdo.otf.ts.helpers.RefsetDescriptorRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.SearchCriteriaList;
import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.helpers.SimpleMapRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.SimpleRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.StringList;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceRefSetMember;
import org.ihtsdo.otf.ts.rf2.AttributeValueRefSetMember;
import org.ihtsdo.otf.ts.rf2.ComplexMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.Component;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.DescriptionTypeRefSetMember;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.ModuleDependencyRefSetMember;
import org.ihtsdo.otf.ts.rf2.RefsetDescriptorRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.SimpleMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.SimpleRefSetMember;
import org.ihtsdo.otf.ts.rf2.TransitiveRelationship;
import org.ihtsdo.otf.ts.services.handlers.ComputePreferredNameHandler;
import org.ihtsdo.otf.ts.services.handlers.GraphResolutionHandler;
import org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler;

/**
 * Generically represents a service for accessing content.
 */
public interface ContentService extends RootService {

  /**
   * Enable listeners.
   */
  public void enableListeners();

  /**
   * Disable listeners.
   */
  public void disableListeners();

  /**
   * Gets all concepts.
   * @param terminology the terminology
   * @param version the terminology version
   * @param pfs the paging, filtering, sorting parameter
   *
   * @return the concepts
   * @throws Exception the exception
   */
  public ConceptList getConcepts(String terminology, String version,
    PfsParameter pfs) throws Exception;

  /**
   * Returns the concept.
   * 
   * @param id the id
   * @return the concept
   * @throws Exception if anything goes wrong
   */
  public Concept getConcept(Long id) throws Exception;

  /**
   * Returns the concept matching the specified parameters. May return more than
   * one concept if there are multiple entries with the same id, terminology,
   * and version. NOTE: this only applies to concept, not to other data types.
   * 
   * @param terminologyId the id
   * @param terminology the terminology
   * @param version the version
   * @return the concept
   * @throws Exception if anything goes wrong
   */
  public ConceptList getConcepts(String terminologyId, String terminology,
    String version) throws Exception;

  /**
   * Returns the single concept for the specified parameters. If there are more
   * than one it throws an exception.
   *
   * @param terminologyId the id
   * @param terminology the terminology
   * @param version the terminology version
   * @return the single concept
   * @throws Exception if there are more than one matching concepts.
   */
  public Concept getSingleConcept(String terminologyId, String terminology,
    String version) throws Exception;

  /**
   * Adds the concept.
   * 
   * @param concept the concept
   * @return the concept
   * @throws Exception the exception
   */
  public Concept addConcept(Concept concept) throws Exception;

  /**
   * Update concept.
   * 
   * @param concept the concept
   * @throws Exception the exception
   */
  public void updateConcept(Concept concept) throws Exception;

  /**
   * Removes the concept.
   * 
   * @param id the id
   * @throws Exception the exception
   */
  public void removeConcept(Long id) throws Exception;

  /**
   * Get descendant concepts.
   *
   * @param concept the concept
   * @param pfsParameter the pfs parameter
   * @return the concept list
   * @throws Exception the exception
   */
  public ConceptList getDescendantConcepts(Concept concept,
    PfsParameter pfsParameter) throws Exception;

  /**
   * Get ancestor concepts.
   *
   * @param concept the concept
   * @param pfsParameter the pfs parameter
   * @return the concept list
   * @throws Exception the exception
   */
  public ConceptList getAncestorConcepts(Concept concept,
    PfsParameter pfsParameter) throws Exception;

  /**
   * Get child concepts.
   *
   * @param concept the concept
   * @param pfs the pfs
   * @return the concept list
   * @throws Exception the exception
   */
  public ConceptList getChildConcepts(Concept concept, PfsParameter pfs)
    throws Exception;

  /**
   * Returns the description.
   * 
   * @param id the id
   * @return the description
   * @throws Exception if anything goes wrong
   */
  public Description getDescription(Long id) throws Exception;

  /**
   * Returns the description matching the specified parameters.
   * 
   * @param terminologyId the id
   * @param terminology the terminology
   * @param version the version
   * @return the description
   * @throws Exception if anything goes wrong
   */
  public Description getDescription(String terminologyId, String terminology,
    String version) throws Exception;

  /**
   * Adds the description.
   * 
   * @param description the description
   * @return the description
   * @throws Exception the exception
   */
  public Description addDescription(Description description) throws Exception;

  /**
   * Update description.
   * 
   * @param description the description
   * @throws Exception the exception
   */
  public void updateDescription(Description description) throws Exception;

  /**
   * Removes the description.
   * 
   * @param id the id
   * @throws Exception the exception
   */
  public void removeDescription(Long id) throws Exception;

  /**
   * Returns the relationship.
   * 
   * @param id the id
   * @return the relationship
   * @throws Exception if anything goes wrong
   */
  public Relationship getRelationship(Long id) throws Exception;

  /**
   * Returns the relationship matching the specified parameters.
   * 
   * @param terminologyId the id
   * @param terminology the terminology
   * @param version the version
   * @return the relationship
   * @throws Exception if anything goes wrong
   */
  public Relationship getRelationship(String terminologyId, String terminology,
    String version) throws Exception;

  /**
   * Adds the relationship.
   * 
   * @param relationship the relationship
   * @return the relationship
   * @throws Exception the exception
   */
  public Relationship addRelationship(Relationship relationship)
    throws Exception;

  /**
   * Update relationship.
   * 
   * @param relationship the relationship
   * @throws Exception the exception
   */
  public void updateRelationship(Relationship relationship) throws Exception;

  /**
   * Removes the relationship.
   * 
   * @param id the id
   * @throws Exception the exception
   */
  public void removeRelationship(Long id) throws Exception;

  /**
   * Adds the transitive relationship.
   * 
   * @param transitiveRelationship the transitive relationship
   * @return the transitive relationship
   * @throws Exception the exception
   */
  public TransitiveRelationship addTransitiveRelationship(
    TransitiveRelationship transitiveRelationship) throws Exception;

  /**
   * Update transitive relationship.
   * 
   * @param transitiveRelationship the transitive relationship
   * @throws Exception the exception
   */
  public void updateTransitiveRelationship(
    TransitiveRelationship transitiveRelationship) throws Exception;

  /**
   * Removes the transitive relationship.
   * 
   * @param id the id
   * @throws Exception the exception
   */
  public void removeTransitiveRelationship(Long id) throws Exception;

  /**
   * Returns the language refset member.
   * 
   * @param id the id
   * @return the language refset member
   * @throws Exception if anything goes wrong
   */
  public LanguageRefSetMember getLanguageRefSetMember(Long id) throws Exception;

  /**
   * Returns the language refset member matching the specified parameters.
   * 
   * @param terminologyId the id
   * @param terminology the terminology
   * @param version the version
   * @return the languageRefSetMember
   * @throws Exception if anything goes wrong
   */
  public LanguageRefSetMember getLanguageRefSetMember(String terminologyId,
    String terminology, String version) throws Exception;

  /**
   * Returns the language ref set members for the specified refset id.
   *
   * @param refsetId the refset id
   * @param terminology the terminology
   * @param version the version
   * @param pfs the pfs
   * @return the language ref set members
   * @throws Exception the exception
   */
  public LanguageRefSetMemberList findLanguageRefSetMembers(String refsetId,
    String terminology, String version, PfsParameter pfs) throws Exception;

  /**
   * Adds the language refset member.
   * 
   * @param member the language refset member
   * @return the languageRefSetMember
   * @throws Exception the exception
   */
  public LanguageRefSetMember addLanguageRefSetMember(
    LanguageRefSetMember member) throws Exception;

  /**
   * Update language refset member.
   * 
   * @param member the language refset member
   * @throws Exception the exception
   */
  public void updateLanguageRefSetMember(LanguageRefSetMember member)
    throws Exception;

  /**
   * Removes the language refset member.
   * 
   * @param id the id
   * @throws Exception the exception
   */
  public void removeLanguageRefSetMember(Long id) throws Exception;

  /**
   * Returns the attribute value refset member.
   * 
   * @param id the id
   * @return the attribute value refset member
   * @throws Exception if anything goes wrong
   */
  public AttributeValueRefSetMember<? extends Component> getAttributeValueRefSetMember(
    Long id) throws Exception;

  /**
   * Returns the attribute value refset member matching the specified
   * parameters.
   * 
   * @param terminologyId the id
   * @param terminology the terminology
   * @param version the version
   * @return the attribute value refset member
   * @throws Exception if anything goes wrong
   */
  public AttributeValueRefSetMember<? extends Component> getAttributeValueRefSetMember(
    String terminologyId, String terminology, String version) throws Exception;

  /**
   * Returns the attribute value ref set members for the specified refset id.
   *
   * @param refsetId the refset id
   * @param terminology the terminology
   * @param version the version
   * @param pfs the pfs
   * @return the attribute value ref set members
   * @throws Exception the exception
   */
  public AttributeValueRefSetMemberList findAttributeValueRefSetMembers(
    String refsetId, String terminology, String version, PfsParameter pfs)
    throws Exception;

  /**
   * Adds the attribute value refset member.
   * 
   * @param member the attribute value refset member
   * @return the attribute value refset member
   * @throws Exception the exception
   */
  public AttributeValueRefSetMember<? extends Component> addAttributeValueRefSetMember(
    AttributeValueRefSetMember<? extends Component> member) throws Exception;

  /**
   * Update attribute value refset member.
   * 
   * @param member the attribute value refset member
   * @throws Exception the exception
   */
  public void updateAttributeValueRefSetMember(
    AttributeValueRefSetMember<? extends Component> member) throws Exception;

  /**
   * Removes the attribute value refset member.
   * 
   * @param id the id
   * @throws Exception the exception
   */
  public void removeAttributeValueRefSetMember(Long id) throws Exception;

  /**
   * Returns the association reference refset member.
   * 
   * @param id the id
   * @return the association reference refset member
   * @throws Exception if anything goes wrong
   */
  public AssociationReferenceRefSetMember<? extends Component> getAssociationReferenceRefSetMember(
    Long id) throws Exception;

  /**
   * Returns the association reference refset member matching the specified
   * parameters.
   * 
   * @param terminologyId the id
   * @param terminology the terminology
   * @param version the version
   * @return the association reference refset member
   * @throws Exception if anything goes wrong
   */
  public AssociationReferenceRefSetMember<? extends Component> getAssociationReferenceRefSetMember(
    String terminologyId, String terminology, String version) throws Exception;

  /**
   * Returns the association reference ref set members for the specified refset
   * id.
   *
   * @param refsetId the refset id
   * @param terminology the terminology
   * @param version the version
   * @param pfs the pfs
   * @return the association reference ref set members
   * @throws Exception the exception
   */
  public AssociationReferenceRefSetMemberList findAssociationReferenceRefSetMembers(
    String refsetId, String terminology, String version, PfsParameter pfs)
    throws Exception;

  /**
   * Adds the association reference refset member.
   * 
   * @param member the association reference refset member
   * @return the attribute value refset member
   * @throws Exception the exception
   */
  public AssociationReferenceRefSetMember<? extends Component> addAssociationReferenceRefSetMember(
    AssociationReferenceRefSetMember<? extends Component> member)
    throws Exception;

  /**
   * Update association reference refset member.
   * 
   * @param member the association reference refset member
   * @throws Exception the exception
   */
  public void updateAssociationReferenceRefSetMember(
    AssociationReferenceRefSetMember<? extends Component> member)
    throws Exception;

  /**
   * Removes the association reference refset member.
   * 
   * @param id the id
   * @throws Exception the exception
   */
  public void removeAssociationReferenceRefSetMember(Long id) throws Exception;

  /**
   * Returns the complex map refset member.
   * 
   * @param id the id
   * @return the complex map refset member
   * @throws Exception if anything goes wrong
   */
  public ComplexMapRefSetMember getComplexMapRefSetMember(Long id)
    throws Exception;

  /**
   * Returns the complex map refset member matching the specified parameters.
   * 
   * @param terminologyId the id
   * @param terminology the terminology
   * @param version the version
   * @return the complex map refset member
   * @throws Exception if anything goes wrong
   */
  public ComplexMapRefSetMember getComplexMapRefSetMember(String terminologyId,
    String terminology, String version) throws Exception;

  /**
   * Returns the complex map ref set members for the specified refset id.
   *
   * @param refsetId the refset id
   * @param terminology the terminology
   * @param version the version
   * @param pfs the pfs
   * @return the complex map ref set members
   * @throws Exception the exception
   */
  public ComplexMapRefSetMemberList findComplexMapRefSetMembers(
    String refsetId, String terminology, String version, PfsParameter pfs)
    throws Exception;

  /**
   * Adds the complex map refset member.
   * 
   * @param member the complex map refset member
   * @return the complex map refset member
   * @throws Exception the exception
   */
  public ComplexMapRefSetMember addComplexMapRefSetMember(
    ComplexMapRefSetMember member) throws Exception;

  /**
   * Update complex map refset member.
   * 
   * @param member the complex map refset member
   * @throws Exception the exception
   */
  public void updateComplexMapRefSetMember(ComplexMapRefSetMember member)
    throws Exception;

  /**
   * Removes the complex map refset member.
   * 
   * @param id the id
   * @throws Exception the exception
   */
  public void removeComplexMapRefSetMember(Long id) throws Exception;

  /**
   * Returns the simple map refset member.
   * 
   * @param id the id
   * @return the simple map refset member
   * @throws Exception if anything goes wrong
   */
  public SimpleMapRefSetMember getSimpleMapRefSetMember(Long id)
    throws Exception;

  /**
   * Returns the simple map refset member matching the specified parameters.
   * 
   * @param terminologyId the id
   * @param terminology the terminology
   * @param version the version
   * @return the simple map refset member
   * @throws Exception if anything goes wrong
   */
  public SimpleMapRefSetMember getSimpleMapRefSetMember(String terminologyId,
    String terminology, String version) throws Exception;

  /**
   * Returns the simple map ref set members for the specified refset id.
   *
   * @param refsetId the refset id
   * @param terminology the terminology
   * @param version the version
   * @param pfs the pfs
   * @return the simple map ref set members
   * @throws Exception the exception
   */
  public SimpleMapRefSetMemberList findSimpleMapRefSetMembers(String refsetId,
    String terminology, String version, PfsParameter pfs) throws Exception;

  /**
   * Adds the simple map refset member.
   * 
   * @param member the simple map refset member
   * @return the simple map refset member
   * @throws Exception the exception
   */
  public SimpleMapRefSetMember addSimpleMapRefSetMember(
    SimpleMapRefSetMember member) throws Exception;

  /**
   * Update simple map refset member.
   * 
   * @param member the simple map refset member
   * @throws Exception the exception
   */
  public void updateSimpleMapRefSetMember(SimpleMapRefSetMember member)
    throws Exception;

  /**
   * Removes the simple map refset member.
   * 
   * @param id the id
   * @throws Exception the exception
   */
  public void removeSimpleMapRefSetMember(Long id) throws Exception;

  /**
   * Returns the simple refset member.
   * 
   * @param id the id
   * @return the simple refset member
   * @throws Exception if anything goes wrong
   */
  public SimpleRefSetMember getSimpleRefSetMember(Long id) throws Exception;

  /**
   * Returns the simple refset member matching the specified parameters.
   * 
   * @param terminologyId the id
   * @param terminology the terminology
   * @param version the version
   * @return the simple refset member
   * @throws Exception if anything goes wrong
   */
  public SimpleRefSetMember getSimpleRefSetMember(String terminologyId,
    String terminology, String version) throws Exception;

  /**
   * Returns the simple ref set members for the specified refset id.
   *
   * @param refsetId the refset id
   * @param terminology the terminology
   * @param version the version
   * @param pfs the pfs
   * @return the simple ref set members
   * @throws Exception the exception
   */
  public SimpleRefSetMemberList findSimpleRefSetMembers(String refsetId,
    String terminology, String version, PfsParameter pfs) throws Exception;

  /**
   * Adds the simple refset member.
   * 
   * @param member the simple refset member
   * @return the simple refset member
   * @throws Exception the exception
   */
  public SimpleRefSetMember addSimpleRefSetMember(SimpleRefSetMember member)
    throws Exception;

  /**
   * Update simple refset member.
   * 
   * @param member the simple refset member
   * @throws Exception the exception
   */
  public void updateSimpleRefSetMember(SimpleRefSetMember member)
    throws Exception;

  /**
   * Removes the simple refset member.
   * 
   * @param id the id
   * @throws Exception the exception
   */
  public void removeSimpleRefSetMember(Long id) throws Exception;

  /**
   * Returns the refset descriptor refset member.
   * 
   * @param id the id
   * @return the refset descriptor refset member
   * @throws Exception if anything goes wrong
   */
  public RefsetDescriptorRefSetMember getRefsetDescriptorRefSetMember(Long id)
    throws Exception;

  /**
   * Returns the refset descriptor refset member matching the specified
   * parameters.
   * 
   * @param terminologyId the id
   * @param terminology the terminology
   * @param version the version
   * @return the refset descriptor refset member
   * @throws Exception if anything goes wrong
   */
  public RefsetDescriptorRefSetMember getRefsetDescriptorRefSetMember(
    String terminologyId, String terminology, String version) throws Exception;

  /**
   * Returns the refset descriptor ref set members for refset.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @return the refset descriptor ref set members for refset
   */
  public RefsetDescriptorRefSetMemberList getRefsetDescriptorRefSetMembersForRefset(
    String terminologyId, String terminology, String version);

  /**
   * Adds the refset descriptor refset member.
   * 
   * @param member the refset descriptor refset member
   * @return the refset descriptor refset member
   * @throws Exception the exception
   */
  public RefsetDescriptorRefSetMember addRefsetDescriptorRefSetMember(
    RefsetDescriptorRefSetMember member) throws Exception;

  /**
   * Update refset descriptor refset member.
   * 
   * @param member the refset descriptor refset member
   * @throws Exception the exception
   */
  public void updateRefsetDescriptorRefSetMember(
    RefsetDescriptorRefSetMember member) throws Exception;

  /**
   * Removes the refset descriptor refset member.
   * 
   * @param id the id
   * @throws Exception the exception
   */
  public void removeRefsetDescriptorRefSetMember(Long id) throws Exception;

  /**
   * Returns the description type refset member.
   * 
   * @param id the id
   * @return the description type refset member
   * @throws Exception if anything goes wrong
   */
  public DescriptionTypeRefSetMember getDescriptionTypeRefSetMember(Long id)
    throws Exception;

  /**
   * Returns the description type refset member matching the specified
   * parameters.
   * 
   * @param terminologyId the id
   * @param terminology the terminology
   * @param version the version
   * @return the description type refset member
   * @throws Exception if anything goes wrong
   */
  public DescriptionTypeRefSetMember getDescriptionTypeRefSetMember(
    String terminologyId, String terminology, String version) throws Exception;

  /**
   * Returns the description type ref set member for description type.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @return the description type ref set member for description type
   * @throws Exception the exception
   */
  public DescriptionTypeRefSetMember getDescriptionTypeRefSetMemberForDescriptionType(
    String terminologyId, String terminology, String version) throws Exception;

  /**
   * Adds the description type refset member.
   * 
   * @param member the description type refset member
   * @return the description type refset member
   * @throws Exception the exception
   */
  public DescriptionTypeRefSetMember addDescriptionTypeRefSetMember(
    DescriptionTypeRefSetMember member) throws Exception;

  /**
   * Update description type refset member.
   * 
   * @param member the description type refset member
   * @throws Exception the exception
   */
  public void updateDescriptionTypeRefSetMember(
    DescriptionTypeRefSetMember member) throws Exception;

  /**
   * Removes the description type refset member.
   * 
   * @param id the id
   * @throws Exception the exception
   */
  public void removeDescriptionTypeRefSetMember(Long id) throws Exception;

  /**
   * Returns the module dependency refset member.
   * 
   * @param id the id
   * @return the module dependency refset member
   * @throws Exception if anything goes wrong
   */
  public ModuleDependencyRefSetMember getModuleDependencyRefSetMember(Long id)
    throws Exception;

  /**
   * Returns the module dependency refset member matching the specified
   * parameters.
   * 
   * @param terminologyId the id
   * @param terminology the terminology
   * @param version the version
   * @return the module dependency refset member
   * @throws Exception if anything goes wrong
   */
  public ModuleDependencyRefSetMember getModuleDependencyRefSetMember(
    String terminologyId, String terminology, String version) throws Exception;

  /**
   * Returns the module dependency ref set members for module.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @return the module dependency ref set members for module
   * @throws Exception the exception
   */
  public ModuleDependencyRefSetMemberList getModuleDependencyRefSetMembersForModule(
    String terminologyId, String terminology, String version) throws Exception;

  /**
   * Adds the module dependency refset member.
   * 
   * @param member the module dependency refset member
   * @return the module dependency refset member
   * @throws Exception the exception
   */
  public ModuleDependencyRefSetMember addModuleDependencyRefSetMember(
    ModuleDependencyRefSetMember member) throws Exception;

  /**
   * Update module dependency refset member.
   * 
   * @param member the module dependency refset member
   * @throws Exception the exception
   */
  public void updateModuleDependencyRefSetMember(
    ModuleDependencyRefSetMember member) throws Exception;

  /**
   * Removes the module dependency refset member.
   * 
   * @param id the id
   * @throws Exception the exception
   */
  public void removeModuleDependencyRefSetMember(Long id) throws Exception;

  /**
   * Returns the concept search results matching the query. Results can be
   * paged, filtered, and sorted.
   * @param terminology the terminology
   * @param version the version
   * @param query the search string
   * @param pfs the paging, filtering, sorting parameter
   * @return the search results for the search string
   * @throws Exception if anything goes wrong
   */
  public SearchResultList findConceptsForQuery(String terminology,
    String version, String query, PfsParameter pfs) throws Exception;

  /**
   * Find concepts for search criteria.
   *
   * @param terminology the terminology
   * @param version the version
   * @param query the query
   * @param criteria the criteria
   * @param pfs the pfs
   * @return the search result list
   * @throws Exception the exception
   */
  public SearchResultList findConceptsForSearchCriteria(String terminology,
    String version, String query, SearchCriteriaList criteria, PfsParameter pfs)
    throws Exception;

  /**
   * Gets the all concepts.
   *
   * @param terminology the terminology
   * @param version the terminology version
   * @return the all concepts
   */
  public ConceptList getAllConcepts(String terminology, String version);

  /**
   * Gets the all relationship ids.
   *
   * @param terminology the terminology
   * @param version the terminology version
   * @return the all relationship ids
   */
  public StringList getAllRelationshipTerminologyIds(String terminology,
    String version);

  /**
   * Gets the all description ids.
   *
   * @param terminology the terminology
   * @param version the terminology version
   * @return the all description ids
   */
  public StringList getAllDescriptionTerminologyIds(String terminology,
    String version);

  /**
   * Gets the all language ref set member ids.
   *
   * @param terminology the terminology
   * @param version the terminology version
   * @return the all language ref set member ids
   */
  public StringList getAllLanguageRefSetMemberTerminologyIds(
    String terminology, String version);

  /**
   * Clear transitive closure.
   *
   * @param terminology the terminology
   * @param version the terminology version
   * @throws Exception the exception
   */
  public void clearTransitiveClosure(String terminology, String version)
    throws Exception;

  /**
   * Removes all concepts and connected data structures.
   *
   * @param terminology the terminology
   * @param version the terminology version
   */
  public void clearConcepts(String terminology, String version);

  /**
   * Returns the graph resolution handler. This is configured internally but
   * made available through this service.
   *
   * @return the graph resolution handler
   * @throws Exception the exception
   */
  public GraphResolutionHandler getGraphResolutionHandler() throws Exception;

  /**
   * Returns the identifier assignment handler.
   *
   * @param terminology the terminology
   * @return the identifier assignment handler
   * @throws Exception the exception
   */
  public IdentifierAssignmentHandler getIdentifierAssignmentHandler(
    String terminology) throws Exception;

  /**
   * Returns the compute preferred name handler.
   *
   * @param terminology the terminology
   * @return the compute preferred name handler
   * @throws Exception the exception
   */
  public ComputePreferredNameHandler getComputePreferredNameHandler(
    String terminology) throws Exception;

  /**
   * Returns the computed preferred name.
   *
   * @param concept the concept
   * @return the computed preferred name
   * @throws Exception the exception
   */
  public String getComputedPreferredName(Concept concept) throws Exception;

  /**
   * Indicates whether or not to assign last modified when changing terminology
   * components. Supports a loader that wants to disable this feature.
   *
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  public boolean isLastModifiedFlag();

  /**
   * Sets the last modified flag.
   *
   * @param lastModifiedFlag the last modified flag
   */
  public void setLastModifiedFlag(boolean lastModifiedFlag);

  /**
   * Returns the concepts in scope.
   *
   * @param project the project
   * @return the concepts in scope
   * @throws Exception the exception
   */
  public ConceptList getConceptsInScope(Project project) throws Exception;

  /**
   * Returns the project.
   *
   * @param id the id
   * @return the project
   */
  public Project getProject(Long id);

  /**
   * Adds the project.
   *
   * @param project the project
   * @return the project
   */
  public Project addProject(Project project);

  /**
   * Update project.
   *
   * @param project the project
   */
  public void updateProject(Project project);

  /**
   * Removes the project.
   *
   * @param projectId the project id
   */
  public void removeProject(Long projectId);

  /**
   * Returns the projects.
   *
   * @return the projects
   */
  public ProjectList getProjects();

  /**
   * Returns the user role for project.
   *
   * @param username the username
   * @param projectId the project id
   * @return the user role for project
   * @throws Exception the exception
   */
  public UserRole getUserRoleForProject(String username, Long projectId)
    throws Exception;

  
  
  
}