package com.fftmback.integration;

import com.fftmback.domain.NotificationEntity;
import com.fftmback.dto.NotificationDto;
import com.fftmback.repository.NotificationRepository;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationControllerTest extends BasicTestClass {

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void getNotificationsForUser() {
        NotificationEntity first = NotificationEntity.builder()
                .userIdTo(userId)
                .message("first")
                .createDate(LocalDateTime.now().minusDays(1))
                .build();
        NotificationEntity second = NotificationEntity.builder()
                .userIdTo(userId)
                .message("second")
                .createDate(LocalDateTime.now())
                .build();
        notificationRepository.saveAll(List.of(first, second));

        List<NotificationDto> result = requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/notifications")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(new TypeRef<>() {});

        assertThat(result).hasSize(2);
        assertThat(result.get(0).message()).isEqualTo("second");
    }

    @Test
    void changeIsRead() {
        NotificationEntity entity = NotificationEntity.builder()
                .userIdTo(userId)
                .message("msg")
                .build();
        entity = notificationRepository.save(entity);

        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .queryParam("isTrue", true)
                .when()
                .patch("/api/v1/notifications/{id}", entity.getId())
                .then()
                .statusCode(HttpStatus.SC_OK);

        NotificationEntity fromDb = notificationRepository.findById(entity.getId()).orElseThrow();
        assertThat(fromDb.isRead()).isTrue();
    }

    @Test
    void deleteNotification() {
        NotificationEntity entity = NotificationEntity.builder()
                .userIdTo(userId)
                .message("del")
                .build();
        entity = notificationRepository.save(entity);

        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/v1/notifications/{id}", entity.getId())
                .then()
                .statusCode(HttpStatus.SC_OK);

        assertThat(notificationRepository.findById(entity.getId())).isEmpty();
    }
}
