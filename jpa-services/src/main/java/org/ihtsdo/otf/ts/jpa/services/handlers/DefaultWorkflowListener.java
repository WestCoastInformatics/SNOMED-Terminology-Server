/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.jpa.services.handlers;

import java.util.Properties;

import org.ihtsdo.otf.ts.rf2.Component;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.RefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.services.handlers.WorkflowListener;

/**
 * A sample validation check for a new concept meeting the minimum qualifying
 * criteria.
 */
public class DefaultWorkflowListener implements WorkflowListener {

  /**
   * Instantiates an empty {@link DefaultWorkflowListener}.
   */
  public DefaultWorkflowListener() {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.helpers.Configurable#setProperties(java.util.Properties)
   */
  @Override
  public void setProperties(Properties p) {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.WorkflowListener#beginTransaction()
   */
  @Override
  public void beginTransaction() {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.handlers.WorkflowListener#preCommit()
   */
  @Override
  public void preCommit() {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.handlers.WorkflowListener#postCommit()
   */
  @Override
  public void postCommit() {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.WorkflowListener#conceptAdded(org.ihtsdo
   * .otf.ts.rf2.Concept)
   */
  @Override
  public void conceptAdded(Concept concept) {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.WorkflowListener#conceptRemoved(org
   * .ihtsdo.otf.ts.rf2.Concept)
   */
  @Override
  public void conceptRemoved(Concept concept) {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.WorkflowListener#conceptUpdated(org
   * .ihtsdo.otf.ts.rf2.Concept)
   */
  @Override
  public void conceptUpdated(Concept concept) {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.WorkflowListener#descriptionAdded(org
   * .ihtsdo.otf.ts.rf2.Description)
   */
  @Override
  public void descriptionAdded(Description description) {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.WorkflowListener#descriptionRemoved
   * (org.ihtsdo.otf.ts.rf2.Description)
   */
  @Override
  public void descriptionRemoved(Description description) {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.WorkflowListener#descriptionUpdated
   * (org.ihtsdo.otf.ts.rf2.Description)
   */
  @Override
  public void descriptionUpdated(Description description) {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.WorkflowListener#relationshipAdded(
   * org.ihtsdo.otf.ts.rf2.Relationship)
   */
  @Override
  public void relationshipAdded(Relationship relationship) {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.WorkflowListener#relationshipRemoved
   * (org.ihtsdo.otf.ts.rf2.Relationship)
   */
  @Override
  public void relationshipRemoved(Relationship relationship) {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.WorkflowListener#relationshipUpdated
   * (org.ihtsdo.otf.ts.rf2.Relationship)
   */
  @Override
  public void relationshipUpdated(Relationship relationship) {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.WorkflowListener#refSetMemberAdded(
   * org.ihtsdo.otf.ts.rf2.RefSetMember)
   */
  @Override
  public void refSetMemberAdded(RefSetMember<? extends Component> member) {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.WorkflowListener#refSetMemberRemoved
   * (org.ihtsdo.otf.ts.rf2.RefSetMember)
   */
  @Override
  public void refSetMemberRemoved(RefSetMember<? extends Component> member) {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.WorkflowListener#refSetMemberUpdated
   * (org.ihtsdo.otf.ts.rf2.RefSetMember)
   */
  @Override
  public void refSetMemberUpdated(RefSetMember<? extends Component> member) {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.WorkflowListener#classificationStarted
   * ()
   */
  @Override
  public void classificationStarted() throws Exception {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.WorkflowListener#classificationFinished
   * ()
   */
  @Override
  public void classificationFinished() throws Exception {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.WorkflowListener#preClassificationStarted
   * ()
   */
  @Override
  public void preClassificationStarted() throws Exception {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.WorkflowListener#preClassificationFinished
   * ()
   */
  @Override
  public void preClassificationFinished() throws Exception {
    // do nothing
  }

  @Override
  public void cancel() {
    // do nothing
  }
}
