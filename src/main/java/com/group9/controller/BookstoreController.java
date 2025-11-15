package com.group9.controller;

import com.group9.dao.BookDao;
import com.group9.dao.GenreDao;
import com.group9.model.Author;
import com.group9.model.Book;
import com.group9.model.Genre;
import com.group9.service.BookService;
import com.group9.service.GenreService;
import com.group9.util.AppExecutors;
import com.group9.util.LayoutOrienter;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.util.Callback;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.group9.util.PopupUtils.showError;

public class BookstoreController {

    private ResourceBundle rb;
    private LayoutOrienter orienter = new LayoutOrienter();

    @FXML private AnchorPane bookstoreAnchor;

    @FXML
    private Label homeLabel;

    @FXML
    private Label bookListLabel;

    @FXML
    private Label bookStoreLabel;

    @FXML
    private VBox genreSidebar;

    @FXML
    private Button sidebarButton;

    @FXML
    private Label loginLabel;
    @FXML
    private Label managementLabel;
    @FXML
    private TextField searchField;

    @FXML private TableView<Book> bookTable;
    @FXML private TableColumn<Book, String> titleColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, String> genreColumn;
    @FXML private TableColumn<Book, Double> priceColumn;

    @FXML private ImageView shoppingCart; // image button for opening shopping cart

    private final BookService bookService = new BookService(new BookDao());
    private final ObservableList<Book> bookData = FXCollections.observableArrayList();
    private final Map<CheckBox, Genre> genreCheckBoxMap = new HashMap<>();

    @FXML
    private TableColumn<Book, Void> actionColumn; // For add to cart button in book list

    @FXML
    private ComboBox<String> languageSelector; // Language dropdown menu

    // cart observable list to store books added to cart by user
    private final ObservableList<Book> cart = FXCollections.observableArrayList();

    // Method for controlling visibility of genres sidebar
    @FXML
    private void toggleSidebar() {
        boolean isVisible = genreSidebar.isVisible();
        genreSidebar.setVisible(!isVisible);

        // Update button text, when sidebar is visible and when it's not
        if (isVisible) {
            sidebarButton.setText(rb.getString("sidebarButtonShow"));
        } else {
            sidebarButton.setText(rb.getString("sidebarButtonHide"));
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

            stage.setTitle(rb.getString("loginPageText"));
            stage.show();
        } catch (Exception e) {
            showError("Error", "Could not open login window.");
        }
    }

    private void openProfileWindow() {
        try {
            // Load the FXML file for the profile window
            FXMLLoader loader;
            loader = new FXMLLoader(getClass().getResource("/profile_view.fxml"));
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

    private void openManagementWindow() {
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
    }

    @FXML
    public void initialize() {
        rb = SessionManager.getResourceBundle();
        orienter.orientLayout(bookstoreAnchor);
        updateUI();

        // Initialize language selector
        languageSelector.setItems(FXCollections.observableArrayList("Japanese", "English", "Arabic"));
        languageSelector.setValue(SessionManager.getLanguage()); // Get current language
        languageSelector.setOnAction(event -> handleLanguageChange());

        // Update login label based on session state
        if (SessionManager.isLoggedIn()) {
            loginLabel.setText(rb.getString("profileLabel"));
            loginLabel.setOnMouseClicked(event -> openProfileWindow());
        } else {
            loginLabel.setText(rb.getString("loginLabel"));
            loginLabel.setOnMouseClicked(event -> openLoginWindow());
        }

        // Show management label only for admin users
        if (SessionManager.isAdmin()) {
            managementLabel.setOnMouseClicked(event -> openManagementWindow());
        } else {
            // Hide management label if not admin
            managementLabel.setVisible(false);
            managementLabel.setManaged(false);
        }

        // Load genres from the database and populate sidebar
        List<Genre> genreList = new GenreService(new GenreDao()).getAllGenres();
        if (genreList != null) {
            for (Genre genre : genreList) {
                CheckBox checkBox = new CheckBox(genre.getName());
                checkBox.setOnAction(event -> sortByGenre());
                genreCheckBoxMap.put(checkBox, genre);
                genreSidebar.getChildren().add(checkBox);
            }
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

                    private final Button btn = new Button();

                    {
                        btn.setOnAction(event -> {
                            Book book = getTableView().getItems().get(getIndex());
                            cart.add(book);
                            System.out.println("Clicked Add to Cart for: " + book.getTitle());
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null); // No button in empty rows
                        } else {
                            btn.setText(rb.getString("addToCartButton"));
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

        // Trigger search when pressing enter (remember to press enter also after clearing search bar to bring back full booklist)
        searchField.setOnAction(event -> handleSearch());
    }

    // Method to handle search input
    public void handleSearch() {
        String query = searchField.getText();

        if (query == null || query.isEmpty()) {
            // If query is empty, reload all books
            bookTable.setItems(bookData);
            bookTable.refresh(); // Makes sure table shows everything correctly (for example buttons don't disappear) after clearing the search
            return;
        }

        // Turn search query to lowercase
        String lowerCaseQuery = query.toLowerCase();


        // Take original bookData list that contains all books.
        ObservableList<Book> filtered = bookData.filtered(book ->
                book.getTitle().toLowerCase().contains(lowerCaseQuery) ||  // Match by book title
                        book.getAuthors().stream().anyMatch(a ->
                                a.getName().toLowerCase().contains(lowerCaseQuery))   // Match by author name
        );

        // Show filtered results in the table
        bookTable.setItems(filtered);
    }

    // Method for opening shopping cart window (new window)
    @FXML
    private void openShoppingCart() {
        System.out.println("Shopping cart clicked!");
        rb = SessionManager.getResourceBundle();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/shopping_cart_view.fxml"));
            Parent root = loader.load();

            // Get the controller instance of the loaded FXML so its methods can be called
            ShoppingCartController controller = loader.getController();
            controller.setCart(cart); // Pass the current cart list to ShoppingCartController so it can display books in cart

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

    private void sortByGenre() {
        List<Integer> selectedGenreIds = new ArrayList<>();

        // Collect IDs of selected genres
        for (Map.Entry<CheckBox, Genre> entry : genreCheckBoxMap.entrySet()) {
            if (entry.getKey().isSelected()) {
                selectedGenreIds.add(entry.getValue().getId());
            }
        }

        // Use a background thread to keep UI responsive
        AppExecutors.databaseExecutor.execute(() -> {
            try {
                List<Book> books;
                if (selectedGenreIds.isEmpty()) {
                    books = bookService.getAllBooks(); // No genres selected, show all books
                } else {
                    books = bookService.searchBooks(new ArrayList<>(), selectedGenreIds); // Search by selected genres only
                }
                Platform.runLater(() -> bookData.setAll(books));
            } catch (Exception e) {
                Platform.runLater(() -> showError("Error", "Could not sort books. Please try again later."));
            }
        });
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

    // For handling language change with the dropdown menu
    private void handleLanguageChange() {
        String selectedLanguage = languageSelector.getValue();
        if (selectedLanguage == null) return;

        switch (selectedLanguage) {
            case "Japanese":
                loadLanguage("ja", "JP");
                SessionManager.setLanguage("Japanese");
                System.out.println("Language changed to Japanese");
                break;
            case "English":
                loadLanguage("en", "US");
                SessionManager.setLanguage("English");
                System.out.println("Language changed to English");
                break;
            case "Arabic":
                loadLanguage("ar", "SA");
                SessionManager.setLanguage("Arabic");
                System.out.println("Language changed to Arabic");
        }
    }

    // Load a specific language
    private void loadLanguage(String langCode, String country) {
        SessionManager.setLocale(new Locale(langCode, country));
        rb = SessionManager.getResourceBundle();

        // Update UI texts
        updateUI();
    }

    private void updateUI() {
        sidebarButton.setText(rb.getString("sidebarButtonShow"));
        loginLabel.setText(SessionManager.isLoggedIn() ? rb.getString("profileLabel") : rb.getString("loginLabel"));
        managementLabel.setText(rb.getString("managementLabel"));
        searchField.setPromptText(rb.getString("searchPrompt"));
        titleColumn.setText(rb.getString("bookNameColumn"));
        authorColumn.setText(rb.getString("authorColumn"));
        genreColumn.setText(rb.getString("genreColumn"));
        priceColumn.setText(rb.getString("priceColumn"));
        actionColumn.setText(rb.getString("addToCartButton"));
        homeLabel.setText(rb.getString("homeLabel"));
        bookListLabel.setText(rb.getString("bookListLabel"));
        bookStoreLabel.setText(rb.getString("bookStoreLabel"));

        // Refresh genre checkboxes with translated names
        refreshGenreCheckboxes();
        // Refresh table to update button texts when language changes
        bookTable.refresh();
    }

    private void refreshGenreCheckboxes() {
        try {
            // Reload genres in the newly selected language
            List<Genre> updatedGenres = new GenreService(new GenreDao()).getAllGenres();

            // A quick lookup: id -> Genre
            Map<Integer, Genre> genreById = updatedGenres.stream()
                    .collect(Collectors.toMap(Genre::getId, g -> g));

            // Update each checkbox with the new translated name
            for (Map.Entry<CheckBox, Genre> entry : genreCheckBoxMap.entrySet()) {
                CheckBox cb = entry.getKey();
                Genre original = entry.getValue();

                Genre updated = genreById.get(original.getId());
                if (updated != null) {
                    cb.setText(updated.getName());
                }
            }
        } catch (Exception e) {
            System.err.println("Error refreshing genre checkboxes: " + e.getMessage());
        }
    }
}
