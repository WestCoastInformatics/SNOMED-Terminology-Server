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
import org.ihtsdo.otf.ts.jpa.services.MetadataServiceJpa;
import org.ihtsdo.otf.ts.services.ContentService;
import org.ihtsdo.otf.ts.services.MetadataService;

/**
 * Goal which removes a terminology from a database.
 * 
 * See admin/remover/pom.xml for sample usage
 * 
 * @goal remove-terminology
 * 
 * @phase package
 */
public class TerminologyRemoverMojo extends AbstractMojo {

  /**
   * Name of terminology to be removed.
   * @parameter
   * @required
   */
  private String terminology;

  /**
   * Instantiates a {@link TerminologyRemoverMojo} from the specified
   * parameters.
   * 
   */
  public TerminologyRemoverMojo() {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.maven.plugin.Mojo#execute()
   */
  @Override
  public void execute() throws MojoFailureException {
    getLog().info("Starting removing " + terminology + " data ...");
    try {
      MetadataService metadataService = new MetadataServiceJpa();
      String terminologyVersion = metadataService.getLatestVersion(terminology);
      metadataService.close();
      if (terminologyVersion != null) {
        ContentService contentService = new ContentServiceJpa();
        contentService.clearConcepts(terminology, terminologyVersion);
        contentService.close();
      }
      getLog().info("done ...");
    } catch (Exception e) {
      e.printStackTrace();
      throw new MojoFailureException("Unexpected exception:", e);
    }
  }

}
