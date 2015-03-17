package org.ihtsdo.otf.ts.jpa.services.handlers;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.services.handlers.IdentifierAssignmentHandler;

/**
 * Snomed RF2 release implementation of {@link IdentifierAssignmentHandler}.
 * NOTE: if identifiers need to be stable across daily builds, you could simply
 * use this for normal editing and keep track of the sequences in the database
 * by, say, extending RootServiceJpa. (although some complexities arise with
 * dual independent review).
 */
public class SnomedReleaseIdentifierAssignmentHandler extends
    SnomedUuidHashIdentifierAssignmentHandler {

  /** The concept sequence. */
  private long conceptSequence;

  /** The description sequence. */
  private long descriptionSequence;

  /** The relationship sequence. */
  private long relationshipSequence;

  /** The is extension. */
  private boolean isExtension = false;

  /** The namespace id. */
  private String namespaceId = "";

  /**
   * Instantiates a {@link SnomedReleaseIdentifierAssignmentHandler} based on
   * counters. Subsequent assignments
   */
  public SnomedReleaseIdentifierAssignmentHandler() {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.helpers.Configurable#setProperties(java.util.Properties)
   */
  @Override
  public void setProperties(Properties p) {
    this.conceptSequence = Long.valueOf(p.getProperty("concept.max"));
    this.descriptionSequence = Long.valueOf(p.getProperty("description.max"));
    this.relationshipSequence =
        Long.valueOf(p.getProperty("relationships.max"));
    this.namespaceId = p.getProperty("namespace.id");
    this.isExtension = (namespaceId == null || namespaceId.isEmpty());
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
  public String getTerminologyId(Concept concept) throws Exception {
    // If already assigned, leave alone
    if (!isSctid(concept.getTerminologyId())) {
      long ct = incrementConceptSequence();
      String num = ct + namespaceId + (isExtension ? "1" : "0") + "1";
      String verhoeff = Verhoeff.generateVerhoeff(num);
      return num + verhoeff;
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
  public String getTerminologyId(Description description) throws Exception {
    // If already assigned, leave alone
    if (!isSctid(description.getTerminologyId())) {
      long ct = incrementDescriptionSequence();
      String num = ct + namespaceId + (isExtension ? "1" : "0") + "1";
      String verhoeff = Verhoeff.generateVerhoeff(num);
      return num + verhoeff;
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
  public String getTerminologyId(Relationship relationship) throws Exception {
    // If already assigned, leave alone
    if (!isSctid(relationship.getTerminologyId())) {
      long ct = incrementRelationshipSequence();
      String num = ct + namespaceId + (isExtension ? "1" : "0") + "1";
      String verhoeff = Verhoeff.generateVerhoeff(num);
      return num + verhoeff;
    }
    return relationship.getTerminologyId();
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
