package com.challenge.scheduler;

import com.challenge.domain.challenge.Challenge;
import com.challenge.domain.challenge.ChallengeQueryRepository;
import com.challenge.domain.challenge.ChallengeRepository;
import com.challenge.domain.challengeRecord.ChallengeRecord;
import com.challenge.domain.challengeRecord.ChallengeRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Profile({"dev", "test"})
@Slf4j
@Component
@RequiredArgsConstructor
public class ChallengeScheduler {

    private final Clock clock;

    private final ChallengeQueryRepository challengeQueryRepository;

    private final ChallengeRepository challengeRepository;
    private final ChallengeRecordRepository challengeRecordRepository;

    /**
     * 챌린지 상태 업데이트 스케줄러 - 매일 00시 00분 00초 실행
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void updateChallengeStatus() {
        LocalDateTime now = LocalDateTime.now(clock);

        // '진행 중' 상태의 챌린지 가져오기
        List<Challenge> ongoingChallenges = challengeQueryRepository.findOngoingChallengesBy();

        for (Challenge challenge : ongoingChallenges) {
            // 종료 시간이 현재 시간보다 이후인 경우 상태 업데이트를 건너뜀
            if (challenge.getEndDateTime().isAfter(now)) {
                continue;
            }

            // 챌린지 기록에서 해당 챌린지 ID에 대한 기록 가져오기
            List<ChallengeRecord> records = challengeRecordRepository.findAllByChallengeId(challenge.getId());

            // 날짜별로 마지막 상태가 '달성'인 기록 카운트
            long successCount = calculateSuccessCount(records);

            // 상태 변경: 목표 횟수 이상이면 성공, 아니면 기간 만료
            if (successCount >= challenge.getTotalGoalCount()) {
                challenge.success();
            } else {
                challenge.expire();
            }

            // 변경된 상태 저장
            challengeRepository.save(challenge);
        }
    }

    private long calculateSuccessCount(List<ChallengeRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(ChallengeRecord::getRecordDate, Collectors.toList()))
                .values().stream()
                .filter(recordList -> {
                    // 해당 날짜의 마지막 기록이 '달성' 상태인지 확인
                    ChallengeRecord lastRecord = recordList.get(recordList.size() - 1);
                    return lastRecord.isSucceed();
                })
                .count();
    }

}
