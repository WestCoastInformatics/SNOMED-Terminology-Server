package org.ihtsdo.otf.ts.services.handlers;

import java.util.Set;

import org.ihtsdo.otf.ts.helpers.Configurable;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.ConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.DescriptionRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;

/**
 * Generically represents an algorithm for reading objects
 * to a certain depth before sending them across the wire.  It also
 * handles wiring objects together that have been sent in from across
 * the wire.  Thus the "depth" of the graph is controlled by the imlementation
 * of this algortihm
 */
public interface GraphResolutionHandler extends Configurable {

  /**
   * Resolve concepts.
   *
   * @param concept the concept
   * @param isaRelTypeIds the isa rel type ids
   */
  public void resolve(Concept concept, Set<String> isaRelTypeIds);
  
  /**
   * Resolve descriptions.
   *
   * @param description the description
   */
  public void resolve(Description description);
  
  /**
   * Resolve relationships.
   *
   * @param relationship the relationship
   */
  public void resolve(Relationship relationship);
  
  /**
   * Resolve description refset members.
   *
   * @param member the member
   */
  public void resolve(DescriptionRefSetMember member);  

  /**
   * Resolve concept refset members.
   *
   * @param member the member
   */
  public void resolve(ConceptRefSetMember member);  
}
