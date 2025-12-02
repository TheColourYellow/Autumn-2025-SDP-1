package com.group9.service;

import com.group9.dao.BookDao;
import com.group9.model.Book;
import com.group9.model.BookAttributeTranslation;
import com.group9.util.SessionManager;

import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookService {
  private final BookDao bookDao;
  private ResourceBundle rb;
  private static final String BOOK_ID_ERROR = "bookIdError";
    private static final Logger log = Logger.getLogger(BookService.class.getName());

  public BookService(BookDao bookDao) {
    this.bookDao = bookDao;
  }

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

  public List<Book> searchBooks(List<Integer> authorIds, List<Integer> genreIds) {
    return bookDao.findBooks(authorIds, genreIds);
  }

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
