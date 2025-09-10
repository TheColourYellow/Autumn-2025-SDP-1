package com.group9;

import com.group9.dao.BookDao;
import com.group9.dao.UserDao;
import com.group9.model.Book;
import com.group9.model.User;
import com.group9.service.UserService;

public class Main {
  public static void main(String[] args) {
    UserService userService = new UserService(new UserDao());

    String userName = "testuser";
    String password = "password123";
    String email = "test@test.com";

    User user = null;
    try {
      user = userService.registerUser(userName, password, email);
    } catch (IllegalArgumentException e) {
      System.out.println("Error registering user: " + e.getMessage());
    } catch (RuntimeException e) {
      System.out.println("Unexpected error: " + e.getMessage());
    } catch (Exception e) {
      System.out.println("General error: " + e.getMessage());
    }

    // User details
    if (user != null) {
      System.out.println("User details:");
      System.out.println("ID: " + user.getId());
      System.out.println("Username: " + user.getUsername());
      System.out.println("Email: " + user.getEmail());
      System.out.println("Password Hash: " + user.getPassword());
    }

    // Book tests
//    System.out.println("Get book with ID 1:");
//    try {
//      Book book = BookDao.getBookById(1);
//      if (book != null) {
//        System.out.println(book.getId() + ": " + book.getTitle());
//        System.out.println("  Authors:");
//        book.getAuthors().forEach(author -> System.out.println("    " + author.getName()));
//        System.out.println("  Genres:");
//        book.getGenres().forEach(genre -> System.out.println("    " + genre.getName()));
//        System.out.println("  ISBN: " + book.getIsbn());
//        System.out.println("  Year: " + book.getYear());
//        System.out.println("  Price: $" + book.getPrice());
//        System.out.println("  Description: " + book.getDescription());
//      } else {
//        System.out.println("Book not found.");
//      }
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//
//    System.out.println("Books in database:");
//    try {
//      for (Book book : BookDao.getAllBooks()) {
//        System.out.println(book.getId() + ": " + book.getTitle());
//        System.out.println("  Authors:");
//        book.getAuthors().forEach(author -> System.out.println("    " + author.getName()));
//        System.out.println("  Genres:");
//        book.getGenres().forEach(genre -> System.out.println("    " + genre.getName()));
//        System.out.println("  ISBN: " + book.getIsbn());
//        System.out.println("  Year: " + book.getYear());
//        System.out.println("  Price: $" + book.getPrice());
//        System.out.println("  Description: " + book.getDescription());
//        System.out.println();
//      }
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
  }
}
