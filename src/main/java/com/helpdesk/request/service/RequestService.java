package com.helpdesk.request.service;

import com.helpdesk.request.domain.Request;
import com.helpdesk.request.domain.RequestPriority;
import com.helpdesk.request.domain.RequestStatus;
import com.helpdesk.request.dto.RequestCreateDto;
import com.helpdesk.request.dto.RequestListItemDto;
import com.helpdesk.request.mapper.RequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {

    private static final int PAGE_SIZE = 10;

    private final RequestMapper requestMapper;

    @Transactional
    public void createRequest(Long requesterId, RequestCreateDto dto) {
        //사용자가 우선순위를 안 골랐으면 기본값(NORMAL)을 채워넣음
        RequestPriority priority = (dto.getPriority() != null) ? dto.getPriority() : RequestPriority.NORMAL;

        Request request = Request.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .status(RequestStatus.WAITING) //상태는 WAITING으로 시작
                .priority(priority)
                .requesterId(requesterId)
                .assigneeId(null)
                .build();

        requestMapper.insertRequest(request);
    }

    public List<RequestListItemDto> getRequests(RequestStatus status, int page) {
        int offset = (page - 1) * PAGE_SIZE;
        return requestMapper.findRequests(status, offset, PAGE_SIZE);
    }

    public int getTotalPages(RequestStatus status) {
        int totalCount = requestMapper.countRequests(status);
        return (int) Math.ceil((double) totalCount / PAGE_SIZE);
    }
}