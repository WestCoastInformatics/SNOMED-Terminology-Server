package org.ihtsdo.otf.ts.test.mojo;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.ihtsdo.otf.ts.Project;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.HistoryServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.ProjectServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.SecurityServiceJpa;
import org.ihtsdo.otf.ts.services.ContentService;
import org.ihtsdo.otf.ts.services.HistoryService;
import org.ihtsdo.otf.ts.services.ProjectService;
import org.ihtsdo.otf.ts.services.SecurityService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Implementation of the "RF2 Snapshot and Delta Load and Unload Test Case".
 */
public class Rf2SnapshotAndDeltaLoadAndUnloadTest {

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
   *   TEST: verify there is a concepts table with no contents
   * Run Reindex mojo to clear the indexes
   *   TEST: verify there is a ConceptJpa index with no contents.
   * Run the RF2-snapshot mojo against the sample config/src/resources/data/snomedct-20140731-mini" data.
   *   TEST: verify each content table exists with the expected number of entries.
   * Run the RF2-delta mojo against the sample config/src/resources/data/snomedct-20150131-delta" data.
   *   TEST: verify changes of each component type.
   * Create a "SNOMEDCT" project (name="Sample Project" description="Sample project." terminology=SNOMEDCT version=latest scope.concepts=138875005 scope.descendants.flag=true admin.user=admin)
   *   TEST: verify there is a project with the expected name
   * Start an editing cycle for "SNOMEDCT"
   *   TEST: verify there is a release info with the expected name and "planned" flag equal to true.
   * Remove the terminology "SNOMEDCT" with version "latest"
   *   TEST: verify there is a concepts table with no contents.
   * </pre>
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
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

    // Verify no contents
    ContentService service = new ContentServiceJpa();
    Assert.assertEquals(0, service.getAllConcepts("SNOMEDCT", "latest")
        .getCount());
    service.close();
    service.closeFactory();

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

    // Verify expected contents
    service = new ContentServiceJpa();
    Assert.assertEquals(10293, service.getAllConcepts("SNOMEDCT", "latest")
        .getCount());
    service.close();
    service.closeFactory();

    // Verify release info
    HistoryService historyService = new HistoryServiceJpa();
    Assert.assertNotNull(historyService.getReleaseInfo("SNOMEDCT", "20140731"));
    historyService.close();
    historyService.closeFactory();

    // Load RF2 delta
    request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/loader/pom.xml"));
    request.setProfiles(Arrays.asList("RF2-delta"));
    request.setGoals(Arrays.asList("clean", "install"));
    p = new Properties();
    p.setProperty("run.config.ts", System.getProperty("run.config.ts"));
    p.setProperty("server", server);
    p.setProperty("terminology", "SNOMEDCT");
    p.setProperty("version", "latest");
    p.setProperty("input.dir",
        "../../config/src/main/resources/data/snomedct-20150131-delta");
    request.setProperties(p);
    invoker = new DefaultInvoker();
    result = invoker.execute(request);
    if (result.getExitCode() != 0) {
      throw result.getExecutionException();
    }

    // Verify expected content (concept 99810001005 should exist and be active)
    service = new ContentServiceJpa();
    Assert.assertNotNull(service.getSingleConcept("99810001005", "SNOMEDCT",
        "latest"));
    Assert.assertTrue(service.getSingleConcept("99810001005", "SNOMEDCT",
        "latest").isActive());
    service.close();
    service.closeFactory();

    // Verify release info
    historyService = new HistoryServiceJpa();
    Assert.assertNotNull(historyService.getReleaseInfo("SNOMEDCT", "20150131"));
    Assert.assertTrue(historyService.getReleaseInfo("SNOMEDCT", "20150131")
        .isPublished());
    historyService.close();
    historyService.closeFactory();

    // Load RF2 delta
    request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/loader/pom.xml"));
    request.setProfiles(Arrays.asList("RF2-delta"));
    request.setGoals(Arrays.asList("clean", "install"));
    p = new Properties();
    p.setProperty("run.config.ts", System.getProperty("run.config.ts"));
    p.setProperty("server", server);
    p.setProperty("terminology", "SNOMEDCT");
    p.setProperty("version", "latest");
    p.setProperty("input.dir",
        "../../config/src/main/resources/data/snomedct-20150731-delta");
    request.setProperties(p);
    invoker = new DefaultInvoker();
    result = invoker.execute(request);
    if (result.getExitCode() != 0) {
      throw result.getExecutionException();
    }

    // Verify expected content (concept 99810001005 should exist and be active)
    service = new ContentServiceJpa();
    Assert.assertNotNull(service.getSingleConcept("99810001005", "SNOMEDCT",
        "latest"));
    Assert.assertFalse(service.getSingleConcept("99810001005", "SNOMEDCT",
        "latest").isActive());
    service.close();
    service.closeFactory();

    // Verify release info
    historyService = new HistoryServiceJpa();
    Assert.assertNotNull(historyService.getReleaseInfo("SNOMEDCT", "20150731"));
    Assert.assertTrue(historyService.getReleaseInfo("SNOMEDCT", "20150731")
        .isPublished());
    historyService.close();
    historyService.closeFactory();

    // Add a SNOMEDCT project
    request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/loader/pom.xml"));
    request.setProfiles(Arrays.asList("Project"));
    request.setGoals(Arrays.asList("clean", "install"));
    p = new Properties();
    p.setProperty("run.config.ts", System.getProperty("run.config.ts"));
    p.setProperty("server", server);
    p.setProperty("name", "Sample project.");
    p.setProperty("description", "Sample project.");
    p.setProperty("terminology", "SNOMEDCT");
    p.setProperty("version", "latest");
    p.setProperty("scope.concepts", "138875005");
    p.setProperty("scope.descendants.flag", "true");
    p.setProperty("admin.user", "admin");
    request.setProperties(p);
    invoker = new DefaultInvoker();
    result = invoker.execute(request);
    if (result.getExitCode() != 0) {
      throw result.getExecutionException();
    }

    // Verify project exists
    ProjectService projectService = new ProjectServiceJpa();
    boolean found = false;
    for (Project project : projectService.getProjects().getObjects()) {
      if (project.getName().equals("Sample project.")
          && project.getDescription().equals("Sample project.")
          && project.getScopeDescendantsFlag()
          && project.getTerminology().equals("SNOMEDCT")
          && project.getTerminologyVersion().equals("latest")
          && project.getScopeConcepts().iterator().next().equals("138875005")) {
        found = true;
      }
    }
    Assert.assertTrue(found);
    projectService.close();
    projectService.closeFactory();

    // Verify admin user
    SecurityService securityService = new SecurityServiceJpa();
    Assert.assertNotNull(securityService.getUser("admin"));
    securityService.close();
    securityService.closeFactory();

    // Start SNOMEDCT editing cycle

    // Add a SNOMEDCT project
    request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/release/pom.xml"));
    request.setProfiles(Arrays.asList("StartEditingCycle"));
    request.setGoals(Arrays.asList("clean", "install"));
    p = new Properties();
    p.setProperty("run.config.ts", System.getProperty("run.config.ts"));
    p.setProperty("server", server);
    p.setProperty("release.version", "20160131");
    p.setProperty("terminology", "SNOMEDCT");
    p.setProperty("version", "latest");
    request.setProperties(p);
    invoker = new DefaultInvoker();
    result = invoker.execute(request);
    if (result.getExitCode() != 0) {
      throw result.getExecutionException();
    }

    // Verify release info for 20160131 as "planned"
    // Verify release info
    historyService = new HistoryServiceJpa();
    Assert.assertNotNull(historyService.getReleaseInfo("SNOMEDCT", "20160131"));
    Assert.assertFalse(historyService.getReleaseInfo("SNOMEDCT", "20160131")
        .isPublished());
    Assert.assertTrue(historyService.getReleaseInfo("SNOMEDCT", "20160131")
        .isPlanned());
    historyService.close();
    historyService.closeFactory();

    // Remove terminology
    request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/remover/pom.xml"));
    request.setProfiles(Arrays.asList("Terminology"));
    request.setGoals(Arrays.asList("clean", "install"));
    p = new Properties();
    p.setProperty("run.config.ts", System.getProperty("run.config.ts"));
    p.setProperty("server", server);
    p.setProperty("terminology", "SNOMEDCT");
    p.setProperty("version", "latest");
    request.setProperties(p);
    invoker = new DefaultInvoker();
    result = invoker.execute(request);
    if (result.getExitCode() != 0) {
      throw result.getExecutionException();
    }

    // Verify no contents
    service = new ContentServiceJpa();
    Assert.assertEquals(0, service.getAllConcepts("SNOMEDCT", "latest")
        .getCount());
    service.close();
    service.closeFactory();
    
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
