package org.ihtsdo.otf.ts.test.other;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.jpa.client.ActionClientRest;
import org.ihtsdo.otf.ts.jpa.client.ContentChangeClientRest;
import org.ihtsdo.otf.ts.jpa.client.ContentClientRest;
import org.ihtsdo.otf.ts.jpa.client.SecurityClientRest;
import org.ihtsdo.otf.ts.jpa.services.helper.TerminologyUtility;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.jpa.AssociationReferenceConceptRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RelationshipJpa;
import org.ihtsdo.otf.ts.services.helpers.ConceptReportHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for REST content service.
 */
public class DemoServiceTest {

  /** The content client. */
  private static ContentClientRest contentClient;

  /** The edit client. */
  private static ContentChangeClientRest editClient;

  /** The action client. */
  @SuppressWarnings("unused")
  private static ActionClientRest actionClient;

  /** The auth token. */
  private static String authToken;

  /** The in. */
  private static BufferedReader in;

  /**
   * Setup.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @Before
  public void setup() throws Exception {
    if (contentClient == null) {
      contentClient =
          new ContentClientRest(ConfigUtility.getConfigProperties());
      editClient =
          new ContentChangeClientRest(ConfigUtility.getConfigProperties());
      actionClient = new ActionClientRest(ConfigUtility.getConfigProperties());
      SecurityClientRest securityClient =
          new SecurityClientRest(ConfigUtility.getConfigProperties());
      // Need admin security
      authToken = securityClient.authenticate("admin", "admin");
      in = new BufferedReader(new InputStreamReader(System.in));
    }
  }

  /**
   * Test demo.
   * @throws Exception
   */
  @SuppressWarnings("null")
  @Test
  public void testDemo() throws Exception {
    Logger.getLogger(this.getClass()).info("Start demo ..." + new Date());

    //
    // Create a concept
    // * Obtain 10001005 Bacterial sepsis from the DB
    // * change it
    // * insert it as a "new" concept.
    Logger.getLogger(this.getClass())
        .info("  Read 10001005 - Bacterial sepsis");
    Concept parent =
        contentClient.getSingleConcept("10001005", "SNOMEDCT", "latest",
            authToken);
    Concept concept =
        contentClient.getSingleConcept("10001005", "SNOMEDCT", "latest",
            authToken);
    Logger.getLogger(this.getClass()).info("  Clear id and effectiveTime");
    concept.setWorkflowStatus(null);
    concept.setId(null);
    concept.setTerminologyId(null);
    concept.setEffectiveTime(null);
    // Change the FN and PT descriptions - keep only 2
    Logger.getLogger(this.getClass()).info("  Change descriptions");
    Set<Description> descs = new HashSet<>(concept.getDescriptions());
    concept.getDescriptions().clear();
    for (Description description : descs) {
      description.setId(null);
      description.setTerminologyId(null);
      description.setConcept(concept);
      if (description.getTerm().equals("Bacterial sepsis (disorder)")) {
        concept.addDescription(description);
        description.setTerm("Bacterial sepsis XXX (disorder)");
        description.setEffectiveTime(null);
        for (LanguageRefSetMember member : description
            .getLanguageRefSetMembers()) {
          member.setId(null);
          member.setTerminologyId(null);
          member.setEffectiveTime(null);
          member.setDescription(description);
        }
      } else if (description.getTerm().equals("Bacterial sepsis")) {
        concept.addDescription(description);
        description.setTerm("Bacterial sepsis XXX");
        description.setEffectiveTime(null);
        for (LanguageRefSetMember member : description
            .getLanguageRefSetMembers()) {
          member.setId(null);
          member.setTerminologyId(null);
          member.setEffectiveTime(null);
          member.setDescription(description);
        }
      }
    }
    Logger.getLogger(this.getClass()).info("  Change relationships");
    // Rewire an active, stated, isa relationship to the parent
    for (Relationship relationship : concept.getRelationships()) {
      if (TerminologyUtility.isHierarchicalIsaRelationship(relationship)
          && relationship.isActive()
          && TerminologyUtility.getStatedType(concept.getTerminology(),
              concept.getTerminologyVersion()).equals(
              relationship.getCharacteristicTypeId())) {
        RelationshipJpa newRelationship = new RelationshipJpa(relationship);
        newRelationship.setId(null);
        newRelationship.setTerminologyId(null);
        newRelationship.setEffectiveTime(null);
        newRelationship.setSourceConcept(concept);
        newRelationship.setDestinationConcept(parent);
        concept.getRelationships().clear();
        concept.addRelationship(newRelationship);
        break;
      }
    }

    // at this point concept has descriptions and relationships as well.
    Logger.getLogger(this.getClass()).info("  Insert it as a new concept");
    concept = editClient.addConcept((ConceptJpa) concept, authToken);
    Logger.getLogger(this.getClass()).info("    id = " + concept.getId());
    Logger.getLogger(this.getClass()).info(
        "    terminologyId = " + concept.getTerminologyId());
    Logger.getLogger(this.getClass()).info(
        "    effectiveTime = " + concept.getEffectiveTime());
    Logger.getLogger(this.getClass()).info(
        "    lastModified = " + concept.getLastModified());
    Logger.getLogger(this.getClass()).info(
        "    lastModifiedBy = " + concept.getLastModifiedBy());
    Logger.getLogger(this.getClass()).info(
        "    defaultPreferredName = " + concept.getDefaultPreferredName());

    // log concept
    Logger.getLogger(this.getClass()).info(
        ConceptReportHelper.getConceptReport(concept));

    // pause
    System.out.println("Pause to show concept in Swagger API");
    in.readLine();

    //
    // Show the new concept in swagger, use the concept id.
    //

    //
    // Add description for "es"
    // * "La sepsis bacteriana xxx"
    Logger.getLogger(this.getClass()).info("  Add description for es");
    Description newDescription = null;
    for (Description description : concept.getDescriptions()) {
      if (description.getTerm().equals("Bacterial sepsis XXX")) {
        newDescription = new DescriptionJpa(description, true, false);
        break;
      }
    }
    newDescription.setId(null);
    newDescription.setTerminologyId(null);
    newDescription.setEffectiveTime(null);
    newDescription.setLanguageCode("es");
    newDescription.setConcept(concept);
    newDescription.setTerm("La sepsis bacteriana XXX");
    LanguageRefSetMember member =
        newDescription.getLanguageRefSetMembers().iterator().next();
    // Spanish
    member.setRefSetId("448879004");
    newDescription.getLanguageRefSetMembers().clear();
    newDescription.addLanguageRefSetMember(member);
    member.setDescription(newDescription);

    newDescription =
        editClient.addDescription((DescriptionJpa) newDescription, authToken);
    Logger.getLogger(this.getClass())
        .info("    id = " + newDescription.getId());
    Logger.getLogger(this.getClass()).info(
        "    terminologyId = " + newDescription.getTerminologyId());
    Logger.getLogger(this.getClass()).info(
        "    effectiveTime = " + newDescription.getEffectiveTime());
    Logger.getLogger(this.getClass()).info(
        "    lastModified = " + newDescription.getLastModified());
    Logger.getLogger(this.getClass()).info(
        "    lastModifiedBy = " + newDescription.getLastModifiedBy());
    // log concept
    concept = contentClient.getConcept(concept.getId(), authToken);
    Logger.getLogger(this.getClass()).info(
        ConceptReportHelper.getConceptReport(concept));

    // pause
    System.out.println("Pause to show concept in Swagger API");
    in.readLine();

    //
    // Show the new description in swagger, use the description id.
    //

    // retire the concept
    // * set concept inactive
    // * set active relationships inactive
    Logger.getLogger(this.getClass()).info("  Retire concept");

    concept.setActive(false);
    for (Relationship relationship : concept.getRelationships()) {
      // inactivate stated rels
      if (TerminologyUtility.isStatedRelationship(relationship) 
          && TerminologyUtility.isHierarchicalIsaRelationship(relationship)) {
        relationship.setActive(false);
      }
    }
    editClient.updateConcept((ConceptJpa) concept, authToken);
    // log concept
    concept = contentClient.getConcept(concept.getId(), authToken);
    Logger.getLogger(this.getClass()).info(
        ConceptReportHelper.getConceptReport(concept));
    
    // add a "reason for inactivation" - though typically you wouldn't do
    // this with a concept that had not yet been published
    Logger.getLogger(this.getClass()).info("  Add reason for inactivation");
    concept = contentClient.getConcept(concept.getId(), authToken);
    AssociationReferenceConceptRefSetMember armember =
        new AssociationReferenceConceptRefSetMemberJpa();
    armember.setConcept(concept);
    armember.setModuleId(concept.getModuleId());
    // possibly equivalent to
    armember.setRefSetId("900000000000523009");
    armember.setTargetComponentId("10001005");
    armember.setTerminology(concept.getTerminology());
    armember.setTerminologyVersion(concept.getTerminologyVersion());
    armember =
        editClient.addAssociationConceptReferenceRefSetMember(
            (AssociationReferenceConceptRefSetMemberJpa) armember, authToken);

    // log member
    armember =
        contentClient.getAssociationReferenceConceptRefSetMember(
            armember.getId(), authToken);
    Logger.getLogger(this.getClass()).info("  MEMBER = " + armember);

    // pause
    System.out.println("\nPause to show concept in Swagger API");
    in.readLine();

    // un-retire the concept and retire the "reason for inactivation"
    Logger.getLogger(this.getClass()).info("  Unretire concept");
    concept = contentClient.getConcept(concept.getId(), authToken);
    concept.setActive(true);
    for (Relationship relationship : concept.getRelationships()) {
      // inactivate stated rels
      if (TerminologyUtility.isStatedRelationship(relationship) &&
          TerminologyUtility.isHierarchicalIsaRelationship(relationship)) {
        relationship.setActive(true);
      }
    }
    editClient.updateConcept((ConceptJpa) concept, authToken);

    // remove "reason for inactivation" - though typically you wouldn't do
    // this with a concept that had not yet been published
    Logger.getLogger(this.getClass()).info("  Retire reason for inactivation");
    editClient.removeAssociationReferenceRefSetMember(armember.getId(),
        authToken);
    armember =
        contentClient.getAssociationReferenceConceptRefSetMember(
            armember.getId(), authToken);
    Logger.getLogger(this.getClass()).info("  MEMBER = " + armember);

    // log concept
    concept = contentClient.getConcept(concept.getId(), authToken);
    Logger.getLogger(this.getClass()).info(
        ConceptReportHelper.getConceptReport(concept));

    // pause
    System.out.println("Pause to show concept in Swagger API");
    in.readLine();

    // change the preferred name by retiring the preferred description and
    // adding another
    Logger.getLogger(this.getClass()).info("  Change preferred name");
    for (Description description : concept.getDescriptions()) {
      if (description.getTerm().equals("Bacterial sepsis XXX")) {
        Description newPt = new DescriptionJpa(description, true, false);
        description.setActive(false);
        for (LanguageRefSetMember member2 : description
            .getLanguageRefSetMembers()) {
          member2.setActive(false);
        }
        editClient.updateDescription((DescriptionJpa) description, authToken);
        newPt.setId(null);
        newPt.setEffectiveTime(null);
        newPt.setTerminologyId(null);
        newPt.setConcept(concept);
        newPt.setTerm("Bacterial sepsis YYY");
        for (LanguageRefSetMember member2 : newPt.getLanguageRefSetMembers()) {
          member2.setId(null);
          member2.setTerminologyId(null);
          member2.setEffectiveTime(null);
          member2.setDescription(newPt);
        }
        editClient.addDescription((DescriptionJpa) newPt, authToken);
        break;
      }      
    }
    // log concept
    concept = contentClient.getConcept(concept.getId(), authToken);
    Logger.getLogger(this.getClass()).info(
        ConceptReportHelper.getConceptReport(concept));

    // pause
    System.out.println("Pause to show concept in Swagger API");
    in.readLine();
    
    // Clean everything up
    Logger.getLogger(this.getClass()).info("  Cleanup");
    editClient.removeAssociationReferenceRefSetMember(armember.getId(), authToken);
    editClient.removeConcept(concept.getId(), authToken);

    // pause
    System.out.println("Pause to show the concept is gone now");
    in.readLine();

  }

  /**
   * Teardown.
   */
  @After
  public void teardown() {
    // do nothing
  }
}
