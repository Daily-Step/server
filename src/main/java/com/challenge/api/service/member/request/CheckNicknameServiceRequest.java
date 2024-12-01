package com.challenge.api.service.member.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CheckNicknameServiceRequest {

    private String nickname;

    @Builder
    private CheckNicknameServiceRequest(String nickname) {
        this.nickname = nickname;
    }

}
