package com.challenge.domain.challenge;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ChallengeStatus {

    ONGOING("진행 중"),
    SUCCEED("성공"),
    EXPIRED("기간 만료"),
    REMOVED("삭제");

    private final String description;
}
