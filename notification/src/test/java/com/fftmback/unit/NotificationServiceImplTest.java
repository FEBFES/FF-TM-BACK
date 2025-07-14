package com.fftmback.unit;

import com.febfes.fftmback.exception.EntityNotFoundException;
import com.fftmback.domain.NotificationEntity;
import com.fftmback.repository.NotificationRepository;
import com.fftmback.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createNotificationSavesEntity() {
        notificationService.createNotification("msg", 1L);
        verify(notificationRepository).save(any(NotificationEntity.class));
    }

    @Test
    void changeIsReadUpdatesEntity() {
        NotificationEntity entity = NotificationEntity.builder().id(1L).build();
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(entity));

        notificationService.changeIsRead(1L, true);

        assertThat(entity.isRead()).isTrue();
        verify(notificationRepository).save(entity);
    }

    @Test
    void deleteNotificationRemovesEntity() {
        NotificationEntity entity = NotificationEntity.builder().id(1L).build();
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(entity));

        notificationService.deleteNotification(1L);

        verify(notificationRepository).deleteById(1L);
    }

    @Test
    void getNotificationByIdThrowsWhenNotFound() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> notificationService.getNotificationById(1L));
    }

    @Test
    void deleteOutdatedNotificationsDeletesRead() {
        List<NotificationEntity> entities = List.of(NotificationEntity.builder().id(1L).build());
        when(notificationRepository.findByIsReadAndCreateDateBefore(eq(true), any(LocalDateTime.class)))
                .thenReturn(entities);

        notificationService.deleteOutdatedNotifications();

        verify(notificationRepository).deleteAll(entities);
    }
}
