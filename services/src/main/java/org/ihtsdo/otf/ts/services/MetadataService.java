package org.ihtsdo.otf.ts.services;

import java.util.List;
import java.util.Map;

import org.ihtsdo.otf.ts.helpers.Configurable;

/**
 * Services to retrieve metadata objects.
 *
 * @author ${author}
 */
public interface MetadataService extends RootService, Configurable {

  
  /**
   * An enum for the keys of the get all metadata call.
   *
   * @author ${author}
   */
  public enum MetadataKeys {
    /** The Modules. */
    Modules,
    /** The Attribute_ types. */
    Attribute_Types,
    /** The Attribute_ descriptions. */
    Attribute_Descriptions,
    /** The Description_ formats. */
    Description_Formats,
    /** The Attribute_ value_ refsets. */
    Attribute_Value_Refsets,
    /** The Case_ significances. */
    Case_Significances,
    /** The Complex_ map_ refsets. */
    Complex_Map_Refsets,
    /** The Definition_ statuses. */
    Definition_Statuses,
    /** The Description_ types. */
    Description_Types,
    /** The Language_ refsets. */
    Language_Refsets,
    /** The Map_ relations. */
    Map_Relations,
    /** The Relationship_ characteristic_ types. */
    Characteristic_Types,
    /** The Relationship_ modifiers. */
    Relationship_Modifiers,
    /** The Simple_ map_ refsets. */
    Simple_Map_Refsets,
    /** The Simple_ refsets. */
    Simple_Refsets,
    /** The Refset_ descriptor_ refsets. */
    Refset_Descriptor_Refsets,
    /** The Description_ type_ refsets. */
    Description_Type_Refsets,
    /** The Module_ dependency_ refsets. */
    Module_Dependency_Refsets,
    /** The Non_ grouping_ relationship_ types. */
    Non_Grouping_Relationship_Types,
    /** The Relationship_ types. */
    Relationship_Types,
    /** The Hierarchical_ relationship_ types. */
    Hierarchical_Relationship_Types,
    /**  Stated relationship types. */
    Stated_Characteristic_Types,
    /**  Inferred relationship types. */
    Inferred_Characteristic_Types;
  }
  /**
   * Returns the terminologies.
   * 
   * @return the terminologies
   * @throws Exception if anything goes wrong
   */
  public List<String> getTerminologies() throws Exception;

  /**
   * Returns the versions.
   * 
   * @param terminology the terminology
   * @return the versions
   * @throws Exception if anything goes wrong
   */
  public List<String> getVersions(String terminology) throws Exception;

  /**
   * Returns the latest version.
   * 
   * @param terminology the terminology
   * @return the latest version
   * @throws Exception if anything goes wrong
   */
  public String getLatestVersion(String terminology) throws Exception;

  /**
   * Returns the terminology latest versions.
   * 
   * @return the terminology latest versions
   * @throws Exception if anything goes wrong
   */
  public Map<String, String> getTerminologyLatestVersions() throws Exception;

  // ////////////////////////////
  // Basic retrieval services //
  // ////////////////////////////
  /**
   * Returns the all metadata.
   * 
   * @param terminology the terminology
   * @param version the version
   * @return all metadata
   * @throws Exception if anything goes wrong
   */
  public Map<String, Map<String, String>> getAllMetadata(String terminology,
    String version) throws Exception;

  /**
   * Returns the modules.
   * 
   * @param terminology the terminology
   * @param version the version
   * @return the modules
   * @throws Exception if anything goes wrong
   */
  public Map<String, String> getModules(String terminology, String version)
    throws Exception;

  /**
   * Returns the attribute descriptions. These are associated with fields of the
   * refset descriptor refset members.
   * @param terminology the terminology
   * @param version the version
   * @return the attribute descriptions
   * @throws Exception the exception
   */
  public Map<String, String> getAttributeDescriptions(String terminology,
    String version) throws Exception;

  /**
   * Returns the attribute types. These are associated with fields of the refset
   * descriptor refset members.
   *
   * @param terminology the terminology
   * @param version the version
   * @return the attribute types
   * @throws Exception the exception
   */
  public Map<String, String> getAttributeTypes(String terminology,
    String version) throws Exception;

  /**
   * Returns the description formats. These are associated with fields of the
   * description type refset members.
   *
   * @param terminology the terminology
   * @param version the version
   * @return the description formats
   * @throws Exception the exception
   */
  public Map<String, String> getDescriptionFormats(String terminology,
    String version) throws Exception;

  /**
   * Returns the attribute value ref sets.
   * 
   * @param terminology the terminology
   * @param version the version
   * @return the attribute value ref sets
   * @throws Exception if anything goes wrong
   */
  public Map<String, String> getAttributeValueRefSets(String terminology,
    String version) throws Exception;

  /**
   * Returns the complex map ref sets.
   * 
   * @param terminology the terminology
   * @param version the version
   * @return the complex map ref sets
   * @throws Exception if anything goes wrong
   */
  public Map<String, String> getComplexMapRefSets(String terminology,
    String version) throws Exception;

  /**
   * Returns the language refsets.
   * 
   * @param terminology the terminology
   * @param version the version
   * @return the language refsets
   * @throws Exception if anything goes wrong
   */
  public Map<String, String> getLanguageRefSets(String terminology,
    String version) throws Exception;

  /**
   * Returns the simple map refsets.
   * 
   * @param terminology the terminology
   * @param version the version
   * @return the simple map refsets
   * @throws Exception if anything goes wrong
   */
  public Map<String, String> getSimpleMapRefSets(String terminology,
    String version) throws Exception;

  /**
   * Returns the simple refsets.
   * 
   * @param terminology the terminology
   * @param version the version
   * @return the simple refsets
   * @throws Exception if anything goes wrong
   */
  public Map<String, String> getSimpleRefSets(String terminology, String version)
    throws Exception;

  /**
   * Returns the resfset descriptor ref sets.
   *
   * @param terminology the terminology
   * @param version the version
   * @return the resfset descriptor ref sets
   * @throws Exception the exception
   */
  public Map<String, String> getRefsetDescriptorRefSets(String terminology,
    String version) throws Exception;

  /**
   * Returns the module dependency ref sets.
   *
   * @param terminology the terminology
   * @param version the version
   * @return the module dependency ref sets
   * @throws Exception the exception
   */
  public Map<String, String> getModuleDependencyRefSets(String terminology,
    String version) throws Exception;

  /**
   * Returns the description type ref sets.
   *
   * @param terminology the terminology
   * @param version the version
   * @return the description type ref sets
   * @throws Exception the exception
   */
  public Map<String, String> getDescriptionTypeRefSets(String terminology,
    String version) throws Exception;

  /**
   * Returns the map relations.
   * 
   * @param terminology the terminology
   * @param version the version
   * @return the map relations
   * @throws Exception if anything goes wrong
   */
  public Map<String, String> getMapRelations(String terminology, String version)
    throws Exception;

  /**
   * Returns the definition statuses.
   * 
   * @param terminology the terminology
   * @param version the version
   * @return the definition statuses
   * @throws Exception if anything goes wrong
   */
  public Map<String, String> getDefinitionStatuses(String terminology,
    String version) throws Exception;

  /**
   * Returns the description types.
   * 
   * @param terminology the terminology
   * @param version the version
   * @return the description types
   * @throws Exception if anything goes wrong
   */
  public Map<String, String> getDescriptionTypes(String terminology,
    String version) throws Exception;

  /**
   * Returns the case significances.
   * 
   * @param terminology the terminology
   * @param version the version
   * @return the case significances
   * @throws Exception if anything goes wrong
   */
  public Map<String, String> getCaseSignificances(String terminology,
    String version) throws Exception;

  /**
   * Returns the relationship types.
   * 
   * @param terminology the terminology
   * @param version the version
   * @return the relationship types
   * @throws Exception if anything goes wrong
   */
  public Map<String, String> getRelationshipTypes(String terminology,
    String version) throws Exception;

  /**
   * Returns the hierarchical relationship types. The idea is that these
   * relationship types define "parent" and "child" relationships. When looking
   * through a concept's relationships, anything with one of these types means
   * the destinationId is a "parent". When looking through a concept's inverse
   * relationships, anything with one of these types means the sourceId is a
   * "child".
   * 
   * @param terminology the terminology
   * @param version the version
   * @return the relationship types
   * @throws Exception if anything goes wrong
   */
  public Map<String, String> getHierarchicalRelationshipTypes(
      String terminology, String version) throws Exception;

  /**
   * Returns the inferred characteristic types.  This will correspond
   * to one of the characteristic type ids.
   *
   * @param terminology the terminology
   * @param version the version
   * @return the inferred characteristic types
   * @throws Exception the exception
   */
  public Map<String, String> getInferredCharacteristicTypes(
      String terminology, String version) throws Exception;

  /**
   * Returns the inferred characteristic types.  This will correspond
   * to one of the characteristic type ids.
   *
   * @param terminology the terminology
   * @param version the version
   * @return the stated characteristic types
   * @throws Exception the exception
   */
  public Map<String, String> getStatedCharacteristicTypes(
      String terminology, String version) throws Exception;

  /**
   * Returns the characteristic types.
   * 
   * @param terminology the terminology
   * @param version the version
   * @return the characteristic types
   * @throws Exception if anything goes wrong
   */
  public Map<String, String> getCharacteristicTypes(
    String terminology, String version) throws Exception;

  /**
   * Returns the relationship modifiers.
   * 
   * @param terminology the terminology
   * @param version the version
   * @return the relationship modifiers
   * @throws Exception if anything goes wrong
   */
  public Map<String, String> getRelationshipModifiers(String terminology,
    String version) throws Exception;

  /**
   * Returns the non grouping relationship types.
   *
   * @param terminology the terminology
   * @param version the version
   * @return the non grouping relationship types
   * @throws Exception the exception
   */
  public Map<String, String> getNonGroupingRelationshipTypes(
    String terminology, String version) throws Exception;
  
}
