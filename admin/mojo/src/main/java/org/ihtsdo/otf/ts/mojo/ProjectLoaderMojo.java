/**
 * Copyright (c) 2012 International Health Terminology Standards Development
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
package org.ihtsdo.otf.ts.mojo;

import java.util.Arrays;
import java.util.HashSet;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.ihtsdo.otf.ts.Project;
import org.ihtsdo.otf.ts.jpa.ProjectJpa;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.SecurityServiceJpa;
import org.ihtsdo.otf.ts.services.ContentService;
import org.ihtsdo.otf.ts.services.SecurityService;

/**
 * Goal which adds a {@link Project} to the database.
 * 
 * See admin/loader/pom.xml for sample usage
 * 
 * @goal add-project
 * @phase package
 */
public class ProjectLoaderMojo extends AbstractMojo {

  /**
   * The name
   * 
   * @parameter
   * @required
   */
  private String name = null;

  /**
   * The description
   * 
   * @parameter
   * @required
   */
  private String description = null;

  /**
   * The terminology
   * 
   * @parameter
   * @required
   */
  private String terminology = null;

  /**
   * The terminology version
   * 
   * @parameter
   * @required
   */
  private String terminologyVersion = null;

  /**
   * The admin user.
   * 
   * @parameter
   * @required
   */
  private String adminUser = null;

  /**
   * The scope concepts.
   * 
   * @parameter
   * @required
   */
  private String scopeConcepts= "";
  /**
   * The scope descendants flag
   * 
   * @parameter
   */
  private boolean scopeDescendantsFlag = true;

  /**
   * The scope excludes concepts.
   * 
   * @parameter
   */
  private String scopeExcludesConcepts = "";
  
  /**
   * The scope excludes descendants flag
   * 
   * @parameter
   */
  private boolean scopeExcludesDescendantsFlag = true;

  /**
   * Instantiates a {@link ProjectLoaderMojo} from the specified parameters.
   */
  public ProjectLoaderMojo() {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.maven.plugin.Mojo#execute()
   */
  @Override
  public void execute() throws MojoFailureException {
    getLog().info("Starting at project");
    getLog().info("  name = " + name);
    getLog().info("  description= " + description);
    getLog().info("  terminology = " + terminology);
    getLog().info("  terminologyVersion = " + terminologyVersion);
    getLog().info("  scope concepts = " + scopeConcepts);
    getLog().info("  scope desc flag = " + scopeDescendantsFlag);
    getLog().info("  scope excludes concepts = " + scopeExcludesConcepts);
    getLog().info("  scope excludes desc flag = " + scopeExcludesDescendantsFlag);
    try {
      ContentService contentService = new ContentServiceJpa();
      SecurityService securityService = new SecurityServiceJpa();
      
      Project project = new ProjectJpa();
      project.setName(name);
      project.setDescription(description);
      project.addAdministrator(securityService.getUser(adminUser));
      project.setPublic(true);
      project.setScopeConcepts(new HashSet<String>(Arrays.asList(scopeConcepts.split(","))));
      project.setScopeDescendantsFlag(scopeDescendantsFlag);
      project.setScopeExcludesConcepts(new HashSet<String>(Arrays.asList(scopeExcludesConcepts.split(","))));
      project.setScopeExcludesDescendantsFlag(scopeExcludesDescendantsFlag);
      project.setTerminology(terminology);
      project.setTerminologyVersion(terminologyVersion);

      // check for this project
      for (Project p : contentService.getProjects().getObjects()) {
        if (p.getName().equals(project.getName()) &&
            p.getDescription().equals(project.getDescription())) {
          throw new Exception("A project with this name and description already exists.");
        }
      }
      // Add the project
      contentService.addProject(project);
      securityService.close();
      contentService.close();
      getLog().info("done ...");
    } catch (Exception e) {
      e.printStackTrace();
      throw new MojoFailureException("Unexpected exception:", e);
    }
  }
}
