package com.rendu.backend.integrationTest;

import com.rendu.backend.enums.RoleName;
import com.rendu.backend.models.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RoleControllerIntgTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String BASE_URL = "/api/roles";

    @BeforeEach
    @Sql(scripts = {"/test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void setup() {
        // Les données de test sont chargées via le script SQL
    }

    @Test
    public void testGetAllRoles() {
        // Act
        ResponseEntity<List<Role>> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Role>>() {}
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        
        // Vérification des rôles
        List<Role> roles = response.getBody();
        assertTrue(roles.stream().anyMatch(role -> role.getName() == RoleName.ADMIN));
        assertTrue(roles.stream().anyMatch(role -> role.getName() == RoleName.MEMBER));
        assertTrue(roles.stream().anyMatch(role -> role.getName() == RoleName.OBSERVATOR));
    }

    @Test
    @Sql(scripts = {"/cleanup-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testGetAllRoles_EmptyList() {
        // Act
        ResponseEntity<List<Role>> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Role>>() {}
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }
} 