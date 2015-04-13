/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.mojo;

import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.jpa.client.ContentClientRest;
import org.ihtsdo.otf.ts.jpa.services.SecurityServiceJpa;
import org.ihtsdo.otf.ts.rest.impl.ContentServiceRestImpl;
import org.ihtsdo.otf.ts.services.SecurityService;

/**
 * Converts claml data to RF2 objects.
 * 
 * See admin/loader/pom.xml for sample usage
 * 
 * @goal load-claml
 * @phase package
 */
public class TerminologyClamlLoaderMojo extends AbstractMojo {

  // NOTE: default visibility is used instead of private
  // so that the inner class parser does not require
  // the use of synthetic accessors

  /**
   * Name of terminology to be loaded.
   * @parameter
   * @required
   */
  String terminology;

  /**
   * Name of version to be loaded.
   * @parameter
   * @required
   */
  String version;

  /**
   * Input file.
   * @parameter
   * @required
   */
  String inputFile;

  /**
   * Whether to run this mojo against an active server
   * @parameter
   */
  private boolean server = false;

  /**
   * Executes the plugin.
   * @throws MojoExecutionException the mojo execution exception
   */
  @Override
  public void execute() throws MojoExecutionException {
    try {
      getLog().info("Starting load of ClaML");
      getLog().info("  terminology = " + terminology);
      getLog().info("  version = " + version);
      getLog().info("  inputFile = " + inputFile);
      getLog().info("  Expect server up   : " + server);

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

        ContentServiceRestImpl contentService = new ContentServiceRestImpl();
        contentService.loadTerminologyClaml(terminology, version, inputFile,
            authToken);

      } else {
        getLog().info("Running against server");

        // invoke the client
        ContentClientRest contentService = new ContentClientRest(properties);
        contentService.loadTerminologyClaml(terminology, version, inputFile,
            authToken);
      }

      getLog().info("done ...");

    } catch (Exception e) {
      e.printStackTrace();
      throw new MojoExecutionException(
          "Conversion of Claml to RF2 objects failed", e);
    }

  }

}