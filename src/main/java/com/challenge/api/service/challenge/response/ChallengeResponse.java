package com.challenge.api.service.challenge.response;

import com.challenge.api.service.category.response.CategoryResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChallengeResponse {

    private final Long id;
    private final String title;
    private final int durationInWeeks;
    private final int weekGoalCount;
    private final CategoryResponse category;
    private final String color;
    private final String content;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;

    @Builder
    private ChallengeResponse(Long id, String title, int durationInWeeks, int weekGoalCount, CategoryResponse category,
            String color, String content, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.id = id;
        this.title = title;
        this.durationInWeeks = durationInWeeks;
        this.weekGoalCount = weekGoalCount;
        this.category = category;
        this.color = color;
        this.content = content;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

}
