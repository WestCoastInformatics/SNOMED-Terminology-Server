/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.helpers;

import org.ihtsdo.otf.ts.Project;

/**
 * Represents a sortable list of {@link Project}
 */
public interface ProjectList extends ResultList<Project> {
  // nothing extra, a simple wrapper for easy serialization
}
