package org.ihtsdo.otf.mapping.helpers;

/**
 * Represents a user
 * TODO: fix!
 */
public interface User {

	public String getId();
	public String getName();
	public String getEmail();
	public UserRole getApplicationRole();
	public String getUserName();

	public String getObjectId();
	
	public void setUserName(String userName);
	public void setName(String fullName);
	public void setEmail(String email);
	public void setApplicationRole(UserRole role);

}
