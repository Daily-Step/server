package com.challenge.api.service.fcm;

import com.challenge.api.controller.fcm.request.FcmSendByIdRequest;
import com.challenge.api.service.fcm.request.FcmMessage;
import com.challenge.api.service.fcm.request.TokenSaveServiceRequest;
import com.challenge.domain.member.Member;
import com.challenge.domain.member.MemberRepository;
import com.challenge.domain.notification.Notification;
import com.challenge.domain.notification.NotificationRepository;
import com.challenge.exception.ErrorCode;
import com.challenge.exception.GlobalException;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.ApsAlert;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final FirebaseMessaging firebaseMessaging;
    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;

    public String sendMessage(FcmMessage request) {
        try {
            // fcm 메시지 발송
            Message message = request.buildMessage().setApnsConfig(getApnsConfig(request)).build();
            firebaseMessaging.send(message);
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            throw new GlobalException(ErrorCode.FCM_SERVICE_UNAVAILABLE);
        }

        return "fcm 푸시 발송 성공";
    }

    @Transactional
    public String sendMessageById(FcmSendByIdRequest request) {
        Member member = memberRepository.findById(request.getMemberId()).orElseThrow(
                () -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));

        if (member.getFcmToken() == null) {
            throw new GlobalException(ErrorCode.FCM_TOKEN_NOT_FOUND);
        }

        // fcm 메시지 전송
        FcmMessage fcmMessage = FcmMessage.of(member.getFcmToken(), request.getTitle(), request.getBody());
        String result = sendMessage(fcmMessage);

        // 알림 발송 내역 저장
        Notification notification = Notification.of(request.getTitle(), request.getBody(), LocalDateTime.now(), member);
        notificationRepository.save(notification);

        return result;
    }

    @Transactional
    public String saveToken(TokenSaveServiceRequest request, Member member) {
        member.updateFcmToken(request.getToken());

        return "fcm 토큰 저장 성공";
    }

    @Transactional
    public String deleteToken(Member member) {
        member.updateFcmToken(null);

        return "fcm 토큰 삭제 성공";
    }

    private ApnsConfig getApnsConfig(FcmMessage request) {
        ApsAlert alert = ApsAlert.builder().setTitle(request.getTitle()).setBody(request.getBody()).build();
        Aps aps = Aps.builder().setAlert(alert).setSound("default").build();
        return ApnsConfig.builder().setAps(aps).build();
    }

}
