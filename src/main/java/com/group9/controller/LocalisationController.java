package com.group9.controller;

import java.util.Locale;
import java.util.ResourceBundle;

public class LocalisationController {
    public ResourceBundle getBundle(String lang, String country) {
        Locale locale = new Locale(lang, country);
        return ResourceBundle.getBundle("LanguageBundle", locale);
    }
    public String getTest(ResourceBundle rb) {
        return rb.getString("testKey");
    }
    public String getStageTitle(ResourceBundle rb) {
        return rb.getString("stageTitle");
    }
    public String getShoppingCartTitle(ResourceBundle rb) {
        return rb.getString("shoppingCartWindowTitle");
    }
    public String getLogoutTitle(ResourceBundle rb) {
        return rb.getString("logoutTitle");
    }
    public String getLogoutConfirmation(ResourceBundle rb) {
        return rb.getString("logoutConfirmation");
    }
    public String getNameLabel(ResourceBundle rb) {
        return rb.getString("nameLabel");
    }
    public String getEmailLabel(ResourceBundle rb) {
        return rb.getString("emailLabel");
    }


}
