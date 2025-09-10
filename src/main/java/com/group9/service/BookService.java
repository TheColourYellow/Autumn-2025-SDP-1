package com.group9.service;

import com.group9.dao.BookDao;
import com.group9.model.Book;

import java.util.List;

public class BookService {
  private final BookDao bookDao;

  public BookService(BookDao bookDao) {
    this.bookDao = bookDao;
  }

  public List<Book> getAllBooks() {
    return bookDao.getAllBooks();
  }

  public Book getBookById(int id) {
    if (id <= 0)
      throw new IllegalArgumentException("Book ID must be positive");

    try {
      return bookDao.getBookById(id);
    } catch (Exception e) {
      System.out.println("Error retrieving book by ID: " + e.getMessage());
      return null;
    }
  }

  public List<Book> searchBooks(List<Integer> authorIds, List<Integer> genreIds) {
    return bookDao.findBooks(authorIds, genreIds);
  }

  public void addBook(Book book) {
    validateBook(book);

    try {
      bookDao.addFullBook(book);
    } catch (Exception e) {
      System.out.println("Error adding book: " + e.getMessage());
    }
  }

  private void validateBook(Book book) {
    if (book == null)
      throw new IllegalArgumentException("Book cannot be null");

    if (book.getTitle() == null || book.getTitle().isEmpty())
      throw new IllegalArgumentException("Book title cannot be null or empty");

    if (book.getPrice() < 0)
      throw new IllegalArgumentException("Book price must be non-negative");

    if (book.getYear() < 0)
      throw new IllegalArgumentException("Book year must be non-negative");
  }
}
