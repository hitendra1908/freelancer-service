package com.api.freelancer;

import com.api.freelancer.user.UserResponseDto;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;


public class UsersIntegrationTest extends AbstractIntegrationTest{

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    );

    @Test
    void testSaveUser() {

        UserResponseDto user1 = new UserResponseDto(1L, "testUser", "Tony", "Stark", "tony.stark@example.com", null);

        given()
                .contentType(ContentType.JSON)
                .body(user1)
        .when()
                .post("/users")
        .then()
                .statusCode(200)
                .body(containsString("testUser"), containsString("Tony"));

    }
}
