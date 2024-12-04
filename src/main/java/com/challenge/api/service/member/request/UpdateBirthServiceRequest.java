package com.challenge.api.service.member.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class UpdateBirthServiceRequest {

    private LocalDate birth;

    @Builder
    private UpdateBirthServiceRequest(LocalDate birth) {
        this.birth = birth;
    }

}
