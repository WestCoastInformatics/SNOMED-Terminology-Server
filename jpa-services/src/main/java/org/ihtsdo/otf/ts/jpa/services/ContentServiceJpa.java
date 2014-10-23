package org.ihtsdo.otf.ts.jpa.services;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
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
import org.ihtsdo.otf.ts.helpers.LocalException;
import org.ihtsdo.otf.ts.helpers.PfsParameter;
import org.ihtsdo.otf.ts.helpers.SearchResult;
import org.ihtsdo.otf.ts.helpers.SearchResultJpa;
import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.helpers.SearchResultListJpa;
import org.ihtsdo.otf.ts.rf2.AttributeValueRefSetMember;
import org.ihtsdo.otf.ts.rf2.ComplexMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.SimpleMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.SimpleRefSetMember;
import org.ihtsdo.otf.ts.rf2.TransitiveRelationship;
import org.ihtsdo.otf.ts.rf2.jpa.AttributeValueRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ComplexMapRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa;
import org.ihtsdo.otf.ts.rf2.jpa.LanguageRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RelationshipJpa;
import org.ihtsdo.otf.ts.rf2.jpa.SimpleMapRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.SimpleRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.TransitiveRelationshipJpa;
import org.ihtsdo.otf.ts.services.ContentService;

/**
 * The Content Services for the Jpa model.
 */
public class ContentServiceJpa extends RootServiceJpa implements ContentService {

  /**
   * Instantiates an empty {@link ContentServiceJpa}.
   *
   * @throws Exception the exception
   */
  public ContentServiceJpa() throws Exception {
    super();
  }


  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.services.ContentService#getConcepts(java.lang.String, java.lang.String, org.ihtsdo.otf.ts.helpers.PfsParameter)
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
      results.addConcept(c);
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

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public ConceptList getConcepts(String terminologyId, String terminology,
    String terminologyVersion) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get concept " + terminologyId + "/" + terminology
            + "/" + terminologyVersion);
    javax.persistence.Query query =
        manager
            .createQuery("select c from ConceptJpa c where terminologyId = :terminologyId and terminologyVersion = :terminologyVersion and terminology = :terminology");
    /*
     * Try to retrieve the single expected result If zero or more than one
     * result are returned, log error and set result to null
     */
    try {
      query.setParameter("terminologyId", terminologyId);
      query.setParameter("terminology", terminology);
      query.setParameter("terminologyVersion", terminologyVersion);
      List<Concept> m = query.getResultList();
      ConceptListJpa conceptList = new ConceptListJpa();
      conceptList.setConcepts(m);
      conceptList.setTotalCount(m.size());
      return conceptList;

    } catch (NoResultException e) {
      return null;
      /*
       * throw new LocalException( "Concept query for terminologyId = " +
       * terminologyId + ", terminology = " + terminology +
       * ", terminologyVersion = " + terminologyVersion +
       * " returned no results!", e);
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
    String terminologyVersion) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get single concept " + terminologyId + "/"
            + terminology + "/" + terminologyVersion);
    ConceptList cl =
        getConcepts(terminologyId, terminology, terminologyVersion);
    if (cl == null || cl.getTotalCount() == 0) {
      Logger.getLogger(ContentServiceJpa.class).debug("  empty concept ");
      return null;
    }
    if (cl.getTotalCount() > 1) {
      throw new Exception("Unexpected number of concepts: "
          + cl.getTotalCount());
    }
    return cl.getConcepts().get(0);
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
  public ConceptList getAllConcepts(String terminology,
    String terminologyVersion) {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get concepts " + terminology + "/"
            + terminologyVersion);
    javax.persistence.Query query =
        manager
            .createQuery("select c from ConceptJpa c where terminologyVersion = :terminologyVersion and terminology = :terminology");
    /*
     * Try to retrieve the single expected result If zero or more than one
     * result are returned, log error and set result to null
     */
    try {
      query.setParameter("terminology", terminology);
      query.setParameter("terminologyVersion", terminologyVersion);
      List<Concept> concepts = query.getResultList();
      ConceptList conceptList = new ConceptListJpa();
      conceptList.setConcepts(concepts);
      return conceptList;
    } catch (NoResultException e) {
      return null;
      /*
       * throw new LocalException( "Concept query for terminologyId = " +
       * terminologyId + ", terminology = " + terminology +
       * ", terminologyVersion = " + terminologyVersion +
       * " returned no results!", e);
       */
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
    if (getTransactionPerOperation()) {
      tx = manager.getTransaction();
      tx.begin();
      manager.persist(concept);
      tx.commit();
    } else {
      manager.persist(concept);
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
    if (getTransactionPerOperation()) {
      tx = manager.getTransaction();
      tx.begin();
      manager.merge(concept);
      tx.commit();
    } else {
      manager.merge(concept);
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
    Concept mu = manager.find(ConceptJpa.class, id);
    if (getTransactionPerOperation()) {
      // remove specialist
      tx.begin();
      if (manager.contains(mu)) {
        manager.remove(mu);
      } else {
        manager.remove(manager.merge(mu));
      }
      tx.commit();

    } else {
      if (manager.contains(mu)) {
        manager.remove(mu);
      } else {
        manager.remove(manager.merge(mu));
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#getDescription(java.lang
   * .String)
   */
  @Override
  public Description getDescription(String id) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get description " + id);
    Description c = manager.find(DescriptionJpa.class, id);
    return c;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Description getDescription(String terminologyId, String terminology,
    String terminologyVersion) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get description " + terminologyId + "/"
            + terminology + "/" + terminologyVersion);
    javax.persistence.Query query =
        manager
            .createQuery("select c from DescriptionJpa c where terminologyId = :terminologyId and terminologyVersion = :terminologyVersion and terminology = :terminology");
    /*
     * Try to retrieve the single expected result If zero or more than one
     * result are returned, log error and set result to null
     */
    try {

      query.setParameter("terminologyId", terminologyId);
      query.setParameter("terminology", terminology);
      query.setParameter("terminologyVersion", terminologyVersion);
      return (Description) query.getSingleResult();
    } catch (NoResultException e) {
      Logger.getLogger(ContentServiceJpa.class).warn(
          "Could not retrieve description " + terminologyId
              + ", terminology = " + terminology + ", terminologyVersion = "
              + terminologyVersion + " returned no results!");
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
    if (getTransactionPerOperation()) {
      tx = manager.getTransaction();
      tx.begin();
      manager.persist(description);
      tx.commit();
    } else {
      manager.persist(description);
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
    if (getTransactionPerOperation()) {
      tx = manager.getTransaction();
      tx.begin();
      manager.merge(description);
      tx.commit();
    } else {
      manager.merge(description);
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
    Description mu = manager.find(DescriptionJpa.class, id);
    if (getTransactionPerOperation()) {
      // remove description
      tx.begin();
      if (manager.contains(mu)) {
        manager.remove(mu);
      } else {
        manager.remove(manager.merge(mu));
      }
      tx.commit();
    } else {
      if (manager.contains(mu)) {
        manager.remove(mu);
      } else {
        manager.remove(manager.merge(mu));
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#getRelationship(java.lang
   * .String)
   */
  @Override
  public Relationship getRelationship(String id) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get relationship " + id);
    Relationship c = manager.find(RelationshipJpa.class, id);
    return c;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Relationship getRelationship(String terminologyId, String terminology,
    String terminologyVersion) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get relationship " + terminologyId + "/"
            + terminology + "/" + terminologyVersion);
    javax.persistence.Query query =
        manager
            .createQuery("select c from RelationshipJpa c where terminologyId = :terminologyId and terminologyVersion = :terminologyVersion and terminology = :terminology");
    /*
     * Try to retrieve the single expected result If zero or more than one
     * result are returned, log error and set result to null
     */
    try {
      query.setParameter("terminologyId", terminologyId);
      query.setParameter("terminology", terminology);
      query.setParameter("terminologyVersion", terminologyVersion);
      Relationship c = (Relationship) query.getSingleResult();
      return c;
    } catch (Exception e) {

      Logger.getLogger(ContentServiceJpa.class).debug(
          "Relationship query for terminologyId = " + terminologyId
              + ", terminology = " + terminology + ", terminologyVersion = "
              + terminologyVersion + " threw an exception!");
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
    if (getTransactionPerOperation()) {
      tx = manager.getTransaction();
      tx.begin();
      manager.persist(relationship);
      tx.commit();
    } else {
      manager.persist(relationship);
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
    if (getTransactionPerOperation()) {
      tx = manager.getTransaction();
      tx.begin();
      manager.merge(relationship);
      tx.commit();
    } else {
      manager.merge(relationship);
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
    Relationship mu = manager.find(RelationshipJpa.class, id);
    if (getTransactionPerOperation()) {
      // remove relationship
      tx.begin();
      if (manager.contains(mu)) {
        manager.remove(mu);
      } else {
        manager.remove(manager.merge(mu));
      }
      tx.commit();
    } else {
      if (manager.contains(mu)) {
        manager.remove(mu);
      } else {
        manager.remove(manager.merge(mu));
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
    TransitiveRelationship transitiveRelationship) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - add transitive relationship "
            + transitiveRelationship.getSuperTypeConcept() + "/"
            + transitiveRelationship.getSubTypeConcept());
    if (getTransactionPerOperation()) {
      tx = manager.getTransaction();
      tx.begin();
      manager.persist(transitiveRelationship);
      tx.commit();
    } else {
      manager.persist(transitiveRelationship);
    }
    return transitiveRelationship;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#updateTransitiveRelationship
   * (org.ihtsdo.otf.mapping.rf2.TransitiveRelationship)
   */
  @Override
  public void updateTransitiveRelationship(
    TransitiveRelationship transitiveRelationship) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - add transitive relationship "
            + transitiveRelationship.getSuperTypeConcept() + "/"
            + transitiveRelationship.getSubTypeConcept());
    if (getTransactionPerOperation()) {
      tx = manager.getTransaction();
      tx.begin();
      manager.merge(transitiveRelationship);
      tx.commit();
    } else {
      manager.merge(transitiveRelationship);
    }
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
    TransitiveRelationship mu =
        manager.find(TransitiveRelationshipJpa.class, id);
    if (getTransactionPerOperation()) {
      // remove transitiveRelationship
      tx.begin();
      if (manager.contains(mu)) {
        manager.remove(mu);
      } else {
        manager.remove(manager.merge(mu));
      }
      tx.commit();
    } else {
      if (manager.contains(mu)) {
        manager.remove(mu);
      } else {
        manager.remove(manager.merge(mu));
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#getAttributeValueRefSetMember
   * (java.lang.String)
   */
  @Override
  public AttributeValueRefSetMember getAttributeValueRefSetMember(String id)
    throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get attribute value refset member " + id);
    AttributeValueRefSetMember c =
        manager.find(AttributeValueRefSetMemberJpa.class, id);
    return c;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AttributeValueRefSetMember getAttributeValueRefSetMember(
    String terminologyId, String terminology, String terminologyVersion)
    throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get attribute value refset member " + terminologyId
            + "/" + terminology + "/" + terminologyVersion);
    javax.persistence.Query query =
        manager
            .createQuery("select c from AttributeValueRefSetMemberJpa c where terminologyId = :terminologyId and terminologyVersion = :terminologyVersion and terminology = :terminology");
    /*
     * Try to retrieve the single expected result If zero or more than one
     * result are returned, log error and set result to null
     */
    try {
      query.setParameter("terminologyId", terminologyId);
      query.setParameter("terminology", terminology);
      query.setParameter("terminologyVersion", terminologyVersion);
      AttributeValueRefSetMember c =
          (AttributeValueRefSetMember) query.getSingleResult();
      return c;
    } catch (NoResultException e) {
      return null;
      /*
       * throw new LocalException(
       * "AttributeValueRefSetMember query for terminologyId = " + terminologyId
       * + ", terminology = " + terminology + ", terminologyVersion = " +
       * terminologyVersion + " returned no results!", e);
       */
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#addAttributeValueRefSetMember
   * (org.ihtsdo.otf.mapping.rf2.AttributeValueRefSetMember)
   */
  @Override
  public AttributeValueRefSetMember addAttributeValueRefSetMember(
    AttributeValueRefSetMember attributeValueRefSetMember) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - add attribute value refset member"
            + attributeValueRefSetMember.getTerminologyId());
    if (getTransactionPerOperation()) {
      tx = manager.getTransaction();
      tx.begin();
      manager.persist(attributeValueRefSetMember);
      tx.commit();
    } else {
      manager.persist(attributeValueRefSetMember);
    }

    return attributeValueRefSetMember;
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
    AttributeValueRefSetMember attributeValueRefSetMember) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - update attribute value refset member "
            + attributeValueRefSetMember.getTerminologyId());
    if (getTransactionPerOperation()) {
      tx = manager.getTransaction();
      tx.begin();
      manager.merge(attributeValueRefSetMember);
      tx.commit();
    } else {
      manager.merge(attributeValueRefSetMember);
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
    tx = manager.getTransaction();
    // retrieve this map specialist
    AttributeValueRefSetMember mu =
        manager.find(AttributeValueRefSetMemberJpa.class, id);
    if (getTransactionPerOperation()) {
      // remove refset member
      tx.begin();
      if (manager.contains(mu)) {
        manager.remove(mu);
      } else {
        manager.remove(manager.merge(mu));
      }
      tx.commit();
    } else {
      if (manager.contains(mu)) {
        manager.remove(mu);
      } else {
        manager.remove(manager.merge(mu));
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#getComplexMapRefSetMember
   * (java.lang.String)
   */
  @Override
  public ComplexMapRefSetMember getComplexMapRefSetMember(String id)
    throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get complex map refset member " + id);
    ComplexMapRefSetMember c =
        manager.find(ComplexMapRefSetMemberJpa.class, id);
    return c;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ComplexMapRefSetMember getComplexMapRefSetMember(String terminologyId,
    String terminology, String terminologyVersion) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get complex map refset member " + terminologyId
            + "/" + terminology + "/" + terminologyVersion);
    javax.persistence.Query query =
        manager
            .createQuery("select c from ComplexMapRefSetMemberJpa c where terminologyId = :terminologyId and terminologyVersion = :terminologyVersion and terminology = :terminology");
    /*
     * Try to retrieve the single expected result If zero or more than one
     * result are returned, log error and set result to null
     */
    try {
      query.setParameter("terminologyId", terminologyId);
      query.setParameter("terminology", terminology);
      query.setParameter("terminologyVersion", terminologyVersion);
      ComplexMapRefSetMember c =
          (ComplexMapRefSetMember) query.getSingleResult();
      return c;
    } catch (NoResultException e) {
      return null;
      /*
       * throw new LocalException(
       * "ComplexMapRefSetMember query for terminologyId = " + terminologyId +
       * ", terminology = " + terminology + ", terminologyVersion = " +
       * terminologyVersion + " returned no results!", e);
       */
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
    ComplexMapRefSetMember complexMapRefSetMember) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - add complex map refset member"
            + complexMapRefSetMember.getTerminologyId());
    if (getTransactionPerOperation()) {
      tx = manager.getTransaction();
      tx.begin();
      manager.persist(complexMapRefSetMember);
      tx.commit();
    } else {
      manager.persist(complexMapRefSetMember);
    }

    return complexMapRefSetMember;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#updateComplexMapRefSetMember
   * (org.ihtsdo.otf.mapping.rf2.ComplexMapRefSetMember)
   */
  @Override
  public void updateComplexMapRefSetMember(
    ComplexMapRefSetMember complexMapRefSetMember) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - update complex map refset member "
            + complexMapRefSetMember.getTerminologyId());
    if (getTransactionPerOperation()) {
      tx = manager.getTransaction();
      tx.begin();
      manager.merge(complexMapRefSetMember);
      tx.commit();
    } else {
      manager.merge(complexMapRefSetMember);
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
    tx = manager.getTransaction();
    // retrieve this complex map ref set member
    ComplexMapRefSetMember mu =
        manager.find(ComplexMapRefSetMemberJpa.class, id);
    if (getTransactionPerOperation()) {
      // remove complex map ref set member
      tx.begin();
      if (manager.contains(mu)) {
        manager.remove(mu);
      } else {
        manager.remove(manager.merge(mu));
      }
      tx.commit();
    } else {
      if (manager.contains(mu)) {
        manager.remove(mu);
      } else {
        manager.remove(manager.merge(mu));
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.services.ContentService#getLanguageRefSetMember
   * (java.lang.String)
   */
  @Override
  public LanguageRefSetMember getLanguageRefSetMember(String id)
    throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get language refset member " + id);
    LanguageRefSetMember c = manager.find(LanguageRefSetMemberJpa.class, id);
    return c;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public LanguageRefSetMember getLanguageRefSetMember(String terminologyId,
    String terminology, String terminologyVersion) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get language refset member " + terminologyId + "/"
            + terminology + "/" + terminologyVersion);
    javax.persistence.Query query =
        manager
            .createQuery("select c from LanguageRefSetMemberJpa c where terminologyId = :terminologyId and terminologyVersion = :terminologyVersion and terminology = :terminology");
    /*
     * Try to retrieve the single expected result If zero or more than one
     * result are returned, log error and set result to null
     */
    try {
      query.setParameter("terminologyId", terminologyId);
      query.setParameter("terminology", terminology);
      query.setParameter("terminologyVersion", terminologyVersion);
      LanguageRefSetMember c = (LanguageRefSetMember) query.getSingleResult();
      return c;
    } catch (NoResultException e) {
      return null;
      /*
       * throw new LocalException(
       * "LanguageRefSetMember query for terminologyId = " + terminologyId +
       * ", terminology = " + terminology + ", terminologyVersion = " +
       * terminologyVersion + " returned no results!", e);
       */
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
    LanguageRefSetMember languageRefSetMember) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - add language refset member"
            + languageRefSetMember.getTerminologyId());
    if (getTransactionPerOperation()) {
      tx = manager.getTransaction();
      tx.begin();
      manager.persist(languageRefSetMember);
      tx.commit();
    } else {
      manager.persist(languageRefSetMember);
    }

    return languageRefSetMember;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#updateLanguageRefSetMember
   * (org.ihtsdo.otf.mapping.rf2.LanguageRefSetMember)
   */
  @Override
  public void updateLanguageRefSetMember(
    LanguageRefSetMember languageRefSetMember) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - update language refset member "
            + languageRefSetMember.getTerminologyId());

    if (getTransactionPerOperation()) {
      tx = manager.getTransaction();
      tx.begin();
      manager.merge(languageRefSetMember);
      tx.commit();
    } else {
      manager.merge(languageRefSetMember);
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
    LanguageRefSetMember mu = manager.find(LanguageRefSetMemberJpa.class, id);
    if (getTransactionPerOperation()) {
      // remove language ref set member
      tx.begin();
      if (manager.contains(mu)) {
        manager.remove(mu);
      } else {
        manager.remove(manager.merge(mu));
      }
      tx.commit();
    } else {
      if (manager.contains(mu)) {
        manager.remove(mu);
      } else {
        manager.remove(manager.merge(mu));
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#getSimpleMapRefSetMember
   * (java.lang.String)
   */
  @Override
  public SimpleMapRefSetMember getSimpleMapRefSetMember(String id)
    throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get simple map refset member " + id);
    SimpleMapRefSetMember c = manager.find(SimpleMapRefSetMemberJpa.class, id);
    return c;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SimpleMapRefSetMember getSimpleMapRefSetMember(String terminologyId,
    String terminology, String terminologyVersion) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get simple map refset member " + terminologyId + "/"
            + terminology + "/" + terminologyVersion);
    javax.persistence.Query query =
        manager
            .createQuery("select c from SimpleMapRefSetMemberJpa c where terminologyId = :terminologyId and terminologyVersion = :terminologyVersion and terminology = :terminology");
    /*
     * Try to retrieve the single expected result If zero or more than one
     * result are returned, log error and set result to null
     */
    try {
      query.setParameter("terminologyId", terminologyId);
      query.setParameter("terminology", terminology);
      query.setParameter("terminologyVersion", terminologyVersion);
      SimpleMapRefSetMember c = (SimpleMapRefSetMember) query.getSingleResult();
      return c;
    } catch (NoResultException e) {
      return null;
      /*
       * throw new LocalException(
       * "SimpleMapRefSetMember query for terminologyId = " + terminologyId +
       * ", terminology = " + terminology + ", terminologyVersion = " +
       * terminologyVersion + " returned no results!", e);
       */
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
    SimpleMapRefSetMember simpleMapRefSetMember) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - add simple map refset member"
            + simpleMapRefSetMember.getTerminologyId());
    if (getTransactionPerOperation()) {
      tx = manager.getTransaction();
      tx.begin();
      manager.persist(simpleMapRefSetMember);
      tx.commit();
    } else {
      manager.persist(simpleMapRefSetMember);
    }

    return simpleMapRefSetMember;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#updateSimpleMapRefSetMember
   * (org.ihtsdo.otf.mapping.rf2.SimpleMapRefSetMember)
   */
  @Override
  public void updateSimpleMapRefSetMember(
    SimpleMapRefSetMember simpleMapRefSetMember) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - update simple map refset member "
            + simpleMapRefSetMember.getTerminologyId());

    if (getTransactionPerOperation()) {
      tx = manager.getTransaction();
      tx.begin();
      manager.merge(simpleMapRefSetMember);
      tx.commit();
    } else {
      manager.merge(simpleMapRefSetMember);
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
    SimpleMapRefSetMember mu = manager.find(SimpleMapRefSetMemberJpa.class, id);
    if (getTransactionPerOperation()) {
      // remove simple map ref set member
      tx.begin();
      if (manager.contains(mu)) {
        manager.remove(mu);
      } else {
        manager.remove(manager.merge(mu));
      }
      tx.commit();
    } else {
      if (manager.contains(mu)) {
        manager.remove(mu);
      } else {
        manager.remove(manager.merge(mu));
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.services.ContentService#getSimpleRefSetMember(
   * java.lang.String)
   */
  @Override
  public SimpleRefSetMember getSimpleRefSetMember(String id) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get simple refset member " + id);
    SimpleRefSetMember c = manager.find(SimpleRefSetMemberJpa.class, id);
    return c;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SimpleRefSetMember getSimpleRefSetMember(String terminologyId,
    String terminology, String terminologyVersion) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get simple refset member " + terminologyId + "/"
            + terminology + "/" + terminologyVersion);
    javax.persistence.Query query =
        manager
            .createQuery("select c from SimpleRefSetMemberJpa c where terminologyId = :terminologyId and terminologyVersion = :terminologyVersion and terminology = :terminology");
    /*
     * Try to retrieve the single expected result If zero or more than one
     * result are returned, log error and set result to null
     */
    try {
      query.setParameter("terminologyId", terminologyId);
      query.setParameter("terminology", terminology);
      query.setParameter("terminologyVersion", terminologyVersion);
      SimpleRefSetMember c = (SimpleRefSetMember) query.getSingleResult();
      return c;
    } catch (NoResultException e) {
      return null;
      /*
       * throw new LocalException(
       * "SimpleRefSetMember query for terminologyId = " + terminologyId +
       * ", terminology = " + terminology + ", terminologyVersion = " +
       * terminologyVersion + " returned no results!", e);
       */
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
  public SimpleRefSetMember addSimpleRefSetMember(
    SimpleRefSetMember simpleRefSetMember) throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - add simple refset member"
            + simpleRefSetMember.getTerminologyId());
    if (getTransactionPerOperation()) {
      tx = manager.getTransaction();
      tx.begin();
      manager.persist(simpleRefSetMember);
      tx.commit();
    } else {
      manager.persist(simpleRefSetMember);
    }

    return simpleRefSetMember;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#updateSimpleRefSetMember
   * (org.ihtsdo.otf.mapping.rf2.SimpleRefSetMember)
   */
  @Override
  public void updateSimpleRefSetMember(SimpleRefSetMember simpleRefSetMember)
    throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - update simple refset member "
            + simpleRefSetMember.getTerminologyId());
    if (getTransactionPerOperation()) {
      tx = manager.getTransaction();
      tx.begin();
      manager.merge(simpleRefSetMember);
      tx.commit();
    } else {
      manager.merge(simpleRefSetMember);
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
    SimpleRefSetMember mu = manager.find(SimpleRefSetMemberJpa.class, id);
    if (getTransactionPerOperation()) {
      // remove simple ref set member
      tx.begin();
      if (manager.contains(mu)) {
        manager.remove(mu);
      } else {
        manager.remove(manager.merge(mu));
      }
      tx.commit();
    } else {
      if (manager.contains(mu)) {
        manager.remove(mu);
      } else {
        manager.remove(manager.merge(mu));
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
      results.addSearchResult(sr);
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
    String terminology, String terminologyVersion, PfsParameter pfs)
    throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - find descendants " + terminologyId + "/"
            + terminology + "/" + terminologyVersion);

    if (pfs  != null && pfs.getQueryRestriction() != null) {
      throw new IllegalArgumentException(
          "Query restriction is not implemented for this call: "
              + pfs.getQueryRestriction());
    }
    SearchResultList searchResultList = new SearchResultListJpa();
    javax.persistence.Query query =
        manager
            .createQuery("select sub from TransitiveRelationshipJpa tr, ConceptJpa super, ConceptJpa sub "
                + " where super.terminologyVersion = :terminologyVersion "
                + " and super.terminology = :terminology "
                + " and super.terminologyId = :terminologyId"
                + " and tr.superTypeConcept = super"
                + " and tr.subTypeConcept = sub");
    query.setParameter("terminology", terminology);
    query.setParameter("terminologyVersion", terminologyVersion);
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
      searchResultList.addSearchResult(searchResult);
    }
    // return the search result list
    return searchResultList;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.services.ContentService#findAncestorConcepts(java.lang.String, java.lang.String, java.lang.String, org.ihtsdo.otf.ts.helpers.PfsParameter)
   */
  @SuppressWarnings("unchecked")
  @Override
  public SearchResultList findAncestorConcepts(String terminologyId,
    String terminology, String terminologyVersion, PfsParameter pfs)
    throws Exception {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - find ancestors " + terminologyId + "/"
            + terminology + "/" + terminologyVersion);

    if (pfs != null && pfs.getQueryRestriction() != null) {
      throw new IllegalArgumentException(
          "Query restriction is not implemented for this call: "
              + pfs.getQueryRestriction());
    }
    SearchResultList searchResultList = new SearchResultListJpa();
    javax.persistence.Query query =
        manager
            .createQuery("select super from TransitiveRelationshipJpa tr, ConceptJpa super, ConceptJpa sub "
                + " where sub.terminologyVersion = :terminologyVersion "
                + " and sub.terminology = :terminology "
                + " and sub.terminologyId = :terminologyId"
                + " and tr.superTypeConcept = super"
                + " and tr.subTypeConcept = sub");
    query.setParameter("terminology", terminology);
    query.setParameter("terminologyVersion", terminologyVersion);
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
      searchResultList.addSearchResult(searchResult);
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
    String terminologyVersion) {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get all relationship terminology ids " + terminology
            + "/" + terminologyVersion);
    javax.persistence.Query query =
        manager
            .createQuery(
                "select c.terminologyId from RelationshipJpa c where terminology=:terminology and terminologyVersion=:terminologyVersion")
            .setParameter("terminology", terminology)
            .setParameter("terminologyVersion", terminologyVersion);

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
    String terminologyVersion) {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get all description terminology ids " + terminology
            + "/" + terminologyVersion);
    javax.persistence.Query query =
        manager
            .createQuery(
                "select c.terminologyId from DescriptionJpa c where terminology=:terminology and terminologyVersion=:terminologyVersion")
            .setParameter("terminology", terminology)
            .setParameter("terminologyVersion", terminologyVersion);

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
    String terminology, String terminologyVersion) {
    Logger.getLogger(ContentServiceJpa.class).debug(
        "Content Service - get all language refset member terminology ids "
            + terminology + "/" + terminologyVersion);
    javax.persistence.Query query =
        manager
            .createQuery(
                "select c.terminologyId from LanguageRefSetMemberJpa c where terminology=:terminology and terminologyVersion=:terminologyVersion")
            .setParameter("terminology", terminology)
            .setParameter("terminologyVersion", terminologyVersion);

    List<String> terminologyIds = query.getResultList();
    Set<String> terminologyIdSet = new HashSet<>(terminologyIds);
    return terminologyIdSet;

  }

  /**
   * Clear transitive closure.
   *
   * @param terminology the terminology
   * @param terminologyVersion
   * @throws Exception
   */
  @Override
  public void clearTransitiveClosure(String terminology,
    String terminologyVersion) throws Exception {
    Logger.getLogger(this.getClass()).info(
        "Content Service - Removing transitive closure data for " + terminology
            + ", " + terminologyVersion);

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
            .createQuery("select tr from TransitiveRelationshipJpa tr where terminology = :terminology and terminologyVersion = :terminologyVersion");
    query.setParameter("terminology", terminology);
    query.setParameter("terminologyVersion", terminologyVersion);
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

  @Override
  public void clearConcepts(String terminology, String terminologyVersion) {
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
  private void applyPfsToLuceneQuery(Class<?> clazz,
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
  private <T> Comparator<T> getPfsComparator(Class<T> clazz, PfsParameter pfs)
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
}
