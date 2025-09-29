package com.group9.dao;

import com.group9.model.Book;
import com.group9.service.BookService;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;
import com.group9.util.MockData;

import static org.junit.jupiter.api.Assertions.*;

class BookDaoTest {
    /*
    BookDao bookDao = new BookDao();

    @BeforeAll
    static void setUp() throws Exception {
        BookDao bookDao = new BookDao();
        Book book = new Book(1, "Test", "1111", 1500, 24.99, "Test" );
        bookDao.addBook(book);
    }

    @AfterAll
    static void tearDown() throws Exception {
        BookDao bookDao = new BookDao();
        bookDao.deleteBook(6);
    }

    @Test
    void addBook() throws SQLException {
        Book book = new Book(1, "Test", "1111", 1500, 24.99, "Test" );
        bookDao.addBook(book);
    }

    @Test
    void getAllBooks() {
        List<Book> books = bookDao.getAllBooks();
        assertTrue(!books.isEmpty());
    }

    @Test
    void getBookById() throws SQLException {
        Book book = bookDao.getBookById(13);
        assertNotNull(book);
    }

    @Test
    void deleteBook() throws SQLException {
        bookDao.deleteBook(13);
        assertTrue(bookDao.getAllBooks().isEmpty());
    }
    */
    @Test
    void createBookObject() {
        Book book = new Book(1,
                "Test",
                "Test-ISBN",
                2000,
                19.99,
                "Test Description");
        assertEquals("Test", book.getTitle());
    }
}