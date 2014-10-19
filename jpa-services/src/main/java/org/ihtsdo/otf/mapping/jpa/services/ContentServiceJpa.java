package org.ihtsdo.otf.mapping.jpa.services;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.NoResultException;

import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
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
import org.ihtsdo.otf.mapping.helpers.ConceptList;
import org.ihtsdo.otf.mapping.helpers.ConceptListJpa;
import org.ihtsdo.otf.mapping.helpers.LocalException;
import org.ihtsdo.otf.mapping.helpers.PfsParameter;
import org.ihtsdo.otf.mapping.helpers.SearchResult;
import org.ihtsdo.otf.mapping.helpers.SearchResultJpa;
import org.ihtsdo.otf.mapping.helpers.SearchResultList;
import org.ihtsdo.otf.mapping.helpers.SearchResultListJpa;
import org.ihtsdo.otf.mapping.rf2.AttributeValueRefSetMember;
import org.ihtsdo.otf.mapping.rf2.ComplexMapRefSetMember;
import org.ihtsdo.otf.mapping.rf2.Concept;
import org.ihtsdo.otf.mapping.rf2.Description;
import org.ihtsdo.otf.mapping.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.mapping.rf2.Relationship;
import org.ihtsdo.otf.mapping.rf2.SimpleMapRefSetMember;
import org.ihtsdo.otf.mapping.rf2.SimpleRefSetMember;
import org.ihtsdo.otf.mapping.rf2.TransitiveRelationship;
import org.ihtsdo.otf.mapping.rf2.jpa.AttributeValueRefSetMemberJpa;
import org.ihtsdo.otf.mapping.rf2.jpa.ComplexMapRefSetMemberJpa;
import org.ihtsdo.otf.mapping.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.mapping.rf2.jpa.DescriptionJpa;
import org.ihtsdo.otf.mapping.rf2.jpa.LanguageRefSetMemberJpa;
import org.ihtsdo.otf.mapping.rf2.jpa.RelationshipJpa;
import org.ihtsdo.otf.mapping.rf2.jpa.SimpleMapRefSetMemberJpa;
import org.ihtsdo.otf.mapping.rf2.jpa.SimpleRefSetMemberJpa;
import org.ihtsdo.otf.mapping.rf2.jpa.TransitiveRelationshipJpa;
import org.ihtsdo.otf.mapping.services.ContentService;
import org.ihtsdo.otf.mapping.services.MetadataService;

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

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.services.ContentService#close()
   */
  @SuppressWarnings("unchecked")
  @Override
  public ConceptList getConcepts() throws Exception {
    List<Concept> m = null;

    javax.persistence.Query query =
        manager.createQuery("select m from ConceptJpa m");

    m = query.getResultList();
    ConceptListJpa ConceptList = new ConceptListJpa();
    ConceptList.setConcepts(m);
    ConceptList.setTotalCount(m.size());
    return ConceptList;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#getConcept(java.lang.String)
   */
  @Override
  public Concept getConcept(Long id) throws Exception {
    Concept c = manager.find(ConceptJpa.class, id);
    return c;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Concept getConcept(String terminologyId, String terminology,
    String terminologyVersion) throws Exception {
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
      Concept c = (Concept) query.getSingleResult();
      return c;
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
   * org.ihtsdo.otf.mapping.services.ContentService#getAllConcepts(java.lang
   * .String, java.lang.String)
   */
  @SuppressWarnings("unchecked")
  @Override
  public ConceptList getAllConcepts(String terminology,
    String terminologyVersion) {
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
    Description c = manager.find(DescriptionJpa.class, id);
    return c;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Description getDescription(String terminologyId, String terminology,
    String terminologyVersion) throws Exception {
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
      Description c = (Description) query.getSingleResult();
      return c;
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
    Relationship c = manager.find(RelationshipJpa.class, id);
    return c;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#getRelationshipId(java.lang
   * .String, java.lang.String, java.lang.String)
   */
  @Override
  public String getRelationshipId(String terminologyId, String terminology,
    String terminologyVersion) throws Exception {
    javax.persistence.Query query =
        manager
            .createQuery(
                "select r.id from RelationshipJpa r where terminologyId=:terminologyId and terminology=:terminology and terminologyVersion=:terminologyVersion")
            .setParameter("terminologyId", terminologyId)
            .setParameter("terminology", terminology)
            .setParameter("terminologyVersion", terminologyVersion);

    try {
      String relationshipId = (String) query.getSingleResult();
      return relationshipId;
    } catch (NoResultException e) {
      Logger.getLogger(ContentServiceJpa.class).info(
          "Could not find relationship id for" + terminologyId
              + " for terminology " + terminology + " and version "
              + terminologyVersion);
      return null;
    } catch (Exception e) {
      Logger.getLogger(ContentServiceJpa.class).info(
          "Unexpected exception retrieving relationship id for" + terminologyId
              + " for terminology " + terminology + " and version "
              + terminologyVersion);
      return null;
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Relationship getRelationship(String terminologyId, String terminology,
    String terminologyVersion) throws Exception {
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

      Logger.getLogger(ContentServiceJpa.class).info(
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

    tx = manager.getTransaction();

    // retrieve this map specialist
    AttributeValueRefSetMember mu =
        manager.find(AttributeValueRefSetMemberJpa.class, id);

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
   * org.ihtsdo.otf.mapping.services.ContentService#getComplexMapRefSetMember
   * (java.lang.String)
   */
  @Override
  public ComplexMapRefSetMember getComplexMapRefSetMember(String id)
    throws Exception {
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
    LanguageRefSetMember c = manager.find(LanguageRefSetMemberJpa.class, id);
    return c;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public LanguageRefSetMember getLanguageRefSetMember(String terminologyId,
    String terminology, String terminologyVersion) throws Exception {
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
    SimpleMapRefSetMember c = manager.find(SimpleMapRefSetMemberJpa.class, id);
    return c;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SimpleMapRefSetMember getSimpleMapRefSetMember(String terminologyId,
    String terminology, String terminologyVersion) throws Exception {
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
    SimpleRefSetMember c = manager.find(SimpleRefSetMemberJpa.class, id);
    return c;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SimpleRefSetMember getSimpleRefSetMember(String terminologyId,
    String terminology, String terminologyVersion) throws Exception {
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
   * @see org.ihtsdo.otf.mapping.services.ContentService#findConcepts(java.lang.
   * String )
   */
  @Override
  public SearchResultList findConceptsForQuery(String searchString,
    PfsParameter pfsParameter) throws Exception {

    SearchResultList results = new SearchResultListJpa();

    FullTextEntityManager fullTextEntityManager =
        Search.getFullTextEntityManager(manager);
    SearchFactory searchFactory = fullTextEntityManager.getSearchFactory();
    Query luceneQuery;

    try {

      // if the field is not indicated in the URL
      if (searchString.indexOf(':') == -1) {
        MultiFieldQueryParser queryParser =
            new MultiFieldQueryParser(Version.LUCENE_36,
                fieldNames.toArray(new String[0]),
                searchFactory.getAnalyzer(ConceptJpa.class));
        queryParser.setAllowLeadingWildcard(false);
        luceneQuery = queryParser.parse(searchString);
        // index field is indicated in the URL with a ':' separating
        // field and value
      } else {
        QueryParser queryParser =
            new QueryParser(Version.LUCENE_36, "summary",
                searchFactory.getAnalyzer(ConceptJpa.class));
        luceneQuery = queryParser.parse(searchString);
      }
    } catch (ParseException e) {
      throw new LocalException(
          "The specified search terms cannot be parsed.  Please check syntax and try again.");
    }

    FullTextQuery fullTextQuery =
        fullTextEntityManager
            .createFullTextQuery(luceneQuery, ConceptJpa.class);

    // set paging/filtering/sorting if indicated
    if (pfsParameter != null) {

      // if start index and max results are set, set paging
      if (pfsParameter.getStartIndex() != -1
          && pfsParameter.getMaxResults() != -1) {
        fullTextQuery.setFirstResult(pfsParameter.getStartIndex());
        fullTextQuery.setMaxResults(pfsParameter.getMaxResults());
      }

      // if sort field is specified, set sort key
      if (pfsParameter.getSortField() != null
          && !pfsParameter.getSortField().isEmpty()) {

        // check that specified sort field exists on Concept and is
        // a string
        if (Concept.class.getDeclaredField(pfsParameter.getSortField())
            .getType().equals(String.class)) {
          fullTextQuery.setSort(new Sort(new SortField(pfsParameter
              .getSortField(), SortField.STRING)));

        } else {
          throw new Exception(
              "Concept query specified a field that does not exist or is not a string");
        }

      }

    }

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
    String terminology, String terminologyVersion, PfsParameter pfsParameter)
    throws Exception {

    Logger.getLogger(ContentServiceJpa.class).info(
        "findDescendantConcepts called: " + terminologyId + ", " + terminology
            + ", " + terminologyVersion);

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
    if (pfsParameter != null
        && (pfsParameter.getSortField() != null && !pfsParameter.getSortField()
            .isEmpty())) {
      // check that specified sort field exists on Concept and is
      // a string
      final Field sortField =
          Concept.class.getDeclaredField(pfsParameter.getSortField());
      if (!sortField.getType().equals(String.class)) {

        throw new Exception(
            "findDescendantConcepts error:  Referenced sort field is not of type String");
      }
      // allow the field to access the Concept values
      sortField.setAccessible(true);

      // sort the list - UNTESTED
      Collections.sort(descendants, new Comparator<Concept>() {
        @Override
        public int compare(Concept c1, Concept c2) {

          // if an exception is returned, simply pass equality
          try {
            return ((String) sortField.get(c1)).compareTo((String) sortField
                .get(c2));
          } catch (Exception e) {
            return 0;
          }
        }
      });

      // get the start and end indexes based on paging parameters
      int startIndex = 0;
      int toIndex = descendants.size();
      if (pfsParameter != null) {
        startIndex = pfsParameter.getStartIndex();
        toIndex =
            Math.min(descendants.size(), startIndex + pfsParameter.getMaxResults());
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
        "Removing transitive closure data for " + terminology + ", "
            + terminologyVersion);

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

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#computeTransitiveClosure
   * (java.lang.String, java.lang.String)
   */
  @Override
  public void computeTransitiveClosure(String rootId, String terminology,
    String terminologyVersion) throws Exception {
    //
    // Check assumptions/prerequisites
    //
    Logger.getLogger(this.getClass()).info(
        "Start computing transitive closure ... " + new Date());

    // Disable transaction per operation
    boolean currentTransactionStrategy = getTransactionPerOperation();
    if (getTransactionPerOperation()) {
      this.setTransactionPerOperation(false);
    }

    //
    // Initialize rels
    // id effectiveTime active moduleId sourceId destinationId relationshipGroup
    // typeId characteristicTypeId modifierId
    //
    Logger.getLogger(this.getClass()).info(
        "  Initialize relationships ... " + new Date());

    MetadataService metadataService = new MetadataServiceJpa();

    // NOTE: assumes single hierarchical rel
    String inferredCharType = "900000000000011006";
    String isaRel =
        metadataService
            .getHierarchicalRelationshipTypes(terminology, terminologyVersion)
            .keySet().iterator().next();
    // Skip non isa
    // Skip non-inferred
    javax.persistence.Query query =
        manager
            .createQuery(
                "select r from RelationshipJpa r where active=1 and terminology=:terminology and terminologyVersion=:terminologyVersion and typeId=:typeId and characteristicTypeId=:characteristicTypeId")
            .setParameter("terminology", terminology)
            .setParameter("terminologyVersion", terminologyVersion)
            .setParameter("typeId", isaRel)
            .setParameter("characteristicTypeId", inferredCharType);

    @SuppressWarnings("unchecked")
    List<Relationship> rels = query.getResultList();
    Map<String, Set<String>> parChd = new HashMap<>();
    int ct = 0;
    for (Relationship rel : rels) {
      String chd = rel.getSourceConcept().getTerminologyId();
      String par = rel.getDestinationConcept().getTerminologyId();
      if (!parChd.containsKey(par)) {
        parChd.put(par, new HashSet<String>());
      }
      Set<String> children = parChd.get(par);
      children.add(chd);
      ct++;
    }
    Logger.getLogger(this.getClass()).info("    ct = " + ct);

    // cache concepts
    Logger.getLogger(this.getClass()).info(
        "  Cache concepts ... " + new Date());
    query =
        manager
            .createQuery(
                "select c.id, c.terminologyId, c.terminology, c.terminologyVersion from ConceptJpa c");

    @SuppressWarnings("unchecked")
    List<Object[]> results = query.getResultList();
    Map<String,Long> conceptCache = new HashMap<>();
    ct = 0;
    for (Object[] result : results) {
      final String key = result[1].toString() + result[2] + result[3];
      conceptCache.put(key, (Long)result[0]);
      ct++;
    }
    Logger.getLogger(this.getClass()).info("    ct = " + ct);
    //
    // Create transitive closure rels
    //
    Logger.getLogger(this.getClass()).info(
        "  Create transitive closure rels... " + new Date());
    ct = 0;
    beginTransaction();
    for (String code : parChd.keySet()) {
      if (rootId.equals(code)) {
        continue;
      }
      ct++;
      Set<String> descs = getDescendants(code, new HashSet<String>(), parChd);
      for (String desc : descs) {
        final TransitiveRelationship tr = new TransitiveRelationshipJpa();
        final String superKey = code+terminology+terminologyVersion;
        final String subKey = desc+terminology+terminologyVersion;
        tr.setSuperTypeConcept(getConcept(conceptCache.get(superKey)));
        tr.setSubTypeConcept(getConcept(conceptCache.get(subKey)));
        tr.setActive(true);
        tr.setEffectiveTime(new Date());
        tr.setLabel("");
        tr.setModuleId("");
        tr.setTerminologyId("");
        tr.setTerminology(terminology);
        tr.setTerminologyVersion(terminologyVersion);
        addTransitiveRelationship(tr);
      }
      if (ct % 500 == 0) {
        Logger.getLogger(this.getClass()).info(
            "      " + ct + " codes processed ..." + new Date());
        commit();
        beginTransaction();
      }
    }
    commit();

    Logger.getLogger(this.getClass()).info(
        "Finished computing transitive closure ... " + new Date());
    // set the transaction strategy based on status starting this routine
    setTransactionPerOperation(currentTransactionStrategy);
  }

  /**
   * Returns the descendants.
   *
   * @param par the par
   * @param seen the seen
   * @param parChd the par chd
   * @return the descendants
   */
  public Set<String> getDescendants(String par, Set<String> seen,
    Map<String, Set<String>> parChd) {
    Logger.getLogger(this.getClass()).debug("  Get descendants for " + par);
    // if we've seen this node already, children are accounted for - bail
    if (seen.contains(par)) {
      return new HashSet<>();
    }
    seen.add(par);

    // Get Children of this node
    Set<String> children = parChd.get(par);

    // If this is a leaf node, bail
    if (children == null || children.isEmpty()) {
      return new HashSet<>();
    }
    // Iterate through children, mark as descendant and recursively call
    Set<String> descendants = new HashSet<>();
    for (String chd : children) {
      descendants.add(chd);
      descendants.addAll(getDescendants(chd, seen, parChd));
    }
    Logger.getLogger(this.getClass()).debug("    descCt = " + descendants.size());

    return descendants;
  }
}
