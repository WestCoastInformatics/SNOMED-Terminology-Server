package org.ihtsdo.otf.ts.jpa.algo;

import org.ihtsdo.otf.ts.jpa.services.helper.ProgressReporter;

/**
 * Generically represents an algortihm. Implementations must fully configure
 * themselves before the compute call is made.
 */
public interface Algorithm extends ProgressReporter {

  /**
   * Rests to initial conditions.
   *
   * @throws Exception the exception
   */
  public void reset() throws Exception;

  /**
   * Compute.
   *
   * @throws Exception the exception
   */
  public void compute() throws Exception;
  
  /**
   * Cancel.
   *
   * @throws Exception the exception
   */
  public void cancel() throws Exception;
}
