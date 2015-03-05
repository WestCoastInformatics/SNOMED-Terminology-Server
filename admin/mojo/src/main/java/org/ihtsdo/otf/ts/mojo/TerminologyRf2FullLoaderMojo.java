package org.ihtsdo.otf.ts.mojo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.ihtsdo.otf.ts.ReleaseInfo;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.jpa.algo.Rf2DeltaLoaderAlgorithm;
import org.ihtsdo.otf.ts.jpa.algo.Rf2FileSorter;
import org.ihtsdo.otf.ts.jpa.algo.Rf2Readers;
import org.ihtsdo.otf.ts.jpa.algo.Rf2SnapshotLoaderAlgorithm;
import org.ihtsdo.otf.ts.jpa.algo.TransitiveClosureAlgorithm;
import org.ihtsdo.otf.ts.jpa.services.HistoryServiceJpa;
import org.ihtsdo.otf.ts.services.HistoryService;

/**
 * Goal which loads an RF2 Full of SNOMED CT data into a database.
 * 
 * See admin/loader/pom.xml for sample usage
 * 
 * @goal load-rf2-full
 * 
 * @phase package
 */
public class TerminologyRf2FullLoaderMojo extends AbstractMojo {

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
   * Instantiates a {@link TerminologyRf2FullLoaderMojo} from the specified
   * parameters.
   * 
   */
  public TerminologyRf2FullLoaderMojo() {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.maven.plugin.Mojo#execute()
   */
  @Override
  public void execute() throws MojoFailureException {
    getLog().info("Starting load of RF2 Full");
    getLog().info("  terminology = " + terminology);
    getLog().info("  inputDir = " + inputDir);
    try {

      // Check the input directory
      File inputDirFile = new File(inputDir);
      if (!inputDirFile.exists()) {
        throw new MojoFailureException(
            "Specified input directory does not exist");
      }

      // Get the release versions
      getLog().info("  Get release versions");
      List<String> releases = getReleaseVersions();
      Collections.sort(releases);

      // check that release info does not already exist
      HistoryService historyService = new HistoryServiceJpa();
      for (String release : releases) {
        ReleaseInfo releaseInfo =
            historyService.getReleaseInfo(terminology, release);
        if (releaseInfo != null) {
          throw new Exception("A release info already exists for " + release);
        }
      }

      // Sort files
      getLog().info("  Sort RF2 Files");
      Rf2FileSorter sorter = new Rf2FileSorter();
      sorter.setSortByEffectiveTime(true);
      sorter.setRequireAllFiles(true);
      File outputDir = new File(inputDirFile, "/RF2-sorted-temp/");
      sorter.sortFiles(inputDirFile, outputDir);

      // Open readers
      Rf2Readers readers = new Rf2Readers(outputDir);
      readers.openReaders();

      // Load initial snapshot - first release version
      Rf2SnapshotLoaderAlgorithm algorithm = new Rf2SnapshotLoaderAlgorithm();
      algorithm.setTerminology(terminology);
      algorithm.setTerminologyVersion(terminologyVersion);
      algorithm.setReleaseVersion(releases.get(0));
      algorithm.setReaders(readers);
      algorithm.compute();

      // Load deltas
      for (String release : releases) {
        if (release.equals(releases.get(0))) {
          continue;
        }

        Rf2DeltaLoaderAlgorithm algorithm2 = new Rf2DeltaLoaderAlgorithm();
        algorithm2.setTerminology(terminology);
        algorithm2.setTerminologyVersion(terminologyVersion);
        algorithm2.setReleaseVersion(release);
        algorithm2.setReaders(readers);
        algorithm2.compute();

      }
      
      // Compute transitive closure
      Logger.getLogger(this.getClass()).info(
          "  Compute transitive closure from  " + terminology
              + "/" + terminologyVersion);
      TransitiveClosureAlgorithm algo = new TransitiveClosureAlgorithm();
      algo.setTerminology(terminology);
      algo.setTerminologyVersion(terminologyVersion);
      algo.reset();
      algo.compute();

      //
      // Create ReleaseInfo for each release, unless already exists
      //
      for (String release : releases) {
        ReleaseInfo info = historyService.getReleaseInfo(terminology, release);
        if (info != null) {
          info.setName(release);
          info.setEffectiveTime(ConfigUtility.DATE_FORMAT.parse(release));
          info.setDescription(terminology + " " + release + " release");
          info.setPlanned(false);
          info.setPublished(true);
          info.setReleaseBeginDate(info.getEffectiveTime());
          info.setReleaseFinishDate(info.getEffectiveTime());
          info.setTerminology(terminology);
          info.setTerminologyVersion(terminologyVersion);
          historyService.addReleaseInfo(info);
        }
      }

      // Clean-up
      readers.closeReaders();
      ConfigUtility
          .deleteDirectory(new File(inputDirFile, "/RF2-sorted-temp/"));

    } catch (Exception e) {
      e.printStackTrace();
      throw new MojoFailureException("Unexpected exception:", e);
    }
  }

  /**
   * Returns the release versions covered by this full build.
   *
   * @return the release versions
   * @throws Exception the exception
   */
  public List<String> getReleaseVersions() throws Exception {
    Rf2FileSorter sorter = new Rf2FileSorter();
    File conceptsFile = sorter.findFile(new File(inputDir), "sct2_Concept");
    Set<String> releaseSet = new HashSet<>();
    BufferedReader reader = new BufferedReader(new FileReader(conceptsFile));
    String line;
    while ((line = reader.readLine()) != null) {
      final String fields[] = line.split("\t");
      releaseSet.add(fields[1]);
    }
    List<String> results = new ArrayList<>(releaseSet);
    Collections.sort(results);
    reader.close();
    return results;
  }

}
