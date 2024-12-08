package com.challenge.domain.challenge;

import com.challenge.api.service.challenge.request.ChallengeCreateServiceRequest;
import com.challenge.domain.category.Category;
import com.challenge.domain.category.CategoryRepository;
import com.challenge.domain.job.Job;
import com.challenge.domain.job.JobRepository;
import com.challenge.domain.member.*;
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
