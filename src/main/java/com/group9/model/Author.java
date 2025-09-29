package com.group9.model;

public class Author extends BookAttribute {
  public Author(int id, String name, String description) {
    super(id, name, description);
  }
  public Author(String name) {
    super(-1, name, null);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Author)) return false;
    Author author = (Author) o;
    return this.getId() == author.getId();
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(this.getId());
  }
}
