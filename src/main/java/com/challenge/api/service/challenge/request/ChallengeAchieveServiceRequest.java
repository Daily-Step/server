package com.challenge.api.service.challenge.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChallengeAchieveServiceRequest {

    private String achieveDate;

    @Builder
    private ChallengeAchieveServiceRequest(String achieveDate) {
        this.achieveDate = achieveDate;
    }

}
