package org.ihtsdo.otf.ts.jpa.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.ihtsdo.classifier.ClassificationRunner;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.KeyValuesMap;
import org.ihtsdo.otf.ts.helpers.LocalException;
import org.ihtsdo.otf.ts.helpers.RelationshipList;
import org.ihtsdo.otf.ts.helpers.StringList;
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
  @Override
  public void prepareToClassify(String sessionToken) throws Exception {
    tokenCheck(sessionToken);
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.preClassificationStarted();
      }
    }
    
    ClassificationRunner classifier = new ClassificationRunner();
    // TODO: figure out how to initialize it
    String terminology = null;
    String version = null;
    classifier.loadConcepts(getAllConcepts(terminology,version));
    classifier.setRelationshipIds(getAllRelationshipIds(terminology, version));
    tokenClassifierMap.put(sessionToken, classifier);
    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.preClassificationFinished();
      }
    }

    
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
  public KeyValuesMap getClassificationEquivalents(String sessionToken)
    throws Exception {
    tokenCheck(sessionToken);
    ClassificationRunner runner = tokenClassifierMap.get(sessionToken);
//    if(runner != null)
//      runner.execute();
    throw new UnsupportedOperationException();

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

  public List<String> getAllRelationshipIds(String terminology,
      String version) {
      Logger.getLogger(ContentServiceJpa.class).debug(
          "Content Service - get all relationship terminology ids " + terminology
              + "/" + version);
      javax.persistence.Query query =
          manager
              .createQuery(
                  "select distinct typeId from RelationshipJpa c where terminology=:terminology and terminologyVersion=:version")
              .setParameter("terminology", terminology)
              .setParameter("version", version);

      @SuppressWarnings("unchecked")
      List<String> relationshipIds = query.getResultList();
      return relationshipIds;

    }
}
