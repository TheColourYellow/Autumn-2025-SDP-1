package com.group9.service;

import com.group9.dao.UserDao;
import com.group9.model.User;
import com.group9.util.SessionManager;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
  private final UserDao userDao;

  public UserService(UserDao userDao) {
    this.userDao = userDao;
  }

  public User loginUser(String username, String password) {
    // Validate input
    if (username == null || username.trim().isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null or empty");
    }
    if (password == null || password.isEmpty()) {
      throw new IllegalArgumentException("Password cannot be null or empty");
    }

    // Retrieve user
    User user = userDao.getUserByUsername(username);
    if (user == null) {
      throw new IllegalArgumentException("Invalid username or password");
    }

    // Verify password
    if (!BCrypt.checkpw(password, user.getPassword())) {
      throw new IllegalArgumentException("Invalid username or password");
    }

    // Successful login
    return user;
  }

  public User registerUser(String username, String password, String email) {
    User newUser = new User(username.trim(), password, email.trim());

    validateUser(newUser);

    // Check if username or email already exists
    if (userDao.getUserByUsername(username) != null) {
      throw new IllegalArgumentException("Username already exists");
    }
    if (userDao.getUserByEmail(email) != null) {
      throw new IllegalArgumentException("Email already registered");
    }

    // Hash password
    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
    newUser.setPassword(hashedPassword);

    // Create new user
    return userDao.addUser(newUser);
  }

  private void validateUser(User user) {
    if (user == null)
      throw new IllegalArgumentException("User cannot be null");

    if (user.getUsername() == null || user.getUsername().isEmpty())
      throw new IllegalArgumentException("Username cannot be null or empty");

    if (user.getPassword() == null || user.getPassword().length() < 6)
      throw new IllegalArgumentException("Password must be at least 6 characters");

    if (user.getEmail() == null || !user.getEmail().contains("@"))
      throw new IllegalArgumentException("Invalid email address");
  }
}
