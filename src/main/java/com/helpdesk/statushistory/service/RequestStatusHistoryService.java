package com.helpdesk.statushistory.service;

import com.helpdesk.request.domain.RequestStatus;
import com.helpdesk.statushistory.domain.RequestStatusHistory;
import com.helpdesk.statushistory.mapper.RequestStatusHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestStatusHistoryService {

    private final RequestStatusHistoryMapper requestStatusHistoryMapper;

    /**
     * 문의가 새로 등록되는 순간을 이력으로 남긴다 (이전 상태 없음 → 대기).
     */
    @Transactional
    public void recordCreation(Long requestId, Long requesterId) {
        record(requestId, null, RequestStatus.WAITING, requesterId);
    }

    /**
     * 상태가 바뀔 때마다 이력으로 남긴다.
     */
    @Transactional
    public void recordStatusChange(Long requestId, RequestStatus fromStatus, RequestStatus toStatus, Long changedById) {
        record(requestId, fromStatus, toStatus, changedById);
    }

    private void record(Long requestId, RequestStatus fromStatus, RequestStatus toStatus, Long changedById) {
        RequestStatusHistory history = RequestStatusHistory.builder()
                .requestId(requestId)
                .fromStatus(fromStatus)
                .toStatus(toStatus)
                .changedById(changedById)
                .build();
        requestStatusHistoryMapper.insertHistory(history);
    }

    public List<RequestStatusHistory> getHistory(Long requestId) {
        return requestStatusHistoryMapper.findByRequestId(requestId);
    }
}