package org.ihtsdo.otf.ts.jpa.services;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.ihtsdo.classifier.ClassificationRunner;
import org.ihtsdo.classifier.model.ConceptGroup;
import org.ihtsdo.classifier.model.EquivalentClasses;
import org.ihtsdo.classifier.model.StringIDConcept;
import org.ihtsdo.classifier.utils.GetDescendants;
import org.ihtsdo.classifier.utils.I_Constants;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.KeyValuePair;
import org.ihtsdo.otf.ts.helpers.KeyValuePairList;
import org.ihtsdo.otf.ts.helpers.KeyValuePairLists;
import org.ihtsdo.otf.ts.helpers.LocalException;
import org.ihtsdo.otf.ts.helpers.RelationshipList;
import org.ihtsdo.otf.ts.helpers.StringList;
import org.ihtsdo.otf.ts.jpa.services.helper.TerminologyUtility;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.services.ActionService;
import org.ihtsdo.otf.ts.services.handlers.WorkflowListener;

/**
 * JPA enabled implementation of {@link ActionService}.
 */
public class ActionServiceJpa extends ContentServiceJpa implements ActionService {

  /** The token login time map. */
  private static Map<String, Date> tokenTimeoutMap = new HashMap<>();

  /** The token login progressmap. */
  private static Map<String, Float> tokenProgressMap = new HashMap<>();

  /** The Constant default timeout. */
  private final static long defaultTimeout = 7200000;

  /** The timeout. */
  private static long timeout = -1;

  /** The token workflow status set map. */
  private static Map<String, StringList> tokenWorkflowStatusMap =
      new HashMap<>();

  private static Map<String, ClassificationRunner> tokenClassifierMap =
      new HashMap<>();

  /** The listeners enabled. */
  private boolean listenersEnabled = true;

  /** The listener. */
  private static List<WorkflowListener> listeners = null;
  static {
    listeners = new ArrayList<>();
    Properties config;
    try {
      config = ConfigUtility.getConfigProperties();
      String key = "workflow.listener.handler";
      for (String handlerName : config.getProperty(key).split(",")) {
        if (handlerName.isEmpty())
          continue;
        // Add handlers to map
        WorkflowListener handlerService =
            ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
                handlerName, WorkflowListener.class);
        listeners.add(handlerService);
      }
    } catch (Exception e) {
      e.printStackTrace();
      listeners = null;
    }
  }

  /**
   * Instantiates an empty {@link ActionServiceJpa}.
   *
   * @throws Exception the exception
   */
  public ActionServiceJpa() throws Exception {
    super();
    if (listeners == null) {
      throw new Exception(
          "Listeners did not properly initialize, serious error.");
    }
    if (timeout == -1) {
      Properties config = ConfigUtility.getConfigProperties();
      String prop = config.getProperty("action.service.timeout");
      if (prop != null) {
        try {
          timeout = Long.valueOf(prop);
        } catch (Exception e) {
          timeout = defaultTimeout;
        }
      }
    } else {
      timeout = defaultTimeout;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.ContentService#enableListeners()
   */
  @Override
  public void enableListeners() {
    listenersEnabled = true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.ContentService#disableListeners()
   */
  @Override
  public void disableListeners() {
    listenersEnabled = false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ActionService#configureActionService(java.util
   * .Set)
   */
  @Override
  public String configureActionService(StringList workflowStatusList)
    throws Exception {
    String token = UUID.randomUUID().toString();
    tokenTimeoutMap.put(token, new Date(new Date().getTime() + timeout));
    tokenWorkflowStatusMap.put(token, workflowStatusList);
    return token;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.ActionService#getProgress(java.lang.String)
   */
  @Override
  public float getProgress(String sessionToken) throws Exception {
    tokenCheck(sessionToken);
    if (tokenProgressMap.containsKey(sessionToken)) {
      return tokenProgressMap.get(sessionToken);
    } else {
      return 0;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.ActionService#cancel(java.lang.String)
   */
  @Override
  public void cancel(String sessionToken) throws Exception {
    tokenCheck(sessionToken);
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ActionService#prepareToClassify(java.lang.String
   * )
   */
  @SuppressWarnings("unchecked")
  @Override
  public void prepareToClassify(String sessionToken) throws Exception {
    tokenCheck(sessionToken);
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.preClassificationStarted();
      }
    }
    // TODO: figure out how to initialize it
    String terminology = null;
    String version = null;
    Map<String, List<?>> concepts = loadConcepts(getAllConcepts(terminology, version));
    List<org.ihtsdo.classifier.model.StringIDConcept> cEditSnoCons = (List<StringIDConcept>) concepts.get(org.ihtsdo.classifier.model.StringIDConcept.class.getSimpleName());
    List<org.ihtsdo.classifier.model.Relationship> cEditRelationships = (List<org.ihtsdo.classifier.model.Relationship>) concepts.get(org.ihtsdo.classifier.model.Relationship.class.getSimpleName());
    ConceptList rootConcepts = getConcepts(I_Constants.SNOMED_ROOT_CONCEPT, terminology, version);
    Concept rootConcept = rootConcepts.getObjects().get(0);
    ConceptList isaConcepts = getConcepts(GetDescendants.ISA_SCTID, terminology, version);
    Concept isaConcept = isaConcepts.getObjects().get(0);
    ConceptList roleRootConcepts = getConcepts(I_Constants.ATTRIBUTE_ROOT_CONCEPT, terminology, version);
    Concept roleRootConcept = roleRootConcepts.getObjects().get(0);
    int[] roles =  getRoles(terminology, version, isaConcept);
    ClassificationRunner classifier = new ClassificationRunner(cEditSnoCons, cEditRelationships, rootConcept, isaConcept, roleRootConcept, roles);
    classifier.loadConcepts(getAllConcepts(terminology,version));
    tokenClassifierMap.put(sessionToken, classifier);
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.preClassificationFinished();
      }
    }

    
  }
  
  private Map<String, List<?>> loadConcepts(ConceptList allConcepts) throws Exception {
    HashMap<String, List<?>> result = new HashMap<>();
    List<org.ihtsdo.classifier.model.StringIDConcept> cEditSnoCons = new ArrayList<>();
    List<org.ihtsdo.classifier.model.Relationship> cEditRelationships = new ArrayList<>();
    result.put(org.ihtsdo.classifier.model.StringIDConcept.class.getSimpleName(), cEditSnoCons);
    result.put(org.ihtsdo.classifier.model.Relationship.class.getSimpleName(), cEditRelationships);
    int count = 0;
    for (org.ihtsdo.otf.ts.rf2.Concept concept : allConcepts.getObjects()) {
      if(count++ % 2000 == 0)
        manager.flush();
      if (I_Constants.PUBLISHED.equals(concept.getWorkflowStatus())) {
        // Add concept
        StringIDConcept stringIdConcept =
            new StringIDConcept(Integer.parseInt(concept.getObjectId()),
                concept.getObjectId(), Boolean.parseBoolean(concept
                    .getDefinitionStatusId()));
        cEditSnoCons.add(stringIdConcept);
        // Iterate through relationships
        for (org.ihtsdo.otf.ts.rf2.Relationship relationship : concept
            .getRelationships()) {
          if (TerminologyUtility.isStatedRelationship(relationship)) {
            org.ihtsdo.classifier.model.Relationship rel =
                new org.ihtsdo.classifier.model.Relationship(Integer.parseInt(concept.getObjectId()),
                    Integer.parseInt(relationship.getDestinationConcept()
                        .getObjectId()), Integer.parseInt(relationship
                            .getTypeId()), relationship.getRelationshipGroup(),
                            relationship.getObjectId());
            // add stated rels for classification
            cEditRelationships.add(rel);
          }
        }
      }
    }
    return result;
  }

  
  private void getRoleDescendants(Integer topObjId, Integer oid, Set<Integer> descendants, String terminology, String version)
      throws Exception {

      // return if seen already
      if (descendants.contains(oid)) {
        return;
      }

      // Process this concept
      ConceptList concepts = getConcepts(oid.toString(), terminology, version);
      Concept concept = concepts.getObjects().get(0);
      // return if inactive
      if (!concept.isActive()) {
        return;
      }

      if (!oid.equals(topObjId)) {
        descendants.add(oid);
      }
      
      for ( Relationship r : concept.getInverseRelationships()) {
        if(r.isActive() && TerminologyUtility.isHierarchicalIsaRelationship(r))
          getRoleDescendants(topObjId, Integer.valueOf(r.getObjectId()), descendants, terminology, version);
      }
    }

  private int[] getRoles(String terminology, String version, Concept isaConcept) throws Exception {
    Set<Integer> roles = new HashSet<>();
    Integer attributeRoot = Integer.valueOf(I_Constants.ATTRIBUTE_ROOT_CONCEPT);
    getRoleDescendants(attributeRoot, attributeRoot, roles, terminology, version); 
    roles.add(Integer.valueOf(isaConcept.getObjectId()));
    int[] result=new int[roles.size()];
    int resIdx=0;
    for (Integer role:roles){
        result[resIdx++] = role;
        resIdx++;
    }
    Arrays.sort(result);
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.ActionService#classify(java.lang.String)
   */
  @Override
  public void classify(String sessionToken) throws Exception {
    tokenCheck(sessionToken);
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.classificationStarted();
      }
    }
    ClassificationRunner runner = tokenClassifierMap.get(sessionToken);
    if(runner != null)
      runner.execute();
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.classificationFinished();
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ActionService#incrementalClassify(java.lang.
   * String)
   */
  @Override
  public void incrementalClassify(String sessionToken) throws Exception {
    tokenCheck(sessionToken);
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.classificationStarted();
      }
    }

    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.classificationFinished();
      }
    }

    throw new UnsupportedOperationException();

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.ActionService#getClassificationEquivalents
   * (java.lang.String)
   */
  @Override
  public KeyValuePairLists getClassificationEquivalents(String sessionToken)
    throws Exception {
    tokenCheck(sessionToken);
    ClassificationRunner runner = tokenClassifierMap.get(sessionToken);
    KeyValuePairLists result = new KeyValuePairLists();
    if(runner != null) {
      EquivalentClasses equivalents = runner.getEquivalentClasses();
      KeyValuePairList list = new KeyValuePairList();
      result.addKeyValuePairList(list);
      int setNumber = 1;
      for(ConceptGroup conceptGroup:equivalents) {
        for(org.ihtsdo.classifier.model.Concept concept:conceptGroup) {
          KeyValuePair pair = new KeyValuePair(String.valueOf(setNumber), String.valueOf(concept.id));
          list.addKeyValuePair(pair);
        }
        setNumber++;
      }
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ActionService#getOldInferredRelationships(java
   * .lang.String)
   */
  @Override
  public RelationshipList getOldInferredRelationships(String sessionToken)
    throws Exception {
    tokenCheck(sessionToken);
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ActionService#getNewInferredRelationships(java
   * .lang.String)
   */
  @Override
  public RelationshipList getNewInferredRelationships(String sessionToken)
    throws Exception {
    tokenCheck(sessionToken);
    throw new UnsupportedOperationException();
  }

  /**
   * Token check.
   *
   * @param token the token
   * @throws Exception if the token has timed out.
   */
  @SuppressWarnings("static-method")
  private void tokenCheck(String token) throws Exception {
    if (!tokenTimeoutMap.containsKey(token)) {
      throw new LocalException("Session token is invalid");
    }
    if (tokenTimeoutMap.get(token).before(new Date())) {
      tokenWorkflowStatusMap.remove(token);
      throw new LocalException("Session token has expired");
    }
    tokenTimeoutMap.put(token, new Date(new Date().getTime() + timeout));
  }

  @Override
  public void addNewInferredRelationships(String sessionToken) throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void retireOldInferredRelationships(String sessionToken)
    throws Exception {
    // TODO Auto-generated method stub
    
  }
}
