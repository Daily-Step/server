package com.challenge.api.validator;

import com.challenge.domain.challenge.Challenge;
import com.challenge.domain.challengeRecord.ChallengeRecord;
import com.challenge.domain.challengeRecord.ChallengeRecordQueryRepository;
import com.challenge.exception.ErrorCode;
import com.challenge.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecordValidator {

    private final ChallengeRecordQueryRepository challengeRecordQueryRepository;

    public ChallengeRecord isLatestRecordSuccessfulBy(Challenge challenge, LocalDate cancelDate) {
        ChallengeRecord challengeRecord = challengeRecordQueryRepository.isLatestRecordSuccessfulBy(challenge,
                cancelDate);

        if (challengeRecord != null) {
            return challengeRecord;
        }

        throw new GlobalException(ErrorCode.LATEST_ACHIEVE_RECORD_NOT_FOUND);
    }

}
