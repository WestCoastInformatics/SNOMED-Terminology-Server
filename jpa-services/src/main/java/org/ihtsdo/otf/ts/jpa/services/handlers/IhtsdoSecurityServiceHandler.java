package org.ihtsdo.otf.ts.jpa.services.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status.Family;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.User;
import org.ihtsdo.otf.ts.helpers.LocalException;
import org.ihtsdo.otf.ts.services.handlers.SecurityServiceHandler;
import org.ihtsdo.otf.ts.services.helpers.UserImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;

/**
 * Implements a security handler that authorizes via IHTSDO authentication.
 */
public class IhtsdoSecurityServiceHandler implements SecurityServiceHandler {

  /** The properties. */
  private Properties properties;

  /**
   * Instantiates an empty {@link IhtsdoSecurityServiceHandler}.
   */
  public IhtsdoSecurityServiceHandler() {
    // do nothing
  }

  @Override
  @SuppressWarnings("unchecked")
  public User authenticate(String username, String password) throws Exception {
    Logger.getLogger(this.getClass()).info(
        "Authenticating " + username + "/*********");

    Form form = new Form();
    form.add("username", username);
    form.add("password", password);
    form.add("queryName", "getUserByNameAuth");

    Client client = Client.create();
    WebResource resource = client.resource(properties.getProperty("url"));
    resource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
    ClientResponse response = resource.post(ClientResponse.class, form);
    String resultString = "";
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      resultString = response.getEntity(String.class);
    } else {
      resultString = response.getEntity(String.class);
      throw new LocalException("Incorrect user name or password.");
    }

    /*
     * Synchronize the information sent back from ITHSDO with the User object.
     * Add a new user if there isn't one matching the username If there is, load
     * and update that user and save the changes
     */
    String authUserName = "";
    String authEmail = "";
    String authGivenName = "";
    String authSurname = "";

    // converting json to
    byte[] data = resultString.getBytes();
    Map<String, Map<String, String>> json = new HashMap<>();

    // parse username from json object
    ObjectMapper objectMapper = new ObjectMapper();
    json = objectMapper.readValue(data, HashMap.class);
    for (Entry<String, Map<String, String>> entrySet : json.entrySet()) {
      if (entrySet.getKey().equals("user")) {
        Map<String, String> inner = entrySet.getValue();
        for (Entry<String, String> innerEntrySet : inner.entrySet()) {
          if (innerEntrySet.getKey().equals("name")) {
            authUserName = innerEntrySet.getValue();
          } else if (innerEntrySet.getKey().equals("email")) {
            authEmail = innerEntrySet.getValue();
          } else if (innerEntrySet.getKey().equals("givenName")) {
            authGivenName = innerEntrySet.getValue();
          } else if (innerEntrySet.getKey().equals("surname")) {
            authSurname = innerEntrySet.getValue();
          }
        }
      }
    }

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
