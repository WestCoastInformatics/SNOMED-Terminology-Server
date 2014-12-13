package org.ihtsdo.otf.ts.rest;

import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.DescriptionList;
import org.ihtsdo.otf.ts.helpers.LanguageRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.RelationshipList;
import org.ihtsdo.otf.ts.helpers.ReleaseInfo;
import org.ihtsdo.otf.ts.helpers.ReleaseInfoJpa;
import org.ihtsdo.otf.ts.helpers.ReleaseInfoList;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;

/**
 * Represents a history services available via a REST service.
 */
public interface HistoryServiceRest {

  /**
   * Find concepts modified since date. Use a "null" date to get the latest
   * changed concepts.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs parameter
   * @param authToken the auth token
   * @return the search result list
   * @throws Exception the exception
   */
  public ConceptList findConceptsModifiedSinceDate(String terminology,
    String date, PfsParameterJpa pfs, String authToken) throws Exception;

  /**
   * Finds all concept revisions for the specified date range. Use a "null"
   * start or end date to make it open ended.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs parameter
   * @param authToken the auth token
   * @return the concept revisions
   * @throws Exception the exception
   */
  public ConceptList findConceptRevisions(String id, String startDate,
    String endDate, PfsParameterJpa pfs, String authToken) throws Exception;

  /**
   * Find concept release revisions for the specified date range.
   *
   * @param id the id
   * @param release the release
   * @param authToken the auth token
   * @return the concept list
   * @throws Exception the exception
   */
  public Concept findConceptReleaseRevision(String id, String release,
    String authToken) throws Exception;

  /**
   * Find descriptions modified since date. Use a "null" date to get the latest
   * changed descriptions.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs parameter
   * @param authToken the auth token
   * @return the search result list
   * @throws Exception the exception
   */
  public DescriptionList findDescriptionsModifiedSinceDate(String terminology,
    String date, PfsParameterJpa pfs, String authToken) throws Exception;

  /**
   * Finds all description revisions for the specified date range. Use a "null"
   * start or end date to make it open ended.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs parameter
   * @param authToken the auth token
   * @return the description list
   * @throws Exception the exception
   */
  public DescriptionList findDescriptionRevisions(String id, String startDate,
    String endDate, PfsParameterJpa pfs, String authToken) throws Exception;

  /**
   * Find description release revisions.
   *
   * @param id the id
   * @param release the release
   * @param authToken the auth token
   * @return the description list
   * @throws Exception the exception
   */
  public Description findDescriptionReleaseRevision(String id, String release,
    String authToken) throws Exception;

  /**
   * Find relationships modified since date. Use a "null" date to get the latest
   * changed relationships.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs parameter
   * @param authToken the auth token
   * @return the search result list
   * @throws Exception the exception
   */
  public RelationshipList findRelationshipsModifiedSinceDate(
    String terminology, String date, PfsParameterJpa pfs, String authToken)
    throws Exception;

  /**
   * Finds all relationship revisions for the specified date range. Use a "null"
   * start or end date to make it open ended.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs parameter
   * @param authToken the auth token
   * @return the relationship list
   * @throws Exception the exception
   */
  public RelationshipList findRelationshipRevisions(String id,
    String startDate, String endDate, PfsParameterJpa pfs, String authToken)
    throws Exception;

  /**
   * Find relationship release revisions.
   *
   * @param id the id
   * @param release the release
   * @param authToken the auth token
   * @return the relationship list
   * @throws Exception the exception
   */
  public Relationship findRelationshipReleaseRevision(String id,
    String release, String authToken) throws Exception;

  /**
   * Find language refset members modified since date. Use a "null" date to get
   * the latest changed language refset members.
   *
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs parameter
   * @param authToken the auth token
   * @return the search result list
   * @throws Exception the exception
   */
  public LanguageRefSetMemberList findLanguageRefSetMembersModifiedSinceDate(
    String terminology, String date, PfsParameterJpa pfs, String authToken)
    throws Exception;

  /**
   * Find language ref set member revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs parameter
   * @param authToken the auth token
   * @return the language ref set member list
   * @throws Exception the exception
   */
  public LanguageRefSetMemberList findLanguageRefSetMemberRevisions(String id,
    String startDate, String endDate, PfsParameterJpa pfs, String authToken)
    throws Exception;

  /**
   * Find language ref set member release revisions. Use a "null" start or end
   * date to make it open ended.
   *
   * @param id the id
   * @param release the release
   * @param authToken the auth token
   * @return the language ref set member list
   * @throws Exception the exception
   */
  public LanguageRefSetMember findLanguageRefSetMemberReleaseRevision(
    String id, String release, String authToken) throws Exception;

  /**
   * Returns concepts changed since certain date – performs a "deep" search for
   * all concepts where it or any of its components have changed in the relevant
   * period.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs parameter
   * @param authToken the auth token
   * @return the concepts deep modified since date
   * @throws Exception the exception
   */
  public ConceptList findConceptsDeepModifiedSinceDate(String terminology,
    String date, PfsParameterJpa pfs, String authToken) throws Exception;

  /**
   * Returns the release history.
   *
   * @param terminology the terminology
   * @param authToken the auth token
   * @return the release history
   * @throws Exception the exception
   */
  public ReleaseInfoList getReleaseHistory(String terminology, String authToken)
    throws Exception;

  /**
   * Returns the current release info.
   *
   * @param terminology the terminology
   * @param authToken the auth token
   * @return the current release info
   * @throws Exception the exception
   */
  public ReleaseInfo getCurrentReleaseInfo(String terminology, String authToken)
    throws Exception;

  /**
   * Returns the previous release info.
   *
   * @param terminology the terminology
   * @param authToken the auth token
   * @return the previous release info
   * @throws Exception the exception
   */
  public ReleaseInfo getPreviousReleaseInfo(String terminology, String authToken)
    throws Exception;

  /**
   * Gets the planned release info.
   *
   * @param terminology the terminology
   * @param authToken the auth token
   * @return the planned release info
   * @throws Exception the exception
   */
  public ReleaseInfo getPlannedReleaseInfo(String terminology, String authToken)
    throws Exception;

  /**
   * Returns the release info.
   *
   * @param terminology the terminology
   * @param name the name
   * @param authToken the auth token
   * @return the release info
   * @throws Exception the exception
   */
  public ReleaseInfo getReleaseInfo(String terminology, String name,
    String authToken) throws Exception;

  /**
   * Adds the release info.
   *
   * @param releaseInfo the release info
   * @param authToken the auth token
   * @return the release info
   * @throws Exception the exception
   */
  public ReleaseInfo addReleaseInfo(ReleaseInfoJpa releaseInfo, String authToken)
    throws Exception;

  /**
   * Updates release info.
   *
   * @param releaseInfo the release info
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void updateReleaseInfo(ReleaseInfoJpa releaseInfo, String authToken)
    throws Exception;

  /**
   * Removes the release info.
   *
   * @param id the id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeReleaseInfo(String id, String authToken) throws Exception;

}
