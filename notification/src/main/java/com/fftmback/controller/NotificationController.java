package com.fftmback.controller;

import com.febfes.fftmback.annotation.ApiDelete;
import com.febfes.fftmback.annotation.ApiGet;
import com.febfes.fftmback.annotation.ApiPatch;
import com.febfes.fftmback.annotation.ProtectedApi;
import com.fftmback.domain.User;
import com.fftmback.dto.NotificationDto;
import com.fftmback.mapper.NotificationMapper;
import com.fftmback.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public List<NotificationDto> getNotificationsForUser(@AuthenticationPrincipal User user) {
        return notificationMapper.notificationsToDto(notificationService.getNotificationsByUserId(user.id()));
    }

    @Operation(summary = "Change notification isRead parameter")
    @ApiPatch("/{notificationId}")
    public void changeIsRead(@PathVariable Long notificationId, @RequestParam boolean isTrue) {
        notificationService.changeIsRead(notificationId, isTrue);
    }

    @Operation(summary = "Delete notification by id")
    @ApiDelete("/{notificationId}")
    public void deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
    }
}
