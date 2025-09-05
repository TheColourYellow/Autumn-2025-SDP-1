package com.group9.model;

public class Author extends BookAttribute {
  public Author(int id, String name, String description) {
    super(id, name, description);
  }
  public Author(String name) {
    super(-1, name, null);
  }
}
