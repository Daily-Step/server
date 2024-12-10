package com.challenge.domain.record;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecordStatus {
    INIT("초기상태"),
    ACHIEVEMENT_COMPLETED("달성 완료"),
    ACHIEVEMENT_CANCEL("달성 취소");

    private final String description;
}
