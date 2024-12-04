package com.challenge.api.controller.member.request;

import com.challenge.api.service.member.request.UpdateGenderServiceRequest;
import com.challenge.domain.member.Gender;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateGenderRequest {

    @NotNull(message = "gender는 필수 입력값입니다.")
    private Gender gender;

    public UpdateGenderServiceRequest toServiceRequest() {
        return UpdateGenderServiceRequest.builder()
                .gender(gender)
                .build();
    }

    @Builder
    private UpdateGenderRequest(@NotNull(message = "gender는 필수 입력값입니다.") Gender gender) {
        this.gender = gender;
    }

}
