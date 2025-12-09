package com.group9.service;

import com.group9.dao.BookDao;
import com.group9.model.Book;
import com.group9.model.BookAttributeTranslation;
import com.group9.util.SessionManager;

import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for managing books and their translations.
 */
public class BookService {
  private final BookDao bookDao;
  private ResourceBundle rb;
  private static final String BOOK_ID_ERROR = "bookIdError";
  private static final Logger log = Logger.getLogger(BookService.class.getName());

  /**
   * Constructor for BookService.
   *
   * @param bookDao the {@link BookDao} instance for database operations
   */
  public BookService(BookDao bookDao) {
    this.bookDao = bookDao;
  }

  /**
   * Retrieves all books from the database.
   *
   * @return a list of {@link Book} objects
   * @throws IllegalArgumentException if an error occurs during retrieval
   */
  public List<Book> getAllBooks() {
    rb = SessionManager.getResourceBundle();
    try {
      return bookDao.getAllBooks(SessionManager.getLocale().getLanguage());
    } catch (Exception e) {
      String message = rb.getString("errorRetrievingBooks");
      log.log(Level.SEVERE, message, e.getMessage());
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Retrieves a book by its ID.
   *
   * @param id the ID of the book
   * @return the {@link Book} object, or null if not found
   * @throws IllegalArgumentException if the ID is invalid or an error occurs during retrieval
   */
  public Book getBookById(int id) {
    rb = SessionManager.getResourceBundle();
    if (id <= 0) {
      String message = rb.getString(BOOK_ID_ERROR);
      throw new IllegalArgumentException(message);
    }

    try {
      return bookDao.getBookById(id);
    } catch (Exception e) {
      String message = rb.getString("errorRetrievingBookById");
      log.log(Level.SEVERE, message, e.getMessage());
      return null;
    }
  }

  /**
   * Searches for books based on author IDs and genre IDs.
   *
   * @param authorIds the list of author IDs to filter by
   * @param genreIds  the list of genre IDs to filter by
   * @return a list of {@link Book} objects matching the criteria
   */
  public List<Book> searchBooks(List<Integer> authorIds, List<Integer> genreIds) {
    return bookDao.findBooks(authorIds, genreIds);
  }

  /**
   * Adds a new book to the database.
   *
   * @param book the {@link Book} object to add
   * @return the ID of the newly added book, or -1 if an error occurs
   * @throws IllegalArgumentException if the book is invalid
   */
  public int addBook(Book book) {
    rb = SessionManager.getResourceBundle();
    validateBook(book);

    try {
      return bookDao.addFullBook(book);
    } catch (Exception e) {
      String message = rb.getString("errorAddingBook");
      log.log(Level.SEVERE, message, e.getMessage());
      return -1;
    }
  }

  /**
   * Updates an existing book in the database.
   *
   * @param book the {@link Book} object with updated information
   * @throws IllegalArgumentException if the book ID is invalid or the book is invalid
   */
  public void updateBook(Book book) {
    rb = SessionManager.getResourceBundle();
    validateBook(book);
    if (book.getId() <= 0) {
      String message = rb.getString(BOOK_ID_ERROR);
      throw new IllegalArgumentException(message);
    }

    try {
      bookDao.updateBook(book);
    } catch (Exception e) {
      String message = rb.getString("errorUpdatingBook");
      log.log(Level.SEVERE, message, e.getMessage());
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Saves a book along with its translations.
   *
   * @param book         the {@link Book} object to save
   * @param translations the list of {@link BookAttributeTranslation} objects
   * @throws IllegalArgumentException if an error occurs during saving
   */
  public void saveBookWithTranslations(Book book, List<BookAttributeTranslation> translations) {
    rb = SessionManager.getResourceBundle();
    int bookId = book.getId();

    // Save new or update existing book
    if (bookId <= 0) {
      bookId = addBook(book);
    } else {
      updateBook(book);
    }

    // Save translations
    try {
      bookDao.upsertTranslations(bookId, translations);
    } catch (Exception e) {
      String message = rb.getString("errorSavingTranslations");
      log.log(Level.SEVERE, message, e.getMessage());
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Deletes (soft delete) a book by its ID.
   *
   * @param bookId the ID of the book to delete
   * @throws IllegalArgumentException if the book ID is invalid or an error occurs during deletion
   */
  public void deleteBook(int bookId) throws IllegalArgumentException {
    rb = SessionManager.getResourceBundle();
    if (bookId <= 0) {
      String message = rb.getString(BOOK_ID_ERROR);
      throw new IllegalArgumentException(message);
    }

    try {
      bookDao.inActivateBook(bookId); // Soft delete
    } catch (Exception e) {
      String message = rb.getString("errorDeletingBook");
      log.log(Level.SEVERE, message, e.getMessage());
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Retrieves translations for a specific book.
   *
   * @param bookId the ID of the book
   * @return a list of {@link BookAttributeTranslation} objects
   * @throws IllegalArgumentException if an error occurs during retrieval
   */
  public List<BookAttributeTranslation> getTranslations(int bookId) {
    rb = SessionManager.getResourceBundle();
    try {
      return bookDao.getTranslations(bookId);
    } catch (Exception e) {
      String message = rb.getString("errorRetrievingTranslations");
      log.log(Level.SEVERE, message, e.getMessage());
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Validates the book object.
   *
   * @param book the {@link Book} object to validate
   * @throws IllegalArgumentException if the book is invalid
   */
  private void validateBook(Book book) {
    rb = SessionManager.getResourceBundle();
    if (book == null) {
      String message = rb.getString("bookNull");
      throw new IllegalArgumentException(message);
    }

    if (book.getTitle() == null || book.getTitle().isEmpty()) {
      String message = rb.getString("bookTitleNull");
      throw new IllegalArgumentException(message);
    }

    if (book.getPrice() < 0) {
      String message = rb.getString("bookPriceNegative");
      throw new IllegalArgumentException(message);
    }

    if (book.getYear() < 0) {
      String message = rb.getString("bookYearNegative");
      throw new IllegalArgumentException(message);
    }
  }
}
