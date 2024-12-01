package com.challenge.api.controller.member;

import com.challenge.api.ApiResponse;
import com.challenge.api.controller.member.request.CheckNicknameRequest;
import com.challenge.api.service.member.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/nickname/valid")
    public ApiResponse<String> checkNicknameIsValid(@RequestBody @Valid CheckNicknameRequest request) {
        return ApiResponse.ok(memberService.checkNicknameIsValid(request.toServiceRequest()));
    }

}
