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
 * Implementation of the "ClaML Load and Unload Test Case".
 */
public class ClamlLoadAndUnloadTest {

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
   * Run the Claml mojo against the sample config/src/resources/data/icd9cm-2013.xml" data.
   *   TEST: verify each content table exists with the expected number of entries.
   * Create a "ICD9CM" project (name="Sample Project" description="Sample project." terminology=ICD9CM version=2013 scope.concepts=001-999.99,E000-E999.9,V01-V91.99 scope.descendants.flag=true admin.user=admin)
   *   TEST: verify there is a project with the expected name
   * Start an editing cycle for "ICD9CM"
   *   TEST: verify there is a release info with the expected name and "planned" flag equal to true.
   * Remove the terminology "ICD9CM" with version "2013"
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
    Assert.assertEquals(
        service.getAllConcepts("ICD9CM", "2013").getCount(), 0);
    service.close();

    // Load ClaML
    request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/loader/pom.xml"));
    request.setProfiles(Arrays.asList("ClaML"));
    request.setGoals(Arrays.asList("clean", "install"));
    p = new Properties();
    p.setProperty("run.config.ts", System.getProperty("run.config.ts"));
    p.setProperty("server", server);
    p.setProperty("terminology", "ICD9CM");
    p.setProperty("version", "2013");
    p.setProperty("input.file",
        "../../config/src/main/resources/data/icd9cm-2013.xml");
    request.setProperties(p);
    invoker = new DefaultInvoker();
    result = invoker.execute(request);
    if (result.getExitCode() != 0) {
      throw result.getExecutionException();
    }

    // Verify expected contents
    service = new ContentServiceJpa();
    Assert.assertEquals(
        service.getAllConcepts("ICD9CM", "2013").getCount(), 17770);
    service.close();

    // Verify release info
    HistoryService historyService = new HistoryServiceJpa();
    Assert.assertNotNull(historyService.getReleaseInfo("ICD9CM", "2013"));
    historyService.close();

    // Add ICD9CM project
    request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/loader/pom.xml"));
    request.setProfiles(Arrays.asList("AddProject"));
    request.setGoals(Arrays.asList("clean", "install"));
    p = new Properties();
    p.setProperty("run.config.ts", System.getProperty("run.config.ts"));
    p.setProperty("server", server);
    p.setProperty("name", "Sample project.");
    p.setProperty("description", "Sample project.");
    p.setProperty("terminology", "ICD9CM");
    p.setProperty("version", "2013");
    p.setProperty("scope.concepts", "001-999.99,E000-E999.9,V01-V91.99");
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
          && project.getTerminology().equals("ICD9CM")
          && project.getTerminologyVersion().equals("2013")
          && project.getScopeConcepts().size() == 3) {
        found = true;
      }
    }
    Assert.assertTrue(found);
    projectService.close();

    // Verify admin user
    SecurityService securityService = new SecurityServiceJpa();
    Assert.assertNotNull(securityService.getUser("admin"));
    securityService.close();

    // Start ICD9CM editing cycle
    request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/release/pom.xml"));
    request.setProfiles(Arrays.asList("StartEditingCycle"));
    request.setGoals(Arrays.asList("clean", "install"));
    p = new Properties();
    p.setProperty("run.config.ts", System.getProperty("run.config.ts"));
    p.setProperty("server", server);
    p.setProperty("release.version", "20150101");
    p.setProperty("terminology", "ICD9CM");
    p.setProperty("version", "2013");
    request.setProperties(p);
    invoker = new DefaultInvoker();
    result = invoker.execute(request);
    if (result.getExitCode() != 0) {
      throw result.getExecutionException();
    }

    // Verify release info for 20160131 as "planned"
    // Verify release info
    historyService = new HistoryServiceJpa();
    Assert.assertNotNull(historyService.getReleaseInfo("ICD9CM", "20150101"));
    Assert.assertTrue(historyService.getReleaseInfo("ICD9CM", "20150101")
        .isPlanned());
    historyService.close();

    // Remove terminology
    request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/remover/pom.xml"));
    request.setProfiles(Arrays.asList("Terminology"));
    request.setGoals(Arrays.asList("clean", "install"));
    p = new Properties();
    p.setProperty("run.config.ts", System.getProperty("run.config.ts"));
    p.setProperty("server", server);
    p.setProperty("terminology", "ICD9CM");
    p.setProperty("version", "2013");
    request.setProperties(p);
    invoker = new DefaultInvoker();
    result = invoker.execute(request);
    if (result.getExitCode() != 0) {
      throw result.getExecutionException();
    }

    // Verify no contents
    service = new ContentServiceJpa();
    Assert.assertEquals(service.getAllConcepts("ICD9CM", "2013"), 0);

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
