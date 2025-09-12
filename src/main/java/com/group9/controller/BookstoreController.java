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
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

import static com.group9.util.PopupUtils.showError;

public class BookstoreController {

    @FXML
    private VBox genreSidebar;

    @FXML
    private Button sidebarButton;

    @FXML
    private Label loginLabel;

    @FXML private TableView<Book> bookTable;
    @FXML private TableColumn<Book, String> titleColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, String> genreColumn;
    @FXML private TableColumn<Book, Double> priceColumn;



    private final BookService bookService = new BookService(new BookDao());
    private final ObservableList<Book> bookData = FXCollections.observableArrayList();

    // Method for controlling visibility of genres sidebar
    @FXML
    private void toggleSidebar() {
        boolean isVisible = genreSidebar.isVisible();
        genreSidebar.setVisible(!isVisible);

        // Update button text, when sidebar is visble and when its not
        if (isVisible) {
            sidebarButton.setText("Show Genres");
        } else {
            sidebarButton.setText("Hide Genres");
        }
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

    @FXML
    public void initialize() {
        // Initialize table columns
        titleColumn.setCellValueFactory(cellData ->
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
        priceColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getPrice())
        );

        // Bind data to table
        bookTable.setItems(bookData);
        loadBooks();
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
