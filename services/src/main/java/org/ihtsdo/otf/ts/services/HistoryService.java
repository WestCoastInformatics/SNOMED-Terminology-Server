package org.ihtsdo.otf.ts.services;

import java.util.Date;

import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.DescriptionList;
import org.ihtsdo.otf.ts.helpers.LanguageRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.PfsParameter;
import org.ihtsdo.otf.ts.helpers.RelationshipList;
import org.ihtsdo.otf.ts.helpers.ReleaseInfo;
import org.ihtsdo.otf.ts.helpers.ReleaseInfoList;

/**
 * Generically represents a service for asking questions about content history.
 */
public interface HistoryService extends ContentService {

  /**
   * Find concepts modified since date.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs parameter
   * @return the search result list
   * @throws Exception the exception
   */
  public ConceptList findConceptsModifiedSinceDate(String terminology,
    Date date, PfsParameter pfs) throws Exception;

  /**
   * Finds all concept revisions for the specified date range.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param releaseRevisionsOnly the release revisions only
   * @param pfs the pfs parameter
   * @return the concept revisions
   */
  public ConceptList findConceptRevisions(Long id, Date startDate,
    Date endDate, boolean releaseRevisionsOnly, PfsParameter pfs);

  /**
   * Find descriptions modified since date.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs parameter
   * @return the search result list
   */
  public DescriptionList findDescriptionsModifiedSinceDate(String terminology,
    Date date, PfsParameter pfs);

  /**
   * Finds all description revisions for the specified date range.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param releaseRevisionsOnly the release revisions only
   * @param pfs the pfs parameter
   * @return the description list
   */
  public DescriptionList findDescriptionRevisions(Long id, Date startDate,
    Date endDate, boolean releaseRevisionsOnly, PfsParameter pfs);

  /**
   * Find relationships modified since date.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs parameter
   * @return the search result list
   */
  public RelationshipList findRelationshipsModifiedSinceDate(String terminology,
    Date date, PfsParameter pfs);

  /**
   * Finds all relationship revisions for the specified date range.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param releaseRevisionsOnly the release revisions only
   * @param pfs the pfs parameter
   * @return the relationship list
   */
  public RelationshipList findRelationshipRevisions(Long id, Date startDate,
    Date endDate, boolean releaseRevisionsOnly, PfsParameter pfs);

  /**
   * Find language ref set members modified since date.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs parameter
   * @return the search result list
   */
  public LanguageRefSetMemberList findLanguageRefSetMembersModifiedSinceDate(
    String terminology, Date date, PfsParameter pfs);

  /**
   * Find language ref set member revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param releaseRevisionsOnly the release revisions only
   * @param pfs the pfs parameter
   * @return the language ref set member list
   */
  public LanguageRefSetMemberList findLanguageRefSetMemberRevisions(Long id, Date startDate,
    Date endDate, boolean releaseRevisionsOnly, PfsParameter pfs);
  
  /**
   * Returns concepts changed since certain date – performs a "deep" search for
   * all concepts where it or any of its components have changed in the relevant
   * period.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs parameter
   * @return the concepts deep modified since date
   * @throws Exception the exception
   */
  public ConceptList findConceptsDeepModifiedSinceDate(String terminology,
    Date date, PfsParameter pfs) throws Exception;

  /**
   * Returns the release history.
   *
   * @return the release history
   * @throws Exception the exception
   */
  public ReleaseInfoList getReleaseHistory() throws Exception;

  /**
   * Returns the current published release info.
   *
   * @return the current release info
   * @throws Exception the exception
   */
  public ReleaseInfo getCurrentReleaseInfo() throws Exception;

  /**
   * Returns the previous published release info.
   *
   * @return the previous release info
   * @throws Exception the exception
   */
  public ReleaseInfo getPreviousReleaseInfo() throws Exception;

  /**
   * Gets the planned release info. (planned not published)
   *
   * @return the planned release info
   * @throws Exception the exception
   */
  public ReleaseInfo getPlannedReleaseInfo() throws Exception;

  /**
   * Returns the release info.
   *
   * @param release the release
   * @return the release info
   * @throws Exception the exception
   */
  public ReleaseInfo getReleaseInfo(String release) throws Exception;

  /**
   * Adds the release info.
   *
   * @param releaseInfo the release info
   * @return the release info
   * @throws Exception the exception
   */
  public ReleaseInfo addReleaseInfo(ReleaseInfo releaseInfo) throws Exception;
  
  /**
   * Updates release info.
   *
   * @param releaseInfo the release info
   * @throws Exception the exception
   */
  public void updateReleaseInfo(ReleaseInfo releaseInfo) throws Exception;
  
  /**
   * Removes the release info.
   *
   * @param id the id
   * @throws Exception the exception
   */
  public void removeReleaseInfo(Long id) throws Exception;
  
}