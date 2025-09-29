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
import com.group9.util.SimpleListCell;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.group9.util.PopupUtils.showConfirmation;
import static com.group9.util.PopupUtils.showError;

public class BookManageController {
  private Book book;
  private Runnable onCloseCallback;

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

  private final BookService bookService = new BookService(new BookDao());
  private final GenreService genreService = new GenreService(new GenreDao());
  private final AuthorService authorService = new AuthorService(new AuthorDao());

  private final ObservableList<Genre> genreData = FXCollections.observableArrayList();
  private final ObservableList<Author> authorData = FXCollections.observableArrayList();

  private final Map<Genre, BooleanProperty> genreSelections = new HashMap<>();
  private final Map<Author, BooleanProperty> authorSelections = new HashMap<>();

  @FXML
  private void initialize() {
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
    addBtn.setText("Update");

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
    if (book != null && book.getId() != -1) {
      // Update existing book
    } else {
      Book newBook = new Book(
              -1,
              titleTextField.getText().trim(),
              isbnTextField.getText().trim(),
              Integer.parseInt(yearTextField.getText().trim()),
              Double.parseDouble(priceTextField.getText().trim()),
              descTextArea.getText().trim()
      );
      newBook.setGenres(getSelectedGenres());
      newBook.setAuthors(getSelectedAuthors());

      if (bookService.addBook(newBook) != -1) { // Successfully added
        // Refresh the book list in the main controller
        if (onCloseCallback != null) {
          onCloseCallback.run();
        }

        // Close the dialog
        Stage stage = (Stage) addBtn.getScene().getWindow();
        stage.close();
      } else {
        showError("Error", "Could not add book. Please try again later.");
      }
    }
  }

  @FXML
  private void handleCancel() {
    Stage stage = (Stage) cancelBtn.getScene().getWindow();
    stage.close();
  }

  @FXML
  private void handleDelete() {
    if (book != null && book.getId() != -1) {
      if (showConfirmation("Delete Book: " + book.getTitle(), "Are you sure you want to delete this book?")) {
        try {
          bookService.deleteBook(book.getId());

          // Refresh the author list in the main controller
          if (onCloseCallback != null) {
            onCloseCallback.run();
          }

          // Close the dialog
          Stage stage = (Stage) deleteBtn.getScene().getWindow();
          stage.close();
        } catch (Exception e) {
          showError("Error", "Could not delete book: " + e.getMessage());
        }
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
    System.out.println("Applying selections for book: " + book.getTitle());
    // reset all checkboxes
    genreSelections.values().forEach(prop -> prop.set(false));
    authorSelections.values().forEach(prop -> prop.set(false));

    // select the book's genres
    for (Genre genre : book.getGenres()) {
      System.out.println("Selecting genre: " + genre.getName());
      BooleanProperty prop = genreSelections.get(genre);
      if (prop != null) prop.set(true);
    }

    // select the book's authors
    for (Author author : book.getAuthors()) {
      System.out.println("Selecting author: " + author.getName());
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
        Platform.runLater(() -> showError("Error", "Could not load data. Please try again later."));
      }
    });
  }
}
