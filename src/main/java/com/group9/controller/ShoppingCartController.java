package com.group9.controller;

import com.group9.model.Book;
import com.group9.util.LayoutOrienter;
import com.group9.util.SessionManager;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.group9.util.SessionManager.getLanguage;

public class ShoppingCartController {

    private ResourceBundle rb;
    private LayoutOrienter orienter = new LayoutOrienter();

    @FXML private AnchorPane shoppingCartAnchor;

    @FXML private VBox cartItems;
    @FXML private Button emptyButton;
    @FXML private Button checkoutButton;
    @FXML private VBox cartVbox; // vbox where items go in cart
    @FXML private Label totalLabel; // label for total amount
    @FXML private Label yourCartLabel; //
    @FXML private Label totalTextLabel;
    @FXML private Label currencyLabel;

    private ObservableList<Book> cart; // ObservableList so that UI updates automatically when changes in cart

    public void initialize() {
        rb = SessionManager.getResourceBundle();
        orienter.orientLayout(shoppingCartAnchor);
        updateUI();
    }

    public void updateUI() {
        yourCartLabel.setText(rb.getString("yourCartLabel"));
        totalTextLabel.setText(rb.getString("totalTextLabel"));
        currencyLabel.setText(rb.getString("currencyLabel"));
        emptyButton.setText(rb.getString("emptyCartButton"));
        checkoutButton.setText(rb.getString("checkoutLabel"));
    }

    public void setCart(ObservableList<Book> cart) {
        this.cart = cart;
        // TODO: populate cartItems VBox with cart contents
        cartVbox.getChildren().clear();
        cartVbox.getChildren().add(new Label("Cart content goes here..."));

        // Listener to refresh UI whenever the cart is modified
        cart.addListener((ListChangeListener<Book>) change -> refreshCartItems());

        refreshCartItems();
        updateTotal();
    }

    // Method to update the cart display to match the current contents of the cart
    private void refreshCartItems() {
        cartVbox.getChildren().clear();
        // Loop through each book in the cart and create Label to display the book's title
        for (Book book : cart) {
            String authors = book.getAuthors()
                    .stream()
                    .map(author -> author.getName())
                    .collect(Collectors.joining(", "));
            Label label = new Label(book.getTitle() + " by " + authors + " - " + currencyPrice(book.getPrice()) + rb.getString("currencyLabel"));
            cartVbox.getChildren().add(label);
        }
    }

    // convert price based on selected language
    private String currencyPrice(double price) {
        String selectedLanguage = getLanguage();
        double convertedPrice = price;

        switch (selectedLanguage) {
            case "English": // euro = dollar
                break;
            case "Japanese":
                convertedPrice = price * 178.68; // 1 Euro = 178 Yen
                break;
            case "Arabic":
                convertedPrice = price * 4.33; // 1 Euro = 4.33 SAR
                break;
            default:
                break;
        }
        String formatted = String.format("%.2f", convertedPrice).replace('.', ',');
        return formatted;
    }

    // calculate total
    private void updateTotal() {
        String selectedLanguage = getLanguage();
        double total = cart.stream().mapToDouble(Book::getPrice).sum();

        switch (selectedLanguage) {
            case "English": // euro = dollar
                totalLabel.setText(String.format("%.2f", total).replace('.', ','));
                break;
            case "Japanese":
                total = total * 178.68; // 1 Euro = 178 Yen
                totalLabel.setText(String.format("%.2f", total).replace('.', ','));
                break;
            case "Arabic":
                total = total * 4.33; // 1 Euro = 4.33 SAR
                totalLabel.setText(String.format("%.2f", total).replace('.', ','));
                break;
            default: // English
                totalLabel.setText(String.format("%.2f", total).replace('.', ','));
                break;

        }
    }

    @FXML
    private void emptyCart() {
        cart.clear();
        System.out.println("Empty cart...");
    }

    @FXML
    private void openCheckoutWindow() {
        System.out.println("Proceed to checkout...");
        rb = SessionManager.getResourceBundle();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/checkout_view.fxml"));
            Parent checkoutRoot = loader.load();

            // get controller of the checkout view
            CheckoutController checkoutController = loader.getController();

            // pass the current cart to the checkout controller
            checkoutController.setCart(cart);

            Stage stage = (Stage) checkoutButton.getScene().getWindow();
            Scene scene = new Scene(checkoutRoot);
            scene.getStylesheets().add(getClass().getResource("/look.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle(rb.getString("checkoutLabel"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


