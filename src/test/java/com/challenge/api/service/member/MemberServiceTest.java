package com.challenge.api.service.member;

import com.challenge.api.service.member.response.MemberInfoResponse;
import com.challenge.domain.job.Job;
import com.challenge.domain.job.JobRepository;
import com.challenge.domain.member.Gender;
import com.challenge.domain.member.JobYear;
import com.challenge.domain.member.LoginType;
import com.challenge.domain.member.Member;
import com.challenge.domain.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JobRepository jobRepository;

    private static final Long MOCK_SOCIAL_ID = 1L;
    private static final String MOCK_EMAIL = "test@naver.com";
    private static final String MOCK_NICKNAME = "test";
    private static final LocalDate MOCK_BIRTH = LocalDate.of(2000, 1, 1);

    private Job job;

    @BeforeEach
    void setUp() {
        // Job 데이터 저장
        job = Job.builder()
                .code("1")
                .description("1")
                .build();
        job = jobRepository.save(job);
    }

    @DisplayName("회원 정보 조회 성공: 회원 정보가 반환된다.")
    @Test
    void getMemberInfoSucceeds() {
        // given
        Member member = createMember();

        // when
        MemberInfoResponse response = memberService.getMemberInfo(member);

        // then
        assertThat(response.getNickname()).isEqualTo(MOCK_NICKNAME);
        assertThat(response.getBirth()).isEqualTo(MOCK_BIRTH);
        assertThat(response.getGender()).isEqualTo(member.getGender());
        assertThat(response.getJobId()).isEqualTo(job.getId());
        assertThat(response.getJobYearId()).isEqualTo(member.getJobYear().getId());
    }

    /*   테스트 공통 메소드   */
    private Member createMember() {
        return memberRepository.save(Member.builder()
                .socialId(MOCK_SOCIAL_ID)
                .email(MOCK_EMAIL)
                .loginType(LoginType.KAKAO)
                .nickname(MOCK_NICKNAME)
                .birth(MOCK_BIRTH)
                .gender(Gender.MALE)
                .jobYear(JobYear.LT_1Y)
                .job(job)
                .build());
    }

}
