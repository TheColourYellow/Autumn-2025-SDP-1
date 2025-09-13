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
    void getGenreByNameTest() throws SQLException {
        int genre = genreDao.getGenreByName("Fantasy");
        assertNotNull(genre);
    }

    @Test
    void addGenre() {

    }

    @Test
    void deleteGenre() {
    }
}