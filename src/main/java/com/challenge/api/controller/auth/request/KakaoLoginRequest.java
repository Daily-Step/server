package com.challenge.api.controller.auth.request;

import com.challenge.api.service.auth.request.KakaoLoginServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoLoginRequest {

    @NotBlank(message = "access token은 필수 입력값입니다.")
    private String accessToken;

    public KakaoLoginServiceRequest toServiceRequest() {
        return KakaoLoginServiceRequest.builder()
                .accessToken(accessToken)
                .build();
    }

    @Builder
    private KakaoLoginRequest(String accessToken) {
        this.accessToken = accessToken;
    }

}
