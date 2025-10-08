package com.group9.util;

import com.group9.dao.UserDao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
  // Default values if environment variables are not set
  private static final String DEFAULT_URL = "jdbc:mariadb://localhost:3306/bookstore";
  private static final String DEFAULT_USER = "bookstore_user";
  private static final String DEFAULT_PASSWORD = "bookstore_pass";

  private static final String URL = System.getenv().getOrDefault("DATABASE_URL", DEFAULT_URL);
  private static final String USER = System.getenv().getOrDefault("DATABASE_USER", DEFAULT_USER);
  private static final String PASSWORD = System.getenv().getOrDefault("DATABASE_PASSWORD", DEFAULT_PASSWORD);

  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL, USER, PASSWORD);
  }

  // Create default admin user if not exists
  public static void init() {
    try (Connection conn = getConnection()) {
      System.out.println("Database connection established.");

      UserDao userDao = new UserDao();
      if (userDao.getUserByUsername("admin") == null) {
        if (userDao.addAdminUser()) {
          System.out.println("Default admin user created.");
        } else {
          System.err.println("Failed to create admin user.");
        }
        try {
          MockData.main(null);
        } catch (Exception e) {
          System.err.println("Failed to populate mock data: " + e.getMessage());
        }
      }

    } catch (SQLException e) {
      System.err.println("Failed to connect to the database: " + e.getMessage());
    }
  }
}
