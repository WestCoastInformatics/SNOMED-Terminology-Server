package org.ihtsdo.otf.ts.services.handlers;

import java.util.Properties;

import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.ConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.DescriptionRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;

/**
 * Default implementation of {@link GraphResolutionHandler}.
 */
public class DefaultGraphResolutionHandler implements GraphResolutionHandler {

  /**
   * Resolve concepts.
   *
   * @param concept the concept
   */
  @Override
  public void resolve(Concept concept) {
    if (concept != null) {
      // Make sure to read descriptions and relationships (prevents
      // serialization error)
      for (Description d : concept.getDescriptions()) {
        d.getLanguageRefSetMembers().size();
      }
      for (Relationship r : concept.getRelationships()) {
        r.getDestinationConcept().getDefaultPreferredName();
      }
      // don't resolve these, limit to what uses Cascade.ALL
      //concept.getAttributeValueRefSetMembers().size();
      //concept.getComplexMapRefSetMembers().size();
      //concept.getSimpleMapRefSetMembers();
      //concept.getSimpleRefSetMembers().size();
    }
  }

  /**
   * Resolve descriptions.
   *
   * @param description the description
   */
  @Override
  public void resolve(Description description) {
    if (description != null) {
      description.getLanguageRefSetMembers().size();
    }
  }

  /**
   * Resolve relationships.
   *
   * @param relationship the relationship
   */
  @Override
  public void resolve(Relationship relationship) {
    if (relationship != null) {
      relationship.getSourceConcept().getDefaultPreferredName();
      relationship.getDestinationConcept().getDefaultPreferredName();
    }
  }

  /**
   * Resolve description refset members.
   *
   * @param member the member
   */
  @Override
  public void resolve(DescriptionRefSetMember member) {
    if (member != null) {
      member.getDescription().getTypeId();
    }
  }

  /**
   * Resolve concept refset members.
   *
   * @param member the member
   */
  @Override
  public void resolve(ConceptRefSetMember member) {
    if (member != null) {
      member.getConcept().getDefaultPreferredName();
    }
  }

  @Override
  public void setProperties(Properties p) {
    // do nothing
  }
}
