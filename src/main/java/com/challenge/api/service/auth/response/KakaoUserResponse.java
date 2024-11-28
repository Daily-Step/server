package com.challenge.api.service.auth.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class KakaoUserResponse {

    private final Long socialId;
    private final String email;

    @Builder
    private KakaoUserResponse(Long socialId, String email) {
        this.socialId = socialId;
        this.email = email;
    }

    public static KakaoUserResponse of(Long socialId, String email) {
        return KakaoUserResponse.builder()
                .socialId(socialId)
                .email(email)
                .build();
    }

}
