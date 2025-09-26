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
}
