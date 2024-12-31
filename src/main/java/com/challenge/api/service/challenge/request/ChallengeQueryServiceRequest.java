package com.challenge.api.service.challenge.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChallengeQueryServiceRequest {

    private String queryDate;

    @Builder
    private ChallengeQueryServiceRequest(String queryDate) {
        this.queryDate = queryDate;
    }

}
