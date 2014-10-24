package org.ihtsdo.otf.ts.jpa.client;

import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status.Family;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ConceptListJpa;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.helpers.SearchResultListJpa;
import org.ihtsdo.otf.ts.rest.ContentServiceRest;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * A client for connecting to a content REST service.
 */
public class ContentClientRest implements ContentServiceRest {

  /** The config. */
  private Properties config = null;

  /**
   * Instantiates a {@link ContentClientRest} from the specified parameters.
   *
   * @param config the config
   */
  public ContentClientRest(Properties config) {
    this.config = config;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.rest.ContentServiceRest#getConcept(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public ConceptList getConcepts(String terminologyId, String terminology,
    String version, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/content/concepts/"
            + terminology + "/" + version + "/" + terminologyId);
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
    ConceptListJpa c =
        (ConceptListJpa) ConfigUtility.getGraphForString(resultString,
            ConceptListJpa.class);
    return c;
  }

  @Override
  public Concept getSingleConcept(String terminologyId, String terminology,
    String version, String authToken) throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/content/concept/"
            + terminology + "/" + version + "/" + terminologyId);
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
    ConceptJpa c =
        (ConceptJpa) ConfigUtility.getGraphForString(resultString,
            ConceptJpa.class);
    return c;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.rest.ContentServiceRest#findConceptsForQuery(java
   * .lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public SearchResultList findConceptsForQuery(String terminology,
    String version, String searchString, PfsParameterJpa pfs, String authToken)
    throws Exception {
    Client client = Client.create();
    WebResource resource =
        client.resource(config.getProperty("base.url") + "/content/concepts/"
            + terminology + "/" + version + "/query/" + searchString);
    String pfsString =
        (pfs != null ? ConfigUtility.getStringForGraph(pfs) : null);
    ClientResponse response =
        resource.accept(MediaType.APPLICATION_XML)
            .header("Authorization", authToken)
            .header("Content-type", MediaType.APPLICATION_XML)
            .post(ClientResponse.class, pfsString);

    String resultString = response.getEntity(String.class);
    if (response.getClientResponseStatus().getFamily() == Family.SUCCESSFUL) {
      Logger.getLogger(this.getClass()).debug(resultString);
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    SearchResultListJpa list =
        (SearchResultListJpa) ConfigUtility.getGraphForString(resultString,
            SearchResultListJpa.class);
    return list;
  }

}
