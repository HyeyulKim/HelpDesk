package com.helpdesk.request.mapper;

import com.helpdesk.request.domain.Request;
import com.helpdesk.request.domain.RequestStatus;
import com.helpdesk.request.dto.RequestListItemDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RequestMapper {

    void insertRequest(Request request); //문의 1건 저장

    List<RequestListItemDto> findRequests( //상태 필터 + 페이징된 목록 조회
            @Param("status") RequestStatus status,
            @Param("offset") int offset,
            @Param("size") int size
    );

    int countRequests(@Param("status") RequestStatus status); //페이징 계산에 필요한 전체 건수 조회
}