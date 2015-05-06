/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.mojo;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.jpa.algo.Rf2FileSorter;
import org.ihtsdo.otf.ts.jpa.algo.Rf2Readers;
import org.ihtsdo.otf.ts.jpa.algo.Rf2SnapshotSamplerAlgorithm;

/**
 * Goal which samples an RF2 Snapshot of SNOMED CT data and outputs RF2.
 * 
 * See admin/loader/pom.xml for sample usage
 * 
 * @goal sample-rf2-snapshot
 * 
 * @phase package
 */
public class TerminologyRf2SnapshotSampler extends AbstractMojo {

  /**
   * Input directory.
   * @parameter
   * @required
   */
  private String inputDir;
  
  /**
   * Whether to run this mojo against an active server
   * @parameter
   */
  private boolean server = false;


  /**
   * Instantiates a {@link TerminologyRf2SnapshotSampler} from the specified
   * parameters.
   * 
   */
  public TerminologyRf2SnapshotSampler() {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.maven.plugin.Mojo#execute()
   */
  @Override
  public void execute() throws MojoFailureException {
   
    try {
      getLog().info("RF2 Snapshot Terminology Sampler called via mojo.");
      getLog().info("  Input directory    : " + inputDir);
      getLog().info("  Expect server up   : " + server);
      
      Properties properties = ConfigUtility.getConfigProperties();

      boolean serverRunning = ConfigUtility.isServerActive();
      
      getLog().info("Server status detected:  " + (!serverRunning ? "DOWN" : "UP"));

      if (serverRunning && !server) {
        throw new MojoFailureException("Mojo expects server to be down, but server is running");
      }
      
      if (!serverRunning && server) {
        throw new MojoFailureException("Mojo expects server to be running, but server is down");
      }
      
      // Check the input directory
      File inputDirFile = new File(inputDir);
      if (!inputDirFile.exists()) {
        throw new Exception("Specified input directory does not exist");
      }

      // Sort and open RF2 files
      Logger.getLogger(getClass()).info("  Sort RF2 Files");
      Rf2FileSorter sorter = new Rf2FileSorter();
      sorter = new Rf2FileSorter();
      sorter.setSortByEffectiveTime(true);
      sorter.setRequireAllFiles(true);
      File outputDir = new File(inputDirFile, "/RF2-sorted-temp/");
      sorter.sortFiles(inputDirFile, outputDir);

      // Open readers
      Rf2Readers readers = new Rf2Readers(outputDir);
      readers.openReaders();

      // Load initial snapshot - first release version
      Rf2SnapshotSamplerAlgorithm algorithm = new Rf2SnapshotSamplerAlgorithm();
      algorithm.setReaders(readers);
      algorithm.setOutputDir(outputDir);
      algorithm.compute();

      

    } catch (Exception e) {
      e.printStackTrace();
      throw new MojoFailureException("Unexpected exception:", e);
    }
  }
}
