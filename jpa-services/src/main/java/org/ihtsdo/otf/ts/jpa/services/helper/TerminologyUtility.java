/*
 * 
 */
package org.ihtsdo.otf.ts.jpa.services.helper;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.ihtsdo.otf.ts.jpa.services.MetadataServiceJpa;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.services.MetadataService;

// TODO: Auto-generated Javadoc
/**
 * Loads and serves configuration.
 */
public class TerminologyUtility {

  /** The isa rels map. */
  public static Map<String, Set<String>> isaRelsMap = new HashMap<>();

  /**  The stated characteristic type value. */
  public static String statedType;

  /**  The stated inferred type value. */
  public static String inferredType;

  /**
   * Returns the hierarchcial isa rels.
   *
   * @param terminology the terminology
   * @param version the version
   * @return the hierarchcial isa rels
   * @throws Exception the exception
   */
  public static Set<String> getHierarchcialIsaRels(String terminology,
    String version) throws Exception {
    cacheIsaRels(terminology, version);
    return isaRelsMap.get(terminology + version);
  }

  /**
   * Indicates whether or not the relationship is a hierarchical isa rel.
   *
   * @param relationship the relationship
   * @return <code>true</code> if so, <code>false</code> otherwise
   * @throws Exception the exception
   */
  public static boolean isHierarchicalIsaRelationship(Relationship relationship)
    throws Exception {
    Set<String> isaRels =
        cacheIsaRels(relationship.getTerminology(),
            relationship.getTerminologyVersion());
    return relationship != null && isaRels.contains(relationship.getTypeId());
  }

  /**
   * Indicates whether or not stated relationship is the case.
   *
   * @param relationship the relationship
   * @return <code>true</code> if so, <code>false</code> otherwise
   * @throws Exception the exception
   */
  public static boolean isStatedRelationship(Relationship relationship) throws Exception {
    cacheCharacteristicTypes(relationship.getTerminology(),
        relationship.getTerminologyVersion());
    return relationship != null && statedType.equals(relationship.getCharacteristicTypeId());
  }

  /**
   * Indicates whether or not inferred relationship is the case.
   *
   * @param relationship the relationship
   * @return <code>true</code> if so, <code>false</code> otherwise
   * @throws Exception the exception
   */
  public static boolean isInferredRelationship(Relationship relationship) throws Exception {
    cacheCharacteristicTypes(relationship.getTerminology(),
        relationship.getTerminologyVersion());
    return relationship != null && inferredType.equals(relationship.getCharacteristicTypeId());
  }

  /**
   * Cache isa rels.
   *
   * @param terminology the terminology
   * @param version the version
   * @return the sets the
   * @throws Exception the exception
   */
  private static Set<String> cacheIsaRels(String terminology, String version)
    throws Exception {
    if (!isaRelsMap.containsKey(terminology + version)) {
      MetadataService metadataService = new MetadataServiceJpa();
      isaRelsMap.put(terminology + version, metadataService
          .getHierarchicalRelationshipTypes(terminology, version).keySet());
      metadataService.close();
    }
    return isaRelsMap.get(terminology + version);
  }

  /**
   * Cache characteristic types.
   *
   * @param terminology the terminology
   * @param version the version
   * @throws Exception the exception
   */
  private static void cacheCharacteristicTypes(String terminology,
    String version) throws Exception {
    if (statedType == null) {
      MetadataService metadataService = new MetadataServiceJpa();
      Map<String, String> map =
          metadataService.getRelationshipCharacteristicTypes(terminology,
              version);
      for (String key : map.keySet()) {
        if (map.get(key).equals("Stated relationship")) {
          statedType = key;
        }
        if (map.get(key).equals("Inferred relationship")) {
          inferredType = key;
        }
      }
    }
  }

  /**
   * Returns the stated type.
   *
   * @param terminology the terminology
   * @param version the version
   * @return the stated type
   * @throws Exception the exception
   */
  public static String getStatedType(String terminology, String version)
    throws Exception {
    cacheCharacteristicTypes(terminology, version);
    return statedType;
  }

  /**
   * Returns the inferred type.
   *
   * @param terminology the terminology
   * @param version the version
   * @return the inferred type
   * @throws Exception the exception
   */
  public static String getInferredType(String terminology, String version)
    throws Exception {
    cacheCharacteristicTypes(terminology, version);
    return inferredType;
  }

  /**
   * Returns the active parent concepts.
   *
   * @param concept the concept
   * @return the active parent concepts
   * @throws Exception the exception
   */
  public static List<Concept> getActiveParentConcepts(Concept concept)
    throws Exception {
    final List<Concept> results = new ArrayList<>();
    final Set<String> isaRels =
        cacheIsaRels(concept.getTerminology(), concept.getTerminologyVersion());
    for (Relationship rel : concept.getRelationships()) {
      if (isaRels.contains(rel.getTypeId()) && rel.isActive()) {
        results.add(rel.getDestinationConcept());
      }
    }
    return results;
  }

  /**
   * Returns the active stated relationships.
   *
   * @param concept the concept
   * @return the active stated relationships
   * @throws Exception the exception
   */
  public static Set<Relationship> getActiveStatedRelationships(Concept concept)
    throws Exception {
    Set<Relationship> rels = new HashSet<>();
    for (Relationship rel : concept.getRelationships()) {
      if (rel.isActive()
          && rel.getTypeId().equals(
              getStatedType(concept.getTerminology(),
                  concept.getTerminologyVersion()))) {
        rels.add(rel);
      }
    }
    return rels;
  }

  /**
   * Returns the active inferred relationships.
   *
   * @param concept the concept
   * @return the active inferred relationships
   * @throws Exception the exception
   */
  public static Set<Relationship> getActiveInferredRelationships(Concept concept)
    throws Exception {
    Set<Relationship> rels = new HashSet<>();
    for (Relationship rel : concept.getRelationships()) {
      if (rel.isActive()
          && rel.getTypeId().equals(
              getInferredType(concept.getTerminology(),
                  concept.getTerminologyVersion()))) {
        rels.add(rel);
      }
    }
    return rels;
  }

  /**
   * Gets the UUID for a string using the null namespace.
   * 
   * @param value to make a UUID from
   * @return the the UUID
   * @throws NoSuchAlgorithmException the no such algorithm exception
   * @throws UnsupportedEncodingException the unsupported encoding exception
   */
  public static UUID getUuid(String value) throws NoSuchAlgorithmException,
    UnsupportedEncodingException {

    MessageDigest sha1Algorithm = MessageDigest.getInstance("SHA-1");

    String namespace = "00000000-0000-0000-0000-000000000000";
    String encoding = "UTF-8";

    UUID namespaceUUID = UUID.fromString(namespace);

    // Generate the digest.
    sha1Algorithm.reset();

    // Generate the digest.
    sha1Algorithm.reset();
    if (namespace != null) {
      sha1Algorithm.update(getRawBytes(namespaceUUID));
    }

    sha1Algorithm.update(value.getBytes(encoding));
    byte[] sha1digest = sha1Algorithm.digest();

    sha1digest[6] &= 0x0f; /* clear version */
    sha1digest[6] |= 0x50; /* set to version 5 */
    sha1digest[8] &= 0x3f; /* clear variant */
    sha1digest[8] |= 0x80; /* set to IETF variant */

    long msb = 0;
    long lsb = 0;
    for (int i = 0; i < 8; i++) {
      msb = (msb << 8) | (sha1digest[i] & 0xff);
    }
    for (int i = 8; i < 16; i++) {
      lsb = (lsb << 8) | (sha1digest[i] & 0xff);
    }

    return new UUID(msb, lsb);

  }

  /**
   * Returns the raw bytes for the UUID.
   * 
   * @param uuid the uuid
   * @return the raw bytes
   */
  private static byte[] getRawBytes(UUID uuid) {
    String id = uuid.toString();
    byte[] rawBytes = new byte[16];

    for (int i = 0, j = 0; i < 36; ++j) {
      // Need to bypass hyphens:
      switch (i) {
        case 8:
        case 13:
        case 18:
        case 23:
          ++i;
          break;
        default:
          break;
      }
      char c = id.charAt(i);

      if (c >= '0' && c <= '9') {
        rawBytes[j] = (byte) ((c - '0') << 4);
      } else if (c >= 'a' && c <= 'f') {
        rawBytes[j] = (byte) ((c - 'a' + 10) << 4);
      }

      c = id.charAt(++i);

      if (c >= '0' && c <= '9') {
        rawBytes[j] |= (byte) (c - '0');
      } else if (c >= 'a' && c <= 'f') {
        rawBytes[j] |= (byte) (c - 'a' + 10);
      }
      ++i;
    }
    return rawBytes;
  }

}
