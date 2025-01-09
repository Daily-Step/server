package com.challenge.docs;

import com.challenge.api.controller.member.MemberController;
import com.challenge.api.controller.member.request.CheckNicknameRequest;
import com.challenge.api.controller.member.request.UpdateBirthRequest;
import com.challenge.api.controller.member.request.UpdateGenderRequest;
import com.challenge.api.controller.member.request.UpdateJobRequest;
import com.challenge.api.controller.member.request.UpdateJobYearRequest;
import com.challenge.api.controller.member.request.UpdateNicknameRequest;
import com.challenge.api.service.member.MemberService;
import com.challenge.api.service.member.request.CheckNicknameServiceRequest;
import com.challenge.api.service.member.request.UpdateBirthServiceRequest;
import com.challenge.api.service.member.request.UpdateGenderServiceRequest;
import com.challenge.api.service.member.request.UpdateJobServiceRequest;
import com.challenge.api.service.member.request.UpdateJobYearServiceRequest;
import com.challenge.api.service.member.request.UpdateNicknameServiceRequest;
import com.challenge.api.service.member.response.MemberInfoResponse;
import com.challenge.domain.job.Job;
import com.challenge.domain.member.Gender;
import com.challenge.domain.member.JobYear;
import com.challenge.domain.member.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MemberControllerDocsTest extends RestDocsSupport {

    private final MemberService memberService = mock(MemberService.class);

    protected static final String MOCK_NICKNAME = "nickname";
    protected static final String MOCK_BIRTH = LocalDate.of(2000, 1, 1).toString();
    protected static final Gender MOCK_GENDER = Gender.MALE;
    protected static final JobYear MOCK_JOBYEAR = JobYear.LT_1Y;
    protected static final Job MOCK_JOB = Job.builder().id(1L).code("1").description("1").build();


    @Override
    protected Object initController() {
        return new MemberController(memberService);
    }

    @DisplayName("회원 정보 조회 성공")
    @Test
    void getMemberInfoSuccess() throws Exception {
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
        given(memberService.getMemberInfo(any(Member.class))).willReturn(memberInfoResponse);

        // when // then
        mockMvc.perform(get("/api/v1/member"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.code").isEmpty())
                .andExpect(jsonPath("$.url").isEmpty())
                .andExpect(jsonPath("$.data.nickname").value(MOCK_NICKNAME))
                .andExpect(jsonPath("$.data.birth").value(MOCK_BIRTH))
                .andExpect(jsonPath("$.data.gender").value(MOCK_GENDER.toString()))
                .andExpect(jsonPath("$.data.jobId").value(MOCK_JOB.getId()))
                .andExpect(jsonPath("$.data.jobYearId").value(MOCK_JOBYEAR.getId()))
                .andDo(restDocs.document(
                        responseFields(successResponse())
                                .and(
                                        fieldWithPath("data").type(OBJECT)
                                                .description("응답 데이터"),
                                        fieldWithPath("data.nickname").type(STRING)
                                                .description("회원 닉네임"),
                                        fieldWithPath("data.birth").type(STRING)
                                                .description("회원 생년월일"),
                                        fieldWithPath("data.gender").type(STRING)
                                                .description("회원 성별"),
                                        fieldWithPath("data.jobId").type(NUMBER)
                                                .description("회원 직무 id"),
                                        fieldWithPath("data.job").type(STRING)
                                                .description("회원 직무 텍스트"),
                                        fieldWithPath("data.jobYearId").type(NUMBER)
                                                .description("회원 연차 id")
                                )
                ));
    }

    @Nested
    @DisplayName("닉네임 중복 확인")
    class CheckNicknameIsValid {

        private String checkNicknameResponse;

        @BeforeEach
        void setUp() {
            // 서비스 mock 처리
            checkNicknameResponse = "사용 가능한 닉네임입니다.";
            given(memberService.checkNicknameIsValid(any(CheckNicknameServiceRequest.class))).willReturn(
                    checkNicknameResponse);
        }

        @DisplayName("닉네임 중복 확인 성공")
        @Test
        void checkNicknameIsValidSuccess() throws Exception {
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
                    .andExpect(jsonPath("$.data").value(checkNicknameResponse))
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("nickname").type(STRING)
                                            .description("닉네임")
                                            .attributes(field("constraints", "4~10자, 특수문자 및 공백 불가"))
                            ),
                            responseFields(successResponse())
                                    .and(
                                            fieldWithPath("data").type(STRING)
                                                    .description("닉네임 중복 확인 결과")
                                    )
                    ));
        }

        @DisplayName("닉네임 중복 확인 실패: nickname이 공백인 경우")
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
                    .andExpect(jsonPath("$.url").value("/api/v1/member/nickname/valid"))
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("nickname").type(STRING)
                                            .description("닉네임")
                                            .attributes(field("constraints", "4~10자, 특수문자 및 공백 불가"))
                            ),
                            responseFields(failResponse())
                    ));
        }

    }

    @Nested
    @DisplayName("닉네임 수정")
    class UpdateNickname {

        private String updateNicknameResponse;

        @BeforeEach
        void setUp() {
            // 서비스 mock 처리
            updateNicknameResponse = "닉네임 수정 성공";
            given(memberService.updateNickname(any(Member.class), any(UpdateNicknameServiceRequest.class))).willReturn(
                    updateNicknameResponse);
        }

        @DisplayName("닉네임 수정 성공")
        @Test
        void updateNicknameSuccess() throws Exception {
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
                    .andExpect(jsonPath("$.data").value(updateNicknameResponse))
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("nickname").type(STRING)
                                            .description("닉네임")
                                            .attributes(field("constraints", "4~10자, 특수문자 및 공백 불가"))
                            ),
                            responseFields(successResponse())
                                    .and(
                                            fieldWithPath("data").type(STRING)
                                                    .description("닉네임 수정 결과")
                                    )
                    ));
        }

        @DisplayName("닉네임 수정 실패: nickname이 공백인 경우")
        @Test
        void checkNicknameIsValidFail() throws Exception {
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
                    .andExpect(jsonPath("$.url").value("/api/v1/member/nickname"))
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("nickname").type(STRING)
                                            .description("닉네임")
                                            .attributes(field("constraints", "4~10자, 특수문자 및 공백 불가"))
                            ),
                            responseFields(failResponse())
                    ));
        }

    }

    @Nested
    @DisplayName("생년월일 수정")
    class UpdateBirth {

        private String updateBirthResponse;

        @BeforeEach
        void setUp() {
            // 서비스 mock 처리
            updateBirthResponse = "생년월일 수정 성공";
            given(memberService.updateBirth(any(Member.class), any(UpdateBirthServiceRequest.class))).willReturn(
                    updateBirthResponse);
        }

        @DisplayName("생년월일 수정 성공")
        @Test
        void updateBirthSuccess() throws Exception {
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
                    .andExpect(jsonPath("$.data").value(updateBirthResponse))
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("birth").type(STRING)
                                            .description("생년월일")
                                            .attributes(field("constraints", "yyyy-MM-dd"))
                            ),
                            responseFields(successResponse())
                                    .and(
                                            fieldWithPath("data").type(STRING)
                                                    .description("생년월일 수정 결과")
                                    )
                    ));
        }

        @DisplayName("생년월일 수정 실패: birth가 과거 날짜가 아닌 경우")
        @Test
        void updateBirthFail() throws Exception {
            // given
            UpdateBirthRequest request = UpdateBirthRequest.builder()
                    .birth(LocalDate.now().plusDays(1).toString())
                    .build();

            // when // then
            mockMvc.perform(put("/api/v1/member/birth")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.message").value("유효하지 않은 날짜입니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.url").value("/api/v1/member/birth"))
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("birth").type(STRING)
                                            .description("생년월일")
                                            .attributes(field("constraints", "yyyy-MM-dd"))
                            ),
                            responseFields(failResponse())
                    ));
        }

    }

    @Nested
    @DisplayName("성별 수정")
    class UpdateGender {

        private String updateGenderResponse;

        @BeforeEach
        void setUp() {
            // 서비스 mock 처리
            updateGenderResponse = "성별 수정 성공";
            given(memberService.updateGender(any(Member.class), any(UpdateGenderServiceRequest.class))).willReturn(
                    updateGenderResponse);
        }

        @DisplayName("성별 수정 성공")
        @Test
        void updateGenderSuccess() throws Exception {
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
                    .andExpect(jsonPath("$.data").value(updateGenderResponse))
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("gender").type(STRING)
                                            .description("성별")
                                            .attributes(field("constraints", "MALE, FEMALE"))
                            ),
                            responseFields(successResponse())
                                    .and(
                                            fieldWithPath("data").type(STRING)
                                                    .description("성별 수정 결과")
                                    )
                    ));
        }

        @DisplayName("성별 수정 실패: gender가 null인 경우")
        @Test
        void updateGenderFail() throws Exception {
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
                    .andExpect(jsonPath("$.url").value("/api/v1/member/gender"))
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("gender").type(NULL)
                                            .description("성별")
                                            .attributes(field("constraints", "MALE, FEMALE"))
                            ),
                            responseFields(failResponse())
                    ));
        }

    }

    @Nested
    @DisplayName("직무 수정")
    class UpdateJob {

        private String updateJobResponse;

        @BeforeEach
        void setUp() {
            // 서비스 mock 처리
            updateJobResponse = "직무 수정 성공";
            given(memberService.updateJob(any(Member.class), any(UpdateJobServiceRequest.class))).willReturn(
                    updateJobResponse);
        }

        @DisplayName("직무 수정 성공")
        @Test
        void updateJobSuccess() throws Exception {
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
                    .andExpect(jsonPath("$.data").value(updateJobResponse))
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("jobId").type(NUMBER)
                                            .description("직무 id")
                                            .attributes(field("constraints", "1~20 사이의 숫자"))
                            ),
                            responseFields(successResponse())
                                    .and(
                                            fieldWithPath("data").type(STRING)
                                                    .description("직무 수정 결과")
                                    )
                    ));
        }

        @DisplayName("직무 수정 실패: jobId가 null인 경우")
        @Test
        void updateJobFailIdNull() throws Exception {
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
                    .andExpect(jsonPath("$.url").value("/api/v1/member/job"))
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("jobId").type(NULL)
                                            .description("직무 id")
                                            .attributes(field("constraints", "1~20 사이의 숫자"))
                            ),
                            responseFields(failResponse())
                    ));
        }

        @DisplayName("직무 수정 실패: jobId가 0 미만인 경우 에러 응답을 반환한다.")
        @Test
        void updateJobFailIdLessThan() throws Exception {
            // given
            UpdateJobRequest request = UpdateJobRequest.builder()
                    .jobId(-1L)
                    .build();

            // when // then
            mockMvc.perform(put("/api/v1/member/job")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.message").value("jobId는 0 이상의 값이어야 합니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.url").value("/api/v1/member/job"))
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("jobId").type(NUMBER)
                                            .description("직무 id")
                                            .attributes(field("constraints", "1~20 사이의 숫자"))
                            ),
                            responseFields(failResponse())
                    ));
        }

        @DisplayName("직무 수정 실패: jobId가 20 초과인 경우 에러 응답을 반환한다.")
        @Test
        void updateJobFailIdMoreThan() throws Exception {
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
                    .andExpect(jsonPath("$.url").value("/api/v1/member/job"))
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("jobId").type(NUMBER)
                                            .description("직무 id")
                                            .attributes(field("constraints", "1~20 사이의 숫자"))
                            ),
                            responseFields(failResponse())
                    ));
        }

    }

    @Nested
    @DisplayName("연차 수정")
    class UpdateJobYear {

        private String updateJobYearResponse;

        @BeforeEach
        void setUp() {
            // 서비스 mock 처리
            updateJobYearResponse = "연차 수정 성공";
            given(memberService.updateJobYear(any(Member.class), any(UpdateJobYearServiceRequest.class))).willReturn(
                    updateJobYearResponse);
        }

        @DisplayName("연차 수정 성공")
        @Test
        void updateJobYearSuccess() throws Exception {
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
                    .andExpect(jsonPath("$.data").value(updateJobYearResponse))
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("yearId").type(NUMBER)
                                            .description("연차 id")
                                            .attributes(field("constraints", "1~4 사이의 숫자"))
                            ),
                            responseFields(successResponse())
                                    .and(
                                            fieldWithPath("data").type(STRING)
                                                    .description("연차 수정 결과")
                                    )
                    ));
        }

        @DisplayName("연차 수정 실패: yearId가 0 미만인 경우 에러 응답을 반환한다.")
        @Test
        void updateJobYearFailIdLessThan() throws Exception {
            // given
            UpdateJobYearRequest request = UpdateJobYearRequest.builder()
                    .yearId(-1)
                    .build();

            // when // then
            mockMvc.perform(put("/api/v1/member/jobyear")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(400))
                    .andExpect(jsonPath("$.message").value("yearId는 0 이상의 값이어야 합니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.url").value("/api/v1/member/jobyear"))
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("yearId").type(NUMBER)
                                            .description("연차 id")
                                            .attributes(field("constraints", "1~4 사이의 숫자"))
                            ),
                            responseFields(failResponse())
                    ));
        }

        @DisplayName("연차 수정 실패: yearId가 4 초과인 경우 에러 응답을 반환한다.")
        @Test
        void updateJobYearFailIdMoreThan() throws Exception {
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
                    .andExpect(jsonPath("$.url").value("/api/v1/member/jobyear"))
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("yearId").type(NUMBER)
                                            .description("연차 id")
                                            .attributes(field("constraints", "1~4 사이의 숫자"))
                            ),
                            responseFields(failResponse())
                    ));
        }

    }

    @Nested
    @DisplayName("push 알림 수신 여부 수정")
    class UpdatePushReceive {

        @DisplayName("push 알림 수신 여부 수정 성공")
        @Test
        void updatePushReceiveSucceeds() throws Exception {
            // given
            String response = "push 알림 여부 수정 성공";
            given(memberService.updatePushReceive(any())).willReturn(response);

            // when // then
            mockMvc.perform(put("/api/v1/member/push")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.code").isEmpty())
                    .andExpect(jsonPath("$.url").isEmpty())
                    .andExpect(jsonPath("$.data").value(response))
                    .andDo(restDocs.document(
                            responseFields(successResponse())
                                    .and(
                                            fieldWithPath("data").type(STRING)
                                                    .description("push 알림 수신 여부 수정 결과")
                                    )
                    ))
            ;

        }

    }

}
