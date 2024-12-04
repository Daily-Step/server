package com.challenge.api.service.member.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateNicknameServiceRequest {

    private String nickname;

    @Builder
    private UpdateNicknameServiceRequest(String nickname) {
        this.nickname = nickname;
    }

}
