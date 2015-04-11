/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.test.other;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.ValidationResult;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.jpa.client.ContentClientRest;
import org.ihtsdo.otf.ts.jpa.client.SecurityClientRest;
import org.ihtsdo.otf.ts.jpa.client.ValidationClientRest;
import org.ihtsdo.otf.ts.jpa.services.helper.TerminologyUtility;
import org.ihtsdo.otf.ts.jpa.services.validation.NewConceptMinRequirementsCheck;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.services.helpers.ConceptReportHelper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for REST content service.
 */
public class ValidationServiceTest {

  /** The client. */
  private static ValidationClientRest client;

  /** The client. */
  private static ContentClientRest contentClient;

  /** The auth token. */
  private static String authToken;

  /**
   * Setup.
   * @throws Exception
   */
  @SuppressWarnings("static-method")
  @Before
  public void setup() throws Exception {
    if (client == null) {
      client =
          new ValidationClientRest(ConfigUtility.getConfigProperties());
      contentClient =
          new ContentClientRest(ConfigUtility.getConfigProperties());
      SecurityClientRest securityClient =
          new SecurityClientRest(ConfigUtility.getConfigProperties());
      authToken = securityClient.authenticate("guest", "guest");
    }

  }

  /**
   * Test validating {@link NewConceptMinRequirementsCheck}.
   * @throws Exception
   */
  @Test
  public void testNewConceptMinRequirementsCheck() throws Exception {
    Logger.getLogger(getClass()).info(
        "TEST - 10013000, SNOMEDCT, latest, " + authToken);

    // Test that this concept is valid
    ConceptJpa concept =
        (ConceptJpa) contentClient.getSingleConcept("10013000", "SNOMEDCT",
            "latest", authToken);
    Logger.getLogger(getClass()).info(
        ConceptReportHelper.getConceptReport(concept));
    ValidationResult result = client.validateConcept(concept, authToken);
    Assert.assertTrue(result.isValid());

    // Remove SY description
    Description sy = null;
    for (Description d : concept.getDescriptions()) {
      Assert.assertTrue(concept.getDescriptions().contains(d));
      if (d.getTerm().equals("Lateral meniscus of knee joint")) {
        sy = d;
        break;
      }
    }
    Assert.assertNotNull(sy);
    for (Description d : concept.getDescriptions()) {
      Assert.assertTrue(concept.getDescriptions().contains(d));
      if (d.getTerm().equals("Lateral meniscus of knee joint")) {
        Assert.assertEquals(sy, d);
        Assert.assertEquals(sy.hashCode(), d.hashCode());
      }
    }
    concept.removeDescription(sy);
    Assert.assertEquals(concept.getDescriptions().size(),2);

    // Validation should pass
    Logger.getLogger(getClass()).info(
        ConceptReportHelper.getConceptReport(concept));
    result = client.validateConcept(concept, authToken);
    Assert.assertTrue(result.isValid());

    // Remove PT description
    Description pt = null;
    for (Description d : concept.getDescriptions()) {
      if (d.getTerm().equals(concept.getDefaultPreferredName())) {
        pt = d;
        break;
      }
    }
    Assert.assertNotNull(pt);
    concept.removeDescription(pt);
    Assert.assertEquals(concept.getDescriptions().size(),1);

    // Validation should fail
    Logger.getLogger(getClass()).info(
        ConceptReportHelper.getConceptReport(concept));
    result = client.validateConcept(concept, authToken);
    Logger.getLogger(getClass()).info(result.toString());
    Assert.assertFalse(result.isValid());

    // Add PT back and remove FN
    concept.addDescription(pt);
    Description fn = null;
    for (Description d : concept.getDescriptions()) {
      if (d.getTerm().equals("Lateral meniscus structure (body structure)")) {
        fn = d;
        break;
      }
    }
    Assert.assertNotNull(fn);
    concept.removeDescription(fn);
    Assert.assertEquals(concept.getDescriptions().size(),1);

    // Validation should fail
    Logger.getLogger(getClass()).info(
        ConceptReportHelper.getConceptReport(concept));
    result = client.validateConcept(concept, authToken);
    Assert.assertFalse(result.isValid());

    // Add FN back
    concept.addDescription(fn);

    // Remove isa relationships
    Set<Relationship> relationships = new HashSet<>(concept.getRelationships());
    for (Relationship relationship : relationships) {
      if (TerminologyUtility.isHierarchicalIsaRelationship(relationship)) {
        concept.removeRelationship(relationship);
      }
    }
    // Validation should pass
    Logger.getLogger(getClass()).info(
        ConceptReportHelper.getConceptReport(concept));
    result = client.validateConcept(concept, authToken);
    Assert.assertFalse(result.isValid());

  }

  /**
   * Teardown.
   */
  @After
  public void teardown() {
    // do nothing
  }
}
