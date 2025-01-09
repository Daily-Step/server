package com.challenge.api.controller.auth.request;

import com.challenge.api.service.auth.request.KakaoSigninServiceRequest;
import com.challenge.domain.member.Gender;
import com.challenge.utils.date.DateUtils;
import com.challenge.validator.annotation.ValidDate;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoSigninRequest {

    @NotBlank(message = "access token은 필수 입력값입니다.")
    private String accessToken;

    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{4,10}$", message = "닉네임은 4~10자이며, 띄어쓰기와 특수문자를 사용할 수 없습니다.")
    private String nickname;

    @ValidDate
    @NotNull(message = "birth는 필수 입력값입니다.")
    private String birth;

    @NotNull(message = "gender는 필수 입력값입니다.")
    private Gender gender;

    @NotNull(message = "jobId는 필수 입력값입니다.")
    @Min(value = 0, message = "jobId는 0 이상의 값이어야 합니다.")
    @Max(value = 20, message = "jobId는 20 이하의 값이어야 합니다.")
    private Long jobId;

    @NotNull(message = "yearId는 필수 입력값입니다.")
    @Min(value = 0, message = "yearId는 0 이상의 값이어야 합니다.")
    @Max(value = 4, message = "yearId는 4 이하의 값이어야 합니다.")
    private int yearId;

    public KakaoSigninServiceRequest toServiceRequest() {
        return KakaoSigninServiceRequest.builder()
                .accessToken(accessToken)
                .nickname(nickname)
                .birth(DateUtils.toLocalDate(birth))
                .gender(gender)
                .jobId(jobId)
                .yearId(yearId)
                .build();
    }

    @Builder
    private KakaoSigninRequest(String accessToken, String nickname, String birth, Gender gender, Long jobId,
                               int yearId) {
        this.accessToken = accessToken;
        this.nickname = nickname;
        this.birth = birth;
        this.gender = gender;
        this.jobId = jobId;
        this.yearId = yearId;
    }

}
