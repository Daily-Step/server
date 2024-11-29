package com.challenge.api.service.auth.request;

import com.challenge.domain.member.Gender;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class KakaoSigninServiceRequest {

    private String accessToken;
    private String nickname;
    private LocalDate birth;
    private Gender gender;
    private Long jobId;
    private int yearId;

    @Builder
    public KakaoSigninServiceRequest(String accessToken, String nickname, LocalDate birth, Gender gender, Long jobId,
                                     int yearId) {
        this.accessToken = accessToken;
        this.nickname = nickname;
        this.birth = birth;
        this.gender = gender;
        this.jobId = jobId;
        this.yearId = yearId;
    }

}
