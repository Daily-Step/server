package com.challenge.api.service.notification;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NewChallengeDTO {

    Long memberId;
    String nickname;

}
