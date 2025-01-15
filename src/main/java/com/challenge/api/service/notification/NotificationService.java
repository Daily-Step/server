package com.challenge.api.service.notification;

import com.challenge.domain.notification.NotificationQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationQueryRepository notificationQueryRepository;

    /**
     * 진행중인 챌린지가 없는 회원 token 및 닉네임 조회
     *
     * @return token, 닉네임
     */
    public Map<String, String> getNewChallengeTargets() {
        return notificationQueryRepository.getNewChallengeTargets();
    }

    /**
     * 현재 시각 기준 달성할 챌린지가 있는 회원 token, 닉네임, 챌린지 제목 리스트 조회
     *
     * @return token, AchieveChallengeCountDTO
     */
    public Map<String, AchieveChallengeDTO> getAchieveTargetsAndChallenge() {
        return notificationQueryRepository.getAchieveTargetsAndChallenge();
    }

}
