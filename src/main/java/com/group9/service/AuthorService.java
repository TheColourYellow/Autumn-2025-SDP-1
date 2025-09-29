package com.group9.service;

import com.group9.dao.AuthorDao;
import com.group9.model.Author;

import java.sql.SQLException;
import java.util.List;

public class AuthorService {
  private AuthorDao authorDao;

  public AuthorService(AuthorDao authorDao) {
    this.authorDao = authorDao;
  }

  public List<Author> getAllAuthors() {
    try {
      return authorDao.getAllAuthors();
    } catch (Exception e) {
      System.out.println("Error retrieving authors: " + e.getMessage());
      return null;
    }
  }

  public void addAuthor(String name, String desc) throws Exception {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("Author and author name cannot be null or empty");
    }

    try {
      authorDao.addAuthor(name, desc);
    } catch (Exception e) {
      System.out.println("Error adding author: " + e.getMessage());
      throw new Exception("Error adding author");
    }
  }

  public void deleteAuthor(String name) throws Exception {
    if (name.isEmpty()) {
      throw new IllegalArgumentException("Author name cannot be empty");
    }

    try {
      authorDao.deleteAuthorByName(name);
    } catch (SQLException e) {
      System.out.println("Error deleting author: " + e.getMessage());
      throw new Exception("Error deleting author");
    }
  }
}
