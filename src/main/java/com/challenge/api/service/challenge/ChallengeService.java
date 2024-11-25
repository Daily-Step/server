package com.challenge.api.service.challenge;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<Map<String, Object>> getChallenges(Long memberId) {
        List<Map<String, Object>> challenges = new ArrayList<>();

        // 1번 챌린지
        LocalDateTime challengeStartTime1 = LocalDateTime.of(2024, 11, 1, 10, 0, 0);
        LocalDateTime adjustedStartTime1 = challengeStartTime1.toLocalDate().atStartOfDay();
        challenges.add(
                Map.of(
                        "challenge_id", 1,
                        "category", "운동",
                        "title", "일일 운동 챌린지",
                        "content", "건강 유지를 위해 매일 운동 루틴을 완수하세요.",
                        "color", "#30B0C7",
                        "weekly_goal_count", 3,
                        "total_goal_count", 12,
                        "start_datetime", challengeStartTime1.format(dateTimeFormatter),
                        "end_datetime",
                        adjustedStartTime1.plusWeeks(4).plusDays(1).minusSeconds(1).format(dateTimeFormatter),
                        "success_date", List.of(
                                LocalDate.of(2024, 11, 3),
                                LocalDate.of(2024, 11, 4),
                                LocalDate.of(2024, 11, 10)
                        )
                )
        );

        // 2번 챌린지
        LocalDateTime challengeStartTime2 = LocalDateTime.of(2024, 11, 15, 13, 0, 0);
        LocalDateTime adjustedStartTime2 = challengeStartTime2.toLocalDate().atStartOfDay();
        challenges.add(
                Map.of(
                        "challenge_id", 2,
                        "category", "교양",
                        "title", "주간 독서",
                        "content", "정신적 성장을 위해 매주 한 권의 책을 완독하세요.",
                        "color", "#2257FF",
                        "weekly_goal_count", 2,
                        "total_goal_count", 4,
                        "start_datetime", challengeStartTime2.format(dateTimeFormatter),
                        "end_datetime",
                        adjustedStartTime2.plusWeeks(2).plusDays(1).minusSeconds(1).format(dateTimeFormatter),
                        "success_date", List.of(
                                LocalDate.of(2024, 11, 16),
                                LocalDate.of(2024, 11, 17),
                                LocalDate.of(2024, 11, 18),
                                LocalDate.of(2024, 11, 20),
                                LocalDate.of(2024, 11, 23),
                                LocalDate.of(2024, 11, 24),
                                LocalDate.of(2024, 11, 25)
                        )
                )
        );

        // 3번 챌린지
        LocalDateTime challengeStartTime3 = LocalDateTime.of(2024, 11, 20, 11, 30, 0);
        LocalDateTime adjustedStartTime3 = challengeStartTime3.toLocalDate().atStartOfDay();
        challenges.add(
                Map.of(
                        "challenge_id", 3,
                        "category", "취미",
                        "title", "매일 기타 연습",
                        "content", "매일 최소 20분 동안 기타를 연습하여 실력을 향상시키세요.",
                        "color", "#8120FF",
                        "weekly_goal_count", 1,
                        "total_goal_count", 2,
                        "start_datetime", challengeStartTime3.format(dateTimeFormatter),
                        "end_datetime",
                        adjustedStartTime3.plusWeeks(2).plusDays(1).minusSeconds(1).format(dateTimeFormatter),
                        "success_date", List.of(
                                LocalDate.of(2024, 11, 21)
                        )
                )
        );

        return challenges;
    }

}
