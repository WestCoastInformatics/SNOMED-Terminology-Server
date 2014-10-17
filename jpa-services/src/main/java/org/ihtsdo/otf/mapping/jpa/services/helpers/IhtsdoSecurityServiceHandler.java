package org.ihtsdo.otf.mapping.jpa.services.helpers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status.Family;

import org.apache.log4j.Logger;
import org.apache.solr.common.util.Hash;
import org.ihtsdo.otf.mapping.helpers.LocalException;
import org.ihtsdo.otf.mapping.helpers.User;
import org.ihtsdo.otf.mapping.helpers.UserJpa;
import org.ihtsdo.otf.mapping.jpa.services.SecurityServiceHandler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;

/**
 * Implements a security handler that authorizes via IHTSDO authentication.
 */
public class IhtsdoSecurityServiceHandler implements SecurityServiceHandler {

  @SuppressWarnings("unchecked")
  @Override
  public User authenticate(String username, String password,
    Properties properties) throws LocalException, JsonParseException,
    JsonMappingException, IOException {
    // TODO: put this into an IhtsdoSecurityServiceHandler and wire
    // this to the config so that it can be injected (lazy init, only once)
    // set up request to be posted to ihtsdo security service
    // the handler should return a User object (not JPA).
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
    String authUserName = "";
    String authEmail = "";
    String authGivenName = "";
    String authSurname = "";

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

    User returnUser = new UserJpa();
    returnUser.setName(authGivenName + " " + authSurname);
    returnUser.setEmail(authEmail);
    returnUser.setUserName(authUserName);
    return returnUser;
  }

}
