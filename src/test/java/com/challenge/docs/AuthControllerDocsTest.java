package com.challenge.docs;

import com.challenge.api.controller.auth.AuthController;
import com.challenge.api.service.auth.AuthService;
import com.challenge.api.service.auth.request.KakaoLoginServiceRequest;
import com.challenge.api.service.auth.request.KakaoSigninServiceRequest;
import com.challenge.api.service.auth.response.LoginResponse;
import com.challenge.domain.member.Gender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static com.challenge.docs.RestDocsConfiguration.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

class AuthControllerDocsTest extends RestDocsSupport {

    private final AuthService authService = mock(AuthService.class);

    @Override
    protected Object initController() {
        return new AuthController(authService);
    }

    @DisplayName("카카오 소셜 로그인 API")
    @Test
    void kakaoLoginSuccess() throws Exception {
        KakaoLoginServiceRequest request = KakaoLoginServiceRequest.builder()
                .accessToken("카카오에서 발급 받은 access token")
                .build();


        given(authService.kakaoLogin(any(KakaoLoginServiceRequest.class)))
                .willReturn(LoginResponse
                        .builder()
                        .memberId(1L)
                        .accessToken("access token")
                        .refreshToken("refresh token")
                        .accessTokenExpiresIn(123456789012L)
                        .build());

        mockMvc.perform(post("/api/v1/auth/login/kakao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("accessToken").type(STRING)
                                        .description("카카오 access token")
                        ),
                        responseFields(this.successResponse())
                                .and(
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

    @DisplayName("카카오 소셜 회원가입 API")
    @Test
    void kakaoSigninSuccess() throws Exception {
        KakaoSigninServiceRequest request = KakaoSigninServiceRequest.builder()
                .accessToken("카카오에서 발급 받은 access token")
                .nickname("회원닉네임")
                .birth(LocalDate.of(2000, 1, 1))
                .gender(Gender.FEMALE)
                .jobId(1L)
                .yearId(1)
                .build();

        given(authService.kakaoSignin(any(KakaoSigninServiceRequest.class))).willReturn(
                LoginResponse.builder()
                        .memberId(1L)
                        .accessToken("access token")
                        .refreshToken("refresh token")
                        .accessTokenExpiresIn(123456789012L)
                        .build()
        );

        mockMvc.perform(post("/api/v1/auth/signin/kakao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("accessToken").type(STRING)
                                        .description("카카오 access token"),
                                fieldWithPath("nickname").type(STRING)
                                        .description("회원 닉네임")
                                        .attributes(field("4~10자, 특수문자 및 공백 불가")),
                                fieldWithPath("birth").type(ARRAY)
                                        .description("생일")
                                        .attributes(field("과거 날짜")),
                                fieldWithPath("gender").type(STRING)
                                        .description("성별")
                                        .attributes(field("MALE, FEMALE")),
                                fieldWithPath("jobId").type(NUMBER)
                                        .description("직무 id")
                                        .attributes(field("1~20 사이의 숫자")),
                                fieldWithPath("yearId").type(NUMBER)
                                        .description("연차 id")
                                        .attributes(field("1~4 사이의 숫자"))
                        ),
                        responseFields(this.successResponse())
                                .and(
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


}
