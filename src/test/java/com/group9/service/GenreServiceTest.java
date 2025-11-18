package com.group9.service;

import com.group9.dao.GenreDao;
import com.group9.model.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GenreServiceTest {
  private GenreDao genreDao;
  private GenreService genreService;

  @BeforeEach
  public void setUp() {
    genreDao = mock(GenreDao.class);
    genreService = new GenreService(genreDao);
  }

  @Test
  public void testGetAllGenres() throws SQLException {
    // Mock Dao response with empty list
    when(genreDao.getAllGenres("en")).thenReturn(new ArrayList<Genre>());
    assertEquals(new ArrayList<Genre>(), genreService.getAllGenres());
    verify(genreDao).getAllGenres("en");

    // Return null on exception
    when(genreDao.getAllGenres("en")).thenThrow(new SQLException("DB error"));
    assertThrows(RuntimeException.class, () -> genreService.getAllGenres());
  }

  @Test
  public void testAddGenre() throws Exception {
    // Invalid inputs
    assertThrows(IllegalArgumentException.class, () -> genreService.addGenre("", "desc"));
    assertThrows(IllegalArgumentException.class, () -> genreService.addGenre(null, "desc"));

    // Valid input should call Dao method
    genreService.addGenre("New Genre", "desc");
    verify(genreDao).addGenre("New Genre", "desc");

    // Simulate Dao exception
    doThrow(new RuntimeException("DB error")).when(genreDao).addGenre("New Genre", "desc");
    assertThrows(Exception.class, () -> genreService.addGenre("New Genre", "desc"));
  }

  @Test
  public void testUpdateGenre() throws SQLException {
    Genre existingGenre = new Genre(1, "Genre", "desc");

    // Invalid inputs
    assertThrows(IllegalArgumentException.class, () -> genreService.updateGenre(new Genre(-1, "Name", "desc")));
    assertThrows(IllegalArgumentException.class, () -> genreService.updateGenre(new Genre(1, "", "desc")));
    assertThrows(IllegalArgumentException.class, () -> genreService.updateGenre(new Genre(1, null, "desc")));

    // Genre does not exist
    when(genreDao.getGenreByName("NonExistent")).thenReturn(null);
    assertThrows(IllegalArgumentException.class, () -> genreService.updateGenre(new Genre(1, "NonExistent", "desc")));
    verify(genreDao).getGenreByName("NonExistent");

    // Another genre with same name exists
    when(genreDao.getGenreByName("Existing Genre")).thenReturn(new Genre(2, "Existing Genre", "desc"));
    assertThrows(IllegalArgumentException.class, () -> genreService.updateGenre(new Genre(1, "Existing Genre", "desc")));
    verify(genreDao).getGenreByName("Existing Genre");

    // Valid update should call Dao method
    when(genreDao.getGenreByName("Genre")).thenReturn(existingGenre);
    genreService.updateGenre(existingGenre);
    verify(genreDao).getGenreByName("Genre");
    verify(genreDao).updateGenre(existingGenre);

    // Simulate Dao exception on update
    when(genreDao.getGenreByName("Genre")).thenReturn(existingGenre);
    doThrow(new RuntimeException("DB error")).when(genreDao).updateGenre(existingGenre);
    assertThrows(RuntimeException.class, () -> genreService.updateGenre(existingGenre));
    verify(genreDao, times(2)).updateGenre(existingGenre);

    // Simulate Dao exception on get
    when(genreDao.getGenreByName("Genre")).thenThrow(new SQLException("DB error"));
    assertThrows(RuntimeException.class, () -> genreService.updateGenre(new Genre(1, "Genre", "desc")));
    verify(genreDao, times(3)).getGenreByName("Genre");
  }

  @Test
  public void testDeleteGenre() throws Exception {
    // Invalid input
    assertThrows(IllegalArgumentException.class, () -> genreService.deleteGenre(""));

    // Valid input should call Dao method
    genreService.deleteGenre("Genre");
    verify(genreDao).deleteGenreByName("Genre");

    // Simulate Dao exception
    doThrow(new SQLException("DB error")).when(genreDao).deleteGenreByName("Genre");
    assertThrows(Exception.class, () -> genreService.deleteGenre("Genre"));
  }
}
