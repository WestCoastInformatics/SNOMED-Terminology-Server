package org.ihtsdo.otf.ts.services.helpers;

import org.ihtsdo.otf.ts.rf2.AssociationReferenceConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceDescriptionRefSetMember;
import org.ihtsdo.otf.ts.rf2.AttributeValueConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.AttributeValueDescriptionRefSetMember;
import org.ihtsdo.otf.ts.rf2.ComplexMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.SimpleMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.SimpleRefSetMember;

/**
 * Helper class for walking graphs of objects.
 */
public class ConceptReportHelper {

  /**
   * Returns the concept report.
   *
   * @param c the concept
   * @return the concept report
   */
  public static String getConceptReport(Concept c) {

    final String nl = System.getProperty("line.separator");
    final StringBuilder builder = new StringBuilder();
    builder.append(nl);
     builder.append("Concept = " + c).append(nl);
    for (Description d : c.getDescriptions()) {
      builder.append("  Description = " + d).append(nl);
      for (LanguageRefSetMember member : d.getLanguageRefSetMembers()) {
        builder.append("    Language = " + member).append(nl);
      }
    }
    for (Relationship r : c.getRelationships()) {
      builder.append("  Relationship = " + r).append(nl);
    }

    for (AttributeValueConceptRefSetMember member : c.getAttributeValueRefSetMembers()) {
      builder.append("  AttributeValue = " + member).append(nl);
    }

    for (AssociationReferenceConceptRefSetMember member : c
        .getAssociationReferenceRefSetMembers()) {
      builder.append("  AssociationReference = " + member).append(nl);
    }
    for (AttributeValueConceptRefSetMember member : c
        .getAttributeValueRefSetMembers()) {
      builder.append("  AttributeValue = " + member).append(nl);

    }
    for (ComplexMapRefSetMember member : c.getComplexMapRefSetMembers()) {
      builder.append("  ComplexMap = " + member).append(nl);
    }
    for (SimpleMapRefSetMember member : c.getSimpleMapRefSetMembers()) {
      builder.append("  SimpleMap = " + member).append(nl);
    }
    for (SimpleRefSetMember member : c.getSimpleRefSetMembers()) {
      builder.append("  Simple = " + member).append(nl);
    }
    return builder.toString();
  }

  /**
   * Returns the description report.
   *
   * @param description the description
   * @return the description report
   */
  public static String getDescriptionReport(Description description) {
    final String nl = System.getProperty("line.separator");
    final StringBuilder builder = new StringBuilder();
    builder.append(nl);
    builder.append("Description = " + description).append(nl);
    for (LanguageRefSetMember member : description.getLanguageRefSetMembers()) {
      builder.append("    Langauge = " + member).append(nl);
    }
    for (AssociationReferenceDescriptionRefSetMember member : description
        .getAssociationReferenceRefSetMembers()) {
      builder.append("  AssociationReference = " + member).append(nl);
    }
    for (AttributeValueDescriptionRefSetMember member : description
        .getAttributeValueRefSetMembers()) {
      builder.append("  AttributeValue = " + member).append(nl);
    }

    return builder.toString();
  }

}
