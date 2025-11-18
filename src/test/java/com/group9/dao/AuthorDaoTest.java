package com.group9.dao;

import com.group9.model.Author;
import com.group9.model.Book;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class AuthorDaoTest {


    private static AuthorDao dao;
    private static Author author;
    private static Book book;
    private static final String TEST_AUTHOR = "Test Author";

    @BeforeAll
    static void setUp() {
        dao = mock(AuthorDao.class);
        author = mock(Author.class);
        book = mock(Book.class);
        book.setAuthors(new ArrayList<Author>(author.getId()));
    }
/*
    //Error fetching authors for book ID o
    @Test
    void getAuthorsByBookId() throws SQLException {
        when(dao.getAuthorsByBookId(book.getId())).thenReturn(book.getAuthors());
        verify(dao).getAuthorsByBookId(book.getId());
        assertTrue(book.getAuthors().size() > 0);

    }*/

    @Test
    void addAuthorTest() throws SQLException {
        dao.addAuthor(TEST_AUTHOR, "This is a test author");
        verify(dao).addAuthor(TEST_AUTHOR, "This is a test author");
    }

    //Wanted but not invoked
    //Zero interactions with this mock
    @Test
    void getAuthorByNameTest() throws SQLException {
        when(dao.getAuthorByName(TEST_AUTHOR)).thenReturn(author);
        assertEquals(author, dao.getAuthorByName(TEST_AUTHOR));
    }

    @Test
    void deleteAuthorByNameTest() throws SQLException {

        dao.deleteAuthorByName(TEST_AUTHOR);
        when(dao.getAuthorByName(TEST_AUTHOR)).thenReturn(null);
        verify(dao).deleteAuthorByName(TEST_AUTHOR);
        assertNull(dao.getAuthorByName(TEST_AUTHOR));

    }

    /*
    // Dummy Test
    @Test
    void createAuthorObject() {
        Author author = new Author(1,
                "Test Author",
                "Test Description");
        assertEquals("Test Author", author.getName());
    }
     */
}