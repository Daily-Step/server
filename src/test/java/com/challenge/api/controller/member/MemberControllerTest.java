package com.challenge.api.controller.member;

import com.challenge.api.controller.ControllerTestSupport;
import com.challenge.api.controller.member.request.CheckNicknameRequest;
import com.challenge.api.service.member.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
public class MemberControllerTest extends ControllerTestSupport {

    @MockBean
    private MemberService memberService;

    private String checkNicknameResponse;

    @Nested
    @DisplayName("닉네임 중복 확인")
    class CheckNicknameIsValidTests {

        @BeforeEach
        void setUp() {
            // 서비스 mock 처리
            checkNicknameResponse = "사용 가능한 닉네임입니다.";
            given(memberService.checkNicknameIsValid(any())).willReturn(checkNicknameResponse);
        }

        @DisplayName("닉네임 중복 확인 성공")
        @Test
        void checkNicknameIsValidSucceeds() throws Exception {
            // given
            CheckNicknameRequest request = CheckNicknameRequest.builder()
                    .nickname("nickname")
                    .build();

            // when // then
            mockMvc.perform(post("/api/v1/member/nickname/valid")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.code").isEmpty())
                    .andExpect(jsonPath("$.url").isEmpty())
                    .andExpect(jsonPath("$.data").value(checkNicknameResponse));
        }

        @DisplayName("닉네임 중복 확인 실패: nickname이 공백인 경우 에러 응답을 반환한다.")
        @Test
        void checkNicknameIsValidFailed() throws Exception {
            // given
            CheckNicknameRequest request = CheckNicknameRequest.builder()
                    .nickname(" ")
                    .build();

            // when // then
            mockMvc.perform(post("/api/v1/member/nickname/valid")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.message").value("닉네임은 4~10자이며, 띄어쓰기와 특수문자를 사용할 수 없습니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.url").value("/api/v1/member/nickname/valid"));
        }

    }

}
