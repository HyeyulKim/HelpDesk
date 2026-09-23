package com.helpdesk.statushistory.mapper;

import com.helpdesk.statushistory.domain.RequestStatusHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RequestStatusHistoryMapper {

    void insertHistory(RequestStatusHistory history);

    List<RequestStatusHistory> findByRequestId(@Param("requestId") Long requestId);
}