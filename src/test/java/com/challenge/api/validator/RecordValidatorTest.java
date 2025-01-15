package com.challenge.api.validator;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class RecordValidatorTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ChallengeRecordRepository challengeRecordRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private RecordValidator recordValidator;

    @AfterEach
    void tearDown() {
        challengeRecordRepository.deleteAllInBatch();
        challengeRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        jobRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
    }

    @DisplayName("특정 날짜의 최신 기록이 성공인지 확인한다.")
    @Test
    void isLatestRecordSuccessful() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory();
        categoryRepository.save(category);

        Challenge challenge = createChallenge(member, category, LocalDateTime.of(2024, 10, 1, 12, 30, 59));
        challengeRepository.save(challenge);

        ChallengeRecord record1 = ChallengeRecord.builder()
                .challenge(challenge)
                .recordDate(LocalDate.of(2024, 10, 3))
                .isSucceed(true)
                .build();
        ChallengeRecord record2 = ChallengeRecord.builder()
                .challenge(challenge)
                .recordDate(LocalDate.of(2024, 10, 3))
                .isSucceed(false)
                .build();
        ChallengeRecord record3 = ChallengeRecord.builder()
                .challenge(challenge)
                .recordDate(LocalDate.of(2024, 10, 3))
                .isSucceed(true)
                .build();
        challengeRecordRepository.saveAll(List.of(record1, record2, record3));

        // when
        ChallengeRecord latestRecord = recordValidator.isLatestRecordSuccessfulBy(challenge, LocalDate.of(2024, 10, 3));

        // then
        assertThat(latestRecord.getId()).isNotNull();
        assertThat(latestRecord.isSucceed()).isTrue();
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
                .nickname("닉네임")
                .birth(LocalDate.of(2000, 1, 1))
                .gender(Gender.MALE)
                .jobYear(JobYear.LT_1Y)
                .job(job)
                .build();
    }

    private Category createCategory() {
        return Category.builder()
                .name("카테고리")
                .build();
    }

    private Challenge createChallenge(Member member, Category category, LocalDateTime startDateTime) {
        return Challenge.builder()
                .member(member)
                .category(category)
                .title("제목")
                .status(ChallengeStatus.ONGOING)
                .color("#30B0C7")
                .durationInWeeks(1)
                .weeklyGoalCount(1)
                .startDateTime(startDateTime)
                .build();
    }

}
