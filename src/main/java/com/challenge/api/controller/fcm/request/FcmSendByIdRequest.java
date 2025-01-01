package com.challenge.api.controller.fcm.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FcmSendByIdRequest {

    @NotNull
    private Long memberId;

    @NotEmpty
    private String title;

    @NotEmpty
    private String body;

    @Builder
    private FcmSendByIdRequest(Long memberId, String title, String body) {
        this.memberId = memberId;
        this.title = title;
        this.body = body;
    }

}
