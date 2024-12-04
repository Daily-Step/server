package com.challenge.api.service.member.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateJobYearServiceRequest {

    private int yearId;

    @Builder
    private UpdateJobYearServiceRequest(int yearId) {
        this.yearId = yearId;
    }

}
