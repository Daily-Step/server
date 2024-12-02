package com.challenge.api.controller.member.request;

import com.challenge.api.service.member.request.UpdateBirthServiceRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class UpdateBirthRequest {

    @NotNull(message = "birth는 필수 입력값입니다.")
    @Past(message = "birth는 과거 날짜여야 합니다.")
    private LocalDate birth;

    public UpdateBirthServiceRequest toServiceRequest() {
        return UpdateBirthServiceRequest.builder()
                .birth(birth)
                .build();
    }

    @Builder
    private UpdateBirthRequest(@NotNull(message = "birth는 필수 입력값입니다.") LocalDate birth) {
        this.birth = birth;
    }

}
