package org.ihtsdo.otf.ts.mojo;

import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.jpa.client.HistoryClientRest;
import org.ihtsdo.otf.ts.jpa.services.SecurityServiceJpa;
import org.ihtsdo.otf.ts.rest.impl.HistoryServiceRestImpl;
import org.ihtsdo.otf.ts.services.SecurityService;

/**
 * Loads unpublished complex maps.
 * 
 * See admin/release/pom.xml for sample usage.
 * 
 * @goal release-rf2
 * @phase package
 */
public class ReleaseRf2ProcessingMojo extends AbstractMojo {

  /**
   * The effective time of release.
   *
   * @parameter effectiveTime
   */
  private String releaseVersion = null;

  /**
   * The terminology.
   * @parameter moduleId
   * @required
   */
  private String terminology = null;

  /**
   * The output directory.
   * @parameter outputDir
   * @required
   */
  private String outputDir = null;

  /**
   * The module id.
   * 
   * @parameter moduleId
   * @required
   */
  private String moduleId = null;

  /**
   * Whether to run this mojo against an active server
   * @parameter
   */
  private boolean server = false;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      getLog().info("Processing release");
      getLog().info("  terminology = " + terminology);
      getLog().info("  outputDir = " + outputDir);
      getLog().info("  releaseVersion = " + releaseVersion);
      getLog().info("  moduleId = " + moduleId);
      getLog().info("  Expect server up: " + server);

      Properties properties = ConfigUtility.getConfigProperties();
      boolean serverRunning = ConfigUtility.isServerActive();
      getLog().info(
          "Server status detected:  " + (!serverRunning ? "DOWN" : "UP"));
      if (serverRunning && !server) {
        throw new MojoFailureException(
            "Mojo expects server to be down, but server is running");
      }

      if (!serverRunning && server) {
        throw new MojoFailureException(
            "Mojo expects server to be running, but server is down");
      }

      // authenticate
      SecurityService service = new SecurityServiceJpa();
      String authToken =
          service.authenticate(properties.getProperty("admin.user"),
              properties.getProperty("admin.password"));
      service.close();

      if (!serverRunning) {
        getLog().info("Running directly");

        HistoryClientRest historyService = new HistoryClientRest(properties);
        historyService.processRf2Release(releaseVersion, terminology,
            outputDir, moduleId, authToken);

      } else {
        getLog().info("Running against server");

        HistoryServiceRestImpl historyService = new HistoryServiceRestImpl();
        historyService.processRf2Release(releaseVersion, terminology,
            outputDir, moduleId, authToken);
      }
    } catch (Exception e) {
      throw new MojoFailureException("Unexpected Failure", e);
    }

  }
}
