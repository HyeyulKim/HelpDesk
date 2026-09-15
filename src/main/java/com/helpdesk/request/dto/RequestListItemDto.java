package com.helpdesk.request.dto;

import com.helpdesk.request.domain.RequestPriority;
import com.helpdesk.request.domain.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestListItemDto { //문의 목록 화면에 한 줄로 보여줄 정보를 담는 그릇
    private Long requestId;
    private String title;
    private RequestStatus status;
    private RequestPriority priority;
    private String requesterName;
    private String assigneeName; // 미배정이면 null
    private LocalDateTime createdAt;
}