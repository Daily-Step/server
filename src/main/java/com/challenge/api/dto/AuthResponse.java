package com.challenge.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthResponse {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class authDto {

        Long memberId;
        String accessToken;
        Long accessTokenExpiresIn;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class kakaoResultDto {

        Long socialId;
        String email;

    }

}
