package com.group9.controller;

import com.group9.dao.AuthorDao;
import com.group9.dao.BookDao;
import com.group9.dao.GenreDao;
import com.group9.model.Author;
import com.group9.model.Book;
import com.group9.model.Genre;
import com.group9.service.AuthorService;
import com.group9.service.BookService;
import com.group9.service.GenreService;
import com.group9.util.AppExecutors;
import com.group9.util.LayoutOrienter;
import com.group9.util.SessionManager;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.group9.util.PopupUtils.showConfirmation;
import static com.group9.util.PopupUtils.showError;

public class BookManageController {
  private Book book;
  private Runnable onCloseCallback;
  private LayoutOrienter orienter = new LayoutOrienter();
  private static final Logger log = Logger.getLogger(BookManageController.class.getName());

  @FXML private AnchorPane bookmanageAnchor;

  @FXML
  private TextField titleTextField;
  @FXML
  private TextField isbnTextField;
  @FXML
  private TextField yearTextField;
  @FXML
  private TextField priceTextField;
  @FXML
  private ListView<Genre> genreListView;
  @FXML
  private ListView<Author> authorListView;
  @FXML
  private TextArea descTextArea;
  @FXML
  private Button cancelBtn;
  @FXML
  private Button addBtn;
  @FXML
  private Button deleteBtn;

  @FXML private Label titleLabel;
  @FXML private Label isbnLabel;
  @FXML private Label yearLabel;
  @FXML private Label priceLabel;
  @FXML private Label descLabel;
  @FXML private Label genresLabel;
  @FXML private Label authorsLabel;

  private final BookService bookService = new BookService(new BookDao());
  private final GenreService genreService = new GenreService(new GenreDao());
  private final AuthorService authorService = new AuthorService(new AuthorDao());

  private final ObservableList<Genre> genreData = FXCollections.observableArrayList();
  private final ObservableList<Author> authorData = FXCollections.observableArrayList();

  private final Map<Genre, BooleanProperty> genreSelections = new HashMap<>();
  private final Map<Author, BooleanProperty> authorSelections = new HashMap<>();

  private ResourceBundle rb;
  private static final String VALID_ERROR = "validationError";
  private static final String ERROR = "error";

  @FXML
  private void initialize() {
    rb = SessionManager.getResourceBundle();
    orienter.orientLayout(bookmanageAnchor);
    titleLabel.setText(rb.getString("titleLabel"));
    isbnLabel.setText(rb.getString("isbnLabel"));
    yearLabel.setText(rb.getString("yearLabel"));
    priceLabel.setText(rb.getString("priceLabel"));
    descLabel.setText(rb.getString("descriptionLabel"));
    genresLabel.setText(rb.getString("genresLabel"));
    authorsLabel.setText(rb.getString("authorsLabel"));
    addBtn.setText(rb.getString("addButton"));
    cancelBtn.setText(rb.getString("cancelButton"));
    deleteBtn.setText(rb.getString("deleteButton"));

    deleteBtn.setVisible(false); // Hide delete button for new books

    genreListView.setItems(genreData);
    authorListView.setItems(authorData);

    // CheckBox cells for genres and authors
    genreListView.setCellFactory(CheckBoxListCell.forListView(genreSelections::get));
    authorListView.setCellFactory(CheckBoxListCell.forListView(authorSelections::get));

    loadData();
  }

  public void setBook(Book book) {
    deleteBtn.setVisible(true); // Show delete button for existing books

    this.book = book;
    titleTextField.setText(book.getTitle());
    isbnTextField.setText(book.getIsbn());
    yearTextField.setText(String.valueOf(book.getYear()));
    priceTextField.setText(String.valueOf(book.getPrice()));
    descTextArea.setText(book.getDescription());
    addBtn.setText(rb.getString("updateButton"));

    // Apply selections if data is already loaded
    if (!genreData.isEmpty() && !authorData.isEmpty()) {
      applyBookSelections();
    }
  }

  public void setOnCloseCallback(Runnable onCloseCallback) {
    this.onCloseCallback = onCloseCallback;
  }

    @FXML
    private void handleSave() {
        try {
            String title = titleTextField.getText().trim();
            String isbn = isbnTextField.getText().trim();
            String yearText = yearTextField.getText().trim();
            String priceText = priceTextField.getText().trim();
            String desc = descTextArea.getText().trim();

            // input validation
            if (title.isEmpty()) {
                showError(rb.getString(VALID_ERROR), rb.getString("bookTitleNull"));
                return;
            }

            if (yearText.isEmpty()) {
                showError(rb.getString(VALID_ERROR), rb.getString("bookYearNull"));
                return;
            }

            if (priceText.isEmpty()) {
                showError(rb.getString(VALID_ERROR), rb.getString("bookPriceNull"));
                return;
            }

            int year;
            double price;
            try {
                year = Integer.parseInt(yearText);
                price = Double.parseDouble(priceText);
            } catch (NumberFormatException e) {
                showError(rb.getString(VALID_ERROR), rb.getString("yearPriceValidationError"));
                return;
            }

            if (book != null && book.getId() != -1) {
                // Update existing book
                book.setTitle(title);
                book.setIsbn(isbn);
                book.setYear(year);
                book.setPrice(price);
                book.setDescription(desc);
                book.setGenres(getSelectedGenres());
                book.setAuthors(getSelectedAuthors());

                bookService.updateBook(book);

            } else {
                // Create new book
                Book newBook = new Book(-1, title, isbn, year, price, desc);
                newBook.setGenres(getSelectedGenres());
                newBook.setAuthors(getSelectedAuthors());

                if (bookService.addBook(newBook) == -1) {
                    showError(rb.getString(ERROR), rb.getString("errorAddingBook"));
                    return;
                }
            }

            // Refresh list & close
            if (onCloseCallback != null) {
                onCloseCallback.run();
            }
            handleCancel();

        } catch (Exception e) {
            showError(rb.getString(ERROR), e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
  private void handleCancel() {
    Stage stage = (Stage) cancelBtn.getScene().getWindow();
    stage.close();
  }

  @FXML
  private void handleDelete() {
      if (book != null && book.getId() != -1
          && showConfirmation(rb.getString("deleteBookConfirmationTitle") + book.getTitle(), rb.getString("deleteBookConfirmationMessage"))) {
              try {
                  bookService.deleteBook(book.getId());
                  // Refresh the author list in the main controller
                  if (onCloseCallback != null) {
                      onCloseCallback.run();
                  }
                  // Close the dialog
                  handleCancel();
              } catch (Exception e) {
                  showError(rb.getString(ERROR), e.getMessage());
              }
      }
  }

  private List<Genre> getSelectedGenres() {
    return genreSelections.entrySet().stream()
            .filter(e -> e.getValue().get())
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
  }

  private List<Author> getSelectedAuthors() {
    return authorSelections.entrySet().stream()
            .filter(e -> e.getValue().get())
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
  }

  private void applyBookSelections() {
    log.log(Level.INFO, "Applying selections for book: {0}", new Object[]{book.getTitle()});
    // reset all checkboxes
    genreSelections.values().forEach(prop -> prop.set(false));
    authorSelections.values().forEach(prop -> prop.set(false));

    // select the book's genres
    for (Genre genre : book.getGenres()) {
      log.log(Level.INFO, "Selecting genre: {0}", new Object[]{genre.getName()});
      BooleanProperty prop = genreSelections.get(genre);
      if (prop != null) prop.set(true);
    }

    // select the book's authors
    for (Author author : book.getAuthors()) {
      log.log(Level.INFO, "Selecting author: {0}", new Object[]{author.getName()});
      BooleanProperty prop = authorSelections.get(author);
      if (prop != null) prop.set(true);
    }
  }

  private void loadData() {
    AppExecutors.databaseExecutor.execute(() -> {
      try {
        List<Genre> genres = genreService.getAllGenres();
        List<Author> authors = authorService.getAllAuthors();
        Platform.runLater(() -> {
          genreData.setAll(genres);
          authorData.setAll(authors);

          genres.forEach(g -> genreSelections.putIfAbsent(g, new SimpleBooleanProperty(false)));
          authors.forEach(a -> authorSelections.putIfAbsent(a, new SimpleBooleanProperty(false)));

          if (book != null) {
            applyBookSelections();
          }
        });
      } catch (Exception e) {
        Platform.runLater(() -> showError(rb.getString(ERROR), rb.getString("dataLoadError")));
      }
    });
  }
}
