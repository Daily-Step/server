package com.challenge.docs;

import com.challenge.api.controller.auth.AuthController;
import com.challenge.api.controller.auth.request.KakaoLoginRequest;
import com.challenge.api.controller.auth.request.KakaoSigninRequest;
import com.challenge.api.controller.auth.request.ReissueTokenRequest;
import com.challenge.api.service.auth.AuthService;
import com.challenge.api.service.auth.request.KakaoLoginServiceRequest;
import com.challenge.api.service.auth.request.KakaoSigninServiceRequest;
import com.challenge.api.service.auth.request.ReissueTokenServiceRequest;
import com.challenge.api.service.auth.response.LoginResponse;
import com.challenge.api.service.auth.response.ReissueTokenResponse;
import com.challenge.domain.job.Job;
import com.challenge.domain.member.Gender;
import com.challenge.domain.member.JobYear;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.time.LocalDate;

import static com.challenge.docs.RestDocsConfiguration.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerDocsTest extends RestDocsSupport {

    private final AuthService authService = mock(AuthService.class);

    @Override
    protected Object initController() {
        return new AuthController(authService);
    }

    private static final String KAKAO_ACCESS_TOKEN = "kakao-access-token";
    private static final String ACCESS_TOKEN = "access-token";
    private static final String REFRESH_TOKEN = "refresh-token";
    private static final String MOCK_NICKNAME = "nickname";
    private static final String MOCK_BIRTH = LocalDate.of(2000, 1, 1).toString();
    private static final Gender MOCK_GENDER = Gender.MALE;
    private static final JobYear MOCK_JOBYEAR = JobYear.LT_1Y;
    private static final Job MOCK_JOB = Job.builder().id(1L).code("1").description("1").build();

    @Nested
    @DisplayName("카카오 회원가입")
    class KakaoSignin {

        private LoginResponse mockLoginResponse;

        private FieldDescriptor[] commonRequest() {
            return new FieldDescriptor[]{
                    fieldWithPath("accessToken").type(STRING).description("카카오 access token"),
                    fieldWithPath("nickname").type(STRING).description("회원 닉네임")
                            .attributes(field("constraints", "4~10자, 특수문자 및 공백 불가")),
                    fieldWithPath("birth").type(STRING).description("생일")
                            .attributes(field("constraints", "과거 날짜, yyyy-MM-dd")),
                    fieldWithPath("gender").type(STRING).description("성별")
                            .attributes(field("constraints", "MALE, FEMALE")),
                    fieldWithPath("jobId").type(NUMBER).description("직무 id")
                            .attributes(field("constraints", "1~20 사이의 숫자")),
                    fieldWithPath("yearId").type(NUMBER).description("연차 id")
                            .attributes(field("constraints", "1~4 사이의 숫자"))
            };
        }

        @BeforeEach
        void setUp() {
            // 서비스 mock 처리
            mockLoginResponse = LoginResponse.builder()
                    .memberId(1L)
                    .accessToken(ACCESS_TOKEN)
                    .refreshToken(REFRESH_TOKEN)
                    .accessTokenExpiresIn(123456789012L)
                    .build();
            given(authService.kakaoSignin(any(KakaoSigninServiceRequest.class))).willReturn(mockLoginResponse);
        }

        @DisplayName("카카오 회원가입 성공")
        @Test
        void kakaoSigninSucceeds() throws Exception {
            // given
            KakaoSigninRequest request = KakaoSigninRequest.builder()
                    .accessToken(KAKAO_ACCESS_TOKEN)
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
                    .andExpect(jsonPath("$.data.accessToken").value(ACCESS_TOKEN))
                    .andExpect(jsonPath("$.data.refreshToken").value(REFRESH_TOKEN))
                    .andDo(restDocs.document(
                            requestFields(commonRequest()),
                            responseFields(successResponse())
                                    .and(
                                            fieldWithPath("data").type(OBJECT)
                                                    .description("응답 데이터"),
                                            fieldWithPath("data.memberId").type(NUMBER)
                                                    .description("회원 id"),
                                            fieldWithPath("data.accessToken").type(STRING)
                                                    .description("access token"),
                                            fieldWithPath("data.refreshToken").type(STRING)
                                                    .description("refresh token"),
                                            fieldWithPath("data.accessTokenExpiresIn").type(NUMBER)
                                                    .description("access token이 만료되는 시간 timestamp")
                                    )
                    ));
        }

        @DisplayName("카카오 회원가입 실패: access token이 공백인 경우")
        @Test
        void kakaoSigninFailBlankAccessToken() throws Exception {
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
                    .andExpect(jsonPath("$.url").value("/api/v1/auth/signin/kakao"))
                    .andDo(restDocs.document(
                            requestFields(commonRequest()),
                            responseFields(failResponse())
                    ));
        }

        @DisplayName("카카오 회원가입 실패: nickname이 공백인 경우")
        @Test
        void kakaoSigninFailBlankNickname() throws Exception {
            // given
            KakaoSigninRequest request = KakaoSigninRequest.builder()
                    .accessToken(KAKAO_ACCESS_TOKEN)
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
                    .andExpect(jsonPath("$.url").value("/api/v1/auth/signin/kakao"))
                    .andDo(restDocs.document(
                            requestFields(commonRequest()),
                            responseFields(failResponse())
                    ));
        }

        @DisplayName("카카오 회원가입 실패: nickname이 길이/문자 조건을 위반한 경우")
        @Test
        void kakaoSigninFailInvalidNickname() throws Exception {
            // given
            KakaoSigninRequest request = KakaoSigninRequest.builder()
                    .accessToken(KAKAO_ACCESS_TOKEN)
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
                    .andExpect(jsonPath("$.url").value("/api/v1/auth/signin/kakao"))
                    .andDo(restDocs.document(
                            requestFields(commonRequest()),
                            responseFields(failResponse())
                    ));
        }

        @DisplayName("카카오 회원가입 실패: birth가 null인 경우")
        @Test
        void kakaoSigninFailBirthNull() throws Exception {
            // given
            KakaoSigninRequest request = KakaoSigninRequest.builder()
                    .accessToken(KAKAO_ACCESS_TOKEN)
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
                    .andExpect(jsonPath("$.url").value("/api/v1/auth/signin/kakao"))
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("accessToken").type(STRING).description("카카오 access token"),
                                    fieldWithPath("nickname").type(STRING).description("회원 닉네임")
                                            .attributes(field("constraints", "4~10자, 특수문자 및 공백 불가")),
                                    fieldWithPath("birth").type(NULL).description("생일")
                                            .attributes(field("constraints", "과거 날짜")),
                                    fieldWithPath("gender").type(STRING).description("성별")
                                            .attributes(field("constraints", "MALE, FEMALE")),
                                    fieldWithPath("jobId").type(NUMBER).description("직무 id")
                                            .attributes(field("constraints", "1~20 사이의 숫자")),
                                    fieldWithPath("yearId").type(NUMBER).description("연차 id")
                                            .attributes(field("constraints", "1~4 사이의 숫자"))),
                            responseFields(failResponse())
                    ));
        }

        @DisplayName("카카오 회원가입 실패: birth가 과거 날짜가 아닌 경우")
        @Test
        void kakaoSigninFailBirthNotPast() throws Exception {
            // given
            KakaoSigninRequest request = KakaoSigninRequest.builder()
                    .accessToken(KAKAO_ACCESS_TOKEN)
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
                    .andExpect(jsonPath("$.url").value("/api/v1/auth/signin/kakao"))
                    .andDo(restDocs.document(
                            requestFields(commonRequest()),
                            responseFields(failResponse())
                    ));
        }

        @DisplayName("카카오 회원가입 실패: gender가 null인 경우")
        @Test
        void kakaoSigninFailGenderNull() throws Exception {
            // given
            KakaoSigninRequest request = KakaoSigninRequest.builder()
                    .accessToken(KAKAO_ACCESS_TOKEN)
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
                    .andExpect(jsonPath("$.url").value("/api/v1/auth/signin/kakao"))
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("accessToken").type(STRING).description("카카오 access token"),
                                    fieldWithPath("nickname").type(STRING).description("회원 닉네임")
                                            .attributes(field("constraints", "4~10자, 특수문자 및 공백 불가")),
                                    fieldWithPath("birth").type(STRING).description("생일")
                                            .attributes(field("constraints", "과거 날짜")),
                                    fieldWithPath("gender").type(NULL).description("성별")
                                            .attributes(field("constraints", "MALE, FEMALE")),
                                    fieldWithPath("jobId").type(NUMBER).description("직무 id")
                                            .attributes(field("constraints", "1~20 사이의 숫자")),
                                    fieldWithPath("yearId").type(NUMBER).description("연차 id")
                                            .attributes(field("constraints", "1~4 사이의 숫자"))
                            ),
                            responseFields(failResponse())
                    ));
        }

        @DisplayName("카카오 회원가입 실패: jobId가 null인 경우")
        @Test
        void kakaoSigninFailJobIdNull() throws Exception {
            // given
            KakaoSigninRequest request = KakaoSigninRequest.builder()
                    .accessToken(KAKAO_ACCESS_TOKEN)
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
                    .andExpect(jsonPath("$.url").value("/api/v1/auth/signin/kakao"))
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("accessToken").type(STRING).description("카카오 access token"),
                                    fieldWithPath("nickname").type(STRING).description("회원 닉네임")
                                            .attributes(field("constraints", "4~10자, 특수문자 및 공백 불가")),
                                    fieldWithPath("birth").type(STRING).description("생일")
                                            .attributes(field("constraints", "과거 날짜")),
                                    fieldWithPath("gender").type(STRING).description("성별")
                                            .attributes(field("constraints", "MALE, FEMALE")),
                                    fieldWithPath("jobId").type(NULL).description("직무 id")
                                            .attributes(field("constraints", "1~20 사이의 숫자")),
                                    fieldWithPath("yearId").type(NUMBER).description("연차 id")
                                            .attributes(field("constraints", "1~4 사이의 숫자"))
                            ),
                            responseFields(failResponse())
                    ));
        }

        @DisplayName("카카오 회원가입 실패: jobId가 1 미만인 경우")
        @Test
        void kakaoSigninFailJobIdLessThan() throws Exception {
            // given
            KakaoSigninRequest request = KakaoSigninRequest.builder()
                    .accessToken(KAKAO_ACCESS_TOKEN)
                    .nickname(MOCK_NICKNAME)
                    .birth(MOCK_BIRTH)
                    .gender(MOCK_GENDER)
                    .jobId(0L)
                    .yearId(MOCK_JOBYEAR.getId())
                    .build();

            // when // then
            mockMvc.perform(post("/api/v1/auth/signin/kakao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.message").value("jobId는 1 이상의 값이어야 합니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.url").value("/api/v1/auth/signin/kakao"))
                    .andDo(restDocs.document(
                            requestFields(commonRequest()),
                            responseFields(failResponse())
                    ));
        }

        @DisplayName("카카오 회원가입 실패: jobId가 20 초과인 경우")
        @Test
        void kakaoSigninFailJobIdGreaterThan() throws Exception {
            // given
            KakaoSigninRequest request = KakaoSigninRequest.builder()
                    .accessToken(KAKAO_ACCESS_TOKEN)
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
                    .andExpect(jsonPath("$.url").value("/api/v1/auth/signin/kakao"))
                    .andDo(restDocs.document(
                            requestFields(commonRequest()),
                            responseFields(failResponse())
                    ));
        }

        @DisplayName("카카오 회원가입 실패: yearId가 1 미만인 경우")
        @Test
        void kakaoSigninFailYearIdLessThan() throws Exception {
            // given
            KakaoSigninRequest request = KakaoSigninRequest.builder()
                    .accessToken(KAKAO_ACCESS_TOKEN)
                    .nickname(MOCK_NICKNAME)
                    .birth(MOCK_BIRTH)
                    .gender(MOCK_GENDER)
                    .jobId(MOCK_JOB.getId())
                    .yearId(0)
                    .build();

            // when // then
            mockMvc.perform(post("/api/v1/auth/signin/kakao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.message").value("yearId는 1 이상의 값이어야 합니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.url").value("/api/v1/auth/signin/kakao"))
                    .andDo(restDocs.document(
                            requestFields(commonRequest()),
                            responseFields(failResponse())
                    ));
        }

        @DisplayName("카카오 회원가입 실패: yearId가 4 초과인 경우")
        @Test
        void kakaoSigninFailYearIdMoreThan() throws Exception {
            // given
            KakaoSigninRequest request = KakaoSigninRequest.builder()
                    .accessToken(KAKAO_ACCESS_TOKEN)
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
                    .andExpect(jsonPath("$.url").value("/api/v1/auth/signin/kakao"))
                    .andDo(restDocs.document(
                            requestFields(commonRequest()),
                            responseFields(failResponse())
                    ));
        }

    }

    @Nested
    @DisplayName("카카오 소셜 로그인")
    class KakaoLogin {

        private LoginResponse mockLoginResponse;

        @BeforeEach
        void setUp() {
            // 서비스 mock 처리
            mockLoginResponse = LoginResponse.builder()
                    .memberId(1L)
                    .accessToken(ACCESS_TOKEN)
                    .refreshToken(REFRESH_TOKEN)
                    .accessTokenExpiresIn(123456789012L)
                    .build();
            given(authService.kakaoLogin(any(KakaoLoginServiceRequest.class))).willReturn(mockLoginResponse);
        }

        @DisplayName("카카오 소셜 로그인 성공")
        @Test
        void kakaoLoginSuccess() throws Exception {
            // given
            KakaoLoginRequest request = KakaoLoginRequest.builder()
                    .accessToken(KAKAO_ACCESS_TOKEN)
                    .build();

            // when // then
            mockMvc.perform(post("/api/v1/auth/login/kakao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.code").isEmpty())
                    .andExpect(jsonPath("$.url").isEmpty())
                    .andExpect(jsonPath("$.data.accessToken").value(ACCESS_TOKEN))
                    .andExpect(jsonPath("$.data.refreshToken").value(REFRESH_TOKEN))
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("accessToken").type(STRING)
                                            .description("카카오 access token")
                            ),
                            responseFields(successResponse())
                                    .and(
                                            fieldWithPath("data").type(OBJECT)
                                                    .description("응답 데이터"),
                                            fieldWithPath("data.memberId").type(NUMBER)
                                                    .description("회원 id"),
                                            fieldWithPath("data.accessToken").type(STRING)
                                                    .description("access token"),
                                            fieldWithPath("data.refreshToken").type(STRING)
                                                    .description("refresh token"),
                                            fieldWithPath("data.accessTokenExpiresIn").type(NUMBER)
                                                    .description("access token이 만료되는 시간 timestamp")
                                    )));
        }

        @DisplayName("카카오 소셜 로그인 실패: access token이 공백인 경우")
        @Test
        void kakaoLoginFailBlankAccessToken() throws Exception {
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
                    .andExpect(jsonPath("$.url").value("/api/v1/auth/login/kakao"))
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("accessToken").type(STRING)
                                            .description("카카오 access token")
                            ),
                            responseFields(failResponse())
                    ));
        }

    }

    @Nested
    @DisplayName("토큰 재발급")
    class reissueToken {

        private ReissueTokenResponse mockReissueTokenResponse;

        @BeforeEach
        void setUp() {
            // 서비스 mock 처리
            mockReissueTokenResponse = ReissueTokenResponse.builder()
                    .accessToken(ACCESS_TOKEN)
                    .refreshToken(REFRESH_TOKEN)
                    .accessTokenExpiresIn(123456789012L)
                    .build();
            given(authService.reissueToken(any(ReissueTokenServiceRequest.class))).willReturn(mockReissueTokenResponse);
        }

        @DisplayName("토큰 재발급 성공")
        @Test
        void reissueTokenSuccess() throws Exception {
            // given
            ReissueTokenRequest request = ReissueTokenRequest.builder()
                    .refreshToken(REFRESH_TOKEN)
                    .build();

            // when // then
            mockMvc.perform(post("/api/v1/auth/reissue")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.code").isEmpty())
                    .andExpect(jsonPath("$.url").isEmpty())
                    .andExpect(jsonPath("$.data.accessToken").value(ACCESS_TOKEN))
                    .andExpect(jsonPath("$.data.refreshToken").value(REFRESH_TOKEN))
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("refreshToken").type(STRING)
                                            .description("refresh token")
                            ),
                            responseFields(successResponse())
                                    .and(
                                            fieldWithPath("data").type(OBJECT)
                                                    .description("응답 데이터"),
                                            fieldWithPath("data.accessToken").type(STRING)
                                                    .description("새로 발급된 access token"),
                                            fieldWithPath("data.refreshToken").type(STRING)
                                                    .description("새로 발급된 refresh token"),
                                            fieldWithPath("data.accessTokenExpiresIn").type(NUMBER)
                                                    .description("access token이 만료되는 시간 timestamp")
                                    ))
                    )
            ;
        }

        @DisplayName("토큰 재발급 실패: refresh token이 공백인 경우")
        @Test
        void reissueTokenFailTokenBlank() throws Exception {
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
                    .andExpect(jsonPath("$.url").value("/api/v1/auth/reissue"))
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("refreshToken").type(STRING)
                                            .description("refresh token")
                            ),
                            responseFields(failResponse())
                    ));
        }

    }

}
