package com.group9.model;

public class BookAttributeTranslation {
  private String languageCode;
  private String translatedName;
  private String translatedDescription;

  public BookAttributeTranslation(String languageCode, String translatedName, String translatedDescription) {
    this.languageCode = languageCode;
    this.translatedName = translatedName;
    this.translatedDescription = translatedDescription;
  }

    public String getLanguageCode() {
        return languageCode;
    }

    public String getTranslatedName() {
        return translatedName;
    }

    public String getTranslatedDescription() {
        return translatedDescription;
    }
}
