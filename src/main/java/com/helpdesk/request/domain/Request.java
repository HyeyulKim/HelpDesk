package com.helpdesk.request.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    private Long requestId;
    private String title;
    private String content;
    private RequestStatus status;
    private RequestPriority priority;
    private Long requesterId;
    private Long assigneeId;   // 미배정 시 null
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
