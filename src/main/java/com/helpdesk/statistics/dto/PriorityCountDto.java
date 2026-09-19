package com.helpdesk.statistics.dto;

import com.helpdesk.request.domain.RequestPriority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriorityCountDto { //우선순위별 건수
    private RequestPriority priority;
    private int count;
}