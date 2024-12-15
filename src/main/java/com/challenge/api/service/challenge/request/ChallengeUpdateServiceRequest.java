package com.challenge.api.service.challenge.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChallengeUpdateServiceRequest {

    private String title;
    private Long categoryId;
    private String color;
    private String content;

    @Builder
    private ChallengeUpdateServiceRequest(String title, Long categoryId, String color, String content) {
        this.title = title;
        this.categoryId = categoryId;
        this.color = color;
        this.content = content;
    }

}
