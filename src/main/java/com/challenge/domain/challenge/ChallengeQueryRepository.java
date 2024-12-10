package com.challenge.domain.challenge;

import com.challenge.domain.member.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.challenge.domain.challenge.QChallenge.challenge;
import static com.challenge.domain.record.QRecord.record;

@RequiredArgsConstructor
@Repository
public class ChallengeQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<Challenge> findChallengesBy(Member member, LocalDateTime currentDateTime) {
        return queryFactory.selectFrom(challenge)
                .where(challenge.member.eq(member)
                        .and(challenge.isDeleted.eq(false))
                        .and(challenge.startDateTime.loe(currentDateTime))
                        .and(challenge.endDateTime.goe(currentDateTime)))
                .fetch();
    }

    public boolean existsDuplicateRecordBy(Challenge challenge, LocalDate successDate) {
        Long count = queryFactory.select(record.count())
                .from(record)
                .where(record.challenge.eq(challenge)
                        .and(record.successDate.eq(successDate)))
                .fetchOne();

        return count != null && count > 0;
    }

}
