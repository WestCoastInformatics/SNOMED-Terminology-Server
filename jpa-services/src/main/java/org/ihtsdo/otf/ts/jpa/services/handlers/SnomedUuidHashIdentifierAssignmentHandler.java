package org.ihtsdo.otf.ts.jpa.services.handlers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.jpa.services.helper.TerminologyUtility;
import org.ihtsdo.otf.ts.rf2.AssociationReferenceRefSetMember;
import org.ihtsdo.otf.ts.rf2.AttributeValueRefSetMember;
import org.ihtsdo.otf.ts.rf2.ComplexMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.Component;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.DescriptionTypeRefSetMember;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.ModuleDependencyRefSetMember;
import org.ihtsdo.otf.ts.rf2.RefsetDescriptorRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.SimpleMapRefSetMember;
import org.ihtsdo.otf.ts.rf2.SimpleRefSetMember;
import org.ihtsdo.otf.ts.rf2.TransitiveRelationship;
import org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler;

/**
 * Default implementation of {@link IdentifierAssignmentHandler}. This supports
 * "application-managed" identifier assignment.
 * 
 * If a component already has an SCTID, it keeps it.
 */
public class SnomedUuidHashIdentifierAssignmentHandler implements
    IdentifierAssignmentHandler {

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.helpers.Configurable#setProperties(java.util.Properties)
   */
  @Override
  public void setProperties(Properties p) {
    // do nothing
  }

  /**
   * Concept ID assignment is based on sorted active parent IDs and the default
   * preferred name.
   */
  @Override
  public String getTerminologyId(Concept concept) throws Exception {
    // If the concept already has an sctid return it
    Logger.getLogger(this.getClass()).debug("assigning concept id");
    if (isSctid(concept.getTerminologyId())) {
      Logger.getLogger(this.getClass()).debug("  SCTID found");
      return concept.getTerminologyId();
    }
    StringBuilder sb = new StringBuilder();
    List<Concept> concepts =
        TerminologyUtility.getActiveParentConcepts(concept);
    Collections.sort(concepts, new Comparator<Concept>() {
      @Override
      public int compare(Concept o1, Concept o2) {
        return o1.getTerminologyId().compareTo(o2.getTerminologyId());
      }
    });
    for (Concept parent : concepts) {
      // assumes parent terminology id is set
      sb.append(parent.getTerminologyId());
    }
    sb.append(concept.getDefaultPreferredName());
    String id = TerminologyUtility.getUuid(sb.toString()).toString();
    Logger.getLogger(this.getClass()).debug("  setting id " + id);
    return id;
  }

  /**
   * Description ID is based on concept id, typeId, term.
   */
  @Override
  public String getTerminologyId(Description description) throws Exception {
    // If the description already has an sctid return it
    // NOTE: this will allow changing the identity fields
    // of a description if the SCTID is already assigned
    if (isSctid(description.getTerminologyId())) {
      return description.getTerminologyId();
    }
    // otherwise return the uuid - for identity comparisons
    String value =
        description.getConcept().getTerminologyId() + description.getTypeId()
            + description.getTerm();
    String id = TerminologyUtility.getUuid(value).toString();
    return id;
  }

  /**
   * Relationship ID is based on source/destination concept, typeId,
   * characteristicType (e.g. inferred or stated) and whether its grouped.
   */
  @Override
  public String getTerminologyId(Relationship relationship) throws Exception {
    // If the relationship already has an sctid return it
    // NOTE: this will allow changing the identity fields
    // of a relationship if the SCTID is already assigned
    if (isSctid(relationship.getTerminologyId())) {
      return relationship.getTerminologyId();
    }
    // always return the uuid - for identity comparisons
    String value =
        relationship.getSourceConcept().getTerminologyId()
            + relationship.getDestinationConcept().getId()
            + relationship.getTypeId() + relationship.getCharacteristicTypeId()
            + (relationship.getRelationshipGroup() == 0 ? "false" : "true");
    String id = TerminologyUtility.getUuid(value).toString();
    return id;
  }

  /**
   * Association reference refset member ID based on refSetId, referenced
   * component id, and the target component.
   */
  @Override
  public String getTerminologyId(
    AssociationReferenceRefSetMember<? extends Component> member)
    throws Exception {
    String value =
        member.getRefSetId() + member.getComponent().getId()
            + member.getTargetComponentId();
    String id = TerminologyUtility.getUuid(value).toString();
    return id;
  }

  /**
   * Attribute value refset member ID based on refSetId, referenced component
   * id.
   */
  @Override
  public String getTerminologyId(
    AttributeValueRefSetMember<? extends Component> member) throws Exception {
    String value = member.getRefSetId() + member.getComponent().getId();
    String id = TerminologyUtility.getUuid(value).toString();
    return id;
  }

  /**
   * Complex map refset member ID based on refset id, referenced component id,
   * map rule, and map target.
   */
  @Override
  public String getTerminologyId(ComplexMapRefSetMember member)
    throws Exception {
    String value =
        member.getRefSetId() + member.getComponent().getId()
            + member.getMapRule() + member.getMapTarget();
    String id = TerminologyUtility.getUuid(value).toString();
    return id;
  }

  /**
   * Description type refset member ID based on refset id, referenced component
   * id.
   */
  @Override
  public String getTerminologyId(DescriptionTypeRefSetMember member)
    throws Exception {
    String value = member.getRefSetId() + member.getComponent().getId();
    String id = TerminologyUtility.getUuid(value).toString();
    return id;
  }

  /**
   * Language refset member ID based on refset id, referenced component id.
   */
  @Override
  public String getTerminologyId(LanguageRefSetMember member) throws Exception {
    String value =
        member.getRefSetId() + member.getComponent().getTerminologyId();
    String id = TerminologyUtility.getUuid(value).toString();
    return id;
  }

  /**
   * Module dependency member ID based on refset id, referenced component id.
   */
  @Override
  public String getTerminologyId(ModuleDependencyRefSetMember member)
    throws Exception {
    String value = member.getRefSetId() + member.getComponent().getId();
    String id = TerminologyUtility.getUuid(value).toString();
    return id;
  }

  /**
   * Refset descriptor member ID based on refset id, referenced component id,
   * attribute description, attribute type, and attribute order.
   */
  @Override
  public String getTerminologyId(RefsetDescriptorRefSetMember member)
    throws Exception {
    String value =
        member.getRefSetId() + member.getComponent().getId()
            + member.getAttributeDescription() + member.getAttributeType()
            + member.getAttributeType();
    String id = TerminologyUtility.getUuid(value).toString();
    return id;
  }

  /**
   * Simple map member ID based on refset id, referenced component id, map
   * target.
   */
  @Override
  public String getTerminologyId(SimpleMapRefSetMember member) throws Exception {
    String value =
        member.getRefSetId() + member.getComponent().getId()
            + member.getMapTarget();
    String id = TerminologyUtility.getUuid(value).toString();
    return id;
  }

  /**
   * Simple member ID based on refset id, referenced component id.
   */
  @Override
  public String getTerminologyId(SimpleRefSetMember member) throws Exception {
    String value = member.getRefSetId() + member.getComponent().getId();
    String id = TerminologyUtility.getUuid(value).toString();
    return id;
  }

  /**
   * Transitive relationship id based on super/subtype concepts.
   */
  @Override
  public String getTerminologyId(TransitiveRelationship relationship)
    throws Exception {
    // always return the uuid - for identity comparisons
    String value =
        relationship.getSuperTypeConcept() + ","
            + relationship.getSubTypeConcept();
    String id = TerminologyUtility.getUuid(value).toString();
    return id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler#
   * allowIdChangeOnUpdate()
   */
  @Override
  public boolean allowIdChangeOnUpdate() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler#
   * allowConceptIdChangeOnUpdate()
   */
  @Override
  public boolean allowConceptIdChangeOnUpdate() {
    return true;
  }

  /**
   * Indicates whether or not empty is the case.
   *
   * @param str the str
   * @return <code>true</code> if so, <code>false</code> otherwise
   * @throws Exception the exception
   */
  protected boolean isSctid(String str) throws Exception {
    if (str == null || str.isEmpty()) {
      return false;
    }
    if (str.matches("^\\d+$")) {
      if (Verhoeff.validateVerhoeff(str)) {
        return true;
      } else {
        String vc = Verhoeff.generateVerhoeff(str.substring(0,str.length()-2));
        Logger.getLogger(this.getClass()).info(
            "Unexpected numeric identifier with bad Verhoeff digit " + str + ", should be "  +vc);
        return true;
      }
    }
    return false;
  }

  /**
   * The Class Verhoeff.
   *
   * @author Colm Rice
   * @see <a href="http://en.wikipedia.org/wiki/Verhoeff_algorithm">More
   *      Info</a>
   * @see <a href="http://en.wikipedia.org/wiki/Dihedral_group">Dihedral
   *      Group</a>
   * @see <a href="http://mathworld.wolfram.com/DihedralGroupD5.html">Dihedral
   *      Group Order 10</a>
   */
  public static class Verhoeff {

    /** The multiplication table. */
    static int[][] d = new int[][] {
        {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9
        }, {
            1, 2, 3, 4, 0, 6, 7, 8, 9, 5
        }, {
            2, 3, 4, 0, 1, 7, 8, 9, 5, 6
        }, {
            3, 4, 0, 1, 2, 8, 9, 5, 6, 7
        }, {
            4, 0, 1, 2, 3, 9, 5, 6, 7, 8
        }, {
            5, 9, 8, 7, 6, 0, 4, 3, 2, 1
        }, {
            6, 5, 9, 8, 7, 1, 0, 4, 3, 2
        }, {
            7, 6, 5, 9, 8, 2, 1, 0, 4, 3
        }, {
            8, 7, 6, 5, 9, 3, 2, 1, 0, 4
        }, {
            9, 8, 7, 6, 5, 4, 3, 2, 1, 0
        }
    };

    /** The permutation table. */
    static int[][] p = new int[][] {
        {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9
        }, {
            1, 5, 7, 6, 2, 8, 3, 0, 9, 4
        }, {
            5, 8, 0, 3, 7, 9, 6, 1, 4, 2
        }, {
            8, 9, 1, 6, 0, 4, 3, 5, 2, 7
        }, {
            9, 4, 5, 3, 1, 2, 6, 8, 7, 0
        }, {
            4, 2, 8, 6, 5, 7, 3, 9, 0, 1
        }, {
            2, 7, 9, 3, 8, 0, 6, 4, 1, 5
        }, {
            7, 0, 4, 6, 9, 1, 3, 2, 5, 8
        }
    };

    /** The inverse table. */
    static int[] inv = {
        0, 4, 3, 2, 1, 5, 6, 7, 8, 9
    };

    /**
     * For a given number generates a Verhoeff digit
     *
     * @param num the num
     * @return the string
     */
    public static String generateVerhoeff(String num) {

      int c = 0;
      int[] myArray = StringToReversedIntArray(num);

      for (int i = 0; i < myArray.length; i++) {
        c = d[c][p[((i + 1) % 8)][myArray[i]]];
      }

      return Integer.toString(inv[c]);
    }

    /**
     * Validates that an entered number is Verhoeff compliant. NB: Make sure the
     * check digit is the last one.
     *
     * @param num the num
     * @return true, if successful
     */
    public static boolean validateVerhoeff(String num) {

      int c = 0;
      int[] myArray = StringToReversedIntArray(num);

      for (int i = 0; i < myArray.length; i++) {
        c = d[c][p[(i % 8)][myArray[i]]];
      }

      return (c == 0);
    }

    /**
     * Converts a string to a reversed integer array.
     *
     * @param num the num
     * @return the int[]
     */
    private static int[] StringToReversedIntArray(String num) {

      int[] myArray = new int[num.length()];

      for (int i = 0; i < num.length(); i++) {
        myArray[i] = Integer.parseInt(num.substring(i, i + 1));
      }

      myArray = Reverse(myArray);

      return myArray;

    }

    /**
     * Reverses an int array
     *
     * @param myArray the my array
     * @return the int[]
     */
    private static int[] Reverse(int[] myArray) {
      int[] reversed = new int[myArray.length];

      for (int i = 0; i < myArray.length; i++) {
        reversed[i] = myArray[myArray.length - (i + 1)];
      }

      return reversed;
    }

  }

}
