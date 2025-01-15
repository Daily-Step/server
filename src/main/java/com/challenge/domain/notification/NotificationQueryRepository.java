package com.challenge.domain.notification;

import com.challenge.api.service.notification.AchieveChallengeDTO;
import com.challenge.domain.challenge.ChallengeStatus;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.challenge.domain.challenge.QChallenge.challenge;
import static com.challenge.domain.member.QMember.member;

@RequiredArgsConstructor
@Repository
public class NotificationQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 진행중인 챌린지가 없는 회원 token 및 닉네임 조회
     *
     * @return
     */
    public Map<String, String> getNewChallengeTargets() {
        LocalDateTime now = LocalDateTime.now();

        // ONGOING 상태인 challenge 개수가 0개인 member의 token, nickname 조회
        List<Tuple> result = queryFactory
                .select(member.fcmToken,
                        member.nickname)
                .from(member)
                .leftJoin(challenge)
                .on(challenge.member.id.eq(member.id)
                        .and(challenge.status.eq(ChallengeStatus.ONGOING)))
                .groupBy(member.id, member.fcmToken, member.nickname)
                .having(challenge.id.count().eq(0L))
                .fetch();

        // 결과를 Map<String,String> 형태로 변환
        Map<String, String> resultMap = new HashMap<>();
        for (Tuple tuple : result) {
            String token = tuple.get(member.fcmToken);
            String nickname = tuple.get(member.nickname);
            resultMap.put(token, nickname);
        }

        return resultMap;
    }

    /**
     * 현재 시각 기준 달성할 챌린지가 있는 회원 token, 닉네임, 챌린지 제목 리스트 조회
     *
     * @return
     */
    public Map<String, AchieveChallengeDTO> getAchieveTargetsAndChallenge() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // status=ONGOING -> 진행중
        // 해당 챌린지의 마지막 기록이 없거나 isSucceed=false -> 달성 가능
        // 그 챌린지의 title, member.fcmToken, member.nickname 조회
        List<Tuple> result = queryFactory
                .select(member.fcmToken,
                        member.nickname,
                        challenge.title)
                .from(challenge)
                .join(member).on(challenge.member.id.eq(member.id))
                .where(challenge.status.eq(ChallengeStatus.ONGOING),
                        lastRecordSucceed(today).eq(false))
                .fetch();

        // 결과를 Map<String, AchieveChallengeDTO> 형태로 변환
        Map<String, AchieveChallengeDTO> resultMap = new HashMap<>();
        for (Tuple tuple : result) {
            String token = tuple.get(member.fcmToken);
            String nickname = tuple.get(member.nickname);
            String title = tuple.get(challenge.title);

            AchieveChallengeDTO dto = resultMap.getOrDefault(
                    token,
                    AchieveChallengeDTO.builder()
                            .nickname(nickname)
                            .challengeTitles(new ArrayList<>())
                            .build());

            dto.getChallengeTitles().add(title);
            resultMap.put(token, dto);
        }

        return resultMap;
    }

    /**
     * 해당 일자의 마지막 ChallengeRecord.isSucceeds를 반환, ChallengeRecord가 없는 경우 false를 반환
     *
     * @param day 일자
     * @return
     */
    private BooleanExpression lastRecordSucceed(LocalDate day) {
        return Expressions.booleanTemplate(
                "COALESCE((" +
                        " SELECT r.is_succeed" +
                        "   FROM challenge_record r" +
                        "  WHERE r.challenge_id = {0}" +
                        "    AND r.record_date = {1}" +
                        "  ORDER BY r.created_at DESC" +
                        "  LIMIT 1" +
                        "), FALSE)",  // 기록이 없으면 null -> false 로 변환
                challenge.id, // {0}
                day           // {1}
        );
    }

}
