package com.challenge.api.service.challenge.response;

import com.challenge.api.service.category.response.CategoryResponse;
import com.challenge.domain.challenge.Challenge;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChallengeResponse {

    private final Long id;
    private final CategoryResponse category;
    private final String title;
    private final String content;
    private final int durationInWeeks;
    private final int weekGoalCount;
    private final int totalGoalCount;
    private final String color;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;

    @Builder
    private ChallengeResponse(Long id, CategoryResponse category, String title, String content, int durationInWeeks,
                              int weekGoalCount, int totalGoalCount, String color, LocalDateTime startDateTime,
                              LocalDateTime endDateTime) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.content = content;
        this.durationInWeeks = durationInWeeks;
        this.weekGoalCount = weekGoalCount;
        this.totalGoalCount = totalGoalCount;
        this.color = color;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public static ChallengeResponse of(Challenge challenge) {
        return ChallengeResponse.builder()
                .id(challenge.getId())
                .category(CategoryResponse.of(challenge.getCategory()))
                .title(challenge.getTitle())
                .content(challenge.getContent())
                .durationInWeeks(challenge.getDurationInWeeks())
                .weekGoalCount(challenge.getWeeklyGoalCount())
                .totalGoalCount(challenge.getTotalGoalCount())
                .color(challenge.getColor())
                .startDateTime(challenge.getStartDateTime())
                .endDateTime(challenge.getEndDateTime())
                .build();
    }

}
