package com.fftmback.authentication.feign;

import com.febfes.fftmback.domain.RoleName;
import lombok.NonNull;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ContextConfiguration(initializers = RoleClientTest.MockServerInitializer.class)
@ActiveProfiles("test")
class RoleClientTest {

    static MockWebServer mockWebServer;

    @Autowired
    private RoleClient roleClient;

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void getUserRoleNameOnProjectReturnsEnum() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("\"OWNER\"")
                .addHeader("Content-Type", "application/json"));

        RoleName role = roleClient.getUserRoleNameOnProject(1L, 2L);

        assertThat(role).isEqualTo(RoleName.OWNER);
    }

    static class MockServerInitializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(@NonNull ConfigurableApplicationContext context) {
            mockWebServer = new MockWebServer();
            try {
                mockWebServer.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            TestPropertyValues.of(
                    "ff-tm-back.url=http://localhost:" + mockWebServer.getPort()
            ).applyTo(context.getEnvironment());
        }
    }
}
