package org.ihtsdo.otf.ts.jpa.algo;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.ts.jpa.services.ContentServiceJpa;
import org.ihtsdo.otf.ts.jpa.services.MetadataServiceJpa;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.rf2.TransitiveRelationship;
import org.ihtsdo.otf.ts.rf2.jpa.TransitiveRelationshipJpa;
import org.ihtsdo.otf.ts.services.ContentService;
import org.ihtsdo.otf.ts.services.MetadataService;

/**
 * Implementation of an algorithm to compute transitive closure using the
 * {@link ContentService}.
 */
public class TransitiveClosureAlgorithm extends ContentServiceJpa implements
    Algorithm {

  /** The root id. */
  private String rootId;

  /** The terminology. */
  private String terminology;

  /** The terminology version. */
  private String terminologyVersion;

  /**
   * Instantiates an empty {@link TransitiveClosureAlgorithm}.
   * @throws Exception if anything goes wrong
   */
  public TransitiveClosureAlgorithm() throws Exception {
    super();
  }

  /**
   * Sets the root id.
   *
   * @param rootId the root id
   */
  public void setRootId(String rootId) {
    this.rootId = rootId;
  }

  /**
   * Sets the terminology.
   *
   * @param terminology the terminology
   */
  public void setTerminology(String terminology) {
    this.terminology = terminology;
  }

  /**
   * Sets the terminology version.
   *
   * @param terminologyVersion the terminology version
   */
  public void setTerminologyVersion(String terminologyVersion) {
    this.terminologyVersion = terminologyVersion;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.jpa.algo.Algorithm#compute()
   */
  @Override
  public void compute() throws Exception {
    computeTransitiveClosure(rootId, terminology, terminologyVersion);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.jpa.algo.Algorithm#reset()
   */
  @Override
  public void reset() throws Exception {
    clearTransitiveClosure(terminology, terminologyVersion);
  }

  /**
   * Compute transitive closure.
   *
   * @param rootId the root id
   * @param terminology the terminology
   * @param terminologyVersion the terminology version
   * @throws Exception the exception
   */
  private void computeTransitiveClosure(String rootId, String terminology,
    String terminologyVersion) throws Exception {
    //
    // Check assumptions/prerequisites
    //
    Logger.getLogger(this.getClass()).info(
        "Start computing transitive closure ... " + new Date());

    // Disable transaction per operation
    boolean currentTransactionStrategy = getTransactionPerOperation();
    if (getTransactionPerOperation()) {
      this.setTransactionPerOperation(false);
    }

    //
    // Initialize rels
    // id effectiveTime active moduleId sourceId destinationId relationshipGroup
    // typeId characteristicTypeId modifierId
    //
    Logger.getLogger(this.getClass()).info(
        "  Initialize relationships ... " + new Date());

    MetadataService metadataService = new MetadataServiceJpa();

    // NOTE: assumes single hierarchical rel
    String inferredCharType = "900000000000011006";
    String isaRel =
        metadataService
            .getHierarchicalRelationshipTypes(terminology, terminologyVersion)
            .keySet().iterator().next();
    // Skip non isa
    // Skip non-inferred
    javax.persistence.Query query =
        manager
            .createQuery(
                "select r from RelationshipJpa r where active=1 and terminology=:terminology and terminologyVersion=:terminologyVersion and typeId=:typeId and characteristicTypeId=:characteristicTypeId")
            .setParameter("terminology", terminology)
            .setParameter("terminologyVersion", terminologyVersion)
            .setParameter("typeId", isaRel)
            .setParameter("characteristicTypeId", inferredCharType);

    @SuppressWarnings("unchecked")
    List<Relationship> rels = query.getResultList();
    Map<String, Set<String>> parChd = new HashMap<>();
    int ct = 0;
    for (Relationship rel : rels) {
      String chd = rel.getSourceConcept().getTerminologyId();
      String par = rel.getDestinationConcept().getTerminologyId();
      if (!parChd.containsKey(par)) {
        parChd.put(par, new HashSet<String>());
      }
      Set<String> children = parChd.get(par);
      children.add(chd);
      ct++;
    }
    Logger.getLogger(this.getClass()).info("    ct = " + ct);

    // cache concepts
    Logger.getLogger(this.getClass())
        .info("  Cache concepts ... " + new Date());
    query =
        manager
            .createQuery("select c.id, c.terminologyId, c.terminology, c.terminologyVersion from ConceptJpa c");

    @SuppressWarnings("unchecked")
    List<Object[]> results = query.getResultList();
    Map<String, Long> conceptCache = new HashMap<>();
    ct = 0;
    for (Object[] result : results) {
      final String key = result[1].toString() + result[2] + result[3];
      conceptCache.put(key, (Long) result[0]);
      ct++;
    }
    Logger.getLogger(this.getClass()).info("    ct = " + ct);
    //
    // Create transitive closure rels
    //
    Logger.getLogger(this.getClass()).info(
        "  Create transitive closure rels... " + new Date());
    ct = 0;
    beginTransaction();
    for (String code : parChd.keySet()) {
      if (rootId.equals(code)) {
        continue;
      }
      ct++;
      Set<String> descs = getDescendants(code, new HashSet<String>(), parChd);
      for (String desc : descs) {
        final TransitiveRelationship tr = new TransitiveRelationshipJpa();
        final String superKey = code + terminology + terminologyVersion;
        final String subKey = desc + terminology + terminologyVersion;
        tr.setSuperTypeConcept(getConcept(conceptCache.get(superKey)));
        tr.setSubTypeConcept(getConcept(conceptCache.get(subKey)));
        tr.setActive(true);
        tr.setLastModified(new Date());
        tr.setLastModifiedBy("admin");
        tr.setEffectiveTime(new Date());
        tr.setLabel("");
        tr.setModuleId("");
        tr.setTerminologyId("");
        tr.setTerminology(terminology);
        tr.setTerminologyVersion(terminologyVersion);
        addTransitiveRelationship(tr);
      }
      if (ct % 500 == 0) {
        Logger.getLogger(this.getClass()).info(
            "      " + ct + " codes processed ..." + new Date());
        commit();
        beginTransaction();
      }
    }
    commit();

    Logger.getLogger(this.getClass()).info(
        "Finished computing transitive closure ... " + new Date());
    // set the transaction strategy based on status starting this routine
    setTransactionPerOperation(currentTransactionStrategy);
  }

  /**
   * Returns the descendants.
   *
   * @param par the par
   * @param seen the seen
   * @param parChd the par chd
   * @return the descendants
   */
  private Set<String> getDescendants(String par, Set<String> seen,
    Map<String, Set<String>> parChd) {
    Logger.getLogger(this.getClass()).debug("  Get descendants for " + par);
    // if we've seen this node already, children are accounted for - bail
    if (seen.contains(par)) {
      return new HashSet<>();
    }
    seen.add(par);

    // Get Children of this node
    Set<String> children = parChd.get(par);

    // If this is a leaf node, bail
    if (children == null || children.isEmpty()) {
      return new HashSet<>();
    }
    // Iterate through children, mark as descendant and recursively call
    Set<String> descendants = new HashSet<>();
    for (String chd : children) {
      descendants.add(chd);
      descendants.addAll(getDescendants(chd, seen, parChd));
    }
    Logger.getLogger(this.getClass()).debug(
        "    descCt = " + descendants.size());

    return descendants;
  }
}
