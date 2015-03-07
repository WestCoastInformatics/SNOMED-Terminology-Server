package org.ihtsdo.otf.ts.mojo;

import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.jpa.client.ContentClientRest;
import org.ihtsdo.otf.ts.jpa.services.SecurityServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.helper.TomcatServerUtility;
import org.ihtsdo.otf.ts.rest.impl.ContentServiceRestImpl;
import org.ihtsdo.otf.ts.services.SecurityService;

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
   * Whether to run this mojo against an active server
   * @parameter
   */
  private boolean server = true;

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

    try {
      getLog().info("RF2 Full Terminology Loader called via mojo.");
      getLog().info("  Terminology        : " + terminology);
      getLog().info("  Terminology Version: " + terminologyVersion);
      getLog().info("  Input directory    : " + inputDir);
      getLog().info("  Expect server up   : " + server);

      Properties properties = ConfigUtility.getConfigProperties();

      boolean serverRunning = TomcatServerUtility.isActive();

      getLog().info(
          "Server status detected:  "
              + (serverRunning == false ? "DOWN" : "UP"));

      if (serverRunning == true && server == false) {
        throw new MojoFailureException(
            "Mojo expects server to be down, but server is running");
      }

      if (serverRunning == false && server == true) {
        throw new MojoFailureException(
            "Mojo expects server to be running, but server is down");
      }

      // authenticate
      SecurityService service = new SecurityServiceJpa();
      String authToken =
          service.authenticate(properties.getProperty("admin.user"),
              properties.getProperty("admin.password"));
      service.close();

      if (serverRunning == false) {
        getLog().info("Running directly");

        ContentServiceRestImpl contentService = new ContentServiceRestImpl();
        contentService.loadTerminologyRf2Full(terminology, terminologyVersion,
            inputDir, authToken);

      } else {
        getLog().info("Running against server");

        // invoke the client
        ContentClientRest client = new ContentClientRest(properties);
        client.loadTerminologyRf2Full(terminology, terminologyVersion,
            inputDir, authToken);
      }

    } catch (Exception e) {
      e.printStackTrace();
      throw new MojoFailureException("Unexpected exception:", e);
    }
  }

}
