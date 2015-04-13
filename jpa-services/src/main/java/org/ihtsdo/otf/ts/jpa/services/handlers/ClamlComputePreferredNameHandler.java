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
 * An RF2-based implementation of {@link ComputePreferredNameHandler}.
 */
public class ClamlComputePreferredNameHandler implements
    ComputePreferredNameHandler {

  /** the default preferred name type */
  private String dpnType = "4";

  /**
   * Instantiates an empty {@link ClamlComputePreferredNameHandler}.
   */
  public ClamlComputePreferredNameHandler() {
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
    String prop = config.getProperty("defaultPreferredNames.type");
    if (prop != null) {
      dpnType = prop;
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
    return description.getTypeId().equals(dpnType) && description.isActive();
  }

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.services.handlers.ComputePreferredNameHandler#isPreferredName(org.ihtsdo.otf.ts.rf2.Description, org.ihtsdo.otf.ts.rf2.LanguageRefSetMember)
   */
  @Override
  public boolean isPreferredName(Description description,
    LanguageRefSetMember member) throws Exception {
    return isPreferredName(description);
  }

}
