package com.group9.dao;

import com.group9.model.Genre;
import com.group9.model.User;
import com.group9.util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
  public User getUserByUsername(String username) {
    String sql = "SELECT id, username, password_hash, email, role FROM users WHERE username = ?";
    try (Connection conn = Database.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, username);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return resultSetToUser(rs);
      } else {
        return null; // User not found
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error retrieving user by username: " + e.getMessage(), e);
    }
  }

  public User getUserByEmail(String email) {
    String sql = "SELECT id, username, password_hash, email FROM users WHERE email = ?";
    try (Connection conn = Database.getConnection();
          PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, email);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return resultSetToUser(rs);
      } else {
        return null; // User not found
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error retrieving user by email: " + e.getMessage(), e);
    }
  }

  private User resultSetToUser(ResultSet rs) throws SQLException {
    return new User(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("password_hash"),
            rs.getString("email"),
            rs.getString("role")
    );
  }

  public User addUser(User user) {
    String sql = "INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)";

    try (Connection conn = Database.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
      stmt.setString(1, user.getUsername());
      stmt.setString(2, user.getPassword());
      stmt.setString(3, user.getEmail());
      stmt.executeUpdate();

      ResultSet rs = stmt.getGeneratedKeys();
      if (rs.next()) {
        int userId = rs.getInt(1);
        return new User(userId, user.getUsername(), user.getPassword(), user.getEmail(), user.getRole());
      } else {
        throw new SQLException("Failed to retrieve generated user ID");
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error adding user: " + e.getMessage(), e);
    }
  }

  public void deleteUser(int id) {
    String sql = "DELETE FROM users WHERE id = ?";

    try (Connection conn = Database.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, id);
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Error deleting user: " + e.getMessage(), e);
    }
  }

  public boolean addAdminUser() {
    String sql = "INSERT INTO users (username, email, password_hash, role) VALUES ('admin', 'default@admin.com', '$2a$10$hBux0sXhxyo8KX1vi6YEo.leo.4QyOCGfnBE3yxu6xrKHYrCowD.q', 'admin');";

    try (Connection conn = Database.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
      int rowsAffected = stmt.executeUpdate();
      return rowsAffected > 0;
    } catch (SQLException e) {
      throw new RuntimeException("Error adding admin user: " + e.getMessage(), e);
    }
  }
  //Function added 15.11. for database localisation
  public String getLanguageByUsername(String username) {
    String sql = "SELECT username, language_code FROM users WHERE username = ?";
    try (Connection conn = Database.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, username);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return rs.getString("language_code");
      } else {
        return null; // User not found
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error retrieving user by username: " + e.getMessage(), e);
    }
  }
  //Function added 15.11. for database localisation
  public void updateUserLanguage(User user, String languageCode) {
    String update = "UPDATE users SET language_code = ? WHERE id = ?";
    try (Connection conn = Database.getConnection();
         PreparedStatement ps = conn.prepareStatement(update)) {
      ps.setString(1, languageCode);
      ps.setInt(2, user.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Error updating genre: " + e.getMessage());
      throw new RuntimeException("Error updating genre", e);
    }
  }
}
