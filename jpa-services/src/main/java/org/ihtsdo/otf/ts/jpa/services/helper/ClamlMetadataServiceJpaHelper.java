package org.ihtsdo.otf.ts.jpa.services.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.ihtsdo.otf.ts.helpers.GraphHelper;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.SearchResult;
import org.ihtsdo.otf.ts.helpers.SearchResultList;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.RootServiceJpa;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.services.ContentService;
import org.ihtsdo.otf.ts.services.MetadataService;

/**
 * Implementation of {@link MetadataService} for ClaML based terminologies.
 * 
 */
public class ClamlMetadataServiceJpaHelper extends RootServiceJpa implements
    MetadataService {

  /**
   * Instantiates an empty {@link ClamlMetadataServiceJpaHelper}.
   * 
   * @throws Exception the exception
   */
  public ClamlMetadataServiceJpaHelper() throws Exception {
    super();
  }

  /**
   * Returns the isa relationship type.
   * 
   * @param terminology the terminology
   * @param version the version
   * @return the isa relationship type
   * @throws Exception the exception
   */
  private static String getIsaRelationshipType(String terminology,
    String version) throws Exception {
    ContentService contentService = new ContentServiceJpa();
    SearchResultList results =
        contentService.findConceptsForQuery(terminology, version, "Isa", new PfsParameterJpa());
    for (SearchResult result : results.getSearchResults()) {
      if (result.getTerminology().equals(terminology)
          && result.getTerminologyVersion().equals(version)
          && result.getValue().equals("Isa")) {

        contentService.close();
        return new String(result.getTerminologyId());
      }
    }
    contentService.close();
    return "-1";
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.MetadataService#getAllMetadata(java.lang
   * .String, java.lang.String)
   */
  @Override
  public Map<String, Map<String, String>> getAllMetadata(String terminology,
    String version) {
    // no-op - this is just helper class
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.MetadataService#getModules(java.lang.String
   * , java.lang.String)
   */
  @Override
  public Map<String, String> getModules(String terminology, String version)
    throws Exception {
    ContentService contentService = new ContentServiceJpa();
    Long rootId = null;
    SearchResultList results =
        contentService.findConceptsForQuery(terminology, version, "Module", new PfsParameterJpa());
    for (SearchResult result : results.getSearchResults()) {
      if (result.getTerminology().equals(terminology)
          && result.getTerminologyVersion().equals(version)
          && result.getValue().equals("Module")) {
        rootId = result.getId();
        break;
      }
    }
    if (rootId == null)
      throw new Exception("Module concept cannot be found.");

    return getDescendantMap(contentService, rootId, terminology, version);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.MetadataService#getAttributeValueRefSets
   * (java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, String> getAttributeValueRefSets(String terminology,
    String version) throws NumberFormatException, Exception {
    return new HashMap<>();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.MetadataService#getComplexMapRefSets(java
   * .lang.String, java.lang.String)
   */
  @Override
  public Map<String, String> getComplexMapRefSets(String terminology,
    String version) throws NumberFormatException, Exception {
    return new HashMap<>();

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.MetadataService#getLanguageRefSets(java
   * .lang.String, java.lang.String)
   */
  @Override
  public Map<String, String> getLanguageRefSets(String terminology,
    String version) throws NumberFormatException, Exception {
    return new HashMap<>();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.MetadataService#getSimpleMapRefSets(java
   * .lang.String, java.lang.String)
   */
  @Override
  public Map<String, String> getSimpleMapRefSets(String terminology,
    String version) throws NumberFormatException, Exception {
    return new HashMap<>();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.MetadataService#getSimpleRefSets(java.lang
   * .String, java.lang.String)
   */
  @Override
  public Map<String, String> getSimpleRefSets(String terminology, String version)
    throws NumberFormatException, Exception {
    ContentService contentService = new ContentServiceJpa();
    Long rootId = null;
    SearchResultList results =
        contentService.findConceptsForQuery(terminology, version, "Simple refsets",
            new PfsParameterJpa());
    for (SearchResult result : results.getSearchResults()) {
      if (result.getTerminology().equals(terminology)
          && result.getTerminologyVersion().equals(version)
          && result.getValue().equals("Simple refsets")) {
        rootId = result.getId();
        break;
      }
    }
    if (rootId == null)
      throw new Exception("Simple refsets concept cannot be found.");

    return getDescendantMap(contentService, rootId, terminology, version);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.MetadataService#getMapRelations(java.lang
   * .String, java.lang.String)
   */
  @Override
  public Map<String, String> getMapRelations(String terminology, String version)
    throws NumberFormatException, Exception {
    return new HashMap<>();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.MetadataService#getDefinitionStatuses(java
   * .lang.String, java.lang.String)
   */
  @Override
  public Map<String, String> getDefinitionStatuses(String terminology,
    String version) throws NumberFormatException, Exception {
    ContentService contentService = new ContentServiceJpa();
    Long rootId = null;
    SearchResultList results =
        contentService.findConceptsForQuery(terminology, version, "Definition status",
            new PfsParameterJpa());
    for (SearchResult result : results.getSearchResults()) {
      if (result.getTerminology().equals(terminology)
          && result.getTerminologyVersion().equals(version)
          && result.getValue().equals("Definition status")) {
        rootId = result.getId();
        break;
      }
    }
    if (rootId == null)
      throw new Exception("Definition status concept cannot be found.");

    return getDescendantMap(contentService, rootId, terminology, version);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.MetadataService#getDescriptionTypes(java
   * .lang.String, java.lang.String)
   */
  @Override
  public Map<String, String> getDescriptionTypes(String terminology,
    String version) throws NumberFormatException, Exception {
    ContentService contentService = new ContentServiceJpa();
    Long rootId = null;
    SearchResultList results =
        contentService.findConceptsForQuery(terminology, version, "Description type",
            new PfsParameterJpa());
    for (SearchResult result : results.getSearchResults()) {
      if (result.getTerminology().equals(terminology)
          && result.getTerminologyVersion().equals(version)
          && result.getValue().equals("Description type")) {
        rootId = result.getId();
        break;
      }
    }
    if (rootId == null)
      throw new Exception("Description type concept cannot be found.");

    return getDescendantMap(contentService, rootId, terminology, version);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.MetadataService#getCaseSignificances(java
   * .lang.String, java.lang.String)
   */
  @Override
  public Map<String, String> getCaseSignificances(String terminology,
    String version) throws NumberFormatException, Exception {
    ContentService contentService = new ContentServiceJpa();
    Long rootId = null;
    SearchResultList results =
        contentService.findConceptsForQuery(terminology, version, "Case significance",
            new PfsParameterJpa());
    for (SearchResult result : results.getSearchResults()) {
      if (result.getTerminology().equals(terminology)
          && result.getTerminologyVersion().equals(version)
          && result.getValue().equals("Case significance")) {
        rootId = result.getId();
        break;
      }
    }
    if (rootId == null)
      throw new Exception("Case significance concept cannot be found.");

    return getDescendantMap(contentService, rootId, terminology, version);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.MetadataService#getRelationshipTypes(java
   * .lang.String, java.lang.String)
   */
  @Override
  public Map<String, String> getRelationshipTypes(String terminology,
    String version) throws NumberFormatException, Exception {
    // find all active descendants of 106237007
    ContentService contentService = new ContentServiceJpa();
    Long rootId = null;
    SearchResultList results =
        contentService.findConceptsForQuery(terminology, version, "Relationship type",
            new PfsParameterJpa());
    for (SearchResult result : results.getSearchResults()) {
      if (result.getTerminology().equals(terminology)
          && result.getTerminologyVersion().equals(version)
          && result.getValue().equals("Relationship type")) {
        rootId = result.getId();
        break;
      }
    }
    if (rootId == null)
      throw new Exception("Relationship type concept cannot be found.");

    return getDescendantMap(contentService, rootId, terminology, version);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.services.MetadataService#
   * getHierarchicalRelationshipTypes(java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, String> getHierarchicalRelationshipTypes(
    String terminology, String version) throws NumberFormatException, Exception {
    Map<String, String> map = new HashMap<>();

    // find all active descendants of 106237007
    ContentService contentService = new ContentServiceJpa();
    Concept isaRel =
        contentService.getSingleConcept(
            getIsaRelationshipType(terminology, version).toString(),
            terminology, version);
    map.put(new String(isaRel.getTerminologyId()),
        isaRel.getDefaultPreferredName());

    contentService.close();
    return map;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.services.MetadataService#
   * getRelationshipCharacteristicTypes(java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, String> getRelationshipCharacteristicTypes(
    String terminology, String version) throws NumberFormatException, Exception {
    ContentService contentService = new ContentServiceJpa();
    Long rootId = null;
    SearchResultList results =
        contentService.findConceptsForQuery(terminology, version, "Characteristic type",
            new PfsParameterJpa());
    for (SearchResult result : results.getSearchResults()) {
      if (result.getTerminology().equals(terminology)
          && result.getTerminologyVersion().equals(version)
          && result.getValue().equals("Characteristic type")) {
        rootId = result.getId();
        break;
      }
    }
    if (rootId == null)
      throw new Exception("Characteristic type concept cannot be found.");

    return getDescendantMap(contentService, rootId, terminology, version);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.MetadataService#getRelationshipModifiers
   * (java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, String> getRelationshipModifiers(String terminology,
    String version) throws NumberFormatException, Exception {
    ContentService contentService = new ContentServiceJpa();
    Long rootId = null;
    SearchResultList results =
        contentService.findConceptsForQuery(terminology, version, "Modifier", new PfsParameterJpa());
    for (SearchResult result : results.getSearchResults()) {
      if (result.getTerminology().equals(terminology)
          && result.getTerminologyVersion().equals(version)
          && result.getValue().equals("Modifier")) {
        rootId = result.getId();
        break;
      }
    }
    if (rootId == null)
      throw new Exception("Modifier concept cannot be found.");

    return getDescendantMap(contentService, rootId, terminology, version);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.services.MetadataService#close()
   */
  @Override
  public void close() {
    // no-op - this is just helper class
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.services.MetadataService#getTerminologies()
   */
  @Override
  public List<String> getTerminologies() {
    // no-op - this is just helper class
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.MetadataService#getVersions(java.lang.String
   * )
   */
  @Override
  public List<String> getVersions(String terminology) {
    // no-op - this is just helper class
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.MetadataService#getLatestVersion(java.lang
   * .String)
   */
  @Override
  public String getLatestVersion(String terminology) {
    // no-op - this is just helper class
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.MetadataService#getTerminologyLatestVersions
   * ()
   */
  @Override
  public Map<String, String> getTerminologyLatestVersions() {
    // no-op - this is just helper class
    return null;
  }

  /**
   * Returns the descendant map for the specified parameters.
   * 
   * @param contentService the content service
   * @param id the concept id
   * @param terminology the terminology
   * @param version the version
   * @return the descendant map
   * @throws Exception
   */
  @SuppressWarnings("static-method")
  private Map<String, String> getDescendantMap(ContentService contentService,
    Long id, String terminology, String version) throws Exception {
    Map<String, String> map = new HashMap<>();

    // want all descendants, do not use pfsParameter
    Concept concept = contentService.getConcept(id);

    Set<Concept> descendants =
        GraphHelper.getDescendantConcepts(concept,
            getIsaRelationshipType(terminology, version));

    for (Concept descendant : descendants) {
      if (descendant.isActive()) {
        map.put(new String(descendant.getTerminologyId()),
            descendant.getDefaultPreferredName());
      }
    }
    contentService.close();
    return map;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.helpers.Configurable#setProperties(java.util.Properties)
   */
  @Override
  public void setProperties(Properties p) {
    // do nothing    
  }
}
