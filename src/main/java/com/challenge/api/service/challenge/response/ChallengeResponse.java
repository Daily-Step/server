package com.challenge.api.service.challenge.response;

import com.challenge.api.service.category.response.CategoryResponse;
import com.challenge.api.service.record.response.RecordResponse;
import com.challenge.domain.challenge.Challenge;
import com.challenge.domain.challenge.ChallengeStatus;
import com.challenge.utils.date.DateUtils;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ChallengeResponse {

    private final Long id;
    private final CategoryResponse category;
    private final RecordResponse record;
    private final String title;
    private final String content;
    private final ChallengeStatus status;
    private final int durationInWeeks;
    private final int weekGoalCount;
    private final int totalGoalCount;
    private final String color;
    private final String startDateTime;
    private final String endDateTime;

    @Builder
    private ChallengeResponse(Long id, CategoryResponse category, RecordResponse record, String title,
                              String content, ChallengeStatus status, int durationInWeeks, int weekGoalCount,
                              int totalGoalCount, String color, String startDateTime, String endDateTime) {
        this.id = id;
        this.category = category;
        this.record = record;
        this.title = title;
        this.content = content;
        this.status = status;
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
                .record(RecordResponse.of(challenge))
                .title(challenge.getTitle())
                .content(challenge.getContent())
                .status(challenge.getStatus())
                .durationInWeeks(challenge.getDurationInWeeks())
                .weekGoalCount(challenge.getWeeklyGoalCount())
                .totalGoalCount(challenge.getTotalGoalCount())
                .color(challenge.getColor())
                .startDateTime(DateUtils.toDateTimeString(challenge.getStartDateTime()))
                .endDateTime(DateUtils.toDateTimeString(challenge.getEndDateTime()))
                .build();
    }

}
