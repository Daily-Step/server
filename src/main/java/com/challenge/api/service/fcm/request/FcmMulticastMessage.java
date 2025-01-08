package com.challenge.api.service.fcm.request;

import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FcmMulticastMessage {

    List<String> targetTokens;
    String title;
    String body;

    public static FcmMulticastMessage of(List<String> targetTokens, String title, String body) {
        return FcmMulticastMessage.builder()
                .targetTokens(targetTokens)
                .title(title)
                .body(body)
                .build();
    }

    public MulticastMessage.Builder buildMessage() {
        return MulticastMessage.builder()
                .setNotification(toNotification())
                .addAllTokens(targetTokens);
    }

    public Notification toNotification() {
        return Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
    }

}
