package com.group9.service;

import com.group9.dao.AuthorDao;
import com.group9.model.Author;
import com.group9.model.BookAttributeTranslation;
import com.group9.util.SessionManager;

import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for managing authors and their translations.
 */
public class AuthorService {
  private final AuthorDao authorDao;
  private ResourceBundle rb;
  private static final Logger log = Logger.getLogger(AuthorService.class.getName());

  /**
   * Constructor for AuthorService.
   *
   * @param authorDao the {@link AuthorDao} instance for database operations
   */
  public AuthorService(AuthorDao authorDao) {
    this.authorDao = authorDao;
  }

  /**
   * Retrieves all authors from the database.
   *
   * @return a list of {@link Author} objects
   * @throws IllegalArgumentException if an error occurs during retrieval
   */
  public List<Author> getAllAuthors() {
    rb = SessionManager.getResourceBundle();
    try {
      return authorDao.getAllAuthors(SessionManager.getLocale().getLanguage());
    } catch (Exception e) {
      String message = rb.getString("errorRetrievingAuthors");
      log.log(Level.SEVERE, message, e.getMessage());
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Adds a new author to the database.
   *
   * @param name the name of the author
   * @param desc the description of the author
   * @return the ID of the newly added author
   * @throws IllegalArgumentException if the name is null/empty or an error occurs during addition
   */
  public int addAuthor(String name, String desc) throws IllegalArgumentException {
    rb = SessionManager.getResourceBundle();
    if (name == null || name.isEmpty()) {
      String message = rb.getString("authorNull");
      throw new IllegalArgumentException(message);
    }

    try {
      return authorDao.addAuthor(name, desc);
    } catch (Exception e) {
      String message = rb.getString("errorAddingAuthor");
      log.log(Level.SEVERE, message, e.getMessage());
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Updates an existing author in the database.
   *
   * @param author the {@link Author} object with updated information
   * @throws IllegalArgumentException if the author ID is invalid, name is null/empty,
   *                                  or an error occurs during update
   */
  public void updateAuthor(Author author) {
    rb = SessionManager.getResourceBundle();
    if (author.getId() <= 0) {
      String message = rb.getString("authorIdError");
      throw new IllegalArgumentException(message);
    }

    if (author.getName() == null || author.getName().isEmpty()) {
      String message = rb.getString("authorNameNull");
      throw new IllegalArgumentException(message);
    }

    // Check if author exists
    try {
      Author existingAuthor = authorDao.getAuthorByName(author.getName());
      if (existingAuthor == null) {
        String message = rb.getString("author");
        String message2 = rb.getString("doesNotExist");
        throw new IllegalArgumentException(message + " " + author.getName() + " " + message2);
      }
      if (existingAuthor.getId() != author.getId()) {
        String message = rb.getString("anotherAuthorExists");
        String message2 = rb.getString("alreadyExists");
        throw new IllegalArgumentException(message + " " + author.getName() + " " + message2);
      }
    } catch (SQLException e) {
      String message = rb.getString("errorCheckingExistingAuthor");
      String message2 = rb.getString("errorUpdatingAuthor");
      log.log(Level.SEVERE, message, e.getMessage());
      throw new IllegalArgumentException(message2);
    }

    // Proceed to update
    try {
      authorDao.updateAuthor(author);
    } catch (Exception e) {
      String message = rb.getString("errorUpdatingAuthor");
      log.log(Level.SEVERE, message, e.getMessage());
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Deletes an author from the database by name.
   *
   * @param name the name of the author to delete
   * @throws IllegalArgumentException if the name is empty or an error occurs during deletion
   */
  public void deleteAuthor(String name) throws IllegalArgumentException {
    rb = SessionManager.getResourceBundle();
    if (name.isEmpty()) {
      String message = rb.getString("authorNameNull");
      throw new IllegalArgumentException(message);
    }

    try {
      authorDao.deleteAuthorByName(name);
    } catch (SQLException e) {
      String message = rb.getString("errorDeletingAuthor");
      log.log(Level.SEVERE, message, e.getMessage());
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Saves an author along with their translations.
   *
   * @param author       the {@link Author} object to save
   * @param translations a list of {@link BookAttributeTranslation} objects
   * @throws IllegalArgumentException if an error occurs during saving
   */
  public void saveAuthorWithTranslations(Author author, List<BookAttributeTranslation> translations) {
    rb = SessionManager.getResourceBundle();
    int authorId = author.getId();

    // Save new or update existing author
    if (authorId <= 0) {
      try {
        authorId = addAuthor(author.getName(), author.getDescription());
      } catch (Exception e) {
        String message = rb.getString("errorAddingAuthor");
        log.log(Level.SEVERE, message, e.getMessage());
        throw new IllegalArgumentException(message);
      }
    } else {
      try {
        updateAuthor(author);
      } catch (Exception e) {
        throw new IllegalArgumentException(e);
      }
    }

    // Save translations
    try {
      authorDao.upsertTranslations(authorId, translations);
    } catch (Exception e) {
      String message = rb.getString("errorSavingTranslations");
      log.log(Level.SEVERE, message, e.getMessage());
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Retrieves translations for a specific author.
   *
   * @param authorId the ID of the author
   * @return a list of {@link BookAttributeTranslation} objects
   * @throws IllegalArgumentException if an error occurs during retrieval
   */
  public List<BookAttributeTranslation> getTranslationsForAuthor(int authorId) {
    rb = SessionManager.getResourceBundle();
    try {
      return authorDao.getTranslations(authorId);
    } catch (Exception e) {
      String message = rb.getString("errorRetrievingTranslations");
      log.log(Level.SEVERE, message, e.getMessage());
      throw new IllegalArgumentException(message);
    }
  }
}
