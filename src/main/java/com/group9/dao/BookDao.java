package com.group9.dao;

import com.group9.model.Author;
import com.group9.model.Book;
import com.group9.model.Genre;
import com.group9.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookDao {

  public List<Book> getAllBooks() {
    List<Book> books = new ArrayList<>();

    try (Connection conn = Database.getConnection()) {
      String bookSql = "SELECT * FROM books";
      PreparedStatement ps = conn.prepareStatement(bookSql);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        books.add(resultSetToBook(rs));
      }

    } catch (SQLException e) {
      throw new RuntimeException("Error fetching all books:" + e.getMessage(), e);
    }

    return books;
  }

  public List<Book> findBooks(List<Integer> authorIds, List<Integer> genreIds) {
    StringBuilder sql = new StringBuilder(
            "SELECT DISTINCT b.* " +
            "FROM books b " +
            "LEFT JOIN book_authors ba ON b.id = ba.book_id " +
            "LEFT JOIN authors a ON ba.author_id = a.id " +
            "LEFT JOIN book_genres bg ON b.id = bg.book_id " +
            "LEFT JOIN genres g ON bg.genre_id = g.id " +
            "WHERE 1=1 "
    );

    List<Object> params = new ArrayList<>();

    if (authorIds != null && !authorIds.isEmpty()) {
      sql.append(" AND a.id IN (");
      sql.append(authorIds.stream().map(id -> "?").collect(Collectors.joining(",")));
      sql.append(") ");
      params.addAll(authorIds);
    }

    if (genreIds != null && !genreIds.isEmpty()) {
      sql.append(" AND g.id IN (");
      sql.append(genreIds.stream().map(id -> "?").collect(Collectors.joining(",")));
      sql.append(") ");
      params.addAll(genreIds);
    }

    try (Connection conn = Database.getConnection();
    PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
      for (int i = 0; i < params.size(); i++) {
        stmt.setObject(i + 1, params.get(i));
      }

      ResultSet rs = stmt.executeQuery();
      List<Book> books = new ArrayList<>();

      while (rs.next()) {
        books.add(resultSetToBook(rs));
      }

      return books;
    } catch (SQLException e) {
      throw new RuntimeException("Error searching books", e);
    }
  }

  public Book getBookById(int id) throws SQLException {
    Book book;
    Connection conn = null;

    try {
      conn = Database.getConnection();

      String bookSql = "SELECT id, title, isbn, published_year, price, description FROM books WHERE id = ?";
      PreparedStatement ps = conn.prepareStatement(bookSql);
      ps.setInt(1, id);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        book = resultSetToBook(rs);
      } else {
        return null; // Book not found
      }
    } catch (SQLException e) {
      throw new SQLException("Error fetching book by ID", e);
    } finally {
      if (conn != null) conn.close();
    }

    return book;
  }

  private Book resultSetToBook(ResultSet rs) throws SQLException {
    Book book = new Book(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("isbn"),
            rs.getInt("published_year"),
            rs.getBigDecimal("price").doubleValue(),
            rs.getString("description")
    );
    book.setAuthors(AuthorDao.getAuthorsByBookId(book.getId()));
    book.setGenres(GenreDao.getGenresByBookId(book.getId()));

    return book;
  }

  public int addBook(Book book) throws SQLException {
    int bookId;
    Connection conn = null;

    try {
      conn = Database.getConnection();
      conn.setAutoCommit(false);

      String insertBookSql = "INSERT INTO books (title, isbn, published_year, price, description) VALUES (?, ?, ?, ?, ?)";
      PreparedStatement stmt = conn.prepareStatement(insertBookSql, Statement.RETURN_GENERATED_KEYS);
      stmt.setString(1, book.getTitle());
      stmt.setString(2, book.getIsbn());
      stmt.setInt(3, book.getYear());
      stmt.setBigDecimal(4, java.math.BigDecimal.valueOf(book.getPrice()));
      stmt.setString(5, book.getDescription());
      stmt.executeUpdate();

      ResultSet rs = stmt.getGeneratedKeys();
      if (rs.next()) {
        bookId = rs.getInt(1);
      } else {
        throw new SQLException("Failed to retrieve generated book ID");
      }
      conn.commit();
    } catch (SQLException e) {
      if (conn != null) {
        conn.rollback();
      }
      throw e;
    } finally {
      if (conn != null) {
        conn.setAutoCommit(true);
        conn.close();
      }
    }

    return bookId;
  }

  // Just for testing
  public void addFullBook(Book book) throws SQLException {
    int bookId = addBook(book);
    for (Author author : book.getAuthors()) {
      int authorId = AuthorDao.addOrGetAuthor(author.getName());
      linkBookAuthor(bookId, authorId);
    }
    for (Genre genre : book.getGenres()) {
      int genreId = GenreDao.addOrGetGenre(genre.getName());
      linkBookGenre(bookId, genreId);
    }
  }

  private void linkBookAuthor(int bookId, int authorId) throws SQLException {
    Connection conn = null;
    try {
      conn = Database.getConnection();
      conn.setAutoCommit(false);

      String sql = "INSERT INTO book_authors (book_id, author_id) VALUES (?, ?)";
      try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, bookId);
        ps.setInt(2, authorId);
        ps.executeUpdate();
      }
      conn.commit();
    } catch (SQLException e) {
      if (conn != null) {
        conn.rollback();
      }
      throw new SQLException("Error linking book and author", e);
    } finally {
      if (conn != null) {
        conn.setAutoCommit(true);
        conn.close();
      }
    }
  }

  private void linkBookGenre(int bookId, int genreId) throws SQLException {
    Connection conn = null;
    try {
      conn = Database.getConnection();
      conn.setAutoCommit(false);

      String sql = "INSERT INTO book_genres (book_id, genre_id) VALUES (?, ?)";
      try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, bookId);
        ps.setInt(2, genreId);
        ps.executeUpdate();
      }
      conn.commit();
    } catch (SQLException e) {
      if (conn != null) {
        conn.rollback();
      }
      throw new SQLException("Error linking book and genre", e);
    } finally {
      if (conn != null) {
        conn.setAutoCommit(true);
        conn.close();
      }
    }
  }
}
