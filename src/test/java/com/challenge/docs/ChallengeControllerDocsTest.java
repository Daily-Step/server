package com.challenge.docs;

import com.challenge.api.controller.challenge.ChallengeController;
import com.challenge.api.service.category.response.CategoryResponse;
import com.challenge.api.service.challenge.ChallengeService;
import com.challenge.api.service.challenge.request.ChallengeAchieveServiceRequest;
import com.challenge.api.service.challenge.request.ChallengeCancelServiceRequest;
import com.challenge.api.service.challenge.request.ChallengeCreateServiceRequest;
import com.challenge.api.service.challenge.request.ChallengeUpdateServiceRequest;
import com.challenge.api.service.challenge.response.ChallengeResponse;
import com.challenge.api.service.record.response.RecordResponse;
import com.challenge.domain.member.Member;
import com.challenge.utils.date.DateUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.challenge.docs.RestDocsConfiguration.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChallengeControllerDocsTest extends RestDocsSupport {

    private final ChallengeService challengeService = mock(ChallengeService.class);

    @Override
    protected Object initController() {
        return new ChallengeController(challengeService);
    }

    @Nested
    @DisplayName("챌린지 생성 API")
    class createChallenge {

        @DisplayName("챌린지 생성 성공 API")
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
                            .record(null)
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
                    .andExpect(status().isOk())
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("title").type(STRING)
                                            .attributes(field("constraints", "공백포함 최대 30자"))
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
                                            .optional()
                                            .description("챌린지 내용")
                            ),
                            responseFields(successResponse())
                                    .and(
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
                                            fieldWithPath("data.record").type(NULL)
                                                    .optional()
                                                    .description("기록 목록"),
                                            fieldWithPath("data.title").type(STRING)
                                                    .description("챌린지 제목"),
                                            fieldWithPath("data.content").type(STRING)
                                                    .optional()
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
                    .andExpect(status().isBadRequest())
                    .andDo(restDocs.document(
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
                                            .optional()
                                            .description("챌린지 내용")
                            ),
                            responseFields(failResponse())
                    ));
        }

    }

    @DisplayName("챌린지를 달성 완료 하는 API")
    @Test
    void achieveChallenge() throws Exception {
        Long challengeId = 1L;
        ChallengeAchieveServiceRequest achieveServiceRequest = ChallengeAchieveServiceRequest.builder()
                .achieveDate(DateUtils.toDayString(LocalDate.now()))
                .build();

        given(challengeService.achieveChallenge(
                any(Member.class),
                any(Long.class),
                any(ChallengeAchieveServiceRequest.class)
        ))
                .willReturn(ChallengeResponse
                        .builder()
                        .id(1L)
                        .category(CategoryResponse.builder()
                                .id(1L)
                                .name("카테고리 이름")
                                .build())
                        .record(RecordResponse.builder()
                                .successDates(
                                        List.of(
                                                DateUtils.toDayString(LocalDate.now()),
                                                DateUtils.toDayString(LocalDate.now().plusDays(1))
                                        )
                                )
                                .build())
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
                        post("/api/v1/challenges/{challengeId}/achieve", challengeId)
                                .content(objectMapper.writeValueAsBytes(achieveServiceRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("challengeId")
                                        .attributes(field("type", "Long"))
                                        .description("챌린지 아이디")
                        ),
                        requestFields(
                                fieldWithPath("achieveDate").type(STRING)
                                        .attributes(field("constraints", "yyyy-MM-dd"))
                                        .description("달성 일자")
                        ),
                        responseFields(successResponse())
                                .and(
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
                                        fieldWithPath("data.record").type(OBJECT)
                                                .description("기록 목록"),
                                        fieldWithPath("data.record.successDates").type(ARRAY)
                                                .description("성공 날짜"),
                                        fieldWithPath("data.title").type(STRING)
                                                .description("챌린지 제목"),
                                        fieldWithPath("data.content").type(STRING)
                                                .optional()
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

    @DisplayName("챌린지를 취소하는 API")
    @Test
    void cancelChallenge() throws Exception {
        // given
        Long challengeId = 1L;
        String cancelDate = DateUtils.toDayString(LocalDate.now());
        ChallengeCancelServiceRequest challengeCancelServiceRequest = ChallengeCancelServiceRequest.builder()
                .cancelDate(cancelDate)
                .build();

        given(challengeService.cancelChallenge(
                any(Member.class),
                any(Long.class),
                any(ChallengeCancelServiceRequest.class)
        ))
                .willReturn(ChallengeResponse
                        .builder()
                        .id(1L)
                        .category(CategoryResponse.builder()
                                .id(1L)
                                .name("카테고리 이름")
                                .build())
                        .record(RecordResponse.builder()
                                .successDates(
                                        List.of(
                                                DateUtils.toDayString(LocalDate.now()),
                                                DateUtils.toDayString(LocalDate.now().plusDays(1))
                                        )
                                )
                                .build())
                        .title("챌린지 제목")
                        .content("챌린지 내용")
                        .durationInWeeks(2)
                        .weekGoalCount(2)
                        .totalGoalCount(4)
                        .color("색상")
                        .startDateTime(DateUtils.toDateTimeString(LocalDateTime.now()))
                        .endDateTime(DateUtils.toDateTimeString(LocalDateTime.now().plusWeeks(2)))
                        .build());

        // when // then
        mockMvc.perform(
                        post("/api/v1/challenges/{challengeId}/cancel", challengeId)
                                .content(objectMapper.writeValueAsString(challengeCancelServiceRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("challengeId")
                                        .attributes(field("type", "Long"))
                                        .description("챌린지 아이디")
                        ),
                        responseFields(successResponse())
                                .and(
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
                                        fieldWithPath("data.record").type(OBJECT)
                                                .optional()
                                                .description("기록 목록"),
                                        fieldWithPath("data.record.successDates").type(ARRAY)
                                                .optional()
                                                .description("성공 날짜"),
                                        fieldWithPath("data.title").type(STRING)
                                                .description("챌린지 제목"),
                                        fieldWithPath("data.content").type(STRING)
                                                .optional()
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

    @DisplayName("챌린지를 수정하는 API")
    @Test
    void updateChallenge() throws Exception {
        // given
        Long challengeId = 1L;
        ChallengeUpdateServiceRequest request = ChallengeUpdateServiceRequest.builder()
                .title("수정된 챌린지 제목")
                .categoryId(1L)
                .color("수정된 색상")
                .content("수정된 내용")
                .build();

        given(challengeService.updateChallenge(
                any(Member.class),
                any(Long.class),
                any(ChallengeUpdateServiceRequest.class)
        )).willReturn(ChallengeResponse
                .builder()
                .id(1L)
                .category(CategoryResponse.builder()
                        .id(1L)
                        .name("카테고리 이름")
                        .build())
                .record(RecordResponse.builder()
                        .successDates(
                                List.of(
                                        DateUtils.toDayString(LocalDate.now()),
                                        DateUtils.toDayString(LocalDate.now().plusDays(1))
                                )
                        )
                        .build())
                .title("수정된 챌린지 제목")
                .content("수정된 챌린지 내용")
                .durationInWeeks(2)
                .weekGoalCount(2)
                .totalGoalCount(4)
                .color("수정된 색상")
                .startDateTime(DateUtils.toDateTimeString(LocalDateTime.now()))
                .endDateTime(DateUtils.toDateTimeString(LocalDateTime.now().plusWeeks(2)))
                .build());

        // when // then
        mockMvc.perform(
                        put("/api/v1/challenges/{challengeId}", challengeId)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("challengeId")
                                        .attributes(field("type", "Long"))
                                        .description("챌린지 아이디")
                        ),
                        requestFields(
                                fieldWithPath("title").type(STRING)
                                        .description("챌린지 제목"),
                                fieldWithPath("categoryId").type(NUMBER)
                                        .description("카테고리 아이디"),
                                fieldWithPath("color").type(STRING)
                                        .description("색상"),
                                fieldWithPath("content").type(STRING)
                                        .optional()
                                        .description("챌린지 내용")
                        ),
                        responseFields(successResponse())
                                .and(
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
                                        fieldWithPath("data.record").type(OBJECT)
                                                .description("기록 목록"),
                                        fieldWithPath("data.record.successDates").type(ARRAY)
                                                .description("성공 날짜"),
                                        fieldWithPath("data.title").type(STRING)
                                                .description("챌린지 제목"),
                                        fieldWithPath("data.content").type(STRING)
                                                .optional()
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

    @DisplayName("챌린지를 삭제하는 API")
    @Test
    void deleteChallenge() throws Exception {
        // given
        Long challengeId = 1L;

        given(challengeService.deleteChallenge(
                any(Member.class),
                any(Long.class)
        )).willReturn(challengeId);

        // when // then
        mockMvc.perform(
                        delete("/api/v1/challenges/{challengeId}", challengeId)
                )
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("challengeId")
                                        .attributes(field("type", "Long"))
                                        .description("챌린지 아이디")
                        ),
                        responseFields(successResponse())
                                .and(
                                        fieldWithPath("data").type(NUMBER)
                                                .description("삭제된 챌린지 아이디")
                                )
                ));
    }

}
