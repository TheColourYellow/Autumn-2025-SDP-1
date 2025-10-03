package com.group9.service;

import com.group9.dao.GenreDao;
import com.group9.model.Genre;

import java.util.List;

public class GenreService {
  private GenreDao genreDao;

  public GenreService(GenreDao genreDao) {
    this.genreDao = genreDao;
  }

  public List<Genre> getAllGenres() {
    try {
      return genreDao.getAllGenres();
    } catch (Exception e) {
      System.out.println("Error retrieving genres: " + e.getMessage());
      return null;
    }
  }

  public void addGenre(String name, String desc) throws Exception {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("Genre and genre name cannot be null or empty");
    }

    try {
      genreDao.addGenre(name, desc);
    } catch (Exception e) {
      System.out.println("Error adding genre: " + e.getMessage());
      throw new Exception("Error adding genre");
    }
  }

  public void updateGenre(Genre genre) {
    if (genre.getId() <= 0) {
      throw new IllegalArgumentException("Genre ID must be a positive integer");
    }

    if (genre.getName() == null || genre.getName().isEmpty()) {
      throw new IllegalArgumentException("Genre name cannot be null or empty");
    }

    // Check if genre exists
    try {
      Genre existingGenre = genreDao.getGenreByName(genre.getName());
      if (existingGenre == null) {
        throw new IllegalArgumentException("Genre " + genre.getName() + " does not exist");
      }
      if (existingGenre.getId() != genre.getId()) {
        throw new IllegalArgumentException("Another genre with name " + genre.getName() + " already exists");
      }
    } catch (Exception e) {
      System.err.println("Error checking existing genre: " + e.getMessage());
      throw new RuntimeException("Error updating genre");
    }

    try {
      genreDao.updateGenre(genre);
    } catch (Exception e) {
      System.out.println("Error updating genre: " + e.getMessage());
      throw new RuntimeException("Error updating genre");
    }
  }

  public void deleteGenre(String name) throws Exception {
    if (name.isEmpty()) {
      throw new IllegalArgumentException("Genre name cannot be empty");
    }

    try {
      genreDao.deleteGenreByName(name);
    } catch (Exception e) {
      System.out.println("Error deleting genre: " + e.getMessage());
      throw new Exception("Error deleting genre");
    }
  }
}
