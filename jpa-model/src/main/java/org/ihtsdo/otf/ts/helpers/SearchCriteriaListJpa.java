/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * JAXB enabled implementation of {@link SearchCriteriaList}.
 */
@XmlRootElement(name = "searchCriteriaList")
public class SearchCriteriaListJpa extends AbstractResultList<SearchCriteria> implements
SearchCriteriaList {

  /* (non-Javadoc)
   * @see org.ihtsdo.otf.ts.helpers.AbstractResultList#getObjects()
   */
  @Override
  @XmlElement(type = SearchCriteriaJpa.class, name = "criteria")
  public List<SearchCriteria> getObjects() {
    return super.getObjects();
  }


}
