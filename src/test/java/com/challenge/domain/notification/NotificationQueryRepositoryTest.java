package com.challenge.domain.notification;

import com.challenge.api.service.notification.AchieveChallengeDTO;
import com.challenge.api.service.notification.NewChallengeDTO;
import com.challenge.domain.category.Category;
import com.challenge.domain.category.CategoryRepository;
import com.challenge.domain.challenge.Challenge;
import com.challenge.domain.challenge.ChallengeRepository;
import com.challenge.domain.challenge.ChallengeStatus;
import com.challenge.domain.challengeRecord.ChallengeRecord;
import com.challenge.domain.challengeRecord.ChallengeRecordRepository;
import com.challenge.domain.job.Job;
import com.challenge.domain.job.JobRepository;
import com.challenge.domain.member.Gender;
import com.challenge.domain.member.JobYear;
import com.challenge.domain.member.LoginType;
import com.challenge.domain.member.Member;
import com.challenge.domain.member.MemberRepository;
import com.challenge.utils.date.DateFormatter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class NotificationQueryRepositoryTest {

    @Autowired
    private NotificationQueryRepository notificationQueryRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ChallengeRecordRepository challengeRecordRepository;

    private Job job;
    private Category category;

    @BeforeEach
    void setUp() {
        job = jobRepository.save(
                Job.builder()
                        .code("1")
                        .description("1")
                        .build());

        category = categoryRepository.save(
                Category.builder()
                        .name("카테고리")
                        .build());
    }

    @AfterEach
    void tearDown() {
        challengeRecordRepository.deleteAllInBatch();
        challengeRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        jobRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("진행중인 챌린지가 없는 회원 조회")
    class GetNewChallengeTargets {

        @DisplayName("회원의 챌린지 상태가 ONGOING인 경우 빈 map을 반환해야 한다.")
        @Test
        void getNewChallengeTargetsAllChallengesOngoing() {
            // given
            LocalDateTime dateTime = LocalDateTime.now().minusDays(1);

            Member member = createMember(1);
            createChallenge(member, category, 1, "title1", ChallengeStatus.ONGOING, dateTime);

            // when
            Map<String, NewChallengeDTO> resultMap = notificationQueryRepository.getNewChallengeTargets();

            // then
            assertThat(resultMap).isEmpty();
        }

        @DisplayName("회원의 챌린지 상태가 ONGOING이 아니지만, 알림을 받지 않도록 설정한 경우 빈 map을 반환해야 한다.")
        @Test
        void getNewChallengeTargetsWhenNotReceiveNotification() {
            // given
            LocalDateTime dateTime = LocalDateTime.now().minusDays(1);

            Member member = createMember(1);
            createChallenge(member, category, 1, "title1", ChallengeStatus.REMOVED, dateTime);

            // when
            Map<String, NewChallengeDTO> resultMap = notificationQueryRepository.getNewChallengeTargets();

            // then
            assertThat(resultMap).isEmpty();
        }

        @DisplayName("모든 회원의 챌린지 상태가 ONGOING이 아니고, 알림을 받도록 설정한 경우 회원 정보를 담은 map을 반환해야 한다.")
        @Test
        void getNewChallengeTargets() {
            // given
            LocalDateTime dateTime = LocalDateTime.now().minusDays(1);

            Member member1 = createMember(1);
            member1.updateNotificationReceived();
            createChallenge(member1, category, 1, "title1", ChallengeStatus.REMOVED, dateTime);

            Member member2 = createMember(2);
            member2.updateNotificationReceived();
            createChallenge(member2, category, 1, "title2", ChallengeStatus.SUCCEED, dateTime);

            Member member3 = createMember(3);
            member3.updateNotificationReceived();
            createChallenge(member3, category, 1, "title3", ChallengeStatus.EXPIRED, dateTime);

            Member member4 = createMember(4);
            member4.updateNotificationReceived();

            memberRepository.saveAll(List.of(member1, member2, member3, member4));

            // when
            Map<String, NewChallengeDTO> resultMap = notificationQueryRepository.getNewChallengeTargets();

            // then
            assertThat(resultMap).hasSize(4);
            assertThat(resultMap).containsKeys("token1", "token2", "token3", "token4");
        }

    }

    @Nested
    @DisplayName("달성할 챌린지가 있는 회원 및 챌린지 제목 조회")
    class GetAchieveTargetsAndChallenge {

        @DisplayName("달성할 챌린지가 여러 개 있는 경우 dto의 챌린지 제목 리스트에 여러 개가 담긴 map을 반환해야 한다.")
        @Test
        void getAchieveTargetsAndChallengeWhenMultipleChallenges() {
            // given
            LocalDateTime dateTime = LocalDateTime.now();

            Member member = createMember(1);
            member.updateNotificationReceived();
            memberRepository.save(member);

            createChallenge(member, category, 1, "title1", ChallengeStatus.ONGOING, dateTime);
            createChallenge(member, category, 1, "title2", ChallengeStatus.ONGOING, dateTime);
            createChallenge(member, category, 1, "title3", ChallengeStatus.ONGOING, dateTime);

            // when
            Map<String, AchieveChallengeDTO> resultMap = notificationQueryRepository.getAchieveTargetsAndChallenge(
                    dateTime.toLocalDate());

            // then
            assertThat(resultMap).hasSize(1);

            AchieveChallengeDTO dto = resultMap.get("token1");
            assertThat(dto.getChallengeTitles()).hasSize(3);
            assertThat(dto.getChallengeTitles()).containsExactly("title1", "title2", "title3");
            assertThat(dto.getNickname()).isEqualTo("nickname1");
            assertThat(dto.getMemberId()).isEqualTo(member.getId());
        }

        @DisplayName("챌린지의 오늘 일자 기록이 존재하지만 마지막 기록의 isSucceeds가 false인 경우 해당 챌린지 정보가 담긴 map을 반환해야 한다.")
        @Test
        void getAchieveTargetsAndChallengeWhenSingleChallenge() throws Exception {
            // given
            LocalDateTime dateTime = LocalDateTime.now();

            // LocalDateTime -> String 변환
            String formattedDate = DateFormatter.LOCAL_DATE_FORMATTER.format(dateTime);

            Member member = createMember(1);
            member.updateNotificationReceived();
            memberRepository.save(member);

            Challenge challenge = createChallenge(member, category, 1, "title1", ChallengeStatus.ONGOING, dateTime);
            createRecord(challenge, dateTime.toLocalDate());
            challengeRecordRepository.save(ChallengeRecord.cancel(challenge, formattedDate));

            // when
            Map<String, AchieveChallengeDTO> resultMap = notificationQueryRepository.getAchieveTargetsAndChallenge(
                    dateTime.toLocalDate());

            // then
            assertThat(resultMap).hasSize(1);

            AchieveChallengeDTO dto = resultMap.get("token1");
            assertThat(dto.getChallengeTitles()).hasSize(1);
            assertThat(dto.getChallengeTitles()).containsExactly("title1");
            assertThat(dto.getNickname()).isEqualTo("nickname1");
            assertThat(dto.getMemberId()).isEqualTo(member.getId());
        }

        @DisplayName("회원의 챌린지 상태가 ONGOING이 아닌 경우 빈 map을 반환해야 한다.")
        @Test
        void getAchieveTargetsAndChallengeNotOngoing() {
            // given
            LocalDateTime dateTime = LocalDateTime.now();

            Member member = createMember(1);
            member.updateNotificationReceived();
            memberRepository.save(member);

            createChallenge(member, category, 1, "title1", ChallengeStatus.REMOVED, dateTime);

            // when
            Map<String, AchieveChallengeDTO> resultMap = notificationQueryRepository.getAchieveTargetsAndChallenge(
                    dateTime.toLocalDate());

            // then
            assertThat(resultMap).isEmpty();
        }

        @DisplayName("회원이 알림을 받지 않도록 설정한 경우 빈 map을 반환해야 한다.")
        @Test
        void getAchieveTargetsAndChallengeWhenNotReceiveNotification() {
            // given
            LocalDateTime dateTime = LocalDateTime.now();

            Member member = createMember(1);

            createChallenge(member, category, 1, "title1", ChallengeStatus.ONGOING, dateTime);

            // when
            Map<String, AchieveChallengeDTO> resultMap = notificationQueryRepository.getAchieveTargetsAndChallenge(
                    dateTime.toLocalDate());

            // then
            assertThat(resultMap).isEmpty();
        }

        @DisplayName("챌린지 상태가 ONGOING이지만 오늘 일자에 이미 달성한 상태인 경우 빈 map을 반환해야 한다.")
        @Test
        void getAchieveTargetsAndChallengeWhenAlreadySucceed() {
            // given
            LocalDateTime dateTime = LocalDateTime.now();

            // LocalDateTime -> String 변환
            String formattedDate = DateFormatter.LOCAL_DATE_FORMATTER.format(dateTime);

            Member member = createMember(1);
            member.updateNotificationReceived();
            memberRepository.save(member);

            Challenge challenge = createChallenge(member, category, 1, "title1", ChallengeStatus.ONGOING, dateTime);
            challengeRecordRepository.save(ChallengeRecord.achieve(challenge, formattedDate));

            // when
            Map<String, AchieveChallengeDTO> resultMap = notificationQueryRepository.getAchieveTargetsAndChallenge(
                    dateTime.toLocalDate());

            // then
            assertThat(resultMap).isEmpty();
        }

    }


    private Member createMember(Integer num) {
        Member member = Member.builder()
                .socialId(1L)
                .email("eamil")
                .loginType(LoginType.KAKAO)
                .nickname("nickname" + num)
                .birth(LocalDate.of(2000, 1, 1))
                .gender(Gender.MALE)
                .jobYear(JobYear.LT_1Y)
                .job(job)
                .build();
        member.updateFcmToken("token" + num.toString());
        return memberRepository.save(member);
    }

    private Challenge createChallenge(Member member, Category category, int durationInWeeks, String title,
                                      ChallengeStatus status, LocalDateTime startDateTime) {
        return challengeRepository.save(
                Challenge.builder()
                        .member(member)
                        .category(category)
                        .durationInWeeks(durationInWeeks)
                        .title(title)
                        .color("#30B0C7")
                        .status(status)
                        .weeklyGoalCount(1)
                        .startDateTime(startDateTime)
                        .build());
    }

    private ChallengeRecord createRecord(Challenge challenge, LocalDate currentDate) {
        return challengeRecordRepository.save(
                ChallengeRecord.builder()
                        .challenge(challenge)
                        .recordDate(currentDate)
                        .isSucceed(true)
                        .build());
    }

}
