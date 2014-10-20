package org.ihtsdo.otf.ts.jpa.client;

import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status.Family;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.mapping.rest.SecurityServiceRest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * A client for connecting to a security  REST service.
 */
public class SecurityClientJpa implements SecurityServiceRest {

  /** The config. */
  private Properties config = null;

  /**
   * Instantiates a {@link ContentClientJpa} from the specified parameters.
   *
   * @param config the config
   */
  public SecurityClientJpa(Properties config) {
    this.config = config;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.mapping.rest.SecurityServiceRest#authenticate(java.lang.String, java.lang.String)
   */
  @Override
  public String authenticate(String username, String password) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/security/authenticate/"
            + username);
    resource.accept(MediaType.APPLICATION_JSON);
    ClientResponse response = resource.post(ClientResponse.class, password);
    String resultString = response.getEntity(String.class);
    Logger.getLogger(this.getClass()).info("status: " + response.getStatus());
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).info(resultString);
    } else {
      throw new Exception(resultString);
    }
    // return auth token
    return resultString;
  }

}
