package com.group9.dao;

import com.group9.model.BookAttributeTranslation;
import com.group9.model.Genre;
import com.group9.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenreDao {
    private static final String DESCRIPTION = "description";
    private static final String NAME = "name";
    private static final String ID = "id";
    public List<Genre> getAllGenres(String languageCode) throws SQLException {
        List<Genre> genres = new ArrayList<>();
        boolean isEnglish = "en".equalsIgnoreCase(languageCode);

        String query;
        if (isEnglish) {
            query = "SELECT id, name, description FROM genres";
        } else {
            query = "SELECT g.id, " +
                    "COALESCE(gt.translated_name, g.name) AS name, " +
                    "COALESCE(gt.translated_description, g.description) AS description " +
                    "FROM genres g " +
                    "LEFT JOIN genre_translations gt " +
                    "ON g.id = gt.genre_id " +
                    "AND gt.language_code = ?";
        }

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            if (!isEnglish) {
                ps.setString(1, languageCode);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    genres.add(new Genre(
                            rs.getInt(ID),
                            rs.getString(NAME),
                            rs.getString(DESCRIPTION)
                    ));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching genres", e);
        }

        return genres;
    }


    public List<Genre> getGenresByBookId(int bookId, String languageCode) throws SQLException {
        List<Genre> genres = new ArrayList<>();
        boolean isEnglish = "en".equalsIgnoreCase(languageCode);

        String query;
        if (isEnglish) {
            query = "SELECT g.id, g.name, g.description " +
                    "FROM genres g " +
                    "JOIN book_genres bg ON g.id = bg.genre_id " +
                    "WHERE bg.book_id = ?";
        } else {
            query = "SELECT g.id, " +
                    "       COALESCE(gt.translated_name, g.name) AS name, " +
                    "       COALESCE(gt.translated_description, g.description) AS description " +
                    "FROM genres g " +
                    "JOIN book_genres bg ON g.id = bg.genre_id " +
                    "LEFT JOIN genre_translations gt ON gt.genre_id = g.id " +
                    "     AND gt.language_code = ? " +
                    "WHERE bg.book_id = ?";
        }

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            if (isEnglish) {
                ps.setInt(1, bookId);
            } else {
                ps.setString(1, languageCode);
                ps.setInt(2, bookId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    genres.add(new Genre(
                            rs.getInt(ID),
                            rs.getString(NAME),
                            rs.getString(DESCRIPTION)
                    ));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching genres for book ID " + bookId, e);
        }

        return genres;
    }


    public static int addOrGetGenre(String name) throws SQLException {
    String select = "SELECT id FROM genres WHERE name = ?";
    try (Connection conn = Database.getConnection();
         PreparedStatement ps = conn.prepareStatement(select)) {
      ps.setString(1, name);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) return rs.getInt(ID);
    }

    String insert = "INSERT INTO genres (name) VALUES (?)";
    try (Connection conn = Database.getConnection();
         PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, name);
      ps.executeUpdate();
      ResultSet keys = ps.getGeneratedKeys();
      keys.next();
      return keys.getInt(1);
    }
  }


  public Genre getGenreByName(String name) throws SQLException {
    Genre genre;
    String select = "SELECT id, name, description FROM genres WHERE name = ?";
    try (Connection conn = Database.getConnection();
         PreparedStatement ps = conn.prepareStatement(select)) {
      ps.setString(1, name);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        genre = new Genre(rs.getInt(ID),
                rs.getString(NAME),
                rs.getString(DESCRIPTION));
        return genre;
      } else {
        return null;
      }
    } catch (SQLException e) {
      throw new SQLException("Error fetching genres for " + name, e);
    }
  }

  public int addGenre(String name, String description) {
    String insert = "INSERT INTO genres (name, description) VALUES (?, ?)";
    try (Connection conn = Database.getConnection();
         PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, name);
      ps.setString(2, description);
      ps.executeUpdate();
      try (ResultSet keys = ps.getGeneratedKeys()) {
        if (keys.next()) {
          return keys.getInt(1);
        } else {
          throw new RuntimeException("Creating genre failed, no ID obtained.");
        }
      }
    } catch (SQLException e) {
      System.err.println("Error adding genre: " + e.getMessage());
      throw new RuntimeException("Error adding genre", e);
    }
  }

  public void updateGenre(Genre genre) {
    String update = "UPDATE genres SET name = ?, description = ? WHERE id = ?";
    try (Connection conn = Database.getConnection();
         PreparedStatement ps = conn.prepareStatement(update)) {
      ps.setString(1, genre.getName());
      ps.setString(2, genre.getDescription());
      ps.setInt(3, genre.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Error updating genre: " + e.getMessage());
      throw new RuntimeException("Error updating genre", e);
    }
  }

    // java
    public void deleteGenreByName(String genreName) throws SQLException {
        String delete = "DELETE FROM genres WHERE name = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(delete)) {
            ps.setString(1, genreName);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error deleting genre with name " + genreName, e);
        }
    }


    public List<BookAttributeTranslation> getTranslations(int genreId) throws SQLException {
        String sql = "SELECT language_code, translated_name, translated_description " +
                "FROM genre_translations " +
                "WHERE genre_id = ?";
        List<BookAttributeTranslation> translations = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, genreId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    translations.add(new BookAttributeTranslation(
                            rs.getString("language_code"),
                            rs.getString("translated_name"),
                            rs.getString("translated_description")
                    ));
                }
            }
        }

        return translations;
    }


    public void upsertTranslations(int genreId, List<BookAttributeTranslation> translations) {
    String sql = "INSERT INTO genre_translations (genre_id, language_code, translated_name, translated_description) " +
        "VALUES (?, ?, ?, ?) " +
        "ON DUPLICATE KEY UPDATE " +
            "translated_name = VALUES(translated_name), " +
            "translated_description = VALUES(translated_description)";

    try (Connection conn = Database.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

      for (BookAttributeTranslation t : translations) {
        ps.setInt(1, genreId);
        ps.setString(2, t.getLanguageCode());
        ps.setString(3, t.getTranslatedName());
        ps.setString(4, t.getTranslatedDescription());
        ps.addBatch();
      }

      ps.executeBatch();
    } catch (SQLException e) {
      System.err.println("Error upserting genre translations: " + e.getMessage());
      throw new RuntimeException("Error upserting genre translations");
    }
  }
}
