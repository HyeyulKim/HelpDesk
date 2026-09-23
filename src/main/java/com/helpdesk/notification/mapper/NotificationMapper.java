package com.helpdesk.notification.mapper;

import com.helpdesk.notification.domain.Notification;
import com.helpdesk.notification.dto.NotificationDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {

    void insertNotification(Notification notification);

    List<NotificationDto> findRecentByUserId(@Param("userId") Long userId, @Param("limit") int limit);

    int countUnread(@Param("userId") Long userId);

    void markRead(@Param("notificationId") Long notificationId, @Param("userId") Long userId);

    void markAllRead(@Param("userId") Long userId);
}