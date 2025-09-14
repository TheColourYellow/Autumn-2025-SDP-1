package com.group9.dao;

import com.group9.model.Genre;
import com.group9.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenreDao {
  public List<Genre> getGenresByBookId(int bookId) throws SQLException {
    Connection conn = null;
    List<Genre> genres = new ArrayList<>();
    try {
      conn = Database.getConnection();
      String query = "SELECT g.id, g.name, g.description FROM genres g " +
              "JOIN book_genres bg ON g.id = bg.genre_id " +
              "WHERE bg.book_id = ?";
      PreparedStatement ps = conn.prepareStatement(query);
      ps.setInt(1, bookId);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        Genre genre = new Genre(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description")
        );
        genres.add(genre);
      }
    } catch (SQLException e) {
      throw new SQLException("Error fetching genres for book ID " + bookId, e);
    } finally {
      if (conn != null) conn.close();
    }

    return genres;
  }


  public static int addOrGetGenre(String name) throws SQLException {
    String select = "SELECT id FROM genres WHERE name = ?";
    try (Connection conn = Database.getConnection();
         PreparedStatement ps = conn.prepareStatement(select)) {
      ps.setString(1, name);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) return rs.getInt("id");
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


  public int getGenreByName(String name) throws SQLException {
    String select = "SELECT id FROM genres WHERE name = ?";
    try (Connection conn = Database.getConnection();
         PreparedStatement ps = conn.prepareStatement(select)) {
      ps.setString(1, name);
      ResultSet rs = ps.executeQuery();
      rs.next();
      return rs.getInt("id");
    }
  }
  public void addGenre(String name) throws SQLException {
    String insert = "INSERT INTO genres (name) VALUES (?)";
    try (Connection conn = Database.getConnection();
         PreparedStatement ps = conn.prepareStatement(insert)) {
      ps.setString(1, name);
      ps.executeUpdate();
    }
  }
  public void deleteGenreByName(String genreName) throws SQLException {
    String delete = "DELETE FROM genres WHERE name = ?";
    try (Connection conn = Database.getConnection();) {
      PreparedStatement ps = conn.prepareStatement(delete);
      ps.setString(1, genreName);
      ps.executeUpdate();
    }
  }
}
