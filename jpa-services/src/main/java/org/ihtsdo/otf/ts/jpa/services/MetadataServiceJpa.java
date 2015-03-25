package org.ihtsdo.otf.ts.jpa.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.services.MetadataService;

/**
 * Implementation of {@link MetadataService} that redirects to
 * terminology-specific implemlentations.
 */
public class MetadataServiceJpa extends RootServiceJpa implements
    MetadataService {

  /** The helper map. */
  private static Map<String, MetadataService> helperMap = null;
  static {
    helperMap = new HashMap<>();
    Properties config;
    try {
      config = ConfigUtility.getConfigProperties();
      String key = "metadata.service.handler";
      for (String handlerName : config.getProperty(key).split(",")) {

        // Add handlers to map
        MetadataService handlerService =
            ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
                handlerName, MetadataService.class);
        helperMap.put(handlerName, handlerService);
      }
    } catch (Exception e) {
      e.printStackTrace();
      helperMap = null;
    }
  }

  /**
   * Instantiates an empty {@link MetadataServiceJpa}.
   *
   * @throws Exception the exception
   */
  public MetadataServiceJpa() throws Exception {
    super();

    if (helperMap == null) {
      throw new Exception("Helper map not properly initialized, serious error.");
    }
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
    String version) throws Exception {
    Map<String, Map<String, String>> idNameMapList = new HashMap<>();
    Map<String, String> modulesIdNameMap = getModules(terminology, version);
    if (modulesIdNameMap != null) {
      idNameMapList.put(MetadataKeys.Modules.toString(), modulesIdNameMap);
    }

    Map<String, String> atNameMap =
        getAttributeTypes(terminology, version);
    if (atNameMap != null) {
      idNameMapList.put(MetadataKeys.Attribute_Types.toString(), atNameMap);
    }

    Map<String, String> adNameMap = getAttributeDescriptions(terminology, version);
    if (adNameMap != null) {
      idNameMapList.put(MetadataKeys.Attribute_Descriptions.toString(),
          adNameMap);
    }

    Map<String, String> dfNameMap = getDescriptionFormats(terminology, version);
    if (dfNameMap != null) {
      idNameMapList.put(MetadataKeys.Description_Formats.toString(), dfNameMap);
    }

    Map<String, String> atvIdNameMap =
        getAttributeValueRefSets(terminology, version);
    if (atvIdNameMap != null) {
      idNameMapList.put(MetadataKeys.Attribute_Value_Refsets.toString(),
          atvIdNameMap);
    }
    Map<String, String> csIdNameMap =
        getCaseSignificances(terminology, version);
    if (csIdNameMap != null) {
      idNameMapList
          .put(MetadataKeys.Case_Significances.toString(), csIdNameMap);
    }
    Map<String, String> cmIdNameMap =
        getComplexMapRefSets(terminology, version);
    if (cmIdNameMap != null) {
      idNameMapList.put(MetadataKeys.Complex_Map_Refsets.toString(),
          cmIdNameMap);
    }
    Map<String, String> dsIdNameMap =
        getDefinitionStatuses(terminology, version);
    if (dsIdNameMap != null) {
      idNameMapList.put(MetadataKeys.Definition_Statuses.toString(),
          dsIdNameMap);
    }
    Map<String, String> dtIdNameMap = getDescriptionTypes(terminology, version);
    if (dtIdNameMap != null) {
      idNameMapList.put(MetadataKeys.Description_Types.toString(), dtIdNameMap);
    }
    Map<String, String> lIdNameMap = getLanguageRefSets(terminology, version);
    if (lIdNameMap != null) {
      idNameMapList.put(MetadataKeys.Language_Refsets.toString(), lIdNameMap);
    }
    Map<String, String> mrIdNameMap = getMapRelations(terminology, version);
    if (mrIdNameMap != null) {
      idNameMapList.put(MetadataKeys.Map_Relations.toString(), mrIdNameMap);
    }
    Map<String, String> rctIdNameMap =
        getCharacteristicTypes(terminology, version);
    if (rctIdNameMap != null) {
      idNameMapList.put(
          MetadataKeys.Characteristic_Types.toString(),
          rctIdNameMap);
    }
    Map<String, String> sctIdNameMap =
        getStatedCharacteristicTypes(terminology, version);
    if (rctIdNameMap != null) {
      idNameMapList.put(
          MetadataKeys.Stated_Characteristic_Types.toString(),
          sctIdNameMap);
    }
    
    Map<String, String> ictIdNameMap =
        getInferredCharacteristicTypes(terminology, version);
    if (rctIdNameMap != null) {
      idNameMapList.put(
          MetadataKeys.Inferred_Characteristic_Types.toString(),
          ictIdNameMap);
    }
    
    Map<String, String> rmIdNameMap =
        getRelationshipModifiers(terminology, version);
    if (rmIdNameMap != null) {
      idNameMapList.put(MetadataKeys.Relationship_Modifiers.toString(),
          rmIdNameMap);
    }
    Map<String, String> rtIdNameMap =
        getRelationshipTypes(terminology, version);
    if (rtIdNameMap != null) {
      idNameMapList
          .put(MetadataKeys.Relationship_Types.toString(), rtIdNameMap);
    }
    Map<String, String> hierRtIdNameMap =
        getHierarchicalRelationshipTypes(terminology, version);
    if (hierRtIdNameMap != null) {
      idNameMapList.put(
          MetadataKeys.Hierarchical_Relationship_Types.toString(),
          hierRtIdNameMap);
    }
    Map<String, String> smIdNameMap = getSimpleMapRefSets(terminology, version);
    if (smIdNameMap != null) {
      idNameMapList
          .put(MetadataKeys.Simple_Map_Refsets.toString(), smIdNameMap);
    }
    Map<String, String> sIdNameMap = getSimpleRefSets(terminology, version);
    if (sIdNameMap != null) {
      idNameMapList.put(MetadataKeys.Simple_Refsets.toString(), sIdNameMap);
    }

    Map<String, String> rdNameMap =
        getRefsetDescriptorRefSets(terminology, version);
    if (rdNameMap != null) {
      idNameMapList.put(MetadataKeys.Refset_Descriptor_Refsets.toString(),
          rdNameMap);
    }

    Map<String, String> dtNameMap =
        getDescriptionTypeRefSets(terminology, version);
    if (dtNameMap != null) {
      idNameMapList.put(MetadataKeys.Description_Type_Refsets.toString(),
          dtNameMap);
    }

    Map<String, String> mdNameMap =
        getModuleDependencyRefSets(terminology, version);
    if (mdNameMap != null) {
      idNameMapList.put(MetadataKeys.Module_Dependency_Refsets.toString(),
          mdNameMap);
    }

    Map<String, String> nonGroupingRelTypeMap =
        getNonGroupingRelationshipTypes(terminology, version);
    if (sIdNameMap != null) {
      idNameMapList.put(
          MetadataKeys.Non_Grouping_Relationship_Types.toString(),
          nonGroupingRelTypeMap);
    }

    return idNameMapList;
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
    if (helperMap.containsKey(terminology)) {
      return helperMap.get(terminology).getModules(terminology, version);
    } else {
      // return an empty map
      return new HashMap<>();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.MetadataService#getAttributeDescriptions(java
   * .lang.String, java.lang.String)
   */
  @Override
  public Map<String, String> getAttributeDescriptions(String terminology,
    String version) throws Exception {
    if (helperMap.containsKey(terminology)) {
      return helperMap.get(terminology).getAttributeDescriptions(terminology,
          version);
    } else {
      // return an empty map
      return new HashMap<>();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.MetadataService#getAttributeTypes(java.lang.
   * String, java.lang.String)
   */
  @Override
  public Map<String, String> getAttributeTypes(String terminology,
    String version) throws Exception {
    if (helperMap.containsKey(terminology)) {
      return helperMap.get(terminology).getAttributeTypes(terminology, version);
    } else {
      // return an empty map
      return new HashMap<>();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.MetadataService#getDescriptionFormats(java.lang
   * .String, java.lang.String)
   */
  @Override
  public Map<String, String> getDescriptionFormats(String terminology,
    String version) throws Exception {
    if (helperMap.containsKey(terminology)) {
      return helperMap.get(terminology).getDescriptionFormats(terminology,
          version);
    } else {
      // return an empty map
      return new HashMap<>();
    }
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
    String version) throws Exception {
    if (helperMap.containsKey(terminology)) {
      return helperMap.get(terminology).getAttributeValueRefSets(terminology,
          version);
    } else {
      // return an empty map
      return new HashMap<>();
    }
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
    String version) throws Exception {
    if (helperMap.containsKey(terminology)) {
      return helperMap.get(terminology).getComplexMapRefSets(terminology,
          version);
    } else {
      // return an empty map
      return new HashMap<>();
    }
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
    String version) throws Exception {
    if (helperMap.containsKey(terminology)) {
      return helperMap.get(terminology)
          .getLanguageRefSets(terminology, version);
    } else {
      // return an empty map
      return new HashMap<>();
    }
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
    String version) throws Exception {
    if (helperMap.containsKey(terminology)) {
      return helperMap.get(terminology).getSimpleMapRefSets(terminology,
          version);
    } else {
      // return an empty map
      return new HashMap<>();
    }
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
    throws Exception {
    if (helperMap.containsKey(terminology)) {
      return helperMap.get(terminology).getSimpleRefSets(terminology, version);
    } else {
      // return an empty map
      return new HashMap<>();
    }
  }

  /**
   * Returns the refset descriptor ref sets.
   *
   * @param terminology the terminology
   * @param version the version
   * @return the refset descriptor ref sets
   * @throws Exception the exception
   */
  @Override
  public Map<String, String> getRefsetDescriptorRefSets(String terminology,
    String version) throws Exception {
    if (helperMap.containsKey(terminology)) {
      return helperMap.get(terminology).getRefsetDescriptorRefSets(terminology,
          version);
    } else {
      // return an empty map
      return new HashMap<>();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.MetadataService#getDescriptionTypeRefSets(java
   * .lang.String, java.lang.String)
   */
  @Override
  public Map<String, String> getDescriptionTypeRefSets(String terminology,
    String version) throws Exception {
    if (helperMap.containsKey(terminology)) {
      return helperMap.get(terminology).getDescriptionTypeRefSets(terminology,
          version);
    } else {
      // return an empty map
      return new HashMap<>();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.MetadataService#getModuleDependencyRefSets(java
   * .lang.String, java.lang.String)
   */
  @Override
  public Map<String, String> getModuleDependencyRefSets(String terminology,
    String version) throws Exception {
    if (helperMap.containsKey(terminology)) {
      return helperMap.get(terminology).getModuleDependencyRefSets(terminology,
          version);
    } else {
      // return an empty map
      return new HashMap<>();
    }
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
    throws Exception {
    if (helperMap.containsKey(terminology)) {
      return helperMap.get(terminology).getMapRelations(terminology, version);
    } else {
      // return an empty map
      return new HashMap<>();
    }
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
    String version) throws Exception {
    if (helperMap.containsKey(terminology)) {
      return helperMap.get(terminology).getDefinitionStatuses(terminology,
          version);
    } else {
      // return an empty map
      return new HashMap<>();
    }
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
    String version) throws Exception {
    if (helperMap.containsKey(terminology)) {
      return helperMap.get(terminology).getDescriptionTypes(terminology,
          version);
    } else {
      // return an empty map
      return new HashMap<>();
    }
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
    String version) throws Exception {
    if (helperMap.containsKey(terminology)) {
      return helperMap.get(terminology).getCaseSignificances(terminology,
          version);
    } else {
      // return an empty map
      return new HashMap<>();
    }
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
    String version) throws Exception {
    if (helperMap.containsKey(terminology)) {
      return helperMap.get(terminology).getRelationshipTypes(terminology,
          version);
    } else {
      // return an empty map
      return new HashMap<>();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.services.MetadataService#
   * getHierarchicalRelationshipTypes(java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, String> getHierarchicalRelationshipTypes(
    String terminology, String version) throws Exception {
    if (helperMap.containsKey(terminology)) {
      return helperMap.get(terminology).getHierarchicalRelationshipTypes(
          terminology, version);
    } else {
      // return an empty map
      return new HashMap<>();
    }
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.services.MetadataService#getInferredRelationshipTypes(java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, String> getInferredCharacteristicTypes(
    String terminology, String version) throws Exception {
    if (helperMap.containsKey(terminology)) {
      return helperMap.get(terminology).getInferredCharacteristicTypes(
          terminology, version);
    } else {
      // return an empty map
      return new HashMap<>();
    }
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.services.MetadataService#getStatedRelationshipTypes(java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, String> getStatedCharacteristicTypes(
    String terminology, String version) throws Exception {
    if (helperMap.containsKey(terminology)) {
      return helperMap.get(terminology).getStatedCharacteristicTypes(
          terminology, version);
    } else {
      // return an empty map
      return new HashMap<>();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.services.MetadataService#
   * getCharacteristicTypes(java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, String> getCharacteristicTypes(
    String terminology, String version) throws Exception {
    if (helperMap.containsKey(terminology)) {
      return helperMap.get(terminology).getCharacteristicTypes(
          terminology, version);
    } else {
      // return an empty map
      return new HashMap<>();
    }
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
    String version) throws Exception {
    if (helperMap.containsKey(terminology)) {
      return helperMap.get(terminology).getRelationshipModifiers(terminology,
          version);
    } else {
      // return an empty map
      return new HashMap<>();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.services.MetadataService#getTerminologies()
   */
  @Override
  public List<String> getTerminologies() throws Exception {

    javax.persistence.Query query =
        manager.createQuery("SELECT distinct c.terminology from ConceptJpa c");
    @SuppressWarnings("unchecked")
    List<String> terminologies = query.getResultList();
    return terminologies;

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.MetadataService#getVersions(java.lang.String
   * )
   */
  @Override
  public List<String> getVersions(String terminology) throws Exception {
    javax.persistence.Query query =
        manager
            .createQuery("SELECT distinct c.terminologyVersion from ConceptJpa c where terminology = :terminology");

    query.setParameter("terminology", terminology);
    @SuppressWarnings("unchecked")
    List<String> versions = query.getResultList();
    return versions;

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.MetadataService#getLatestVersion(java.lang
   * .String)
   */
  @Override
  public String getLatestVersion(String terminology) throws Exception {

    javax.persistence.Query query =
        manager
            .createQuery("SELECT max(c.terminologyVersion) from ConceptJpa c where terminology = :terminology");

    query.setParameter("terminology", terminology);
    Object o = query.getSingleResult();
    if (o == null) {
      return null;
    }
    return o.toString();

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.MetadataService#getTerminologyLatestVersions
   * ()
   */
  @Override
  public Map<String, String> getTerminologyLatestVersions() throws Exception {

    javax.persistence.TypedQuery<Object[]> query =
        manager
            .createQuery(
                "SELECT c.terminology, max(c.version) from ConceptJpa c group by c.terminology",
                Object[].class);

    List<Object[]> resultList = query.getResultList();
    Map<String, String> resultMap = new HashMap<>(resultList.size());
    for (Object[] result : resultList)
      resultMap.put((String) result[0], (String) result[1]);

    return resultMap;

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.MetadataService#getNonGroupingRelationshipTypes
   * (java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, String> getNonGroupingRelationshipTypes(
    String terminology, String version) throws Exception {
    if (helperMap.containsKey(terminology)) {
      return helperMap.get(terminology).getNonGroupingRelationshipTypes(
          terminology, version);
    } else {
      // return an empty map
      return new HashMap<>();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.helpers.Configurable#setProperties(java.util.Properties)
   */
  @Override
  public void setProperties(Properties p) {
    // do nothing
  }

}
