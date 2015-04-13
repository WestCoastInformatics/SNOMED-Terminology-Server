/*
 * Copyright 2015 West Coast Informatics, LLC
 */
/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
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
 * Goal which makes lucene indexes based on hibernate-search annotations.
 * 
 * See admin/lucene/pom.xml for sample usage
 *
 * @goal reindex
 * 
 * @phase package
 */
public class LuceneReindexMojo extends AbstractMojo {

  /**
   * The specified objects to index
   * @parameter
   */
  private String indexedObjects;

  /**
   * Whether to run this mojo against an active server
   * @parameter
   */
  private boolean server = false;

  /**
   * Instantiates a {@link LuceneReindexMojo} from the specified parameters.
   */
  public LuceneReindexMojo() {
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
      getLog().info("Lucene reindexing called via mojo.");
      getLog().info("  Indexed objects : " + indexedObjects);
      getLog().info("  Expect server up: " + server);
      Properties properties = ConfigUtility.getConfigProperties();

      boolean serverRunning = ConfigUtility.isServerActive();
      
      getLog().info("Server status detected:  " + (!serverRunning ? "DOWN" : "UP"));

      if (serverRunning && !server) {
        throw new MojoFailureException("Mojo expects server to be down, but server is running");
      }
      
      if (!serverRunning&& server) {
        throw new MojoFailureException("Mojo expects server to be running, but server is down");
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
        contentService.luceneReindex(indexedObjects, authToken);

      } else {
        getLog().info("Running against server");

        // invoke the client
        ContentClientRest client = new ContentClientRest(properties);
        client.luceneReindex(indexedObjects, authToken);
      }

    } catch (Exception e) {
      e.printStackTrace();
      throw new MojoFailureException("Unexpected exception:", e);
    }

  }

}
