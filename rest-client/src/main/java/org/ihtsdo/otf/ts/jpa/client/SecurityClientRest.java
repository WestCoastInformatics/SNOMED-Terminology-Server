package org.ihtsdo.otf.ts.jpa.client;

import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status.Family;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.LocalException;
import org.ihtsdo.otf.ts.helpers.User;
import org.ihtsdo.otf.ts.helpers.UserJpa;
import org.ihtsdo.otf.ts.helpers.UserList;
import org.ihtsdo.otf.ts.helpers.UserListJpa;
import org.ihtsdo.otf.ts.rest.SecurityServiceRest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * A client for connecting to a security REST service.
 */
public class SecurityClientRest implements SecurityServiceRest {

  /** The config. */
  private Properties config = null;

  /**
   * Instantiates a {@link ContentClientRest} from the specified parameters.
   *
   * @param config the config
   */
  public SecurityClientRest(Properties config) {
    this.config = config;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.rest.SecurityServiceRest#authenticate(java.lang.
   * String, java.lang.String)
   */
  @Override
  public String authenticate(String username, String password) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/security/authenticate/" + username);
    resource.accept(MediaType.APPLICATION_JSON);
    ClientResponse response = resource.post(ClientResponse.class, password);
    String resultString = response.getEntity(String.class);
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).info(resultString);
    } else {
      throw new LocalException(resultString);
    }
    // return auth token
    return resultString.replaceAll("\"", "");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.SecurityServiceRest#logout(java.lang.String)
   */
  @Override
  public boolean logout(String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/security/logout/"
            + authToken);
    resource.accept(MediaType.APPLICATION_JSON);
    ClientResponse response = resource.get(ClientResponse.class);
    String resultString = response.getEntity(String.class);
    Logger.getLogger(this.getClass()).info("status: " + response.getStatus());
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).info(resultString);
    } else {
      throw new Exception(resultString);
    }
    return resultString.toLowerCase().equals("true");

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.SecurityServiceRest#getUser(java.lang.Long,
   * java.lang.String)
   */
  @Override
  public User getUser(Long id, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/security/user/id/"
            + id);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);
    
    if (response.getStatus() == 204)
      return null;

    String resultString = response.getEntity(String.class);
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    UserJpa user =
        (UserJpa) ConfigUtility.getGraphForString(resultString, UserJpa.class);
    return user;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.SecurityServiceRest#getUser(java.lang.String,
   * java.lang.String)
   */
  @Override
  public User getUser(String username, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/security/user/name/"
            + username);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    if (response.getStatus() == 204)
      return null;
    
    String resultString = response.getEntity(String.class);
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    UserJpa user =
        (UserJpa) ConfigUtility.getGraphForString(resultString, UserJpa.class);
    return user;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.SecurityServiceRest#getUsers(java.lang.String)
   */
  @Override
  public UserList getUsers(String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client
            .resource(config.getProperty("base.url") + "/security/user/users");
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).get(ClientResponse.class);

    String resultString = response.getEntity(String.class);
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    UserListJpa list =
        (UserListJpa) ConfigUtility.getGraphForString(resultString,
            UserListJpa.class);
    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.SecurityServiceRest#addUser(org.ihtsdo.otf.ts.helpers
   * .UserJpa, java.lang.String)
   */
  @Override
  public User addUser(UserJpa user, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/security/user/add");

    String userString =
        (user != null ? ConfigUtility.getStringForGraph(user) : null);
    Logger.getLogger(this.getClass()).info(userString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .put(ClientResponse.class, userString);

    String resultString = response.getEntity(String.class);
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    UserJpa result =
        (UserJpa) ConfigUtility.getGraphForString(resultString, UserJpa.class);

    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.rest.SecurityServiceRest#removeUser(java.lang.Long,
   * java.lang.String)
   */
  @Override
  public void removeUser(Long id, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/security/user/remove/" + id);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken).delete(ClientResponse.class);

    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      // do nothing
    } else {
      throw new Exception("Unexpected status " + response.getStatus());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.SecurityServiceRest#updateUser(org.ihtsdo.otf.ts
   * .helpers.UserJpa, java.lang.String)
   */
  @Override
  public void updateUser(UserJpa user, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url")
            + "/security/user/update");

    String userString =
        (user != null ? ConfigUtility.getStringForGraph(user) : null);
    Logger.getLogger(this.getClass()).info(userString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, userString);

    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      // do nothing
    } else {
      throw new Exception("Unexpected status " + response.getStatus());
    }
  }

}
