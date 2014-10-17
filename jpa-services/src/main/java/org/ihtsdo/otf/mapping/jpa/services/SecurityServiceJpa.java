package org.ihtsdo.otf.mapping.jpa.services;

import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.ihtsdo.otf.mapping.helpers.LocalException;
import org.ihtsdo.otf.mapping.helpers.User;
import org.ihtsdo.otf.mapping.helpers.UserJpa;
import org.ihtsdo.otf.mapping.helpers.UserList;
import org.ihtsdo.otf.mapping.helpers.UserListJpa;
import org.ihtsdo.otf.mapping.helpers.UserRole;
import org.ihtsdo.otf.mapping.services.SecurityService;
import org.ihtsdo.otf.mapping.services.SecurityServiceHandler;
import org.ihtsdo.otf.mapping.services.helpers.ConfigUtility;

/**
 * Reference implementation of the {@link SecurityService}.
 */
public class SecurityServiceJpa extends RootServiceJpa implements
    SecurityService {

  /** The token username . */
  private static Map<String, String> tokenUsername = new HashMap<>();

  /** The token login time . */
  private static Map<String, Date> tokenLogin = new HashMap<>();

  /** config properties */
  private static Properties config = null;

  /** The handler. */
  private static SecurityServiceHandler handler = null;

  /** The handler properties. */
  private static Properties handlerProperties = new Properties();

  /**
   * Instantiates an empty {@link SecurityServiceJpa}.
   *
   * @throws Exception the exception
   */
  public SecurityServiceJpa() throws Exception {
    super();
    // Configure default security for guest user
    tokenUsername.put("guest", "guest");
    tokenLogin.put("guest", null);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.SecurityService#authenticate(java.lang.
   * String, java.lang.String)
   */
  @Override
  public String authenticate(String username, String password) throws Exception {
    // Check username and password are not null
    if (username == null)
      throw new LocalException("Invalid username: null");
    if (password == null)
      throw new LocalException("Invalid password: null");

    // read ihtsdo security url and active status from config file
    if (config == null) {
      String configFileName = System.getProperty("run.config");
      Logger.getLogger(this.getClass())
          .info("  run.config = " + configFileName);

      config = new Properties();
      FileReader in = new FileReader(new File(configFileName));
      config.load(in);
      in.close();
    }

    if (handler == null) {

      String handlerName = config.getProperty("security.handler");
      String handlerClass = config.getProperty("security.handler.");

      handlerProperties = new Properties();
      handler =
          (SecurityServiceHandler) ConfigUtility.newHandlerInstance(
              handlerName, handlerClass, SecurityServiceHandler.class);
      for (Object key : config.keySet()) {
        if (key.toString().startsWith("security.handler." + handlerName + ".")) {
          String shortKey =
              key.toString().substring(
                  ("security.handler." + handlerName + ".").length());
          handlerProperties.put(shortKey, config.getProperty(key.toString()));
        }
      }
    }

    //
    // Call the security service
    //
    User authUser = handler.authenticate(username, password, handlerProperties);

    // check if authenticated user matches one of our users
    UserList userList = getUsers();
    User userFound = null;
    for (User user : userList.getUsers()) {
      if (user.getUserName().equals(authUser.getUserName())) {
        userFound = user;
        break;
      }
    }

    // if user was found, update to match settings
    if (userFound != null) {
      userFound.setEmail(authUser.getEmail());
      userFound.setName(authUser.getName());
      userFound.setUserName(authUser.getUserName());
      updateUser(userFound);

    }
    // if User not found, create one for our use
    else {
      User newUser = new UserJpa();
      newUser.setEmail(authUser.getEmail());
      newUser.setName(authUser.getName());
      newUser.setUserName(authUser.getUserName());
      newUser.setApplicationRole(UserRole.VIEWER);
      addUser(newUser);
    }

    // Generate application-managed token
    String token = UUID.randomUUID().toString();
    tokenUsername.put(token, authUser.getUserName());
    tokenLogin.put(token, new Date());

    Logger.getLogger(this.getClass()).info("User = " + authUser.getUserName());

    return token;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ping.services.SecurityService#getUsernameForToken(java
   * .lang.String)
   */
  @Override
  public String getUsernameForToken(String authToken) throws Exception {
    // use guest user for null auth token
    if (authToken == null)
      throw new LocalException(
          "Attempt to access a service without an authorization token, the user is likely not logged in.");

    String parsedToken = authToken.replace("\"", "");
    if (tokenUsername.containsKey(parsedToken)) {
      String username = tokenUsername.get(parsedToken);
      Logger.getLogger(this.getClass()).info(
          "User = " + username + " Token = " + parsedToken);
      if (tokenLogin.get(username) != null
          && tokenLogin.get(username).before(new Date())) {
        throw new LocalException("AuthToken has expired");
      }
      return username;

    } else {
      throw new LocalException("AuthToken does not have a valid username.");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.ping.services.SecurityService#authorizeToken(java.lang
   * .String)
   */
  @Override
  public UserRole getApplicationRoleForToken(String authToken) throws Exception {

    if (authToken == null)
      throw new LocalException(
          "Attempt to access a service without an authorization token, the user is likely not logged in.");
    String parsedToken = authToken.replace("\"", "");

    String username = getUsernameForToken(parsedToken);

    return getUser(username.toLowerCase()).getApplicationRole();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.SecurityService#getUser(java.lang.Long)
   */
  @Override
  public User getUser(Long id) throws Exception {
    javax.persistence.Query query =
        manager.createQuery("select u from UserJpa u where id = :id");
    query.setParameter("id", id);
    return (User) query.getSingleResult();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.SecurityService#getUser(java.lang.String)
   */
  @Override
  public User getUser(String username) throws Exception {
    javax.persistence.Query query =
        manager
            .createQuery("select u from UserJpa u where userName = :userName");
    query.setParameter("userName", username);
    return (User) query.getSingleResult();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.SecurityService#addUser(org.ihtsdo.otf.
   * mapping.helpers.User)
   */
  @Override
  public User addUser(User user) {
    if (getTransactionPerOperation()) {
      tx = manager.getTransaction();
      tx.begin();
      manager.persist(user);
      tx.commit();
    } else {
      manager.persist(user);
    }
    return user;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.SecurityService#removeUser(java.lang.String
   * )
   */
  @Override
  public void removeUser(String id) {
    tx = manager.getTransaction();
    // retrieve this user
    User mu = manager.find(UserJpa.class, id);
    if (getTransactionPerOperation()) {
      tx.begin();
      if (manager.contains(mu)) {
        manager.remove(mu);
      } else {
        manager.remove(manager.merge(mu));
      }
      tx.commit();

    } else {
      if (manager.contains(mu)) {
        manager.remove(mu);
      } else {
        manager.remove(manager.merge(mu));
      }
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ihtsdo.otf.mapping.services.SecurityService#updateUser(org.ihtsdo.otf
   * .mapping.helpers.User)
   */
  @Override
  public void updateUser(User user) {
    if (getTransactionPerOperation()) {
      tx = manager.getTransaction();
      tx.begin();
      manager.merge(user);
      tx.commit();
    } else {
      manager.merge(user);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ihtsdo.otf.mapping.services.SecurityService#getUsers()
   */
  @SuppressWarnings("unchecked")
  @Override
  public UserList getUsers() {
    javax.persistence.Query query =
        manager.createQuery("select u from UserJpa u");
    List<User> m = query.getResultList();
    UserListJpa mapUserList = new UserListJpa();
    mapUserList.setUsers(m);
    mapUserList.setTotalCount(m.size());
    return mapUserList;
  }

}
