package com.group9;

import com.group9.dao.BookDao;
import com.group9.model.Book;

public class Main {
  public static void main(String[] args) {
    BookDao bookDao = new BookDao();

    System.out.println("Get book with ID 1:");
    try {
      Book book = bookDao.getBookById(1);
      if (book != null) {
        System.out.println(book.getId() + ": " + book.getTitle());
        System.out.println("  Authors:");
        book.getAuthors().forEach(author -> System.out.println("    " + author.getName()));
        System.out.println("  Genres:");
        book.getGenres().forEach(genre -> System.out.println("    " + genre.getName()));
        System.out.println("  ISBN: " + book.getIsbn());
        System.out.println("  Year: " + book.getYear());
        System.out.println("  Price: $" + book.getPrice());
        System.out.println("  Description: " + book.getDescription());
      } else {
        System.out.println("Book not found.");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println("Books in database:");
    try {
      for (Book book : bookDao.getAllBooks()) {
        System.out.println(book.getId() + ": " + book.getTitle());
        System.out.println("  Authors:");
        book.getAuthors().forEach(author -> System.out.println("    " + author.getName()));
        System.out.println("  Genres:");
        book.getGenres().forEach(genre -> System.out.println("    " + genre.getName()));
        System.out.println("  ISBN: " + book.getIsbn());
        System.out.println("  Year: " + book.getYear());
        System.out.println("  Price: $" + book.getPrice());
        System.out.println("  Description: " + book.getDescription());
        System.out.println();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
