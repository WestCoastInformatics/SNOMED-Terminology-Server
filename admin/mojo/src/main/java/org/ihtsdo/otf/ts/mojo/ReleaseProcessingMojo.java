package org.ihtsdo.otf.ts.mojo;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.services.ContentService;

/**
 * Loads unpublished complex maps.
 * 
 * See admin/release/pom.xml for sample usage.
 * 
 * @goal release
 * @phase package
 */
public class ReleaseProcessingMojo extends AbstractMojo {

  /**
   * The refSet id
   * 
   * @parameter refSetId
   */
  private String refSetId = null;

  /**
   * The refSet id
   * 
   * @parameter outputDirName
   */
  private String outputDirName = null;

  /**
   * The effective time of release
   * 
   * @parameter effectiveTime
   */
  private String effectiveTime = null;

  /**
   * The module id.
   * 
   * @parameter moduleId
   */
  private String moduleId = null;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info("Processing release for ref set ids: " + refSetId);

    if (refSetId == null) {
      throw new MojoExecutionException("You must specify a refSetId.");
    }

    if (refSetId == null) {
      throw new MojoExecutionException(
          "You must specify an output file directory.");
    }

    File outputDir = new File(outputDirName);
    if (!outputDir.isDirectory())
      throw new MojoExecutionException("Output file directory ("
          + outputDirName + ") could not be found.");

    if (effectiveTime == null)
      throw new MojoExecutionException("You must specify a release time");

    if (moduleId == null)
      throw new MojoExecutionException("You must specify a module id");

    try {

      ContentService service = new ContentServiceJpa();

      getLog().info("done ...");
      service.close();

    } catch (Exception e) {
      e.printStackTrace();
      throw new MojoExecutionException("Performing release processing failed.",
          e);
    }

  }

}
