package com.challenge.api.service.auth.response;

import com.challenge.domain.member.LoginType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SocialInfoResponse {

    private final Long socialId;
    private final String email;
    private final LoginType loginType;

    @Builder
    private SocialInfoResponse(Long socialId, String email, LoginType loginType) {
        this.socialId = socialId;
        this.email = email;
        this.loginType = loginType;
    }

    public static SocialInfoResponse of(Long socialId, String email, LoginType loginType) {
        return SocialInfoResponse.builder()
                .socialId(socialId)
                .email(email)
                .loginType(loginType)
                .build();
    }

}
