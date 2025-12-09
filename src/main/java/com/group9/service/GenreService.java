package com.group9.service;

import com.group9.dao.GenreDao;
import com.group9.model.BookAttributeTranslation;
import com.group9.model.Genre;
import com.group9.util.SessionManager;

import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for managing genres and their translations.
 */
public class GenreService {
  private final GenreDao genreDao;
  private ResourceBundle rb;
  private static final Logger log = Logger.getLogger(GenreService.class.getName());

  /**
   * Constructor for GenreService.
   *
   * @param genreDao the {@link GenreDao} instance for database operations
   */
  public GenreService(GenreDao genreDao) {
    this.genreDao = genreDao;
  }

  /**
   * Retrieves all genres from the database.
   *
   * @return a list of {@link Genre} objects
   * @throws IllegalArgumentException if an error occurs during retrieval
   */
  public List<Genre> getAllGenres() {
    rb = SessionManager.getResourceBundle();
    try {
      return genreDao.getAllGenres(SessionManager.getLocale().getLanguage());
    } catch (Exception e) {
      String message = rb.getString("errorRetrievingGenres");
      log.log(Level.SEVERE, message, e.getMessage());
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Adds a new genre to the database.
   *
   * @param name the name of the genre
   * @param desc the description of the genre
   * @return the ID of the newly added genre
   * @throws IllegalArgumentException if the name is null/empty or an error occurs during addition
   */
  public int addGenre(String name, String desc) throws IllegalArgumentException {
    rb = SessionManager.getResourceBundle();
    if (name == null || name.isEmpty()) {
      String message = rb.getString("genreNull");
      throw new IllegalArgumentException(message);
    }

    try {
      return genreDao.addGenre(name, desc);
    } catch (Exception e) {
      String message = rb.getString("errorAddingGenre");
      log.log(Level.SEVERE, message, e.getMessage());
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Updates an existing genre in the database.
   *
   * @param genre the {@link Genre} object to update
   * @throws IllegalArgumentException if the genre is invalid or an error occurs during update
   */
  public void updateGenre(Genre genre) {
    rb = SessionManager.getResourceBundle();

    if (genre.getId() <= 0) {
      String message = rb.getString("genreIdError");
      throw new IllegalArgumentException(message);
    }

    if (genre.getName() == null || genre.getName().isEmpty()) {
      String message = rb.getString("genreNameNull");
      throw new IllegalArgumentException(message);
    }

    // Check if genre exists
    try {
      Genre existingGenre = genreDao.getGenreByName(genre.getName());
      if (existingGenre == null) {
        String message = rb.getString("genre");
        String message2 = rb.getString("doesNotExist");
        throw new IllegalArgumentException(message + " " + genre.getName() + " " + message2);
      }
      if (existingGenre.getId() != genre.getId()) {
        String message = rb.getString("genreNameConflict");
        String message2 = rb.getString("alreadyExists");
        throw new IllegalArgumentException(message + " " + genre.getName() + " " + message2);
      }
    } catch (SQLException e) {
      String message = rb.getString("errorCheckingGenre");
      String message2 = rb.getString("errorUpdatingGenre");
      log.log(Level.SEVERE, message, e.getMessage());
      throw new IllegalArgumentException(message2);
    }

    try {
      genreDao.updateGenre(genre);
    } catch (Exception e) {
      String message = rb.getString("errorUpdatingGenre");
      log.log(Level.SEVERE, message, e.getMessage());
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Saves a genre along with its translations.
   *
   * @param genre        the {@link Genre} object to save
   * @param translations the list of {@link BookAttributeTranslation} objects
   * @throws IllegalArgumentException if an error occurs during saving
   */
  public void saveGenreWithTranslations(Genre genre, List<BookAttributeTranslation> translations) {
    rb = SessionManager.getResourceBundle();
    int genreId = genre.getId();

    // Save new or update existing genre
    if (genreId <= 0) {
      try {
        genreId = addGenre(genre.getName(), genre.getDescription());
      } catch (Exception e) {
        throw new IllegalArgumentException(e);
      }
    } else {
      try {
        updateGenre(genre);
      } catch (Exception e) {
        throw new IllegalArgumentException(e);
      }
    }

    // Save translations
    try {
      genreDao.upsertTranslations(genreId, translations);
    } catch (Exception e) {
      String message = rb.getString("errorSavingTranslations");
      log.log(Level.SEVERE, message, e.getMessage());
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Deletes (soft delete) a genre by its name.
   *
   * @param name the name of the genre to delete
   * @throws IllegalArgumentException if the name is null/empty or an error occurs during deletion
   */
  public void deleteGenre(String name) throws IllegalArgumentException {
    rb = SessionManager.getResourceBundle();
    if (name.isEmpty()) {
      String message = rb.getString("genreNameNull");
      throw new IllegalArgumentException(message);
    }

    try {
      genreDao.deleteGenreByName(name);
    } catch (Exception e) {
      String message = rb.getString("errorDeletingGenre");
      log.log(Level.SEVERE, message, e.getMessage());
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Retrieves translations for a specific genre by its ID.
   *
   * @param genreId the ID of the genre
   * @return a list of {@link BookAttributeTranslation} objects
   * @throws IllegalArgumentException if an error occurs during retrieval
   */
  public List<BookAttributeTranslation> getTranslationsForGenre(int genreId) {
    rb = SessionManager.getResourceBundle();
    try {
      return genreDao.getTranslations(genreId);
    } catch (Exception e) {
      String message = rb.getString("errorRetrievingTranslations");
      log.log(Level.SEVERE, message, e.getMessage());
      throw new IllegalArgumentException(message);
    }
  }
}
