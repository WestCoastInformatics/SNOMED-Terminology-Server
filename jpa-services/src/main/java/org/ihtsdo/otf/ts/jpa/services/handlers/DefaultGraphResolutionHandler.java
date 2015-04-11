/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.jpa.services.handlers;

import java.util.Properties;
import java.util.Set;

import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.ConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.DescriptionRefSetMember;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;
import org.ihtsdo.otf.ts.services.handlers.GraphResolutionHandler;

/**
 * Default implementation of {@link GraphResolutionHandler}. This connects
 * graphs at the level at which CascadeType.ALL is used in the data model.
 */
public class DefaultGraphResolutionHandler implements GraphResolutionHandler {

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.GraphResolutionHandler#resolve(org.
   * ihtsdo.otf.ts.rf2.Concept, java.lang.String)
   */
  @Override
  public void resolve(Concept concept, Set<String> isaRelTypeIds) {
    if (concept != null) {
      boolean nullId = concept.getId() == null;
      // Make sure to read descriptions and relationships (prevents
      // serialization error)
      for (Description description : concept.getDescriptions()) {
        description.setConcept(concept);
        // if the concept is "new", then the description must be too
        if (nullId) {
          description.setId(null);
        }
        resolve(description);
      }

      for (Relationship relationship : concept.getRelationships()) {
        // if the concept is "new", then the relationship must be too
        if (nullId) {
          relationship.setId(null);
        }
        relationship.setSourceConcept(concept);
        relationship.getDestinationConcept().getDefaultPreferredName();

      }

      // TODO: consider having fields for counts of other data structures so a
      // user knows whether
      // to make a callback
      // e.g. concept.setSimpleRefSetMemberCount(3);

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
      boolean nullId = description.getId() == null;
      for (LanguageRefSetMember member : description.getLanguageRefSetMembers()) {
        // if the description is "new", then the language refset member must be
        // too
        if (nullId) {
          member.setId(null);
        }
        member.setDescription(description);
      }
      // TODO: consider having fields for counts of other data structures so a
      // user knows whether
      // to make a callback
      // e.g. description.setAttributeValueRefsetMemberCount(1);
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
