package com.challenge.api.controller.auth.request;

import com.challenge.api.service.auth.request.KakaoSigninServiceRequest;
import com.challenge.domain.member.Gender;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class KakaoSigninRequest {

    @NotBlank(message = "access token은 필수 입력값입니다.")
    private String accessToken;

    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{4,10}$", message = "닉네임은 4~10자이며, 띄어쓰기와 특수문자를 사용할 수 없습니다.")
    private String nickname;

    @NotNull(message = "birth는 필수 입력값입니다.")
    @Past(message = "birth는 과거 날짜여야 합니다.")
    private LocalDate birth;

    @NotNull(message = "gender는 필수 입력값입니다.")
    private Gender gender;

    @NotNull(message = "jobId는 필수 입력값입니다.")
    @Min(value = 1, message = "jobId는 1 이상의 값이어야 합니다.")
    @Max(value = 20, message = "jobId는 20 이하의 값이어야 합니다.")
    private Long jobId;

    @NotNull(message = "yearId는 필수 입력값입니다.")
    @Min(value = 1, message = "yearId는 1 이상의 값이어야 합니다.")
    @Max(value = 4, message = "yearId는 4 이하의 값이어야 합니다.")
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

    @Builder
    private KakaoSigninRequest(String accessToken, String nickname, LocalDate birth, Gender gender, Long jobId,
            int yearId) {
        this.accessToken = accessToken;
        this.nickname = nickname;
        this.birth = birth;
        this.gender = gender;
        this.jobId = jobId;
        this.yearId = yearId;
    }

}
