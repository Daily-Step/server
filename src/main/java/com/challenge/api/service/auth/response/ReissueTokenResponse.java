package com.challenge.api.service.auth.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReissueTokenResponse {

    private final String accessToken;
    private final String refreshToken;
    private final Long accessTokenExpiresIn;

    @Builder
    private ReissueTokenResponse(String accessToken, String refreshToken, Long accessTokenExpiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresIn = accessTokenExpiresIn;
    }

    public static ReissueTokenResponse of(String accessToken, String refreshToken, Long accessTokenExpiresIn) {
        return ReissueTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(accessTokenExpiresIn)
                .build();
    }

}
