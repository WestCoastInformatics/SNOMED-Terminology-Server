package org.ihtsdo.otf.ts.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.ihtsdo.otf.ts.jpa.algo.StartEditingCycleAlgorithm;

/**
 * Admin tool for indicating the beginning of a release process.
 * 
 * See admin/release/pom.xml for sample usage.
 * 
 * @goal start-editing-cycle
 * @phase package
 */
public class StartEditingCycleMojo extends AbstractMojo {

  /**
   * The release version
   * 
   * @parameter releaseVersion
   */
  private String releaseVersion = null;

  /**
   * The terminology
   * 
   * @parameter terminology
   */
  private String terminology = null;

  /**
   * The terminology version
   * 
   * @parameter terminologyVersion
   */
  private String terminologyVersion = null;

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.maven.plugin.Mojo#execute()
   */
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      // log start
      getLog().info("Starting editing cycle for: ");
      getLog().info("  releaseVersion = " + releaseVersion);
      getLog().info("  terminology = " + terminology);
      getLog().info("  terminologyVersion = " + terminologyVersion);

      // Check preconditions
      if (releaseVersion == null) {
        throw new Exception("A release version must be specified.");
      }

      if (terminology == null) {
        throw new Exception("A terminology must be specified.");
      }

      if (terminologyVersion == null) {
        throw new Exception("A terminology version must be specified.");
      }

      // Perform operations
      StartEditingCycleAlgorithm algorithm =
          new StartEditingCycleAlgorithm(releaseVersion, terminology,
              terminologyVersion);
      algorithm.compute();
      
      getLog().info("...done");
    } catch (Exception e) {
      e.printStackTrace();
      throw new MojoExecutionException("Performing release begin failed.", e);
    }

  }
}
