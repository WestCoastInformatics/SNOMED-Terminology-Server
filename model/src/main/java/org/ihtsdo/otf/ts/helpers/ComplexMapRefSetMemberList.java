package org.ihtsdo.otf.ts.helpers;

import org.ihtsdo.otf.ts.rf2.ComplexMapRefSetMember;

/**
 * Represents a sortable list of {@link ComplexMapRefSetMember}
 */
public interface ComplexMapRefSetMemberList extends
    ResultList<ComplexMapRefSetMember> {
  // nothing extra, a simple wrapper for easy serialization
}
