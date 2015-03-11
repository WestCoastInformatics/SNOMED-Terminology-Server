package org.ihtsdo.otf.ts.jpa.algo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.CacheMode;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.ihtsdo.otf.ts.algo.Algorithm;
import org.ihtsdo.otf.ts.jpa.ProjectJpa;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.RootServiceJpa;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;
import org.ihtsdo.otf.ts.services.ContentService;
import org.ihtsdo.otf.ts.services.helpers.ProgressEvent;
import org.ihtsdo.otf.ts.services.helpers.ProgressListener;

/**
 * Implementation of an algorithm to compute transitive closure using the
 * {@link ContentService}.
 */
public class LuceneReindexAlgorithm extends RootServiceJpa implements
    Algorithm {

  /** Listeners */
  private List<ProgressListener> listeners = new ArrayList<>();

  /** The request cancel flag. */
  boolean requestCancel = false;

  /** The terminology. */
  private String indexedObjects;
  
  /**  The full text entity manager. */
  private FullTextEntityManager fullTextEntityManager;

  /**
   * Instantiates an empty {@link LuceneReindexAlgorithm}.
   * @throws Exception if anything goes wrong
   */
  public LuceneReindexAlgorithm() throws Exception {
    super();
  }

  /**
   * Sets the indexed objects.
   *
   * @param indexedObjects the indexed objects
   */
  public void setIndexedObjects(String indexedObjects) {
    this.indexedObjects = indexedObjects;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.jpa.algo.Algorithm#compute()
   */
  @Override
  public void compute() throws Exception {
    if (fullTextEntityManager == null) {
      fullTextEntityManager = Search.getFullTextEntityManager(manager);
    }
    computeLuceneIndexes(indexedObjects);
    fullTextEntityManager.close();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.jpa.algo.Algorithm#reset()
   */
  @Override
  public void reset() throws Exception {
    if (fullTextEntityManager == null) {
      fullTextEntityManager = Search.getFullTextEntityManager(manager);
    }
    clearLuceneIndexes();
    fullTextEntityManager.close();
  }

  /**
   * Compute lucene indexes.
   *
   * @param indexedObjects the indexed objects
   * @throws Exception the exception
   */
  private void computeLuceneIndexes(String indexedObjects) throws Exception {
    // set of objects to be re-indexed
    Set<String> objectsToReindex = new HashSet<>();

    // if no parameter specified, re-index all objects
    if (indexedObjects == null || indexedObjects.isEmpty()) {
      objectsToReindex.add("ConceptJpa");
      objectsToReindex.add("ProjectJpa");

      // otherwise, construct set of indexed objects
    } else {

      // remove white-space and split by comma
      String[] objects = indexedObjects.replaceAll(" ", "").split(",");

      // add each value to the set
      for (String object : objects)
        objectsToReindex.add(object);

    }
    
    Logger.getLogger(ContentServiceJpa.class).info("Starting reindexing for:");
    for (String objectToReindex : objectsToReindex) {
      Logger.getLogger(ContentServiceJpa.class).info("  " + objectToReindex);
    }

    // full text entity manager
    FullTextEntityManager fullTextEntityManager =
        Search.getFullTextEntityManager(manager);

    // Concepts
    if (objectsToReindex.contains("ConceptJpa")) {
      Logger.getLogger(ContentServiceJpa.class).info(
          "  Creating indexes for ConceptJpa");
      fullTextEntityManager.purgeAll(ConceptJpa.class);
      fullTextEntityManager.flushToIndexes();
      fullTextEntityManager.createIndexer(ConceptJpa.class)
          .batchSizeToLoadObjects(100).cacheMode(CacheMode.NORMAL)
          .threadsToLoadObjects(4).threadsForSubsequentFetching(8)
          .startAndWait();

      objectsToReindex.remove("ConceptJpa");
    }   

    // Projects
    if (objectsToReindex.contains("ProjectJpa")) {
      Logger.getLogger(ContentServiceJpa.class).info(
          "  Creating indexes for ProjectJpa");
      fullTextEntityManager.purgeAll(ProjectJpa.class);
      fullTextEntityManager.flushToIndexes();
      fullTextEntityManager.createIndexer(ProjectJpa.class)
          .batchSizeToLoadObjects(100).cacheMode(CacheMode.NORMAL)
          .threadsToLoadObjects(4).threadsForSubsequentFetching(8)
          .startAndWait();

      objectsToReindex.remove("ProjectJpa");
    }

    if (objectsToReindex.size() != 0) {
      throw new Exception(
          "The following objects were specified for re-indexing, but do not exist as indexed objects: "
              + objectsToReindex.toString());
    }

    // Cleanup
    Logger.getLogger(ContentServiceJpa.class).info("done ...");
  }
  
  /**
   * Clear lucene indexes.
   *
   * @throws Exception the exception
   */
  private void clearLuceneIndexes() throws Exception {
    fullTextEntityManager.purgeAll(ConceptJpa.class);
    fullTextEntityManager.purgeAll(ProjectJpa.class);
  }

  /**
   * Fires a {@link ProgressEvent}.
   * @param pct percent done
   * @param note progress note
   */
  public void fireProgressEvent(int pct, String note) {
    ProgressEvent pe = new ProgressEvent(this, pct, pct, note);
    for (int i = 0; i < listeners.size(); i++) {
      listeners.get(i).updateProgress(pe);
    }
    Logger.getLogger(this.getClass()).info("    " + pct + "% " + note);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.jpa.services.helper.ProgressReporter#addProgressListener
   * (org.ihtsdo.otf.ts.jpa.services.helper.ProgressListener)
   */
  @Override
  public void addProgressListener(ProgressListener l) {
    listeners.add(l);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.jpa.services.helper.ProgressReporter#removeProgressListener
   * (org.ihtsdo.otf.ts.jpa.services.helper.ProgressListener)
   */
  @Override
  public void removeProgressListener(ProgressListener l) {
    listeners.remove(l);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.jpa.algo.Algorithm#cancel()
   */
  @Override
  public void cancel() {
    requestCancel = true;
  }
}
