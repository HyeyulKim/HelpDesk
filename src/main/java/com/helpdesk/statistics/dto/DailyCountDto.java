package com.helpdesk.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyCountDto { //일별 건수(등록/완료 추이 공용)
    private LocalDate date;
    private int count;
}