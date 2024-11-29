package com.challenge.api.controller.auth.request;

import com.challenge.api.service.auth.request.KakaoLoginServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoLoginRequest {

    @NotBlank
    private String accessToken;

    public KakaoLoginServiceRequest toServiceRequest() {
        return KakaoLoginServiceRequest.builder()
                .accessToken(accessToken)
                .build();
    }

}
