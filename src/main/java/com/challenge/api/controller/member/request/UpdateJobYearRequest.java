package com.challenge.api.controller.member.request;

import com.challenge.api.service.member.request.UpdateJobYearServiceRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateJobYearRequest {

    @NotNull(message = "yearId는 필수 입력값입니다.")
    @Min(value = 0, message = "yearId는 0 이상의 값이어야 합니다.")
    @Max(value = 4, message = "yearId는 4 이하의 값이어야 합니다.")
    private int yearId;

    public UpdateJobYearServiceRequest toServiceRequest() {
        return UpdateJobYearServiceRequest.builder()
                .yearId(yearId)
                .build();
    }

    @Builder
    private UpdateJobYearRequest(@NotNull(message = "yearId는 필수 입력값입니다.") int yearId) {
        this.yearId = yearId;
    }

}
