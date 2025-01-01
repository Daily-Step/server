package com.challenge.api.controller.challenge.request;

import com.challenge.api.service.challenge.request.ChallengeQueryServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChallengeQueryRequest {

    @NotBlank(message = "조회일은 필수 입력 값입니다.")
    private String queryDate;

    @Builder
    private ChallengeQueryRequest(String queryDate) {
        this.queryDate = queryDate;
    }

    public ChallengeQueryServiceRequest toServiceRequest() {
        return ChallengeQueryServiceRequest.builder()
                .queryDate(queryDate)
                .build();
    }

}
