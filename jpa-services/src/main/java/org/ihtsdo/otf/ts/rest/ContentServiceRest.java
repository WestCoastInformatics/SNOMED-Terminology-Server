/*
 * 
 */
package org.ihtsdo.otf.ts.rest;

import org.ihtsdo.otf.ts.Project;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.ProjectList;
import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;

// TODO: Auto-generated Javadoc
/**
 * Represents a content available via a REST service.
 *
 * @author ${author}
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
   * Returns the concept for user.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the concept for user
   * @throws Exception the exception
   */
  public Concept getConceptForUser(String terminologyId, String terminology,
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
   * Gets the concept for the specified identifier.
   *
   * @param id the internal concept id. Used when other REST APIs return
   *          information that includes internal identifiers.
   * @param authToken the auth token
   * @return the concept
   * @throws Exception the exception
   */
  public Concept getConcept(Long id, String authToken) throws Exception;

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
  public ConceptList getChildConcepts(String terminologyId, String terminology,
    String version, PfsParameterJpa pfs, String authToken) throws Exception;

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
  public ConceptList getDescendantConcepts(String terminologyId,
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
  public ConceptList getAncestorConcepts(String terminologyId,
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
   * Gets the description for the specified identifier.
   *
   * @param id the internal description id. Used when other REST APIs return
   *          information that includes internal identifiers.
   * @param authToken the auth token
   * @return the description
   * @throws Exception the exception
   */
  public Description getDescription(Long id, String authToken) throws Exception;

  /**
   * Returns the relationship for the specified parameters.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the relationship
   * @throws Exception the exception
   */
  public Relationship getRelationship(String terminologyId, String terminology,
    String version, String authToken) throws Exception;

  /**
   * Gets the relationship for the specified identifier.
   *
   * @param id the internal relationship id. Used when other REST APIs return
   *          information that includes internal identifiers.
   * @param authToken the auth token
   * @return the relationship
   * @throws Exception the exception
   */
  public Relationship getRelationship(Long id, String authToken)
    throws Exception;

  /**
   * Returns the language refset member for the specified parameters.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the language refset member
   * @throws Exception the exception
   */
  public LanguageRefSetMember getLanguageRefSetMember(String terminologyId,
    String terminology, String version, String authToken) throws Exception;

  /**
   * Gets the language refset member for the specified identifier.
   *
   * @param id the internal language refset member id. Used when other REST APIs
   *          return information that includes internal identifiers.
   * @param authToken the auth token
   * @return the language refset member
   * @throws Exception the exception
   */
  public LanguageRefSetMember getLanguageRefSetMember(Long id, String authToken)
    throws Exception;

  /**
   * Returns the association reference concept ref set member.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the association reference concept ref set member
   * @throws Exception the exception
   */
  public AssociationReferenceConceptRefSetMember getAssociationReferenceConceptRefSetMember(
    String terminologyId, String terminology, String version, String authToken)
    throws Exception;

  /**
   * Returns the association reference concept ref set member.
   *
   * @param id the id
   * @param authToken the auth token
   * @return the association reference concept ref set member
   * @throws Exception the exception
   */
  public AssociationReferenceConceptRefSetMember getAssociationReferenceConceptRefSetMember(
    Long id, String authToken) throws Exception;

  /**
   * Returns the concepts in scope.
   *
   * @param projectId the project id
   * @param authToken the auth token
   * @return the concepts in scope
   * @throws Exception the exception
   */
  public ConceptList getConceptsInScope(Long projectId, String authToken)
    throws Exception;

  /**
   * Returns the project.
   *
   * @param id the id
   * @param authToken the auth token
   * @return the project
   * @throws Exception the exception
   */
  public Project getProject(Long id, String authToken) throws Exception;

  /**
   * Returns the projects.
   *
   * @param authToken the auth token
   * @return the projects
   * @throws Exception the exception
   */
  public ProjectList getProjects(String authToken) throws Exception;

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
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void computeTransitiveClosure(String terminology, String authToken) throws Exception;

  /**
   * Load terminology snapshot from RF2 directory.
   *
   * @param terminology the terminology
   * @param terminologyVersion the terminology version
   * @param inputDir the input dir
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void loadTerminologyRf2Snapshot(String terminology,
    String terminologyVersion, String inputDir, String authToken)
    throws Exception;

  /**
   * Load terminology full from RF2 directory.
   *
   * @param terminology the terminology
   * @param terminologyVersion the terminology version
   * @param inputDir the input dir
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void loadTerminologyRf2Full(String terminology,
    String terminologyVersion, String inputDir, String authToken)
    throws Exception;

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
   * @param terminologyVersion the terminology version
   * @param inputFile the input file
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void loadTerminologyClaml(String terminology,
    String terminologyVersion, String inputFile, String authToken)
    throws Exception;

  /**
   * Removes the terminology.
   *
   * @param terminology the terminology
   * @param terminologyVersion the terminology version
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeTerminology(String terminology, String terminologyVersion,
    String authToken) throws Exception;

}
