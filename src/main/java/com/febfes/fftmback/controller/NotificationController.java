package com.febfes.fftmback.controller;

import com.febfes.fftmback.annotation.ApiGet;
import com.febfes.fftmback.annotation.ProtectedApi;
import com.febfes.fftmback.dto.NotificationDto;
import com.febfes.fftmback.mapper.NotificationMapper;
import com.febfes.fftmback.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@ProtectedApi
@Tag(name = "Notification")
@RequestMapping("v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    @Operation(summary = "Get notifications for the user they were sent to")
    @ApiGet
    public List<NotificationDto> getNotificationsForUser(
            @RequestParam(value = "userId") long userId,
            @RequestParam(value = "isRead") Optional<Boolean> isRead
    ) {
        if (isRead.isPresent()) {
            return notificationMapper.notificationsToDto(
                    notificationService.getNotificationsByUserIdAndIsRead(userId, isRead.get())
            );
        } else {
            return notificationMapper.notificationsToDto(notificationService.getNotificationsByUserId(userId));
        }
    }
}
