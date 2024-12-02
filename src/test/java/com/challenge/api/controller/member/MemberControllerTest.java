package com.challenge.api.controller.member;

import com.challenge.api.controller.ControllerTestSupport;
import com.challenge.api.controller.member.request.CheckNicknameRequest;
import com.challenge.api.controller.member.request.UpdateNicknameRequest;
import com.challenge.api.service.member.MemberService;
import com.challenge.api.service.member.response.MemberInfoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
public class MemberControllerTest extends ControllerTestSupport {

    @MockBean
    private MemberService memberService;

    private String checkNicknameResponse;
    private String updateNicknameResponse;

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

    @DisplayName("회원 정보 조회 성공")
    @Test
    void getMemberInfoSucceeds() throws Exception {
        // given
        // 서비스 mock 처리
        MemberInfoResponse memberInfoResponse = MemberInfoResponse.builder()
                .nickname(MOCK_NICKNAME)
                .birth(MOCK_BIRTH)
                .gender(MOCK_GENDER)
                .jobId(MOCK_JOB.getId())
                .job(MOCK_JOB.getDescription())
                .jobYearId(MOCK_JOBYEAR.getId())
                .build();
        given(memberService.getMemberInfo(any())).willReturn(memberInfoResponse);

        // when // then
        mockMvc.perform(get("/api/v1/member")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.code").isEmpty())
                .andExpect(jsonPath("$.url").isEmpty())
                .andExpect(jsonPath("$.data.nickname").value(MOCK_NICKNAME))
                .andExpect(jsonPath("$.data.birth").value(MOCK_BIRTH.toString()))
                .andExpect(jsonPath("$.data.gender").value(MOCK_GENDER.toString()))
                .andExpect(jsonPath("$.data.jobId").value(MOCK_JOB.getId()))
                .andExpect(jsonPath("$.data.jobYearId").value(MOCK_JOBYEAR.getId()));
    }

    @Nested
    @DisplayName("닉네임 수정")
    class UpdateNicknameTests {

        @BeforeEach
        void setUp() {
            // 서비스 mock 처리
            updateNicknameResponse = "닉네임 수정 성공";
            given(memberService.updateNickname(any(), any())).willReturn(updateNicknameResponse);
        }

        @DisplayName("닉네임 수정 성공")
        @Test
        void updateNicknameSucceeds() throws Exception {
            // given
            UpdateNicknameRequest request = UpdateNicknameRequest.builder()
                    .nickname("nickname")
                    .build();

            // when // then
            mockMvc.perform(patch("/api/v1/member/nickname")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.code").isEmpty())
                    .andExpect(jsonPath("$.url").isEmpty())
                    .andExpect(jsonPath("$.data").value(updateNicknameResponse));
        }

        @DisplayName("닉네임 수정 실패: nickname이 공백인 경우 에러 응답을 반환한다.")
        @Test
        void checkNicknameIsValidFailed() throws Exception {
            // given
            UpdateNicknameRequest request = UpdateNicknameRequest.builder()
                    .nickname(" ")
                    .build();

            // when // then
            mockMvc.perform(patch("/api/v1/member/nickname")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.message").value("닉네임은 4~10자이며, 띄어쓰기와 특수문자를 사용할 수 없습니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.url").value("/api/v1/member/nickname"));
        }

    }

}
