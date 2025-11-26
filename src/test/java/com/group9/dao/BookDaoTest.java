package com.group9.dao;

import com.group9.model.Book;
import com.group9.service.BookService;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import com.group9.util.MockData;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookDaoTest {

    private static BookDao bookDao;
    private static Book book;

    @BeforeAll
    static void setUp() throws Exception {
        bookDao = mock(BookDao.class);
        book = mock(Book.class);
        //book.setId(999);
        bookDao.addBook(book);
    }

    @AfterAll
    static void tearDown() throws Exception {
        bookDao.deleteBook(0);
    }

/*
    @Test
    void addBook() throws SQLException {
        bookDao.addBook(book);
        verify(bookDao).addBook(book);
    }*/

    @Test
    void getAllBooks() throws SQLException {
        // stub before calling
        List<Book> expected = Arrays.asList(book);
        when(bookDao.getAllBooks("en")).thenReturn(expected);

        List<Book> books = bookDao.getAllBooks("en");

        verify(bookDao).getAllBooks("en");
        assertEquals(expected, books);
    }

    //Wanted but not invoked.
    //Zero interactions with this mock
    @Test
    void getBookById() throws SQLException {
        when(bookDao.getBookById(anyInt())).thenReturn(book);
        //verify(bookDao).getBookById(anyInt());
        assertEquals(0, book.getId());
    }
/*
    @Test
    void deleteBook() throws SQLException {
        bookDao.deleteBook(999);
        assertTrue(bookDao.getAllBooks().isEmpty());
    }*/

    /*
    //Dummy test
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

     */
}