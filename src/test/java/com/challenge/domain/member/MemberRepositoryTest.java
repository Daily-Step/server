package com.challenge.domain.member;

import com.challenge.domain.job.Job;
import com.challenge.domain.job.JobRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    JobRepository jobRepository;

    private static final Long MEMBER_SOCIAL_ID = 1L;
    private static final String MEMBER_EMAIL = "test@naver.com";
    private static final LoginType MEMBER_LOGIN_TYPE = LoginType.KAKAO;
    private static final String MEMBER_NICKNAME = "test";
    private static final LocalDate MEMBER_BIRTH = LocalDate.of(2000, 1, 1);
    private static final Gender MEMBER_GENDER = Gender.MALE;
    private static final JobYear MEMBER_JOBYEAR = JobYear.LT_1Y;

    private Job MEMBER_JOB;

    @DisplayName("socialId와 loginType으로 회원을 조회한다.")
    @Test
    void findBySocialIdAndLoginType() {
        // given
        Member member = createMember();

        // when
        Member resultMember = memberRepository.findBySocialIdAndLoginType(MEMBER_SOCIAL_ID, MEMBER_LOGIN_TYPE);

        // then
        assertEquals(resultMember.getId(), member.getId());
        assertEquals(resultMember.getSocialId(), MEMBER_SOCIAL_ID);
        assertEquals(resultMember.getLoginType(), MEMBER_LOGIN_TYPE);
    }

    @DisplayName("socialId와 loginType으로 특정 회원의 존재 여부를 조회한다.")
    @Test
    void existsBySocialIdAndLoginType() {
        // given
        createMember();

        // when
        boolean exists = memberRepository.existsBySocialIdAndLoginType(MEMBER_SOCIAL_ID, MEMBER_LOGIN_TYPE);

        // then
        assertTrue(exists);
    }

    @DisplayName("특정 닉네임을 가진 회원의 존재 여부를 조회한다.")
    @Test
    void existsByNickname() {
        // given
        createMember();

        // when
        boolean exists = memberRepository.existsByNickname(MEMBER_NICKNAME);

        // then
        assertTrue(exists);

    }

    /*  테스트 공통 메소드  */
    private Job createJob() {
        MEMBER_JOB = jobRepository.save(Job.builder()
                .code("1")
                .description("1")
                .build());
        return MEMBER_JOB;
    }

    private Member createMember() {
        return memberRepository.save(Member.builder()
                .socialId(MEMBER_SOCIAL_ID)
                .email(MEMBER_EMAIL)
                .loginType(MEMBER_LOGIN_TYPE)
                .nickname(MEMBER_NICKNAME)
                .birth(MEMBER_BIRTH)
                .gender(MEMBER_GENDER)
                .jobYear(MEMBER_JOBYEAR)
                .job(MEMBER_JOB)
                .build());
    }

}
