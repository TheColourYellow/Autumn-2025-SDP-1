package com.group9.model;

public class Genre extends BookAttribute {
  public Genre(int id, String name, String description) {
    super(id, name, description);
  }
  public Genre(String name) {
    super(-1, name, null);
  }
}
