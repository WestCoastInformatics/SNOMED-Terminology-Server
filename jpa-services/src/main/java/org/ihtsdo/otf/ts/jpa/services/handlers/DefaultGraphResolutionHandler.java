/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.jpa.services.handlers;

import java.util.HashSet;
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
  public void resolve(Concept concept, Set<String> isaRelTypeIds)
    throws Exception {
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

    } else if (concept == null) {
      throw new Exception("Cannot resolve a null concept.");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.GraphResolutionHandler#resolveEmpty
   * (org.ihtsdo.otf.ts.rf2.Concept)
   */
  @Override
  public void resolveEmpty(Concept concept) {
    if (concept != null) {
      concept.setDescriptions(new HashSet<Description>());
      concept.setRelationships(new HashSet<Relationship>());
    }
  }

  /**
   * Resolve descriptions.
   *
   * @param description the description
   */
  @Override
  public void resolve(Description description) throws Exception {
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
    } else if (description == null) {
      throw new Exception("Cannot resolve a null description.");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.GraphResolutionHandler#resolveEmpty
   * (org.ihtsdo.otf.ts.rf2.Description)
   */
  @Override
  public void resolveEmpty(Description description) {
    if (description != null) {
      description.setLanguageRefSetMembers(new HashSet<LanguageRefSetMember>());
    }
    }

  /**
   * Resolve relationships.
   *
   * @param relationship the relationship
   */
  @Override
  public void resolve(Relationship relationship) throws Exception {
    if (relationship != null && relationship.getSourceConcept() != null
        && relationship.getDestinationConcept() != null) {
      relationship.getSourceConcept().getDefaultPreferredName();
      relationship.getDestinationConcept().getDefaultPreferredName();
    } else if (relationship == null) {
      throw new Exception("Cannot resolve a null relationship.");
    }
  }

  /**
   * Resolve description refset members.
   *
   * @param member the member
   */
  @Override
  public void resolve(DescriptionRefSetMember member) throws Exception {
    if (member != null && member.getDescription() != null) {
      member.getDescription().getTypeId();
    } else if (member == null) {
      throw new Exception("Cannot resolve a null description refset member.");
    }
  }

  /**
   * Resolve concept refset members.
   *
   * @param member the member
   */
  @Override
  public void resolve(ConceptRefSetMember member) throws Exception {
    if (member != null && member.getConcept() != null) {
      member.getConcept().getDefaultPreferredName();
    } else if (member == null) {
      throw new Exception("Cannot resolve a null concept refset member.");
    }
  }

  @Override
  public void setProperties(Properties p) {
    // do nothing
  }
}
