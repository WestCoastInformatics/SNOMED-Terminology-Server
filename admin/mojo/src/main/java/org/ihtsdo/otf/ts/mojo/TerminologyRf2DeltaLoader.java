/*
 * 
 */
package org.ihtsdo.otf.ts.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;

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


  }

}
