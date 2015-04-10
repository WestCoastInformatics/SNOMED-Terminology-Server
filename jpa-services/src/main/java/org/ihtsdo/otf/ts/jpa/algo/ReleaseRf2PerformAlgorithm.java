/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.jpa.algo;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.algo.Algorithm;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.services.helpers.ProgressEvent;
import org.ihtsdo.otf.ts.services.helpers.ProgressListener;

/**
 * An algorithm for performing an RF2 release.
 */
public class ReleaseRf2PerformAlgorithm extends ContentServiceJpa implements
    Algorithm {

  /** The release version. */
  private String releaseVersion = null;

  /** The terminology. */
  private String terminology = null;

  /** The output dir. */
  private String outputDir = null;

  /** The module id. */
  private String moduleId = null;

  /** The request cancel flag. */
  boolean requestCancel = false;

  /** Listeners. */
  private List<ProgressListener> listeners = new ArrayList<>();

  /**
   * Instantiates an empty {@link ReleaseRf2PerformAlgorithm}.
   *
   * @param releaseVersion the release version
   * @param terminology the terminology
   * @param outputDir the output dir
   * @param moduleId the module id
   * @throws Exception if anything goes wrong
   */
  public ReleaseRf2PerformAlgorithm(String releaseVersion, String terminology,
      String outputDir, String moduleId) throws Exception {
    super();
    this.releaseVersion = releaseVersion;
    this.terminology = terminology;
    this.outputDir = outputDir;
    this.moduleId = moduleId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.jpa.algo.Algorithm#reset()
   */
  @Override
  public void reset() throws Exception {
    // do nothing

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.jpa.algo.Algorithm#compute()
   */
  @Override
  public void compute() throws Exception {
    fireProgressEvent(0, "Starting processing of RF2 release");
    Logger.getLogger(getClass()).info("  releaseVersion = " + releaseVersion);
    Logger.getLogger(getClass()).info("  terminology = " + terminology);
    Logger.getLogger(getClass()).info("  outputDir = " + outputDir);
    Logger.getLogger(getClass()).info("  moduleId = " + moduleId);

    // done, log and close
    fireProgressEvent(100, "Finished processing of RF2 release");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.jpa.services.helper.ProgressReporter#addProgressListener
   * (org.ihtsdo.otf.ts.jpa.services.helper.ProgressListener)
   */
  @Override
  public void addProgressListener(ProgressListener l) {
    listeners.add(l);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.jpa.services.helper.ProgressReporter#removeProgressListener
   * (org.ihtsdo.otf.ts.jpa.services.helper.ProgressListener)
   */
  @Override
  public void removeProgressListener(ProgressListener l) {
    listeners.remove(l);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.jpa.algo.Algorithm#cancel()
   */
  @Override
  public void cancel() {
    requestCancel = true;
  }

  /**
   * Fire progress event.
   *
   * @param pct the pct
   * @param note the note
   */
  protected void fireProgressEvent(int pct, String note) {
    ProgressEvent pe = new ProgressEvent(this, pct, pct, note);
    for (int i = 0; i < listeners.size(); i++) {
      listeners.get(i).updateProgress(pe);
    }
    Logger.getLogger(getClass()).info("    " + pct + "% " + note);
  }

}
