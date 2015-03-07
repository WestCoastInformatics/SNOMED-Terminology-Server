/**
 * Copyright (c) 2009 International Health Terminology Standards Development
 * Organisation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ihtsdo.otf.ts.jpa.services.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.Project;
import org.ihtsdo.otf.ts.classifier.model.Concept;
import org.ihtsdo.otf.ts.classifier.model.ConceptGroup;
import org.ihtsdo.otf.ts.classifier.model.EquivalentClasses;
import org.ihtsdo.otf.ts.classifier.model.Relationship;
import org.ihtsdo.otf.ts.classifier.model.RelationshipGroup;
import org.ihtsdo.otf.ts.classifier.model.RelationshipGroupList;
import org.ihtsdo.otf.ts.classifier.model.StringIDConcept;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.KeyValuesMap;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.helper.TerminologyUtility;
import org.ihtsdo.otf.ts.services.ContentService;
import org.ihtsdo.otf.ts.services.handlers.Classifier;
import org.ihtsdo.otf.ts.services.helpers.ProgressEvent;
import org.ihtsdo.otf.ts.services.helpers.ProgressListener;

import au.csiro.snorocket.core.IFactory_123;
import au.csiro.snorocket.snapi.I_Snorocket_123.I_Callback;
import au.csiro.snorocket.snapi.I_Snorocket_123.I_EquivalentCallback;
import au.csiro.snorocket.snapi.Snorocket_123;

/**
 * The Class ClassificationRunner. This class is responsible to classify stated
 * relationships from RF2 format using snorocket reasoner. Output results are
 * inferred relationships composed of taxonomy and attributes. Inferred
 * relationships are saved in file which is a parameter of class constructor.
 *
 * @author Alejandro Rodriguez.
 * @author modified by bcarlsenca
 * @version 2.0
 */
public class SnorocketClassifier implements Classifier {

  /** Listeners. */
  private List<ProgressListener> listeners = new ArrayList<>();

  /** The request cancel. */
  private boolean requestCancel = true;

  /** The isa. */
  private int isaConcept;

  /** The root concept. */
  private int rootConcept;

  /** The role root concept. */
  private int roleRootConcept;

  /** The project. */
  private Project project;

  /** The roles. */
  private int[] roles;

  /** The concepts. */
  private int[] concepts;

  /** The defined concepts flag. */
  private boolean[] definedConcepts;

  /** The cidx. */
  private int cidx;

  /** The relationships. */
  private List<Relationship> relationships;

  /** The logger. */
  Logger logger;

  /** The previous inferred relationships. */
  private List<Relationship> previousInferredRelationships;

  /** The new inferred relationships */
  private List<Relationship> currentInferredRelationships;

  /** The retired set. */
  private Set<Relationship> oldInferredRelationships;

  /** The new set. */
  private Set<Relationship> newInferredRelationships;

  /** The equivalents. */
  private ProcessEquiv equivalents = new ProcessEquiv();

  /**
   * Instantiates a {@link SnorocketClassifier} from the specified parameters.
   */
  public SnorocketClassifier() {
    logger = Logger.getLogger(getClass());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.classifier.Classifier#getEquivalentClasses()
   */
  @Override
  public KeyValuesMap getEquivalentClasses() {
    KeyValuesMap map = new KeyValuesMap();
    int setNumber = 1;
    for (ConceptGroup conceptGroup : equivalents.getEquivalentClasses()) {
      for (org.ihtsdo.otf.ts.classifier.model.Concept concept : conceptGroup) {
        map.put(String.valueOf(setNumber), String.valueOf(concept.id));
      }
      setNumber++;
    }
    return map;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.jpa.algo.Algorithm#compute()
   */
  @Override
  public void compute() throws Exception {

    logger.info("Begin Classification");

    // verify roles size
    int ridx = roles.length;
    if (roles.length > 100) {
      String errStr =
          "Role types exceeds 100. This will cause a memory issue. "
              + "Please check that role root is set to 'Concept mode attribute'";
      logger.error(errStr);
      throw new Exception(errStr);
    }

    // Fill array to make binary search work correctly.
    Arrays.fill(concepts, cidx, concepts.length, Integer.MAX_VALUE);

    // Handle cancel
    if (requestCancel) {
      return;
    }

    // Set root concept and instantiate classifier
    logger.info("  Root concept = " + rootConcept);
    int root = rootConcept;
    Snorocket_123 rocket_123 =
        new Snorocket_123(concepts, cidx, roles, ridx, root);

    // Set the "isa" concept.
    logger.info("  Isa concept = " + isaConcept);
    rocket_123.setIsaNid(isaConcept);

    // Set the role root concepts.
    rocket_123.setRoleRoot(isaConcept, true);
    logger.info("  Role root concept = " + roleRootConcept);
    int roleRoot = roleRootConcept;
    rocket_123.setRoleRoot(roleRoot, false);

    // add defined concepts
    logger.info("  Add defined concepts");
    for (int i = 0; i < definedConcepts.length; i++) {
      if (definedConcepts[i]) {
        rocket_123.setConceptIdxAsDefined(i);
      }
    }

    // Handle cancel
    if (requestCancel) {
      return;
    }

    concepts = null;
    definedConcepts = null;

    // add relationships
    Collections.sort(relationships);
    for (Relationship sr : relationships) {
      int err =
          rocket_123.addRelationship(sr.sourceId, sr.typeId, sr.destinationId,
              sr.group);
      if (err > 0) {
        StringBuilder sb = new StringBuilder();
        if ((err & 1) == 1) {
          sb.append(" --UNDEFINED_C1-- ");
        }
        if ((err & 2) == 2) {
          sb.append(" --UNDEFINED_ROLE-- ");
        }
        if ((err & 4) == 4) {
          sb.append(" --UNDEFINED_C2-- ");
        }
      }
    }

    // Handle cancel
    if (requestCancel) {
      return;
    }

    // clear memory
    relationships = null;
    System.gc();

    // Run classifier
    logger.info("  Starting Classifier ");
    rocket_123.classify();

    // Handle cancel
    if (requestCancel) {
      return;
    }

    // Get equivalents
    logger.info("  Get equivalents");
    rocket_123.getEquivalents(equivalents);
    logger.info("    count = " + equivalents.countConSet);

    // Handle cancel
    if (requestCancel) {
      return;
    }

    // Get classifier results
    currentInferredRelationships = new ArrayList<Relationship>();
    logger.info("  Get classifier results...");
    ProcessResults pr = new ProcessResults(currentInferredRelationships);
    rocket_123.getDistributionFormRelationships(pr);
    logger.info("    count = " + pr.countRel);

    // Handle cancel
    if (requestCancel) {
      return;
    }

    // Compare previous and curent rels
    logger.info("  Compare relationships...");
    compareAndWriteBack(previousInferredRelationships,
        currentInferredRelationships);
    previousInferredRelationships = null;
    currentInferredRelationships = null;

    // At this point retiredSet and newSet correspond t o

    // Handle cancel
    if (requestCancel) {
      return;
    }

    // clear memory
    pr = null;
    rocket_123 = null;
    System.gc();
  }

  /**
   * Handles computing equivalencies.
   */
  private class ProcessResults implements I_Callback {

    /** The snorels. */
    private List<Relationship> snorels;

    /** The count rel. */
    int countRel = 0; // STATISTICS COUNTER

    /**
     * Instantiates a new process results.
     *
     * @param snorels the snorels
     */
    public ProcessResults(List<Relationship> snorels) {
      this.snorels = snorels;
      this.countRel = 0;
    }

    /**
     * Adds the relationship.
     *
     * @param conceptId1 the concept id1
     * @param roleId the role id
     * @param conceptId2 the concept id2
     * @param group the group
     */
    @Override
    public void addRelationship(int conceptId1, int roleId, int conceptId2,
      int group) {
      countRel++;
      Relationship relationship =
          new Relationship(conceptId1, conceptId2, roleId, group);
      snorels.add(relationship);
      if (countRel % 25000 == 0) {
        // ** GUI: ProcessResults
        logger.info("rels processed " + countRel);
      }
    }
  }

  /**
   * Handles processing into distribution normal form.
   */
  private class ProcessEquiv implements I_EquivalentCallback {

    /** The count con set. */
    int countConSet = 0; // STATISTICS COUNTER

    /** The equiv concept. */
    private EquivalentClasses equivalentClasses;

    /**
     * Instantiates a new process equiv.
     */
    public ProcessEquiv() {
      equivalentClasses = new EquivalentClasses();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * au.csiro.snorocket.snapi.I_Snorocket_123.I_EquivalentCallback#equivalent
     * (java.util.ArrayList)
     */
    /**
     * Equivalent.
     *
     * @param equivalentConcepts the equivalent concepts
     */
    @Override
    public void equivalent(ArrayList<Integer> equivalentConcepts) {
      equivalentClasses.add(new ConceptGroup(equivalentConcepts));
      countConSet += 1;
    }

    /**
     * Gets the equiv concept.
     *
     * @return the equiv concept
     */
    public EquivalentClasses getEquivalentClasses() {
      return equivalentClasses;
    }
  }

  /**
   * Compare and write back.
   *
   * @param snorelA the snorel a
   * @param snorelB the snorel b
   * @throws Exception the exception
   */
  @SuppressWarnings("null")
  private void compareAndWriteBack(List<Relationship> snorelA,
    List<Relationship> snorelB) throws Exception {

    oldInferredRelationships = new HashSet<Relationship>();
    // STATISTICS COUNTERS
    int countConSeen = 0;
    int countSame = 0;
    int countSameISA = 0;
    int countA_Diff = 0;
    int countA_DiffISA = 0;
    int countA_Total = 0;
    int countB_Diff = 0;
    int countB_DiffISA = 0;
    int countB_Total = 0;

    Collections.sort(snorelA);
    Collections.sort(snorelB);

    // Typically, A is the Classifier Path (for previously inferred)
    // Typically, B is the SnoRocket Results Set (for newly inferred)
    Iterator<Relationship> itA = snorelA.iterator();
    Iterator<Relationship> itB = snorelB.iterator();
    Relationship rel_A = null;
    boolean done_A = false;
    if (itA.hasNext()) {
      rel_A = itA.next();
    } else {
      done_A = true;
    }
    Relationship rel_B = null;
    boolean done_B = false;
    if (itB.hasNext()) {
      rel_B = itB.next();
    } else {
      done_B = true;
    }

    logger.info("    snorelA.size() = " + snorelA.size());
    logger.info("    snorelB.size() = " + snorelB.size());

    // BY SORT ORDER, LOWER NUMBER ADVANCES FIRST
    while (!done_A && !done_B) {
      if (++countConSeen % 25000 == 0) {
        logger.info("::: [SnorocketMojo] compareAndWriteBack @ #\t"
            + countConSeen);
      }

      if (rel_A != null && rel_B != null && rel_A.sourceId == rel_B.sourceId) {

        // COMPLETELY PROCESS ALL C1 FOR BOTH IN & OUT
        // PROCESS C1 WITH GROUP == 0
        int thisC1 = rel_A.sourceId;

        // PROCESS WHILE BOTH HAVE GROUP 0
        while (rel_A.sourceId == thisC1 && rel_B.sourceId == thisC1
            && rel_A.group == 0 && rel_B.group == 0 && !done_A && !done_B) {

          // PROGESS GROUP ZERO
          switch (compareRelationships(rel_A, rel_B)) {
            case 1: // SAME
              // GATHER STATISTICS
              countA_Total++;
              countB_Total++;
              countSame++;
              // NOTHING TO WRITE IN THIS CASE
              if (rel_A.typeId == isaConcept) {
                countSameISA++;
              }
              if (itA.hasNext()) {
                rel_A = itA.next();
              } else {
                done_A = true;
              }
              if (itB.hasNext()) {
                rel_B = itB.next();
              } else {
                done_B = true;
              }
              break;

            case 2: // REL_A > REL_B -- B has extra stuff
              // WRITEBACK REL_B (Classifier Results) AS CURRENT
              countB_Diff++;
              countB_Total++;
              if (rel_B.typeId == isaConcept) {
                countB_DiffISA++;
              }
              oldInferredRelationships.add(rel_B);

              if (itB.hasNext()) {
                rel_B = itB.next();
              } else {
                done_B = true;
              }
              break;

            case 3: // REL_A < REL_B -- A has extra stuff
              // WRITEBACK REL_A (Classifier Input) AS RETIRED
              // GATHER STATISTICS
              countA_Diff++;
              countA_Total++;
              if (rel_A.typeId == isaConcept) {
                countA_DiffISA++;
              }
              oldInferredRelationships.add(rel_A);
              if (itA.hasNext()) {
                rel_A = itA.next();
              } else {
                done_A = true;
              }
              break;
            default:
              throw new Exception("Unexpected condition.");
          }
        }

        // REMAINDER LIST_A GROUP 0 FOR C1
        while (rel_A.sourceId == thisC1 && rel_A.group == 0 && !done_A) {

          countA_Diff++;
          countA_Total++;
          if (rel_A.typeId == isaConcept) {
            countA_DiffISA++;
          }
          oldInferredRelationships.add(rel_A);
          if (itA.hasNext()) {
            rel_A = itA.next();
          } else {
            done_A = true;
            break;
          }
        }

        // REMAINDER LIST_B GROUP 0 FOR C1
        while (rel_B.sourceId == thisC1 && rel_B.group == 0 && !done_B) {
          countB_Diff++;
          countB_Total++;
          if (rel_B.typeId == isaConcept) {
            countB_DiffISA++;
          }
          newInferredRelationships.add(rel_B);
          if (itB.hasNext()) {
            rel_B = itB.next();
          } else {
            done_B = true;
            break;
          }
        }

        // ** SEGMENT GROUPS **
        RelationshipGroupList groupList_A = new RelationshipGroupList();
        RelationshipGroupList groupList_B = new RelationshipGroupList();
        RelationshipGroup groupA = null;
        RelationshipGroup groupB = null;

        // SEGMENT GROUPS IN LIST_A
        int prevGroup = Integer.MIN_VALUE;
        while (rel_A.sourceId == thisC1 && !done_A) {
          if (rel_A.group != prevGroup) {
            groupA = new RelationshipGroup();
            groupList_A.add(groupA);
          }

          groupA.add(rel_A);

          prevGroup = rel_A.group;
          if (itA.hasNext()) {
            rel_A = itA.next();
          } else {
            done_A = true;
          }
        }
        // SEGMENT GROUPS IN LIST_B
        prevGroup = Integer.MIN_VALUE;
        while (rel_B.sourceId == thisC1 && !done_B) {
          if (rel_B.group != prevGroup) {
            groupB = new RelationshipGroup();
            groupList_B.add(groupB);
          }

          groupB.add(rel_B);

          prevGroup = rel_B.group;
          if (itB.hasNext()) {
            rel_B = itB.next();
          } else {
            done_B = true;
          }
        }

        // FIND GROUPS IN GROUPLIST_A WITHOUT AN EQUAL IN GROUPLIST_B
        // WRITE THESE GROUPED RELS AS "RETIRED"
        RelationshipGroupList groupList_NotEqual;
        if (groupList_A.size() > 0) {
          groupList_NotEqual = groupList_A.whichNotEqual(groupList_B);
          for (RelationshipGroup sg : groupList_NotEqual) {
            for (Relationship sr_A : sg) {
              oldInferredRelationships.add(sr_A);
            }
          }
          countA_Total += groupList_A.countRels();
          countA_Diff += groupList_NotEqual.countRels();
        }

        // FIND GROUPS IN GROUPLIST_B WITHOUT AN EQUAL IN GROUPLIST_A
        // WRITE THESE GROUPED RELS AS "NEW, CURRENT"
        int rgNum = 0; // USED TO DETERMINE "AVAILABLE" ROLE GROUP NUMBERS
        if (groupList_B.size() > 0) {
          groupList_NotEqual = groupList_B.whichNotEqual(groupList_A);
          for (RelationshipGroup sg : groupList_NotEqual) {
            if (sg.get(0).group != 0) {
              rgNum = nextRoleGroupNumber(groupList_A, rgNum);
              for (Relationship sr_B : sg) {
                sr_B.group = rgNum;
                newInferredRelationships.add(sr_B);
              }
            } else {
              for (Relationship sr_B : sg) {
                newInferredRelationships.add(sr_B);
              }
            }
          }
          countB_Total += groupList_A.countRels();
          countB_Diff += groupList_NotEqual.countRels();
        }
      } else if (rel_A.sourceId > rel_B.sourceId) {
        // CASE 2: LIST_B HAS CONCEPT NOT IN LIST_A
        // COMPLETELY *ADD* ALL THIS C1 FOR REL_B AS NEW, CURRENT
        int thisC1 = rel_B.sourceId;
        while (rel_B.sourceId == thisC1) {
          countB_Diff++;
          countB_Total++;
          if (rel_B.typeId == isaConcept) {
            countB_DiffISA++;
          }
          newInferredRelationships.add(rel_B);
          if (itB.hasNext()) {
            rel_B = itB.next();
          } else {
            done_B = true;
            break;
          }
        }

      } else {
        // CASE 3: LIST_A HAS CONCEPT NOT IN LIST_B
        // COMPLETELY *RETIRE* ALL THIS C1 FOR REL_A
        int thisC1 = rel_A.sourceId;
        while (rel_A.sourceId == thisC1) {
          countA_Diff++;
          countA_Total++;
          if (rel_A.typeId == isaConcept) {
            countA_DiffISA++;
          }
          oldInferredRelationships.add(rel_A);
          if (itA.hasNext()) {
            rel_A = itA.next();
          } else {
            done_A = true;
            break;
          }
        }
      }

      logger.info("  STATS");
      logger.info("    Concepts seen = " + countConSeen);
      logger.info("    Concepts same = " + countSame);
      logger.info("    Concepts sameIsa = " + countSameISA);
      logger.info("    CountA diff = " + countA_Diff);
      logger.info("    CountA diffIsa = " + countA_DiffISA);
      logger.info("    CountA total = " + countA_Total);
      logger.info("    CountB diff = " + countB_Diff);
      logger.info("    CountB diffIsa = " + countB_DiffISA);
      logger.info("    CountB total = " + countB_Total);
    }

    // AT THIS POINT, THE PREVIOUS C1 HAS BE PROCESSED COMPLETELY
    // AND, EITHER REL_A OR REL_B HAS BEEN COMPLETELY PROCESSED
    // AND, ANY REMAINDER IS ONLY ON REL_LIST_A OR ONLY ON REL_LIST_B
    // AND, THAT REMAINDER HAS A "STANDALONE" C1 VALUE
    // THEREFORE THAT REMAINDER WRITEBACK COMPLETELY
    // AS "NEW CURRENT" OR "OLD RETIRED"
    //
    // LASTLY, IF .NOT.DONE_A THEN THE NEXT REL_A IN ALREADY IN PLACE
    while (!done_A) {
      countA_Diff++;
      countA_Total++;
      if (rel_A.typeId == isaConcept) {
        countA_DiffISA++;
      }
      // COMPLETELY UPDATE ALL REMAINING REL_A AS RETIRED
      oldInferredRelationships.add(rel_A);
      if (itA.hasNext()) {
        rel_A = itA.next();
      } else {
        done_A = true;
        break;
      }
    }

    while (!done_B) {
      countB_Diff++;
      countB_Total++;
      if (rel_B.typeId == isaConcept) {
        countB_DiffISA++;
      }
      // COMPLETELY UPDATE ALL REMAINING REL_B AS NEW, CURRENT
      newInferredRelationships.add(rel_B);
      if (itB.hasNext()) {
        rel_B = itB.next();
      } else {
        done_B = true;
        break;
      }
    }
  }

  /**
   * Compare snomed relationships.
   *
   * @param inR the in r
   * @param outR the out r
   * @return the int
   */
  private static int compareRelationships(Relationship inR, Relationship outR) {
    if ((inR.sourceId == outR.sourceId) && (inR.group == outR.group)
        && (inR.typeId == outR.typeId)
        && (inR.destinationId == outR.destinationId)) {
      return 1; // SAME
    } else if (inR.sourceId > outR.sourceId) {
      return 2; // ADDED
    } else if ((inR.sourceId == outR.sourceId) && (inR.group > outR.group)) {
      return 2; // ADDED
    } else if ((inR.sourceId == outR.sourceId) && (inR.group == outR.group)
        && (inR.typeId > outR.typeId)) {
      return 2; // ADDED
    } else if ((inR.sourceId == outR.sourceId) && (inR.group == outR.group)
        && (inR.typeId == outR.typeId)
        && (inR.destinationId > outR.destinationId)) {
      return 2; // ADDED
    } else {
      return 3; // DROPPED
    }
  } // compareSnoRel

  /**
   * Next role group number.
   *
   * @param sgl the sgl
   * @param gnum the gnum
   * @return the int
   */
  private static int nextRoleGroupNumber(RelationshipGroupList sgl, int gnum) {

    int testNum = gnum + 1;
    int sglSize = sgl.size();
    int trial = 0;
    while (trial <= sglSize) {

      boolean exists = false;
      for (int i = 0; i < sglSize; i++) {
        if (sgl.get(i).get(0).group == testNum) {
          exists = true;
        }
      }

      if (exists == false) {
        return testNum;
      } else {
        testNum++;
        trial++;
      }
    }

    return testNum;
  }

  /**
   * Load concepts.
   *
   * @throws Exception the exception
   */
  @Override
  public void loadConcepts() throws Exception {

    ContentService service = new ContentServiceJpa();

    // remember to respect project - need to compute the scope definition

    ConceptList list =
        service.getAllConcepts(project.getTerminology(),
            project.getTerminologyVersion());

    List<StringIDConcept> inputConcepts = new ArrayList<>();
    relationships = new ArrayList<>();

    int count = 0;
    for (org.ihtsdo.otf.ts.rf2.Concept concept : list.getObjects()) {
      // Clear cache
      if (count++ % 2000 == 0) {
        service.clear();
      }

      if (project.getActionWorkflowStatusValues() != null
          && project.getActionWorkflowStatusValues().contains(
              concept.getWorkflowStatus())) {

        // Add concept
        StringIDConcept stringIdConcept =
            new StringIDConcept(Integer.parseInt(concept.getObjectId()),
                concept.getObjectId(), Boolean.parseBoolean(concept
                    .getDefinitionStatusId()));
        inputConcepts.add(stringIdConcept);

        // Iterate through active stated relationships relationships
        for (org.ihtsdo.otf.ts.rf2.Relationship relationship : concept
            .getRelationships()) {
          // Convert stated rels for classification
          if (TerminologyUtility.isStatedRelationship(relationship)
              && relationship.isActive()) {
            Relationship rel =
                new Relationship(Integer.parseInt(concept.getObjectId()),
                    Integer.parseInt(relationship.getDestinationConcept()
                        .getObjectId()), Integer.parseInt(relationship
                        .getTypeId()), relationship.getRelationshipGroup(),
                    relationship.getObjectId());
            // add stated rels for classification
            relationships.add(rel);
          }

          // save inferred rels for comparison
          else if (TerminologyUtility.isInferredRelationship(relationship)
              && relationship.isActive()) {
            Relationship rel =
                new Relationship(Integer.parseInt(concept.getObjectId()),
                    Integer.parseInt(relationship.getDestinationConcept()
                        .getObjectId()), Integer.parseInt(relationship
                        .getTypeId()), relationship.getRelationshipGroup(),
                    relationship.getObjectId());
            previousInferredRelationships.add(rel);
          }
        }
      }
    }

    // Reserve ids for TOP and BOTTOM
    final int reserved = 2;
    cidx = reserved;
    int margin = inputConcepts.size() >> 2; // Add 50%
    concepts = new int[inputConcepts.size() + margin + reserved];
    definedConcepts = new boolean[concepts.length];
    concepts[IFactory_123.TOP_CONCEPT] = IFactory_123.TOP;
    concepts[IFactory_123.BOTTOM_CONCEPT] = IFactory_123.BOTTOM;
    Collections.sort(inputConcepts);
    if (inputConcepts.get(0).id <= Integer.MIN_VALUE + reserved) {
      throw new Exception("TOP & BOTTOM ids NOT reserved");
    }

    // Add input concepts
    for (Concept sc : inputConcepts) {
      if (sc.isDefined) {
        definedConcepts[cidx] = true;
      }
      concepts[cidx++] = sc.id;
    }

  }

  /**
   * Load roles.
   *
   * @throws Exception the exception
   */
  @Override
  public void loadRoles() throws Exception {

    ContentService service = new ContentServiceJpa();
    org.ihtsdo.otf.ts.rf2.Concept roleRoot =
        service.getConcept((long) roleRootConcept);
    ConceptList list = service.getDescendantConcepts(roleRoot, null);

    roles = new int[list.getCount() + 1];
    // add isa
    roles[0] = isaConcept;
    // add others
    int resIdx = 1;
    for (org.ihtsdo.otf.ts.rf2.Concept concept : list.getObjects()) {
      roles[resIdx++] = Integer.valueOf(concept.getId().toString());
      resIdx++;
    }
    Arrays.sort(roles);
  }

  /**
   * Reset.
   *
   * @throws Exception the exception
   */
  @Override
  public void reset() throws Exception {
    // do nothing
    roles = null;
    concepts = null;
    definedConcepts = null;
    relationships = null;
    previousInferredRelationships = null;
    currentInferredRelationships = null;
    oldInferredRelationships = null;
    newInferredRelationships = null;
  }

  /**
   * Cancel.
   *
   * @throws Exception the exception
   */
  @Override
  public void cancel() throws Exception {
    requestCancel = true;
  }

  /**
   * Adds the progress listener.
   *
   * @param l the l
   */
  @Override
  public void addProgressListener(ProgressListener l) {
    listeners.add(l);
  }

  /**
   * Removes the progress listener.
   *
   * @param l the l
   */
  @Override
  public void removeProgressListener(ProgressListener l) {
    listeners.remove(l);
  }

  /**
   * Fires a {@link ProgressEvent}.
   * @param pct percent done
   * @param note progress note
   */
  public void fireProgressEvent(int pct, String note) {
    ProgressEvent pe = new ProgressEvent(this, pct, pct, note);
    for (int i = 0; i < listeners.size(); i++) {
      listeners.get(i).updateProgress(pe);
    }
    Logger.getLogger(this.getClass()).info("    " + pct + "% " + note);
  }

  /**
   * Sets the properties.
   *
   * @param p the properties
   * @throws Exception the exception
   */
  @Override
  public void setProperties(Properties p) throws Exception {
    // do nothing
  }

  /**
   * Returns the new inferred relationships.
   *
   * @return the new inferred relationships
   */
  @Override
  public List<org.ihtsdo.otf.ts.rf2.Relationship> getNewInferredRelationships() {
    return null;
  }

  /**
   * Returns the old inferred relationships.
   *
   * @return the old inferred relationships
   */
  @Override
  public List<org.ihtsdo.otf.ts.rf2.Relationship> getOldInferredRelationships() {
    return null;
  }

  /**
   * Sets the root id.
   *
   * @param rootId the root id
   */
  @Override
  public void setRootId(int rootId) {
    this.rootConcept = rootId;
  }

  /**
   * Sets the isa rel id.
   *
   * @param isaRelId the isa rel id
   */
  @Override
  public void setIsaRelId(int isaRelId) {
    this.isaConcept = isaRelId;
  }

  /**
   * Sets the role root id.
   *
   * @param roleRootId the role root id
   */
  @Override
  public void setRoleRootId(int roleRootId) {
    this.roleRootConcept = roleRootId;
  }

  /**
   * Sets the project.
   *
   * @param project the project
   */
  @Override
  public void setProject(Project project) {
    this.project = project;
  }

  @Override
  public void close() throws Exception {
    // TODO Auto-generated method stub
    
  }

}
