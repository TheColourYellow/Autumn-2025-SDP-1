package com.group9.service;

import com.group9.dao.AuthorDao;
import com.group9.model.Author;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthorServiceTest {
  private AuthorDao authorDao;
  private AuthorService authorService;
  private static final String DB_ERROR = "DB error";
  private static final String NEW_AUTHOR = "New Author";
  private static final String EXISTING_AUTHOR = "Existing Author";
  private static final String NON_EXISTENT = "NonExistent";
  private static final String AUTHOR = "Author";
  private static final String AUTHOR_NAME = "Author Name";
  private static final String DESC = "desc";
  private static final String EN = "en";

  @BeforeEach
  public void setUp() {
    authorDao = mock(AuthorDao.class);
    authorService = new AuthorService(authorDao);
  }

  @Test
  public void testGetAllAuthors() throws SQLException {
    // Mock Dao response with empty list
    when(authorDao.getAllAuthors(EN)).thenReturn(new ArrayList<>());
    assertEquals(new ArrayList<Author>(), authorService.getAllAuthors());
    verify(authorDao).getAllAuthors(EN);

    // Return null on exception
    when(authorDao.getAllAuthors(EN)).thenThrow(new SQLException(DB_ERROR));
    assertThrows(RuntimeException.class, () -> authorService.getAllAuthors());
  }

  @Test
  public void testAddAuthor() {
    // Invalid inputs
    assertThrows(IllegalArgumentException.class, () -> authorService.addAuthor("", DESC));
    assertThrows(IllegalArgumentException.class, () -> authorService.addAuthor(null, DESC));

    // Valid input should call Dao method
    authorService.addAuthor(NEW_AUTHOR, DESC);
    verify(authorDao).addAuthor(NEW_AUTHOR, DESC);

    // Simulate Dao exception
    doThrow(new RuntimeException(DB_ERROR)).when(authorDao).addAuthor(NEW_AUTHOR, DESC);
    assertThrows(Exception.class, () -> authorService.addAuthor(NEW_AUTHOR, DESC));
  }

  @Test
  public void testUpdateAuthor() throws SQLException {
    Author existingAuthor = new Author(1, AUTHOR, DESC);

    // Invalid inputs
    assertThrows(IllegalArgumentException.class, () -> authorService.updateAuthor(new Author(-1, "Name", DESC)));
    assertThrows(IllegalArgumentException.class, () -> authorService.updateAuthor(new Author(1, "", DESC)));
    assertThrows(IllegalArgumentException.class, () -> authorService.updateAuthor(new Author(1, null, DESC)));

    // Author does not exist
    when(authorDao.getAuthorByName(NON_EXISTENT)).thenReturn(null);
    assertThrows(IllegalArgumentException.class, () -> authorService.updateAuthor(new Author(1, NON_EXISTENT, DESC)));
    verify(authorDao).getAuthorByName(NON_EXISTENT);

    // Another author with same name exists
    when(authorDao.getAuthorByName(EXISTING_AUTHOR)).thenReturn(new Author(2, EXISTING_AUTHOR, DESC));
    assertThrows(IllegalArgumentException.class, () -> authorService.updateAuthor(new Author(1, EXISTING_AUTHOR, DESC)));
    verify(authorDao).getAuthorByName(EXISTING_AUTHOR);

    // Valid update
    when(authorDao.getAuthorByName(AUTHOR)).thenReturn(existingAuthor);
    authorService.updateAuthor(existingAuthor);
    verify(authorDao).getAuthorByName(AUTHOR);
    verify(authorDao).updateAuthor(existingAuthor);

    // Simulate Dao exception on update
    when(authorDao.getAuthorByName(AUTHOR)).thenReturn(existingAuthor);
    doThrow(new RuntimeException(DB_ERROR)).when(authorDao).updateAuthor(existingAuthor);
    assertThrows(RuntimeException.class, () -> authorService.updateAuthor(existingAuthor));
    verify(authorDao, times(2)).updateAuthor(existingAuthor);

    // Simulate SQLException during existence check
    when(authorDao.getAuthorByName(AUTHOR)).thenThrow(new SQLException(DB_ERROR));
    assertThrows(RuntimeException.class, () -> authorService.updateAuthor(existingAuthor));
    verify(authorDao, times(3)).getAuthorByName(AUTHOR);
  }

  @Test
  public void testDeleteAuthor() throws Exception {
    // Invalid input
    assertThrows(IllegalArgumentException.class, () -> authorService.deleteAuthor(""));

    // Valid input should call Dao method
    authorService.deleteAuthor(AUTHOR_NAME);
    verify(authorDao).deleteAuthorByName(AUTHOR_NAME);

    // Simulate Dao exception
    doThrow(new SQLException(DB_ERROR)).when(authorDao).deleteAuthorByName(AUTHOR_NAME);
    assertThrows(Exception.class, () -> authorService.deleteAuthor(AUTHOR_NAME));
  }

  @Test
  public void testSaveAuthorWithTranslations() throws SQLException {
    Author author = new Author(0, NEW_AUTHOR, DESC);

    // Mock Dao response for adding author
    when(authorDao.addAuthor(NEW_AUTHOR, DESC))
            .thenAnswer(invocation -> 1);
    // Valid save (add) without translations
    authorService.saveAuthorWithTranslations(author, new ArrayList<>());
    verify(authorDao).addAuthor(NEW_AUTHOR, DESC);
    verify(authorDao).upsertTranslations(1, new ArrayList<>());

    // Update existing author
    Author existingAuthor = new Author(2, AUTHOR, DESC);
    when(authorDao.getAuthorByName(AUTHOR)).thenReturn(existingAuthor);
    authorService.saveAuthorWithTranslations(existingAuthor, new ArrayList<>());
    verify(authorDao).updateAuthor(existingAuthor);
    verify(authorDao).upsertTranslations(2, new ArrayList<>());

    // Simulate Dao exception on add
    doThrow(new RuntimeException(DB_ERROR)).when(authorDao).addAuthor(NEW_AUTHOR, DESC);
    assertThrows(IllegalArgumentException.class, () -> authorService.saveAuthorWithTranslations(author, new ArrayList<>()));

    // Simulate Dao exception on update
    doThrow(new RuntimeException(DB_ERROR)).when(authorDao).updateAuthor(existingAuthor);
    assertThrows(IllegalArgumentException.class, () -> authorService.saveAuthorWithTranslations(existingAuthor, new ArrayList<>()));

    // Simulate Dao exception on upsert translations
    Author authorForUpsertError = new Author(3, NEW_AUTHOR, DESC);
    when(authorDao.getAuthorByName(NEW_AUTHOR)).thenReturn(authorForUpsertError);
    doThrow(new RuntimeException(DB_ERROR)).when(authorDao).upsertTranslations(3, new ArrayList<>());
    assertThrows(IllegalArgumentException.class, () -> authorService.saveAuthorWithTranslations(authorForUpsertError, new ArrayList<>()));
  }

  @Test
  public void testGetTranslationsForAuthor() {
    int authorId = 1;
    // Mock Dao response
    when(authorDao.getTranslations(authorId))
            .thenReturn(new ArrayList<>());

    // Valid translation retrieval
    assertEquals(new ArrayList<>(), authorService.getTranslationsForAuthor(authorId));
    verify(authorDao).getTranslations(authorId);

    // Simulate DB error on retrieval
    when(authorDao.getTranslations(2))
            .thenThrow(new RuntimeException(DB_ERROR));
    assertThrows(RuntimeException.class, () -> authorService.getTranslationsForAuthor(2));
  }
}
