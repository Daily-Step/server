package com.challenge.domain.notification;

import com.challenge.api.service.notification.AchieveChallengeDTO;
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

@RequiredArgsConstructor
@Repository
public class NotificationQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Map<String, AchieveChallengeDTO> getAchieveTargetsAndChallenge() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // isDeleted=false, endDateTime>now -> 진행중
        // 해당 챌린지의 마지막 기록이 없거나 isSucceed=false -> 달성 가능
        // 그 챌린지의 title, member.fcmToken, member.nickname 선택
        List<Tuple> result = queryFactory
                .select(
                        challenge.member.fcmToken,
                        challenge.member.nickname,
                        challenge.title
                )
                .from(challenge)
                .where(
                        challenge.isDeleted.eq(false),
                        challenge.endDateTime.gt(now),
                        lastRecordSucceed(today).eq(false)
                )
                .fetch();

        // 결과를 Map<String, AchieveChallengeDTO> 형태로 변환
        Map<String, AchieveChallengeDTO> resultMap = new HashMap<>();
        for (Tuple tuple : result) {
            String token = tuple.get(challenge.member.fcmToken);
            String nickname = tuple.get(challenge.member.nickname);
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
