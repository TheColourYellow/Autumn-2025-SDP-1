package com.group9.service;

import com.group9.dao.BookDao;
import com.group9.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookServiceTest {
  private BookDao bookDao;
  private BookService bookService;
  private static final String TEST_BOOK = "Test Book";
  private static final String BOOK_FOR_TESTING = "A book for testing";
  private static final String DB_ERROR = "DB error";
  private static final String ERROR_ISBN = "¨1234-5678-9012";
  private static final String ISBN = "1234-5678-9012";
  private static final String ANOTHER_BOOK_FOR_TESTING = "Another book for testing";

  @BeforeEach

  public void setUp() {
    bookDao = mock(BookDao.class);
    bookService = new BookService(bookDao);
  }

  // removed testGetAllBooks, as getAllBooks is no longer used in BookService ?
  @Test
  public void testGetAllBooks() throws SQLException {
    // Mock Dao response
    when(bookDao.getAllBooks("en")).thenReturn(Collections.emptyList());

    // BookService should call the Dao method and return the same empty list
    assertEquals(Collections.emptyList(), bookService.getAllBooks());
    verify(bookDao).getAllBooks("en");

    // Simulate DB error
    when(bookDao.getAllBooks("en"))
            .thenThrow(new SQLException(DB_ERROR));
    assertThrows(RuntimeException.class, () -> bookService.getAllBooks());
  }

  @Test
  public void testGetBookById() throws SQLException {
    int bookId = 1;
    Book testBook = new Book(
            bookId,
            TEST_BOOK,
            ERROR_ISBN,
            2025,
            10.00,
            BOOK_FOR_TESTING
    );

    // Mock Dao response
    when(bookDao.getBookById(bookId))
            .thenAnswer(invocation -> testBook);

    // Valid book retrieval
    assertEquals(bookService.getBookById(bookId), testBook);
    verify(bookDao).getBookById(bookId);

    // Invalid book retrieval with invalid ID
    int invalidBookId = -1;
    assertThrows(IllegalArgumentException.class, () -> bookService.getBookById(invalidBookId));

    // Simulate DB error on retrieval
    when(bookDao.getBookById(2))
            .thenThrow(new SQLException(DB_ERROR));
    assertNull(bookService.getBookById(2));
  }

  @Test
  public void testSearchBooks() {
    // Various combinations of null and non-null parameters
    bookService.searchBooks(null, null);
    verify(bookDao).findBooks(null, null);

    bookService.searchBooks(java.util.Arrays.asList(1, 2), null);
    verify(bookDao).findBooks(java.util.Arrays.asList(1, 2), null);

    bookService.searchBooks(null, java.util.Arrays.asList(3, 4));
    verify(bookDao).findBooks(null, java.util.Arrays.asList(3, 4));

    bookService.searchBooks(Collections.singletonList(1), Collections.singletonList(3));
    verify(bookDao).findBooks(Collections.singletonList(1), Collections.singletonList(3));
  }

  @Test
  public void testAddBook() throws SQLException {
    Book testBook = new Book(
            1,
            TEST_BOOK,
            ERROR_ISBN,
            2025,
            10.00,
            BOOK_FOR_TESTING
    );

    // Mock Dao response
    when(bookDao.addFullBook(any(Book.class)))
            .thenAnswer(invocation -> 1);

    // Valid book addition
    assertEquals(1, bookService.addBook(testBook));
    verify(bookDao).addFullBook(testBook);

    Book testBookInvalidTitle = new Book(
            2,
            null,
            ISBN,
            2025,
            10.00,
            ANOTHER_BOOK_FOR_TESTING
    );
    Book testBookInvalidPrice = new Book(
            3,
            "Invalid Price Book",
            ISBN,
            2025,
            -5.00,
            "Book with invalid price"
    );
    Book testBookInvalidYear = new Book(
            4,
            "Invalid Year Book",
            ISBN,
            -2025,
            10.00,
            "Book with invalid year"
    );

    // Tests for validateBook through addBook
    assertThrows(IllegalArgumentException.class, () -> bookService.addBook(null));
    assertThrows(IllegalArgumentException.class, () -> bookService.addBook(testBookInvalidTitle));
    assertThrows(IllegalArgumentException.class, () -> bookService.addBook(testBookInvalidPrice));
    assertThrows(IllegalArgumentException.class, () -> bookService.addBook(testBookInvalidYear));

    // Simulate DB error on add
    when(bookDao.addFullBook(any(Book.class)))
            .thenThrow(new SQLException(DB_ERROR));
    assertEquals(-1, bookService.addBook(testBook));
  }

  @Test
  public void testUpdateBook() {
    Book testBook = new Book(
            1,
            TEST_BOOK,
            ISBN,
            2025,
            10.00,
            BOOK_FOR_TESTING
    );

    // Valid book update
    bookService.updateBook(testBook);
    verify(bookDao).updateBook(testBook);

    Book testBookInvalidId = new Book(
            -1,
            "Invalid ID Book",
            ISBN,
            2025,
            10.00,
            "Book with invalid ID"
    );
    Book testBookInvalidTitle = new Book(
            2,
            null,
            ISBN,
            2025,
            10.00,
            ANOTHER_BOOK_FOR_TESTING
    );
    Book testBookInvalidTitle2 = new Book(
            2,
            "",
            ISBN,
            2025,
            10.00,
            ANOTHER_BOOK_FOR_TESTING
    );
    Book testBookInvalidPrice = new Book(
            3,
            "Invalid Price Book",
            ISBN,
            2025,
            -5.00,
            "Book with invalid price"
    );
    Book testBookInvalidYear = new Book(
            4,
            "Invalid Year Book",
            ISBN,
            -2025,
            10.00,
            "Book with invalid year"
    );

    // Tests for validateBook through updateBook
    assertThrows(IllegalArgumentException.class, () -> bookService.updateBook(testBookInvalidId));
    assertThrows(IllegalArgumentException.class, () -> bookService.updateBook(null));
    assertThrows(IllegalArgumentException.class, () -> bookService.updateBook(testBookInvalidTitle));
    assertThrows(IllegalArgumentException.class, () -> bookService.updateBook(testBookInvalidTitle2));
    assertThrows(IllegalArgumentException.class, () -> bookService.updateBook(testBookInvalidPrice));
    assertThrows(IllegalArgumentException.class, () -> bookService.updateBook(testBookInvalidYear));

    // Simulate DB error on update
    doThrow(new RuntimeException(DB_ERROR)).when(bookDao).updateBook(any(Book.class));
    assertThrows(Exception.class, () -> bookService.updateBook(testBook));
  }

  @Test
  public void testDeleteBook() throws Exception {
    int bookId = 1;

    // Valid deletion
    bookService.deleteBook(bookId);
    verify(bookDao).inActivateBook(bookId);

    // Invalid deletion with invalid ID
    int invalidBookId = -1;
    assertThrows(IllegalArgumentException.class, () -> bookService.deleteBook(invalidBookId));

    // Simulate DB error on delete
    doThrow(new SQLException(DB_ERROR)).when(bookDao).inActivateBook(2);
    assertThrows(Exception.class, () -> bookService.deleteBook(2));
  }

  @Test
  public void testGetTranslations() {
    int bookId = 1;
    // Mock Dao response
    when(bookDao.getTranslations(bookId))
            .thenReturn(Collections.emptyList());

    // Valid translation retrieval
    assertEquals(Collections.emptyList(), bookService.getTranslations(bookId));
    verify(bookDao).getTranslations(bookId);

    // Simulate DB error on retrieval
    when(bookDao.getTranslations(2))
            .thenThrow(new RuntimeException(DB_ERROR));
    assertThrows(RuntimeException.class, () -> bookService.getTranslations(2));
  }

  @Test
  public void testSaveBookWithTranslations() throws SQLException {
    Book testBook = new Book(
            -1,
            TEST_BOOK,
            ISBN,
            2025,
            10.00,
            BOOK_FOR_TESTING
    );

    // Mock Dao response for adding book
    when(bookDao.addFullBook(any(Book.class)))
            .thenAnswer(invocation -> 1);
    // Valid save (add) without translations
    bookService.saveBookWithTranslations(testBook, Collections.emptyList());
    verify(bookDao).addFullBook(testBook);

    // Update existing book
    testBook.setId(2);
    // Valid save with translations
    bookService.saveBookWithTranslations(testBook, Collections.emptyList());
    verify(bookDao).upsertTranslations(testBook.getId(), Collections.emptyList());

    // Simulate DB error on upsert translations
    doThrow(new RuntimeException(DB_ERROR)).when(bookDao).upsertTranslations(anyInt(), anyList());
    assertThrows(Exception.class, () -> bookService.saveBookWithTranslations(testBook, Collections.emptyList()));
  }
}
