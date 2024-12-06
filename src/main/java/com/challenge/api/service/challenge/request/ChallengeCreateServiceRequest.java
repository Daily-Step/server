package com.challenge.api.service.challenge.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChallengeCreateServiceRequest {

    private String title;
    private int durationInWeeks;
    private int weeklyGoalCount;
    private Long categoryId;
    private String color;
    private String content;

    @Builder
    private ChallengeCreateServiceRequest(String title, int durationInWeeks, int weeklyGoalCount, Long categoryId,
            String color, String content) {
        this.title = title;
        this.durationInWeeks = durationInWeeks;
        this.weeklyGoalCount = weeklyGoalCount;
        this.categoryId = categoryId;
        this.color = color;
        this.content = content;
    }

}
