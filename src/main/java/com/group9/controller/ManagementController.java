package com.group9.controller;

import com.group9.dao.AuthorDao;
import com.group9.dao.BookDao;
import com.group9.dao.GenreDao;
import com.group9.model.Author;
import com.group9.model.Book;
import com.group9.model.BookAttribute;
import com.group9.model.Genre;
import com.group9.service.AuthorService;
import com.group9.service.BookService;
import com.group9.service.GenreService;
import com.group9.util.AppExecutors;
import com.group9.util.SimpleListCell;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import static com.group9.util.PopupUtils.showError;

public class ManagementController {

    @FXML private Label addGenreBtn;
    @FXML private Label addBookBtn;
    @FXML private Label addAuthorBtn;

    @FXML private ListView<Genre> genreListView;
    @FXML private ListView<Book> bookListView;
    @FXML private ListView<Author> authorListView;

    @FXML private Label loginLabel; // for profile
    @FXML private Label homeLabel;
    @FXML private Label managementLabel;
    @FXML private ImageView shoppingCart;

    private final ObservableList<Genre> genreData = FXCollections.observableArrayList();
    private final ObservableList<Book> bookData = FXCollections.observableArrayList();
    private final ObservableList<Author> authorData = FXCollections.observableArrayList();
    private final GenreService genreService = new GenreService(new GenreDao());
    private final BookService bookService = new BookService(new BookDao());
    private final AuthorService authorService = new AuthorService(new AuthorDao());

    @FXML
    private void openManagementWindow() {
        try {
            // Load the FXML file for the register window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/management_view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) managementLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Register");
            stage.show();
        } catch (Exception e) {
            showError("Error", "Could not open register window.");
        }
    }

    // Method for opening home window
    @FXML
    private void openHomeWindow() {
        try {
            // Load the FXML file for the home window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bookstore_view.fxml"));
            Parent loginRoot = loader.load();

            Stage stage = (Stage) homeLabel.getScene().getWindow();

            stage.setScene(new Scene(loginRoot));

            stage.setTitle("Bookstore Management System");
            stage.show();
        } catch (Exception e) {
            showError("Error", "Could not open home window");
        }
    }

    @FXML
    private void openProfileWindow() {
        try {
            // Load the FXML file for the profile window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/profile_view.fxml"));
            Parent root = loader.load();

            // login label is clicked, content of the window is replaced with the content of profile window
            Stage stage = (Stage) loginLabel.getScene().getWindow();

            // Change the view to the new profile view
            stage.setScene(new Scene(root));

            stage.setTitle("Profile");
            stage.show();
        } catch (Exception e) {
            showError("Error", "Could not open profile window.");
        }
    }

    @FXML
    private void openShoppingCart() {
        System.out.println("Shopping cart clicked!");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/shopping_cart_view.fxml"));
            Parent root = loader.load();

            Stage owner = (Stage) shoppingCart.getScene().getWindow(); // acts as a popup window

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.WINDOW_MODAL); // makes the cart window as modal
            stage.setTitle("Your Cart");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/look.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        genreListView.setItems(genreData);
        bookListView.setItems(bookData);
        authorListView.setItems(authorData);

        genreListView.setCellFactory(lv -> new SimpleListCell<>(item -> {
            System.out.println("Genre clicked: " + item);
            openBookAttributeWindow("Edit Genre", item);
        }));
        bookListView.setCellFactory(lv -> new SimpleListCell<>(item -> {
            System.out.println("Book clicked: " + item);
            openBookManageWindow("Edit Book", item);
        }));
        authorListView.setCellFactory(lv -> new SimpleListCell<>(item -> {
            System.out.println("Author clicked: " + item);
            openBookAttributeWindow("Edit Author", item);
        }));

        loadData();
    }

    @FXML
    private void addBook () {
        openBookManageWindow("Add Book", null);
    }

    @FXML
    private void addAuthor () {
        openBookAttributeWindow("Add Author", null);
    }

    @FXML
    private void addGenre () {
        openBookAttributeWindow("Add Genre", null);
    }

    private void openBookAttributeWindow(String title, BookAttribute item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bookattribute_view.fxml"));
            Parent root = loader.load();

            BookAttributeController attributeController = loader.getController();
            if (item != null) attributeController.setBookAttribute(item.copy());
            else {
                // TODO: do this in a better way
                if (title.contains("Genre")) attributeController.setBookAttribute(new Genre(-1, "", ""));
                else if (title.contains("Author")) attributeController.setBookAttribute(new Author(-1, "", ""));
            }

            // Refresh data when the attribute window is closed
            attributeController.setOnCloseCallback(this::loadData);

            Stage owner = (Stage) shoppingCart.getScene().getWindow(); // acts as a popup window

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openBookManageWindow(String title, Book item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bookmanage_view.fxml"));
            Parent root = loader.load();

            BookManageController bookManageController = loader.getController();
            if (item != null) bookManageController.setBook(item);

            bookManageController.setOnCloseCallback(this::loadData);

            Stage owner = (Stage) shoppingCart.getScene().getWindow(); // acts as a popup window

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load data from the database
    private void loadData() {
        // Use a background thread to keep UI responsive
        AppExecutors.databaseExecutor.execute(() -> {
            try {
                List<Book> books = bookService.getAllBooks();
                List<Genre> genres = genreService.getAllGenres();
                List<Author> authors = authorService.getAllAuthors();
                Platform.runLater(() -> {
                    bookData.setAll(books);
                    genreData.setAll(genres);
                    authorData.setAll(authors);
                });
            } catch (Exception e) {
                Platform.runLater(() -> showError("Error", "Could not load data. Please try again later."));
            }
        });
    }
}
