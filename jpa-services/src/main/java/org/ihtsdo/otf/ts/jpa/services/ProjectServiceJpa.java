package org.ihtsdo.otf.ts.jpa.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.Project;
import org.ihtsdo.otf.ts.User;
import org.ihtsdo.otf.ts.UserRole;
import org.ihtsdo.otf.ts.helpers.ConceptList;
import org.ihtsdo.otf.ts.helpers.ConceptListJpa;
import org.ihtsdo.otf.ts.helpers.ProjectList;
import org.ihtsdo.otf.ts.helpers.ProjectListJpa;
import org.ihtsdo.otf.ts.jpa.ProjectJpa;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.services.ContentService;
import org.ihtsdo.otf.ts.services.ProjectService;

/**
 * JPA enabled implementation of {@link ContentService}.
 */
public class ProjectServiceJpa extends RootServiceJpa implements ProjectService {

  /**
   * Instantiates an empty {@link ProjectServiceJpa}.
   *
   * @throws Exception the exception
   */
  public ProjectServiceJpa() throws Exception {
    super();
  }

  /**
   * Returns the concepts in scope.
   *
   * @param project the project
   * @return the concepts in scope
   * @throws Exception the exception
   */
  @Override
  public ConceptList getConceptsInScope(Project project) throws Exception {
    Logger.getLogger(getClass()).info(
        "Content Service - get project scope - " + project);
    if (project == null) {
      throw new Exception("Unexpected null project");
    }
    ContentService contentService = new ContentServiceJpa();
    Set<Concept> include = new HashSet<>();
    for (String terminologyId : project.getScopeConcepts()) {
      // get concept
      Concept concept =
          contentService.getSingleConcept(terminologyId,
              project.getTerminology(), project.getTerminologyVersion());
      include.add(concept);
      // get descendants
      if (project.getScopeDescendantsFlag()) {
        for (Concept desc : contentService.getDescendantConcepts(concept, null)
            .getObjects()) {
          include.add(desc);
        }
      }
    }
    Logger.getLogger(getClass()).info("  include count = " + include.size());

    Set<Concept> exclude = new HashSet<>();
    for (String terminologyId : project.getScopeExcludesConcepts()) {
      // get concept
      Concept concept =
          contentService.getSingleConcept(terminologyId,
              project.getTerminology(), project.getTerminologyVersion());
      exclude.add(concept);
      // get descendants
      if (project.getScopeExcludesDescendantsFlag()) {
        for (Concept desc : contentService.getDescendantConcepts(concept, null)
            .getObjects()) {
          exclude.add(desc);
        }
      }
    }
    Logger.getLogger(getClass()).info("  exclude count = " + exclude.size());

    include.removeAll(exclude);
    Logger.getLogger(getClass()).info("  count = " + include.size());
    ConceptList list = new ConceptListJpa();
    list.setObjects(new ArrayList<>(include));
    contentService.close();
    return list;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.ContentService#getProject(java.lang.Long)
   */
  @Override
  public Project getProject(Long id) {
    Logger.getLogger(getClass()).debug("Content Service - get project " + id);
    Project project = manager.find(ProjectJpa.class, id);
    return project;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.ContentService#getProjects()
   */
  @Override
  @SuppressWarnings("unchecked")
  public ProjectList getProjects() {
    Logger.getLogger(getClass()).debug("Content Service - get projects");
    javax.persistence.Query query =
        manager.createQuery("select a from ProjectJpa a");
    try {
      List<Project> concepts = query.getResultList();
      ProjectList list = new ProjectListJpa();
      list.setObjects(concepts);
      return list;
    } catch (NoResultException e) {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#getUserRoleForProject(java.lang
   * .String, java.lang.Long)
   */
  @Override
  public UserRole getUserRoleForProject(String username, Long projectId)
    throws Exception {
    Project project = getProject(projectId);
    if (project == null) {
      throw new Exception("No project found for " + projectId);
    }

    // check admin
    for (User user : project.getAdministrators()) {
      if (username.equals(user.getUserName())) {
        return UserRole.ADMINISTRATOR;
      }
    }

    // check lead
    for (User user : project.getLeads()) {
      if (username.equals(user.getUserName())) {
        return UserRole.LEAD;
      }
    }

    // check author
    for (User user : project.getAuthors()) {
      if (username.equals(user.getUserName())) {
        return UserRole.AUTHOR;
      }
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#addProject(org.ihtsdo.otf.ts.
   * Project)
   */
  @Override
  public Project addProject(Project project) {
    Logger.getLogger(getClass()).debug(
        "Content Service - add project - " + project);
    try {
      // Set last modified date
      project.setLastModified(new Date());

      // add the project
      if (getTransactionPerOperation()) {
        tx = manager.getTransaction();
        tx.begin();
        manager.persist(project);
        tx.commit();
      } else {
        manager.persist(project);
      }
      return project;
    } catch (Exception e) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw e;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#updateProject(org.ihtsdo.otf.
   * ts.Project)
   */
  @Override
  public void updateProject(Project project) {
    Logger.getLogger(getClass()).debug(
        "Content Service - update project - " + project);

    try {
      // Set modification date
      project.setLastModified(new Date());

      // update
      if (getTransactionPerOperation()) {
        tx = manager.getTransaction();
        tx.begin();
        manager.merge(project);
        tx.commit();
      } else {
        manager.merge(project);
      }
    } catch (Exception e) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw e;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.ContentService#removeProject(java.lang.Long)
   */
  @Override
  public void removeProject(Long id) {
    Logger.getLogger(getClass())
        .debug("Content Service - remove project " + id);
    try {
      // Get transaction and object
      tx = manager.getTransaction();
      Project project = manager.find(ProjectJpa.class, id);

      // Set modification date
      project.setLastModified(new Date());

      // Remove
      if (getTransactionPerOperation()) {
        // remove refset member
        tx.begin();
        if (manager.contains(project)) {
          manager.remove(project);
        } else {
          manager.remove(manager.merge(project));
        }
        tx.commit();
      } else {
        if (manager.contains(project)) {
          manager.remove(project);
        } else {
          manager.remove(manager.merge(project));
        }
      }
    } catch (Exception e) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw e;
    }
  }

}
