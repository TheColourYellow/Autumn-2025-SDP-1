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
  private static final String DB_ERROR = "DB error";
  private static final String GENRE = "Genre";
  private static final String NEW_GENRE = "New Genre";
  private static final String NON_EXISTENT = "NonExistent";
  private static final String EXISTING_GENRE = "Existing Genre";
  private static final String DESC = "desc";
  private static final String EN = "en";

  @BeforeEach
  public void setUp() {
    genreDao = mock(GenreDao.class);
    genreService = new GenreService(genreDao);
  }

  @Test
  public void testGetAllGenres() throws SQLException {
    // Mock Dao response with empty list
    when(genreDao.getAllGenres(EN)).thenReturn(new ArrayList<Genre>());
    assertEquals(new ArrayList<Genre>(), genreService.getAllGenres());
    verify(genreDao).getAllGenres(EN);

    // Return null on exception
    when(genreDao.getAllGenres(EN)).thenThrow(new SQLException(DB_ERROR));
    assertThrows(RuntimeException.class, () -> genreService.getAllGenres());
  }

  @Test
  public void testAddGenre() throws Exception {
    // Invalid inputs
    assertThrows(IllegalArgumentException.class, () -> genreService.addGenre("", DESC));
    assertThrows(IllegalArgumentException.class, () -> genreService.addGenre(null, DESC));

    // Valid input should call Dao method
    genreService.addGenre(NEW_GENRE, DESC);
    verify(genreDao).addGenre(NEW_GENRE, DESC);

    // Simulate Dao exception
    doThrow(new RuntimeException(DB_ERROR)).when(genreDao).addGenre(NEW_GENRE, DESC);
    assertThrows(Exception.class, () -> genreService.addGenre(NEW_GENRE, DESC));
  }

  @Test
  public void testUpdateGenre() throws SQLException {
    Genre existingGenre = new Genre(1, GENRE, DESC);

    // Invalid inputs
    assertThrows(IllegalArgumentException.class, () -> genreService.updateGenre(new Genre(-1, "Name", DESC)));
    assertThrows(IllegalArgumentException.class, () -> genreService.updateGenre(new Genre(1, "", DESC)));
    assertThrows(IllegalArgumentException.class, () -> genreService.updateGenre(new Genre(1, null, DESC)));

    // Genre does not exist
    when(genreDao.getGenreByName(NON_EXISTENT)).thenReturn(null);
    assertThrows(IllegalArgumentException.class, () -> genreService.updateGenre(new Genre(1, NON_EXISTENT, DESC)));
    verify(genreDao).getGenreByName(NON_EXISTENT);

    // Another genre with same name exists
    when(genreDao.getGenreByName(EXISTING_GENRE)).thenReturn(new Genre(2, EXISTING_GENRE, DESC));
    assertThrows(IllegalArgumentException.class, () -> genreService.updateGenre(new Genre(1, EXISTING_GENRE, DESC)));
    verify(genreDao).getGenreByName(EXISTING_GENRE);

    // Valid update should call Dao method
    when(genreDao.getGenreByName(GENRE)).thenReturn(existingGenre);
    genreService.updateGenre(existingGenre);
    verify(genreDao).getGenreByName(GENRE);
    verify(genreDao).updateGenre(existingGenre);

    // Simulate Dao exception on update
    when(genreDao.getGenreByName(GENRE)).thenReturn(existingGenre);
    doThrow(new RuntimeException(DB_ERROR)).when(genreDao).updateGenre(existingGenre);
    assertThrows(RuntimeException.class, () -> genreService.updateGenre(existingGenre));
    verify(genreDao, times(2)).updateGenre(existingGenre);

    // Simulate Dao exception on get
    when(genreDao.getGenreByName(GENRE)).thenThrow(new SQLException(DB_ERROR));
    assertThrows(RuntimeException.class, () -> genreService.updateGenre(new Genre(1, GENRE, DESC)));
    verify(genreDao, times(3)).getGenreByName(GENRE);
  }

  @Test
  public void testDeleteGenre() throws Exception {
    // Invalid input
    assertThrows(IllegalArgumentException.class, () -> genreService.deleteGenre(""));

    // Valid input should call Dao method
    genreService.deleteGenre(GENRE);
    verify(genreDao).deleteGenreByName(GENRE);

    // Simulate Dao exception
    doThrow(new SQLException(DB_ERROR)).when(genreDao).deleteGenreByName(GENRE);
    assertThrows(Exception.class, () -> genreService.deleteGenre(GENRE));
  }

  @Test
  public void testSaveGenreWithTranslations() throws Exception {
    Genre genre = new Genre(0, NEW_GENRE, DESC);

    // Mock Dao response for adding genre
    when(genreDao.addGenre(NEW_GENRE, DESC))
            .thenAnswer(invocation -> 1);
    // Valid save (add) without translations
    genreService.saveGenreWithTranslations(genre, new ArrayList<>());
    verify(genreDao).addGenre(NEW_GENRE, DESC);
    verify(genreDao).upsertTranslations(1, new ArrayList<>());

    // Update existing genre
    Genre existingGenre = new Genre(2, GENRE, DESC);
    when(genreDao.getGenreByName(GENRE)).thenReturn(existingGenre);
    genreService.saveGenreWithTranslations(existingGenre, new ArrayList<>());
    verify(genreDao).updateGenre(existingGenre);
    verify(genreDao).upsertTranslations(2, new ArrayList<>());

    // Simulate Dao exception on add
    doThrow(new RuntimeException(DB_ERROR)).when(genreDao).addGenre(NEW_GENRE, DESC);
    assertThrows(IllegalArgumentException.class, () -> genreService.saveGenreWithTranslations(genre, new ArrayList<>()));

    // Simulate Dao exception on update
    doThrow(new RuntimeException(DB_ERROR)).when(genreDao).updateGenre(existingGenre);
    assertThrows(IllegalArgumentException.class, () -> genreService.saveGenreWithTranslations(existingGenre, new ArrayList<>()));

    // Simulate Dao exception on upsert translations
    Genre genreForUpsertError = new Genre(3, NEW_GENRE, DESC);
    when(genreDao.getGenreByName(NEW_GENRE)).thenReturn(genreForUpsertError);
    doThrow(new RuntimeException(DB_ERROR)).when(genreDao).upsertTranslations(3, new ArrayList<>());
    assertThrows(IllegalArgumentException.class, () -> genreService.saveGenreWithTranslations(genreForUpsertError, new ArrayList<>()));
  }

  @Test
  public void testGetTranslationsForGenre() throws SQLException {
    int genreId = 1;
    // Mock Dao response
    when(genreDao.getTranslations(genreId))
            .thenReturn(new ArrayList<>());

    // Valid translation retrieval
    assertEquals(new ArrayList<>(), genreService.getTranslationsForGenre(genreId));
    verify(genreDao).getTranslations(genreId);

    // Simulate DB error on retrieval
    when(genreDao.getTranslations(2))
            .thenThrow(new RuntimeException(DB_ERROR));
    assertThrows(RuntimeException.class, () -> genreService.getTranslationsForGenre(2));
  }
}
