package com.helpdesk.statistics.service;

import com.helpdesk.statistics.dto.DailyCountDto;
import com.helpdesk.statistics.dto.DashboardDto;
import com.helpdesk.statistics.mapper.StatisticsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private static final int TREND_DAYS = 14;
    private static final DateTimeFormatter LABEL_FORMAT = DateTimeFormatter.ofPattern("MM/dd");

    private final StatisticsMapper statisticsMapper;

    public DashboardDto getDashboard() {
        Map<LocalDate, Integer> registeredMap = toMap(statisticsMapper.dailyRegisteredTrend(TREND_DAYS));
        Map<LocalDate, Integer> completedMap = toMap(statisticsMapper.dailyCompletedTrend(TREND_DAYS));

        List<String> labels = new ArrayList<>();
        List<Integer> registeredCounts = new ArrayList<>();
        List<Integer> completedCounts = new ArrayList<>();

        LocalDate start = LocalDate.now().minusDays(TREND_DAYS - 1);
        for (int i = 0; i < TREND_DAYS; i++) {
            LocalDate date = start.plusDays(i);
            labels.add(date.format(LABEL_FORMAT));
            registeredCounts.add(registeredMap.getOrDefault(date, 0));
            completedCounts.add(completedMap.getOrDefault(date, 0));
        }

        return DashboardDto.builder()
                .totalCount(statisticsMapper.countAll())
                .waitingCount(statisticsMapper.countByStatusName("WAITING"))
                .inProgressCount(statisticsMapper.countByStatusName("IN_PROGRESS"))
                .doneCount(statisticsMapper.countByStatusName("DONE"))
                .unassignedCount(statisticsMapper.countUnassigned())
                .statusCounts(statisticsMapper.countGroupByStatus())
                .priorityCounts(statisticsMapper.countGroupByPriority())
                .assigneeWorkloads(statisticsMapper.countGroupByAssignee())
                .trendLabels(labels)
                .trendRegisteredCounts(registeredCounts)
                .trendCompletedCounts(completedCounts)
                .avgResponseMinutes(statisticsMapper.avgResponseMinutes())
                .avgResolutionMinutes(statisticsMapper.avgResolutionMinutes())
                .build();
    }

    private Map<LocalDate, Integer> toMap(List<DailyCountDto> list) {
        Map<LocalDate, Integer> map = new HashMap<>();
        for (DailyCountDto dto : list) {
            map.put(dto.getDate(), dto.getCount());
        }
        return map;
    }
}