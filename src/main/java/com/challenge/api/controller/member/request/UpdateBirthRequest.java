package com.challenge.api.controller.member.request;

import com.challenge.api.service.member.request.UpdateBirthServiceRequest;
import com.challenge.utils.date.DateUtils;
import com.challenge.validator.annotation.ValidDate;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateBirthRequest {

    @ValidDate
    @NotNull(message = "birth는 필수 입력값입니다.")
    private String birth;

    public UpdateBirthServiceRequest toServiceRequest() {
        return UpdateBirthServiceRequest.builder()
                .birth(DateUtils.toLocalDate(birth))
                .build();
    }

    @Builder
    private UpdateBirthRequest(String birth) {
        this.birth = birth;
    }

}
