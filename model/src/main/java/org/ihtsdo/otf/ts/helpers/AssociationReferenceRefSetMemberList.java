package org.ihtsdo.otf.ts.helpers;

import org.ihtsdo.otf.ts.rf2.AssociationReferenceRefSetMember;
import org.ihtsdo.otf.ts.rf2.Component;

/**
 * Represents a sortable list of {@link AssociationReferenceRefSetMember}
 */
public interface AssociationReferenceRefSetMemberList extends
    ResultList<AssociationReferenceRefSetMember<? extends Component>> {
  // nothing extra, a simple wrapper for easy serialization
}
