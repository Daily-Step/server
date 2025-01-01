package com.challenge.api.controller.fcm;

import com.challenge.annotation.AuthMember;
import com.challenge.api.ApiResponse;
import com.challenge.api.controller.fcm.request.FcmSendRequest;
import com.challenge.api.controller.fcm.request.TokenSaveRequest;
import com.challenge.api.service.fcm.FcmService;
import com.challenge.domain.member.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fcm")
public class FcmController {

    private final FcmService fcmService;

    @PostMapping("/send")
    public ApiResponse<String> pushMessage(@RequestBody FcmSendRequest request) {
        return ApiResponse.ok(fcmService.sendMessage(FcmSendRequest.toFcmMessage(request)));
    }

    @PostMapping
    public ApiResponse<String> saveToken(@Valid @RequestBody TokenSaveRequest request,
                                         @AuthMember Member member) {
        return ApiResponse.ok(fcmService.saveToken(request.toServiceRequest(), member));
    }

}
