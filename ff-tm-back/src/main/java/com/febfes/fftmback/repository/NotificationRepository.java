package com.febfes.fftmback.repository;

import com.febfes.fftmback.domain.dao.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findByUserIdTo(Long userIdTo);

    List<NotificationEntity> findByIsReadAndCreateDateBefore(boolean isRead, LocalDateTime createDateBefore);
}
