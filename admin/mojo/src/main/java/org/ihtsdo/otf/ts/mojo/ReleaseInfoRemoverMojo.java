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
import org.ihtsdo.otf.ts.ReleaseInfo;
import org.ihtsdo.otf.ts.jpa.services.HistoryServiceJpa;
import org.ihtsdo.otf.ts.services.HistoryService;

/**
 * Goal which removes one or more {@link ReleaseInfo}s from the database.
 * 
 * See admin/remover/pom.xml for sample usage
 * 
 * @goal remove-release-info
 * @phase package
 */
public class ReleaseInfoRemoverMojo extends AbstractMojo {

  /**
   * The terminology
   * 
   * @parameter
   * @required
   */
  private String terminology = null;

  /**
   * A comma-separated list of release info names to remove
   * 
   * @parameter
   */
  private String releaseInfoNames;

  /**
   * Instantiates a {@link ReleaseInfoRemoverMojo} from the specified
   * parameters.
   */
  public ReleaseInfoRemoverMojo() {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.maven.plugin.Mojo#execute()
   */
  @Override
  public void execute() throws MojoFailureException {
    getLog().info("Starting removing ReleaseInfos");
    try {
      HistoryService service = new HistoryServiceJpa();
      if (releaseInfoNames == null || releaseInfoNames.isEmpty()) {
        for (ReleaseInfo releaseInfo : service.getReleaseHistory(terminology).getObjects()) {
          getLog().info("  Removing " + releaseInfo.getName());
          service.removeReleaseInfo(releaseInfo.getId());
        }
      } else {
        for (String releaseInfoValue : releaseInfoNames.split(",")) {
          getLog().info("  Removing " + releaseInfoValue);
          service.removeReleaseInfo(service.getReleaseInfo(terminology,releaseInfoValue)
              .getId());
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
