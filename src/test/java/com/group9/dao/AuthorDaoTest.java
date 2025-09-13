package com.group9.dao;

import com.group9.model.Author;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuthorDaoTest {

    AuthorDao dao = new AuthorDao();

    @Test
    void getAuthorsByBookId() throws SQLException {
        List response = AuthorDao.getAuthorsByBookId(13);
        assertTrue(!response.isEmpty());

    }

    @Test
    void getAuthorByNameTest() throws SQLException {
        dao.getAuthorByName("J.R.R. Tolkien");
    }
}