package com.challenge.api.service.notification;

import com.challenge.domain.member.Member;
import com.challenge.domain.member.MemberRepository;
import com.challenge.domain.notification.Notification;
import com.challenge.domain.notification.NotificationQueryRepository;
import com.challenge.domain.notification.NotificationRepository;
import com.challenge.exception.ErrorCode;
import com.challenge.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationQueryRepository notificationQueryRepository;
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    /**
     * 진행중인 챌린지가 없는 회원 token, 닉네임, id 조회
     *
     * @return token, NewChallengeDTO
     */
    public Map<String, NewChallengeDTO> getNewChallengeTargets() {
        return notificationQueryRepository.getNewChallengeTargets();
    }

    /**
     * 현재 시각 기준 달성할 챌린지가 있는 회원 token, id, 닉네임, 챌린지 제목 리스트 조회
     *
     * @return token, AchieveChallengeCountDTO
     */
    public Map<String, AchieveChallengeDTO> getAchieveTargetsAndChallenge() {
        LocalDate today = LocalDate.now();
        return notificationQueryRepository.getAchieveTargetsAndChallenge(today);
    }

    /**
     * 알림 내역 생성 및 저장
     *
     * @param memberId
     * @param title
     * @param content
     * @return
     */
    @Transactional
    public Notification createAndSave(Long memberId, String title, String content) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));

        return notificationRepository.save(Notification.of(title, content, LocalDateTime.now(), member));
    }

}
