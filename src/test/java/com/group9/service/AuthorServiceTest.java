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

  @BeforeEach
  public void setUp() {
    authorDao = mock(AuthorDao.class);
    authorService = new AuthorService(authorDao);
  }

  @Test
  public void testGetAllAuthors() throws SQLException {
    // Mock Dao response with empty list
    when(authorDao.getAllAuthors()).thenReturn(new ArrayList<Author>());
    assertEquals(new ArrayList<Author>(), authorService.getAllAuthors());
    verify(authorDao).getAllAuthors();

    // Return null on exception
    when(authorDao.getAllAuthors()).thenThrow(new SQLException("DB error"));
    assertNull(authorService.getAllAuthors());
  }

  @Test
  public void testAddAuthor() throws Exception {
    // Invalid inputs
    assertThrows(IllegalArgumentException.class, () -> authorService.addAuthor("", "desc"));
    assertThrows(IllegalArgumentException.class, () -> authorService.addAuthor(null, "desc"));

    // Valid input should call Dao method
    authorService.addAuthor("New Author", "desc");
    verify(authorDao).addAuthor("New Author", "desc");

    // Simulate Dao exception
    doThrow(new SQLException("DB error")).when(authorDao).addAuthor("New Author", "desc");
    assertThrows(Exception.class, () -> authorService.addAuthor("New Author", "desc"));
  }

  @Test
  public void testUpdateAuthor() throws SQLException {
    Author existingAuthor = new Author(1, "Author", "desc");

    // Invalid inputs
    assertThrows(IllegalArgumentException.class, () -> authorService.updateAuthor(new Author(-1, "Name", "desc")));
    assertThrows(IllegalArgumentException.class, () -> authorService.updateAuthor(new Author(1, "", "desc")));
    assertThrows(IllegalArgumentException.class, () -> authorService.updateAuthor(new Author(1, null, "desc")));

    // Author does not exist
    when(authorDao.getAuthorByName("NonExistent")).thenReturn(null);
    assertThrows(IllegalArgumentException.class, () -> authorService.updateAuthor(new Author(1, "NonExistent", "desc")));
    verify(authorDao).getAuthorByName("NonExistent");

    // Another author with same name exists
    when(authorDao.getAuthorByName("Existing Author")).thenReturn(new Author(2, "Existing Author", "desc"));
    assertThrows(IllegalArgumentException.class, () -> authorService.updateAuthor(new Author(1, "Existing Author", "desc")));
    verify(authorDao).getAuthorByName("Existing Author");

    // Valid update
    when(authorDao.getAuthorByName("Author")).thenReturn(existingAuthor);
    authorService.updateAuthor(existingAuthor);
    verify(authorDao).getAuthorByName("Author");
    verify(authorDao).updateAuthor(existingAuthor);

    // Simulate Dao exception on update
    when(authorDao.getAuthorByName("Author")).thenReturn(existingAuthor);
    doThrow(new SQLException("DB error")).when(authorDao).updateAuthor(existingAuthor);
    assertThrows(RuntimeException.class, () -> authorService.updateAuthor(existingAuthor));
    verify(authorDao, times(2)).updateAuthor(existingAuthor);

    // Simulate SQLException during existence check
    when(authorDao.getAuthorByName("Author")).thenThrow(new SQLException("DB error"));
    assertThrows(RuntimeException.class, () -> authorService.updateAuthor(existingAuthor));
    verify(authorDao, times(3)).getAuthorByName("Author");
  }

  @Test
  public void testDeleteAuthor() throws Exception {
    // Invalid input
    assertThrows(IllegalArgumentException.class, () -> authorService.deleteAuthor(""));

    // Valid input should call Dao method
    authorService.deleteAuthor("Author Name");
    verify(authorDao).deleteAuthorByName("Author Name");

    // Simulate Dao exception
    doThrow(new SQLException("DB error")).when(authorDao).deleteAuthorByName("Author Name");
    assertThrows(Exception.class, () -> authorService.deleteAuthor("Author Name"));
  }
}
