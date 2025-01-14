package com.challenge.domain.challenge;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ChallengeStatus {

    // - Q. 챌린지 기간이 만료되는 경우, 어떻게 자동으로 챌린지의 상태를 변경할 수 있을까?
    //     A. 스케줄러 라이브러리를 사용하여 매 00시 00분 00초에 챌린지의 상태를 확인하여 챌린지 기간이 만료된 경우 상태를 변경한다.

    // - Q. 챌린지의 상태를 변경하는 로직은 어디에 위치하는 것이 좋을까?
    //     A. 챌린지의 상태를 변경하는 로직은 챌린지 도메인에 위치하는 것이 좋다.

    ONGOING("진행 중"),
    SUCCEED("성공"), // 모든 목표를 달성한 경우
    FAILED("실패"), // 하나라도 목표를 달성하지 못한 경우
    REMOVED("삭제");

    private final String description;
}
