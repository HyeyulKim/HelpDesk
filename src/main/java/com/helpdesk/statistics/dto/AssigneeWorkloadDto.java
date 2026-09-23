package com.helpdesk.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssigneeWorkloadDto { //담당자별 처리 건수
    private String assigneeName;
    private int count;
}