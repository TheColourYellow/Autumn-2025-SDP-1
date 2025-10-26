package com.group9.controller;

import com.group9.model.Author;
import com.group9.model.BookAttribute;
import com.group9.model.Genre;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;


import static org.junit.jupiter.api.Assertions.*;

class BookAttributeControllerTest {
    private static BookAttributeController controller;
    private static BookAttribute author;
    private static BookAttribute genre;
    private static BookAttribute failedAuthor;
    private static Button deleteBtn;
    //private static TextField nameTextField;
    //private static TextField descTextField;
    //@FXML private TextField nameTextField;
    @FXML private TextField descTextField;

    @BeforeAll
    static void setup() {
        controller = mock(BookAttributeController.class);
        author = new Author(1, "Author", "Author");
        genre = mock(BookAttribute.class);
        //author = new Author(1, "Author", "Author");
        //genre = new Genre(2, "Genre", "Genre");
        failedAuthor = new Author(-1, "FailedAuthor", "FailedAuthor");

    }

    @Test
    void setBookAttribute() {
        controller.setBookAttribute(author);
        verify(controller).setBookAttribute(author);
        when(author.getName()).thenReturn("Author");
        when(author.getDescription()).thenReturn("Author");
        when(author.getId()).thenReturn(1);
        assertTrue(author.getId() != -1);
        assertEquals("Author", author.getName());
        assertEquals("Author", author.getDescription());
        /*
        controller.setBookAttribute(genre);
        assertTrue(genre.getId() != -1);
        assertEquals("Genre", genre.getName());
        assertEquals("Genre", genre.getDescription());

         */


    }

    @Test
    void setOnCloseCallback() {
    }
}