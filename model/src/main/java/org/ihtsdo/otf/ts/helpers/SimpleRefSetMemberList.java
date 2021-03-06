/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.helpers;

import org.ihtsdo.otf.ts.rf2.SimpleRefSetMember;

/**
 * Represents a sortable list of {@link SimpleRefSetMember}
 */
public interface SimpleRefSetMemberList extends ResultList<SimpleRefSetMember> {
  // nothing extra, a simple wrapper for easy serialization
}
