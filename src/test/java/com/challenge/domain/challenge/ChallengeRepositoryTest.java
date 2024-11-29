package com.challenge.domain.challenge;

import com.challenge.domain.job.Job;
import com.challenge.domain.job.JobRepository;
import com.challenge.domain.member.Gender;
import com.challenge.domain.member.JobYear;
import com.challenge.domain.member.LoginType;
import com.challenge.domain.member.Member;
import com.challenge.domain.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

@ActiveProfiles("test")
@SpringBootTest
class ChallengeRepositoryTest {

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JobRepository jobRepository;

    @DisplayName("중복되는 챌린지 제목을 입력하면 예외가 발생한다.")
    @Test
    void existsByMemberIdAndTitle() throws Exception {
        // given
        Job job = Job.builder()
                .code("1")
                .description("1")
                .build();
        jobRepository.save(job);

        Member member = Member.builder()
                .socialId(1L)
                .email("test@naver.com")
                .nickname("test")
                .birth(LocalDate.of(1995, 1, 1))
                .gender(Gender.MALE)
                .jobYear(JobYear.LT_1Y)
                .loginType(LoginType.KAKAO)
                .job(job)
                .build();
        memberRepository.save(member);

        
    }

}
