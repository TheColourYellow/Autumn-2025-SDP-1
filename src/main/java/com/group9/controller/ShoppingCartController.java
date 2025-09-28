package com.group9.controller;

import com.group9.model.Book;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.stream.Collectors;

public class ShoppingCartController {

    @FXML private VBox cartItems;
    @FXML private Button emptyButton;
    @FXML private Button checkoutButton;
    @FXML private VBox cartVbox; // vbox where items go in cart
    @FXML private Label totalLabel; // label for total amount

    private ObservableList<Book> cart; // ObservableList so that UI updates automatically when changes in cart

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
            Label label = new Label(book.getTitle() + " by " + authors + " - " + book.getPrice() + "€");
            cartVbox.getChildren().add(label);
        }
    }

    // calculate total
    private void updateTotal() {
        double total = cart.stream().mapToDouble(Book::getPrice).sum();
        totalLabel.setText(String.format("%.2f", total));
    }

    @FXML
    private void emptyCart() {
        cartVbox.getChildren().clear();
        System.out.println("Empty cart...");
    }

    @FXML
    private void openCheckoutWindow() {
        System.out.println("Proceed to checkout...");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/checkout_view.fxml"));
            Parent checkoutRoot = loader.load();

            // get controller of the checkout view
            CheckoutController checkoutController = loader.getController();

            // pass the current cart to the checkout controller
            checkoutController.setCart(cart);

            Stage stage = (Stage) checkoutButton.getScene().getWindow();
            stage.setScene(new Scene(checkoutRoot));
            stage.setTitle("Checkout");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


