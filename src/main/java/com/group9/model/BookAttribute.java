package com.group9.model;

import com.group9.controller.ShoppingCartController;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BookAttribute {
  private int id;
  private String name;
  private String description;

  private static final Logger logger = Logger.getLogger(BookAttribute.class.getName());


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
        logger.log(Level.INFO, "Copy BookAttribute", e);
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
