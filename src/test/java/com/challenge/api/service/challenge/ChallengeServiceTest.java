package com.challenge.api.service.challenge;

import com.challenge.api.controller.challenge.request.ChallengeCancelRequest;
import com.challenge.api.controller.challenge.request.ChallengeCreateRequest;
import com.challenge.api.controller.challenge.request.ChallengeQueryRequest;
import com.challenge.api.service.challenge.request.ChallengeAchieveServiceRequest;
import com.challenge.api.service.challenge.request.ChallengeCreateServiceRequest;
import com.challenge.api.service.challenge.request.ChallengeUpdateServiceRequest;
import com.challenge.api.service.challenge.response.ChallengeResponse;
import com.challenge.domain.category.Category;
import com.challenge.domain.category.CategoryRepository;
import com.challenge.domain.challenge.Challenge;
import com.challenge.domain.challenge.ChallengeRepository;
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
    private ChallengeRecordRepository challengeRecordRepository;

    @Autowired
    private ChallengeService challengeService;

    @AfterEach
    void tearDown() {
        challengeRecordRepository.deleteAllInBatch();
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

        ChallengeQueryRequest request = ChallengeQueryRequest.builder()
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
        List<ChallengeResponse> challenges = challengeService.getChallenges(member, request.toServiceRequest());

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

        ChallengeRecord challengeRecord1 = createRecord(challenge, LocalDate.of(2024, 11, 11));
        ChallengeRecord challengeRecord2 = createRecord(challenge, LocalDate.of(2024, 11, 12));
        ChallengeRecord challengeRecord3 = createRecord(challenge, LocalDate.of(2024, 11, 13));
        challengeRecordRepository.saveAll(List.of(challengeRecord1, challengeRecord2, challengeRecord3));

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

    @DisplayName("챌린지 달성을 취소한다.")
    @Test
    void cancelChallenge() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory("카테고리");
        categoryRepository.save(category);

        ChallengeCreateRequest challengeCreateRequest = ChallengeCreateRequest.builder()
                .title("제목")
                .durationInWeeks(2)
                .weeklyGoalCount(3)
                .categoryId(category.getId())
                .color("색상")
                .content("내용")
                .build();

        Challenge challenge1 = Challenge.create(
                member,
                category,
                challengeCreateRequest.toServiceRequest(),
                LocalDateTime.of(2024, 11, 11, 10, 10, 30)
        );
        Challenge challenge2 = Challenge.create(
                member,
                category,
                challengeCreateRequest.toServiceRequest(),
                LocalDateTime.of(2024, 12, 1, 10, 10, 30)
        );
        challengeRepository.saveAll(List.of(challenge1, challenge2));

        // 챌린지 달성
        challengeService.achieveChallenge(member, challenge1.getId(),
                ChallengeAchieveServiceRequest.builder().achieveDate("2024-11-11").build());
        challengeService.achieveChallenge(member, challenge1.getId(),
                ChallengeAchieveServiceRequest.builder().achieveDate("2024-11-12").build());
        challengeService.achieveChallenge(member, challenge1.getId(),
                ChallengeAchieveServiceRequest.builder().achieveDate("2024-11-13").build());

        challengeService.achieveChallenge(member, challenge2.getId(),
                ChallengeAchieveServiceRequest.builder().achieveDate("2024-12-02").build());
        challengeService.achieveChallenge(member, challenge2.getId(),
                ChallengeAchieveServiceRequest.builder().achieveDate("2024-12-03").build());

        // 챌린지 취소
        ChallengeCancelRequest challengeCancelRequest1 = ChallengeCancelRequest.builder()
                .cancelDate("2024-11-13")
                .build();
        ChallengeCancelRequest challengeCancelRequest2 = ChallengeCancelRequest.builder()
                .cancelDate("2024-12-03")
                .build();

        // when - 취소된 챌린지 상태 확인
        ChallengeResponse challengeResponse1 = challengeService.cancelChallenge(
                member,
                challenge1.getId(),
                challengeCancelRequest1.toServiceRequest()
        );
        ChallengeResponse challengeResponse2 = challengeService.cancelChallenge(
                member,
                challenge2.getId(),
                challengeCancelRequest2.toServiceRequest()
        );

        // then - 취소된 챌린지 검증
        assertThat(challengeResponse1.getId()).isNotNull();
        assertThat(challengeResponse1.getTitle()).isEqualTo("제목");
        assertThat(challengeResponse1.getRecord().getSuccessDates())
                .containsExactlyInAnyOrder("2024-11-11", "2024-11-12");

        assertThat(challengeResponse2.getId()).isNotNull();
        assertThat(challengeResponse2.getTitle()).isEqualTo("제목");
        assertThat(challengeResponse2.getRecord().getSuccessDates())
                .containsExactlyInAnyOrder("2024-12-02");

        // given - 모든 챌린지 조회를 위한 요청 request
        ChallengeQueryRequest challengeQueryRequest = ChallengeQueryRequest.builder()
                .queryDate("2024-12-28")
                .build();

        // when - 모든 챌린지 조회
        List<ChallengeResponse> challenges = challengeService.getChallenges(
                member,
                challengeQueryRequest.toServiceRequest()
        );

        // then - 조회된 챌린지들에 대한 검증
        assertThat(challenges).hasSize(2)
                .extracting("record.successDates")
                .containsExactlyInAnyOrder(
                        List.of("2024-11-11", "2024-11-12"),
                        List.of("2024-12-02")
                );
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

        ChallengeCreateRequest request1 = ChallengeCreateRequest.builder()
                .title("제목1")
                .durationInWeeks(2)
                .weeklyGoalCount(3)
                .categoryId(category.getId())
                .color("색상1")
                .content("내용1")
                .build();
        ChallengeCreateRequest request2 = ChallengeCreateRequest.builder()
                .title("제목2")
                .durationInWeeks(2)
                .weeklyGoalCount(3)
                .categoryId(category.getId())
                .color("색상2")
                .content("내용2")
                .build();

        Challenge challenge1 = Challenge.create(member, category, request1.toServiceRequest()
                , LocalDateTime.of(2024, 11, 11, 10, 10, 30));
        Challenge challenge2 = Challenge.create(member, category, request2.toServiceRequest(),
                LocalDateTime.of(2024, 11, 11, 11, 10, 30));
        challengeRepository.saveAll(List.of(challenge1, challenge2));

        // when - 챌린지 삭제
        Long deletedChallenge = challengeService.deleteChallenge(member, challenge2.getId());

        // then - 삭제된 챌린지 검증
        assertThat(deletedChallenge).isEqualTo(challenge2.getId());
        assertThat(challengeRepository.findAll()).hasSize(2)
                .extracting("title", "content", "isDeleted")
                .containsExactly(
                        tuple("제목1", "내용1", false),
                        tuple("제목2", "내용2", true)
                );

        // given - 모든 챌린지 조회를 위한 요청 request
        ChallengeQueryRequest challengeQueryRequest = ChallengeQueryRequest.builder()
                .queryDate("2024-12-28")
                .build();

        // when - 모든 챌린지 조회
        List<ChallengeResponse> challenges = challengeService.getChallenges(
                member,
                challengeQueryRequest.toServiceRequest()
        );

        // then - 조회된 챌린지들에 대한 검증
        assertThat(challenges).hasSize(1)
                .extracting("title", "content")
                .containsExactlyInAnyOrder(
                        tuple("제목1", "내용1")
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

    private ChallengeRecord createRecord(Challenge challenge, LocalDate currentDate) {
        return ChallengeRecord.builder()
                .challenge(challenge)
                .recordDate(currentDate)
                .isSucceed(true)
                .build();
    }

}
