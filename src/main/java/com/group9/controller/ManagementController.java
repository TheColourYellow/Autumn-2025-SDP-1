package com.group9.controller;

import com.group9.dao.BookDao;
import com.group9.dao.GenreDao;
import com.group9.model.Author;
import com.group9.model.Book;
import com.group9.model.Genre;
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

//    @FXML private TableView<Book> managementTable;
//    @FXML private TableColumn<Book, String> bookColumn;
//    @FXML private TableColumn<Book, String> authorColumn;
//    @FXML private TableColumn<Book, String> genreColumn;
//
//    @FXML private Button addBookButton;
//    @FXML private Button addAuthorButton;
//    @FXML private Button addGenreButton;

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
            stage.setScene(new Scene(root));
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
        }));
        bookListView.setCellFactory(lv -> new SimpleListCell<>(item -> {
            System.out.println("Book clicked: " + item);
        }));
        authorListView.setCellFactory(lv -> new SimpleListCell<>(item -> {
            System.out.println("Author clicked: " + item);
        }));

        loadData();
    }

    @FXML
    private void addBook () {
        // Implementation for adding a book, addBookButton
    }

    @FXML
    private void addAuthor () {
        // Implementation for adding an author, addAuthorButton
    }

    @FXML
    private void addGenre () {
        // Implementation for adding a genre, addGenreButton
    }

    // Load data from the database
    private void loadData() {
        // Use a background thread to keep UI responsive
        AppExecutors.databaseExecutor.execute(() -> {
            try {
                List<Book> books = bookService.getAllBooks();
                List<Genre> genres = genreService.getAllGenres();
                Platform.runLater(() -> {
                    bookData.setAll(books);
                    genreData.setAll(genres);
                });
            } catch (Exception e) {
                Platform.runLater(() -> showError("Error", "Could not load data. Please try again later."));
            }
        });
    }
}
