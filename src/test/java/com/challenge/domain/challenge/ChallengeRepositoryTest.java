package com.challenge.domain.challenge;

import com.challenge.domain.category.Category;
import com.challenge.domain.category.CategoryRepository;
import com.challenge.domain.job.Job;
import com.challenge.domain.job.JobRepository;
import com.challenge.domain.member.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class ChallengeRepositoryTest {

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @DisplayName("진행중인 챌린지 목록을 조회한다.")
    @Test
    void findChallengesBy() {
        // given
        LocalDateTime currentDateTime = LocalDateTime.of(2024, 11, 30, 23, 59, 59);

        Category category = createCategory();
        categoryRepository.save(category);

        Job job = createJob();
        jobRepository.save(job);

        Member member = createMember(job);
        memberRepository.save(member);

        Challenge challenge1 = createChallenge(
                member,
                category,
                1,
                currentDateTime,
                LocalDateTime.of(2024, 11, 30, 23, 59, 59)
        );
        Challenge challenge2 = createChallenge(
                member,
                category,
                2,
                LocalDateTime.of(2024, 11, 16, 14, 0, 0),
                LocalDateTime.of(2024, 11, 23, 14, 0, 0)
        );
        Challenge challenge3 = createChallenge(
                member,
                category,
                1,
                LocalDateTime.of(2024, 11, 16, 14, 0, 0),
                LocalDateTime.of(2024, 11, 23, 23, 59, 59)
        );
        challengeRepository.saveAll(List.of(challenge1, challenge2, challenge3));

        // when
        List<Challenge> challenges = challengeRepository.findChallengesBy(member.getId(), currentDateTime);

        // then
        assertThat(challenges).hasSize(1);
    }

    private Category createCategory() {
        return Category.builder()
                .name("운동")
                .isDeleted(false)
                .build();
    }

    private Job createJob() {
        return Job.builder()
                .code("1")
                .description("1")
                .build();
    }

    private Member createMember(Job job) {
        return Member.builder()
                .socialId(1L)
                .email("test@naver.com")
                .nickname("닉네임")
                .birth(LocalDate.of(1995, 1, 1))
                .gender(Gender.MALE)
                .jobYear(JobYear.LT_1Y)
                .loginType(LoginType.KAKAO)
                .job(job)
                .build();
    }

    private Challenge createChallenge(Member member, Category category, int durationInWeeks,
                                      LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return Challenge.builder()
                .member(member)
                .category(category)
                .durationInWeeks(durationInWeeks)
                .title("제목")
                .color("#30B0C7")
                .isDeleted(false)
                .weeklyGoalCount(1)
                .totalGoalCount(1)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
    }

}
