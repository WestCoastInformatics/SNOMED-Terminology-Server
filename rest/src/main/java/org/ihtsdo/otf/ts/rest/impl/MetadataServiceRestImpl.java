/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.rest.impl;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.UserRole;
import org.ihtsdo.otf.ts.helpers.KeyValuePair;
import org.ihtsdo.otf.ts.helpers.KeyValuePairList;
import org.ihtsdo.otf.ts.helpers.KeyValuePairLists;
import org.ihtsdo.otf.ts.jpa.services.MetadataServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.SecurityServiceJpa;
import org.ihtsdo.otf.ts.rest.MetadataServiceRest;
import org.ihtsdo.otf.ts.services.MetadataService;
import org.ihtsdo.otf.ts.services.SecurityService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * REST implementation for {@link MetadataServiceRest}.
 */
@Path("/metadata")
@Api(value = "/metadata", description = "Operations providing terminology metadata.")
@Produces({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
public class MetadataServiceRestImpl extends RootServiceRestImpl implements
    MetadataServiceRest {

  /** The security service. */
  private SecurityService securityService;

  /**
   * Instantiates an empty {@link MetadataServiceRestImpl}.
   *
   * @throws Exception the exception
   */
  public MetadataServiceRestImpl() throws Exception {
    securityService = new SecurityServiceJpa();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.rest.MetadataServiceRest#getMetadata(java.lang.String
   * , java.lang.String, java.lang.String)
   */
  @Override
  @GET
  @Path("/all/terminology/id/{terminology}/{version}")
  @ApiOperation(value = "Get metadata for terminology and version.", notes = "Gets the key-value pairs representing all metadata for a particular terminology and version.", response = KeyValuePairLists.class)
  public KeyValuePairLists getAllMetadata(
    @ApiParam(value = "Terminology name, e.g. SNOMEDCT", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Metadata): /all/terminology/id/" + terminology + "/"
            + version);

    String user = "";
    try {
      user = securityService.getUsernameForToken(authToken);

      // authorize call
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.VIEWER))
        throw new WebApplicationException(Response.status(401)
            .entity("User does not have permissions to retrieve the metadata.")
            .build());

      return getMetadataHelper(terminology, version);

    } catch (Exception e) {
      handleException(e, "trying to retrieve the metadata", user);
      return null;
    } finally {
      securityService.close();
    }
  }

  /**
   * Gets the metadata helper.
   *
   * @param terminology the terminology
   * @param version the version
   * @return the metadata helper
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  private KeyValuePairLists getMetadataHelper(String terminology, String version)
    throws Exception {
    MetadataService metadataService = new MetadataServiceJpa();
    try {
      // verify terminology and version pair exist
      if (metadataService.getTerminologies().contains(terminology)) {

        // if this version does not exist for terminology, throw 204 (No
        // Content)
        if (!metadataService.getVersions(terminology).contains(version)) {
          throw new WebApplicationException(Response
              .status(204)
              .entity(
                  "No version " + version + " is loaded for terminology "
                      + terminology).build());
        } else {
          // do nothing
        }
      } else {
        // terminology does not exist, throw 204 (No Content)
        throw new WebApplicationException(Response.status(204)
            .entity("No terminology " + terminology + " is loaded").build());
      }

      // call jpa service and get complex map return type
      Map<String, Map<String, String>> mapOfMaps =
          metadataService.getAllMetadata(terminology, version);

      // convert complex map to KeyValuePair objects for easy transformation to
      // XML/JSON
      KeyValuePairLists keyValuePairLists = new KeyValuePairLists();
      for (Map.Entry<String, Map<String, String>> entry : mapOfMaps.entrySet()) {
        String metadataType = entry.getKey();
        Map<String, String> metadataPairs = entry.getValue();
        KeyValuePairList keyValuePairList = new KeyValuePairList();
        keyValuePairList.setName(metadataType);
        for (Map.Entry<String, String> pairEntry : metadataPairs.entrySet()) {
          KeyValuePair keyValuePair =
              new KeyValuePair(pairEntry.getKey().toString(),
                  pairEntry.getValue());
          keyValuePairList.addKeyValuePair(keyValuePair);
        }
        keyValuePairLists.addKeyValuePairList(keyValuePairList);
      }
      return keyValuePairLists;
    } catch (Exception e) {
      throw e;
    } finally {
      metadataService.close();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.rest.MetadataServiceRest#
   * getAllTerminologiesLatestVersions(java.lang.String)
   */
  @Override
  @GET
  @Path("/terminology/terminologies/latest")
  @ApiOperation(value = "Get all terminologies and their latest versions.", notes = "Gets the list of terminologies and their latest versions.", response = KeyValuePairList.class)
  public KeyValuePairList getAllTerminologiesLatestVersions(
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Metadata): /terminologies/latest/");

    String user = "";
    MetadataService metadataService = new MetadataServiceJpa();
    try {
      // authorize call
      user = securityService.getUsernameForToken(authToken);
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.VIEWER))
        throw new WebApplicationException(
            Response
                .status(401)
                .entity(
                    "User does not have permissions to retrieve the latest versions of all terminologies.")
                .build());

      Map<String, String> versionMap =
          metadataService.getTerminologyLatestVersions();
      KeyValuePairList keyValuePairList = new KeyValuePairList();
      for (Map.Entry<String, String> termVersionPair : versionMap.entrySet()) {
        keyValuePairList.addKeyValuePair(new KeyValuePair(termVersionPair
            .getKey(), termVersionPair.getValue()));
      }
      return keyValuePairList;
    } catch (Exception e) {
      handleException(e,
          "trying to retrieve the latest versions of all terminologies", user);
      return null;
    } finally {
      metadataService.close();
      securityService.close();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.rest.MetadataServiceRest#getAllTerminologiesVersions
   * (java.lang.String)
   */
  @Override
  @GET
  @Path("/terminology/terminologies")
  @ApiOperation(value = "Get all terminologies and all their versions", notes = "Gets the list of all terminologies and all of their versions", response = KeyValuePairList.class)
  public KeyValuePairLists getAllTerminologiesVersions(
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass())
        .info("RESTful call (Metadata): /terminologies");

    String user = "";
    MetadataService metadataService = new MetadataServiceJpa();

    try {
      // authorize call
      user = securityService.getUsernameForToken(authToken);
      UserRole role = securityService.getApplicationRoleForToken(authToken);
      if (!role.hasPrivilegesOf(UserRole.VIEWER))
        throw new WebApplicationException(
            Response
                .status(401)
                .entity(
                    "User does not have permissions to retrieve the versions of all terminologies.")
                .build());

      KeyValuePairLists keyValuePairLists = new KeyValuePairLists();
      List<String> terminologies = metadataService.getTerminologies();
      for (String terminology : terminologies) {
        List<String> versions = metadataService.getVersions(terminology);
        KeyValuePairList keyValuePairList = new KeyValuePairList();
        for (String version : versions) {
          keyValuePairList.addKeyValuePair(new KeyValuePair(terminology,
              version));
        }
        keyValuePairList.setName(terminology);
        keyValuePairLists.addKeyValuePairList(keyValuePairList);
      }
      return keyValuePairLists;
    } catch (Exception e) {
      handleException(e,
          "trying to retrieve the versions of all terminologies", user);
      return null;
    } finally {
      metadataService.close();
      securityService.close();
    }
  }

}
