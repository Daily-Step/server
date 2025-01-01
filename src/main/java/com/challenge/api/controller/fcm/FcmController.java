package com.challenge.api.controller.fcm;

import com.challenge.annotation.AuthMember;
import com.challenge.api.ApiResponse;
import com.challenge.api.controller.fcm.request.FcmSendByIdRequest;
import com.challenge.api.controller.fcm.request.FcmSendByTokenRequest;
import com.challenge.api.controller.fcm.request.TokenSaveRequest;
import com.challenge.api.service.fcm.FcmService;
import com.challenge.domain.member.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fcm")
public class FcmController {

    private final FcmService fcmService;

    @PostMapping("/send/token")
    public ApiResponse<String> sendMessageByToken(@Valid @RequestBody FcmSendByTokenRequest request) {
        return ApiResponse.ok(fcmService.sendMessage(FcmSendByTokenRequest.toFcmMessage(request)));
    }

    @PostMapping("/send/member")
    public ApiResponse<String> sendMessageByMemberId(@Valid @RequestBody FcmSendByIdRequest request) {
        return ApiResponse.ok(fcmService.sendMessageById(request));
    }

    @PostMapping
    public ApiResponse<String> saveToken(@Valid @RequestBody TokenSaveRequest request,
                                         @AuthMember Member member) {
        return ApiResponse.ok(fcmService.saveToken(request.toServiceRequest(), member));
    }

    @DeleteMapping
    public ApiResponse<String> deleteToken(@AuthMember Member member) {
        return ApiResponse.ok(fcmService.deleteToken(member));
    }

}
