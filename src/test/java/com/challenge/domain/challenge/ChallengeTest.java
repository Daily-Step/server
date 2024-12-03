package com.challenge.domain.challenge;

import com.challenge.api.service.challenge.request.ChallengeCreateServiceRequest;
import com.challenge.domain.category.Category;
import com.challenge.domain.job.Job;
import com.challenge.domain.member.Gender;
import com.challenge.domain.member.JobYear;
import com.challenge.domain.member.LoginType;
import com.challenge.domain.member.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ChallengeTest {

    private Category category;
    private Job job;
    private Member member;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .name("카테고리")
                .build();

        job = Job.builder()
                .code("코드")
                .description("설명")
                .build();
    }

    @DisplayName("챌린지 생성 시 기록은 비어있다.")
    @Test
    void recordInit() {
        // given
        member = createMember();
        ChallengeCreateServiceRequest request = ChallengeCreateServiceRequest.builder()
                .title("제목")
                .durationInWeeks(1)
                .weeklyGoalCount(1)
                .categoryId(1L)
                .color("색상")
                .content("내용")
                .build();

        // when
        Challenge challenge = Challenge.create(member, category, request, LocalDateTime.now());

        // then
        assertThat(challenge.getRecords()).isEmpty();
    }

    @DisplayName("챌린지 생성 시 총 목표 횟수는 기간 * 목표 횟수로 계산한다. ")
    @Test
    void calculateTotalGoalCount() {
        // given
        member = createMember();
        ChallengeCreateServiceRequest request = ChallengeCreateServiceRequest.builder()
                .title("제목")
                .durationInWeeks(2)
                .weeklyGoalCount(3)
                .categoryId(1L)
                .color("색상")
                .content("내용")
                .build();

        // when
        Challenge challenge = Challenge.create(member, category, request, LocalDateTime.now());

        // then
        assertThat(challenge.getTotalGoalCount()).isEqualTo(6);
    }

    @DisplayName("챌린지 생성 시 종료 시간은 시작 시간으로부터 목표주 후의 마지막 시간(23:59:59)으로 설정된다.")
    @Test
    void calculateEndDateTime() {
        LocalDateTime startDateTime = LocalDateTime.of(2024, 12, 3, 12, 0, 0);

        // given
        member = createMember();
        ChallengeCreateServiceRequest request = ChallengeCreateServiceRequest.builder()
                .title("제목")
                .durationInWeeks(1)
                .weeklyGoalCount(3)
                .categoryId(1L)
                .color("색상")
                .content("내용")
                .build();

        // when
        Challenge challenge = Challenge.create(member, category, request, startDateTime);

        // then
        assertThat(challenge.getEndDateTime()).isEqualTo(LocalDateTime.of(2024, 12, 10, 23, 59, 59));
        assertThat(challenge.getEndDateTime()).isEqualTo(startDateTime.plusWeeks(1)
                .toLocalDate()
                .plusDays(1)
                .atStartOfDay()
                .minusSeconds(1));
    }

    private Member createMember() {
        return Member.builder()
                .socialId(1L)
                .email("email")
                .loginType(LoginType.KAKAO)
                .nickname("nickname")
                .birth(LocalDate.of(1999, 1, 1))
                .gender(Gender.MALE)
                .jobYear(JobYear.LT_1Y)
                .job(job)
                .build();
    }

}
