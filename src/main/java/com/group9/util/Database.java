package com.group9.util;

import com.group9.dao.UserDao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
  private static final String URL = System.getenv("DATABASE_URL");
  private static final String USER = System.getenv("DATABASE_USER");
  private static final String PASSWORD = System.getenv("DATABASE_PASSWORD");

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
      }

    } catch (SQLException e) {
      System.err.println("Failed to connect to the database: " + e.getMessage());
    }
  }
}
