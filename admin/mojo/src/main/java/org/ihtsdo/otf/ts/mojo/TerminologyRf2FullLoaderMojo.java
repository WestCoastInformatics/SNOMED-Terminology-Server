package org.ihtsdo.otf.ts.mojo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.ihtsdo.otf.ts.jpa.algo.Rf2FileSorter;

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
