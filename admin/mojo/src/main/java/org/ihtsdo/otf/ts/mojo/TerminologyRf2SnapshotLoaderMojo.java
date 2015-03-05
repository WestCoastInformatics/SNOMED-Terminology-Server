package org.ihtsdo.otf.ts.mojo;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.ihtsdo.otf.ts.ReleaseInfo;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.jpa.ReleaseInfoJpa;
import org.ihtsdo.otf.ts.jpa.algo.Rf2FileSorter;
import org.ihtsdo.otf.ts.jpa.algo.Rf2Readers;
import org.ihtsdo.otf.ts.jpa.algo.Rf2SnapshotLoaderAlgorithm;
import org.ihtsdo.otf.ts.jpa.algo.TransitiveClosureAlgorithm;
import org.ihtsdo.otf.ts.jpa.services.HistoryServiceJpa;
import org.ihtsdo.otf.ts.services.HistoryService;

/**
 * Goal which loads an RF2 Snapshot of SNOMED CT data into a database.
 * 
 * See admin/loader/pom.xml for sample usage
 * 
 * @goal load-rf2-snapshot
 * 
 * @phase package
 */
public class TerminologyRf2SnapshotLoaderMojo extends AbstractMojo {

  /**
   * Name of terminology to be loaded.
   * @parameter
   * @required
   */
  private String terminology;

  /**
   * The terminology version.
   * @parameter
   * @required
   */
  private String terminologyVersion;

  /**
   * Input directory.
   * @parameter
   * @required
   */
  private String inputDir;

  /**
   * Instantiates a {@link TerminologyRf2SnapshotLoaderMojo} from the specified
   * parameters.
   * 
   */
  public TerminologyRf2SnapshotLoaderMojo() {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.maven.plugin.Mojo#execute()
   */
  @Override
  public void execute() throws MojoFailureException {
    getLog().info("Starting load of RF2 snapshot");
    getLog().info("  terminology = " + terminology);
    getLog().info("  inputDir = " + inputDir);
    try {

      // Check the input directory
      File inputDirFile = new File(inputDir);
      if (!inputDirFile.exists()) {
        throw new MojoFailureException(
            "Specified input directory does not exist");
      }

      // Sort files
      getLog().info("  Sort RF2 Files");
      Rf2FileSorter sorter = new Rf2FileSorter();
      sorter.setSortByEffectiveTime(false);
      sorter.setRequireAllFiles(true);
      File outputDir = new File(inputDirFile, "/RF2-sorted-temp/");
      sorter.sortFiles(inputDirFile, outputDir);
      String releaseVersion = sorter.getFileVersion();
      getLog().info("  releaseVersion = " + releaseVersion);

      // Open readers
      Rf2Readers readers = new Rf2Readers(outputDir);
      readers.openReaders();


      // Load snapshot
      Rf2SnapshotLoaderAlgorithm algorithm = new Rf2SnapshotLoaderAlgorithm();
      algorithm.setTerminology(terminology);
      algorithm.setTerminologyVersion(terminologyVersion);
      algorithm.setReleaseVersion(releaseVersion);
      algorithm.setReaders(readers);
      algorithm.compute();

      //
      // Create ReleaseInfo for this release if it does not already exist
      //
      HistoryService historyService = new HistoryServiceJpa();
      historyService.setLastModifiedFlag(false);
      ReleaseInfo info =
          historyService.getReleaseInfo(terminology, releaseVersion);
      if (info == null) {
        info = new ReleaseInfoJpa();
        info.setName(releaseVersion);
        info.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(releaseVersion));
        info.setDescription(terminology + " " + releaseVersion + " release");
        info.setPlanned(false);
        info.setPublished(true);
        info.setReleaseBeginDate(info.getEffectiveTime());
        info.setReleaseFinishDate(info.getEffectiveTime());
        info.setTerminology(terminology);
        info.setTerminologyVersion(terminologyVersion);
        historyService.addReleaseInfo(info);
      }
      historyService.close();

      // Compute transitive closure
      Logger.getLogger(this.getClass()).info(
          "  Compute transitive closure from  " + terminology
              + "/" + terminologyVersion);
      TransitiveClosureAlgorithm algo = new TransitiveClosureAlgorithm();
      algo.setTerminology(terminology);
      algo.setTerminologyVersion(terminologyVersion);
      algo.reset();
      algo.compute();

      // Clean-up
      readers.closeReaders();
      ConfigUtility
          .deleteDirectory(new File(inputDirFile, "/RF2-sorted-temp/"));

    } catch (Exception e) {
      e.printStackTrace();
      throw new MojoFailureException("Unexpected exception:", e);
    }
  }
}
