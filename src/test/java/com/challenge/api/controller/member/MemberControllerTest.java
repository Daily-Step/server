package com.challenge.api.controller.member;

import com.challenge.api.controller.ControllerTestSupport;
import com.challenge.api.service.member.MemberService;
import com.challenge.api.service.member.response.MemberInfoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
public class MemberControllerTest extends ControllerTestSupport {

    @MockBean
    private MemberService memberService;

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

}
