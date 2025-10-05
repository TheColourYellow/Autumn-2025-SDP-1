package com.group9.integration;

import com.group9.dao.UserDao;
import com.group9.model.User;
import com.group9.service.UserService;
import com.group9.util.SessionManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.api.FxRobot;

import static org.junit.jupiter.api.Assertions.*;

public class LoginIntegrationTest extends ApplicationTest {
  private UserService userService;
  private UserDao userDao;
  private User testUser;

  @BeforeEach
  void setup() {
    userDao = new UserDao();
    userService = new UserService(userDao);

    // Create a test user in the database
    testUser = userService.registerUser("testUser", "testPassword", "test@email.com");
  }

  @AfterEach
  void teardown() {
    // Clean up test user from database
    if (testUser != null) {
      userDao.deleteUser(testUser.getId());
    }

    userDao = null;
    userService = null;
  }

  @Override
  public void start(Stage stage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login_view.fxml"));
    Scene scene = new Scene(loader.load());
    stage.setScene(scene);
    stage.show();
  }

  @Test
  @Disabled("Disabled until database setup is confirmed")
  void testLogin() {
    FxRobot robot = new FxRobot();

    assertFalse(SessionManager.isLoggedIn());

    robot.clickOn("#usernameField").write(testUser.getUsername());
    robot.clickOn("#passwordField").write("testPassword");
    robot.clickOn("#loginButton");

    assertTrue(SessionManager.isLoggedIn());
    assertEquals(testUser.getUsername(), SessionManager.getCurrentUser().getUsername());
    assertEquals(testUser.getPassword(), SessionManager.getCurrentUser().getPassword());
    assertEquals(testUser.getEmail(), SessionManager.getCurrentUser().getEmail());
  }
}
