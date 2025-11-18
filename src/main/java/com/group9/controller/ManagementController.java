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
import com.group9.util.LayoutOrienter;
import com.group9.util.SessionManager;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static com.group9.util.PopupUtils.showError;

public class ManagementController {

    private LayoutOrienter orienter = new LayoutOrienter();
    private static final Logger log = Logger.getLogger(ShoppingCartController.class.getName());
    private static final String ERROR = "error";

    @FXML private AnchorPane managementAnchor;

    @FXML private Label addGenreBtn;
    @FXML private Label addBookBtn;
    @FXML private Label addAuthorBtn;

    @FXML private ListView<Genre> genreListView;
    @FXML private ListView<Book> bookListView;
    @FXML private ListView<Author> authorListView;

    @FXML private Label bookStoreLabel;
    @FXML private Label loginLabel; // for profile
    @FXML private Label homeLabel;
    @FXML private Label managementLabel;
    @FXML private ImageView shoppingCart;

    @FXML private Label addGenreLabel;
    @FXML private Label addBookLabel;
    @FXML private Label addAuthorLabel;

    private final ObservableList<Genre> genreData = FXCollections.observableArrayList();
    private final ObservableList<Book> bookData = FXCollections.observableArrayList();
    private final ObservableList<Author> authorData = FXCollections.observableArrayList();
    private final GenreService genreService = new GenreService(new GenreDao());
    private final BookService bookService = new BookService(new BookDao());
    private final AuthorService authorService = new AuthorService(new AuthorDao());

    private ResourceBundle rb;

    @FXML
    private void openManagementWindow() {
        try {
            // Load the FXML file for the register window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/management_view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) managementLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(rb.getString("registerText"));
            stage.show();
        } catch (Exception e) {
            showError(rb.getString(ERROR), rb.getString("couldNotOpenRegister"));
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
            showError(rb.getString(ERROR), rb.getString("couldNotOpenHome"));
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

            stage.setTitle(rb.getString("profileLabel"));
            stage.show();
        } catch (Exception e) {
            showError(rb.getString(ERROR), rb.getString("couldNotOpenProfile"));
        }
    }

    @FXML
    private void openShoppingCart() {
        log.info("Shopping cart clicked!");
        rb = SessionManager.getResourceBundle();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/shopping_cart_view.fxml"));
            Parent root = loader.load();

            Stage owner = (Stage) shoppingCart.getScene().getWindow(); // acts as a popup window

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.WINDOW_MODAL); // makes the cart window as modal
            stage.setTitle(rb.getString("yourCartLabel"));
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
        rb = SessionManager.getResourceBundle();
        orienter.orientLayout(managementAnchor);
        updateUI();

        genreListView.setItems(genreData);
        bookListView.setItems(bookData);
        authorListView.setItems(authorData);

        genreListView.setCellFactory(lv -> new SimpleListCell<>(item -> {
            log.log(Level.INFO, "Genre clicked: {0}", new Object[]{item});
            openBookAttributeWindow(rb.getString("editGenreTitle"), item, true);
        }));
        bookListView.setCellFactory(lv -> new SimpleListCell<>(item -> {
            log.log(Level.INFO,"Book clicked: {0}", new Object[]{item});
            openBookManageWindow(rb.getString("editBookTitle"), item);
        }));
        authorListView.setCellFactory(lv -> new SimpleListCell<>(item -> {
            log.log(Level.INFO, "Author clicked: {0}", new Object[]{item});
            openBookAttributeWindow(rb.getString("editAuthorTitle"), item, false);
        }));

        loadData();
    }

    private void updateUI() {
        bookStoreLabel.setText(rb.getString("bookStoreLabel"));
        homeLabel.setText(rb.getString("homeLabel"));
        managementLabel.setText(rb.getString("managementLabel"));
        loginLabel.setText(rb.getString("profileLabel"));

        addGenreLabel.setText(rb.getString("genreColumn"));
        addBookLabel.setText(rb.getString("bookColumn"));
        addAuthorLabel.setText(rb.getString("authorColumn"));
    }

    @FXML
    private void addBook () {
        openBookManageWindow(rb.getString("addBookTitle"), null);
    }

    @FXML
    private void addAuthor () {
        openBookAttributeWindow(rb.getString("addAuthorTitle"), null, false);
    }

    @FXML
    private void addGenre () {
        openBookAttributeWindow(rb.getString("addGenreTitle"), null, true);
    }

    private void openBookAttributeWindow(String title, BookAttribute item, boolean isGenre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bookattribute_view.fxml"));
            Parent root = loader.load();

            BookAttributeController attributeController = loader.getController();
            if (item != null) attributeController.setBookAttribute(item.copy());
            else {
                if (isGenre) attributeController.setBookAttribute(new Genre(-1, "", ""));
                else attributeController.setBookAttribute(new Author(-1, "", ""));
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
                Platform.runLater(() -> showError(rb.getString(ERROR), rb.getString("dataLoadError")));
            }
        });
    }
}
