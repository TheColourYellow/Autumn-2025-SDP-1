package com.group9.controller;

import com.group9.dao.AuthorDao;
import com.group9.dao.GenreDao;
import com.group9.model.Author;
import com.group9.model.BookAttribute;
import com.group9.model.Genre;
import com.group9.service.AuthorService;
import com.group9.service.GenreService;
import com.group9.util.AppExecutors;
import com.group9.util.LayoutOrienter;
import com.group9.util.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.ResourceBundle;

import static com.group9.util.PopupUtils.showConfirmation;
import static com.group9.util.PopupUtils.showError;

public class BookAttributeController {
  private BookAttribute bookAttribute;
  private Runnable onCloseCallback;
  private LayoutOrienter orienter = new LayoutOrienter();

  @FXML private AnchorPane bookattributeAnchor;

  @FXML
  private TextField nameTextField;
  @FXML
  private TextField descTextField;

  @FXML
  private Button addBtn;
  @FXML
  private Button cancelBtn;
  @FXML
  private Button deleteBtn;

  @FXML private Label nameLabel;
  @FXML private Label descLabel;

  private ResourceBundle rb;

  // Services
  private final AuthorService authorService = new AuthorService(new AuthorDao());
  private final GenreService genreService = new GenreService(new GenreDao());

  public void setBookAttribute(BookAttribute bookAttribute) {
    this.bookAttribute = bookAttribute;
    nameTextField.setText(bookAttribute.getName());
    descTextField.setText(bookAttribute.getDescription());
    if (bookAttribute.getId() != -1) {
      deleteBtn.setVisible(true);
      addBtn.setText(rb.getString("updateButton"));
    } else {
      deleteBtn.setVisible(false);
    }
  }

  @FXML
  private void initialize() {
    rb = SessionManager.getResourceBundle();
    orienter.orientLayout(bookattributeAnchor);
    nameLabel.setText(rb.getString("nameLabel"));
    descLabel.setText(rb.getString("descriptionLabel"));
    addBtn.setText(rb.getString("addButton"));
    cancelBtn.setText(rb.getString("cancelButton"));
    deleteBtn.setText(rb.getString("deleteButton"));
  }

  public void setOnCloseCallback(Runnable onCloseCallback) {
    this.onCloseCallback = onCloseCallback;
  }

  @FXML
  private void handleSave() {
    String name = nameTextField.getText().trim();
    String desc = descTextField.getText().trim();

    if (name.isEmpty()) {
      showError("Validation Error", "Name cannot be empty.");
      return;
    }

    bookAttribute.setName(name);
    bookAttribute.setDescription(desc);

    boolean isUpdate = bookAttribute.getId() != -1;

    AppExecutors.databaseExecutor.execute(() -> {
      try {
        if (bookAttribute instanceof Author) {
          if (isUpdate)
            authorService.updateAuthor((Author) bookAttribute);
          else
            authorService.addAuthor(bookAttribute.getName(), bookAttribute.getDescription());
        } else if (bookAttribute instanceof Genre) {
          if (isUpdate)
            genreService.updateGenre((Genre) bookAttribute);
          else
            genreService.addGenre(bookAttribute.getName(), bookAttribute.getDescription());
        } else {
          throw new IllegalArgumentException("Unknown book attribute type");
        }

        // Run UI updates on the JavaFX Application Thread
        Platform.runLater(() -> {
          if (onCloseCallback != null) onCloseCallback.run();
          handleClose();
        });

      } catch (Exception e) {
        Platform.runLater(() ->
                showError("Error", "Failed to save: " + e.getMessage())
        );
      }
    });
  }

  @FXML
  private void handleDelete() {
    if (bookAttribute.getId() == -1) {
      showError("Error", "Cannot delete unsaved attribute.");
      return;
    }

    if (!showConfirmation("Delete Attribute",
            "Are you sure you want to delete this attribute?")) {
      return;
    }

    AppExecutors.databaseExecutor.execute(() -> {
      try {
        if (bookAttribute instanceof Author) {
          authorService.deleteAuthor(bookAttribute.getName());
        } else if (bookAttribute instanceof Genre) {
          genreService.deleteGenre(bookAttribute.getName());
        } else {
          throw new IllegalArgumentException("Unknown attribute type");
        }

        Platform.runLater(() -> {
          if (onCloseCallback != null) onCloseCallback.run();
          handleClose();
        });

      } catch (Exception e) {
        Platform.runLater(() ->
                showError("Error", "Failed to delete: " + e.getMessage())
        );
      }
    });
  }

  @FXML
  private void handleClose() {
    Stage stage = (Stage) cancelBtn.getScene().getWindow();
    stage.close();
  }
}
