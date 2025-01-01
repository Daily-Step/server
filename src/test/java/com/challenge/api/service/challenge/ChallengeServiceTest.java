package com.challenge.api.service.challenge;

import com.challenge.api.controller.challenge.request.ChallengeCreateRequest;
import com.challenge.api.service.challenge.request.ChallengeAchieveServiceRequest;
import com.challenge.api.service.challenge.request.ChallengeCancelServiceRequest;
import com.challenge.api.service.challenge.request.ChallengeCreateServiceRequest;
import com.challenge.api.service.challenge.request.ChallengeQueryServiceRequest;
import com.challenge.api.service.challenge.request.ChallengeUpdateServiceRequest;
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

    @DisplayName("입력 받은 날짜 기준 이전 2달간의 챌린지 목록을 조회한다.")
    @Test
    void getChallenges() {
        // given
        String targetDate = "2024-12-28";

        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory("카테고리");
        categoryRepository.save(category);

        ChallengeQueryServiceRequest request = ChallengeQueryServiceRequest.builder()
                .queryDate(targetDate)
                .build();

        Challenge challenge1 = createChallenge(member, category, 1, "제목1",
                LocalDateTime.of(2024, 10, 1, 12, 30, 59));
        Challenge challenge2 = createChallenge(member, category, 2, "제목2",
                LocalDateTime.of(2024, 11, 16, 14, 0, 0));
        Challenge challenge3 = createChallenge(member, category, 3, "제목3",
                LocalDateTime.of(2024, 12, 1, 0, 0, 0));
        challengeRepository.saveAll(List.of(challenge1, challenge2, challenge3));

        // when
        List<ChallengeResponse> challenges = challengeService.getChallenges(member, request);

        // then
        assertThat(challenges).hasSize(2)
                .extracting("title", "durationInWeeks")
                .containsExactlyInAnyOrder(
                        tuple("제목2", 2),
                        tuple("제목3", 3)
                );
    }

    @DisplayName("챌린지를 생성한다.")
    @Test
    void createChallenge() {
        // given
        LocalDateTime startDateTime = LocalDateTime.of(2024, 11, 11, 10, 10, 30);

        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory("카테고리");
        categoryRepository.save(category);

        ChallengeCreateRequest request = ChallengeCreateRequest.builder()
                .title("제목")
                .durationInWeeks(2)
                .weeklyGoalCount(3)
                .categoryId(category.getId())
                .color("색상")
                .content("내용")
                .build();

        // when
        ChallengeResponse challengeResponse = challengeService.createChallenge(
                member,
                request.toServiceRequest(),
                startDateTime);

        // then
        assertThat(challengeResponse.getId()).isNotNull();
        assertThat(challengeResponse.getRecord()).isNull();
        assertThat(challengeResponse)
                .extracting("startDateTime", "totalGoalCount")
                .contains("2024-11-11 10:10:30", 6);
    }

    @DisplayName("챌린지를 달성 한다.")
    @Test
    void achieveChallenge() {
        // given
        Member member = createMember();

        memberRepository.save(member);

        Category category = createCategory("카테고리");
        categoryRepository.save(category);

        ChallengeCreateServiceRequest challengeCreateServiceRequest = ChallengeCreateServiceRequest.builder()
                .title("제목")
                .durationInWeeks(2)
                .weeklyGoalCount(3)
                .categoryId(category.getId())
                .color("색상")
                .content("내용")
                .build();

        Challenge challenge = Challenge.create(member, category, challengeCreateServiceRequest,
                LocalDateTime.of(2024, 11, 11, 10, 10, 30));
        challengeRepository.save(challenge);

        Record record1 = createRecord(challenge, LocalDate.of(2024, 11, 11));
        Record record2 = createRecord(challenge, LocalDate.of(2024, 11, 12));
        Record record3 = createRecord(challenge, LocalDate.of(2024, 11, 13));
        recordRepository.saveAll(List.of(record1, record2, record3));

        ChallengeAchieveServiceRequest challengeAchieveServiceRequest = ChallengeAchieveServiceRequest.builder()
                .achieveDate("2024-11-14")
                .build();

        // when
        ChallengeResponse challengeResponse = challengeService.achieveChallenge(
                member,
                challenge.getId(),
                challengeAchieveServiceRequest
        );

        // then
        assertThat(challengeResponse.getId()).isNotNull();
        assertThat(challengeResponse.getTitle()).isEqualTo("제목");
        assertThat(challengeResponse.getRecord().getSuccessDates())
                .containsExactlyInAnyOrder(
                        "2024-11-11",
                        "2024-11-12",
                        "2024-11-13",
                        "2024-11-14"
                );
    }

    @DisplayName("챌린지를 취소한다.")
    @Test
    void cancelChallenge() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory("카테고리");
        categoryRepository.save(category);

        ChallengeCreateServiceRequest request = ChallengeCreateServiceRequest.builder()
                .title("제목")
                .durationInWeeks(2)
                .weeklyGoalCount(3)
                .categoryId(category.getId())
                .color("색상")
                .content("내용")
                .build();

        Challenge challenge = Challenge.create(member, category, request, LocalDateTime.of(2024, 11, 11, 10, 10, 30));
        challengeRepository.save(challenge);

        Record record1 = createRecord(challenge, LocalDate.of(2024, 11, 11));
        Record record2 = createRecord(challenge, LocalDate.of(2024, 11, 12));
        Record record3 = createRecord(challenge, LocalDate.of(2024, 11, 13));
        recordRepository.saveAll(List.of(record1, record2, record3));

        ChallengeCancelServiceRequest cancelRequest = ChallengeCancelServiceRequest.builder()
                .cancelDate("2024-11-13")
                .build();

        // when
        ChallengeResponse challengeResponse = challengeService.cancelChallenge(
                member,
                challenge.getId(),
                cancelRequest
        );

        // then
        assertThat(challengeResponse.getId()).isNotNull();
        assertThat(challengeResponse.getTitle()).isEqualTo("제목");
        assertThat(challengeResponse.getRecord().getSuccessDates())
                .containsExactlyInAnyOrder("2024-11-11", "2024-11-12");
    }

    @DisplayName("챌린지를 수정한다.")
    @Test
    void updateChallenge() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category1 = createCategory("카테고리");
        Category category2 = createCategory("수정된 카테고리");
        categoryRepository.saveAll(List.of(category1, category2));

        ChallengeCreateServiceRequest request = ChallengeCreateServiceRequest.builder()
                .title("제목")
                .durationInWeeks(2)
                .weeklyGoalCount(3)
                .categoryId(category1.getId())
                .color("색상")
                .content("내용")
                .build();

        Challenge challenge = Challenge.create(member, category1, request, LocalDateTime.of(2024, 11, 11, 10, 10, 30));
        challengeRepository.save(challenge);

        ChallengeUpdateServiceRequest updateRequest = ChallengeUpdateServiceRequest.builder()
                .title("수정된 제목")
                .categoryId(category2.getId())
                .color("수정된 색상")
                .content("수정된 내용")
                .build();

        // when
        ChallengeResponse updateChallengeResponse = challengeService.updateChallenge(
                member,
                challenge.getId(),
                updateRequest);

        // then
        assertThat(updateChallengeResponse.getId()).isNotNull();
        assertThat(updateChallengeResponse.getCategory().getName()).isEqualTo("수정된 카테고리");
        assertThat(updateChallengeResponse.getTitle()).isEqualTo("수정된 제목");
        assertThat(updateChallengeResponse.getColor()).isEqualTo("수정된 색상");
        assertThat(updateChallengeResponse.getContent()).isEqualTo("수정된 내용");
    }

    @DisplayName("챌린지를 삭제한다.")
    @Test
    void deleteChallenge() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory("카테고리");
        categoryRepository.save(category);

        ChallengeCreateServiceRequest request1 = ChallengeCreateServiceRequest.builder()
                .title("제목1")
                .durationInWeeks(2)
                .weeklyGoalCount(3)
                .categoryId(category.getId())
                .color("색상1")
                .content("내용1")
                .build();
        ChallengeCreateServiceRequest request2 = ChallengeCreateServiceRequest.builder()
                .title("제목2")
                .durationInWeeks(2)
                .weeklyGoalCount(3)
                .categoryId(category.getId())
                .color("색상2")
                .content("내용2")
                .build();

        Challenge challenge1 = Challenge.create(member, category, request1, LocalDateTime.of(2024, 11, 11, 10, 10, 30));
        Challenge challenge2 = Challenge.create(member, category, request2, LocalDateTime.of(2024, 11, 11, 11, 10, 30));
        challengeRepository.saveAll(List.of(challenge1, challenge2));

        // when
        Long deletedChallenge = challengeService.deleteChallenge(member, challenge2.getId());

        // then
        assertThat(deletedChallenge).isEqualTo(challenge2.getId());
        assertThat(challengeRepository.findAll()).hasSize(1)
                .extracting("title", "content")
                .containsExactly(tuple("제목1", "내용1"));
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
            LocalDateTime startDateTime) {
        return Challenge.builder()
                .member(member)
                .category(category)
                .durationInWeeks(durationInWeeks)
                .title(title)
                .color("#30B0C7")
                .weeklyGoalCount(1)
                .startDateTime(startDateTime)
                .build();
    }

    private Category createCategory(String category) {
        return Category.builder()
                .name(category)
                .build();
    }

    private Record createRecord(Challenge challenge, LocalDate currentDate) {
        return Record.builder()
                .challenge(challenge)
                .successDate(currentDate)
                .build();
    }

}
