package com.challenge.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthRequest {

    @Getter
    @NoArgsConstructor
    public static class kakaoRequest {

        String accessToken;

    }

}
