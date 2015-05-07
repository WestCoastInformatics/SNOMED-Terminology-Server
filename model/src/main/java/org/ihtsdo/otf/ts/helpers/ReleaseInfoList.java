/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.helpers;

import org.ihtsdo.otf.ts.ReleaseInfo;

/**
 * Represents a sortable list of {@link ReleaseInfo}
 */
public interface ReleaseInfoList extends ResultList<ReleaseInfo> {
  // nothing extra, a simple wrapper for easy serialization
}
