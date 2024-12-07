package com.challenge.api.validator;

import com.challenge.domain.challenge.ChallengeRepository;
import com.challenge.exception.ErrorCode;
import com.challenge.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeValidator {

    private final ChallengeRepository challengeRepository;

    public void validateChallengeExists(Long challengeId) {
        boolean exists = challengeRepository.existsById(challengeId);
        if (!exists) {
            throw new GlobalException(ErrorCode.CHALLENGE_NOT_FOUND);
        }
    }

}
