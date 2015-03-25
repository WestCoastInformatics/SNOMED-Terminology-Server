/*
 * 
 */
package org.ihtsdo.otf.ts.rest;

import org.ihtsdo.otf.ts.Project;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ProjectList;
import org.ihtsdo.otf.ts.jpa.ProjectJpa;

/**
 * Represents a content available via a REST service.
 */
public interface ProjectServiceRest {

  /**
   * Adds the project.
   *
   * @param project the project
   * @param authToken the auth token
   * @return the project
   * @throws Exception the exception
   */
  public Project addProject(ProjectJpa project, String authToken)
    throws Exception;

  /**
   * Returns the concepts in scope.
   *
   * @param projectId the project id
   * @param authToken the auth token
   * @return the concepts in scope
   * @throws Exception the exception
   */
  public ConceptList getConceptsInScope(Long projectId, String authToken)
    throws Exception;

  /**
   * Returns the project.
   *
   * @param id the id
   * @param authToken the auth token
   * @return the project
   * @throws Exception the exception
   */
  public Project getProject(Long id, String authToken) throws Exception;

  /**
   * Returns the projects.
   *
   * @param authToken the auth token
   * @return the projects
   * @throws Exception the exception
   */
  public ProjectList getProjects(String authToken) throws Exception;

}