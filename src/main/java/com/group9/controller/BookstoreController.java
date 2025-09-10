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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

import static com.group9.util.PopupUtils.showError;

public class BookstoreController {

    @FXML
    private VBox genreSidebar;

    @FXML
    private Button sidebarButton;

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
