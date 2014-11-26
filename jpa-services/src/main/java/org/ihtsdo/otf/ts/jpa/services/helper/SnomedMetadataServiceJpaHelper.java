package org.ihtsdo.otf.ts.jpa.services.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.services.MetadataService;
import org.ihtsdo.otf.ts.services.helpers.GraphHelper;

/**
 * Implementation of {@link MetadataService} for SNOMEDCT.
 */
public class SnomedMetadataServiceJpaHelper extends ContentServiceJpa implements
    MetadataService {

  /**
   * Instantiates an empty {@link SnomedMetadataServiceJpaHelper}.
   *
   * @throws Exception the exception
   */
  public SnomedMetadataServiceJpaHelper() throws Exception {
    super();

  }

  /** The Constant isaRelationshipType. */
  private final static String isaRelationshipType = "116680003";

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
    Map<String, String> map = new HashMap<>();

    // want all descendants, do not use pfsParameter
    Set<Concept> descendants =
        getDescendantConcepts("900000000000443000", terminology, version,
            isaRelationshipType);

    for (Concept descendant : descendants) {
      if (descendant.isActive()) {
        map.put(new String(descendant.getTerminologyId()),
            descendant.getDefaultPreferredName());
      }
    }
    return map;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.services.MetadataService#getAttributeDescriptions(java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, String> getAttributeDescriptions(String terminology,
    String version) throws Exception {
    Map<String, String> map = new HashMap<>();

    // want all descendants, do not use pfsParameter
    // descendants of of 900000000000457003 | Reference set attribute |
    Set<Concept> descendants =
        getDescendantConcepts("900000000000457003", terminology, version,
            isaRelationshipType);

    for (Concept descendant : descendants) {
      if (descendant.isActive()) {
        map.put(new String(descendant.getTerminologyId()),
            descendant.getDefaultPreferredName());
      }
    }
    return map;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.services.MetadataService#getAttributeTypes(java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, String> getAttributeTypes(String terminology,
    String version) throws Exception {
    Map<String, String> map = new HashMap<>();

    // want all descendants, do not use pfsParameter
    // descendants of 900000000000460005 | component type |
    Set<Concept> descendants =
        getDescendantConcepts("900000000000460005", terminology, version,
            isaRelationshipType);

    for (Concept descendant : descendants) {
      if (descendant.isActive()) {
        map.put(new String(descendant.getTerminologyId()),
            descendant.getDefaultPreferredName());
      }
    }
    return map;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.services.MetadataService#getDescriptionFormats(java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, String> getDescriptionFormats(String terminology,
    String version) throws Exception {
    Map<String, String> map = new HashMap<>();

    // want all descendants, do not use pfsParameter
    // descendants of 900000000000539002 | Description format |
    Set<Concept> descendants =
        getDescendantConcepts("900000000000539002", terminology, version,
            isaRelationshipType);

    for (Concept descendant : descendants) {
      if (descendant.isActive()) {
        map.put(new String(descendant.getTerminologyId()),
            descendant.getDefaultPreferredName());
      }
    }
    return map;
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
    Map<String, String> map = new HashMap<>();

    // want all descendants, do not use pfsParameter
    Set<Concept> descendants =
        getDescendantConcepts("900000000000480006", terminology, version,
            isaRelationshipType);

    for (Concept descendant : descendants) {
      if (descendant.isActive()) {
        map.put(new String(descendant.getTerminologyId()),
            descendant.getDefaultPreferredName());
      }
    }

    return map;
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
    Map<String, String> map = new HashMap<>();

    // want all descendants, do not use pfsParameter
    Set<Concept> descendants =
        getDescendantConcepts("447250001", terminology, version,
            isaRelationshipType);

    for (Concept descendant : descendants) {
      if (descendant.isActive()) {
        map.put(new String(descendant.getTerminologyId()),
            descendant.getDefaultPreferredName());
      }
    }
    return map;
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
    Map<String, String> map = new HashMap<>();

    // want all descendants, do not use pfsParameter
    Set<Concept> descendants =
        getDescendantConcepts("900000000000506000", terminology, version,
            isaRelationshipType);

    for (Concept descendant : descendants) {
      if (descendant.isActive()) {
        map.put(new String(descendant.getTerminologyId()),
            descendant.getDefaultPreferredName());
      }
    }
    return map;
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
    Map<String, String> map = new HashMap<>();

    // want all descendants, do not use pfsParameter
    Set<Concept> descendants =
        getDescendantConcepts("900000000000496009", terminology, version,
            isaRelationshipType);

    for (Concept descendant : descendants) {
      if (descendant.isActive()) {
        map.put(new String(descendant.getTerminologyId()),
            descendant.getDefaultPreferredName());
      }
    }
    return map;
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
    Map<String, String> map = new HashMap<>();

    // want all descendants, do not use pfsParameter
    Set<Concept> descendants =
        getDescendantConcepts("446609009", terminology, version,
            isaRelationshipType);

    for (Concept descendant : descendants) {
      if (descendant.isActive()) {
        map.put(new String(descendant.getTerminologyId()),
            descendant.getDefaultPreferredName());
      }
    }
    return map;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.services.MetadataService#getRefsetDescriptorRefSets(java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, String> getRefsetDescriptorRefSets(String terminology,
    String version) throws Exception {
    Map<String, String> map = new HashMap<>();
    // want all descendants, do not use pfsParameter
    Concept concept =
        getSingleConcept("900000000000456007", terminology, version);
    map.put(concept.getTerminologyId(), concept.getDefaultPreferredName());
    return map;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.services.MetadataService#getModuleDependencyRefSets(java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, String> getModuleDependencyRefSets(String terminology,
    String version) throws Exception {
    Map<String, String> map = new HashMap<>();
    // want all descendants, do not use pfsParameter
    Concept concept =
        getSingleConcept("900000000000534007", terminology, version);
    map.put(concept.getTerminologyId(), concept.getDefaultPreferredName());
    return map;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.services.MetadataService#getDescriptionTypeRefSets(java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, String> getDescriptionTypeRefSets(String terminology,
    String version) throws Exception {
    Map<String, String> map = new HashMap<>();
    // want all descendants, do not use pfsParameter
    Concept concept =
        getSingleConcept("900000000000538005", terminology, version);
    map.put(concept.getTerminologyId(), concept.getDefaultPreferredName());
    return map;
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
    Map<String, String> map = new HashMap<>();

    // want all descendants, do not use pfsParameter
    Set<Concept> descendants =
        getDescendantConcepts("447634004", terminology, version,
            isaRelationshipType);

    Logger.getLogger(this.getClass()).debug(
        "Descendants of 447634004 " + descendants);
    for (Concept descendant : descendants) {
      if (descendant.isActive()) {
        map.put(new String(descendant.getTerminologyId()),
            descendant.getDefaultPreferredName());
      }
    }

    // find all active descendants of 447247004
    // 447247004 - SNOMED CT source code not mappable to target coding scheme
    // want all descendants, do not use pfsParameter
    descendants =
        getDescendantConcepts("447247004", terminology, version,
            isaRelationshipType);

    Logger.getLogger(this.getClass()).debug(
        "Descendants of 447247004 " + descendants);
    for (Concept descendant : descendants) {
      if (descendant.isActive()) {
        map.put(new String(descendant.getTerminologyId()),
            descendant.getDefaultPreferredName());
      }
    }
    return map;
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
    Map<String, String> map = new HashMap<>();

    // want all descendants, do not use pfsParameter
    Set<Concept> descendants =
        getDescendantConcepts("900000000000444006", terminology, version,
            isaRelationshipType);

    for (Concept descendant : descendants) {
      if (descendant.isActive()) {
        map.put(new String(descendant.getTerminologyId()),
            descendant.getDefaultPreferredName());
      }
    }
    return map;
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
    Map<String, String> map = new HashMap<>();

    // want all descendants, do not use pfsParameter
    Set<Concept> descendants =
        getDescendantConcepts("900000000000446008", terminology, version,
            isaRelationshipType);

    for (Concept descendant : descendants) {
      if (descendant.isActive()) {
        map.put(new String(descendant.getTerminologyId()),
            descendant.getDefaultPreferredName());
      }
    }
    return map;
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
    Map<String, String> map = new HashMap<>();

    // want all descendants, do not use pfsParameter
    Set<Concept> descendants =
        getDescendantConcepts("900000000000447004", terminology, version,
            isaRelationshipType);

    for (Concept descendant : descendants) {
      if (descendant.isActive()) {
        map.put(new String(descendant.getTerminologyId()),
            descendant.getDefaultPreferredName());
      }
    }
    return map;
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
    Map<String, String> map = new HashMap<>();

    // want all descendants, do not use pfsParameter
    Set<Concept> descendants =
        getDescendantConcepts("106237007", terminology, version,
            isaRelationshipType);

    for (Concept descendant : descendants) {
      if (descendant.isActive()) {
        map.put(new String(descendant.getTerminologyId()),
            descendant.getDefaultPreferredName());
      }
    }
    return map;
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

    // find all active descendants
    Concept isaRel =
        getSingleConcept(isaRelationshipType, terminology, version);
    map.put(new String(isaRel.getTerminologyId()),
        isaRel.getDefaultPreferredName());
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
    Map<String, String> map = new HashMap<>();

    // want all descendants, do not use pfsParameter
    Set<Concept> descendants =
        getDescendantConcepts("900000000000449001", terminology, version,
            isaRelationshipType);

    for (Concept descendant : descendants) {
      if (descendant.isActive()) {
        map.put(new String(descendant.getTerminologyId()),
            descendant.getDefaultPreferredName());
      }
    }
    return map;
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
    Map<String, String> map = new HashMap<>();

    // want all descendants, do not use pfsParameter
    Set<Concept> descendants =
        getDescendantConcepts("900000000000450001", terminology, version,
            isaRelationshipType);

    for (Concept descendant : descendants) {
      if (descendant.isActive()) {
        map.put(new String(descendant.getTerminologyId()),
            descendant.getDefaultPreferredName());
      }
    }
    return map;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.jpa.services.RootServiceJpa#close()
   */
  @Override
  public void close() {
    // no-op - this is just helper class
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.services.MetadataService#getTerminologies()
   */
  @Override
  public List<String> getTerminologies() {
    // no-op - this is just helper class
    return null;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.services.MetadataService#getVersions(java.lang.String)
   */
  @Override
  public List<String> getVersions(String terminology) {
    // no-op - this is just helper class
    return null;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.services.MetadataService#getLatestVersion(java.lang.String)
   */
  @Override
  public String getLatestVersion(String terminology) {
    // no-op - this is just helper class
    return null;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.services.MetadataService#getTerminologyLatestVersions()
   */
  @Override
  public Map<String, String> getTerminologyLatestVersions() {
    // no-op - this is just helper class
    return null;
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.services.MetadataService#getNonGroupingRelationshipTypes(java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, String> getNonGroupingRelationshipTypes(
    String terminology, String version) throws Exception {
    Map<String, String> map = new HashMap<>();
    final String[] nonGroupingRelIds = new String[] {
        "123005000", "272741003", "127489000", "411116001"
    };
    for (String id : nonGroupingRelIds) {
      Concept concept = getSingleConcept(id, terminology, version);
      map.put(id, concept.getDefaultPreferredName());
    }
    return map;
  }

  /**
   * Helper method for getting descendants.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param terminologyVersion the terminology version
   * @param typeId the type id
   * @return the descendant concepts
   * @throws Exception the exception
   */
  private Set<Concept> getDescendantConcepts(String terminologyId,
    String terminology, String terminologyVersion, String typeId)
    throws Exception {
    Concept concept =
        getSingleConcept(terminologyId, terminology, terminologyVersion);
    return GraphHelper.getDescendantConcepts(concept, typeId);

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
