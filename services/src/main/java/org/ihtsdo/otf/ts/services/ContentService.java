package org.ihtsdo.otf.ts.services;

import java.util.Set;

import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.PfsParameter;
import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.rf2.AttributeValueRefSetMember;
import org.ihtsdo.otf.ts.rf2.ComplexMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.Component;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.SimpleMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.SimpleRefSetMember;
import org.ihtsdo.otf.ts.rf2.TransitiveRelationship;

/**
 * Generically represents a service for accessing content.
 */
public interface ContentService extends RootService {

  /**
   * Gets all concepts.
   * @param terminology the terminology
   * @param version the terminology version
   * @param pfs the paging, filtering, sorting parameter
   *
   * @return the concepts
   * @throws Exception the exception
   */
  public ConceptList getConcepts(String terminology, String version, PfsParameter pfs) throws Exception;

  /**
   * Returns the concept.
   * 
   * @param id the id
   * @return the concept
   * @throws Exception if anything goes wrong
   */
  public Concept getConcept(Long id) throws Exception;

  /**
   * Returns the concept matching the specified parameters.
   * May return more than one concept if there are multiple entries
   * with the same id, terminology, and version.
   * NOTE: this only applies to concept, not to other data types.
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
   * Returns the single concept for the specified parameters.  If there
   * are more than one it throws an exception.
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
   * Returns the description.
   * 
   * @param id the id
   * @return the description
   * @throws Exception if anything goes wrong
   */
  public Description getDescription(String id) throws Exception;

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
  public Relationship getRelationship(String id) throws Exception;

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
  public LanguageRefSetMember getLanguageRefSetMember(
    String id) throws Exception;

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
   * Adds the language refset member.
   * 
   * @param languageRefSetMember the language refset member
   * @return the languageRefSetMember
   * @throws Exception the exception
   */
  public LanguageRefSetMember addLanguageRefSetMember(
    LanguageRefSetMember languageRefSetMember) throws Exception;

  /**
   * Update language refset member.
   * 
   * @param languageRefSetMember the language refset member
   * @throws Exception the exception
   */
  public void updateLanguageRefSetMember(
    LanguageRefSetMember languageRefSetMember) throws Exception;

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
    String id) throws Exception;

  /**
   * Returns the attribute value refset member matching the specified parameters.
   * 
   * @param terminologyId the id
   * @param terminology the terminology
   * @param version the version
   * @return the attribute value refset member
   * @throws Exception if anything goes wrong
   */
  public AttributeValueRefSetMember<? extends Component> getAttributeValueRefSetMember(
    String terminologyId, String terminology, String version)
    throws Exception;

  /**
   * Adds the attribute value refset member.
   * 
   * @param attributeValueRefSetMember the attribute value  refset member
   * @return the attribute value refset member
   * @throws Exception the exception
   */
  public AttributeValueRefSetMember<? extends Component> addAttributeValueRefSetMember(
    AttributeValueRefSetMember<? extends Component> attributeValueRefSetMember) throws Exception;

  /**
   * Update attribute value refset member.
   * 
   * @param attributeValueRefSetMember the attribute value refset member
   * @throws Exception the exception
   */
  public void updateAttributeValueRefSetMember(
    AttributeValueRefSetMember<? extends Component> attributeValueRefSetMember) throws Exception;

  /**
   * Removes the attribute value refset member.
   * 
   * @param id the id
   * @throws Exception the exception
   */
  public void removeAttributeValueRefSetMember(Long id) throws Exception;

  /**
   * Returns the complex map refset member.
   * 
   * @param id the id
   * @return the complex map refset member
   * @throws Exception if anything goes wrong
   */
  public ComplexMapRefSetMember getComplexMapRefSetMember(
    String id) throws Exception;

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
   * Adds the complex map refset member.
   * 
   * @param complexMapRefSetMember the complex map refset member
   * @return the complex map refset member
   * @throws Exception the exception
   */
  public ComplexMapRefSetMember addComplexMapRefSetMember(
    ComplexMapRefSetMember complexMapRefSetMember) throws Exception;

  /**
   * Update complex map refset member.
   * 
   * @param complexMapRefSetMember the complex map refset member
   * @throws Exception the exception
   */
  public void updateComplexMapRefSetMember(
    ComplexMapRefSetMember complexMapRefSetMember) throws Exception;

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
  public SimpleMapRefSetMember getSimpleMapRefSetMember(
    String id) throws Exception;

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
   * Adds the simple map refset member.
   * 
   * @param simpleMapRefSetMember the simple map refset member
   * @return the simple map refset member
   * @throws Exception the exception
   */
  public SimpleMapRefSetMember addSimpleMapRefSetMember(
    SimpleMapRefSetMember simpleMapRefSetMember) throws Exception;

  /**
   * Update simple map refset member.
   * 
   * @param simpleMapRefSetMember the simple map refset member
   * @throws Exception the exception
   */
  public void updateSimpleMapRefSetMember(
    SimpleMapRefSetMember simpleMapRefSetMember) throws Exception;

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
  public SimpleRefSetMember getSimpleRefSetMember(String id)
    throws Exception;

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
   * Adds the simple refset member.
   * 
   * @param simpleRefSetMember the simple refset member
   * @return the simpleRefSetMember
   * @throws Exception the exception
   */
  public SimpleRefSetMember addSimpleRefSetMember(
    SimpleRefSetMember simpleRefSetMember) throws Exception;

  /**
   * Update simple refset member.
   * 
   * @param simpleRefSetMember the simple refset member
   * @throws Exception the exception
   */
  public void updateSimpleRefSetMember(SimpleRefSetMember simpleRefSetMember)
    throws Exception;

  /**
   * Removes the simple refset member.
   * 
   * @param id the id
   * @throws Exception the exception
   */
  public void removeSimpleRefSetMember(Long id) throws Exception;

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
  public SearchResultList findConceptsForQuery(String terminology, String version, String query,
    PfsParameter pfs) throws Exception;

  /**
   * Finds the descendants of a concept, subject to max results limitation in
   * PFS parameters object.
   * 
   * @param terminologyId the id
   * @param terminology the terminology
   * @param version the terminology version
   * @param pfs the pfs parameter containing the max results
   *          restriction
   * @return the set of concepts
   * @throws Exception the exception
   */
  public SearchResultList findDescendantConcepts(String terminologyId,
    String terminology, String version, PfsParameter pfs)
    throws Exception;

  /**
   * Finds the ancestor of a concept, subject to max results limitation in
   * PFS parameters object.
   * 
   * @param terminologyId the id
   * @param terminology the terminology
   * @param version the terminology version
   * @param pfs the pfs parameter containing the max results
   *          restriction
   * @return the set of concepts
   * @throws Exception the exception
   */
  public SearchResultList findAncestorConcepts(String terminologyId,
    String terminology, String version, PfsParameter pfs)
    throws Exception;

  /**
   * Gets the all concepts.
   *
   * @param terminology the terminology
   * @param version the terminology version
   * @return the all concepts
   */
  public ConceptList getAllConcepts(String terminology,
    String version);

  /**
   * Gets the all relationship ids.
   *
   * @param terminology the terminology
   * @param version the terminology version
   * @return the all relationship ids
   */
  public Set<String> getAllRelationshipTerminologyIds(String terminology,
    String version);

  /**
   * Gets the all description ids.
   *
   * @param terminology the terminology
   * @param version the terminology version
   * @return the all description ids
   */
  public Set<String> getAllDescriptionTerminologyIds(String terminology,
    String version);

  /**
   * Gets the all language ref set member ids.
   *
   * @param terminology the terminology
   * @param version the terminology version
   * @return the all language ref set member ids
   */
  public Set<String> getAllLanguageRefSetMemberTerminologyIds(
    String terminology, String version);

  /**
   * Clear transitive closure.
   *
   * @param terminology the terminology
   * @param version the terminology version
   * @throws Exception the exception
   */
  public void clearTransitiveClosure(String terminology,
    String version) throws Exception;

  /**
   * Removes all concepts and connected data structures
   *
   * @param terminology the terminology
   * @param version the terminology version
   */
  public void clearConcepts(String terminology, String version);
}