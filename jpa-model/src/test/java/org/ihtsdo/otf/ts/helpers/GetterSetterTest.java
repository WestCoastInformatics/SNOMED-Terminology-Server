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

      Object[] objects = new Object[] {
          new AssociationReferenceConceptRefSetMemberJpa(),
          new AssociationReferenceDescriptionRefSetMemberJpa(),
          new AttributeValueConceptRefSetMemberJpa(),
          new AttributeValueDescriptionRefSetMemberJpa(),
          new ComplexMapRefSetMemberJpa(),
          new DescriptionTypeRefSetMemberJpa(),
          new LanguageRefSetMemberJpa(),
          new ModuleDependencyRefSetMemberJpa(),
          new RefsetDescriptorRefSetMemberJpa(),
          new RelationshipJpa(),
          new SimpleMapRefSetMemberJpa(),
          new SimpleRefSetMemberJpa(),
          new TransitiveRelationshipJpa(),
          new ConceptListJpa(),
          new DescriptionListJpa(),
          new LanguageRefSetMemberListJpa(),
          new PfsParameterJpa(),
          new RelationshipListJpa(),
          new ReleaseInfoJpa(),
          new ReleaseInfoListJpa(),
          new RestPrimitiveJpa(),
          new SearchCriteriaJpa(),
          new SearchResultJpa(),
          new SearchResultListJpa(),
          new UserJpa(),
          new UserListJpa(),
          new ValidationResultJpa()
      };

      for (Object object : objects) {
        Logger.getLogger(this.getClass()).info("  Testing " + object.getClass().getName());
        GetterSetterTester tester = new GetterSetterTester(object);
        tester.exclude("objectId");
        tester.test();        
      }

      // Test ConceptJpa
      GetterSetterTester tester = new GetterSetterTester(new ConceptJpa());
      tester.exclude("objectId");
      tester.exclude("descriptions");
      tester.exclude("relationships");
      tester.exclude("inverseRelationships");
      tester.exclude("associationReferenceRefSetMembers");
      tester.exclude("attributeValueRefSetMembers");
      tester.exclude("complexMapRefSetMembers");
      tester.exclude("simpleMapRefSetMembers");
      tester.exclude("simpleRefSetMembers");
      tester.test();
      
      // Test DescriptionJpa
      tester = new GetterSetterTester(new DescriptionJpa());
      tester.exclude("objectId");
      tester.exclude("associationReferenceRefSetMembers");
      tester.exclude("attributeValueRefSetMembers");
      tester.exclude("languageRefSetMembers");
      tester.test();

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
