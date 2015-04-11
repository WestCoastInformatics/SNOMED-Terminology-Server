/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.helpers;

import org.ihtsdo.otf.ts.rf2.Concept;

/**
 * Represents a sortable list of {@link Concept}
 */
public interface ConceptList extends ResultList<Concept> {
  // nothing extra, a simple wrapper for easy serialization
}
