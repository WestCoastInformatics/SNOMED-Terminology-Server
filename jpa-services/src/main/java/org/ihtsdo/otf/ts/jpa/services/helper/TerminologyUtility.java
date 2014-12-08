/*
 * 
 */
package org.ihtsdo.otf.ts.jpa.services.helper;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.ihtsdo.otf.ts.jpa.services.MetadataServiceJpa;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.services.MetadataService;

/**
 * Loads and serves configuration.
 */
public class TerminologyUtility {

  /** The isa rels map. */
  public static Map<String, Set<String>> isaRelsMap = new HashMap<>();

  /**
   * Returns the hierarchical isa rels.
   *
   * @param terminology the terminology
   * @param version the version
   * @return the hierarchical isa rels
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
  public static boolean isHierarchicalIsaRel(Relationship relationship) throws Exception{
    Set<String> isaRels =
        cacheIsaRels(relationship.getTerminology(),
            relationship.getTerminologyVersion());
    return relationship != null && isaRels.contains(relationship.getTypeId());
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
