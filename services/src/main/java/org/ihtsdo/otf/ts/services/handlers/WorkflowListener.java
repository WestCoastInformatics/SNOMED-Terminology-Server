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
   */
  public void beginTransaction();

  /**
   * Notification pre-commit.
   */
  public void preCommit();

  /**
   * Notification post-commit.
   */
  public void postCommit();

  /**
   * Notification of concept added.
   *
   * @param concept the concept
   */
  public void conceptAdded(Concept concept);

  /**
   * Notification of concept removed.
   *
   * @param concept the concept
   */
  public void conceptRemoved(Concept concept);

  /**
   * Notification of concept update.
   *
   * @param concept the concept
   */
  public void conceptUpdated(Concept concept);

  /**
   * Notification of description added.
   *
   * @param description the description
   */
  public void descriptionAdded(Description description);

  /**
   * Notification of description removed.
   *
   * @param description the description
   */
  public void descriptionRemoved(Description description);

  /**
   * Notification of description update.
   *
   * @param description the description
   */
  public void descriptionUpdated(Description description);

  /**
   * Notification of relationship added.
   *
   * @param relationship the relationship
   */
  public void relationshipAdded(Relationship relationship);

  /**
   * Notification of relationship removed.
   *
   * @param relationship the relationship
   */
  public void relationshipRemoved(Relationship  relationship);

  /**
   * Notification of relationship update.
   *
   * @param relationship the relationship
   */
  public void relationshipUpdated(Relationship  relationship);

  /**
   * Notification of refset member added.
   *
   * @param member the member
   */
  public void refSetMemberAdded(RefSetMember<? extends Component> member);

  /**
   * Notification of refset member removed.
   *
   * @param member the member
   */
  public void refSetMemberRemoved(RefSetMember<? extends Component> member);

  /**
   * Notification of ref set member update.
   *
   * @param member the member
   */
  public void refSetMemberUpdated(RefSetMember<? extends Component> member);

}
