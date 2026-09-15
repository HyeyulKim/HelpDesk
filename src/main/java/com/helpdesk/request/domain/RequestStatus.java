package com.helpdesk.request.domain;

public enum RequestStatus {
    WAITING, IN_PROGRESS, DONE;

    /**
     * 대기 -> 처리중 -> 완료 순서로 다음 상태를 반환한다.
     * 이미 완료 상태면 null을 반환한다 (더 이상 진행할 상태 없음).
     */
    public RequestStatus next() {
        switch (this) {
            case WAITING:
                return IN_PROGRESS;
            case IN_PROGRESS:
                return DONE;
            default:
                return null;
        }
    }
}