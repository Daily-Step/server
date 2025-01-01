package com.challenge.api.controller.challenge.request;

import com.challenge.api.service.challenge.request.ChallengeCreateServiceRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChallengeCreateRequest {

    @NotBlank(message = "제목은 필수 입력값입니다.")
    @Size(max = 30, message = "제목은 공백 포함 30자 이하여야 합니다.")
    private String title;

    @Min(value = 1, message = "챌린지 기간은 최소 1주 이상이어야 합니다.")
    @Max(value = 4, message = "챌린지 기간은 최대 4주 이하여야 합니다.")
    private int durationInWeeks;

    @Min(value = 1, message = "주간 목표 횟수는 최소 1회 이상이어야 합니다.")
    @Max(value = 7, message = "주간 목표 횟수는 최대 7회 이하여야 합니다.")
    private int weeklyGoalCount;

    @NotNull(message = "카테고리는 필수 입력값입니다.")
    private Long categoryId;

    @NotBlank(message = "색상은 필수 입력값입니다.")
    private String color;

    @Size(max = 500, message = "상세 내용은 공백 포함 최대 500자 이하여야 합니다.")
    private String content;

    @Builder
    private ChallengeCreateRequest(String title, int durationInWeeks, int weeklyGoalCount, Long categoryId,
            String color, String content) {
        this.title = title;
        this.durationInWeeks = durationInWeeks;
        this.weeklyGoalCount = weeklyGoalCount;
        this.categoryId = categoryId;
        this.color = color;
        this.content = content;
    }

    public ChallengeCreateServiceRequest toServiceRequest() {
        return ChallengeCreateServiceRequest.builder()
                .title(title)
                .durationInWeeks(durationInWeeks)
                .weeklyGoalCount(weeklyGoalCount)
                .categoryId(categoryId)
                .color(color)
                .content(content)
                .build();
    }

}
