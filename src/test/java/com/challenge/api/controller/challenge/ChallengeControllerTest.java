package com.challenge.api.controller.challenge;

import com.challenge.api.controller.ControllerTestSupport;
import com.challenge.api.service.challenge.ChallengeService;
import com.challenge.api.service.challenge.request.ChallengeAchieveServiceRequest;
import com.challenge.api.service.challenge.request.ChallengeCancelServiceRequest;
import com.challenge.api.service.challenge.request.ChallengeCreateServiceRequest;
import com.challenge.api.service.challenge.request.ChallengeUpdateServiceRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChallengeController.class)
class ChallengeControllerTest extends ControllerTestSupport {

    @MockitoBean
    private ChallengeService challengeService;

    @Nested
    @DisplayName("챌린지를 생성한다.")
    class CreateChallenge {

        @DisplayName("챌린지를 성공적으로 생성한다.")
        @Test
        void createChallenge() throws Exception {
            // given
            ChallengeCreateServiceRequest request = ChallengeCreateServiceRequest
                    .builder()
                    .title("챌린지 제목")
                    .durationInWeeks(1)
                    .weeklyGoalCount(2)
                    .categoryId(1L)
                    .color("색상")
                    .content("내용")
                    .build();

            // when // then
            mockMvc.perform(
                            post("/api/v1/challenges")
                                    .content(objectMapper.writeValueAsString(request))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.code").isEmpty())
                    .andExpect(jsonPath("$.url").isEmpty());
        }

        @DisplayName("챌린지를 생성할 때 제목은 필수 입력값이다.")
        @Test
        void createChallenge_Fail_TitleBlank() throws Exception {
            // given
            ChallengeCreateServiceRequest request = ChallengeCreateServiceRequest
                    .builder()
                    .title("") // 제목 빈 값
                    .durationInWeeks(1)
                    .weeklyGoalCount(2)
                    .categoryId(1L)
                    .color("색상")
                    .content("내용")
                    .build();

            // when // then
            mockMvc.perform(
                            post("/api/v1/challenges")
                                    .content(objectMapper.writeValueAsString(request))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("제목은 필수 입력값입니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.data").isEmpty());
        }

        @DisplayName("챌린지를 생성할 때 제목은 공백 포함 30자 이하여야 한다.")
        @Test
        void createChallenge_Fail_TitleSizeMax() throws Exception {
            // given
            String title = "a".repeat(31);
            ChallengeCreateServiceRequest request = ChallengeCreateServiceRequest
                    .builder()
                    .title(title)
                    .durationInWeeks(1)
                    .weeklyGoalCount(2)
                    .categoryId(1L)
                    .color("색상")
                    .content("내용")
                    .build();

            // when // then
            mockMvc.perform(
                            post("/api/v1/challenges")
                                    .content(objectMapper.writeValueAsString(request))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("제목은 공백 포함 30자 이하여야 합니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.data").isEmpty());
        }

        @DisplayName("챌린지를 생성할 때 챌린지 기간은 최소 1주 이상이어야 한다.")
        @Test
        void createChallenge_Fail_DurationMin() throws Exception {
            // given
            ChallengeCreateServiceRequest request = ChallengeCreateServiceRequest
                    .builder()
                    .title("챌린지 제목")
                    .durationInWeeks(0) // 1주 미만
                    .weeklyGoalCount(2)
                    .categoryId(1L)
                    .color("색상")
                    .content("내용")
                    .build();

            // when // then
            mockMvc.perform(
                            post("/api/v1/challenges")
                                    .content(objectMapper.writeValueAsString(request))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("챌린지 기간은 최소 1주 이상이어야 합니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.data").isEmpty());
        }

        @DisplayName("챌린지를 생성할 때 챌린지 기간은 최대 4주 이하여야 한다.")
        @Test
        void createChallenge_Fail_DurationMax() throws Exception {
            // given
            ChallengeCreateServiceRequest request = ChallengeCreateServiceRequest
                    .builder()
                    .title("챌린지 제목")
                    .durationInWeeks(5) // 4주 초과
                    .weeklyGoalCount(2)
                    .categoryId(1L)
                    .color("색상")
                    .content("내용")
                    .build();

            // when // then
            mockMvc.perform(
                            post("/api/v1/challenges")
                                    .content(objectMapper.writeValueAsString(request))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("챌린지 기간은 최대 4주 이하여야 합니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.data").isEmpty());
        }

        @DisplayName("챌린지를 생성할 때 주간 목표 횟수는 최소 1회 이상이어야 한다.")
        @Test
        void createChallenge_Fail_WeeklyGoalCountMin() throws Exception {
            // given
            ChallengeCreateServiceRequest request = ChallengeCreateServiceRequest
                    .builder()
                    .title("챌린지 제목")
                    .durationInWeeks(3)
                    .weeklyGoalCount(0) // 1회 미만
                    .categoryId(1L)
                    .color("색상")
                    .content("내용")
                    .build();

            // when // then
            mockMvc.perform(
                            post("/api/v1/challenges")
                                    .content(objectMapper.writeValueAsString(request))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("주간 목표 횟수는 최소 1회 이상이어야 합니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.data").isEmpty());
        }

        @DisplayName("챌린지를 생성할 때 주간 목표 횟수는 최대 7회 이하여야 한다.")
        @Test
        void createChallenge_Fail_WeekGoalCountMax() throws Exception {
            // given
            ChallengeCreateServiceRequest request = ChallengeCreateServiceRequest
                    .builder()
                    .title("챌린지 제목")
                    .durationInWeeks(3)
                    .weeklyGoalCount(8) // 7회 초과
                    .categoryId(1L)
                    .color("색상")
                    .content("내용")
                    .build();

            // when // then
            mockMvc.perform(
                            post("/api/v1/challenges")
                                    .content(objectMapper.writeValueAsString(request))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("주간 목표 횟수는 최대 7회 이하여야 합니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.data").isEmpty());
        }

        @DisplayName("챌린지를 생성할 때 카테고리는 필수 입력값이다.")
        @Test
        void createChallenge_Fail_CategoryNull() throws Exception {
            // given
            ChallengeCreateServiceRequest request = ChallengeCreateServiceRequest
                    .builder()
                    .title("챌린지 제목")
                    .durationInWeeks(1)
                    .weeklyGoalCount(2)
                    .categoryId(null) // 카테고리 누락
                    .color("색상")
                    .content("내용")
                    .build();

            // when // then
            mockMvc.perform(
                            post("/api/v1/challenges")
                                    .content(objectMapper.writeValueAsString(request))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("카테고리는 필수 입력값입니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.data").isEmpty());
        }

        @DisplayName("챌린지를 생성할 때 색상은 필수 입력값이다.")
        @Test
        void createChallenge_Fail_ColorBlank() throws Exception {
            // given
            ChallengeCreateServiceRequest request = ChallengeCreateServiceRequest
                    .builder()
                    .title("챌린지 제목")
                    .durationInWeeks(1)
                    .weeklyGoalCount(2)
                    .categoryId(1L)
                    .color("")
                    .content("내용")
                    .build();

            // when // then
            mockMvc.perform(
                            post("/api/v1/challenges")
                                    .content(objectMapper.writeValueAsString(request))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("색상은 필수 입력값입니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.data").isEmpty());
        }

        @DisplayName("챌린지를 생성할 때 상세 내용은 공백 포함 최대 500자 이하여야 한다.")
        void createChallenge_Fail_ContentSizeMax() throws Exception {
            // given
            String content = "a".repeat(501);
            ChallengeCreateServiceRequest request = ChallengeCreateServiceRequest
                    .builder()
                    .title("챌린지 제목")
                    .durationInWeeks(1)
                    .weeklyGoalCount(2)
                    .categoryId(1L)
                    .color("색상")
                    .content(content)
                    .build();

            // when // then
            mockMvc.perform(
                            post("/api/v1/challenges")
                                    .content(objectMapper.writeValueAsString(request))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("상세 내용은 공백 포함 최대 500자 이하여야 합니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.data").isEmpty());
        }

    }

    @Nested
    @DisplayName("챌린지를 달성한다")
    class AchieveChallenge {

        @DisplayName("챌린지를 성공적으로 달성한다.")
        @Test
        void achieveChallenge() throws Exception {
            // given
            Long challengeId = 1L;
            String achieveDate = "2024-11-11";
            ChallengeAchieveServiceRequest achieveRequest = ChallengeAchieveServiceRequest.builder()
                    .achieveDate(achieveDate)
                    .build();

            // when // then
            mockMvc.perform(
                            post("/api/v1/challenges/{challengeId}/achieve", challengeId)
                                    .content(objectMapper.writeValueAsString(achieveRequest))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.code").isEmpty())
                    .andExpect(jsonPath("$.url").isEmpty());
        }

        @DisplayName("챌린지를 달성할 때 달성일은 필수 입력값이다.")
        @Test
        void achieveChallenge_Fail_achieveDateBlank() throws Exception {
            // given
            Long challengeId = 1L;
            ChallengeAchieveServiceRequest request = ChallengeAchieveServiceRequest.builder()
                    .achieveDate("") // 달성일 누락
                    .build();

            // when // then
            mockMvc.perform(
                            post("/api/v1/challenges/{challengeId}/achieve", challengeId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("달성일은 필수 입력 값입니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.data").isEmpty());
        }

    }

    @Nested
    @DisplayName("챌린지를 취소한다.")
    class CancelChallenge {

        @DisplayName("챌린지를 성공적으로 취소한다.")
        @Test
        void cancelChallenge() throws Exception {
            // given
            Long challengeId = 1L;
            String cancelDate = "2024-11-11";
            ChallengeCancelServiceRequest cancelRequest = ChallengeCancelServiceRequest.builder()
                    .cancelDate(cancelDate)
                    .build();

            // when // then
            mockMvc.perform(
                            post("/api/v1/challenges/{challengeId}/cancel", challengeId)
                                    .content(objectMapper.writeValueAsString(cancelRequest))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.code").isEmpty())
                    .andExpect(jsonPath("$.url").isEmpty());
        }

        @DisplayName("챌린지를 취소할 때 취소일은 필수 입력값이다.")
        @Test
        void cancelChallenge_Fail_cancelDateBlank() throws Exception {
            // given
            Long challengeId = 1L;

            ChallengeCancelServiceRequest request = ChallengeCancelServiceRequest.builder()
                    .cancelDate("") // 취소일 누락
                    .build();

            // when // then
            mockMvc.perform(
                            post("/api/v1/challenges/{challengeId}/cancel", challengeId)
                                    .content(objectMapper.writeValueAsString(request))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("취소일은 필수 입력 값입니다."))
                    .andExpect(jsonPath("$.code").value("VALID_ERROR"))
                    .andExpect(jsonPath("$.data").isEmpty());
        }

    }

    @DisplayName("챌린지를 성공적으로 수정한다.")
    @Test
    void updateChallenge() throws Exception {
        // given
        Long challengeId = 1L;
        ChallengeUpdateServiceRequest request = ChallengeUpdateServiceRequest.builder()
                .title("챌린지 제목")
                .categoryId(1L)
                .color("색상")
                .content("내용")
                .build();

        // when // then
        mockMvc.perform(
                        put("/api/v1/challenges/{challengeId}", challengeId)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.code").isEmpty())
                .andExpect(jsonPath("$.url").isEmpty());
    }

    @DisplayName("챌린지를 성공적으로 삭제한다.")
    @Test
    void deleteChallenge() throws Exception {
        // given
        Long challengeId = 1L;

        // when // then
        mockMvc.perform(
                        delete("/api/v1/challenges/{challengeId}", challengeId)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.code").isEmpty())
                .andExpect(jsonPath("$.url").isEmpty());
    }

}
