package com.helpdesk.notification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private Long notificationId;
    private Long userId;      // 알림을 받는 사람
    private Long requestId;
    private String type;      // STATUS_CHANGED, NEW_COMMENT
    private String message;
    private boolean read;
    private LocalDateTime createdAt;
}