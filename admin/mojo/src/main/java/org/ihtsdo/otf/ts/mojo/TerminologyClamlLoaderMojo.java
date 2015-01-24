package org.ihtsdo.otf.ts.mojo;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.ihtsdo.otf.ts.jpa.algo.ClamlLoaderAlgorithm;
import org.ihtsdo.otf.ts.jpa.algo.TransitiveClosureAlgorithm;

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
   * Name of terminologyVersion to be loaded.
   * @parameter
   * @required
   */
  String terminologyVersion;

  /**
   * Input file.
   * @parameter
   * @required
   */
  String inputFile;

  /**
   * Executes the plugin.
   * @throws MojoExecutionException the mojo execution exception
   */
  @Override
  public void execute() throws MojoExecutionException {
    getLog().info("Starting load of ClaML");
    getLog().info("  terminology = " + terminology);
    getLog().info("  terminologyVersion = " + terminologyVersion);
    getLog().info("  inputFile = " + inputFile);

    try {

      // Check the input directory
      if (!new File(inputFile).exists()) {
        throw new MojoFailureException(
            "Specified input file does not exist " + inputFile);
      }

      // Load snapshot
      ClamlLoaderAlgorithm algorithm = new ClamlLoaderAlgorithm();
      algorithm.setTerminology(terminology);
      algorithm.setTerminologyVersion(terminologyVersion);
      algorithm.setInputFile(inputFile);
      algorithm.compute();

      // Let service begin its own transaction
      getLog().info("Start computing transtive closure");
      TransitiveClosureAlgorithm algo = new TransitiveClosureAlgorithm();
      algo.setTerminology(terminology);
      algo.setTerminologyVersion(terminologyVersion);
      algo.reset();
      algo.compute();
      algo.close();

      getLog().info("done ...");

    } catch (Exception e) {
      e.printStackTrace();
      throw new MojoExecutionException(
          "Conversion of Claml to RF2 objects failed", e);
    } 

  }



}