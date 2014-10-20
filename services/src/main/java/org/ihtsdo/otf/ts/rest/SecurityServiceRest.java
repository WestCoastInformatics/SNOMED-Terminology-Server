package org.ihtsdo.otf.ts.rest;


/**
 * Represents a security available via a REST service.
 */
public interface SecurityServiceRest {

  /**
   * Authenticate.
   * 
   * @param username the username
   * @param password the password
   * @return the string
   * @throws Exception if anything goes wrong
   */
  public String authenticate(String username, String password) throws Exception;
}