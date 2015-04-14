/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.rest;

import org.ihtsdo.otf.ts.ReleaseInfo;
import org.ihtsdo.otf.ts.helpers.AssociationReferenceRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.AttributeValueRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ComplexMapRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.DescriptionList;
import org.ihtsdo.otf.ts.helpers.DescriptionTypeRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.LanguageRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.ModuleDependencyRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.RefsetDescriptorRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.RelationshipList;
import org.ihtsdo.otf.ts.helpers.ReleaseInfoList;
import org.ihtsdo.otf.ts.helpers.SimpleMapRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.SimpleRefSetMemberList;
import org.ihtsdo.otf.ts.jpa.ReleaseInfoJpa;
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

/**
 * Represents a history services available via a REST service.
 *
 * @author ${author}
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
  public void removeReleaseInfo(Long id, String authToken) throws Exception;

  /**
   * Start editing cycle for a release.
   *
   * @param releaseVersion the release version
   * @param terminology the terminology
   * @param version the terminology version
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void startEditingCycle(String releaseVersion, String terminology,
    String version, String authToken) throws Exception;

  /**
   * Begin rf2 release.
   *
   * @param releaseVersion the release version
   * @param terminology the terminology
   * @param validate the validate
   * @param workflowStatusValues the workflow status values
   * @param saveIdentifiers the save identifiers
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void beginRf2Release(String releaseVersion, String terminology,
    boolean validate, String workflowStatusValues, boolean saveIdentifiers,
    String authToken) throws Exception;

  /**
   * Process rf2 release.
   *
   * @param releaseVersion the release version
   * @param terminology the terminology
   * @param outputDir the output dir
   * @param moduleId the module id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void processRf2Release(String releaseVersion, String terminology,
    String outputDir, String moduleId, String authToken) throws Exception;

  /**
   * Finish rf2 release.
   *
   * @param releaseVersion the release version
   * @param terminology the terminology
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void finishRf2Release(String releaseVersion, String terminology,
    String authToken) throws Exception;

  /**
   * Find association reference ref set member release revision.
   *
   * @param id the id
   * @param release the release
   * @param authToken the auth token
   * @return the association reference ref set member
   * @throws Exception the exception
   */
  public AssociationReferenceRefSetMember<?> findAssociationReferenceRefSetMemberReleaseRevision(
    String id, String release, String authToken) throws Exception;

  /**
   * Find association reference ref set members modified since date.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the association reference ref set member list
   * @throws Exception the exception
   */
  public AssociationReferenceRefSetMemberList findAssociationReferenceRefSetMembersModifiedSinceDate(
    String terminology, String date, PfsParameterJpa pfs, String authToken)
    throws Exception;

  /**
   * Find association reference ref set member revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the association reference ref set member list
   * @throws Exception the exception
   */
  public AssociationReferenceRefSetMemberList findAssociationReferenceRefSetMemberRevisions(
    String id, String startDate, String endDate, PfsParameterJpa pfs,
    String authToken) throws Exception;

  /**
   * Find attribute value ref set members modified since date.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the attribute value ref set member list
   * @throws Exception the exception
   */
  public AttributeValueRefSetMemberList findAttributeValueRefSetMembersModifiedSinceDate(
    String terminology, String date, PfsParameterJpa pfs, String authToken)
    throws Exception;

  /**
   * Find attribute value ref set member revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the attribute value ref set member list
   * @throws Exception the exception
   */
  public AttributeValueRefSetMemberList findAttributeValueRefSetMemberRevisions(
    String id, String startDate, String endDate, PfsParameterJpa pfs,
    String authToken) throws Exception;

  /**
   * Find attribute value ref set member release revision.
   *
   * @param id the id
   * @param release the release
   * @param authToken the auth token
   * @return the attribute value ref set member
   * @throws Exception the exception
   */
  public AttributeValueRefSetMember<?> findAttributeValueRefSetMemberReleaseRevision(
    String id, String release, String authToken) throws Exception;

  /**
   * Find complex map ref set members modified since date.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the complex map ref set member list
   * @throws Exception the exception
   */
  public ComplexMapRefSetMemberList findComplexMapRefSetMembersModifiedSinceDate(
    String terminology, String date, PfsParameterJpa pfs, String authToken)
    throws Exception;

  /**
   * Find complex map ref set member revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the complex map ref set member list
   * @throws Exception the exception
   */
  public ComplexMapRefSetMemberList findComplexMapRefSetMemberRevisions(
    String id, String startDate, String endDate, PfsParameterJpa pfs,
    String authToken) throws Exception;

  /**
   * Find complex map ref set member release revision.
   *
   * @param id the id
   * @param release the release
   * @param authToken the auth token
   * @return the complex map ref set member
   * @throws Exception the exception
   */
  public ComplexMapRefSetMember findComplexMapRefSetMemberReleaseRevision(
    String id, String release, String authToken) throws Exception;

  /**
   * Find description type ref set members modified since date.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the description type ref set member list
   * @throws Exception the exception
   */
  public DescriptionTypeRefSetMemberList findDescriptionTypeRefSetMembersModifiedSinceDate(
    String terminology, String date, PfsParameterJpa pfs, String authToken)
    throws Exception;

  /**
   * Find description type ref set member revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the description type ref set member list
   * @throws Exception the exception
   */
  public DescriptionTypeRefSetMemberList findDescriptionTypeRefSetMemberRevisions(
    String id, String startDate, String endDate, PfsParameterJpa pfs,
    String authToken) throws Exception;

  /**
   * Find description type ref set member release revision.
   *
   * @param id the id
   * @param release the release
   * @param authToken the auth token
   * @return the description type ref set member
   * @throws Exception the exception
   */
  public DescriptionTypeRefSetMember findDescriptionTypeRefSetMemberReleaseRevision(
    String id, String release, String authToken) throws Exception;

  /**
   * Find module dependency ref set members modified since date.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the module dependency ref set member list
   * @throws Exception the exception
   */
  public ModuleDependencyRefSetMemberList findModuleDependencyRefSetMembersModifiedSinceDate(
    String terminology, String date, PfsParameterJpa pfs, String authToken)
    throws Exception;

  /**
   * Find module dependency ref set member revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the module dependency ref set member list
   * @throws Exception the exception
   */
  public ModuleDependencyRefSetMemberList findModuleDependencyRefSetMemberRevisions(
    String id, String startDate, String endDate, PfsParameterJpa pfs,
    String authToken) throws Exception;

  /**
   * Find module dependency ref set member release revision.
   *
   * @param id the id
   * @param release the release
   * @param authToken the auth token
   * @return the module dependency ref set member
   * @throws Exception the exception
   */
  public ModuleDependencyRefSetMember findModuleDependencyRefSetMemberReleaseRevision(
    String id, String release, String authToken) throws Exception;

  /**
   * Find refset descriptor ref set members modified since date.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the refset descriptor ref set member list
   * @throws Exception the exception
   */
  public RefsetDescriptorRefSetMemberList findRefsetDescriptorRefSetMembersModifiedSinceDate(
    String terminology, String date, PfsParameterJpa pfs, String authToken)
    throws Exception;

  /**
   * Find refset descriptor ref set member revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the refset descriptor ref set member list
   * @throws Exception the exception
   */
  public RefsetDescriptorRefSetMemberList findRefsetDescriptorRefSetMemberRevisions(
    String id, String startDate, String endDate, PfsParameterJpa pfs,
    String authToken) throws Exception;

  /**
   * Find refset descriptor ref set member release revision.
   *
   * @param id the id
   * @param release the release
   * @param authToken the auth token
   * @return the refset descriptor ref set member
   * @throws Exception the exception
   */
  public RefsetDescriptorRefSetMember findRefsetDescriptorRefSetMemberReleaseRevision(
    String id, String release, String authToken) throws Exception;

  /**
   * Find simple map ref set members modified since date.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the simple map ref set member list
   * @throws Exception the exception
   */
  public SimpleMapRefSetMemberList findSimpleMapRefSetMembersModifiedSinceDate(
    String terminology, String date, PfsParameterJpa pfs, String authToken)
    throws Exception;

  /**
   * Find simple map ref set member revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the simple map ref set member list
   * @throws Exception the exception
   */
  public SimpleMapRefSetMemberList findSimpleMapRefSetMemberRevisions(
    String id, String startDate, String endDate, PfsParameterJpa pfs,
    String authToken) throws Exception;

  /**
   * Find simple map ref set member release revision.
   *
   * @param id the id
   * @param release the release
   * @param authToken the auth token
   * @return the simple map ref set member
   * @throws Exception the exception
   */
  public SimpleMapRefSetMember findSimpleMapRefSetMemberReleaseRevision(
    String id, String release, String authToken) throws Exception;

  /**
   * Find simple ref set members modified since date.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the simple ref set member list
   * @throws Exception the exception
   */
  public SimpleRefSetMemberList findSimpleRefSetMembersModifiedSinceDate(
    String terminology, String date, PfsParameterJpa pfs, String authToken)
    throws Exception;

  /**
   * Find simple ref set member revisions.
   *
   * @param id the id
   * @param startDate the start date
   * @param endDate the end date
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the simple ref set member list
   * @throws Exception the exception
   */
  public SimpleRefSetMemberList findSimpleRefSetMemberRevisions(String id,
    String startDate, String endDate, PfsParameterJpa pfs, String authToken)
    throws Exception;

  /**
   * Find simple ref set member release revision.
   *
   * @param id the id
   * @param release the release
   * @param authToken the auth token
   * @return the simple ref set member
   * @throws Exception the exception
   */
  public SimpleRefSetMember findSimpleRefSetMemberReleaseRevision(String id,
    String release, String authToken) throws Exception;

}
