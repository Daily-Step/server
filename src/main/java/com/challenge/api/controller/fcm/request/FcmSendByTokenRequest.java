package com.challenge.api.controller.fcm.request;

import com.challenge.api.service.fcm.request.FcmMessage;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FcmSendByTokenRequest {

    @NotEmpty
    private String token;

    @NotEmpty
    private String title;

    @NotEmpty
    private String body;

    @Builder
    private FcmSendByTokenRequest(String token, String title, String body) {
        this.token = token;
        this.title = title;
        this.body = body;
    }

    public static FcmMessage toFcmMessage(FcmSendByTokenRequest request) {
        return FcmMessage.of(request.getToken(), request.getTitle(), request.getBody());
    }

}
