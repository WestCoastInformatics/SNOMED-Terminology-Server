package org.ihtsdo.otf.ts.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Goal which recomputes transitive closure for the latest version of a
 * specified terminology.
 * 
 * See admin/loader/pom.xml for sample usage
 * 
 * @goal compute-transitive-closure
 * 
 * @phase package
 */
public class TransitiveClosureComputerMojo extends AbstractMojo {

  /**
   * Name of terminology to be loaded.
   * @parameter
   * @required
   */
  private String terminology;

  /**
   * Instantiates a {@link TransitiveClosureComputerMojo} from the specified
   * parameters.
   * 
   */
  public TransitiveClosureComputerMojo() {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.maven.plugin.Mojo#execute()
   */
  @Override
  public void execute() throws MojoFailureException {
    getLog().info("Starting computation of transitive closure");
    getLog().info("  terminology = " + terminology);

    try {


      // Clean-up
    } catch (Exception e) {
      e.printStackTrace();
      throw new MojoFailureException("Unexpected exception:", e);
    }
  }

  
}
