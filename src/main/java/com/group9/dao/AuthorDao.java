package com.group9.dao;

import com.group9.model.Author;
import com.group9.model.Genre;
import com.group9.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthorDao {
  public List<Author> getAllAuthors(String languageCode) throws SQLException {
    Connection conn = null;
    List<Author> authors = new ArrayList<>();
    // If language is English, fetch directly from genres table
    String query;
    boolean isEnglish = "en".equalsIgnoreCase(languageCode);
    if (isEnglish) {
      query = "SELECT id, name, description FROM authors";
    } else {
      query = "SELECT " +
              "au.id, " +
              "COALESCE(aut.translated_name, au.name) AS name, " +
              "COALESCE(aut.translated_description, au.description) AS description " +
              "FROM authors au " +
              "LEFT JOIN author_translations aut " +
              "ON au.id = aut.author_id " +
              "AND aut.language_code = ?";
    }

    try {
      conn = Database.getConnection();
      PreparedStatement ps = conn.prepareStatement(query);
      if (!isEnglish) {
        ps.setString(1, languageCode);
      }
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
      throw new SQLException("Error fetching genres", e);
    } finally {
      if (conn != null) conn.close();
    }

    return authors;
  }

  public List<Author> getAuthorsByBookId(int bookId) throws SQLException {
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

  public void updateAuthor(Author author) {
    String update = "UPDATE authors SET name = ?, description = ? WHERE id = ?";
    try (Connection conn = Database.getConnection();
         PreparedStatement ps = conn.prepareStatement(update)) {
      ps.setString(1, author.getName());
      ps.setString(2, author.getDescription());
      ps.setInt(3, author.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Error updating author: " + e.getMessage());
      throw new RuntimeException("Error updating author", e);
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
