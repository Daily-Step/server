package com.challenge.api.service.notification;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AchieveChallengeCountDTO {

    String nickname;
    int count;

}
