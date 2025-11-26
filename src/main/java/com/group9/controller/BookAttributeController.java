package com.group9.controller;

import com.group9.dao.AuthorDao;
import com.group9.dao.GenreDao;
import com.group9.model.Author;
import com.group9.model.BookAttribute;
import com.group9.model.BookAttributeTranslation;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import static com.group9.util.PopupUtils.showConfirmation;
import static com.group9.util.PopupUtils.showError;

public class BookAttributeController {
  private BookAttribute bookAttribute;
  private Runnable onCloseCallback;
  private LayoutOrienter orienter = new LayoutOrienter();

  @FXML private AnchorPane bookattributeAnchor;

  @FXML private TextField nameTextFieldEn;
  @FXML private TextField nameTextFieldJa;
  @FXML private TextField nameTextFieldAr;

  @FXML private TextField descTextFieldEn;
  @FXML private TextField descTextFieldJa;
  @FXML private TextField descTextFieldAr;

  @FXML
  private Button addBtn;
  @FXML
  private Button cancelBtn;
  @FXML
  private Button deleteBtn;

  @FXML private Label nameLabel;
  @FXML private Label descLabel;

  // Translations
  private ResourceBundle rb;
  private HashMap<String, TextField> nameFields;
  private HashMap<String, TextField> descFields;

  // Services
  private final AuthorService authorService = new AuthorService(new AuthorDao());
  private final GenreService genreService = new GenreService(new GenreDao());
  private static final String UNKNOWN_ERROR = "unknownAttributeTypeError";

  public void setBookAttribute(BookAttribute bookAttribute) {
    this.bookAttribute = bookAttribute;
    nameTextFieldEn.setText(bookAttribute.getName());
    descTextFieldEn.setText(bookAttribute.getDescription());

    if (bookAttribute.getId() != -1) {
      deleteBtn.setVisible(true);
      addBtn.setText(rb.getString("updateButton"));
    } else {
      deleteBtn.setVisible(false);
    }

    loadTranslations();
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

    nameFields = new HashMap<>();
    nameFields.put("en", nameTextFieldEn);
    nameFields.put("ja", nameTextFieldJa);
    nameFields.put("ar", nameTextFieldAr);
    descFields = new HashMap<>();
    descFields.put("en", descTextFieldEn);
    descFields.put("ja", descTextFieldJa);
    descFields.put("ar", descTextFieldAr);
  }

  private void loadTranslations() {
    if (bookAttribute == null) return;

    List<BookAttributeTranslation> translations;

    if (bookAttribute instanceof Genre) {
      translations = genreService.getTranslationsForGenre(bookAttribute.getId());
    }
    else if (bookAttribute instanceof Author) {
      translations = authorService.getTranslationsForAuthor(bookAttribute.getId());
    } else {
      throw new IllegalArgumentException(rb.getString(UNKNOWN_ERROR));
    }

    for (BookAttributeTranslation t : translations) {
      TextField nameField = nameFields.get(t.getLanguageCode());
      if (nameField != null) {
        nameField.setText(t.getTranslatedName());
      }
      TextField descField = descFields.get(t.getLanguageCode());
      if (descField != null) {
        descField.setText(t.getTranslatedDescription());
      }
    }
  }

  public void setOnCloseCallback(Runnable onCloseCallback) {
    this.onCloseCallback = onCloseCallback;
  }

  @FXML
  private void handleSave() {
    // Make sure the name field for the default language (English) is not empty
    if (nameTextFieldEn.getText().trim().isEmpty()) {
      showError(rb.getString("validationError"), rb.getString("nameEmptyError"));
      return;
    }

    bookAttribute.setName(nameTextFieldEn.getText().trim());
    bookAttribute.setDescription(descTextFieldEn.getText().trim());

    // Gather translations
    List<BookAttributeTranslation> translationsToSave = new ArrayList<>();
    for (String langCode : nameFields.keySet()) {
      if (langCode.equals("en")) continue; // Skip default language
      String name = nameFields.get(langCode).getText().trim();
      String desc = descFields.get(langCode).getText().trim();
      if (!name.isEmpty() || !desc.isEmpty()) {
        translationsToSave.add(new BookAttributeTranslation(langCode, name, desc));
      }
    }

    AppExecutors.databaseExecutor.execute(() -> {
      try {
        if (bookAttribute instanceof Author) {
            authorService.saveAuthorWithTranslations((Author) bookAttribute, translationsToSave);
        } else if (bookAttribute instanceof Genre) {
            genreService.saveGenreWithTranslations((Genre) bookAttribute, translationsToSave);
        } else {
          throw new IllegalArgumentException(rb.getString(UNKNOWN_ERROR));
        }

        // Refresh list & close
        Platform.runLater(() -> {
          if (onCloseCallback != null) onCloseCallback.run();
          handleClose();
        });
      } catch (Exception e) {
        Platform.runLater(() -> showError(rb.getString("error"), e.getMessage()));
      }
    });
  }

  @FXML
  private void handleDelete() {
    if (bookAttribute.getId() == -1) {
      return; // Should not happen, button hidden for new attributes
    }

    if (!showConfirmation(rb.getString("deleteAttributeConfirmationTitle"),
            rb.getString("deleteAttributeConfirmationMessage"))) {
      return;
    }

    AppExecutors.databaseExecutor.execute(() -> {
      try {
        if (bookAttribute instanceof Author) {
          authorService.deleteAuthor(bookAttribute.getName());
        } else if (bookAttribute instanceof Genre) {
          genreService.deleteGenre(bookAttribute.getName());
        } else {
          throw new IllegalArgumentException(rb.getString(UNKNOWN_ERROR));
        }

        Platform.runLater(() -> {
          if (onCloseCallback != null) onCloseCallback.run();
          handleClose();
        });

      } catch (Exception e) {
        Platform.runLater(() ->
                showError(rb.getString("error"), e.getMessage())
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
