package com.challenge.api.controller.member.request;

import com.challenge.api.service.member.request.UpdateJobServiceRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateJobRequest {

    @NotNull(message = "jobId는 필수 입력값입니다.")
    @Min(value = 1, message = "jobId는 1 이상의 값이어야 합니다.")
    @Max(value = 20, message = "jobId는 20 이하의 값이어야 합니다.")
    private Long jobId;

    public UpdateJobServiceRequest toServiceRequest() {
        return UpdateJobServiceRequest.builder()
                .jobId(jobId)
                .build();
    }

    @Builder
    private UpdateJobRequest(Long jobId) {
        this.jobId = jobId;
    }

}
