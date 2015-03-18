package org.ihtsdo.otf.ts.jpa.algo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.ReleaseInfo;
import org.ihtsdo.otf.ts.ValidationResult;
import org.ihtsdo.otf.ts.algo.Algorithm;
import org.ihtsdo.otf.ts.helpers.CancelException;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.LocalException;
import org.ihtsdo.otf.ts.jpa.ValidationResultJpa;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.HistoryServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.ValidationServiceJpa;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.services.HistoryService;
import org.ihtsdo.otf.ts.services.ValidationService;
import org.ihtsdo.otf.ts.services.helpers.ProgressEvent;
import org.ihtsdo.otf.ts.services.helpers.ProgressListener;

/**
 * An algorithm for beginning an RF2 release. The following steps are included
 * 
 * <pre>
 *   - Mark the release info start date
 *   - (Optionally) validate all concepts according to validation checks
 *   - Assigns identifiers
 * </pre>
 */
public class ReleaseRf2BeginAlgorithm extends ContentServiceJpa implements
    Algorithm {

  /** The release version. */
  private String releaseVersion = null;

  /**  The terminology. */
  private String terminology = null;

  /** The validate flag. */
  private boolean validate = false;

  /** The workflow status values. */
  private Set<String> workflowStatusValues = null;

  /** Listeners. */
  private List<ProgressListener> listeners = new ArrayList<>();

  /** The validation result. */
  private ValidationResult validationResult;

  /** The request cancel flag. */
  boolean requestCancel = false;

  /** The save identifiers flag. */
  boolean saveIdentifiers = false;

  /**
   * Instantiates an empty {@link ReleaseRf2BeginAlgorithm}.
   *
   * @param releaseVersion the release version
   * @param terminology the terminology
   * @param validate the validate
   * @param workflowStatusValues the workflow status values
   * @param saveIdentifiers the save identifiers
   * @throws Exception if anything goes wrong
   */
  public ReleaseRf2BeginAlgorithm(String releaseVersion, String terminology,
      boolean validate, Set<String> workflowStatusValues,
      boolean saveIdentifiers) throws Exception {
    super();
    this.releaseVersion = releaseVersion;
    this.terminology = terminology;
    this.validate = validate;
    this.workflowStatusValues = workflowStatusValues;
    this.saveIdentifiers = saveIdentifiers;
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
    fireProgressEvent(0, "Starting begin RF2 release");

    //
    // Check that there is a planned release info entry
    // that has not yet been started
    //
    fireProgressEvent(1, "  Check assumptions");
    HistoryService historyService = new HistoryServiceJpa();
    ReleaseInfo info = historyService.getReleaseInfo(terminology, releaseVersion);
    // must exist
    if (info == null) {
      throw new Exception(
          "Missing a planned release info, StartEditingCycle was never run -"
              + releaseVersion);
    }

    //
    // Mark the beginning of the release
    //
    else if (info.getReleaseBeginDate() == null) {
      Date date = new Date();
      Logger.getLogger(getClass()).info(
          "  Set release begin date = " + date);
      info.setReleaseBeginDate(date);
      historyService.updateReleaseInfo(info);
    }

    //
    // Get all concepts that have changed
    //
    ReleaseInfo previousInfo = historyService.getPreviousReleaseInfo(terminology);
    Date previousDate =
        previousInfo == null ? ConfigUtility.DATE_FORMAT.parse("20140101")
            : previousInfo.getReleaseFinishDate();
    if (previousDate == null) {
      throw new Exception(
          "Unable to compute previous release date or beginning of time");
    }
    ConceptList list =
        historyService.findConceptsDeepModifiedSinceDate(info.getTerminology(),
            previousDate, null);

    //
    // Validate concepts
    //
    fireProgressEvent(2, "  Validate concepts");
    validationResult = new ValidationResultJpa();
    if (validate) {
      Logger.getLogger(getClass()).info("  VALIDATE all concepts");
      ValidationService validationService = new ValidationServiceJpa();
      int ct = 0;
      int progress = 0;
      int progressMax = list.getCount();
      for (Concept concept : list.getObjects()) {

        // handle cancel
        if (requestCancel) {
          throw new CancelException("Operation cancelled");
        }

        // handle progress
        ct++;
        int ctProgress = (int) ((((ct * 100) / progressMax) * .48) + 2);
        if (ctProgress > progress) {
          progress = ctProgress;
          fireProgressEvent((int) ((progress * .48) + 2),
              "  Validating concepts");
        }

        // verify workflow status
        if (workflowStatusValues.isEmpty()
            || workflowStatusValues.contains(concept.getWorkflowStatus())) {
          // increment counter

          ValidationResult conceptValidation =
              validationService.validateConcept(concept);
          if (!conceptValidation.isValid()) {
            validationResult.merge(conceptValidation);
            Logger.getLogger(getClass()).info(
                "    INVALID concept - " + concept.getTerminologyId());
          } else {
            Logger.getLogger(getClass()).debug(
                "    VALID concept - " + concept.getTerminologyId());

          }
        }
      }

    } else {
      Logger.getLogger(getClass()).info("  DO NOT VALIDATE all concepts");
    }
    // handle validation failures
    if (!validationResult.isValid()) {
      throw new LocalException("Validation failed.");
    } else {
      // show warnings
      for (String warning : validationResult.getWarnings()) {
        Logger.getLogger(getClass()).warn("      WARNING:" + warning);
      }
    }

    // Assigns identifiers prior to
    fireProgressEvent(50, "  Assign identifiers");

    // Concepts

    // Descriptions

    //

    // done, log and close
    fireProgressEvent(100, "Finished begin RF2 release");
    historyService.close();
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

  /**
   * Returns the validation result.
   *
   * @return the validation result
   */
  public ValidationResult getValidationResult() {
    return validationResult;
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
