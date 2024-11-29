package com.challenge.api.service.challenge.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChallengeCreateServiceRequest {

    private String title;
    private int durationInWeeks;
    private int weekGoalCount;
    private Long categoryId;
    private String color;
    private String content;

    @Builder
    private ChallengeCreateServiceRequest(String title, int durationInWeeks, int weekGoalCount, Long categoryId,
            String color, String content) {
        this.title = title;
        this.durationInWeeks = durationInWeeks;
        this.weekGoalCount = weekGoalCount;
        this.categoryId = categoryId;
        this.color = color;
        this.content = content;
    }

}
