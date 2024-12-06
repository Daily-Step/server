package com.challenge.docs.challenge;

import com.challenge.api.controller.challenge.ChallengeController;
import com.challenge.api.service.category.response.CategoryResponse;
import com.challenge.api.service.challenge.ChallengeService;
import com.challenge.api.service.challenge.request.ChallengeCreateServiceRequest;
import com.challenge.api.service.challenge.response.ChallengeResponse;
import com.challenge.api.service.record.response.RecordResponse;
import com.challenge.docs.RestDocsSupport;
import com.challenge.domain.member.Member;
import com.challenge.utils.DateUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChallengeControllerDocsTest extends RestDocsSupport {

    private final ChallengeService challengeService = mock(ChallengeService.class);

    @Override
    protected Object initController() {
        return new ChallengeController(challengeService);
    }

    @DisplayName("신규 챌린지를 생성하는 API")
    @Test
    void createChallengeSuccess() throws Exception {
        ChallengeCreateServiceRequest request = ChallengeCreateServiceRequest
                .builder()
                .title("챌린지 제목")
                .durationInWeeks(1)
                .weeklyGoalCount(2)
                .categoryId(1L)
                .color("색상")
                .content("내용")
                .build();

        given(challengeService.createChallenge(
                any(Member.class),
                any(ChallengeCreateServiceRequest.class),
                any(LocalDateTime.class)
        ))
                .willReturn(ChallengeResponse
                        .builder()
                        .id(1L)
                        .category(CategoryResponse.builder()
                                .id(1L)
                                .name("카테고리 이름")
                                .build())
                        .records(List.of(RecordResponse.builder()
                                .id(1L)
                                .successDate(LocalDate.now())
                                .build()))
                        .title("챌린지 제목")
                        .content("챌린지 내용")
                        .durationInWeeks(2)
                        .weekGoalCount(2)
                        .totalGoalCount(4)
                        .color("색상")
                        .startDateTime(DateUtils.toDateTimeString(LocalDateTime.now()))
                        .endDateTime(DateUtils.toDateTimeString(LocalDateTime.now().plusWeeks(2)))
                        .build());

        mockMvc.perform(
                        post("/api/v1/challenges")
                                .content(objectMapper.writeValueAsBytes(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("challenge-create-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title").type(STRING)
                                        .description("챌린지 제목"),
                                fieldWithPath("durationInWeeks").type(NUMBER)
                                        .description("챌린지 기간"),
                                fieldWithPath("weeklyGoalCount").type(NUMBER)
                                        .description("챌린지 주간 목표 횟수"),
                                fieldWithPath("categoryId").type(NUMBER)
                                        .description("카테고리 아이디"),
                                fieldWithPath("color").type(STRING)
                                        .description("색상"),
                                fieldWithPath("content").type(STRING)
                                        .description("챌린지 내용")
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
                                fieldWithPath("data").type(OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.id").type(NUMBER)
                                        .description("챌린지 ID"),
                                fieldWithPath("data.category").type(OBJECT)
                                        .description("카테고리 정보"),
                                fieldWithPath("data.category.id").type(NUMBER)
                                        .description("카테고리 ID"),
                                fieldWithPath("data.category.name").type(STRING)
                                        .description("카테고리 이름"),
                                fieldWithPath("data.records").type(ARRAY)
                                        .description("기록 목록"),
                                fieldWithPath("data.records[].id").type(NUMBER)
                                        .description("기록 ID"),
                                fieldWithPath("data.records[].successDate").type(ARRAY)
                                        .description("성공 날짜"),
                                fieldWithPath("data.title").type(STRING)
                                        .description("챌린지 제목"),
                                fieldWithPath("data.content").type(STRING)
                                        .description("챌린지 내용"),
                                fieldWithPath("data.durationInWeeks").type(NUMBER)
                                        .description("챌린지 기간 (주 단위)"),
                                fieldWithPath("data.weekGoalCount").type(NUMBER)
                                        .description("주간 목표 횟수"),
                                fieldWithPath("data.totalGoalCount").type(NUMBER)
                                        .description("총 목표 횟수"),
                                fieldWithPath("data.color").type(STRING)
                                        .description("챌린지 색상"),
                                fieldWithPath("data.startDateTime").type(STRING)
                                        .description("챌린지 시작 일시"),
                                fieldWithPath("data.endDateTime").type(STRING)
                                        .description("챌린지 종료 일시")
                        )
                ));
    }

    @DisplayName("챌린지 생성 실패 API (필수 필드 누락)")
    @Test
    void createChallengeFail() throws Exception {
        ChallengeCreateServiceRequest request = ChallengeCreateServiceRequest.builder()
                .durationInWeeks(2)
                .weeklyGoalCount(2)
                .categoryId(1L)
                .color("색상")
                .content("내용")
                .build();

        mockMvc.perform(post("/api/v1/challenges")
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("challenge-create-fail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title").type(NULL)
                                        .description("챌린지 제목"),
                                fieldWithPath("durationInWeeks").type(NUMBER)
                                        .description("챌린지 기간"),
                                fieldWithPath("weeklyGoalCount").type(NUMBER)
                                        .description("챌린지 주간 목표 횟수"),
                                fieldWithPath("categoryId").type(NUMBER)
                                        .description("카테고리 아이디"),
                                fieldWithPath("color").type(STRING)
                                        .description("색상"),
                                fieldWithPath("content").type(STRING)
                                        .description("챌린지 내용")
                        ),
                        responseFields(
                                fieldWithPath("status").type(NUMBER)
                                        .description("상태 코드"),
                                fieldWithPath("message").type(STRING)
                                        .description("검증 실패 메시지"),
                                fieldWithPath("code").type(STRING)
                                        .description("에러 코드"),
                                fieldWithPath("url").type(STRING)
                                        .description("API 호출 URL"),
                                fieldWithPath("data").type(NULL)
                                        .description("응답 데이터 (실패 시 null)")
                        )
                ));
    }

}
