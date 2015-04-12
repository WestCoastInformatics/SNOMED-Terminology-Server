/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.services.handlers;

import org.ihtsdo.otf.ts.helpers.Configurable;
import org.ihtsdo.otf.ts.rf2.Component;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.RefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;

/**
 * Generically represents a validation check on a concept.
 */
public interface WorkflowListener extends Configurable {

  /**
   * Notification of transaction starting.
   *
   * @throws Exception the exception
   */
  public void beginTransaction() throws Exception;

  /**
   * Notification pre-commit.
   *
   * @throws Exception the exception
   */
  public void preCommit() throws Exception;

  /**
   * Notification post-commit.
   *
   * @throws Exception the exception
   */
  public void postCommit() throws Exception;

  /**
   * Classification started.
   *
   * @throws Exception the exception
   */
  public void classificationStarted() throws Exception;
  
  /**
   * Classification finished.
   *
   * @throws Exception the exception
   */
  public void classificationFinished() throws Exception;
  
  /**
   * Pre classification started.
   *
   * @throws Exception the exception
   */
  public void preClassificationStarted() throws Exception;
  
  /**
   * Pre classification finished.
   *
   * @throws Exception the exception
   */
  public void preClassificationFinished() throws Exception;
  
  /**
   * Notification of concept added.
   *
   * @param concept the concept
   * @throws Exception the exception
   */
  public void conceptAdded(Concept concept) throws Exception;

  /**
   * Notification of concept removed.
   *
   * @param concept the concept
   * @throws Exception the exception
   */
  public void conceptRemoved(Concept concept) throws Exception;

  /**
   * Notification of concept update.
   *
   * @param concept the concept
   * @throws Exception the exception
   */
  public void conceptUpdated(Concept concept) throws Exception;

  /**
   * Notification of description added.
   *
   * @param description the description
   * @throws Exception the exception
   */
  public void descriptionAdded(Description description) throws Exception;

  /**
   * Notification of description removed.
   *
   * @param description the description
   * @throws Exception the exception
   */
  public void descriptionRemoved(Description description) throws Exception;

  /**
   * Notification of description update.
   *
   * @param description the description
   * @throws Exception the exception
   */
  public void descriptionUpdated(Description description) throws Exception;

  /**
   * Notification of relationship added.
   *
   * @param relationship the relationship
   * @throws Exception the exception
   */
  public void relationshipAdded(Relationship relationship) throws Exception;

  /**
   * Notification of relationship removed.
   *
   * @param relationship the relationship
   * @throws Exception the exception
   */
  public void relationshipRemoved(Relationship  relationship) throws Exception;

  /**
   * Notification of relationship update.
   *
   * @param relationship the relationship
   * @throws Exception the exception
   */
  public void relationshipUpdated(Relationship  relationship) throws Exception;

  /**
   * Notification of refset member added.
   *
   * @param member the member
   * @throws Exception the exception
   */
  public void refSetMemberAdded(RefSetMember<? extends Component> member) throws Exception;

  /**
   * Notification of refset member removed.
   *
   * @param member the member
   * @throws Exception the exception
   */
  public void refSetMemberRemoved(RefSetMember<? extends Component> member) throws Exception;

  /**
   * Notification of ref set member update.
   *
   * @param member the member
   * @throws Exception the exception
   */
  public void refSetMemberUpdated(RefSetMember<? extends Component> member) throws Exception;

  /**
   * Notification of a cancelled operation.
   */
  public void cancel();
}
