package com.challenge.api.controller.auth.request;

import com.challenge.api.service.auth.request.ReissueTokenServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReissueTokenRequest {

    @NotBlank(message = "refresh token은 필수 입력값입니다.")
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
