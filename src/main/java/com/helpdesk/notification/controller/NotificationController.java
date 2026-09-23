package com.helpdesk.notification.controller;

import com.helpdesk.notification.dto.NotificationDto;
import com.helpdesk.notification.service.NotificationService;
import com.helpdesk.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationDto> list(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return notificationService.getRecentNotifications(userDetails.getUser().getUserId());
    }

    @GetMapping("/unread-count")
    public Map<String, Integer> unreadCount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return Map.of("count", notificationService.getUnreadCount(userDetails.getUser().getUserId()));
    }

    @PostMapping("/read-all")
    public void markAllRead(@AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationService.markAllRead(userDetails.getUser().getUserId());
    }
}