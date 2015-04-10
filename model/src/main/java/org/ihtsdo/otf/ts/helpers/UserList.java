/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package org.ihtsdo.otf.ts.helpers;

import org.ihtsdo.otf.ts.User;


/**
 * Represents a sortable list of {@link User}.
 */
public interface UserList extends ResultList<User> {
  // nothing extra, a simple wrapper for easy serialization
}
