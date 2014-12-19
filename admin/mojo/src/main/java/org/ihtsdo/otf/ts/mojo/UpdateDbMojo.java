package org.ihtsdo.otf.ts.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.ihtsdo.otf.ts.jpa.services.RootServiceJpa;

/**
 * Goal which updates the db to sync it with the model via JPA.
 * 
 * See admin/updatedb/pom.xml for sample usage
 * 
 * @goal updatedb
 * 
 * @phase package
 */
public class UpdateDbMojo extends AbstractMojo {

  /**
   * Instantiates a {@link UpdateDbMojo} from the specified parameters.
   * 
   */
  public UpdateDbMojo() {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.maven.plugin.Mojo#execute()
   */
  @Override
  public void execute() throws MojoFailureException {
    getLog().info("Start updating database schema...");
    try {
      // Trigger a JPA event
      new RootServiceJpa().close();
      getLog().info("done ...");
    } catch (Exception e) {
      e.printStackTrace();
      throw new MojoFailureException("Unexpected exception:", e);
    }

  }

}
