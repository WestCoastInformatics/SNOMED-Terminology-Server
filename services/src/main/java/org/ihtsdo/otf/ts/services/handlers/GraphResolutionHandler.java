package org.ihtsdo.otf.ts.services.handlers;

import org.ihtsdo.otf.ts.helpers.Configurable;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.ConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.DescriptionRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;

/**
 * Generically represents an algorithm for reading objects
 * to a certain depth before sending them across the wire.
 */
public interface GraphResolutionHandler extends Configurable {

  /**
   * Resolve concepts.
   *
   * @param concept the concept
   */
  public void resolve(Concept concept);
  
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
