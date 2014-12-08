package org.ihtsdo.otf.ts.helpers;

import org.ihtsdo.otf.ts.rf2.SimpleMapRefSetMember;

/**
 * Represents a sortable list of {@link SimpleMapRefSetMember}
 */
public interface SimpleMapRefSetMemberList extends
    ResultList<SimpleMapRefSetMember> {
  // nothing extra, a simple wrapper for easy serialization
}
