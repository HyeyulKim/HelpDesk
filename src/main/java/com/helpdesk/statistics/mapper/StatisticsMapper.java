package com.helpdesk.statistics.mapper;

import com.helpdesk.statistics.dto.AssigneeWorkloadDto;
import com.helpdesk.statistics.dto.DailyCountDto;
import com.helpdesk.statistics.dto.PriorityCountDto;
import com.helpdesk.statistics.dto.StatusCountDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StatisticsMapper {

    int countAll();

    int countByStatusName(@Param("status") String status);

    int countUnassigned();

    List<StatusCountDto> countGroupByStatus();

    List<PriorityCountDto> countGroupByPriority();

    List<AssigneeWorkloadDto> countGroupByAssignee();

    List<DailyCountDto> dailyRegisteredTrend(@Param("sinceDays") int sinceDays);

    List<DailyCountDto> dailyCompletedTrend(@Param("sinceDays") int sinceDays);

    Double avgResponseMinutes();

    Double avgResolutionMinutes();
}