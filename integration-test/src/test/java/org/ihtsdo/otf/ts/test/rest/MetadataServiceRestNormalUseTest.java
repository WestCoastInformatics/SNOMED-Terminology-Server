package org.ihtsdo.otf.ts.test.rest;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.KeyValuePair;
import org.ihtsdo.otf.ts.helpers.KeyValuePairList;
import org.ihtsdo.otf.ts.helpers.KeyValuePairLists;
import org.ihtsdo.otf.ts.services.MetadataService.MetadataKeys;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Implementation of the "Metadata Service REST Normal Use" Test Cases.
 */
public class MetadataServiceRestNormalUseTest extends MetadataServiceRestTest {

  /**  The auth token. */
  private static String authToken;

  /**
   * Create test fixtures per test.
   *
   * @throws Exception the exception
   */
  @Override
  @Before
  public void setup() throws Exception {

    // authentication
    authToken = securityService.authenticate(testUser, testPassword);

  }

  /**
   * Test retrieval of all terminology/version pairs.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @Test
  public void testNormalUseRestMetadata001() throws Exception {
    KeyValuePairLists keyValuePairLists =
        metadataService.getAllTerminologiesVersions(authToken);

    // flags for whether SNOMEDCT and ICD9CM were found
    boolean foundSnomedct = false;
    boolean foundIcd9cm = false;

    for (KeyValuePairList keyValuePairList : keyValuePairLists
        .getKeyValuePairLists()) {
      for (KeyValuePair keyValuePair : keyValuePairList.getKeyValuePairList()) {

        System.out.println(keyValuePair.toString());
        // test versions
        switch (keyValuePair.getKey()) {
          case "SNOMEDCT":
            foundSnomedct = true;
            assertTrue(keyValuePair.getValue().equals("latest"));
            break;
          case "ICD9CM":
            foundIcd9cm = true;
            assertTrue(keyValuePair.getValue().equals("2013"));
            break;
          default:
            // ignore other terminologies, only two above are assumed
            break;
        }
      }
    }

    // test that both were found
    assertTrue(foundSnomedct && foundIcd9cm);
  }

  /**
   * Tests retrieval of all terminology and latest version pairs NOTE: Test is
   * identical to testNormalUseRestMetadata001 but uses different API call.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @Test
  public void testNormalUseRestMetadata002() throws Exception {

    // flags for whether SNOMEDCT and ICD9CM were found
    boolean foundSnomedct = false;
    boolean foundIcd9cm = false;

    // make the call
    KeyValuePairList keyValuePairList =
        metadataService.getAllTerminologiesLatestVersions(authToken);

    // cycle over each pair in list
    for (KeyValuePair keyValuePair : keyValuePairList.getKeyValuePairList()) {
      System.out.println(keyValuePair.toString());
      // test versions
      switch (keyValuePair.getKey()) {
        case "SNOMEDCT":
          foundSnomedct = true;
          assertTrue(keyValuePair.getValue().equals("latest"));
          break;
        case "ICD9CM":
          foundIcd9cm = true;
          assertTrue(keyValuePair.getValue().equals("2013"));
          break;
        default:
          // ignore other terminologies, only two above are assumed
          break;
      }
    }

    assertTrue(foundSnomedct && foundIcd9cm);
  }

  /**
   * Test retrieving all metadata for a terminology.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseRestMetadata003() throws Exception {

    Logger.getLogger(MetadataServiceRestNormalUseTest.class).info(
        "Running testNormalUseRestMetadata003");

    // test SNOMED metadata
    assertTrue(testSnomedMetadata(metadataService.getAllMetadata("SNOMEDCT",
        authToken)));

    // test ICD9CM metadata
    assertTrue(testIcd9Metadata(metadataService.getAllMetadata("ICD9CM",
        authToken)));
  }

  /**
   * Test retrieving all metadata for latest version of a terminology.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormalUseRestMetadata004() throws Exception {

    Logger.getLogger(MetadataServiceRestNormalUseTest.class).info(
        "Running testNormalUseRestMetadata004");

    // test SNOMED metadata
    assertTrue(testSnomedMetadata(metadataService.getAllMetadata("SNOMEDCT",
        authToken)));

    // test ICD9CM metadata
    assertTrue(testIcd9Metadata(metadataService.getAllMetadata("ICD9CM",
        authToken)));
  }

  /**
   * Teardown.
   *
   * @throws Exception the exception
   */
  @Override
  @After
  public void teardown() throws Exception {

    // logout
    securityService.logout(authToken);
  }

  /**
   * Helper function used by cases 003 and 004.
   *
   * @param keyValuePairLists the key value pair lists
   * @return true, if successful
   * @throws Exception the exception
   */
  @SuppressWarnings("null")
  private boolean testSnomedMetadata(KeyValuePairLists keyValuePairLists)
    throws Exception {

    /**
     * Three checks: (1) Metadata matches hard-coded values taken from SNOMEDCT
     * browser (2) Non-group rel types and hierarchical rel types are
     * relationship types (3) Stated and inferred characteristic types are
     * characteristic types
     */

    Logger.getLogger(this.getClass()).info(
        "Testing SNOMEDCT metadata retrieval, " + keyValuePairLists.getCount()
            + " pair lists found (" + MetadataKeys.values().length
            + " expected)");

    // the count of categories successfully passing test
    int categorySuccessCt = 0;

    KeyValuePairList relTypes = null;
    KeyValuePairList charTypes = null;
    // retrieve relationship types and characteristic types for ease of access
    for (KeyValuePairList keyValuePairList : keyValuePairLists
        .getKeyValuePairLists()) {
      if (MetadataKeys.valueOf(keyValuePairList.getName()).equals(
          MetadataKeys.Relationship_Types))
        relTypes = keyValuePairList;
      else if (MetadataKeys.valueOf(keyValuePairList.getName()).equals(
          MetadataKeys.Characteristic_Types))
        charTypes = keyValuePairList;
    }

    Logger.getLogger(this.getClass()).info(
        "Retrieved relationship types and characteristic types for testing");
    System.out.println(relTypes);
    System.out.println(charTypes);

    // cycle over all retrieved metadata
    for (KeyValuePairList keyValuePairList : keyValuePairLists
        .getKeyValuePairLists()) {

      // initialize the test variables
      int expectedSize = -1;
      String expectedId = null;
      Set<String> expectedNames = new HashSet<>();
      Set<KeyValuePair> pairsNotMatched = new HashSet<>();

      Logger.getLogger(this.getClass()).info(
          "Checking " + keyValuePairList.getKeyValuePairList().size() + " "
              + keyValuePairList.getName());

      // for each type of metadata category, specify:
      // (1) the expected number of concepts returned
      // (2) the id and all possible names of a single concept expected to be in
      // the list
      switch (MetadataKeys.valueOf(keyValuePairList.getName())) {
        case Attribute_Descriptions:
          // NOTE: As of 3/1/15, 119 in current release (SNOMEDCT browser), 99
          // in test data
          expectedSize = 99;
          expectedId = "900000000000461009";
          expectedNames
              .add("Concept type component (foundation metadata concept)");
          expectedNames.add("Concept type component");
          break;
        case Attribute_Types:
          // in current SNOMEDCT metadata as of 3/1/2015, but not in test data
          // 707000009 SNOMED CT parsable string (foundation metadata concept)
          expectedSize = 19;
          expectedId = "900000000000464001";
          expectedNames
              .add("Reference set member type component (foundation metadata concept)");
          expectedNames.add("Reference set member type component");
          break;
        case Attribute_Value_Refsets:
          expectedSize = 5;
          expectedId = "900000000000489007";
          expectedNames
              .add("Concept inactivation indicator attribute value reference set (foundation metadata concept)");
          expectedNames
              .add("Concept inactivation indicator attribute value reference set");
          expectedNames.add("Concept inactivation indicator reference set");
          break;
        case Case_Significances:
          expectedSize = 3;
          expectedId = "900000000000020002";
          expectedNames
              .add("Only initial character case insensitive (core metadata concept)");
          expectedNames.add("Initial character case insensitive");
          expectedNames.add("Only initial character case insensitive");
          break;
        case Characteristic_Types:

          expectedSize = 5;
          expectedId = "900000000000011006";
          expectedNames.add("Inferred relationship (core metadata concept)");
          expectedNames.add("Inferred relationship");

          break;
        case Complex_Map_Refsets:
          expectedSize = 3;
          expectedId = "447563008";
          expectedNames
              .add("ICD-9-CM equivalence complex map reference set (foundation metadata concept)");
          expectedNames.add("ICD-9-CM equivalence complex map reference set");
          break;
        case Definition_Statuses:
          expectedSize = 2;
          expectedId = "900000000000073002";
          expectedNames
              .add("Sufficiently defined concept definition status (core metadata concept)");
          expectedNames.add("Defined");
          expectedNames.add("Necessary and sufficient definition");
          expectedNames.add("Sufficiently defined concept definition status");
          break;
        case Description_Formats:
          expectedSize = 4;
          expectedId = "900000000000541001";

          expectedNames
              .add("Limited HyperText Markup Language (foundation metadata concept)");
          expectedNames.add("Limited HTML");
          expectedNames.add("Limited HyperText Markup Language");
          break;
        case Description_Type_Refsets:
          expectedSize = 1;
          expectedId = "900000000000538005";
          expectedNames
              .add("Description format reference set (foundation metadata concept)");
          expectedNames.add("Description format");
          expectedNames.add("Description format reference set");
          break;
        case Description_Types:
          expectedSize = 3;
          expectedId = "900000000000550004";
          expectedNames.add("Definition (core metadata concept)");
          expectedNames.add("Definition");
          break;
        case Hierarchical_Relationship_Types:
          expectedSize = 1;
          expectedId = "116680003";
          expectedNames.add("Is a (attribute)");
          expectedNames.add("Is a");

          // if all values not in the relationship type descendant list,
          // decrement success counter
          // if all values not in the relationship type descendant list,
          // decrement success counter
          for (KeyValuePair pair : keyValuePairList.getKeyValuePairList()) {
            if (relTypes == null || !relTypes.contains(pair)) {
              pairsNotMatched.add(pair);
            }
          }

          if (pairsNotMatched.size() > 0) {
            Logger
                .getLogger(this.getClass())
                .error(
                    "The following hierarchical relationship types are not found in the set of relationship types:");
            for (KeyValuePair pair : pairsNotMatched) {
              Logger.getLogger(this.getClass()).error(
                  "  " + pair.getKey() + ", " + pair.getValue());
            }
          }
          break;
        case Language_Refsets:
          expectedSize = 4;
          expectedId = "900000000000507009";
          expectedNames
              .add("English [International Organization for Standardization 639-1 code en] language reference set (foundation metadata concept)");
          expectedNames.add("English");
          expectedNames
              .add("English [International Organization for Standardization 639-1 code en] language reference set");
          break;
        case Map_Relations:
          expectedSize = 14;
          expectedId = "447559001";
          expectedNames
              .add("Broad to narrow map from SNOMED CT source code to target code (foundation metadata concept)");
          expectedNames
              .add("Broad to narrow map from SNOMED CT source code to target code");
          break;
        case Module_Dependency_Refsets:
          expectedSize = 1;
          expectedId = "900000000000534007";
          expectedNames
              .add("Module dependency reference set (foundation metadata concept)");
          expectedNames.add("Module dependency");
          expectedNames.add("Module dependency reference set");
          break;
        case Modules:
          expectedSize = 6;
          expectedId = "900000000000012004";
          expectedNames
              .add("SNOMED CT model component module (core metadata concept)");
          expectedNames.add("SNOMED CT model component");
          expectedNames.add("SNOMED CT model component module");
          break;
        case Non_Grouping_Relationship_Types:
          // NOTE: These are hardcoded in the SnomedMetadataServiceJpaHelper
          expectedSize = 4;
          expectedId = "272741003";

          expectedNames.add("Laterality (attribute)");
          expectedNames.add("Laterality");

          // if all values not in the relationship type descendant list,
          // decrement success counter
          for (KeyValuePair pair : keyValuePairList.getKeyValuePairList()) {
            if (relTypes == null || !relTypes.contains(pair)) {
              pairsNotMatched.add(pair);
            }
          }

          if (pairsNotMatched.size() > 0) {
            Logger
                .getLogger(this.getClass())
                .error(
                    "The following non-grouping relationship types are not found in the set of relationship types:");
            for (KeyValuePair pair : pairsNotMatched) {
              Logger.getLogger(this.getClass()).error(
                  "  " + pair.getKey() + ", " + pair.getValue());
            }
          }
          break;
        case Refset_Descriptor_Refsets:
          expectedSize = 1;
          expectedId = "900000000000456007";
          expectedNames
              .add("Reference set descriptor reference set (foundation metadata concept)");
          expectedNames.add("Reference set descriptor");
          expectedNames.add("Reference set descriptor reference set");
          break;

        case Relationship_Modifiers:
          expectedSize = 2;
          expectedId = "900000000000451002";
          expectedNames
              .add("Existential restriction modifier (core metadata concept)");
          expectedNames.add("Existential restriction modifier");
          expectedNames.add("Some");
          break;
        case Relationship_Types:
          expectedSize = 63;
          expectedId = "260507000";
          expectedNames.add("Access (attribute)");
          expectedNames.add("Access");
          break;
        case Simple_Map_Refsets:
          // NOTE: Extra concept exists in SNOMEDCT current version, as of
          // 3/1/2015, 467614008, GMDN simple map reference set (foundation
          // metadata concept)
          expectedSize = 3;
          expectedId = "446608001";
          expectedNames
              .add("ICD-O simple map reference set (foundation metadata concept)");
          expectedNames.add("ICD-O simple map reference set");
          break;
        case Simple_Refsets:
          expectedSize = 23;
          expectedId = "447565001";
          expectedNames
              .add("Virtual therapeutic moiety simple reference set (foundation metadata concept)");
          expectedNames.add("Virtual therapeutic moiety simple reference set");
          break;
        case Inferred_Characteristic_Types:
          expectedSize = 1;
          expectedId = "900000000000011006";
          expectedNames.add("Inferred relationship (core metadata concept)");
          expectedNames.add("Inferred relationship");

          // if all values not in the characteristic type descendant list,
          // decrement success counter
          pairsNotMatched = new HashSet<>();
          for (KeyValuePair pair : keyValuePairList.getKeyValuePairList()) {
            if (charTypes == null || !charTypes.contains(pair)) {
              pairsNotMatched.add(pair);
            }
          }

          if (pairsNotMatched.size() > 0) {
            categorySuccessCt--;
            Logger
                .getLogger(this.getClass())
                .error(
                    "The following inferred characteristic types are not found in the set of characteristic types:");
            for (KeyValuePair pair : pairsNotMatched) {
              Logger.getLogger(this.getClass()).error(
                  "  " + pair.getKey() + ", " + pair.getValue());
            }
          }
          break;
        case Stated_Characteristic_Types:
          expectedSize = 1;
          expectedId = "900000000000010007";
          expectedNames.add("Stated relationship (core metadata concept)");
          expectedNames.add("Stated relationship");

          // if all values not in the characteristic type descendant list,
          // decrement success counter
          for (KeyValuePair pair : keyValuePairList.getKeyValuePairList()) {
            if (charTypes == null || !charTypes.contains(pair)) {
              pairsNotMatched.add(pair);
            }
          }

          if (pairsNotMatched.size() > 0) {
            Logger
                .getLogger(this.getClass())
                .error(
                    "The following stated characteristic types are not found in the set of characteristic types:");
            for (KeyValuePair pair : pairsNotMatched) {
              Logger.getLogger(this.getClass()).error(
                  "  " + pair.getKey() + ", " + pair.getValue());
            }
          }
          break;
        default:
          break;

      }
      List<KeyValuePair> pairs = keyValuePairList.getKeyValuePairList();
      KeyValuePair testCase = null;

      // if this case has been specified, check it
      if (expectedSize != -1 && pairs.size() != 0) {

        for (KeyValuePair pair : pairs) {
          if (expectedId.equals(pair.getKey())
              && expectedNames.contains(pair.getValue()))
            testCase = pair;
        }

        if (expectedSize != pairs.size()) {
          Logger.getLogger(this.getClass()).warn(
              "  Expected size " + expectedSize + " did not match actual size "
                  + pairs.size());
          Logger.getLogger(this.getClass()).info("  Retrieved pairs were: ");
          for (KeyValuePair pair : pairs) {
            Logger.getLogger(this.getClass()).info("    " + pair.toString());
          }
        }

        else if (testCase == null) {
          Logger.getLogger(this.getClass()).warn(
              "  Could not find pair for id = " + expectedId + ", names "
                  + expectedNames.toString());
          Logger.getLogger(this.getClass()).info("  Available pairs were: ");
          for (KeyValuePair pair : pairs) {
            Logger.getLogger(this.getClass()).info("    " + pair.toString());
          }
        } else {
          categorySuccessCt++;
        }
      }
    }

    Logger.getLogger(this.getClass()).info(
        "SNOMEDCT Metadata Categories Validated:  " + categorySuccessCt
            + " out of " + MetadataKeys.values().length);

    return categorySuccessCt == MetadataKeys.values().length;
  }

  /**
   * Test icd9 metadata.
   *
   * @param keyValuePairLists the key value pair lists
   * @return true, if successful
   * @throws Exception the exception
   */
  private boolean testIcd9Metadata(KeyValuePairLists keyValuePairLists)
    throws Exception {

    /**
     * Two components: - Metadata matches reported SNOMEDCT crap - Non-group rel
     * types and hierarchical rel types are relationship types - Stated and
     * inferred characteristic types are characteristic types
     */

    Logger.getLogger(this.getClass()).info(
        "Testing ICD9CM metadata retrieval, " + keyValuePairLists.getCount()
            + " pair lists found (" + MetadataKeys.values().length
            + " expected)");

    KeyValuePairList relTypes = null;
    KeyValuePairList charTypes = null;

    // retrieve relationship types and characteristic types for ease of access
    for (KeyValuePairList keyValuePairList : keyValuePairLists
        .getKeyValuePairLists()) {
      if (MetadataKeys.valueOf(keyValuePairList.getName()).equals(
          MetadataKeys.Relationship_Types))
        relTypes = keyValuePairList;
      else if (MetadataKeys.valueOf(keyValuePairList.getName()).equals(
          MetadataKeys.Characteristic_Types))
        charTypes = keyValuePairList;
    }

    // the count of categories successfully passing test
    int categorySuccessCt = 0;

    for (KeyValuePairList keyValuePairList : keyValuePairLists
        .getKeyValuePairLists()) {

      // initialize the test variables
      int expectedSize = -1;
      Set<KeyValuePair> pairsNotMatched = new HashSet<>();

      Logger.getLogger(this.getClass()).info(
          "Checking " + keyValuePairList.getKeyValuePairList().size() + " "
              + keyValuePairList.getName());

      switch (MetadataKeys.valueOf(keyValuePairList.getName())) {
        case Case_Significances:
          expectedSize = 1;
          break;
        case Characteristic_Types:
          expectedSize = 1;
          break;
        case Definition_Statuses:
          expectedSize = 1;
          break;
        case Description_Types:
          expectedSize = 13;
          break;
        case Hierarchical_Relationship_Types:
          expectedSize = 1;
          
       // if all values not in the characteristic type descendant list,
          // decrement success counter
          pairsNotMatched = new HashSet<>();
          for (KeyValuePair pair : keyValuePairList.getKeyValuePairList()) {
            if (relTypes == null || !relTypes.contains(pair)) {
              pairsNotMatched.add(pair);
            }
          }

          if (pairsNotMatched.size() > 0) {
            categorySuccessCt--;
            Logger
                .getLogger(this.getClass())
                .error(
                    "The following hierarchical relationship types are not found in the set of relationship types:");
            for (KeyValuePair pair : pairsNotMatched) {
              Logger.getLogger(this.getClass()).error(
                  "  " + pair.getKey() + ", " + pair.getValue());
            }
          }
          break;
        case Inferred_Characteristic_Types:
          expectedSize = 1;

          // if all values not in the characteristic type descendant list,
          // decrement success counter
          pairsNotMatched = new HashSet<>();
          for (KeyValuePair pair : keyValuePairList.getKeyValuePairList()) {
            if (charTypes == null || !charTypes.contains(pair)) {
              pairsNotMatched.add(pair);
            }
          }

          if (pairsNotMatched.size() > 0) {
            categorySuccessCt--;
            Logger
                .getLogger(this.getClass())
                .error(
                    "The following inferred characteristic types are not found in the set of characteristic types:");
            for (KeyValuePair pair : pairsNotMatched) {
              Logger.getLogger(this.getClass()).error(
                  "  " + pair.getKey() + ", " + pair.getValue());
            }
          }
          break;
        case Modules:
          expectedSize = 1;
          break;
        case Relationship_Modifiers:
          expectedSize = 1;
          break;
        case Relationship_Types:
          expectedSize = 4;
          break;
        case Simple_Refsets:
          expectedSize = 2;
          break;
        case Stated_Characteristic_Types:
          expectedSize = 1;

          // if all values not in the characteristic type descendant list,
          // decrement success counter
          pairsNotMatched = new HashSet<>();
          for (KeyValuePair pair : keyValuePairList.getKeyValuePairList()) {
            if (charTypes == null || !charTypes.contains(pair)) {
              pairsNotMatched.add(pair);
            }
          }

          if (pairsNotMatched.size() > 0) {
            categorySuccessCt--;
            Logger
                .getLogger(this.getClass())
                .error(
                    "The following stated characteristic types are not found in the set of characteristic types:");
            for (KeyValuePair pair : pairsNotMatched) {
              Logger.getLogger(this.getClass()).error(
                  "  " + pair.getKey() + ", " + pair.getValue());
            }
          }
          break;
        default:
          if (keyValuePairList.getKeyValuePairList().size() != 0)
            Logger.getLogger(this.getClass()).error(
                "Unexpected metadata type retrieved:  "
                    + keyValuePairList.getName());
          break;
      }

      List<KeyValuePair> pairs = keyValuePairList.getKeyValuePairList();

      // if pairs retrieved do not match expected size, output error
      if (pairs.size() != 0 && expectedSize != pairs.size()) {
        Logger.getLogger(this.getClass()).warn(
            "  Expected size " + expectedSize + " did not match actual size "
                + pairs.size());
        Logger.getLogger(this.getClass()).info("  Retrieved pairs were: ");
        for (KeyValuePair pair : pairs) {
          Logger.getLogger(this.getClass()).info("    " + pair.toString());
        }

        // if pairs retrieved match expected size, increment the counter
      } else if (pairs.size() != 0 && expectedSize == pairs.size()) {
        categorySuccessCt++;

        // if this case was not expected and no pairs retrieved, increment
        // counter
      } else if (pairs.size() == 0 && expectedSize == -1) {
        categorySuccessCt++;
      }
    }

    Logger.getLogger(this.getClass()).info(
        "ICD9CM  Metadata Categories Validated:  " + categorySuccessCt
            + " out of " + MetadataKeys.values().length);

    return categorySuccessCt == MetadataKeys.values().length;
  }

}
