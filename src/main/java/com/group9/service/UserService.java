package com.group9.service;

import com.group9.dao.UserDao;
import com.group9.model.User;
import com.group9.util.SessionManager;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ResourceBundle;

/**
 * Service class for managing user-related operations such as registration and login.
 */
public class UserService {
  private final UserDao userDao;
  private ResourceBundle rb;

  /**
   * Constructs a UserService with the specified UserDao.
   *
   * @param userDao the {@link UserDao} for database interactions
   */
  public UserService(UserDao userDao) {
    this.userDao = userDao;
  }

  /**
   * Logs in a user by validating credentials.
   *
   * @param username the username
   * @param password the password
   * @return the {@link User} object if login is successful
   * @throws IllegalArgumentException if validation fails or credentials are invalid
   */
  public User loginUser(String username, String password) {
      rb = SessionManager.getResourceBundle();
      // Validate input
      if (username == null || username.trim().isEmpty()) {
          String message = rb.getString("usernameEmpty");
          throw new IllegalArgumentException(message);
      }
      if (password == null || password.isEmpty()) {
          String message = rb.getString("passwordEmpty");
          throw new IllegalArgumentException(message);
      }
      // Retrieve user
      User user = userDao.getUserByUsername(username);
      if (user == null) {
          String message = rb.getString("invalidCredentials");
          throw new IllegalArgumentException(message);
      }
      // Verify password
      if (!BCrypt.checkpw(password, user.getPassword())) {
          String message = rb.getString("invalidCredentials");
        throw new IllegalArgumentException(message);
      }
      // Successful login
        return user;
  }

  /**
   * Registers a new user after validating input and hashing the password.
   *
   * @param username the desired username
   * @param password the desired password
   * @param email    the user's email address
   * @return the newly created {@link User} object
   * @throws IllegalArgumentException if validation fails or username/email already exists
   */
  public User registerUser(String username, String password, String email) {
      rb = SessionManager.getResourceBundle();
      User newUser = new User(username.trim(), password, email.trim());
      validateUser(newUser);

      // Check if username or email already exists
      if (userDao.getUserByUsername(username) != null) {
          String message = rb.getString("usernameExists");
          throw new IllegalArgumentException(message);
      }
      if (userDao.getUserByEmail(email) != null) {
          String message = rb.getString("emailExists");
          throw new IllegalArgumentException(message);
      }

      // Hash password
      String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
      newUser.setPassword(hashedPassword);

      // Create new user
      return userDao.addUser(newUser);
  }

  /**
   * Validates the {@link User} object for registration.
   *
   * @param user the {@link User} object to validate
   * @throws IllegalArgumentException if validation fails
   */
  private void validateUser(User user) {
      rb = SessionManager.getResourceBundle();
      if (user == null) {
          String message = rb.getString("userNull");
          throw new IllegalArgumentException(message);
      }

      if (user.getUsername() == null || user.getUsername().isEmpty()) {
          String message = rb.getString("usernameEmpty");
          throw new IllegalArgumentException(message);
      }

      if (user.getPassword() == null || user.getPassword().length() < 6) {
          String message = rb.getString("passwordLength");
          throw new IllegalArgumentException(message);
      }

      if (user.getEmail() == null || !user.getEmail().contains("@")) {
          String message = rb.getString("invalidEmailFormat");
          throw new IllegalArgumentException(message);
      }
  }
}
