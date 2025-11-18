package com.group9.service;

import com.group9.dao.UserDao;
import com.group9.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {
  private UserDao userDao;
  private UserService userService;
  private static final String PASSWORD = "password123";
  private static final String TEST_USER = "testUser";
  private static final String NON_EXISTENT_USER = "nonExistentUser";
  private static final String EMAIL = "test@email.com";

  @BeforeEach
  public void setUp() {
    userDao = mock(UserDao.class);
    userService = new UserService(userDao);
  }

  @Test
  public void testLoginUser() {
    // Invalid inputs
    assertThrows(IllegalArgumentException.class, () -> userService.loginUser("", PASSWORD));
    assertThrows(IllegalArgumentException.class, () -> userService.loginUser(null, PASSWORD));
    assertThrows(IllegalArgumentException.class, () -> userService.loginUser(TEST_USER, ""));
    assertThrows(IllegalArgumentException.class, () -> userService.loginUser(TEST_USER, null));

    // Non-existent user
    when(userDao.getUserByUsername(NON_EXISTENT_USER)).thenReturn(null);
    assertThrows(IllegalArgumentException.class, () -> userService.loginUser(NON_EXISTENT_USER, PASSWORD));
    verify(userDao).getUserByUsername(NON_EXISTENT_USER);

    // Wrong password
    String rawPassword = PASSWORD;
    String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    User existingUser = new User(TEST_USER, hashedPassword, EMAIL);
    when(userDao.getUserByUsername(TEST_USER)).thenReturn(existingUser);
    assertThrows(IllegalArgumentException.class, () -> userService.loginUser(TEST_USER, "wrongPassword"));
    verify(userDao).getUserByUsername(TEST_USER);

    // Successful login
    User result = userService.loginUser(TEST_USER, rawPassword);
    assertEquals(existingUser, result);
  }

  @DisplayName("registerUser should hash password and return user")
  @Test
  public void testRegisterUser() {
    // Test data
    String username = TEST_USER;
    String rawPassword = PASSWORD;
    String email = EMAIL;

    // Mock Dao response, returning the user passed to addUser
    when(userDao.addUser(any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    // Call the method under test
    User result = userService.registerUser(username, rawPassword, email);

    // Verify the returned user
    assertNotNull(result);
    assertEquals(username, result.getUsername());
    assertEquals(email, result.getEmail());

    // Password should be hashed and match the original
    assertNotEquals(rawPassword, result.getPassword());
    assertTrue(BCrypt.checkpw(rawPassword, result.getPassword()));

    // Verify that addUser was called once
    verify(userDao).addUser(any(User.class));

    // Test duplicate username
    when(userDao.getUserByUsername(username)).thenReturn(new User());
    assertThrows(IllegalArgumentException.class, () ->
            userService.registerUser(username, rawPassword, email));
    verify(userDao, times(2)).getUserByUsername(username);

    // Test duplicate email
    when(userDao.getUserByUsername(username)).thenReturn(null);
    when(userDao.getUserByEmail(email)).thenReturn(new User());
    assertThrows(IllegalArgumentException.class, () ->
            userService.registerUser(username, rawPassword, email));
    verify(userDao, times(2)).getUserByEmail(email);
  }

  // Tests for validateUser through registerUser
  @Test
  public void validateUser_EmptyUsername_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () ->
            userService.registerUser("", PASSWORD, EMAIL));
  }

  @Test
  public void validateUser_ShortPassword_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () ->
            userService.registerUser(TEST_USER, "123", EMAIL));
    assertThrows(IllegalArgumentException.class, () ->
            userService.registerUser(TEST_USER, null, EMAIL));
  }

  @Test
  public void validateUser_InvalidEmail_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () ->
            userService.registerUser(TEST_USER, PASSWORD, "invalidEmail"));
  }
}
