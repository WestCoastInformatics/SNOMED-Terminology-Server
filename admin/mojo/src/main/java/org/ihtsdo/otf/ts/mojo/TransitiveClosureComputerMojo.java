package org.ihtsdo.otf.ts.mojo;

import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.jpa.client.ContentClientRest;
import org.ihtsdo.otf.ts.jpa.services.SecurityServiceJpa;
import org.ihtsdo.otf.ts.rest.impl.ContentServiceRestImpl;
import org.ihtsdo.otf.ts.services.SecurityService;

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
   * Whether to run this mojo against an active server
   * @parameter
   */
  private boolean server = true;

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
      
      Properties properties = ConfigUtility.getConfigProperties();


      boolean serverRunning = ConfigUtility.isServerActive();

      getLog().info(
          "Server status detected:  "
              + (!serverRunning ? "DOWN" : "UP"));

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
        contentService.computeTransitiveClosure(terminology, authToken);
      } else {
        getLog().info("Running against server");

        // invoke the client
        ContentClientRest client = new ContentClientRest(properties);
        client.computeTransitiveClosure(terminology, authToken);
      }

      // Clean-up
    } catch (Exception e) {
      e.printStackTrace();
      throw new MojoFailureException("Unexpected exception:", e);
    }
  }

}
