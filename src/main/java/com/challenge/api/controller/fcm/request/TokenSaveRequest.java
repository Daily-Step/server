package com.challenge.api.controller.fcm.request;

import com.challenge.api.service.fcm.request.TokenSaveServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenSaveRequest {

    @NotBlank(message = "token은 필수 입력값입니다.")
    String token;

    @Builder
    private TokenSaveRequest(String token) {
        this.token = token;
    }

    public TokenSaveServiceRequest toServiceRequest() {
        return TokenSaveServiceRequest.builder()
                .token(this.token)
                .build();
    }

}
