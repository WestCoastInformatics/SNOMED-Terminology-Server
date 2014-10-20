package org.ihtsdo.otf.ts.jpa.client;

import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status.Family;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.mapping.helpers.SearchResultList;
import org.ihtsdo.otf.mapping.rest.ContentServiceRest;
import org.ihtsdo.otf.mapping.rf2.Concept;
import org.ihtsdo.otf.mapping.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.mapping.services.helpers.ConfigUtility;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * A client for connecting to a content REST service.
 */
public class ContentClientJpa implements ContentServiceRest {

  /** The config. */
  private Properties config = null;

  /**
   * Instantiates a {@link ContentClientJpa} from the specified parameters.
   *
   * @param config the config
   */
  public ContentClientJpa(Properties config) {
    this.config = config;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.mapping.rest.ContentServiceRest#getConcept(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public Concept getConcept(String terminologyId, String terminology,
    String terminologyVersion, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/concept/"
            + terminology + "/" + terminologyVersion + "/" + terminologyId);
    resource.accept(MediaType.APPLICATION_JSON);
    resource.setProperty("Authorization", authToken);
    ClientResponse response = resource.get(ClientResponse.class);
    String resultString = response.getEntity(String.class);
    Logger.getLogger(this.getClass()).info("status: " + response.getStatus());
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).info(resultString);
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    Concept c =
        (Concept) ConfigUtility.getGraphForString(resultString,
            ConceptJpa.class);
    return c;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.mapping.rest.ContentServiceRest#findConceptsForQuery(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public SearchResultList findConceptsForQuery(String terminology,
    String terminologyVersion, String searchString, String authToken) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SearchResultList findDescendantConcepts(String terminologyId,
    String terminology, String terminologyVersion, String authToken) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SearchResultList findChildConcepts(String terminologyId,
    String terminology, String terminologyVersion, String authToken) {
    // TODO Auto-generated method stub
    return null;
  }

}
