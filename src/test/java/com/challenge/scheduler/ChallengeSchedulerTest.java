package com.challenge.scheduler;

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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class ChallengeSchedulerTest {

    @MockitoBean
    private Clock clock;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private ChallengeRecordRepository challengeRecordRepository;

    @Autowired
    private ChallengeScheduler challengeScheduler;

    @AfterEach
    void tearDown() {
        challengeRecordRepository.deleteAllInBatch();
        challengeRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        jobRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("챌린지 상태 업데이트 스케줄러 테스트")
    void updateChallengeStatus() {
        // given
        // 1월 9일 00:00:00으로 고정된 Clock 설정
        LocalDateTime fixedDateTime = LocalDateTime.of(2025, 1, 9, 0, 0, 0);
        Clock fixedClock = Clock.fixed(
                fixedDateTime.atZone(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault());
        given(clock.instant()).willReturn(fixedClock.instant());
        given(clock.getZone()).willReturn(fixedClock.getZone());

        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory();
        categoryRepository.save(category);

        // 챌린지 생성
        // 종료 기간은 1월 8일 23시 59분 59초
        Challenge challenge1 = createChallenge(member, category, 3,
                LocalDateTime.of(2025, 1, 1, 12, 30, 59));
        Challenge challenge2 = createChallenge(member, category, 2,
                LocalDateTime.of(2025, 1, 1, 14, 0, 0));
        challengeRepository.saveAll(List.of(challenge1, challenge2));

        // 기록 생성
        ChallengeRecord challengeRecord1 = createRecord(challenge1, true,
                LocalDate.of(2025, 1, 2));
        ChallengeRecord challengeRecord2 = createRecord(challenge1, false,
                LocalDate.of(2024, 1, 3));
        ChallengeRecord challengeRecord3 = createRecord(challenge1, true,
                LocalDate.of(2024, 1, 4));

        ChallengeRecord challengeRecord4 = createRecord(challenge2, true,
                LocalDate.of(2025, 1, 2));
        ChallengeRecord challengeRecord5 = createRecord(challenge2, false,
                LocalDate.of(2025, 1, 2));
        ChallengeRecord challengeRecord6 = createRecord(challenge2, true,
                LocalDate.of(2025, 1, 3));
        ChallengeRecord challengeRecord7 = createRecord(challenge2, true,
                LocalDate.of(2025, 1, 4));
        challengeRecordRepository.saveAll(
                List.of(
                        challengeRecord1, challengeRecord2, challengeRecord3,
                        challengeRecord4, challengeRecord5, challengeRecord6, challengeRecord7
                )
        );

        // when
        challengeScheduler.updateChallengeStatus();

        // then
        assertThat(challengeRepository.findById(challenge1.getId()).get().getStatus())
                .isEqualTo(ChallengeStatus.EXPIRED);
        assertThat(challengeRepository.findById(challenge2.getId()).get().getStatus())
                .isEqualTo(ChallengeStatus.SUCCEED);
    }

    private Member createMember() {
        Job job = Job.builder()
                .code("1")
                .description("1")
                .build();
        jobRepository.save(job);

        return Member.builder()
                .socialId(1L)
                .email("eamil")
                .loginType(LoginType.KAKAO)
                .nickname("nickname")
                .birth(LocalDate.of(2000, 1, 1))
                .gender(Gender.MALE)
                .jobYear(JobYear.LT_1Y)
                .job(job)
                .build();
    }

    private Category createCategory() {
        return Category.builder()
                .name("category")
                .build();
    }

    private Challenge createChallenge(Member member, Category category, int weeklyGoalCount,
                                      LocalDateTime startDateTime) {
        return Challenge.builder()
                .member(member)
                .category(category)
                .durationInWeeks(1)
                .title("제목")
                .status(ChallengeStatus.ONGOING)
                .color("#30B0C7")
                .weeklyGoalCount(weeklyGoalCount)
                .startDateTime(startDateTime)
                .build();
    }

    private ChallengeRecord createRecord(Challenge challenge, boolean isSucceed, LocalDate currentDate) {
        return ChallengeRecord.builder()
                .challenge(challenge)
                .recordDate(currentDate)
                .isSucceed(isSucceed)
                .build();
    }

}

