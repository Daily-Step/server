package com.challenge.api.controller.auth.request;

import com.challenge.api.service.auth.request.ReissueTokenServiceRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReissueTokenRequest {

    private String refreshToken;

    public ReissueTokenServiceRequest toServiceRequest() {
        return ReissueTokenServiceRequest.builder()
                .refreshToken(refreshToken)
                .build();
    }

    @Builder
    private ReissueTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
