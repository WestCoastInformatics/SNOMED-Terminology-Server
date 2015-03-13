package org.ihtsdo.otf.ts.jpa.services;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.ihtsdo.otf.ts.Project;
import org.ihtsdo.otf.ts.ReleaseInfo;
import org.ihtsdo.otf.ts.User;
import org.ihtsdo.otf.ts.UserRole;
import org.ihtsdo.otf.ts.helpers.AssociationReferenceRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.AssociationReferenceRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.AttributeValueRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.AttributeValueRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.ComplexMapRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ComplexMapRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ConceptListJpa;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.LanguageRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.LanguageRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.LocalException;
import org.ihtsdo.otf.ts.helpers.ModuleDependencyRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ModuleDependencyRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.PfsParameter;
import org.ihtsdo.otf.ts.helpers.ProjectList;
import org.ihtsdo.otf.ts.helpers.ProjectListJpa;
import org.ihtsdo.otf.ts.helpers.RefsetDescriptorRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.RefsetDescriptorRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.SearchCriteriaList;
import org.ihtsdo.otf.ts.helpers.SearchResult;
import org.ihtsdo.otf.ts.helpers.SearchResultJpa;
import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.helpers.SearchResultListJpa;
import org.ihtsdo.otf.ts.helpers.SimpleMapRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.SimpleMapRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.SimpleRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.SimpleRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.StringList;
import org.ihtsdo.otf.ts.jpa.ProjectJpa;
import org.ihtsdo.otf.ts.jpa.services.helper.TerminologyUtility;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceRefSetMember;
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
import org.ihtsdo.otf.ts.rf2.jpa.AbstractAttributeValueRefSetMemberJpa;
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

// TODO: Auto-generated Javadoc
/**
 * JPA enabled implementation of {@link ContentService}.
 */
public class ContentServiceJpa extends RootServiceJpa implements ContentService {

  /** The listeners enabled. */
  private boolean listenersEnabled = true;

  /** The config properties. */
  private static Properties config = null;

  /** The listener. */
  private static List<WorkflowListener> listeners = null;
  static {
    listeners = new ArrayList<>();
    try {
      if (config == null)
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

    try {
      if (config == null)
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
  public static Map<String, IdentifierAssignmentHandler> idHandlerMap =
      new HashMap<>();
  static {

    try {
      if (config == null)
        config = ConfigUtility.getConfigProperties();
      String key = "identifier.assignment.handler";
      for (String handlerName : config.getProperty(key).split(",")) {
        if (handlerName.isEmpty())
          continue;
        // Add handlers to map
        IdentifierAssignmentHandler handlerService =
            ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
                handlerName, IdentifierAssignmentHandler.class);
        idHandlerMap.put(handlerName, handlerService);
      }
    } catch (Exception e) {
      e.printStackTrace();
      idHandlerMap = null;
    }
  }

  /** The helper map. */
  private static Map<String, ComputePreferredNameHandler> pnHandlerMap = null;
  static {
    pnHandlerMap = new HashMap<>();

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

  /** The planned effective time map. */
  public static Map<String, Date> plannedEffectiveTimeMap = new HashMap<>();

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

    if (idHandlerMap == null) {
      throw new Exception(
          "Identifier assignment handler did not properly initialize, serious error.");
    }

  }

  /** The last modified flag. */
  private boolean lastModifiedFlag = false;

  /** The assign identifiers flag. */
  private boolean assignIdentifiersFlag = false;

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
            .createQuery("select c from ConceptJpa c where terminologyId = :terminologyId and version = :version and terminology = :terminology");
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
            .createQuery("select c from ConceptJpa c where version = :version and terminology = :terminology");
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
   * org.ihtsdo.otf.ts.services.ContentService#getDescendantConcepts(org.ihtsdo
   * .otf.ts.rf2.Concept, org.ihtsdo.otf.ts.helpers.PfsParameter)
   */
  @SuppressWarnings("unchecked")
  @Override
  public ConceptList getDescendantConcepts(Concept concept, PfsParameter pfs)
    throws Exception {

    if (pfs != null && pfs.getQueryRestriction() != null) {
      throw new IllegalArgumentException(
          "Query restriction is not implemented for this call: "
              + pfs.getQueryRestriction());
    }
    ConceptList list = new ConceptListJpa();
    String queryStr =
        "select a from TransitiveRelationshipJpa tr, ConceptJpa super, ConceptJpa a "
            + " where super.version = :version "
            + " and super.terminology = :terminology "
            + " and super.terminologyId = :terminologyId"
            + " and tr.superTypeConcept = super"
            + " and tr.subTypeConcept = a ";
    javax.persistence.Query query = applyPfsToQuery(queryStr, pfs);

    javax.persistence.Query ctQuery =
        manager
            .createQuery("select count(*) from TransitiveRelationshipJpa tr, ConceptJpa super"
                + " where super.version = :version "
                + " and super.terminology = :terminology "
                + " and super.terminologyId = :terminologyId"
                + " and tr.superTypeConcept = super");

    ctQuery.setParameter("terminology", concept.getTerminology());
    ctQuery.setParameter("version", concept.getTerminologyVersion());
    ctQuery.setParameter("terminologyId", concept.getTerminologyId());
    System.out.println("ctQuery: " + ctQuery.getSingleResult());
    list.setTotalCount(((Long) ctQuery.getSingleResult()).intValue());

    query.setParameter("terminology", concept.getTerminology());
    query.setParameter("version", concept.getTerminologyVersion());
    query.setParameter("terminologyId", concept.getTerminologyId());

    List<Concept> descendants = query.getResultList();
    list.setObjects(descendants);
    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getAncestorConcepts(org.ihtsdo
   * .otf.ts.rf2.Concept, org.ihtsdo.otf.ts.helpers.PfsParameter)
   */
  @SuppressWarnings("unchecked")
  @Override
  public ConceptList getAncestorConcepts(Concept concept, PfsParameter pfs)
    throws Exception {

    if (pfs != null && pfs.getQueryRestriction() != null) {
      throw new IllegalArgumentException(
          "Query restriction is not implemented for this call: "
              + pfs.getQueryRestriction());
    }
    ConceptList list = new ConceptListJpa();
    String queryStr =
        "select a from TransitiveRelationshipJpa tr, ConceptJpa a, ConceptJpa sub "
            + " where sub.version = :version "
            + " and sub.terminology = :terminology "
            + " and sub.terminologyId = :terminologyId"
            + " and tr.superTypeConcept = a" + " and tr.subTypeConcept = sub";
    javax.persistence.Query query = applyPfsToQuery(queryStr, pfs);

    javax.persistence.Query ctQuery =
        manager
            .createQuery("select count(*) from TransitiveRelationshipJpa tr, ConceptJpa sub"
                + " where sub.version = :version "
                + " and sub.terminology = :terminology "
                + " and sub.terminologyId = :terminologyId"
                + " and tr.subTypeConcept = sub");

    ctQuery.setParameter("terminology", concept.getTerminology());
    ctQuery.setParameter("version", concept.getTerminologyVersion());
    ctQuery.setParameter("terminologyId", concept.getTerminologyId());
    list.setTotalCount(((Long) ctQuery.getSingleResult()).intValue());

    query.setParameter("terminology", concept.getTerminology());
    query.setParameter("version", concept.getTerminologyVersion());
    query.setParameter("terminologyId", concept.getTerminologyId());
    List<Concept> ancestors = query.getResultList();
    list.setObjects(ancestors);
    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getChildConcepts(org.ihtsdo.otf
   * .ts.rf2.Concept, org.ihtsdo.otf.ts.helpers.PfsParameter)
   */
  @SuppressWarnings("unchecked")
  @Override
  public ConceptList getChildConcepts(Concept concept, PfsParameter pfs)
    throws Exception {
    ConceptList childrenConcepts = new ConceptListJpa();

    // construct query
    javax.persistence.Query query =
        manager.createQuery("select r from RelationshipJpa r, ConceptJpa c"
            + " where r.destinationConcept = c"
            + " and c.terminology = :terminology "
            + " and c.terminologyId = :terminologyId"
            + " and c.version = :version");
    query.setParameter("terminologyId", concept.getTerminologyId());
    query.setParameter("terminology", concept.getTerminology());
    query.setParameter("version", concept.getTerminologyVersion());

    // execute query
    List<Relationship> relationships = query.getResultList();

    // cycle over this concepts relationships
    for (Relationship r : relationships) {

      // if active hierarchcial relationship
      // It's a set so dont worry about stated vs. inferred
      if (r.isActive() && TerminologyUtility.isHierarchicalIsaRelationship(r)) {

        // add to children list
        childrenConcepts.addObject(r.getSourceConcept());
      }
    }

    // set total count
    childrenConcepts.setTotalCount(childrenConcepts.getCount());

    // if paging/filtering/sorting required
    if (pfs != null) {

      // filtering -- not supported

      // sorting
      Comparator<Concept> comparator = getPfsComparator(Concept.class, pfs);
      if (comparator != null) {
        childrenConcepts.sortBy(comparator);
      }

      // paging
      if (pfs.getStartIndex() != -1 && pfs.getMaxResults() != -1) {

        childrenConcepts.setObjects(childrenConcepts.getObjects().subList(
            Math.min(pfs.getStartIndex(), childrenConcepts.getTotalCount()),
            Math.min(pfs.getStartIndex() + pfs.getMaxResults(),
                childrenConcepts.getTotalCount())));
      }
    }

    return childrenConcepts;

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#addConcept(org.ihtsdo.otf
   * .mapping.rf2.Concept)
   */
  @SuppressWarnings("null")
  @Override
  public Concept addConcept(Concept concept) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - add concept " + concept.getTerminologyId());
    // Assign id
    IdentifierAssignmentHandler idHandler = null;
    if (assignIdentifiersFlag) {
      idHandler = idHandlerMap.get(concept.getTerminology());
      String id = idHandler.getTerminologyId(concept);
      concept.setTerminologyId(id);
    }
    // Process Cascade.ALL data structures
    Date date = new Date();
    for (Description description : concept.getDescriptions()) {
      if (assignIdentifiersFlag) {
        String id = idHandler.getTerminologyId(description);
        description.setTerminologyId(id);
      }
      if (lastModifiedFlag) {
        description.setLastModified(date);
        description.setLastModifiedBy(concept.getLastModifiedBy());
      }
      for (LanguageRefSetMember member : description.getLanguageRefSetMembers()) {
        if (assignIdentifiersFlag) {
          String id = idHandler.getTerminologyId(member);
          member.setTerminologyId(id);
        }
        if (lastModifiedFlag) {
          member.setLastModifiedBy(concept.getLastModifiedBy());
          member.setLastModified(date);
        }
      }
    }
    for (Relationship relationship : concept.getRelationships()) {
      if (assignIdentifiersFlag) {
        String id = idHandler.getTerminologyId(relationship);
        relationship.setTerminologyId(id);
      }
      if (lastModifiedFlag) {
        relationship.setLastModifiedBy(concept.getLastModifiedBy());
        relationship.setLastModified(date);
      }

    }

    // Add component
    Concept newConcept = addComponent(concept);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.conceptAdded(newConcept);
      }
    }
    return newConcept;
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
    final IdentifierAssignmentHandler idHandler =
        idHandlerMap.get(concept.getTerminology());
    if (assignIdentifiersFlag) {
      if (!idHandler.allowConceptIdChangeOnUpdate()) {
        Concept concept2 = getConcept(concept.getId());
        if (!idHandler.getTerminologyId(concept).equals(
            idHandler.getTerminologyId(concept2))) {
          throw new Exception(
              "Update cannot be used to change object identity.");
        }
      } else {
        // set concept id on update
        concept.setTerminologyId(idHandler.getTerminologyId(concept));
      }
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
    // Remove the component
    Concept concept = removeComponent(id, ConceptJpa.class);

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
            .createQuery("select c from DescriptionJpa c where terminologyId = :terminologyId and version = :version and terminology = :terminology");
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
  @SuppressWarnings("null")
  @Override
  public Description addDescription(Description description) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - add description " + description.getTerminologyId());
    // Assign id
    IdentifierAssignmentHandler idHandler = null;
    if (assignIdentifiersFlag) {
      idHandler = idHandlerMap.get(description.getTerminology());
      description.setTerminologyId(idHandler.getTerminologyId(description));
    }

    // Process Cascade.ALL data structures
    Date date = new Date();
    for (LanguageRefSetMember member : description.getLanguageRefSetMembers()) {
      if (assignIdentifiersFlag) {
        member.setTerminologyId(idHandler.getTerminologyId(member));
      }
      if (lastModifiedFlag) {
        member.setLastModifiedBy(description.getLastModifiedBy());
        member.setLastModified(date);
      }
    }

    // Add component
    Description newDescription = addComponent(description);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.descriptionAdded(newDescription);
      }
    }
    return newDescription;
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
    final IdentifierAssignmentHandler idHandler =
        idHandlerMap.get(description.getTerminology());
    if (!idHandler.allowIdChangeOnUpdate() && assignIdentifiersFlag) {
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
    // Remove the component
    Description description = removeComponent(id, DescriptionJpa.class);

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
            .createQuery("select c from RelationshipJpa c where terminologyId = :terminologyId and version = :version and terminology = :terminology");
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
    if (assignIdentifiersFlag) {
      relationship.setTerminologyId(idHandlerMap.get(
          relationship.getTerminology()).getTerminologyId(relationship));
    }
    // Add component
    Relationship newRelationship = addComponent(relationship);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.relationshipAdded(newRelationship);
      }
    }
    return newRelationship;
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
    final IdentifierAssignmentHandler idHandler =
        idHandlerMap.get(relationship.getTerminology());
    if (!idHandler.allowIdChangeOnUpdate() && assignIdentifiersFlag) {
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
    // Remove the component
    Relationship relationship = removeComponent(id, RelationshipJpa.class);

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
    if (assignIdentifiersFlag) {
      relationship.setTerminologyId(idHandlerMap.get(
          relationship.getTerminology()).getTerminologyId(relationship));
    }

    // Add component
    return addComponent(relationship);

    // no listener
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
    final IdentifierAssignmentHandler idHandler =
        idHandlerMap.get(relationship.getTerminology());
    if (!idHandler.allowIdChangeOnUpdate() && assignIdentifiersFlag) {
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
    // Remove the component
    removeComponent(id, TransitiveRelationshipJpa.class);

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
    AttributeValueRefSetMember<?> c =
        manager.find(AbstractAttributeValueRefSetMemberJpa.class, id);
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
            .createQuery("select c from AttributeValueRefSetMemberJpa c where terminologyId = :terminologyId and version = :version and terminology = :terminology");
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
   * org.ihtsdo.otf.ts.services.ContentService#getAttributeValueRefSetMembers
   * (java.lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameter)
   */
  @SuppressWarnings("unchecked")
  @Override
  public AttributeValueRefSetMemberList findAttributeValueRefSetMembers(
    String refsetId, String terminology, String version, PfsParameter pfs)
    throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get association reference refset members "
            + refsetId + "/" + terminology + "/" + version);
    javax.persistence.Query query =
        applyPfsToQuery("select a from AttributeValueRefSetMemberJpa a "
            + "where refSetId = :refsetId " + "and version = :version "
            + "and terminology = :terminology", pfs);
    javax.persistence.Query ctQuery =
        manager
            .createQuery("select count(a) ct from AttributeValueRefSetMemberJpa a "
                + "where refSetId = :refsetId "
                + "and version = :version "
                + "and terminology = :terminology");
    try {
      AttributeValueRefSetMemberList list =
          new AttributeValueRefSetMemberListJpa();

      // execute count query
      ctQuery.setParameter("refsetId", refsetId);
      ctQuery.setParameter("terminology", terminology);
      ctQuery.setParameter("version", version);
      list.setTotalCount(((BigDecimal) ctQuery.getResultList().get(0))
          .intValue());

      // Get results
      query.setParameter("refsetId", refsetId);
      query.setParameter("terminology", terminology);
      query.setParameter("version", version);
      list.setObjects(query.getResultList());

      return list;
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
    if (assignIdentifiersFlag) {
      member.setTerminologyId(idHandlerMap.get(member.getTerminology())
          .getTerminologyId(member));
    }

    // Add component
    AttributeValueRefSetMember<? extends Component> newMember =
        addComponent(member);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberAdded(newMember);
      }
    }
    return newMember;
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
    final IdentifierAssignmentHandler idHandler =
        idHandlerMap.get(member.getTerminology());
    if (!idHandler.allowIdChangeOnUpdate() && assignIdentifiersFlag) {
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
        removeComponent(id, AbstractAttributeValueRefSetMemberJpa.class);

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
    AssociationReferenceRefSetMember<?> c =
        manager.find(AbstractAssociationReferenceRefSetMemberJpa.class, id);
    return c;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getAssociationReferenceRefSetMembers
   * (java.lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameter)
   */
  @SuppressWarnings("unchecked")
  @Override
  public AssociationReferenceRefSetMemberList findAssociationReferenceRefSetMembers(
    String refsetId, String terminology, String version, PfsParameter pfs)
    throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get association reference refset members "
            + refsetId + "/" + terminology + "/" + version);
    javax.persistence.Query query =
        applyPfsToQuery("select a from AssociationReferenceRefSetMemberJpa a "
            + "where refSetId = :refsetId " + "and version = :version "
            + "and terminology = :terminology", pfs);
    javax.persistence.Query ctQuery =
        manager
            .createQuery("select count(a) from AssociationReferenceRefSetMemberJpa a "
                + "where refSetId = :refsetId "
                + "and version = :version "
                + "and terminology = :terminology");
    try {
      AssociationReferenceRefSetMemberList list =
          new AssociationReferenceRefSetMemberListJpa();
      ctQuery.setParameter("refsetId", refsetId);
      ctQuery.setParameter("terminology", terminology);
      ctQuery.setParameter("version", version);
      list.setTotalCount(((BigDecimal) ctQuery.getResultList().get(0))
          .intValue());

      query.setParameter("refsetId", refsetId);
      query.setParameter("terminology", terminology);
      query.setParameter("version", version);
      list.setObjects(query.getResultList());

      return list;
    } catch (NoResultException e) {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getAssociationReferenceRefSetMember
   * (java.lang.String, java.lang.String, java.lang.String)
   */
  @SuppressWarnings("unchecked")
  @Override
  public AssociationReferenceRefSetMember<? extends Component> getAssociationReferenceRefSetMember(
    String terminologyId, String terminology, String version) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get association reference refset member "
            + terminologyId + "/" + terminology + "/" + version);
    javax.persistence.Query query =
        manager
            .createQuery("select c from AbstractAssociationReferenceRefSetMemberJpa c where terminologyId = :terminologyId and version = :version and terminology = :terminology");
    /*
     * Try to retrieve the single expected result If zero or more than one
     * result are returned, log error and set result to null
     */
    try {
      query.setParameter("terminologyId", terminologyId);
      query.setParameter("terminology", terminology);
      query.setParameter("version", version);
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
    if (assignIdentifiersFlag) {
    member.setTerminologyId(idHandlerMap.get(member.getTerminology())
        .getTerminologyId(member));
    }
    // Add component
    AssociationReferenceRefSetMember<? extends Component> newMember =
        addComponent(member);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberAdded(newMember);
      }
    }
    return newMember;
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
    final IdentifierAssignmentHandler idHandler =
        idHandlerMap.get(member.getTerminology());
    if (!idHandler.allowIdChangeOnUpdate() && assignIdentifiersFlag) {
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

    RefSetMember<? extends Component> member =
        getAssociationReferenceRefSetMember(id);
    // Remove the component
    removeComponent(id, member.getClass());

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
            .createQuery("select c from ComplexMapRefSetMemberJpa c where terminologyId = :terminologyId and version = :version and terminology = :terminology");
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
   * org.ihtsdo.otf.ts.services.ContentService#getComplexMapRefSetMembers(java
   * .lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameter)
   */
  @SuppressWarnings("unchecked")
  @Override
  public ComplexMapRefSetMemberList findComplexMapRefSetMembers(
    String refsetId, String terminology, String version, PfsParameter pfs)
    throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get association reference refset members "
            + refsetId + "/" + terminology + "/" + version);
    javax.persistence.Query query =
        applyPfsToQuery("select a from ComplexMapRefSetMemberJpa a "
            + "where refSetId = :refsetId " + "and version = :version "
            + "and terminology = :terminology", pfs);
    javax.persistence.Query ctQuery =
        manager.createQuery("select count(a) from ComplexMapRefSetMemberJpa a "
            + "where refSetId = :refsetId " + "and version = :version "
            + "and terminology = :terminology");
    try {
      ComplexMapRefSetMemberList list = new ComplexMapRefSetMemberListJpa();
      ctQuery.setParameter("refsetId", refsetId);
      ctQuery.setParameter("terminology", terminology);
      ctQuery.setParameter("version", version);
      list.setTotalCount(((BigDecimal) ctQuery.getResultList().get(0))
          .intValue());

      query.setParameter("refsetId", refsetId);
      query.setParameter("terminology", terminology);
      query.setParameter("version", version);
      list.setObjects(query.getResultList());

      return list;
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
    if (assignIdentifiersFlag) {
    member.setTerminologyId(idHandlerMap.get(member.getTerminology())
        .getTerminologyId(member));
    }
    // Add component
    ComplexMapRefSetMember newMember = addComponent(member);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberAdded(newMember);
      }
    }

    return newMember;
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
    final IdentifierAssignmentHandler idHandler =
        idHandlerMap.get(member.getTerminology());
    if (!idHandler.allowIdChangeOnUpdate() && assignIdentifiersFlag) {
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
            .createQuery("select c from LanguageRefSetMemberJpa c where terminologyId = :terminologyId and version = :version and terminology = :terminology");
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
   * org.ihtsdo.otf.ts.services.ContentService#getLanguageRefSetMembers(java
   * .lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameter)
   */
  @SuppressWarnings("unchecked")
  @Override
  public LanguageRefSetMemberList findLanguageRefSetMembers(String refsetId,
    String terminology, String version, PfsParameter pfs) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get association reference refset members "
            + refsetId + "/" + terminology + "/" + version);
    javax.persistence.Query query =
        applyPfsToQuery("select a from LanguageRefSetMemberJpa a "
            + "where refSetId = :refsetId " + "and version = :version "
            + "and terminology = :terminology", pfs);
    javax.persistence.Query ctQuery =
        manager.createQuery("select count(a) from LanguageRefSetMemberJpa a "
            + "where refSetId = :refsetId " + "and version = :version "
            + "and terminology = :terminology");
    try {
      LanguageRefSetMemberList list = new LanguageRefSetMemberListJpa();
      ctQuery.setParameter("refsetId", refsetId);
      ctQuery.setParameter("terminology", terminology);
      ctQuery.setParameter("version", version);
      list.setTotalCount(((BigDecimal) ctQuery.getResultList().get(0))
          .intValue());

      query.setParameter("refsetId", refsetId);
      query.setParameter("terminology", terminology);
      query.setParameter("version", version);

      list.setObjects(query.getResultList());

      return list;
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
    if (assignIdentifiersFlag) {
    member.setTerminologyId(idHandlerMap.get(member.getTerminology())
        .getTerminologyId(member));
    }
    // Add component
    LanguageRefSetMember newMember = addComponent(member);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberAdded(newMember);
      }
    }
    return newMember;
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
    final IdentifierAssignmentHandler idHandler =
        idHandlerMap.get(member.getTerminology());
    if (!idHandler.allowIdChangeOnUpdate() && assignIdentifiersFlag) {
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
    // Remove the component
    RefSetMember<?> member = removeComponent(id, LanguageRefSetMemberJpa.class);

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
            .createQuery("select c from SimpleMapRefSetMemberJpa c where terminologyId = :terminologyId and version = :version and terminology = :terminology");
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
   * org.ihtsdo.otf.ts.services.ContentService#getSimpleMapRefSetMembers(java
   * .lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameter)
   */
  @SuppressWarnings("unchecked")
  @Override
  public SimpleMapRefSetMemberList findSimpleMapRefSetMembers(String refsetId,
    String terminology, String version, PfsParameter pfs) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get association reference refset members "
            + refsetId + "/" + terminology + "/" + version);
    javax.persistence.Query query =
        applyPfsToQuery("select a from SimpleMapRefSetMemberJpa a "
            + "where refSetId = :refsetId " + "and version = :version "
            + "and terminology = :terminology", pfs);
    javax.persistence.Query ctQuery =
        manager.createQuery("select count(a) from SimpleMapRefSetMemberJpa a "
            + "where refSetId = :refsetId " + "and version = :version "
            + "and terminology = :terminology");
    try {
      SimpleMapRefSetMemberList list = new SimpleMapRefSetMemberListJpa();
      ctQuery.setParameter("refsetId", refsetId);
      ctQuery.setParameter("terminology", terminology);
      ctQuery.setParameter("version", version);
      list.setTotalCount(((BigDecimal) ctQuery.getResultList().get(0))
          .intValue());

      query.setParameter("refsetId", refsetId);
      query.setParameter("terminology", terminology);
      query.setParameter("version", version);
      list.setObjects(query.getResultList());

      return list;
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
    if (assignIdentifiersFlag) {
    member.setTerminologyId(idHandlerMap.get(member.getTerminology())
        .getTerminologyId(member));
    }
    // Add component
    SimpleMapRefSetMember newMember = addComponent(member);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberAdded(newMember);
      }
    }
    return newMember;
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
    final IdentifierAssignmentHandler idHandler =
        idHandlerMap.get(member.getTerminology());
    if (!idHandler.allowIdChangeOnUpdate() && assignIdentifiersFlag) {
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
    // Remove the component
    RefSetMember<?> member =
        removeComponent(id, SimpleMapRefSetMemberJpa.class);

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
            .createQuery("select c from SimpleRefSetMemberJpa c where terminologyId = :terminologyId and version = :version and terminology = :terminology");
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
   * org.ihtsdo.otf.ts.services.ContentService#getSimpleRefSetMembers(java.lang
   * .String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.PfsParameter)
   */
  @SuppressWarnings("unchecked")
  @Override
  public SimpleRefSetMemberList findSimpleRefSetMembers(String refsetId,
    String terminology, String version, PfsParameter pfs) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get association reference refset members "
            + refsetId + "/" + terminology + "/" + version);
    javax.persistence.Query query =
        applyPfsToQuery("select a from SimpleRefSetMemberJpa a "
            + "where refSetId = :refsetId " + "and version = :version "
            + "and terminology = :terminology", pfs);
    javax.persistence.Query ctQuery =
        manager.createQuery("select count(a) from SimpleRefSetMemberJpa a "
            + "where refSetId = :refsetId " + "and version = :version "
            + "and terminology = :terminology");
    try {
      SimpleRefSetMemberList list = new SimpleRefSetMemberListJpa();
      ctQuery.setParameter("refsetId", refsetId);
      ctQuery.setParameter("terminology", terminology);
      ctQuery.setParameter("version", version);
      list.setTotalCount(((BigDecimal) ctQuery.getResultList().get(0))
          .intValue());

      query.setParameter("refsetId", refsetId);
      query.setParameter("terminology", terminology);
      query.setParameter("version", version);
      list.setObjects(query.getResultList());

      return list;
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
    if (assignIdentifiersFlag) {
    member.setTerminologyId(idHandlerMap.get(member.getTerminology())
        .getTerminologyId(member));
    }
    // Add component
    SimpleRefSetMember newMember = addComponent(member);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberAdded(newMember);
      }
    }

    return newMember;
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
    final IdentifierAssignmentHandler idHandler =
        idHandlerMap.get(member.getTerminology());
    if (!idHandler.allowIdChangeOnUpdate() && assignIdentifiersFlag) {
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
    // Remove the component
    RefSetMember<?> member = removeComponent(id, SimpleRefSetMemberJpa.class);

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
            .createQuery("select c from RefsetDescriptorRefSetMemberJpa c where terminologyId = :terminologyId and version = :version and terminology = :terminology");
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
  public RefsetDescriptorRefSetMemberList getRefsetDescriptorRefSetMembersForRefset(
    String terminologyId, String terminology, String version) {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get refset descriptor refset member for refset "
            + terminologyId + "/" + terminology + "/" + version);
    javax.persistence.Query query =
        manager
            .createQuery("select c from RefsetDescriptorRefSetMemberJpa c where referencedComponentId = :terminologyId and version = :version and terminology = :terminology order by attributeOrder and active = 1");

    try {
      query.setParameter("terminologyId", terminologyId);
      query.setParameter("terminology", terminology);
      query.setParameter("version", version);
      @SuppressWarnings("unchecked")
      List<RefsetDescriptorRefSetMember> results = query.getResultList();
      RefsetDescriptorRefSetMemberList list =
          new RefsetDescriptorRefSetMemberListJpa();
      list.setObjects(results);
      list.setTotalCount(list.getCount());
      return list;
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
    if (assignIdentifiersFlag) {
    member.setTerminologyId(idHandlerMap.get(member.getTerminology())
        .getTerminologyId(member));
    }
    // Add component
    RefsetDescriptorRefSetMember newMember = addComponent(member);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberAdded(newMember);
      }
    }

    return newMember;
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
    final IdentifierAssignmentHandler idHandler =
        idHandlerMap.get(member.getTerminology());
    if (!idHandler.allowIdChangeOnUpdate() && assignIdentifiersFlag) {
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
    // Remove the component
    RefSetMember<?> member =
        removeComponent(id, RefsetDescriptorRefSetMemberJpa.class);

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
            .createQuery("select c from DescriptionTypeRefSetMemberJpa c where referencedComponentId = :terminologyId and version = :version and terminology = :terminology and active = 1");
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
            .createQuery("select c from DescriptionTypeRefSetMemberJpa c where terminologyId = :terminologyId and version = :version and terminology = :terminology");
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
    if (assignIdentifiersFlag) {
    member.setTerminologyId(idHandlerMap.get(member.getTerminology())
        .getTerminologyId(member));
    }
    // Add component
    DescriptionTypeRefSetMember newMember = addComponent(member);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberAdded(newMember);
      }
    }

    return newMember;
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
    final IdentifierAssignmentHandler idHandler =
        idHandlerMap.get(member.getTerminology());
    if (!idHandler.allowIdChangeOnUpdate() && assignIdentifiersFlag) {
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
    // Remove the component
    RefSetMember<?> member =
        removeComponent(id, DescriptionTypeRefSetMemberJpa.class);

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
            .createQuery("select c from ModuleDependencyRefSetMemberJpa c where terminologyId = :terminologyId and version = :version and terminology = :terminology");
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
  public ModuleDependencyRefSetMemberList getModuleDependencyRefSetMembersForModule(
    String terminologyId, String terminology, String version) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get module dependency refset member for module "
            + terminologyId + "/" + terminology + "/" + version);
    javax.persistence.Query query =
        manager
            .createQuery("select c from ModuleDependencyRefSetMemberJpa c where moduleId = :terminologyId and version = :version and terminology = :terminology and active = 1 order by sourceEffectiveTime");
    try {
      query.setParameter("terminologyId", terminologyId);
      query.setParameter("terminology", terminology);
      query.setParameter("version", version);
      @SuppressWarnings("unchecked")
      List<ModuleDependencyRefSetMember> results = query.getResultList();
      ModuleDependencyRefSetMemberList list =
          new ModuleDependencyRefSetMemberListJpa();
      list.setObjects(results);
      list.setTotalCount(list.getCount());
      return list;
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
    if (assignIdentifiersFlag) {
    member.setTerminologyId(idHandlerMap.get(member.getTerminology())
        .getTerminologyId(member));
    }
    // Add component
    ModuleDependencyRefSetMember newMember = addComponent(member);

    // Inform listeners
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.refSetMemberAdded(newMember);
      }
    }
    return newMember;
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
    final IdentifierAssignmentHandler idHandler =
        idHandlerMap.get(member.getTerminology());
    if (!idHandler.allowIdChangeOnUpdate() && assignIdentifiersFlag) {
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
    // Remove the component
    RefSetMember<?> member =
        removeComponent(id, ModuleDependencyRefSetMemberJpa.class);

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

    // Prepare the query string
    StringBuilder finalQuery = new StringBuilder();
    finalQuery.append(searchString);
    finalQuery.append(" AND terminology:" + terminology
        + " AND terminologyVersion:" + version);
    if (pfs != null && pfs.getQueryRestriction() != null) {
      finalQuery.append(" AND ");
      finalQuery.append(pfs.getQueryRestriction());
    }
    Logger.getLogger(getClass()).info("query " + finalQuery);

    // Prepare the manager and lucene query
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
   * org.ihtsdo.otf.ts.services.ContentService#findConceptsForSearchCriteria
   * (java.lang.String, java.lang.String, java.lang.String,
   * org.ihtsdo.otf.ts.helpers.SearchCriteriaList,
   * org.ihtsdo.otf.ts.helpers.PfsParameter)
   */
  @Override
  public SearchResultList findConceptsForSearchCriteria(String terminology,
    String version, String query, SearchCriteriaList criteria, PfsParameter pfs)
    throws Exception {
    throw new UnsupportedOperationException();

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
  public StringList getAllRelationshipTerminologyIds(String terminology,
    String version) {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get all relationship terminology ids " + terminology
            + "/" + version);
    javax.persistence.Query query =
        manager
            .createQuery(
                "select c.terminologyId from RelationshipJpa c where terminology=:terminology and version=:version")
            .setParameter("terminology", terminology)
            .setParameter("version", version);

    List<String> terminologyIds = query.getResultList();
    StringList list = new StringList();
    list.setObjects(terminologyIds);
    list.setTotalCount(list.getCount());
    return list;

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
  public StringList getAllDescriptionTerminologyIds(String terminology,
    String version) {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get all description terminology ids " + terminology
            + "/" + version);
    javax.persistence.Query query =
        manager
            .createQuery(
                "select c.terminologyId from DescriptionJpa c where terminology=:terminology and version=:version")
            .setParameter("terminology", terminology)
            .setParameter("version", version);

    List<String> terminologyIds = query.getResultList();
    StringList list = new StringList();
    list.setObjects(terminologyIds);
    list.setTotalCount(list.getCount());
    return list;
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
  public StringList getAllLanguageRefSetMemberTerminologyIds(
    String terminology, String version) {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get all language refset member terminology ids "
            + terminology + "/" + version);
    javax.persistence.Query query =
        manager
            .createQuery(
                "select c.terminologyId from LanguageRefSetMemberJpa c where terminology=:terminology and version=:version")
            .setParameter("terminology", terminology)
            .setParameter("version", version);

    List<String> terminologyIds = query.getResultList();
    StringList list = new StringList();
    list.setObjects(terminologyIds);
    list.setTotalCount(list.getCount());
    return list;
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
    Logger.getLogger(getClass()).info(
        "Content Service - Removing transitive closure data for " + terminology
            + ", " + version);
    try {
      if (getTransactionPerOperation()) {
        // remove simple ref set member
        tx.begin();
      }

      javax.persistence.Query query =
          manager.createQuery("DELETE From TransitiveRelationshipJpa "
              + " c where terminology = :terminology "
              + " and version = :version");
      query.setParameter("terminology", terminology);
      query.setParameter("version", version);
      int deleteRecords = query.executeUpdate();
      Logger.getLogger(getClass()).info(
          "    TransitiveRelationshipJpa records deleted: " + deleteRecords);

      if (getTransactionPerOperation()) {
        // remove simple ref set member
        tx.commit();
      }
    } catch (Exception e) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw e;
    }
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
    try {
      if (getTransactionPerOperation()) {
        // remove simple ref set member
        tx.begin();
      }

      String[] types =
          new String[] {
              "DescriptionTypeRefSetMemberJpa",
              "ModuleDependencyRefSetMemberJpa",
              "RefsetDescriptorRefSetMemberJpa", "SimpleRefSetMemberJpa",
              "SimpleMapRefSetMemberJpa", "ComplexMapRefSetMemberJpa",
              "AttributeValueConceptRefSetMemberJpa",
              "AssociationReferenceConceptRefSetMemberJpa",
              "AttributeValueDescriptionRefSetMemberJpa",
              "AssociationReferenceDescriptionRefSetMemberJpa",
              "LanguageRefSetMemberJpa", "DescriptionJpa", "RelationshipJpa",
              "TransitiveRelationshipJpa", "ConceptJpa"
          };

      for (String type : types) {
        Logger.getLogger(getClass()).info("  Remove " + type);
        javax.persistence.Query query =
            manager.createQuery("DELETE FROM " + type
                + " WHERE terminology = :terminology "
                + " AND version = :version");
        query.setParameter("terminology", terminology);
        query.setParameter("version", version);
        int deleteRecords = query.executeUpdate();
        Logger.getLogger(getClass()).info(
            "    " + type + " records deleted: " + deleteRecords);
      }

      if (getTransactionPerOperation()) {
        // remove simple ref set member
        tx.commit();
      }
    } catch (Exception e) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw e;
    }

  }

  /**
   * Returns the concepts in scope.
   *
   * @param project the project
   * @return the concepts in scope
   * @throws Exception the exception
   */
  @Override
  public ConceptList getConceptsInScope(Project project) throws Exception {
    Logger.getLogger(getClass()).info(
        "Content Service - get project scope - " + project);
    if (project == null) {
      throw new Exception("Unexpected null project");
    }

    Set<Concept> include = new HashSet<>();
    for (String terminologyId : project.getScopeConcepts()) {
      // get concept
      Concept concept =
          getSingleConcept(terminologyId, project.getTerminology(),
              project.getTerminologyVersion());
      include.add(concept);
      // get descendants
      if (project.getScopeDescendantsFlag()) {
        for (Concept desc : getDescendantConcepts(concept, null).getObjects()) {
          include.add(desc);
        }
      }
    }
    Logger.getLogger(getClass()).info("  include count = " + include.size());

    Set<Concept> exclude = new HashSet<>();
    for (String terminologyId : project.getScopeExcludesConcepts()) {
      // get concept
      Concept concept =
          getSingleConcept(terminologyId, project.getTerminology(),
              project.getTerminologyVersion());
      exclude.add(concept);
      // get descendants
      if (project.getScopeExcludesDescendantsFlag()) {
        for (Concept desc : getDescendantConcepts(concept, null).getObjects()) {
          exclude.add(desc);
        }
      }
    }
    Logger.getLogger(getClass()).info("  exclude count = " + exclude.size());

    include.removeAll(exclude);
    Logger.getLogger(getClass()).info("  count = " + include.size());
    ConceptList list = new ConceptListJpa();
    list.setObjects(new ArrayList<>(include));
    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.ContentService#getProject(java.lang.Long)
   */
  @Override
  public Project getProject(Long id) {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get project " + id);
    Project project = manager.find(ProjectJpa.class, id);
    return project;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.ContentService#getProjects()
   */
  @Override
  @SuppressWarnings("unchecked")
  public ProjectList getProjects() {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get projects");
    javax.persistence.Query query =
        manager.createQuery("select a from ProjectJpa a");
    try {
      List<Project> concepts = query.getResultList();
      ProjectList list = new ProjectListJpa();
      list.setObjects(concepts);
      return list;
    } catch (NoResultException e) {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getUserRoleForProject(java.lang
   * .String, java.lang.Long)
   */
  @Override
  public UserRole getUserRoleForProject(String username, Long projectId)
    throws Exception {
    Project project = getProject(projectId);
    if (project == null) {
      throw new Exception("No project found for " + projectId);
    }

    // check admin
    for (User user : project.getAdministrators()) {
      if (username.equals(user.getUserName())) {
        return UserRole.ADMINISTRATOR;
      }
    }

    // check lead
    for (User user : project.getLeads()) {
      if (username.equals(user.getUserName())) {
        return UserRole.LEAD;
      }
    }

    // check author
    for (User user : project.getAuthors()) {
      if (username.equals(user.getUserName())) {
        return UserRole.AUTHOR;
      }
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#addProject(org.ihtsdo.otf.ts.
   * Project)
   */
  @Override
  public Project addProject(Project project) {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - add project - " + project);
    try {
      // Set last modified date
      if (lastModifiedFlag) {
        project.setLastModified(new Date());
      }

      // add the project
      if (getTransactionPerOperation()) {
        tx = manager.getTransaction();
        tx.begin();
        manager.persist(project);
        tx.commit();
      } else {
        manager.persist(project);
      }
      return project;
    } catch (Exception e) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw e;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#updateProject(org.ihtsdo.otf.
   * ts.Project)
   */
  @Override
  public void updateProject(Project project) {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - update project - " + project);

    try {
      // Set modification date
      if (lastModifiedFlag) {
        project.setLastModified(new Date());
      }

      // update
      if (getTransactionPerOperation()) {
        tx = manager.getTransaction();
        tx.begin();
        manager.merge(project);
        tx.commit();
      } else {
        manager.merge(project);
      }
    } catch (Exception e) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw e;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#removeProject(java.lang.Long)
   */
  @Override
  public void removeProject(Long id) {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - remove project " + id);
    try {
      // Get transaction and object
      tx = manager.getTransaction();
      Project project = manager.find(ProjectJpa.class, id);

      // Set modification date
      if (lastModifiedFlag) {
        project.setLastModified(new Date());
      }

      // Remove
      if (getTransactionPerOperation()) {
        // remove refset member
        tx.begin();
        if (manager.contains(project)) {
          manager.remove(project);
        } else {
          manager.remove(manager.merge(project));
        }
        tx.commit();
      } else {
        if (manager.contains(project)) {
          manager.remove(project);
        } else {
          manager.remove(manager.merge(project));
        }
      }
    } catch (Exception e) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw e;
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
  protected void applyPfsToLuceneQuery(Class<?> clazz,
    FullTextQuery fullTextQuery, PfsParameter pfs) throws Exception {

    // set paging/filtering/sorting if indicated
    if (pfs != null) {
      // if start index and max results are set, set paging
      if (pfs.getStartIndex() != -1 && pfs.getMaxResults() != -1) {
        System.out.println("  Applying first/max results");
        fullTextQuery.setFirstResult(pfs.getStartIndex());
        fullTextQuery.setMaxResults(pfs.getMaxResults());
      }

      // if sort field is specified, set sort key
      if (pfs.getSortField() != null && !pfs.getSortField().isEmpty()) {

        Map<String, Boolean> nameToAnalyzedMap =
            this.getNameAnalyzedPairsFromAnnotation(clazz, pfs.getSortField());
        String sortField = null;

        if (nameToAnalyzedMap.size() == 0) {
          throw new Exception(clazz.getName()
              + " does not have declared, annotated method for field "
              + pfs.getSortField());
        }

        // first check the default name (rendered as ""), if not analyzed, use
        // this as sort
        if (nameToAnalyzedMap.get("") != null
            && nameToAnalyzedMap.get("").equals(false)) {
          sortField = pfs.getSortField();
        }

        // otherwise check explicit [name]Sort index
        else if (nameToAnalyzedMap.get(pfs.getSortField() + "Sort") != null
            && nameToAnalyzedMap.get(pfs.getSortField() + "Sort").equals(false)) {
          sortField = pfs.getSortField() + "Sort";
        }

        // if none, throw exception
        if (sortField == null) {
          throw new Exception(
              "Could not retrieve a non-analyzed Field annotation for get method for variable name "
                  + pfs.getSortField());
        }

        Sort sort =
            new Sort(new SortField(sortField, SortField.STRING,
                !pfs.isAscending()));
        fullTextQuery.setSort(sort);
      }
    }
  }

  /**
   * Returns the name analyzed pairs from annotation.
   *
   * @param clazz the clazz
   * @param sortField the sort field
   * @return the name analyzed pairs from annotation
   * @throws NoSuchMethodException the no such method exception
   * @throws SecurityException the security exception
   */
  @SuppressWarnings("static-method")
  private Map<String, Boolean> getNameAnalyzedPairsFromAnnotation(
    Class<?> clazz, String sortField) throws NoSuchMethodException,
    SecurityException {

    // initialize the name->analyzed pair map
    Map<String, Boolean> nameAnalyzedPairs = new HashMap<>();

    Method m =
        clazz.getMethod("get" + sortField.substring(0, 1).toUpperCase()
            + sortField.substring(1), new Class<?>[] {});

    Set<org.hibernate.search.annotations.Field> annotationFields =
        new HashSet<>();

    // check for Field annotation
    if (m.isAnnotationPresent(org.hibernate.search.annotations.Field.class)) {
      annotationFields.add(m
          .getAnnotation(org.hibernate.search.annotations.Field.class));
    }

    // check for Fields annotation
    if (m.isAnnotationPresent(org.hibernate.search.annotations.Fields.class)) {
      // add all specified fields
      for (org.hibernate.search.annotations.Field f : m.getAnnotation(
          org.hibernate.search.annotations.Fields.class).value()) {
        annotationFields.add(f);
      }
    }

    // cycle over discovered fields and put name and analyze == YES into map
    for (org.hibernate.search.annotations.Field f : annotationFields) {
      nameAnalyzedPairs.put(f.name(), f.analyze().equals(Analyze.YES) ? true
          : false);
    }

    return nameAnalyzedPairs;
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
      final Field sortField = clazz.getField(pfs.getSortField());

      // allow the field to access the Concept values
      sortField.setAccessible(true);

      if (pfs.isAscending()) {
        // make comparator
        return new Comparator<T>() {
          @Override
          public int compare(T o1, T o2) {
            try {
              // handle dates explicitly
              if (o2 instanceof Date) {
                return ((Date) sortField.get(o1)).compareTo((Date) sortField
                    .get(o2));
              } else {
                // otherwise, sort based on conversion to string
                return (sortField.get(o1).toString()).compareTo(sortField.get(
                    o2).toString());
              }
            } catch (IllegalAccessException e) {
              // on exception, return equality
              return 0;
            }
          }
        };
      } else {
        // make comparator
        return new Comparator<T>() {
          @Override
          public int compare(T o2, T o1) {
            try {
              // handle dates explicitly
              if (o2 instanceof Date) {
                return ((Date) sortField.get(o1)).compareTo((Date) sortField
                    .get(o2));
              } else {
                // otherwise, sort based on conversion to string
                return (sortField.get(o1).toString()).compareTo(sortField.get(
                    o2).toString());
              }
            } catch (IllegalAccessException e) {
              // on exception, return equality
              return 0;
            }
          }
        };
      }

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
    if (pfs != null && pfs.getSortField() != null) {
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
  public IdentifierAssignmentHandler getIdentifierAssignmentHandler(
    String terminology) {
    return idHandlerMap.get(terminology);
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
  public String getComputedPreferredName(Concept concept) throws Exception {
    try {
      graphResolver.resolve(concept, TerminologyUtility.getHierarchcialIsaRels(
          concept.getTerminology(), concept.getTerminologyVersion()));
      ComputePreferredNameHandler handler =
          pnHandlerMap.get(concept.getTerminology());
      if (handler == null) {
        throw new Exception(
            "Compute preferred name handler is not configured for "
                + concept.getTerminology());
      }
      final String pn = handler.computePreferredName(concept.getDescriptions());
      return pn;
    } catch (Exception e) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw e;
    }
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
   * @param <T> the
   * @param component the component
   * @return the component
   * @throws Exception the exception
   */
  private <T extends Component> T addComponent(T component) throws Exception {
    try {
      // Set last modified date
      if (lastModifiedFlag) {
        component.setLastModified(new Date());
      }

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
    } catch (Exception e) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw e;
    }

  }

  /**
   * Update component.
   *
   * @param <T> the generic type
   * @param component the component
   * @throws Exception the exception
   */
  private <T extends Component> void updateComponent(T component)
    throws Exception {
    try {
      // Set modification date
      if (lastModifiedFlag) {
        component.setLastModified(new Date());
      }

      // set effective time to null unless this is the planned effective time
      if (component.getEffectiveTime() != null
          && !component.getEffectiveTime().equals(
              getPlannedEffectiveTime(component.getTerminology(),
                  component.getTerminologyVersion()))) {
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
    } catch (Exception e) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw e;
    }

  }

  /**
   * Removes the component.
   *
   * @param <T> the generic type
   * @param id the id
   * @param clazz the clazz
   * @return the component
   * @throws Exception the exception
   */
  private <T extends Component> T removeComponent(Long id, Class<T> clazz)
    throws Exception {
    try {
      // Get transaction and object
      tx = manager.getTransaction();
      T component = manager.find(clazz, id);

      // Set modification date
      if (lastModifiedFlag) {
        component.setLastModified(new Date());
      }

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
    } catch (Exception e) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw e;
    }
  }

  /**
   * Gets the planned effective time. Normally controlled by history service, we
   * need to know this here to be able to get the effective time which is used
   * for managing publication effective times.
   *
   * @param terminology the terminology
   * @param version the version
   * @return the planned effective time
   */
  private Date getPlannedEffectiveTime(String terminology, String version) {
    if (plannedEffectiveTimeMap.containsKey(terminology + version)) {
      return plannedEffectiveTimeMap.get(terminology + version);
    }
    javax.persistence.Query query =
        manager
            .createQuery("select a from ReleaseInfoJpa a where planned is TRUE "
                + "and terminology = :terminology and terminologyVersion = :version");
    try {
      query.setParameter("terminology", terminology);
      query.setParameter("version", version);
      ReleaseInfo releaseInfo = (ReleaseInfo) query.getSingleResult();
      plannedEffectiveTimeMap.put(terminology + version,
          releaseInfo.getEffectiveTime());
      return releaseInfo.getEffectiveTime();
    } catch (NoResultException e) {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.ContentService#isLastModifiedFlag()
   */
  @Override
  public boolean isLastModifiedFlag() {
    return lastModifiedFlag;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.ContentService#setLastModifiedFlag(boolean)
   */
  @Override
  public void setLastModifiedFlag(boolean lastModifiedFlag) {
    this.lastModifiedFlag = lastModifiedFlag;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#setAssignIdentifiersFlag(boolean)
   */
  @Override
  public void setAssignIdentifiersFlag(boolean assignIdentifiersFlag) {
    this.assignIdentifiersFlag = assignIdentifiersFlag;
  }

}
