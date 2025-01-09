package com.challenge.api.service.notification;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AchieveChallengeTitleDTO {

    List<String> challengeTitles;

}
