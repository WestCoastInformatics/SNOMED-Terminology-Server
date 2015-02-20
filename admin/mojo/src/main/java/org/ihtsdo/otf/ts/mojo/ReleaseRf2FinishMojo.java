package org.ihtsdo.otf.ts.mojo;

import java.util.HashSet;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.ihtsdo.otf.ts.jpa.algo.ReleaseRf2FinishAlgorithm;

/**
 * Mojo wrapper around {@link ReleaseRf2FinishAlgorithm}.
 * 
 * See admin/release/pom.xml for sample usage.
 * 
 * @goal release-rf2-finish
 * @phase package
 */
public class ReleaseRf2FinishMojo extends AbstractMojo {

  /**
   * The terminology
   * @parameter terminology
   */
  private String terminology = null;
  
  /**
   * The release version
   * 
   * @parameter releaseVersion
   */
  private String releaseVersion = null;

  /**
   * The validation flag.
   * 
   * @parameter validate
   */
  private boolean validate = false;

  /**
   * The workflow status values.
   * 
   * @parameter workflowStatusValues
   */
  private String workflowStatusValues = null;

  /**
   * A flag indicating whether to save identifiers.
   * 
   * @parameter saveIdentifiers
   * @required
   */
  private String saveIdentifiers = null;

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.maven.plugin.Mojo#execute()
   */
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      // log start
      getLog().info("Starting Release");
      getLog().info("  releaseVersion = " + releaseVersion);
      getLog().info("  validate = " + validate);
      getLog().info("  workflowStatusvalues = " + workflowStatusValues);
      getLog().info("  saveIdentfiers = " + saveIdentifiers);

      // Check preconditions
      if (releaseVersion == null) {
        throw new Exception("A release version must be specified.");
      }

      // Perform the operation
      Set<String> statusSet = new HashSet<>();
      if (workflowStatusValues != null) {
        for (String status : workflowStatusValues.split(",")) {
          statusSet.add(status);
        }
      }
      // ReleaseRf2FinishAlgorithm algorithm =
      // new ReleaseRf2FinishAlgorithm(releaseVersion, validate, statusSet);
      // algorithm.compute();

      getLog().info("...done");
    } catch (Exception e) {
      e.printStackTrace();
      throw new MojoExecutionException("Performing release finish failed.", e);
    }

  }
}
