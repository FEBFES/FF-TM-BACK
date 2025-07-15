package com.fftmback.integration;

import com.febfes.fftmback.domain.RoleName;
import com.fftmback.config.KafkaTestMockConfig;
import com.fftmback.config.WebSecurityTestConfig;
import com.fftmback.util.DatabaseCleanup;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.kafka.annotation.KafkaBootstrapConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
@ImportAutoConfiguration(exclude = {KafkaAutoConfiguration.class, KafkaBootstrapConfiguration.class})
@Import({WebSecurityTestConfig.class, KafkaTestMockConfig.class})
public abstract class BasicTestClass {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @LocalServerPort
    private int port;

    @Value("${custom-headers.user-role}")
    private String userRoleHeader;

    @Value("${jwt.secret}")
    private String secret;

    protected Long userId = 1L;
    protected String token;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        token = generateToken(userId);
    }

    @AfterEach
    void cleanup() {
        databaseCleanup.execute();
    }

    protected RequestSpecification requestWithBearerToken() {
        return given()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(userRoleHeader, RoleName.OWNER.name());
    }

    private String generateToken(Long userId) {
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        return Jwts.builder()
                .claim("userId", userId)
                .setSubject("user" + userId)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(Duration.ofHours(1))))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
