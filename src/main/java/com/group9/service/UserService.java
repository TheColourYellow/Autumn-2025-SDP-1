package com.group9.service;

import com.group9.dao.UserDao;
import com.group9.model.User;
import com.group9.util.SessionManager;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ResourceBundle;

public class UserService {
  private final UserDao userDao;
  private ResourceBundle rb;

  public UserService(UserDao userDao) {
    this.userDao = userDao;
  }

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
