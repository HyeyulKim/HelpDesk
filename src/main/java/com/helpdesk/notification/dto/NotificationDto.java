package com.helpdesk.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long notificationId;
    private Long requestId;
    private String requestTitle;
    private String type;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;
}