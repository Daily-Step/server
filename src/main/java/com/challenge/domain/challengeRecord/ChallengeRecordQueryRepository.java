package com.challenge.domain.challengeRecord;

import com.challenge.domain.challenge.Challenge;
import com.challenge.exception.ErrorCode;
import com.challenge.exception.GlobalException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

import static com.challenge.domain.challengeRecord.QChallengeRecord.challengeRecord;

@RequiredArgsConstructor
@Repository
public class ChallengeRecordQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ChallengeRecord findLatestRecordBy(Challenge challenge, LocalDate cancelDate) {
        ChallengeRecord latestRecord = queryFactory.selectFrom(challengeRecord)
                .where(challengeRecord.challenge.eq(challenge)
                        .and(challengeRecord.recordDate.eq(cancelDate))
                )
                .orderBy(challengeRecord.createdAt.desc())
                .fetchFirst();

        if (latestRecord != null && latestRecord.isSucceed()) {
            return latestRecord;
        }

        throw new GlobalException(ErrorCode.LATEST_ACHIEVE_RECORD_NOT_FOUND);
    }

}
