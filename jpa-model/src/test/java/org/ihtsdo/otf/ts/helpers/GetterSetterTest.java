package org.ihtsdo.otf.ts.helpers;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.rf2.jpa.AssociationReferenceConceptRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.AssociationReferenceDescriptionRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.AttributeValueConceptRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.AttributeValueDescriptionRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ComplexMapRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionTypeRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.LanguageRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ModuleDependencyRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RefsetDescriptorRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.RelationshipJpa;
import org.ihtsdo.otf.ts.rf2.jpa.SimpleMapRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.SimpleRefSetMemberJpa;
import org.ihtsdo.otf.ts.rf2.jpa.TransitiveRelationshipJpa;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class GetterSetterTest.
 */
public class GetterSetterTest {

  /**
   * Setup.
   */
  @Before
  public void setup() {

  }

  /**
   * Test model.
   */
  @Test
  public void testModel() {
    try {

      Object[] objects =
          new Object[] {
              new AssociationReferenceConceptRefSetMemberJpa(),
              new AssociationReferenceDescriptionRefSetMemberJpa(),
              new AttributeValueConceptRefSetMemberJpa(),
              new AttributeValueDescriptionRefSetMemberJpa(),
              new ComplexMapRefSetMemberJpa(),
              new DescriptionTypeRefSetMemberJpa(),
              new LanguageRefSetMemberJpa(),
              new ModuleDependencyRefSetMemberJpa(),
              new RefsetDescriptorRefSetMemberJpa(), new RelationshipJpa(),
              new SimpleMapRefSetMemberJpa(), new SimpleRefSetMemberJpa(),
              new TransitiveRelationshipJpa(), new ConceptListJpa(),
              new DescriptionListJpa(), new LanguageRefSetMemberListJpa(),
              new PfsParameterJpa(), new RelationshipListJpa(),
              new ReleaseInfoJpa(), new ReleaseInfoListJpa(),
              new RestPrimitiveJpa(), new SearchCriteriaJpa(),
              new SearchResultJpa(), new SearchResultListJpa(), new UserJpa(),
              new UserListJpa(), new ValidationResultJpa()
          };

      for (Object object : objects) {
        Logger.getLogger(this.getClass()).info(
            "  Testing " + object.getClass().getName());
        GetterSetterTester tester = new GetterSetterTester(object);
        tester.exclude("objectId");
        tester.test();
      }

      // Test ConceptJpa
      ConceptJpa concept = new ConceptJpa();
      GetterSetterTester tester = new GetterSetterTester(concept);
      tester.exclude("objectId");
      tester.exclude("inverseRelationships");
      tester.test();
      // test setting to null
      concept.setDescriptions(null);
      concept.setRelationships(null);
      concept.setAssociationReferenceRefSetMembers(null);
      concept.setAttributeValueRefSetMembers(null);
      concept.setComplexMapRefSetMembers(null);
      concept.setSimpleMapRefSetMembers(null);
      concept.setSimpleRefSetMembers(null);

      // Test DescriptionJpa
      DescriptionJpa desc = new DescriptionJpa();
      tester = new GetterSetterTester(desc);
      tester.exclude("objectId");
      tester.test();
      // test setting to null
      desc.setLanguageRefSetMembers(null);
      desc.setAssociationReferenceRefSetMembers(null);
      desc.setAttributeValueRefSetMembers(null);

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  /**
   * Teardown.
   */
  @After
  public void teardown() {

  }

}
