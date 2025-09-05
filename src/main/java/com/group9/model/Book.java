package com.group9.model;

import java.util.ArrayList;
import java.util.List;

public class Book {
  private int id;
  private String title;
  private String isbn;
  private int year;
  private double price;
  private List<Author> authors;
  private List<Genre> genres;
  private String description;

  public Book(int id, String title, String isbn, int year, double price, String description) {
    this.id = id;
    this.title = title;
    this.isbn = isbn;
    this.year = year;
    this.price = price;
    this.authors = new ArrayList<>();
    this.genres = new ArrayList<>();
    this.description = description;
  }

  public int getId() {
    return id;
  }
  public String getTitle() {
    return title;
  }
  public String getIsbn() {
    return isbn;
  }
  public int getYear() {
    return year;
  }
  public double getPrice() {
    return price;
  }
  public List<Author> getAuthors() {
    return authors;
  }
  public List<Genre> getGenres() {
    return genres;
  }
  public String getDescription() {
    return description;
  }

  public void setAuthors(List<Author> authors) {
    this.authors = authors;
  }
  public void setGenres(List<Genre> genres) {
    this.genres = genres;
  }
}
