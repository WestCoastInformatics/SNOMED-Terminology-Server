package org.ihtsdo.otf.ts.jpa.services.helper;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.ihtsdo.otf.ts.helpers.ConfigUtility;



// TODO: Auto-generated Javadoc
/**
 * The Class JdbcConnectionHandler.
 *
 * @author ${author}
 */
public class TomcatServerUtility {
  
  public static Properties config = null;

  
  public static boolean isActive() throws Exception {
    if (config == null)
      config = ConfigUtility.getConfigProperties();
    
    try {
      new URL(config.getProperty("base.url") + "/index.html").openConnection().connect();
      return true;
    } catch (IOException e) {
      return false;
    }
  }
}
