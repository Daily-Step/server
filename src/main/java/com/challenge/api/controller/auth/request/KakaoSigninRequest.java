package com.challenge.api.controller.auth.request;

import com.challenge.domain.member.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
public class KakaoSigninRequest {

    String accessToken;
    String nickname;
    Date birth;
    Gender gender;
    int jobId;

}
