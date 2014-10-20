package org.ihtsdo.otf.ts.jpa.services;

import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.lucene.search.Query;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ConceptListJpa;
import org.ihtsdo.otf.ts.helpers.DescriptionList;
import org.ihtsdo.otf.ts.helpers.DescriptionListJpa;
import org.ihtsdo.otf.ts.helpers.LanguageRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.LanguageRefSetMemberListJpa;
import org.ihtsdo.otf.ts.helpers.PfsParameter;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.RelationshipList;
import org.ihtsdo.otf.ts.helpers.RelationshipListJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.services.HistoryService;

/**
 * The Content Services for the Jpa model.
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
  @SuppressWarnings("unchecked")
  @Override
  public ConceptList getConceptsModifiedSinceDate(String terminology,
    Date date, PfsParameter pfsParameter) throws Exception {
    ConceptList results = new ConceptListJpa();

    Date minDate = date;

    PfsParameter localPfsParameter = pfsParameter;

    if (localPfsParameter == null)
      localPfsParameter = new PfsParameterJpa();

    if (date == null) {
      javax.persistence.Query query;

      // if no date provided, get the latest modified concepts
      query =
          manager.createQuery(
              "select max(c.effectiveTime) from ConceptJpa c"
                  + " where terminology = :terminology").setParameter(
              "terminology", terminology);

      Date tempDate = (Date) query.getSingleResult();
      minDate = tempDate;
    }
    FullTextEntityManager fullTextEntityManager =
        Search.getFullTextEntityManager(manager);

    SearchFactory searchFactory = fullTextEntityManager.getSearchFactory();

    QueryBuilder qb =
        searchFactory.buildQueryBuilder().forEntity(ConceptJpa.class).get();

    Query luceneQuery;
    if (localPfsParameter.getQueryRestriction() == null) {
      luceneQuery =
          qb.bool()
              .must(
                  qb.keyword().onField("effectiveTime").matching(minDate)
                      .createQuery())
              .must(
                  qb.keyword().onField("terminology").matching(terminology)
                      .createQuery()).createQuery();
    } else {
      luceneQuery =
          qb.bool()
              .must(
                  qb.keyword().onField("effectiveTime").matching(minDate)
                      .createQuery())
              .must(
                  qb.keyword().onField("terminology").matching(terminology)
                      .createQuery())
              .must(
                  qb.keyword()
                      .onFields("terminologyId", "defaultPreferredName")
                      .matching(localPfsParameter.getQueryRestriction())
                      .createQuery()).createQuery();

    }
    Logger.getLogger(ContentServiceJpa.class).info(
        "Query text: " + luceneQuery.toString());

    org.hibernate.search.jpa.FullTextQuery ftquery =
        fullTextEntityManager
            .createFullTextQuery(luceneQuery, ConceptJpa.class);

    if (localPfsParameter.getStartIndex() != -1
        && localPfsParameter.getMaxResults() != -1) {
      ftquery.setFirstResult(localPfsParameter.getStartIndex());
      ftquery.setMaxResults(localPfsParameter.getMaxResults());

    }
    results.setTotalCount(ftquery.getResultSize());
    results.setConcepts(ftquery.getResultList());
    return results;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.ContentService#getDescriptionsModifiedSinceDate
   * (java.lang.String, java.util.Date)
   */
  @SuppressWarnings("unchecked")
  @Override
  public DescriptionList getDescriptionsModifiedSinceDate(String terminology,
    Date date) {
    DescriptionList results = new DescriptionListJpa();

    javax.persistence.Query query =
        manager
            .createQuery(
                "select d from DescriptionJpa d"
                    + " where effectiveTime >= :releaseDate"
                    + " and terminology = :terminology")
            .setParameter("releaseDate", date)
            .setParameter("terminology", terminology);

    results.setDescriptions(query.getResultList());
    return results;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.services.ContentService#
   * getRelationshipsModifiedSinceDate(java.lang.String, java.util.Date)
   */
  @SuppressWarnings("unchecked")
  @Override
  public RelationshipList getRelationshipsModifiedSinceDate(String terminology,
    Date date) {
    RelationshipList results = new RelationshipListJpa();

    javax.persistence.Query query =
        manager
            .createQuery(
                "select r from RelationshipJpa r"
                    + " where effectiveTime >= :releaseDate"
                    + " and terminology = :terminology")
            .setParameter("releaseDate", date)
            .setParameter("terminology", terminology);

    results.setRelationships(query.getResultList());

    return results;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.services.ContentService#
   * getLanguageRefSetMembersModifiedSinceDate(java.lang.String, java.util.Date)
   */
  @SuppressWarnings("unchecked")
  @Override
  public LanguageRefSetMemberList getLanguageRefSetMembersModifiedSinceDate(
    String terminology, Date date) {
    LanguageRefSetMemberList results = new LanguageRefSetMemberListJpa();

    javax.persistence.Query query =
        manager
            .createQuery(
                "select l from LanguageRefSetMemberJpa l"
                    + " where effectiveTime >= :releaseDate"
                    + " and terminology = :terminology")
            .setParameter("releaseDate", date)
            .setParameter("terminology", terminology);

    results.setLanguageRefSetMembers(query.getResultList());

    return results;
  }

}
