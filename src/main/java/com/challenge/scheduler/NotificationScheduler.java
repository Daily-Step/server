package com.challenge.scheduler;

import com.challenge.api.service.fcm.FcmService;
import com.challenge.api.service.fcm.request.FcmMessage;
import com.challenge.api.service.notification.AchieveChallengeCountDTO;
import com.challenge.api.service.notification.AchieveChallengeTitleDTO;
import com.challenge.api.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationService notificationService;
    private final FcmService fcmService;

    private static final String NEW_CHALLENGE_TITLE = "⛳️ 챌린지를 등록해 보세요!";
    private static final String ACHIEVE_DAY_START_TITLE = "📬 오늘 달성할 수 있는 챌린지";
    private static final String ACHIEVE_DAY_END_TITLE = "🙌 달성할 수 있는 챌린지가 있어요!";
    private static final String NEW_CHALLENGE_BODY = "님, 지금 새 챌린지를 등록하고 달성할 수 있어요";
    private static final String ACHIEVE_DAY_START_BODY = "포함 %d개의 챌린지";
    private static final String ACHIEVE_DAY_END_BODY = "님, 아직 달성할 수 있는 챌린지가 %d개 있어요!";
    private static final int MAX_TITLES = 3;


    /**
     * 새로운 챌린지 등록 알림 발송
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void sendNewChallengeNotification() {
        // 알림 전송 대상 조회
        Map<String, String> targetMap = notificationService.getNewChallengeTargets();

        // 알림 객체 생성
        List<FcmMessage> fcmMessages = targetMap.entrySet().stream()
                .map(entry ->
                        FcmMessage.of(entry.getKey(), NEW_CHALLENGE_TITLE, entry.getValue() + NEW_CHALLENGE_BODY)
                ).toList();

        // 알림 발송
        fcmMessages.forEach(fcmService::sendMessage);
    }

    /**
     * 챌린지 하루 시작 알림 발송
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void sendDayStartNotification() {
        // 알림 전송 대상 조회
        Map<String, AchieveChallengeTitleDTO> targetMap = notificationService.getAchieveTargetsAndChallengeTitle();

        // 알림 객체 생성
        List<FcmMessage> fcmMessages = targetMap.entrySet().stream()
                .map(entry -> FcmMessage.of(entry.getKey(), ACHIEVE_DAY_START_TITLE,
                        getDayStartNotificationBody(entry.getValue()))
                ).toList();

        // 알림 발송
        fcmMessages.forEach(fcmService::sendMessage);
    }

    @Scheduled(cron = "0 0 21 * * *")
    public void sendDayEndNotification() {
        // 알림 전송 대상 조회
        Map<String, AchieveChallengeCountDTO> targetMap = notificationService.getAchieveTargetsAndChallengeCount();

        // 알림 객체 생성
        List<FcmMessage> fcmMessages = targetMap.entrySet().stream()
                .map(entry ->
                        FcmMessage.of(entry.getKey(), ACHIEVE_DAY_END_TITLE,
                                getDayEndNotificationBody(entry.getValue()))
                ).toList();

        // 알림 발송
        fcmMessages.forEach(fcmService::sendMessage);
    }

    private String getDayStartNotificationBody(AchieveChallengeTitleDTO dto) {
        List<String> challengeTitles = dto.getChallengeTitles();
        String body = challengeTitles.stream()
                .map(title -> "- " + title)
                .collect(Collectors.joining("\n"));

        // 타이틀 개수가 3 이상인 경우 메시지 추가
        if (challengeTitles.size() >= MAX_TITLES) {
            body += "\n" + String.format(ACHIEVE_DAY_START_BODY, challengeTitles.size());
        }

        return body;
    }

    private String getDayEndNotificationBody(AchieveChallengeCountDTO dto) {
        return dto.getNickname() + String.format(ACHIEVE_DAY_END_BODY, dto.getCount());
    }

}
