package org.ihtsdo.otf.ts.test.mojo;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.services.ContentService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Implementation of the "Compare RF2 Full and RF2 Snapshot Loads Test Case".
 */
public class CompareRf2FullRf2SnapshotLoadersTest {

  /** The properties. */
  static Properties config;

  /** The server. */
  static String server = "false";

  /**
   * Create test fixtures for class.
   *
   * @throws Exception the exception
   */
  @BeforeClass
  public static void setupClass() throws Exception {
    config = ConfigUtility.getConfigProperties();
    if (ConfigUtility.isServerActive()) {
      server = "true";
    }
  }

  /**
   * Test the sequence:
   * 
   * <pre>
   * Run Updatedb mojo in "create" mode to clear the database
   * Run Reindex mojo to clear the indexes
   * Run the RF2-full mojo against the sample config/src/main/resources/data/snomedct-20140731-minif" data.
   * Count all data structures (though API) and save data
   * Run Updatedb mojo in "create" mode to clear the database
   * Run Reindex mojo to clear the indexes
   * Run the RF2-apshot mojo against the sample config/src/main/rresources/data/snomedct-20140731-mini" data.
   * Count all data structures (though API) and save data
   *   TEST: compare the full and shapshot model object counts, they should all be equals.
   *   TEST: verify each content table exists with the expected number of entries.
   * </pre>
   * @throws Exception the exception
   */
  @Test
  public void test() throws Exception {

    // Createdb
    InvocationRequest request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/db/pom.xml"));
    request.setProfiles(Arrays.asList("Createdb"));
    request.setGoals(Arrays.asList("clean", "install"));
    Properties p = new Properties();
    p.setProperty("run.config.ts", System.getProperty("run.config.ts"));
    p.setProperty("server", server);
    request.setProperties(p);
    DefaultInvoker invoker = new DefaultInvoker();
    InvocationResult result = invoker.execute(request);
    if (result.getExitCode() != 0) {
      throw result.getExecutionException();
    }

    // Reindex
    request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/lucene/pom.xml"));
    request.setProfiles(Arrays.asList("Reindex"));
    request.setGoals(Arrays.asList("clean", "install"));
    p = new Properties();
    p.setProperty("run.config.ts", System.getProperty("run.config.ts"));
    p.setProperty("server", server);
    request.setProperties(p);
    invoker = new DefaultInvoker();
    result = invoker.execute(request);
    if (result.getExitCode() != 0) {
      throw result.getExecutionException();
    }

    // Load RF2 full
    request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/loader/pom.xml"));
    request.setProfiles(Arrays.asList("RF2-full"));
    request.setGoals(Arrays.asList("clean", "install"));
    p = new Properties();
    p.setProperty("run.config.ts", System.getProperty("run.config.ts"));
    p.setProperty("server", server);
    p.setProperty("terminology", "SNOMEDCT");
    p.setProperty("version", "latest");
    p.setProperty("input.dir",
        "../../config/src/main/resources/data/snomedct-20140731-minif");
    request.setProperties(p);
    invoker = new DefaultInvoker();
    result = invoker.execute(request);
    if (result.getExitCode() != 0) {
      throw result.getExecutionException();
    }

    // count data
    ContentService service = new ContentServiceJpa();
    Map<String, Integer> fullStats = new HashMap<>();
    fullStats.put("concept", service.getAllConcepts("SNOMEDCT", "latest")
        .getCount());
    fullStats.put("descrpition",
        service.getAllDescriptionTerminologyIds("SNOMEDCT", "latest")
            .getCount());
    fullStats.put("relationship",
        service.getAllRelationshipTerminologyIds("SNOMEDCT", "latest")
            .getCount());
    fullStats.put("language",
        service.getAllLanguageRefSetMemberTerminologyIds("SNOMEDCT", "latest")
            .getCount());
    fullStats.put("attributeValue", service
        .getAllAttributeValueRefSetMemberTerminologyIds("SNOMEDCT", "latest")
        .getCount());
    fullStats.put(
        "associationReference",
        service.getAllAssociationReferenceRefSetMemberTerminologyIds(
            "SNOMEDCT", "latest").getCount());
    fullStats.put("complexMap", service
        .getAllComplexMapRefSetMemberTerminologyIds("SNOMEDCT", "latest")
        .getCount());
    fullStats.put("descriptionType", service
        .getAllDescriptionTypeRefSetMemberTerminologyIds("SNOMEDCT", "latest")
        .getCount());
    fullStats.put("moduleDependency", service
        .getAllModuleDependencyRefSetMemberTerminologyIds("SNOMEDCT", "latest")
        .getCount());
    fullStats.put("refsetDescriptor", service
        .getAllRefsetDescriptorRefSetMemberTerminologyIds("SNOMEDCT", "latest")
        .getCount());
    fullStats.put("simple",
        service.getAllSimpleRefSetMemberTerminologyIds("SNOMEDCT", "latest")
            .getCount());
    fullStats.put("simpleMap", service
        .getAllSimpleMapRefSetMemberTerminologyIds("SNOMEDCT", "latest")
        .getCount());
    service.close();
    service.closeFactory();
    Logger.getLogger(getClass()).info("Full Stats = " + fullStats);

    // Createdb
    request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/db/pom.xml"));
    request.setProfiles(Arrays.asList("Createdb"));
    request.setGoals(Arrays.asList("clean", "install"));
    p = new Properties();
    p.setProperty("run.config.ts", System.getProperty("run.config.ts"));
    p.setProperty("server", server);
    request.setProperties(p);
    invoker = new DefaultInvoker();
    result = invoker.execute(request);
    if (result.getExitCode() != 0) {
      throw result.getExecutionException();
    }

    // Reindex
    request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/lucene/pom.xml"));
    request.setProfiles(Arrays.asList("Reindex"));
    request.setGoals(Arrays.asList("clean", "install"));
    p = new Properties();
    p.setProperty("run.config.ts", System.getProperty("run.config.ts"));
    p.setProperty("server", server);
    request.setProperties(p);
    invoker = new DefaultInvoker();
    result = invoker.execute(request);
    if (result.getExitCode() != 0) {
      throw result.getExecutionException();
    }

    // Load RF2 snapshot
    request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/loader/pom.xml"));
    request.setProfiles(Arrays.asList("RF2-snapshot"));
    request.setGoals(Arrays.asList("clean", "install"));
    p = new Properties();
    p.setProperty("run.config.ts", System.getProperty("run.config.ts"));
    p.setProperty("server", server);
    p.setProperty("terminology", "SNOMEDCT");
    p.setProperty("version", "latest");
    p.setProperty("input.dir",
        "../../config/src/main/resources/data/snomedct-20140731-mini");
    request.setProperties(p);
    invoker = new DefaultInvoker();
    result = invoker.execute(request);
    if (result.getExitCode() != 0) {
      throw result.getExecutionException();
    }

    // count data
    service = new ContentServiceJpa();
    Map<String, Integer> snapStats = new HashMap<>();
    snapStats.put("concept", service.getAllConcepts("SNOMEDCT", "latest")
        .getCount());
    snapStats.put("descrpition",
        service.getAllDescriptionTerminologyIds("SNOMEDCT", "latest")
            .getCount());
    snapStats.put("relationship",
        service.getAllRelationshipTerminologyIds("SNOMEDCT", "latest")
            .getCount());
    snapStats.put("language",
        service.getAllLanguageRefSetMemberTerminologyIds("SNOMEDCT", "latest")
            .getCount());
    snapStats.put("attributeValue", service
        .getAllAttributeValueRefSetMemberTerminologyIds("SNOMEDCT", "latest")
        .getCount());
    snapStats.put(
        "associationReference",
        service.getAllAssociationReferenceRefSetMemberTerminologyIds(
            "SNOMEDCT", "latest").getCount());
    snapStats.put("complexMap", service
        .getAllComplexMapRefSetMemberTerminologyIds("SNOMEDCT", "latest")
        .getCount());
    snapStats.put("descriptionType", service
        .getAllDescriptionTypeRefSetMemberTerminologyIds("SNOMEDCT", "latest")
        .getCount());
    snapStats.put("moduleDependency", service
        .getAllModuleDependencyRefSetMemberTerminologyIds("SNOMEDCT", "latest")
        .getCount());
    snapStats.put("refsetDescriptor", service
        .getAllRefsetDescriptorRefSetMemberTerminologyIds("SNOMEDCT", "latest")
        .getCount());
    snapStats.put("simple",
        service.getAllSimpleRefSetMemberTerminologyIds("SNOMEDCT", "latest")
            .getCount());
    snapStats.put("simpleMap", service
        .getAllSimpleMapRefSetMemberTerminologyIds("SNOMEDCT", "latest")
        .getCount());
    service.close();
    service.closeFactory();
    Logger.getLogger(getClass()).info("Snap Stats = " + fullStats);

    // Assert equivalence of counts
    Assert.assertEquals(fullStats.get("concept"), snapStats.get("concept"));
    Assert.assertEquals(fullStats.get("description"),
        snapStats.get("description"));
    Assert.assertEquals(fullStats.get("relationship"),
        snapStats.get("relationship"));
    Assert.assertEquals(fullStats.get("language"), snapStats.get("language"));

    // Finish by clearing the DB again
    request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/db/pom.xml"));
    request.setProfiles(Arrays.asList("Createdb"));
    request.setGoals(Arrays.asList("clean", "install"));
    p = new Properties();
    p.setProperty("run.config.ts", System.getProperty("run.config.ts"));
    p.setProperty("server", server);
    request.setProperties(p);
    invoker = new DefaultInvoker();
    result = invoker.execute(request);
    if (result.getExitCode() != 0) {
      throw result.getExecutionException();
    }
  }

  /**
   * Create test fixtures per test.
   *
   * @throws Exception the exception
   */
  @Before
  public void setup() throws Exception {
    // n/a
  }

  /**
   * Teardown.
   *
   * @throws Exception the exception
   */
  @After
  public void teardown() throws Exception {
    // n/a
  }

  /**
   * Teardown class.
   *
   * @throws Exception the exception
   */
  @AfterClass
  public static void teardownClass() throws Exception {
    // n/a
  }

}
