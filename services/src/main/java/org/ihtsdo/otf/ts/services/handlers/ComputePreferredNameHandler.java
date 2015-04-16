/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.services.handlers;

import java.util.Set;

import org.ihtsdo.otf.ts.helpers.Configurable;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;

/**
 * Generically represents an algorithm for computing preferred names.
 */
public interface ComputePreferredNameHandler extends Configurable {

  /**
   * Compute preferred name for a concept.
   *
   * @param concept the concept
   * @return the string
   * @throws Exception the exception
   */
  public String computePreferredName(Concept concept) throws Exception;

  /**
   * Compute preferred name among a set of descriptions.
   *
   * @param descriptions the descriptions
   * @return the string
   * @throws Exception the exception
   */
  public String computePreferredName(Set<Description> descriptions)
    throws Exception;

  /**
   * Indicates whether or not the description is the preferred name.
   *
   * @param description the description
   * @return <code>true</code> if so, <code>false</code> otherwise
   * @throws Exception the exception
   */
  public boolean isPreferredName(Description description) throws Exception;

  /**
   * Indicates whether or not the description with the language refset member is
   * the preferred name.
   *
   * @param description the description
   * @param member the member
   * @return <code>true</code> if so, <code>false</code> otherwise
   * @throws Exception the exception
   */
  public boolean isPreferredName(Description description,
    LanguageRefSetMember member) throws Exception;
}
