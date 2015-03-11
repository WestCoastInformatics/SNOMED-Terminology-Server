package org.ihtsdo.otf.ts.test.other;

import java.util.Date;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.ReleaseInfo;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.DescriptionList;
import org.ihtsdo.otf.ts.helpers.LanguageRefSetMemberList;
import org.ihtsdo.otf.ts.helpers.PfsParameterJpa;
import org.ihtsdo.otf.ts.helpers.RelationshipList;
import org.ihtsdo.otf.ts.helpers.ReleaseInfoList;
import org.ihtsdo.otf.ts.jpa.ReleaseInfoJpa;
import org.ihtsdo.otf.ts.jpa.client.HistoryClientRest;
import org.ihtsdo.otf.ts.jpa.client.SecurityClientRest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for REST content service.
 */
public class HistoryServiceTest {

  /** The client. */
  private static HistoryClientRest client;

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
      client = new HistoryClientRest(ConfigUtility.getConfigProperties());
      SecurityClientRest securityClient =
          new SecurityClientRest(ConfigUtility.getConfigProperties());
      authToken = securityClient.authenticate("admin", "admin");
    }
  }

  /**
   * Test release info.
   *
   * @throws Exception the exception
   */
  @Test
  public void testReleaseInfo() throws Exception {
    Logger.getLogger(this.getClass()).debug("Test ReleaseInfo");

    // Add release info
    ReleaseInfoJpa info = new ReleaseInfoJpa();
    info.setName("2014test");
    info.setDescription("test description");
    info.setEffectiveTime(new Date());
    info.setPlanned(true);
    info.setPublished(false);
    info.setTerminology("SNOMEDCT");
    info.setTerminologyVersion("latest");
    client.addReleaseInfo(info, authToken);
    ReleaseInfo info2 = client.getReleaseInfo("SNOMEDCT", "2014test", authToken);
    Assert.assertEquals(info, info2);

    // get release history
    ReleaseInfoList list = client.getReleaseHistory("SNOMEDCT", authToken);
    Assert.assertTrue(list.getObjects().contains(info));

    // planned, current, previous (with just one)
    // TODO: 

    // Planned, current, previous (with two)
    // TODO: 
    // client.removeReleaseInfo(info4.getId().toString(), authToken);

    // Update release info
    info2.setPublished(true);
    client.updateReleaseInfo((ReleaseInfoJpa)info2, authToken);
    info2 = client.getReleaseInfo("SNOMEDCT", "test2014", authToken);
    Assert.assertTrue(info2.isPublished());
    Assert.assertNotEquals(info, info2);

    // remove release info
    client.removeReleaseInfo(info2.getId(), authToken);
    info2 = client.getReleaseInfo("SNOMEDCT", "test2014", authToken);
    Assert.assertNull(info2);


  }

  /**
   * Find concepts modified since.
   *
   * @throws Exception the exception
   */
  @Test
  public void testFindConceptsModifiedSince() throws Exception {
    // Find concepts as of 20140131 and it should be everything
    // as lastModified date is set at time of load
    Logger.getLogger(this.getClass()).debug(
        "Find concepts modified since: SNOMEDCT, 20140131");
    PfsParameterJpa pfs = new PfsParameterJpa();
    pfs.setStartIndex(0);
    pfs.setMaxResults(10);
    ConceptList concepts =
        client.findConceptsModifiedSinceDate("SNOMEDCT", "20140131", pfs,
            authToken);
    Assert.assertEquals(concepts.getCount(), 10);
    Assert.assertEquals(concepts.getTotalCount(), 10293);

    concepts =
        client
            .findConceptsModifiedSinceDate("SNOMEDCT", "null", pfs, authToken);
    Assert.assertEquals(concepts.getCount(), 10);
    Assert.assertEquals(concepts.getTotalCount(), 10293);
  }

  /**
   * Find descriptions modified since.
   *
   * @throws Exception the exception
   */
  @Test
  public void findDescriptionsModifiedSince() throws Exception {
    // Find descriptions as of 20140131 and it should be everything
    // as lastModified date is set at time of load
    Logger.getLogger(this.getClass()).debug(
        "Find descriptions modified since: SNOMEDCT, 20140131");
    PfsParameterJpa pfs = new PfsParameterJpa();
    pfs.setStartIndex(0);
    pfs.setMaxResults(10);
    DescriptionList descriptions =
        client.findDescriptionsModifiedSinceDate("SNOMEDCT", "20140131", pfs,
            authToken);
    Assert.assertEquals(descriptions.getCount(), 10);
    Assert.assertEquals(descriptions.getTotalCount(), 34105);
    descriptions =
        client.findDescriptionsModifiedSinceDate("SNOMEDCT", "null", pfs,
            authToken);
    Assert.assertEquals(descriptions.getCount(), 10);
    Assert.assertEquals(descriptions.getTotalCount(), 34105);
  }

  /**
   * Find relationships modified since.
   *
   * @throws Exception the exception
   */
  @Test
  public void findRelationshipsModifiedSince() throws Exception {
    // Find relationships as of 20140131 and it should be everything
    // as lastModified date is set at time of load
    Logger.getLogger(this.getClass()).debug(
        "Find relationships modified since: SNOMEDCT, 20140131");
    PfsParameterJpa pfs = new PfsParameterJpa();
    pfs.setStartIndex(0);
    pfs.setMaxResults(10);
    RelationshipList relationships =
        client.findRelationshipsModifiedSinceDate("SNOMEDCT", "20140131", pfs,
            authToken);
    Assert.assertEquals(relationships.getCount(), 10);
    Assert.assertEquals(relationships.getTotalCount(), 79098);
    relationships =
        client.findRelationshipsModifiedSinceDate("SNOMEDCT", "null", pfs,
            authToken);
    Assert.assertEquals(relationships.getCount(), 10);
    Assert.assertEquals(relationships.getTotalCount(), 79098);
  }

  /**
   * Find languageRefSetMembers modified since.
   *
   * @throws Exception the exception
   */
  @Test
  public void findLanguageRefSetMemberModifiedSince() throws Exception {
    // Find languageRefSetMembers as of 20140131 and it should be everything
    // as lastModified date is set at time of load
    Logger.getLogger(this.getClass()).debug(
        "Find language refset members modified since: SNOMEDCT, 20140131");
    PfsParameterJpa pfs = new PfsParameterJpa();
    pfs.setStartIndex(0);
    pfs.setMaxResults(10);
    LanguageRefSetMemberList members =
        client.findLanguageRefSetMembersModifiedSinceDate("SNOMEDCT",
            "20140131", pfs, authToken);
    Assert.assertEquals(members.getCount(), 10);
    Assert.assertEquals(members.getTotalCount(), 66653);
    members =
        client.findLanguageRefSetMembersModifiedSinceDate("SNOMEDCT", "null",
            pfs, authToken);
    Assert.assertEquals(members.getCount(), 10);
    Assert.assertEquals(members.getTotalCount(), 66653);
  }

  /**
   * Teardown.
   */
  @After
  public void teardown() {
    // do nothing
  }
}
