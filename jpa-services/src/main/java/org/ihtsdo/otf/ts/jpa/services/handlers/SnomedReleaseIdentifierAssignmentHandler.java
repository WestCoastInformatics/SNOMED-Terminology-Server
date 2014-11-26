package org.ihtsdo.otf.ts.jpa.services.handlers;

import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler;

// TODO: Auto-generated Javadoc
/**
 * Default implementation of {@link IdentifierAssignmentHandler}. This supports
 * "application-managed" identifier assignment.
 */
public class SnomedReleaseIdentifierAssignmentHandler extends
    UuidHashtIdentifierAssignmentHandler {

  /** The concept sequence. */
  private long conceptSequence;

  /** The description sequence. */
  private long descriptionSequence;

  /** The relationship sequence. */
  private long relationshipSequence;

  /** The is extension. */
  private boolean isExtension = false;

  /**
   * Instantiates a {@link SnomedReleaseIdentifierAssignmentHandler} based on
   * counters. Subsequent assignments
   *
   * @param conceptSequence the concept sequence
   * @param descriptionSequence the description sequence
   * @param relationshipSequence the relationship sequence
   * @param isExtension indicator of extension - for partition id
   */
  public SnomedReleaseIdentifierAssignmentHandler(long conceptSequence,
      long descriptionSequence, long relationshipSequence, boolean isExtension) {
    this.conceptSequence = conceptSequence;
    this.descriptionSequence = descriptionSequence;
    this.relationshipSequence = relationshipSequence;
    this.isExtension = isExtension;
  }

  /**
   * Increment concept sequence.
   *
   * @return the long
   */
  private synchronized long incrementConceptSequence() {
    conceptSequence++;
    return conceptSequence;
  }

  /**
   * Increment description sequence.
   *
   * @return the long
   */
  private synchronized long incrementDescriptionSequence() {
    descriptionSequence++;
    return descriptionSequence;
  }

  /**
   * Increment relationship sequence.
   *
   * @return the long
   */
  private synchronized long incrementRelationshipSequence() {
    relationshipSequence++;
    return relationshipSequence;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler#assign(
   * org.ihtsdo.otf.ts.rf2.Concept)
   */
  @Override
  public String getTerminologyId(Concept concept) {
    // If already assigned, leave alone
    if (isEmpty(concept.getTerminologyId())) {
      long ct = incrementConceptSequence();
      String num = ct + (isExtension ? "1" : "0") + "1";
      String verhoeff = Verhoeff.generateVerhoeff(num);
      concept.setTerminology(num + verhoeff);
    }
    return concept.getTerminologyId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler#assign(
   * org.ihtsdo.otf.ts.rf2.Description)
   */
  @Override
  public String getTerminologyId(Description description) {
    // If already assigned, leave alone
    if (isEmpty(description.getTerminologyId())) {
      long ct = incrementDescriptionSequence();
      String num = ct + (isExtension ? "1" : "0") + "1";
      String verhoeff = Verhoeff.generateVerhoeff(num);
      description.setTerminology(num + verhoeff);
    }
    return description.getTerminologyId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler#assign(
   * org.ihtsdo.otf.ts.rf2.Relationship)
   */
  @Override
  public String getTerminologyId(Relationship relationship) {
    // If already assigned, leave alone
    if (isEmpty(relationship.getTerminologyId())) {
      long ct = incrementRelationshipSequence();
      String num = ct + (isExtension ? "1" : "0") + "1";
      String verhoeff = Verhoeff.generateVerhoeff(num);
      relationship.setTerminology(num + verhoeff);
    }
    return relationship.getTerminologyId();

  }

  /**
   * Indicates whether or not empty is the case.
   *
   * @param str the str
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private static boolean isEmpty(String str) {
    return str == null || str.isEmpty();
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
