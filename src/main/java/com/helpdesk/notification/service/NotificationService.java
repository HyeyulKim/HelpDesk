package com.helpdesk.notification.service;

import com.helpdesk.notification.domain.Notification;
import com.helpdesk.notification.dto.NotificationDto;
import com.helpdesk.notification.mapper.NotificationMapper;
import com.helpdesk.request.domain.RequestStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final int RECENT_LIMIT = 20;

    private final NotificationMapper notificationMapper;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 상태 변경 알림: 담당자가 상태를 바꾸면 요청자에게 알림.
     */
    @Transactional
    public void notifyStatusChange(Long requestId, Long recipientUserId, String requestTitle, String actorName, RequestStatus newStatus) {
        if (recipientUserId == null) {
            return;
        }
        String message = (newStatus == RequestStatus.IN_PROGRESS)
                ? actorName + "님이 '" + requestTitle + "' 문의 처리를 시작했습니다."
                : actorName + "님이 '" + requestTitle + "' 문의를 완료했습니다.";
        create(requestId, recipientUserId, "STATUS_CHANGED", message);
    }

    /**
     * 댓글 알림: 요청자가 쓰면 담당자에게, 담당자가 쓰면 요청자에게.
     */
    @Transactional
    public void notifyNewComment(Long requestId, Long recipientUserId, String requestTitle) {
        if (recipientUserId == null) {
            return;
        }
        create(requestId, recipientUserId, "NEW_COMMENT", "'" + requestTitle + "' 문의에 새 댓글이 달렸습니다.");
    }

    private void create(Long requestId, Long recipientUserId, String type, String message) {
        Notification notification = Notification.builder()
                .userId(recipientUserId)
                .requestId(requestId)
                .type(type)
                .message(message)
                .build();

        notificationMapper.insertNotification(notification);

        // DB 저장이 끝난 직후, 그 사용자가 구독 중인 웹소켓 토픽으로 즉시 push
        // "/topic/notifications-{userId}" : 사용자마다 고유한 개인 채널
        messagingTemplate.convertAndSend(
                "/topic/notifications-" + recipientUserId,
                Map.of("requestId", requestId, "message", message)
        );
    }

    public List<NotificationDto> getRecentNotifications(Long userId) {
        return notificationMapper.findRecentByUserId(userId, RECENT_LIMIT);
    }

    public int getUnreadCount(Long userId) {
        return notificationMapper.countUnread(userId);
    }

    @Transactional
    public void markRead(Long notificationId, Long userId) {
        notificationMapper.markRead(notificationId, userId);
    }

    @Transactional
    public void markAllRead(Long userId) {
        notificationMapper.markAllRead(userId);
    }
}