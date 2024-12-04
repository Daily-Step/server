package com.challenge.api.service.member.request;

import com.challenge.domain.member.Gender;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateGenderServiceRequest {

    private Gender gender;

    @Builder
    private UpdateGenderServiceRequest(Gender gender) {
        this.gender = gender;
    }

}
