package com.challenge.api.service.auth;

import com.challenge.api.service.auth.request.KakaoLoginServiceRequest;
import com.challenge.api.service.auth.request.KakaoSigninServiceRequest;
import com.challenge.api.service.auth.request.ReissueTokenServiceRequest;
import com.challenge.api.service.auth.response.LoginResponse;
import com.challenge.api.service.auth.response.ReissueTokenResponse;
import com.challenge.api.service.auth.response.SocialInfoResponse;
import com.challenge.domain.job.Job;
import com.challenge.domain.job.JobRepository;
import com.challenge.domain.member.Gender;
import com.challenge.domain.member.JobYear;
import com.challenge.domain.member.LoginType;
import com.challenge.domain.member.Member;
import com.challenge.domain.member.MemberRepository;
import com.challenge.exception.GlobalException;
import com.challenge.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @MockBean
    private KakaoApiService kakaoApiService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private Job job;

    private static final Long MOCK_SOCIAL_ID = 1L;
    private static final String MOCK_EMAIL = "test@naver.com";
    private static final String MOCK_NICKNAME = "test";
    private static final LocalDate MOCK_BIRTH = LocalDate.of(2000, 1, 1);
    private static final String MOCK_KAKAO_ACCESS_TOKEN = "test-access-token";

    @BeforeEach
    void setUp() {
        // Job 데이터 저장
        job = Job.builder()
                .code("1")
                .description("1")
                .build();
        job = jobRepository.save(job);
    }

    @DisplayName("카카오 로그인 성공: 토큰과 회원id가 반환된다.")
    @Test
    void kakaoLoginSucceeds() {
        // given
        Member member = createMember();

        // 카카오 API 호출 mock 처리
        mockKakaoApiResponse(MOCK_SOCIAL_ID);

        // request 값 세팅
        KakaoLoginServiceRequest request = KakaoLoginServiceRequest.builder()
                .accessToken(MOCK_KAKAO_ACCESS_TOKEN)
                .build();

        // when
        LoginResponse response = authService.kakaoLogin(request);

        // then
        assertThat(response.getMemberId()).isEqualTo(member.getId());
        assertThat(response.getAccessToken()).isNotEmpty();
        assertThat(response.getRefreshToken()).isNotEmpty();
    }

    @DisplayName("카카오 로그인 실패: 가입된 회원 정보가 없는 경우 예외가 발생한다.")
    @Test
    void kakaoLoginFailedWhenMemberNotFound() {
        // given
        // 카카오 API 호출 mock 처리
        mockKakaoApiResponse(MOCK_SOCIAL_ID);

        // request 값 세팅
        KakaoLoginServiceRequest request = KakaoLoginServiceRequest.builder()
                .accessToken(MOCK_KAKAO_ACCESS_TOKEN)
                .build();

        // when // then
        assertThatThrownBy(() -> authService.kakaoLogin(request))
                .isInstanceOf(GlobalException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @DisplayName("카카오 회원가입 성공: 토큰과 회원id가 반환된다.")
    @Test
    void kakaoSigninSucceeds() {
        // given
        // 카카오 API 호출 mock 처리
        mockKakaoApiResponse(MOCK_SOCIAL_ID);

        // request 값 세팅
        KakaoSigninServiceRequest request = KakaoSigninServiceRequest.builder()
                .accessToken(MOCK_KAKAO_ACCESS_TOKEN)
                .nickname(MOCK_NICKNAME)
                .birth(MOCK_BIRTH)
                .gender(Gender.FEMALE)
                .jobId(job.getId())
                .yearId(1)
                .build();

        // when
        LoginResponse response = authService.kakaoSignin(request);

        // then
        Member savedMember = memberRepository.findBySocialIdAndLoginType(MOCK_SOCIAL_ID, LoginType.KAKAO);
        assertThat(response.getMemberId()).isEqualTo(savedMember.getId());
        assertThat(response.getAccessToken()).isNotEmpty();
        assertThat(response.getRefreshToken()).isNotEmpty();
    }

    @DisplayName("카카오 회원가입 실패: 이미 가입된 회원인 경우 예외가 발생한다.")
    @Test
    void kakaoSigninFailedWhenExistMember() {
        // given
        // 회원 데이터 생성
        createMember();

        // 카카오 API 호출 mock 처리
        mockKakaoApiResponse(MOCK_SOCIAL_ID);

        // request 값 세팅
        KakaoSigninServiceRequest request = KakaoSigninServiceRequest.builder()
                .accessToken(MOCK_KAKAO_ACCESS_TOKEN)
                .nickname(MOCK_NICKNAME)
                .birth(MOCK_BIRTH)
                .gender(Gender.FEMALE)
                .jobId(job.getId())
                .yearId(1)
                .build();

        // when // then
        assertThatThrownBy(() -> authService.kakaoSignin(request))
                .isInstanceOf(GlobalException.class)
                .hasMessage("이미 존재하는 회원입니다.");
    }

    @DisplayName("카카오 회원가입 실패: 중복된 닉네임을 요청하는 경우 예외가 발생한다.")
    @Test
    void kakaoSigninFailedWhenDuplicatedNickname() {
        // given
        // 회원 데이터 생성
        createMember();

        // 카카오 API 호출 mock 처리
        mockKakaoApiResponse(2L);

        // request 값 세팅
        KakaoSigninServiceRequest request = KakaoSigninServiceRequest.builder()
                .accessToken(MOCK_KAKAO_ACCESS_TOKEN)
                .nickname(MOCK_NICKNAME) // 동일한 닉네임으로 요청
                .birth(MOCK_BIRTH)
                .gender(Gender.FEMALE)
                .jobId(job.getId())
                .yearId(1)
                .build();

        // when // then
        assertThatThrownBy(() -> authService.kakaoSignin(request))
                .isInstanceOf(GlobalException.class)
                .hasMessage("이미 사용중인 닉네임입니다.");
    }

    @DisplayName("토큰 재발급 성공: 토큰이 반환된다.")
    @Test
    void reissueTokenSucceeds() {
        // given
        Member member = createMember();
        String refreshToken = jwtUtil.createRefreshToken(member.getId());

        // request 값 세팅
        ReissueTokenServiceRequest request = ReissueTokenServiceRequest.builder()
                .refreshToken(refreshToken)
                .build();

        // when
        ReissueTokenResponse response = authService.reissueToken(request);

        // then
        assertThat(response.getAccessToken()).isNotEmpty();
        assertThat(response.getRefreshToken()).isNotEmpty();
    }

    @DisplayName("토큰 재발급 실패: refresh token이 만료된 경우 예외가 발생한다.")
    @Test
    void reissueTokenFailedWhenExpiredToken() {
        // given
        Member member = createMember();
        String refreshToken = jwtUtil.createRefreshTokenForTest(member.getId());

        // request 값 세팅
        ReissueTokenServiceRequest request = ReissueTokenServiceRequest.builder()
                .refreshToken(refreshToken)
                .build();

        // when // then
        assertThatThrownBy(() -> authService.reissueToken(request))
                .isInstanceOf(GlobalException.class)
                .hasMessage("리프레쉬 토큰이 만료되었습니다. 다시 로그인 해주세요");
    }

    @DisplayName("토큰 재발급 실패: 토큰에 담긴 memberId에 해당하는 회원이 존재하지 않는 경우 예외가 발생한다.")
    @Test
    void reissueTokenFailedWhenMemberNotFound() {
        // given
        String refreshToken = jwtUtil.createRefreshToken(1L);

        // request 값 세팅
        ReissueTokenServiceRequest request = ReissueTokenServiceRequest.builder()
                .refreshToken(refreshToken)
                .build();

        // when // then
        assertThatThrownBy(() -> authService.reissueToken(request))
                .isInstanceOf(GlobalException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
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

    private SocialInfoResponse createMockSocialInfoResponse(Long socialId, LoginType loginType) {
        return SocialInfoResponse.builder()
                .socialId(socialId)
                .email(MOCK_EMAIL)
                .loginType(loginType)
                .build();
    }

    private void mockKakaoApiResponse(Long socialId) {
        given(kakaoApiService.getUserInfo(MOCK_KAKAO_ACCESS_TOKEN))
                .willReturn(createMockSocialInfoResponse(socialId, LoginType.KAKAO));
    }

}
