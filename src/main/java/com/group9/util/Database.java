package com.group9.util;

import com.group9.dao.UserDao;
import com.group9.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
  private static final String URL = "jdbc:mariadb://localhost:3306/bookstore";
  private static final String USER = "bookstore_user";
  private static final String PASSWORD = "bookstore_pass";

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
