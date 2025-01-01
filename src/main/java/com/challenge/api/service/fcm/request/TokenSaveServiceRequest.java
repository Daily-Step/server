package com.challenge.api.service.fcm.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenSaveServiceRequest {

    String token;

    @Builder
    private TokenSaveServiceRequest(String token) {
        this.token = token;
    }

}
