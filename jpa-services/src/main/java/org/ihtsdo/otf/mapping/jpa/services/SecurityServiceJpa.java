package org.ihtsdo.otf.mapping.jpa.services;

import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status.Family;

import org.apache.log4j.Logger;
import org.apache.solr.common.util.Hash;
import org.ihtsdo.otf.mapping.helpers.LocalException;
import org.ihtsdo.otf.mapping.helpers.User;
import org.ihtsdo.otf.mapping.helpers.UserJpa;
import org.ihtsdo.otf.mapping.helpers.UserList;
import org.ihtsdo.otf.mapping.helpers.UserRole;
import org.ihtsdo.otf.mapping.services.SecurityService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;

/**
 * Reference implementation of the {@link SecurityService}.
 *
 * @author ${author}
 */
public class SecurityServiceJpa extends RootServiceJpa implements
    SecurityService {

  /** The token username . */
  private static Map<String, String> tokenUsername = new HashMap<>();

  /** The token login time . */
  private static Map<String, Date> tokenLogin = new HashMap<>();

  /** config properties */
  private Properties config = null;

  /**
   * Instantiates an empty {@link SecurityServiceJpa}.
   *
   * @throws Exception the exception
   */
  public SecurityServiceJpa() throws Exception {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.SecurityService#authenticate(java.lang.
   * String, java.lang.String)
   */
  @SuppressWarnings("unchecked")
  @Override
  public String authenticate(String username, String password) throws Exception {
    if (username == null)
      throw new LocalException("Invalid username: null");
    if (password == null)
      throw new LocalException("Invalid password: null");

    // read ihtsdo security url and active status from config file
    if (config == null) {
      String configFileName = System.getProperty("run.config");
      Logger.getLogger(this.getClass())
          .info("  run.config = " + configFileName);

      config = new Properties();
      FileReader in = new FileReader(new File(configFileName));
      config.load(in);
      in.close();
    }
    String ihtsdoSecurityUrl = config.getProperty("ihtsdo.security.url");
    boolean ihtsdoSecurityActivated =
        new Boolean(config.getProperty("ihtsdo.security.activated"));

    // if ihtsdo security is off, use username as token
    if (!ihtsdoSecurityActivated || username.equals("guest")) {
      tokenUsername.put(username, username);
      return getUser(username).getUserName();
    }

    // set up request to be posted to ihtsdo security service
    Form form = new Form();
    form.add("username", username);
    form.add("password", password);
    form.add("queryName", "getUserByNameAuth");

    Client client = Client.create();
    WebResource resource = client.resource(ihtsdoSecurityUrl);

    resource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE);

    ClientResponse response = resource.post(ClientResponse.class, form);

    String resultString = "";
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass())
          .info("Success! " + response.getStatus());
      resultString = response.getEntity(String.class);
      Logger.getLogger(this.getClass()).info(resultString);
    } else {
      // TODO Differentiate error messages with NO RESPONE and Authentication
      // Failed (Check text)
      Logger.getLogger(this.getClass()).info("ERROR! " + response.getStatus());
      resultString = response.getEntity(String.class);
      Logger.getLogger(this.getClass()).info(resultString);
      throw new LocalException("Incorrect user name or password.");
    }

    /*
     * Synchronize the information sent back from ITHSDO with the User object.
     * Add a new user if there isn't one matching the username If there is, load
     * and update that user and save the changes
     */
    String ihtsdoUserName = "";
    String ihtsdoEmail = "";
    String ihtsdoGivenName = "";
    String ihtsdoSurname = "";

    // converting json to
    byte[] Data = resultString.getBytes();
    Map<String, Map<String, String>> json = new HashMap<>();

    // parse username from json object
    ObjectMapper objectMapper = new ObjectMapper();
    json =
        (Map<String, Map<String, String>>) objectMapper.readValue(Data,
            Hash.class);
    for (Entry<String, Map<String, String>> entrySet : json.entrySet()) {
      if (entrySet.getKey().equals("user")) {
        Map<String, String> inner = entrySet.getValue();
        for (Entry<String, String> innerEntrySet : inner.entrySet()) {
          if (innerEntrySet.getKey().equals("name")) {
            ihtsdoUserName = innerEntrySet.getValue();
          } else if (innerEntrySet.getKey().equals("email")) {
            ihtsdoEmail = innerEntrySet.getValue();
          } else if (innerEntrySet.getKey().equals("givenName")) {
            ihtsdoGivenName = innerEntrySet.getValue();
          } else if (innerEntrySet.getKey().equals("surname")) {
            ihtsdoSurname = innerEntrySet.getValue();
          }
        }
      }
    }
    // check if ihtsdo user matches one of our Users
    UserList userList = getUsers();
    User userFound = null;
    for (User user : userList.getUsers()) {
      if (user.getUserName().equals(ihtsdoUserName)) {
        userFound = user;
        break;
      }
    }
    // if User was found, update to match ihtsdo settings
    if (userFound != null) {
      userFound.setEmail(ihtsdoEmail);
      userFound.setName(ihtsdoGivenName + " " + ihtsdoSurname);
      userFound.setUserName(ihtsdoUserName);
      updateUser(userFound);
      // if User not found, create one for our use
    } else {
      User newUser = new UserJpa();
      newUser.setName(ihtsdoGivenName + " " + ihtsdoSurname);
      newUser.setUserName(ihtsdoUserName);
      newUser.setEmail(ihtsdoEmail);
      newUser.setApplicationRole(UserRole.VIEWER);
      addUser(newUser);
    }

    // Generate application-managed token
    String token = UUID.randomUUID().toString();
    tokenUsername.put(token, ihtsdoUserName);
    tokenLogin.put(token, new Date());

    Logger.getLogger(this.getClass()).info("User = " + resultString);

    return token;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ping.services.SecurityService#getUsernameForToken(java
   * .lang.String)
   */
  @Override
  public String getUsernameForToken(String authToken) throws Exception {
    // use guest user for null auth token
    if (authToken == null)
      throw new LocalException(
          "Attempt to access a service without an authorization token, the user is likely not logged in.");
    String parsedToken = authToken.replace("\"", "");
    if (tokenUsername.containsKey(parsedToken)) {
      String username = tokenUsername.get(parsedToken);
      Logger.getLogger(this.getClass()).info(
          "User = " + username + " Token = " + parsedToken);
      return username;
    } else
      throw new LocalException("AuthToken does not have a valid username.");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ping.services.SecurityService#authorizeToken(java.lang
   * .String)
   */
  @Override
  public UserRole getApplicationRoleForToken(String authToken) throws Exception {

    if (authToken == null)
      throw new LocalException(
          "Attempt to access a service without an authorization token, the user is likely not logged in.");
    String parsedToken = authToken.replace("\"", "");

    String username = getUsernameForToken(parsedToken);

    return getUser(username.toLowerCase()).getApplicationRole();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.SecurityService#getUser(java.lang.String)
   */
  @Override
  public User getUser(String username) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.SecurityService#addUser(org.ihtsdo.otf.
   * mapping.helpers.User)
   */
  @Override
  public User addUser(User user) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.SecurityService#removeUser(java.lang.String
   * )
   */
  @Override
  public User removeUser(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.SecurityService#updateUser(org.ihtsdo.otf
   * .mapping.helpers.User)
   */
  @Override
  public User updateUser(User user) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.SecurityService#getUserRoleForProject(java
   * .lang.String, java.lang.Long)
   */
  @Override
  public UserRole getUserRoleForProject(String username, Long projectId) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.services.SecurityService#getUsers()
   */
  @Override
  public UserList getUsers() {
    // TODO Auto-generated method stub
    return null;
  }

}
