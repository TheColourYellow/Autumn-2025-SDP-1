package com.group9.util;

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
}
