package org.ihtsdo.otf.ts.jpa.algo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.helper.ProgressEvent;
import org.ihtsdo.otf.ts.jpa.services.helper.ProgressListener;

/**
 * An algorithm for finishing an RF2 release. The following steps are included
 * 
 * <pre>
 *   - Assign the final identifiers and update null effective times
 *   - Mark the release info as published
 * </pre>
 */
public class ReleaseRf2FinishAlgorithm extends ContentServiceJpa implements
    Algorithm {

  /** The release version. */
  @SuppressWarnings("unused")
  private String releaseVersion = null;

  /** The workflow status values. */
  @SuppressWarnings("unused")
  private Set<String> workflowStatusValues = null;

  /** Listeners. */
  private List<ProgressListener> listeners = new ArrayList<>();

  /** The request cancel flag. */
  boolean requestCancel = false;

  /**
   * Instantiates an empty {@link ReleaseRf2FinishAlgorithm}.
   *
   * @param releaseVersion the release version
   * @param workflowStatusValues the workflow status values
   * @throws Exception if anything goes wrong
   */
  public ReleaseRf2FinishAlgorithm(String releaseVersion,
      Set<String> workflowStatusValues) throws Exception {
    super();
    this.releaseVersion = releaseVersion;
    this.workflowStatusValues = workflowStatusValues;
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
    // TBD
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
  public void fireProgressEvent(int pct, String note) {
    ProgressEvent pe = new ProgressEvent(this, pct, pct, note);
    for (int i = 0; i < listeners.size(); i++) {
      listeners.get(i).updateProgress(pe);
    }
    Logger.getLogger(this.getClass()).info("    " + pct + "% " + note);
  }

}