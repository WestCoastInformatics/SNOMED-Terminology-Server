package org.ihtsdo.otf.mapping.services;

import java.util.Date;

import org.ihtsdo.otf.mapping.helpers.ConceptList;
import org.ihtsdo.otf.mapping.helpers.DescriptionList;
import org.ihtsdo.otf.mapping.helpers.LanguageRefSetMemberList;
import org.ihtsdo.otf.mapping.helpers.PfsParameter;
import org.ihtsdo.otf.mapping.helpers.RelationshipList;


/**
 * Generically represents a service for asking questions about content
 * history.
 */
public interface HistoryService extends ContentService {

  /**
   * Find concepts modified since date.
   *
   * @param terminology the terminology
   * @param date the date
   * @param pfsParameter the pfs parameter
   * @return the search result list
   * @throws Exception the exception
   */
  public ConceptList getConceptsModifiedSinceDate(String terminology,
    Date date, PfsParameter pfsParameter) throws Exception;

  /**
   * Find descriptions modified since date.
   * 
   * @param terminology the terminology
   * @param date the date
   * @return the search result list
   */
  public DescriptionList getDescriptionsModifiedSinceDate(String terminology,
    Date date);

  /**
   * Find relationships modified since date.
   * 
   * @param terminology the terminology
   * @param date the date
   * @return the search result list
   */
  public RelationshipList getRelationshipsModifiedSinceDate(String terminology,
    Date date);

  /**
   * Find language ref set members modified since date.
   * 
   * @param terminology the terminology
   * @param date the date
   * @return the search result list
   */
  public LanguageRefSetMemberList getLanguageRefSetMembersModifiedSinceDate(
    String terminology, Date date);

}