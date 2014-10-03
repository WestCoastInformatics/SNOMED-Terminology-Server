package org.ihtsdo.otf.mapping.helpers;

/**
 * The Enum UserRole.
 *
 */
public enum UserRole {
  
  /**  The viewer. */
  VIEWER ("Viewer"),
  
  /**  The specialist. */
  SPECIALIST ("Specialist"),
  
  /**  The lead. */
  LEAD ("Lead"),
  
  /**  The administrator. */
  ADMINISTRATOR ("Administrator");
  
  private String value;
  
  private UserRole(String value) {
  	this.value = value;
  }
  
  /**
   * Returns the value.
   *
   * @return the value
   */
  public String getValue() {
  	return value;
  }
  
  /**
   * Checks for privileges of.
   *
   * @param role the role
   * @return true, if successful
   */
  public boolean hasPrivilegesOf(UserRole role) {
    if (this.equals(UserRole.VIEWER) && role.equals(UserRole.VIEWER))
    	return true;
    else if (this.equals(UserRole.SPECIALIST) && 
    		(role.equals(UserRole.VIEWER) || role.equals(UserRole.SPECIALIST)))
      return true;
    else if (this.equals(UserRole.LEAD) && 
    		(role.equals(UserRole.VIEWER) || role.equals(UserRole.SPECIALIST) || role.equals(UserRole.LEAD)))
    	return true;
    else if (this.equals(UserRole.ADMINISTRATOR))
    	return true;
    else
    	return false;
  }
  
}
