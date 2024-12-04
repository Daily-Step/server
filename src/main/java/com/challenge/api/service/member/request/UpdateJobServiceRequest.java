package com.challenge.api.service.member.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateJobServiceRequest {

    private Long jobId;

    @Builder
    private UpdateJobServiceRequest(Long jobId) {
        this.jobId = jobId;
    }

}
