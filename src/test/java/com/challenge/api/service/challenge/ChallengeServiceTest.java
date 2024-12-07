package com.challenge.api.service.challenge;

import com.challenge.api.service.challenge.request.ChallengeCreateServiceRequest;
import com.challenge.api.service.challenge.response.ChallengeResponse;
import com.challenge.domain.category.Category;
import com.challenge.domain.category.CategoryRepository;
import com.challenge.domain.challenge.Challenge;
import com.challenge.domain.challenge.ChallengeRepository;
import com.challenge.domain.job.Job;
import com.challenge.domain.job.JobRepository;
import com.challenge.domain.member.Gender;
import com.challenge.domain.member.JobYear;
import com.challenge.domain.member.LoginType;
import com.challenge.domain.member.Member;
import com.challenge.domain.member.MemberRepository;
import com.challenge.domain.record.Record;
import com.challenge.domain.record.RecordRepository;
import com.challenge.utils.DateUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@SpringBootTest
@ActiveProfiles("test")
class ChallengeServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private ChallengeService challengeService;

    @AfterEach
    void tearDown() {
        recordRepository.deleteAllInBatch();
        challengeRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        jobRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
    }

    @DisplayName("챌린지를 생성한다.")
    @Test
    void createChallenge() {
        // given
        LocalDateTime startDateTime = LocalDateTime.of(2024, 11, 11, 10, 10, 30);

        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory();
        categoryRepository.save(category);

        ChallengeCreateServiceRequest request = createChallengeServiceRequest();

        // when
        ChallengeResponse challengeResponse = challengeService.createChallenge(member, request, startDateTime);

        // then
        assertThat(challengeResponse.getId()).isNotNull();
        assertThat(challengeResponse.getCategory())
                .extracting("id", "name")
                .contains(1L, "카테고리");
        assertThat(challengeResponse.getRecords()).isEmpty();
        assertThat(challengeResponse)
                .extracting("startDateTime", "totalGoalCount")
                .contains("2024-11-11 10:10:30", 6);
    }

    @DisplayName("챌린지를 달성 한다.")
    @Test
    void successChallenge() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory();
        categoryRepository.save(category);

        ChallengeCreateServiceRequest request = createChallengeServiceRequest();

        Challenge challenge = Challenge.create(member, category, request, LocalDateTime.of(2024, 11, 11, 10, 10, 30));
        challengeRepository.save(challenge);

        Record record1 = createRecord(challenge, LocalDate.of(2024, 11, 11));
        Record record2 = createRecord(challenge, LocalDate.of(2024, 11, 12));
        Record record3 = createRecord(challenge, LocalDate.of(2024, 11, 13));
        recordRepository.saveAll(List.of(record1, record2, record3));

        // when
        ChallengeResponse challengeResponse = challengeService.successChallenge(
                challenge.getId(),
                LocalDate.of(2024, 11, 14)
        );

        // then
        assertThat(challengeResponse.getId()).isNotNull();
        assertThat(challengeResponse.getTitle()).isEqualTo("제목");
        assertThat(challengeResponse.getRecords()).hasSize(4)
                .extracting("id", "successDate")
                .containsExactlyInAnyOrder(
                        tuple(1L, DateUtils.toDayString(LocalDate.of(2024, 11, 11))),
                        tuple(2L, DateUtils.toDayString(LocalDate.of(2024, 11, 12))),
                        tuple(3L, DateUtils.toDayString(LocalDate.of(2024, 11, 13))),
                        tuple(4L, DateUtils.toDayString(LocalDate.of(2024, 11, 14)))
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
