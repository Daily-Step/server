package com.challenge.api.validator;

import com.challenge.domain.challenge.ChallengeRepository;
import com.challenge.exception.ErrorCode;
import com.challenge.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ChallengeValidator {

    private final ChallengeRepository challengeRepository;

    /**
     * 챌린지 제목 중복 검증을 한다.
     *
     * @param memberId 회원 아이디
     * @param title    챌린지 제목
     */
    public void validateDuplicateTitle(Long memberId, String title) {
        boolean existsByTitle = challengeRepository.existsByMemberIdAndTitle(memberId, title);
        if (existsByTitle) {
            throw new GlobalException(ErrorCode.CHALLENGE_DUPLICATE_TITLE);
        }
    }

}
