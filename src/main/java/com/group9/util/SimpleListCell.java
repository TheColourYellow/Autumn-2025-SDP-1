package com.group9.util;

import javafx.scene.control.ListCell;

import java.util.function.Consumer;

public class SimpleListCell<T> extends ListCell<T> {
  private final Consumer<T> onClick;

  public SimpleListCell(Consumer<T> onClick) {
    this.onClick = onClick;
    setOnMouseClicked(e -> {
      if (!isEmpty() && onClick != null) {
        onClick.accept(getItem());
      }
    });
  }

  @Override
  protected void updateItem(T item, boolean empty) {
    super.updateItem(item, empty);
    if (empty || item == null) {
      setText(null);
      setStyle("");
    } else {
      setText(item.toString());
      setStyle("-fx-padding: 8; -fx-border-color: lightgray; -fx-border-width: 0 0 1 0;");
    }
  }
}
