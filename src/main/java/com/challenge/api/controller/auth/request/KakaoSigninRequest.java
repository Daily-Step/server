package com.challenge.api.controller.auth.request;

import com.challenge.api.service.auth.request.KakaoSigninServiceRequest;
import com.challenge.domain.member.Gender;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class KakaoSigninRequest {

    @NotBlank
    private String accessToken;

    @NotBlank
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{4,10}$", message = "닉네임은 4~10자이며, 띄어쓰기와 특수문자를 사용할 수 없습니다.")
    private String nickname;

    @NotNull
    @Past
    private LocalDate birth;

    @NotNull
    private Gender gender;

    @NotNull
    @Min(1)
    @Max(20)
    private Long jobId;

    @NotNull
    @Min(1)
    @Max(4)
    private int yearId;

    public KakaoSigninServiceRequest toServiceRequest() {
        return KakaoSigninServiceRequest.builder()
                .accessToken(accessToken)
                .nickname(nickname)
                .birth(birth)
                .gender(gender)
                .jobId(jobId)
                .yearId(yearId)
                .build();
    }

}
