/*
 * 
 */
package org.ihtsdo.otf.ts.jpa.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.ihtsdo.otf.ts.Project;
import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.helpers.KeyValuesMap;
import org.ihtsdo.otf.ts.helpers.LocalException;
import org.ihtsdo.otf.ts.helpers.RelationshipList;
import org.ihtsdo.otf.ts.helpers.RelationshipListJpa;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.services.ActionService;
import org.ihtsdo.otf.ts.services.handlers.Classifier;
import org.ihtsdo.otf.ts.services.handlers.WorkflowListener;

/**
 * JPA enabled implementation of {@link ActionService}.
 */
public class ActionServiceJpa extends ContentServiceJpa implements
    ActionService {

  /** The token login time map. */
  private static Map<String, ActionServiceConfig> tokenConfigMap =
      new HashMap<>();

  /** The Constant default timeout. */
  private final static long defaultTimeout = 7200000;

  /** The timeout. */
  private static long actualTimeout = -1;

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
    if (actualTimeout == -1) {
      Properties config = ConfigUtility.getConfigProperties();
      String prop = config.getProperty("action.service.timeout");
      if (prop != null) {
        try {
          actualTimeout = Long.valueOf(prop);
        } catch (Exception e) {
          actualTimeout = defaultTimeout;
        }
      }
    } else {
      actualTimeout = defaultTimeout;
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

  @Override
  public String configureActionService(Project project) throws Exception {
    String token = UUID.randomUUID().toString();
    ActionServiceConfig config = new ActionServiceConfig(project);
    config.setTimeout(new Date(new Date().getTime() + actualTimeout));
    tokenConfigMap.put(token, config);
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
    if (tokenConfigMap.containsKey(sessionToken)) {
      return tokenConfigMap.get(sessionToken).getProgress();
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
    tokenConfigMap.get(sessionToken).setRequestCancel(true);
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

    /** The classifier. */
    Classifier classifier = null;

    try {
      Properties config;
      config = ConfigUtility.getConfigProperties();
      String key = "classifier.handler";
      String handlerName = config.getProperty(key);
      if (handlerName == null || handlerName.isEmpty()) {
        throw new Exception("Undefined classifier handler");
      }
      // Set handler up
      Classifier handler =
          ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
              handlerName, Classifier.class);
      classifier = handler;

    } catch (Exception e) {
      e.printStackTrace();
      classifier = null;
    }

    if (classifier == null) {
      throw new Exception("Unable to instantiate classifier");
    }

    if (listenersEnabled) {
      for (WorkflowListener listener : listeners) {
        listener.preClassificationStarted();
      }
    }

    ActionServiceConfig config = tokenConfigMap.get(sessionToken);
    if (config == null) {
      throw new Exception(
          "Cannot pre-classify until configure has been called.");
    }

    classifier.setProject(config.getProject());

    // TODO; get all this from metadata service
    // Set the root id
    String SNOMED_ROOT_CONCEPT = "138875005";
    classifier.setRootId(Integer.valueOf(getSingleConcept(SNOMED_ROOT_CONCEPT,
        config.getProject().getTerminology(),
        config.getProject().getTerminologyVersion()).getObjectId()));

    // Set the isa id
    String ISA_SCTID = "116680003";
    classifier.setIsaRelId(Integer.valueOf(getSingleConcept(ISA_SCTID,
        config.getProject().getTerminology(),
        config.getProject().getTerminologyVersion()).getObjectId()));

    // Set attribure root
    String ATTRIBUTE_ROOT_CONCEPT = "410662002";
    classifier.setRoleRootId(Integer.valueOf(getSingleConcept(
        ATTRIBUTE_ROOT_CONCEPT, config.getProject().getTerminology(),
        config.getProject().getTerminologyVersion()).getObjectId()));

    classifier.loadConcepts();

    // Handle cancel
    if (tokenConfigMap.get(sessionToken).isRequestCancel()) {
      if (listenersEnabled) {
        for (WorkflowListener listener : listeners) {
          listener.cancel();
        }
      }
      return;
    }

    classifier.loadRoles();

    // Handle cancel
    if (tokenConfigMap.get(sessionToken).isRequestCancel()) {
      if (listenersEnabled) {
        for (WorkflowListener listener : listeners) {
          listener.cancel();
        }
      }
      return;
    }

    tokenConfigMap.get(sessionToken).setClassifier(classifier);
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

    Classifier runner = tokenConfigMap.get(sessionToken).getClassifier();
    if (runner != null) {
      runner.compute();
    }

    // Handle cancel
    if (tokenConfigMap.get(sessionToken).isRequestCancel()) {
      if (listenersEnabled) {
        for (WorkflowListener listener : listeners) {
          listener.cancel();
        }
      }
      return;
    }

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

    // Handle cancel
    if (tokenConfigMap.get(sessionToken).isRequestCancel()) {
      if (listenersEnabled) {
        for (WorkflowListener listener : listeners) {
          listener.cancel();
        }
      }
      return;
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
   * @see
   * org.ihtsdo.otf.ts.services.ActionService#getClassificationEquivalents(java
   * .lang.String)
   */
  @Override
  public KeyValuesMap getClassificationEquivalents(String sessionToken)
    throws Exception {
    tokenCheck(sessionToken);
    Classifier runner = tokenConfigMap.get(sessionToken).getClassifier();
    if (runner != null) {
      KeyValuesMap map = runner.getEquivalentClasses();
      return map;
    }
    return null;
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
    List<Relationship> rels =
        tokenConfigMap.get(sessionToken).getClassifier()
            .getOldInferredRelationships();
    RelationshipList list = new RelationshipListJpa();
    list.setObjects(rels);
    return list;
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
    List<Relationship> rels =
        tokenConfigMap.get(sessionToken).getClassifier()
            .getNewInferredRelationships();
    RelationshipList list = new RelationshipListJpa();
    list.setObjects(rels);
    return list;
  }

  /**
   * Token check.
   *
   * @param token the token
   * @throws Exception if the token has timed out.
   */
  @SuppressWarnings("static-method")
  private void tokenCheck(String token) throws Exception {
    if (!tokenConfigMap.containsKey(token)) {
      throw new LocalException("Session token is invalid");
    }
    if (tokenConfigMap.get(token).getTimeout().before(new Date())) {
      tokenConfigMap.remove(token);
      throw new LocalException("Session token has expired");
    }
    tokenConfigMap.get(token).setTimeout(
        new Date(new Date().getTime() + actualTimeout));
  }

  @Override
  public void addNewInferredRelationships(String sessionToken) throws Exception {
    // TODO Auto-generated method stub

    // Get the new inferred rels and add them
  }

  @Override
  public void retireOldInferredRelationships(String sessionToken)
    throws Exception {
    // TODO Auto-generated method stub

    // get the old inferred rels and retire them
  }

  /**
   * Local configuration object. NEVER expose this outside this class.
   */
  private class ActionServiceConfig {

    /** The project. */
    private Project project;

    /** The progress. */
    private int progress = -1;

    /** The timeout. */
    private Date timeout;

    /** The classifier. */
    private Classifier classifier;

    /** The request cancel. */
    private boolean requestCancel;

    /**
     * Instantiates a {@link ActionServiceConfig} from the specified parameters.
     *
     * @param project the project
     */
    ActionServiceConfig(Project project) {
      this.project = project;
    }

    /**
     * Returns the project.
     *
     * @return the project
     */
    public Project getProject() {
      return project;
    }

    /**
     * Returns the progress.
     *
     * @return the progress
     */
    public int getProgress() {
      return progress;
    }

    /**
     * Sets the progress.
     *
     * @param progress the progress
     */
    @SuppressWarnings("unused")
    public void setProgress(int progress) {
      this.progress = progress;
    }

    /**
     * Returns the timeout.
     *
     * @return the timeout
     */
    public Date getTimeout() {
      return timeout;
    }

    /**
     * Sets the timeout.
     *
     * @param timeout the timeout
     */
    public void setTimeout(Date timeout) {
      this.timeout = timeout;
    }

    /**
     * Returns the classifier.
     *
     * @return the classifier
     */
    public Classifier getClassifier() {
      return classifier;
    }

    /**
     * Sets the classifier.
     *
     * @param classifier the classifier
     */
    public void setClassifier(Classifier classifier) {
      this.classifier = classifier;
    }

    /**
     * Indicates whether or not request cancel is the case.
     *
     * @return <code>true</code> if so, <code>false</code> otherwise
     */
    public boolean isRequestCancel() {
      return requestCancel;
    }

    /**
     * Sets the request cancel.
     *
     * @param requestCancel the request cancel
     */
    public void setRequestCancel(boolean requestCancel) {
      this.requestCancel = requestCancel;
    }
  }

}
