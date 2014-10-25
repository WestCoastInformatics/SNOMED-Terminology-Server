package org.ihtsdo.otf.ts.jpa.services.handlers;

import java.util.Properties;

import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.services.handlers.WorkflowListener;

/**
 * A sample validation check for a new concept meeting the minimum
 * qualifying criteria.
 */
public class DefaultWorkflowListener implements WorkflowListener {

  /**
   * Instantiates an empty {@link DefaultWorkflowListener}.
   */
  public DefaultWorkflowListener() {
    // do nothing
  }
  
  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.helpers.Configurable#setProperties(java.util.Properties)
   */
  @Override
  public void setProperties(Properties p) {
    // do nothing    
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.services.handlers.WorkflowListener#conceptAdded(org.ihtsdo.otf.ts.rf2.Concept)
   */
  @Override
  public void conceptAdded(Concept concept) {
    // do nothing    
  }

}
