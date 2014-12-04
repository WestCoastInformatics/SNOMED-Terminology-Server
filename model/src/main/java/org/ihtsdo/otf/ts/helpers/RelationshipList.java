package org.ihtsdo.otf.ts.helpers;

import org.ihtsdo.otf.ts.rf2.Relationship;

/**
 * Represents a sortable list of {@link Relationship}
 */
public interface RelationshipList extends ResultList<Relationship> {
  // nothing extra, a simple wrapper for easy serialization
}
