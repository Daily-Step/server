package com.challenge.api.service.fcm.request;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FcmMessage {

    String token;
    String title;
    String body;

    public static FcmMessage of(String token, String title, String body) {
        return FcmMessage.builder()
                .token(token)
                .title(title)
                .body(body)
                .build();
    }

    public Message.Builder buildMessage() {
        return Message.builder()
                .setToken(token)
                .setNotification(toNotification());
    }

    public Notification toNotification() {
        return Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
    }

}
