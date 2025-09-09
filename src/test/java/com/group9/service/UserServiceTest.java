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

  @DisplayName("registerUser should hash password and return user")
  @Test
  public void registerUserTest() {
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
  }

  @Test
  public void validateUser_InvalidEmail_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () ->
            userService.registerUser("testUser", "password123", "invalidEmail"));
  }
}
