package com.helpdesk.statushistory.domain;

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
public class RequestStatusHistory {
    private Long historyId;
    private Long requestId;
    private RequestStatus fromStatus;   // 문의 등록 시점은 null (이전 상태가 없음)
    private RequestStatus toStatus;
    private Long changedById;
    private LocalDateTime changedAt;
}