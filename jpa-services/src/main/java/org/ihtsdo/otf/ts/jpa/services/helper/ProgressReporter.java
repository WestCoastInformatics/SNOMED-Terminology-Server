package org.ihtsdo.otf.ts.jpa.services.helper;

/**
 * Generically something that will report progress to a listener.
 */
public interface ProgressReporter {

  /**
   * Adds a {@link ProgressListener}.
   * @param l thef{@link ProgressListener}
   */
  public void addProgressListener(ProgressListener l);
  
  /**
   * Removes a {@link ProgressListener}.
   * @param l thef{@link ProgressListener}
   */
  public void removeProgressListener(ProgressListener l);
}
