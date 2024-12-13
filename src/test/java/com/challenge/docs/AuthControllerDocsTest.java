package com.challenge.docs;

import com.challenge.api.controller.auth.AuthController;
import com.challenge.api.service.auth.AuthService;
import com.challenge.api.service.auth.request.KakaoLoginServiceRequest;
import com.challenge.api.service.auth.response.LoginResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerDocsTest extends RestDocsSupport {

    private final AuthService authService = mock(AuthService.class);

    @Override
    protected Object initController() {
        return new AuthController(authService);
    }

    @DisplayName("카카오 소셜 로그인 API")
    @Test
    void kakaoLogin() throws Exception {
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
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("kakao-login-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        requestFields(

                                fieldWithPath("accessToken").type(STRING)
                                        .description("카카오 access token")
                        ),
                        responseFields(
                                fieldWithPath("status").type(NUMBER)
                                        .description("상태"),
                                fieldWithPath("message").type(STRING)
                                        .description("메시지"),
                                fieldWithPath("code").type(NULL)
                                        .description("코드 (성공 시 null)"),
                                fieldWithPath("url").type(NULL)
                                        .description("API 호출 URL (성공 시 null)"),
                                fieldWithPath("data.memberId").type(NUMBER)
                                        .description("회원 id"),
                                fieldWithPath("data.accessToken").type(STRING)
                                        .description("access token"),
                                fieldWithPath("data.refreshToken").type(STRING)
                                        .description("refresh token"),
                                fieldWithPath("data.accessTokenExpiresIn").type(NUMBER)
                                        .description("access token이 만료되는 시간 timestamp")
                        )))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.code").isEmpty())
                .andExpect(jsonPath("$.url").isEmpty());
    }


}
