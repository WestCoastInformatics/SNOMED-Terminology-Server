package org.ihtsdo.otf.ts.services;

import java.util.Date;

import org.ihtsdo.otf.ts.ReleaseInfo;
import org.ihtsdo.otf.ts.helpers.AssociationReferenceRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.AttributeValueRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ComplexMapRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.DescriptionList;
import org.ihtsdo.otf.ts.helpers.DescriptionTypeRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.LanguageRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ModuleDependencyRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.PfsParameter;
import org.ihtsdo.otf.ts.helpers.RefsetDescriptorRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.RelationshipList;
import org.ihtsdo.otf.ts.helpers.ReleaseInfoList;
import org.ihtsdo.otf.ts.helpers.SimpleMapRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.SimpleRefSetMemberList;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceRefSetMember;
import org.ihtsdo.otf.ts.rf2.AttributeValueRefSetMember;
import org.ihtsdo.otf.ts.rf2.ComplexMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.DescriptionTypeRefSetMember;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.ModuleDependencyRefSetMember;
import org.ihtsdo.otf.ts.rf2.RefsetDescriptorRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.SimpleMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.SimpleRefSetMember;

// TODO: Auto-generated Javadoc
/**
 * Generically represents a service for asking questions about content history.
 *
 * @author ${author}
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
   * @param pfs the pfs parameter
   * @return the concept revisions
   * @throws Exception the exception
   */
  public ConceptList findConceptRevisions(Long id, Date startDate,
    Date endDate, PfsParameter pfs) throws Exception;

  /**
   * Find concept release revision.
   *
   * @param id the id
   * @param release the release
   * @return the description list
   * @throws Exception the exception
   */
  public Concept findConceptReleaseRevision(Long id, Date release) throws Exception;

  /**
   * Find descriptions modified since date.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs parameter
   * @return the search result list
   * @throws Exception the exception
   */
  public DescriptionList findDescriptionsModifiedSinceDate(String terminology,
    Date date, PfsParameter pfs) throws Exception;

  /**
   * Finds all description revisions for the specified date range.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs parameter
   * @return the description list
   * @throws Exception the exception
   */
  public DescriptionList findDescriptionRevisions(Long id, Date startDate,
    Date endDate, PfsParameter pfs) throws Exception;

  /**
   * Find description release revision.
   *
   * @param id the id
   * @param release the release
   * @return the description list
   * @throws Exception the exception
   */
  public Description findDescriptionReleaseRevision(Long id, Date release) throws Exception;

  /**
   * Find relationships modified since date.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs parameter
   * @return the search result list
   * @throws Exception the exception
   */
  public RelationshipList findRelationshipsModifiedSinceDate(
    String terminology, Date date, PfsParameter pfs) throws Exception;

  /**
   * Find relationship release revision.
   *
   * @param id the id
   * @param release the release
   * @return the description list
   * @throws Exception the exception
   */
  public Relationship findRelationshipReleaseRevision(Long id, Date release) throws Exception;

  /**
   * Finds all relationship revisions for the specified date range.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs parameter
   * @return the relationship list
   * @throws Exception the exception
   */
  public RelationshipList findRelationshipRevisions(Long id, Date startDate,
    Date endDate, PfsParameter pfs) throws Exception;

  /**
   * Find language ref set members modified since date.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs parameter
   * @return the search result list
   * @throws Exception the exception
   */
  public LanguageRefSetMemberList findLanguageRefSetMembersModifiedSinceDate(
    String terminology, Date date, PfsParameter pfs) throws Exception;

  /**
   * Find language ref set member revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs parameter
   * @return the language ref set member list
   * @throws Exception the exception
   */
  public LanguageRefSetMemberList findLanguageRefSetMemberRevisions(Long id,
    Date startDate, Date endDate, PfsParameter pfs) throws Exception;

  /**
   * Find language refset member release revision.
   *
   * @param id the id
   * @param release the release
   * @return the description list
   * @throws Exception the exception
   */
  public LanguageRefSetMember findLanguageRefSetMemberReleaseRevision(
    Long id, Date release) throws Exception;

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
   * @param terminology the terminology
   * @return the release history
   * @throws Exception the exception
   */
  public ReleaseInfoList getReleaseHistory(String terminology) throws Exception;

  /**
   * Returns the current published release info.
   *
   * @param terminology the terminology
   * @return the current release info
   * @throws Exception the exception
   */
  public ReleaseInfo getCurrentReleaseInfo(String terminology) throws Exception;

  /**
   * Returns the previous published release info.
   *
   * @param terminology the terminology
   * @return the previous release info
   * @throws Exception the exception
   */
  public ReleaseInfo getPreviousReleaseInfo(String terminology) throws Exception;

  /**
   * Gets the planned release info. (planned not published)
   *
   * @param terminology the terminology
   * @return the planned release info
   * @throws Exception the exception
   */
  public ReleaseInfo getPlannedReleaseInfo(String terminology) throws Exception;

  /**
   * Returns the release info.
   *
   * @param terminology the terminology
   * @param name the name
   * @return the release info
   * @throws Exception the exception
   */
  public ReleaseInfo getReleaseInfo(String terminology, String name) throws Exception;

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

  /**
   * Find abstract association reference ref set members modified since date.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs
   * @return the association reference ref set member list
   * @throws Exception the exception
   */
  public AssociationReferenceRefSetMemberList findAbstractAssociationReferenceRefSetMembersModifiedSinceDate(
    String terminology, Date date, PfsParameter pfs) throws Exception;

  /**
   * Find abstract association reference ref set member revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @return the association reference ref set member list
   */
  public AssociationReferenceRefSetMemberList findAbstractAssociationReferenceRefSetMemberRevisions(
    Long id, Date startDate, Date endDate, PfsParameter pfs);

  /**
   * Find abstract association reference ref set member release revision.
   *
   * @param id the id
   * @param release the release
   * @return the association reference ref set member
   * @throws Exception the exception
   */
  public AssociationReferenceRefSetMember<?> findAbstractAssociationReferenceRefSetMemberReleaseRevision(
    Long id, Date release) throws Exception;
  
  /**
   * Find attribute value ref set members modified since date.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs
   * @return the attribute value ref set member list
   * @throws Exception the exception
   */
  public AttributeValueRefSetMemberList findAttributeValueRefSetMembersModifiedSinceDate(
    String terminology, Date date, PfsParameter pfs) throws Exception;

  /**
   * Find attribute value ref set member revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @return the attribute value ref set member list
   */
  public AttributeValueRefSetMemberList findAttributeValueRefSetMemberRevisions(
    Long id, Date startDate, Date endDate, PfsParameter pfs);

  /**
   * Find attribute value ref set member release revision.
   *
   * @param id the id
   * @param release the release
   * @return the attribute value ref set member
   * @throws Exception the exception
   */
  public AttributeValueRefSetMember<?> findAttributeValueRefSetMemberReleaseRevision(
    Long id, Date release) throws Exception;

  /**
   * Find complex map ref set members modified since date.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs
   * @return the complex map ref set member list
   * @throws Exception the exception
   */
  public ComplexMapRefSetMemberList findComplexMapRefSetMembersModifiedSinceDate(
    String terminology, Date date, PfsParameter pfs) throws Exception;

  /**
   * Find complex map ref set member revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @return the complex map ref set member list
   */
  public ComplexMapRefSetMemberList findComplexMapRefSetMemberRevisions(Long id,
    Date startDate, Date endDate, PfsParameter pfs);

  /**
   * Find complex map ref set member release revision.
   *
   * @param id the id
   * @param release the release
   * @return the complex map ref set member
   * @throws Exception the exception
   */
  public ComplexMapRefSetMember findComplexMapRefSetMemberReleaseRevision(Long id,
    Date release) throws Exception;

  /**
   * Find description type ref set members modified since date.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs
   * @return the description type ref set member list
   * @throws Exception the exception
   */
  public DescriptionTypeRefSetMemberList findDescriptionTypeRefSetMembersModifiedSinceDate(
    String terminology, Date date, PfsParameter pfs) throws Exception;

  /**
   * Find description type ref set member revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @return the description type ref set member list
   */
  public DescriptionTypeRefSetMemberList findDescriptionTypeRefSetMemberRevisions(
    Long id, Date startDate, Date endDate, PfsParameter pfs);

  /**
   * Find description type ref set member release revision.
   *
   * @param id the id
   * @param release the release
   * @return the description type ref set member
   * @throws Exception the exception
   */
  public DescriptionTypeRefSetMember findDescriptionTypeRefSetMemberReleaseRevision(
    Long id, Date release) throws Exception;

  /**
   * Find module dependency ref set members modified since date.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs
   * @return the module dependency ref set member list
   * @throws Exception the exception
   */
  public ModuleDependencyRefSetMemberList findModuleDependencyRefSetMembersModifiedSinceDate(
    String terminology, Date date, PfsParameter pfs) throws Exception;

  /**
   * Find module dependency ref set member revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @return the module dependency ref set member list
   */
  public ModuleDependencyRefSetMemberList findModuleDependencyRefSetMemberRevisions(
    Long id, Date startDate, Date endDate, PfsParameter pfs);

  /**
   * Find module dependency ref set member release revision.
   *
   * @param id the id
   * @param release the release
   * @return the module dependency ref set member
   * @throws Exception the exception
   */
  public ModuleDependencyRefSetMember findModuleDependencyRefSetMemberReleaseRevision(
    Long id, Date release) throws Exception;

  /**
   * Find refset descriptor ref set members modified since date.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs
   * @return the refset descriptor ref set member list
   * @throws Exception the exception
   */
  public RefsetDescriptorRefSetMemberList findRefsetDescriptorRefSetMembersModifiedSinceDate(
    String terminology, Date date, PfsParameter pfs) throws Exception;

  /**
   * Find refset descriptor ref set member revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @return the refset descriptor ref set member list
   */
  public RefsetDescriptorRefSetMemberList findRefsetDescriptorRefSetMemberRevisions(
    Long id, Date startDate, Date endDate, PfsParameter pfs);

  /**
   * Find refset descriptor ref set member release revision.
   *
   * @param id the id
   * @param release the release
   * @return the refset descriptor ref set member
   * @throws Exception the exception
   */
  public RefsetDescriptorRefSetMember findRefsetDescriptorRefSetMemberReleaseRevision(
    Long id, Date release) throws Exception;

  /**
   * Find simple map ref set members modified since date.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs
   * @return the simple map ref set member list
   * @throws Exception the exception
   */
  public SimpleMapRefSetMemberList findSimpleMapRefSetMembersModifiedSinceDate(
    String terminology, Date date, PfsParameter pfs) throws Exception;

  /**
   * Find simple map ref set member revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @return the simple map ref set member list
   */
  public SimpleMapRefSetMemberList findSimpleMapRefSetMemberRevisions(Long id,
    Date startDate, Date endDate, PfsParameter pfs);

  /**
   * Find simple map ref set member release revision.
   *
   * @param id the id
   * @param release the release
   * @return the simple map ref set member
   * @throws Exception the exception
   */
  public SimpleMapRefSetMember findSimpleMapRefSetMemberReleaseRevision(Long id,
    Date release) throws Exception;

  /**
   * Find simple ref set members modified since date.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs
   * @return the simple ref set member list
   * @throws Exception the exception
   */
  public SimpleRefSetMemberList findSimpleRefSetMembersModifiedSinceDate(
    String terminology, Date date, PfsParameter pfs) throws Exception;

  /**
   * Find simple ref set member revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @return the simple ref set member list
   */
  public SimpleRefSetMemberList findSimpleRefSetMemberRevisions(Long id,
    Date startDate, Date endDate, PfsParameter pfs);

  /**
   * Find simple ref set member release revision.
   *
   * @param id the id
   * @param release the release
   * @return the simple ref set member
   * @throws Exception the exception
   */
  public SimpleRefSetMember findSimpleRefSetMemberReleaseRevision(Long id, Date release)
    throws Exception;




}