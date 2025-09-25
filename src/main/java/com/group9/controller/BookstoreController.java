package com.group9.controller;

import com.group9.dao.BookDao;
import com.group9.model.Author;
import com.group9.model.Book;
import com.group9.model.Genre;
import com.group9.service.BookService;
import com.group9.util.AppExecutors;
import com.group9.util.SessionManager;
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
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.util.Callback;

import java.io.IOException;
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
    @FXML
    private Label managementLabel;

    @FXML private TableView<Book> bookTable;
    @FXML private TableColumn<Book, String> titleColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, String> genreColumn;
    @FXML private TableColumn<Book, Double> priceColumn;

    @FXML private ImageView shoppingCart; // image button for opening shopping cart

    private final BookService bookService = new BookService(new BookDao());
    private final ObservableList<Book> bookData = FXCollections.observableArrayList();

    @FXML
    private TableColumn<Book, Void> actionColumn; // For add to cart button in book list

    // Method for controlling visibility of genres sidebar
    @FXML
    private void toggleSidebar() {
        boolean isVisible = genreSidebar.isVisible();
        genreSidebar.setVisible(!isVisible);

        // Update button text, when sidebar is visible and when it's not
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
            // Load the FXML file for the login window
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
        if (SessionManager.isLoggedIn()) {
            loginLabel.setText("Profile");
            loginLabel.setOnMouseClicked(event -> {
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
            });
        }

        // Show management label only for admin users
        if (SessionManager.isAdmin()) {
            managementLabel.setOnMouseClicked(event -> {
               try {
                   FXMLLoader loader = new FXMLLoader(getClass().getResource("/management_view.fxml"));
                   Parent root = loader.load();

                   Stage stage = (Stage) managementLabel.getScene().getWindow();

                   stage.setScene(new Scene(root));

                   stage.setTitle("Management");
                   stage.show();
               } catch (Exception e) {
                   showError("Error", "Could not open management window.");

               }
            });
        } else {
            // Hide management label if not admin
            managementLabel.setVisible(false);
            managementLabel.setManaged(false);
        }

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

        // Create add to cart button for each row
        actionColumn.setCellFactory(new Callback<TableColumn<Book, Void>, TableCell<Book, Void>>() {
            @Override
            public TableCell<Book, Void> call(final TableColumn<Book, Void> param) {
                final TableCell<Book, Void> cell = new TableCell<Book, Void>() {

                    private final Button btn = new Button("Add to Cart");

                    {
                        btn.setOnAction(event -> {
                            Book book = getTableView().getItems().get(getIndex());
                            System.out.println("Clicked Add to Cart for: " + book.getTitle()); // Doesn't do anything else yet than print this to console
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null); // No button in empty rows
                        } else {
                            setGraphic(btn);  // Show button in data rows
                        }
                    }
                };
                return cell;
            }
        });

        // Bind data to table
        bookTable.setItems(bookData);
        loadBooks();
    }

    // Method for opening shopping cart window (new window)
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
