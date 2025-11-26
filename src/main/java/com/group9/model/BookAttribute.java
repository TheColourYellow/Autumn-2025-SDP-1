package com.group9.model;

public abstract class BookAttribute {
  private int id;
  private String name;
  private String description;

  protected BookAttribute(int id, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
  }

  public BookAttribute copy() {
    try {
      return this.getClass()
                 .getConstructor(int.class, String.class, String.class)
                 .newInstance(this.id, this.name, this.description);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
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
  public void setName(String name) {
    this.name = name;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
}
