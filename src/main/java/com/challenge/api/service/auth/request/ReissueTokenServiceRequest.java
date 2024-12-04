package com.challenge.api.service.auth.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReissueTokenServiceRequest {

    private String refreshToken;

    @Builder
    private ReissueTokenServiceRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
