package com.helpdesk.statistics.dto;

import com.helpdesk.request.domain.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusCountDto { //상태별 건수
    private RequestStatus status;
    private int count;
}