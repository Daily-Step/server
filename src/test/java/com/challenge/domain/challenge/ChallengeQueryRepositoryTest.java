package com.challenge.domain.challenge;

import com.challenge.api.service.challenge.request.ChallengeCreateServiceRequest;
import com.challenge.domain.category.Category;
import com.challenge.domain.category.CategoryRepository;
import com.challenge.domain.job.Job;
import com.challenge.domain.job.JobRepository;
import com.challenge.domain.member.Gender;
import com.challenge.domain.member.JobYear;
import com.challenge.domain.member.LoginType;
import com.challenge.domain.member.Member;
import com.challenge.domain.member.MemberRepository;
import com.challenge.domain.record.Record;
import com.challenge.domain.record.RecordRepository;
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
    private RecordRepository recordRepository;

    @AfterEach
    void tearDown() {
        recordRepository.deleteAllInBatch();
        challengeRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        jobRepository.deleteAllInBatch();
    }

    @DisplayName("진행중인 챌린지 목록을 조회한다.")
    @Test
    void findChallengesBy() {
        // given
        LocalDateTime currentDateTime = LocalDateTime.of(2024, 11, 30, 23, 59, 59);

        Category category = createCategory();
        categoryRepository.save(category);

        Member member = createMember();
        memberRepository.save(member);

        Challenge challenge1 = createChallenge(member, category, currentDateTime, 1);
        Challenge challenge2 = createChallenge(member, category,
                LocalDateTime.of(2024, 11, 16, 14, 0, 0),
                2);
        Challenge challenge3 = createChallenge(member,
                category,
                LocalDateTime.of(2024, 12, 1, 0, 0, 0),
                1);
        challengeRepository.saveAll(List.of(challenge1, challenge2, challenge3));

        // when
        List<Challenge> challenges = challengeQueryRepository.findChallengesBy(member, currentDateTime);

        // then
        assertThat(challenges).hasSize(2);
//                .extracting("records", "durationInWeeks")
//                .containsExactlyInAnyOrder(
//                        tuple(List.of(), 1),
//                        tuple(List.of(), 2)
//                );
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

        Record record = createRecord(challenge, currentDate);
        recordRepository.save(record);

        // when
        boolean result = challengeQueryRepository.existsDuplicateRecordBy(challenge, currentDate);

        // then
        assertThat(result).isTrue();
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

    private Challenge createChallenge(Member member, Category category, LocalDateTime startDateTime,
            int durationInWeeks) {
        return Challenge.builder()
                .member(member)
                .category(category)
                .durationInWeeks(durationInWeeks)
                .title("제목")
                .color("#30B0C7")
                .weeklyGoalCount(1)
                .startDateTime(startDateTime)
                .build();
    }

    private Category createCategory() {
        return Category.builder()
                .name("카테고리")
                .build();
    }

    private Record createRecord(Challenge challenge, LocalDate currentDate) {
        return Record.builder()
                .challenge(challenge)
                .successDate(currentDate)
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
