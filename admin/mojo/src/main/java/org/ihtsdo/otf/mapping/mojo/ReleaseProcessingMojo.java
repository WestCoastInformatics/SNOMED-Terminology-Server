package org.ihtsdo.otf.mapping.mojo;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.ihtsdo.otf.mapping.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.mapping.services.ContentService;

/**
 * Loads unpublished complex maps.
 * 
 * Sample execution:
 * 
 * <pre>
 *     <profile>
 *       <id>Release</id>
 *       <build>
 *         <plugins>
 *           <plugin>
 *             <groupId>org.ihtsdo.otf.mapping</groupId>
 *             <artifactId>mapping-admin-mojo</artifactId>
 *             <version>${project.version}</version>
 *             <executions>
 *               <execution>
 *                 <id>release</id>
 *                 <phase>package</phase>
 *                 <goals>
 *                   <goal>release</goal>
 *                 </goals>
 *                 <configuration>
 *                   <refSetId>${refset.id}</refSetId>
 *                   <outputDirName>$(output.dir)</outputDirName>
 *                   <effectiveTime>${time}</effectiveTime>
 *                   <moduleId>${module.id}</moduleId>
 *                 </configuration>
 *               </execution>
 *             </executions>
 *           </plugin>
 *         </plugins>
 *       </build>
 *     </profile>
 * </pre>
 * 
 * @goal release
 * @phase package
 */
public class ReleaseProcessingMojo extends AbstractMojo {

  /**
   * The refSet id
   * 
   * @parameter refSetId
   */
  private String refSetId = null;

  /**
   * The refSet id
   * 
   * @parameter outputDirName
   */
  private String outputDirName = null;

  /**
   * The effective time of release
   * 
   * @parameter effectiveTime
   */
  private String effectiveTime = null;

  /**
   * The module id.
   * 
   * @parameter moduleId
   */
  private String moduleId = null;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info("Processing release for ref set ids: " + refSetId);

    if (refSetId == null) {
      throw new MojoExecutionException("You must specify a refSetId.");
    }

    if (refSetId == null) {
      throw new MojoExecutionException(
          "You must specify an output file directory.");
    }

    File outputDir = new File(outputDirName);
    if (!outputDir.isDirectory())
      throw new MojoExecutionException("Output file directory ("
          + outputDirName + ") could not be found.");

    if (effectiveTime == null)
      throw new MojoExecutionException("You must specify a release time");

    if (moduleId == null)
      throw new MojoExecutionException("You must specify a module id");

    try {

      ContentService service = new ContentServiceJpa();

      getLog().info("done ...");
      service.close();

    } catch (Exception e) {
      e.printStackTrace();
      throw new MojoExecutionException("Performing release processing failed.",
          e);
    }

  }

}
