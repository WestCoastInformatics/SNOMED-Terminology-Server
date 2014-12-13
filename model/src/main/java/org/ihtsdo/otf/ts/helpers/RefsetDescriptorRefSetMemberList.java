package org.ihtsdo.otf.ts.helpers;

import org.ihtsdo.otf.ts.rf2.RefsetDescriptorRefSetMember;

/**
 * Represents a sortable list of {@link RefsetDescriptorRefSetMember}
 */
public interface RefsetDescriptorRefSetMemberList extends
    ResultList<RefsetDescriptorRefSetMember> {
  // nothing extra, a simple wrapper for easy serialization
}
