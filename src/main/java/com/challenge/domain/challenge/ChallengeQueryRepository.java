package com.challenge.domain.challenge;

import com.challenge.domain.member.Member;
import com.challenge.utils.date.DateUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.challenge.domain.challenge.QChallenge.challenge;
import static com.challenge.domain.challengeRecord.QChallengeRecord.challengeRecord;

@RequiredArgsConstructor
@Repository
public class ChallengeQueryRepository {

    private final JPAQueryFactory queryFactory;

    // 입력 받은 날짜 기준 이전 2달간의 챌린지 목록 조회 (2달전 00:00:00 ~ 입력받은 날짜 23:59:59)
    public List<Challenge> findChallengesBy(Member member, LocalDate targetDate) {
        LocalDateTime startDateTime = targetDate.minusMonths(2).atStartOfDay();
        LocalDateTime endDateTime = targetDate.atTime(23, 59, 59);

        return queryFactory.selectFrom(challenge)
                .where(challenge.member.eq(member)
                        .and(challenge.status.ne(ChallengeStatus.REMOVED))
                        .and(challenge.endDateTime.goe(startDateTime))
                        .and(challenge.startDateTime.loe(endDateTime)))
                .fetch();
    }

    // 중복된 기록이 존재하는지 조회
    public boolean existsDuplicateRecordBy(Challenge challenge, LocalDate successDate) {
        Long count = queryFactory.select(challengeRecord.count())
                .from(challengeRecord)
                .where(challengeRecord.challenge.eq(challenge)
                        .and(challengeRecord.recordDate.eq(successDate))
                        .and(challengeRecord.isSucceed.eq(true)))
                .fetchOne();

        return count != null && count > 0;
    }

    // 진행중인 챌린지 수 조회 테스트용
    public Long countOngoingChallengesBy(Member member, String targetDateTime) {
        LocalDateTime now = DateUtils.toLocalDateTime(targetDateTime);
        return queryFactory.select(challenge.count())
                .from(challenge)
                .where(challenge.member.eq(member)
                        .and(challenge.status.ne(ChallengeStatus.REMOVED))
                        .and(challenge.endDateTime.goe(now))
                )
                .fetchOne();
    }

    // 진행중인 챌린지 수 조회
    public Long countOngoingChallengesBy(Member member) {
        LocalDateTime now = LocalDateTime.now();
        return queryFactory.select(challenge.count())
                .from(challenge)
                .where(challenge.member.eq(member)
                        .and(challenge.status.ne(ChallengeStatus.REMOVED))
                        .and(challenge.endDateTime.goe(now))
                )
                .fetchOne();
    }

    // 완료된 챌린지 수 조회
    public Long countCompletedChallengesBy(Member member) {
        // 챌린지 기간이 종료 됐고, 삭제되지 않은 챌린지 수 조회
        return null;
    }

    // 전체 챌린지 수 조회
    public Long countAllChallengesBy(Member member) {
        return queryFactory.select(challenge.count())
                .from(challenge)
                .where(challenge.member.eq(member)
                        .and(challenge.status.ne(ChallengeStatus.REMOVED)))
                .fetchOne();
    }

}
