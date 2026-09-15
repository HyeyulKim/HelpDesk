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
public class RequestDetailDto {
    private Long requestId;
    private String title;
    private String content;
    private RequestStatus status;
    private RequestPriority priority;
    private Long requesterId;      // 권한 체크(삭제 가능 여부)에 필요해서 이름뿐 아니라 id도 포함
    private String requesterName;
    private String assigneeName;   // 미배정이면 null
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}