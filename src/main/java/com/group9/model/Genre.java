package com.group9.model;

public class Genre extends BookAttribute {
  public Genre(int id, String name, String description) {
    super(id, name, description);
  }
  public Genre(String name) {
    super(-1, name, null);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Genre)) return false;
    Genre genre = (Genre) o;
    return this.getId() == genre.getId();
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(this.getId());
  }
}
