package com.helpdesk.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto { //전체 대시보드 데이터를 담는 최상위 DTO
    // 요약 카드
    private int totalCount;
    private int waitingCount;
    private int inProgressCount;
    private int doneCount;
    private int unassignedCount;

    // 분포 차트
    private List<StatusCountDto> statusCounts; //상태별 건수
    private List<PriorityCountDto> priorityCounts; //우선순위별 건수
    private List<AssigneeWorkloadDto> assigneeWorkloads; //담당자별 처리 건수

    // 추이 차트 (최근 14일, 0으로 채워진 연속 날짜 배열)
    private List<String> trendLabels;
    private List<Integer> trendRegisteredCounts;
    private List<Integer> trendCompletedCounts;

    // 처리 시간 (분 단위, 데이터 없으면 null)
    private Double avgResponseMinutes;    // 등록 -> 처리시작
    private Double avgResolutionMinutes;  // 처리시작 -> 완료
}