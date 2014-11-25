package org.ihtsdo.otf.ts.services.handlers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.UUID;

import org.ihtsdo.otf.ts.helpers.User;
import org.ihtsdo.otf.ts.services.helpers.UserImpl;

/**
 * Implements a security handler that authorizes via IHTSDO authentication.
 */
public class UtsSecurityServiceHandler implements SecurityServiceHandler {

  /** The properties. */
  private Properties properties;

  /**
   * Instantiates an empty {@link UtsSecurityServiceHandler}.
   */
  public UtsSecurityServiceHandler() {
    // do nothing
  }

  @Override
  public User authenticate(String username, String password) throws Exception {

    final String licenseCode = properties.getProperty("license.code");
    if (licenseCode == null) {
      throw new Exception("License code must be specified.");
    }
    String data =
        URLEncoder.encode("licenseCode", "UTF-8") + "="
            + URLEncoder.encode(licenseCode, "UTF-8");
    data +=
        "&" + URLEncoder.encode("user", "UTF-8") + "="
            + URLEncoder.encode(username, "UTF-8");
    data +=
        "&" + URLEncoder.encode("password", "UTF-8") + "="
            + URLEncoder.encode(password, "UTF-8");

    final String urlProp = properties.getProperty("url");
    if (urlProp == null) {
      throw new Exception("URL must be specified.");
    }

    URL url = new URL(urlProp);
    URLConnection conn = url.openConnection();
    conn.setDoOutput(true);
    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
    wr.write(data);
    wr.flush();

    BufferedReader rd =
        new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String line;
    while ((line = rd.readLine()) != null) {
      // see if we can extract user info from this
      System.out.println(line);
    }
    wr.close();
    rd.close();

    /*
     * Synchronize the information sent back from ITHSDO with the User object.
     * Add a new user if there isn't one matching the username If there is, load
     * and update that user and save the changes
     */
    String authUserName = "";
    String authEmail = "";
    String authGivenName = "";
    String authSurname = "";

    User returnUser = new UserImpl();
    returnUser.setName(authGivenName + " " + authSurname);
    returnUser.setEmail(authEmail);
    returnUser.setUserName(authUserName);
    return returnUser;

  }

  /**
   * Always timeout user.
   * @see org.ihtsdo.otf.ts.services.handlers.SecurityServiceHandler#timeoutUser(java.lang.String)
   */
  @Override
  public boolean timeoutUser(String user) {
    return true;
  }

  /**
   * Compute token as a random UUID.
   * @see org.ihtsdo.otf.ts.services.handlers.SecurityServiceHandler#computeTokenForUser(java.lang.String)
   */
  @Override
  public String computeTokenForUser(String user) {
    String token = UUID.randomUUID().toString();
    return token;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.helpers.Configurable#setProperties(java.util.Properties)
   */
  @Override
  public void setProperties(Properties properties) {
    this.properties = properties;
  }

}
