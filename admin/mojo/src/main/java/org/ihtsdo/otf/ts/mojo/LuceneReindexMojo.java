package org.ihtsdo.otf.ts.mojo;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.hibernate.CacheMode;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.services.helpers.ConfigUtility;

/**
 * Goal which makes lucene indexes based on hibernate-search annotations.
 * 
 * Sample execution:
 * 
 * <pre>
 *     <plugin>
 *        <groupId>org.ihtsdo.otf.mapping</groupId>
 *        <artifactId>mapping-admin-mojo</artifactId>
 *        <version>${project.version}</version>
 *        <executions>
 *          <execution>
 *            <id>reindex</id>
 *            <phase>package</phase>
 *            <goals>
 *              <goal>reindex</goal>
 *            </goals>
 *            <configuration>
 *                 <indexedObjects>${indexedObjects}</indexedObjects>
 *               </configuration>
 *          </execution>
 *        </executions>
 *      </plugin>
 * </pre>
 * 
 * @goal reindex
 * 
 * @phase package
 */
public class LuceneReindexMojo extends AbstractMojo {

  /** The manager. */
  private EntityManager manager;

  /**
   * The specified objects to index
   * @parameter
   */

  private String indexedObjects;

  /**
   * Instantiates a {@link LuceneReindexMojo} from the specified parameters.
   */
  public LuceneReindexMojo() {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.maven.plugin.Mojo#execute()
   */
  @Override
  public void execute() throws MojoFailureException {

    // set of objects to be re-indexed
    Set<String> objectsToReindex = new HashSet<>();

    // if no parameter specified, re-index all objects
    if (indexedObjects == null) {
      objectsToReindex.add("ConceptJpa");

      // otherwise, construct set of indexed objects
    } else {

      // remove white-space and split by comma
      String[] objects = indexedObjects.replaceAll(" ", "").split(",");

      // add each value to the set
      for (String object : objects)
        objectsToReindex.add(object);

    }
    getLog().info("Starting reindexing for:");
    for (String objectToReindex : objectsToReindex) {
      getLog().info("  " + objectToReindex);
    }

    try {
      Properties config = ConfigUtility.getConfigProperties();

      EntityManagerFactory factory =
          Persistence.createEntityManagerFactory("TermServiceDS", config);

      manager = factory.createEntityManager();

      // full text entity manager
      FullTextEntityManager fullTextEntityManager =
          Search.getFullTextEntityManager(manager);

      // Concepts
      if (objectsToReindex.contains("ConceptJpa")) {
        getLog().info("  Creating indexes for ConceptJpa");
        fullTextEntityManager.purgeAll(ConceptJpa.class);
        fullTextEntityManager.flushToIndexes();
        fullTextEntityManager.createIndexer(ConceptJpa.class)
            .batchSizeToLoadObjects(100).cacheMode(CacheMode.NORMAL)
            .threadsToLoadObjects(4).threadsForSubsequentFetching(8)
            .startAndWait();

        objectsToReindex.remove("ConceptJpa");
      }

      if (objectsToReindex.size() != 0) {
        throw new MojoFailureException(
            "The following objects were specified for re-indexing, but do not exist as indexed objects: "
                + objectsToReindex.toString());
      }

      // Cleanup
      getLog().info("done ...");
      manager.close();
      factory.close();

    } catch (Throwable e) {
      e.printStackTrace();
      throw new MojoFailureException("Unexpected exception:", e);
    }

  }

}
