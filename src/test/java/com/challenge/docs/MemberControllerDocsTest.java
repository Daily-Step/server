package com.challenge.docs;

import com.challenge.api.controller.member.MemberController;
import com.challenge.api.service.member.MemberService;
import com.challenge.api.service.member.response.MemberInfoResponse;
import com.challenge.domain.job.Job;
import com.challenge.domain.member.Gender;
import com.challenge.domain.member.JobYear;
import com.challenge.domain.member.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

}
