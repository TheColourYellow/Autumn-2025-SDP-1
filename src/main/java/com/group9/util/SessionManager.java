package com.group9.util;

import com.group9.model.User;

public class SessionManager {
  private static User currentUser;

  public static void login(User user) {
    currentUser = user;
  }

  public static void logout() {
    currentUser = null;
  }

  public static User getCurrentUser() {
    return currentUser;
  }

  public static boolean isLoggedIn() {
    return currentUser != null;
  }

  public static boolean isAdmin() {
    return isLoggedIn() && "admin".equals(currentUser.getRole());
  }
}
