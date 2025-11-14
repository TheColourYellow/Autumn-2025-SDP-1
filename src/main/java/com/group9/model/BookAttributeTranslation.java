package com.group9.model;

public class BookAttributeTranslation {
  public String languageCode;
  public String translatedName;
  public String translatedDescription;

  public BookAttributeTranslation(String languageCode, String translatedName, String translatedDescription) {
    this.languageCode = languageCode;
    this.translatedName = translatedName;
    this.translatedDescription = translatedDescription;
  }
}
