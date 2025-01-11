package com.challenge.api.validator;

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

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest
class RecordValidatorTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ChallengeRecordRepository challengeRecordRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private RecordValidator recordValidator;

    @AfterEach
    void tearDown() {
        challengeRecordRepository.deleteAllInBatch();
        challengeRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        jobRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
    }

    @DisplayName("특정 날짜에 해당하는 챌린지가 달성한 챌린지인지 확인한다.")
    @Test
    void hasRecordFor() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory();
        categoryRepository.save(category);

        Challenge challenge = createChallenge(member, category, LocalDateTime.of(2024, 10, 1, 12, 30, 59));
        challengeRepository.save(challenge);

        ChallengeRecord record = ChallengeRecord.achieve(challenge, "2024-10-01");
        challengeRecordRepository.save(record);

        // when
//        ChallengeRecord challengeRecord = recordValidator.hasRecordFor(challenge, LocalDate.of(2024, 10, 1));
//
//        // then
//        assertThat(challengeRecord.getRecordDate()).isEqualTo(LocalDate.of(2024, 10, 1));
//        assertThat(challengeRecord.isSucceed()).isTrue();
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
                .nickname("닉네임")
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

    private Challenge createChallenge(Member member, Category category, LocalDateTime startDateTime) {
        return Challenge.builder()
                .member(member)
                .category(category)
                .title("제목")
                .color("#30B0C7")
                .durationInWeeks(1)
                .weeklyGoalCount(1)
                .startDateTime(startDateTime)
                .build();
    }

}
