package org.ihtsdo.otf.ts.jpa.services;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.persistence.NoResultException;

import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.util.Version;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ConceptListJpa;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.LocalException;
import org.ihtsdo.otf.ts.helpers.PfsParameter;
import org.ihtsdo.otf.ts.helpers.ReleaseInfo;
import org.ihtsdo.otf.ts.helpers.SearchResult;
import org.ihtsdo.otf.ts.helpers.SearchResultJpa;
import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.helpers.SearchResultListJpa;
import org.ihtsdo.otf.ts.jpa.services.helper.TerminologyUtility;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceRefSetMember;
import org.ihtsdo.otf.ts.rf2.AttributeValueConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.AttributeValueRefSetMember;
import org.ihtsdo.otf.ts.rf2.ComplexMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.Component;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.DescriptionTypeRefSetMember;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.ModuleDependencyRefSetMember;
import org.ihtsdo.otf.ts.rf2.RefSetMember;
import org.ihtsdo.otf.ts.rf2.RefsetDescriptorRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.SimpleMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.SimpleRefSetMember;
import org.ihtsdo.otf.ts.rf2.TransitiveRelationship;
import org.ihtsdo.otf.ts.rf2.jpa.AbstractAssociationReferenceRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.AssociationReferenceConceptRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.AttributeValueConceptRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ComplexMapRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionTypeRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.LanguageRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ModuleDependencyRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RefsetDescriptorRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RelationshipJpa;
import org.ihtsdo.otf.ts.rf2.jpa.SimpleMapRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.SimpleRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.TransitiveRelationshipJpa;
import org.ihtsdo.otf.ts.services.ContentService;
import org.ihtsdo.otf.ts.services.handlers.ComputePreferredNameHandler;
import org.ihtsdo.otf.ts.services.handlers.GraphResolutionHandler;
import org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler;
import org.ihtsdo.otf.ts.services.handlers.WorkflowListener;

/**
 * JPA enabled implementation of {@link ContentService}.
 */
public class ContentServiceJpa extends RootServiceJpa implements ContentService {

  /** The listeners enabled. */
  private boolean listenersEnabled = true;

  /** The listener. */
  private static List<WorkflowListener> listeners = null;
  static {
    listeners = new ArrayList<>();
    Properties config;
    try {
      config = ConfigUtility.getConfigProperties();
      String key = "workflow.listener.handler";
      for (String handlerName : config.getProperty(key).split(",")) {
        if (handlerName.isEmpty())
          continue;
        // Add handlers to map
        WorkflowListener handlerService =
            ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
                handlerName, WorkflowListener.class);
        listeners.add(handlerService);
      }
    } catch (Exception e) {
      e.printStackTrace();
      listeners = null;
    }
  }

  /** The graph resolver. */
  public static GraphResolutionHandler graphResolver = null;
  static {
    Properties config;
    try {
      config = ConfigUtility.getConfigProperties();
      String key = "graph.resolution.handler";
      String handlerName = config.getProperty(key);
      if (handlerName == null || handlerName.isEmpty()) {
        throw new Exception("Undefined graph resolution handler");
      }
      // Set handler up
      GraphResolutionHandler handler =
          ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
              handlerName, GraphResolutionHandler.class);
      graphResolver = handler;

    } catch (Exception e) {
      e.printStackTrace();
      graphResolver = null;
    }
  }

  /** The id assignment handler . */
  public static IdentifierAssignmentHandler idHandler = null;
  static {
    Properties config;
    try {
      config = ConfigUtility.getConfigProperties();
      String key = "identifier.assignment.handler";
      String handlerName = config.getProperty(key);
      if (handlerName == null || handlerName.isEmpty()) {
        throw new Exception("Undefined identifier assignment handler");
      }
      // Set handler up
      IdentifierAssignmentHandler handler =
          ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
              handlerName, IdentifierAssignmentHandler.class);
      idHandler = handler;

    } catch (Exception e) {
      e.printStackTrace();
      idHandler = null;
    }
  }

  /** The helper map. */
  private static Map<String, ComputePreferredNameHandler> pnHandlerMap = null;
  static {
    pnHandlerMap = new HashMap<>();
    Properties config;
    try {
      config = ConfigUtility.getConfigProperties();
      String key = "compute.preferred.name.handler";
      for (String handlerName : config.getProperty(key).split(",")) {

        // Add handlers to map
        ComputePreferredNameHandler handlerService =
            ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
                handlerName, ComputePreferredNameHandler.class);
        pnHandlerMap.put(handlerName, handlerService);
      }
    } catch (Exception e) {
      e.printStackTrace();
      pnHandlerMap = null;
    }
  }

  /** The planned effective time. */
  public static Date plannedEffectiveTime = null;

  /**
   * Instantiates an empty {@link ContentServiceJpa}.
   *
   * @throws Exception the exception
   */
  public ContentServiceJpa() throws Exception {
    super();
    if (listeners == null) {
      throw new Exception(
          "Listeners did not properly initialize, serious error.");
    }
    if (graphResolver == null) {
      throw new Exception(
          "Graph resolver did not properly initialize, serious error.");
    }

    if (idHandler == null) {
      throw new Exception(
          "Identifier assignment handler did not properly initialize, serious error.");
    }

    if (pnHandlerMap == null) {
      throw new Exception(
          "Identifier compute preferred name handler did not properly initialize, serious error.");
    }
    if (plannedEffectiveTime == null) {
      plannedEffectiveTime = getPlannedEffectiveTime();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.jpa.services.RootServiceJpa#beginTransaction()
   */
  @Override
  public void beginTransaction() throws Exception {
    super.beginTransaction();
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.beginTransaction();
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.jpa.services.RootServiceJpa#commit()
   */
  @Override
  public void commit() throws Exception {
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.preCommit();
      }
    }
    super.commit();
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.postCommit();
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getConcepts(java.lang.String,
   * java.lang.String, org.ihtsdo.otf.ts.helpers.PfsParameter)
   */
  @SuppressWarnings("unchecked")
  @Override
  public ConceptList getConcepts(String terminology, String version,
    PfsParameter pfs) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get concepts " + terminology + "/" + version);

    // Prepare results
    ConceptList results = new ConceptListJpa();

    // Prepare the query
    StringBuilder finalQuery = new StringBuilder();
    finalQuery.append("terminology:" + terminology + " AND terminologyVersion:"
        + version);
    if (pfs != null && pfs.getQueryRestriction() != null) {
      finalQuery.append(" AND ");
      finalQuery.append(pfs.getQueryRestriction());
    }

    // Prepare the query
    FullTextEntityManager fullTextEntityManager =
        Search.getFullTextEntityManager(manager);
    SearchFactory searchFactory = fullTextEntityManager.getSearchFactory();
    Query luceneQuery;
    QueryParser queryParser =
        new QueryParser(Version.LUCENE_36, "all",
            searchFactory.getAnalyzer(ConceptJpa.class));
    luceneQuery = queryParser.parse(finalQuery.toString());
    FullTextQuery fullTextQuery =
        fullTextEntityManager
            .createFullTextQuery(luceneQuery, ConceptJpa.class);
    results.setTotalCount(fullTextQuery.getResultSize());
    // Apply paging and sorting parameters
    applyPfsToLuceneQuery(ConceptJpa.class, fullTextQuery, pfs);

    // execute the query
    List<Concept> concepts = fullTextQuery.getResultList();
    // construct the search results
    for (Concept c : concepts) {
      results.addObject(c);
    }
    fullTextEntityManager.close();
    // closing fullTextEntityManager closes manager as well, recreate
    manager = factory.createEntityManager();
    return results;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#getConcept(java.lang.String)
   */
  @Override
  public Concept getConcept(Long id) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get concept " + id);
    Concept c = manager.find(ConceptJpa.class, id);
    return c;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getConcepts(java.lang.String,
   * java.lang.String, java.lang.String)
   */
  @SuppressWarnings("unchecked")
  @Override
  public ConceptList getConcepts(String terminologyId, String terminology,
    String version) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get concept " + terminologyId + "/" + terminology
            + "/" + version);
    javax.persistence.Query query =
        manager
            .createQuery("select c from ConceptJpa c where terminologyId = :terminologyId and terminologyVersion = :version and terminology = :terminology");
    /*
     * Try to retrieve the single expected result If zero or more than one
     * result are returned, log error and set result to null
     */
    try {
      query.setParameter("terminologyId", terminologyId);
      query.setParameter("terminology", terminology);
      query.setParameter("version", version);
      List<Concept> m = query.getResultList();
      ConceptListJpa conceptList = new ConceptListJpa();
      conceptList.setObjects(m);
      conceptList.setTotalCount(m.size());
      return conceptList;

    } catch (NoResultException e) {
      return null;
      /*
       * throw new LocalException( "Concept query for terminologyId = " +
       * terminologyId + ", terminology = " + terminology + ", version = " +
       * version + " returned no results!", e);
       */
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getSingleConcept(java.lang.String
   * , java.lang.String, java.lang.String)
   */
  @Override
  public Concept getSingleConcept(String terminologyId, String terminology,
    String version) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get single concept " + terminologyId + "/"
            + terminology + "/" + version);
    ConceptList cl = getConcepts(terminologyId, terminology, version);
    if (cl == null || cl.getTotalCount() == 0) {
      Logger.getLogger(ContentServiceJpa.class).debug("  empty concept ");
      return null;
    }
    if (cl.getTotalCount() > 1) {
      throw new Exception("Unexpected number of concepts: "
          + cl.getTotalCount());
    }
    return cl.getObjects().get(0);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#getAllConcepts(java.lang
   * .String, java.lang.String)
   */
  @SuppressWarnings("unchecked")
  @Override
  public ConceptList getAllConcepts(String terminology, String version) {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get concepts " + terminology + "/" + version);
    javax.persistence.Query query =
        manager
            .createQuery("select c from ConceptJpa c where terminologyVersion = :version and terminology = :terminology");
    /*
     * Try to retrieve the single expected result If zero or more than one
     * result are returned, log error and set result to null
     */
    try {
      query.setParameter("terminology", terminology);
      query.setParameter("version", version);
      List<Concept> concepts = query.getResultList();
      ConceptList conceptList = new ConceptListJpa();
      conceptList.setObjects(concepts);
      return conceptList;
    } catch (NoResultException e) {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#addConcept(org.ihtsdo.otf
   * .mapping.rf2.Concept)
   */
  @Override
  public Concept addConcept(Concept concept) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - add concept " + concept.getTerminologyId());
    // Assign id
    concept.setTerminologyId(idHandler.getTerminologyId(concept));

    // Add component
    concept = addComponent(concept);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.conceptAdded(concept);
      }
    }
    return concept;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#updateConcept(org.ihtsdo
   * .otf.mapping.rf2.Concept)
   */
  @Override
  public void updateConcept(Concept concept) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - update concept " + concept.getTerminologyId());

    // Id assignment should not change
    if (!idHandler.allowConceptIdChangeOnUpdate()) {
      Concept concept2 = getConcept(concept.getId());
      if (!idHandler.getTerminologyId(concept).equals(
          idHandler.getTerminologyId(concept2))) {
        throw new Exception("Update cannot be used to change object identity.");
      }
    } else {
      // set concept id on update
      concept.setTerminologyId(idHandler.getTerminologyId(concept));
    }

    // update component
    this.updateComponent(concept);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.conceptUpdated(concept);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#removeConcept(java.lang.
   * String)
   */
  @Override
  public void removeConcept(Long id) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - remove concept " + id);
    tx = manager.getTransaction();
    // retrieve this concept
    Concept concept = manager.find(ConceptJpa.class, id);
    // Set modification date
    concept.setLastModified(new Date());
    if (getTransactionPerOperation()) {
      // remove specialist
      tx.begin();
      if (manager.contains(concept)) {
        manager.remove(concept);
      } else {
        manager.remove(manager.merge(concept));
      }
      tx.commit();

    } else {
      if (manager.contains(concept)) {
        manager.remove(concept);
      } else {
        manager.remove(manager.merge(concept));
      }
    }
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.conceptRemoved(concept);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getDescription(java.lang.Long)
   */
  @Override
  public Description getDescription(Long id) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get description " + id);
    Description c = manager.find(DescriptionJpa.class, id);
    return c;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getDescription(java.lang.String,
   * java.lang.String, java.lang.String)
   */
  @Override
  public Description getDescription(String terminologyId, String terminology,
    String version) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get description " + terminologyId + "/"
            + terminology + "/" + version);
    javax.persistence.Query query =
        manager
            .createQuery("select c from DescriptionJpa c where terminologyId = :terminologyId and terminologyVersion = :version and terminology = :terminology");
    /*
     * Try to retrieve the single expected result If zero or more than one
     * result are returned, log error and set result to null
     */
    try {

      query.setParameter("terminologyId", terminologyId);
      query.setParameter("terminology", terminology);
      query.setParameter("version", version);
      return (Description) query.getSingleResult();
    } catch (NoResultException e) {
      Logger.getLogger(ContentServiceJpa.class).warn(
          "Could not retrieve description " + terminologyId
              + ", terminology = " + terminology + ", version = " + version
              + " returned no results!");
      return null;

    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#addDescription(org.ihtsdo
   * .otf.mapping.rf2.Description)
   */
  @Override
  public Description addDescription(Description description) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - add description " + description.getTerminologyId());
    // Assign id
    description.setTerminologyId(idHandler.getTerminologyId(description));

    // Add component
    description = addComponent(description);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.descriptionAdded(description);
      }
    }
    return description;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#updateDescription(org.ihtsdo
   * .otf.mapping.rf2.Description)
   */
  @Override
  public void updateDescription(Description description) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - update description "
            + description.getTerminologyId());
    // Id assignment should not change
    if (!idHandler.allowIdChangeOnUpdate()) {
      Description description2 = getDescription(description.getId());
      if (!idHandler.getTerminologyId(description).equals(
          idHandler.getTerminologyId(description2))) {
        throw new Exception("Update cannot be used to change object identity.");
      }
    }
    // don't set id on update

    // update component
    this.updateComponent(description);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.descriptionUpdated(description);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#removeDescription(java.lang
   * .String)
   */
  @Override
  public void removeDescription(Long id) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - remove description " + id);
    tx = manager.getTransaction();
    // retrieve this description
    Description description = manager.find(DescriptionJpa.class, id);
    // Set modification date
    description.setLastModified(new Date());

    if (getTransactionPerOperation()) {
      // remove description
      tx.begin();
      if (manager.contains(description)) {
        manager.remove(description);
      } else {
        manager.remove(manager.merge(description));
      }
      tx.commit();
    } else {
      if (manager.contains(description)) {
        manager.remove(description);
      } else {
        manager.remove(manager.merge(description));
      }
    }
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.descriptionRemoved(description);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getRelationship(java.lang.Long)
   */
  @Override
  public Relationship getRelationship(Long id) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get relationship " + id);
    Relationship c = manager.find(RelationshipJpa.class, id);
    return c;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getRelationship(java.lang.String,
   * java.lang.String, java.lang.String)
   */
  @Override
  public Relationship getRelationship(String terminologyId, String terminology,
    String version) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get relationship " + terminologyId + "/"
            + terminology + "/" + version);
    javax.persistence.Query query =
        manager
            .createQuery("select c from RelationshipJpa c where terminologyId = :terminologyId and terminologyVersion = :version and terminology = :terminology");
    /*
     * Try to retrieve the single expected result If zero or more than one
     * result are returned, log error and set result to null
     */
    try {
      query.setParameter("terminologyId", terminologyId);
      query.setParameter("terminology", terminology);
      query.setParameter("version", version);
      Relationship c = (Relationship) query.getSingleResult();
      return c;
    } catch (Exception e) {

      Logger.getLogger(ContentServiceJpa.class).debug(
          "Relationship query for terminologyId = " + terminologyId
              + ", terminology = " + terminology + ", version = " + version
              + " threw an exception!");
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#addRelationship(org.ihtsdo
   * .otf.mapping.rf2.Relationship)
   */
  @Override
  public Relationship addRelationship(Relationship relationship)
    throws Exception {
    Logger.getLogger(ContentServiceJpa.class)
        .debug(
            "Content Service - add relationship "
                + relationship.getTerminologyId());
    // Assign id
    relationship.setTerminologyId(idHandler.getTerminologyId(relationship));

    // Add component
    relationship = addComponent(relationship);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.relationshipAdded(relationship);
      }
    }
    return relationship;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#updateRelationship(org.ihtsdo
   * .otf.mapping.rf2.Relationship)
   */
  @Override
  public void updateRelationship(Relationship relationship) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - update relationship "
            + relationship.getTerminologyId());
    // Id assignment should not change
    if (!idHandler.allowIdChangeOnUpdate()) {
      Relationship relationship2 = getRelationship(relationship.getId());
      if (!idHandler.getTerminologyId(relationship).equals(
          idHandler.getTerminologyId(relationship2))) {
        throw new Exception("Update cannot be used to change object identity.");
      }
    }
    // don't set id on update

    // update component
    this.updateComponent(relationship);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.relationshipUpdated(relationship);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#removeRelationship(java.
   * lang.String)
   */
  @Override
  public void removeRelationship(Long id) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - remove relationship " + id);
    tx = manager.getTransaction();
    // retrieve this relationship
    Relationship relationship = manager.find(RelationshipJpa.class, id);
    // Set modification date
    relationship.setLastModified(new Date());
    if (getTransactionPerOperation()) {
      // remove relationship
      tx.begin();
      if (manager.contains(relationship)) {
        manager.remove(relationship);
      } else {
        manager.remove(manager.merge(relationship));
      }
      tx.commit();
    } else {
      if (manager.contains(relationship)) {
        manager.remove(relationship);
      } else {
        manager.remove(manager.merge(relationship));
      }
    }
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.relationshipRemoved(relationship);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#addTransitiveRelationship
   * (org.ihtsdo .otf.mapping.rf2.TransitiveRelationship)
   */
  @Override
  public TransitiveRelationship addTransitiveRelationship(
    TransitiveRelationship relationship) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - add transitive relationship "
            + relationship.getSuperTypeConcept() + "/"
            + relationship.getSubTypeConcept());
    // Assign id
    relationship.setTerminologyId(idHandler.getTerminologyId(relationship));

    // Add component
    relationship = addComponent(relationship);

    // no listener
    return relationship;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#updateTransitiveRelationship
   * (org.ihtsdo.otf.mapping.rf2.TransitiveRelationship)
   */
  @Override
  public void updateTransitiveRelationship(TransitiveRelationship relationship)
    throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - add transitive relationship "
            + relationship.getSuperTypeConcept() + "/"
            + relationship.getSubTypeConcept());
    // Id assignment should not change
    if (!idHandler.allowIdChangeOnUpdate()) {
      Relationship relationship2 = getRelationship(relationship.getId());
      if (!idHandler.getTerminologyId(relationship).equals(
          idHandler.getTerminologyId(relationship2))) {
        throw new Exception("Update cannot be used to change object identity.");
      }
    }
    // don't set id on update

    // update component
    this.updateComponent(relationship);

    // no listeners
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#removeTransitiveRelationship
   * (java. lang.String)
   */
  @Override
  public void removeTransitiveRelationship(Long id) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - remove transitive relationship " + id);

    tx = manager.getTransaction();
    // retrieve this transitiveRelationship
    TransitiveRelationship relationship =
        manager.find(TransitiveRelationshipJpa.class, id);
    // Set modification date
    relationship.setLastModified(new Date());
    if (getTransactionPerOperation()) {
      // remove transitiveRelationship
      tx.begin();
      if (manager.contains(relationship)) {
        manager.remove(relationship);
      } else {
        manager.remove(manager.merge(relationship));
      }
      tx.commit();
    } else {
      if (manager.contains(relationship)) {
        manager.remove(relationship);
      } else {
        manager.remove(manager.merge(relationship));
      }
    }
    // no notifications
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getAttributeValueRefSetMember
   * (java.lang.Long)
   */
  @Override
  public AttributeValueRefSetMember<? extends Component> getAttributeValueRefSetMember(
    Long id) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get attribute value refset member " + id);
    AttributeValueConceptRefSetMember c =
        manager.find(AttributeValueConceptRefSetMemberJpa.class, id);
    return c;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getAttributeValueRefSetMember
   * (java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public AttributeValueRefSetMember<? extends Component> getAttributeValueRefSetMember(
    String terminologyId, String terminology, String version) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get attribute value refset member " + terminologyId
            + "/" + terminology + "/" + version);
    javax.persistence.Query query =
        manager
            .createQuery("select c from AttributeValueRefSetMemberJpa c where terminologyId = :terminologyId and terminologyVersion = :version and terminology = :terminology");
    /*
     * Try to retrieve the single expected result If zero or more than one
     * result are returned, log error and set result to null
     */
    try {
      query.setParameter("terminologyId", terminologyId);
      query.setParameter("terminology", terminology);
      query.setParameter("version", version);
      @SuppressWarnings("unchecked")
      AttributeValueRefSetMember<? extends Component> c =
          (AttributeValueRefSetMember<? extends Component>) query
              .getSingleResult();
      return c;
    } catch (NoResultException e) {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#addAttributeValueRefSetMember
   * (org.ihtsdo.otf.mapping.rf2.AttributeValueRefSetMember)
   */
  /**
   * Adds the attribute value ref set member.
   *
   * @param member the member
   * @return the attribute value ref set member<? extends component>
   * @throws Exception the exception
   */
  @Override
  public AttributeValueRefSetMember<? extends Component> addAttributeValueRefSetMember(
    AttributeValueRefSetMember<? extends Component> member) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - add attribute value refset member"
            + member.getTerminologyId());
    // Assign id
    member.setTerminologyId(idHandler.getTerminologyId(member));

    // Add component
    member = addComponent(member);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberAdded(member);
      }
    }
    return member;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#updateAttributeValueRefSetMember
   * (org.ihtsdo.otf.mapping.rf2.AttributeValueRefSetMember)
   */
  @Override
  public void updateAttributeValueRefSetMember(
    AttributeValueRefSetMember<? extends Component> member) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - update attribute value refset member "
            + member.getTerminologyId());
    // Id assignment should not change
    if (!idHandler.allowIdChangeOnUpdate()) {
      AttributeValueRefSetMember<? extends Component> member2 =
          getAttributeValueRefSetMember(member.getId());
      if (!idHandler.getTerminologyId(member).equals(
          idHandler.getTerminologyId(member2))) {
        throw new Exception("Update cannot be used to change object identity.");
      }
    }
    // don't set id on update

    // update component
    this.updateComponent(member);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberUpdated(member);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#removeAttributeValueRefSetMember
   * (java.lang.String)
   */
  @Override
  public void removeAttributeValueRefSetMember(Long id) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - remove attribute value refset member " + id);
    // Remove the component
    RefSetMember<?> member =
        removeComponent(id, AbstractAssociationReferenceRefSetMemberJpa.class);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberRemoved(member);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getAssociationReferenceRefSetMember
   * (java.lang.Long)
   */
  @Override
  public AssociationReferenceRefSetMember<? extends Component> getAssociationReferenceRefSetMember(
    Long id) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get association reference refset member " + id);
    AssociationReferenceConceptRefSetMember c =
        manager.find(AssociationReferenceConceptRefSetMemberJpa.class, id);
    return c;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getAssociationReferenceRefSetMember
   * (java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public AssociationReferenceRefSetMember<? extends Component> getAssociationReferenceRefSetMember(
    String terminologyId, String terminology, String version) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get association reference refset member "
            + terminologyId + "/" + terminology + "/" + version);
    javax.persistence.Query query =
        manager
            .createQuery("select c from AssociationReferenceRefSetMemberJpa c where terminologyId = :terminologyId and terminologyVersion = :version and terminology = :terminology");
    /*
     * Try to retrieve the single expected result If zero or more than one
     * result are returned, log error and set result to null
     */
    try {
      query.setParameter("terminologyId", terminologyId);
      query.setParameter("terminology", terminology);
      query.setParameter("version", version);
      @SuppressWarnings("unchecked")
      AssociationReferenceRefSetMember<? extends Component> c =
          (AssociationReferenceRefSetMember<? extends Component>) query
              .getSingleResult();
      return c;
    } catch (NoResultException e) {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.services.ContentService#
   * addAssociationReferenceRefSetMember
   * (org.ihtsdo.otf.mapping.rf2.AssociationReferenceRefSetMember)
   */
  @Override
  public AssociationReferenceRefSetMember<? extends Component> addAssociationReferenceRefSetMember(
    AssociationReferenceRefSetMember<? extends Component> member)
    throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - add association reference refset member"
            + member.getTerminologyId());
    // Assign id
    member.setTerminologyId(idHandler.getTerminologyId(member));

    // Add component
    member = addComponent(member);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberAdded(member);
      }
    }
    return member;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.services.ContentService#
   * updateAssociationReferenceRefSetMember
   * (org.ihtsdo.otf.mapping.rf2.AssociationReferenceRefSetMember)
   */
  @Override
  public void updateAssociationReferenceRefSetMember(
    AssociationReferenceRefSetMember<? extends Component> member)
    throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - update association reference refset member "
            + member.getTerminologyId());
    // Id assignment should not change
    if (!idHandler.allowIdChangeOnUpdate()) {
      AssociationReferenceRefSetMember<? extends Component> member2 =
          getAssociationReferenceRefSetMember(member.getId());
      if (!idHandler.getTerminologyId(member).equals(
          idHandler.getTerminologyId(member2))) {
        throw new Exception("Update cannot be used to change object identity.");
      }
    }
    // don't set id on update

    // update component
    this.updateComponent(member);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberUpdated(member);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.services.ContentService#
   * removeAssociationReferenceRefSetMember (java.lang.String)
   */
  @Override
  public void removeAssociationReferenceRefSetMember(Long id) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - remove association reference refset member " + id);

    // Remove the component
    RefSetMember<?> member =
        removeComponent(id, AbstractAssociationReferenceRefSetMemberJpa.class);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberRemoved(member);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getComplexMapRefSetMember(java
   * .lang.Long)
   */
  @Override
  public ComplexMapRefSetMember getComplexMapRefSetMember(Long id)
    throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get complex map refset member " + id);
    ComplexMapRefSetMember c =
        manager.find(ComplexMapRefSetMemberJpa.class, id);
    return c;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getComplexMapRefSetMember(java
   * .lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public ComplexMapRefSetMember getComplexMapRefSetMember(String terminologyId,
    String terminology, String version) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get complex map refset member " + terminologyId
            + "/" + terminology + "/" + version);
    javax.persistence.Query query =
        manager
            .createQuery("select c from ComplexMapRefSetMemberJpa c where terminologyId = :terminologyId and terminologyVersion = :version and terminology = :terminology");
    /*
     * Try to retrieve the single expected result If zero or more than one
     * result are returned, log error and set result to null
     */
    try {
      query.setParameter("terminologyId", terminologyId);
      query.setParameter("terminology", terminology);
      query.setParameter("version", version);
      ComplexMapRefSetMember c =
          (ComplexMapRefSetMember) query.getSingleResult();
      return c;
    } catch (NoResultException e) {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#addComplexMapRefSetMember
   * (org.ihtsdo.otf.mapping.rf2.ComplexMapRefSetMember)
   */
  @Override
  public ComplexMapRefSetMember addComplexMapRefSetMember(
    ComplexMapRefSetMember member) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - add complex map refset member"
            + member.getTerminologyId());
    // Assign id
    member.setTerminologyId(idHandler.getTerminologyId(member));

    // Add component
    member = addComponent(member);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberAdded(member);
      }
    }

    return member;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#updateComplexMapRefSetMember
   * (org.ihtsdo.otf.mapping.rf2.ComplexMapRefSetMember)
   */
  @Override
  public void updateComplexMapRefSetMember(ComplexMapRefSetMember member)
    throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - update complex map refset member "
            + member.getTerminologyId());
    // Id assignment should not change
    if (!idHandler.allowIdChangeOnUpdate()) {
      ComplexMapRefSetMember member2 =
          getComplexMapRefSetMember(member.getId());
      if (!idHandler.getTerminologyId(member).equals(
          idHandler.getTerminologyId(member2))) {
        throw new Exception("Update cannot be used to change object identity.");
      }
    }
    // don't set id on update

    // update component
    this.updateComponent(member);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberUpdated(member);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#removeComplexMapRefSetMember
   * (java.lang.String)
   */
  @Override
  public void removeComplexMapRefSetMember(Long id) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - remove complex map refset member " + id);
    // Remove the component
    RefSetMember<?> member =
        removeComponent(id, AbstractAssociationReferenceRefSetMemberJpa.class);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberRemoved(member);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getLanguageRefSetMember(java.
   * lang.Long)
   */
  @Override
  public LanguageRefSetMember getLanguageRefSetMember(Long id) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get language refset member " + id);
    LanguageRefSetMember c = manager.find(LanguageRefSetMemberJpa.class, id);
    return c;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getLanguageRefSetMember(java.
   * lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public LanguageRefSetMember getLanguageRefSetMember(String terminologyId,
    String terminology, String version) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get language refset member " + terminologyId + "/"
            + terminology + "/" + version);
    javax.persistence.Query query =
        manager
            .createQuery("select c from LanguageRefSetMemberJpa c where terminologyId = :terminologyId and terminologyVersion = :version and terminology = :terminology");
    /*
     * Try to retrieve the single expected result If zero or more than one
     * result are returned, log error and set result to null
     */
    try {
      query.setParameter("terminologyId", terminologyId);
      query.setParameter("terminology", terminology);
      query.setParameter("version", version);
      LanguageRefSetMember c = (LanguageRefSetMember) query.getSingleResult();
      return c;
    } catch (NoResultException e) {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#addLanguageRefSetMember(
   * org.ihtsdo.otf.mapping.rf2.LanguageRefSetMember)
   */
  @Override
  public LanguageRefSetMember addLanguageRefSetMember(
    LanguageRefSetMember member) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - add language refset member"
            + member.getTerminologyId());
    // Assign id
    member.setTerminologyId(idHandler.getTerminologyId(member));

    // Add component
    member = addComponent(member);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberAdded(member);
      }
    }
    return member;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#updateLanguageRefSetMember
   * (org.ihtsdo.otf.mapping.rf2.LanguageRefSetMember)
   */
  @Override
  public void updateLanguageRefSetMember(LanguageRefSetMember member)
    throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - update language refset member "
            + member.getTerminologyId());
    // Id assignment should not change
    if (!idHandler.allowIdChangeOnUpdate()) {
      LanguageRefSetMember member2 = getLanguageRefSetMember(member.getId());
      if (!idHandler.getTerminologyId(member).equals(
          idHandler.getTerminologyId(member2))) {
        throw new Exception("Update cannot be used to change object identity.");
      }
    }
    // don't set id on update

    // update component
    this.updateComponent(member);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberUpdated(member);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#removeLanguageRefSetMember
   * (java.lang.String)
   */
  @Override
  public void removeLanguageRefSetMember(Long id) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - remove language refset member " + id);
    tx = manager.getTransaction();
    // retrieve this language ref set member
    LanguageRefSetMember member =
        manager.find(LanguageRefSetMemberJpa.class, id);
    // Set modification date
    member.setLastModified(new Date());

    if (getTransactionPerOperation()) {
      // remove language ref set member
      tx.begin();
      if (manager.contains(member)) {
        manager.remove(member);
      } else {
        manager.remove(manager.merge(member));
      }
      tx.commit();
    } else {
      if (manager.contains(member)) {
        manager.remove(member);
      } else {
        manager.remove(manager.merge(member));
      }
    }
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberRemoved(member);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getSimpleMapRefSetMember(java
   * .lang.Long)
   */
  @Override
  public SimpleMapRefSetMember getSimpleMapRefSetMember(Long id)
    throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get simple map refset member " + id);
    SimpleMapRefSetMember c = manager.find(SimpleMapRefSetMemberJpa.class, id);
    return c;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getSimpleMapRefSetMember(java
   * .lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public SimpleMapRefSetMember getSimpleMapRefSetMember(String terminologyId,
    String terminology, String version) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get simple map refset member " + terminologyId + "/"
            + terminology + "/" + version);
    javax.persistence.Query query =
        manager
            .createQuery("select c from SimpleMapRefSetMemberJpa c where terminologyId = :terminologyId and terminologyVersion = :version and terminology = :terminology");
    /*
     * Try to retrieve the single expected result If zero or more than one
     * result are returned, log error and set result to null
     */
    try {
      query.setParameter("terminologyId", terminologyId);
      query.setParameter("terminology", terminology);
      query.setParameter("version", version);
      SimpleMapRefSetMember c = (SimpleMapRefSetMember) query.getSingleResult();
      return c;
    } catch (NoResultException e) {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#addSimpleMapRefSetMember
   * (org.ihtsdo.otf.mapping.rf2.SimpleMapRefSetMember)
   */
  @Override
  public SimpleMapRefSetMember addSimpleMapRefSetMember(
    SimpleMapRefSetMember member) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - add simple map refset member"
            + member.getTerminologyId());
    // Assign id
    member.setTerminologyId(idHandler.getTerminologyId(member));

    // Add component
    member = addComponent(member);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberAdded(member);
      }
    }
    return member;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#updateSimpleMapRefSetMember
   * (org.ihtsdo.otf.mapping.rf2.SimpleMapRefSetMember)
   */
  @Override
  public void updateSimpleMapRefSetMember(SimpleMapRefSetMember member)
    throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - update simple map refset member "
            + member.getTerminologyId());
    // Id assignment should not change
    if (!idHandler.allowIdChangeOnUpdate()) {
      SimpleMapRefSetMember member2 = getSimpleMapRefSetMember(member.getId());
      if (!idHandler.getTerminologyId(member).equals(
          idHandler.getTerminologyId(member2))) {
        throw new Exception("Update cannot be used to change object identity.");
      }
    }
    // don't set id on update

    // update component
    this.updateComponent(member);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberUpdated(member);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#removeSimpleMapRefSetMember
   * (java.lang.String)
   */
  @Override
  public void removeSimpleMapRefSetMember(Long id) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - remove simple map refset member " + id);
    tx = manager.getTransaction();
    // retrieve this simple map ref set member
    SimpleMapRefSetMember member =
        manager.find(SimpleMapRefSetMemberJpa.class, id);
    // Set modification date
    member.setLastModified(new Date());
    if (getTransactionPerOperation()) {
      // remove simple map ref set member
      tx.begin();
      if (manager.contains(member)) {
        manager.remove(member);
      } else {
        manager.remove(manager.merge(member));
      }
      tx.commit();
    } else {
      if (manager.contains(member)) {
        manager.remove(member);
      } else {
        manager.remove(manager.merge(member));
      }
    }
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberRemoved(member);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getSimpleRefSetMember(java.lang
   * .Long)
   */
  @Override
  public SimpleRefSetMember getSimpleRefSetMember(Long id) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get simple refset member " + id);
    SimpleRefSetMember c = manager.find(SimpleRefSetMemberJpa.class, id);
    return c;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getSimpleRefSetMember(java.lang
   * .String, java.lang.String, java.lang.String)
   */
  @Override
  public SimpleRefSetMember getSimpleRefSetMember(String terminologyId,
    String terminology, String version) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get simple refset member " + terminologyId + "/"
            + terminology + "/" + version);
    javax.persistence.Query query =
        manager
            .createQuery("select c from SimpleRefSetMemberJpa c where terminologyId = :terminologyId and terminologyVersion = :version and terminology = :terminology");
    /*
     * Try to retrieve the single expected result If zero or more than one
     * result are returned, log error and set result to null
     */
    try {
      query.setParameter("terminologyId", terminologyId);
      query.setParameter("terminology", terminology);
      query.setParameter("version", version);
      SimpleRefSetMember c = (SimpleRefSetMember) query.getSingleResult();
      return c;
    } catch (NoResultException e) {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#addSimpleRefSetMember(org
   * .ihtsdo.otf.mapping.rf2.SimpleRefSetMember)
   */
  @Override
  public SimpleRefSetMember addSimpleRefSetMember(SimpleRefSetMember member)
    throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - add simple refset member"
            + member.getTerminologyId());
    // Assign id
    member.setTerminologyId(idHandler.getTerminologyId(member));

    // Add component
    member = addComponent(member);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberAdded(member);
      }
    }

    return member;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#updateSimpleRefSetMember
   * (org.ihtsdo.otf.mapping.rf2.SimpleRefSetMember)
   */
  @Override
  public void updateSimpleRefSetMember(SimpleRefSetMember member)
    throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - update simple refset member "
            + member.getTerminologyId());
    // Id assignment should not change
    if (!idHandler.allowIdChangeOnUpdate()) {
      SimpleRefSetMember member2 = getSimpleRefSetMember(member.getId());
      if (!idHandler.getTerminologyId(member).equals(
          idHandler.getTerminologyId(member2))) {
        throw new Exception("Update cannot be used to change object identity.");
      }
    }
    // don't set id on update

    // update component
    this.updateComponent(member);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberUpdated(member);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#removeSimpleRefSetMember
   * (java.lang.String)
   */
  @Override
  public void removeSimpleRefSetMember(Long id) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - remove simple refset member " + id);
    tx = manager.getTransaction();
    // retrieve this simple ref set member
    SimpleRefSetMember member = manager.find(SimpleRefSetMemberJpa.class, id);
    // Set modification date
    member.setLastModified(new Date());
    if (getTransactionPerOperation()) {
      // remove simple ref set member
      tx.begin();
      if (manager.contains(member)) {
        manager.remove(member);
      } else {
        manager.remove(manager.merge(member));
      }
      tx.commit();
    } else {
      if (manager.contains(member)) {
        manager.remove(member);
      } else {
        manager.remove(manager.merge(member));
      }
    }
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberRemoved(member);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getRefsetDescriptorRefSetMember
   * (java.lang.Long)
   */
  @Override
  public RefsetDescriptorRefSetMember getRefsetDescriptorRefSetMember(Long id)
    throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get refset descriptor refset member " + id);
    RefsetDescriptorRefSetMember c =
        manager.find(RefsetDescriptorRefSetMemberJpa.class, id);
    return c;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getRefsetDescriptorRefSetMember
   * (java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public RefsetDescriptorRefSetMember getRefsetDescriptorRefSetMember(
    String terminologyId, String terminology, String version) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get refset descriptor refset member "
            + terminologyId + "/" + terminology + "/" + version);
    javax.persistence.Query query =
        manager
            .createQuery("select c from RefsetDescriptorRefSetMemberJpa c where terminologyId = :terminologyId and terminologyVersion = :version and terminology = :terminology");
    /*
     * Try to retrieve the single expected result If zero or more than one
     * result are returned, log error and set result to null
     */
    try {
      query.setParameter("terminologyId", terminologyId);
      query.setParameter("terminology", terminology);
      query.setParameter("version", version);
      RefsetDescriptorRefSetMember c =
          (RefsetDescriptorRefSetMember) query.getSingleResult();
      return c;
    } catch (NoResultException e) {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.ContentService#
   * getRefsetDescriptorRefSetMembersForRefset(java.lang.String,
   * java.lang.String, java.lang.String)
   */
  @Override
  public List<RefsetDescriptorRefSetMember> getRefsetDescriptorRefSetMembersForRefset(
    String terminologyId, String terminology, String version) {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get refset descriptor refset member for refset "
            + terminologyId + "/" + terminology + "/" + version);
    javax.persistence.Query query =
        manager
            .createQuery("select c from RefsetDescriptorRefSetMemberJpa c where referencedComponentId = :terminologyId and terminologyVersion = :version and terminology = :terminology order by attributeOrder and active = 1");

    try {
      query.setParameter("terminologyId", terminologyId);
      query.setParameter("terminology", terminology);
      query.setParameter("version", version);
      @SuppressWarnings("unchecked")
      List<RefsetDescriptorRefSetMember> results = query.getResultList();
      return results;
    } catch (NoResultException e) {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#addRefsetDescriptorRefSetMember
   * (org.ihtsdo.otf.ts.rf2.RefsetDescriptorRefSetMember)
   */
  @Override
  public RefsetDescriptorRefSetMember addRefsetDescriptorRefSetMember(
    RefsetDescriptorRefSetMember member) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - add refset descriptor refset member"
            + member.getTerminologyId());
    // Assign id
    member.setTerminologyId(idHandler.getTerminologyId(member));

    // Add component
    member = addComponent(member);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberAdded(member);
      }
    }

    return member;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#updateRefsetDescriptorRefSetMember
   * (org.ihtsdo.otf.ts.rf2.RefsetDescriptorRefSetMember)
   */
  @Override
  public void updateRefsetDescriptorRefSetMember(
    RefsetDescriptorRefSetMember member) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - update refset descriptor refset member "
            + member.getTerminologyId());
    // Id assignment should not change
    if (!idHandler.allowIdChangeOnUpdate()) {
      RefsetDescriptorRefSetMember member2 =
          getRefsetDescriptorRefSetMember(member.getId());
      if (!idHandler.getTerminologyId(member).equals(
          idHandler.getTerminologyId(member2))) {
        throw new Exception("Update cannot be used to change object identity.");
      }
    }
    // don't set id on update

    // update component
    this.updateComponent(member);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberUpdated(member);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#removeRefsetDescriptorRefSetMember
   * (java.lang.Long)
   */
  @Override
  public void removeRefsetDescriptorRefSetMember(Long id) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - remove refset descriptor refset member " + id);
    tx = manager.getTransaction();
    // retrieve this refset descriptor ref set member
    RefsetDescriptorRefSetMember member =
        manager.find(RefsetDescriptorRefSetMemberJpa.class, id);
    // Set modification date
    member.setLastModified(new Date());
    if (getTransactionPerOperation()) {
      // remove refset descriptor ref set member
      tx.begin();
      if (manager.contains(member)) {
        manager.remove(member);
      } else {
        manager.remove(manager.merge(member));
      }
      tx.commit();
    } else {
      if (manager.contains(member)) {
        manager.remove(member);
      } else {
        manager.remove(manager.merge(member));
      }
    }
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberRemoved(member);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getDescriptionTypeRefSetMember
   * (java.lang.Long)
   */
  @Override
  public DescriptionTypeRefSetMember getDescriptionTypeRefSetMember(Long id)
    throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get description type refset member " + id);
    DescriptionTypeRefSetMember c =
        manager.find(DescriptionTypeRefSetMemberJpa.class, id);
    return c;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getDescriptionTypeRefSetMember
   * (java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public DescriptionTypeRefSetMember getDescriptionTypeRefSetMember(
    String terminologyId, String terminology, String version) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get description type refset member " + terminologyId
            + "/" + terminology + "/" + version);
    javax.persistence.Query query =
        manager
            .createQuery("select c from DescriptionTypeRefSetMemberJpa c where referencedComponentId = :terminologyId and terminologyVersion = :version and terminology = :terminology and active = 1");
    /*
     * Try to retrieve the single expected result If zero or more than one
     * result are returned, log error and set result to null
     */
    try {
      query.setParameter("terminologyId", terminologyId);
      query.setParameter("terminology", terminology);
      query.setParameter("version", version);
      DescriptionTypeRefSetMember c =
          (DescriptionTypeRefSetMember) query.getSingleResult();
      return c;
    } catch (NoResultException e) {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.ContentService#
   * getDescriptionTypeRefSetMemberForDescriptionType(java.lang.String,
   * java.lang.String, java.lang.String)
   */
  @Override
  public DescriptionTypeRefSetMember getDescriptionTypeRefSetMemberForDescriptionType(
    String terminologyId, String terminology, String version) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get description type refset member for description type "
            + terminologyId + "/" + terminology + "/" + version);
    javax.persistence.Query query =
        manager
            .createQuery("select c from DescriptionTypeRefSetMemberJpa c where terminologyId = :terminologyId and terminologyVersion = :version and terminology = :terminology");
    /*
     * Try to retrieve the single expected result If zero or more than one
     * result are returned, log error and set result to null
     */
    try {
      query.setParameter("terminologyId", terminologyId);
      query.setParameter("terminology", terminology);
      query.setParameter("version", version);
      DescriptionTypeRefSetMember c =
          (DescriptionTypeRefSetMember) query.getSingleResult();
      return c;
    } catch (NoResultException e) {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#addDescriptionTypeRefSetMember
   * (org.ihtsdo.otf.ts.rf2.DescriptionTypeRefSetMember)
   */
  @Override
  public DescriptionTypeRefSetMember addDescriptionTypeRefSetMember(
    DescriptionTypeRefSetMember member) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - add description type refset member"
            + member.getTerminologyId());
    // Assign id
    member.setTerminologyId(idHandler.getTerminologyId(member));

    // Add component
    member = addComponent(member);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberAdded(member);
      }
    }

    return member;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#updateDescriptionTypeRefSetMember
   * (org.ihtsdo.otf.ts.rf2.DescriptionTypeRefSetMember)
   */
  @Override
  public void updateDescriptionTypeRefSetMember(
    DescriptionTypeRefSetMember member) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - update description type refset member "
            + member.getTerminologyId());
    // Id assignment should not change
    if (!idHandler.allowIdChangeOnUpdate()) {
      DescriptionTypeRefSetMember member2 =
          getDescriptionTypeRefSetMember(member.getId());
      if (!idHandler.getTerminologyId(member).equals(
          idHandler.getTerminologyId(member2))) {
        throw new Exception("Update cannot be used to change object identity.");
      }
    }
    // don't set id on update

    // update component
    this.updateComponent(member);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberUpdated(member);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#removeDescriptionTypeRefSetMember
   * (java.lang.Long)
   */
  @Override
  public void removeDescriptionTypeRefSetMember(Long id) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - remove description type refset member " + id);
    tx = manager.getTransaction();
    // retrieve this description type ref set member
    DescriptionTypeRefSetMember member =
        manager.find(DescriptionTypeRefSetMemberJpa.class, id);
    // Set modification date
    member.setLastModified(new Date());
    if (getTransactionPerOperation()) {
      // remove description type ref set member
      tx.begin();
      if (manager.contains(member)) {
        manager.remove(member);
      } else {
        manager.remove(manager.merge(member));
      }
      tx.commit();
    } else {
      if (manager.contains(member)) {
        manager.remove(member);
      } else {
        manager.remove(manager.merge(member));
      }
    }
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberRemoved(member);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getModuleDependencyRefSetMember
   * (java.lang.Long)
   */
  @Override
  public ModuleDependencyRefSetMember getModuleDependencyRefSetMember(Long id)
    throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get module dependency refset member " + id);
    ModuleDependencyRefSetMember c =
        manager.find(ModuleDependencyRefSetMemberJpa.class, id);
    return c;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getModuleDependencyRefSetMember
   * (java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public ModuleDependencyRefSetMember getModuleDependencyRefSetMember(
    String terminologyId, String terminology, String version) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get module dependency refset member "
            + terminologyId + "/" + terminology + "/" + version);
    javax.persistence.Query query =
        manager
            .createQuery("select c from ModuleDependencyRefSetMemberJpa c where terminologyId = :terminologyId and terminologyVersion = :version and terminology = :terminology");
    /*
     * Try to retrieve the single expected result If zero or more than one
     * result are returned, log error and set result to null
     */
    try {
      query.setParameter("terminologyId", terminologyId);
      query.setParameter("terminology", terminology);
      query.setParameter("version", version);
      ModuleDependencyRefSetMember c =
          (ModuleDependencyRefSetMember) query.getSingleResult();
      return c;
    } catch (NoResultException e) {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.ContentService#
   * getModuleDependencyRefSetMembersForModule(java.lang.String,
   * java.lang.String, java.lang.String)
   */
  @Override
  public List<ModuleDependencyRefSetMember> getModuleDependencyRefSetMembersForModule(
    String terminologyId, String terminology, String version) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get module dependency refset member for module "
            + terminologyId + "/" + terminology + "/" + version);
    javax.persistence.Query query =
        manager
            .createQuery("select c from ModuleDependencyRefSetMemberJpa c where moduleId = :terminologyId and terminologyVersion = :version and terminology = :terminology and active = 1 order by sourceEffectiveTime");
    try {
      query.setParameter("terminologyId", terminologyId);
      query.setParameter("terminology", terminology);
      query.setParameter("version", version);
      @SuppressWarnings("unchecked")
      List<ModuleDependencyRefSetMember> results = query.getResultList();
      return results;
    } catch (NoResultException e) {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#addModuleDependencyRefSetMember
   * (org.ihtsdo.otf.ts.rf2.ModuleDependencyRefSetMember)
   */
  @Override
  public ModuleDependencyRefSetMember addModuleDependencyRefSetMember(
    ModuleDependencyRefSetMember member) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - add module dependency refset member"
            + member.getTerminologyId());
    // Assign id
    member.setTerminologyId(idHandler.getTerminologyId(member));

    // Add component
    member = addComponent(member);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberAdded(member);
      }
    }
    return member;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#updateModuleDependencyRefSetMember
   * (org.ihtsdo.otf.ts.rf2.ModuleDependencyRefSetMember)
   */
  @Override
  public void updateModuleDependencyRefSetMember(
    ModuleDependencyRefSetMember member) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - update module dependency refset member "
            + member.getTerminologyId());
    // Id assignment should not change
    if (!idHandler.allowIdChangeOnUpdate()) {
      ModuleDependencyRefSetMember member2 =
          getModuleDependencyRefSetMember(member.getId());
      if (!idHandler.getTerminologyId(member).equals(
          idHandler.getTerminologyId(member2))) {
        throw new Exception("Update cannot be used to change object identity.");
      }
    }
    // don't set id on update

    // update component
    this.updateComponent(member);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberUpdated(member);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#removeModuleDependencyRefSetMember
   * (java.lang.Long)
   */
  @Override
  public void removeModuleDependencyRefSetMember(Long id) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - remove module dependency refset member " + id);
    tx = manager.getTransaction();
    // retrieve this module dependency ref set member
    ModuleDependencyRefSetMember member =
        manager.find(ModuleDependencyRefSetMemberJpa.class, id);
    // Set modification date
    member.setLastModified(new Date());
    if (getTransactionPerOperation()) {
      // remove module dependency ref set member
      tx.begin();
      if (manager.contains(member)) {
        manager.remove(member);
      } else {
        manager.remove(manager.merge(member));
      }
      tx.commit();
    } else {
      if (manager.contains(member)) {
        manager.remove(member);
      } else {
        manager.remove(manager.merge(member));
      }
    }
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberRemoved(member);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#findConceptsForQuery(java.lang
   * .String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameter)
   */
  @Override
  public SearchResultList findConceptsForQuery(String terminology,
    String version, String searchString, PfsParameter pfs) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).info(
        "Content Service - find concepts " + terminology + "/" + version + "/"
            + searchString);
    if (pfs != null) {
      Logger.getLogger(ContentServiceJpa.class).info(
          "  pfs = " + pfs.toString());
    }

    // Prepare results
    SearchResultList results = new SearchResultListJpa();

    // Prepare the query
    StringBuilder finalQuery = new StringBuilder();
    finalQuery.append(searchString);
    finalQuery.append(" AND terminology:" + terminology
        + " AND terminologyVersion:" + version);
    if (pfs != null && pfs.getQueryRestriction() != null) {
      finalQuery.append(" AND ");
      finalQuery.append(pfs.getQueryRestriction());
    }
    Logger.getLogger(this.getClass()).info("query " + finalQuery);
    // Prepare the query
    FullTextEntityManager fullTextEntityManager =
        Search.getFullTextEntityManager(manager);
    SearchFactory searchFactory = fullTextEntityManager.getSearchFactory();
    Query luceneQuery;
    try {
      QueryParser queryParser =
          new QueryParser(Version.LUCENE_36, "all",
              searchFactory.getAnalyzer(ConceptJpa.class));
      luceneQuery = queryParser.parse(finalQuery.toString());
    } catch (ParseException e) {
      throw new LocalException(
          "The specified search terms cannot be parsed.  Please check syntax and try again.");
    }
    FullTextQuery fullTextQuery =
        fullTextEntityManager
            .createFullTextQuery(luceneQuery, ConceptJpa.class);

    results.setTotalCount(fullTextQuery.getResultSize());

    // Apply paging and sorting parameters
    applyPfsToLuceneQuery(ConceptJpa.class, fullTextQuery, pfs);

    // execute the query
    @SuppressWarnings("unchecked")
    List<Concept> concepts = fullTextQuery.getResultList();
    // construct the search results
    for (Concept c : concepts) {
      SearchResult sr = new SearchResultJpa();
      sr.setId(c.getId());
      sr.setTerminologyId(c.getTerminologyId());
      sr.setTerminology(c.getTerminology());
      sr.setTerminologyVersion(c.getTerminologyVersion());
      sr.setValue(c.getDefaultPreferredName());
      results.addObject(sr);
    }
    fullTextEntityManager.close();
    // closing fullTextEntityManager closes manager as well, recreate
    manager = factory.createEntityManager();

    return results;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#getDescendants(java.lang
   * .String, java.lang.String, java.lang.String, java.lang.String)
   */
  @SuppressWarnings("unchecked")
  @Override
  public SearchResultList findDescendantConcepts(String terminologyId,
    String terminology, String version, PfsParameter pfs) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - find descendants " + terminologyId + "/"
            + terminology + "/" + version);

    if (pfs != null && pfs.getQueryRestriction() != null) {
      throw new IllegalArgumentException(
          "Query restriction is not implemented for this call: "
              + pfs.getQueryRestriction());
    }
    SearchResultList searchResultList = new SearchResultListJpa();
    javax.persistence.Query query =
        manager
            .createQuery("select sub from TransitiveRelationshipJpa tr, ConceptJpa super, ConceptJpa sub "
                + " where super.version = :version "
                + " and super.terminology = :terminology "
                + " and super.terminologyId = :terminologyId"
                + " and tr.superTypeConcept = super"
                + " and tr.subTypeConcept = sub");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    query.setParameter("terminologyId", terminologyId);

    // Get the descendants
    List<Concept> descendants = query.getResultList();

    searchResultList.setTotalCount(descendants.size());

    // apply paging, and sorting if appropriate
    Comparator<Concept> comp = getPfsComparator(Concept.class, pfs);
    if (comp != null) {
      Collections.sort(descendants, comp);
    }
    // get the start and end indexes based on paging parameters
    int startIndex = 0;
    int toIndex = descendants.size();
    if (pfs != null) {
      startIndex = pfs.getStartIndex();
      toIndex = Math.min(descendants.size(), startIndex + pfs.getMaxResults());
    }
    // construct the search results
    for (Concept c : descendants.subList(startIndex, toIndex)) {
      SearchResult searchResult = new SearchResultJpa();
      searchResult.setId(c.getId());
      searchResult.setTerminologyId(c.getTerminologyId());
      searchResult.setTerminology(c.getTerminology());
      searchResult.setTerminologyVersion(c.getTerminologyVersion());
      searchResult.setValue(c.getDefaultPreferredName());
      searchResultList.addObject(searchResult);
    }
    // return the search result list
    return searchResultList;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#findAncestorConcepts(java.lang
   * .String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameter)
   */
  @SuppressWarnings("unchecked")
  @Override
  public SearchResultList findAncestorConcepts(String terminologyId,
    String terminology, String version, PfsParameter pfs) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - find ancestors " + terminologyId + "/" + terminology
            + "/" + version);

    if (pfs != null && pfs.getQueryRestriction() != null) {
      throw new IllegalArgumentException(
          "Query restriction is not implemented for this call: "
              + pfs.getQueryRestriction());
    }
    SearchResultList searchResultList = new SearchResultListJpa();
    javax.persistence.Query query =
        manager
            .createQuery("select super from TransitiveRelationshipJpa tr, ConceptJpa super, ConceptJpa sub "
                + " where sub.version = :version "
                + " and sub.terminology = :terminology "
                + " and sub.terminologyId = :terminologyId"
                + " and tr.superTypeConcept = super"
                + " and tr.subTypeConcept = sub");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    query.setParameter("terminologyId", terminologyId);

    // Get the ancestors
    List<Concept> ancestors = query.getResultList();

    searchResultList.setTotalCount(ancestors.size());

    // apply paging, and sorting if appropriate
    Comparator<Concept> comp = getPfsComparator(Concept.class, pfs);
    if (comp != null) {
      Collections.sort(ancestors, comp);
    }
    // get the start and end indexes based on paging parameters
    int startIndex = 0;
    int toIndex = ancestors.size();
    if (pfs != null) {
      startIndex = pfs.getStartIndex();
      toIndex = Math.min(ancestors.size(), startIndex + pfs.getMaxResults());
    }
    // construct the search results
    for (Concept c : ancestors.subList(startIndex, toIndex)) {
      SearchResult searchResult = new SearchResultJpa();
      searchResult.setId(c.getId());
      searchResult.setTerminologyId(c.getTerminologyId());
      searchResult.setTerminology(c.getTerminology());
      searchResult.setTerminologyVersion(c.getTerminologyVersion());
      searchResult.setValue(c.getDefaultPreferredName());
      searchResultList.addObject(searchResult);
    }
    // return the search result list
    return searchResultList;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#getAllRelationshipTerminologyIds
   * (java.lang.String, java.lang.String)
   */
  @SuppressWarnings("unchecked")
  @Override
  public Set<String> getAllRelationshipTerminologyIds(String terminology,
    String version) {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get all relationship terminology ids " + terminology
            + "/" + version);
    javax.persistence.Query query =
        manager
            .createQuery(
                "select c.terminologyId from RelationshipJpa c where terminology=:terminology and terminologyVersion=:version")
            .setParameter("terminology", terminology)
            .setParameter("version", version);

    List<String> terminologyIds = query.getResultList();
    Set<String> terminologyIdSet = new HashSet<>(terminologyIds);
    return terminologyIdSet;

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#getAllDescriptionTerminologyIds
   * (java.lang.String, java.lang.String)
   */
  @SuppressWarnings("unchecked")
  @Override
  public Set<String> getAllDescriptionTerminologyIds(String terminology,
    String version) {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get all description terminology ids " + terminology
            + "/" + version);
    javax.persistence.Query query =
        manager
            .createQuery(
                "select c.terminologyId from DescriptionJpa c where terminology=:terminology and terminologyVersion=:version")
            .setParameter("terminology", terminology)
            .setParameter("version", version);

    List<String> terminologyIds = query.getResultList();
    Set<String> terminologyIdSet = new HashSet<>(terminologyIds);
    return terminologyIdSet;

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.services.ContentService#
   * getAllLanguageRefSetMemberTerminologyIds(java.lang.String,
   * java.lang.String)
   */
  @SuppressWarnings("unchecked")
  @Override
  public Set<String> getAllLanguageRefSetMemberTerminologyIds(
    String terminology, String version) {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get all language refset member terminology ids "
            + terminology + "/" + version);
    javax.persistence.Query query =
        manager
            .createQuery(
                "select c.terminologyId from LanguageRefSetMemberJpa c where terminology=:terminology and terminologyVersion=:version")
            .setParameter("terminology", terminology)
            .setParameter("version", version);

    List<String> terminologyIds = query.getResultList();
    Set<String> terminologyIdSet = new HashSet<>(terminologyIds);
    return terminologyIdSet;

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#clearTransitiveClosure(java.lang
   * .String, java.lang.String)
   */
  @Override
  public void clearTransitiveClosure(String terminology, String version)
    throws Exception {
    Logger.getLogger(this.getClass()).info(
        "Content Service - Removing transitive closure data for " + terminology
            + ", " + version);

    // if currently in transaction-per-operation mode, temporarily set to
    // false
    boolean currentTransactionStrategy = getTransactionPerOperation();
    if (getTransactionPerOperation()) {
      this.setTransactionPerOperation(false);
    }

    int results = 0; // progress tracker
    int commitSize = 5000; // retrieval/delete batch size

    javax.persistence.Query query =
        manager
            .createQuery("select tr from TransitiveRelationshipJpa tr where terminology = :terminology and terminologyVersion = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    query.setFirstResult(0);
    query.setMaxResults(commitSize);

    boolean resultsFound = true;

    while (resultsFound) {

      @SuppressWarnings("unchecked")
      List<TransitiveRelationship> transitiveRels = query.getResultList();

      if (transitiveRels.size() == 0)
        resultsFound = false;

      this.beginTransaction();
      for (TransitiveRelationship tr : transitiveRels) {
        results++;
        removeTransitiveRelationship(tr.getId());
      }
      this.commit();
      Logger.getLogger(this.getClass()).info(
          "  " + results + " transitive closure rels deleted");
    }

    Logger.getLogger(this.getClass()).info(
        "Finished:  deleted " + results + " transitive closure rels");

    // set the transaction strategy based on status starting this routine
    setTransactionPerOperation(currentTransactionStrategy);

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#clearConcepts(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void clearConcepts(String terminology, String version) {
    if (getTransactionPerOperation()) {
      // remove simple ref set member
      tx.begin();
    }
    // Truncate refsets
    javax.persistence.Query query =
        manager
            .createQuery("DELETE From SimpleRefSetMemberJpa rs where terminology = :terminology");
    query.setParameter("terminology", terminology);
    int deleteRecords = query.executeUpdate();
    Logger.getLogger(this.getClass()).info(
        "    simple_ref_set records deleted: " + deleteRecords);

    query =
        manager
            .createQuery("DELETE From SimpleMapRefSetMemberJpa rs where terminology = :terminology");
    query.setParameter("terminology", terminology);
    deleteRecords = query.executeUpdate();
    Logger.getLogger(this.getClass()).info(
        "    simple_map_ref_set records deleted: " + deleteRecords);

    query =
        manager
            .createQuery("DELETE From ComplexMapRefSetMemberJpa rs where terminology = :terminology");
    query.setParameter("terminology", terminology);
    deleteRecords = query.executeUpdate();
    Logger.getLogger(this.getClass()).info(
        "    complex_map_ref_set records deleted: " + deleteRecords);

    query =
        manager
            .createQuery("DELETE From AttributeValueRefSetMemberJpa rs where terminology = :terminology");
    query.setParameter("terminology", terminology);
    deleteRecords = query.executeUpdate();
    Logger.getLogger(this.getClass()).info(
        "    attribute_value_ref_set records deleted: " + deleteRecords);

    query =
        manager
            .createQuery("DELETE From LanguageRefSetMemberJpa rs where terminology = :terminology");
    query.setParameter("terminology", terminology);
    deleteRecords = query.executeUpdate();
    Logger.getLogger(this.getClass()).info(
        "    language_ref_set records deleted: " + deleteRecords);

    // Truncate Terminology Elements
    query =
        manager
            .createQuery("DELETE From DescriptionJpa d where terminology = :terminology");
    query.setParameter("terminology", terminology);
    deleteRecords = query.executeUpdate();
    Logger.getLogger(this.getClass()).info(
        "    description records deleted: " + deleteRecords);
    query =
        manager
            .createQuery("DELETE From RelationshipJpa r where terminology = :terminology");
    query.setParameter("terminology", terminology);
    deleteRecords = query.executeUpdate();
    Logger.getLogger(this.getClass()).info(
        "    relationship records deleted: " + deleteRecords);
    query =
        manager
            .createQuery("DELETE From TransitiveRelationshipJpa c where terminology = :terminology");
    query.setParameter("terminology", terminology);
    deleteRecords = query.executeUpdate();
    Logger.getLogger(this.getClass()).info(
        "    transitive relationships records deleted: " + deleteRecords);
    query =
        manager
            .createQuery("DELETE From ConceptJpa c where terminology = :terminology");
    query.setParameter("terminology", terminology);
    deleteRecords = query.executeUpdate();
    Logger.getLogger(this.getClass()).info(
        "    concept records deleted: " + deleteRecords);

    if (getTransactionPerOperation()) {
      // remove simple ref set member
      tx.commit();
    }

  }

  /**
   * Apply pfs to lucene query.
   *
   * @param clazz the clazz
   * @param fullTextQuery the full text query
   * @param pfs the pfs
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  protected void applyPfsToLuceneQuery(Class<?> clazz,
    FullTextQuery fullTextQuery, PfsParameter pfs) throws Exception {
    // set paging/filtering/sorting if indicated
    if (pfs != null) {
      // if start index and max results are set, set paging
      if (pfs.getStartIndex() != -1 && pfs.getMaxResults() != -1) {
        fullTextQuery.setFirstResult(pfs.getStartIndex());
        fullTextQuery.setMaxResults(pfs.getMaxResults());
      }
      // if sort field is specified, set sort key
      if (pfs.getSortField() != null && !pfs.getSortField().isEmpty()) {

        // check that specified sort field exists on Concept and is
        // a string
        if (clazz.getDeclaredField(pfs.getSortField()).getType()
            .equals(String.class)) {
          fullTextQuery.setSort(new Sort(new SortField(pfs.getSortField(),
              SortField.STRING)));

        } else {
          throw new Exception(
              clazz.getName()
                  + " query specified a field that does not exist or is not a string");
        }
      }
    }
  }

  /**
   * Returns the pfs comparator.
   *
   * @param <T> the
   * @param clazz the clazz
   * @param pfs the pfs
   * @return the pfs comparator
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  protected <T> Comparator<T> getPfsComparator(Class<T> clazz, PfsParameter pfs)
    throws Exception {
    if (pfs != null
        && (pfs.getSortField() != null && !pfs.getSortField().isEmpty())) {
      // check that specified sort field exists on Concept and is
      // a string
      final Field sortField = clazz.getDeclaredField(pfs.getSortField());
      if (!sortField.getType().equals(String.class)) {

        throw new Exception(
            "findDescendantConcepts error:  Referenced sort field is not of type String");
      }
      // allow the field to access the Concept values
      sortField.setAccessible(true);

      // make comparator
      return new Comparator<T>() {
        @Override
        public int compare(T o1, T o2) {
          try {
            return ((String) sortField.get(o1)).compareTo((String) sortField
                .get(o2));
          } catch (IllegalAccessException e) {
            // on exception, return equality
            return 0;
          }
        }
      };

    } else {
      return null;
    }
  }

  /**
   * Apply pfs to query.
   *
   * @param queryStr the query str
   * @param pfs the pfs
   * @return the javax.persistence. query
   */
  protected javax.persistence.Query applyPfsToQuery(String queryStr,
    PfsParameter pfs) {
    String localQueryStr = queryStr;
    if (pfs.getSortField() != null) {
      localQueryStr += " order by a." + pfs.getSortField();
    }
    javax.persistence.Query query = manager.createQuery(localQueryStr);
    if (pfs != null && pfs.getStartIndex() > -1 && pfs.getMaxResults() > -1) {
      query.setFirstResult(pfs.getStartIndex());
      query.setMaxResults(pfs.getMaxResults());
    }
    return query;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.ContentService#getGraphResolutionHandler()
   */
  @Override
  public GraphResolutionHandler getGraphResolutionHandler() {
    return graphResolver;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getIdentifierAssignmentHandler()
   */
  @Override
  public IdentifierAssignmentHandler getIdentifierAssignmentHandler() {
    return idHandler;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getComputePreferredNameHandler
   * (java.lang.String)
   */
  @Override
  public ComputePreferredNameHandler getComputePreferredNameHandler(
    String terminology) throws Exception {
    return pnHandlerMap.get(terminology);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#computePreferredName(org.ihtsdo
   * .otf.ts.rf2.Concept)
   */
  @Override
  public void computePreferredName(Concept concept) throws Exception {
    graphResolver.resolve(concept, TerminologyUtility.getHierarchcialIsaRels(
        concept.getTerminology(), concept.getTerminologyVersion()));
    final String pn =
        pnHandlerMap.get(concept.getTerminologyId()).computePreferredName(
            concept.getDescriptions());
    concept.setDefaultPreferredName(pn);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.ContentService#enableListeners()
   */
  @Override
  public void enableListeners() {
    listenersEnabled = true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.ContentService#disableListeners()
   */
  @Override
  public void disableListeners() {
    listenersEnabled = false;
  }

  /**
   * Adds the component.
   *
   * @param component the component
   * @return
   * @return the component
   */
  private <T extends Component> T addComponent(T component) throws Exception {

    // Set last modified date
    component.setLastModified(new Date());

    // Leave effective time alone

    // add
    if (getTransactionPerOperation()) {
      tx = manager.getTransaction();
      tx.begin();
      manager.persist(component);
      tx.commit();
    } else {
      manager.persist(component);
    }
    return component;
  }

  /**
   * Update component.
   *
   * @param <T> the generic type
   * @param component the component
   * @return the t
   * @throws Exception the exception
   */
  private <T extends Component> void updateComponent(T component)
    throws Exception {

    // Set modification date
    component.setLastModified(new Date());

    // set effective time to nnull unless this is the planned effective time
    if (component.getEffectiveTime() != null
        && !component.getEffectiveTime().equals(plannedEffectiveTime)) {
      component.setEffectiveTime(null);
    }

    // update
    if (getTransactionPerOperation()) {
      tx = manager.getTransaction();
      tx.begin();
      manager.merge(component);
      tx.commit();
    } else {
      manager.merge(component);
    }
  }

  /**
   * Removes the component.
   *
   * @param <T> the generic type
   * @param id the id
   * @param clazz the clazz
   * @throws Exception the exception
   */
  private <T extends Component> T removeComponent(Long id, Class<T> clazz)
    throws Exception {
    // Get transaction and object
    tx = manager.getTransaction();
    T component = manager.find(clazz, id);

    // Set modification date
    component.setLastModified(new Date());

    // Remove
    if (getTransactionPerOperation()) {
      // remove refset member
      tx.begin();
      if (manager.contains(component)) {
        manager.remove(component);
      } else {
        manager.remove(manager.merge(component));
      }
      tx.commit();
    } else {
      if (manager.contains(component)) {
        manager.remove(component);
      } else {
        manager.remove(manager.merge(component));
      }
    }
    return component;
  }

  /**
   * Gets the planned effective time. Normally controlled by history service, we
   * need to know this here to be able to get the effective time which is used
   * for managing publication effective times.
   *
   * @return the planned effective time
   */
  private Date getPlannedEffectiveTime() {
    javax.persistence.Query query =
        manager
            .createQuery("select a from ReleaseInfoJpa a where planned is TRUE");
    try {
      ReleaseInfo releaseInfo = (ReleaseInfo) query.getSingleResult();
      return releaseInfo.getEffectiveTime();
    } catch (NoResultException e) {
      return null;
    }
  }
}
