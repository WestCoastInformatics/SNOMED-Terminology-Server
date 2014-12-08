package org.ihtsdo.otf.ts.jpa.services.helper;

/**
 * Generically listens for progress updates.
 *
 * @see ProgressEvent
 */
public interface ProgressListener {

  /**
   * Update progress.
   *
   * @param pe the pe
   */
  public void updateProgress(ProgressEvent pe);

}
