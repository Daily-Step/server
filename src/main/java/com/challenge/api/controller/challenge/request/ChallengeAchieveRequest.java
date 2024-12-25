package com.challenge.api.controller.challenge.request;

import com.challenge.api.service.challenge.request.ChallengeAchieveServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChallengeAchieveRequest {

    @NotBlank(message = "달성일은 필수 입력 값입니다.")
    private String achieveDate;

    public ChallengeAchieveServiceRequest toServiceRequest() {
        return ChallengeAchieveServiceRequest.builder()
                .achieveDate(achieveDate)
                .build();
    }

}
