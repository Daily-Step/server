package com.challenge.domain.challenge;

import com.challenge.api.service.challenge.request.ChallengeCreateServiceRequest;
import com.challenge.domain.category.Category;
import com.challenge.domain.category.CategoryRepository;
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

import static com.challenge.domain.challenge.ChallengeStatus.ONGOING;
import static com.challenge.domain.challenge.ChallengeStatus.REMOVED;
import static com.challenge.domain.challenge.ChallengeStatus.SUCCEED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@SpringBootTest
@ActiveProfiles("test")
class ChallengeQueryRepositoryTest {

    @Autowired
    private ChallengeQueryRepository challengeQueryRepository;

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

    @DisplayName("입력 받은 날짜 기준 이전 2달간의 챌린지 목록을 조회한다.")
    @Test
    void findChallengesBy() {
        // given
        LocalDate targetDate = LocalDate.of(2024, 12, 28);

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

        // when
        List<Challenge> challenges = challengeQueryRepository.findChallengesBy(member, targetDate);

        // then
        assertThat(challenges).hasSize(2)
                .extracting("title", "durationInWeeks")
                .containsExactlyInAnyOrder(
                        tuple("제목2", 2),
                        tuple("제목3", 3)
                );
    }

    @DisplayName("입력 받은 날짜 기준 2달 전 날짜의 00:00:00부터 입력받은 날짜의 23:59:59까지의 시간 범위를 계산 한다.")
    @Test
    void shouldCalculateStartAndEndDateTimeForChallengeQuery() {
        // given
        LocalDate localDate = LocalDate.of(2024, 11, 11);

        // when
        LocalDateTime startDateTime = localDate.minusMonths(2).atStartOfDay();
        LocalDateTime endDateTime = localDate.atTime(23, 59, 59);

        // then
        startDateTime.isEqual(LocalDateTime.of(2024, 9, 11, 0, 0, 0));
        endDateTime.isEqual(LocalDateTime.of(2024, 11, 11, 23, 59, 59));
    }

    @DisplayName("이미 챌린지를 달성해 중복된 챌린지 기록이 존재하면 true를 반환한다.")
    @Test
    void existsDuplicateRecordBy() {
        // given
        LocalDate currentDate = LocalDate.now();

        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory();
        categoryRepository.save(category);

        ChallengeCreateServiceRequest request = createChallengeServiceRequest();

        Challenge challenge = Challenge.create(member, category, request, LocalDateTime.now());
        challengeRepository.save(challenge);

        ChallengeRecord challengeRecord = createRecord(challenge, currentDate);
        challengeRecordRepository.save(challengeRecord);

        // when
        boolean result = challengeQueryRepository.existsDuplicateRecordBy(challenge, currentDate);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("진행중인 챌린지 수를 조회한다.")
    @Test
    void countOngoingChallenges() {
        // given
        String targetDateTime = "2025-01-08 12:30:59";

        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory();
        categoryRepository.save(category);

        Challenge challenge1 = createChallenge(member, category, 1, "제목1", ONGOING,
                LocalDateTime.of(2024, 10, 1, 12, 30, 59));
        Challenge challenge2 = createChallenge(member, category, 2, "제목2", ONGOING,
                LocalDateTime.of(2024, 11, 11, 14, 0, 0));
        Challenge challenge3 = createChallenge(member, category, 3, "제목3", ONGOING,
                LocalDateTime.of(2024, 12, 23, 0, 0, 0));
        Challenge challenge4 = createChallenge(member, category, 1, "제목4", ONGOING,
                LocalDateTime.of(2025, 1, 1, 0, 0, 0));
        challengeRepository.saveAll(List.of(challenge1, challenge2, challenge3, challenge4));

        // when
        Long count = challengeQueryRepository.countOngoingChallengesBy(member, targetDateTime);

        // then
        assertThat(count).isEqualTo(2);
    }

    @DisplayName("완료된 챌린지 수를 조회한다.")
    @Test
    void countSucceedChallenges() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory();
        categoryRepository.save(category);

        Challenge challenge1 = createChallenge(member, category, 1, "제목1", ONGOING,
                LocalDateTime.of(2024, 10, 1, 12, 30, 59));
        Challenge challenge2 = createChallenge(member, category, 2, "제목2", ONGOING,
                LocalDateTime.of(2024, 11, 11, 14, 0, 0));
        Challenge challenge3 = createChallenge(member, category, 3, "제목3", ONGOING,
                LocalDateTime.of(2024, 12, 23, 0, 0, 0));
        Challenge challenge4 = createChallenge(member, category, 1, "제목4", SUCCEED,
                LocalDateTime.of(2025, 1, 1, 0, 0, 0));
        challengeRepository.saveAll(List.of(challenge1, challenge2, challenge3, challenge4));

        // when
        Long count = challengeQueryRepository.countSucceedChallengesBy(member);

        // then
        assertThat(count).isEqualTo(1);
    }

    @DisplayName("전체 챌린지 수를 조회한다.")
    @Test
    void countAllChallenges() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory();
        categoryRepository.save(category);

        Challenge challenge1 = createChallenge(member, category, 1, "제목1", ONGOING,
                LocalDateTime.of(2024, 10, 1, 12, 30, 59));
        Challenge challenge2 = createChallenge(member, category, 2, "제목2", ONGOING,
                LocalDateTime.of(2024, 11, 11, 14, 0, 0));
        Challenge challenge3 = createChallenge(member, category, 3, "제목3", REMOVED,
                LocalDateTime.of(2024, 12, 23, 0, 0, 0));
        Challenge challenge4 = createChallenge(member, category, 1, "제목4", REMOVED,
                LocalDateTime.of(2025, 1, 1, 0, 0, 0));
        challengeRepository.saveAll(List.of(challenge1, challenge2, challenge3, challenge4));

        // when
        Long count = challengeQueryRepository.countAllChallengesBy(member);

        // then
        assertThat(count).isEqualTo(2);
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

    private ChallengeRecord createRecord(Challenge challenge, LocalDate currentDate) {
        return ChallengeRecord.builder()
                .challenge(challenge)
                .recordDate(currentDate)
                .isSucceed(true)
                .build();
    }

    private ChallengeCreateServiceRequest createChallengeServiceRequest() {
        return ChallengeCreateServiceRequest.builder()
                .title("제목")
                .durationInWeeks(2)
                .weeklyGoalCount(3)
                .categoryId(1L)
                .color("색상")
                .content("내용")
                .build();
    }

}
