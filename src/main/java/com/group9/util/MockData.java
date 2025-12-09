package com.group9.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group9.dao.BookDao;
import com.group9.model.Author;
import com.group9.model.Book;
import com.group9.model.Genre;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class to populate the database with mock data from a JSON file.
 * For testing and development purposes.
 */
public class MockData {
    private static final Logger log = Logger.getLogger(MockData.class.getName());
  public static void main(String[] args) throws Exception {
    BookDao bookDao = new BookDao();
    InputStream in = MockData.class.getClassLoader().getResourceAsStream("mockData.json");
    ObjectMapper mapper = new ObjectMapper();

    JsonNode root = mapper.readTree(in);
    JsonNode booksNode = root.get("books");

    if (booksNode != null && booksNode.isArray()) {
      for (JsonNode node : booksNode) {
        Book book = parseBook(node);
        bookDao.addFullBookGenreAuthor(book);
        log.log(Level.INFO, "Added book: {0}", book.getTitle());
      }
    }
  }

  private static Book parseBook(JsonNode node) {
    Book book = new Book(
            -1,
            node.get("title").asText(),
            node.get("isbn").asText(),
            node.get("year").asInt(),
            BigDecimal.valueOf(node.get("price").asDouble()).doubleValue(),
            node.has("description") ? node.get("description").asText() : null
    );

    // Convert author names to Author objects (create if not exists)
    List<Author> authors = new ArrayList<>();
    for (JsonNode authorNode : node.withArray("authors")) {
      String name = authorNode.asText();
      authors.add(new Author(name));
    }
    book.setAuthors(authors);

    // Convert genre names to Genre objects (create if not exists)
    List<Genre> genres = new ArrayList<>();
    for (JsonNode genreNode : node.withArray("genres")) {
      String name = genreNode.asText();
      genres.add(new Genre(name));
    }
    book.setGenres(genres);

    return book;
  }
}
