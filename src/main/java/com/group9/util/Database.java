package com.group9.util;

import com.group9.dao.UserDao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {
  private static final Logger log = Logger.getLogger(Database.class.getName());

  // Database configuration from environment variables
  private static final String URL = System.getenv().get("DATABASE_URL");
  private static final String USER = System.getenv().get("DATABASE_USER");
  private static final String PASSWORD = System.getenv().get("DATABASE_PASSWORD");

  // Private constructor prevents instantiation
  private Database() {
    throw new UnsupportedOperationException("Database class");
  }

  /**
   * Creates and returns a JDBC connection using the configured database URL, username, and password.
   * This method does not manage connection pooling; each call opens a new connection.
   *
   * @return an active {@link Connection} to the database
   * @throws SQLException if the database is unreachable or credentials are invalid
   */
  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL, USER, PASSWORD);
  }

  // Create default admin user if not exists
  /**
   * Initialize the database by creating a default admin user if it does not exist.
   */
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
