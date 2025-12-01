package com.group9.dao;

import com.group9.model.Author;
import com.group9.model.Book;
import com.group9.model.BookAttributeTranslation;
import com.group9.model.Genre;
import com.group9.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookDao {

  GenreDao genreDao = new GenreDao();
  AuthorDao authorDao = new AuthorDao();

    public List<Book> getAllBooks(String languageCode) throws SQLException {
        List<Book> books = new ArrayList<>();

        String sql;
        boolean isEnglish = "en".equalsIgnoreCase(languageCode);
        if (isEnglish) {
            sql = "SELECT * FROM books WHERE active = TRUE";
        } else {
            sql = "SELECT b.id, " +
                    "COALESCE(bt.translated_title, b.title) AS title, " +
                    "COALESCE(bt.translated_description, b.description) AS description, " +
                    "b.isbn, b.published_year, b.price, b.created_at, b.active " +
                    "FROM books b " +
                    "LEFT JOIN book_translations bt " +
                    "ON bt.book_id = b.id " +
                    "AND bt.language_code = ? " +
                    "WHERE b.active = TRUE";
        }

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (!isEnglish) {
                ps.setString(1, languageCode);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(resultSetToBook(rs, languageCode));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching all books", e);
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
            "WHERE 1=1 AND b.active = TRUE "
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
        books.add(resultSetToBook(rs, "en"));
      }

      return books;
    } catch (SQLException e) {
      throw new RuntimeException("Error searching books", e);
    }
  }

    public Book getBookById(int id) throws SQLException {
        String bookSql = "SELECT id, title, isbn, published_year, price, description FROM books WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(bookSql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return resultSetToBook(rs, "en");
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching book by ID " + id, e);
        }
    }


    private Book resultSetToBook(ResultSet rs, String langCode) throws SQLException {
    Book book = new Book(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("isbn"),
            rs.getInt("published_year"),
            rs.getBigDecimal("price").doubleValue(),
            rs.getString("description")
    );
    book.setAuthors(authorDao.getAuthorsByBookId(book.getId(), langCode));
    book.setGenres(genreDao.getGenresByBookId(book.getId(), langCode));

    return book;
  }

    public int addBook(Book book) throws SQLException {
        int bookId;
        Connection conn = null;

        String insertBookSql = "INSERT INTO books (title, isbn, published_year, price, description) VALUES (?, ?, ?, ?, ?)";

        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(insertBookSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, book.getTitle());
                stmt.setString(2, book.getIsbn());
                stmt.setInt(3, book.getYear());
                stmt.setBigDecimal(4, java.math.BigDecimal.valueOf(book.getPrice()));
                stmt.setString(5, book.getDescription());
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        bookId = rs.getInt(1);
                    } else {
                        throw new SQLException("Failed to retrieve generated book ID");
                    }
                }
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // suppressed: rollback failure
                }
            }
            throw new SQLException("Error adding book", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }

        return bookId;
    }


    public void updateBook(Book book) {
        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            // Update book details
            String updateBookSql =
                    "UPDATE books SET title = ?, isbn = ?, published_year = ?, price = ?, description = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateBookSql)) {
                stmt.setString(1, book.getTitle());
                stmt.setString(2, book.getIsbn());
                stmt.setInt(3, book.getYear());
                stmt.setBigDecimal(4, java.math.BigDecimal.valueOf(book.getPrice()));
                stmt.setString(5, book.getDescription());
                stmt.setInt(6, book.getId());
                stmt.executeUpdate();
            }

            // Delete existing authors
            String deleteAuthorsSql = "DELETE FROM book_authors WHERE book_id = ?";
            try (PreparedStatement deleteAuthorsStmt = conn.prepareStatement(deleteAuthorsSql)) {
                deleteAuthorsStmt.setInt(1, book.getId());
                deleteAuthorsStmt.executeUpdate();
            }

            // Delete existing genres
            String deleteGenresSql = "DELETE FROM book_genres WHERE book_id = ?";
            try (PreparedStatement deleteGenresStmt = conn.prepareStatement(deleteGenresSql)) {
                deleteGenresStmt.setInt(1, book.getId());
                deleteGenresStmt.executeUpdate();
            }

            for (Author author : book.getAuthors()) {
                linkBookAuthor(conn, book.getId(), author.getId());
            }

            for (Genre genre : book.getGenres()) {
                linkBookGenre(conn, book.getId(), genre.getId());
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Error rolling back transaction", ex);
                }
            }
            throw new RuntimeException("Error updating book", e);

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }


    public void inActivateBook(Integer id) throws SQLException {
        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            String inActivateBookSql = "UPDATE books SET active = FALSE WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(inActivateBookSql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // suppressed: rollback failure
                }
            }
            throw new SQLException("Error inactivating book with ID " + id, e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    public void deleteBook(Integer id) throws SQLException {
        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            String deleteBookSql = "DELETE FROM books WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteBookSql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // suppressed: rollback failure
                }
            }
            throw new SQLException("Error deleting book with ID " + id, e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }


    public int addFullBook(Book book) throws SQLException {
    int bookId = addBook(book);
    for (Author author : book.getAuthors()) {
      linkBookAuthor(bookId, author.getId());
    }
    for (Genre genre : book.getGenres()) {
      linkBookGenre(bookId, genre.getId());
    }

    return bookId;
  }

  // Just for testing
  public void addFullBookGenreAuthor(Book book) throws SQLException {
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

  private void linkBookAuthor(Connection conn, int bookId, int authorId) throws SQLException {
    String sql = "INSERT INTO book_authors (book_id, author_id) VALUES (?, ?)";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, bookId);
      ps.setInt(2, authorId);
      ps.executeUpdate();
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

  private void linkBookGenre(Connection conn, int bookId, int genreId) throws SQLException {
    String sql = "INSERT INTO book_genres (book_id, genre_id) VALUES (?, ?)";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, bookId);
      ps.setInt(2, genreId);
      ps.executeUpdate();
    }
  }

  public List<BookAttributeTranslation> getTranslations(int bookId) {
    String sql = "SELECT language_code, translated_title, translated_description " +
            "FROM book_translations " +
            "WHERE book_id = ?";
    List<BookAttributeTranslation> translations = new ArrayList<>();

    try (Connection conn = Database.getConnection();
    PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, bookId);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        translations.add(new BookAttributeTranslation(
                rs.getString("language_code"),
                rs.getString("translated_title"),
                rs.getString("translated_description")
        ));
      }
    } catch (SQLException e) {
      System.err.println("Error fetching book translations: " + e.getMessage());
      throw new RuntimeException("Error fetching book translations");
    }

    return translations;
  }

  public void upsertTranslations(int bookId, List<BookAttributeTranslation> translations) {
    String sql = "INSERT INTO book_translations (book_id, language_code, translated_title, translated_description) " +
            "VALUES (?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE " +
            "translated_title = VALUES(translated_title), " +
            "translated_description = VALUES(translated_description)";

    try (Connection conn = Database.getConnection();
          PreparedStatement ps = conn.prepareStatement(sql)) {

      for (BookAttributeTranslation t : translations) {
        ps.setInt(1, bookId);
        ps.setString(2, t.getLanguageCode());
        ps.setString(3, t.getTranslatedName());
        ps.setString(4, t.getTranslatedDescription());
        ps.addBatch();
      }

      ps.executeBatch();
    } catch (SQLException e) {
      System.err.println("Error upserting book translations: " + e.getMessage());
      throw new RuntimeException("Error upserting book translations");
    }
  }
}
