package com.challenge.api.controller.member;

import com.challenge.api.controller.ControllerTestSupport;
import com.challenge.api.controller.member.request.CheckNicknameRequest;
import com.challenge.api.controller.member.request.UpdateBirthRequest;
import com.challenge.api.controller.member.request.UpdateGenderRequest;
import com.challenge.api.controller.member.request.UpdateJobRequest;
import com.challenge.api.controller.member.request.UpdateJobYearRequest;
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

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
public class MemberControllerTest extends ControllerTestSupport {

    @MockBean
    private MemberService memberService;

    private String checkNicknameResponse;
    private String updateNicknameResponse;
    private String updateBirthResponse;
    private String updateGenderResponse;
    private String updateJobResponse;
    private String updateJobYearResponse;

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
            mockMvc.perform(put("/api/v1/member/nickname")
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
            mockMvc.perform(put("/api/v1/member/nickname")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.message").value("닉네임은 4~10자이며, 띄어쓰기와 특수문자를 사용할 수 없습니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.url").value("/api/v1/member/nickname"));
        }

    }

    @Nested
    @DisplayName("생년월일 수정")
    class UpdateBirthTests {

        @BeforeEach
        void setUp() {
            // 서비스 mock 처리
            updateBirthResponse = "생년월일 수정 성공";
            given(memberService.updateBirth(any(), any())).willReturn(updateBirthResponse);
        }

        @DisplayName("생년월일 수정 성공")
        @Test
        void updateBirthSucceeds() throws Exception {
            // given
            UpdateBirthRequest request = UpdateBirthRequest.builder()
                    .birth(MOCK_BIRTH)
                    .build();

            // when // then
            mockMvc.perform(put("/api/v1/member/birth")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.code").isEmpty())
                    .andExpect(jsonPath("$.url").isEmpty())
                    .andExpect(jsonPath("$.data").value(updateBirthResponse));
        }

        @DisplayName("생년월일 수정 실패: birth가 과거 날짜가 아닌 경우 에러 응답을 반환한다.")
        @Test
        void checkNicknameIsValidFailed() throws Exception {
            // given
            UpdateBirthRequest request = UpdateBirthRequest.builder()
                    .birth(LocalDate.now())
                    .build();

            // when // then
            mockMvc.perform(put("/api/v1/member/birth")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.message").value("birth는 과거 날짜여야 합니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.url").value("/api/v1/member/birth"));
        }

    }

    @Nested
    @DisplayName("성별 수정")
    class UpdateGenderTests {

        @BeforeEach
        void setUp() {
            // 서비스 mock 처리
            updateGenderResponse = "성별 수정 성공";
            given(memberService.updateGender(any(), any())).willReturn(updateGenderResponse);
        }

        @DisplayName("성별 수정 성공")
        @Test
        void updateGenderSucceeds() throws Exception {
            // given
            UpdateGenderRequest request = UpdateGenderRequest.builder()
                    .gender(MOCK_GENDER)
                    .build();

            // when // then
            mockMvc.perform(put("/api/v1/member/gender")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.code").isEmpty())
                    .andExpect(jsonPath("$.url").isEmpty())
                    .andExpect(jsonPath("$.data").value(updateGenderResponse));
        }

        @DisplayName("성별 수정 실패: gender가 null인 경우 에러 응답을 반환한다.")
        @Test
        void checkNicknameIsValidFailed() throws Exception {
            // given
            UpdateGenderRequest request = UpdateGenderRequest.builder()
                    .gender(null)
                    .build();

            // when // then
            mockMvc.perform(put("/api/v1/member/gender")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.message").value("gender는 필수 입력값입니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.url").value("/api/v1/member/gender"));
        }

    }

    @Nested
    @DisplayName("직무 수정")
    class UpdateJobTests {

        @BeforeEach
        void setUp() {
            // 서비스 mock 처리
            updateJobResponse = "직무 수정 성공";
            given(memberService.updateJob(any(), any())).willReturn(updateJobResponse);
        }

        @DisplayName("직무 수정 성공")
        @Test
        void updateJobSucceeds() throws Exception {
            // given
            UpdateJobRequest request = UpdateJobRequest.builder()
                    .jobId(1L)
                    .build();

            // when // then
            mockMvc.perform(put("/api/v1/member/job")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.code").isEmpty())
                    .andExpect(jsonPath("$.url").isEmpty())
                    .andExpect(jsonPath("$.data").value(updateJobResponse));
        }

        @DisplayName("직무 수정 실패: jobId가 null인 경우 에러 응답을 반환한다.")
        @Test
        void updateJobFailedWhenIdIsNull() throws Exception {
            // given
            UpdateJobRequest request = UpdateJobRequest.builder()
                    .jobId(null)
                    .build();

            // when // then
            mockMvc.perform(put("/api/v1/member/job")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.message").value("jobId는 필수 입력값입니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.url").value("/api/v1/member/job"));
        }

        @DisplayName("직무 수정 실패: jobId가 1 미만인 경우 에러 응답을 반환한다.")
        @Test
        void updateJobFailedWhenJobIdIsLessThanMin() throws Exception {
            // given
            UpdateJobRequest request = UpdateJobRequest.builder()
                    .jobId(0L)
                    .build();

            // when // then
            mockMvc.perform(put("/api/v1/member/job")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.message").value("jobId는 1 이상의 값이어야 합니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.url").value("/api/v1/member/job"));
        }

        @DisplayName("직무 수정 실패: jobId가 20 초과인 경우 에러 응답을 반환한다.")
        @Test
        void updateJobFailedWhenJobIdIsMoreThanMax() throws Exception {
            // given
            UpdateJobRequest request = UpdateJobRequest.builder()
                    .jobId(21L)
                    .build();

            // when // then
            mockMvc.perform(put("/api/v1/member/job")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.message").value("jobId는 20 이하의 값이어야 합니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.url").value("/api/v1/member/job"));
        }

    }

    @Nested
    @DisplayName("연차 수정")
    class UpdateJobYearTests {

        @BeforeEach
        void setUp() {
            // 서비스 mock 처리
            updateJobYearResponse = "연차 수정 성공";
            given(memberService.updateJobYear(any(), any())).willReturn(updateJobYearResponse);
        }

        @DisplayName("연차 수정 성공")
        @Test
        void updateJobYearSucceeds() throws Exception {
            // given
            UpdateJobYearRequest request = UpdateJobYearRequest.builder()
                    .yearId(1)
                    .build();

            // when // then
            mockMvc.perform(put("/api/v1/member/jobyear")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.code").isEmpty())
                    .andExpect(jsonPath("$.url").isEmpty())
                    .andExpect(jsonPath("$.data").value(updateJobYearResponse));
        }

        @DisplayName("연차 수정 실패: yearId가 1 미만인 경우 에러 응답을 반환한다.")
        @Test
        void updateJobYearFailedWhenIdIsLessThanMin() throws Exception {
            // given
            UpdateJobYearRequest request = UpdateJobYearRequest.builder()
                    .yearId(0)
                    .build();

            // when // then
            mockMvc.perform(put("/api/v1/member/jobyear")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.message").value("yearId는 1 이상의 값이어야 합니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.url").value("/api/v1/member/jobyear"));
        }

        @DisplayName("연차 수정 실패: yearId가 4 초과인 경우 에러 응답을 반환한다.")
        @Test
        void updateJobYearFailedWhenIdIsMoreThanMax() throws Exception {
            // given
            UpdateJobYearRequest request = UpdateJobYearRequest.builder()
                    .yearId(5)
                    .build();

            // when // then
            mockMvc.perform(put("/api/v1/member/jobyear")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.message").value("yearId는 4 이하의 값이어야 합니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.url").value("/api/v1/member/jobyear"));
        }

    }

}
