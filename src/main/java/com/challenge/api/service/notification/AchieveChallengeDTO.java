package com.challenge.api.service.notification;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AchieveChallengeDTO {

    Long memberId;
    String nickname;
    List<String> challengeTitles;

}
