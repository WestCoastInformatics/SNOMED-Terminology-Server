package org.ihtsdo.otf.mapping.jpa.algo;

/**
 * Generically represents an algortihm. Implementations must fully configure
 * themselves before the compute call is made.
 */
public interface Algorithm {

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
}
