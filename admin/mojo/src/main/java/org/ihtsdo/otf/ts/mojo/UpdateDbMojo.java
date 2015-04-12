/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.mojo;

import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
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
   * Mode: create or update
   * @parameter
   * @required
   */ 
  public String mode;
  
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
    getLog().info("  mode = " + mode);
    try {
      if (!mode.equals("update") && !mode.equals("create")) {
        throw new Exception("Mode has illegal value: " + mode);
      }
      Properties config = ConfigUtility.getConfigProperties();
      config.setProperty("hibernate.hbm2ddl.auto", mode);
      
      // Trigger a JPA event
      new RootServiceJpa().close();
      getLog().info("done ...");
    } catch (Exception e) {
      e.printStackTrace();
      throw new MojoFailureException("Unexpected exception:", e);
    }

  }

}
