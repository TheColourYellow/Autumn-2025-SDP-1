package com.group9.model;

public abstract class BookAttribute {
  private int id;
  private String name;
  private String description;

  public BookAttribute(int id, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
  }

  @Override
  public String toString() {
    return name;
  }

  public int getId() {
    return id;
  }
  public String getName() {
    return name;
  }
  public String getDescription() {
    return description;
  }
}
