/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.services.handlers;

import java.util.Set;

import org.ihtsdo.otf.ts.helpers.Configurable;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.ConceptRefSetMember;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.DescriptionRefSetMember;
import org.ihtsdo.otf.ts.rf2.Relationship;

/**
 * Generically represents an algorithm for reading objects to a certain depth
 * before sending them across the wire. It also handles wiring objects together
 * that have been sent in from across the wire. Thus the "depth" of the graph is
 * controlled by the implementation of this algorithm
 */
public interface GraphResolutionHandler extends Configurable {

  /**
   * Resolve concepts.
   *
   * @param concept the concept
   * @param isaRelTypeIds the isa rel type ids
 * @throws Exception 
   */
  public void resolve(Concept concept, Set<String> isaRelTypeIds) throws Exception;

  /**
   * Resolve a concept to simply the concept element and none of the graph,
   * ready for JAXB serialization.
   *
   * @param concept the concept
   */
  public void resolveEmpty(Concept concept);

  /**
   * Resolve descriptions.
   * 
   * @param description the description
 * @throws Exception 
   */
  public void resolve(Description description) throws Exception;

  /**
   * Resolve a description to simply the concept element and none of the graph,
   * ready for JAXB serialization.
   *
   * @param description the description
   */
  public void resolveEmpty(Description description);

  /**
   * Resolve relationships.
   *
   * @param relationship the relationship
   * @throws Exception the exception
   */
  public void resolve(Relationship relationship) throws Exception;

  /**
   * Resolve description refset members.
   *
   * @param member the member
   * @throws Exception the exception
   */
  public void resolve(DescriptionRefSetMember member) throws Exception;  

  /**
   * Resolve concept refset members.
   *
   * @param member the member
   * @throws Exception the exception
   */
  public void resolve(ConceptRefSetMember member) throws Exception;  
}
