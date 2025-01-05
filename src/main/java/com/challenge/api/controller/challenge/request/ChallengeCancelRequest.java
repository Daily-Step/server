package com.challenge.api.controller.challenge.request;

import com.challenge.api.service.challenge.request.ChallengeCancelServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChallengeCancelRequest {

    @NotBlank(message = "취소일은 필수 입력 값입니다.")
    private String cancelDate;

    @Builder
    private ChallengeCancelRequest(String cancelDate) {
        this.cancelDate = cancelDate;
    }

    public ChallengeCancelServiceRequest toServiceRequest() {
        return ChallengeCancelServiceRequest.builder()
                .cancelDate(cancelDate)
                .build();
    }

}
