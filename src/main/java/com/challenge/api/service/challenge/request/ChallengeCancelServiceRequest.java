package com.challenge.api.service.challenge.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChallengeCancelServiceRequest {

    private String cancelDate;

    @Builder
    private ChallengeCancelServiceRequest(String cancelDate) {
        this.cancelDate = cancelDate;
    }

}
