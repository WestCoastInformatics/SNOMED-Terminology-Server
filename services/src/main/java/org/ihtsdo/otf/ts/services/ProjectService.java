/*
 * 
 */
package org.ihtsdo.otf.ts.services;

import org.ihtsdo.otf.ts.Project;
import org.ihtsdo.otf.ts.UserRole;
import org.ihtsdo.otf.ts.helpers.PfsParameter;
import org.ihtsdo.otf.ts.helpers.ProjectList;
import org.ihtsdo.otf.ts.helpers.SearchResultList;

/**
 * Generically represents a service for accessing {@link Project} information.
 */
public interface ProjectService extends RootService {

  /**
   * Returns the concepts in scope.
   *
   * @param project the project
   * @param pfs the pfs
   * @return the concepts in scope
   * @throws Exception the exception
   */
  public SearchResultList findConceptsInScope(Project project, PfsParameter pfs)
    throws Exception;

  /**
   * Returns the project.
   *
   * @param id the id
   * @return the project
   */
  public Project getProject(Long id);

  /**
   * Adds the project.
   *
   * @param project the project
   * @return the project
   */
  public Project addProject(Project project);

  /**
   * Update project.
   *
   * @param project the project
   */
  public void updateProject(Project project);

  /**
   * Removes the project.
   *
   * @param projectId the project id
   */
  public void removeProject(Long projectId);

  /**
   * Returns the projects.
   *
   * @return the projects
   */
  public ProjectList getProjects();

  /**
   * Returns the user role for project.
   *
   * @param username the username
   * @param projectId the project id
   * @return the user role for project
   * @throws Exception the exception
   */
  public UserRole getUserRoleForProject(String username, Long projectId)
    throws Exception;

}