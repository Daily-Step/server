package com.challenge.api.validator;

import com.challenge.domain.challenge.Challenge;
import com.challenge.domain.challenge.ChallengeQueryRepository;
import com.challenge.domain.challenge.ChallengeRepository;
import com.challenge.exception.ErrorCode;
import com.challenge.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeValidator {

    private final ChallengeRepository challengeRepository;
    private final ChallengeQueryRepository challengeQueryRepository;

    public void validateChallengeExists(Long challengeId) {
        boolean exists = challengeRepository.existsById(challengeId);
        if (!exists) {
            throw new GlobalException(ErrorCode.CHALLENGE_NOT_FOUND);
        }
    }

    public void validateDuplicateRecordBy(Challenge challenge, LocalDate currentDate) {
        boolean exists = challengeQueryRepository.existsDuplicateRecordBy(challenge, currentDate);
        if (exists) {
            throw new GlobalException(ErrorCode.DUPLICATE_RECORD);
        }
    }

}
