package com.api.freelancer;

import com.api.freelancer.freelancer.FreelancerResponseDto;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;


public class FreelancerIntegrationTest extends AbstractIntegrationTest{

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    );

    @Test
    void testSaveUser() {

        FreelancerResponseDto user1 = FreelancerResponseDto.builder()
                .id(1L)
                .userName("testUser")
                .firstName("Tony")
                .lastName("Stark")
                .email("tony.stark@example.com")
                .documents(null)
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(user1)
        .when()
                .post()
        .then()
                .statusCode(200)
                .body(containsString("testUser"), containsString("Tony"));
    }
}
