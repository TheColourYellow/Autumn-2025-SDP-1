package com.group9.controller;

import com.group9.dao.BookDao;
import com.group9.model.Author;
import com.group9.model.Book;
import com.group9.model.Genre;
import com.group9.service.BookService;
import com.group9.util.AppExecutors;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import java.util.List;
import java.util.stream.Collectors;
import static com.group9.util.PopupUtils.showError;

public class ManagementController {

    @FXML private TableView<Book> managementTable;
    @FXML private TableColumn<Book, String> bookColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, String> genreColumn;

    @FXML private Button addBookButton;
    @FXML private Button addAuthorButton;
    @FXML private Button addGenreButton;

    @FXML private Label loginLabel;
    @FXML private Label homeLabel;

    private final ObservableList<Book> bookData = FXCollections.observableArrayList();
    private final BookService bookService = new BookService(new BookDao());

    @FXML
    public void initialize() {
        // Initialize table columns
        bookColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTitle())
        );
        authorColumn.setCellValueFactory(cellData -> {
            String authors = cellData.getValue().getAuthors().stream()
                    .map(Author::getName)
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(authors);
        });
        genreColumn.setCellValueFactory(cellData -> {
            String genres = cellData.getValue().getGenres().stream()
                    .map(Genre::getName)
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(genres);
        });

        // Bind data to table
        managementTable.setItems(bookData);
        loadBooks();
    }

    // Method for opening login window
    @FXML
    private void openLoginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login_view.fxml"));
            Parent root = loader.load();

            // login label is clicked, content of the window is replaced with the content of login window
            Stage stage = (Stage) loginLabel.getScene().getWindow();

            // Change the view to the new login view
            stage.setScene(new Scene(root));

            stage.setTitle("Login");
            stage.show();
        } catch (Exception e) {
            showError("Error", "Could not open login window.");
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
    private void addBook () {
        // Implementation for adding a book
    }

    @FXML
    private void addAuthor () {
        // Implementation for adding an author
    }

    @FXML
    private void addGenre () {
        // Implementation for adding a genre
    }

    // Load books from the database
    private void loadBooks() {
        // Use a background thread to keep UI responsive
        AppExecutors.databaseExecutor.execute(() -> {
            try {
                List<Book> books = bookService.getAllBooks();
                Platform.runLater(() -> bookData.setAll(books));
            } catch (Exception e) {
                Platform.runLater(() -> showError("Error", "Could not load books. Please try again later."));
            }
        });
    }
}
