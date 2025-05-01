package com.rendu.backend.integrationTest;

import com.rendu.backend.models.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class NotificationControllerIntgTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String BASE_URL = "/api/notifications";

    @BeforeEach
    @Sql(scripts = {"/test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void setup() {
        // Les données de test sont chargées via le script SQL
    }

    @Test
    public void testSendNotification() {
        // Arrange
        Long userId = 1L;
        String message = "Test notification message";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(message, headers);

        // Act
        ResponseEntity<Notification> response = restTemplate.exchange(
                BASE_URL + "/send/{userId}",
                HttpMethod.POST,
                request,
                Notification.class,
                userId
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(message, response.getBody().getMessage());
        assertFalse(response.getBody().isReadNot());
        assertNotNull(response.getBody().getUser());
        assertEquals(userId, response.getBody().getUser().getId());
    }

    @Test
    public void testGetUserNotifications() {
        // Arrange
        Long userId = 1L;

        // Act
        ResponseEntity<List<Notification>> response = restTemplate.exchange(
                BASE_URL + "/user/{userId}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Notification>>() {},
                userId
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        
        // Vérification des notifications de l'utilisateur
        List<Notification> notifications = response.getBody();
        notifications.forEach(notification -> {
            assertNotNull(notification.getMessage());
            assertNotNull(notification.getUser());
            assertEquals(userId, notification.getUser().getId());
        });
    }

    @Test
    public void testMarkAsRead() {
        // Arrange
        Long notificationId = 1L;

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(
                BASE_URL + "/{id}/read",
                HttpMethod.PUT,
                null,
                Void.class,
                notificationId
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Vérification que la notification est marquée comme lue
        ResponseEntity<Notification> getResponse = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.GET,
                null,
                Notification.class,
                notificationId
        );

        assertNotNull(getResponse);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertTrue(getResponse.getBody().isReadNot());
    }

    @Test
    public void testGetUserNotifications_NoNotifications() {
        // Arrange
        Long userId = 999L; // ID d'un utilisateur sans notifications

        // Act
        ResponseEntity<List<Notification>> response = restTemplate.exchange(
                BASE_URL + "/user/{userId}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Notification>>() {},
                userId
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    public void testMarkAsRead_NotFound() {
        // Arrange
        Long notificationId = 999L; // ID d'une notification inexistante

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(
                BASE_URL + "/{id}/read",
                HttpMethod.PUT,
                null,
                Void.class,
                notificationId
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
} 