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

    /**
     * 입력 받은 날짜 기준 이전 2달간의 챌린지 목록 조회 (2달전 00:00:00 ~ 입력받은 날짜 23:59:59)
     */
    public List<Challenge> findChallengesBy(Member member, LocalDate targetDate) {
        LocalDateTime startDateTime = targetDate.minusMonths(2).atStartOfDay();
        LocalDateTime endDateTime = targetDate.atTime(23, 59, 59);

        return queryFactory.selectFrom(challenge)
                .where(challenge.member.eq(member)
                        .and(challenge.isDeleted.isFalse())
                        .and(challenge.endDateTime.goe(startDateTime))
                        .and(challenge.startDateTime.loe(endDateTime)))
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
