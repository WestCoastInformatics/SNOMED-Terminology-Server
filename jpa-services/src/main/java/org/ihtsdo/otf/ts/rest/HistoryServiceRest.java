package org.ihtsdo.otf.ts.rest;

import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.DescriptionList;
import org.ihtsdo.otf.ts.helpers.LanguageRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.RelationshipList;
import org.ihtsdo.otf.ts.helpers.ReleaseInfo;
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
   * Find concepts modified since date. This is not
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
   * Finds all concept revisions for the specified date range.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs parameter
   * @param authToken the auth token
   * @return the concept revisions
   * @throws Exception
   */
  public ConceptList findConceptRevisions(String id, String startDate,
    String endDate, PfsParameterJpa pfs, String authToken) throws Exception;

  /**
   * Find concept release revisions for the specified date range.
   *
   * @param id the id
   * @param release the release
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the concept list
   * @throws Exception the exception
   */
  public Concept findConceptReleaseRevision(String id, String release,
   String authToken) throws Exception;

  /**
   * Find descriptions modified since date.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs parameter
   * @param authToken the auth token
   * @return the search result list
   */
  public DescriptionList findDescriptionsModifiedSinceDate(String terminology,
    String date, PfsParameterJpa pfs, String authToken) throws Exception;

  /**
   * Finds all description revisions for the specified date range.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs parameter
   * @param authToken the auth token
   * @return the description list
   */
  public DescriptionList findDescriptionRevisions(String id, String startDate,
    String endDate, PfsParameterJpa pfs, String authToken) throws Exception;

  /**
   * Find description release revisions.
   *
   * @param id the id
   * @param release the release
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the description list
   * @throws Exception the exception
   */
  public Description findDescriptionReleaseRevision(String id,
    String release,  String authToken) throws Exception;

  /**
   * Find relationships modified since date.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs parameter
   * @param authToken the auth token
   * @return the search result list
   */
  public RelationshipList findRelationshipsModifiedSinceDate(
    String terminology, String date, PfsParameterJpa pfs, String authToken)
    throws Exception;

  /**
   * Finds all relationship revisions for the specified date range.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param releaseRevisionsOnly the release revisions only
   * @param pfs the pfs parameter
   * @param authToken the auth token
   * @return the relationship list
   */
  public RelationshipList findRelationshipRevisions(String id,
    String startDate, String endDate, PfsParameterJpa pfs, String authToken)
    throws Exception;

  /**
   * Find relationship release revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the relationship list
   */
  public Relationship findRelationshipReleaseRevision(String id,
    String release, String authToken) throws Exception;

  /**
   * Find language ref set members modified since date.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs parameter
   * @param authToken the auth token
   * @return the search result list
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
   * @param releaseRevisionsOnly the release revisions only
   * @param pfs the pfs parameter
   * @param authToken the auth token
   * @return the language ref set member list
   */
  public LanguageRefSetMemberList findLanguageRefSetMemberRevisions(String id,
    String startDate, String endDate, PfsParameterJpa pfs, String authToken)
    throws Exception;

  /**
   * Find language ref set member release revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the language ref set member list
   */
  public LanguageRefSetMember findLanguageRefSetMemberReleaseRevision(
    String id, String release, String authToken)
    throws Exception;

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
   * @param authToken the auth token
   * @return the release history
   * @throws Exception the exception
   */
  public ReleaseInfoList getReleaseHistory(String authToken) throws Exception;

  /**
   * Returns the current release info.
   *
   * @param authToken the auth token
   * @return the current release info
   * @throws Exception the exception
   */
  public ReleaseInfo getCurrentReleaseInfo(String authToken) throws Exception;

  /**
   * Returns the previous release info.
   *
   * @param authToken the auth token
   * @return the previous release info
   * @throws Exception the exception
   */
  public ReleaseInfo getPreviousReleaseInfo(String authToken) throws Exception;

  /**
   * Gets the planned release info.
   *
   * @param authToken the auth token
   * @return the planned release info
   * @throws Exception the exception
   */
  public ReleaseInfo getPlannedReleaseInfo(String authToken) throws Exception;

  /**
   * Returns the release info.
   *
   * @param release the release
   * @param authToken the auth token
   * @return the release info
   * @throws Exception the exception
   */
  public ReleaseInfo getReleaseInfo(String release, String authToken)
    throws Exception;

  /**
   * Adds the release info.
   *
   * @param releaseInfo the release info
   * @return the release info
   * @throws Exception the exception
   */
  public ReleaseInfo addReleaseInfo(ReleaseInfo releaseInfo, String authToken)
    throws Exception;

  /**
   * Updates release info.
   *
   * @param releaseInfo the release info
   * @throws Exception the exception
   */
  public void updateReleaseInfo(ReleaseInfo releaseInfo, String authToken)
    throws Exception;

  /**
   * Removes the release info.
   *
   * @param id the id
   * @throws Exception the exception
   */
  public void removeReleaseInfo(String id, String authToken) throws Exception;

}
