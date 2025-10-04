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

  @BeforeEach
  public void setUp() {
    userDao = mock(UserDao.class);
    userService = new UserService(userDao);
  }

  @Test
  public void testLoginUser() {
    // Invalid inputs
    assertThrows(IllegalArgumentException.class, () -> userService.loginUser("", "password123"));
    assertThrows(IllegalArgumentException.class, () -> userService.loginUser(null, "password123"));
    assertThrows(IllegalArgumentException.class, () -> userService.loginUser("testUser", ""));
    assertThrows(IllegalArgumentException.class, () -> userService.loginUser("testUser", null));

    // Non-existent user
    when(userDao.getUserByUsername("nonExistentUser")).thenReturn(null);
    assertThrows(IllegalArgumentException.class, () -> userService.loginUser("nonExistentUser", "password123"));
    verify(userDao).getUserByUsername("nonExistentUser");

    // Wrong password
    String rawPassword = "password123";
    String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    User existingUser = new User("testUser", hashedPassword, "test@email.com");
    when(userDao.getUserByUsername("testUser")).thenReturn(existingUser);
    assertThrows(IllegalArgumentException.class, () -> userService.loginUser("testUser", "wrongPassword"));
    verify(userDao).getUserByUsername("testUser");

    // Successful login
    User result = userService.loginUser("testUser", rawPassword);
    assertEquals(existingUser, result);
  }

  @DisplayName("registerUser should hash password and return user")
  @Test
  public void testRegisterUser() {
    // Test data
    String username = "testUser";
    String rawPassword = "password123";
    String email = "test@email.com";

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
            userService.registerUser("", "password123", "test@email.com"));
  }

  @Test
  public void validateUser_ShortPassword_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () ->
            userService.registerUser("testUser", "123", "test@email.com"));
    assertThrows(IllegalArgumentException.class, () ->
            userService.registerUser("testUser", null, "test@email.com"));
  }

  @Test
  public void validateUser_InvalidEmail_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () ->
            userService.registerUser("testUser", "password123", "invalidEmail"));
  }
}
