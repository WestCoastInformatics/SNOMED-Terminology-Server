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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.helper.TerminologyUtility;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.services.ContentService;

/**
 * Goal which removes demo data from the database to re-run it.
 * 
 * See admin/remover/pom.xml for sample usage
 * 
 * @goal remove-demo
 * @phase package
 */
public class DemoRemoverMojo extends AbstractMojo {

  /**
   * Instantiates a {@link DemoRemoverMojo} from the specified
   * parameters.
   */
  public DemoRemoverMojo() {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.maven.plugin.Mojo#execute()
   */
  @Override
  public void execute() throws MojoFailureException {
    getLog().info("Starting demo cleanup");
    try {
      ContentService service = new ContentServiceJpa();
     
      // Remove any children of
      Concept concept =
          service.getSingleConcept("10001005", "SNOMEDCT", "20140731");
      for (Relationship rel : concept.getInverseRelationships()) {
        if (TerminologyUtility.isHierarchicalIsaRelationship(rel)) {
          getLog().info(
              "  Removing " + rel.getSourceConcept().getTerminologyId());
          service.removeConcept(rel.getSourceConcept().getId());
        }
      }
      
      service.close();
      getLog().info("done ...");
    } catch (Exception e) {
      e.printStackTrace();
      throw new MojoFailureException("Unexpected exception:", e);
    }
  }

}
