package org.ihtsdo.otf.ts.jpa.services.validation;

import java.util.Properties;

import org.ihtsdo.otf.ts.helpers.ValidationResult;
import org.ihtsdo.otf.ts.helpers.ValidationResultJpa;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.services.handlers.ValidationCheck;

/**
 * A sample validation check for a new concept meeting the minimum
 * qualifying criteria.
 */
public class NewConceptMinRequirementsCheck implements ValidationCheck {

  /** The isa rel. */
  private static String isaRel = "116680003";

  /** The fn type. */
  private static String fnType = "900000000000003001";

  /** The pt type. */
  private static String ptType = "900000000000013009";

  /** The preferred id. */
  private static String preferredId = "900000000000548007";

  /** US Language. */
  private static String languageId = "900000000000509007";

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.helpers.Configurable#setProperties(java.util.Properties)
   */
  @Override
  public void setProperties(Properties p) {
    String prop = p.getProperty("isaRel");
    if (prop != null) {
      isaRel = prop;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.validation.ValidationCheck#validate(org.ihtsdo
   * .otf.ts.rf2.Concept)
   */
  @Override
  public ValidationResult validate(Concept c) {
    ValidationResult result = new ValidationResultJpa();

    // Verify there is at least one parent
    boolean isaRelFound = false;
    for (Relationship r : c.getRelationships()) {
      if (r.getTypeId().equals(isaRel)) {
        isaRelFound = true;
        break;
      }
    }
    if (!isaRelFound) {
      result.addError("Concept missing isa relationship: " + isaRel);
    }

    // Verify there is at least one active FN
    // Verify there is at least one active PT marked as prefered.
    boolean fnFound = false;
    boolean ptFound = false;
    for (Description d : c.getDescriptions()) {
      if (!d.isActive()) {
        continue;
      }
      for (LanguageRefSetMember member : d.getLanguageRefSetMembers()) {
        if (!member.isActive()) {
          continue;
        }
        if (!member.getRefSetId().equals(languageId)) {
          continue;
        }
        if (!member.getAcceptabilityId().equals(preferredId)) {
          continue;
        }
        if (d.getTypeId().equals(fnType)) {
          fnFound = true;
          if (ptFound)
            break;
        }
        if (d.getTypeId().equals(ptType)) {
          ptFound = true;
          if (fnFound)
            break;
        }
      }

    }
    if (!fnFound) {
      result.addError("Concept is missing an active preferred FN description: "
          + fnType + ", " + preferredId + ", " + languageId);
    }
    if (!ptFound) {
      result.addError("Concept is missing a active preferred SY description: "
          + ptType + ", " + preferredId + ", " + languageId);
    }

    return result;
  }

}
