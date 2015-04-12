/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.ihtsdo.otf.ts.rf2.Concept;
import org.ihtsdo.otf.ts.rf2.jpa.ConceptJpa;

/**
 * JAXB enabled implementation of {@link ConceptList}.
 */
@XmlRootElement(name = "conceptList")
public class ConceptListJpa extends AbstractResultList<Concept> implements
    ConceptList {

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.helpers.ConceptList#getConcepts()
   */
  @Override
  @XmlElement(type = ConceptJpa.class, name = "concept")
  public List<Concept> getObjects() {
    return super.getObjects();
  }


}
