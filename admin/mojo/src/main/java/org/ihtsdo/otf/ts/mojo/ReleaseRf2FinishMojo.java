/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.mojo;

import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.jpa.algo.ReleaseRf2FinishAlgorithm;
import org.ihtsdo.otf.ts.jpa.client.HistoryClientRest;
import org.ihtsdo.otf.ts.jpa.services.SecurityServiceJpa;
import org.ihtsdo.otf.ts.rest.impl.HistoryServiceRestImpl;
import org.ihtsdo.otf.ts.services.SecurityService;

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
   * The release version
   * @parameter
   * @required
   */
  private String releaseVersion = null;

  /**
   * The terminology
   * @parameter
   * @required
   */
  private String terminology = null;

  /**
   * Whether to run this mojo against an active server
   * @parameter
   */
  private boolean server = false;

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.maven.plugin.Mojo#execute()
   */
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      // log start
      getLog().info("Finishing Release");
      getLog().info("  releaseVersion = " + releaseVersion);
      getLog().info("  terminology = " + terminology);
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
        historyService.finishRf2Release(releaseVersion, terminology, authToken);

      } else {
        getLog().info("Running against server");

        HistoryServiceRestImpl historyService = new HistoryServiceRestImpl();
        historyService.finishRf2Release(releaseVersion, terminology, authToken);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new MojoExecutionException("Performing release finish failed.", e);
    }

  }
}
