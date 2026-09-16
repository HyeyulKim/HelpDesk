package com.helpdesk.request.mapper;

import com.helpdesk.request.domain.Request;
import com.helpdesk.request.domain.RequestStatus;
import com.helpdesk.request.dto.RequestDetailDto;
import com.helpdesk.request.dto.RequestListItemDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RequestMapper {

    void insertRequest(Request request);

    List<RequestListItemDto> findRequests(
            @Param("status") RequestStatus status,
            @Param("offset") int offset,
            @Param("size") int size
    );

    int countRequests(@Param("status") RequestStatus status);

    Optional<RequestDetailDto> findById(@Param("requestId") Long requestId);

    void updateStatus(
            @Param("requestId") Long requestId,
            @Param("status") RequestStatus status,
            @Param("assigneeId") Long assigneeId
    );

    void deleteById(@Param("requestId") Long requestId);

    List<RequestListItemDto> findByRequesterId(
            @Param("requesterId") Long requesterId,
            @Param("offset") int offset,
            @Param("size") int size
    );

    int countByRequesterId(@Param("requesterId") Long requesterId);

    List<RequestListItemDto> findByAssigneeId(
            @Param("assigneeId") Long assigneeId,
            @Param("offset") int offset,
            @Param("size") int size
    );

    int countByAssigneeId(@Param("assigneeId") Long assigneeId);
}