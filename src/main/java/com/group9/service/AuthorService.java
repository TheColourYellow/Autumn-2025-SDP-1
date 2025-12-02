package com.group9.service;

import com.group9.dao.AuthorDao;
import com.group9.model.Author;
import com.group9.model.BookAttributeTranslation;
import com.group9.util.SessionManager;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthorService {
    private AuthorDao authorDao;
    private ResourceBundle rb;
    private static final Logger log = Logger.getLogger(AuthorService.class.getName());

    public AuthorService(AuthorDao authorDao) {
    this.authorDao = authorDao;
  }

    public List<Author> getAllAuthors() {
        rb = SessionManager.getResourceBundle();
        try {
            return authorDao.getAllAuthors(SessionManager.getLocale().getLanguage());
        } catch (Exception e) {
            String message = rb.getString("errorRetrievingAuthors");
            log.log(Level.SEVERE, message, e.getMessage());
            return Collections.emptyList();
        }
    }

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

    public List<BookAttributeTranslation> getTranslationsForAuthor(int authorId) {
        try {
            return authorDao.getTranslations(authorId);
        } catch (Exception e) {
            String message = rb.getString("errorRetrievingTranslations");
            log.log(Level.SEVERE, message, e.getMessage());
            throw new IllegalArgumentException(message);
        }
    }
}
