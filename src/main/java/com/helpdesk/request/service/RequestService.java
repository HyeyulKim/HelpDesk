package com.helpdesk.request.service;

import com.helpdesk.request.domain.Request;
import com.helpdesk.request.domain.RequestPriority;
import com.helpdesk.request.domain.RequestStatus;
import com.helpdesk.request.dto.RequestCreateDto;
import com.helpdesk.request.dto.RequestDetailDto;
import com.helpdesk.request.dto.RequestListItemDto;
import com.helpdesk.request.mapper.RequestMapper;
import com.helpdesk.user.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RequestService {

    private static final int PAGE_SIZE = 10;

    private final RequestMapper requestMapper;

    @Transactional
    public void createRequest(Long requesterId, RequestCreateDto dto) {
        RequestPriority priority = (dto.getPriority() != null) ? dto.getPriority() : RequestPriority.NORMAL;

        Request request = Request.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .status(RequestStatus.WAITING)
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

    public List<RequestListItemDto> getMyRequests(Long requesterId, int page) {
        int offset = (page - 1) * PAGE_SIZE;
        return requestMapper.findByRequesterId(requesterId, offset, PAGE_SIZE);
    }

    public int getMyRequestsTotalPages(Long requesterId) {
        int totalCount = requestMapper.countByRequesterId(requesterId);
        return (int) Math.ceil((double) totalCount / PAGE_SIZE);
    }

    public List<RequestListItemDto> getAssignedRequests(Long assigneeId, int page) {
        int offset = (page - 1) * PAGE_SIZE;
        return requestMapper.findByAssigneeId(assigneeId, offset, PAGE_SIZE);
    }

    public int getAssignedRequestsTotalPages(Long assigneeId) {
        int totalCount = requestMapper.countByAssigneeId(assigneeId);
        return (int) Math.ceil((double) totalCount / PAGE_SIZE);
    }

    public RequestDetailDto getRequestDetail(Long requestId) {
        return requestMapper.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 문의입니다. id=" + requestId));
    }

    /**
     * 문의 상태를 다음 단계로 변경한다 (대기 -> 처리중 -> 완료).
     * 담당자(AGENT) 또는 관리자(ADMIN)만 변경할 수 있다.
     */
    @Transactional
    public void advanceStatus(Long requestId, Long currentUserId, Role currentUserRole) {
        if (currentUserRole != Role.AGENT && currentUserRole != Role.ADMIN) {
            throw new AccessDeniedException("담당자 또는 관리자만 상태를 변경할 수 있습니다.");
        }

        RequestDetailDto request = getRequestDetail(requestId);
        RequestStatus nextStatus = request.getStatus().next();
        if (nextStatus == null) {
            throw new IllegalStateException("이미 완료된 문의는 상태를 변경할 수 없습니다.");
        }

        requestMapper.updateStatus(requestId, nextStatus, currentUserId);
    }

    /**
     * 문의를 삭제한다. 작성자 본인 또는 관리자만 삭제할 수 있다.
     */
    @Transactional
    public void deleteRequest(Long requestId, Long currentUserId, Role currentUserRole) {
        RequestDetailDto request = getRequestDetail(requestId);

        boolean isOwner = request.getRequesterId().equals(currentUserId);
        boolean isAdmin = currentUserRole == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("작성자 본인 또는 관리자만 삭제할 수 있습니다.");
        }

        requestMapper.deleteById(requestId);
    }

    /**
     * 수정 폼에 채울 문의를 조회하면서, 수정 가능한 상태인지(작성자 본인 + 대기 상태)도 같이 검증한다.
     */
    public RequestDetailDto getRequestForEdit(Long requestId, Long currentUserId) {
        RequestDetailDto request = getRequestDetail(requestId);
        assertEditable(request, currentUserId);
        return request;
    }

    /**
     * 문의를 수정한다. 작성자 본인만, 그리고 아직 대기(WAITING) 상태일 때만 가능하다.
     * (담당자가 이미 처리를 시작한 문의 내용이 바뀌면 혼란스러우므로)
     */
    @Transactional
    public void updateRequest(Long requestId, Long currentUserId, RequestCreateDto dto) {
        RequestDetailDto request = getRequestDetail(requestId);
        assertEditable(request, currentUserId);

        RequestPriority priority = (dto.getPriority() != null) ? dto.getPriority() : RequestPriority.NORMAL;
        requestMapper.updateContent(requestId, dto.getTitle(), dto.getContent(), priority);
    }

    private void assertEditable(RequestDetailDto request, Long currentUserId) {
        if (!request.getRequesterId().equals(currentUserId)) {
            throw new AccessDeniedException("작성자 본인만 문의를 수정할 수 있습니다.");
        }
        if (request.getStatus() != RequestStatus.WAITING) {
            throw new IllegalStateException("처리가 시작된 문의는 수정할 수 없습니다.");
        }
    }
}