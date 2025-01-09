package com.challenge.api.controller.auth;

import com.challenge.api.controller.ControllerTestSupport;
import com.challenge.api.controller.auth.request.KakaoLoginRequest;
import com.challenge.api.controller.auth.request.KakaoSigninRequest;
import com.challenge.api.controller.auth.request.ReissueTokenRequest;
import com.challenge.api.service.auth.AuthService;
import com.challenge.api.service.auth.response.LoginResponse;
import com.challenge.api.service.auth.response.ReissueTokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest extends ControllerTestSupport {

    @MockitoBean
    private AuthService authService;

    private static final String MOCK_KAKAO_ACCESS_TOKEN = "test-kakao-access-token";
    private static final String MOCK_ACCESS_TOKEN = "test-access-token";
    private static final String MOCK_REFRESH_TOKEN = "test-refresh-token";

    private LoginResponse mockLoginResponse;
    private ReissueTokenResponse mockReissueTokenResponse;

    @Nested
    @DisplayName("카카오 로그인")
    class KakaoLoginTests {

        @BeforeEach
        void setUp() {
            // 서비스 mock 처리
            mockLoginResponse = LoginResponse.builder()
                    .memberId(1L)
                    .accessToken(MOCK_ACCESS_TOKEN)
                    .refreshToken(MOCK_REFRESH_TOKEN)
                    .build();
            given(authService.kakaoLogin(any())).willReturn(mockLoginResponse);
        }

        @DisplayName("카카오 로그인 성공")
        @Test
        void kakaoLoginSucceeds() throws Exception {
            // given
            KakaoLoginRequest request = KakaoLoginRequest.builder()
                    .accessToken(MOCK_ACCESS_TOKEN)
                    .build();

            // when // then
            mockMvc.perform(post("/api/v1/auth/login/kakao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.code").isEmpty())
                    .andExpect(jsonPath("$.url").isEmpty())
                    .andExpect(jsonPath("$.data.accessToken").value(MOCK_ACCESS_TOKEN))
                    .andExpect(jsonPath("$.data.refreshToken").value(MOCK_REFRESH_TOKEN));
        }

        @DisplayName("카카오 로그인 실패: access token이 공백인 경우 에러 응답을 반환한다.")
        @Test
        void kakaoLoginFailedWhenAccessTokenIsBlank() throws Exception {
            // given
            KakaoLoginRequest request = KakaoLoginRequest.builder()
                    .accessToken(" ")
                    .build();

            // when //then
            mockMvc.perform(post("/api/v1/auth/login/kakao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.message").value("access token은 필수 입력값입니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.url").value("/api/v1/auth/login/kakao"));
        }

    }

    @Nested
    @DisplayName("카카오 회원가입")
    class KakaoSigninTests {

        @BeforeEach
        void setUp() {
            // 서비스 mock 처리
            mockLoginResponse = LoginResponse.builder()
                    .memberId(1L)
                    .accessToken(MOCK_ACCESS_TOKEN)
                    .refreshToken(MOCK_REFRESH_TOKEN)
                    .build();
            given(authService.kakaoSignin(any())).willReturn(mockLoginResponse);
        }

        @DisplayName("카카오 회원가입 성공")
        @Test
        void kakaoSigninSucceeds() throws Exception {
            // given
            KakaoSigninRequest request = KakaoSigninRequest.builder()
                    .accessToken(MOCK_KAKAO_ACCESS_TOKEN)
                    .nickname(MOCK_NICKNAME)
                    .birth(MOCK_BIRTH)
                    .gender(MOCK_GENDER)
                    .jobId(MOCK_JOB.getId())
                    .yearId(MOCK_JOBYEAR.getId())
                    .build();

            // when // then
            mockMvc.perform(post("/api/v1/auth/signin/kakao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.code").isEmpty())
                    .andExpect(jsonPath("$.url").isEmpty())
                    .andExpect(jsonPath("$.data.memberId").value(1L))
                    .andExpect(jsonPath("$.data.accessToken").value(MOCK_ACCESS_TOKEN))
                    .andExpect(jsonPath("$.data.refreshToken").value(MOCK_REFRESH_TOKEN));
        }

        @DisplayName("카카오 회원가입 실패: access token이 공백인 경우 에러 응답을 반환한다.")
        @Test
        void kakaoSigninFailedWhenAccessTokenIsBlank() throws Exception {
            // given
            KakaoSigninRequest request = KakaoSigninRequest.builder()
                    .accessToken(" ")
                    .nickname(MOCK_NICKNAME)
                    .birth(MOCK_BIRTH)
                    .gender(MOCK_GENDER)
                    .jobId(MOCK_JOB.getId())
                    .yearId(MOCK_JOBYEAR.getId())
                    .build();

            // when //then
            mockMvc.perform(post("/api/v1/auth/signin/kakao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.message").value("access token은 필수 입력값입니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.url").value("/api/v1/auth/signin/kakao"));
        }

        @DisplayName("카카오 회원가입 실패: nickname이 공백인 경우 에러 응답을 반환한다.")
        @Test
        void kakaoSigninFailedWhenNicknameIsBlank() throws Exception {
            // given
            KakaoSigninRequest request = KakaoSigninRequest.builder()
                    .accessToken(MOCK_KAKAO_ACCESS_TOKEN)
                    .nickname(" ")
                    .birth(MOCK_BIRTH)
                    .gender(MOCK_GENDER)
                    .jobId(MOCK_JOB.getId())
                    .yearId(MOCK_JOBYEAR.getId())
                    .build();

            // when // then
            mockMvc.perform(post("/api/v1/auth/signin/kakao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.message").value("닉네임은 4~10자이며, 띄어쓰기와 특수문자를 사용할 수 없습니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.url").value("/api/v1/auth/signin/kakao"));
        }

        @DisplayName("카카오 회원가입 실패: nickname이 길이/문자 조건을 위반한 경우 에러 응답을 반환한다.")
        @Test
        void kakaoSigninFailedWhenNicknameIsInvalid() throws Exception {
            // given
            KakaoSigninRequest request = KakaoSigninRequest.builder()
                    .accessToken(MOCK_KAKAO_ACCESS_TOKEN)
                    .nickname("abcdefg!!@12")
                    .birth(MOCK_BIRTH)
                    .gender(MOCK_GENDER)
                    .jobId(MOCK_JOB.getId())
                    .yearId(MOCK_JOBYEAR.getId())
                    .build();

            // when // then
            mockMvc.perform(post("/api/v1/auth/signin/kakao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.message").value("닉네임은 4~10자이며, 띄어쓰기와 특수문자를 사용할 수 없습니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.url").value("/api/v1/auth/signin/kakao"));
        }

        @DisplayName("카카오 회원가입 실패: birth가 null인 경우 에러 응답을 반환한다.")
        @Test
        void kakaoSigninFailedWhenBirthIsNull() throws Exception {
            // given
            KakaoSigninRequest request = KakaoSigninRequest.builder()
                    .accessToken(MOCK_KAKAO_ACCESS_TOKEN)
                    .nickname(MOCK_NICKNAME)
                    .birth(null)
                    .gender(MOCK_GENDER)
                    .jobId(MOCK_JOB.getId())
                    .yearId(MOCK_JOBYEAR.getId())
                    .build();

            // when // then
            mockMvc.perform(post("/api/v1/auth/signin/kakao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.message").value("birth는 필수 입력값입니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.url").value("/api/v1/auth/signin/kakao"));
        }

        @DisplayName("카카오 회원가입 실패: birth가 과거 날짜가 아닌 경우 에러 응답을 반환한다.")
        @Test
        void kakaoSigninFailedWhenBirthIsNotPast() throws Exception {
            // given
            KakaoSigninRequest request = KakaoSigninRequest.builder()
                    .accessToken(MOCK_KAKAO_ACCESS_TOKEN)
                    .nickname(MOCK_NICKNAME)
                    .birth(LocalDate.now().plusDays(1).toString())
                    .gender(MOCK_GENDER)
                    .jobId(MOCK_JOB.getId())
                    .yearId(MOCK_JOBYEAR.getId())
                    .build();

            // when // then
            mockMvc.perform(post("/api/v1/auth/signin/kakao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.message").value("유효하지 않은 날짜입니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.url").value("/api/v1/auth/signin/kakao"));
        }

        @DisplayName("카카오 회원가입 실패: gender가 null인 경우 에러 응답을 반환한다.")
        @Test
        void kakaoSigninFailedWhenGenderIsNull() throws Exception {
            // given
            KakaoSigninRequest request = KakaoSigninRequest.builder()
                    .accessToken(MOCK_KAKAO_ACCESS_TOKEN)
                    .nickname(MOCK_NICKNAME)
                    .birth(MOCK_BIRTH)
                    .gender(null)
                    .jobId(MOCK_JOB.getId())
                    .yearId(MOCK_JOBYEAR.getId())
                    .build();

            // when // then
            mockMvc.perform(post("/api/v1/auth/signin/kakao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.message").value("gender는 필수 입력값입니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.url").value("/api/v1/auth/signin/kakao"));
        }

        @DisplayName("카카오 회원가입 실패: jobId가 null인 경우 에러 응답을 반환한다.")
        @Test
        void kakaoSigninFailedWhenJobIdIsNull() throws Exception {
            // given
            KakaoSigninRequest request = KakaoSigninRequest.builder()
                    .accessToken(MOCK_KAKAO_ACCESS_TOKEN)
                    .nickname(MOCK_NICKNAME)
                    .birth(MOCK_BIRTH)
                    .gender(MOCK_GENDER)
                    .jobId(null)
                    .yearId(MOCK_JOBYEAR.getId())
                    .build();

            // when // then
            mockMvc.perform(post("/api/v1/auth/signin/kakao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.message").value("jobId는 필수 입력값입니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.url").value("/api/v1/auth/signin/kakao"));
        }

        @DisplayName("카카오 회원가입 실패: jobId가 0 미만인 경우 에러 응답을 반환한다.")
        @Test
        void kakaoSigninFailedWhenJobIdIsLessThanMin() throws Exception {
            // given
            KakaoSigninRequest request = KakaoSigninRequest.builder()
                    .accessToken(MOCK_KAKAO_ACCESS_TOKEN)
                    .nickname(MOCK_NICKNAME)
                    .birth(MOCK_BIRTH)
                    .gender(MOCK_GENDER)
                    .jobId(-1L)
                    .yearId(MOCK_JOBYEAR.getId())
                    .build();

            // when // then
            mockMvc.perform(post("/api/v1/auth/signin/kakao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.message").value("jobId는 0 이상의 값이어야 합니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.url").value("/api/v1/auth/signin/kakao"));
        }

        @DisplayName("카카오 회원가입 실패: jobId가 20 초과인 경우 에러 응답을 반환한다.")
        @Test
        void kakaoSigninFailedWhenJobIdIsMoreThanMax() throws Exception {
            // given
            KakaoSigninRequest request = KakaoSigninRequest.builder()
                    .accessToken(MOCK_KAKAO_ACCESS_TOKEN)
                    .nickname(MOCK_NICKNAME)
                    .birth(MOCK_BIRTH)
                    .gender(MOCK_GENDER)
                    .jobId(21L)
                    .yearId(MOCK_JOBYEAR.getId())
                    .build();

            // when // then
            mockMvc.perform(post("/api/v1/auth/signin/kakao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.message").value("jobId는 20 이하의 값이어야 합니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.url").value("/api/v1/auth/signin/kakao"));
        }

        @DisplayName("카카오 회원가입 실패: yearId가 0 미만인 경우 에러 응답을 반환한다.")
        @Test
        void kakaoSigninFailedWhenYearIdIsLessThanMin() throws Exception {
            // given
            KakaoSigninRequest request = KakaoSigninRequest.builder()
                    .accessToken(MOCK_KAKAO_ACCESS_TOKEN)
                    .nickname(MOCK_NICKNAME)
                    .birth(MOCK_BIRTH)
                    .gender(MOCK_GENDER)
                    .jobId(MOCK_JOB.getId())
                    .yearId(-1)
                    .build();

            // when // then
            mockMvc.perform(post("/api/v1/auth/signin/kakao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.message").value("yearId는 0 이상의 값이어야 합니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.url").value("/api/v1/auth/signin/kakao"));
        }

        @DisplayName("카카오 회원가입 실패: yearId가 4 초과인 경우 에러 응답을 반환한다.")
        @Test
        void kakaoSigninFailedWhenYearIdIsMoreThanMax() throws Exception {
            // given
            KakaoSigninRequest request = KakaoSigninRequest.builder()
                    .accessToken(MOCK_KAKAO_ACCESS_TOKEN)
                    .nickname(MOCK_NICKNAME)
                    .birth(MOCK_BIRTH)
                    .gender(MOCK_GENDER)
                    .jobId(MOCK_JOB.getId())
                    .yearId(5)
                    .build();

            // when // then
            mockMvc.perform(post("/api/v1/auth/signin/kakao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.message").value("yearId는 4 이하의 값이어야 합니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.url").value("/api/v1/auth/signin/kakao"));
        }

    }

    @Nested
    @DisplayName("토큰 재발급")
    class reissueTokenTests {

        @BeforeEach
        void setUp() {
            // 서비스 mock 처리
            mockReissueTokenResponse = ReissueTokenResponse.builder()
                    .accessToken(MOCK_ACCESS_TOKEN)
                    .refreshToken(MOCK_REFRESH_TOKEN)
                    .build();
            given(authService.reissueToken(any())).willReturn(mockReissueTokenResponse);
        }

        @DisplayName("토큰 재발급 성공")
        @Test
        void reissueTokenSucceeds() throws Exception {
            // given
            ReissueTokenRequest request = ReissueTokenRequest.builder()
                    .refreshToken(MOCK_REFRESH_TOKEN)
                    .build();

            // when // then
            mockMvc.perform(post("/api/v1/auth/reissue")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.code").isEmpty())
                    .andExpect(jsonPath("$.url").isEmpty())
                    .andExpect(jsonPath("$.data.accessToken").value(MOCK_ACCESS_TOKEN))
                    .andExpect(jsonPath("$.data.refreshToken").value(MOCK_REFRESH_TOKEN));
        }

        @DisplayName("토큰 재발급 실패: refresh token이 공백인 경우 에러 응답을 반환한다.")
        @Test
        void reissueTokenFailedWhenTokenIsBlank() throws Exception {
            // given
            ReissueTokenRequest request = ReissueTokenRequest.builder()
                    .refreshToken(" ")
                    .build();

            // when // then
            mockMvc.perform(post("/api/v1/auth/reissue")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.message").value("refresh token은 필수 입력값입니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.url").value("/api/v1/auth/reissue"));
        }

    }

}
