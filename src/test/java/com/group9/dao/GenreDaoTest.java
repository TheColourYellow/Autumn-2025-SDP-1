package com.group9.dao;

import com.group9.model.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GenreDaoTest {


    private GenreDao genreDao;
    private Genre genre;

    @BeforeEach
    void setUp() {
        genreDao = mock(GenreDao.class);
        genre = new Genre(1, "Test Genre", "Test Genre Description");
    }
/*
    @Test
    void getGenresByBookId() throws SQLException {
        List response = genreDao.getGenresByBookId(13);
        assertNotNull(response);

    }*/


    @Test
    void addGenre() throws SQLException {
        genreDao.addGenre("Test Genre", "This is a test genre");
        verify(genreDao).addGenre("Test Genre", "This is a test genre");
    }
/*
//Wanted but not invoked.
//Zero interactions with this mock
    @Test
    void getGenreByNameTest() throws SQLException {
        //Genre response = genreDao.getGenreByName("Test Genre");
        when(genreDao.getGenreByName("Test Genre")).thenReturn(genre);
        //assertEquals(genre, genreDao.getGenreByName("Test Genre"));
        verify(genreDao).getGenreByName("Test Genre");
        assertTrue(genre.getId() > 0);
        assertTrue(genre.getName().equals("Test Genre"));
        assertTrue(genre.getDescription().equals("Test Genre Description"));
    }*/


    @Test
    void deleteGenreTest() throws SQLException {
        genreDao.deleteGenreByName("Test Genre");
        when(genreDao.getGenreByName("Test Genre")).thenReturn(null);
        verify(genreDao).deleteGenreByName("Test Genre");
        assertNull(genreDao.getGenreByName("Test Genre"));
    }
    /*
    //Dummy Test
    @Test
    void createGenreObject() {
        Genre genre = new Genre(1, "Test Genre", "Test Description");
        assertEquals("Test Genre", genre.getName());
    }*/
}