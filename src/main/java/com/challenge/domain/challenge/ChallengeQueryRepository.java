package com.challenge.domain.challenge;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

import static com.challenge.domain.record.QRecord.record;

@RequiredArgsConstructor
@Repository
public class ChallengeQueryRepository {

    private final JPAQueryFactory queryFactory;

    public boolean existsDuplicateRecordBy(Challenge challenge, LocalDate successDate) {
        Long count = queryFactory.select(record.count())
                .from(record)
                .where(record.challenge.eq(challenge)
                        .and(record.successDate.eq(successDate)))
                .fetchOne();

        return count != null && count > 0;
    }

}
