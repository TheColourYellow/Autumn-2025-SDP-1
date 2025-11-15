package com.group9.controller;

import com.group9.dao.OrderDao;
import com.group9.model.Author;
import com.group9.model.Book;
import com.group9.model.Order;
import com.group9.model.OrderItem;
import com.group9.service.OrderService;
import com.group9.util.LayoutOrienter;
import com.group9.util.SessionManager;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.group9.util.SessionManager.getLanguage;

public class CheckoutController {

    private LayoutOrienter orienter = new LayoutOrienter();
    private static final Logger log = Logger.getLogger(CheckoutController.class.getName());

    @FXML private AnchorPane checkoutAnchor;
    @FXML private Button orderButton;
    @FXML private Button returnButton; // return to shopping cart
    @FXML private TextField cardNumberField; // text field for card number
    @FXML private Label totalLabel; // label for total amount
    @FXML private VBox checkoutBox; // vbox for checkout items
    @FXML private ImageView visaImage;
    @FXML private ImageView mastercardImage;

    @FXML private Label currencyLabel;
    @FXML private Label totalTextLabel;
    @FXML private Label checkoutTitleLabel;
    @FXML private Label selectPaymentLabel;


    // selected card
    private ImageView selectedCard = null;
    private static final String NORMAL_STYLE = "-fx-effect: dropshadow(gaussian, gray, 5, 0.3, 0, 0); -fx-cursor: hand;";
    private static final String SELECTED_STYLE = "-fx-effect: dropshadow(gaussian, blue, 15, 0.7, 0, 0); -fx-border-color: blue; -fx-border-width: 3; -fx-cursor: hand;";

    private ObservableList<Book> cart; // holds cart items
    private ResourceBundle rb;

    @FXML
    public void initialize() {
        rb = SessionManager.getResourceBundle();
        orienter.orientLayout(checkoutAnchor);
        updateUI();
        // initial style and click handler
        visaImage.setStyle(NORMAL_STYLE);
        mastercardImage.setStyle(NORMAL_STYLE);

        visaImage.setOnMouseClicked(e -> selectCard(visaImage));
        mastercardImage.setOnMouseClicked(e -> selectCard(mastercardImage));

        // move focus away
        Platform.runLater(() -> cardNumberField.getParent().requestFocus());
    }

    public void updateUI() {
        checkoutTitleLabel.setText(rb.getString("checkoutLabel"));
        totalTextLabel.setText(rb.getString("totalTextLabel"));
        currencyLabel.setText(rb.getString("currencyLabel"));
        selectPaymentLabel.setText(rb.getString("selectPaymentLabel"));
        orderButton.setText(rb.getString("placeOrderButton"));
        returnButton.setText(rb.getString("returnToCartButton"));
        cardNumberField.setPromptText(rb.getString("cardNumberField"));
    }

    // called by ShoppingCartController to pass the cart items
    public void setCart(ObservableList<Book> cart) {
        this.cart = cart;
        refreshCheckoutItems();
        updateTotal();
    }

    // show cart items
    private void refreshCheckoutItems() {
        checkoutBox.getChildren().clear();
        for (Book book : cart) {
            // join author names
            String authors = book.getAuthors()
                    .stream()
                    .map(Author::getName)
                    .collect(Collectors.joining(", "));
            Label label = new Label(book.getTitle() + " by " + authors + " - " +  currencyPrice(book.getPrice()) + rb.getString("currencyLabel"));
            checkoutBox.getChildren().add(label);
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
        return String.format("%.2f", convertedPrice).replace('.', ',');
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

    private void selectCard(ImageView card) {
        if (selectedCard != null) {
            selectedCard.setStyle(NORMAL_STYLE);
        }
        selectedCard = card;
        selectedCard.setStyle(SELECTED_STYLE);
    }

    @FXML
    private void placeOrder() {
        boolean success = false;

        if (selectedCard == null) {
            log.info("No card selected!");
            return;
        }

        String cardNumber = cardNumberField.getText().trim();
        if (cardNumber.isEmpty()) {
            log.info("Card number cannot be empty!");
            return;
        }

        cardNumber = cardNumber.replaceAll("\\s+", "");
        if (!cardNumber.matches("\\d{16}")) {
            log.info("Card number must be 16 digits!");
            return;
        }

        String selectedCardType = selectedCard == visaImage ? "Visa" : "MasterCard";
        log.log(Level.INFO, "Placing order with {0} card number: {1}", new Object[]{selectedCardType, cardNumber});

        // if the user is logged in, save the order
        if (SessionManager.isLoggedIn()) {
            // create OrderItems from cart
            List<OrderItem> orderItems = cart.stream()
                    .map(book -> new OrderItem(-1, -1, book, 1)) // default quantity = 1
                    .collect(Collectors.toList());

            // create Order for current user
            Order order = new Order(-1, SessionManager.getCurrentUser().getId(), orderItems);

            // save Order to database
            OrderService orderService = new OrderService(new OrderDao());
            int orderId = orderService.createOrder(order);
            log.log(Level.INFO, "Created order with ID: {0}", orderId);
        } else {
            log.info("User not logged in, skipping database save.");
        }

        // Switch to accept view
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/accept_view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) orderButton.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.sizeToScene();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (success && cart != null) {
            cart.clear(); // clear cart after successful order
            log.info("Cart cleared after payment.");
        }
    }

    @FXML
    private void returnToCart() {
        log.info("Return to cart...");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/shopping_cart_view.fxml"));
            Parent cartRoot = loader.load();

            // Get the controller of the shopping cart
            ShoppingCartController cartController = loader.getController();

            // Pass the current cart back to it
            cartController.setCart(cart);

            // Set the scene
            Stage stage = (Stage) returnButton.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(cartRoot));
            stage.setTitle("Shopping Cart");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
