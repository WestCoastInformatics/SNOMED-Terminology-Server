/*
 * 
 */
package org.ihtsdo.otf.ts.rest;

import org.ihtsdo.otf.ts.helpers.AssociationReferenceRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.AttributeValueRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ComplexMapRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.DescriptionTypeRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.LanguageRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ModuleDependencyRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.RefsetDescriptorRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.RelationshipList;
import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.helpers.SimpleMapRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.SimpleRefSetMemberList;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;

/**
 * Represents a content available via a REST service.
 */
public interface ContentServiceRest {

  /**
   * Returns the concept for search string.
   *
   * @param terminology the terminology
   * @param version the terminology version
   * @param searchString the lucene search string
   * @param pfs the paging, filtering, sorting parameter
   * @param authToken the auth token
   * @return the concept for id
   * @throws Exception if anything goes wrong
   */
  public SearchResultList findConceptsForQuery(String terminology,
    String version, String searchString, PfsParameterJpa pfs, String authToken)
    throws Exception;

  /**
   * Returns the concept for the specified parameters. As there may be multiple
   * simultaneous versions of the same concept this returns a list. The returned
   * concept(s) include descriptions language refsets and relationships.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the concepts
   * @throws Exception the exception
   */
  public ConceptList getConcepts(String terminologyId, String terminology,
    String version, String authToken) throws Exception;

  /**
   * Returns the single concept for the specified parameters. If there are more
   * than one, it throws an exception. The returned concept includes
   * descriptions language refsets and relationships.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the single concept
   * @throws Exception if there are more than one matching concepts.
   */
  public Concept getSingleConcept(String terminologyId, String terminology,
    String version, String authToken) throws Exception;

  /**
   * Returns the concept children.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the terminology version
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the concept children
   * @throws Exception the exception
   */
  public ConceptList findChildConcepts(String terminologyId,
    String terminology, String version, PfsParameterJpa pfs, String authToken)
    throws Exception;

  /**
   * Find parent concepts.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the concept list
   * @throws Exception the exception
   */
  public ConceptList findParentConcepts(String terminologyId,
    String terminology, String version, PfsParameterJpa pfs, String authToken)
    throws Exception;

  /**
   * Returns the concept descendants.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the terminology version
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the concept descendants
   * @throws Exception the exception
   */
  public ConceptList findDescendantConcepts(String terminologyId,
    String terminology, String version, PfsParameterJpa pfs, String authToken)
    throws Exception;

  /**
   * Returns the ancestor concepts.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the ancestor concepts
   * @throws Exception the exception
   */
  public ConceptList findAncestorConcepts(String terminologyId,
    String terminology, String version, PfsParameterJpa pfs, String authToken)
    throws Exception;

  /**
   * Returns the description for the specified parameters.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the description
   * @throws Exception the exception
   */
  public Description getDescription(String terminologyId, String terminology,
    String version, String authToken) throws Exception;

  /**
   * Find association reference ref set members.
   *
   * @param refSetId the ref set id
   * @param terminology the terminology
   * @param version the version
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the association reference ref set member list
   * @throws Exception the exception
   */
  public AssociationReferenceRefSetMemberList findAssociationReferenceRefSetMembers(
    String refSetId, String terminology, String version, PfsParameterJpa pfs,
    String authToken) throws Exception;

  /**
   * Find attribute value ref set members.
   *
   * @param refSetId the ref set id
   * @param terminology the terminology
   * @param version the version
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the attribute value ref set member list
   * @throws Exception the exception
   */
  public AttributeValueRefSetMemberList findAttributeValueRefSetMembers(
    String refSetId, String terminology, String version, PfsParameterJpa pfs,
    String authToken) throws Exception;

  /**
   * Find complex map ref set members.
   *
   * @param refSetId the ref set id
   * @param terminology the terminology
   * @param version the version
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the complex map ref set member list
   * @throws Exception the exception
   */
  public ComplexMapRefSetMemberList findComplexMapRefSetMembers(
    String refSetId, String terminology, String version, PfsParameterJpa pfs,
    String authToken) throws Exception;

  /**
   * Find description type ref set members.
   *
   * @param refSetId the ref set id
   * @param terminology the terminology
   * @param version the version
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the description type ref set member list
   * @throws Exception the exception
   */
  public DescriptionTypeRefSetMemberList findDescriptionTypeRefSetMembers(
    String refSetId, String terminology, String version, PfsParameterJpa pfs,
    String authToken) throws Exception;

  /**
   * Find language ref set members.
   *
   * @param refSetId the ref set id
   * @param terminology the terminology
   * @param version the version
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the language ref set member list
   * @throws Exception the exception
   */
  public LanguageRefSetMemberList findLanguageRefSetMembers(String refSetId,
    String terminology, String version, PfsParameterJpa pfs, String authToken)
    throws Exception;

  /**
   * Get module dependency ref set members for the specified module.
   *
   * @param moduleId the ref set id
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the module dependency ref set member list
   * @throws Exception the exception
   */
  public ModuleDependencyRefSetMemberList getModuleDependencyRefSetMembersForModule(
    String moduleId, String terminology, String version, String authToken)
    throws Exception;

  /**
   * Get refset descriptor ref set members.
   *
   * @param refSetId the ref set id
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the refset descriptor ref set member list
   * @throws Exception the exception
   */
  public RefsetDescriptorRefSetMemberList getRefsetDescriptorRefSetMembers(
    String refSetId, String terminology, String version, String authToken)
    throws Exception;

  /**
   * Find simple map ref set members.
   *
   * @param refSetId the ref set id
   * @param terminology the terminology
   * @param version the version
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the simple map ref set member list
   * @throws Exception the exception
   */
  public SimpleMapRefSetMemberList findSimpleMapRefSetMembers(String refSetId,
    String terminology, String version, PfsParameterJpa pfs, String authToken)
    throws Exception;

  /**
   * Find simple ref set members.
   *
   * @param refSetId the ref set id
   * @param terminology the terminology
   * @param version the version
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the simple ref set member list
   * @throws Exception the exception
   */
  public SimpleRefSetMemberList findSimpleRefSetMembers(String refSetId,
    String terminology, String version, PfsParameterJpa pfs, String authToken)
    throws Exception;

  /**
   * Returns the association reference ref set members for concept.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the association reference ref set members for concept
   * @throws Exception the exception
   */
  public AssociationReferenceRefSetMemberList getAssociationReferenceRefSetMembersForConcept(
    String terminologyId, String terminology, String version, String authToken)
    throws Exception;

  /**
   * Returns the association reference ref set members for description.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the association reference ref set members for description
   * @throws Exception the exception
   */
  public AssociationReferenceRefSetMemberList getAssociationReferenceRefSetMembersForDescription(
    String terminologyId, String terminology, String version, String authToken)
    throws Exception;

  /**
   * Returns the attribute value ref set members for concept.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the attribute value ref set members for concept
   * @throws Exception the exception
   */
  public AttributeValueRefSetMemberList getAttributeValueRefSetMembersForConcept(
    String terminologyId, String terminology, String version, String authToken)
    throws Exception;

  /**
   * Returns the attribute value ref set members for description.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the attribute value ref set members for description
   * @throws Exception the exception
   */
  public AttributeValueRefSetMemberList getAttributeValueRefSetMembersForDescription(
    String terminologyId, String terminology, String version, String authToken)
    throws Exception;

  /**
   * Returns the complex map ref set members for concept.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the complex map ref set members for concept
   * @throws Exception the exception
   */
  public ComplexMapRefSetMemberList getComplexMapRefSetMembersForConcept(
    String terminologyId, String terminology, String version, String authToken)
    throws Exception;

  /**
   * Returns the language ref set members for description.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the language ref set members for description
   * @throws Exception the exception
   */
  public LanguageRefSetMemberList getLanguageRefSetMembersForDescription(
    String terminologyId, String terminology, String version, String authToken)
    throws Exception;

  /**
   * Returns the simple map ref set members for concept.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the simple map ref set members for concept
   * @throws Exception the exception
   */
  public SimpleMapRefSetMemberList getSimpleMapRefSetMembersForConcept(
    String terminologyId, String terminology, String version, String authToken)
    throws Exception;

  /**
   * Returns the simple ref set members for concept.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the simple ref set members for concept
   * @throws Exception the exception
   */
  public SimpleRefSetMemberList getSimpleRefSetMembersForConcept(
    String terminologyId, String terminology, String version, String authToken)
    throws Exception;

  /**
   * Returns the inverse relationships for concept.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the inverse relationships for concept
   * @throws Exception the exception
   */
  public RelationshipList getInverseRelationshipsForConcept(
    String terminologyId, String terminology, String version, String authToken)
    throws Exception;

  /**
   * Recomputes lucene indexes for the specified objects as a comma-separated
   * string list.
   *
   * @param indexedObjects the indexed objects
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void luceneReindex(String indexedObjects, String authToken)
    throws Exception;

  /**
   * Compute transitive closure for latest version of a terminology.
   *
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void computeTransitiveClosure(String terminology, String version,
    String authToken) throws Exception;

  /**
   * Load terminology snapshot from RF2 directory.
   *
   * @param terminology the terminology
   * @param version the terminology version
   * @param inputDir the input dir
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void loadTerminologyRf2Snapshot(String terminology, String version,
    String inputDir, String authToken) throws Exception;

  /**
   * Load terminology full from RF2 directory.
   *
   * @param terminology the terminology
   * @param version the terminology version
   * @param inputDir the input dir
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void loadTerminologyRf2Full(String terminology, String version,
    String inputDir, String authToken) throws Exception;

  /**
   * Load terminology delta from RF2 directory.
   *
   * @param terminology the terminology
   * @param inputDir the input dir
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void loadTerminologyRf2Delta(String terminology, String inputDir,
    String authToken) throws Exception;

  /**
   * Load terminology from ClaML file.
   *
   * @param terminology the terminology
   * @param version the terminology version
   * @param inputFile the input file
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void loadTerminologyClaml(String terminology, String version,
    String inputFile, String authToken) throws Exception;

  /**
   * Removes the terminology.
   *
   * @param terminology the terminology
   * @param version the terminology version
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeTerminology(String terminology, String version,
    String authToken) throws Exception;

}
