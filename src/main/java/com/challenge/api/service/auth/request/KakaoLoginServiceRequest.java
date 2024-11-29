package com.challenge.api.service.auth.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoLoginServiceRequest {

    private String accessToken;

    @Builder
    private KakaoLoginServiceRequest(String accessToken) {
        this.accessToken = accessToken;
    }

}
