package com.challenge.api.controller.member;

import com.challenge.annotation.AuthMember;
import com.challenge.api.ApiResponse;
import com.challenge.api.controller.member.request.CheckNicknameRequest;
import com.challenge.api.controller.member.request.UpdateBirthRequest;
import com.challenge.api.controller.member.request.UpdateGenderRequest;
import com.challenge.api.controller.member.request.UpdateJobRequest;
import com.challenge.api.controller.member.request.UpdateJobYearRequest;
import com.challenge.api.controller.member.request.UpdateNicknameRequest;
import com.challenge.api.service.member.MemberService;
import com.challenge.api.service.member.response.MemberInfoResponse;
import com.challenge.domain.member.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/nickname/valid")
    public ApiResponse<String> checkNicknameIsValid(@RequestBody @Valid CheckNicknameRequest request) {
        return ApiResponse.ok(memberService.checkNicknameIsValid(request.toServiceRequest()));
    }

    @PutMapping("/nickname")
    public ApiResponse<String> updateNickname(@RequestBody @Valid UpdateNicknameRequest request,
            @AuthMember Member member) {
        return ApiResponse.ok(memberService.updateNickname(member, request.toServiceRequest()));
    }

    @PutMapping("/birth")
    public ApiResponse<String> updateBirth(@RequestBody @Valid UpdateBirthRequest request, @AuthMember Member member) {
        return ApiResponse.ok(memberService.updateBirth(member, request.toServiceRequest()));
    }

    @PutMapping("/gender")
    public ApiResponse<String> updateGender(@RequestBody @Valid UpdateGenderRequest request,
            @AuthMember Member member) {
        return ApiResponse.ok(memberService.updateGender(member, request.toServiceRequest()));
    }

    @PutMapping("/job")
    public ApiResponse<String> updateJob(@RequestBody @Valid UpdateJobRequest request, @AuthMember Member member) {
        return ApiResponse.ok(memberService.updateJob(member, request.toServiceRequest()));
    }

    @PutMapping("/jobyear")
    public ApiResponse<String> updateJobYear(@RequestBody @Valid UpdateJobYearRequest request,
            @AuthMember Member member) {
        return ApiResponse.ok(memberService.updateJobYear(member, request.toServiceRequest()));
    }

}
