/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.services.helpers;

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
