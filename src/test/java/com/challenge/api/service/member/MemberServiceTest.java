package com.challenge.api.service.member;

import com.challenge.api.service.member.request.CheckNicknameServiceRequest;
import com.challenge.api.service.member.response.MemberInfoResponse;
import com.challenge.domain.job.Job;
import com.challenge.domain.job.JobRepository;
import com.challenge.domain.member.Gender;
import com.challenge.domain.member.JobYear;
import com.challenge.domain.member.LoginType;
import com.challenge.domain.member.Member;
import com.challenge.domain.member.MemberRepository;
import com.challenge.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


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

    private static final Long MEMBER_SOCIAL_ID = 1L;
    private static final String MEMBER_EMAIL = "test@naver.com";
    private static final String MEMBER_NICKNAME = "test";
    private static final LocalDate MEMBER_BIRTH = LocalDate.of(2000, 1, 1);
    private static final Gender MEMBER_GENDER = Gender.MALE;
    private static final JobYear MEMBER_JOBYEAR = JobYear.LT_1Y;
    private Job MEMBER_JOB;

    @BeforeEach
    void setUp() {
        // Job 데이터 저장
        Job job = Job.builder()
                .code("1")
                .description("1")
                .build();
        MEMBER_JOB = jobRepository.save(job);
    }

    @DisplayName("닉네임 중복 확인 성공: 닉네임 사용 가능 메시지가 반환된다.")
    @Test
    void checkNicknameIsValidSucceeds() {
        // given
        // request 값 세팅
        CheckNicknameServiceRequest request = CheckNicknameServiceRequest.builder()
                .nickname("new nickname")
                .build();

        // when
        String response = memberService.checkNicknameIsValid(request);

        // then
        assertThat(response).isEqualTo("사용 가능한 닉네임입니다.");
    }

    @DisplayName("닉네임 중복 확인 실패: 이미 사용중인 닉네임인 경우 예외가 발생한다.")
    @Test
    void checkNicknameIsValidFailed() {
        // given
        createMember();

        // request 값 세팅
        CheckNicknameServiceRequest request = CheckNicknameServiceRequest.builder()
                .nickname(MEMBER_NICKNAME)
                .build();

        // when // then
        assertThatThrownBy(() -> memberService.checkNicknameIsValid(request))
                .isInstanceOf(GlobalException.class)
                .hasMessage("이미 사용중인 닉네임입니다.");
    }

    @DisplayName("회원 정보 조회 성공: 회원 정보가 반환된다.")
    @Test
    void getMemberInfoSucceeds() {
        // given
        Member member = createMember();

        // when
        MemberInfoResponse response = memberService.getMemberInfo(member);

        // then
        assertThat(response.getNickname()).isEqualTo(MEMBER_NICKNAME);
        assertThat(response.getBirth()).isEqualTo(MEMBER_BIRTH);
        assertThat(response.getGender()).isEqualTo(MEMBER_GENDER);
        assertThat(response.getJobId()).isEqualTo(MEMBER_JOB.getId());
        assertThat(response.getJobYearId()).isEqualTo(MEMBER_JOBYEAR.getId());
    }

    /*   테스트 공통 메소드   */
    private Member createMember() {
        return memberRepository.save(Member.builder()
                .socialId(MEMBER_SOCIAL_ID)
                .email(MEMBER_EMAIL)
                .loginType(LoginType.KAKAO)
                .nickname(MEMBER_NICKNAME)
                .birth(MEMBER_BIRTH)
                .gender(MEMBER_GENDER)
                .jobYear(MEMBER_JOBYEAR)
                .job(MEMBER_JOB)
                .build());
    }

}
