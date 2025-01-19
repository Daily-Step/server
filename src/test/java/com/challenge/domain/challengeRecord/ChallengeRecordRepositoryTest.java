package com.challenge.domain.challengeRecord;

import com.challenge.domain.category.Category;
import com.challenge.domain.category.CategoryRepository;
import com.challenge.domain.challenge.Challenge;
import com.challenge.domain.challenge.ChallengeRepository;
import com.challenge.domain.challenge.ChallengeStatus;
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

import static com.challenge.domain.challenge.ChallengeStatus.ONGOING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@SpringBootTest
@ActiveProfiles("test")
class ChallengeRecordRepositoryTest {

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

    @AfterEach
    void tearDown() {
        challengeRecordRepository.deleteAllInBatch();
        challengeRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        jobRepository.deleteAllInBatch();
    }

    @DisplayName("특정 챌린지 ID에 대한 모든 챌린지 기록을 조회한다.")
    @Test
    void findAllByChallengeId() {
        // given
        Category category = createCategory();
        categoryRepository.save(category);

        Member member = createMember();
        memberRepository.save(member);

        Challenge challenge1 = createChallenge(member, category, 1, "제목1", ONGOING,
                LocalDateTime.of(2024, 10, 1, 12, 30, 59));
        Challenge challenge2 = createChallenge(member, category, 2, "제목2", ONGOING,
                LocalDateTime.of(2024, 11, 16, 14, 0, 0));
        Challenge challenge3 = createChallenge(member, category, 3, "제목3", ONGOING,
                LocalDateTime.of(2024, 12, 1, 0, 0, 0));
        challengeRepository.saveAll(List.of(challenge1, challenge2, challenge3));

        LocalDate currentDate = LocalDate.of(2025, 1, 18);
        ChallengeRecord challengeRecord1 = createRecord(challenge1, currentDate, true);
        ChallengeRecord challengeRecord2 = createRecord(challenge1, currentDate, false);
        ChallengeRecord challengeRecord3 = createRecord(challenge1, currentDate, true);
        challengeRecordRepository.saveAll(List.of(challengeRecord1, challengeRecord2, challengeRecord3));

        // when
        List<ChallengeRecord> challengeRecords = challengeRecordRepository.findAllByChallengeId(challenge1.getId());

        // then
        assertThat(challengeRecords).hasSize(3)
                .extracting("recordDate", "isSucceed")
                .containsExactlyInAnyOrder(
                        tuple(currentDate, true),
                        tuple(currentDate, false),
                        tuple(currentDate, true)
                );
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

    private Challenge createChallenge(Member member, Category category, int durationInWeeks, String title,
                                      ChallengeStatus status, LocalDateTime startDateTime) {
        return Challenge.builder()
                .member(member)
                .category(category)
                .durationInWeeks(durationInWeeks)
                .title(title)
                .color("#30B0C7")
                .status(status)
                .weeklyGoalCount(1)
                .startDateTime(startDateTime)
                .build();
    }

    private Category createCategory() {
        return Category.builder()
                .name("카테고리")
                .build();
    }

    private ChallengeRecord createRecord(Challenge challenge, LocalDate currentDate, boolean isSucceed) {
        return ChallengeRecord.builder()
                .challenge(challenge)
                .recordDate(currentDate)
                .isSucceed(isSucceed)
                .build();
    }

}
