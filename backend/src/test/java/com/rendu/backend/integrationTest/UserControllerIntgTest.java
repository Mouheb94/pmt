package com.rendu.backend.integrationTest;

import com.rendu.backend.controleurs.UserController;
import com.rendu.backend.dto.AuthDto;
import com.rendu.backend.models.User;
import com.rendu.backend.dao.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-Test.properties")
@Commit
public class UserControllerIntgTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    private String authToken;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        // Créer un utilisateur de test et obtenir le token
        User testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setUsername("testuser");
        userRepository.save(testUser);

        AuthDto authDto = new AuthDto();
        authDto.setEmail("test@example.com");
        authDto.setPassword("password");

        ResponseEntity<String> response = restTemplate.postForEntity(
            "/pmt/users/login",
            authDto,
            String.class
        );

        authToken = response.getBody();
    }

    @Test
    void testSignUp() {
        // Arrange
        User newUser = new User();
        newUser.setEmail("newuser@example.com");
        newUser.setPassword("password");
        newUser.setUsername("newuser");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<User> request = new HttpEntity<>(newUser, headers);

        // Act
        ResponseEntity<User> response = restTemplate.postForEntity(
            "/pmt/users/signup",
            request,
            User.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newUser.getEmail(), response.getBody().getEmail());
        assertEquals(newUser.getUsername(), response.getBody().getUsername());
    }

    @Test
    void testGetUserById() {
        // Arrange
        User testUser = userRepository.findByEmail("test@example.com");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        // Act
        ResponseEntity<User> response = restTemplate.exchange(
            "/pmt/users/" + testUser.getId(),
            HttpMethod.GET,
            request,
            User.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testUser.getId(), response.getBody().getId());
        assertEquals(testUser.getEmail(), response.getBody().getEmail());
    }

    @Test
    void testGetCurrentUser() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        // Act
        ResponseEntity<User> response = restTemplate.exchange(
            "/pmt/users/me",
            HttpMethod.GET,
            request,
            User.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test@example.com", response.getBody().getEmail());
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        // Act
        ResponseEntity<User[]> response = restTemplate.exchange(
            "/pmt/users",
            HttpMethod.GET,
            request,
            User[].class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    void testLogin() {
        // Arrange
        AuthDto authDto = new AuthDto();
        authDto.setEmail("test@example.com");
        authDto.setPassword("password");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AuthDto> request = new HttpEntity<>(authDto, headers);

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/pmt/users/login",
            request,
            String.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }
}