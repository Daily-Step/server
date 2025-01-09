package com.challenge.api.validator;

import com.challenge.api.service.challenge.request.ChallengeCreateServiceRequest;
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
import com.challenge.exception.GlobalException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class ChallengeValidatorTest {

    @Autowired
    private ChallengeValidator challengeValidator;

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

    @DisplayName("존재하지 않는 챌린지 ID로 조회하는 경우 예외가 발생한다.")
    @Test
    void challengeExists() {
        // given
        Member member = createMember("nickname");
        Member savedMember = memberRepository.save(member);

        Long notExistsChallengeId = 999L;

        // when // then
        assertThatThrownBy(() -> challengeValidator.challengeExistsBy(savedMember, notExistsChallengeId))
                .isInstanceOf(GlobalException.class)
                .hasMessage("챌린지 정보를 찾을 수 없습니다. 관리자에게 문의 바랍니다.");
    }

    @DisplayName("챌린지 달성 기록에 중복된 기록이 있는 경우 예외가 발생한다.")
    @Test
    void duplicateRecord() {
        // given
        LocalDate currentDate = LocalDate.now();

        Member member = createMember("nickname");
        memberRepository.save(member);

        Category category = createCategory();
        categoryRepository.save(category);

        ChallengeCreateServiceRequest request = createChallengeServiceRequest();

        Challenge challenge = Challenge.create(member, category, request, LocalDateTime.now());
        challengeRepository.save(challenge);

        Record record = createRecord(challenge, currentDate);
        recordRepository.save(record);

        // when // then
        assertThatThrownBy(() -> challengeValidator.hasDuplicateRecordFor(challenge, currentDate))
                .isInstanceOf(GlobalException.class)
                .hasMessage("오늘 이미 해당 챌린지를 달성했습니다.");
    }

    private Member createMember(String nickname) {
        Job job = Job.builder()
                .code("1")
                .description("1")
                .build();
        jobRepository.save(job);

        return Member.builder()
                .socialId(1L)
                .email("eamil")
                .loginType(LoginType.KAKAO)
                .nickname(nickname)
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
