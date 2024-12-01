package com.challenge.api.controller.member;

import com.challenge.annotation.AuthMember;
import com.challenge.api.ApiResponse;
import com.challenge.api.service.member.MemberService;
import com.challenge.api.service.member.response.MemberInfoResponse;
import com.challenge.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public ApiResponse<MemberInfoResponse> getMemberInfo(@AuthMember Member member) {
        return ApiResponse.ok(memberService.getMemberInfo(member));
    }

}
