/*
 * 
 */
package org.ihtsdo.otf.ts.services.handlers;

import java.util.List;

import org.ihtsdo.otf.ts.Project;
import org.ihtsdo.otf.ts.algo.Algorithm;
import org.ihtsdo.otf.ts.helpers.Configurable;
import org.ihtsdo.otf.ts.helpers.KeyValuesMap;
import org.ihtsdo.otf.ts.rf2.Relationship;

/**
 * Generically represents a classifier based around integer ids.
 */
public interface Classifier extends Algorithm, Configurable {

  /**
   * Returns the equivalent classes.
   *
   * @return the equivalent classes
   */
  public KeyValuesMap getEquivalentClasses();

  /**
   * Returns the new inferred relationships.
   *
   * @return the new inferred relationships
   */
  public List<Relationship> getNewInferredRelationships();
  
  /**
   * Returns the old inferred relationships.
   *
   * @return the old inferred relationships
   */
  public List<Relationship> getOldInferredRelationships();
  
  /**
   * Sets the root id.
   *
   * @param rootId the root id
   */
  public void setRootId(int rootId);

  /**
   * Sets the isa rel id.
   *
   * @param isaRelId the isa rel id
   */
  public void setIsaRelId(int isaRelId);

  /**
   * Sets the role root id.
   *
   * @param roleRootId the role root id
   */
  public void setRoleRootId(int roleRootId);

  /**
   * Sets the project.
   *
   * @param project the project
   */
  public void setProject(Project project);

  /**
   * Load concepts.
   *
   * @throws Exception the exception
   */
  public void loadConcepts() throws Exception;

  /**
   * Load roles.
   *
   * @throws Exception the exception
   */
  public void loadRoles() throws Exception;
}
