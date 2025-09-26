package com.group9.dao;

import com.group9.model.Genre;
import org.junit.jupiter.api.Test;
import java.util.List;


import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class GenreDaoTest {

    GenreDao genreDao = new GenreDao();

    @Test
    void getGenresByBookId() throws SQLException {
        List response = genreDao.getGenresByBookId(13);
        assertNotNull(response);

    }


    @Test
    void addGenre() throws SQLException {
        genreDao.addGenre("Test Genre", "This is a test genre");
    }

    @Test
    void getGenreByNameTest() throws SQLException {
        Genre response = genreDao.getGenreByName("Test Genre");
        assertTrue(response.getName().equals("Test Genre"));
    }


    @Test
    void deleteGenreTest() throws SQLException {
        genreDao.deleteGenreByName("Test Genre");
        assertNull(genreDao.getGenreByName("Test Genre"));
    }
}