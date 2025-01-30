package com.fftmback.controller;

import com.fftmback.annotation.ApiDelete;
import com.fftmback.annotation.ApiGet;
import com.fftmback.annotation.ApiPatch;
import com.fftmback.annotation.ProtectedApi;
import com.fftmback.domain.User;
import com.fftmback.dto.NotificationDto;
import com.fftmback.mapper.NotificationMapper;
import com.fftmback.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public List<NotificationDto> getNotificationsForUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return notificationMapper.notificationsToDto(notificationService.getNotificationsByUserId(user.id()));
    }

//    @Operation(summary = "Get notifications for the user they were sent to")
//    @ApiGet("/projects/{projectId}")
//    @PreAuthorize("hasAuthority('OWNER')")
//    public List<NotificationDto> getNotificationsForUserProject(@PathVariable String projectId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Long userId = (Long) authentication.getPrincipal();
//        return notificationMapper.notificationsToDto(notificationService.getNotificationsByUserId(userId));
//    }

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
