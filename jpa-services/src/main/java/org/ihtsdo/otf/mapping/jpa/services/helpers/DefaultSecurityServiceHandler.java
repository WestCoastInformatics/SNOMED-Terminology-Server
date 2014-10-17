package org.ihtsdo.otf.mapping.jpa.services.helpers;

import java.io.IOException;
import java.util.Properties;

import org.ihtsdo.otf.mapping.helpers.LocalException;
import org.ihtsdo.otf.mapping.helpers.User;
import org.ihtsdo.otf.mapping.helpers.UserJpa;
import org.ihtsdo.otf.mapping.helpers.UserRole;
import org.ihtsdo.otf.mapping.jpa.services.SecurityServiceHandler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Implements a security handler that authorizes via IHTSDO authentication.
 */
public class DefaultSecurityServiceHandler implements SecurityServiceHandler {

  @Override
  public User authenticate(String username, String password,
    Properties properties) throws LocalException, JsonParseException,
    JsonMappingException, IOException {

    User user = new UserJpa();
    user.setUserName("guest");
    user.setName("Guest");
    user.setApplicationRole(UserRole.VIEWER);
    user.setEmail("guest@example.com");

    return user;
  }

}
