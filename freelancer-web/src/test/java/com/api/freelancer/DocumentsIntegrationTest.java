package com.api.freelancer;

import com.api.freelancer.model.Users;
import com.api.freelancer.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

public class DocumentsIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    );

    @Container
    static final KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.6.1")
    );

    @DynamicPropertySource
    static void registerKafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Test
    void testSaveDocument() {
        //creating a user before uploading document for that user
        userRepository.deleteAll();
        Users validUser = Users.builder()
                .userName("testUser")
                .firstName("Tony")
                .lastName("Stark")
                .email("tony.stark@example.com")
                .build();
        userRepository.save(validUser);

        File json = new File("src/test/resources/documentRequest.json");

        given()
                .multiPart("request", json, "application/json")
                .multiPart("file", "testUser_document.pdf", "test content".getBytes())
        .when()
                .post("/documents")
        .then()
                .statusCode(200)
                .body(containsString("testUser_document"),
                        containsString("testUser"),
                        containsString("TYPE1"));
    }
}
