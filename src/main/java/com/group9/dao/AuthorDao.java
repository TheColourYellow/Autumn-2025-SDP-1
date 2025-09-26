package com.group9.dao;

import com.group9.model.Author;
import com.group9.model.Genre;
import com.group9.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthorDao {
  public List<Author> getAllAuthors() throws SQLException {
    Connection conn = null;
    List<Author> authors = new ArrayList<>();
    try {
      conn = Database.getConnection();
      String query = "SELECT id, name, description FROM authors";
      PreparedStatement ps = conn.prepareStatement(query);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        Author author = new Author(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description")
        );
        authors.add(author);
      }
    } catch (SQLException e) {
      throw new SQLException("Error fetching authors", e);
    } finally {
      if (conn != null) conn.close();
    }

    return authors;
  }

  public static List<Author> getAuthorsByBookId(int bookId) throws SQLException {
    Connection conn = null;
    List<Author> authors = new ArrayList<>();
    try {
      conn = Database.getConnection();
      String query = "SELECT a.id, a.name, a.description FROM authors a " +
              "JOIN book_authors ba ON a.id = ba.author_id " +
              "WHERE ba.book_id = ?";
      PreparedStatement ps = conn.prepareStatement(query);
      ps.setInt(1, bookId);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        Author author = new Author(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description")
        );
        authors.add(author);
      }
    } catch (SQLException e) {
      throw new SQLException("Error fetching authors for book ID " + bookId, e);
    } finally {
      if (conn != null) conn.close();
    }

    return authors;
  }

  public static int addOrGetAuthor(String name) throws SQLException {
    String select = "SELECT id FROM authors WHERE name = ?";
    try (Connection conn = Database.getConnection();
         PreparedStatement ps = conn.prepareStatement(select)) {
      ps.setString(1, name);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) return rs.getInt("id");
    }

    // If not found, insert
    String insert = "INSERT INTO authors (name) VALUES (?)";
    try (Connection conn = Database.getConnection();
         PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, name);
      ps.executeUpdate();
      ResultSet keys = ps.getGeneratedKeys();
      keys.next();
      return keys.getInt(1);
    }
  }

  public Author getAuthorByName(String name) throws SQLException {
    Author author;
    String select = "SELECT id, name, description FROM authors WHERE name = ?";
    try (Connection conn = Database.getConnection();
         PreparedStatement ps = conn.prepareStatement(select)) {
      ps.setString(1, name);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        author = new Author(rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"));
        return author;
      } else {
        return null;
      }
    } catch (SQLException e) {
      throw new SQLException("Error fetching genres for " + name, e);
    }
  }

  public void addAuthor(String name, String description) throws SQLException {
    String insert = "INSERT INTO authors (name, description) VALUES (?, ?)";
    try (Connection conn = Database.getConnection();
         PreparedStatement ps = conn.prepareStatement(insert)) {
      ps.setString(1, name);
      ps.setString(2, description);
      ps.executeUpdate();
    }
  }
  public void deleteAuthorByName(String authorName) throws SQLException {
    String delete = "DELETE FROM authors WHERE name = ?";
    try (Connection conn = Database.getConnection();) {
      PreparedStatement ps = conn.prepareStatement(delete);
      ps.setString(1, authorName);
      ps.executeUpdate();
    }
  }
}
