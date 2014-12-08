package org.ihtsdo.otf.ts.services.helpers;

import org.ihtsdo.otf.ts.rf2.AttributeValueConceptRefSetMember;
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
    builder.append("CONCEPT " + c).append(nl);
    for (Description d : c.getDescriptions()) {
      builder.append("  DESC = " + d).append(nl);
      for (LanguageRefSetMember member : d.getLanguageRefSetMembers()) {
        builder.append("    LANG = " + member).append(nl);
      }
    }
    for (Relationship r : c.getRelationships()) {
      builder.append("  REL = " + r).append(nl);
    }
    for (AttributeValueConceptRefSetMember member : c.getAttributeValueRefSetMembers()) {
      builder.append("  ATT_VALUE = " + member).append(nl);
    }
    for (ComplexMapRefSetMember member : c.getComplexMapRefSetMembers()) {
      builder.append("  COMPLEX_MAP = " + member).append(nl);
    }
    for (SimpleMapRefSetMember member : c.getSimpleMapRefSetMembers()) {
      builder.append("  SIMPLE_MAP = " + member).append(nl);
    }
    for (SimpleRefSetMember member : c.getSimpleRefSetMembers()) {
      builder.append("  SIMPLE = " + member).append(nl);      
    }
    return builder.toString();
  }

}
