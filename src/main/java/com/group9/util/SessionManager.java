package com.group9.util;

import com.group9.model.User;

import java.util.Locale;
import java.util.ResourceBundle;

public class SessionManager {
    // Prevent instantiation
    private SessionManager() {
        throw new UnsupportedOperationException("SessionManager is a utility class and cannot be instantiated");
    }

  private static User currentUser;
  private static ResourceBundle currentBundle;
  private static Locale currentLocale = new Locale("en", "US");
  private static String currentLanguage = "English"; // For storing the selected language in dropdown

  static {
    setLocale(currentLocale);
  }

  // User
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

  // Localization
  public static void setLocale(Locale locale) {
    currentLocale = locale;
    currentBundle = ResourceBundle.getBundle("LanguageBundle", locale);
  }

  public static Locale getLocale() {
    return currentLocale;
  }

  public static ResourceBundle getResourceBundle() {
    return currentBundle;
  }

  public static String getLanguage() {
    return currentLanguage;
  }
  public static void setLanguage(String language) {
    currentLanguage = language;
  }
}
