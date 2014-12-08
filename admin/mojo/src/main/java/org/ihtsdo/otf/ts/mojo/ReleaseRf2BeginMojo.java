package org.ihtsdo.otf.ts.mojo;

import java.util.HashSet;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.ihtsdo.otf.ts.helpers.LocalException;
import org.ihtsdo.otf.ts.helpers.ValidationResult;
import org.ihtsdo.otf.ts.jpa.algo.ReleaseRf2BeginAlgorithm;

/**
 * Mojo wrapper around {@link ReleaseRf2BeginAlgorithm}.
 * 
 * See admin/release/pom.xml for sample usage.
 * 
 * @goal release-rf2-begin
 * @phase package
 */
public class ReleaseRf2BeginMojo extends AbstractMojo {

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
      getLog().info("  workflowStatusValues = " + workflowStatusValues);
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
      ReleaseRf2BeginAlgorithm algorithm =
          new ReleaseRf2BeginAlgorithm(releaseVersion, validate, statusSet,
              saveIdentifiers.toLowerCase().equals("true"));
      try {
        algorithm.compute();
      } catch (LocalException e) {
        // validation failure
        ValidationResult result = algorithm.getValidationResult();
        getLog().info("  VALIDATION FAILED");
        for (String error : result.getErrors()) {
          getLog().info("    ERROR: " + error);
        }
        for (String warning : result.getWarnings()) {
          getLog().info("    WARNING: " + warning);
        }
        if (!result.isValid()) {
          throw new Exception("Validation Failed");
        }
      }

      getLog().info("...done");
    } catch (Exception e) {
      e.printStackTrace();
      throw new MojoExecutionException("Performing release begin failed.", e);
    }

  }
}
