package com.group9.controller;

import com.group9.dao.AuthorDao;
import com.group9.dao.GenreDao;
import com.group9.model.Author;
import com.group9.model.BookAttribute;
import com.group9.model.Genre;
import com.group9.service.AuthorService;
import com.group9.service.GenreService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import static com.group9.util.PopupUtils.showConfirmation;
import static com.group9.util.PopupUtils.showError;

public class BookAttributeController {
  private BookAttribute bookAttribute;
  private Runnable onCloseCallback;

  @FXML private TextField nameTextField;
  @FXML private TextField descTextField;

  @FXML private Button addBtn;
  @FXML private Button cancelBtn;
  @FXML private Button deleteBtn;

  // Services
  private final AuthorService authorService = new AuthorService(new AuthorDao());
  private final GenreService genreService = new GenreService(new GenreDao());

  public void setBookAttribute(BookAttribute bookAttribute) {
    this.bookAttribute = bookAttribute;
    nameTextField.setText(bookAttribute.getName());
    descTextField.setText(bookAttribute.getDescription());
    if (bookAttribute.getId() != -1) {
      deleteBtn.setVisible(true);
      addBtn.setText("Update");
    } else {
      deleteBtn.setVisible(false);
    }
  }

  public void setOnCloseCallback(Runnable onCloseCallback) {
    this.onCloseCallback = onCloseCallback;
  }

  @FXML
  private void handleSave() {
    if (nameTextField.getText().isEmpty()) {
      showError("Validation Error", "Name cannot be empty.");
      return;
    }

    bookAttribute.setName(nameTextField.getText().trim());
    bookAttribute.setDescription(descTextField.getText().trim());

    if (bookAttribute.getId() != -1) {
      // Update existing attribute
      System.out.println("Updating existing attribute");
      if (bookAttribute instanceof Author) {
        try {
          authorService.updateAuthor((Author) bookAttribute);

          // Refresh the author list in the main controller
          if (onCloseCallback != null) {
            onCloseCallback.run();
          }

          // Close the dialog
          handleClose();

        } catch (Exception e) {
          showError("Error", "Failed to update author: " + e.getMessage());
        }
      } else if (bookAttribute instanceof Genre) {
        try {
          genreService.updateGenre((Genre) bookAttribute);

          // Refresh the genre list in the main controller
          if (onCloseCallback != null) {
            onCloseCallback.run();
          }

          // Close the dialog
          handleClose();

        } catch (Exception e) {
          showError("Error", "Failed to update genre: " + e.getMessage());
        }
      }
    } else {
      System.out.println("Adding new attribute");
      if (bookAttribute instanceof Author) {
        try {
          System.out.println("Adding new author");
          authorService.addAuthor(bookAttribute.getName(), bookAttribute.getDescription());

          // Refresh the author list in the main controller
          if (onCloseCallback != null) {
            onCloseCallback.run();
          }

          // Close the dialog
          handleClose();
        } catch (Exception e) {
          showError("Error", "Failed to add author: " + e.getMessage());
        }
      } else if (bookAttribute instanceof Genre) {
        try {
          System.out.println("Adding new genre");
          genreService.addGenre(bookAttribute.getName(), bookAttribute.getDescription());

          // Refresh the genre list in the main controller
          if (onCloseCallback != null) {
            onCloseCallback.run();
          }

          // Close the dialog
          handleClose();
        } catch (Exception e) {
          showError("Error", "Failed to add genre: " + e.getMessage());
        }
      }
    }
  }

  @FXML
  private void handleDelete() {
    if (bookAttribute.getId() != -1) {
      if (showConfirmation("Delete Attribute", "Are you sure you want to delete this attribute?")) {
        if (bookAttribute instanceof Author) {
          try {
            AuthorService authorService = new AuthorService(new AuthorDao());
            authorService.deleteAuthor(bookAttribute.getName());

            // Refresh the author list in the main controller
            if (onCloseCallback != null) {
              onCloseCallback.run();
            }

            // Close the dialog
            handleClose();
          } catch (Exception e) {
            showError("Error", "Failed to delete author: " + e.getMessage());
          }
        } else if (bookAttribute instanceof Genre) {
          try {
            GenreService genreService = new GenreService(new GenreDao());
            genreService.deleteGenre(bookAttribute.getName());

            // Refresh the author list in the main controller
            if (onCloseCallback != null) {
              onCloseCallback.run();
            }

            // Close the dialog
            handleClose();
          } catch (Exception e) {
            showError("Error", "Failed to delete genre: " + e.getMessage());
          }
        }
      }
    }
  }

  @FXML
  private void handleClose() {
    Stage stage = (Stage) cancelBtn.getScene().getWindow();
    stage.close();
  }
}
