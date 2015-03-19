package org.ihtsdo.otf.ts.jpa.algo;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.ReleaseInfo;
import org.ihtsdo.otf.ts.algo.Algorithm;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.jpa.ReleaseInfoJpa;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.HistoryServiceJpa;
import org.ihtsdo.otf.ts.services.HistoryService;
import org.ihtsdo.otf.ts.services.helpers.ProgressEvent;
import org.ihtsdo.otf.ts.services.helpers.ProgressListener;

/**
 * An algorithm for starting an editing cycle.
 * 
 * Mostly, this creates a {@link ReleaseInfo} for the upcoming release. It is
 * typically run after {@link ReleaseRf2FinishAlgorithm}.
 *
 */
public class StartEditingCycleAlgorithm extends ContentServiceJpa implements
    Algorithm {

  /** The release version. */
  private String releaseVersion = null;

  /** The terminology. */
  private String terminology = null;

  /** The terminology version. */
  private String terminologyVersion = null;

  /** Listeners. */
  private List<ProgressListener> listeners = new ArrayList<>();

  /** The request cancel flag. */
  boolean requestCancel = false;

  /**
   * Instantiates an empty {@link StartEditingCycleAlgorithm}.
   *
   * @param releaseVersion the release version
   * @param terminology the terminology
   * @param version the terminology version
   * @throws Exception if anything goes wrong
   */
  public StartEditingCycleAlgorithm(String releaseVersion, String terminology,
      String version) throws Exception {
    super();
    this.releaseVersion = releaseVersion;
    this.terminology = terminology;
    this.terminologyVersion = version;
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
    Logger.getLogger(getClass()).info(
        "Starting editing cycle for " + releaseVersion);

    // Check that there is a planned release info entry that has not yet been
    // started
    HistoryService service = new HistoryServiceJpa();
    ReleaseInfo info = service.getReleaseInfo(terminology, releaseVersion);
    if (info != null) {
      throw new Exception("Editing cycle already started for " + releaseVersion);
    }

    Logger.getLogger(getClass()).info("  Create release info");
    info = new ReleaseInfoJpa();
    info.setDescription("RF2 Release for " + releaseVersion);
    info.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(releaseVersion));
    info.setName(releaseVersion);
    info.setPlanned(true);
    info.setPublished(false);
    info.setTerminology(terminology);
    info.setTerminologyVersion(terminologyVersion);
    service.addReleaseInfo(info);
    service.close();

    Logger.getLogger(getClass()).info("Done starting editing cycle");

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
    Logger.getLogger(getClass()).info("    " + pct + "% " + note);
  }

}
