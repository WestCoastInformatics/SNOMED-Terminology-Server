/*
 * 
 */
package org.ihtsdo.otf.ts.mojo;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.ihtsdo.otf.ts.helpers.ReleaseInfo;
import org.ihtsdo.otf.ts.jpa.algo.Rf2DeltaLoaderAlgorithm;
import org.ihtsdo.otf.ts.jpa.algo.Rf2FileSorter;
import org.ihtsdo.otf.ts.jpa.algo.Rf2Readers;
import org.ihtsdo.otf.ts.jpa.algo.TransitiveClosureAlgorithm;
import org.ihtsdo.otf.ts.jpa.services.HistoryServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.MetadataServiceJpa;
import org.ihtsdo.otf.ts.services.HistoryService;
import org.ihtsdo.otf.ts.services.MetadataService;

/**
 * Goal which loads an RF2 Delta of SNOMED CT data
 * 
 * See admin/loader/pom.xml for sample usage
 * 
 * @goal load-rf2-delta
 * 
 * @phase package
 */
public class TerminologyRf2DeltaLoader extends AbstractMojo {

  /**
   * Name of terminology to be loaded.
   * 
   * @parameter
   * @required
   */
  private String terminology;

  /**
   * The input directory
   * 
   * @parameter
   * @required
   */
  private String inputDir;

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.maven.plugin.Mojo#execute()
   */
  @Override
  public void execute() throws MojoFailureException {

    try {
      getLog().info("Starting RF2 delta loader");
      getLog().info("  terminology = " + terminology);
      getLog().info("  inputDir = " + inputDir);

      // Check the input directory
      File inputDirFile = new File(inputDir);
      if (!inputDirFile.exists()) {
        throw new MojoFailureException(
            "Specified input directory does not exist");
      }

      // Previous computation of terminology version is based on file name
      // but for delta/daily build files, this is not the current version
      // look up the current version instead
      MetadataService metadataService = new MetadataServiceJpa();
      final String terminologyVersion =
          metadataService.getLatestVersion(terminology);
      metadataService.close();
      if (terminologyVersion == null) {
        throw new Exception("Unable to determine terminology version.");
      }

      //
      // Verify that there is a release info for this version that is
      // marked as "isPlanned"
      //
      HistoryService historyService = new HistoryServiceJpa();
      ReleaseInfo releaseInfo =
          historyService.getReleaseInfo(terminology, terminologyVersion);
      if (releaseInfo == null) {
        throw new Exception("A release info must exist for "
            + terminologyVersion);
      } else if (!releaseInfo.isPlanned()) {
        throw new Exception("Release info for " + terminologyVersion
            + " is not marked as planned'");
      } else if (releaseInfo.isPublished()) {
        throw new Exception("Release info for " + terminologyVersion
            + " is marked as published");
      }
      historyService.close();

      // Sort files
      getLog().info("  Sort RF2 Files");
      Rf2FileSorter sorter = new Rf2FileSorter();
      sorter.setSortByEffectiveTime(false);
      sorter.setRequireAllFiles(false);
      File outputDir = new File(inputDirFile, "/RF2-sorted-temp/");
      sorter.sortFiles(inputDirFile, outputDir);

      // Open readers
      Rf2Readers readers = new Rf2Readers(outputDir);
      readers.openReaders();

      // Load delta
      Rf2DeltaLoaderAlgorithm algorithm = new Rf2DeltaLoaderAlgorithm();
      algorithm.setTerminology(terminology);
      algorithm.setTerminologyVersion(terminologyVersion);
      algorithm.setReleaseVersion(sorter.getFileVersion());
      algorithm.setReaders(readers);
      algorithm.compute();

      // Compute transitive closure
      Logger.getLogger(this.getClass()).info(
          "  Compute transitive closure from  " + terminology
              + "/" + terminologyVersion);
      TransitiveClosureAlgorithm algo = new TransitiveClosureAlgorithm();
      algo.setTerminology(terminology);
      algo.setTerminologyVersion(terminologyVersion);
      algo.reset();
      algo.compute();

      // No changes to release info
      
      // Clean-up
      readers.closeReaders();
      getLog().info("...done");
    } catch (Exception e) {
      e.printStackTrace();
      throw new MojoFailureException("Unexpected exception:", e);
    }

  }

}
