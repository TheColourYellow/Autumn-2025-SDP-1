package com.group9.util;

import com.group9.dao.UserDao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {
  private static ResourceBundle rd = ResourceBundle.getBundle("System");
  private static final Logger log = Logger.getLogger(Database.class.getName());
  // Default values if environment variables are not set
  private static final String DEFAULT_URL = "jdbc:mariadb://localhost:3306/bookstore";
  private static final String DEFAULT_USER = "bookstore_user";
  private static final String DEFAULT_PASSWORD = rd.getString("password");

  private static final String URL = System.getenv().getOrDefault("DATABASE_URL", DEFAULT_URL);
  private static final String USER = System.getenv().getOrDefault("DATABASE_USER", DEFAULT_USER);
  private static final String PASSWORD = System.getenv().getOrDefault("DATABASE_PASSWORD", DEFAULT_PASSWORD);

    // Private constructor prevents instantiation
    private Database() {
        throw new UnsupportedOperationException("Database class");
    }
  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL, USER, PASSWORD);
  }

  // Create default admin user if not exists
  public static void init() {
    try (Connection conn = getConnection()) {
      log.info("Database connection established.");

      UserDao userDao = new UserDao();
      if (userDao.getUserByUsername("admin") == null) {
        if (userDao.addAdminUser()) {
            log.info("Default admin user created.");
        } else {
            log.info("Failed to create admin user.");
        }
      }

    } catch (SQLException e) {
      log.log(Level.SEVERE, "Failed to connect to the database: {0}", e.getMessage());
    }
  }
}
