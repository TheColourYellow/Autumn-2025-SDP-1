package com.group9.dao;

import com.group9.model.Author;
import com.group9.model.BookAttributeTranslation;
import com.group9.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthorDao {
  public List<Author> getAllAuthors(String languageCode) throws SQLException {
    Connection conn = null;
    List<Author> authors = new ArrayList<>();
    // If language is English, fetch directly from authors table
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
      throw new SQLException("Error fetching authors", e);
    } finally {
      if (conn != null) conn.close();
    }

    return authors;
  }

  public List<Author> getAuthorsByBookId(int bookId, String languageCode) throws SQLException {
    Connection conn = null;
    List<Author> authors = new ArrayList<>();

    try {
      conn = Database.getConnection();

      boolean isEnglish = "en".equalsIgnoreCase(languageCode);

      String query;
      if (isEnglish) {
        query =
                "SELECT a.id, a.name, a.description " +
                        "FROM authors a " +
                        "JOIN book_authors ba ON a.id = ba.author_id " +
                        "WHERE ba.book_id = ?";
      } else {
        query =
                "SELECT a.id, " +
                        "       COALESCE(at.translated_name, a.name) AS name, " +
                        "       COALESCE(at.translated_description, a.description) AS description " +
                        "FROM authors a " +
                        "JOIN book_authors ba ON a.id = ba.author_id " +
                        "LEFT JOIN author_translations at ON at.author_id = a.id " +
                        "     AND at.language_code = ? " +
                        "WHERE ba.book_id = ?";
      }

      PreparedStatement ps = conn.prepareStatement(query);

      if (isEnglish) {
        ps.setInt(1, bookId);
      } else {
        ps.setString(1, languageCode);
        ps.setInt(2, bookId);
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

  public int addAuthor(String name, String description) {
    String insert = "INSERT INTO authors (name, description) VALUES (?, ?)";
    try (Connection conn = Database.getConnection();
         PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, name);
      ps.setString(2, description);
      ps.executeUpdate();
      try (ResultSet keys = ps.getGeneratedKeys()) {
        if (keys.next())
          return keys.getInt(1);
        else
          throw new RuntimeException("Creating author failed, no ID obtained.");
      }
    } catch (SQLException e) {
      System.err.println("Error adding author: " + e.getMessage());
      throw new RuntimeException("Error adding author", e);
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

  public List<BookAttributeTranslation> getTranslations(int authorId) {
    String sql = "SELECT language_code, translated_name, translated_description " +
            "FROM author_translations " +
            "WHERE author_id = ?";
    List<BookAttributeTranslation> translations = new ArrayList<>();

    try (Connection conn = Database.getConnection();
    PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, authorId);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        translations.add(new BookAttributeTranslation(
                rs.getString("language_code"),
                rs.getString("translated_name"),
                rs.getString("translated_description")
        ));
      }
    } catch (SQLException e) {
      System.err.println("Error fetching author translations: " + e.getMessage());
      throw new RuntimeException("Error fetching author translations", e);
    }

    return translations;
  }

  public void upsertTranslations(int authorId, List<BookAttributeTranslation> translations) throws SQLException {
    String insertOrUpdate = "INSERT INTO author_translations (author_id, language_code, translated_name, translated_description) " +
            "VALUES (?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE " +
            "translated_name = VALUES(translated_name), " +
            "translated_description = VALUES(translated_description)";

    try (Connection conn = Database.getConnection();
         PreparedStatement ps = conn.prepareStatement(insertOrUpdate)) {

      for (BookAttributeTranslation t : translations) {
        ps.setInt(1, authorId);
        ps.setString(2, t.languageCode);
        ps.setString(3, t.translatedName);
        ps.setString(4, t.translatedDescription);
        ps.addBatch();
      }

      ps.executeBatch();
    } catch (SQLException e) {
      System.err.println("Error upserting author translations: " + e.getMessage());
      throw new SQLException("Error upserting author translations", e);
    }
  }
}
