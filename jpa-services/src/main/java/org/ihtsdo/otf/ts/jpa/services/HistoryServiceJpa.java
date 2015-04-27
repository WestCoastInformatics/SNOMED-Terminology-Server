/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.jpa.services;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;

import org.apache.log4j.Logger;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.ihtsdo.otf.ts.ReleaseInfo;
import org.ihtsdo.otf.ts.helpers.AssociationReferenceRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.AssociationReferenceRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.AttributeValueRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.AttributeValueRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.ComplexMapRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ComplexMapRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ConceptListJpa;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.DescriptionList;
import org.ihtsdo.otf.ts.helpers.DescriptionListJpa;
import org.ihtsdo.otf.ts.helpers.DescriptionTypeRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.DescriptionTypeRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.LanguageRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.LanguageRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.LocalException;
import org.ihtsdo.otf.ts.helpers.ModuleDependencyRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ModuleDependencyRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.PfsParameter;
import org.ihtsdo.otf.ts.helpers.RefsetDescriptorRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.RefsetDescriptorRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.RelationshipList;
import org.ihtsdo.otf.ts.helpers.RelationshipListJpa;
import org.ihtsdo.otf.ts.helpers.ReleaseInfoList;
import org.ihtsdo.otf.ts.helpers.ReleaseInfoListJpa;
import org.ihtsdo.otf.ts.helpers.SimpleMapRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.SimpleMapRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.SimpleRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.SimpleRefSetMemberListJpa;
import org.ihtsdo.otf.ts.jpa.ReleaseInfoJpa;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceDescriptionRefSetMember;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceRefSetMember;
import org.ihtsdo.otf.ts.rf2.AttributeValueConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.AttributeValueDescriptionRefSetMember;
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
import org.ihtsdo.otf.ts.services.HistoryService;

/**
 * JPA enabled implmementation of {@link HistoryService}.
 */
public class HistoryServiceJpa extends ContentServiceJpa implements
    HistoryService {

  /**
   * Instantiates an empty {@link HistoryServiceJpa}.
   *
   * @throws Exception the exception
   */
  public HistoryServiceJpa() throws Exception {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#getConceptsModifiedSinceDate
   * (java.lang.String, java.util.Date,
   * org.ihtsdo.otf.mapping.helpers.PfsParameter)
   */
  @Override
  public ConceptList findConceptsModifiedSinceDate(String terminology,
    Date date, PfsParameter pfs) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find concepts modified since date " + terminology
            + "," + date);
    int[] totalCt = new int[1];
    List<ConceptJpa> results =
        findModifiedSinceDateFromLucene(ConceptJpa.class, terminology, date,
            pfs, totalCt);
    ConceptList resultList = new ConceptListJpa();
    resultList.setTotalCount(totalCt[0]);
    for (ConceptJpa concept : results) {
      resultList.addObject(concept);
    }
    return resultList;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.HistoryService#findConceptRevisions(java.lang
   * .Long, java.util.Date, java.util.Date, boolean,
   * org.ihtsdo.otf.ts.helpers.PfsParameter)
   */
  @Override
  public ConceptList findConceptRevisions(Long id, Date startDate,
    Date endDate, PfsParameter pfs) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find concept revisions " + id + "," + startDate
            + ", " + endDate);

    List<ConceptJpa> revisions =
        findRevisions(id, ConceptJpa.class, startDate, endDate, pfs);
    // Repackage as ConceptList
    ConceptList results = new ConceptListJpa();
    results.setTotalCount(revisions.size());
    for (ConceptJpa concept : revisions) {
      results.addObject(concept);
    }
    return results;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.HistoryService#findConceptReleaseRevision(java
   * .lang.Long, java.lang.String)
   */
  @Override
  public Concept findConceptReleaseRevision(Long id, Date release)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find concept release revision " + id + ","
            + ConfigUtility.DATE_FORMAT.format(release));

    ConceptJpa revision = findReleaseRevision(id, release, ConceptJpa.class);
    return revision;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#getDescriptionsModifiedSinceDate
   * (java.lang.String, java.util.Date)
   */
  @Override
  public DescriptionList findDescriptionsModifiedSinceDate(String terminology,
    Date date, PfsParameter pfs) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find descriptions modified since date "
            + terminology + ", " + date);
    int[] totalCt = new int[1];
    List<DescriptionJpa> results =
        findModifiedSinceDateFromQuery(DescriptionJpa.class, terminology, date,
            pfs, totalCt);
    DescriptionList resultList = new DescriptionListJpa();
    resultList.setTotalCount(totalCt[0]);
    for (DescriptionJpa description : results) {
      resultList.addObject(description);
    }
    return resultList;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.HistoryService#findDescriptionRevisions(java
   * .lang.Long, java.util.Date, java.util.Date, boolean,
   * org.ihtsdo.otf.ts.helpers.PfsParameter)
   */
  @Override
  public DescriptionList findDescriptionRevisions(Long id, Date startDate,
    Date endDate, PfsParameter pfs) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find description revisions " + id + "," + startDate
            + ", " + endDate);
    List<DescriptionJpa> revisions =
        findRevisions(id, DescriptionJpa.class, startDate, endDate, pfs);
    // Repackage as DescriptionList
    DescriptionList results = new DescriptionListJpa();
    results.setTotalCount(revisions.size());
    for (DescriptionJpa Description : revisions) {
      results.addObject(Description);
    }
    return results;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.HistoryService#findDescriptionReleaseRevision
   * (java.lang.Long, java.lang.String)
   */
  @Override
  public Description findDescriptionReleaseRevision(Long id, Date release)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find description release revision " + id + ","
            + release);

    DescriptionJpa revision =
        findReleaseRevision(id, release, DescriptionJpa.class);
    return revision;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.services.ContentService#
   * getRelationshipsModifiedSinceDate(java.lang.String, java.util.Date)
   */
  @Override
  public RelationshipList findRelationshipsModifiedSinceDate(
    String terminology, Date date, PfsParameter pfs) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find relationships modified since date "
            + terminology + ", " + date);
    int[] totalCt = new int[1];
    List<RelationshipJpa> results =
        findModifiedSinceDateFromQuery(RelationshipJpa.class, terminology,
            date, pfs, totalCt);
    RelationshipList resultList = new RelationshipListJpa();
    resultList.setTotalCount(totalCt[0]);
    for (RelationshipJpa relationship : results) {
      resultList.addObject(relationship);
    }
    return resultList;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.HistoryService#findRelationshipRevisions(java
   * .lang.Long, java.util.Date, java.util.Date, boolean,
   * org.ihtsdo.otf.ts.helpers.PfsParameter)
   */
  @Override
  public RelationshipList findRelationshipRevisions(Long id, Date startDate,
    Date endDate, PfsParameter pfs) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find relationship revisions " + id + "," + startDate
            + ", " + endDate);
    List<RelationshipJpa> revisions =
        findRevisions(id, RelationshipJpa.class, startDate, endDate, pfs);
    // Repackage as RelationshipList
    RelationshipList results = new RelationshipListJpa();
    results.setTotalCount(revisions.size());
    for (RelationshipJpa Relationship : revisions) {
      results.addObject(Relationship);
    }
    return results;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.HistoryService#findRelationshipReleaseRevision
   * (java.lang.Long, java.lang.String)
   */
  @Override
  public Relationship findRelationshipReleaseRevision(Long id, Date release)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find relationship release revision " + id + ","
            + release);

    RelationshipJpa revision =
        findReleaseRevision(id, release, RelationshipJpa.class);
    return revision;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.services.ContentService#
   * getLanguageRefSetMembersModifiedSinceDate(java.lang.String, java.util.Date)
   */
  @Override
  public LanguageRefSetMemberList findLanguageRefSetMembersModifiedSinceDate(
    String terminology, Date date, PfsParameter pfs) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find language refset members modified since date "
            + terminology + ", " + date);
    int[] totalCt = new int[1];
    List<LanguageRefSetMemberJpa> results =
        findModifiedSinceDateFromQuery(LanguageRefSetMemberJpa.class,
            terminology, date, pfs, totalCt);
    LanguageRefSetMemberList resultList = new LanguageRefSetMemberListJpa();
    resultList.setTotalCount(totalCt[0]);
    for (LanguageRefSetMemberJpa member : results) {
      resultList.addObject(member);
    }
    return resultList;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.HistoryService#findLanguageRefSetMemberRevisions
   * (java.lang.Long, java.util.Date, java.util.Date, boolean,
   * org.ihtsdo.otf.ts.helpers.PfsParameter)
   */
  @Override
  public LanguageRefSetMemberList findLanguageRefSetMemberRevisions(Long id,
    Date startDate, Date endDate, PfsParameter pfs) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find language revsest revisions" + id + ","
            + startDate + ", " + endDate);
    List<LanguageRefSetMemberJpa> revisions =
        findRevisions(id, LanguageRefSetMemberJpa.class, startDate, endDate,
            pfs);
    // Repackage as LanguageRefSetMemberList
    LanguageRefSetMemberList results = new LanguageRefSetMemberListJpa();
    results.setTotalCount(revisions.size());
    for (LanguageRefSetMemberJpa LanguageRefSetMember : revisions) {
      results.addObject(LanguageRefSetMember);
    }
    return results;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.HistoryService#
   * findLanguageRefSetMemberReleaseRevision(java.lang.Long, java.lang.String)
   */
  @Override
  public LanguageRefSetMember findLanguageRefSetMemberReleaseRevision(Long id,
    Date release) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find language refset member release revision " + id
            + "," + release);

    LanguageRefSetMemberJpa revision =
        findReleaseRevision(id, release, LanguageRefSetMemberJpa.class);
    return revision;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.services.ContentService#
   * getAbstractAssociationReferenceRefSetMembersModifiedSinceDate
   * (java.lang.String, java.util.Date)
   */
  @SuppressWarnings("rawtypes")
  @Override
  public AssociationReferenceRefSetMemberList findAssociationReferenceRefSetMembersModifiedSinceDate(
    String terminology, Date date, PfsParameter pfs) throws Exception {
    Logger
        .getLogger(getClass())
        .debug(
            "History Service - find abstractAssociationReferenceRefSetMembers modified since date "
                + terminology + ", " + date);
    int[] totalCt = new int[1];
    List<AbstractAssociationReferenceRefSetMemberJpa> results =
        findModifiedSinceDateFromQuery(
            AbstractAssociationReferenceRefSetMemberJpa.class, terminology,
            date, pfs, totalCt);
    AssociationReferenceRefSetMemberList resultList =
        new AssociationReferenceRefSetMemberListJpa();
    resultList.setTotalCount(totalCt[0]);
    for (AssociationReferenceRefSetMember<?> abstractAssociationReferenceRefSetMember : results) {
      resultList.addObject(abstractAssociationReferenceRefSetMember);
    }
    return resultList;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.HistoryService#
   * findAbstractAssociationReferenceRefSetMemberRevisions(java .lang.Long,
   * java.util.Date, java.util.Date, boolean,
   * org.ihtsdo.otf.ts.helpers.PfsParameter)
   */
  @SuppressWarnings({
      "rawtypes", "unchecked"
  })
  @Override
  public AssociationReferenceRefSetMemberList findAssociationReferenceRefSetMemberRevisions(
    Long id, Date startDate, Date endDate, PfsParameter pfs) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find abstractAssociationReferenceRefSetMember revisions "
            + id + "," + startDate + ", " + endDate);
    List<AbstractAssociationReferenceRefSetMemberJpa> revisions =
        findRevisions(id, AbstractAssociationReferenceRefSetMemberJpa.class,
            startDate, endDate, pfs);

    // Repackage as AbstractAssociationReferenceRefSetMemberList
    AssociationReferenceRefSetMemberList results =
        new AssociationReferenceRefSetMemberListJpa();
    results.setTotalCount(revisions.size());
    for (AssociationReferenceRefSetMember<? extends Component> associationReferenceRefSetMember : revisions) {
      results.addObject(associationReferenceRefSetMember);
    }
    return results;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.HistoryService#
   * findAbstractAssociationReferenceRefSetMemberReleaseRevision
   * (java.lang.Long, java.lang.String)
   */
  @SuppressWarnings("unchecked")
  @Override
  public AssociationReferenceRefSetMember<?> findAssociationReferenceRefSetMemberReleaseRevision(
    Long id, Date release) throws Exception {
    Logger
        .getLogger(getClass())
        .debug(
            "History Service - find abstractAssociationReferenceRefSetMember release revision "
                + id + "," + release);

    AbstractAssociationReferenceRefSetMemberJpa<? extends Component> revision =
        findReleaseRevision(id, release,
            AbstractAssociationReferenceRefSetMemberJpa.class);
    return revision;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.services.ContentService#
   * getAttributeValueRefSetMembersModifiedSinceDate(java.lang.String,
   * java.util.Date)
   */
  @SuppressWarnings({
    "rawtypes"
  })
  @Override
  public AttributeValueRefSetMemberList findAttributeValueRefSetMembersModifiedSinceDate(
    String terminology, Date date, PfsParameter pfs) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find attributeValueRefSetMembers modified since date "
            + terminology + ", " + date);
    int[] totalCt = new int[1];
    List<AbstractAttributeValueRefSetMemberJpa> results =
        findModifiedSinceDateFromQuery(
            AbstractAttributeValueRefSetMemberJpa.class, terminology, date,
            pfs, totalCt);
    AttributeValueRefSetMemberList resultList =
        new AttributeValueRefSetMemberListJpa();
    resultList.setTotalCount(totalCt[0]);
    for (AttributeValueRefSetMember<?> attributeValueRefSetMember : results) {
      resultList.addObject(attributeValueRefSetMember);
    }
    return resultList;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.HistoryService#
   * findAttributeValueRefSetMemberRevisions(java .lang.Long, java.util.Date,
   * java.util.Date, boolean, org.ihtsdo.otf.ts.helpers.PfsParameter)
   */
  @SuppressWarnings({
    "rawtypes"
  })
  @Override
  public AttributeValueRefSetMemberList findAttributeValueRefSetMemberRevisions(
    Long id, Date startDate, Date endDate, PfsParameter pfs) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find attributeValueRefSetMember revisions " + id
            + "," + startDate + ", " + endDate);
    List<AbstractAttributeValueRefSetMemberJpa> revisions =
        findRevisions(id, AbstractAttributeValueRefSetMemberJpa.class,
            startDate, endDate, pfs);
    // Repackage as AttributeValueRefSetMemberList
    AttributeValueRefSetMemberList results =
        new AttributeValueRefSetMemberListJpa();
    results.setTotalCount(revisions.size());
    for (AttributeValueRefSetMember<?> AttributeValueRefSetMember : revisions) {
      results.addObject(AttributeValueRefSetMember);
    }
    return results;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.HistoryService#
   * findAttributeValueRefSetMemberReleaseRevision (java.lang.Long,
   * java.lang.String)
   */
  @Override
  public AttributeValueRefSetMember<?> findAttributeValueRefSetMemberReleaseRevision(
    Long id, Date release) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find attributeValueRefSetMember release revision "
            + id + "," + release);

    AbstractAttributeValueRefSetMemberJpa<?> revision =
        findReleaseRevision(id, release,
            AbstractAttributeValueRefSetMemberJpa.class);
    return revision;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.services.ContentService#
   * getComplexMapRefSetMembersModifiedSinceDate(java.lang.String,
   * java.util.Date)
   */
  @Override
  public ComplexMapRefSetMemberList findComplexMapRefSetMembersModifiedSinceDate(
    String terminology, Date date, PfsParameter pfs) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find complexMapRefSetMembers modified since date "
            + terminology + ", " + date);
    int[] totalCt = new int[1];
    List<ComplexMapRefSetMemberJpa> results =
        findModifiedSinceDateFromQuery(ComplexMapRefSetMemberJpa.class,
            terminology, date, pfs, totalCt);
    ComplexMapRefSetMemberList resultList = new ComplexMapRefSetMemberListJpa();
    resultList.setTotalCount(totalCt[0]);
    for (ComplexMapRefSetMemberJpa complexMapRefSetMember : results) {
      resultList.addObject(complexMapRefSetMember);
    }
    return resultList;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.HistoryService#findComplexMapRefSetMemberRevisions
   * (java .lang.Long, java.util.Date, java.util.Date, boolean,
   * org.ihtsdo.otf.ts.helpers.PfsParameter)
   */
  @Override
  public ComplexMapRefSetMemberList findComplexMapRefSetMemberRevisions(
    Long id, Date startDate, Date endDate, PfsParameter pfs) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find complexMapRefSetMember revisions " + id + ","
            + startDate + ", " + endDate);
    List<ComplexMapRefSetMemberJpa> revisions =
        findRevisions(id, ComplexMapRefSetMemberJpa.class, startDate, endDate,
            pfs);
    // Repackage as ComplexMapRefSetMemberList
    ComplexMapRefSetMemberList results = new ComplexMapRefSetMemberListJpa();
    results.setTotalCount(revisions.size());
    for (ComplexMapRefSetMemberJpa ComplexMapRefSetMember : revisions) {
      results.addObject(ComplexMapRefSetMember);
    }
    return results;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.HistoryService#
   * findComplexMapRefSetMemberReleaseRevision (java.lang.Long,
   * java.lang.String)
   */
  @Override
  public ComplexMapRefSetMember findComplexMapRefSetMemberReleaseRevision(
    Long id, Date release) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find complexMapRefSetMember release revision " + id
            + "," + release);

    ComplexMapRefSetMemberJpa revision =
        findReleaseRevision(id, release, ComplexMapRefSetMemberJpa.class);
    return revision;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.services.ContentService#
   * getDescriptionTypeRefSetMembersModifiedSinceDate(java.lang.String,
   * java.util.Date)
   */
  @Override
  public DescriptionTypeRefSetMemberList findDescriptionTypeRefSetMembersModifiedSinceDate(
    String terminology, Date date, PfsParameter pfs) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find descriptionTypeRefSetMembers modified since date "
            + terminology + ", " + date);
    int[] totalCt = new int[1];
    List<DescriptionTypeRefSetMemberJpa> results =
        findModifiedSinceDateFromQuery(DescriptionTypeRefSetMemberJpa.class,
            terminology, date, pfs, totalCt);
    DescriptionTypeRefSetMemberList resultList =
        new DescriptionTypeRefSetMemberListJpa();
    resultList.setTotalCount(totalCt[0]);
    for (DescriptionTypeRefSetMemberJpa descriptionTypeRefSetMember : results) {
      resultList.addObject(descriptionTypeRefSetMember);
    }
    return resultList;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.HistoryService#
   * findDescriptionTypeRefSetMemberRevisions(java .lang.Long, java.util.Date,
   * java.util.Date, boolean, org.ihtsdo.otf.ts.helpers.PfsParameter)
   */
  @Override
  public DescriptionTypeRefSetMemberList findDescriptionTypeRefSetMemberRevisions(
    Long id, Date startDate, Date endDate, PfsParameter pfs) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find descriptionTypeRefSetMember revisions " + id
            + "," + startDate + ", " + endDate);
    List<DescriptionTypeRefSetMemberJpa> revisions =
        findRevisions(id, DescriptionTypeRefSetMemberJpa.class, startDate,
            endDate, pfs);
    // Repackage as DescriptionTypeRefSetMemberList
    DescriptionTypeRefSetMemberList results =
        new DescriptionTypeRefSetMemberListJpa();
    results.setTotalCount(revisions.size());
    for (DescriptionTypeRefSetMemberJpa DescriptionTypeRefSetMember : revisions) {
      results.addObject(DescriptionTypeRefSetMember);
    }
    return results;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.HistoryService#
   * findDescriptionTypeRefSetMemberReleaseRevision (java.lang.Long,
   * java.lang.String)
   */
  @Override
  public DescriptionTypeRefSetMember findDescriptionTypeRefSetMemberReleaseRevision(
    Long id, Date release) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find descriptionTypeRefSetMember release revision "
            + id + "," + release);

    DescriptionTypeRefSetMemberJpa revision =
        findReleaseRevision(id, release, DescriptionTypeRefSetMemberJpa.class);
    return revision;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.services.ContentService#
   * getModuleDependencyRefSetMembersModifiedSinceDate(java.lang.String,
   * java.util.Date)
   */
  @Override
  public ModuleDependencyRefSetMemberList findModuleDependencyRefSetMembersModifiedSinceDate(
    String terminology, Date date, PfsParameter pfs) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find moduleDependencyRefSetMembers modified since date "
            + terminology + ", " + date);
    int[] totalCt = new int[1];
    List<ModuleDependencyRefSetMemberJpa> results =
        findModifiedSinceDateFromQuery(ModuleDependencyRefSetMemberJpa.class,
            terminology, date, pfs, totalCt);
    ModuleDependencyRefSetMemberList resultList =
        new ModuleDependencyRefSetMemberListJpa();
    resultList.setTotalCount(totalCt[0]);
    for (ModuleDependencyRefSetMemberJpa moduleDependencyRefSetMember : results) {
      resultList.addObject(moduleDependencyRefSetMember);
    }
    return resultList;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.HistoryService#
   * findModuleDependencyRefSetMemberRevisions(java .lang.Long, java.util.Date,
   * java.util.Date, boolean, org.ihtsdo.otf.ts.helpers.PfsParameter)
   */
  @Override
  public ModuleDependencyRefSetMemberList findModuleDependencyRefSetMemberRevisions(
    Long id, Date startDate, Date endDate, PfsParameter pfs) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find moduleDependencyRefSetMember revisions " + id
            + "," + startDate + ", " + endDate);
    List<ModuleDependencyRefSetMemberJpa> revisions =
        findRevisions(id, ModuleDependencyRefSetMemberJpa.class, startDate,
            endDate, pfs);
    // Repackage as ModuleDependencyRefSetMemberList
    ModuleDependencyRefSetMemberList results =
        new ModuleDependencyRefSetMemberListJpa();
    results.setTotalCount(revisions.size());
    for (ModuleDependencyRefSetMemberJpa ModuleDependencyRefSetMember : revisions) {
      results.addObject(ModuleDependencyRefSetMember);
    }
    return results;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.HistoryService#
   * findModuleDependencyRefSetMemberReleaseRevision (java.lang.Long,
   * java.lang.String)
   */
  @Override
  public ModuleDependencyRefSetMember findModuleDependencyRefSetMemberReleaseRevision(
    Long id, Date release) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find moduleDependencyRefSetMember release revision "
            + id + "," + release);

    ModuleDependencyRefSetMemberJpa revision =
        findReleaseRevision(id, release, ModuleDependencyRefSetMemberJpa.class);
    return revision;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.services.ContentService#
   * getRefsetDescriptorRefSetMembersModifiedSinceDate(java.lang.String,
   * java.util.Date)
   */
  @Override
  public RefsetDescriptorRefSetMemberList findRefsetDescriptorRefSetMembersModifiedSinceDate(
    String terminology, Date date, PfsParameter pfs) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find refsetDescriptorRefSetMembers modified since date "
            + terminology + ", " + date);
    int[] totalCt = new int[1];
    List<RefsetDescriptorRefSetMemberJpa> results =
        findModifiedSinceDateFromQuery(RefsetDescriptorRefSetMemberJpa.class,
            terminology, date, pfs, totalCt);
    RefsetDescriptorRefSetMemberList resultList =
        new RefsetDescriptorRefSetMemberListJpa();
    resultList.setTotalCount(totalCt[0]);
    for (RefsetDescriptorRefSetMemberJpa refsetDescriptorRefSetMember : results) {
      resultList.addObject(refsetDescriptorRefSetMember);
    }
    return resultList;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.HistoryService#
   * findRefsetDescriptorRefSetMemberRevisions(java .lang.Long, java.util.Date,
   * java.util.Date, boolean, org.ihtsdo.otf.ts.helpers.PfsParameter)
   */
  @Override
  public RefsetDescriptorRefSetMemberList findRefsetDescriptorRefSetMemberRevisions(
    Long id, Date startDate, Date endDate, PfsParameter pfs) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find refsetDescriptorRefSetMember revisions " + id
            + "," + startDate + ", " + endDate);
    List<RefsetDescriptorRefSetMemberJpa> revisions =
        findRevisions(id, RefsetDescriptorRefSetMemberJpa.class, startDate,
            endDate, pfs);
    // Repackage as RefsetDescriptorRefSetMemberList
    RefsetDescriptorRefSetMemberList results =
        new RefsetDescriptorRefSetMemberListJpa();
    results.setTotalCount(revisions.size());
    for (RefsetDescriptorRefSetMemberJpa RefsetDescriptorRefSetMember : revisions) {
      results.addObject(RefsetDescriptorRefSetMember);
    }
    return results;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.HistoryService#
   * findRefsetDescriptorRefSetMemberReleaseRevision (java.lang.Long,
   * java.lang.String)
   */
  @Override
  public RefsetDescriptorRefSetMember findRefsetDescriptorRefSetMemberReleaseRevision(
    Long id, Date release) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find refsetDescriptorRefSetMember release revision "
            + id + "," + release);

    RefsetDescriptorRefSetMemberJpa revision =
        findReleaseRevision(id, release, RefsetDescriptorRefSetMemberJpa.class);
    return revision;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.services.ContentService#
   * getSimpleMapRefSetMembersModifiedSinceDate(java.lang.String,
   * java.util.Date)
   */
  @Override
  public SimpleMapRefSetMemberList findSimpleMapRefSetMembersModifiedSinceDate(
    String terminology, Date date, PfsParameter pfs) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find simpleMapRefSetMembers modified since date "
            + terminology + ", " + date);
    int[] totalCt = new int[1];
    List<SimpleMapRefSetMemberJpa> results =
        findModifiedSinceDateFromQuery(SimpleMapRefSetMemberJpa.class,
            terminology, date, pfs, totalCt);
    SimpleMapRefSetMemberList resultList = new SimpleMapRefSetMemberListJpa();
    resultList.setTotalCount(totalCt[0]);
    for (SimpleMapRefSetMemberJpa simpleMapRefSetMember : results) {
      resultList.addObject(simpleMapRefSetMember);
    }
    return resultList;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.HistoryService#findSimpleMapRefSetMemberRevisions
   * (java .lang.Long, java.util.Date, java.util.Date, boolean,
   * org.ihtsdo.otf.ts.helpers.PfsParameter)
   */
  @Override
  public SimpleMapRefSetMemberList findSimpleMapRefSetMemberRevisions(Long id,
    Date startDate, Date endDate, PfsParameter pfs) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find simpleMapRefSetMember revisions " + id + ","
            + startDate + ", " + endDate);
    List<SimpleMapRefSetMemberJpa> revisions =
        findRevisions(id, SimpleMapRefSetMemberJpa.class, startDate, endDate,
            pfs);
    // Repackage as SimpleMapRefSetMemberList
    SimpleMapRefSetMemberList results = new SimpleMapRefSetMemberListJpa();
    results.setTotalCount(revisions.size());
    for (SimpleMapRefSetMemberJpa SimpleMapRefSetMember : revisions) {
      results.addObject(SimpleMapRefSetMember);
    }
    return results;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.HistoryService#
   * findSimpleMapRefSetMemberReleaseRevision (java.lang.Long, java.lang.String)
   */
  @Override
  public SimpleMapRefSetMember findSimpleMapRefSetMemberReleaseRevision(
    Long id, Date release) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find simpleMapRefSetMember release revision " + id
            + "," + release);

    SimpleMapRefSetMemberJpa revision =
        findReleaseRevision(id, release, SimpleMapRefSetMemberJpa.class);
    return revision;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.services.ContentService#
   * getSimpleRefSetMembersModifiedSinceDate(java.lang.String, java.util.Date)
   */
  @Override
  public SimpleRefSetMemberList findSimpleRefSetMembersModifiedSinceDate(
    String terminology, Date date, PfsParameter pfs) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find simpleRefSetMembers modified since date "
            + terminology + ", " + date);
    int[] totalCt = new int[1];
    List<SimpleRefSetMemberJpa> results =
        findModifiedSinceDateFromQuery(SimpleRefSetMemberJpa.class,
            terminology, date, pfs, totalCt);
    SimpleRefSetMemberList resultList = new SimpleRefSetMemberListJpa();
    resultList.setTotalCount(totalCt[0]);
    for (SimpleRefSetMemberJpa simpleRefSetMember : results) {
      resultList.addObject(simpleRefSetMember);
    }
    return resultList;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.HistoryService#findSimpleRefSetMemberRevisions
   * (java .lang.Long, java.util.Date, java.util.Date, boolean,
   * org.ihtsdo.otf.ts.helpers.PfsParameter)
   */
  @Override
  public SimpleRefSetMemberList findSimpleRefSetMemberRevisions(Long id,
    Date startDate, Date endDate, PfsParameter pfs) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find simpleRefSetMember revisions " + id + ","
            + startDate + ", " + endDate);
    List<SimpleRefSetMemberJpa> revisions =
        findRevisions(id, SimpleRefSetMemberJpa.class, startDate, endDate, pfs);
    // Repackage as SimpleRefSetMemberList
    SimpleRefSetMemberList results = new SimpleRefSetMemberListJpa();
    results.setTotalCount(revisions.size());
    for (SimpleRefSetMemberJpa SimpleRefSetMember : revisions) {
      results.addObject(SimpleRefSetMember);
    }
    return results;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.HistoryService#findSimpleRefSetMemberReleaseRevision
   * (java.lang.Long, java.lang.String)
   */
  @Override
  public SimpleRefSetMember findSimpleRefSetMemberReleaseRevision(Long id,
    Date release) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - find simpleRefSetMember release revision " + id
            + "," + release);

    SimpleRefSetMemberJpa revision =
        findReleaseRevision(id, release, SimpleRefSetMemberJpa.class);
    return revision;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.HistoryService#getReleaseHistory(java.lang.String
   * )
   */
  @SuppressWarnings("unchecked")
  @Override
  public ReleaseInfoList getReleaseHistory(String terminology) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - get release history " + terminology);
    javax.persistence.Query query =
        manager
            .createQuery("select a from ReleaseInfoJpa a where terminology = :terminology order by a.effectiveTime");
    /*
     * Try to retrieve the single expected result If zero or more than one
     * result are returned, log error and set result to null
     */
    try {
      query.setParameter("terminology", terminology);
      List<ReleaseInfo> releaseInfos = query.getResultList();
      ReleaseInfoList releaseInfoList = new ReleaseInfoListJpa();
      releaseInfoList.setObjects(releaseInfos);
      releaseInfoList.setTotalCount(releaseInfos.size());
      return releaseInfoList;
    } catch (NoResultException e) {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.HistoryService#getCurrentReleaseInfo(java.lang
   * .String)
   */
  @Override
  public ReleaseInfo getCurrentReleaseInfo(String terminology) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - get current release info " + terminology);
    List<ReleaseInfo> results = getReleaseHistory(terminology).getObjects();
    // get max release that is published and not planned
    for (int i = results.size() - 1; i >= 0; i--) {
      if (results.get(i).isPublished() && !results.get(i).isPlanned()
          && results.get(i).getTerminology().equals(terminology)) {
        return results.get(i);
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.HistoryService#getPreviousReleaseInfo(java.lang
   * .String)
   */
  @Override
  public ReleaseInfo getPreviousReleaseInfo(String terminology)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - get previous release info " + terminology);
    List<ReleaseInfo> results = getReleaseHistory(terminology).getObjects();
    // get one before the max release that is published
    for (int i = results.size() - 1; i >= 0; i--) {
      if (results.get(i).isPublished() && !results.get(i).isPlanned()
          && results.get(i).getTerminology().equals(terminology)) {
        if (i > 0) {
          return results.get(i - 1);
        } else {
          return null;
        }
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.HistoryService#getPlannedReleaseInfo(java.lang
   * .String)
   */
  @Override
  public ReleaseInfo getPlannedReleaseInfo(String terminology) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - get planned release info " + terminology);
    List<ReleaseInfo> results = getReleaseHistory(terminology).getObjects();
    // get one before the max release that is published
    for (int i = results.size() - 1; i >= 0; i--) {
      if (!results.get(i).isPublished() && results.get(i).isPlanned()
          && results.get(i).getTerminology().equals(terminology)) {
        return results.get(i);
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.HistoryService#getReleaseInfo(java.lang.String,
   * java.lang.String)
   */
  @Override
  public ReleaseInfo getReleaseInfo(String terminology, String name)
    throws ParseException {
    Logger.getLogger(getClass()).debug(
        "History Service - get release info " + terminology + ", " + name);
    javax.persistence.Query query =
        manager.createQuery("select r from ReleaseInfoJpa r "
            + "where name = :name " + "and terminology = :terminology");
    /*
     * Try to retrieve the single expected result If zero or more than one
     * result are returned, log error and set result to null
     */
    try {
      query.setParameter("name", name);
      query.setParameter("terminology", terminology);
      return (ReleaseInfo) query.getSingleResult();
    } catch (NoResultException e) {
      Logger.getLogger(getClass()).error("No release info found for name/terminology " + name + "/" + terminology);
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.HistoryService#findConceptsDeepModifiedSinceDate
   * (java.lang.String, java.util.Date, org.ihtsdo.otf.ts.helpers.PfsParameter)
   */
  @Override
  public ConceptList findConceptsDeepModifiedSinceDate(String terminology,
    Date date, PfsParameter pfs) throws Exception {
    // Load each object type by date and see if it changed. Look back up to the
    // concept level where appropriate
    Set<Concept> results = new HashSet<>();

    ConceptList concepts =
        findConceptsModifiedSinceDate(terminology, date, pfs);
    for (Concept concept : concepts.getObjects()) {
      results.add(concept);
    }

    DescriptionList descriptions =
        findDescriptionsModifiedSinceDate(terminology, date, pfs);
    for (Description description : descriptions.getObjects()) {
      results.add(description.getConcept());
    }

    RelationshipList rels =
        findRelationshipsModifiedSinceDate(terminology, date, pfs);
    for (Relationship rel : rels.getObjects()) {
      results.add(rel.getSourceConcept());
    }

    LanguageRefSetMemberList members =
        findLanguageRefSetMembersModifiedSinceDate(terminology, date, pfs);
    for (LanguageRefSetMember member : members.getObjects()) {
      results.add(member.getDescription().getConcept());
    }

    SimpleRefSetMemberList members2 =
        findSimpleRefSetMembersModifiedSinceDate(terminology, date, pfs);
    for (SimpleRefSetMember member : members2.getObjects()) {
      results.add(member.getConcept());
    }

    SimpleMapRefSetMemberList members3 =
        findSimpleMapRefSetMembersModifiedSinceDate(terminology, date, pfs);
    for (SimpleMapRefSetMember member : members3.getObjects()) {
      results.add(member.getConcept());
    }

    ComplexMapRefSetMemberList members4 =
        findComplexMapRefSetMembersModifiedSinceDate(terminology, date, pfs);
    for (ComplexMapRefSetMember member : members4.getObjects()) {
      results.add(member.getConcept());
    }

    AssociationReferenceRefSetMemberList members5 =
        this.findAssociationReferenceRefSetMembersModifiedSinceDate(
            terminology, date, pfs);
    for (AssociationReferenceRefSetMember<?> member : members5.getObjects()) {
      if (member instanceof AssociationReferenceConceptRefSetMember) {
        results.add(((AssociationReferenceConceptRefSetMember) member)
            .getConcept());
      }
      if (member instanceof AssociationReferenceDescriptionRefSetMember) {
        results.add(((AssociationReferenceDescriptionRefSetMember) member)
            .getDescription().getConcept());
      }
    }

    AttributeValueRefSetMemberList members6 =
        this.findAttributeValueRefSetMembersModifiedSinceDate(terminology,
            date, pfs);
    for (AttributeValueRefSetMember<?> member : members6.getObjects()) {
      if (member instanceof AttributeValueConceptRefSetMember) {
        results.add(((AttributeValueConceptRefSetMember) member).getConcept());
      }
      if (member instanceof AttributeValueDescriptionRefSetMember) {
        results.add(((AttributeValueDescriptionRefSetMember) member)
            .getDescription().getConcept());
      }
    }

    // TODO Fully implement this method, decide paging strategy
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.HistoryService#addReleaseInfo(org.ihtsdo.otf
   * .ts.helpers.ReleaseInfo)
   */
  @Override
  public ReleaseInfo addReleaseInfo(ReleaseInfo releaseInfo) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - add release info " + releaseInfo.getName());
    if (lastModifiedFlag) {
      releaseInfo.setLastModified(new Date());
    }
    try {
      if (getTransactionPerOperation()) {
        tx = manager.getTransaction();
        tx.begin();
        manager.persist(releaseInfo);
        tx.commit();
      } else {
        manager.persist(releaseInfo);
      }
    } catch (Exception e) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw e;
    }

    return releaseInfo;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.HistoryService#updateReleaseInfo(org.ihtsdo.
   * otf.ts.helpers.ReleaseInfo)
   */
  @Override
  public void updateReleaseInfo(ReleaseInfo releaseInfo) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History Service - update release info " + releaseInfo.getName());
    if (lastModifiedFlag) {
      releaseInfo.setLastModified(new Date());
    }
    try {
      if (getTransactionPerOperation()) {
        tx = manager.getTransaction();
        tx.begin();
        manager.merge(releaseInfo);
        tx.commit();
      } else {
        manager.merge(releaseInfo);
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
   * org.ihtsdo.otf.ts.services.HistoryService#removeReleaseInfo(java.lang.Long)
   */
  @Override
  public void removeReleaseInfo(Long id) throws Exception {
    Logger.getLogger(getClass()).debug(
        "History  Service - remove release info " + id);
    tx = manager.getTransaction();
    // retrieve this release info
    ReleaseInfo releaseInfo = manager.find(ReleaseInfoJpa.class, id);
    try {
      if (getTransactionPerOperation()) {
        // remove description
        tx.begin();
        if (manager.contains(releaseInfo)) {
          manager.remove(releaseInfo);
        } else {
          manager.remove(manager.merge(releaseInfo));
        }
        tx.commit();
      } else {
        if (manager.contains(releaseInfo)) {
          manager.remove(releaseInfo);
        } else {
          manager.remove(manager.merge(releaseInfo));
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
   * Find modified since date.
   *
   * @param <T> the generic type
   * @param clazz the clazz
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs parameter
   * @param totalCt the total ct
   * @return the list
   * @throws Exception the exception
   */
  @SuppressWarnings("unchecked")
  private <T> List<T> findModifiedSinceDateFromLucene(Class<T> clazz,
    String terminology, Date date, PfsParameter pfs, int[] totalCt)
    throws Exception {

    // Use latest date of the type
    Date minDate = date;
    if (date == null) {
      javax.persistence.Query query;
      // if no date provided, get the latest modified concepts
      query =
          manager.createQuery(
              "select max(c.effectiveTime) from " + clazz.getName() + " c"
                  + " where terminology = :terminology").setParameter(
              "terminology", terminology);
      Date tempDate = (Date) query.getSingleResult();
      minDate = tempDate;
      Logger.getLogger(getClass()).debug("  date is null, use " + minDate);
    }
    FullTextEntityManager fullTextEntityManager =
        Search.getFullTextEntityManager(manager);

    SearchFactory searchFactory = fullTextEntityManager.getSearchFactory();
    QueryBuilder qb =
        searchFactory.buildQueryBuilder().forEntity(ConceptJpa.class).get();
    Query luceneQuery;
    if (pfs == null || pfs.getQueryRestriction() == null) {
      luceneQuery =
          qb.bool()
              .must(
                  qb.range().onField("lastModified").above(minDate)
                      .createQuery())
              .must(
                  qb.keyword().onField("terminology").matching(terminology)
                      .createQuery()).createQuery();
    } else {
      luceneQuery =
          qb.bool()
              .must(
                  qb.range().onField("lastModified").above(minDate)
                      .createQuery())
              .must(
                  qb.keyword().onField("terminology").matching(terminology)
                      .createQuery())
              .must(
                  qb.keyword()
                      .onFields("terminologyId", "defaultPreferredName")
                      .matching(pfs.getQueryRestriction()).createQuery())

              .createQuery();

    }
    Logger.getLogger(clazz).info("Query text: " + luceneQuery.toString());

    org.hibernate.search.jpa.FullTextQuery ftquery =
        fullTextEntityManager
            .createFullTextQuery(luceneQuery, ConceptJpa.class);

    if (pfs != null && pfs.getStartIndex() != -1 && pfs.getMaxResults() != -1) {
      ftquery.setFirstResult(pfs.getStartIndex());
      ftquery.setMaxResults(pfs.getMaxResults());
    }
    


    if (pfs != null && pfs.getSortField() != null
        && !pfs.getSortField().isEmpty()) {
      
      // check that sort field is valid
      if (clazz.getField(pfs.getSortField()) == null)
          throw new LocalException("Cannot sort on field: " + pfs.getSortField());
      
      ftquery.setSort(new Sort(new SortField(pfs.getSortField(), Type.STRING,
          !pfs.isAscending())));
    }

    totalCt[0] = ftquery.getResultSize();
    return ftquery.getResultList();
  }

  /**
   * Find modified since date.
   *
   * @param <T> the generic type
   * @param clazz the clazz
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs parameter
   * @param totalCt the total ct
   * @return the list
   * @throws Exception the exception
   */
  @SuppressWarnings("unchecked")
  private <T> List<T> findModifiedSinceDateFromQuery(Class<T> clazz,
    String terminology, Date date, PfsParameter pfs, int[] totalCt)
    throws Exception {

    // Use latest date of the type
    Date minDate = date;
    if (date == null) {
      javax.persistence.Query query;
      // if no date provided, get the latest modified concepts
      query =
          manager.createQuery(
              "select max(c.effectiveTime) from " + clazz.getName() + " c"
                  + " where terminology = :terminology").setParameter(
              "terminology", terminology);
      Date tempDate = (Date) query.getSingleResult();
      minDate = tempDate;
      Logger.getLogger(getClass()).debug("  date is null, use " + minDate);
    }

    javax.persistence.Query query =
        manager.createQuery("select count(a.id) from " + clazz.getName() + " a"
            + " where lastModified >= :date"
            + " and terminology = :terminology");
    query.setParameter("date", minDate);
    query.setParameter("terminology", terminology);
    long ct = (long) query.getSingleResult();
    totalCt[0] = (int) ct;

    query =
        applyPfsToQuery("select a from " + clazz.getName() + " a"
            + " where lastModified >= :date"
            + " and terminology = :terminology", pfs);
    query.setParameter("date", minDate);
    query.setParameter("terminology", terminology);
    return query.getResultList();
  }

  /**
   * Find revisions.
   *
   * @param <T> the generic type
   * @param id the id
   * @param clazz the clazz
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs parameter
   * @return the list
   * @throws LocalException 
   */
  @SuppressWarnings("unchecked")
  private <T> List<T> findRevisions(Long id, Class<T> clazz, Date startDate,
    Date endDate, PfsParameter pfs) throws Exception {

    AuditReader reader = AuditReaderFactory.get(manager);
    AuditQuery query = reader.createQuery()

    // all revisions, returned as objects, not finding deleted entries
        .forRevisionsOfEntity(clazz, true, false)

        // search by id
        .add(AuditEntity.id().eq(id));

    if (startDate != null) {
      // search by lower bound on last modified
      query.add(AuditEntity.property("lastModified").ge(startDate));
    }

    if (endDate != null) {
      // search by upper bound on last modified
      query.add(AuditEntity.property("lastModified").le(endDate));
    }

    if (pfs != null && pfs.getSortField() != null) {
      query = query.addOrder(AuditEntity.property(pfs.getSortField()).asc());
    } else {
      // order by descending timestamp
      query = query.addOrder(AuditEntity.property("lastModified").asc());
    }

    if (pfs != null && pfs.getStartIndex() > -1 && pfs.getMaxResults() > -1) {
      query =
          query.setFirstResult(pfs.getStartIndex()).setMaxResults(
              pfs.getMaxResults());
    } else if (pfs != null && (pfs.getStartIndex() < -1 || pfs.getMaxResults() < -1)) {
      throw new LocalException("Invalid paging parameters");
    }
    
    if (pfs != null && pfs.getQueryRestriction() != null) {
      throw new LocalException("Query restriction not supported");
    }

    // execute query
    List<T> revisions = query.getResultList();
    return revisions;
  }

  /**
   * Find release revision. This is the last revision in which an effective time
   * value matches the parameter.
   *
   * @param <T> the generic type
   * @param id the id
   * @param release the release
   * @param clazz the clazz
   * @return the t
   * @throws ParseException the parse exception
   */
  private <T> T findReleaseRevision(Long id, Date release, Class<T> clazz)
    throws ParseException {

    AuditReader reader = AuditReaderFactory.get(manager);
    AuditQuery query = reader.createQuery()

    // all revisions, returned as objects, not finding deleted entries
        .forRevisionsOfEntity(clazz, false, false)

        .addProjection(AuditEntity.revisionNumber().max())

        // search by id
        .add(AuditEntity.id().eq(id))

        // search by lower bound on last modified
        .add(AuditEntity.property("effectiveTime").eq(release));

    // execute query
    @SuppressWarnings("unchecked")
    List<Long> revisions = query.getResultList();
    if (revisions.size() > 0) {
      if (revisions.get(0) != null) {
        return reader.find(clazz, id, revisions.get(0));
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

}
