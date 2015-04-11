/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.jpa.services.handlers;

import java.util.Properties;
import java.util.Set;

import org.ihtsdo.otf.ts.helpers.ConfigUtility;
import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.Description;
import org.ihtsdo.otf.ts.rf2.LanguageRefSetMember;
import org.ihtsdo.otf.ts.services.handlers.ComputePreferredNameHandler;

/**
 * A ClaML based implementation of {@link ComputePreferredNameHandler}.
 */
public class Rf2ComputePreferredNameHandler implements
    ComputePreferredNameHandler {

  /** the defaultPreferredNames values. */
  private String dpnTypeId = "900000000000013009";

  /** The dpn ref set id. */
  private String dpnRefSetId = "900000000000509007";

  /** The dpn acceptability id. */
  private String dpnAcceptabilityId = "900000000000548007";

  /**
   * Instantiates an empty {@link Rf2ComputePreferredNameHandler}.
   */
  public Rf2ComputePreferredNameHandler() {
    // do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.helpers.Configurable#setProperties(java.util.Properties)
   */
  @Override
  public void setProperties(Properties p) throws Exception {
    Properties config = ConfigUtility.getConfigProperties();
    // Use defaults if not otherwise supplied
    String prop = config.getProperty("defaultPreferredNames.typeId");
    if (prop != null) {
      dpnTypeId = prop;
    }
    prop = config.getProperty("defaultPreferredNames.refSetId");
    if (prop != null) {
      dpnRefSetId = prop;
    }
    prop = config.getProperty("defaultPreferredNames.acceptabilityId");
    if (prop != null) {
      dpnAcceptabilityId = prop;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ts.services.handlers.ComputePreferredNameHandler#
   * computePreferredName(org.ihtsdo.otf.ts.rf2.Concept)
   */
  @Override
  public String computePreferredName(Concept concept) throws Exception {
    for (Description description : concept.getDescriptions()) {
      if (isPreferredName(description)) {
        return description.getTerm();
      }
    }
    // If not found, return normal name
    return concept.getDefaultPreferredName();
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.services.handlers.ComputePreferredNameHandler#computePreferredName(java.util.Set)
   */
  @Override
  public String computePreferredName(Set<Description> descriptions)
    throws Exception {
    String first = "";
    for (Description description : descriptions) {
      // Use first description encountered if no preferred is found
      if (first.equals("")) {
        first = description.getTerm();
      }
      if (isPreferredName(description)) {
        return description.getTerm();
      }
    }
    return first;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.ts.services.handlers.ComputePreferredNameHandler#isPreferredName
   * (org.ihtsdo.otf.ts.rf2.Description)
   */
  @Override
  public boolean isPreferredName(Description description) throws Exception {
    for (LanguageRefSetMember member : description.getLanguageRefSetMembers()) {
      // Check if this language refset and description form the
      // defaultPreferredName
      if (isPreferredName(description,member)) {
        return true;
      }
    }
    return false;
  }
  
  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.services.handlers.ComputePreferredNameHandler#isPreferredName(org.ihtsdo.otf.ts.rf2.Description, org.ihtsdo.otf.ts.rf2.LanguageRefSetMember)
   */
  @Override  
  public boolean isPreferredName(Description description,
    LanguageRefSetMember member) throws Exception {
      // Check if this language refset and description form the
      // defaultPreferredName
      return description.isActive() && description.getTypeId().equals(dpnTypeId)
          && member.getRefSetId().equals(dpnRefSetId) && member.isActive()
          && member.getAcceptabilityId().equals(dpnAcceptabilityId);
  }

}
