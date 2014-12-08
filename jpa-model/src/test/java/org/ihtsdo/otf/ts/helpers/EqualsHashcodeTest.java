package org.ihtsdo.otf.ts.helpers;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.time.FastDateFormat;
import org.ihtsdo.otf.ts.rf2.Component;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.rf2.jpa.DescriptionJpa;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class GetterSetterTest.
 */
public class EqualsHashcodeTest {

  /** The date format. */
  private final static FastDateFormat format = FastDateFormat
      .getInstance("yyyyMMdd");

  /**
   * Setup.
   */
  @Before
  public void setup() {

  }

  /**
   * Test component and concept
   * @throws ParseException 
   */
  @Test
  public void testComponentAndConcept() throws ParseException {
    // For each object, test the equals/hashcode methods

    ConceptJpa concept = new ConceptJpa();
    setComponentFields(concept);
    ConceptJpa concept2 = new ConceptJpa();
    setComponentFields(concept2);
    // test matching
    Assert.assertTrue(concept.equals(concept2));
    Assert.assertTrue(concept.hashCode() == concept2.hashCode());
    // test still matching
    changeComponentFieldsSame(concept2);
    Assert.assertTrue(concept.equals(concept2));
    Assert.assertTrue(concept.hashCode() == concept2.hashCode());

    // test not matching, then matching
    concept2.setDefinitionStatusId("defStatusId");
    Assert.assertFalse(concept.equals(concept2));
    Assert.assertFalse(concept.hashCode() == concept2.hashCode());
    concept.setDefinitionStatusId("defStatusId");
    Assert.assertTrue(concept.equals(concept2));
    Assert.assertTrue(concept.hashCode() == concept2.hashCode());
  }

  /**
   * Test component and concept
   * @throws ParseException 
   */
  @Test
  public void testDescription() throws ParseException {
    DescriptionJpa desc = new DescriptionJpa();
    setComponentFields(desc);
    DescriptionJpa desc2 = new DescriptionJpa();
    setComponentFields(desc2);

    // test matching
    Assert.assertTrue(desc.equals(desc2));
    Assert.assertTrue(desc.hashCode() == desc2.hashCode());


    // test not matching, then matching
    desc2.setCaseSignificanceId("caseSignificanceId");
    Assert.assertFalse(desc.equals(desc2));
    Assert.assertFalse(desc.hashCode() == desc2.hashCode());
    desc.setCaseSignificanceId("caseSignificanceId");
    Assert.assertTrue(desc.equals(desc2));
    Assert.assertTrue(desc.hashCode() == desc2.hashCode());

    ConceptJpa concept = new ConceptJpa();
    setComponentFields(concept);
    concept.setId(1L);
    desc2.setConcept(concept);
    Assert.assertFalse(desc.equals(desc2));
    Assert.assertFalse(desc.hashCode() == desc2.hashCode());
    desc.setConcept(concept);
    Assert.assertTrue(desc.equals(desc2));
    Assert.assertTrue(desc.hashCode() == desc2.hashCode());

    desc2.setLanguageCode("en");
    Assert.assertFalse(desc.equals(desc2));
    Assert.assertFalse(desc.hashCode() == desc2.hashCode());
    desc.setLanguageCode("en");
    Assert.assertTrue(desc.equals(desc2));
    Assert.assertTrue(desc.hashCode() == desc2.hashCode());

    desc2.setTerm("term");
    Assert.assertFalse(desc.equals(desc2));
    Assert.assertFalse(desc.hashCode() == desc2.hashCode());
    desc.setTerm("term");
    Assert.assertTrue(desc.equals(desc2));
    Assert.assertTrue(desc.hashCode() == desc2.hashCode());

    desc2.setTypeId("typeId");
    Assert.assertFalse(desc.equals(desc2));
    Assert.assertFalse(desc.hashCode() == desc2.hashCode());
    desc.setTypeId("typeId");
    Assert.assertTrue(desc.equals(desc2));
    Assert.assertTrue(desc.hashCode() == desc2.hashCode());

    // TODO: flesh this out for other objects.
  }
  /**
   * Sets the component fields.
   *
   * @param c the new component fields
   * @throws ParseException the parse exception
   */
  private void setComponentFields(Component c) throws ParseException {
    c.setActive(true);
    c.setEffectiveTime(format.parse("20140731"));
    c.setId(1L);
    c.setLabel("label");
    c.setLastModified(new Date());
    c.setLastModifiedBy("tester");
    c.setModuleId("moduleId");
    c.setTerminology("SNOMEDCT");
    c.setTerminologyId("12345");
    c.setTerminologyVersion("20140731");
  }

  /**
   * Sets the component fields in a way that causes equals/hahshcode the same..
   *
   * @param c the new component fields
   * @throws ParseException the parse exception
   */
  private void changeComponentFieldsSame(Component c) throws ParseException {
    c.setEffectiveTime(format.parse("20150131"));
    c.setId(2L);
    c.setLabel("label2");
    c.setLastModified(format.parse("20140731"));
    c.setLastModifiedBy("tester2");
  }

  /**
   * Teardown.
   */
  @After
  public void teardown() {

  }

}
