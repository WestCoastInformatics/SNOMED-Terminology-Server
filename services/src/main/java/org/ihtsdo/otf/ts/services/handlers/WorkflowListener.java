package org.ihtsdo.otf.ts.services.handlers;

import org.ihtsdo.otf.ts.helpers.Configurable;
import org.ihtsdo.otf.ts.rf2.Concept;

/**
 * Generically represents a validation check on a concept
 */
public interface WorkflowListener extends Configurable {

  /**
   * Notification of concept added.
   *
   * @param concept the concept
   */
  public void conceptAdded(Concept concept);

}
