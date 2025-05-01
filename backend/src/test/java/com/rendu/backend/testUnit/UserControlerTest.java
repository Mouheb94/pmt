package com.rendu.backend.testUnit;

import com.rendu.backend.controleurs.UserController;
import com.rendu.backend.dto.AuthDto;
import com.rendu.backend.models.User;
import com.rendu.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserControlerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignUp() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("test@example.com");
        
        when(userService.createUser(any(User.class))).thenReturn(savedUser);

        // Act
        ResponseEntity<User> response = userController.signUp(user);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(savedUser, response.getBody());
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void testGetUserById() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        
        when(userService.getUserById(userId)).thenReturn(user);

        // Act
        ResponseEntity<User> response = userController.getUserById(userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void testGetCurrentUser() {
        // Arrange
        String token = "Bearer testToken";
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        
        when(userService.getUserByToken(token)).thenReturn(user);

        // Act
        ResponseEntity<User> response = userController.getCurrentUser(token);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).getUserByToken(token);
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("test1@example.com");
        
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("test2@example.com");
        
        List<User> users = Arrays.asList(user1, user2);
        
        when(userService.getAllUsers()).thenReturn(users);

        // Act
        ResponseEntity<List<User>> response = userController.getAllUsers();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals(users, response.getBody());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testLogin() {
        // Arrange
        AuthDto authDto = new AuthDto();
        authDto.setEmail("test@example.com");
        authDto.setPassword("password");
        
        String expectedToken = "testToken";
        
        when(userService.logIn(authDto.getEmail(), authDto.getPassword())).thenReturn(expectedToken);

        // Act
        ResponseEntity<String> response = userController.login(authDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedToken, response.getBody());
        verify(userService, times(1)).logIn(authDto.getEmail(), authDto.getPassword());
    }
}
