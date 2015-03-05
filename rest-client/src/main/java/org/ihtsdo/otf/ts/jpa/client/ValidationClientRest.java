package org.ihtsdo.otf.ts.jpa.client;

import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status.Family;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.ValidationResult;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.jpa.ValidationResultJpa;
import org.ihtsdo.otf.ts.rest.ValidationServiceRest;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * A client for connecting to a validation REST service.
 */
public class ValidationClientRest implements ValidationServiceRest {

  /** The config. */
  private Properties config = null;

  /**
   * Instantiates a {@link ContentClientRest} from the specified parameters.
   *
   * @param config the config
   */
  public ValidationClientRest(Properties config) {
    this.config = config;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.rest.ValidationServiceRest#validateConcept(org.ihtsdo
   * .otf.ts.rf2.Concept, java.lang.String)
   */
  @Override
  public ValidationResult validateConcept(ConceptJpa concept, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/validation/concept");

    String conceptString =
        (concept != null ? ConfigUtility.getStringForGraph(concept) : null);
    Logger.getLogger(this.getClass()).info(conceptString);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, conceptString);

    String resultString = response.getEntity(String.class);
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    ValidationResult result =
        (ValidationResult) ConfigUtility.getGraphForString(resultString,
            ValidationResultJpa.class);
    return result;
  }

}
