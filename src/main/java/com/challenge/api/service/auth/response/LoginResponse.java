package com.challenge.api.service.auth.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResponse {

    private final Long memberId;
    private final String accessToken;
    private final String refreshToken;
    private final Long accessTokenExpiresIn;

    @Builder
    private LoginResponse(Long memberId, String accessToken, String refreshToken, Long accessTokenExpiresIn) {
        this.memberId = memberId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresIn = accessTokenExpiresIn;
    }

    public static LoginResponse of(Long memberId, String accessToken, String refreshToken, Long accessTokenExpiresIn) {
        return LoginResponse.builder()
                .memberId(memberId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(accessTokenExpiresIn)
                .build();
    }

}
