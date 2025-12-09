package com.group9.model;

/**
 * Model class representing a translation for a book attribute.
 */
public class BookAttributeTranslation {
  private final String languageCode;
  private final String translatedName;
  private final String translatedDescription;

  /**
   * Constructor for BookAttributeTranslation.
   *
   * @param languageCode       the language code of the translation
   * @param translatedName     the translated name
   * @param translatedDescription the translated description
   */
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
